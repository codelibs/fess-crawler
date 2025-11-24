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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
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
import org.codelibs.fess.crawler.client.http.Authentication;
import org.codelibs.fess.crawler.client.http.RequestHeader;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;

import com.google.common.base.Charsets;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Extract a text by using external http server.
 */
public class ApiExtractor extends AbstractExtractor {

    private static final Logger logger = LogManager.getLogger(ApiExtractor.class);

    /** The URL of the API endpoint. */
    protected String url;

    /** The access timeout in seconds. */
    protected Integer accessTimeout; // sec

    /** The HTTP client used for API calls. */
    protected CloseableHttpClient httpClient;

    /** The connection timeout in milliseconds. */
    protected Integer connectionTimeout;

    /** The socket timeout in milliseconds. */
    protected Integer soTimeout;

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

        }
        final Integer soTimeoutParam = soTimeout;
        if (soTimeoutParam != null) {
            requestConfigBuilder.setSocketTimeout(soTimeoutParam);
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
        final Authentication[] siteCredentialList = {};
        for (final Authentication authentication : siteCredentialList) {
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
            }
        }
    }

    /**
     * Extracts text from the input stream using the API endpoint.
     *
     * @param in the input stream to extract text from
     * @param params additional parameters
     * @return the extracted data
     * @throws ExtractException if extraction fails
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
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

        final ExtractData data = new ExtractData();
        final HttpPost httpPost = new HttpPost(url);
        final HttpEntity postEntity = MultipartEntityBuilder.create()
                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(Charset.forName("UTF-8"))
                .addBinaryBody("filedata", in)
                .build();
        httpPost.setEntity(postEntity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() != Constants.OK_STATUS_CODE) {
                logger.warn("Failed to access API extractor endpoint: url={}, statusCode={}", url,
                        response.getStatusLine().getStatusCode());
                return null;
            }

            data.setContent(EntityUtils.toString(response.getEntity(), Charsets.UTF_8));
            final Header[] headers = response.getAllHeaders();
            for (final Header header : headers) {
                data.putValue(header.getName(), header.getValue());
            }
        } catch (final IOException e) {
            throw new ExtractException(e);
        } finally {
            if (accessTimeout != null) {
                accessTimeoutTarget.stop();
                if (!accessTimeoutTask.isCanceled()) {
                    accessTimeoutTask.cancel();
                }
            }
        }
        return data;
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

}
