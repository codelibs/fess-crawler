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
package org.codelibs.fess.crawler.client.http.form;

import java.util.Collections;

import org.dbflute.utflute.core.PlainTestCase;

public class FormSchemeTest extends PlainTestCase {

    public void test_getTokenValue() {
        FormScheme formScheme = new FormScheme(Collections.emptyMap());

        String tokenPattern = "name=\"authenticity_token\" +value=\"([^\"]+)\"";
        String content = "<input name=\"authenticity_token\" value=\"abcdefg\">";
        assertEquals("abcdefg", formScheme.getTokenValue(tokenPattern, content));

        tokenPattern = "name=\"authenticity_token\" +value=\"[^\"]+\"";
        content = "<input name=\"authenticity_token\" value=\"abcdefg\">";
        assertNull(formScheme.getTokenValue(tokenPattern, content));

        tokenPattern = "name=\"authenticity_token\" +value=\"([^\"]+)\"";
        content = "<input name=\"authenticity_token\" hoge value=\"abcdefg\">";
        assertNull(formScheme.getTokenValue(tokenPattern, content));
    }
}
