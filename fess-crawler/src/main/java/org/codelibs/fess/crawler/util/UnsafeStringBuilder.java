/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.util;

import org.apache.commons.lang3.text.StrBuilder;
import org.codelibs.core.lang.StringUtil;

public class UnsafeStringBuilder extends StrBuilder {

    private static final long serialVersionUID = 1L;

    public UnsafeStringBuilder() {
        super();
    }

    public UnsafeStringBuilder(int initialCapacity) {
        super(initialCapacity);
    }

    public UnsafeStringBuilder(String str) {
        super(str);
    }

    public String toUnsafeString() {
        for (int i = size; i < buffer.length; i++) {
            buffer[i] = ' ';
        }
        return StringUtil.newStringUnsafe(buffer);
    }

    public StrBuilder appendCodePoint(int codePoint) {
        if (Character.isBmpCodePoint(codePoint)) {
            append((char) codePoint);
        } else if (Character.isValidCodePoint(codePoint)) {
            append(Character.highSurrogate(codePoint));
            append(Character.lowSurrogate(codePoint));
        } else {
            throw new IllegalArgumentException();
        }
        return this;
    }
}
