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
package org.seasar.robot.http.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.http.HttpClient;
import org.seasar.robot.util.StreamUtil;
import org.seasar.robot.util.TemporaryFileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class CommonsHttpClient implements HttpClient {
    private final Logger logger = LoggerFactory
            .getLogger(CommonsHttpClient.class);

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

    protected synchronized void init() {
        if (httpClient != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing " + CommonsHttpClient.class.getName());
        }

        MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        if (connectionTimeout != null) {
            params.setConnectionTimeout(connectionTimeout);
        }
        if (maxTotalConnections != null) {
            params.setMaxTotalConnections(maxTotalConnections);
        }
        if (staleCheckingEnabled != null) {
            params.setStaleCheckingEnabled(staleCheckingEnabled);
        }
        if (soTimeout != null) {
            params.setSoTimeout(soTimeout);
        }
        if (linger != null) {
            params.setLinger(linger);
        }
        connectionManager.setParams(params);

        httpClient = new org.apache.commons.httpclient.HttpClient(
                connectionManager);

        // proxy
        if (proxyHost != null && proxyPort != null) {
            httpClient.getHostConfiguration().setProxy(proxyHost, proxyPort);
            if (proxyCredentials != null) {
                httpClient.getState().setProxyCredentials(AuthScope.ANY,
                        proxyCredentials);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.http.HttpClient#doGet(java.lang.String)
     */
    public ResponseData doGet(String url) {
        if (httpClient == null) {
            init();
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Accessing " + url);
        }

        GetMethod getMethod = new GetMethod(url);

        // do not redirect
        getMethod.setFollowRedirects(false);

        // cookie
        if (cookiePolicy != null) {
            getMethod.getParams().setCookiePolicy(cookiePolicy);
        }

        // user agent
        httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
                userAgent);

        try {
            // get a content 
            httpClient.executeMethod(getMethod);

            int httpStatusCode = getMethod.getStatusCode();
            // redirect
            if (httpStatusCode >= 300 && httpStatusCode < 400) {
                Header locationHeader = getMethod.getResponseHeader("location");
                if (locationHeader != null) {
                    ResponseData responseData = new ResponseData();
                    responseData.setRedirectLocation(locationHeader.getValue());
                    return responseData;
                } else {
                    logger.warn("Invalid redirect location at " + url);
                }
            }

            File outputFile = File.createTempFile("s2robot-", ".out");
            outputFile.deleteOnExit();
            DeferredFileOutputStream dfos = new DeferredFileOutputStream(
                    responseBodyInMemoryThresholdSize, outputFile);
            StreamUtil.drain(getMethod.getResponseBodyAsStream(), dfos);

            InputStream inputStream = null;
            if (dfos.isInMemory()) {
                inputStream = new ByteArrayInputStream(dfos.getData());
            } else {
                inputStream = new TemporaryFileInputStream(outputFile);
            }
            ResponseData responseData = new ResponseData();
            responseData.setMethod(Constants.GET_METHOD);
            responseData.setUrl(url);
            responseData.setCharSet(getMethod.getResponseCharSet());
            responseData.setContentLength(getMethod.getResponseContentLength());
            responseData.setResponseBody(inputStream);
            responseData.setHttpStatusCode(httpStatusCode);
            for (Header header : getMethod.getResponseHeaders()) {
                responseData.addHeader(header.getName(), header.getValue());
            }
            Header contentTypeHeader = getMethod
                    .getResponseHeader("Content-Type");
            if (contentTypeHeader != null) {
                String contentType = contentTypeHeader.getValue();
                int idx = contentType.indexOf(";");
                if (idx > 0) {
                    contentType = contentType.substring(0, idx);
                }
                responseData.setMimeType(contentType);
            }
            Header contentLengthHeader = getMethod
                    .getResponseHeader("Content-Length");
            if (contentLengthHeader != null) {
                String value = contentLengthHeader.getValue();
                try {
                    responseData.setContentLength(Long.parseLong(value));
                } catch (Exception e) {
                    responseData.setContentLength(-1);
                }
            } else {
                responseData.setContentLength(-1);
            }
            Header lastModifiedHeader = getMethod
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
        } catch (Exception e) {
            throw new RobotSystemException("Failed to access " + url, e);
        } finally {
            getMethod.releaseConnection();
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
