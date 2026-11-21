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
package org.codelibs.fess.crawler.client.smb;

import java.net.ServerSocket;

import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests that the message of the exception of {@link SmbClient}, which is stored as the error log of the failure URL,
 * does not carry the user name and password of the crawl URL. It needs no SMB server: the port is closed.
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
            assertEquals("Could not access smb://127.0.0.1:" + port + "/share/file.txt", e.getMessage());
        } finally {
            smbClient.close();
        }
    }
}
