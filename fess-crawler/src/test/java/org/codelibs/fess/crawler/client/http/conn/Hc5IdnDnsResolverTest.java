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

import java.net.IDN;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

public class Hc5IdnDnsResolverTest extends PlainTestCase {

    @Test
    public void test_toAscii() {
        Hc5IdnDnsResolver resolver = new Hc5IdnDnsResolver();

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

    @Test
    public void test_resolve_ipv6() throws UnknownHostException {
        Hc5IdnDnsResolver resolver = new Hc5IdnDnsResolver();

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

    @Test
    public void test_resolve_ipv4() throws UnknownHostException {
        Hc5IdnDnsResolver resolver = new Hc5IdnDnsResolver();

        // Test IPv4 loopback address
        String host = "127.0.0.1";
        InetAddress[] addresses = resolver.resolve(host);
        assertNotNull(addresses);
        assertTrue(addresses.length > 0);
        assertEquals("127.0.0.1", addresses[0].getHostAddress());
    }

    @Test
    public void test_resolve_hostname() throws UnknownHostException {
        Hc5IdnDnsResolver resolver = new Hc5IdnDnsResolver();

        // Test localhost hostname
        String host = "localhost";
        InetAddress[] addresses = resolver.resolve(host);
        assertNotNull(addresses);
        assertTrue(addresses.length > 0);
    }

    @Test
    public void test_resolve_invalid_brackets() {
        Hc5IdnDnsResolver resolver = new Hc5IdnDnsResolver();

        // Test empty brackets - should be treated as invalid hostname
        try {
            resolver.resolve("[]");
            fail();
        } catch (UnknownHostException e) {
            // Expected behavior
        }
    }

    @Test
    public void test_resolveCanonicalHostname() throws UnknownHostException {
        Hc5IdnDnsResolver resolver = new Hc5IdnDnsResolver();

        // Test with localhost
        String canonicalHostname = resolver.resolveCanonicalHostname("localhost");
        assertNotNull(canonicalHostname);
        assertTrue(canonicalHostname.length() > 0);

        // Test with IP address
        canonicalHostname = resolver.resolveCanonicalHostname("127.0.0.1");
        assertNotNull(canonicalHostname);
        assertTrue(canonicalHostname.length() > 0);
    }

    @Test
    public void test_setFlag() {
        Hc5IdnDnsResolver resolver = new Hc5IdnDnsResolver();

        resolver.setFlag(IDN.ALLOW_UNASSIGNED);

        // Test that flag is applied correctly by converting a host
        String host = "テスト.org";
        String result = resolver.toAscii(host);
        assertNotNull(result);
        assertEquals("xn--zckzah.org", result);
    }

    @Test
    public void test_setEncoding() {
        Hc5IdnDnsResolver resolver = new Hc5IdnDnsResolver();

        resolver.setEncoding("UTF-8");

        // Test URL-encoded host
        String host = "%E3%83%86%E3%82%B9%E3%83%88.org";
        String result = resolver.toAscii(host);
        assertEquals("xn--zckzah.org", result);
    }

    @Test
    public void test_decode_noEncodedChars() {
        Hc5IdnDnsResolver resolver = new Hc5IdnDnsResolver();

        // Test that hosts without % are returned as-is
        String host = "www.example.com";
        String result = resolver.decode(host);
        assertEquals(host, result);
    }
}
