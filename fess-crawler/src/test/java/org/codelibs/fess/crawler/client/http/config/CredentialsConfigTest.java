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

import org.codelibs.fess.crawler.client.http.config.CredentialsConfig.CredentialsType;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for CredentialsConfig.
 * Tests all getters, setters, enum values, and toString functionality.
 */
public class CredentialsConfigTest extends PlainTestCase {

    /**
     * Test default values on new instance
     */
    public void test_defaultValues() {
        CredentialsConfig config = new CredentialsConfig();

        assertEquals(CredentialsType.USERNAME_PASSWORD, config.getType());
        assertNull(config.getUsername());
        assertNull(config.getPassword());
        assertNull(config.getDomain());
        assertNull(config.getWorkstation());
    }

    /**
     * Test CredentialsType enum values
     */
    public void test_credentialsTypeEnum() {
        assertEquals(2, CredentialsType.values().length);

        assertEquals(CredentialsType.USERNAME_PASSWORD, CredentialsType.valueOf("USERNAME_PASSWORD"));
        assertEquals(CredentialsType.NTLM, CredentialsType.valueOf("NTLM"));
    }

    /**
     * Test type getter and setter
     */
    public void test_type() {
        CredentialsConfig config = new CredentialsConfig();

        assertEquals(CredentialsType.USERNAME_PASSWORD, config.getType());

        config.setType(CredentialsType.NTLM);
        assertEquals(CredentialsType.NTLM, config.getType());

        config.setType(CredentialsType.USERNAME_PASSWORD);
        assertEquals(CredentialsType.USERNAME_PASSWORD, config.getType());

        config.setType(null);
        assertNull(config.getType());
    }

    /**
     * Test username getter and setter
     */
    public void test_username() {
        CredentialsConfig config = new CredentialsConfig();

        config.setUsername("testuser");
        assertEquals("testuser", config.getUsername());

        config.setUsername("user@domain.com");
        assertEquals("user@domain.com", config.getUsername());

        config.setUsername("DOMAIN\\user");
        assertEquals("DOMAIN\\user", config.getUsername());

        config.setUsername(null);
        assertNull(config.getUsername());

        config.setUsername("");
        assertEquals("", config.getUsername());
    }

    /**
     * Test password getter and setter
     */
    public void test_password() {
        CredentialsConfig config = new CredentialsConfig();

        config.setPassword("secret123");
        assertEquals("secret123", config.getPassword());

        config.setPassword("p@ssw0rd!#$%");
        assertEquals("p@ssw0rd!#$%", config.getPassword());

        config.setPassword(null);
        assertNull(config.getPassword());

        config.setPassword("");
        assertEquals("", config.getPassword());
    }

    /**
     * Test domain getter and setter (NTLM)
     */
    public void test_domain() {
        CredentialsConfig config = new CredentialsConfig();

        config.setDomain("MYDOMAIN");
        assertEquals("MYDOMAIN", config.getDomain());

        config.setDomain("CORP");
        assertEquals("CORP", config.getDomain());

        config.setDomain("domain.local");
        assertEquals("domain.local", config.getDomain());

        config.setDomain(null);
        assertNull(config.getDomain());
    }

    /**
     * Test workstation getter and setter (NTLM)
     */
    public void test_workstation() {
        CredentialsConfig config = new CredentialsConfig();

        config.setWorkstation("MYWORKSTATION");
        assertEquals("MYWORKSTATION", config.getWorkstation());

        config.setWorkstation("PC001");
        assertEquals("PC001", config.getWorkstation());

        config.setWorkstation(null);
        assertNull(config.getWorkstation());
    }

    /**
     * Test basic username/password configuration
     */
    public void test_basicConfiguration() {
        CredentialsConfig config = new CredentialsConfig();

        config.setType(CredentialsType.USERNAME_PASSWORD);
        config.setUsername("admin");
        config.setPassword("admin123");

        assertEquals(CredentialsType.USERNAME_PASSWORD, config.getType());
        assertEquals("admin", config.getUsername());
        assertEquals("admin123", config.getPassword());
        assertNull(config.getDomain());
        assertNull(config.getWorkstation());
    }

    /**
     * Test NTLM configuration with all fields
     */
    public void test_ntlmConfiguration() {
        CredentialsConfig config = new CredentialsConfig();

        config.setType(CredentialsType.NTLM);
        config.setUsername("ntlmuser");
        config.setPassword("ntlmpass");
        config.setDomain("NTLMDOMAIN");
        config.setWorkstation("NTLMWORKSTATION");

        assertEquals(CredentialsType.NTLM, config.getType());
        assertEquals("ntlmuser", config.getUsername());
        assertEquals("ntlmpass", config.getPassword());
        assertEquals("NTLMDOMAIN", config.getDomain());
        assertEquals("NTLMWORKSTATION", config.getWorkstation());
    }

