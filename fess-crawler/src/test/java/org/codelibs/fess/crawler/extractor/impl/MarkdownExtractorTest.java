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
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for MarkdownExtractor.
 */
public class MarkdownExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(MarkdownExtractorTest.class);

    public MarkdownExtractor markdownExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        final StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("markdownExtractor", MarkdownExtractor.class);
        markdownExtractor = container.getComponent("markdownExtractor");
    }

    @Test
    public void test_getText() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/markdown/test.md");
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();
        logger.info(content);

        // Verify plain text extraction
        assertTrue(content.contains("Introduction"));
        assertTrue(content.contains("This is a sample Markdown document"));
        assertTrue(content.contains("Features"));
        assertTrue(content.contains("Code Examples"));
    }

    @Test
    public void test_frontMatterExtraction() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/markdown/test.md");
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        // Verify front matter metadata
        final String[] titles = extractData.getValues("frontmatter.title");
        assertNotNull(titles);
        assertEquals("Sample Markdown Document", titles[0]);

        final String[] authors = extractData.getValues("frontmatter.author");
        assertNotNull(authors);
        assertEquals("John Doe", authors[0]);

        final String[] dates = extractData.getValues("frontmatter.date");
        assertNotNull(dates);
        assertEquals("2025-01-15", dates[0]);
    }

    @Test
    public void test_headingExtraction() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/markdown/test.md");
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        // Verify heading metadata
        final String[] headings = extractData.getValues("headings");
        assertNotNull(headings);
        assertTrue(headings.length > 0);

        boolean foundIntroduction = false;
        boolean foundFeatures = false;
        for (final String heading : headings) {
            if (heading.contains("Introduction")) {
                foundIntroduction = true;
            }
            if (heading.contains("Features")) {
                foundFeatures = true;
            }
        }
        assertTrue(foundIntroduction);
        assertTrue(foundFeatures);
    }

    @Test
    public void test_linkExtraction() {
        markdownExtractor.setExtractLinks(true);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/markdown/test.md");
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        // Verify link metadata
        final String[] links = extractData.getValues("links");
        assertNotNull(links);
        assertTrue(links.length > 0);

        boolean foundGitHubLink = false;
        for (final String link : links) {
            if (link.contains("github.com/codelibs/fess-crawler")) {
                foundGitHubLink = true;
            }
        }
        assertTrue(foundGitHubLink);
    }

    @Test
    public void test_getText_null() {
        try {
            markdownExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // Expected
        }
    }

    @Test
    public void test_getText_withoutFrontMatter() {
        markdownExtractor.setExtractFrontMatter(false);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/markdown/test.md");
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        // Verify no front matter metadata
        assertNull(extractData.getValues("frontmatter.title"));
        assertNull(extractData.getValues("frontmatter.author"));

        // But content should still be present
        final String content = extractData.getContent();
        assertTrue(content.contains("Introduction"));
    }

    @Test
    public void test_getText_withoutHeadings() {
        markdownExtractor.setExtractHeadings(false);
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/markdown/test.md");
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        // Verify no heading metadata
        assertNull(extractData.getValues("headings"));

        // But content should still be present
        final String content = extractData.getContent();
        assertTrue(content.contains("Introduction"));
    }

    @Test
    public void test_codeBlockHandling() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/markdown/test.md");
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();

        // Code blocks should be converted to plain text
        assertTrue(content.contains("public class Example") || content.contains("Example"));
    }

    @Test
    public void test_extractsBody() {
        final String md = "# Title\n\nHello world body.";
        final InputStream in = new ByteArrayInputStream(md.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String content = extractData.getContent();
        assertTrue(content.contains("Title"));
        assertTrue(content.contains("Hello world body."));
    }

    @Test
    public void test_extractsYamlFrontMatter() {
        final String md = "---\n" + "title: My Title\n" + "author: Alice\n" + "tags: [one, two]\n" + "---\n\n" + "# Heading\n\nBody.";
        final InputStream in = new ByteArrayInputStream(md.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String[] titles = extractData.getValues("frontmatter.title");
        assertNotNull(titles);
        assertEquals("My Title", titles[0]);

        final String[] authors = extractData.getValues("frontmatter.author");
        assertNotNull(authors);
        assertEquals("Alice", authors[0]);

        final String content = extractData.getContent();
        assertTrue(content.contains("Heading"));
        assertTrue(content.contains("Body."));
    }

    @Test
    public void test_handlesNoFrontMatter() {
        final String md = "Just some plain markdown without front matter.\n\n## Section\n\nText.";
        final InputStream in = new ByteArrayInputStream(md.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        // No front matter values should be present.
        assertNull(extractData.getValues("frontmatter.title"));

        final String content = extractData.getContent();
        assertTrue(content.contains("plain markdown"));
        assertTrue(content.contains("Section"));
    }

    @Test
    public void test_extractsUtf8WithBom() {
        final String md = "# Heading\n\nBody with BOM.";
        final byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
        final InputStream in =
                new SequenceInputStream(new ByteArrayInputStream(bom), new ByteArrayInputStream(md.getBytes(StandardCharsets.UTF_8)));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();
        // BOM-induced replacement character at the start of the heading would block parsing as a heading.
        // After our fix, the heading should be recognized as plain text.
        assertTrue(content.contains("Heading"));
        assertTrue(content.contains("Body with BOM."));
        // Heading metadata extraction should also see the heading correctly.
        final String[] headings = extractData.getValues("headings");
        assertNotNull(headings);
        boolean foundHeading = false;
        for (final String heading : headings) {
            if ("Heading".equals(heading)) {
                foundHeading = true;
                break;
            }
        }
        assertTrue(foundHeading);
    }

    @Test
    public void test_truncatesAtMaxTextLength() {
        markdownExtractor.setMaxTextLength(20);
        // The Markdown source itself is truncated at 20 chars before parsing.
        final String md = "# Title\n\nThis body is much longer than twenty characters.";
        final InputStream in = new ByteArrayInputStream(md.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);

        final String content = extractData.getContent();
        // The rendered content cannot exceed the truncated source length.
        assertTrue(content.length() <= 20);
    }

    @Test
    public void test_extractsUtf16LeWithBom_markdown() {
        markdownExtractor.setEncoding("UTF-8");
        final String md = "# Hello UTF-16 LE\n\nBody text.";
        final byte[] bom = { (byte) 0xFF, (byte) 0xFE };
        final InputStream in =
                new SequenceInputStream(new ByteArrayInputStream(bom), new ByteArrayInputStream(md.getBytes(StandardCharsets.UTF_16LE)));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String content = extractData.getContent();
        assertTrue(content.contains("Hello UTF-16 LE"));
        assertTrue(content.contains("Body text."));
    }

    @Test
    public void test_extractsUtf16BeWithBom_markdown() {
        markdownExtractor.setEncoding("UTF-8");
        final String md = "# Hello UTF-16 BE\n\nBody text.";
        final byte[] bom = { (byte) 0xFE, (byte) 0xFF };
        final InputStream in =
                new SequenceInputStream(new ByteArrayInputStream(bom), new ByteArrayInputStream(md.getBytes(StandardCharsets.UTF_16BE)));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String content = extractData.getContent();
        assertTrue(content.contains("Hello UTF-16 BE"));
        assertTrue(content.contains("Body text."));
    }

    @Test
    public void test_extractsShiftJis_markdown_noBom() {
        markdownExtractor.setEncoding("Shift_JIS");
        final String md = "# シフトJIS\n\n本文テスト。";
        final InputStream in = new ByteArrayInputStream(md.getBytes(Charset.forName("Shift_JIS")));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String content = extractData.getContent();
        assertTrue(content.contains("シフトJIS"));
        assertTrue(content.contains("本文テスト"));
    }

    @Test
    public void test_maxTextLength_zero_isUnlimited() {
        // maxTextLength=0 means unlimited (condition: maxTextLength > 0 is false).
        markdownExtractor.setMaxTextLength(0);
        final String md = "# Title\n\nHello world full content.";
        final InputStream in = new ByteArrayInputStream(md.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String content = extractData.getContent();
        assertTrue(content.contains("Title"));
        assertTrue(content.contains("Hello world full content."));
        // No truncation metadata expected.
        assertNull(extractData.getValues("truncated"));
    }

    @Test
    public void test_maxTextLength_negative_isUnlimited() {
        // Negative maxTextLength means unlimited.
        markdownExtractor.setMaxTextLength(-1);
        final String md = "# Title\n\nFull content survives.";
        final InputStream in = new ByteArrayInputStream(md.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String content = extractData.getContent();
        assertTrue(content.contains("Full content survives."));
        assertNull(extractData.getValues("truncated"));
    }

    @Test
    public void test_truncatedMetadataFlag() {
        // When truncation occurs the ExtractData must carry truncated=true and maxTextLength metadata.
        markdownExtractor.setMaxTextLength(10);
        final String md = "# Title\n\nThis is a much longer Markdown document that should be truncated.";
        final InputStream in = new ByteArrayInputStream(md.getBytes(StandardCharsets.UTF_8));
        final ExtractData extractData = markdownExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        final String[] truncated = extractData.getValues("truncated");
        assertNotNull(truncated);
        assertEquals("true", truncated[0]);
        final String[] maxLen = extractData.getValues("maxTextLength");
        assertNotNull(maxLen);
        assertEquals("10", maxLen[0]);
    }
}
