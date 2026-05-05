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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.input.BoundedInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.beans.BeanDesc;
import org.codelibs.core.beans.PropertyDesc;
import org.codelibs.core.beans.factory.BeanDescFactory;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.client.AccessTimeoutTarget;
import org.codelibs.fess.crawler.client.http.Hc4Authentication;
import org.codelibs.fess.crawler.client.http.RequestHeader;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Extract a text by using external http server.
 */
public class ApiExtractor extends AbstractExtractor {

    private static final Logger logger = LogManager.getLogger(ApiExtractor.class);

    /** Parameter key that, if present, overrides the configured URL for a single request. */
    public static final String PARAM_EXTRACTOR_URL = "extractorUrl";

    /** The URL of the API endpoint. */
    protected String url;

    /** The access timeout in seconds. */
    protected Integer accessTimeout; // sec

    /** The HTTP client used for API calls. */
    protected volatile CloseableHttpClient httpClient;

    /** The connection timeout in milliseconds. */
    protected Integer connectionTimeout;

    /** The socket timeout in milliseconds. */
    protected Integer soTimeout;

    /** Maximum total connections in the pool. */
    protected int maxConnections = 50;

    /** Maximum connections per route in the pool. */
    protected int maxConnectionsPerRoute = 10;

    /** Maximum size in bytes accepted for an API response body. Default is 100 MiB. */
    protected long maxResponseSize = 100L * 1024L * 1024L;

    /** Maximum size in bytes accepted for an outgoing request body. Default is 100 MiB. */
    protected long maxRequestSize = 100L * 1024L * 1024L;

    /** Maximum number of retry attempts on transient failures. */
    protected int maxRetries = 2;

    /** Initial backoff in milliseconds between retries (doubled per attempt). */
    protected long retryBackoffMs = 500L;

    /** The map of authentication scheme providers. */
    protected Map<String, AuthSchemeProvider> authSchemeProviderMap;

    /** The user agent string. */
    protected String userAgent = "Crawler";

    /** The credentials provider. */
    protected CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    /** The authentication cache. */
    protected AuthCache authCache = new BasicAuthCache();

    /** The HTTP client context. */
    protected HttpClientContext httpClientContext = HttpClientContext.create();

    /** Map of HTTP client properties */
    private final Map<String, Object> httpClientPropertyMap = new HashMap<>();

    /** List of request headers */
    private final List<Header> requestHeaderList = new ArrayList<>();

    /** Pooled connection manager backing the HTTP client. */
    protected PoolingHttpClientConnectionManager connectionManager;

    /**
     * Constructs a new ApiExtractor.
     */
    public ApiExtractor() {
        // NOP
    }

