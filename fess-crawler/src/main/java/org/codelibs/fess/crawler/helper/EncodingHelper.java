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

    protected String defaultEncoding;

    protected Map<String, String> encodingMap = new HashMap<>();

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

    public void setDefaultEncoding(final String defaultEncoding) {
        if (StringUtil.isBlank(defaultEncoding)) {
            throw new IllegalArgumentException("Default encoding must not be blank.");
        }
        this.defaultEncoding = defaultEncoding;
    }

    public void addEncodingMapping(final String source, final String target) {
        if (StringUtil.isBlank(source) || StringUtil.isBlank(target)) {
            throw new IllegalArgumentException("Source and target encodings must not be blank.");
        }
        encodingMap.put(toLowerCase(source), target);
    }

    protected String toLowerCase(final String enc) {
        return enc.toLowerCase(Locale.ROOT);
    }
}
