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
package org.codelibs.fess.crawler.client.http;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

public class RequestHeaderTest extends PlainTestCase {

    @Test
    public void test_constructor() {
        RequestHeader header = new RequestHeader("Content-Type", "application/json");
        assertEquals("Content-Type", header.getName());
        assertEquals("application/json", header.getValue());
    }

    @Test
    public void test_isValid_validHeader() {
        RequestHeader header = new RequestHeader("Content-Type", "application/json");
        assertTrue(header.isValid());
    }

    @Test
    public void test_isValid_blankName() {
        // empty string
        RequestHeader header1 = new RequestHeader("", "application/json");
        assertFalse(header1.isValid());

        // whitespace only
        RequestHeader header2 = new RequestHeader("   ", "application/json");
        assertFalse(header2.isValid());
    }

    @Test
    public void test_isValid_nullName() {
        RequestHeader header = new RequestHeader(null, "application/json");
        assertFalse(header.isValid());
    }

    @Test
    public void test_isValid_nullValue() {
        RequestHeader header = new RequestHeader("Content-Type", null);
        assertFalse(header.isValid());
    }

    @Test
    public void test_isValid_emptyValue() {
        // empty string value is valid (not null)
        RequestHeader header = new RequestHeader("X-Empty-Header", "");
        assertTrue(header.isValid());
    }

    @Test
    public void test_setters() {
        RequestHeader header = new RequestHeader("Original-Name", "original-value");

        header.setName("New-Name");
        assertEquals("New-Name", header.getName());

        header.setValue("new-value");
        assertEquals("new-value", header.getValue());
    }
}
