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
package org.codelibs.fess.crawler.client.s3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.Tag;

/**
 * A crawler client implementation for accessing and retrieving content from S3-compatible storage systems
 * using AWS SDK v2. This client supports operations on Amazon S3 and S3-compatible storage systems.
 *
 * <p>This client requires the following initialization parameters:
 * <ul>
 *   <li>endpoint - The URL of the S3-compatible server</li>
 *   <li>accessKey - The access key for authentication</li>
 *   <li>secretKey - The secret key for authentication</li>
 *   <li>region - The AWS region (default: us-east-1)</li>
 *   <li>connectTimeout - Connection timeout in milliseconds (default: 10000)</li>
 *   <li>readTimeout - Read timeout in milliseconds (default: 10000)</li>
 * </ul>
 *
 * <p>The client supports URLs in the format: {@code s3://bucket-name/object-path}
 *
 * <p>Features:
 * <ul>
 *   <li>Automatic initialization of AWS S3 client</li>
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
 */
public class S3Client extends AbstractCrawlerClient {

    private static final Logger logger = LogManager.getLogger(S3Client.class);

    /**
     * The character encoding to use for content. Defaults to UTF-8.
     */
    protected String charset = Constants.UTF_8;

    /**
     * Helper for managing content length validation and limits.
     */
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    /**
     * Flag indicating whether the client has been initialized.
     */
    protected volatile boolean isInit = false;

    /**
     * The AWS S3 client instance for interacting with object storage.
     */
    protected software.amazon.awssdk.services.s3.S3Client awsS3Client;

    /**
     * Creates a new S3Client instance.
     */
    public S3Client() {
        super();
    }

    @Override
    public synchronized void init() {
        if (isInit) {
            return;
        }

        super.init();

        final String endpoint = getInitParameter("endpoint", null, String.class);
        if (StringUtil.isBlank(endpoint)) {
            throw new CrawlingAccessException(
                    "S3 endpoint is blank. Please set the S3_ENDPOINT environment variable or endpoint parameter.");
        }
        final String accessKey = getInitParameter("accessKey", null, String.class);
        if (StringUtil.isBlank(accessKey)) {
            throw new CrawlingAccessException(
                    "S3 access key is blank. Please set the S3_ACCESS_KEY environment variable or accessKey parameter.");
        }
        final String secretKey = getInitParameter("secretKey", null, String.class);
        if (StringUtil.isBlank(secretKey)) {
            throw new CrawlingAccessException(
                    "S3 secret key is blank. Please set the S3_SECRET_KEY environment variable or secretKey parameter.");
        }
        final String region = getInitParameter("region", "us-east-1", String.class);

        try {
            final S3ClientBuilder builder = software.amazon.awssdk.services.s3.S3Client.builder();

            // Set endpoint override (for MinIO/LocalStack compatibility)
            builder.endpointOverride(URI.create(endpoint));

            // Set credentials
            builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));

            // Set region
            builder.region(Region.of(region));

            // Set path-style access (required for MinIO and other S3-compatible services)
            builder.forcePathStyle(true);

