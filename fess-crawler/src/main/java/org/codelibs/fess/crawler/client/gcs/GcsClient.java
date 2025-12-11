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
package org.codelibs.fess.crawler.client.gcs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.Date;
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

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import jakarta.annotation.Resource;

/**
 * A crawler client implementation for accessing and retrieving content from Google Cloud Storage.
 * This client supports operations on GCS buckets and objects.
 *
 * <p>This client requires the following initialization parameters:
 * <ul>
 *   <li>projectId - The Google Cloud project ID</li>
 *   <li>endpoint - Custom endpoint URL (optional, for testing with fake-gcs-server)</li>
 *   <li>credentialsFile - Path to service account JSON file (optional)</li>
 *   <li>connectTimeout - Connection timeout in milliseconds (default: 10000)</li>
 *   <li>writeTimeout - Write timeout in milliseconds (default: 10000)</li>
 *   <li>readTimeout - Read timeout in milliseconds (default: 10000)</li>
 * </ul>
 *
 * <p>The client supports URLs in the format: {@code gcs://bucket-name/object-path}
 *
 * <p>Features:
 * <ul>
 *   <li>Automatic initialization of GCS client</li>
 *   <li>Support for HEAD and GET operations</li>
 *   <li>Content length validation</li>
 *   <li>MIME type detection</li>
 *   <li>Handling of large files through temporary file storage</li>
 *   <li>Object metadata retrieval</li>
 *   <li>Directory listing capabilities</li>
 * </ul>
 *
 * <p>The client handles timeout management for access operations and includes proper
 * resource cleanup mechanisms.
 */
public class GcsClient extends AbstractCrawlerClient {

    private static final Logger logger = LogManager.getLogger(GcsClient.class);

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
     * The GCS Storage client instance for interacting with Google Cloud Storage.
     */
    protected Storage storage;

    /**
     * Creates a new GcsClient instance.
     */
    public GcsClient() {
        super();
    }

    @Override
    public synchronized void init() {
        if (isInit) {
            return;
        }

        super.init();

        final String projectId = getInitParameter("projectId", null, String.class);
        if (StringUtil.isBlank(projectId)) {
            throw new CrawlingAccessException(
                    "GCS project ID is blank. Please set the GCS_PROJECT_ID environment variable or projectId parameter.");
        }

        final StorageOptions.Builder builder = StorageOptions.newBuilder().setProjectId(projectId);

        final String endpoint = getInitParameter("endpoint", null, String.class);
        if (StringUtil.isNotBlank(endpoint)) {
            // For fake-gcs-server or custom endpoint
            builder.setHost(endpoint);
            builder.setCredentials(NoCredentials.getInstance());
            if (logger.isDebugEnabled()) {
                logger.debug("Using custom GCS endpoint: {}", endpoint);
            }
        } else {
            // Production: use credentials file or default credentials
            final String credentialsFile = getInitParameter("credentialsFile", null, String.class);
            if (StringUtil.isNotBlank(credentialsFile)) {
                try (InputStream is = new FileInputStream(credentialsFile)) {
                    builder.setCredentials(ServiceAccountCredentials.fromStream(is));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Using GCS credentials from file: {}", credentialsFile);
                    }
                } catch (final Exception e) {
                    throw new CrawlingAccessException("Failed to load GCS credentials: credentialsFile=" + credentialsFile, e);
                }
            }
            // If no credentials file, GoogleCredentials.getApplicationDefault() will be used automatically
        }

        try {
            storage = builder.build().getService();
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to create GCS client: projectId=" + projectId, e);
        }

