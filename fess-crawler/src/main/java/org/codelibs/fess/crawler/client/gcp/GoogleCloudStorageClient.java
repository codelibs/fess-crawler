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
package org.codelibs.fess.crawler.client.gcp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

import jakarta.annotation.Resource;

/**
 * A crawler client implementation for accessing and retrieving content from Google Cloud Storage.
 * This client supports operations on GCS buckets and objects.
 *
 * <p>This client requires the following initialization parameters:
 * <ul>
 *   <li>projectId - The GCP project ID</li>
 *   <li>credentialsFile - Path to the credentials JSON file (optional, uses default credentials if not specified)</li>
 * </ul>
 *
 * <p>The client supports URLs in the format: {@code gs://bucket-name/object-name}
 *
 * @author shinsuke
 */
public class GoogleCloudStorageClient extends AbstractCrawlerClient {

    private static final Logger logger = LogManager.getLogger(GoogleCloudStorageClient.class);

    /** The character encoding to use for content. Defaults to UTF-8. */
    protected String charset = Constants.UTF_8;

    /** Helper for managing content length validation and limits. */
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    /** Flag indicating whether the client has been initialized. */
    protected volatile boolean isInit = false;

    /** The Google Cloud Storage client instance. */
    protected Storage storage;

    /**
     * Creates a new GoogleCloudStorageClient instance.
     */
    public GoogleCloudStorageClient() {
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
            throw new CrawlingAccessException("projectId is blank.");
        }

        try {
            final StorageOptions.Builder builder = StorageOptions.newBuilder().setProjectId(projectId);

            final String credentialsFile = getInitParameter("credentialsFile", null, String.class);
            if (StringUtil.isNotBlank(credentialsFile)) {
                try (FileInputStream credentialsStream = new FileInputStream(credentialsFile)) {
                    final GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
                    builder.setCredentials(credentials);
                }
            }

            storage = builder.build().getService();
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to create Google Cloud Storage client: projectId=" + projectId, e);
        }

        isInit = true;
        if (logger.isInfoEnabled()) {
            logger.info("Google Cloud Storage client initialized successfully: projectId={}", projectId);
        }
    }

    @Override
    public void close() {
        if (storage != null) {
            try {
                storage.close();
            } catch (final Exception e) {
                logger.warn("Failed to close Google Cloud Storage client", e);
            }
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
     * Processes a GCS request with timeout management.
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
            logger.debug("Accessing GCS object: uri={}, includeContent={}", uri, includeContent);
        }

        final ResponseData responseData = new ResponseData();
        try {
            responseData.setMethod(includeContent ? Constants.GET_METHOD : Constants.HEAD_METHOD);
            final String normalizedUri = normalizeUri(uri);
            responseData.setUrl(normalizedUri);

            final String[] paths = parsePath(normalizedUri.replaceFirst("^gs:/+", StringUtil.EMPTY));
            final String bucketName = paths[0];
            final String objectName = paths[1];
            if (logger.isDebugEnabled()) {
                logger.debug("Parsed GCS path: bucket={}, object={}", bucketName, objectName);
            }

            Blob blob = null;
            try {
                blob = storage.get(BlobId.of(bucketName, objectName));
            } catch (final StorageException e) {
                if (e.getCode() == 404) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Object not found: bucket={}, object={}", bucketName, objectName);
                    }
                } else {
                    throw e;
                }
            }

            if (blob == null || !blob.exists()) {
                // Try to list objects with prefix
                final Set<RequestData> requestDataSet = new HashSet<>();
                final Iterable<Blob> blobs = storage.list(bucketName, Storage.BlobListOption.prefix(objectName)).iterateAll();

                for (final Blob blobItem : blobs) {
                    final String itemName = blobItem.getName();
                    requestDataSet.add(RequestDataBuilder.newRequestData().get().url("gs://" + bucketName + "/" + itemName).build());
                }
                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
            }

            // Object found
            responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(blob.getSize());
            checkMaxContentLength(responseData);

            if (blob.getUpdateTimeOffsetDateTime() != null) {
                responseData.setLastModified(Date.from(blob.getUpdateTimeOffsetDateTime().toInstant()));
            }
            if (blob.getContentType() != null) {
                responseData.setMimeType(blob.getContentType());
            }

            // Add metadata
            if (blob.getMetadata() != null) {
                blob.getMetadata().forEach((key, value) -> responseData.addMetaData(key, value));
            }

            if (contentLengthHelper != null) {
                final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                if (responseData.getContentLength() > maxLength) {
                    throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                            + maxLength + " byte. The url is " + normalizedUri);
                }
            }

            if (includeContent) {
                // Get object content
                if (blob.getSize() < maxCachedContentSize) {
                    try (InputStream contentStream = new BufferedInputStream(Channels.newInputStream(blob.reader()))) {
                        responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                    } catch (final Exception e) {
                        logger.warn("Failed to read GCS object content: bucket={}, object={}, size={}", bucketName, objectName,
                                blob.getSize(), e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                    }
                } else {
                    File outputFile = null;
                    try {
                        outputFile = createTempFile("crawler-GoogleCloudStorageClient-", ".out", null);
                        blob.downloadTo(outputFile.toPath());
                        responseData.setResponseBody(outputFile, true);
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                    "Object size exceeds cache threshold, using temp file: bucket={}, object={}, size={}, threshold={}, tempFile={}",
                                    bucketName, objectName, blob.getSize(), maxCachedContentSize, outputFile.getAbsolutePath());
                        }
                    } catch (final Exception e) {
                        logger.warn("Failed to write GCS object to temp file: bucket={}, object={}, size={}, tempFile={}", bucketName,
                                objectName, blob.getSize(), outputFile != null ? outputFile.getAbsolutePath() : "null", e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                        FileUtil.deleteInBackground(outputFile);
                    }
                }

                if (StringUtil.isBlank(responseData.getMimeType())) {
                    final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                    try (final InputStream is = responseData.getResponseBody()) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(is, objectName));
                    } catch (final Exception e) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(null, objectName));
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
     * Parses a GCS path into bucket name and object name components.
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
        if (!normalized.startsWith("gs:")) {
            normalized = "gs://" + normalized;
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
