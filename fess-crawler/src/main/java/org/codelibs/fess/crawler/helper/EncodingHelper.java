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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.codelibs.core.lang.StringUtil;

/**
 * @author shinsuke
 *
 */
public class EncodingHelper {

    protected String defaultEncoding = null;

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
        this.defaultEncoding = defaultEncoding;
    }

    public void addEncodingMapping(final String source, final String target) {
        encodingMap.put(toLowerCase(source), target);
    }

    protected String toLowerCase(final String enc) {
        return enc.toLowerCase(Locale.ROOT);
    }
}
