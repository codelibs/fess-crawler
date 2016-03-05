/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;

/**
 * @author shinsuke
 *
 */
public class ExtractorFactory {
    protected Map<String, Extractor> extractorMap = new HashMap<String, Extractor>();

    public void addExtractor(final String key, final Extractor extractor) {
        if (StringUtil.isBlank(key)) {
            throw new CrawlerSystemException("The key is null.");
        }
        if (extractor == null) {
            throw new CrawlerSystemException("The extractor is null.");
        }
        extractorMap.put(key, extractor);
    }

    public void addExtractor(final List<String> keyList,
            final Extractor extractor) {
        if (keyList == null || keyList.isEmpty()) {
            throw new CrawlerSystemException("The key list is empty.");
        }
        for (final String key : keyList) {
            addExtractor(key, extractor);
        }
    }

    public Extractor getExtractor(final String key) {
        return extractorMap.get(key);
    }

    public void setExtractorMap(final Map<String, Extractor> extractorMap) {
        this.extractorMap = extractorMap;
    }
}
