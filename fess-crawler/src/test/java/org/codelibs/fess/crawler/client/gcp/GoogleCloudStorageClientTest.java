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
package org.codelibs.fess.crawler.client.gcp;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for GoogleCloudStorageClient.
 *
 * @author shinsuke
 */
public class GoogleCloudStorageClientTest extends PlainTestCase {

    public GoogleCloudStorageClient gcsClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("mimeTypeHelper", org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl.class);
        gcsClient = new GoogleCloudStorageClient();
        gcsClient.crawlerContainer = container;
    }

    @Override
    protected void tearDown() throws Exception {
        gcsClient.close();
        super.tearDown();
    }

    public void test_parsePath() {
        String[] result = gcsClient.parsePath("my-bucket/path/to/object.txt");
        assertEquals("my-bucket", result[0]);
        assertEquals("path/to/object.txt", result[1]);

        result = gcsClient.parsePath("my-bucket/object.txt");
        assertEquals("my-bucket", result[0]);
        assertEquals("object.txt", result[1]);

        result = gcsClient.parsePath("my-bucket");
        assertEquals("my-bucket", result[0]);
        assertEquals("", result[1]);
    }

    public void test_parsePath_invalid() {
        try {
            gcsClient.parsePath("");
            fail("Should throw CrawlingAccessException for empty path");
        } catch (final CrawlingAccessException e) {
            assertTrue(e.getMessage().contains("Invalid path"));
        }
    }

    public void test_normalizeUri() {
        assertEquals("gs://my-bucket/object.txt", gcsClient.normalizeUri("gs://my-bucket/object.txt"));
        assertEquals("gs://my-bucket/object.txt", gcsClient.normalizeUri("my-bucket/object.txt"));
    }

    public void test_charsetGetterSetter() {
        gcsClient.setCharset("UTF-16");
        assertEquals("UTF-16", gcsClient.getCharset());
    }
}
