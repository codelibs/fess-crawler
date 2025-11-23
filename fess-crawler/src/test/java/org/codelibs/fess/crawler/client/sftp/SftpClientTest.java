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
package org.codelibs.fess.crawler.client.sftp;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for SftpClient.
 *
 * @author shinsuke
 */
public class SftpClientTest extends PlainTestCase {

    public SftpClient sftpClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("mimeTypeHelper", org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl.class);
        sftpClient = new SftpClient();
        sftpClient.crawlerContainer = container;
    }

    @Override
    protected void tearDown() throws Exception {
        sftpClient.close();
        super.tearDown();
    }

    public void test_init() {
        final Map<String, Object> params = new HashMap<>();
        params.put("connectTimeout", 5000);
        params.put("strictHostKeyChecking", "yes");

        sftpClient.setInitParameterMap(params);
        sftpClient.init();

        assertEquals(5000, sftpClient.getConnectTimeout());
        assertEquals("yes", sftpClient.getStrictHostKeyChecking());
    }

    public void test_SftpInfo() {
        // Test basic URL parsing
        SftpClient.SftpInfo info = new SftpClient.SftpInfo("sftp://example.com/path/to/file.txt", Constants.UTF_8);
        assertEquals("example.com", info.getHost());
        assertEquals(22, info.getPort());
        assertEquals("/path/to/file.txt", info.getPath());
        assertEquals("file.txt", info.getFilename());

        // Test with custom port
        info = new SftpClient.SftpInfo("sftp://example.com:2222/path/to/file.txt", Constants.UTF_8);
        assertEquals("example.com", info.getHost());
        assertEquals(2222, info.getPort());

        // Test root directory
        info = new SftpClient.SftpInfo("sftp://example.com/", Constants.UTF_8);
        assertEquals("/", info.getPath());
        assertEquals("", info.getFilename());

        // Test child URL generation
        info = new SftpClient.SftpInfo("sftp://example.com/path/to", Constants.UTF_8);
        String childUrl = info.toChildUrl("file.txt");
        assertTrue(childUrl.contains("file.txt"));
    }

    public void test_SftpInfo_invalidScheme() {
        try {
            new SftpClient.SftpInfo("http://example.com/file.txt", Constants.UTF_8);
            fail("Should throw CrawlingAccessException for invalid scheme");
        } catch (final CrawlingAccessException e) {
            assertTrue(e.getMessage().contains("Invalid scheme"));
        }
    }

    public void test_SftpInfo_blankUrl() {
        try {
            new SftpClient.SftpInfo("", Constants.UTF_8);
            fail("Should throw CrawlingAccessException for blank URL");
        } catch (final CrawlingAccessException e) {
            assertTrue(e.getMessage().contains("blank"));
        }
    }

    public void test_SftpAuthentication() {
        final SftpAuthentication auth = new SftpAuthentication();
        auth.setServer("sftp://example\\.com/.*");
        auth.setPort(2222);
        auth.setUsername("testuser");
        auth.setPassword("testpass");

        assertEquals(2222, auth.getPort());
        assertEquals("testuser", auth.getUsername());
        assertEquals("testpass", auth.getPassword());
        assertNotNull(auth.getServerPattern());
    }

    public void test_SftpAuthenticationHolder() {
        final SftpAuthenticationHolder holder = new SftpAuthenticationHolder();

        final SftpAuthentication auth1 = new SftpAuthentication();
        auth1.setServer("sftp://example\\.com/.*");
        auth1.setUsername("user1");

        final SftpAuthentication auth2 = new SftpAuthentication();
        auth2.setServer("sftp://test\\.com/.*");
        auth2.setUsername("user2");

        holder.add(auth1);
        holder.add(auth2);

        SftpAuthentication found = holder.get("sftp://example.com/path/file.txt");
        assertNotNull(found);
        assertEquals("user1", found.getUsername());

        found = holder.get("sftp://test.com/path/file.txt");
        assertNotNull(found);
        assertEquals("user2", found.getUsername());

        found = holder.get("sftp://unknown.com/path/file.txt");
        assertNull(found);
    }

    public void test_charsetGetterSetter() {
        sftpClient.setCharset("UTF-16");
        assertEquals("UTF-16", sftpClient.getCharset());
    }

    public void test_connectTimeoutGetterSetter() {
        sftpClient.setConnectTimeout(15000);
        assertEquals(15000, sftpClient.getConnectTimeout());
    }

    public void test_strictHostKeyCheckingGetterSetter() {
        sftpClient.setStrictHostKeyChecking("yes");
        assertEquals("yes", sftpClient.getStrictHostKeyChecking());
    }
}
