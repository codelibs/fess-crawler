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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.ServerSocket;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.jcifs.smb1.SmbFile;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests that the message of the exception of {@link SmbClient}, which is stored as the error log of the failure URL,
 * names the cause of the failure and does not carry the user name and password of the crawl URL. It needs no SMB
 * server: the port is closed, and the ACL is read from a mock.
 */
public class SmbClientUserInfoTest extends PlainTestCase {

    @Test
    public void test_doGet_errorMessageHasNoUserInfo() throws Exception {
        final int port;
        try (final ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        final StandardCrawlerContainer container = new StandardCrawlerContainer()//
                .singleton("smbClient", SmbClient.class)//
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        final SmbClient smbClient = container.getComponent("smbClient");
        smbClient.init();
        try {
            smbClient.doGet("smb://crawluser:s3cr3tpw@127.0.0.1:" + port + "/share/file.txt");
            fail();
        } catch (final CrawlingAccessException e) {
            assertFalse(e.getMessage().contains("s3cr3tpw"));
            assertFalse(e.getMessage().contains("crawluser"));
            assertTrue(e.getMessage().startsWith("Could not access smb://127.0.0.1:" + port + "/share/file.txt: "));
            assertFalse(e.getMessage().endsWith(": "));
        } finally {
            smbClient.close();
        }
    }

    @Test
    public void test_processAccessControlEntries_errorMessageNamesCause() throws Exception {
        final CrawlingAccessException e =
                accessAclWith(new IOException("Encryption is required but not available for the negotiated dialect"));
        assertEquals("Could not access smb://127.0.0.1/share/file.txt: Encryption is required but not available for the negotiated dialect",
                e.getMessage());
    }

    @Test
    public void test_processAccessControlEntries_causeMessageHasNoUserInfo() throws Exception {
        CrawlingAccessException e = accessAclWith(new IOException("smb://crawluser:s3cr3tpw@127.0.0.1/share/file.txt"));
        assertEquals("Could not access smb://127.0.0.1/share/file.txt: smb://127.0.0.1/share/file.txt", e.getMessage());

        e = accessAclWith(new IOException("The requested list operations is invalid: smb://crawluser:s3cr3tpw@127.0.0.1/share/file.txt"));
        assertEquals(
                "Could not access smb://127.0.0.1/share/file.txt: The requested list operations is invalid: smb://127.0.0.1/share/file.txt",
                e.getMessage());

        e = accessAclWith(new IOException("smb://crawluser:s3cr3t pw@127.0.0.1/share/file.txt"));
        assertEquals("Could not access smb://127.0.0.1/share/file.txt: smb://127.0.0.1/share/file.txt", e.getMessage());
    }

    @Test
    public void test_processAccessControlEntries_noCauseMessage() throws Exception {
        final CrawlingAccessException e = accessAclWith(new IOException());
        assertEquals("Could not access smb://127.0.0.1/share/file.txt", e.getMessage());
    }

    private CrawlingAccessException accessAclWith(final IOException cause) throws IOException {
        final SmbFile file = mock(SmbFile.class);
        doReturn("smb://crawluser:s3cr3tpw@127.0.0.1/share/file.txt").when(file).getPath();
        doThrow(cause).when(file).getSecurity(true);
        try {
            new SmbClient().processAccessControlEntries(new ResponseData(), file);
            fail();
            return null;
        } catch (final CrawlingAccessException e) {
            return e;
        }
    }
}
