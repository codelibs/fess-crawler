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

import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;

import org.apache.http.conn.DnsResolver;

/**
 * The {@code IdnDnsResolver} class implements the {@code DnsResolver} interface
 * to provide DNS resolution with support for Internationalized Domain Names (IDN).
 * It converts Unicode domain names to ASCII Compatible Encoding (ACE) using the
 * {@link java.net.IDN} class.
 *
 * <p>This class allows setting a custom flag for the IDN conversion and a custom
 * encoding for URL decoding.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * IdnDnsResolver resolver = new IdnDnsResolver();
 * resolver.setFlag(IDN.ALLOW_UNASSIGNED);
 * resolver.setEncoding("UTF-8");
 * InetAddress[] addresses = resolver.resolve("example.com");
 * }
 * </pre>
 *
 */
public class IdnDnsResolver implements DnsResolver {

    /** Flag for IDN conversion. */
    protected int flag = 0;

    /** Encoding for URL decoding. */
    protected String encoding = "UTF-8";

    /**
     * Creates a new IdnDnsResolver instance with default settings.
     */
    public IdnDnsResolver() {
        super();
    }

    /**
     * Resolves the given host name to an array of IP addresses.
     * The host name is first converted to ASCII using IDN before resolution.
     * IPv6 addresses in bracket notation (e.g., [::1] or [2001:db8::1]) are
     * handled specially by removing the brackets before resolution.
     *
     * @param host the host name to resolve
     * @return an array of IP addresses for the host
     * @throws UnknownHostException if the host name cannot be resolved
     */
    @Override
    public InetAddress[] resolve(final String host) throws UnknownHostException {
        // Handle IPv6 addresses in bracket notation
        if (host != null && host.startsWith("[") && host.endsWith("]")) {
            // Remove brackets for IPv6 address resolution
            final String ipv6Address = host.substring(1, host.length() - 1);
            return InetAddress.getAllByName(ipv6Address);
        }
        return InetAddress.getAllByName(toAscii(host));
    }

    /**
     * Decodes the given host string using the specified encoding.
     *
     * @param host the host string to decode
     * @return the decoded host string
     */
    protected String decode(final String host) {
        if (host.indexOf('%') == -1) {
            return host;
        }
        try {
            return URLDecoder.decode(host, encoding);
        } catch (final UnsupportedEncodingException e) {
            return host;
        }
    }

    /**
     * Converts the given host string to ASCII using IDN.
     *
     * @param host the host string to convert
     * @return the ASCII representation of the host string
     */
    protected String toAscii(final String host) {
        return IDN.toASCII(decode(host), flag);
    }

    /**
     * Sets the flag for IDN conversion.
     *
     * @param flag the flag to set
     */
    public void setFlag(final int flag) {
        this.flag = flag;
    }

    /**
     * Sets the encoding for URL decoding.
     *
     * @param encoding the encoding to set
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
}
