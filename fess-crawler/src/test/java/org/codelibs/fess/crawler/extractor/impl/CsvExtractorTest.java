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
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for CsvExtractor.
 */
public class CsvExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(CsvExtractorTest.class);

    public CsvExtractor csvExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        final StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("csvExtractor", CsvExtractor.class);
        csvExtractor = container.getComponent("csvExtractor");
    }

    private static InputStream csv(final String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    private static InputStream bytes(final byte[] data) {
        return new ByteArrayInputStream(data);
    }

    // ----------------------------------------------------------------------
    // Existing/baseline tests
    // ----------------------------------------------------------------------

    @Test
    public void test_getText() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/csv/test.csv");
        final ExtractData extractData = csvExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();
        logger.info(content);

        // Verify header extraction
        assertTrue(content.contains("Name"));
        assertTrue(content.contains("Email"));
        assertTrue(content.contains("Age"));
        assertTrue(content.contains("Department"));

        // Verify data extraction
        assertTrue(content.contains("John Doe"));
        assertTrue(content.contains("john@example.com"));
        assertTrue(content.contains("Engineering"));
    }

    @Test
    public void test_columnMetadata() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/csv/test.csv");
        final ExtractData extractData = csvExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        // Verify column metadata
        final String[] columns = extractData.getValues("columns");
        assertNotNull(columns);
        assertEquals(4, columns.length);
        assertEquals("Name", columns[0]);
        assertEquals("Email", columns[1]);
        assertEquals("Age", columns[2]);
        assertEquals("Department", columns[3]);

        // Verify row count
        final String rowCount = extractData.getValues("row_count")[0];
        assertEquals("4", rowCount);
    }

    @Test
    public void test_getText_null() {
        try {
            csvExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // Expected
        }
    }

    @Test
    public void test_getText_withoutHeader() {
        csvExtractor.setHasHeader(false);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/csv/test.csv");
        final ExtractData extractData = csvExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();
        logger.info("Without header: " + content);

        // All lines should be treated as data
        assertTrue(content.contains("Name"));
        assertTrue(content.contains("John Doe"));
    }

    @Test
    public void test_getText_withoutColumnMetadata() {
        csvExtractor.setExtractColumnMetadata(false);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/csv/test.csv");
        final ExtractData extractData = csvExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        // Verify no column metadata
        assertNull(extractData.getValues("columns"));

        // But content should still be present
        final String content = extractData.getContent();
        assertTrue(content.contains("John Doe"));
    }

    @Test
    public void test_delimiterDetection() {
        // The test.csv uses comma delimiter
        csvExtractor.setAutoDetectDelimiter(true);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/csv/test.csv");
        final ExtractData extractData = csvExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();
        assertTrue(content.contains("John Doe"));
        assertTrue(content.contains("Engineering"));
    }

    @Test
    public void test_maxRows() {
        csvExtractor.setMaxRows(2);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/csv/test.csv");
        final ExtractData extractData = csvExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String rowCount = extractData.getValues("row_count")[0];
        // Should extract 2 data rows (header doesn't count toward maxRows)
        assertEquals("2", rowCount);
    }

    // ----------------------------------------------------------------------
    // RFC 4180 edge cases
    // ----------------------------------------------------------------------

    @Test
    public void test_simpleCsv() {
        csvExtractor.setHasHeader(false);
        final ExtractData extractData = csvExtractor.getText(csv("a,b,c\n1,2,3\n"), null);
        final String content = extractData.getContent();
        assertTrue(content.contains("a"));
        assertTrue(content.contains("b"));
        assertTrue(content.contains("c"));
        assertTrue(content.contains("1"));
        assertTrue(content.contains("2"));
        assertTrue(content.contains("3"));
        assertEquals("2", extractData.getValues("row_count")[0]);
    }

    @Test
    public void test_quotedFields() {
        csvExtractor.setHasHeader(false);
        // Two fields: a,b  and  c
        final ExtractData extractData = csvExtractor.getText(csv("\"a,b\",\"c\"\n"), null);
        final String content = extractData.getContent();
        // The embedded comma must be preserved as part of the first field.
        assertTrue(content.contains("a,b"));
        assertTrue(content.contains("c"));
        assertEquals("1", extractData.getValues("row_count")[0]);
    }

    @Test
    public void test_escapedQuotes() {
        csvExtractor.setHasHeader(false);
        // Field 1 = a"b, field 2 = c
        final ExtractData extractData = csvExtractor.getText(csv("\"a\"\"b\",\"c\"\n"), null);
        final String content = extractData.getContent();
        assertTrue(content.contains("a\"b"));
        assertTrue(content.contains("c"));
    }

    @Test
    public void test_multilineQuotedField() {
        csvExtractor.setHasHeader(false);
        // First field has an embedded newline
        final ExtractData extractData = csvExtractor.getText(csv("\"line1\nline2\",\"next\"\n"), null);
        final String content = extractData.getContent();
        assertTrue(content.contains("line1\nline2"));
        assertTrue(content.contains("next"));
        assertEquals("1", extractData.getValues("row_count")[0]);
    }

    @Test
    public void test_utf8Bom_strippedAndDecoded() {
        // EF BB BF is the UTF-8 BOM
        final byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        final byte[] body = "name,value\nfoo,bar\n".getBytes(StandardCharsets.UTF_8);
        final byte[] all = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, all, 0, bom.length);
        System.arraycopy(body, 0, all, bom.length, body.length);

        final ExtractData extractData = csvExtractor.getText(bytes(all), null);
        final String[] columns = extractData.getValues("columns");
        assertNotNull(columns);
        assertEquals(2, columns.length);
        // First column header must NOT contain the BOM
        assertEquals("name", columns[0]);
        assertEquals("value", columns[1]);
        assertTrue(extractData.getContent().contains("foo"));
    }

    @Test
    public void test_utf16LeBom_decoded() {
        // UTF-16LE BOM = FF FE
        final byte[] bom = { (byte) 0xFF, (byte) 0xFE };
        final byte[] body = "name,value\nfoo,bar\n".getBytes(StandardCharsets.UTF_16LE);
        final byte[] all = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, all, 0, bom.length);
        System.arraycopy(body, 0, all, bom.length, body.length);

        final ExtractData extractData = csvExtractor.getText(bytes(all), null);
        final String[] columns = extractData.getValues("columns");
        assertNotNull(columns);
        assertEquals(2, columns.length);
        assertEquals("name", columns[0]);
        assertEquals("value", columns[1]);
        assertTrue(extractData.getContent().contains("foo"));
    }

    @Test
    public void test_customDelimiter() {
        csvExtractor.setAutoDetectDelimiter(false);
        csvExtractor.setDelimiter('\t');
        csvExtractor.setHasHeader(false);
        final ExtractData extractData = csvExtractor.getText(csv("a\tb\tc\n1\t2\t3\n"), null);
        final String content = extractData.getContent();
        assertTrue(content.contains("a"));
        assertTrue(content.contains("b"));
        assertTrue(content.contains("c"));
        assertTrue(content.contains("1"));
        assertTrue(content.contains("2"));
        assertTrue(content.contains("3"));
        assertEquals("2", extractData.getValues("row_count")[0]);
    }

    @Test
    public void test_emptyField() {
        csvExtractor.setHasHeader(false);
        // Three fields with empty middle
        final ExtractData extractData = csvExtractor.getText(csv("a,,c\n"), null);
        final String content = extractData.getContent();
        assertTrue(content.contains("a"));
        assertTrue(content.contains("c"));
        // We can't easily detect empty in the rendered text; instead verify
        // that we got exactly one row and its rendering surfaced both
        // populated fields.
        assertEquals("1", extractData.getValues("row_count")[0]);
    }

    @Test
    public void test_maxRecords_truncates() {
        csvExtractor.setHasHeader(false);
        csvExtractor.setMaxRecords(2L);
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("row").append(i).append(",val").append(i).append('\n');
        }
        final ExtractData extractData = csvExtractor.getText(csv(sb.toString()), null);
        assertEquals("2", extractData.getValues("row_count")[0]);
    }

    @Test
    public void test_malformedCsv_throwsExtractException() {
        // Unterminated quote at EOF.
        try {
            csvExtractor.getText(csv("\"unterminated,foo\nbar"), null);
            fail();
        } catch (final ExtractException e) {
            final String msg = e.getMessage();
            assertNotNull(msg);
            assertTrue(msg.contains("CSV parse error"));
            assertTrue(msg.contains("line="));
            assertTrue(msg.contains("error="));
        }
    }
}
