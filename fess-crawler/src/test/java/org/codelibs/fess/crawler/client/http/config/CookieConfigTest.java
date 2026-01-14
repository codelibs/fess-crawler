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
package org.codelibs.fess.crawler.client.http.config;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for CookieConfig.
 * Tests all getters, setters, and toString functionality.
 */
public class CookieConfigTest extends PlainTestCase {

    /**
     * Test default values on new instance
     */
    @Test
    public void test_defaultValues() {
        CookieConfig config = new CookieConfig();

        assertNull(config.getName());
        assertNull(config.getValue());
        assertNull(config.getDomain());
        assertNull(config.getPath());
        assertNull(config.getExpiryDate());
        assertFalse(config.isSecure());
        assertFalse(config.isHttpOnly());
    }

    /**
     * Test name getter and setter
     */
    @Test
    public void test_name() {
        CookieConfig config = new CookieConfig();

        config.setName("sessionId");
        assertEquals("sessionId", config.getName());

        config.setName("JSESSIONID");
        assertEquals("JSESSIONID", config.getName());

        config.setName(null);
        assertNull(config.getName());

        config.setName("");
        assertEquals("", config.getName());
    }

    /**
     * Test value getter and setter
     */
    @Test
    public void test_value() {
        CookieConfig config = new CookieConfig();

        config.setValue("abc123");
        assertEquals("abc123", config.getValue());

        config.setValue("value with spaces and special chars !@#$%");
        assertEquals("value with spaces and special chars !@#$%", config.getValue());

        config.setValue(null);
        assertNull(config.getValue());

        config.setValue("");
        assertEquals("", config.getValue());
    }

    /**
     * Test domain getter and setter
     */
    @Test
    public void test_domain() {
        CookieConfig config = new CookieConfig();

        config.setDomain("example.com");
        assertEquals("example.com", config.getDomain());

        config.setDomain(".example.com");
        assertEquals(".example.com", config.getDomain());

        config.setDomain("subdomain.example.com");
        assertEquals("subdomain.example.com", config.getDomain());

        config.setDomain(null);
        assertNull(config.getDomain());
    }

    /**
     * Test path getter and setter
     */
    @Test
    public void test_path() {
        CookieConfig config = new CookieConfig();

        config.setPath("/");
        assertEquals("/", config.getPath());

        config.setPath("/app/context");
        assertEquals("/app/context", config.getPath());

        config.setPath("/path/with/many/segments");
        assertEquals("/path/with/many/segments", config.getPath());

        config.setPath(null);
        assertNull(config.getPath());
    }

    /**
     * Test expiryDate getter and setter
     */
    @Test
    public void test_expiryDate() {
        CookieConfig config = new CookieConfig();

        Date now = new Date();
        config.setExpiryDate(now);
        assertEquals(now, config.getExpiryDate());

        Date future = new Date(System.currentTimeMillis() + 86400000L); // 1 day in future
        config.setExpiryDate(future);
        assertEquals(future, config.getExpiryDate());

        Date past = new Date(0L); // epoch
        config.setExpiryDate(past);
        assertEquals(past, config.getExpiryDate());

        config.setExpiryDate(null);
        assertNull(config.getExpiryDate());
    }

    /**
     * Test secure flag getter and setter
     */
    @Test
    public void test_secure() {
        CookieConfig config = new CookieConfig();

        assertFalse(config.isSecure());

        config.setSecure(true);
        assertTrue(config.isSecure());

        config.setSecure(false);
        assertFalse(config.isSecure());
    }

    /**
     * Test httpOnly flag getter and setter
     */
    @Test
    public void test_httpOnly() {
        CookieConfig config = new CookieConfig();

        assertFalse(config.isHttpOnly());

        config.setHttpOnly(true);
        assertTrue(config.isHttpOnly());

        config.setHttpOnly(false);
        assertFalse(config.isHttpOnly());
    }

    /**
     * Test full configuration with all properties set
     */
    @Test
    public void test_fullConfiguration() {
        CookieConfig config = new CookieConfig();
        Date expiry = new Date(System.currentTimeMillis() + 3600000L);

        config.setName("auth_token");
        config.setValue("xyz789");
        config.setDomain("secure.example.com");
        config.setPath("/secure");
        config.setExpiryDate(expiry);
        config.setSecure(true);
        config.setHttpOnly(true);

        assertEquals("auth_token", config.getName());
        assertEquals("xyz789", config.getValue());
        assertEquals("secure.example.com", config.getDomain());
        assertEquals("/secure", config.getPath());
        assertEquals(expiry, config.getExpiryDate());
        assertTrue(config.isSecure());
        assertTrue(config.isHttpOnly());
    }

    /**
     * Test toString method
     */
    @Test
    public void test_toString() {
        CookieConfig config = new CookieConfig();
        config.setName("testCookie");
        config.setDomain("example.com");
        config.setPath("/");
        config.setSecure(true);
        config.setHttpOnly(false);

        String result = config.toString();

        assertNotNull(result);
        assertTrue(result.contains("CookieConfig"));
        assertTrue(result.contains("name=testCookie"));
        assertTrue(result.contains("domain=example.com"));
        assertTrue(result.contains("path=/"));
        assertTrue(result.contains("secure=true"));
        assertTrue(result.contains("httpOnly=false"));
    }

    /**
     * Test toString with null values
     */
    @Test
    public void test_toString_withNullValues() {
        CookieConfig config = new CookieConfig();

        String result = config.toString();

        assertNotNull(result);
        assertTrue(result.contains("CookieConfig"));
        assertTrue(result.contains("name=null"));
        assertTrue(result.contains("domain=null"));
        assertTrue(result.contains("path=null"));
    }

    /**
     * Test toString does not include value (security)
     */
    @Test
    public void test_toString_excludesValue() {
        CookieConfig config = new CookieConfig();
        config.setName("session");
        config.setValue("sensitive_value_123");

        String result = config.toString();

        // Value should not be included in toString for security
        assertFalse(result.contains("sensitive_value_123"));
    }

    /**
     * Test special characters in cookie name
     */
    @Test
    public void test_specialCharactersInName() {
        CookieConfig config = new CookieConfig();

        config.setName("cookie_name_with-dashes");
        assertEquals("cookie_name_with-dashes", config.getName());

        config.setName("cookie.name.with.dots");
        assertEquals("cookie.name.with.dots", config.getName());
    }

    /**
     * Test Unicode characters in value
     */
    @Test
    public void test_unicodeValue() {
        CookieConfig config = new CookieConfig();

        config.setValue("value_with_日本語_characters");
        assertEquals("value_with_日本語_characters", config.getValue());

        config.setValue("émojis_🍪_cookie");
        assertEquals("émojis_🍪_cookie", config.getValue());
    }

    /**
     * Test multiple instances are independent
     */
    @Test
    public void test_multipleInstances() {
        CookieConfig config1 = new CookieConfig();
        CookieConfig config2 = new CookieConfig();

        config1.setName("cookie1");
        config1.setValue("value1");
        config1.setSecure(true);

        config2.setName("cookie2");
        config2.setValue("value2");
        config2.setSecure(false);

        assertEquals("cookie1", config1.getName());
        assertEquals("value1", config1.getValue());
        assertTrue(config1.isSecure());

        assertEquals("cookie2", config2.getName());
        assertEquals("value2", config2.getValue());
        assertFalse(config2.isSecure());
    }
}
