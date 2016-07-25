package org.codelibs.fess.crawler.extractor.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;

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
import org.codelibs.core.beans.BeanDesc;
import org.codelibs.core.beans.PropertyDesc;
import org.codelibs.core.beans.factory.BeanDescFactory;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.crawler.client.AccessTimeoutTarget;
import org.codelibs.fess.crawler.client.http.Authentication;
import org.codelibs.fess.crawler.client.http.RequestHeader;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;

/**
 * Extract a text by using external http server.
 *
 * @author shinsuke
 *
 */
public class ApiExtractor implements Extractor {

    private static final Logger logger = LoggerFactory.getLogger(ApiExtractor.class);

    protected String url;

    protected Integer accessTimeout; // sec

    protected CloseableHttpClient httpClient;

    protected Integer connectionTimeout;

    protected Integer soTimeout;

    protected Map<String, AuthSchemeProvider> authSchemeProviderMap;

    protected String userAgent = "Crawler";

    protected CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    protected AuthCache authCache = new BasicAuthCache();

    protected HttpClientContext httpClientContext = HttpClientContext.create();

    private final Map<String, Object> httpClientPropertyMap = new HashMap<String, Object>();

    private final List<Header> requestHeaderList = new ArrayList<Header>();

    @PostConstruct
    public void init() {
        if (logger.isDebugEnabled()) {
            logger.debug("Initializing " + ApiExtractor.class.getName());
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
        final Authentication[] siteCredentialList = new Authentication[0];
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
                    logger.warn("DefaultHttpClient does not have " + propertyName + ".");
                }
            }
        }

        httpClient = closeableHttpClient;
    }

    @PreDestroy
    public void destroy() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (final IOException e) {
                logger.error("Failed to close httpClient.", e);
            }
        }
    }

    @Override
    public ExtractData getText(InputStream in, Map<String, String> params) {
        if (logger.isDebugEnabled()) {
            logger.debug("Accessing " + url);
        }

        // start
        AccessTimeoutTarget accessTimeoutTarget = null;
        TimeoutTask accessTimeoutTask = null;
        if (accessTimeout != null) {
            accessTimeoutTarget = new AccessTimeoutTarget(Thread.currentThread());
            accessTimeoutTask = TimeoutManager.getInstance().addTimeoutTarget(accessTimeoutTarget, accessTimeout.intValue(), false);
        }

        ExtractData data = new ExtractData();
        HttpPost httpPost = new HttpPost(url);
        HttpEntity postEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                .setCharset(Charset.forName("UTF-8")).addBinaryBody("filedata", in).build();
        httpPost.setEntity(postEntity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
                logger.error("Failed to access " + url + ", code: " + response.getStatusLine().getStatusCode() + ".");
                return null;
            }

            data.setContent(EntityUtils.toString(response.getEntity(), Charsets.UTF_8));
            Header[] headers = response.getAllHeaders();
            for (final Header header : headers) {
                data.putValue(header.getName(), header.getValue());
            }
        } catch (IOException e) {
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

    public void setUrl(String url) {
        this.url = url;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setSoTimeout(Integer soTimeout) {
        this.soTimeout = soTimeout;
    }

    public void setAuthSchemeProviderMap(Map<String, AuthSchemeProvider> authSchemeProviderMap) {
        this.authSchemeProviderMap = authSchemeProviderMap;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setCredentialsProvider(CredentialsProvider credentialsProvider) {
        this.credentialsProvider = credentialsProvider;
    }

    public void setAuthCache(AuthCache authCache) {
        this.authCache = authCache;
    }

    public void setHttpClientContext(HttpClientContext httpClientContext) {
        this.httpClientContext = httpClientContext;
    }

    public void setAccessTimeout(Integer accessTimeout) {
        this.accessTimeout = accessTimeout;
    }

}
