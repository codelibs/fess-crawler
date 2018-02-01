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
package org.codelibs.fess.crawler.client.http.conn;

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
}
