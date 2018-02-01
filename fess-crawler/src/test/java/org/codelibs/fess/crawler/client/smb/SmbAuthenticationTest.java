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
package org.codelibs.fess.crawler.client.smb;

import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class SmbAuthenticationTest extends PlainTestCase {
    public void test_getPathPrefix() {
        SmbAuthentication smbAuthentication;

        smbAuthentication = new SmbAuthentication();
        assertEquals("smb://", smbAuthentication.getPathPrefix());

        smbAuthentication = new SmbAuthentication();
        smbAuthentication.setServer("hoge");
        assertEquals("smb://hoge/", smbAuthentication.getPathPrefix());

        smbAuthentication = new SmbAuthentication();
        smbAuthentication.setServer("hoge");
        smbAuthentication.setPort(1000);
        assertEquals("smb://hoge:1000/", smbAuthentication.getPathPrefix());

        smbAuthentication = new SmbAuthentication();
        smbAuthentication.setPort(1000);
        assertEquals("smb://", smbAuthentication.getPathPrefix());
    }
}
