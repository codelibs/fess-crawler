/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.client.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.MaxLengthExceededException;
import org.seasar.robot.RobotCrawlAccessException;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.S2RobotContext;
import org.seasar.robot.client.AbstractS2RobotClient;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.RobotsTxt;
import org.seasar.robot.helper.ContentLengthHelper;
import org.seasar.robot.helper.RobotsTxtHelper;
import org.seasar.robot.util.CrawlingParameterUtil;
import org.seasar.robot.util.StreamUtil;
import org.seasar.robot.util.TemporaryFileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * 
 */
public class CommonsHttpClient extends AbstractS2RobotClient {

    public static final String CONNECTION_TIMEOUT_PROPERTY =
        "connectionTimeout";

    public static final String MAX_TOTAL_CONNECTIONS_PROPERTY =
        "maxTotalConnections";

    public static final String STALE_CHECKING_ENABLED_PROPERTY =
        "staleCheckingEnabled";

    public static final String SO_TIMEOUT_PROPERTY = "soTimeout";

    public static final String LINGER_PROPERTY = "linger";

    public static final String PROXY_HOST_PROPERTY = "proxyHost";

    public static final String PROXY_PORT_PROPERTY = "proxyPort";

    public static final String PROXY_CREDENTIALS_PROPERTY = "proxyCredentials";

    public static final String USER_AGENT_PROPERTY = "userAgent";

    public static final String BASIC_AUTHENTICATIONS_PROPERTY =
        "basicAuthentications";

    public static final String REQUERT_HEADERS_PROPERTY = "requestHeaders";

    private static final Logger logger = LoggerFactory // NOPMD
        .getLogger(CommonsHttpClient.class);

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected RobotsTxtHelper robotsTxtHelper;

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    public Integer connectionTimeout;

    public Integer maxTotalConnections;

    public Boolean staleCheckingEnabled;

    public Integer soTimeout;

    public Integer linger;

    public String cookiePolicy;

    public String userAgent = "S2Robot";

    protected volatile org.apache.commons.httpclient.HttpClient httpClient; // NOPMD

    public String proxyHost;

    public Integer proxyPort;

    @Binding(bindingType = BindingType.MAY)
    public Credentials proxyCredentials;

    public int responseBodyInMemoryThresholdSize = 1 * 1024 * 1024; // 1M

    private final List<Header> requestHeaderList = new ArrayList<Header>();

    public String defaultMimeType = "application/octet-stream";

