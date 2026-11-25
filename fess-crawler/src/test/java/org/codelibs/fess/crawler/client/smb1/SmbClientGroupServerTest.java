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
package org.codelibs.fess.crawler.client.smb1;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.net.URL;

import org.codelibs.jcifs.smb1.SmbFile;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests the server that the SMB1 {@link SmbClient} gives to the group member lookup.
 */
public class SmbClientGroupServerTest extends PlainTestCase {

    private final SmbClient smbClient = new SmbClient();

    private SmbFile file(final String server, final String url) throws Exception {
        final SmbFile file = mock(SmbFile.class);
        doReturn(server).when(file).getServer();
        doReturn(new URL(url)).when(file).getURL();
        return file;
    }

    @Test
    public void test_defaultPort_isTheHostOnly() throws Exception {
        assertEquals("fileserver", smbClient.getServerOf(file("fileserver", "http://fileserver/share/a.txt")));
    }

    @Test
    public void test_otherPort_followsTheHost() throws Exception {
        assertEquals("fileserver:1139", smbClient.getServerOf(file("fileserver", "http://fileserver:1139/share/a.txt")));
    }

    @Test
    public void test_serverWithColon_isLeftAsIs() throws Exception {
        assertEquals("::1", smbClient.getServerOf(file("::1", "http://[::1]:1139/share/a.txt")));
    }
}
