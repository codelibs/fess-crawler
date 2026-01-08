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
package org.codelibs.fess.crawler.client.http;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.lang3.SystemUtils;
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
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
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
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider.CompatibilityLevel;
import org.apache.http.impl.cookie.IgnoreSpecProvider;
import org.apache.http.impl.cookie.NetscapeDraftSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import jakarta.annotation.Resource;

/**
 * Hc4HttpClient is an HTTP client implementation using Apache HttpComponents 4.x.
 * It extends {@link HcHttpClient} and provides various configurations and settings
 * for making HTTP requests, including connection timeouts, proxy settings, user agent,
 * request headers, cookie management, and SSL configurations. The client also supports
 * robots.txt parsing and form-based authentication schemes.
 *
 * @see HcHttpClient
 */
public class Hc4HttpClient extends HcHttpClient {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(Hc4HttpClient.class);

    /**
     * Constructs a new Hc4HttpClient.
     */
    public Hc4HttpClient() {
        // Default constructor
    }

    /** Helper for processing robots.txt files */
    @Resource
    protected RobotsTxtHelper robotsTxtHelper;

    /** Helper for managing content length limits */
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    /** Helper for determining MIME types */
    @Resource
    protected MimeTypeHelper mimeTypeHelper;

    /** The HTTP client instance */
    protected volatile CloseableHttpClient httpClient;

    /** List of request headers to be sent with each request */
    private final List<Header> requestHeaderList = new ArrayList<>();

    /** Map of HTTP client properties */
    private final Map<String, Object> httpClientPropertyMap = new HashMap<>();

    /** Task for monitoring idle connections */
    private TimeoutTask connectionMonitorTask;

    /** Connection timeout in milliseconds */
    protected Integer connectionTimeout;

    /** Maximum total number of connections */
    protected Integer maxTotalConnections;

    /** Maximum connections per route */
    protected Integer maxConnectionsPerRoute;

    /** Socket timeout in milliseconds */
    protected Integer soTimeout;

    /** Cookie specification to use */
    protected String cookieSpec;

    /** User agent string */
    protected String userAgent = "Crawler";

    /** HTTP client context for requests */
    protected HttpClientContext httpClientContext = HttpClientContext.create();

    /** Proxy host name */
    protected String proxyHost;

    /** Proxy port number */
    protected Integer proxyPort;

    /** Proxy authentication scheme */
    protected AuthScheme proxyAuthScheme = new BasicScheme();

    /** Proxy credentials */
    protected Credentials proxyCredentials;

    /** Default MIME type for unknown content */
    protected String defaultMimeType = APPLICATION_OCTET_STREAM;

    /** Cookie store for managing cookies */
    protected CookieStore cookieStore = new BasicCookieStore();

    /** HTTP client connection manager */
    protected HttpClientConnectionManager clientConnectionManager;

    /** DNS resolver for hostname resolution */
    protected DnsResolver dnsResolver = new IdnDnsResolver();

    /** Map of authentication scheme providers */
    protected Map<String, AuthSchemeProvider> authSchemeProviderMap;

    /** Connection check interval in seconds */
    protected int connectionCheckInterval = 5; // sec

    /** Idle connection timeout in milliseconds */
    protected long idleConnectionTimeout = 60 * 1000L; // 1min

    /** Pattern for matching HTTP redirect status codes */
    protected Pattern redirectHttpStatusPattern = Pattern.compile("[3][0-9][0-9]");

    /** Whether to use robots.txt disallow rules */
    protected boolean useRobotsTxtDisallows = true;

    /** Whether to use robots.txt allow rules */
    protected boolean useRobotsTxtAllows = true;

    /** Credentials provider for authentication */
    protected CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    /** Authentication cache */
    protected AuthCache authCache = new BasicAuthCache();

    /** HTTP route planner */
    protected HttpRoutePlanner routePlanner;

    /** Whether HTTP redirects are enabled */
    protected boolean redirectsEnabled = false;

    /** Cookie specification registry */
    protected Lookup<CookieSpecProvider> cookieSpecRegistry;

