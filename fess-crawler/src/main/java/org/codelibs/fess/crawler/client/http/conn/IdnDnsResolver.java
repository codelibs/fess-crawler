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

    protected int flag = 0;

    protected String encoding = "UTF-8";

    @Override
    public InetAddress[] resolve(final String host) throws UnknownHostException {
        return InetAddress.getAllByName(toAscii(host));
    }

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

    protected String toAscii(final String host) {
        return IDN.toASCII(decode(host), flag);
    }

    public void setFlag(final int flag) {
        this.flag = flag;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
}
