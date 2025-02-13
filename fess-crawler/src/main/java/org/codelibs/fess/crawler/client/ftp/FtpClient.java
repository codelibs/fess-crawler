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
package org.codelibs.fess.crawler.client.ftp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClient.NatServerResolverImpl;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilters;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.commons.net.util.TrustManagerUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.util.StringUtil;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.AbstractCrawlerClient;
import org.codelibs.fess.crawler.client.AccessTimeoutTarget;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerLoginFailureException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

import jakarta.annotation.Resource;

/**
 * FtpClient is a crawler client implementation for accessing resources via the FTP protocol.
 * It extends {@link AbstractCrawlerClient} and provides methods to retrieve content and metadata
 * from FTP servers. The client supports various configurations, including authentication, timeouts,
 * passive/active modes, and encoding settings. It also handles FTP file attributes such as user,
 * group, and symbolic links.
 *
 * <p>
 * The class uses Apache Commons Net library for FTP communication. It maintains a queue of FTPClient
 * instances to improve performance by reusing connections.
 * </p>
 *
 * <p>
 * The client can be configured with FTP-specific settings via init parameters, such as:
 * <ul>
 *   <li>ftpConfigSystemKey: The system key used to configure the FTPClientConfig.</li>
 *   <li>ftpConfigServerLanguageCode: The language code used by the FTP server.</li>
 *   <li>ftpConfigServerTimeZoneId: The time zone ID of the FTP server.</li>
 *   <li>activeExternalHost: The external IP address to use for active mode FTP.</li>
 *   <li>activeMinPort: The minimum port number for active mode FTP.</li>
 *   <li>activeMaxPort: The maximum port number for active mode FTP.</li>
 *   <li>autodetectEncoding: Whether to automatically detect UTF-8 encoding.</li>
 *   <li>connectTimeout: The timeout for establishing a connection to the FTP server.</li>
 *   <li>dataTimeout: The timeout for data transfers.</li>
 *   <li>controlEncoding: The character encoding for control messages.</li>
 *   <li>bufferSize: The buffer size for data transfers.</li>
 *   <li>passiveLocalHost: The local IP address to use for passive mode FTP.</li>
 *   <li>passiveNatWorkaround: Whether to use a NAT workaround in passive mode.</li>
 *   <li>reportActiveExternalHost: The external IP address to report in active mode.</li>
 *   <li>useEPSVwithIPv4: Whether to use EPSV with IPv4.</li>
 *   <li>isImplicit: Whether to use implicit SSL/TLS encryption.</li>
 *   <li>trustManager: The trust manager to use for SSL/TLS connections ("all", "valid", or "none").</li>
 *   <li>enterLocalPassiveMode: Whether to enter local passive mode.</li>
 *   <li>ftpAuthentications: An array of {@link FtpAuthentication} objects for different FTP URLs.</li>
 * </ul>
 * </p>
 *
 * <p>
 * The class also supports FTP authentication via {@link FtpAuthenticationHolder}, which stores
 * authentication details for different FTP URLs.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 *
 * <pre>
 * {@code
 * FtpClient ftpClient = new FtpClient();
 * ftpClient.init(); // Initialize with configured parameters
 * ResponseData responseData = ftpClient.doGet("ftp://example.com/path/to/file.txt");
 * // Process the responseData
 * ftpClient.close(); // Close and release resources
 * }
 * </pre>
 *
 * @author shinsuke
 */
public class FtpClient extends AbstractCrawlerClient {

    private static final Logger logger = LogManager.getLogger(FtpClient.class);

    public static final String FTP_FILE_GROUP = "ftpFileGroup";

    public static final String FTP_FILE_USER = "ftpFileUser";

    public static final String FTP_AUTHENTICATIONS_PROPERTY = "ftpAuthentications";

    protected String charset = Constants.UTF_8;

    @Resource
    protected ContentLengthHelper contentLengthHelper;

    protected volatile FtpAuthenticationHolder ftpAuthenticationHolder;

    protected FTPClientConfig ftpClientConfig;

    protected final Queue<FTPClient> ftpClientQueue = new ConcurrentLinkedQueue<>();

    protected String activeExternalHost;

    protected int activeMinPort;

    protected int activeMaxPort;

    protected boolean autodetectEncoding;

    protected int connectTimeout;

    protected int dataTimeout;

    protected String controlEncoding;

    protected int bufferSize;

    protected String passiveLocalHost;

    protected boolean passiveNatWorkaround;

    protected String reportActiveExternalHost;

    protected boolean useEPSVwithIPv4;

    protected Boolean isImplicit;

