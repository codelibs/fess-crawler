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
package org.codelibs.fess.crawler.client.ftp;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 *
 */
public class FtpAuthenticationTest extends PlainTestCase {

    @Test
    public void test_matches() throws Exception {
        FtpAuthentication auth = new FtpAuthentication();
        auth.setServer("hostname");
        auth.setPort(21);
        auth.setUsername("testuser");
        auth.setPassword("testpass");

        assertTrue(auth.matches("ftp://hostname:21/test/aaa.html"));
        assertTrue(auth.matches("ftp://hostname/test/aaa.html"));
        assertTrue(auth.matches("ftp://hostname:21/test"));
        assertTrue(auth.matches("ftp://hostname/test"));
        assertTrue(auth.matches("ftp://hostname:21"));
        assertTrue(auth.matches("ftp://hostname"));
        assertTrue(auth.matches("ftp://hostname:21/"));
        assertTrue(auth.matches("ftp://hostname/"));

        assertFalse(auth.matches("ftp://hostname:111/"));
        assertFalse(auth.matches("ftp://hostname:xx/"));
        assertFalse(auth.matches("ftp://hoge/test/aaa.html"));
        assertFalse(auth.matches("ftp://hoge/test"));
        assertFalse(auth.matches("ftp://hoge/"));
        assertFalse(auth.matches("ftp://hoge"));
        assertFalse(auth.matches("ftp://"));
        assertFalse(auth.matches("http://hostname/"));
        assertFalse(auth.matches("http://hostname:21/"));
        assertFalse(auth.matches(""));
        assertFalse(auth.matches(null));

        // Test with different ports
        auth.setPort(8080);
        assertTrue(auth.matches("ftp://hostname:8080/test/aaa.html"));
        assertFalse(auth.matches("ftp://hostname:21/test/aaa.html"));

        // Test with no port specified in FtpAuthentication
        auth.setPort(0);
        assertTrue(auth.matches("ftp://hostname:21/test/aaa.html"));
        assertTrue(auth.matches("ftp://hostname/test/aaa.html"));

        // Test with different server
        auth.setServer("otherhost");
        assertFalse(auth.matches("ftp://hostname:21/test/aaa.html"));
        assertTrue(auth.matches("ftp://otherhost:21/test/aaa.html"));

        // Test with IP address
        auth.setServer("192.168.1.1");
        assertTrue(auth.matches("ftp://192.168.1.1:21/test/aaa.html"));
        assertTrue(auth.matches("ftp://192.168.1.1/test/aaa.html"));
        assertFalse(auth.matches("ftp://hostname/test/aaa.html"));

        // Test with more complex paths
        auth.setServer("hostname");
        auth.setPort(21);
        assertTrue(auth.matches("ftp://hostname:21/path/to/resource.txt"));
        assertTrue(auth.matches("ftp://hostname/path/to/resource.txt"));
        assertFalse(auth.matches("ftp://hostname:22/path/to/resource.txt"));
        assertFalse(auth.matches("ftp://otherhost/path/to/resource.txt"));

        // Test with special characters in path
        assertTrue(auth.matches("ftp://hostname:21/path%20with%20spaces/resource.txt"));
        assertTrue(auth.matches("ftp://hostname/path%20with%20spaces/resource.txt"));

        // Test with empty path
        auth.setServer("test.example.com");
        assertTrue(auth.matches("ftp://test.example.com/"));
        assertTrue(auth.matches("ftp://test.example.com:21/"));
    }
}
