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
import org.codelibs.fess.crawler.exception.UnsupportedExtractException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class PsExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(PsExtractorTest.class);

    public PsExtractor psExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("psExtractor", PsExtractor.class);
        psExtractor = container.getComponent("psExtractor");
    }

    @Test
    public void test_getText() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.ps");
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello World"));
        assertTrue(content.contains("test of PostScript"));
    }

    @Test
    public void test_getText_null() {
        try {
            psExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    @Test
    public void test_getText_hexString() {
        final String ps = "%!PS\n72 700 moveto\n<48656C6C6F> show\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello"));
    }

    @Test
    public void test_getText_escapedString() {
        final String ps = "%!PS\n72 700 moveto\n(Hello\\nWorld) show\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello"));
        assertTrue(content.contains("World"));
    }

    @Test
    public void test_getText_nestedParens() {
        final String ps = "%!PS\n72 700 moveto\n(Hello \\(World\\)) show\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello (World)"));
    }

    @Test
    public void test_getText_empty() {
        final String ps = "%!PS\n72 700 moveto\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        try {
            psExtractor.getText(in, null);
            fail();
        } catch (final UnsupportedExtractException e) {
            // NOP
        } finally {
            CloseableUtil.closeQuietly(in);
        }
    }

    @Test
    public void test_getText_withNameLiterals() {
        final String ps = "%!PS\n/Helvetica findfont 12 scalefont setfont\n72 700 moveto\n(Test) show\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Test"));
    }

    @Test
    public void test_getText_withDictionary() {
        final String ps = "%!PS\n<< /PageSize [612 792] >> setpagedevice\n72 700 moveto\n(Dict Test) show\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Dict Test"));
    }

    @Test
    public void test_getText_withArray() {
        final String ps = "%!PS\n72 700 moveto [1 0 0 1 0 0] concat\n(Array Test) show\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Array Test"));
    }

    @Test
    public void test_getText_withProcedure() {
        final String ps = "%!PS\n/myproc { 72 700 moveto (Proc Test) show } def\nmyproc\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Proc Test"));
    }

    @Test
    public void test_getText_complexPs() {
        final String ps = "%!PS-Adobe-3.0\n/Courier findfont 12 scalefont setfont\n" + "<< /PageSize [612 792] >> setpagedevice\n"
                + "72 720 moveto (Hello) show\n" + "/myproc { 72 700 moveto (World) show } def\n" + "myproc\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello"));
        assertTrue(content.contains("World"));
    }

    @Test
    public void test_getText_delimitersOnly() {
        final String ps = "/ [ ] { } < > << >> )";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        try {
            psExtractor.getText(in, null);
            fail();
        } catch (final UnsupportedExtractException e) {
            // NOP
        } finally {
            CloseableUtil.closeQuietly(in);
        }
    }

    @Test
    public void test_getText_ashow() {
        final String ps = "%!PS\n72 700 moveto\n0 0 (Hello ashow) ashow\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello ashow"));
    }

    @Test
    public void test_getText_widthshow() {
        final String ps = "%!PS\n72 700 moveto\n0 0 32 (Hello widthshow) widthshow\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello widthshow"));
    }

    @Test
    public void test_getText_awidthshow() {
        final String ps = "%!PS\n72 700 moveto\n0 0 32 0 0 (Hello awidthshow) awidthshow\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello awidthshow"));
    }

    @Test
    public void test_getText_xshow() {
        final String ps = "%!PS\n72 700 moveto\n(Hello xshow) [10 20 30 40 50 60 70 80 90 100 110] xshow\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello xshow"));
    }

    @Test
    public void test_getText_yshow() {
        final String ps = "%!PS\n72 700 moveto\n(Hello yshow) [0 0 0 0 0 0 0 0 0 0 0] yshow\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello yshow"));
    }

    @Test
    public void test_getText_xyshow() {
        final String ps =
                "%!PS\n72 700 moveto\n(Hello xyshow) [10 0 20 0 30 0 40 0 50 0 60 0 70 0 80 0 90 0 100 0 110 0] xyshow\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello xyshow"));
    }

    @Test
    public void test_getText_kshow() {
        final String ps = "%!PS\n72 700 moveto\n{} (Hello kshow) kshow\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello kshow"));
    }

    @Test
    public void test_getText_exponentialNumbers() {
        final String ps = "%!PS\n72 700 moveto\n(Hello exp) [1e10 1.5E-3 6.023E23 2e1 3.0e+2] xshow\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        final String content = psExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("Hello exp"));
    }

    @Test
    public void test_getText_incompleteLessThan() {
        final String ps = "%!PS\n<";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        try {
            psExtractor.getText(in, null);
            fail();
        } catch (final UnsupportedExtractException e) {
            // NOP
        } finally {
            CloseableUtil.closeQuietly(in);
        }
    }

    @Test
    public void test_getText_truncatesAtMaxTextLength() {
        // A pathological/oversized PostScript stream must be bounded rather than read
        // entirely into memory. Build a stream far larger than a small cap and verify the
        // extractor stops well short of consuming/decoding the whole thing.
        psExtractor.setMaxTextLength(200);
        final StringBuilder ps = new StringBuilder("%!PS\n72 700 moveto\n");
        for (int i = 0; i < 100_000; i++) {
            ps.append("(Hello World ").append(i).append(") show\n");
        }
        ps.append("showpage\n");
        final InputStream in = new ByteArrayInputStream(ps.toString().getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = psExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        // The raw (pre-parse) character count fed into extractText is capped at 200 chars,
        // so the resulting extracted text must be far smaller than the ~2MB source.
        assertTrue(extractData.getContent().length() < 200);
        final String[] truncated = extractData.getValues("truncated");
        assertNotNull(truncated);
        assertEquals("true", truncated[0]);
        final String[] maxLen = extractData.getValues("maxTextLength");
        assertNotNull(maxLen);
        assertEquals("200", maxLen[0]);
    }

    @Test
    public void test_getText_maxTextLengthDoesNotAffectNormalInput() {
        // Default maxTextLength (unlimited) must not change output for a normal-sized file.
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test.ps");
        final ExtractData extractData = psExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertTrue(extractData.getContent().contains("Hello World"));
        assertTrue(extractData.getContent().contains("test of PostScript"));
        assertNull(extractData.getValues("truncated"));
    }

    @Test
    public void test_getText_maxTextLength_exceedsCapButNoShowTextFound() {
        // When truncation lands before any show-family operator, the (now-shorter) content
        // still yields no extractable text and must throw the same exception as an empty
        // document, not silently succeed with a different result shape.
        psExtractor.setMaxTextLength(5);
        final String ps = "%!PS\n72 700 moveto\n(Hello World) show\nshowpage\n";
        final InputStream in = new ByteArrayInputStream(ps.getBytes(StandardCharsets.UTF_8));
        try {
            psExtractor.getText(in, null);
            fail();
        } catch (final UnsupportedExtractException e) {
            // NOP
        } finally {
            CloseableUtil.closeQuietly(in);
        }
    }
}
