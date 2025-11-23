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
package org.codelibs.fess.crawler.client.sftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import jakarta.annotation.Resource;

/**
 * SftpClient is a crawler client implementation for accessing resources via the SFTP protocol.
 * It extends {@link AbstractCrawlerClient} and provides methods to retrieve content and metadata
 * from SFTP servers. The client supports various configurations, including authentication, timeouts,
 * and encoding settings.
 *
 * <p>
 * The class uses JSch library for SFTP communication. It maintains a queue of ChannelSftp
 * instances to improve performance by reusing connections.
 * </p>
 *
 * <p>
 * The client can be configured with SFTP-specific settings via init parameters, such as:
 * </p>
 * <ul>
 *   <li>connectTimeout: The timeout for establishing a connection to the SFTP server.</li>
 *   <li>charset: The character encoding for file operations.</li>
 *   <li>strictHostKeyChecking: Whether to strictly check host keys (default: no).</li>
 *   <li>sftpAuthentications: An array of {@link SftpAuthentication} objects for different SFTP URLs.</li>
 * </ul>
 *
 * @author shinsuke
 */
public class SftpClient extends AbstractCrawlerClient {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(SftpClient.class);

    /** Metadata key for SFTP file owner */
    public static final String SFTP_FILE_OWNER = "sftpFileOwner";

    /** Metadata key for SFTP file group */
    public static final String SFTP_FILE_GROUP = "sftpFileGroup";

    /** Metadata key for SFTP file permissions */
    public static final String SFTP_FILE_PERMISSIONS = "sftpFilePermissions";

    /** Property name for SFTP authentications */
    public static final String SFTP_AUTHENTICATIONS_PROPERTY = "sftpAuthentications";

    /** Character encoding for SFTP operations */
    protected String charset = Constants.UTF_8;

    /** Helper for managing content length limits */
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    /** The SFTP authentication holder */
    protected volatile SftpAuthenticationHolder sftpAuthenticationHolder;

    /** The queue of ChannelSftp instances */
    protected final Queue<ChannelSftp> sftpChannelQueue = new ConcurrentLinkedQueue<>();

    /** The queue of Session instances */
    protected final Queue<Session> sessionQueue = new ConcurrentLinkedQueue<>();

    /** The connect timeout */
    protected int connectTimeout = 10000;

    /** Whether to strictly check host keys */
    protected String strictHostKeyChecking = "no";

    /**
     * Creates a new SftpClient instance.
     */
    public SftpClient() {
        // Default constructor
    }

