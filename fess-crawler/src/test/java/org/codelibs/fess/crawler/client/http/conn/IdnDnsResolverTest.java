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
package org.codelibs.fess.crawler.client.http.conn;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.dbflute.utflute.core.PlainTestCase;

public class IdnDnsResolverTest extends PlainTestCase {

    public void test_toAscii() {
        IdnDnsResolver resolver = new IdnDnsResolver();

        String host = "www.codelibs.org";
        String expected = "www.codelibs.org";
        assertEquals(expected, resolver.toAscii(host));

        host = "テスト.org";
        expected = "xn--zckzah.org";
        assertEquals(expected, resolver.toAscii(host));

        host = "xn--zckzah.org";
        expected = "xn--zckzah.org";
        assertEquals(expected, resolver.toAscii(host));

        host = "%E3%83%86%E3%82%B9%E3%83%88.org";
        expected = "xn--zckzah.org";
        assertEquals(expected, resolver.toAscii(host));

    }

    public void test_resolve_ipv6() throws UnknownHostException {
        IdnDnsResolver resolver = new IdnDnsResolver();

        // Test IPv6 loopback address with brackets
        String host = "[::1]";
        InetAddress[] addresses = resolver.resolve(host);
        assertNotNull(addresses);
        assertTrue(addresses.length > 0);
        // IPv6 loopback can be represented as ::1 or 0:0:0:0:0:0:0:1
        String hostAddress = addresses[0].getHostAddress();
        assertTrue(hostAddress.contains(":"));
        assertTrue(hostAddress.equals("::1") || hostAddress.equals("0:0:0:0:0:0:0:1"));

        // Test IPv6 address with brackets
        host = "[2001:db8::1]";
        addresses = resolver.resolve(host);
        assertNotNull(addresses);
        assertTrue(addresses.length > 0);
        // The address may be normalized differently depending on the system
        hostAddress = addresses[0].getHostAddress();
        assertTrue(hostAddress.contains(":"));
        assertTrue(hostAddress.contains("2001") || hostAddress.toLowerCase().contains("2001"));
    }

    public void test_resolve_ipv4() throws UnknownHostException {
        IdnDnsResolver resolver = new IdnDnsResolver();

        // Test IPv4 loopback address
        String host = "127.0.0.1";
        InetAddress[] addresses = resolver.resolve(host);
        assertNotNull(addresses);
        assertTrue(addresses.length > 0);
        assertEquals("127.0.0.1", addresses[0].getHostAddress());
    }

    public void test_resolve_hostname() throws UnknownHostException {
        IdnDnsResolver resolver = new IdnDnsResolver();

        // Test localhost hostname
        String host = "localhost";
        InetAddress[] addresses = resolver.resolve(host);
        assertNotNull(addresses);
        assertTrue(addresses.length > 0);
    }

    public void test_resolve_invalid_brackets() {
        IdnDnsResolver resolver = new IdnDnsResolver();

        // Test empty brackets - should be treated as invalid hostname
        try {
            resolver.resolve("[]");
            fail("Should throw UnknownHostException for empty brackets");
        } catch (UnknownHostException e) {
            // Expected behavior
        }
    }
}