    public synchronized void init() {
        if (httpClient != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing " + CommonsHttpClient.class.getName());
        }

        final MultiThreadedHttpConnectionManager connectionManager =
            new MultiThreadedHttpConnectionManager();
        final HttpConnectionManagerParams params =
            new HttpConnectionManagerParams();
        final Integer connectionTimeout =
            getInitParameter(
                CONNECTION_TIMEOUT_PROPERTY,
                this.connectionTimeout);
        if (connectionTimeout != null) {
            params.setConnectionTimeout(connectionTimeout);
        }
        final Integer maxTotalConnections =
            getInitParameter(
                MAX_TOTAL_CONNECTIONS_PROPERTY,
                this.maxTotalConnections);
        if (maxTotalConnections != null) {
            params.setMaxTotalConnections(maxTotalConnections);
        }
        final Boolean staleCheckingEnabled =
            getInitParameter(
                STALE_CHECKING_ENABLED_PROPERTY,
                this.staleCheckingEnabled);
        if (staleCheckingEnabled != null) {
            params.setStaleCheckingEnabled(staleCheckingEnabled);
        }
        final Integer soTimeout =
            getInitParameter(SO_TIMEOUT_PROPERTY, this.soTimeout);
        if (soTimeout != null) {
            params.setSoTimeout(soTimeout);
        }
        final Integer linger = getInitParameter(LINGER_PROPERTY, this.linger);
        if (linger != null) {
            params.setLinger(linger);
        }
        connectionManager.setParams(params);

        httpClient =
            new org.apache.commons.httpclient.HttpClient(connectionManager);

        // proxy
        final String proxyHost =
            getInitParameter(PROXY_HOST_PROPERTY, this.proxyHost);
        final Integer proxyPort =
            getInitParameter(PROXY_PORT_PROPERTY, this.proxyPort);
        if (proxyHost != null && proxyPort != null) {
            httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
            final Credentials proxyCredentials =
                getInitParameter(
                    PROXY_CREDENTIALS_PROPERTY,
                    this.proxyCredentials);
            if (proxyCredentials != null) {
                httpClient.getState().setProxyCredentials(
                    new AuthScope(proxyHost, proxyPort),
                    proxyCredentials);
            }
        }

        // user agent
        final String userAgent =
            getInitParameter(USER_AGENT_PROPERTY, this.userAgent);
        if (StringUtil.isNotBlank(userAgent)) {
            httpClient.getParams().setParameter(
                HttpMethodParams.USER_AGENT,
                userAgent);
        }

        // Basic Authentication
        final HttpState httpState = httpClient.getState();
        final BasicAuthentication[] siteCredentialList =
            getInitParameter(
                BASIC_AUTHENTICATIONS_PROPERTY,
                new BasicAuthentication[0]);
        for (final BasicAuthentication basicAuthentication : siteCredentialList) {
            httpState.setCredentials(
                basicAuthentication.getAuthScope(),
                basicAuthentication.getCredentials());
        }

        // Request Header
        final RequestHeader[] requestHeaders =
            getInitParameter(REQUERT_HEADERS_PROPERTY, new RequestHeader[0]);
        for (final RequestHeader requestHeader : requestHeaders) {
            if (requestHeader.isValid()) {
                requestHeaderList.add(new Header(
                    requestHeader.getName(),
                    requestHeader.getValue()));
            }
        }
    }

    protected void processRobotsTxt(final String url) {
        if (StringUtil.isBlank(url)) {
            throw new RobotSystemException("url is null or empty.");
        }

        if (robotsTxtHelper == null) {
            // not support robots.txt
            return;
        }

        // robot context
        final S2RobotContext robotContext =
            CrawlingParameterUtil.getRobotContext();
        if (robotContext == null) {
            // wrong state
            return;
        }

        final int idx = url.indexOf('/', url.indexOf("://") + 3);
        String hostUrl;
        if (idx >= 0) {
            hostUrl = url.substring(0, idx);
        } else {
            hostUrl = url;
        }
        final String robotTxtUrl = hostUrl + "/robots.txt";

        // check url
        if (robotContext.getRobotTxtUrlSet().contains(robotTxtUrl)) {
            if (logger.isDebugEnabled()) {
                logger.debug(robotTxtUrl + " is already visited.");
            }
            return;
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Checking URL: " + robotTxtUrl);
            }
            // add url to a set
            robotContext.getRobotTxtUrlSet().add(robotTxtUrl);
        }

        final GetMethod getMethod = new GetMethod(robotTxtUrl);

        // cookie
        if (cookiePolicy != null) {
            getMethod.getParams().setCookiePolicy(cookiePolicy);
        }

        // request header
        for (final Header header : requestHeaderList) {
            getMethod.setRequestHeader(header);
        }

