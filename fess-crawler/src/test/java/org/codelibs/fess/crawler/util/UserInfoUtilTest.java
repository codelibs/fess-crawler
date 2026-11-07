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
package org.codelibs.fess.crawler.util;

import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link UserInfoUtil}.
 */
public class UserInfoUtilTest extends PlainTestCase {

    @Test
    public void test_removeUserInfo_userAndPassword() {
        assertEquals("http://host.example.com/path", UserInfoUtil.removeUserInfo("http://user:secret@host.example.com/path"));
        assertEquals("https://host.example.com:8443/a?b=c#d",
                UserInfoUtil.removeUserInfo("https://user:secret@host.example.com:8443/a?b=c#d"));
    }

    @Test
    public void test_removeUserInfo_userOnly() {
        assertEquals("http://host.example.com/", UserInfoUtil.removeUserInfo("http://user@host.example.com/"));
    }

    @Test
    public void test_removeUserInfo_otherSchemes() {
        assertEquals("smb://host.example.com/share/", UserInfoUtil.removeUserInfo("smb://DOMAIN;user:secret@host.example.com/share/"));
        assertEquals("ftp://host.example.com/pub", UserInfoUtil.removeUserInfo("ftp://user:secret@host.example.com/pub"));
    }

    @Test
    public void test_removeUserInfo_ipv6Host() {
        assertEquals("http://[::1]:8080/", UserInfoUtil.removeUserInfo("http://user:secret@[::1]:8080/"));
    }

    @Test
    public void test_removeUserInfo_passwordWithSpecialCharacters() {
        assertEquals("http://host.example.com/", UserInfoUtil.removeUserInfo("http://user:p@ss:w%40rd@host.example.com/"));
        assertEquals("http://host.example.com/", UserInfoUtil.removeUserInfo("http://user:pass word@host.example.com/"));
    }

    @Test
    public void test_removeUserInfo_noUserInfo() {
        final String url = "http://host.example.com/path";
        assertTrue(url == UserInfoUtil.removeUserInfo(url));
    }

    @Test
    public void test_removeUserInfo_atSignOutsideTheAuthority() {
        assertEquals("http://host.example.com/a@b", UserInfoUtil.removeUserInfo("http://host.example.com/a@b"));
        assertEquals("http://host.example.com/?mail=a@b", UserInfoUtil.removeUserInfo("http://host.example.com/?mail=a@b"));
        assertEquals("http://host.example.com/#a@b", UserInfoUtil.removeUserInfo("http://host.example.com/#a@b"));
    }

    @Test
    public void test_removeUserInfo_notAbsoluteUrl() {
        assertEquals("user:secret@host", UserInfoUtil.removeUserInfo("user:secret@host"));
        assertEquals("/path/a@b", UserInfoUtil.removeUserInfo("/path/a@b"));
        assertEquals("://user@host/", UserInfoUtil.removeUserInfo("://user@host/"));
        assertEquals("1http://user:secret@host/", UserInfoUtil.removeUserInfo("1http://user:secret@host/"));
    }

    @Test
    public void test_removeUserInfo_nullAndEmpty() {
        assertNull(UserInfoUtil.removeUserInfo(null));
        assertEquals("", UserInfoUtil.removeUserInfo(""));
        assertEquals("@", UserInfoUtil.removeUserInfo("@"));
        assertEquals("http://", UserInfoUtil.removeUserInfo("http://"));
        assertEquals("http://", UserInfoUtil.removeUserInfo("http://@"));
    }
}
