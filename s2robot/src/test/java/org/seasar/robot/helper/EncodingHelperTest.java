/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.helper;

import org.seasar.extension.unit.S2TestCase;

/**
 * @author shinsuke
 *
 */
public class EncodingHelperTest extends S2TestCase {
    public EncodingHelper encodingHelper;

    @Override
    protected String getRootDicon() throws Throwable {
        return "s2robot_encoding.dicon";
    }

    public void test_normalize() {
        String enc;

        enc = "UTF-8";
        assertEquals("UTF-8", encodingHelper.normalize(enc));

        enc = "Shift_JIS";
        assertEquals("Shift_JIS", encodingHelper.normalize(enc));

        enc = "S-JIS";
        assertEquals("S-JIS", encodingHelper.normalize(enc));
    }

    public void test_normalize_map() {
        String enc;

        encodingHelper.encodingMap.put("S-JIS", "Shift_JIS");

        enc = "UTF-8";
        assertEquals("UTF-8", encodingHelper.normalize(enc));

        enc = "Shift_JIS";
        assertEquals("Shift_JIS", encodingHelper.normalize(enc));

        enc = "S-JIS";
        assertEquals("Shift_JIS", encodingHelper.normalize(enc));
    }

    public void test_normalize_null() {
        String enc;

        enc = null;
        assertNull(encodingHelper.normalize(enc));

        enc = "";
        assertNull(encodingHelper.normalize(enc));

        enc = " ";
        assertNull(encodingHelper.normalize(enc));
    }

    public void test_normalize_default() {
        String enc;

        encodingHelper.defaultEncoding = "UTF-8";

        enc = null;
        assertEquals("UTF-8", encodingHelper.normalize(enc));

        enc = "";
        assertEquals("UTF-8", encodingHelper.normalize(enc));

        enc = " ";
        assertEquals("UTF-8", encodingHelper.normalize(enc));

        enc = "UTF-8";
        assertEquals("UTF-8", encodingHelper.normalize(enc));

        enc = "Shift_JIS";
        assertEquals("Shift_JIS", encodingHelper.normalize(enc));

        enc = "S-JIS";
        assertEquals("S-JIS", encodingHelper.normalize(enc));
    }
}
