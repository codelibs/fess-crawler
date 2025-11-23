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
package org.codelibs.fess.crawler.client.aws;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for AwsS3Client.
 *
 * @author shinsuke
 */
public class AwsS3ClientTest extends PlainTestCase {

    public AwsS3Client awsS3Client;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("mimeTypeHelper", org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl.class);
        awsS3Client = new AwsS3Client();
        awsS3Client.crawlerContainer = container;
    }

    @Override
    protected void tearDown() throws Exception {
        awsS3Client.close();
        super.tearDown();
    }

    public void test_parsePath() {
        String[] result = awsS3Client.parsePath("my-bucket/path/to/object.txt");
        assertEquals("my-bucket", result[0]);
        assertEquals("path/to/object.txt", result[1]);

        result = awsS3Client.parsePath("my-bucket/object.txt");
        assertEquals("my-bucket", result[0]);
        assertEquals("object.txt", result[1]);

        result = awsS3Client.parsePath("my-bucket");
        assertEquals("my-bucket", result[0]);
        assertEquals("", result[1]);
    }

    public void test_parsePath_invalid() {
        try {
            awsS3Client.parsePath("");
            fail("Should throw CrawlingAccessException for empty path");
        } catch (final CrawlingAccessException e) {
            assertTrue(e.getMessage().contains("Invalid path"));
        }
    }

    public void test_normalizeUri() {
        assertEquals("s3://my-bucket/object.txt", awsS3Client.normalizeUri("s3://my-bucket/object.txt"));
        assertEquals("s3://my-bucket/object.txt", awsS3Client.normalizeUri("my-bucket/object.txt"));
    }

    public void test_charsetGetterSetter() {
        awsS3Client.setCharset("UTF-16");
        assertEquals("UTF-16", awsS3Client.getCharset());
    }
}
