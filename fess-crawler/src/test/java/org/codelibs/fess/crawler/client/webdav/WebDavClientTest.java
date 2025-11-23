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
package org.codelibs.fess.crawler.client.webdav;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for WebDavClient.
 *
 * @author shinsuke
 */
public class WebDavClientTest extends PlainTestCase {

    public WebDavClient webDavClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("mimeTypeHelper", org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl.class);
        webDavClient = new WebDavClient();
        webDavClient.crawlerContainer = container;
    }

    @Override
    protected void tearDown() throws Exception {
        webDavClient.close();
        super.tearDown();
    }

    public void test_init() {
        final Map<String, Object> params = new HashMap<>();
        webDavClient.setInitParameterMap(params);
        webDavClient.init();

        assertNotNull(webDavClient);
    }

    public void test_WebDavAuthentication() {
        final WebDavAuthentication auth = new WebDavAuthentication();
        auth.setServer("http://example\\.com/webdav/.*");
        auth.setUsername("testuser");
        auth.setPassword("testpass");

        assertEquals("testuser", auth.getUsername());
        assertEquals("testpass", auth.getPassword());
        assertNotNull(auth.getServerPattern());
    }

    public void test_WebDavAuthenticationHolder() {
        final WebDavAuthenticationHolder holder = new WebDavAuthenticationHolder();

        final WebDavAuthentication auth1 = new WebDavAuthentication();
        auth1.setServer("http://example\\.com/webdav/.*");
        auth1.setUsername("user1");

        final WebDavAuthentication auth2 = new WebDavAuthentication();
        auth2.setServer("http://test\\.com/webdav/.*");
        auth2.setUsername("user2");

        holder.add(auth1);
        holder.add(auth2);

        WebDavAuthentication found = holder.get("http://example.com/webdav/files/file.txt");
        assertNotNull(found);
        assertEquals("user1", found.getUsername());

        found = holder.get("http://test.com/webdav/files/file.txt");
        assertNotNull(found);
        assertEquals("user2", found.getUsername());

        found = holder.get("http://unknown.com/webdav/files/file.txt");
        assertNull(found);
    }

    public void test_charsetGetterSetter() {
        webDavClient.setCharset("UTF-16");
        assertEquals("UTF-16", webDavClient.getCharset());
    }

    public void test_getFileName() {
        assertEquals("file.txt", webDavClient.getFileName("http://example.com/path/to/file.txt"));
        assertEquals("file.txt", webDavClient.getFileName("/path/to/file.txt"));
        assertEquals("", webDavClient.getFileName("http://example.com/path/to/"));
        assertEquals("", webDavClient.getFileName(null));
    }
}
