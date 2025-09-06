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
package org.codelibs.fess.crawler.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tika.metadata.ClimateForcast;
import org.apache.tika.metadata.CreativeCommons;
import org.apache.tika.metadata.Geographic;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Message;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.TikaMimeKeys;

/**
 * Represents extracted data from a crawled resource, including content and metadata.
 */
public class ExtractData
        implements TikaCoreProperties, CreativeCommons, Geographic, HttpHeaders, Message, ClimateForcast, TIFF, TikaMimeKeys, Serializable {

    private static final long serialVersionUID = 1L;

    /** Resource name key for metadata */
    public static final String RESOURCE_NAME_KEY = "resourceName";

    /** URL key for metadata */
    public static final String URL = "url";

    /** File passwords key for metadata */
    public static final String FILE_PASSWORDS = "file.passwords";

    /** Map containing metadata key-value pairs */
    protected Map<String, String[]> metadata = new HashMap<>();

    /** The extracted content text */
    protected String content;

    /**
     * Constructs a new ExtractData.
     */
    public ExtractData() {
        // Default constructor
    }

    /**
     * Constructs a new ExtractData with the specified content.
     *
     * @param content the content to set
     */
    public ExtractData(final String content) {
        this.content = content;
    }

    /**
     * Puts multiple values for a given key in the metadata.
     *
     * @param key the metadata key
     * @param values the values to associate with the key
     */
    public void putValues(final String key, final String[] values) {
        metadata.put(key, values);
    }

    /**
     * Puts a single value for a given key in the metadata.
     *
     * @param key the metadata key
     * @param value the value to associate with the key
     */
    public void putValue(final String key, final String value) {
        metadata.put(key, new String[] { value });
    }

    /**
     * Gets the values associated with a given key from the metadata.
     *
     * @param key the metadata key
     * @return the values associated with the key, or null if not found
     */
    public String[] getValues(final String key) {
        return metadata.get(key);
    }

    /**
     * Gets the set of all metadata keys.
     *
     * @return the set of metadata keys
     */
    public Set<String> getKeySet() {
        return metadata.keySet();
    }

    /**
     * Gets the extracted content.
     *
     * @return the extracted content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the extracted content.
     *
     * @param content the content to set
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * Returns a string representation of this object.
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "ExtractData [metadata=" + metadata + ", content=" + content + "]";
    }
}
