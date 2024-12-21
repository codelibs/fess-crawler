/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.extractor;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.UnsupportedExtractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;

/**
 * @author shinsuke
 *
 */
public class ExtractorFactory {

    private static final Logger logger = LoggerFactory.getLogger(ExtractorFactory.class);

    @Resource
    protected CrawlerContainer crawlerContainer;

    protected Map<String, Extractor[]> extractorMap = new HashMap<>();

    public void addExtractor(final String key, final Extractor extractor) {
        if (StringUtil.isBlank(key)) {
            throw new CrawlerSystemException("The key is null.");
        }
        if (extractor == null) {
            throw new CrawlerSystemException("The extractor is null.");
        }
        if (extractorMap.containsKey(key)) {
            final Extractor[] existingExtractors = extractorMap.get(key);

            final Extractor[] newExtractors = new Extractor[existingExtractors.length + 1];
            System.arraycopy(existingExtractors, 0, newExtractors, 0, existingExtractors.length);
            newExtractors[newExtractors.length - 1] = extractor;

            Arrays.sort(newExtractors, Comparator.comparingInt(Extractor::getWeight).reversed());

            extractorMap.put(key, newExtractors);
        } else {
            extractorMap.put(key, new Extractor[] { extractor });
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Loaded {} : {}", key, extractor.getClass().getName());
        }
    }

    public void addExtractor(final List<String> keyList, final Extractor extractor) {
        if (keyList == null || keyList.isEmpty()) {
            throw new CrawlerSystemException("The key list is empty.");
        }
        for (final String key : keyList) {
            addExtractor(key, extractor);
        }
    }

    public Extractor getExtractor(final String key) {
        final Extractor[] extractors = extractorMap.get(key);
        if (extractors == null || extractors.length == 0) {
            return null;
        }
        if (extractors.length == 1) {
            return extractors[0];
        }
        return new Extractor() {
            @Override
            public ExtractData getText(final InputStream in, final Map<String, String> params) {
                for (final Extractor extractor : extractors) {
                    try {
                        return extractor.getText(in, params);
                    } catch (final UnsupportedExtractException e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("{} does not support this data. {}", extractor.getClass().getName(), e.getMessage());
                        }
                    }
                }
                throw new ExtractException("Failed to extract the content using available extractors.");
            }

            @Override
            public int getWeight() {
                return extractors[0].getWeight();
            }
        };
    }

    public Extractor[] getExtractors(final String key) {
        final Extractor[] extractors = extractorMap.get(key);
        if (extractors == null || extractors.length == 0) {
            return new Extractor[0];
        }
        return extractors;
    }

    public void setExtractorMap(final Map<String, Extractor[]> extractorMap) {
        this.extractorMap = extractorMap;
    }

    public ExtractorBuilder builder(final InputStream in, final Map<String, String> params) {
        return new ExtractorBuilder(crawlerContainer, in, params);
    }
}
