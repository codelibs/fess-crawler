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
package org.codelibs.fess.crawler.client.azure;

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

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.BlobStorageException;
import com.azure.storage.blob.models.ListBlobsOptions;

import jakarta.annotation.Resource;

/**
 * A crawler client implementation for accessing and retrieving content from Azure Blob Storage.
 * This client supports operations on Azure Storage containers and blobs.
 *
 * <p>This client requires the following initialization parameters:
 * <ul>
 *   <li>connectionString - The Azure Storage connection string</li>
 * </ul>
 *
 * <p>The client supports URLs in the format: {@code azure://container-name/blob-name}
 *
 * @author shinsuke
 */
public class AzureBlobClient extends AbstractCrawlerClient {

    private static final Logger logger = LogManager.getLogger(AzureBlobClient.class);

    /** The character encoding to use for content. Defaults to UTF-8. */
    protected String charset = Constants.UTF_8;

    /** Helper for managing content length validation and limits. */
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    /** Flag indicating whether the client has been initialized. */
    protected volatile boolean isInit = false;

    /** The Azure Blob Service client instance. */
    protected BlobServiceClient blobServiceClient;

    /**
     * Creates a new AzureBlobClient instance.
     */
    public AzureBlobClient() {
        super();
    }

    @Override
    public synchronized void init() {
        if (isInit) {
            return;
        }

        super.init();

        final String connectionString = getInitParameter("connectionString", null, String.class);
        if (StringUtil.isBlank(connectionString)) {
            throw new CrawlingAccessException("connectionString is blank.");
        }

        try {
            blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to create Azure Blob Service client", e);
        }

        isInit = true;
        if (logger.isInfoEnabled()) {
            logger.info("Azure Blob client initialized successfully");
        }
    }

    @Override
    public void close() {
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
     * Processes an Azure Blob request with timeout management.
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
            logger.debug("Accessing Azure blob: uri={}, includeContent={}", uri, includeContent);
        }

        final ResponseData responseData = new ResponseData();
        try {
            responseData.setMethod(includeContent ? Constants.GET_METHOD : Constants.HEAD_METHOD);
            final String normalizedUri = normalizeUri(uri);
            responseData.setUrl(normalizedUri);

            final String[] paths = parsePath(normalizedUri.replaceFirst("^azure:/+", StringUtil.EMPTY));
            final String containerName = paths[0];
            final String blobName = paths[1];
            if (logger.isDebugEnabled()) {
                logger.debug("Parsed Azure path: container={}, blob={}", containerName, blobName);
            }

            final BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            final BlobClient blobClient = containerClient.getBlobClient(blobName);

            BlobProperties properties = null;
            try {
                properties = blobClient.getProperties();
            } catch (final BlobStorageException e) {
                if (e.getStatusCode() == 404) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Blob not found: container={}, blob={}", containerName, blobName);
                    }
                } else {
                    throw e;
                }
            }

            if (properties == null) {
                // Try to list blobs with prefix
                final Set<RequestData> requestDataSet = new HashSet<>();
                final ListBlobsOptions options = new ListBlobsOptions().setPrefix(blobName);

                for (final BlobItem blobItem : containerClient.listBlobs(options, null)) {
                    final String itemName = blobItem.getName();
                    requestDataSet.add(RequestDataBuilder.newRequestData().get().url("azure://" + containerName + "/" + itemName).build());
                }
                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
            }

            // Blob found
            responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(properties.getBlobSize());
            checkMaxContentLength(responseData);

            if (properties.getLastModified() != null) {
                responseData.setLastModified(Date.from(properties.getLastModified().toInstant()));
            }
            if (properties.getContentType() != null) {
                responseData.setMimeType(properties.getContentType());
            }

            // Add metadata
            if (properties.getMetadata() != null) {
                properties.getMetadata().forEach((key, value) -> responseData.addMetaData(key, value));
            }

            if (contentLengthHelper != null) {
                final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                if (responseData.getContentLength() > maxLength) {
                    throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                            + maxLength + " byte. The url is " + normalizedUri);
                }
            }

            if (includeContent) {
                // Add tags as metadata
                try {
                    blobClient.getTags().forEach((key, value) -> responseData.addMetaData("tag_" + key, value));
                } catch (final Exception e) {
                    logger.warn("Failed to get blob tags: container={}, blob={}", containerName, blobName, e);
                }

                // Get blob content
                if (properties.getBlobSize() < maxCachedContentSize) {
                    try (InputStream contentStream = new BufferedInputStream(blobClient.openInputStream())) {
                        responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                    } catch (final Exception e) {
                        logger.warn("Failed to read Azure blob content: container={}, blob={}, size={}", containerName, blobName,
                                properties.getBlobSize(), e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                    }
                } else {
                    File outputFile = null;
                    try {
                        outputFile = createTempFile("crawler-AzureBlobClient-", ".out", null);
                        try (InputStream in = blobClient.openInputStream()) {
                            CopyUtil.copy(in, outputFile);
                        }
                        responseData.setResponseBody(outputFile, true);
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                    "Blob size exceeds cache threshold, using temp file: container={}, blob={}, size={}, threshold={}, tempFile={}",
                                    containerName, blobName, properties.getBlobSize(), maxCachedContentSize, outputFile.getAbsolutePath());
                        }
                    } catch (final Exception e) {
                        logger.warn("Failed to write Azure blob to temp file: container={}, blob={}, size={}, tempFile={}", containerName,
                                blobName, properties.getBlobSize(), outputFile != null ? outputFile.getAbsolutePath() : "null", e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                        FileUtil.deleteInBackground(outputFile);
                    }
                }

                if (StringUtil.isBlank(responseData.getMimeType())) {
                    final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                    try (final InputStream is = responseData.getResponseBody()) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(is, blobName));
                    } catch (final Exception e) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(null, blobName));
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
     * Parses an Azure path into container name and blob name components.
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
        if (!normalized.startsWith("azure:")) {
            normalized = "azure://" + normalized;
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
