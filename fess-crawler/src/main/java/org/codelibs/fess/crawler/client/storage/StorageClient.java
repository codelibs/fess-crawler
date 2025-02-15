/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.crawler.client.storage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.AbstractCrawlerClient;
import org.codelibs.fess.crawler.client.AccessTimeoutTarget;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectTagsArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.MinioClient.Builder;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import io.minio.messages.Tags;
import jakarta.annotation.Resource;

/**
 * A crawler client implementation for accessing and retrieving content from storage systems using MinIO.
 * This client supports operations on object storage systems compatible with S3 protocol.
 *
 * <p>This client requires the following initialization parameters:
 * <ul>
 *   <li>endpoint - The URL of the MinIO server</li>
 *   <li>accessKey - The access key for authentication</li>
 *   <li>secretKey - The secret key for authentication</li>
 *   <li>connectTimeout - Connection timeout in milliseconds (default: 10000)</li>
 *   <li>writeTimeout - Write timeout in milliseconds (default: 10000)</li>
 *   <li>readTimeout - Read timeout in milliseconds (default: 10000)</li>
 * </ul>
 *
 * <p>The client supports URLs in the format: {@code storage://bucket-name/object-path}
 *
 * <p>Features:
 * <ul>
 *   <li>Automatic initialization of MinIO client</li>
 *   <li>Support for HEAD and GET operations</li>
 *   <li>Content length validation</li>
 *   <li>MIME type detection</li>
 *   <li>Handling of large files through temporary file storage</li>
 *   <li>Object metadata and tags retrieval</li>
 *   <li>Directory listing capabilities</li>
 * </ul>
 *
 * <p>The client handles timeout management for access operations and includes proper
 * resource cleanup mechanisms.
 *
 */
public class StorageClient extends AbstractCrawlerClient {

    private static final Logger logger = LogManager.getLogger(StorageClient.class);

    protected String charset = Constants.UTF_8;

    @Resource
    protected ContentLengthHelper contentLengthHelper;

    protected volatile boolean isInit = false;

    protected MinioClient minioClient;

    @Override
    public synchronized void init() {
        if (isInit) {
            return;
        }

        super.init();

        final Builder builder = MinioClient.builder();
        final String endpoint = getInitParameter("endpoint", null, String.class);
        if (StringUtil.isBlank(endpoint)) {
            throw new CrawlingAccessException("endpoint is blank.");
        }
        builder.endpoint(endpoint);
        final String accessKey = getInitParameter("accessKey", null, String.class);
        if (StringUtil.isBlank(accessKey)) {
            throw new CrawlingAccessException("accessKey is blank.");
        }
        final String secretKey = getInitParameter("secretKey", null, String.class);
        if (StringUtil.isBlank(secretKey)) {
            throw new CrawlingAccessException("secretKey is blank.");
        }
        builder.credentials(accessKey, secretKey);
        try {
            minioClient = builder.build();
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to create MinioClient(" + endpoint + ")", e);
        }

        minioClient.setTimeout(getInitParameter("connectTimeout", (long) 10000, Long.class),
                getInitParameter("writeTimeout", (long) 10000, Long.class), getInitParameter("readTimeout", (long) 10000, Long.class));

        isInit = true;
    }

    protected boolean bucketExists(final String name) {
        try {
            final BucketExistsArgs args = BucketExistsArgs.builder().bucket(name).build();
            return minioClient.bucketExists(args);
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not access bucket:" + name, e);
        }
    }

    protected ResponseData processRequest(final String uri, final boolean includeContent) {
        if (!isInit) {
            init();
        }

        // start
        AccessTimeoutTarget accessTimeoutTarget = null;
        TimeoutTask accessTimeoutTask = null;
        if (accessTimeout != null) {
            accessTimeoutTarget = new AccessTimeoutTarget(Thread.currentThread());
            accessTimeoutTask = TimeoutManager.getInstance().addTimeoutTarget(accessTimeoutTarget, accessTimeout, false);
        }

        try {
            return getResponseData(uri, includeContent);
        } finally {
            if (accessTimeoutTarget != null) {
                accessTimeoutTarget.stop();
                if (!accessTimeoutTask.isCanceled()) {
                    accessTimeoutTask.cancel();
                }
            }
        }
    }

    protected String[] parsePath(final String path) {
        if (StringUtil.isNotEmpty(path)) {
            final String[] values = path.split("/", 2);
            if (values.length == 2) {
                return values;
            }
            if (values.length == 1 && StringUtil.isNotEmpty(values[0])) {
                return new String[] { values[0], StringUtil.EMPTY };
            }
        }
        throw new CrawlingAccessException("Invalid path: " + path);
    }