    @Override
    public synchronized void init() {
        if (sftpAuthenticationHolder != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing SftpClient...");
        }

        super.init();

        connectTimeout = getInitParameter("connectTimeout", connectTimeout, Integer.class);
        strictHostKeyChecking = getInitParameter("strictHostKeyChecking", strictHostKeyChecking, String.class);

        // Initialize SFTP authentication holder
        final SftpAuthenticationHolder holder = new SftpAuthenticationHolder();
        final SftpAuthentication[] sftpAuthentications =
                getInitParameter(SFTP_AUTHENTICATIONS_PROPERTY, new SftpAuthentication[0], SftpAuthentication[].class);
        if (sftpAuthentications != null) {
            for (final SftpAuthentication sftpAuthentication : sftpAuthentications) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding SftpAuthentication: {}", sftpAuthentication);
                }
                holder.add(sftpAuthentication);
            }
        }
        sftpAuthenticationHolder = holder;

        if (logger.isInfoEnabled()) {
            logger.info("SFTP client initialized successfully: connectTimeout={}ms, strictHostKeyChecking={}", connectTimeout,
                    strictHostKeyChecking);
        }
    }

    @Override
    public void close() {
        if (sftpAuthenticationHolder == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Closing SftpClient...");
        }
        sftpAuthenticationHolder = null;

        for (final ChannelSftp channel : sftpChannelQueue) {
            try {
                if (channel.isConnected()) {
                    channel.disconnect();
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to disconnect SFTP channel: connected={}", channel.isConnected(), e);
                }
            }
        }

        for (final Session session : sessionQueue) {
            try {
                if (session.isConnected()) {
                    session.disconnect();
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to disconnect SFTP session: connected={}", session.isConnected(), e);
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("SFTP client closed");
        }
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
     * Processes an SFTP request to retrieve data from the specified URI.
     *
     * @param uri The URI to retrieve data from
     * @param includeContent Whether to include the actual content in the response
     * @return The response data containing the retrieved information
     * @throws CrawlingAccessException If the SFTP request fails
     */
    protected ResponseData processRequest(final String uri, final boolean includeContent) {
        if (sftpAuthenticationHolder == null) {
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
     * Retrieves response data from the SFTP server for the specified URI.
     *
     * @param uri The URI to retrieve data from
     * @param includeContent Whether to include the actual content in the response
     * @return The response data containing the retrieved information
     * @throws CrawlingAccessException If the SFTP operation fails
     */
    protected ResponseData getResponseData(final String uri, final boolean includeContent) {
        final ResponseData responseData = new ResponseData();
        ChannelSftp channel = null;
        try {
            responseData.setMethod(Constants.GET_METHOD);

            final SftpInfo sftpInfo = new SftpInfo(uri, charset);
            responseData.setUrl(sftpInfo.toUrl());

            channel = getChannel(sftpInfo);

            if (sftpInfo.getPath() == null || sftpInfo.getPath().isEmpty() || "/".equals(sftpInfo.getPath())) {
                // root directory
                final Set<RequestData> requestDataSet = new HashSet<>();
                if (includeContent) {
                    try {
                        @SuppressWarnings("unchecked")
                        final Vector<ChannelSftp.LsEntry> files = channel.ls("/");
                        for (final ChannelSftp.LsEntry entry : files) {
                            if (!".".equals(entry.getFilename()) && !"..".equals(entry.getFilename())) {
                                final String childUri = sftpInfo.toChildUrl(entry.getFilename());
                                requestDataSet.add(RequestDataBuilder.newRequestData().get().url(childUri).build());
                            }
                        }
                    } catch (final SftpException e) {
                        disconnectInternalChannel(channel);
                        throw new CrawlingAccessException("Could not access " + uri, e);
                    }
                }
                sftpChannelQueue.offer(channel);
                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
            }

            SftpATTRS attrs = null;
            try {
                attrs = channel.stat(sftpInfo.getPath());
            } catch (final SftpException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("File not found: {}", sftpInfo.getPath());
                }
            }

            updateResponseData(uri, includeContent, responseData, channel, sftpInfo, attrs);
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
     * Disconnects the internal SFTP channel and logs any errors.
     *
     * @param channel The SFTP channel to disconnect
     */
    protected void disconnectInternalChannel(final ChannelSftp channel) {
        try {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        } catch (final Exception e) {
            logger.warn("Failed to disconnect SFTP channel: connected={}", channel.isConnected(), e);
        }
    }

    /**
     * Updates the response data based on the SFTP file information.
     *
     * @param uri The original URI being accessed
     * @param includeContent Whether to include the actual content in the response
     * @param responseData The response data to update
     * @param channel The SFTP channel used for the operation
     * @param sftpInfo Information about the SFTP connection
     * @param attrs The SFTP file attributes, or null if not found
     */
    protected void updateResponseData(final String uri, final boolean includeContent, final ResponseData responseData,
            final ChannelSftp channel, final SftpInfo sftpInfo, final SftpATTRS attrs) {
        if (attrs == null) {
            responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(0);
            sftpChannelQueue.offer(channel);
            return;
        }

        if (attrs.isDir()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Processing SFTP directory: {}", sftpInfo.getPath());
            }
            final Set<RequestData> requestDataSet = new HashSet<>();
            if (includeContent) {
                try {
                    @SuppressWarnings("unchecked")
                    final Vector<ChannelSftp.LsEntry> files = channel.ls(sftpInfo.getPath());
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found {} entries in directory: {}", files.size(), sftpInfo.getPath());
                    }
                    for (final ChannelSftp.LsEntry entry : files) {
                        if (!".".equals(entry.getFilename()) && !"..".equals(entry.getFilename())) {
                            final String childUri = sftpInfo.toChildUrl(entry.getFilename());
                            requestDataSet.add(RequestDataBuilder.newRequestData().get().url(childUri).build());
                        }
                    }
                } catch (final SftpException e) {
                    disconnectInternalChannel(channel);
                    throw new CrawlingAccessException("Could not access " + uri, e);
                }
            }
            sftpChannelQueue.offer(channel);
            throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
        }

        if (attrs.isReg()) {
            responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
            responseData.setCharSet(Constants.UTF_8);
            responseData.setLastModified(new Date(attrs.getMTime() * 1000L));

            // check file size
            responseData.setContentLength(attrs.getSize());
            checkMaxContentLength(responseData);

            // Set file metadata
            responseData.addMetaData(SFTP_FILE_OWNER, String.valueOf(attrs.getUId()));
            responseData.addMetaData(SFTP_FILE_GROUP, String.valueOf(attrs.getGId()));
            responseData.addMetaData(SFTP_FILE_PERMISSIONS, attrs.getPermissionsString());

            if (includeContent) {
                File tempFile = null;
                File outputFile = null;
                try {
                    tempFile = createTempFile("sftp-", ".tmp", null);
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile));
                            InputStream in = channel.get(sftpInfo.getPath())) {
                        CopyUtil.copy(in, out);
                    }

                    final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                    try (InputStream is = new BufferedInputStream(new java.io.FileInputStream(tempFile))) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(is, sftpInfo.getFilename()));
                    } catch (final Exception e) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(null, sftpInfo.getFilename()));
                    }

                    if (contentLengthHelper != null) {
                        final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                        if (responseData.getContentLength() > maxLength) {
                            throw new MaxLengthExceededException("The content length (" + responseData.getContentLength()
                                    + " byte) is over " + maxLength + " byte. The url is " + uri);
                        }
                    }

                    responseData.setCharSet(getCharSet(tempFile));

                    if (tempFile.length() < maxCachedContentSize) {
                        try (InputStream contentStream = new BufferedInputStream(new java.io.FileInputStream(tempFile))) {
                            responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                        }
                    } else {
                        outputFile = createTempFile("crawler-SftpClient-", ".out", null);
                        CopyUtil.copy(tempFile, outputFile);
                        responseData.setResponseBody(outputFile, true);
                        if (logger.isDebugEnabled()) {
                            logger.debug(
                                    "File size exceeds cache threshold, using temp file: path={}, size={}, threshold={}, tempFile={}",
                                    sftpInfo.getPath(), attrs.getSize(), maxCachedContentSize, outputFile.getAbsolutePath());
                        }
                    }
                    sftpChannelQueue.offer(channel);
                } catch (final CrawlingAccessException e) {
                    sftpChannelQueue.offer(channel);
                    throw e;
                } catch (final Exception e) {
                    logger.warn("Failed to retrieve SFTP file content: uri={}, path={}, size={}", uri, sftpInfo.getPath(), attrs.getSize(),
                            e);
                    disconnectInternalChannel(channel);
                    responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                } finally {
                    FileUtil.deleteInBackground(tempFile);
                }
            } else {
                sftpChannelQueue.offer(channel);
            }
        } else {
            responseData.setHttpStatusCode(Constants.BAD_REQUEST_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(0);
            sftpChannelQueue.offer(channel);
        }
    }

    /**
     * Determines the character set for the given file.
     *
     * @param file The file to determine the charset for
     * @return The character set name
     */
    protected String getCharSet(final File file) {
        return charset;
    }

    /**
     * Gets the character encoding used for SFTP operations.
     *
     * @return The character encoding
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the character encoding used for SFTP operations.
     *
     * @param charset The character encoding to set
     */
    public void setCharset(final String charset) {
        this.charset = charset;
    }

    /**
     * Gets or creates an SFTP channel for the specified SFTP information.
     *
     * @param info The SFTP information containing host, port, and other connection details
     * @return A configured SFTP channel ready for use
     * @throws JSchException If the SFTP channel cannot be created or connected
     */
    protected ChannelSftp getChannel(final SftpInfo info) throws JSchException {
        ChannelSftp channel = sftpChannelQueue.poll();
        if (channel != null && channel.isConnected()) {
            return channel;
        }

        Session session = null;
        try {
            final JSch jsch = new JSch();
            final SftpAuthentication auth = sftpAuthenticationHolder.get(info.toUrl());

            if (auth != null && StringUtil.isNotBlank(auth.getPrivateKey())) {
                if (StringUtil.isNotBlank(auth.getPassphrase())) {
                    jsch.addIdentity("sftp-key", auth.getPrivateKey().getBytes(Constants.UTF_8), null,
                            auth.getPassphrase().getBytes(Constants.UTF_8));
                } else {
                    jsch.addIdentity("sftp-key", auth.getPrivateKey().getBytes(Constants.UTF_8), null, null);
                }
            }

            final int port = auth != null ? auth.getPort() : info.getPort();
            final String username = auth != null && auth.getUsername() != null ? auth.getUsername() : "anonymous";

            session = jsch.getSession(username, info.getHost(), port);

            if (auth != null && StringUtil.isNotBlank(auth.getPassword())) {
                session.setPassword(auth.getPassword());
            }

            session.setConfig("StrictHostKeyChecking", strictHostKeyChecking);
            session.setTimeout(connectTimeout);
            session.connect();

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();

            sessionQueue.offer(session);
            return channel;
        } catch (final JSchException e) {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
            throw e;
        }
    }

    /**
     * SftpInfo is a helper class that encapsulates information about an SFTP URL.
     */
    public static class SftpInfo {

        private static final int DEFAULT_SFTP_PORT = 22;

        private URI uri;

        private String path;

        /**
         * Constructs a new SftpInfo from a URL string.
         *
         * @param s The URL string to parse
         * @param c The character encoding (not currently used)
         * @throws CrawlingAccessException If the URL is invalid or malformed
         */
        public SftpInfo(final String s, final String c) {
            if (StringUtil.isBlank(s)) {
                throw new CrawlingAccessException("uri is blank.");
            }

            try {
                uri = new URI(normalize(s).replace(" ", "%20"));
            } catch (final URISyntaxException e) {
                throw new CrawlingAccessException("Invalid URL: " + s, e);
            }

            if (!"sftp".equals(uri.getScheme())) {
                throw new CrawlingAccessException("Invalid scheme: " + uri.getScheme());
            }

            path = uri.getPath();
            if (path == null || path.isEmpty()) {
                path = "/";
            }
        }

        /**
         * Normalizes the URL string.
         *
         * @param s The URL string to normalize
         * @return The normalized URL string
         */
        protected String normalize(final String s) {
            if (s == null) {
                return null;
            }
            String url = s.replaceAll("/+", "/").replace("sftp:/", "sftp://");
            while (url.indexOf("/../") != -1) {
                url = url.replaceFirst("/[^/]+/\\.\\./", "/");
            }
            return url;
        }

        /**
         * Gets the host name from the SFTP URL.
         *
         * @return The host name
         */
        public String getHost() {
            return uri.getHost();
        }

        /**
         * Gets the port number from the SFTP URL.
         *
         * @return The port number
         */
        public int getPort() {
            int port = uri.getPort();
            if (port == -1) {
                port = DEFAULT_SFTP_PORT;
            }
            return port;
        }

        /**
         * Gets the path from the SFTP URL.
         *
         * @return The path
         */
        public String getPath() {
            return path;
        }

        /**
         * Gets the filename from the path.
         *
         * @return The filename
         */
        public String getFilename() {
            if (path == null || path.isEmpty() || "/".equals(path)) {
                return "";
            }
            final int index = path.lastIndexOf('/');
            if (index >= 0 && index < path.length() - 1) {
                return path.substring(index + 1);
            }
            return path;
        }

        /**
         * Constructs a complete SFTP URL.
         *
         * @return The complete SFTP URL
         */
        public String toUrl() {
            final StringBuilder buf = new StringBuilder(100);
            buf.append("sftp://");
            buf.append(getHost());
            final int port = getPort();
            if (port != DEFAULT_SFTP_PORT) {
                buf.append(':').append(port);
            }
            buf.append(path);
            return normalize(buf.toString());
        }

        /**
         * Constructs a child URL by appending the specified child path.
         *
         * @param child The child path to append
         * @return The complete child URL
         */
        public String toChildUrl(final String child) {
            final String url = toUrl();
            if (url.endsWith("/")) {
                return normalize(url + child);
            }
            return normalize(url + "/" + child);
        }
    }

    /**
     * Gets the connection timeout in milliseconds.
     *
     * @return The connection timeout
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Sets the connection timeout in milliseconds.
     *
     * @param connectTimeout The connection timeout
     */
    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Gets the strict host key checking setting.
     *
     * @return The strict host key checking setting
     */
    public String getStrictHostKeyChecking() {
        return strictHostKeyChecking;
    }

    /**
     * Sets the strict host key checking setting.
     *
     * @param strictHostKeyChecking The strict host key checking setting
     */
    public void setStrictHostKeyChecking(final String strictHostKeyChecking) {
        this.strictHostKeyChecking = strictHostKeyChecking;
    }

    /**
     * Sets the SFTP authentication holder.
     *
     * @param sftpAuthenticationHolder The SFTP authentication holder to set
     */
    public void setSftpAuthenticationHolder(final SftpAuthenticationHolder sftpAuthenticationHolder) {
        this.sftpAuthenticationHolder = sftpAuthenticationHolder;
    }
}
