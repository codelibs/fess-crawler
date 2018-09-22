/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.lang.SystemUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider.CompatibilityLevel;
import org.apache.http.impl.cookie.IgnoreSpecProvider;
import org.apache.http.impl.cookie.NetscapeDraftSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.codelibs.core.beans.BeanDesc;
import org.codelibs.core.beans.PropertyDesc;
import org.codelibs.core.beans.factory.BeanDescFactory;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.client.AbstractCrawlerClient;
import org.codelibs.fess.crawler.client.AccessTimeoutTarget;
import org.codelibs.fess.crawler.client.http.conn.IdnDnsResolver;
import org.codelibs.fess.crawler.client.http.form.FormScheme;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.RobotsTxt;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;
import org.codelibs.fess.crawler.helper.RobotsTxtHelper;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class HcHttpClient extends AbstractCrawlerClient {

    public static final String CONNECTION_TIMEOUT_PROPERTY = "connectionTimeout";

    public static final String SO_TIMEOUT_PROPERTY = "soTimeout";

    public static final String PROXY_HOST_PROPERTY = "proxyHost";

    public static final String PROXY_PORT_PROPERTY = "proxyPort";

    public static final String PROXY_AUTH_SCHEME_PROPERTY = "proxyAuthScheme";

    public static final String PROXY_CREDENTIALS_PROPERTY = "proxyCredentials";

    public static final String USER_AGENT_PROPERTY = "userAgent";

    public static final String ROBOTS_TXT_ENABLED_PROPERTY = "robotsTxtEnabled";

    public static final String BASIC_AUTHENTICATIONS_PROPERTY = "basicAuthentications";

    public static final String REQUERT_HEADERS_PROPERTY = "requestHeaders";

    public static final String REDIRECTS_ENABLED = "redirectsEnabled";

    public static final String COOKIES_PROPERTY = "cookies";

    public static final String AUTH_SCHEME_PROVIDERS_PROPERTY = "authSchemeProviders";

    public static final String IGNORE_SSL_CERTIFICATE_PROPERTY = "ignoreSslCertificate";

    private static final Logger logger = LoggerFactory
            .getLogger(HcHttpClient.class);

    @Resource
    protected RobotsTxtHelper robotsTxtHelper;

    @Resource
    protected ContentLengthHelper contentLengthHelper;

    @Resource
    protected MimeTypeHelper mimeTypeHelper;

    protected volatile CloseableHttpClient httpClient;

    private final List<Header> requestHeaderList = new ArrayList<>();

    private final Map<String, Object> httpClientPropertyMap = new HashMap<>();

    private TimeoutTask connectionMonitorTask;

    protected Integer accessTimeout; // sec

    protected Integer connectionTimeout;

    protected Integer maxTotalConnections;

    protected Integer maxConnectionsPerRoute;

    protected Integer soTimeout;

    protected String cookieSpec;

    protected String userAgent = "Crawler";

    protected HttpClientContext httpClientContext = HttpClientContext.create();

    protected String proxyHost;

    protected Integer proxyPort;

    protected AuthScheme proxyAuthScheme = new BasicScheme();

    protected Credentials proxyCredentials;

    protected String defaultMimeType = APPLICATION_OCTET_STREAM;

    protected CookieStore cookieStore = new BasicCookieStore();

    protected HttpClientConnectionManager clientConnectionManager;

    protected DnsResolver dnsResolver = new IdnDnsResolver();

    protected Map<String, AuthSchemeProvider> authSchemeProviderMap;

    protected int connectionCheckInterval = 5; // sec

    protected long idleConnectionTimeout = 60 * 1000L; // 1min

    protected Pattern redirectHttpStatusPattern = Pattern
            .compile("[3][0-9][0-9]");

    protected boolean useRobotsTxtDisallows = true;

    protected boolean useRobotsTxtAllows = false;

    protected CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    protected AuthCache authCache = new BasicAuthCache();

    protected HttpRoutePlanner routePlanner;

    protected boolean redirectsEnabled = false;

    protected Lookup<CookieSpecProvider> cookieSpecRegistry;

    protected String[] cookieDatePatterns = { //
            DateUtils.PATTERN_RFC1123, //
            DateUtils.PATTERN_RFC1036, //
            DateUtils.PATTERN_ASCTIME, //
            StringUtil.EMPTY//
    };

    protected LayeredConnectionSocketFactory sslSocketFactory;

    @Override
    public synchronized void init() {
        if (httpClient != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing " + HcHttpClient.class.getName());
        }

        super.init();

        // robots.txt parser
        final Boolean robotsTxtEnabled = getInitParameter(ROBOTS_TXT_ENABLED_PROPERTY, Boolean.TRUE, Boolean.class);
        if (robotsTxtHelper != null) {
            robotsTxtHelper.setEnabled(robotsTxtEnabled.booleanValue());
        }

        // httpclient
        final org.apache.http.client.config.RequestConfig.Builder requestConfigBuilder = RequestConfig
                .custom();
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        final Integer connectionTimeoutParam = getInitParameter(CONNECTION_TIMEOUT_PROPERTY, connectionTimeout, Integer.class);
        if (connectionTimeoutParam != null) {
            requestConfigBuilder.setConnectTimeout(connectionTimeoutParam);

        }
        final Integer soTimeoutParam = getInitParameter(SO_TIMEOUT_PROPERTY, soTimeout, Integer.class);
        if (soTimeoutParam != null) {
            requestConfigBuilder.setSocketTimeout(soTimeoutParam);
        }

        // AuthSchemeFactory
        final RegistryBuilder<AuthSchemeProvider> authSchemeProviderBuilder = RegistryBuilder
                .create();
        @SuppressWarnings("unchecked")
        final Map<String, AuthSchemeProvider> factoryMap =
                getInitParameter(AUTH_SCHEME_PROVIDERS_PROPERTY, authSchemeProviderMap, Map.class);
        if (factoryMap != null) {
            for (final Map.Entry<String, AuthSchemeProvider> entry : factoryMap
                    .entrySet()) {
                authSchemeProviderBuilder.register(entry.getKey(),
                        entry.getValue());
            }
        }

        // user agent
        userAgent = getInitParameter(USER_AGENT_PROPERTY, userAgent, String.class);
        if (StringUtil.isNotBlank(userAgent)) {
            httpClientBuilder.setUserAgent(userAgent);
        }

        final HttpRoutePlanner planner = buildRoutePlanner();
        if (planner != null) {
            httpClientBuilder.setRoutePlanner(planner);
        }

        // Authentication
        final Authentication[] siteCredentialList =
                getInitParameter(BASIC_AUTHENTICATIONS_PROPERTY, new Authentication[0], Authentication[].class);
        final List<Pair<FormScheme, Credentials>> formSchemeList = new ArrayList<>();
        for (final Authentication authentication : siteCredentialList) {
            final AuthScheme authScheme = authentication.getAuthScheme();
            if (authScheme instanceof FormScheme) {
                formSchemeList.add(new Pair<>((FormScheme) authScheme, authentication.getCredentials()));
            } else {
                final AuthScope authScope = authentication.getAuthScope();
                credentialsProvider.setCredentials(authScope, authentication.getCredentials());
                if (authScope.getHost() != null && authScheme != null) {
                    final HttpHost targetHost = new HttpHost(authScope.getHost(), authScope.getPort());
                    authCache.put(targetHost, authScheme);
                }
            }
        }

        httpClientContext.setAuthCache(authCache);
        httpClientContext.setCredentialsProvider(credentialsProvider);

        // Request Header
        final RequestHeader[] requestHeaders = getInitParameter(REQUERT_HEADERS_PROPERTY, new RequestHeader[0], RequestHeader[].class);
        for (final RequestHeader requestHeader : requestHeaders) {
            if (requestHeader.isValid()) {
                requestHeaderList.add(new BasicHeader(requestHeader.getName(),
                        requestHeader.getValue()));
            }
        }

        // do not redirect
        requestConfigBuilder.setRedirectsEnabled(getInitParameter(REDIRECTS_ENABLED, redirectsEnabled, Boolean.class));

        // cookie
        if (cookieSpec != null) {
            requestConfigBuilder.setCookieSpec(cookieSpec);
        }

        // cookie store
        httpClientBuilder.setDefaultCookieStore(cookieStore);
        if (cookieStore != null) {
            final Cookie[] cookies = getInitParameter(COOKIES_PROPERTY, new Cookie[0], Cookie[].class);
            for (final Cookie cookie : cookies) {
                cookieStore.addCookie(cookie);
            }
        }

        // cookie registry
        final Lookup<CookieSpecProvider> cookieSpecRegistry = buildCookieSpecRegistry();
        if (cookieSpecRegistry != null) {
            httpClientBuilder.setDefaultCookieSpecRegistry(cookieSpecRegistry);
        }

        // SSL
        final LayeredConnectionSocketFactory sslSocketFactory = buildSSLSocketFactory();
        if (sslSocketFactory != null) {
            httpClientBuilder.setSSLSocketFactory(sslSocketFactory);
        }

        connectionMonitorTask = TimeoutManager.getInstance().addTimeoutTarget(
                new HcConnectionMonitorTarget(clientConnectionManager,
                        idleConnectionTimeout), connectionCheckInterval, true);

        final CloseableHttpClient closeableHttpClient = httpClientBuilder
                .setDnsResolver(dnsResolver)
                .setConnectionManager(clientConnectionManager)
                .setDefaultRequestConfig(requestConfigBuilder.build()).build();
        if (!httpClientPropertyMap.isEmpty()) {
            final BeanDesc beanDesc = BeanDescFactory
                    .getBeanDesc(closeableHttpClient.getClass());
            for (final Map.Entry<String, Object> entry : httpClientPropertyMap
                    .entrySet()) {
                final String propertyName = entry.getKey();
                if (beanDesc.hasPropertyDesc(propertyName)) {
                    final PropertyDesc propertyDesc = beanDesc
                            .getPropertyDesc(propertyName);
                    propertyDesc
                            .setValue(closeableHttpClient, entry.getValue());
                } else {
                    logger.warn("DefaultHttpClient does not have "
                            + propertyName + ".");
                }
            }
        }

        formSchemeList.forEach(p -> {
            final FormScheme scheme = p.getFirst();
            final Credentials credentials = p.getSecond();
            scheme.authenticate(credentials, (request, consumer) -> {

                // request header
                for (final Header header : requestHeaderList) {
                    request.addHeader(header);
                }

                HttpEntity httpEntity = null;
                try {
                    final HttpResponse response = closeableHttpClient.execute(request, new BasicHttpContext(httpClientContext));
                    httpEntity = response.getEntity();
                    consumer.accept(response, httpEntity);
                } catch (final Exception e) {
                    request.abort();
                    logger.warn("Failed to authenticate on " + scheme, e);
                } finally {
                    EntityUtils.consumeQuietly(httpEntity);
                }
            });
        });

        httpClient = closeableHttpClient;
    }

    protected LayeredConnectionSocketFactory buildSSLSocketFactory() {
        if (sslSocketFactory != null) {
            return sslSocketFactory;
        } else if (getInitParameter(IGNORE_SSL_CERTIFICATE_PROPERTY, false, Boolean.class)) {
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                return new SSLConnectionSocketFactory(builder.build());
            } catch (Exception e) {
                logger.warn("Failed to create TrustSelfSignedStrategy.", e);
            }
        }
        return null;
    }

    protected Lookup<CookieSpecProvider> buildCookieSpecRegistry() {
        if (cookieSpecRegistry != null) {
            return cookieSpecRegistry;
        }

        final PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();
        final CookieSpecProvider defaultProvider =
                new DefaultCookieSpecProvider(CompatibilityLevel.DEFAULT, publicSuffixMatcher, cookieDatePatterns, false);
        final CookieSpecProvider laxStandardProvider =
                new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.RELAXED, publicSuffixMatcher);
        final CookieSpecProvider strictStandardProvider =
                new RFC6265CookieSpecProvider(RFC6265CookieSpecProvider.CompatibilityLevel.STRICT, publicSuffixMatcher);
        return RegistryBuilder.<CookieSpecProvider> create()//
                .register(CookieSpecs.DEFAULT, defaultProvider)//
                .register("best-match", defaultProvider)//
                .register("compatibility", defaultProvider)//
                .register(CookieSpecs.STANDARD, laxStandardProvider)//
                .register(CookieSpecs.STANDARD_STRICT, strictStandardProvider)//
                .register(CookieSpecs.NETSCAPE, new NetscapeDraftSpecProvider())//
                .register(CookieSpecs.IGNORE_COOKIES, new IgnoreSpecProvider())//
                .build();
    }

    @PreDestroy
    public void destroy() {
        if (connectionMonitorTask != null) {
            connectionMonitorTask.cancel();
        }
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (final IOException e) {
                logger.error("Failed to close httpClient.", e);
            }
        }
    }

    public void addHttpClientProperty(final String name, final Object value) {
        if (StringUtil.isNotBlank(name) && value != null) {
            httpClientPropertyMap.put(name, value);
        }
    }

    protected void processRobotsTxt(final String url) {
        if (StringUtil.isBlank(url)) {
            throw new CrawlerSystemException("url is null or empty.");
        }

        if (robotsTxtHelper == null || !robotsTxtHelper.isEnabled()) {
            // not support robots.txt
            return;
        }

        // crawler context
        final CrawlerContext crawlerContext = CrawlingParameterUtil
                .getCrawlerContext();
        if (crawlerContext == null) {
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
        if (crawlerContext.getRobotsTxtUrlSet().contains(robotTxtUrl)) {
            if (logger.isDebugEnabled()) {
                logger.debug(robotTxtUrl + " is already visited.");
            }
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("Checking URL: " + robotTxtUrl);
        }
        // add url to a set
        crawlerContext.getRobotsTxtUrlSet().add(robotTxtUrl);

        final HttpGet httpGet = new HttpGet(robotTxtUrl);

        // request header
        for (final Header header : requestHeaderList) {
            httpGet.addHeader(header);
        }

        HttpEntity httpEntity = null;
        try {
            // get a content
            final HttpResponse response = executeHttpClient(httpGet);
            httpEntity = response.getEntity();

            final int httpStatusCode = response.getStatusLine().getStatusCode();
            if (httpStatusCode == 200) {

                // check file size
                final Header contentLengthHeader = response
                        .getFirstHeader("Content-Length");
                if (contentLengthHeader != null) {
                    final String value = contentLengthHeader.getValue();
                    final long contentLength = Long.parseLong(value);
                    if (contentLengthHelper != null) {
                        final long maxLength = contentLengthHelper
                                .getMaxLength("text/plain");
                        if (contentLength > maxLength) {
                            throw new MaxLengthExceededException(
                                    "The content length (" + contentLength
                                            + " byte) is over " + maxLength
                                            + " byte. The url is "
                                            + robotTxtUrl);
                        }
                    }
                }

                if (httpEntity != null) {
                    final RobotsTxt robotsTxt = robotsTxtHelper
                            .parse(httpEntity.getContent());
                    if (robotsTxt != null) {
                        final String[] sitemaps = robotsTxt.getSitemaps();
                        if (sitemaps.length > 0) {
                            crawlerContext.addSitemaps(sitemaps);
                        }

                        final RobotsTxt.Directive directive = robotsTxt
                                .getMatchedDirective(userAgent);
                        if (directive != null) {
                            if (useRobotsTxtDisallows) {
                                for (String urlPattern : directive
                                        .getDisallows()) {
                                    if (StringUtil.isNotBlank(urlPattern)) {
                                        urlPattern = convertRobotsTxtPathPattern(urlPattern);
                                        crawlerContext.getUrlFilter().addExclude(
                                                hostUrl + urlPattern);
                                    }
                                }
                            }
                            if (useRobotsTxtAllows) {
                                for (String urlPattern : directive.getAllows()) {
                                    if (StringUtil.isNotBlank(urlPattern)) {
                                        urlPattern = convertRobotsTxtPathPattern(urlPattern);
                                        crawlerContext.getUrlFilter().addInclude(
                                                hostUrl + urlPattern);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (final CrawlerSystemException e) {
            httpGet.abort();
            throw e;
        } catch (final Exception e) {
            httpGet.abort();
            throw new CrawlingAccessException("Could not process "
                    + robotTxtUrl + ". ", e);
        } finally {
            EntityUtils.consumeQuietly(httpEntity);
        }
    }

    protected String convertRobotsTxtPathPattern(final String path) {
        String newPath = path.replaceAll("\\.", "\\\\.")
                .replaceAll("\\*", ".*");
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
     * @see org.codelibs.fess.crawler.http.HttpClient#doGet(java.lang.String)
     */
    @Override
    public ResponseData doGet(final String url) {
        HttpUriRequest httpGet;
        try {
            httpGet = new HttpGet(url);
        } catch (final IllegalArgumentException e) {
            throw new CrawlingAccessException("The url may not be valid: "
                    + url, e);
        }
        return doHttpMethod(url, httpGet);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.http.HttpClient#doHead(java.lang.String)
     */
    @Override
    public ResponseData doHead(final String url) {
        HttpUriRequest httpHead;
        try {
            httpHead = new HttpHead(url);
        } catch (final IllegalArgumentException e) {
            throw new CrawlingAccessException("The url may not be valid: "
                    + url, e);
        }
        return doHttpMethod(url, httpHead);
    }

    public ResponseData doHttpMethod(final String url,
            final HttpUriRequest httpRequest) {
        if (httpClient == null) {
            init();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Accessing " + url);
        }

        // start
        AccessTimeoutTarget accessTimeoutTarget = null;
        TimeoutTask accessTimeoutTask = null;
        if (accessTimeout != null) {
            accessTimeoutTarget = new AccessTimeoutTarget(
                    Thread.currentThread());
            accessTimeoutTask = TimeoutManager.getInstance().addTimeoutTarget(
                    accessTimeoutTarget, accessTimeout.intValue(), false);
        }

        try {
            return processHttpMethod(url, httpRequest);
        } finally {
            if (accessTimeout != null) {
                accessTimeoutTarget.stop();
                if (!accessTimeoutTask.isCanceled()) {
                    accessTimeoutTask.cancel();
                }
            }
        }
    }

    protected ResponseData processHttpMethod(final String url,
            final HttpUriRequest httpRequest) {
        try {
            processRobotsTxt(url);
        } catch (final CrawlingAccessException e) {
            if (logger.isInfoEnabled()) {
                final StringBuilder buf = new StringBuilder(100);
                buf.append(e.getMessage());
                if (e.getCause() != null) {
                    buf.append(e.getCause().getMessage());
                }
                logger.info(buf.toString());
            } else if (logger.isDebugEnabled()) {
                logger.debug("Crawling Access Exception at " + url, e);
            }
        }

        // request header
        for (final Header header : requestHeaderList) {
            httpRequest.addHeader(header);
        }

        ResponseData responseData = new ResponseData();
        HttpEntity httpEntity = null;
        try {
            // get a content
            final HttpResponse response = executeHttpClient(httpRequest);
            httpEntity = response.getEntity();

            final int httpStatusCode = response.getStatusLine().getStatusCode();
            // redirect
            if (isRedirectHttpStatus(httpStatusCode)) {
                final Header locationHeader = response
                        .getFirstHeader("location");
                if (locationHeader == null) {
                    logger.warn("Invalid redirect location at " + url);
                } else {
                    final String redirectLocation;
                    if (locationHeader.getValue().startsWith("/")) {
                        redirectLocation = buildRedirectLocation(url, locationHeader.getValue());
                    } else {
                        redirectLocation = locationHeader.getValue();
                    }
                    responseData = new ResponseData();
                    responseData.setRedirectLocation(redirectLocation);
                    return responseData;
                }
            }

            String contentType = null;
            final Header contentTypeHeader = response
                    .getFirstHeader("Content-Type");
            if (contentTypeHeader != null) {
                contentType = contentTypeHeader.getValue();
                final int idx = contentType.indexOf(';');
                if (idx > 0) {
                    contentType = contentType.substring(0, idx);
                    if (APPLICATION_OCTET_STREAM.equals(contentType)) {
                        contentType = null;
                    }
                }
            }

            long contentLength = 0;
            String contentEncoding = Constants.UTF_8;
            if (httpEntity == null) {
                responseData.setResponseBody(new byte[0]);
                if (contentType == null) {
                    contentType = defaultMimeType;
                }
            } else {
                final InputStream responseBodyStream = httpEntity.getContent();
                try (final DeferredFileOutputStream dfos = new DeferredFileOutputStream((int) maxCachedContentSize, "crawler-HcHttpClient-",
                        ".out", SystemUtils.getJavaIoTmpDir())) {
                    CopyUtil.copy(responseBodyStream, dfos);
                    dfos.flush();

                    if (dfos.isInMemory()) {
                        responseData.setResponseBody(dfos.getData());
                        contentLength = dfos.getData().length;
                        if (contentType == null) {
                            try (InputStream is = new ByteArrayInputStream(dfos.getData())) {
                                contentType = mimeTypeHelper.getContentType(is, url);
                            } catch (final Exception e) {
                                logger.debug("Failed to detect mime-type.", e);
                                contentType = defaultMimeType;
                            }
                        }
                    } else {
                        final File outputFile = dfos.getFile();
                        responseData.setResponseBody(outputFile, true);
                        contentLength = outputFile.length();
                        if (contentType == null) {
                            try (InputStream is = new FileInputStream(outputFile)) {
                                contentType = mimeTypeHelper.getContentType(is, url);
                            } catch (final Exception e) {
                                logger.debug("Failed to detect mime-type.", e);
                                contentType = defaultMimeType;
                            }
                        }
                    }
                }

                final Header contentEncodingHeader = httpEntity
                        .getContentEncoding();
                if (contentEncodingHeader != null) {
                    contentEncoding = contentEncodingHeader.getValue();
                }
            }

            // check file size
            if (contentLengthHelper != null) {
                final long maxLength = contentLengthHelper
                        .getMaxLength(contentType);
                if (contentLength > maxLength) {
                    throw new MaxLengthExceededException("The content length ("
                            + contentLength + " byte) is over " + maxLength
                            + " byte. The url is " + url);
                }
            }

            responseData.setUrl(url);
            responseData.setCharSet(contentEncoding);
            if (httpRequest instanceof HttpHead) {
                responseData.setMethod(Constants.HEAD_METHOD);
            } else {
                responseData.setMethod(Constants.GET_METHOD);
            }
            responseData.setHttpStatusCode(httpStatusCode);
            for (final Header header : response.getAllHeaders()) {
                responseData.addMetaData(header.getName(), header.getValue());
            }
            responseData.setMimeType(contentType);
            final Header contentLengthHeader = response
                    .getFirstHeader("Content-Length");
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
            checkMaxContentLength(responseData);
            final Header lastModifiedHeader = response
                    .getFirstHeader("Last-Modified");
            if (lastModifiedHeader != null) {
                final String value = lastModifiedHeader.getValue();
                if (StringUtil.isNotBlank(value)) {
                    final Date d = parseLastModified(value);
                    if (d != null) {
                        responseData.setLastModified(d);
                    }
                }
            }

            return responseData;
        } catch (final UnknownHostException e) {
            closeResources(httpRequest, responseData);
            throw new CrawlingAccessException("Unknown host("
                    + e.getMessage() + "): " + url, e);
        } catch (final NoRouteToHostException e) {
            closeResources(httpRequest, responseData);
            throw new CrawlingAccessException("No route to host("
                    + e.getMessage() + "): " + url, e);
        } catch (final ConnectException e) {
            closeResources(httpRequest, responseData);
            throw new CrawlingAccessException("Connection time out("
                    + e.getMessage() + "): " + url, e);
        } catch (final SocketException e) {
            closeResources(httpRequest, responseData);
            throw new CrawlingAccessException("Socket exception("
                    + e.getMessage() + "): " + url, e);
        } catch (final IOException e) {
            closeResources(httpRequest, responseData);
            throw new CrawlingAccessException("I/O exception("
                    + e.getMessage() + "): " + url, e);
        } catch (final CrawlerSystemException e) {
            closeResources(httpRequest, responseData);
            throw e;
        } catch (final Exception e) {
            closeResources(httpRequest, responseData);
            throw new CrawlerSystemException("Failed to access " + url, e);
        } finally {
            EntityUtils.consumeQuietly(httpEntity);
        }
    }

    protected void closeResources(final HttpUriRequest httpRequest, final ResponseData responseData) {
        CloseableUtil.closeQuietly(responseData);
        httpRequest.abort();
    }

    protected boolean isRedirectHttpStatus(final int httpStatusCode) {
        return redirectHttpStatusPattern.matcher(
                Integer.toString(httpStatusCode)).matches();
    }

    protected HttpResponse executeHttpClient(final HttpUriRequest httpRequest)
            throws IOException {
        return httpClient.execute(httpRequest, new BasicHttpContext(
                httpClientContext));
    }

    protected Date parseLastModified(final String value) {
        final SimpleDateFormat sdf = new SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        try {
            return sdf.parse(value);
        } catch (final ParseException e) {
            return null;
        }
    }

    protected HttpRoutePlanner buildRoutePlanner() {
        if (routePlanner != null) {
            return routePlanner;
        }

        // proxy
        final String proxyHost = getInitParameter(PROXY_HOST_PROPERTY, this.proxyHost, String.class);
        final Integer proxyPort = getInitParameter(PROXY_PORT_PROPERTY, this.proxyPort, Integer.class);
        if (proxyHost != null && proxyPort != null) {
            final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            final DefaultProxyRoutePlanner defaultRoutePlanner = new DefaultProxyRoutePlanner(proxy);

            final Credentials credentials = getInitParameter(PROXY_CREDENTIALS_PROPERTY, proxyCredentials, Credentials.class);
            if (credentials != null) {
                credentialsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), credentials);
                final AuthScheme authScheme = getInitParameter(PROXY_AUTH_SCHEME_PROPERTY, proxyAuthScheme, AuthScheme.class);
                if (authScheme != null) {
                    authCache.put(proxy, authScheme);
                }
            }
            return defaultRoutePlanner;
        }
        return null;
    }

    protected static String buildRedirectLocation(final String url, final String location) throws MalformedURLException {
        return new URL(new URL(url), location).toExternalForm();
    }

    @Override
    public void setAccessTimeout(final Integer accessTimeout) {
        this.accessTimeout = accessTimeout;
    }

    public void setConnectionTimeout(final Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setMaxTotalConnections(final Integer maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    public void setMaxConnectionsPerRoute(final Integer maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    public void setSoTimeout(final Integer soTimeout) {
        this.soTimeout = soTimeout;
    }

    public void setCookieSpec(final String cookieSpec) {
        this.cookieSpec = cookieSpec;
    }

    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }

    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(final Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setProxyAuthScheme(final AuthScheme proxyAuthScheme) {
        this.proxyAuthScheme = proxyAuthScheme;
    }

    public void setProxyCredentials(final Credentials proxyCredentials) {
        this.proxyCredentials = proxyCredentials;
    }

    public void setDefaultMimeType(final String defaultMimeType) {
        this.defaultMimeType = defaultMimeType;
    }

    public void setCookieStore(final CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    public void setHttpClientContext(final HttpClientContext httpClientContext) {
        this.httpClientContext = httpClientContext;
    }

    public void setClientConnectionManager(
            final HttpClientConnectionManager clientConnectionManager) {
        this.clientConnectionManager = clientConnectionManager;
    }

    public void setAuthSchemeProviderMap(
            final Map<String, AuthSchemeProvider> authSchemeProviderMap) {
        this.authSchemeProviderMap = authSchemeProviderMap;
    }

    public void setConnectionCheckInterval(final int connectionCheckInterval) {
        this.connectionCheckInterval = connectionCheckInterval;
    }

    public void setIdleConnectionTimeout(final long idleConnectionTimeout) {
        this.idleConnectionTimeout = idleConnectionTimeout;
    }

    public void setRedirectHttpStatusPattern(
            final Pattern redirectHttpStatusPattern) {
        this.redirectHttpStatusPattern = redirectHttpStatusPattern;
    }

    public void setUseRobotsTxtDisallows(final boolean useRobotsTxtDisallows) {
        this.useRobotsTxtDisallows = useRobotsTxtDisallows;
    }

    public void setUseRobotsTxtAllows(final boolean useRobotsTxtAllows) {
        this.useRobotsTxtAllows = useRobotsTxtAllows;
    }

    public void setCredentialsProvider(final CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public void setAuthCache(final AuthCache authCache) {
        this.authCache = authCache;
    }

    public void setRoutePlanner(final HttpRoutePlanner routePlanner) {
        this.routePlanner = routePlanner;
    }

    public void setRedirectsEnabled(final boolean redirectsEnabled) {
        this.redirectsEnabled = redirectsEnabled;
    }

    public void setCookieSpecRegistry(final Lookup<CookieSpecProvider> cookieSpecRegistry) {
        this.cookieSpecRegistry = cookieSpecRegistry;
    }

    public void setCookieDatePatterns(final String[] cookieDatePatterns) {
        this.cookieDatePatterns = cookieDatePatterns;
    }

    public void setDnsResolver(final DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver;
    }

    public void setSslSocketFactory(LayeredConnectionSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }
}
