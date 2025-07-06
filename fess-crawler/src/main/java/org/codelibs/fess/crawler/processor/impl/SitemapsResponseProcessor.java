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
package org.codelibs.fess.crawler.processor.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

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

import jakarta.annotation.Resource;

/**
 * A response processor implementation that handles sitemaps.
 * It parses the response body as a SitemapSet, extracts URLs from the sitemaps,
 * and adds them as child URLs to be crawled.
 *
 * <p>
 * This class uses a {@link SitemapsHelper} to parse the sitemap XML or text.
 * It then iterates through the sitemaps in the SitemapSet, extracts the URL
 * from each sitemap, and creates a new {@link RequestData} object for each URL.
 * These RequestData objects are added to a set of child URLs, which are then
 * passed to a {@link ChildUrlsException} to be processed by the crawler.
 * </p>
 *
 * <p>
 * The class also handles potential {@link IOException}s that may occur during
 * the parsing of the response body.
 * </p>
 */
public class SitemapsResponseProcessor implements ResponseProcessor {
    /** The crawler container for component lookup. */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /**
     * Creates a new SitemapsResponseProcessor instance.
     */
    public SitemapsResponseProcessor() {
        super();
    }

    /**
     * Processes the given response data, extracting URLs from sitemaps.
     * @param responseData The response data.
     */
    @Override
    public void process(final ResponseData responseData) {
        final SitemapsHelper sitemapsHelper = crawlerContainer.getComponent("sitemapsHelper");
        try (final InputStream responseBody = responseData.getResponseBody()) {
            final SitemapSet sitemapSet = sitemapsHelper.parse(responseBody);
            final Set<RequestData> requestDataSet = new LinkedHashSet<>();
            for (final Sitemap sitemap : sitemapSet.getSitemaps()) {
                if (sitemap != null) {
                    requestDataSet.add(RequestDataBuilder.newRequestData().get().url(sitemap.getLoc()).build()); // TODO priority
                }
            }
            throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#process");
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

}
