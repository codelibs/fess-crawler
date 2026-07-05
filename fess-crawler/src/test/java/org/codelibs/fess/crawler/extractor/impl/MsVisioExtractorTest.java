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
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test for MsVisioExtractor.
 */
public class MsVisioExtractorTest extends PlainTestCase {

    private MsVisioExtractor msVisioExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("msVisioExtractor", MsVisioExtractor.class);
        msVisioExtractor = container.getComponent("msVisioExtractor");
    }

    @Test
    public void test_nullInput_throwsCrawlerSystemException() {
        try {
            msVisioExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void test_corruptedInput_throwsExtractException() {
        // Random bytes are not a valid Visio document; the extractor must
        // surface this as an ExtractException rather than leaking the
        // underlying POI resources.
        final byte[] randomBytes = new byte[4096];
        new Random(42L).nextBytes(randomBytes);
        final InputStream in = new ByteArrayInputStream(randomBytes);
        try {
            msVisioExtractor.getText(in, null);
            fail();
        } catch (final ExtractException e) {
            // Expected - random bytes are not a valid Visio file
        }
    }

    @Test
    public void test_textInput_throwsExtractException() {
        // Plain text bytes are not a valid Visio document.
        final InputStream in = new ByteArrayInputStream("This is not a valid Visio file".getBytes());
        try {
            msVisioExtractor.getText(in, null);
            fail();
        } catch (final ExtractException e) {
            // Expected - invalid Visio format
        }
    }

    @Test
    public void test_emptyInput_throwsExtractException() {
        final InputStream in = new ByteArrayInputStream(new byte[0]);
        try {
            msVisioExtractor.getText(in, null);
            fail();
        } catch (final ExtractException e) {
            // Expected - empty stream is not a valid Visio file
        }
    }

    @Test
    public void test_corruptedInput_closesUnderlyingStream() throws IOException {
        // Verify that even when extraction fails, the underlying input stream
        // is closed by the try-with-resources block (no resource leak).
        final byte[] randomBytes = new byte[4096];
        new Random(7L).nextBytes(randomBytes);
        final CloseTrackingInputStream tracking = new CloseTrackingInputStream(new ByteArrayInputStream(randomBytes));
        try {
            msVisioExtractor.getText(tracking, null);
            fail();
        } catch (final ExtractException e) {
            // Expected
        }
        assertTrue(tracking.isClosed());
    }

    @Test
    public void test_constructor() {
        // Verify the extractor can be instantiated
        final MsVisioExtractor extractor = new MsVisioExtractor();
        assertNotNull(extractor);
    }

    /**
     * Test helper that records whether {@link #close()} has been invoked.
     */
    private static final class CloseTrackingInputStream extends FilterInputStream {
        private boolean closed;

        CloseTrackingInputStream(final InputStream in) {
            super(in);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }

        boolean isClosed() {
            return closed;
        }
    }
}
