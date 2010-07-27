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

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
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
public class HcHttpClient extends AbstractS2RobotClient {

    public static final String CONNECTION_TIMEOUT_PROPERTY = "connectionTimeout";

    public static final String MAX_TOTAL_CONNECTIONS_PROPERTY = "maxTotalConnections";

    public static final String MAX_CONNECTIONS_PER_ROUTE_PROPERTY = "maxConnectionsPerRoute";

    public static final String STALE_CHECKING_ENABLED_PROPERTY = "staleCheckingEnabled";

    public static final String SO_TIMEOUT_PROPERTY = "soTimeout";

    public static final String LINGER_PROPERTY = "linger";

    public static final String CONNECTION_MANAGER_TIMEOUT_PROPERTY = "connectionManagerTimeout";

    public static final String PROXY_HOST_PROPERTY = "proxyHost";

    public static final String PROXY_PORT_PROPERTY = "proxyPort";

    public static final String PROXY_CREDENTIALS_PROPERTY = "proxyCredentials";

    public static final String USER_AGENT_PROPERTY = "userAgent";

    public static final String BASIC_AUTHENTICATIONS_PROPERTY = "basicAuthentications";

    public static final String REQUERT_HEADERS_PROPERTY = "requestHeaders";

    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(HcHttpClient.class);

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected RobotsTxtHelper robotsTxtHelper;

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    public Integer connectionTimeout;

    public Integer maxTotalConnections;

    public Integer maxConnectionsPerRoute;

    public Boolean staleCheckingEnabled;

    public Integer soTimeout;

    public Integer linger;

    public Integer connectionManagerTimeout;

    public String cookiePolicy;

    public String userAgent = "S2Robot";

    protected volatile DefaultHttpClient httpClient; // NOPMD

    public HttpContext httpClientContext = new BasicHttpContext();

    public String proxyHost;

    public Integer proxyPort;

    @Binding(bindingType = BindingType.MAY)
    public Credentials proxyCredentials;

    public int responseBodyInMemoryThresholdSize = 1 * 1024 * 1024; // 1M

    private List<Header> requestHeaderList = new ArrayList<Header>();

    public String defaultMimeType = "application/octet-stream";

    public SchemeRegistry schemeRegistry;

    public CookieStore cookieStore = new BasicCookieStore();