        isInit = true;
        if (logger.isInfoEnabled()) {
            logger.info("GCS client initialized successfully: projectId={}, endpoint={}", projectId,
                    endpoint != null ? endpoint : "default");
        }
    }

    /**
     * Processes a GCS request with timeout management.
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
     * Parses a GCS path into bucket name and object path components.
     * @param path the GCS path to parse (format: bucket/object/path)
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
            logger.debug("Accessing GCS object: uri={}, includeContent={}", uri, includeContent);
        }
        final ResponseData responseData = new ResponseData();
        try {
            responseData.setMethod(includeContent ? Constants.GET_METHOD : Constants.HEAD_METHOD);
            final String filePath = preprocessUri(uri);
            responseData.setUrl(filePath);

            final String[] paths = parsePath(filePath.replaceFirst("^gcs:/+", StringUtil.EMPTY));
            final String bucketName = paths[0];
            final String path = paths[1];
            if (logger.isDebugEnabled()) {
                logger.debug("Parsed GCS path: bucket={}, objectPath={}", bucketName, path);
            }
            final Blob blob = getBlob(bucketName, path);
            if (blob == null) {
                final Set<RequestData> requestDataSet = new HashSet<>();
                final Page<Blob> blobs = storage.list(bucketName, BlobListOption.prefix(path), BlobListOption.currentDirectory());
                for (final Blob item : blobs.iterateAll()) {
                    final String objectName = item.getName();
                    requestDataSet.add(RequestDataBuilder.newRequestData().get().url("gcs://" + bucketName + "/" + objectName).build());
                }
                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
            }
            // check file size
            responseData.setContentLength(blob.getSize());
            checkMaxContentLength(responseData);

            responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
            responseData.setCharSet(getCharset());
            responseData.setLastModified(
                    blob.getUpdateTimeOffsetDateTime() == null ? null : Date.from(blob.getUpdateTimeOffsetDateTime().toInstant()));
            responseData.setMimeType(blob.getContentType());

            if (contentLengthHelper != null) {
                final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                if (responseData.getContentLength() > maxLength) {
                    throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                            + maxLength + " byte. The url is " + filePath);
                }
            }

            if (includeContent) {
                // Add blob metadata as response metadata (only when content is included)
                final Map<String, String> blobMetadata = blob.getMetadata();
                if (blobMetadata != null) {
                    blobMetadata.forEach(responseData::addMetaData);
                }
                if (blob.getSize() < maxCachedContentSize) {
                    try (InputStream contentStream = new BufferedInputStream(Channels.newInputStream(blob.reader()))) {
                        responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                    } catch (final Exception e) {
                        logger.warn("Failed to read GCS object content: bucket={}, path={}, size={}", bucketName, path, blob.getSize(), e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                    }
                } else {
                    File outputFile = null;
                    try {
                        outputFile = createTempFile("crawler-GcsClient-", ".out", null);
                        try (InputStream contentStream = Channels.newInputStream(blob.reader())) {
                            CopyUtil.copy(contentStream, outputFile);
                        }
                        responseData.setResponseBody(outputFile, true);
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                    "Object size exceeds cache threshold, using temp file: bucket={}, path={}, size={}, threshold={}, tempFile={}",
                                    bucketName, path, blob.getSize(), maxCachedContentSize, outputFile.getAbsolutePath());
                        }
                    } catch (final Exception e) {
                        logger.warn("Failed to write GCS object to temp file: bucket={}, path={}, size={}, tempFile={}", bucketName, path,
                                blob.getSize(), outputFile != null ? outputFile.getAbsolutePath() : "null", e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                        FileUtil.deleteInBackground(outputFile);
                    }
                }

                final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                try (final InputStream is = responseData.getResponseBody()) {
                    responseData.setMimeType(mimeTypeHelper.getContentType(is, blob.getName()));
                } catch (final Exception e) {
                    responseData.setMimeType(mimeTypeHelper.getContentType(null, blob.getName()));
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
     * Retrieves a Blob object from the specified bucket and path.
     * @param bucketName the name of the bucket containing the object
     * @param path the path to the object within the bucket
     * @return the Blob object, or null if the object does not exist
     * @throws CrawlingAccessException if access is denied
     */
    protected Blob getBlob(final String bucketName, final String path) {
        if (StringUtil.isEmpty(path)) {
            return null;
        }

        try {
            return storage.get(BlobId.of(bucketName, path));
        } catch (final StorageException e) {
            final int code = e.getCode();
            switch (code) {
            case 404:
                if (logger.isDebugEnabled()) {
                    logger.debug("Object not found: bucket={}, path={}", bucketName, path);
                }
                break;
            case 403:
                throw new CrawlingAccessException("Access denied: bucket=" + bucketName + ", path=" + path, e);
            default:
                logger.warn("Failed to access object with error code {}: bucket={}, path={}", code, bucketName, path, e);
                break;
            }
        } catch (final Exception e) {
            logger.warn("Failed to get object: bucket={}, path={}", bucketName, path, e);
        }
        return null;
    }

    /**
     * Preprocesses a URI to ensure it has the correct GCS protocol prefix.
     * @param uri the URI to preprocess
     * @return the preprocessed URI with gcs:// prefix
     * @throws CrawlerSystemException if the URI is empty
     */
    protected String preprocessUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new CrawlerSystemException("GCS URI is empty. Please provide a valid GCS URI (gcs://...).");
        }

        String filePath = uri;
        if (!filePath.startsWith("gcs:")) {
            filePath = "gcs://" + filePath;
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