    /** Cookie date patterns for parsing */
    protected String[] cookieDatePatterns = { //
            DateUtils.PATTERN_RFC1123, //
            DateUtils.PATTERN_RFC1036, //
            DateUtils.PATTERN_ASCTIME, //
            StringUtil.EMPTY//
    };

    /** SSL socket factory for HTTPS connections */
    protected LayeredConnectionSocketFactory sslSocketFactory;

    /**
    * Initializes the HTTP client with the necessary configurations and settings.
    * This method sets up the request configurations, authentication schemes,
    * user agent, proxy settings, request headers, cookie store, and connection manager.
    * It also processes form-based authentication schemes and sets up the HTTP client context.
    */
    @Override
    public synchronized void init() {
        if (httpClient != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", Hc4HttpClient.class.getName());
        }

        super.init();

        // robots.txt parser
        final Boolean robotsTxtEnabled = getInitParameter(ROBOTS_TXT_ENABLED_PROPERTY, Boolean.TRUE, Boolean.class);
        if (robotsTxtHelper != null) {
            robotsTxtHelper.setEnabled(robotsTxtEnabled);
        }

        // httpclient
        final RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
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
        final RegistryBuilder<AuthSchemeProvider> authSchemeProviderBuilder = RegistryBuilder.create();
        @SuppressWarnings("unchecked")
        final Map<String, AuthSchemeProvider> factoryMap =
                getInitParameter(AUTH_SCHEME_PROVIDERS_PROPERTY, authSchemeProviderMap, Map.class);
        if (factoryMap != null) {
            for (final Map.Entry<String, AuthSchemeProvider> entry : factoryMap.entrySet()) {
                authSchemeProviderBuilder.register(entry.getKey(), entry.getValue());
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
                getInitParameter(AUTHENTICATIONS_PROPERTY, new Authentication[0], Authentication[].class);
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
        final RequestHeader[] requestHeaders = getInitParameter(REQUEST_HEADERS_PROPERTY, new RequestHeader[0], RequestHeader[].class);
        for (final RequestHeader requestHeader : requestHeaders) {
            if (requestHeader.isValid()) {
                requestHeaderList.add(new BasicHeader(requestHeader.getName(), requestHeader.getValue()));
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

        clientConnectionManager = buildConnectionManager(httpClientBuilder);

        connectionMonitorTask = TimeoutManager.getInstance()
                .addTimeoutTarget(new Hc4ConnectionMonitorTarget(clientConnectionManager, idleConnectionTimeout), connectionCheckInterval,
                        true);

        final CloseableHttpClient closeableHttpClient = httpClientBuilder.setDnsResolver(dnsResolver)
                .setConnectionManager(clientConnectionManager)
                .setDefaultRequestConfig(requestConfigBuilder.build())
                .build();
        if (!httpClientPropertyMap.isEmpty()) {
            final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(closeableHttpClient.getClass());
            for (final Map.Entry<String, Object> entry : httpClientPropertyMap.entrySet()) {
                final String propertyName = entry.getKey();
                if (beanDesc.hasPropertyDesc(propertyName)) {
                    final PropertyDesc propertyDesc = beanDesc.getPropertyDesc(propertyName);
                    propertyDesc.setValue(closeableHttpClient, entry.getValue());
                } else {
                    logger.warn("Property not found in HTTP client: propertyName={}, clientClass={}", propertyName,
                            closeableHttpClient.getClass().getName());
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
                    logger.warn("Failed to authenticate with form-based authentication: scheme={}, url={}", scheme, request.getURI(), e);
                } finally {
                    EntityUtils.consumeQuietly(httpEntity);
                }
            });
        });

        httpClient = closeableHttpClient;
        if (logger.isInfoEnabled()) {
            logger.info("HTTP client initialized successfully: userAgent={}, maxTotal={}, defaultMaxPerRoute={}", userAgent,
                    maxTotalConnections, maxConnectionsPerRoute);
        }
    }

    /**
     * Builds the HTTP client connection manager with SSL and connection pool settings.
     *
     * @param httpClientBuilder The HTTP client builder
     * @return The configured connection manager
     */
    protected HttpClientConnectionManager buildConnectionManager(final HttpClientBuilder httpClientBuilder) {
        // SSL
        final LayeredConnectionSocketFactory sslSocketFactory = buildSSLSocketFactory(httpClientBuilder);
        final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()//
                .register("http", PlainConnectionSocketFactory.getSocketFactory())//
                .register("https", sslSocketFactory)
                .build();

        final long timeToLive = getInitParameter(TIME_TO_LIVE_PROPERTY, 5L, Long.class);
        final TimeUnit timeUnit = TimeUnit.valueOf(getInitParameter(TIME_TO_LIVE_TIME_UNIT_PROPERTY, "MINUTES", String.class));
        final int maxTotal = getInitParameter(MAX_TOTAL_CONNECTION_PROPERTY, 200, Integer.class);
        final int defaultMaxPerRoute = getInitParameter(DEFAULT_MAX_CONNECTION_PER_ROUTE_PROPERTY, 20, Integer.class);

        final PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager(socketFactoryRegistry, null, null, dnsResolver, timeToLive, timeUnit);
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return connectionManager;
    }

    /**
     * Builds the SSL socket factory for HTTPS connections.
     *
     * @param httpClientBuilder The HTTP client builder
     * @return The configured SSL socket factory
     */
    protected LayeredConnectionSocketFactory buildSSLSocketFactory(final HttpClientBuilder httpClientBuilder) {
        if (sslSocketFactory != null) {
            return sslSocketFactory;
        }
        if (getInitParameter(IGNORE_SSL_CERTIFICATE_PROPERTY, false, Boolean.class)) {
            try {
                final SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
                httpClientBuilder.setSSLContext(sslContext);
                logger.warn(
                        "SSL certificate validation is disabled. This configuration is insecure and should only be used in development/testing environments.");
                return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
            } catch (final Exception e) {
                logger.warn("Failed to create SSL context with trust-all strategy: property={}", IGNORE_SSL_CERTIFICATE_PROPERTY, e);
            }
        }
        return SSLConnectionSocketFactory.getSocketFactory();
    }

    /**
     * Builds the cookie specification registry.
     *
     * @return The configured cookie specification registry
     */
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

    @Override
    public void close() {
        if (httpClient == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Closing HTTP client...");
        }
        if (connectionMonitorTask != null) {
            connectionMonitorTask.cancel();
            if (logger.isDebugEnabled()) {
                logger.debug("Connection monitor task cancelled");
            }
        }
        if (httpClient != null) {
            try {
                httpClient.close();
                if (logger.isDebugEnabled()) {
                    logger.debug("HTTP client closed successfully");
                }
            } catch (final IOException e) {
                logger.warn("Failed to close HTTP client", e);
            }
            httpClient = null;
            if (clientConnectionManager != null) {
                clientConnectionManager.shutdown();
                if (logger.isDebugEnabled()) {
                    logger.debug("HTTP client connection manager shutdown complete");
                }
            }
        }
    }

    /**
     * Adds a property to the HTTP client configuration.
     *
     * @param name The property name
     * @param value The property value
     */
    public void addHttpClientProperty(final String name, final Object value) {
        if (StringUtil.isNotBlank(name) && value != null) {
            httpClientPropertyMap.put(name, value);
        }
    }

    /**
     * Processes robots.txt for the given URL.
     * This method fetches and parses the robots.txt file to extract disallow/allow rules
     * and sitemap information.
     *
     * @param url The URL to process robots.txt for
     */
    protected void processRobotsTxt(final String url) {
        if (StringUtil.isBlank(url)) {
            throw new CrawlerSystemException("HTTP URL is null or empty. Cannot process robots.txt without a valid URL.");
        }

        if (robotsTxtHelper == null || !robotsTxtHelper.isEnabled()) {
            // not support robots.txt
            return;
        }

        // crawler context
        final CrawlerContext crawlerContext = CrawlingParameterUtil.getCrawlerContext();
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
                logger.debug("{} is already visited.", robotTxtUrl);
            }
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("Checking URL: {}", robotTxtUrl);
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
                final Header contentLengthHeader = response.getFirstHeader("Content-Length");
                if (contentLengthHeader != null) {
                    final String value = contentLengthHeader.getValue();
                    final long contentLength = Long.parseLong(value);
                    if (contentLengthHelper != null) {
                        final long maxLength = contentLengthHelper.getMaxLength("text/plain");
                        if (contentLength > maxLength) {
                            throw new MaxLengthExceededException("The content length (" + contentLength + " byte) is over " + maxLength
                                    + " byte. The url is " + robotTxtUrl);
                        }
                    }
                }

                if (httpEntity != null) {
                    final RobotsTxt robotsTxt = robotsTxtHelper.parse(httpEntity.getContent());
                    if (robotsTxt != null) {
                        final String[] sitemaps = robotsTxt.getSitemaps();
                        if (sitemaps.length > 0) {
                            crawlerContext.addSitemaps(sitemaps);
                        }

                        final RobotsTxt.Directive directive = robotsTxt.getMatchedDirective(userAgent);
                        if (directive != null) {
                            if (useRobotsTxtDisallows) {
                                for (String urlPattern : directive.getDisallows()) {
                                    if (StringUtil.isNotBlank(urlPattern)) {
                                        urlPattern = convertRobotsTxtPatternToRegex(urlPattern);
                                        final String urlValue = hostUrl + urlPattern;
                                        crawlerContext.getUrlFilter().addExclude(urlValue);
                                        if (logger.isInfoEnabled()) {
                                            logger.info("Excluded URL: {}", urlValue);
                                        }
                                    }
                                }
                            }
                            if (useRobotsTxtAllows) {
                                for (String urlPattern : directive.getAllows()) {
                                    if (StringUtil.isNotBlank(urlPattern)) {
                                        urlPattern = convertRobotsTxtPatternToRegex(urlPattern);
                                        final String urlValue = hostUrl + urlPattern;
                                        crawlerContext.getUrlFilter().addInclude(urlValue);
                                        if (logger.isInfoEnabled()) {
                                            logger.info("Included URL: {}", urlValue);
                                        }
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
            throw new CrawlingAccessException("Could not process " + robotTxtUrl + ". ", e);
        } finally {
            EntityUtils.consumeQuietly(httpEntity);
        }
    }

    /**
     * Converts a robots.txt pattern to a regular expression.
     *
     * @param path The robots.txt pattern to convert
     * @return The equivalent regular expression
     */
    protected String convertRobotsTxtPatternToRegex(final String path) {
        String newPath = path.replace(".", "\\.").replace("?", "\\?").replace("*", ".*");
        if (newPath.charAt(0) != '/') {
            newPath = ".*" + newPath;
        }
        if (!newPath.endsWith("$") && !newPath.endsWith(".*")) {
            newPath = newPath + ".*";
        }
        return newPath.replace(".*.*", ".*");
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
            throw new CrawlingAccessException("The url may not be valid: " + url, e);
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
            throw new CrawlingAccessException("The url may not be valid: " + url, e);
        }
        return doHttpMethod(url, httpHead);
    }

    /**
     * Executes an HTTP method for the given URL and request.
     *
     * @param url The URL to access
     * @param httpRequest The HTTP request to execute
     * @return The response data
     */
    public ResponseData doHttpMethod(final String url, final HttpUriRequest httpRequest) {
        if (httpClient == null) {
            init();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Accessing {}", url);
        }

        // start
        AccessTimeoutTarget accessTimeoutTarget = null;
        TimeoutTask accessTimeoutTask = null;
        if (accessTimeout != null) {
            accessTimeoutTarget = new AccessTimeoutTarget(Thread.currentThread());
            accessTimeoutTask = TimeoutManager.getInstance().addTimeoutTarget(accessTimeoutTarget, accessTimeout, false);
        }

        try {
            return processHttpMethod(url, httpRequest);
        } finally {
            if (accessTimeoutTarget != null) {
                accessTimeoutTarget.stop();
                if (!accessTimeoutTask.isCanceled()) {
                    accessTimeoutTask.cancel();
                }
            }
        }
    }

    /**
     * Processes an HTTP method request and returns the response data.
     * This method handles the complete HTTP request lifecycle including content processing,
     * redirect handling, and error management.
     *
     * @param url The URL being accessed
     * @param httpRequest The HTTP request to process
     * @return The response data containing the retrieved information
     */
    protected ResponseData processHttpMethod(final String url, final HttpUriRequest httpRequest) {
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
                logger.debug("Crawling Access Exception at {}", url, e);
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
                final Header locationHeader = response.getFirstHeader("location");
                if (locationHeader != null) {
                    final String redirectLocation;
                    if (locationHeader.getValue().startsWith("/")) {
                        redirectLocation = constructRedirectLocation(url, locationHeader.getValue());
                    } else {
                        redirectLocation = locationHeader.getValue();
                    }
                    responseData.setRedirectLocation(redirectLocation);
                    return responseData;
                }
                throw new CrawlingAccessException("Invalid redirect location at " + url);
            }

            String contentType = null;
            final Header contentTypeHeader = response.getFirstHeader("Content-Type");
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
                try (final DeferredFileOutputStream dfos = DeferredFileOutputStream.builder()
                        .setThreshold((int) maxCachedContentSize)
                        .setPrefix("crawler-Hc4HttpClient-")
                        .setSuffix(".out")
                        .setDirectory(SystemUtils.getJavaIoTmpDir())
                        .get()) {
                    CopyUtil.copy(responseBodyStream, dfos);
                    dfos.flush();

                    if (dfos.isInMemory()) {
                        responseData.setResponseBody(dfos.getData());
                        contentLength = dfos.getData().length;
                        if (contentType == null) {
                            try (InputStream is = new ByteArrayInputStream(dfos.getData())) {
                                contentType = mimeTypeHelper.getContentType(is, url);
                            } catch (final Exception e) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Failed to detect MIME type, using default: url={}, defaultMimeType={}", url,
                                            defaultMimeType, e);
                                }
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
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Failed to detect MIME type from file, using default: url={}, file={}, defaultMimeType={}",
                                            url, outputFile.getAbsolutePath(), defaultMimeType, e);
                                }
                                contentType = defaultMimeType;
                            }
                        }
                    }
                }