    @InitMethod
    public void init() {
        if (httpClient != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing " + HcHttpClient.class.getName());
        }

        if (schemeRegistry == null) {
            schemeRegistry = new SchemeRegistry();
            schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory
                    .getSocketFactory()));
            schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory
                    .getSocketFactory()));
        }

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(
                schemeRegistry);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient(cm);
        HttpParams params = defaultHttpClient.getParams();

        Integer connectionTimeout = getInitParameter(
                CONNECTION_TIMEOUT_PROPERTY, this.connectionTimeout);
        if (connectionTimeout != null) {
            HttpConnectionParams
                    .setConnectionTimeout(params, connectionTimeout);
        }
        Integer maxTotalConnections = getInitParameter(
                MAX_TOTAL_CONNECTIONS_PROPERTY, this.maxTotalConnections);
        if (maxTotalConnections != null) {
            cm.setMaxTotalConnections(maxTotalConnections);
        }
        Integer maxConnectionsPerRoute = getInitParameter(
                MAX_CONNECTIONS_PER_ROUTE_PROPERTY, this.maxConnectionsPerRoute);
        if (maxConnectionsPerRoute != null) {
            cm.setDefaultMaxPerRoute(maxConnectionsPerRoute);
        }
        Boolean staleCheckingEnabled = getInitParameter(
                STALE_CHECKING_ENABLED_PROPERTY, this.staleCheckingEnabled);
        if (staleCheckingEnabled != null) {
            HttpConnectionParams.setStaleCheckingEnabled(params,
                    staleCheckingEnabled);
        }
        Integer soTimeout = getInitParameter(SO_TIMEOUT_PROPERTY,
                this.soTimeout);
        if (soTimeout != null) {
            HttpConnectionParams.setSoTimeout(params, soTimeout);
        }
        Integer linger = getInitParameter(LINGER_PROPERTY, this.linger);
        if (linger != null) {
            HttpConnectionParams.setLinger(params, linger);
        }
        Integer connectionManagerTimeout = getInitParameter(
                CONNECTION_MANAGER_TIMEOUT_PROPERTY,
                this.connectionManagerTimeout);
        if (connectionManagerTimeout != null) {
            ConnManagerParams.setTimeout(params, connectionManagerTimeout);
        }

        // proxy
        String proxyHost = getInitParameter(PROXY_HOST_PROPERTY, this.proxyHost);
        Integer proxyPort = getInitParameter(PROXY_PORT_PROPERTY,
                this.proxyPort);
        if (proxyHost != null && proxyPort != null) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            params.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

            Credentials proxyCredentials = getInitParameter(
                    PROXY_CREDENTIALS_PROPERTY, this.proxyCredentials);
            if (proxyCredentials != null) {
                defaultHttpClient.getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, proxyPort), proxyCredentials);

            }
        }

        // user agent
        String userAgent = getInitParameter(USER_AGENT_PROPERTY, this.userAgent);
        if (StringUtil.isNotBlank(userAgent)) {
            HttpProtocolParams.setUserAgent(params, userAgent);
        }

        // Authentication
        Authentication[] siteCredentialList = getInitParameter(
                BASIC_AUTHENTICATIONS_PROPERTY, new Authentication[0]);
        AuthCache authCache = new BasicAuthCache();
        boolean useAuthCache = false;
        for (Authentication authentication : siteCredentialList) {
            AuthScope authScope = authentication.getAuthScope();
            defaultHttpClient.getCredentialsProvider().setCredentials(
                    authScope, authentication.getCredentials());
            if (authScope.getHost() != null
                    && authentication.getAuthScheme() != null) {
                HttpHost targetHost = new HttpHost(authScope.getHost(),
                        authScope.getPort());
                authCache.put(targetHost, new BasicScheme());
                useAuthCache = true;
            }
        }
        if (useAuthCache) {
            httpClientContext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        }

        // Request Header
        RequestHeader[] requestHeaders = getInitParameter(
                REQUERT_HEADERS_PROPERTY, new RequestHeader[0]);
        for (RequestHeader requestHeader : requestHeaders) {
            if (requestHeader.isValid()) {
                requestHeaderList.add(new BasicHeader(requestHeader.getName(),
                        requestHeader.getValue()));
            }
        }

        // do not redirect
        defaultHttpClient.getParams().setBooleanParameter(
                ClientPNames.HANDLE_REDIRECTS, false);

        // cookie store
        defaultHttpClient.setCookieStore(cookieStore);

        httpClient = defaultHttpClient;
    }

    @DestroyMethod
    public void destroy() {
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
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

        HttpGet httpGet = new HttpGet(robotTxtUrl);

        // cookie
        if (cookiePolicy != null) {
            httpGet.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    cookiePolicy);
        }

        // request header
        for (Header header : requestHeaderList) {
            httpGet.addHeader(header);
        }

        HttpEntity httpEntity = null;
        try {
            // get a content 
            HttpResponse response = httpClient.execute(httpGet,
                    httpClientContext);

            int httpStatusCode = response.getStatusLine().getStatusCode();
            if (httpStatusCode == 200) {

                // check file size
                Header contentLengthHeader = response
                        .getFirstHeader("Content-Length");
                if (contentLengthHeader != null) {
                    String value = contentLengthHeader.getValue();
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
                } else {
                    // TODO check?
                }

                httpEntity = response.getEntity();
                RobotsTxt robotsTxt = robotsTxtHelper.parse(httpEntity
                        .getContent());
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
        } catch (RobotSystemException e) {
            throw e;
        } catch (Exception e) {
            throw new RobotCrawlAccessException("Could not process "
                    + robotTxtUrl + ". ", e);
        } finally {
            if (httpEntity != null) {
                try {
                    httpEntity.consumeContent();
                } catch (IOException e) {
                    logger.warn("Could not consume a content for "
                            + robotTxtUrl);
                }
            }
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
        HttpUriRequest httpGet;
        try {
            httpGet = new HttpGet(url);
        } catch (IllegalArgumentException e) {
            throw new RobotCrawlAccessException("The url may not be valid: "
                    + url, e);
        }
        return doHttpMethod(url, httpGet);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.http.HttpClient#doHead(java.lang.String)
     */
    public ResponseData doHead(String url) {
        HttpUriRequest httpHead;
        try {
            httpHead = new HttpHead(url);
        } catch (IllegalArgumentException e) {
            throw new RobotCrawlAccessException("The url may not be valid: "
                    + url, e);
        }
        return doHttpMethod(url, httpHead);
    }

    public ResponseData doHttpMethod(String url, HttpUriRequest httpRequest) {
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

        // cookie
        if (cookiePolicy != null) {
            httpRequest.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                    cookiePolicy);
        }

        // request header
        for (Header header : requestHeaderList) {
            httpRequest.addHeader(header);
        }

        ResponseData responseData = null;
        InputStream inputStream = null;
        HttpEntity httpEntity = null;
        try {
            // get a content 
            HttpResponse response = httpClient.execute(httpRequest,
                    httpClientContext);

            int httpStatusCode = response.getStatusLine().getStatusCode();
            // redirect
            if (httpStatusCode >= 300 && httpStatusCode < 400) {
                Header locationHeader = response.getFirstHeader("location");
                if (locationHeader != null) {
                    responseData = new ResponseData();
                    responseData.setRedirectLocation(locationHeader.getValue());
                    return responseData;
                } else {
                    logger.warn("Invalid redirect location at " + url);
                }
            }

            httpEntity = response.getEntity();
            long contentLength = 0;
            if (httpEntity != null) {
                InputStream responseBodyStream = httpEntity.getContent();
                File outputFile = File.createTempFile(
                        "s2robot-CommonsHttpClient-", ".out");
                DeferredFileOutputStream dfos = null;
                try {
                    try {
                        dfos = new DeferredFileOutputStream(
                                responseBodyInMemoryThresholdSize, outputFile);
                        StreamUtil.drain(responseBodyStream, dfos);
                        dfos.flush();
                    } finally {
                        IOUtils.closeQuietly(dfos);
                    }
                } catch (Exception e) {
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
            } else {
                inputStream = new ByteArrayInputStream(new byte[0]);
            }

            String contentType = null;
            Header contentTypeHeader = response.getFirstHeader("Content-Type");
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
            responseData.setUrl(url);
            if (httpRequest instanceof HttpHead) {
                responseData.setMethod(Constants.HEAD_METHOD);
                responseData.setCharSet(Constants.UTF_8);
            } else {
                responseData.setMethod(Constants.GET_METHOD);
                Header contentEncodingHeader = httpEntity.getContentEncoding();
                if (contentEncodingHeader != null) {
                    responseData.setCharSet(contentEncodingHeader.getValue());
                } else {
                    responseData.setCharSet(Constants.UTF_8);
                }
            }
            responseData.setResponseBody(inputStream);
            responseData.setHttpStatusCode(httpStatusCode);
            for (Header header : response.getAllHeaders()) {
                responseData.addHeader(header.getName(), header.getValue());
            }
            if (contentType != null) {
                responseData.setMimeType(contentType);
            } else {
                responseData.setMimeType(defaultMimeType);
            }
            Header contentLengthHeader = response
                    .getFirstHeader("Content-Length");
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
            Header lastModifiedHeader = response
                    .getFirstHeader("Last-Modified");
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
            if (httpEntity != null) {
                try {
                    httpEntity.consumeContent();
                } catch (IOException e) {
                    logger.warn("Could not consume a content for " + url);
                }
            }
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
