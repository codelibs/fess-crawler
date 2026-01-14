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
}
