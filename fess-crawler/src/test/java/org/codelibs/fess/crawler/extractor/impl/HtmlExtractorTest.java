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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpression;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class HtmlExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(HtmlExtractorTest.class);

    public HtmlExtractor htmlExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("htmlExtractor", HtmlExtractor.class);
        htmlExtractor = container.getComponent("htmlExtractor");
        htmlExtractor.addMetadata("title", "//TITLE");
    }

    private HtmlExtractor newExtractor() {
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("htmlExtractor2", HtmlExtractor.class);
        return container.getComponent("htmlExtractor2");
    }

    private static InputStream toStream(final String html) {
        return new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void test_getHtml_utf8() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        final String content = data.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertEquals("タイトル", data.getValues("title")[0]);
    }

    @Test
    public void test_getHtml_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        final String content = data.getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertEquals("タイトル", data.getValues("title")[0]);
    }

    @Test
    public void test_getHtml_empty() {
        final InputStream in = new ByteArrayInputStream("".getBytes());
        final String content = htmlExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertEquals("", content);
    }

    @Test
    public void test_getEncoding_utf8() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = htmlExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("UTF-8", encoding);
    }

    @Test
    public void test_getEncoding_sjis() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_sjis.html");
        final BufferedInputStream bis = new BufferedInputStream(in);
        final String encoding = htmlExtractor.getEncoding(bis);
        CloseableUtil.closeQuietly(bis);
        assertEquals("Shift_JIS", encoding);
    }

    @Test
    public void test_getHtml_null() {
        try {
            htmlExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    // ------------------------------------------------------------------
    // PR-G: default metadata extraction, JSON-LD, and XPath cache tests
    // ------------------------------------------------------------------

    @Test
    public void test_extractsTitle() {
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head><title>Hello World</title></head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        final String[] title = data.getValues("title");
        assertNotNull(title);
        assertEquals("Hello World", title[0]);
    }

    @Test
    public void test_extractsOpenGraph() {
        final HtmlExtractor extractor = newExtractor();
        // language=html
        final String html = "<html><head>" //
                + "<title>Page</title>" //
                + "<meta property=\"og:title\" content=\"OG Title\">" //
                + "<meta property=\"og:description\" content=\"OG Desc\">" //
                + "<meta property=\"og:image\" content=\"https://example.com/img.png\">" //
                + "<meta property=\"og:type\" content=\"article\">" //
                + "<meta property=\"og:url\" content=\"https://example.com/p\">" //
                + "<meta name=\"twitter:card\" content=\"summary\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        assertEquals("OG Title", data.getValues("og:title")[0]);
        assertEquals("OG Desc", data.getValues("og:description")[0]);
        assertEquals("https://example.com/img.png", data.getValues("og:image")[0]);
        assertEquals("article", data.getValues("og:type")[0]);
        assertEquals("https://example.com/p", data.getValues("og:url")[0]);
        assertEquals("summary", data.getValues("twitter:card")[0]);
    }

    @Test
    public void test_extractsCanonical() {
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<title>Page</title>" //
                + "<link rel=\"canonical\" href=\"https://example.com/canonical\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        assertEquals("https://example.com/canonical", data.getValues("canonical")[0]);
    }

    @Test
    public void test_extractsKeywordsAndAuthor() {
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<meta name=\"keywords\" content=\"java, crawler\">" //
                + "<meta name=\"author\" content=\"Alice\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        assertEquals("java, crawler", data.getValues("keywords")[0]);
        assertEquals("Alice", data.getValues("author")[0]);
    }

    @Test
    public void test_extractsDescriptionFromMeta() {
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<meta name=\"description\" content=\"A description\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        assertEquals("A description", data.getValues("description")[0]);
    }

    @Test
    public void test_extractsJsonLd() {
        final HtmlExtractor extractor = newExtractor();
        final String json = "{\"@context\":\"https://schema.org\",\"@type\":\"Article\",\"headline\":\"H\"}";
        final String html = "<html><head><title>T</title>" //
                + "<script type=\"application/ld+json\">" + json + "</script>" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        assertEquals(1, types.length);
        assertEquals("Article", types[0]);

        final String[] raw = data.getValues(HtmlExtractor.JSONLD_RAW_KEY);
        assertNotNull(raw);
        assertEquals(1, raw.length);
        assertTrue(raw[0].contains("\"@type\":\"Article\""));
    }

    @Test
    public void test_extractsJsonLd_multipleBlocks_andArrayTypes() {
        final HtmlExtractor extractor = newExtractor();
        final String first = "{\"@type\":\"Article\"}";
        final String second = "{\"@type\":[\"BlogPosting\",\"NewsArticle\"]}";
        final String html = "<html><head>" //
                + "<script type=\"application/ld+json\">" + first + "</script>" //
                + "<script type=\"application/ld+json\">" + second + "</script>" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        final List<String> typeList = Arrays.asList(types);
        Assertions.assertTrue(typeList.contains("Article"), "expected Article in " + typeList);
        Assertions.assertTrue(typeList.contains("BlogPosting"), "expected BlogPosting in " + typeList);
        Assertions.assertTrue(typeList.contains("NewsArticle"), "expected NewsArticle in " + typeList);

        final String[] raw = data.getValues(HtmlExtractor.JSONLD_RAW_KEY);
        assertNotNull(raw);
        assertEquals(2, raw.length);
    }

    @Test
    public void test_xpathCacheReused() throws Exception {
        final HtmlExtractor extractor = newExtractor();
        // Disable default-metadata + JSON-LD so only the configured XPaths run
        // and we get a small, predictable cache size.
        extractor.setExtractDefaultMetadata(false);
        extractor.setExtractJsonLd(false);
        extractor.addMetadata("custom", "//TITLE");
        final String html = "<html><head><title>T</title></head><body>b</body></html>";

        // First call: warms the cache.
        extractor.getText(toStream(html), null);
        // Second call: should reuse cached compiled expressions.
        extractor.getText(toStream(html), null);

        final Field cacheField = HtmlExtractor.class.getDeclaredField("threadLocalXPathCache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final ThreadLocal<Map<String, XPathExpression>> tl = (ThreadLocal<Map<String, XPathExpression>>) cacheField.get(extractor);
        final Map<String, XPathExpression> cache = tl.get();
        // contentXpath ("//BODY") + "//TITLE" should be cached.
        Assertions.assertTrue(cache.containsKey("//BODY"), "cache should contain //BODY: " + cache.keySet());
        Assertions.assertTrue(cache.containsKey("//TITLE"), "cache should contain //TITLE: " + cache.keySet());

        // Capture the compiled expression and confirm reusing the same instance.
        final XPathExpression cachedBody = cache.get("//BODY");
        extractor.getText(toStream(html), null);
        Assertions.assertSame(cachedBody, cache.get("//BODY"), "XPathExpression must be reused across calls");

        // clearXPathCache() should empty the cache.
        extractor.clearXPathCache();
        Assertions.assertTrue(cache.isEmpty(), "cache must be empty after clearXPathCache");
    }

    @Test
    public void test_disableDefaultMetadata() {
        final HtmlExtractor extractor = newExtractor();
        extractor.setExtractDefaultMetadata(false);
        final String html = "<html><head>" //
                + "<title>T</title>" //
                + "<meta property=\"og:title\" content=\"X\">" //
                + "<meta name=\"description\" content=\"D\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        // The user did not call addMetadata for these keys, and defaults are off.
        Assertions.assertNull(data.getValues("title"), "title should not be set when defaults disabled");
        Assertions.assertNull(data.getValues("og:title"), "og:title should not be set when defaults disabled");
        Assertions.assertNull(data.getValues("description"), "description should not be set when defaults disabled");
    }

    @Test
    public void test_disableJsonLd() {
        final HtmlExtractor extractor = newExtractor();
        extractor.setExtractJsonLd(false);
        final String html = "<html><head>" //
                + "<title>T</title>" //
                + "<script type=\"application/ld+json\">{\"@type\":\"Article\"}</script>" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        Assertions.assertNull(data.getValues(HtmlExtractor.JSONLD_TYPE_KEY), "jsonld.type must be absent when JSON-LD disabled");
        Assertions.assertNull(data.getValues(HtmlExtractor.JSONLD_RAW_KEY), "jsonld.raw must be absent when JSON-LD disabled");
    }

    @Test
    public void test_corruptJsonLd_doesNotFailExtraction() {
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<title>T</title>" //
                + "<script type=\"application/ld+json\">{not valid json</script>" //
                + "<script type=\"application/ld+json\">{\"@type\":\"Article\"}</script>" //
                + "</head><body>body content</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        // Body content still extracted.
        assertTrue(data.getContent().contains("body content"));
        // Title default still applied.
        assertEquals("T", data.getValues("title")[0]);
        // The valid JSON-LD block is still indexed.
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        assertEquals(1, types.length);
        assertEquals("Article", types[0]);
        // Raw values capture both the malformed and valid blocks.
        final String[] raw = data.getValues(HtmlExtractor.JSONLD_RAW_KEY);
        assertNotNull(raw);
        assertEquals(2, raw.length);
    }

    @Test
    public void test_metaTagOverride_via_setDefaultFieldRules() {
        final HtmlExtractor extractor = newExtractor();
        final Map<String, String> custom = new LinkedHashMap<>();
        // Only one custom rule, replacing the entire defaults map.
        custom.put("page-title", "//TITLE/text()");
        extractor.setDefaultFieldRules(custom);

        final String html = "<html><head>" //
                + "<title>Replaced</title>" //
                + "<meta property=\"og:title\" content=\"X\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        assertEquals("Replaced", data.getValues("page-title")[0]);
        // The original defaults were replaced; og:title rule no longer applies.
        Assertions.assertNull(data.getValues("og:title"), "og:title should not be set when defaults are overridden");
        Assertions.assertNull(data.getValues("title"), "title should not be set when defaults are overridden");
    }
}
