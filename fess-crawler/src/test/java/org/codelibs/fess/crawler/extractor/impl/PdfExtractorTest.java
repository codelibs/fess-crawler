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

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class PdfExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(PdfExtractorTest.class);

    public PdfExtractor pdfExtractor;

    private PdfExtractor pdfExtractorForPdfPassword;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("pdfExtractor", PdfExtractor.class)//
                .singleton("pdfExtractorForPdfPassword", PdfExtractor.class)//
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("tikaExtractor", TikaExtractor.class)//
                .<ExtractorFactory> singleton("extractorFactory", ExtractorFactory.class, factory -> {
                    TikaExtractor tikaExtractor = container.getComponent("tikaExtractor");
                    PdfExtractor pdfExtractor = container.getComponent("pdfExtractor");
                    factory.addExtractor("text/plain", tikaExtractor);
                    factory.addExtractor("text/html", tikaExtractor);
                    factory.addExtractor("application/pdf", pdfExtractor);
                });
        pdfExtractor = container.getComponent("pdfExtractor");
        pdfExtractorForPdfPassword = container.getComponent("pdfExtractorForPdfPassword");
        pdfExtractorForPdfPassword.addPassword(".*test_.*.pdf", "word");
    }

    @Test
    public void test_getText() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.pdf");
        final ExtractData extractData = pdfExtractor.getText(in, null);
        final String content = extractData.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertEquals("Writer", extractData.getValues("Creator")[0]);
        assertEquals("OpenOffice.org 3.0", extractData.getValues("Producer")[0]);
        assertEquals("D:20090627222631+09'00'", extractData.getValues("CreationDate")[0]);
    }

    @Test
    public void test_getText_pass() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_pass.pdf");
        final Map<String, String> params = new HashMap<String, String>();
        params.put(ExtractData.URL, "http://example.com/test_pass.pdf");
        final String content = pdfExtractorForPdfPassword.getText(in, params).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    @Test
    public void test_getText_null() {
        try {
            pdfExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    @Test
    public void test_getPassword_null() {
        String url;
        String resourceName;
        HashMap<String, String> params = new HashMap<>();

        url = null;
        resourceName = null;
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        resourceName = null;
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        resourceName = "hoge2.pdf";
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = null;
        resourceName = "hoge2.pdf";
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));
    }

    @Test
    public void test_getPassword() {
        String url;
        String resourceName;
        pdfExtractor.addPassword(".*hoge1.pdf", "password");
        pdfExtractor.addPassword("fuga.pdf", "PASSWORD");
        HashMap<String, String> params = new HashMap<>();

        url = null;
        resourceName = null;
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        resourceName = null;
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertEquals("password", pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        resourceName = "hoge2.pdf";
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertEquals("password", pdfExtractor.getPassword(params));

        url = null;
        resourceName = "hoge2.pdf";
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = null;
        resourceName = "hoge1.pdf";
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertEquals("password", pdfExtractor.getPassword(params));

        url = "http://test.com/fuga.pdf";
        resourceName = null;
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertNull(pdfExtractor.getPassword(params));

        url = null;
        resourceName = "fuga.pdf";
        params.put(ExtractData.URL, url);
        params.put(ExtractData.RESOURCE_NAME_KEY, resourceName);
        assertEquals("PASSWORD", pdfExtractor.getPassword(params));
    }

    @Test
    public void test_getPassword_json() {
        String url;
        Map<String, String> params = new HashMap<>();
        params.put(ExtractData.FILE_PASSWORDS, "{\".*hoge1.pdf\":\"password\",\"fuga.pdf\":\"PASSWORD\"}");

        url = null;
        params.put(ExtractData.URL, url);
        assertNull(pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        params.put(ExtractData.URL, url);
        assertEquals("password", pdfExtractor.getPassword(params));

        url = "http://test.com/hoge1.pdf";
        params.put(ExtractData.URL, url);
        assertEquals("password", pdfExtractor.getPassword(params));

        url = "http://test.com/fuga.pdf";
        params.put(ExtractData.URL, url);
        assertNull(pdfExtractor.getPassword(params));
    }

    /**
     * A {@link PDFTextStripper} that blocks until interrupted, used to simulate a
     * PDFBox call that does not finish within the configured timeout.
     */
    private static class SleepingStripper extends PDFTextStripper {
        private final long sleepMs;
        private final AtomicReference<Boolean> interrupted = new AtomicReference<>(Boolean.FALSE);

        SleepingStripper(final long sleepMs) throws IOException {
            super();
            this.sleepMs = sleepMs;
        }

        @Override
        public void writeText(final PDDocument doc, final Writer outputStream) throws IOException {
            try {
                Thread.sleep(sleepMs);
            } catch (final InterruptedException e) {
                interrupted.set(Boolean.TRUE);
                Thread.currentThread().interrupt();
                throw new IOException("interrupted", e);
            }
        }

        boolean wasInterrupted() {
            return Boolean.TRUE.equals(interrupted.get());
        }
    }

    @Test
    public void test_extractionTimeout_throwsExtractException() {
        final SleepingStripper sleepingStripper;
        try {
            sleepingStripper = new SleepingStripper(60_000L);
        } catch (final IOException e) {
            throw new AssertionError("Failed to construct SleepingStripper: " + e.getMessage(), e);
        }

        final PdfExtractor slowExtractor = new PdfExtractor() {
            @Override
            protected PDFTextStripper createStripper() throws IOException {
                return sleepingStripper;
            }
        };
        slowExtractor.setTimeout(100L);
        slowExtractor.setCancelGracePeriodMs(2000L);

        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.pdf");
        ExtractException caught = null;
        try {
            slowExtractor.getText(in, null);
        } catch (final ExtractException e) {
            caught = e;
        } finally {
            CloseableUtil.closeQuietly(in);
        }
        assertNotNull(caught);
        final String msg = caught.getMessage();
        assertNotNull(msg);
        assertTrue(msg.contains("PDFBox"));
        assertTrue(msg.contains("100"));

        // The worker should have been interrupted by future.cancel(true).
        assertTrue(sleepingStripper.wasInterrupted());
    }

    @Test
    public void test_extractionCancellation_releasesThread() {
        final SleepingStripper sleepingStripper;
        try {
            sleepingStripper = new SleepingStripper(60_000L);
        } catch (final IOException e) {
            throw new AssertionError("Failed to construct SleepingStripper: " + e.getMessage(), e);
        }

        final AtomicReference<Boolean> useSleeping = new AtomicReference<>(Boolean.TRUE);
        final PdfExtractor extractor = new PdfExtractor() {
            @Override
            protected PDFTextStripper createStripper() throws IOException {
                if (Boolean.TRUE.equals(useSleeping.get())) {
                    return sleepingStripper;
                }
                return super.createStripper();
            }
        };
        extractor.setTimeout(200L);
        extractor.setCancelGracePeriodMs(2000L);

        // First call should time out and cancel cleanly.
        final InputStream in1 = ResourceUtil.getResourceAsStream("extractor/test.pdf");
        ExtractException firstError = null;
        try {
            extractor.getText(in1, null);
        } catch (final ExtractException expected) {
            firstError = expected;
        } finally {
            CloseableUtil.closeQuietly(in1);
        }
        assertNotNull(firstError);

        // Second call (using a normal stripper, generous timeout) on the same instance
        // must succeed, proving the executor was shut down and no resources are leaking.
        useSleeping.set(Boolean.FALSE);
        extractor.setTimeout(30_000L);
        final InputStream in2 = ResourceUtil.getResourceAsStream("extractor/test.pdf");
        try {
            final ExtractData data = extractor.getText(in2, null);
            assertNotNull(data);
            assertNotNull(data.getContent());
            assertTrue(data.getContent().contains("テスト"));
        } finally {
            CloseableUtil.closeQuietly(in2);
        }
    }

    @Test
    public void test_extractionInterrupt_propagates() throws Exception {
        final SleepingStripper sleepingStripper = new SleepingStripper(60_000L);
        final PdfExtractor extractor = new PdfExtractor() {
            @Override
            protected PDFTextStripper createStripper() throws IOException {
                return sleepingStripper;
            }
        };
        extractor.setTimeout(30_000L);
        extractor.setCancelGracePeriodMs(2000L);

        final AtomicReference<Throwable> caught = new AtomicReference<>();
        final AtomicReference<Boolean> interruptFlagAfter = new AtomicReference<>(Boolean.FALSE);
        final Thread runner = new Thread(() -> {
            final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.pdf");
            try {
                extractor.getText(in, null);
            } catch (final Throwable t) {
                caught.set(t);
            } finally {
                interruptFlagAfter.set(Thread.currentThread().isInterrupted());
                CloseableUtil.closeQuietly(in);
            }
        }, "pdf-interrupt-test");
        runner.setDaemon(true);
        runner.start();

        // Give the runner time to enter Future.get(...).
        Thread.sleep(500L);
        runner.interrupt();
        runner.join(10_000L);

        assertFalse(runner.isAlive());
        final Throwable t = caught.get();
        assertNotNull(t);
        assertTrue(t instanceof ExtractException);
        assertTrue(Boolean.TRUE.equals(interruptFlagAfter.get()));
    }
}
