/*
 * Copyright 2012-2020 CodeLibs Project and the Others.
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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.minio.ErrorCode;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;

/**
 * StorageClient is CrawlerClient implementation to crawl files on a storage
 * system.
 *
 * @author shinsuke
 *
 */
public class StorageClient extends AbstractCrawlerClient {

    private static final Logger logger = LoggerFactory.getLogger(StorageClient.class);

    protected String charset = Constants.UTF_8;

    @Resource
    protected ContentLengthHelper contentLengthHelper;

    protected AtomicBoolean isInit = new AtomicBoolean(false);

    protected MinioClient minioClient;

    @Override
    public void init() {
        super.init();

        final String endpoint = getInitParameter("endpoint", null, String.class);
        if (StringUtil.isBlank(endpoint)) {
            throw new CrawlingAccessException("endpoint is blank.");
        }
        final String accessKey = getInitParameter("accessKey", null, String.class);
        if (StringUtil.isBlank(accessKey)) {
            throw new CrawlingAccessException("accessKey is blank.");
        }
        final String secretKey = getInitParameter("secretKey", null, String.class);
        if (StringUtil.isBlank(secretKey)) {
            throw new CrawlingAccessException("secretKey is blank.");
        }
        try {
            minioClient = new MinioClient(endpoint, accessKey, secretKey);
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to create MinioClient(" + endpoint + ")", e);
        }

        minioClient.setTimeout(getInitParameter("connectTimeout", (long) 10000, Long.class),
                getInitParameter("writeTimeout", (long) 10000, Long.class),
                getInitParameter("readTimeout", (long) 10000, Long.class));
    }

    protected boolean bucketExists(final String name) {
        try {
            return minioClient.bucketExists(name);
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not access bucket:" + name, e);
        }
    }

    protected ResponseData processRequest(final String uri, final boolean includeContent) {
        if (!isInit.get()) {
            synchronized (isInit) {
                if (!isInit.get()) {
                    init();
                    isInit.set(true);
                }
            }
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
            } else if (values.length == 1 && StringUtil.isNotEmpty(values[0])) {
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
            final ObjectStat statObject = getStatObject(bucketName, path);
            if (statObject != null) {
                // check file size
                responseData.setContentLength(statObject.length());
                checkMaxContentLength(responseData);

                responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
                responseData.setCharSet(getCharset());
                responseData.setLastModified(statObject.createdTime() == null ? null : Date.from(statObject.createdTime().toInstant()));
                responseData.setMimeType(statObject.contentType());
                statObject.httpHeaders().entrySet().forEach(e -> responseData.addMetaData(e.getKey(), e.getValue()));

                if (contentLengthHelper != null) {
                    final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                    if (responseData.getContentLength() > maxLength) {
                        throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                                + maxLength + " byte. The url is " + filePath);
                    }
                }

                if (includeContent) {
                    if (statObject.length() < maxCachedContentSize) {
                        try (InputStream contentStream = new BufferedInputStream(minioClient.getObject(bucketName, path))) {
                            responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                        } catch (final Exception e) {
                            logger.warn("I/O Exception.", e);
                            responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                        }
                    } else {
                        File outputFile = null;
                        try {
                            outputFile = File.createTempFile("crawler-SmbClient-", ".out");
                            CopyUtil.copy(minioClient.getObject(bucketName, path), outputFile);
                            responseData.setResponseBody(outputFile, true);
                        } catch (final Exception e) {
                            logger.warn("I/O Exception.", e);
                            responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                            FileUtil.deleteInBackground(outputFile);
                        }
                    }

                    final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                    try (final InputStream is = responseData.getResponseBody()) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(is, statObject.name()));
                    } catch (final Exception e) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(null, statObject.name()));
                    }
                }
            } else {
                final Set<RequestData> requestDataSet = new HashSet<>();
                for (final Result<Item> result : minioClient.listObjects(bucketName, path, false)) {
                    final Item item = result.get();
                    final String objectName = item.objectName();
                    requestDataSet.add(RequestDataBuilder.newRequestData().get().url("storage://" + bucketName + "/" + objectName).build());
                }
                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
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

    protected ObjectStat getStatObject(final String bucketName, final String path) {
        if (StringUtil.isEmpty(path)) {
            return null;
        }

        try {
            return minioClient.statObject(bucketName, path);
        } catch (final ErrorResponseException e) {
            final ErrorCode code = e.errorResponse().errorCode();
            switch (code) {
            case NO_SUCH_BUCKET:
                throw new CrawlingAccessException("Bucket " + bucketName + " is not found.", e);
            case NO_SUCH_KEY:
            case NO_SUCH_OBJECT:
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
