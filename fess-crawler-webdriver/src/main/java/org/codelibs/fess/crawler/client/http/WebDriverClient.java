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

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.pool2.ObjectPool;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.client.AbstractCrawlerClient;
import org.codelibs.fess.crawler.client.http.action.UrlAction;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class WebDriverClient extends AbstractCrawlerClient {

    private static final Logger logger = LoggerFactory
            .getLogger(WebDriverClient.class);

    @Resource
    protected ObjectPool<WebDriver> webDriverPool;

    protected Map<String, UrlAction> urlActionMap = new LinkedHashMap<>();

    public void addUrlAction(final UrlAction urlAction) {
        urlActionMap.put(urlAction.getName(), urlAction);
    }

    @Override
    public ResponseData execute(final RequestData request) {
        WebDriver webDriver = null;
        try {
            webDriver = webDriverPool.borrowObject();

            Map<String, String> paramMap = null;
            final String url = request.getUrl();
            final String metaData = request.getMetaData();
            if (StringUtil.isNotBlank(metaData)) {
                paramMap = parseParamMap(metaData);
            }

            if (!url.equals(webDriver.getCurrentUrl())) {
                webDriver.get(url);
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Base URL: " + url + "\nContent: "
                        + webDriver.getPageSource());
            }

            if (paramMap != null) {
                final String processorName = paramMap.get(UrlAction.URL_ACTION);
                final UrlAction urlAction = urlActionMap.get(processorName);
                if (urlAction == null) {
                    throw new CrawlerSystemException("Unknown processor: "
                            + processorName);
                }
                urlAction.navigate(webDriver, paramMap);
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

            responseData.setResponseBody(source.getBytes(charSet));

            for (final UrlAction urlAction : urlActionMap.values()) {
                urlAction.collect(url, webDriver, responseData);
            }

            return responseData;
        } catch (final Exception e) {
            throw new CrawlerSystemException("Failed to access "
                    + request.getUrl(), e);
        } finally {
            if (webDriver != null) {
                try {
                    webDriverPool.returnObject(webDriver);
                } catch (final Exception e) {
                    logger.warn("Failed to return a returned object.", e);
                }
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
            final Object ret = jsExecutor
                    .executeScript("return document.contentType;");
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
            final Object ret = jsExecutor
                    .executeScript("return new Date(document.lastModified).getTime();");
            if (ret != null) {
                try {
                    return new Date(Long.parseLong(ret.toString()));
                } catch (final NumberFormatException e) {
                    logger.warn("Invalid format: " + ret, e);
                }
            }
        }
        return null;
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
            final Object ret = jsExecutor
                    .executeScript("return document.characterSet;");
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
