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
package org.codelibs.fess.crawler.client.webdav;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

import jakarta.annotation.Resource;

/**
 * WebDavClient is a crawler client implementation for accessing resources via the WebDAV protocol.
 * It extends {@link AbstractCrawlerClient} and provides methods to retrieve content and metadata
 * from WebDAV servers. The client supports various configurations, including authentication and timeouts.
 *
 * <p>
 * The class uses Sardine library for WebDAV communication.
 * </p>
 *
 * <p>
 * The client can be configured with WebDAV-specific settings via init parameters, such as:
 * </p>
 * <ul>
 *   <li>charset: The character encoding for file operations.</li>
 *   <li>webDavAuthentications: An array of {@link WebDavAuthentication} objects for different WebDAV URLs.</li>
 * </ul>
 *
 * @author shinsuke
 */
public class WebDavClient extends AbstractCrawlerClient {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(WebDavClient.class);

    /** Property name for WebDAV authentications */
    public static final String WEBDAV_AUTHENTICATIONS_PROPERTY = "webDavAuthentications";

    /** Character encoding for WebDAV operations */
    protected String charset = Constants.UTF_8;

    /** Helper for managing content length limits */
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    /** The WebDAV authentication holder */
    protected volatile WebDavAuthenticationHolder webDavAuthenticationHolder;

    /**
     * Creates a new WebDavClient instance.
     */
    public WebDavClient() {
        // Default constructor
    }

