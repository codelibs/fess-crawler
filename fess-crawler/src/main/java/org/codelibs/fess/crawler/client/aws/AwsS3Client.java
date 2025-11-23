/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.aws;

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

import jakarta.annotation.Resource;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * A crawler client implementation for accessing and retrieving content from AWS S3.
 * This client supports operations on Amazon S3 buckets and objects.
 *
 * <p>This client requires the following initialization parameters:
 * <ul>
 *   <li>region - The AWS region (default: us-east-1)</li>
 *   <li>accessKey - The AWS access key</li>
 *   <li>secretKey - The AWS secret key</li>
 * </ul>
 *
 * <p>The client supports URLs in the format: {@code s3://bucket-name/object-key}
 *
 * @author shinsuke
 */
public class AwsS3Client extends AbstractCrawlerClient {

    private static final Logger logger = LogManager.getLogger(AwsS3Client.class);

    /** The character encoding to use for content. Defaults to UTF-8. */
    protected String charset = Constants.UTF_8;

    /** Helper for managing content length validation and limits. */
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    /** Flag indicating whether the client has been initialized. */
    protected volatile boolean isInit = false;

    /** The AWS S3 client instance. */
    protected S3Client s3Client;

    /**
     * Creates a new AwsS3Client instance.
     */
    public AwsS3Client() {
        super();
    }

    @Override
    public synchronized void init() {
        if (isInit) {
            return;
        }

        super.init();

        final String region = getInitParameter("region", "us-east-1", String.class);
        final String accessKey = getInitParameter("accessKey", null, String.class);
        if (StringUtil.isBlank(accessKey)) {
            throw new CrawlingAccessException("accessKey is blank.");
        }
        final String secretKey = getInitParameter("secretKey", null, String.class);
        if (StringUtil.isBlank(secretKey)) {
            throw new CrawlingAccessException("secretKey is blank.");
        }

        try {
            final AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to create AWS S3 client: region=" + region, e);
        }

        isInit = true;
        if (logger.isInfoEnabled()) {
            logger.info("AWS S3 client initialized successfully: region={}", region);
        }
    }

    @Override
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
        isInit = false;
    }

    @Override
    public ResponseData doGet(final String uri) {
        return processRequest(uri, true);
    }

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

    /**
     * Processes an S3 request with timeout management.
     */
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
                if (accessTimeoutTask != null && !accessTimeoutTask.isCanceled()) {
                    accessTimeoutTask.cancel();
                }
            }
        }
    }

    /**
     * Retrieves response data for the specified URI.
     */
    protected ResponseData getResponseData(final String uri, final boolean includeContent) {
        if (logger.isDebugEnabled()) {
            logger.debug("Accessing S3 object: uri={}, includeContent={}", uri, includeContent);
        }

        final ResponseData responseData = new ResponseData();
        try {
            responseData.setMethod(includeContent ? Constants.GET_METHOD : Constants.HEAD_METHOD);
            final String normalizedUri = normalizeUri(uri);
            responseData.setUrl(normalizedUri);

            final String[] paths = parsePath(normalizedUri.replaceFirst("^s3:/+", StringUtil.EMPTY));
            final String bucketName = paths[0];
            final String key = paths[1];
            if (logger.isDebugEnabled()) {
                logger.debug("Parsed S3 path: bucket={}, key={}", bucketName, key);
            }

            HeadObjectResponse headResponse = null;
            try {
                headResponse = s3Client.headObject(HeadObjectRequest.builder().bucket(bucketName).key(key).build());
            } catch (final NoSuchKeyException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Object not found: bucket={}, key={}", bucketName, key);
                }
            }

            if (headResponse == null) {
                // Try to list objects with prefix
                final Set<RequestData> requestDataSet = new HashSet<>();
                final ListObjectsV2Request listRequest =
                        ListObjectsV2Request.builder().bucket(bucketName).prefix(key).delimiter("/").build();
                final ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

                for (final S3Object s3Object : listResponse.contents()) {
                    final String objectKey = s3Object.key();
                    requestDataSet.add(RequestDataBuilder.newRequestData().get().url("s3://" + bucketName + "/" + objectKey).build());
                }
                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
            }

            // Object found
            responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(headResponse.contentLength());
            checkMaxContentLength(responseData);

            if (headResponse.lastModified() != null) {
                responseData.setLastModified(Date.from(headResponse.lastModified()));
            }
            if (headResponse.contentType() != null) {
                responseData.setMimeType(headResponse.contentType());
            }

            if (contentLengthHelper != null) {
                final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                if (responseData.getContentLength() > maxLength) {
                    throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                            + maxLength + " byte. The url is " + normalizedUri);
                }
            }

            if (includeContent) {
                // Get object tags
                try {
                    final GetObjectTaggingResponse taggingResponse =
                            s3Client.getObjectTagging(GetObjectTaggingRequest.builder().bucket(bucketName).key(key).build());
                    taggingResponse.tagSet().forEach(tag -> responseData.addMetaData(tag.key(), tag.value()));
                } catch (final Exception e) {
                    logger.warn("Failed to get object tags: bucket={}, key={}", bucketName, key, e);
                }

                // Get object content
                if (headResponse.contentLength() < maxCachedContentSize) {
                    final GetObjectRequest getRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
                    try (ResponseInputStream<GetObjectResponse> contentStream =
                            s3Client.getObject(getRequest);
                            InputStream in = new BufferedInputStream(contentStream)) {
                        responseData.setResponseBody(InputStreamUtil.getBytes(in));
                    } catch (final Exception e) {
                        logger.warn("Failed to read S3 object content: bucket={}, key={}, size={}", bucketName, key,
                                headResponse.contentLength(), e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                    }
                } else {
                    File outputFile = null;
                    try {
                        outputFile = createTempFile("crawler-AwsS3Client-", ".out", null);
                        final GetObjectRequest getRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
                        try (ResponseInputStream<GetObjectResponse> in = s3Client.getObject(getRequest)) {
                            CopyUtil.copy(in, outputFile);
                        }
                        responseData.setResponseBody(outputFile, true);
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                    "Object size exceeds cache threshold, using temp file: bucket={}, key={}, size={}, threshold={}, tempFile={}",
                                    bucketName, key, headResponse.contentLength(), maxCachedContentSize, outputFile.getAbsolutePath());
                        }
                    } catch (final Exception e) {
                        logger.warn("Failed to write S3 object to temp file: bucket={}, key={}, size={}, tempFile={}", bucketName, key,
                                headResponse.contentLength(), outputFile != null ? outputFile.getAbsolutePath() : "null", e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                        FileUtil.deleteInBackground(outputFile);
                    }
                }

                if (StringUtil.isBlank(responseData.getMimeType())) {
                    final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                    try (final InputStream is = responseData.getResponseBody()) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(is, key));
                    } catch (final Exception e) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(null, key));
                    }
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

    /**
     * Parses an S3 path into bucket name and key components.
     */
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

    /**
     * Normalizes the URI.
     */
    protected String normalizeUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new CrawlerSystemException("The uri is empty.");
        }
        String normalized = uri;
        if (!normalized.startsWith("s3:")) {
            normalized = "s3://" + normalized;
        }
        return normalized;
    }

    /**
     * Gets the character encoding.
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the character encoding.
     */
    public void setCharset(final String charset) {
        this.charset = charset;
    }
}
