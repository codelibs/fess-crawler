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

    public static final String CONNECTION_TIMEOUT_PROPERTY = "connectionTimeout";

    public static final String MAX_TOTAL_CONNECTIONS_PROPERTY = "maxTotalConnections";

    public static final String STALE_CHECKING_ENABLED_PROPERTY = "staleCheckingEnabled";

    public static final String SO_TIMEOUT_PROPERTY = "soTimeout";

    public static final String LINGER_PROPERTY = "linger";

    public static final String PROXY_HOST_PROPERTY = "proxyHost";

    public static final String PROXY_PORT_PROPERTY = "proxyPort";

    public static final String PROXY_CREDENTIALS_PROPERTY = "proxyCredentials";

    public static final String USER_AGENT_PROPERTY = "userAgent";

    public static final String BASIC_AUTHENTICATIONS_PROPERTY = "basicAuthentications";

    public static final String REQUERT_HEADERS_PROPERTY = "requestHeaders";

    private final Logger logger = LoggerFactory
            .getLogger(CommonsHttpClient.class);

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

    protected volatile org.apache.commons.httpclient.HttpClient httpClient;

    public String proxyHost;

    public Integer proxyPort;

    @Binding(bindingType = BindingType.MAY)
    public Credentials proxyCredentials;

    public int responseBodyInMemoryThresholdSize = 1 * 1024 * 1024; // 1M

    private List<Header> requestHeaderList = new ArrayList<Header>();

    public String defaultMimeType = "application/octet-stream";

    protected synchronized void init() {
        if (httpClient != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing " + CommonsHttpClient.class.getName());
        }

        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        Integer connectionTimeout = getInitParameter(
                CONNECTION_TIMEOUT_PROPERTY, this.connectionTimeout);
        if (connectionTimeout != null) {
            params.setConnectionTimeout(connectionTimeout);
        }
        Integer maxTotalConnections = getInitParameter(
                MAX_TOTAL_CONNECTIONS_PROPERTY, this.maxTotalConnections);
        if (maxTotalConnections != null) {
            params.setMaxTotalConnections(maxTotalConnections);
        }
        Boolean staleCheckingEnabled = getInitParameter(
                STALE_CHECKING_ENABLED_PROPERTY, this.staleCheckingEnabled);
        if (staleCheckingEnabled != null) {
            params.setStaleCheckingEnabled(staleCheckingEnabled);
        }
        Integer soTimeout = getInitParameter(SO_TIMEOUT_PROPERTY,
                this.soTimeout);
        if (soTimeout != null) {
            params.setSoTimeout(soTimeout);
        }
        Integer linger = getInitParameter(LINGER_PROPERTY, this.linger);
        if (linger != null) {
            params.setLinger(linger);
        }
        connectionManager.setParams(params);

        httpClient = new org.apache.commons.httpclient.HttpClient(
                connectionManager);

        // proxy
        String proxyHost = getInitParameter(PROXY_HOST_PROPERTY, this.proxyHost);
        Integer proxyPort = getInitParameter(PROXY_PORT_PROPERTY,
                this.proxyPort);
        if (proxyHost != null && proxyPort != null) {
            httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
            Credentials proxyCredentials = getInitParameter(
                    PROXY_CREDENTIALS_PROPERTY, this.proxyCredentials);
            if (proxyCredentials != null) {
                httpClient.getState().setProxyCredentials(AuthScope.ANY,
                        proxyCredentials);
            }
        }

        // user agent
        String userAgent = getInitParameter(USER_AGENT_PROPERTY, this.userAgent);
        if (StringUtil.isNotBlank(userAgent)) {
            httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
                    userAgent);
        }

        // Basic Authentication
        HttpState httpState = httpClient.getState();
        BasicAuthentication[] siteCredentialList = getInitParameter(
                BASIC_AUTHENTICATIONS_PROPERTY, new BasicAuthentication[0]);
        for (BasicAuthentication basicAuthentication : siteCredentialList) {
            httpState.setCredentials(basicAuthentication.getAuthScope(),
                    basicAuthentication.getCredentials());
        }

        // Request Header
        RequestHeader[] requestHeaders = getInitParameter(
                REQUERT_HEADERS_PROPERTY, new RequestHeader[0]);
        for (RequestHeader requestHeader : requestHeaders) {
            if (requestHeader.isValid()) {
                requestHeaderList.add(new Header(requestHeader.getName(),
                        requestHeader.getValue()));
            }
        }
    }

    protected void processRobotsTxt(String url) {
        if (StringUtil.isBlank(url)) {
            throw new RobotSystemException("url is null or empty.");
        }

        if (robotsTxtHelper == null) {
            // not support robots.txt
            return;
        }

        // robot context
        S2RobotContext robotContext = CrawlingParameterUtil.getRobotContext();
        if (robotContext == null) {
            // wrong state
            return;
        }

        int idx = url.indexOf('/', url.indexOf("://") + 3);
        String hostUrl;
        if (idx >= 0) {
            hostUrl = url.substring(0, idx);
        } else {
            hostUrl = url;
        }
        String robotTxtUrl = hostUrl + "/robots.txt";

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

        GetMethod getMethod = new GetMethod(robotTxtUrl);

        // cookie
        if (cookiePolicy != null) {
            getMethod.getParams().setCookiePolicy(cookiePolicy);
        }

        // request header
        for (Header header : requestHeaderList) {
            getMethod.setRequestHeader(header);
        }

        try { // get a content 
            httpClient.executeMethod(getMethod);

            int httpStatusCode = getMethod.getStatusCode();
            if (httpStatusCode == 200) {

                // check file size
                Header contentLengthHeader = getMethod
                        .getResponseHeader("Content-Length");
                if (contentLengthHeader != null) {
                    String value = contentLengthHeader.getValue();
                    try {
                        long contentLength = Long.parseLong(value);
                        if (contentLengthHelper != null) {
                            long maxLength = contentLengthHelper
                                    .getMaxLength("text/plain");
                            if (contentLength > maxLength) {
                                throw new RobotCrawlAccessException(
                                        "The content length (" + contentLength
                                                + " byte) is over " + maxLength
                                                + " byte. The url is "
                                                + robotTxtUrl);
                            }
                        }
                    } catch (Exception e) {
                        // TODO check?
                    }
                } else {
                    // TODO check?
                }

                RobotsTxt robotsTxt = robotsTxtHelper.parse(getMethod
                        .getResponseBodyAsStream());
                if (robotsTxt != null) {
                    RobotsTxt.Directives directives = robotsTxt
                            .getDirectives(userAgent);
                    if (directives != null) {
                        for (String urlPattern : directives.getDisallows()) {
                            if (StringUtil.isNotBlank(urlPattern)) {
                                urlPattern = convertRobotsTxtPathPattern(urlPattern);
                                robotContext.getUrlFilter().addExclude(
                                        hostUrl + urlPattern);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RobotCrawlAccessException("Could not process "
                    + robotTxtUrl + ". ", e);
        } finally {
            getMethod.releaseConnection();
        }
    }

    protected String convertRobotsTxtPathPattern(String path) {
        String newPath = path.replaceAll("\\.", "\\\\.")
                .replaceAll("\\*", ".*");
        if (!newPath.startsWith("/")) {
            newPath = ".*" + newPath;
        }
        if (!newPath.endsWith("$") && !newPath.endsWith(".*")) {
            newPath = newPath + ".*";
        }
        return newPath.replaceAll("\\.\\*\\.\\*", ".*");
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.http.HttpClient#doGet(java.lang.String)
     */
    public ResponseData doGet(String url) {
        HttpMethod getMethod;
        try {
            getMethod = new GetMethod(url);
        } catch (IllegalArgumentException e) {
            throw new RobotCrawlAccessException("The url may not be valid: "
                    + url, e);
        }
        return doHttpMethod(url, getMethod);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.http.HttpClient#doHead(java.lang.String)
     */
    public ResponseData doHead(String url) {
        HttpMethod headMethod;
        try {
            headMethod = new HeadMethod(url);
        } catch (IllegalArgumentException e) {
            throw new RobotCrawlAccessException("The url may not be valid: "
                    + url, e);
        }
        return doHttpMethod(url, headMethod);
    }

    public ResponseData doHttpMethod(String url, HttpMethod httpMethod) {
        if (httpClient == null) {
            init();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Accessing " + url);
        }

        try {
            processRobotsTxt(url);
        } catch (RobotCrawlAccessException e) {
            if (logger.isInfoEnabled()) {
                StringBuilder buf = new StringBuilder();
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
        for (Header header : requestHeaderList) {
            httpMethod.setRequestHeader(header);
        }

        ResponseData responseData = null;
        InputStream inputStream = null;
        try {
            // get a content 
            httpClient.executeMethod(httpMethod);

            int httpStatusCode = httpMethod.getStatusCode();
            // redirect
            if (httpStatusCode >= 300 && httpStatusCode < 400) {
                Header locationHeader = httpMethod
                        .getResponseHeader("location");
                if (locationHeader != null) {
                    responseData = new ResponseData();
                    responseData.setRedirectLocation(locationHeader.getValue());
                    return responseData;
                } else {
                    logger.warn("Invalid redirect location at " + url);
                }
            }

            long contentLength = 0;
            InputStream responseBodyStream = httpMethod
                    .getResponseBodyAsStream();
            if (responseBodyStream != null) {
                File outputFile = File.createTempFile(
                        "s2robot-CommonsHttpClient-", ".out");
                DeferredFileOutputStream dfos = new DeferredFileOutputStream(
                        responseBodyInMemoryThresholdSize, outputFile);
                StreamUtil.drain(httpMethod.getResponseBodyAsStream(), dfos);

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
            } else {
                inputStream = new ByteArrayInputStream(new byte[0]);
            }

            String contentType = null;
            Header contentTypeHeader = httpMethod
                    .getResponseHeader("Content-Type");
            if (contentTypeHeader != null) {
                contentType = contentTypeHeader.getValue();
                int idx = contentType.indexOf(";");
                if (idx > 0) {
                    contentType = contentType.substring(0, idx);
                }
            }

            // check file size
            if (contentLengthHelper != null) {
                long maxLength = contentLengthHelper.getMaxLength(contentType);
                if (contentLength > maxLength) {
                    throw new RobotCrawlAccessException("The content length ("
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
            for (Header header : httpMethod.getResponseHeaders()) {
                responseData.addHeader(header.getName(), header.getValue());
            }
            if (contentType != null) {
                responseData.setMimeType(contentType);
            } else {
                responseData.setMimeType(defaultMimeType);
            }
            Header contentLengthHeader = httpMethod
                    .getResponseHeader("Content-Length");
            if (contentLengthHeader != null) {
                String value = contentLengthHeader.getValue();
                try {
                    responseData.setContentLength(Long.parseLong(value));
                } catch (Exception e) {
                    responseData.setContentLength(contentLength);
                }
            } else {
                responseData.setContentLength(contentLength);
            }
            Header lastModifiedHeader = httpMethod
                    .getResponseHeader("Last-Modified");
            if (lastModifiedHeader != null) {
                String value = lastModifiedHeader.getValue();
                if (StringUtil.isNotBlank(value)) {
                    Date d = parseLastModified(value);
                    if (d != null) {
                        responseData.setLastModified(d);
                    } else {
                        responseData.setLastModified(new Date()); //set current time
                    }
                }
            } else {
                responseData.setLastModified(new Date()); //set current time
            }

            return responseData;
        } catch (UnknownHostException e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotCrawlAccessException("Unknown host("
                    + e.getMessage() + "): " + url, e);
        } catch (NoRouteToHostException e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotCrawlAccessException("No route to host("
                    + e.getMessage() + "): " + url, e);
        } catch (ConnectException e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotCrawlAccessException("Connection time out("
                    + e.getMessage() + "): " + url, e);
        } catch (SocketException e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotCrawlAccessException("Socket exception("
                    + e.getMessage() + "): " + url, e);
        } catch (IOException e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotCrawlAccessException("I/O exception("
                    + e.getMessage() + "): " + url, e);
        } catch (RobotSystemException e) {
            IOUtils.closeQuietly(inputStream);
            throw e;
        } catch (Exception e) {
            IOUtils.closeQuietly(inputStream);
            throw new RobotSystemException("Failed to access " + url, e);
        } finally {
            httpMethod.releaseConnection();
        }

    }

    protected Date parseLastModified(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

}
