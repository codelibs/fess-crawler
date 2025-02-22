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
package org.codelibs.fess.crawler.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.codelibs.fess.crawler.exception.CrawlerSystemException;

/**
 * Helper class for converting URLs based on a set of predefined rules.
 *
 * <p>This class provides functionality to convert URLs by replacing parts of the URL
 * based on a map of target strings and their corresponding replacements. It allows
 * adding new conversion rules, setting the entire conversion map, and converting
 * URLs using these rules.</p>
 *
 * <p>The conversion is performed by iterating through the conversion map and applying
 * each replacement rule sequentially. The order of the rules in the map is preserved
 * during the conversion process.</p>
 *
 * <p>Example usage:</p>
 *
 * <pre>{@code
 * UrlConvertHelper helper = new UrlConvertHelper();
 * helper.add("old-domain.com", "new-domain.com");
 * String convertedUrl = helper.convert("http://old-domain.com/path");
 * // convertedUrl will be "http://new-domain.com/path"
 * }</pre>
 */
public class UrlConvertHelper {

    protected Map<String, String> convertMap = new LinkedHashMap<>();

    public String convert(final String url) {
        if (url == null) {
            return null;
        }
        String convertedUrl = url;
        for (final Map.Entry<String, String> entry : convertMap.entrySet()) {
            convertedUrl = convertedUrl.replaceAll(entry.getKey(), entry.getValue());
        }
        return convertedUrl;
    }

    public void add(final String target, final String replacement) {
        if (target == null || replacement == null) {
            throw new CrawlerSystemException("Target or replacement cannot be null.");
        }
        convertMap.put(target, replacement);
    }

    public void setConvertMap(final Map<String, String> convertMap) {
        if (convertMap == null) {
            throw new CrawlerSystemException("convertMap is null.");
        }
        this.convertMap = convertMap;
    }
}
