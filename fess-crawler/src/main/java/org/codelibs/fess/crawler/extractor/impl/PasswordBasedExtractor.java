/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.extractor.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class PasswordBasedExtractor extends AbstractExtractor {

    private static final Logger logger = LoggerFactory.getLogger(PasswordBasedExtractor.class);

    protected Map<Pattern, String> passwordMap = new HashMap<>();

    private final Map<String, List<Pair<Pattern, String>>> configPasswordMap = new ConcurrentHashMap<>();

    public void addPassword(final String regex, final String password) {
        passwordMap.put(Pattern.compile(regex), password);
    }

    protected String getPassword(final Map<String, String> params) {
        final String url = params != null ? params.get(ExtractData.URL) : null;
        if (!passwordMap.isEmpty()) {
            final String resourceName = params != null ? params.get(ExtractData.RESOURCE_NAME_KEY) : null;

            String value = null;
            if (StringUtil.isNotEmpty(url)) {
                value = url;
            } else if (StringUtil.isNotEmpty(resourceName)) {
                value = resourceName;
            }

            if (value != null) {
                for (final Map.Entry<Pattern, String> entry : passwordMap.entrySet()) {
                    if (entry.getKey().matcher(value).matches()) {
                        return entry.getValue();
                    }
                }
            }
        }

        if (params != null && url != null) {
            final String value = params.get(ExtractData.FILE_PASSWORDS);
            if (StringUtil.isNotBlank(value)) {
                List<Pair<Pattern, String>> list = configPasswordMap.get(value);
                if (list == null) {
                    try {
                        final ObjectMapper mapper = new ObjectMapper();
                        final Map<String, String> passwordMap = mapper.readValue(value, new TypeReference<Map<String, String>>() {
                        });
                        list = passwordMap.entrySet().stream().map(e -> new Pair<>(Pattern.compile(e.getKey()), e.getValue()))
                                .collect(Collectors.toList());
                    } catch (final Exception e) {
                        logger.warn("Failed to parse passwords for " + url, e);
                        list = Collections.emptyList();
                    }
                    configPasswordMap.put(value, list);
                }
                for (final Pair<Pattern, String> pair : list) {
                    if (pair.getFirst().matcher(url).matches()) {
                        return pair.getSecond();
                    }
                }
            }
        }

        return params != null ? params.get(ExtractData.PDF_PASSWORD) : null;
    }

}
