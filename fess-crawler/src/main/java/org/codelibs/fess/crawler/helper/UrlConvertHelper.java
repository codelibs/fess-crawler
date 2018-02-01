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
package org.codelibs.fess.crawler.helper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.codelibs.fess.crawler.exception.CrawlerSystemException;

/**
 * @author shinsuke
 *
 */
public class UrlConvertHelper {

    protected Map<String, String> convertMap = new LinkedHashMap<>();

    public String convert(final String url) {
        if (url == null) {
            return null;
        }
        String convertedUrl = url;
        for (final Map.Entry<String, String> entry : convertMap.entrySet()) {
            convertedUrl = convertedUrl.replaceAll(entry.getKey(),
                    entry.getValue());
        }
        return convertedUrl;
    }

    public void add(final String target, final String replacement) {
        if (target == null || replacement == null) {
            throw new CrawlerSystemException("target or replacement are null.");
        }
        convertMap.put(target, replacement);
    }

    public void setConvertMap(final Map<String, String> convertMap) {
        this.convertMap = convertMap;
    }
}
