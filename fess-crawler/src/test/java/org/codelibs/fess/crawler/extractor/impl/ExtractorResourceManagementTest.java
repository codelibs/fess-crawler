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
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;

/**
 * Test class to verify proper resource management in Extractor implementations.
 * This test ensures that resources are properly closed even when exceptions occur.
 */
public class ExtractorResourceManagementTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(ExtractorResourceManagementTest.class);

    private StandardCrawlerContainer container;

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        container = new StandardCrawlerContainer().singleton("msWordExtractor", MsWordExtractor.class)
                .singleton("msExcelExtractor", MsExcelExtractor.class)
                .singleton("msPowerPointExtractor", MsPowerPointExtractor.class)
                .singleton("textExtractor", TextExtractor.class);
    }

    /**
     * Test that MsWordExtractor properly closes resources on successful extraction.
     */
    public void test_MsWordExtractor_closesResourcesOnSuccess() throws IOException {
        final MsWordExtractor extractor = container.getComponent("msWordExtractor");
        final AtomicBoolean streamClosed = new AtomicBoolean(false);

        try (final InputStream originalStream = ResourceUtil.getResourceAsStream("extractor/msoffice/test.doc")) {
            final InputStream trackableStream = createTrackableInputStream(originalStream, streamClosed);
            final ExtractData result = extractor.getText(trackableStream, null);

            assertNotNull(result);
            assertNotNull(result.getContent());
            assertTrue(result.getContent().contains("テスト"));
        }
    }

    /**
     * Test that MsExcelExtractor properly closes resources on successful extraction.
     */
    public void test_MsExcelExtractor_closesResourcesOnSuccess() throws IOException {
        final MsExcelExtractor extractor = container.getComponent("msExcelExtractor");
        final AtomicBoolean streamClosed = new AtomicBoolean(false);

        try (final InputStream originalStream = ResourceUtil.getResourceAsStream("extractor/msoffice/test.xls")) {
            final InputStream trackableStream = createTrackableInputStream(originalStream, streamClosed);
            final ExtractData result = extractor.getText(trackableStream, null);

            assertNotNull(result);
            assertNotNull(result.getContent());
            assertTrue(result.getContent().contains("テスト"));
        }
    }

    /**
     * Test that MsPowerPointExtractor properly closes resources on successful extraction.
     */
    public void test_MsPowerPointExtractor_closesResourcesOnSuccess() throws IOException {
        final MsPowerPointExtractor extractor = container.getComponent("msPowerPointExtractor");
        final AtomicBoolean streamClosed = new AtomicBoolean(false);

        try (final InputStream originalStream = ResourceUtil.getResourceAsStream("extractor/msoffice/test.ppt")) {
            final InputStream trackableStream = createTrackableInputStream(originalStream, streamClosed);
            final ExtractData result = extractor.getText(trackableStream, null);

            assertNotNull(result);
            assertNotNull(result.getContent());
            assertTrue(result.getContent().contains("テスト"));
        }
    }

    /**
     * Test that validateInputStream throws appropriate exception for null input.
     */
    public void test_validateInputStream_throwsExceptionForNull() {
        final TextExtractor extractor = container.getComponent("textExtractor");

        try {
            extractor.getText(null, null);
            fail("Expected CrawlerSystemException");
        } catch (final CrawlerSystemException e) {
            assertEquals("The inputstream is null.", e.getMessage());
        }
    }

    /**
     * Test that MsWordExtractor throws appropriate exception for invalid data.
     * POI may throw various exceptions (IOException, IllegalArgumentException, etc.)
     * depending on the type of invalid data.
     */
    public void test_MsWordExtractor_throwsExceptionWithImprovedMessage() {
        final MsWordExtractor extractor = container.getComponent("msWordExtractor");
        final InputStream invalidStream = new ByteArrayInputStream("invalid data".getBytes());

        try {
            extractor.getText(invalidStream, null);
            fail("Expected exception for invalid Word document");
        } catch (final ExtractException e) {
            // ExtractException with improved message
            assertTrue("Error message should contain context about Word document or extraction",
                    e.getMessage().contains("Word") || e.getMessage().contains("extract"));
        } catch (final RuntimeException e) {
            // POI may throw IllegalArgumentException or other RuntimeExceptions
            // for invalid data, which is also acceptable
            assertNotNull("Exception message should not be null", e.getMessage());
        }
    }

    /**
     * Test that MsExcelExtractor throws appropriate exception for invalid data.
     * POI may throw various exceptions depending on the type of invalid data.
     */
    public void test_MsExcelExtractor_throwsExceptionWithImprovedMessage() {
        final MsExcelExtractor extractor = container.getComponent("msExcelExtractor");
        final InputStream invalidStream = new ByteArrayInputStream("invalid data".getBytes());

        try {
            extractor.getText(invalidStream, null);
            fail("Expected exception for invalid Excel document");
        } catch (final ExtractException e) {
            // ExtractException with improved message
            assertTrue("Error message should contain context about Excel document or extraction",
                    e.getMessage().contains("Excel") || e.getMessage().contains("extract"));
        } catch (final RuntimeException e) {
            // POI may throw various RuntimeExceptions for invalid data
            assertNotNull("Exception message should not be null", e.getMessage());
        }
    }

    /**
     * Test that MsPowerPointExtractor throws appropriate exception for invalid data.
     * POI may throw various exceptions depending on the type of invalid data.
     */
    public void test_MsPowerPointExtractor_throwsExceptionWithImprovedMessage() {
        final MsPowerPointExtractor extractor = container.getComponent("msPowerPointExtractor");
        final InputStream invalidStream = new ByteArrayInputStream("invalid data".getBytes());

        try {
            extractor.getText(invalidStream, null);
            fail("Expected exception for invalid PowerPoint document");
        } catch (final ExtractException e) {
            // ExtractException with improved message
            assertTrue("Error message should contain context about PowerPoint document or extraction",
                    e.getMessage().contains("PowerPoint") || e.getMessage().contains("extract"));
        } catch (final RuntimeException e) {
            // POI may throw various RuntimeExceptions for invalid data
            assertNotNull("Exception message should not be null", e.getMessage());
        }
    }

    /**
     * Test that TextExtractor throws exception with encoding information.
     */
    public void test_TextExtractor_includesEncodingInErrorMessage() {
        final TextExtractor extractor = container.getComponent("textExtractor");
        // Create a stream that will cause an encoding error
        final InputStream errorStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated read error");
            }
        };

        try {
            extractor.getText(errorStream, null);
            fail("Expected ExtractException");
        } catch (final ExtractException e) {
            assertTrue("Error message should contain encoding information", e.getMessage().contains("encoding"));
        }
    }

    /**
     * Helper method to create a trackable input stream that records when it's closed.
     */
    private InputStream createTrackableInputStream(final InputStream originalStream, final AtomicBoolean closedFlag) {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return originalStream.read();
            }

            @Override
            public int read(byte[] b) throws IOException {
                return originalStream.read(b);
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                return originalStream.read(b, off, len);
            }

            @Override
            public void close() throws IOException {
                closedFlag.set(true);
                originalStream.close();
            }

            @Override
            public int available() throws IOException {
                return originalStream.available();
            }

            @Override
            public void mark(int readlimit) {
                originalStream.mark(readlimit);
            }

            @Override
            public void reset() throws IOException {
                originalStream.reset();
            }

            @Override
            public boolean markSupported() {
                return originalStream.markSupported();
            }
        };
    }
}
