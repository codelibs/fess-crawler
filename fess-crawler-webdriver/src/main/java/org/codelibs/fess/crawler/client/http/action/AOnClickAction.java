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
package org.codelibs.fess.crawler.client.http.action;

import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * @author shinsuke
 *
 */
public class AOnClickAction extends BaseUrlAction {

    public String attrName = "onclick";

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.client.UrlProcessor#navigate(org.openqa.selenium.WebDriver
     * , java.util.Map)
     */
    @Override
    public void navigate(final WebDriver webDriver,
            final Map<String, String> paramMap) {
        final String cssQuery = paramMap.get(CSS_QUERY);
        final int index = Integer.parseInt(paramMap.get(INDEX));
        if (StringUtil.isNotBlank(cssQuery) && index >= 0) {
            final List<WebElement> elementList = webDriver.findElements(By
                    .cssSelector(cssQuery));
            if (index < elementList.size()) {
                elementList.get(index).click();
                return;
            }
        }
        throw new CrawlerSystemException("Invalid position. css query: "
                + cssQuery + ", index: " + index);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.client.UrlProcessor#collect(java.lang.String,
     * org.openqa.selenium.WebDriver, org.codelibs.fess.crawler.entity.ResponseData)
     */
    @Override
    public void collect(final String url, final WebDriver webDriver,
            final ResponseData responseData) {
        final List<WebElement> aElementList = webDriver.findElements(By
                .cssSelector(cssQuery));
        for (int i = 0; i < aElementList.size(); i++) {
            final WebElement aElement = aElementList.get(i);
            final String onclickAttr = aElement.getAttribute(attrName);
            if (StringUtil.isNotBlank(onclickAttr)) {
                final StringBuilder buf = new StringBuilder(url.length() + 30);
                buf.append(URL_ACTION).append("=").append(name).append("&")
                        .append(CSS_QUERY).append("=").append(cssQuery)
                        .append("&").append(INDEX).append("=").append(i);
                responseData.addChildUrl(RequestDataBuilder.newRequestData()
                        .get().url(url).metaData(buf.toString()).build());
            }
        }
    }

}
