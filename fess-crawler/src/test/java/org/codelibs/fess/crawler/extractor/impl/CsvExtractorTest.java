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
import org.junit.jupiter.api.BeforeEach;

/**
 * Test class for CsvExtractor.
 */
public class CsvExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(CsvExtractorTest.class);

    public CsvExtractor csvExtractor;

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        final StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("csvExtractor", CsvExtractor.class);
        csvExtractor = container.getComponent("csvExtractor");
    }

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

    public void test_getText_null() {
        try {
            csvExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // Expected
        }
    }

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

    public void test_maxRows() {
        csvExtractor.setMaxRows(2);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/csv/test.csv");
        final ExtractData extractData = csvExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String rowCount = extractData.getValues("row_count")[0];
        // Should extract 2 data rows (header doesn't count toward maxRows)
        assertEquals("2", rowCount);
    }
}
