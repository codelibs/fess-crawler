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
package org.codelibs.fess.crawler.client.azure;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for AzureBlobClient.
 *
 * @author shinsuke
 */
public class AzureBlobClientTest extends PlainTestCase {

    public AzureBlobClient azureBlobClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("mimeTypeHelper", org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl.class);
        azureBlobClient = new AzureBlobClient();
        azureBlobClient.crawlerContainer = container;
    }

    @Override
    protected void tearDown() throws Exception {
        azureBlobClient.close();
        super.tearDown();
    }

    public void test_parsePath() {
        String[] result = azureBlobClient.parsePath("my-container/path/to/blob.txt");
        assertEquals("my-container", result[0]);
        assertEquals("path/to/blob.txt", result[1]);

        result = azureBlobClient.parsePath("my-container/blob.txt");
        assertEquals("my-container", result[0]);
        assertEquals("blob.txt", result[1]);

        result = azureBlobClient.parsePath("my-container");
        assertEquals("my-container", result[0]);
        assertEquals("", result[1]);
    }

    public void test_parsePath_invalid() {
        try {
            azureBlobClient.parsePath("");
            fail("Should throw CrawlingAccessException for empty path");
        } catch (final CrawlingAccessException e) {
            assertTrue(e.getMessage().contains("Invalid path"));
        }
    }

    public void test_normalizeUri() {
        assertEquals("azure://my-container/blob.txt", azureBlobClient.normalizeUri("azure://my-container/blob.txt"));
        assertEquals("azure://my-container/blob.txt", azureBlobClient.normalizeUri("my-container/blob.txt"));
    }

    public void test_charsetGetterSetter() {
        azureBlobClient.setCharset("UTF-16");
        assertEquals("UTF-16", azureBlobClient.getCharset());
    }
}