    /**
     * Test toString method
     */
    public void test_toString() {
        CredentialsConfig config = new CredentialsConfig();
        config.setType(CredentialsType.NTLM);
        config.setUsername("user");
        config.setDomain("DOMAIN");
        config.setWorkstation("WORKSTATION");

        String result = config.toString();

        assertNotNull(result);
        assertTrue(result.contains("CredentialsConfig"));
        assertTrue(result.contains("type=NTLM"));
        assertTrue(result.contains("username=user"));
        assertTrue(result.contains("domain=DOMAIN"));
        assertTrue(result.contains("workstation=WORKSTATION"));
    }

    /**
     * Test toString excludes password (security)
     */
    public void test_toString_excludesPassword() {
        CredentialsConfig config = new CredentialsConfig();
        config.setUsername("user");
        config.setPassword("supersecretpassword123");

        String result = config.toString();

        // Password should NOT be included in toString for security
        assertFalse(result.contains("supersecretpassword123"));
        assertFalse(result.contains("password="));
    }

    /**
     * Test toString with null values
     */
    public void test_toString_withNullValues() {
        CredentialsConfig config = new CredentialsConfig();

        String result = config.toString();

        assertNotNull(result);
        assertTrue(result.contains("CredentialsConfig"));
        assertTrue(result.contains("type=USERNAME_PASSWORD"));
        assertTrue(result.contains("username=null"));
    }

    /**
     * Test special characters in credentials
     */
    public void test_specialCharacters() {
        CredentialsConfig config = new CredentialsConfig();

        config.setUsername("user+tag@example.com");
        assertEquals("user+tag@example.com", config.getUsername());

        config.setPassword("p@$$w0rd!#%^&*()[]{}");
        assertEquals("p@$$w0rd!#%^&*()[]{}", config.getPassword());

        config.setDomain("DOMAIN-NAME");
        assertEquals("DOMAIN-NAME", config.getDomain());
    }

    /**
     * Test Unicode characters
     */
    public void test_unicodeCharacters() {
        CredentialsConfig config = new CredentialsConfig();

        config.setUsername("用户名");
        assertEquals("用户名", config.getUsername());

        config.setPassword("密码123");
        assertEquals("密码123", config.getPassword());
    }

    /**
     * Test multiple instances are independent
     */
    public void test_multipleInstances() {
        CredentialsConfig config1 = new CredentialsConfig();
        CredentialsConfig config2 = new CredentialsConfig();

        config1.setType(CredentialsType.USERNAME_PASSWORD);
        config1.setUsername("user1");
        config1.setPassword("pass1");

        config2.setType(CredentialsType.NTLM);
        config2.setUsername("user2");
        config2.setPassword("pass2");
        config2.setDomain("DOMAIN2");

        assertEquals(CredentialsType.USERNAME_PASSWORD, config1.getType());
        assertEquals("user1", config1.getUsername());
        assertNull(config1.getDomain());

        assertEquals(CredentialsType.NTLM, config2.getType());
        assertEquals("user2", config2.getUsername());
        assertEquals("DOMAIN2", config2.getDomain());
    }

    /**
     * Test setting NTLM fields with USERNAME_PASSWORD type
     */
    public void test_ntlmFieldsWithBasicType() {
        CredentialsConfig config = new CredentialsConfig();

        // Even with USERNAME_PASSWORD type, NTLM fields can be set
        // (it's up to the consuming code to use them appropriately)
        config.setType(CredentialsType.USERNAME_PASSWORD);
        config.setUsername("user");
        config.setPassword("pass");
        config.setDomain("SOMEDOMAIN");
        config.setWorkstation("SOMEWORKSTATION");

        assertEquals(CredentialsType.USERNAME_PASSWORD, config.getType());
        assertEquals("SOMEDOMAIN", config.getDomain());
        assertEquals("SOMEWORKSTATION", config.getWorkstation());
    }

    /**
     * Test empty strings for all fields
     */
    public void test_emptyStrings() {
        CredentialsConfig config = new CredentialsConfig();

        config.setUsername("");
        config.setPassword("");
        config.setDomain("");
        config.setWorkstation("");

        assertEquals("", config.getUsername());
        assertEquals("", config.getPassword());
        assertEquals("", config.getDomain());
        assertEquals("", config.getWorkstation());
    }
}
