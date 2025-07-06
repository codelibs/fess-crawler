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
package org.codelibs.fess.crawler.helper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;

/**
 * EncodingHelper provides utility methods for managing and normalizing character encodings.
 * It allows setting a default encoding, mapping various encoding names to preferred ones,
 * and normalizing an encoding string to its preferred form or the default if no mapping is found.
 */
public class EncodingHelper {

    /** The default encoding to use when no mapping is found */
    protected String defaultEncoding;

    /** Map of encoding names to their preferred forms */
    protected Map<String, String> encodingMap = new HashMap<>();

    /**
     * Constructs a new EncodingHelper.
     */
    public EncodingHelper() {
        // Default constructor
    }

    /**
     * Normalizes an encoding string to its preferred form.
     *
     * @param enc the encoding string to normalize
     * @return the normalized encoding or the default encoding if the input is blank
     */
    public String normalize(final String enc) {
        if (StringUtil.isBlank(enc)) {
            return defaultEncoding;
        }

        final String newEnc = encodingMap.get(toLowerCase(enc));
        if (StringUtil.isBlank(newEnc)) {
            return enc;
        }
        return newEnc;
    }

    /**
     * Sets the default encoding to use when no mapping is found.
     *
     * @param defaultEncoding the default encoding to set
     * @throws IllegalArgumentException if the default encoding is blank
     */
    public void setDefaultEncoding(final String defaultEncoding) {
        if (StringUtil.isBlank(defaultEncoding)) {
            throw new IllegalArgumentException("Default encoding must not be blank.");
        }
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * Adds an encoding mapping from source to target encoding.
     *
     * @param source the source encoding name
     * @param target the target encoding name
     * @throws IllegalArgumentException if source or target is blank
     */
    public void addEncodingMapping(final String source, final String target) {
        if (StringUtil.isBlank(source) || StringUtil.isBlank(target)) {
            throw new IllegalArgumentException("Source and target encodings must not be blank.");
        }
        encodingMap.put(toLowerCase(source), target);
    }

    /**
     * Converts the given encoding string to lowercase.
     * @param enc The encoding string.
     * @return The lowercase encoding string.
     */
    protected String toLowerCase(final String enc) {
        return enc.toLowerCase(Locale.ROOT);
    }
}