            awsS3Client = builder.build();
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to create S3 client: endpoint=" + endpoint, e);
        }

        isInit = true;
        if (logger.isInfoEnabled()) {
            logger.info("S3 client initialized successfully: endpoint={}, region={}", endpoint, region);
        }
    }

    /**
     * Checks if a bucket exists in the object storage.
     * @param name the name of the bucket to check
     * @return true if the bucket exists, false otherwise
     * @throws CrawlingAccessException if an error occurs while checking bucket existence
     */
    protected boolean bucketExists(final String name) {
        try {
            final HeadBucketRequest request = HeadBucketRequest.builder().bucket(name).build();
            awsS3Client.headBucket(request);
            return true;
        } catch (final NoSuchBucketException e) {
            return false;
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to check bucket existence: bucket=" + name, e);
        }
    }

    /**
     * Processes an S3 request with timeout management.
     * @param uri the URI to process
     * @param includeContent whether to include the actual content in the response
     * @return the response data for the request
     * @throws CrawlingAccessException if an error occurs while processing the request
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
     * Parses an S3 path into bucket name and object path components.
     * @param path the S3 path to parse (format: bucket/object/path)
     * @return an array containing the bucket name and object path
     * @throws CrawlingAccessException if the path format is invalid
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
     * Retrieves response data for the specified URI.
     * @param uri the URI to retrieve data for
     * @param includeContent whether to include the actual content in the response
     * @return the response data containing metadata and optionally content
     * @throws CrawlingAccessException if an error occurs while accessing the resource
     * @throws ChildUrlsException if the URI represents a directory with child URLs
     */
    protected ResponseData getResponseData(final String uri, final boolean includeContent) {
        if (logger.isDebugEnabled()) {
            logger.debug("Accessing S3 object: uri={}, includeContent={}", uri, includeContent);
        }
        final ResponseData responseData = new ResponseData();
        try {
            responseData.setMethod(includeContent ? Constants.GET_METHOD : Constants.HEAD_METHOD);
            final String filePath = preprocessUri(uri);
            responseData.setUrl(filePath);

            final String[] paths = parsePath(filePath.replaceFirst("^s3:/+", StringUtil.EMPTY));
            final String bucketName = paths[0];
            final String path = paths[1];
            if (logger.isDebugEnabled()) {
                logger.debug("Parsed S3 path: bucket={}, objectPath={}", bucketName, path);
            }
            final HeadObjectResponse headObject = getHeadObject(bucketName, path);
            if (headObject == null) {
                final Set<RequestData> requestDataSet = new HashSet<>();
                final ListObjectsV2Request listRequest =
                        ListObjectsV2Request.builder().bucket(bucketName).prefix(path).delimiter("/").build();

                final ListObjectsV2Response listResponse = awsS3Client.listObjectsV2(listRequest);

                // Add objects (excluding the prefix itself if it matches exactly)
                for (final S3Object s3Object : listResponse.contents()) {
                    final String objectKey = s3Object.key();
                    if (!objectKey.equals(path)) {
                        requestDataSet.add(RequestDataBuilder.newRequestData().get().url("s3://" + bucketName + "/" + objectKey).build());
                    }
                }

                // Add common prefixes (directories)
                for (final CommonPrefix prefix : listResponse.commonPrefixes()) {
                    requestDataSet.add(RequestDataBuilder.newRequestData().get().url("s3://" + bucketName + "/" + prefix.prefix()).build());
                }

                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
            }
            // check file size
            responseData.setContentLength(headObject.contentLength());
            checkMaxContentLength(responseData);

            responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
            responseData.setCharSet(getCharset());
            responseData.setLastModified(headObject.lastModified() == null ? null : Date.from(headObject.lastModified()));
            responseData.setMimeType(headObject.contentType());

            // Add metadata
            if (headObject.metadata() != null) {
                headObject.metadata().forEach(responseData::addMetaData);
            }

            if (contentLengthHelper != null) {
                final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                if (responseData.getContentLength() > maxLength) {
                    throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                            + maxLength + " byte. The url is " + filePath);
                }
            }

            if (includeContent) {
                final Map<String, String> objectTags = getObjectTags(bucketName, path);
                if (objectTags != null) {
                    objectTags.forEach(responseData::addMetaData);
                }
                if (headObject.contentLength() < maxCachedContentSize) {
                    final GetObjectRequest getRequest = GetObjectRequest.builder().bucket(bucketName).key(path).build();
                    try (InputStream contentStream = new BufferedInputStream(awsS3Client.getObject(getRequest))) {
                        responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                    } catch (final Exception e) {
                        logger.warn("Failed to read S3 object content: bucket={}, path={}, size={}", bucketName, path,
                                headObject.contentLength(), e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                    }
                } else {
                    File outputFile = null;
                    try {
                        outputFile = createTempFile("crawler-S3Client-", ".out", null);
                        final GetObjectRequest getRequest = GetObjectRequest.builder().bucket(bucketName).key(path).build();
                        CopyUtil.copy(awsS3Client.getObject(getRequest), outputFile);
                        responseData.setResponseBody(outputFile, true);
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                    "Object size exceeds cache threshold, using temp file: bucket={}, path={}, size={}, threshold={}, tempFile={}",
                                    bucketName, path, headObject.contentLength(), maxCachedContentSize, outputFile.getAbsolutePath());
                        }
                    } catch (final Exception e) {
                        logger.warn("Failed to write S3 object to temp file: bucket={}, path={}, size={}, tempFile={}", bucketName, path,
                                headObject.contentLength(), outputFile != null ? outputFile.getAbsolutePath() : "null", e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                        FileUtil.deleteInBackground(outputFile);
                    }
                }

                final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                try (final InputStream is = responseData.getResponseBody()) {
                    responseData.setMimeType(mimeTypeHelper.getContentType(is, path));
                } catch (final Exception e) {
                    responseData.setMimeType(mimeTypeHelper.getContentType(null, path));
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
     * Retrieves metadata information for an object in the specified bucket.
     * @param bucketName the name of the bucket containing the object
     * @param path the path to the object within the bucket
     * @return the object metadata, or null if the object does not exist
     * @throws CrawlingAccessException if the bucket does not exist
     */
    protected HeadObjectResponse getHeadObject(final String bucketName, final String path) {
        if (StringUtil.isEmpty(path)) {
            return null;
        }

        try {
            final HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName).key(path).build();
            return awsS3Client.headObject(request);
        } catch (final NoSuchKeyException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Object not found: bucket={}, path={}", bucketName, path);
            }
        } catch (final NoSuchBucketException e) {
            throw new CrawlingAccessException("Bucket not found: bucket=" + bucketName, e);
        } catch (final S3Exception e) {
            logger.warn("Failed to access object with error code {}: bucket={}, path={}", e.awsErrorDetails().errorCode(), bucketName, path,
                    e);
        } catch (final Exception e) {
            logger.warn("Failed to get object head: bucket={}, path={}", bucketName, path, e);
        }
        return null;
    }

    /**
     * Retrieves tags associated with an object in the specified bucket.
     * @param bucketName the name of the bucket containing the object
     * @param path the path to the object within the bucket
     * @return the tags associated with the object, or null if no tags exist or object not found
     */
    protected Map<String, String> getObjectTags(final String bucketName, final String path) {
        if (StringUtil.isEmpty(path)) {
            return null;
        }

        try {
            final GetObjectTaggingRequest request = GetObjectTaggingRequest.builder().bucket(bucketName).key(path).build();
            final GetObjectTaggingResponse response = awsS3Client.getObjectTagging(request);
            if (response.tagSet() == null || response.tagSet().isEmpty()) {
                return null;
            }
            final Map<String, String> tags = new HashMap<>();
            for (final Tag tag : response.tagSet()) {
                tags.put(tag.key(), tag.value());
            }
            return tags;
        } catch (final NoSuchKeyException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Object not found when retrieving tags: bucket={}, path={}", bucketName, path);
            }
        } catch (final NoSuchBucketException e) {
            throw new CrawlingAccessException("Bucket not found: bucket=" + bucketName, e);
        } catch (final S3Exception e) {
            logger.warn("Failed to retrieve object tags with error code {}: bucket={}, path={}", e.awsErrorDetails().errorCode(),
                    bucketName, path, e);
        } catch (final Exception e) {
            logger.warn("Failed to get object tags: bucket={}, path={}", bucketName, path, e);
        }
        return null;
    }

    /**
     * Preprocesses a URI to ensure it has the correct S3 protocol prefix.
     * @param uri the URI to preprocess
     * @return the preprocessed URI with s3:// prefix
     * @throws CrawlerSystemException if the URI is empty
     */
    protected String preprocessUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new CrawlerSystemException("S3 URI is empty. Please provide a valid S3 URI (s3://...).");
        }

        String filePath = uri;
        if (!filePath.startsWith("s3:")) {
            filePath = "s3://" + filePath;
        }

        return filePath;
    }

    /**
     * Returns the character set used for content encoding.
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the character set used for content encoding.
     * @param charset the charset to set
     */
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

    /**
     * Executes a HEAD request for the given URL.
     * @param url The URL to request.
     * @return The ResponseData.
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