    protected String trustManager;

    protected boolean enterLocalPassiveMode;

    @Override
    public synchronized void init() {
        if (ftpAuthenticationHolder != null) {
            return;
        }

        super.init();

        final String systemKey = getInitParameter("ftpConfigSystemKey", FTPClientConfig.SYST_UNIX, String.class);
        ftpClientConfig = new FTPClientConfig(systemKey);

        final String serverLanguageCode = getInitParameter("ftpConfigServerLanguageCode", "en", String.class);
        ftpClientConfig.setServerLanguageCode(serverLanguageCode);

        final String serverTimeZoneId = getInitParameter("ftpConfigServerTimeZoneId", null, String.class);
        if (serverTimeZoneId != null) {
            ftpClientConfig.setServerTimeZoneId(serverTimeZoneId);
        }

        activeExternalHost = getInitParameter("activeExternalHost", null, String.class);
        activeMinPort = getInitParameter("activeMinPort", -1, Integer.class);
        activeMaxPort = getInitParameter("activeMaxPort", -1, Integer.class);
        autodetectEncoding = getInitParameter("autodetectEncoding", true, Boolean.class);
        connectTimeout = getInitParameter("connectTimeout", 0, Integer.class);
        dataTimeout = getInitParameter("dataTimeout", -1, Integer.class);
        controlEncoding = getInitParameter("controlEncoding", Constants.UTF_8, String.class);
        bufferSize = getInitParameter("bufferSize", 0, Integer.class);
        passiveLocalHost = getInitParameter("passiveLocalHost", null, String.class);
        passiveNatWorkaround = getInitParameter("passiveNatWorkaround", true, Boolean.class);
        reportActiveExternalHost = getInitParameter("reportActiveExternalHost", null, String.class);
        useEPSVwithIPv4 = getInitParameter("useEPSVwithIPv4", false, Boolean.class);
        isImplicit = getInitParameter("isImplicit", null, Boolean.class);
        trustManager = getInitParameter("trustManager", null, String.class);
        enterLocalPassiveMode = getInitParameter("enterLocalPassiveMode", false, Boolean.class);

        // Initialize FTP authentication holder
        final FtpAuthenticationHolder holder = new FtpAuthenticationHolder();
        final FtpAuthentication[] ftpAuthentications =
                getInitParameter(FTP_AUTHENTICATIONS_PROPERTY, new FtpAuthentication[0], FtpAuthentication[].class);
        if (ftpAuthentications != null) {
            for (final FtpAuthentication ftpAuthentication : ftpAuthentications) {
                holder.add(ftpAuthentication);
            }
        }
        ftpAuthenticationHolder = holder;
    }