        try { // get a content
            httpClient.executeMethod(getMethod);

            final int httpStatusCode = getMethod.getStatusCode();
            if (httpStatusCode == 200) {

                // check file size
                final Header contentLengthHeader =
                    getMethod.getResponseHeader("Content-Length");
                if (contentLengthHeader != null) {
                    final String value = contentLengthHeader.getValue();
                    final long contentLength = Long.parseLong(value);
                    if (contentLengthHelper != null) {
                        final long maxLength =
                            contentLengthHelper.getMaxLength("text/plain");
                        if (contentLength > maxLength) {
                            throw new MaxLengthExceededException(
                                "The content length (" + contentLength
                                    + " byte) is over " + maxLength
                                    + " byte. The url is " + robotTxtUrl);
                        }
                    }
                }

                final RobotsTxt robotsTxt =
                    robotsTxtHelper.parse(getMethod.getResponseBodyAsStream());
                if (robotsTxt != null) {
                    final RobotsTxt.Directives directives =
                        robotsTxt.getDirectives(userAgent);
                    if (directives != null) {
                        for (String urlPattern : directives.getDisallows()) {
                            if (StringUtil.isNotBlank(urlPattern)) {
                                urlPattern =
                                    convertRobotsTxtPathPattern(urlPattern);
                                robotContext.getUrlFilter().addExclude(
                                    hostUrl + urlPattern);
                            }
                        }
                    }
                }
            }
        } catch (final RobotSystemException e) {
            throw e;
        } catch (final Exception e) {
            throw new RobotCrawlAccessException("Could not process "
                + robotTxtUrl + ". ", e);
        } finally {
            getMethod.releaseConnection();
        }
    }

    protected String convertRobotsTxtPathPattern(final String path) {
        String newPath =
            path.replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
        if (newPath.charAt(0) != '/') {
            newPath = ".*" + newPath;
        }
        if (!newPath.endsWith("$") && !newPath.endsWith(".*")) {
            newPath = newPath + ".*";
        }
        return newPath.replaceAll("\\.\\*\\.\\*", ".*");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.http.HttpClient#doGet(java.lang.String)
     */
    @Override
    public ResponseData doGet(final String url) {
        HttpMethod getMethod;
        try {
            getMethod = new GetMethod(url);
        } catch (final IllegalArgumentException e) {
            throw new RobotCrawlAccessException("The url may not be valid: "
                + url, e);
        }
        return doHttpMethod(url, getMethod);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.http.HttpClient#doHead(java.lang.String)
     */
    @Override
    public ResponseData doHead(final String url) {
        HttpMethod headMethod;
        try {
            headMethod = new HeadMethod(url);
        } catch (final IllegalArgumentException e) {
            throw new RobotCrawlAccessException("The url may not be valid: "
                + url, e);
        }
        return doHttpMethod(url, headMethod);
    }

    public ResponseData doHttpMethod(final String url,
            final HttpMethod httpMethod) {
        if (httpClient == null) {
            init();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Accessing " + url);
        }

        try {
            processRobotsTxt(url);
        } catch (final RobotCrawlAccessException e) {
            if (logger.isInfoEnabled()) {
                final StringBuilder buf = new StringBuilder();
                buf.append(e.getMessage());
                if (e.getCause() != null) {
                    buf.append(e.getCause().getMessage());
                }
                logger.info(buf.toString());
            } else if (logger.isDebugEnabled()) {
                logger.debug("Crawling Access Exception at " + url, e);
            }
        }

        // do not redirect
        httpMethod.setFollowRedirects(false);

        // cookie
        if (cookiePolicy != null) {
            httpMethod.getParams().setCookiePolicy(cookiePolicy);
        }

        // request header
        for (final Header header : requestHeaderList) {
            httpMethod.setRequestHeader(header);
        }

        ResponseData responseData = null;
        InputStream inputStream = null;
        try {
            // get a content
            httpClient.executeMethod(httpMethod);

            final int httpStatusCode = httpMethod.getStatusCode();
            // redirect
            if (httpStatusCode >= 300 && httpStatusCode < 400) {
                final Header locationHeader =
                    httpMethod.getResponseHeader("location");
                if (locationHeader == null) {
                    logger.warn("Invalid redirect location at " + url);
                } else {
                    responseData = new ResponseData();
                    responseData.setRedirectLocation(locationHeader.getValue());
                    return responseData;
                }
            }

            long contentLength = 0;
            final InputStream responseBodyStream =
                httpMethod.getResponseBodyAsStream();
            if (responseBodyStream == null) {
                inputStream = new ByteArrayInputStream(new byte[0]);
            } else {
                final File outputFile =
                    File.createTempFile("s2robot-CommonsHttpClient-", ".out");
                DeferredFileOutputStream dfos = null;
                try {
                    try {
                        dfos =
                            new DeferredFileOutputStream(
                                responseBodyInMemoryThresholdSize,
                                outputFile);
                        StreamUtil.drain(
                            httpMethod.getResponseBodyAsStream(),
                            dfos);
                        dfos.flush();
                    } finally {
                        IOUtils.closeQuietly(dfos);
                    }
                } catch (final Exception e) {
                    if (!outputFile.delete()) {
                        logger.warn("Could not delete "
                            + outputFile.getAbsolutePath());
                    }
                    throw e;
                }

                if (dfos.isInMemory()) {
                    inputStream = new ByteArrayInputStream(dfos.getData());
                    contentLength = dfos.getData().length;
                    if (!outputFile.delete()) {
                        logger.warn("Could not delete "
                            + outputFile.getAbsolutePath());
                    }
                } else {
                    inputStream = new TemporaryFileInputStream(outputFile);
                    contentLength = outputFile.length();
                }
            }

            String contentType = null;
            final Header contentTypeHeader =
                httpMethod.getResponseHeader("Content-Type");
            if (contentTypeHeader != null) {
                contentType = contentTypeHeader.getValue();
                final int idx = contentType.indexOf(';');
                if (idx > 0) {
                    contentType = contentType.substring(0, idx);
                }
            }

            // check file size
            if (contentLengthHelper != null) {
                final long maxLength =
                    contentLengthHelper.getMaxLength(contentType);
                if (contentLength > maxLength) {
                    throw new MaxLengthExceededException("The content length ("
                        + contentLength + " byte) is over " + maxLength
                        + " byte. The url is " + url);
                }
            }

            responseData = new ResponseData();
            if (httpMethod instanceof HeadMethod) {
                responseData.setMethod(Constants.HEAD_METHOD);
            } else {
                responseData.setMethod(Constants.GET_METHOD);
            }
            responseData.setUrl(url);
            if (httpMethod instanceof GetMethod) {
                responseData.setCharSet(((GetMethod) httpMethod)
                    .getResponseCharSet());
            } else {
                responseData.setCharSet(Constants.UTF_8);
            }
            responseData.setResponseBody(inputStream);
            responseData.setHttpStatusCode(httpStatusCode);
            for (final Header header : httpMethod.getResponseHeaders()) {
                responseData.addMetaData(header.getName(), header.getValue());
            }
            if (contentType == null) {
                responseData.setMimeType(defaultMimeType);
            } else {
                responseData.setMimeType(contentType);
            }
            final Header contentLengthHeader =
                httpMethod.getResponseHeader("Content-Length");
            if (contentLengthHeader == null) {
                responseData.setContentLength(contentLength);
            } else {
                final String value = contentLengthHeader.getValue();
                try {
                    responseData.setContentLength(Long.parseLong(value));
                } catch (final Exception e) {
                    responseData.setContentLength(contentLength);
                }
            }
            final Header lastModifiedHeader =
                httpMethod.getResponseHeader("Last-Modified");
            if (lastModifiedHeader != null) {
                final String value = lastModifiedHeader.getValue();
                if (StringUtil.isNotBlank(value)) {
                    final Date d = parseLastModified(value);
                    if (d != null) {
                        responseData.setLastModified(d);
                    }
                }
            }
            if (responseData.getLastModified() == null) {
                responseData.setLastModified(new Date()); // set current time
            }

            return responseData;
        } catch (final UnknownHostException e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotCrawlAccessException("Unknown host("
                + e.getMessage() + "): " + url, e);
        } catch (final NoRouteToHostException e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotCrawlAccessException("No route to host("
                + e.getMessage() + "): " + url, e);
        } catch (final ConnectException e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotCrawlAccessException("Connection time out("
                + e.getMessage() + "): " + url, e);
        } catch (final SocketException e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotCrawlAccessException("Socket exception("
                + e.getMessage() + "): " + url, e);
        } catch (final IOException e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotCrawlAccessException("I/O exception("
                + e.getMessage() + "): " + url, e);
        } catch (final RobotSystemException e) {
            IOUtils.closeQuietly(inputStream);
            throw e;
        } catch (final Exception e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotSystemException("Failed to access " + url, e);
        } finally {
            httpMethod.releaseConnection();
        }

    }

    protected Date parseLastModified(final String value) {
        final SimpleDateFormat sdf =
            new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        try {
            return sdf.parse(value);
        } catch (final ParseException e) {
            return null;
        }
    }

}
