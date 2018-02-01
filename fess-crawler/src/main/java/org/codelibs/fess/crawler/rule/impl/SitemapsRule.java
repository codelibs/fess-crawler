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
package org.codelibs.fess.crawler.rule.impl;

import java.io.InputStream;

import javax.annotation.Resource;

import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.SitemapsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class SitemapsRule extends RegexRule {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory
            .getLogger(SitemapsRule.class);

    @Resource
    protected CrawlerContainer crawlerContainer;

    @Override
    public boolean match(final ResponseData responseData) {
        if (super.match(responseData)) {
            try (final InputStream is = responseData.getResponseBody()) {
                final SitemapsHelper sitemapsHelper = crawlerContainer.getComponent("sitemapsHelper");
                return sitemapsHelper.isValid(is);
            } catch (final CrawlingAccessException e) {
                throw e;
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed a sitemap check: " + responseData, e);
                }
            }
        }

        return false;
    }
}
