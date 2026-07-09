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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for JsonExtractor.
 */
public class JsonExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(JsonExtractorTest.class);

    public JsonExtractor jsonExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        final StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("jsonExtractor", JsonExtractor.class);
        jsonExtractor = container.getComponent("jsonExtractor");
    }

    @Test
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

    @Test
    public void test_getText_nested() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();

        // Verify nested content extraction
        assertTrue(content.contains("content.summary"));
        assertTrue(content.contains("This is a sample JSON document for testing"));
    }

    @Test
    public void test_getText_array() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();

        // Verify array element extraction
        assertTrue(content.contains("tags[0]"));
        assertTrue(content.contains("crawler"));
    }

    @Test
    public void test_getText_null() {
        try {
            jsonExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // Expected
        }
    }

    @Test
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

    @Test
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

    @Test
    public void test_getText_normalInputUnaffectedByBounds() {
        // Default bounds (tightened StreamReadConstraints; unlimited maxTextLength) must not
        // change output for the existing normal-sized fixture.
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        assertTrue(extractData.getContent().contains("Sample Document"));
        assertEquals("Sample Document", extractData.getValues("title")[0]);
        assertNull(extractData.getValues("truncated"));
    }

    @Test
    public void test_getText_truncatesAtMaxTextLength() {
        // A JSON object with a very large number of top-level fields (unbounded by
        // maxArrayElements, which only caps arrays) must not grow the accumulated output
        // text without bound; maxTextLength must cap it, mirroring TextExtractor/
        // MarkdownExtractor's truncate-not-reject convention.
        jsonExtractor.setMaxTextLength(50);
        final StringBuilder json = new StringBuilder("{");
        for (int i = 0; i < 100_000; i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append("\"field").append(i).append("\":\"value").append(i).append('"');
        }
        json.append('}');
        final InputStream in = new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        assertTrue(extractData.getContent().length() <= 50);
        final String[] truncated = extractData.getValues("truncated");
        assertNotNull(truncated);
        assertEquals("true", truncated[0]);
        final String[] maxLen = extractData.getValues("maxTextLength");
        assertNotNull(maxLen);
        assertEquals("50", maxLen[0]);
    }

    @Test
    public void test_getText_maxTextLength_zero_isUnlimited() {
        // maxTextLength=0 means unlimited, matching TextExtractor's convention.
        jsonExtractor.setMaxTextLength(0);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertNull(extractData.getValues("truncated"));
        assertTrue(extractData.getContent().contains("Sample Document"));
    }

    @Test
    public void test_getText_deeplyNestedJsonIsRejected() {
        // The shared ObjectMapper is configured with StreamReadConstraints.maxNestingDepth
        // pinned to Jackson's own default (1000), so a document with pathological nesting
        // that exceeds even that generous ceiling must fail fast at parse time rather than
        // risking stack/heap exhaustion by materializing an arbitrarily deep tree.
        final int depth = 2000;
        final StringBuilder json = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            json.append("{\"a\":");
        }
        json.append('1');
        for (int i = 0; i < depth; i++) {
            json.append('}');
        }
        final InputStream in = new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
        try {
            jsonExtractor.getText(in, null);
            fail();
        } catch (final ExtractException e) {
            // Expected: StreamReadConstraints.maxNestingDepth rejects the pathological input.
        } finally {
            CloseableUtil.closeQuietly(in);
        }
    }

    @Test
    public void test_getText_legitimatelyDeepJsonIsNotRejected() {
        // A nesting depth of ~500 is well under Jackson's default maxNestingDepth (1000), so
        // it must parse successfully just like it would with a default-configured Jackson
        // ObjectMapper on master. This would have FAILED when MAX_NESTING_DEPTH was tightened
        // to 64, and guards against re-tightening the parse-time constraint below Jackson's
        // own default. Each level carries a scalar "value" field alongside the nested
        // "nested" object so the extractor's own maxDepth=10 output traversal (a separate,
        // intentional bound on the recursive text walk) still yields non-empty content.
        final int depth = 500;
        final StringBuilder json = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            json.append("{\"value\":").append(i).append(",\"nested\":");
        }
        json.append("null");
        for (int i = 0; i < depth; i++) {
            json.append('}');
        }
        final InputStream in = new ByteArrayInputStream(json.toString().getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData;
        try {
            extractData = jsonExtractor.getText(in, null);
        } finally {
            CloseableUtil.closeQuietly(in);
        }

        final String content = extractData.getContent();
        assertNotNull(content);
        assertFalse(content.isEmpty());
        // The output-side maxDepth=10 traversal bound (independent of parse-time
        // StreamReadConstraints) means only the first few levels' "value" fields are walked.
        assertTrue(content.contains("value: 0"));
        assertNull(extractData.getValues("truncated"));
    }

    @Test
    public void test_getText_maxContentLength_rejectsOversizedInput() {
        // Input clearly larger than the 10-byte cap must be rejected (with
        // MaxLengthExceededException) before the JsonNode tree is materialized.
        jsonExtractor.setMaxContentLength(10);
        final InputStream in = new ByteArrayInputStream("{\"key\":\"aaaaaaaaaaaaaaaaaaaa\"}".getBytes(StandardCharsets.UTF_8));
        try {
            jsonExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("input size exceeded limit"));
        } finally {
            CloseableUtil.closeQuietly(in);
        }
    }

    @Test
    public void test_getText_maxContentLength_underCapExtractsNormally() {
        // A cap well above the fixture size must not change extraction.
        jsonExtractor.setMaxContentLength(1_000_000);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        assertFalse(extractData.getContent().isEmpty());
        assertTrue(extractData.getContent().contains("Sample Document"));
        assertNull(extractData.getValues("truncated"));
    }

    @Test
    public void test_getText_maxContentLength_defaultUnlimited() {
        // Default (unset, 0) preserves the previous unbounded behavior for a normal fixture.
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/json/test.json");
        final ExtractData extractData = jsonExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        assertTrue(extractData.getContent().contains("Sample Document"));
        assertEquals("Sample Document", extractData.getValues("title")[0]);
    }
}
