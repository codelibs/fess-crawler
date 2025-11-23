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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Extracts text content and metadata from JSON files.
 * This extractor provides better structured data extraction compared to Tika's generic text extraction.
 *
 * <p>Features:
 * <ul>
 *   <li>Structured text extraction with key-value pairs</li>
 *   <li>Top-level field extraction as metadata</li>
 *   <li>Nested structure flattening with configurable depth</li>
 *   <li>Array element extraction</li>
 *   <li>Configurable field separator and array formatting</li>
 * </ul>
 */
public class JsonExtractor extends AbstractExtractor {
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(JsonExtractor.class);

    /** Jackson ObjectMapper for JSON parsing. */
    protected final ObjectMapper objectMapper = new ObjectMapper();

    /** Maximum depth for nested structure extraction. */
    protected int maxDepth = 10;

    /** Separator for key-value pairs in extracted text. */
    protected String fieldSeparator = ": ";

    /** Separator between different fields in extracted text. */
    protected String lineSeparator = "\n";

    /** Whether to extract top-level fields as metadata. */
    protected boolean extractMetadata = true;

    /** Prefix for flattened nested keys. */
    protected String nestedKeySeparator = ".";

    /** Maximum number of array elements to extract. */
    protected int maxArrayElements = 100;

    /**
     * Constructs a new JsonExtractor.
     */
    public JsonExtractor() {
        super();
    }

    @Override
    public int getWeight() {
        return 2; // Higher priority than TikaExtractor (weight=1)
    }

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);

        try {
            final JsonNode rootNode = objectMapper.readTree(in);
            final StringBuilder textBuilder = new StringBuilder();
            final Map<String, List<String>> metadataMap = new LinkedHashMap<>();

            extractContent(rootNode, "", textBuilder, metadataMap, 0);

            final ExtractData extractData = new ExtractData(textBuilder.toString().trim());

            if (extractMetadata) {
                for (final Map.Entry<String, List<String>> entry : metadataMap.entrySet()) {
                    final List<String> values = entry.getValue();
                    extractData.putValues(entry.getKey(), values.toArray(new String[0]));
                }
            }

            return extractData;
        } catch (final IOException e) {
            throw new ExtractException("Failed to parse JSON content", e);
        }
    }

    /**
     * Recursively extracts content from JSON nodes.
     *
     * @param node the JSON node to extract from
     * @param parentKey the parent key path
     * @param textBuilder the string builder for text content
     * @param metadataMap the map for metadata extraction
     * @param depth the current depth in the JSON structure
     */
    protected void extractContent(final JsonNode node, final String parentKey, final StringBuilder textBuilder,
            final Map<String, List<String>> metadataMap, final int depth) {
        if (node == null || depth > maxDepth) {
            return;
        }

        if (node.isObject()) {
            extractObject((ObjectNode) node, parentKey, textBuilder, metadataMap, depth);
        } else if (node.isArray()) {
            extractArray((ArrayNode) node, parentKey, textBuilder, metadataMap, depth);
        } else {
            extractValue(node, parentKey, textBuilder, metadataMap, depth);
        }
    }

    /**
     * Extracts content from a JSON object node.
     *
     * @param node the object node
     * @param parentKey the parent key path
     * @param textBuilder the string builder for text content
     * @param metadataMap the map for metadata extraction
     * @param depth the current depth
     */
    protected void extractObject(final ObjectNode node, final String parentKey, final StringBuilder textBuilder,
            final Map<String, List<String>> metadataMap, final int depth) {
        final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> field = fields.next();
            final String fieldName = field.getKey();
            final JsonNode fieldValue = field.getValue();

            final String currentKey = buildKey(parentKey, fieldName);
            extractContent(fieldValue, currentKey, textBuilder, metadataMap, depth + 1);
        }
    }

    /**
     * Extracts content from a JSON array node.
     *
     * @param node the array node
     * @param parentKey the parent key path
     * @param textBuilder the string builder for text content
     * @param metadataMap the map for metadata extraction
     * @param depth the current depth
     */
    protected void extractArray(final ArrayNode node, final String parentKey, final StringBuilder textBuilder,
            final Map<String, List<String>> metadataMap, final int depth) {
        final int size = Math.min(node.size(), maxArrayElements);
        for (int i = 0; i < size; i++) {
            final JsonNode element = node.get(i);
            final String currentKey = parentKey + "[" + i + "]";
            extractContent(element, currentKey, textBuilder, metadataMap, depth + 1);
        }

        if (node.size() > maxArrayElements) {
            if (logger.isDebugEnabled()) {
                logger.debug("Array at {} has {} elements, only first {} extracted", parentKey, node.size(), maxArrayElements);
            }
        }
    }

    /**
     * Extracts a primitive value from a JSON node.
     *
     * @param node the value node
     * @param key the key for this value
     * @param textBuilder the string builder for text content
     * @param metadataMap the map for metadata extraction
     * @param depth the current depth
     */
    protected void extractValue(final JsonNode node, final String key, final StringBuilder textBuilder,
            final Map<String, List<String>> metadataMap, final int depth) {
        if (node.isNull()) {
            return;
        }

        final String value = node.asText();
        if (StringUtil.isBlank(value)) {
            return;
        }

        // Add to text content
        if (StringUtil.isNotBlank(key)) {
            textBuilder.append(key).append(fieldSeparator).append(value).append(lineSeparator);
        } else {
            textBuilder.append(value).append(lineSeparator);
        }

        // Add to metadata if at top level
        if (depth <= 1 && StringUtil.isNotBlank(key)) {
            metadataMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }
    }

    /**
     * Builds a nested key path.
     *
     * @param parentKey the parent key
     * @param currentKey the current key
     * @return the combined key path
     */
    protected String buildKey(final String parentKey, final String currentKey) {
        if (StringUtil.isBlank(parentKey)) {
            return currentKey;
        }
        return parentKey + nestedKeySeparator + currentKey;
    }

    /**
     * Sets the maximum depth for nested structure extraction.
     *
     * @param maxDepth the maximum depth
     */
    public void setMaxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Sets the field separator for key-value pairs.
     *
     * @param fieldSeparator the field separator
     */
    public void setFieldSeparator(final String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    /**
     * Sets the line separator between fields.
     *
     * @param lineSeparator the line separator
     */
    public void setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    /**
     * Sets whether to extract top-level fields as metadata.
     *
     * @param extractMetadata true to extract metadata
     */
    public void setExtractMetadata(final boolean extractMetadata) {
        this.extractMetadata = extractMetadata;
    }

    /**
     * Sets the separator for nested keys.
     *
     * @param nestedKeySeparator the nested key separator
     */
    public void setNestedKeySeparator(final String nestedKeySeparator) {
        this.nestedKeySeparator = nestedKeySeparator;
    }

    /**
     * Sets the maximum number of array elements to extract.
     *
     * @param maxArrayElements the maximum array elements
     */
    public void setMaxArrayElements(final int maxArrayElements) {
        this.maxArrayElements = maxArrayElements;
    }
}
