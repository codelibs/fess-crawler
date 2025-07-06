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
package org.codelibs.fess.crawler.extractor.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.misc.Pair;
import org.codelibs.fess.crawler.entity.ExtractData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PasswordBasedExtractor is an abstract base class for extractors that can handle password-protected files.
 * It provides functionality to manage passwords for different file patterns using regular expressions.
 *
 * <p>The extractor supports two types of password management:
 * <ul>
 *   <li>Static passwords configured via {@link #addPassword(String, String)}</li>
 *   <li>Dynamic passwords provided through extraction parameters</li>
 * </ul>
 *
 * <p>Passwords are matched against URLs or resource names using regular expression patterns.
 * The extractor first tries to match against the URL, then falls back to the resource name if available.
 *
 * @author shinsuke
 */
public abstract class PasswordBasedExtractor extends AbstractExtractor {

    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(PasswordBasedExtractor.class);

    /** Map of regex patterns to passwords for static password configuration. */
    protected Map<Pattern, String> passwordMap = new HashMap<>();

    /** Cache for parsed password configurations from extraction parameters. */
    private final Map<String, List<Pair<Pattern, String>>> configPasswordMap = new ConcurrentHashMap<>();

    /**
     * Creates a new PasswordBasedExtractor instance.
     */
    public PasswordBasedExtractor() {
        super();
    }

    /**
     * Adds a password for files matching the given regular expression pattern.
     * @param regex the regular expression pattern to match against URLs or resource names
     * @param password the password to use for matching files
     */
    public void addPassword(final String regex, final String password) {
        passwordMap.put(Pattern.compile(regex), password);
    }

    /**
     * Returns the password for the given parameters.
     * @param params The parameters.
     * @return The password.
     */
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

        return null;
    }

}
