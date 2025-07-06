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
package org.codelibs.fess.crawler.extractor;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.UnsupportedExtractException;

import jakarta.annotation.Resource;

/**
 * Factory class for managing and retrieving {@link Extractor} instances.
 * This class provides methods to add, retrieve, and manage extractors based on a key.
 * It also includes a builder for creating extractors.
 *
 * <p>
 * The factory maintains a map of keys to an array of {@link Extractor} objects.
 * When multiple extractors are associated with a single key, they are sorted by weight
 * in descending order. The {@link #getExtractor(String)} method returns a composite
 * extractor that iterates through the available extractors until one successfully
 * extracts the data.
 * </p>
 *
 * <p>
 * The class uses a {@link CrawlerContainer} for managing crawler components and
 * supports dependency injection via the {@link Resource} annotation.
 * </p>
 */
public class ExtractorFactory {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(ExtractorFactory.class);

    /** Container for managing crawler components */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /** Map of keys to arrays of extractors */
    protected Map<String, Extractor[]> extractorMap = new HashMap<>();

    /**
     * Constructs a new ExtractorFactory.
     */
    public ExtractorFactory() {
        // Default constructor
    }

    /**
     * Adds an extractor to the factory for the specified key.
     * If an extractor already exists for the key, the new extractor is added to the array of extractors,
     * and the array is sorted by weight in descending order.
     * If no extractor exists for the key, a new array containing the extractor is created and associated with the key.
     *
     * @param key       The key associated with the extractor. Must not be null or blank.
     * @param extractor The extractor to add. Must not be null.
     */
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

    /**
     * Adds an extractor to the factory for all keys in the provided list.
     *
     * @param keyList the list of keys to associate with the extractor
     * @param extractor the extractor to add
     * @throws CrawlerSystemException if the key list is null or empty, or if the extractor is null
     */
    public void addExtractor(final List<String> keyList, final Extractor extractor) {
        if (keyList == null || keyList.isEmpty()) {
            throw new CrawlerSystemException("The key list is empty.");
        }
        keyList.stream().distinct().forEach(key -> addExtractor(key, extractor));
    }

    /**
     * Retrieves an extractor for the specified key.
     * If multiple extractors are associated with the key, returns a composite extractor
     * that tries each extractor in order until one succeeds.
     *
     * @param key the key to look up
     * @return the extractor for the key, or null if not found
     */
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

    /**
     * Retrieves all extractors for the specified key.
     * @param key The key associated with the extractors.
     * @return An array of Extractor instances, or an empty array if no extractors are found.
     */
    public Extractor[] getExtractors(final String key) {
        final Extractor[] extractors = extractorMap.get(key);
        if (extractors == null || extractors.length == 0) {
            return new Extractor[0];
        }
        return extractors;
    }

    /**
     * Sets the extractor map with the provided map.
     *
     * @param extractorMap a map of keys to arrays of {@link Extractor} objects
     */
    public void setExtractorMap(final Map<String, Extractor[]> extractorMap) {
        this.extractorMap = extractorMap;
    }

    /**
     * Creates a new ExtractorBuilder instance.
     *
     * @param in      The input stream to be processed by the extractor.
     * @param params  The parameters to be used by the extractor.
     * @return A new ExtractorBuilder instance.
     */
    public ExtractorBuilder builder(final InputStream in, final Map<String, String> params) {
        return new ExtractorBuilder(crawlerContainer, in, params);
    }
}
