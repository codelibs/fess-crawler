/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.pool2.ObjectPool;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.client.AbstractS2RobotClient;
import org.seasar.robot.client.http.action.UrlAction;
import org.seasar.robot.entity.RequestData;
import org.seasar.robot.entity.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class WebDriverClient extends AbstractS2RobotClient {

    private static final Logger logger = LoggerFactory
        .getLogger(WebDriverClient.class); // NOPMD

    protected ObjectPool<WebDriver> webDriverPool;

    protected Map<String, UrlAction> urlActionMap = new LinkedHashMap<>();

    public void addUrlProcessor(final UrlAction urlProcessor) {
        urlActionMap.put(urlProcessor.getName(), urlProcessor);
    }

    @Override
    public ResponseData execute(final RequestData request) {
        WebDriver webDriver = null;
        try {
            webDriver = webDriverPool.borrowObject();

            Map<String, String> paramMap = null;
            String url = request.getUrl();
            final int pos = url.indexOf(UrlAction.URL_SPLITTER);
            if (pos == 0) {
                throw new RobotSystemException("Invalid split position.");
            } else if (pos > 0) {
                final String[] params = url.split(UrlAction.URL_SPLITTER);
                if (params.length > 1) {
                    url = params[0];
                    paramMap = parseParamMap(params[1]);
                } else {
                    url = params[0];
                }
            }

            if (!url.equals(webDriver.getCurrentUrl())) {
                webDriver.get(url);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Base URL: " + url + ", Content: "
                    + webDriver.getPageSource());
            }

            if (paramMap != null) {
                final String processorName =
                    paramMap.get(UrlAction.URL_ACTION);
                final UrlAction urlProcessor =
                    urlActionMap.get(processorName);
                if (urlProcessor == null) {
                    throw new RobotSystemException("Unknown processor: "
                        + processorName);
                }
                urlProcessor.navigate(webDriver, paramMap);
            }

            final String source = webDriver.getPageSource();

            final ResponseData responseData = new ResponseData();

            responseData.setUrl(webDriver.getCurrentUrl());
            responseData.setMethod(request.getMethod().name());
            responseData.setContentLength(source.length());

            final String charSet = getCharSet(webDriver);
            responseData.setCharSet(charSet);
            responseData.setHttpStatusCode(getStatusCode(webDriver));
            responseData.setLastModified(getLastModified(webDriver));
            responseData.setMimeType(getContentType(webDriver));

            responseData.setResponseBody(new ByteArrayInputStream(source
                .getBytes(charSet)));

            for (final UrlAction urlProcessor : urlActionMap.values()) {
                urlProcessor.collect(url, webDriver, responseData);
            }

            return responseData;
        } catch (final Exception e) {
            throw new RobotSystemException("Failed to access "
                + request.getUrl(), e);
        } finally {
            try {
                webDriverPool.returnObject(webDriver);
            } catch (final Exception e) {
                logger.warn("Failed to return a returned object.", e);
            }
        }
    }

    protected Map<String, String> parseParamMap(final String paramStr) {
        final Map<String, String> paramMap = new HashMap<>();
        final String[] pairs = paramStr.split("&");
        for (final String pair : pairs) {
            final String[] values = pair.split("=");
            if (values.length > 1) {
                paramMap.put(values[0], values[1]);
            }
        }
        return paramMap;
    }

    /**
     * @param wd
     * @return
     */
    private String getContentType(final WebDriver wd) {
        if (wd instanceof JavascriptExecutor) {
            final JavascriptExecutor jsExecutor = (JavascriptExecutor) wd;
            // TODO document.contentType does not exist.
            final Object ret =
                jsExecutor.executeScript("return document.contentType;");
            if (ret != null) {
                return ret.toString();
            }
        }
        return "text/html";
    }

    /**
     * @param wd
     * @return
     */
    private Date getLastModified(final WebDriver wd) {
        if (wd instanceof JavascriptExecutor) {
            final JavascriptExecutor jsExecutor = (JavascriptExecutor) wd;
            final Object ret =
                jsExecutor
                    .executeScript("return new Date(document.lastModified).getTime();");
            if (ret != null) {
                try {
                    return new Date(Long.parseLong(ret.toString()));
                } catch (final NumberFormatException e) {
                    logger.warn("Invalid format: " + ret, e);
                }
            }
        }
        return new Date();
    }

    /**
     * @param wd
     * @return
     */
    private int getStatusCode(final WebDriver wd) {
        return Constants.OK_STATUS_CODE;
    }

    /**
     * @param wd
     * @return
     */
    private String getCharSet(final WebDriver wd) {
        if (wd instanceof JavascriptExecutor) {
            final JavascriptExecutor jsExecutor = (JavascriptExecutor) wd;
            final Object ret =
                jsExecutor.executeScript("return document.characterSet;");
            if (ret != null) {
                return ret.toString();
            }
        }
        return Constants.UTF_8;
    }

    public ObjectPool<WebDriver> getWebDriverPool() {
        return webDriverPool;
    }

    public void setWebDriverPool(final ObjectPool<WebDriver> webDriverPool) {
        this.webDriverPool = webDriverPool;
    }

}
