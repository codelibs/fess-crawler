/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.smb1;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 *
 */
public class SmbAuthenticationHolderTest extends PlainTestCase {

    public void test_get() {
        final SmbAuthenticationHolder smbAuthenticationHolder = new SmbAuthenticationHolder();
        final SmbAuthentication hogeAuth = new SmbAuthentication();
        hogeAuth.setServer("hoge");
        smbAuthenticationHolder.add(hogeAuth);
        final SmbAuthentication fugaAuth = new SmbAuthentication();
        fugaAuth.setServer("fuga");
        smbAuthenticationHolder.add(fugaAuth);
        final SmbAuthentication fooAuth = new SmbAuthentication();
        fooAuth.setServer("foo");
        fooAuth.setPort(1000);
        smbAuthenticationHolder.add(fooAuth);

        assertEquals(hogeAuth, smbAuthenticationHolder.get("smb1://hoge/"));
        assertEquals(fugaAuth, smbAuthenticationHolder.get("smb1://fuga/"));
        assertEquals(fooAuth, smbAuthenticationHolder.get("smb1://foo:1000/"));
        assertEquals(hogeAuth, smbAuthenticationHolder.get("smb1://hoge/text.txt"));
        assertEquals(fugaAuth, smbAuthenticationHolder.get("smb1://fuga/text.txt"));
        assertEquals(fooAuth, smbAuthenticationHolder.get("smb1://foo:1000/text.txt"));

        assertNull(smbAuthenticationHolder.get(null));
        assertNull(smbAuthenticationHolder.get(""));
        assertNull(smbAuthenticationHolder.get(" "));
        assertNull(smbAuthenticationHolder.get("smb1://"));
        assertNull(smbAuthenticationHolder.get("smb1://hoge:1000/"));
        assertNull(smbAuthenticationHolder.get("smb1://foo/"));
        assertNull(smbAuthenticationHolder.get("smb1://foo:10000/"));
    }
}
