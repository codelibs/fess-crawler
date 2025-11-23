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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for JsonExtractor.
 */
public class JsonExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(JsonExtractorTest.class);

    public JsonExtractor jsonExtractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("jsonExtractor", JsonExtractor.class);
        jsonExtractor = container.getComponent("jsonExtractor");
    }

    public void test_getText() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();
        logger.info(content);

        // Verify content contains expected fields
        assertTrue(content.contains("title"));
        assertTrue(content.contains("Sample Document"));
        assertTrue(content.contains("author"));
        assertTrue(content.contains("John Doe"));

        // Verify metadata extraction
        assertNotNull(extractData.getValues("title"));
        assertEquals("Sample Document", extractData.getValues("title")[0]);
        assertNotNull(extractData.getValues("author"));
        assertEquals("John Doe", extractData.getValues("author")[0]);
    }

    public void test_getText_nested() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();

        // Verify nested content extraction
        assertTrue(content.contains("content.summary"));
        assertTrue(content.contains("This is a sample JSON document for testing"));
    }

    public void test_getText_array() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();

        // Verify array element extraction
        assertTrue(content.contains("tags[0]"));
        assertTrue(content.contains("crawler"));
    }

    public void test_getText_null() {
        try {
            jsonExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // Expected
        }
    }

    public void test_getText_withoutMetadata() {
        jsonExtractor.setExtractMetadata(false);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        // Verify no metadata extracted
        assertNull(extractData.getValues("title"));
        assertNull(extractData.getValues("author"));

        // But content should still be present
        final String content = extractData.getContent();
        assertTrue(content.contains("Sample Document"));
    }

    public void test_maxDepth() {
        jsonExtractor.setMaxDepth(1);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();
        logger.info("Limited depth content: " + content);

        // With depth 1, nested content should not be deeply processed
        assertNotNull(content);
    }
}