    /**
     * Initializes the API extractor, setting up the HTTP client and configuration.
     * This method is called after construction to initialize the HTTP client with
     * configured timeouts, authentication, and request headers.
     */
    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing {}", ApiExtractor.class.getName());
        }

        // httpclient
        final org.apache.http.client.config.RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        final Integer connectionTimeoutParam = connectionTimeout;
        if (connectionTimeoutParam != null) {
            requestConfigBuilder.setConnectTimeout(connectionTimeoutParam);

        } else {
            // sane default to avoid hanging on dead servers
            requestConfigBuilder.setConnectTimeout(5000);
        }
        final Integer soTimeoutParam = soTimeout;
        if (soTimeoutParam != null) {
            requestConfigBuilder.setSocketTimeout(soTimeoutParam);
        } else {
            // sane default to mitigate slow loris servers
            requestConfigBuilder.setSocketTimeout(30000);
        }

        // AuthSchemeFactory
        final RegistryBuilder<AuthSchemeProvider> authSchemeProviderBuilder = RegistryBuilder.create();
        // @SuppressWarnings("unchecked")
        final Map<String, AuthSchemeProvider> factoryMap = authSchemeProviderMap;
        if (factoryMap != null) {
            for (final Map.Entry<String, AuthSchemeProvider> entry : factoryMap.entrySet()) {
                authSchemeProviderBuilder.register(entry.getKey(), entry.getValue());
            }
        }

        // user agent
        if (StringUtil.isNotBlank(userAgent)) {
            httpClientBuilder.setUserAgent(userAgent);
        }

        // Authentication
        final Hc4Authentication[] siteCredentialList = {};
        for (final Hc4Authentication authentication : siteCredentialList) {
            final AuthScope authScope = authentication.getAuthScope();
            credentialsProvider.setCredentials(authScope, authentication.getCredentials());
            final AuthScheme authScheme = authentication.getAuthScheme();
            if (authScope.getHost() != null && authScheme != null) {
                final HttpHost targetHost = new HttpHost(authScope.getHost(), authScope.getPort());
                authCache.put(targetHost, authScheme);
            }
        }

        httpClientContext.setAuthCache(authCache);
        httpClientContext.setCredentialsProvider(credentialsProvider);

        // Request Header
        final RequestHeader[] requestHeaders = { new RequestHeader("enctype", "multipart/form-data") };
        for (final RequestHeader requestHeader : requestHeaders) {
            if (requestHeader.isValid()) {
                requestHeaderList.add(new BasicHeader(requestHeader.getName(), requestHeader.getValue()));
            }
        }

        // Pooled connection manager so connections are reused across calls.
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
        httpClientBuilder.setConnectionManager(connectionManager);
        // Disable the built-in retry handler; we implement our own classification below.
        httpClientBuilder.disableAutomaticRetries();

        final CloseableHttpClient closeableHttpClient = httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build()).build();
        if (!httpClientPropertyMap.isEmpty()) {
            final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(closeableHttpClient.getClass());
            for (final Map.Entry<String, Object> entry : httpClientPropertyMap.entrySet()) {
                final String propertyName = entry.getKey();
                if (beanDesc.hasPropertyDesc(propertyName)) {
                    final PropertyDesc propertyDesc = beanDesc.getPropertyDesc(propertyName);
                    propertyDesc.setValue(closeableHttpClient, entry.getValue());
                } else {
                    logger.warn("DefaultHttpClient does not have {}.", propertyName);
                }
            }
        }

        httpClient = closeableHttpClient;
    }

    /**
     * Destroys the HTTP client.
     */
    @PreDestroy
    public void destroy() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (final IOException e) {
                logger.warn("Failed to close HTTP client for API extractor", e);
            } finally {
                httpClient = null;
            }
        }
        if (connectionManager != null) {
            try {
                connectionManager.close();
            } catch (final Exception e) {
                logger.warn("Failed to close connection manager for API extractor", e);
            } finally {
                connectionManager = null;
            }
        }
    }

    /**
     * Extracts text from the input stream using the API endpoint.
     *
     * @param in the input stream to extract text from
     * @param params additional parameters (an {@code extractorUrl} entry overrides the configured URL)
     * @return the extracted data
     * @throws ExtractException if extraction fails
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        // Allow per-call URL override.
        String targetUrl = url;
        if (params != null) {
            final String override = params.get(PARAM_EXTRACTOR_URL);
            if (StringUtil.isNotBlank(override)) {
                targetUrl = override;
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Accessing {}", targetUrl);
        }

        // start
        AccessTimeoutTarget accessTimeoutTarget = null;
        TimeoutTask accessTimeoutTask = null;
        if (accessTimeout != null) {
            accessTimeoutTarget = new AccessTimeoutTarget(Thread.currentThread());
            accessTimeoutTask = TimeoutManager.getInstance().addTimeoutTarget(accessTimeoutTarget, accessTimeout, false);
        }

        try {
            // The request body is the supplied input stream; multipart body builders consume it,
            // so we can only execute the post once. Retries thus only apply when the request
            // can be re-issued — currently only on connection-establishment failures or
            // a status-code-only retry path that doesn't consume the entity. To support retries
            // we buffer the request stream into memory, bounded by maxRequestSize so a hostile
            // or malformed source document cannot OOM the extractor.
            return executeWithRetries(in, targetUrl);
        } finally {
            if (accessTimeout != null) {
                accessTimeoutTarget.stop();
                if (!accessTimeoutTask.isCanceled()) {
                    accessTimeoutTask.cancel();
                }
            }
        }
    }

    /**
     * Executes the API request with bounded retries on transient failures.
     *
     * @param in the input stream to forward to the API endpoint
     * @param targetUrl the URL to call
     * @return the extracted data
     */
    protected ExtractData executeWithRetries(final InputStream in, final String targetUrl) {
        // Buffer the input once so we can re-send it on retry. Bounded to maxRequestSize so a
        // hostile/large source document cannot OOM the extractor.
        final byte[] requestBody;
        try (BoundedInputStream bounded = BoundedInputStream.builder().setInputStream(in).setMaxCount(maxRequestSize + 1L).get()) {
            requestBody = bounded.readAllBytes();
        } catch (final IOException e) {
            throw new ExtractException("Failed to read input stream for API extractor", e);
        }
        if (requestBody.length > maxRequestSize) {
            throw new ExtractException("ApiExtractor request body exceeded limit: limit=" + maxRequestSize);
        }

        IOException lastIoException = null;
        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return executeOnce(requestBody, targetUrl, attempt);
            } catch (final RetryableStatusException e) {
                final long sleepMs = computeBackoff(attempt, e.retryAfterMs);
                if (attempt >= maxRetries) {
                    throw new ExtractException("API request failed after " + (attempt + 1) + " attempts: status=" + e.statusCode);
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Retrying API request (status={}, attempt={}/{}, sleep={}ms)", e.statusCode, attempt + 1, maxRetries + 1,
                            sleepMs);
                }
                sleepQuietly(sleepMs);
            } catch (final ConnectTimeoutException e) {
                lastIoException = e;
                if (attempt >= maxRetries) {
                    throw new ExtractException("API request failed", e);
                }
                final long sleepMs = computeBackoff(attempt, -1L);
                if (logger.isDebugEnabled()) {
                    logger.debug("Retrying API request after connect timeout (attempt={}/{}, sleep={}ms)", attempt + 1, maxRetries + 1,
                            sleepMs, e);
                }
                sleepQuietly(sleepMs);
            } catch (final NoHttpResponseException e) {
                lastIoException = e;
                if (attempt >= maxRetries) {
                    throw new ExtractException("API request failed", e);
                }
                final long sleepMs = computeBackoff(attempt, -1L);
                if (logger.isDebugEnabled()) {
                    logger.debug("Retrying API request after no-response (attempt={}/{}, sleep={}ms)", attempt + 1, maxRetries + 1, sleepMs,
                            e);
                }
                sleepQuietly(sleepMs);
            } catch (final IOException e) {
                lastIoException = e;
                if (attempt >= maxRetries) {
                    throw new ExtractException("API request failed", e);
                }
                final long sleepMs = computeBackoff(attempt, -1L);
                if (logger.isDebugEnabled()) {
                    logger.debug("Retrying API request after I/O error (attempt={}/{}, sleep={}ms)", attempt + 1, maxRetries + 1, sleepMs,
                            e);
                }
                sleepQuietly(sleepMs);
            }
        }

        // Should not reach here, but defend against it.
        throw new ExtractException("API request failed", lastIoException != null ? lastIoException : new IOException("unknown error"));
    }

    /**
     * Executes a single API call.
     *
     * @param requestBody bytes to upload as the multipart body
     * @param targetUrl URL to POST to
     * @param attempt current attempt number (0-based) — included for logging
     * @return the extracted data on success
     * @throws RetryableStatusException when the response status is retryable
     * @throws IOException on transport-level failure
     */
    protected ExtractData executeOnce(final byte[] requestBody, final String targetUrl, final int attempt) throws IOException {
        if (attempt > 0 && logger.isDebugEnabled()) {
            logger.debug("Executing API request (url={}, attempt={})", targetUrl, attempt);
        }
        final HttpPost httpPost = new HttpPost(targetUrl);
        final HttpEntity postEntity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(Charset.forName("UTF-8"))
                .addBinaryBody("filedata", requestBody)
                .build();
        httpPost.setEntity(postEntity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != Constants.OK_STATUS_CODE) {
                if (isRetryableStatus(statusCode)) {
                    final long retryAfterMs = parseRetryAfterMs(response.getFirstHeader("Retry-After"));
                    // Drain entity so the connection can be reused. Bound the drain so a hostile
                    // server cannot stream an unbounded body just to keep us reading.
                    drainBounded(response.getEntity());
                    throw new RetryableStatusException(statusCode, retryAfterMs);
                }
                logger.warn("Failed to access API extractor endpoint: url={}, statusCode={}, attempt={}", targetUrl, statusCode, attempt);
                drainBounded(response.getEntity());
                return null;
            }

            final ExtractData data = new ExtractData();
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                final Charset charset = resolveCharset(entity);
                try (InputStream entityStream = entity.getContent();
                        BoundedInputStream bounded =
                                BoundedInputStream.builder().setInputStream(entityStream).setMaxCount(maxResponseSize + 1L).get()) {
                    final byte[] body = bounded.readAllBytes();
                    if (body.length > maxResponseSize) {
                        throw new ExtractException("ApiExtractor response exceeded limit: limit=" + maxResponseSize);
                    }
                    data.setContent(new String(body, charset));
                }
            } else {
                data.setContent("");
            }
            final Header[] headers = response.getAllHeaders();
            for (final Header header : headers) {
                data.putValue(header.getName(), header.getValue());
            }
            return data;
        }
    }

    /**
     * Returns true if the given status code is considered retryable.
     *
     * @param statusCode HTTP status code
     * @return true when the request should be retried
     */
    protected boolean isRetryableStatus(final int statusCode) {
        if (statusCode >= 500 && statusCode <= 599) {
            return true;
        }
        return statusCode == 408 || statusCode == 429;
    }

    /**
     * Parses a {@code Retry-After} header into milliseconds. Both delta-seconds and the
     * HTTP-date form (RFC 7231) are honored; on parse failure {@code -1L} is returned and the
     * caller should fall back to the default backoff schedule.
     *
     * @param header the {@code Retry-After} header (may be null)
     * @return delay in milliseconds, or {@code -1L} if unspecified or invalid
     */
    protected long parseRetryAfterMs(final Header header) {
        if (header == null) {
            return -1L;
        }
        final String value = header.getValue();
        if (StringUtil.isBlank(value)) {
            return -1L;
        }
        final String trimmed = value.trim();
        // Try delta-seconds first.
        try {
            final long seconds = Long.parseLong(trimmed);
            if (seconds < 0L) {
                return -1L;
            }
            return seconds * 1000L;
        } catch (final NumberFormatException ignore) {
            // Not a numeric value — try HTTP-date form below.
        }
        // HTTP-date form per RFC 7231.
        final Date when = DateUtils.parseDate(trimmed);
        if (when == null) {
            return -1L;
        }
        final long deltaMs = when.getTime() - System.currentTimeMillis();
        if (deltaMs <= 0L) {
            return 0L;
        }
        return deltaMs;
    }

    /**
     * Computes exponential backoff for a retry attempt. A small random jitter in the range
     * {@code [0, retryBackoffMs)} is added to the exponential base to avoid thundering-herd
     * retry storms when many extractors hit the same upstream concurrently.
     *
     * @param attempt 0-based attempt number
     * @param retryAfterMs server-suggested delay; non-negative values take precedence
     * @return sleep duration in milliseconds
     */
    protected long computeBackoff(final int attempt, final long retryAfterMs) {
        if (retryAfterMs >= 0L) {
            return retryAfterMs;
        }
        final long base = retryBackoffMs * (1L << attempt);
        final long jitter = retryBackoffMs > 0L ? ThreadLocalRandom.current().nextLong(0L, retryBackoffMs) : 0L;
        return base + jitter;
    }

    /**
     * Sleeps without throwing on interruption (preserves the interrupt flag).
     *
     * @param millis sleep duration in milliseconds
     */
    protected void sleepQuietly(final long millis) {
        if (millis <= 0L) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Drains the response entity, ignoring errors. Safe even when the entity is null.
     *
     * @param entity entity to consume (may be null)
     */
    protected void consumeQuietly(final HttpEntity entity) {
        if (entity == null) {
            return;
        }
        try {
            org.apache.http.util.EntityUtils.consumeQuietly(entity);
        } catch (final Exception e) {
            // ignore
        }
    }

    /**
     * Drains the response entity content with a hard upper bound of {@link #maxResponseSize}
     * bytes so a hostile server cannot trickle out an unbounded body purely to keep us reading.
     * Errors are intentionally swallowed: the goal is just to make the connection reusable.
     *
     * @param entity entity to drain (may be null)
     */
    protected void drainBounded(final HttpEntity entity) {
        if (entity == null) {
            return;
        }
        try (InputStream content = entity.getContent();
                BoundedInputStream bounded = BoundedInputStream.builder().setInputStream(content).setMaxCount(maxResponseSize).get()) {
            final byte[] buf = new byte[8192];
            while (bounded.read(buf) >= 0) {
                // discard
            }
        } catch (final Exception e) {
            // ignore — best-effort drain
        }
    }

    /**
     * Resolves the charset advertised by the response entity, falling back to UTF-8.
     *
     * @param entity response entity (non-null)
     * @return charset to decode the response with
     */
    protected Charset resolveCharset(final HttpEntity entity) {
        try {
            final org.apache.http.entity.ContentType contentType = org.apache.http.entity.ContentType.get(entity);
            if (contentType != null && contentType.getCharset() != null) {
                return contentType.getCharset();
            }
        } catch (final Exception e) {
            // ignore and fall back to UTF-8
        }
        return Constants.UTF_8_CHARSET;
    }

    /** Internal signal that a response was retryable; carries the status and any Retry-After hint. */
    @SuppressWarnings("serial")
    protected static class RetryableStatusException extends IOException {
        final int statusCode;
        final long retryAfterMs;

        RetryableStatusException(final int statusCode, final long retryAfterMs) {
            super("retryable status: " + statusCode);
            this.statusCode = statusCode;
            this.retryAfterMs = retryAfterMs;
        }
    }

    /**
     * Sets the URL of the API endpoint.
     * @param url The URL to set.
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Sets the connection timeout.
     * @param connectionTimeout The connection timeout in milliseconds.
     */
    public void setConnectionTimeout(final Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Sets the socket timeout.
     * @param soTimeout The socket timeout in milliseconds.
     */
    public void setSoTimeout(final Integer soTimeout) {
        this.soTimeout = soTimeout;
    }

    /**
     * Sets the map of authentication scheme providers.
     * @param authSchemeProviderMap The map of authentication scheme providers.
     */
    public void setAuthSchemeProviderMap(final Map<String, AuthSchemeProvider> authSchemeProviderMap) {
        this.authSchemeProviderMap = authSchemeProviderMap;
    }

    /**
     * Sets the user agent string.
     * @param userAgent The user agent string.
     */
    public void setUserAgent(final String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Sets the credentials provider.
     * @param credentialsProvider The credentials provider.
     */
    public void setCredentialsProvider(final CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    /**
     * Sets the authentication cache.
     * @param authCache The authentication cache.
     */
    public void setAuthCache(final AuthCache authCache) {
        this.authCache = authCache;
    }

    /**
     * Sets the HTTP client context.
     * @param httpClientContext The HTTP client context.
     */
    public void setHttpClientContext(final HttpClientContext httpClientContext) {
        this.httpClientContext = httpClientContext;
    }

    /**
     * Sets the access timeout.
     * @param accessTimeout The access timeout to set.
     */
    public void setAccessTimeout(final Integer accessTimeout) {
        this.accessTimeout = accessTimeout;
    }

    /**
     * Sets the maximum total connections in the pool.
     * @param maxConnections maximum total pooled connections
     */
    public void setMaxConnections(final int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * Sets the maximum connections per route in the pool.
     * @param maxConnectionsPerRoute maximum pooled connections per route
     */
    public void setMaxConnectionsPerRoute(final int maxConnectionsPerRoute) {
        this.maxConnectionsPerRoute = maxConnectionsPerRoute;
    }

    /**
     * Sets the maximum size in bytes accepted for an API response body.
     * Responses larger than this trigger an {@link ExtractException}.
     * @param maxResponseSize maximum accepted response size in bytes
     */
    public void setMaxResponseSize(final long maxResponseSize) {
        this.maxResponseSize = maxResponseSize;
    }

    /**
     * Sets the maximum size in bytes accepted for an outgoing request body.
     * Inputs larger than this trigger an {@link ExtractException} before any HTTP call is made.
     * @param maxRequestSize maximum accepted request body size in bytes
     */
    public void setMaxRequestSize(final long maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    /**
     * Sets the maximum number of retry attempts for transient failures.
     * @param maxRetries number of retries (in addition to the initial attempt)
     */
    public void setMaxRetries(final int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * Sets the initial backoff in milliseconds between retries.
     * The actual delay doubles per attempt.
     * @param retryBackoffMs initial backoff in milliseconds
     */
    public void setRetryBackoffMs(final long retryBackoffMs) {
        this.retryBackoffMs = retryBackoffMs;
    }

}