    protected ResponseData getResponseData(final String uri, final boolean includeContent) {
        final ResponseData responseData = new ResponseData();
        try {
            responseData.setMethod(includeContent ? Constants.GET_METHOD : Constants.HEAD_METHOD);
            final String filePath = preprocessUri(uri);
            responseData.setUrl(filePath);

            final String[] paths = parsePath(filePath.replaceFirst("^storage:/+", StringUtil.EMPTY));
            final String bucketName = paths[0];
            final String path = paths[1];
            final StatObjectResponse statObject = getStatObject(bucketName, path);
            if (statObject == null) {
                final Set<RequestData> requestDataSet = new HashSet<>();
                final ListObjectsArgs args = ListObjectsArgs.builder().bucket(bucketName).prefix(path).recursive(false)
                        .includeUserMetadata(false).useApiVersion1(false).build();
                for (final Result<Item> result : minioClient.listObjects(args)) {
                    final Item item = result.get();
                    final String objectName = item.objectName();
                    requestDataSet.add(RequestDataBuilder.newRequestData().get().url("storage://" + bucketName + "/" + objectName).build());
                }
                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
            }
            // check file size
            responseData.setContentLength(statObject.size());
            checkMaxContentLength(responseData);

            responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
            responseData.setCharSet(getCharset());
            responseData.setLastModified(statObject.lastModified() == null ? null : Date.from(statObject.lastModified().toInstant()));
            responseData.setMimeType(statObject.contentType());
            statObject.headers().forEach(e -> responseData.addMetaData(e.getFirst(), e.getSecond()));

            if (contentLengthHelper != null) {
                final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                if (responseData.getContentLength() > maxLength) {
                    throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                            + maxLength + " byte. The url is " + filePath);
                }
            }

            if (includeContent) {
                final Tags objectTags = getObjectTags(bucketName, path);
                if (objectTags != null) {
                    objectTags.get().entrySet().forEach(e -> responseData.addMetaData(e.getKey(), e.getValue()));
                }
                if (statObject.size() < maxCachedContentSize) {
                    final GetObjectArgs args = GetObjectArgs.builder().bucket(bucketName).object(path).build();
                    try (InputStream contentStream = new BufferedInputStream(minioClient.getObject(args))) {
                        responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                    } catch (final Exception e) {
                        logger.warn("I/O Exception.", e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                    }
                } else {
                    File outputFile = null;
                    try {
                        outputFile = File.createTempFile("crawler-StorageClient-", ".out");
                        final GetObjectArgs args = GetObjectArgs.builder().bucket(bucketName).object(path).build();
                        CopyUtil.copy(minioClient.getObject(args), outputFile);
                        responseData.setResponseBody(outputFile, true);
                    } catch (final Exception e) {
                        logger.warn("I/O Exception.", e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                        FileUtil.deleteInBackground(outputFile);
                    }
                }

                final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                try (final InputStream is = responseData.getResponseBody()) {
                    responseData.setMimeType(mimeTypeHelper.getContentType(is, statObject.object()));
                } catch (final Exception e) {
                    responseData.setMimeType(mimeTypeHelper.getContentType(null, statObject.object()));
                }
            }
        } catch (final CrawlerSystemException e) {
            CloseableUtil.closeQuietly(responseData);
            throw e;
        } catch (final Exception e) {
            CloseableUtil.closeQuietly(responseData);
            throw new CrawlingAccessException("Could not access " + uri, e);
        }
        return responseData;
    }

    protected StatObjectResponse getStatObject(final String bucketName, final String path) {
        if (StringUtil.isEmpty(path)) {
            return null;
        }

        try {
            final StatObjectArgs args = StatObjectArgs.builder().bucket(bucketName).object(path).build();
            return minioClient.statObject(args);
        } catch (final ErrorResponseException e) {
            final String code = e.errorResponse().code();
            switch (code) {
            case "NoSuchBucket":
                throw new CrawlingAccessException("Bucket " + bucketName + " is not found.", e);
            case "NoSuchKey", "NoSuchObject":
                if (logger.isDebugEnabled()) {
                    logger.debug("{} is not an object.", path);
                }
                break;
            default:
                logger.warn(path + " is not an object.", e);
                break;
            }
        } catch (final Exception e) {
            logger.warn(path + " is not an object.", e);
        }
        return null;
    }

    protected Tags getObjectTags(final String bucketName, final String path) {
        if (StringUtil.isEmpty(path)) {
            return null;
        }

        try {
            final GetObjectTagsArgs args = GetObjectTagsArgs.builder().bucket(bucketName).object(path).build();
            return minioClient.getObjectTags(args);
        } catch (final ErrorResponseException e) {
            final String code = e.errorResponse().code();
            switch (code) {
            case "NoSuchBucket":
                throw new CrawlingAccessException("Bucket " + bucketName + " is not found.", e);
            case "NoSuchKey", "NoSuchObject":
                if (logger.isDebugEnabled()) {
                    logger.debug("{} is not an object.", path);
                }
                break;
            default:
                logger.warn(path + " is not an object.", e);
                break;
            }
        } catch (final Exception e) {
            logger.warn(path + " is not an object.", e);
        }
        return null;
    }

    protected String preprocessUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new CrawlerSystemException("The uri is empty.");
        }

        String filePath = uri;
        if (!filePath.startsWith("storage:")) {
            filePath = "storage://" + filePath;
        }

        return filePath;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(final String charset) {
        this.charset = charset;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.client.CrawlerClient#doGet(java.lang.String)
     */
    @Override
    public ResponseData doGet(final String uri) {
        return processRequest(uri, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.client.CrawlerClient#doHead(java.lang.String)
     */
    @Override
    public ResponseData doHead(final String url) {
        try {
            final ResponseData responseData = processRequest(url, false);
            responseData.setMethod(Constants.HEAD_METHOD);
            return responseData;
        } catch (final ChildUrlsException e) {
            return null;
        }
    }

}
