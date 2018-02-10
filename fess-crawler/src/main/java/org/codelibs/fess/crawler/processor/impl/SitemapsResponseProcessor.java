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
package org.codelibs.fess.crawler.processor.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.Sitemap;
import org.codelibs.fess.crawler.entity.SitemapSet;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.helper.SitemapsHelper;
import org.codelibs.fess.crawler.processor.ResponseProcessor;

/**
 * @author shinsuke
 *
 */
public class SitemapsResponseProcessor implements ResponseProcessor {
    @Resource
    protected CrawlerContainer crawlerContainer;

    @Override
    public void process(final ResponseData responseData) {
        final SitemapsHelper sitemapsHelper = crawlerContainer.getComponent("sitemapsHelper");
        try (final InputStream responseBody = responseData.getResponseBody()) {
            final SitemapSet sitemapSet = sitemapsHelper.parse(responseBody);
            final Set<RequestData> requestDataSet = new LinkedHashSet<>();
            for (final Sitemap sitemap : sitemapSet.getSitemaps()) {
                if (sitemap != null) {
                    requestDataSet.add(RequestDataBuilder.newRequestData().get().url(sitemap.getLoc()).build());
                }
            }
            throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#process");
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

}