    @Override
    public synchronized void init() {
        if (webDavAuthenticationHolder != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing WebDavClient...");
        }

        super.init();

        // Initialize WebDAV authentication holder
        final WebDavAuthenticationHolder holder = new WebDavAuthenticationHolder();
        final WebDavAuthentication[] webDavAuthentications =
                getInitParameter(WEBDAV_AUTHENTICATIONS_PROPERTY, new WebDavAuthentication[0], WebDavAuthentication[].class);
        if (webDavAuthentications != null) {
            for (final WebDavAuthentication webDavAuthentication : webDavAuthentications) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding WebDavAuthentication: {}", webDavAuthentication);
                }
                holder.add(webDavAuthentication);
            }
        }
        webDavAuthenticationHolder = holder;

        if (logger.isInfoEnabled()) {
            logger.info("WebDAV client initialized successfully");
        }
    }

    @Override
    public void close() {
        if (webDavAuthenticationHolder == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Closing WebDavClient...");
        }
        webDavAuthenticationHolder = null;
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
     * Processes a WebDAV request to retrieve data from the specified URI.
     *
     * @param uri The URI to retrieve data from
     * @param includeContent Whether to include the actual content in the response
     * @return The response data containing the retrieved information
     * @throws CrawlingAccessException If the WebDAV request fails
     */
    protected ResponseData processRequest(final String uri, final boolean includeContent) {
        if (webDavAuthenticationHolder == null) {
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
     * Retrieves response data from the WebDAV server for the specified URI.
     *
     * @param uri The URI to retrieve data from
     * @param includeContent Whether to include the actual content in the response
     * @return The response data containing the retrieved information
     * @throws CrawlingAccessException If the WebDAV operation fails
     */
    protected ResponseData getResponseData(final String uri, final boolean includeContent) {
        if (logger.isDebugEnabled()) {
            logger.debug("Accessing WebDAV resource: uri={}, includeContent={}", uri, includeContent);
        }

        final ResponseData responseData = new ResponseData();
        try {
            responseData.setMethod(includeContent ? Constants.GET_METHOD : Constants.HEAD_METHOD);
            final String normalizedUri = normalizeUri(uri);
            responseData.setUrl(normalizedUri);

            final Sardine sardine = createSardine(normalizedUri);

            if (!sardine.exists(normalizedUri)) {
                responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
                return responseData;
            }

            final List<DavResource> resources = sardine.list(normalizedUri, 0);
            if (resources == null || resources.isEmpty()) {
                responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
                return responseData;
            }

            final DavResource resource = resources.get(0);

            if (resource.isDirectory()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Processing WebDAV directory: {}", normalizedUri);
                }
                final Set<RequestData> requestDataSet = new HashSet<>();
                if (includeContent) {
                    final List<DavResource> children = sardine.list(normalizedUri, 1);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found {} entries in directory: {}", children.size(), normalizedUri);
                    }
                    for (int i = 1; i < children.size(); i++) {
                        final DavResource child = children.get(i);
                        final String childUri = child.getHref().toString();
                        requestDataSet.add(RequestDataBuilder.newRequestData().get().url(childUri).build());
                    }
                }
                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
            }

            // File resource
            responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(resource.getContentLength());
            checkMaxContentLength(responseData);

            if (resource.getModified() != null) {
                responseData.setLastModified(resource.getModified());
            } else if (resource.getCreation() != null) {
                responseData.setLastModified(resource.getCreation());
            } else {
                responseData.setLastModified(new Date());
            }

            if (StringUtil.isNotBlank(resource.getContentType())) {
                responseData.setMimeType(resource.getContentType());
            }

            if (contentLengthHelper != null) {
                final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                if (responseData.getContentLength() > maxLength) {
                    throw new MaxLengthExceededException("The content length (" + responseData.getContentLength() + " byte) is over "
                            + maxLength + " byte. The url is " + normalizedUri);
                }
            }

            if (includeContent) {
                if (resource.getContentLength() < maxCachedContentSize) {
                    try (InputStream contentStream = new BufferedInputStream(sardine.get(normalizedUri))) {
                        responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                    } catch (final Exception e) {
                        logger.warn("Failed to read WebDAV content: uri={}, size={}", normalizedUri, resource.getContentLength(), e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                    }
                } else {
                    File outputFile = null;
                    try {
                        outputFile = createTempFile("crawler-WebDavClient-", ".out", null);
                        try (InputStream in = sardine.get(normalizedUri)) {
                            CopyUtil.copy(in, outputFile);
                        }
                        responseData.setResponseBody(outputFile, true);
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                    "File size exceeds cache threshold, using temp file: uri={}, size={}, threshold={}, tempFile={}",
                                    normalizedUri, resource.getContentLength(), maxCachedContentSize, outputFile.getAbsolutePath());
                        }
                    } catch (final Exception e) {
                        logger.warn("Failed to write WebDAV content to temp file: uri={}, size={}, tempFile={}", normalizedUri,
                                resource.getContentLength(), outputFile != null ? outputFile.getAbsolutePath() : "null", e);
                        responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                        FileUtil.deleteInBackground(outputFile);
                    }
                }

                if (StringUtil.isBlank(responseData.getMimeType())) {
                    final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                    try (final InputStream is = responseData.getResponseBody()) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(is, getFileName(normalizedUri)));
                    } catch (final Exception e) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(null, getFileName(normalizedUri)));
                    }
                }
            }

        } catch (final CrawlerSystemException e) {
            CloseableUtil.closeQuietly(responseData);
            throw e;
        } catch (final IOException e) {
            CloseableUtil.closeQuietly(responseData);
            throw new CrawlingAccessException("Could not access " + uri, e);
        } catch (final Exception e) {
            CloseableUtil.closeQuietly(responseData);
            throw new CrawlingAccessException("Could not access " + uri, e);
        }

        return responseData;
    }

    /**
     * Creates a Sardine client for the given URI.
     *
     * @param uri The URI to create a client for
     * @return A Sardine client
     */
    protected Sardine createSardine(final String uri) {
        final WebDavAuthentication auth = webDavAuthenticationHolder.get(uri);
        if (auth != null && StringUtil.isNotBlank(auth.getUsername())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Creating authenticated Sardine client for: {}", uri);
            }
            return SardineFactory.begin(auth.getUsername(), auth.getPassword());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Creating anonymous Sardine client for: {}", uri);
        }
        return SardineFactory.begin();
    }

    /**
     * Normalizes the URI.
     *
     * @param uri The URI to normalize
     * @return The normalized URI
     * @throws CrawlerSystemException If the URI is empty
     */
    protected String normalizeUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new CrawlerSystemException("The uri is empty.");
        }
        return uri;
    }

    /**
     * Extracts the file name from the URI.
     *
     * @param uri The URI
     * @return The file name
     */
    protected String getFileName(final String uri) {
        if (uri == null) {
            return "";
        }
        final int index = uri.lastIndexOf('/');
        if (index >= 0 && index < uri.length() - 1) {
            return uri.substring(index + 1);
        }
        return uri;
    }

    /**
     * Gets the character encoding used for WebDAV operations.
     *
     * @return The character encoding
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the character encoding used for WebDAV operations.
     *
     * @param charset The character encoding to set
     */
    public void setCharset(final String charset) {
        this.charset = charset;
    }

    /**
     * Sets the WebDAV authentication holder.
     *
     * @param webDavAuthenticationHolder The WebDAV authentication holder to set
     */
    public void setWebDavAuthenticationHolder(final WebDavAuthenticationHolder webDavAuthenticationHolder) {
        this.webDavAuthenticationHolder = webDavAuthenticationHolder;
    }
}
