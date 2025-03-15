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
package org.codelibs.fess.crawler.rule.impl;

import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.SitemapsHelper;

/**
 * SitemapsRule is a rule implementation that extends RegexRule to determine if a given response data
 * represents a valid sitemap. It uses a SitemapsHelper to validate the response body as an InputStream.
 * The rule checks if the URL matches the defined regex pattern and then validates the content as a sitemap.
 * If any exception occurs during the sitemap validation, it logs the error and returns false.
 *
 */
public class SitemapsRule extends RegexRule {
    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    private static final Logger logger = LogManager.getLogger(SitemapsRule.class);

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
                    logger.debug("Failed a sitemap check: {}", responseData, e);
                }
            }
        }

        return false;
    }
}