    @Override
    public void close() {
        if (ftpAuthenticationHolder == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Closing FtpClient...");
        }
        ftpAuthenticationHolder = null;
        for (final FTPClient ftpClient : ftpClientQueue) {
            try {
                ftpClient.disconnect();
            } catch (final IOException e) {
                logger.debug("Failed to disconnect FTPClient.", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.client.S2RobotClient#doGet(java.lang.String)
     */
    @Override
    public ResponseData doGet(final String uri) {
        return processRequest(uri, true);
    }

    protected ResponseData processRequest(final String uri, final boolean includeContent) {
        if (ftpAuthenticationHolder == null) {
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

    protected ResponseData getResponseData(final String uri, final boolean includeContent) {
        final ResponseData responseData = new ResponseData();
        FTPClient client = null;
        try {
            responseData.setMethod(Constants.GET_METHOD);

            final FtpInfo ftpInfo = new FtpInfo(uri, charset);
            responseData.setUrl(ftpInfo.toUrl());

            client = getClient(ftpInfo);

            FTPFile file = null;
            client.changeWorkingDirectory(ftpInfo.getParent());
            validateRequest(client);

            if (ftpInfo.getName() == null) {
                // root directory
                final Set<RequestData> requestDataSet = new HashSet<>();
                if (includeContent) {
                    try {
                        final FTPFile[] files = client.listFiles(ftpInfo.getParent(), FTPFileFilters.NON_NULL);
                        validateRequest(client);
                        for (final FTPFile f : files) {
                            final String childUri = ftpInfo.toChildUrl(f.getName());
                            requestDataSet.add(RequestDataBuilder.newRequestData().get().url(childUri).build());
                        }
                    } catch (final IOException e) {
                        disconnectInternalClient(client);
                        throw new CrawlingAccessException("Could not access " + uri, e);
                    }
                }
                ftpClientQueue.offer(client);
                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
            }

            final FTPFile[] files = client.listFiles(null, FTPFileFilters.NON_NULL);
            validateRequest(client);
            for (final FTPFile f : files) {
                if (ftpInfo.getName().equals(f.getName())) {
                    file = f;
                    break;
                }
            }

            updateResponseData(uri, includeContent, responseData, client, ftpInfo, file);
        } catch (final CrawlerSystemException e) {
            CloseableUtil.closeQuietly(responseData);
            throw e;
        } catch (final Exception e) {
            CloseableUtil.closeQuietly(responseData);
            throw new CrawlingAccessException("Could not access " + uri, e);
        }

        return responseData;
    }

    protected void disconnectInternalClient(final FTPClient client) {
        try {
            client.disconnect();
        } catch (final IOException e) {
            logger.warn("Failed to close FTPClient", e);
        }
    }

    protected void updateResponseData(final String uri, final boolean includeContent, final ResponseData responseData,
            final FTPClient client, final FtpInfo ftpInfo, final FTPFile file) {
        if (file == null) {
            responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(0);
            ftpClientQueue.offer(client);
            return;
        }

        if (file.isSymbolicLink()) {
            final String link = file.getLink();
            String redirect = null;
            if (link == null) {
                responseData.setHttpStatusCode(Constants.BAD_REQUEST_STATUS_CODE);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
                ftpClientQueue.offer(client);
                return;
            }
            if (link.startsWith("/")) {
                redirect = ftpInfo.toUrl(file.getLink());
            } else if (link.startsWith("../")) {
                redirect = ftpInfo.toChildUrl(file.getLink());
            } else {
                redirect = ftpInfo.toChildUrl("../" + file.getLink());
            }
            if (!uri.equals(redirect)) {
                responseData.setHttpStatusCode(Constants.OK_STATUS);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
                responseData.setRedirectLocation(redirect);
                ftpClientQueue.offer(client);
                return;
            }
        }

        if (file.isFile()) {
            responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
            responseData.setCharSet(Constants.UTF_8);
            responseData.setLastModified(file.getTimestamp().getTime());

            // check file size
            responseData.setContentLength(file.getSize());
            checkMaxContentLength(responseData);

            if (file.getUser() != null) {
                responseData.addMetaData(FTP_FILE_USER, file.getUser());
            }
            if (file.getGroup() != null) {
                responseData.addMetaData(FTP_FILE_GROUP, file.getGroup());
            }

            if (includeContent) {
                File tempFile = null;
                File outputFile = null;
                try {
                    tempFile = File.createTempFile("ftp-", ".tmp");
                    try (OutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile))) {
                        if (!client.retrieveFile(ftpInfo.getName(), out)) {
                            throw new CrawlingAccessException("Failed to retrieve: " + ftpInfo.toUrl());
                        }
                    }

                    final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                    try (InputStream is = new FileInputStream(tempFile)) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(is, file.getName()));
                    } catch (final Exception e) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(null, file.getName()));
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
                        try (InputStream contentStream = new BufferedInputStream(new FileInputStream(tempFile))) {
                            responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                        }
                    } else {
                        outputFile = File.createTempFile("crawler-FtpClient-", ".out");
                        CopyUtil.copy(tempFile, outputFile);
                        responseData.setResponseBody(outputFile, true);
                    }
                    ftpClientQueue.offer(client);
                } catch (final CrawlingAccessException e) {
                    ftpClientQueue.offer(client);
                    throw e;
                } catch (final Exception e) {
                    logger.warn("I/O Exception.", e);
                    disconnectInternalClient(client);
                    responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                } finally {
                    FileUtil.deleteInBackground(tempFile);
                }
            }
        } else if (file.isDirectory() || file.isSymbolicLink()) {
            final Set<RequestData> requestDataSet = new HashSet<>();
            if (includeContent) {
                try {
                    final FTPFile[] ftpFiles = client.listFiles(ftpInfo.getName(), FTPFileFilters.NON_NULL);
                    validateRequest(client);
                    for (final FTPFile f : ftpFiles) {
                        final String chileUri = ftpInfo.toChildUrl(f.getName());
                        requestDataSet.add(RequestDataBuilder.newRequestData().get().url(chileUri).build());
                    }
                } catch (final IOException e) {
                    disconnectInternalClient(client);
                    throw new CrawlingAccessException("Could not access " + uri, e);
                }
            }
            ftpClientQueue.offer(client);
            throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
        } else {
            responseData.setHttpStatusCode(Constants.BAD_REQUEST_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(0);
            ftpClientQueue.offer(client);
        }
    }

    /**
     * Validates the FTP request by checking the reply code from the FTP server.
     * If the reply code indicates an error, a CrawlingAccessException is thrown.
     *
     * @param client the FTPClient instance to validate
     * @throws CrawlingAccessException if the FTP request failed
     */
    private void validateRequest(final FTPClient client) {
        final int replyCode = client.getReplyCode();
        if (replyCode >= 200 && replyCode < 300) {
            return;
        }
        throw new CrawlingAccessException("Failed FTP request: " + client.getReplyString().trim());
    }

    protected String getCharSet(final File file) {
        return charset;
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
     * @see org.codelibs.robot.client.S2RobotClient#doHead(java.lang.String)
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

    protected FTPClient getClient(final FtpInfo info) throws IOException {
        FTPClient ftpClient = ftpClientQueue.poll();
        if (ftpClient != null) {
            if (ftpClient.isAvailable()) {
                return ftpClient;
            }
            try {
                ftpClient.disconnect();
            } catch (final Exception e) {
                logger.debug("Failed to disconnect {}", info.toUrl(), e);
            }
        }

        try {
            if (isImplicit != null) {
                final FTPSClient ftpsClient = new FTPSClient(isImplicit);
                if ("all".equals(trustManager)) {
                    ftpsClient.setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
                } else if ("valid".equals(trustManager)) {
                    ftpsClient.setTrustManager(TrustManagerUtils.getValidateServerCertificateTrustManager());
                } else if ("none".equals(trustManager)) {
                    ftpsClient.setTrustManager(null);
                }
                ftpClient = ftpsClient;
            } else {
                ftpClient = new FTPClient();
            }

            if (activeExternalHost != null) {
                ftpClient.setActiveExternalIPAddress(activeExternalHost);
            }
            if (passiveLocalHost != null) {
                ftpClient.setPassiveLocalIPAddress(passiveLocalHost);
            }
            if (reportActiveExternalHost != null) {
                ftpClient.setReportActiveExternalIPAddress(reportActiveExternalHost);
            }
            if (activeMinPort != -1 && activeMaxPort != -1) {
                ftpClient.setActivePortRange(activeMinPort, activeMaxPort);
            }
            ftpClient.setAutodetectUTF8(autodetectEncoding);
            ftpClient.setConnectTimeout(connectTimeout);
            ftpClient.setDataTimeout(Duration.ofMillis(dataTimeout));
            ftpClient.setControlEncoding(controlEncoding);
            ftpClient.setBufferSize(bufferSize);
            if (passiveNatWorkaround) {
                ftpClient.setPassiveNatWorkaroundStrategy(new NatServerResolverImpl(ftpClient));
            }
            ftpClient.setUseEPSVwithIPv4(useEPSVwithIPv4);

            ftpClient.configure(ftpClientConfig);

            ftpClient.connect(info.getHost(), info.getPort());
            validateRequest(ftpClient);

            final FtpAuthentication auth = ftpAuthenticationHolder.get(info.toUrl());
            if (auth != null && !ftpClient.login(auth.getUsername(), auth.getPassword())) {
                throw new CrawlerLoginFailureException("Login Failure: " + auth.getUsername() + " for " + info.toUrl());
            }

            if (enterLocalPassiveMode) {
                ftpClient.enterLocalPassiveMode();
            }

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient;
        } catch (final IOException e) {
            if (ftpClient != null) {
                try {
                    ftpClient.disconnect();
                } catch (final Exception e1) {
                    logger.debug("Failed to disconnect {}", info.toUrl(), e);
                }
            }
            throw e;
        }
    }

    /**
     * FtpInfo is a helper class that encapsulates information about an FTP URL.
     * It parses the URL and provides methods to retrieve the host, port, parent directory,
     * and file name. It also provides methods to construct URLs for child paths.
     *
     * <p>
     * Usage example:
     * </p>
     *
     * <pre>
     * {@code
     * FtpInfo ftpInfo = new FtpInfo("ftp://example.com/path/to/file.txt");
     * String host = ftpInfo.getHost(); // example.com
     * int port = ftpInfo.getPort(); // 21
     * String parent = ftpInfo.getParent(); // /path/to
     * String name = ftpInfo.getName(); // file.txt
     * String childUrl = ftpInfo.toChildUrl("anotherfile.txt"); // ftp://example.com/path/to/anotherfile.txt
     * }
     * </pre>
     */
    public static class FtpInfo {

        private static final int DEFAULT_FTP_PORT = 21;

        private URI uri;

        private String parent;

        private String name;

        public FtpInfo(final String s, final String c) {
            if (StringUtil.isBlank(s)) {
                throw new CrawlingAccessException("uri is blank.");
            }

            try {
                uri = new URI(normalize(s).replace("%", "%25").replace(" ", "%20"));
            } catch (final URISyntaxException e) {
                throw new CrawlingAccessException("Invalid URL: " + s, e);
            }

            if (!"ftp".equals(uri.getScheme())) {
                throw new CrawlingAccessException("Invalid scheme: " + uri.getScheme());
            }

            final String path = uri.getPath();
            if (path == null) {
                parent = "/";
                name = null;
            } else {
                final String[] values = path.replaceAll("/+", "/").replaceFirst("/$", "").split("/");
                if (values.length == 1) {
                    parent = "/";
                    name = null;
                } else if (values.length == 2) {
                    parent = "/";
                    name = values[1];
                } else {
                    parent = StringUtils.join(values, "/", 0, values.length - 1);
                    name = values[values.length - 1];
                }
            }
        }

        protected String normalize(final String s) {
            if (s == null) {
                return null;
            }
            String url = s.replaceAll("/+", "/").replace("ftp:/", "ftp://");
            while (url.indexOf("/../") != -1) {
                url = url.replaceFirst("/[^/]+/\\.\\./", "/");
            }
            return url;
        }

        public String getCacheKey() {
            return getHost() + ":" + getPort();
        }

        public String getHost() {
            return uri.getHost();
        }

        public int getPort() {
            int port = uri.getPort();
            if (port == -1) {
                port = DEFAULT_FTP_PORT;
            }
            return port;
        }

        public String toUrl(final String path) {
            final StringBuilder buf = new StringBuilder(100);
            buf.append("ftp://");
            buf.append(getHost());
            final int port = getPort();
            if (port != DEFAULT_FTP_PORT) {
                buf.append(':').append(port);
            }
            buf.append(path);
            final String url = normalize(buf.toString());
            if ("/".equals(path)) {
                return url;
            }
            return url.replaceAll("/+$", "");
        }

        public String toUrl() {
            return toUrl(uri.getPath());
        }

        public String toChildUrl(final String child) {
            final String url = toUrl();
            if (url.endsWith("/")) {
                return normalize(toUrl() + child);
            }
            return normalize(toUrl() + "/" + child);
        }

        public String getParent() {
            return parent;
        }

        public String getName() {
            return name;
        }
    }

    public String getActiveExternalHost() {
        return activeExternalHost;
    }

    public void setActiveExternalHost(final String activeExternalHost) {
        this.activeExternalHost = activeExternalHost;
    }

    public int getActiveMinPort() {
        return activeMinPort;
    }

    public void setActiveMinPort(final int activeMinPort) {
        this.activeMinPort = activeMinPort;
    }

    public int getActiveMaxPort() {
        return activeMaxPort;
    }

    public void setActiveMaxPort(final int activeMaxPort) {
        this.activeMaxPort = activeMaxPort;
    }

    public boolean isAutodetectEncoding() {
        return autodetectEncoding;
    }

    public void setAutodetectEncoding(final boolean autodetectEncoding) {
        this.autodetectEncoding = autodetectEncoding;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getDataTimeout() {
        return dataTimeout;
    }

    public void setDataTimeout(final int dataTimeout) {
        this.dataTimeout = dataTimeout;
    }

    public String getControlEncoding() {
        return controlEncoding;
    }

    public void setControlEncoding(final String controlEncoding) {
        this.controlEncoding = controlEncoding;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public String getPassiveLocalHost() {
        return passiveLocalHost;
    }

    public void setPassiveLocalHost(final String passiveLocalHost) {
        this.passiveLocalHost = passiveLocalHost;
    }

    public boolean isPassiveNatWorkaround() {
        return passiveNatWorkaround;
    }

    public void setPassiveNatWorkaround(final boolean passiveNatWorkaround) {
        this.passiveNatWorkaround = passiveNatWorkaround;
    }

    public String getReportActiveExternalHost() {
        return reportActiveExternalHost;
    }

    public void setReportActiveExternalHost(final String reportActiveExternalHost) {
        this.reportActiveExternalHost = reportActiveExternalHost;
    }

    public boolean isUseEPSVwithIPv4() {
        return useEPSVwithIPv4;
    }

    public void setUseEPSVwithIPv4(final boolean useEPSVwithIPv4) {
        this.useEPSVwithIPv4 = useEPSVwithIPv4;
    }

    public void setFtpAuthenticationHolder(final FtpAuthenticationHolder ftpAuthenticationHolder) {
        this.ftpAuthenticationHolder = ftpAuthenticationHolder;
    }
}