                final Header contentEncodingHeader = httpEntity.getContentEncoding();
                if (contentEncodingHeader != null) {
                    contentEncoding = contentEncodingHeader.getValue();
                }
            }

            // check file size
            if (contentLengthHelper != null) {
                final long maxLength = contentLengthHelper.getMaxLength(contentType);
                if (contentLength > maxLength) {
                    throw new MaxLengthExceededException(
                            "The content length (" + contentLength + " byte) is over " + maxLength + " byte. The url is " + url);
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
            final Header contentLengthHeader = response.getFirstHeader("Content-Length");
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
            final Header lastModifiedHeader = response.getFirstHeader("Last-Modified");
            if (lastModifiedHeader != null) {
                final String value = lastModifiedHeader.getValue();
                if (StringUtil.isNotBlank(value)) {
                    final Date d = parseLastModifiedDate(value);
                    if (d != null) {
                        responseData.setLastModified(d);
                    }
                }
            }

            return responseData;
        } catch (final UnknownHostException e) {
            closeResources(httpRequest, responseData);
            throw new CrawlingAccessException("Unknown host(" + e.getMessage() + "): " + url, e);
        } catch (final NoRouteToHostException e) {
            closeResources(httpRequest, responseData);
            throw new CrawlingAccessException("No route to host(" + e.getMessage() + "): " + url, e);
        } catch (final ConnectException e) {
            closeResources(httpRequest, responseData);
            throw new CrawlingAccessException("Connection time out(" + e.getMessage() + "): " + url, e);
        } catch (final SocketException e) {
            closeResources(httpRequest, responseData);
            throw new CrawlingAccessException("Socket exception(" + e.getMessage() + "): " + url, e);
        } catch (final IOException e) {
            closeResources(httpRequest, responseData);
            throw new CrawlingAccessException("I/O exception(" + e.getMessage() + "): " + url, e);
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

    /**
     * Closes resources associated with the HTTP request.
     *
     * @param httpRequest The HTTP request to abort
     * @param responseData The response data to close
     */
    protected void closeResources(final HttpUriRequest httpRequest, final ResponseData responseData) {
        CloseableUtil.closeQuietly(responseData);
        httpRequest.abort();
    }

    /**
     * Checks if the HTTP status code indicates a redirect.
     *
     * @param httpStatusCode The HTTP status code to check
     * @return True if the status code indicates a redirect, false otherwise
     */
    protected boolean isRedirectHttpStatus(final int httpStatusCode) {
        return redirectHttpStatusPattern.matcher(Integer.toString(httpStatusCode)).matches();
    }

    /**
     * Executes the HTTP client request.
     *
     * @param httpRequest The HTTP request to execute
     * @return The HTTP response
     * @throws IOException If an I/O error occurs
     */
    protected HttpResponse executeHttpClient(final HttpUriRequest httpRequest) throws IOException {
        return httpClient.execute(httpRequest, new BasicHttpContext(httpClientContext));
    }

    /** Thread-local SimpleDateFormat for parsing non-standard date formats */
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT_HOLDER =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH));

    /**
     * Parses the last modified date from a string value.
     * First attempts to parse using Apache DateUtils (which supports RFC 1123, RFC 1036, and ANSI C formats).
     * Falls back to a custom pattern for non-standard formats.
     *
     * @param value The date string to parse
     * @return The parsed date, or null if parsing fails
     */
    protected Date parseLastModifiedDate(final String value) {
        // Try Apache DateUtils first (handles standard HTTP date formats)
        final Date date = DateUtils.parseDate(value);
        if (date != null) {
            return date;
        }
        // Fallback to custom pattern for non-standard formats (e.g., single-digit day)
        try {
            return DATE_FORMAT_HOLDER.get().parse(value);
        } catch (final ParseException e) {
            return null;
        }
    }

    /**
     * Builds the HTTP route planner with proxy configuration.
     *
     * @return The configured route planner, or null if no proxy is configured
     */
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

    /**
     * Constructs the redirect location from a base URL and location header.
     *
     * @param url The base URL
     * @param location The location header value
     * @return The constructed redirect location
     */
    protected static String constructRedirectLocation(final String url, final String location) {
        try {
            URI uri = new URI(url);
            if (StringUtil.isNotEmpty(location)) {
                uri = uri.resolve(location.replace(" ", "%20"));
            }
            return uri.normalize().toASCIIString();
        } catch (URISyntaxException e) {
            throw new CrawlingAccessException(e);
        }
    }

    /**
     * Sets the connection timeout in milliseconds.
     *
     * @param connectionTimeout The connection timeout
     */
    public void setConnectionTimeout(final Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Sets the maximum total number of connections.
     *
     * @param maxTotalConnections The maximum total connections
     */
    public void setMaxTotalConnections(final Integer maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
    }

    /**
     * Sets the maximum connections per route.
     *
     * @param maxConnectionsPerRoute The maximum connections per route
     */
    public void setMaxConnectionsPerRoute(final Integer maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    /**
     * Sets the socket timeout in milliseconds.
     *
     * @param soTimeout The socket timeout
     */
    public void setSoTimeout(final Integer soTimeout) {
        this.soTimeout = soTimeout;
    }

    /**
     * Sets the cookie specification to use.
     *
     * @param cookieSpec The cookie specification
     */
    public void setCookieSpec(final String cookieSpec) {
        this.cookieSpec = cookieSpec;
    }

    /**
     * Sets the user agent string.
     *
     * @param userAgent The user agent string
     */
    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Sets the proxy host name.
     *
     * @param proxyHost The proxy host name
     */
    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * Sets the proxy port number.
     *
     * @param proxyPort The proxy port number
     */
    public void setProxyPort(final Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    /**
     * Sets the proxy authentication scheme.
     *
     * @param proxyAuthScheme The proxy authentication scheme
     */
    public void setProxyAuthScheme(final AuthScheme proxyAuthScheme) {
        this.proxyAuthScheme = proxyAuthScheme;
    }

    /**
     * Sets the proxy credentials.
     *
     * @param proxyCredentials The proxy credentials
     */
    public void setProxyCredentials(final Credentials proxyCredentials) {
        this.proxyCredentials = proxyCredentials;
    }

    /**
     * Sets the default MIME type for unknown content.
     *
     * @param defaultMimeType The default MIME type
     */
    public void setDefaultMimeType(final String defaultMimeType) {
        this.defaultMimeType = defaultMimeType;
    }

    /**
     * Sets the cookie store for managing cookies.
     *
     * @param cookieStore The cookie store
     */
    public void setCookieStore(final CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    /**
     * Sets the HTTP client context for requests.
     *
     * @param httpClientContext The HTTP client context
     */
    public void setHttpClientContext(final HttpClientContext httpClientContext) {
        this.httpClientContext = httpClientContext;
    }

    /**
     * Sets the authentication scheme provider map.
     *
     * @param authSchemeProviderMap The authentication scheme provider map
     */
    public void setAuthSchemeProviderMap(final Map<String, AuthSchemeProvider> authSchemeProviderMap) {
        this.authSchemeProviderMap = authSchemeProviderMap;
    }

    /**
     * Sets the connection check interval in seconds.
     *
     * @param connectionCheckInterval The connection check interval
     */
    public void setConnectionCheckInterval(final int connectionCheckInterval) {
        this.connectionCheckInterval = connectionCheckInterval;
    }

    /**
     * Sets the idle connection timeout in milliseconds.
     *
     * @param idleConnectionTimeout The idle connection timeout
     */
    public void setIdleConnectionTimeout(final long idleConnectionTimeout) {
        this.idleConnectionTimeout = idleConnectionTimeout;
    }

    /**
     * Sets the pattern for matching HTTP redirect status codes.
     *
     * @param redirectHttpStatusPattern The redirect HTTP status pattern
     */
    public void setRedirectHttpStatusPattern(final Pattern redirectHttpStatusPattern) {
        this.redirectHttpStatusPattern = redirectHttpStatusPattern;
    }

    /**
     * Sets whether to use robots.txt disallow rules.
     *
     * @param useRobotsTxtDisallows True to use disallow rules, false otherwise
     */
    public void setUseRobotsTxtDisallows(final boolean useRobotsTxtDisallows) {
        this.useRobotsTxtDisallows = useRobotsTxtDisallows;
    }

    /**
     * Sets whether to use robots.txt allow rules.
     *
     * @param useRobotsTxtAllows True to use allow rules, false otherwise
     */
    public void setUseRobotsTxtAllows(final boolean useRobotsTxtAllows) {
        this.useRobotsTxtAllows = useRobotsTxtAllows;
    }

    /**
     * Sets the credentials provider for authentication.
     *
     * @param credentialsProvider The credentials provider
     */
    public void setCredentialsProvider(final CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    /**
     * Sets the authentication cache.
     *
     * @param authCache The authentication cache
     */
    public void setAuthCache(final AuthCache authCache) {
        this.authCache = authCache;
    }

    /**
     * Sets the HTTP route planner.
     *
     * @param routePlanner The HTTP route planner
     */
    public void setRoutePlanner(final HttpRoutePlanner routePlanner) {
        this.routePlanner = routePlanner;
    }

    /**
     * Sets whether HTTP redirects are enabled.
     *
     * @param redirectsEnabled True to enable redirects, false otherwise
     */
    public void setRedirectsEnabled(final boolean redirectsEnabled) {
        this.redirectsEnabled = redirectsEnabled;
    }

    /**
     * Sets the cookie specification registry.
     *
     * @param cookieSpecRegistry The cookie specification registry
     */
    public void setCookieSpecRegistry(final Lookup<CookieSpecProvider> cookieSpecRegistry) {
        this.cookieSpecRegistry = cookieSpecRegistry;
    }

    /**
     * Sets the cookie date patterns for parsing.
     *
     * @param cookieDatePatterns The cookie date patterns
     */
    public void setCookieDatePatterns(final String[] cookieDatePatterns) {
        this.cookieDatePatterns = cookieDatePatterns;
    }

    /**
     * Sets the DNS resolver for hostname resolution.
     *
     * @param dnsResolver The DNS resolver
     */
    public void setDnsResolver(final DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver;
    }

    /**
     * Sets the SSL socket factory.
     * @param sslSocketFactory The SSL socket factory.
     */
    public void setSslSocketFactory(final LayeredConnectionSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }
}
