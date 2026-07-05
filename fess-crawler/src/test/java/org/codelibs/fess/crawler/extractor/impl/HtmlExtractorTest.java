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
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

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
        final HtmlExtractor extractor = container.getComponent("htmlExtractor2");
        // JSON-LD extraction is opt-in in production (extractJsonLd defaults to false);
        // the JSON-LD feature tests below exercise it, so enable it here. The
        // production default is pinned by test_jsonLdDisabledByDefault.
        extractor.setExtractJsonLd(true);
        return extractor;
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
    public void test_nonNodeSetXPathRoutingIsCachedAndStable() throws Exception {
        // A non-node-set rule (string(...)) is evaluated via the fallback path. The
        // primary node-set attempt throws once; the expression is then remembered so
        // subsequent documents skip the throwing attempt. Output must stay correct either way.
        final HtmlExtractor extractor = newExtractor();
        extractor.setExtractDefaultMetadata(false);
        extractor.setExtractJsonLd(false);
        extractor.addMetadata("titleStr", "string(//TITLE)");
        final String html1 = "<html><head><title>First</title></head><body>b</body></html>";
        final String html2 = "<html><head><title>Second</title></head><body>b</body></html>";

        // First call: primary NODESET attempt throws, falls back, records the path.
        final ExtractData d1 = extractor.getText(toStream(html1), null);
        assertEquals("First", d1.getValues("titleStr")[0]);

        // The non-node-set expression must now be remembered for this thread.
        final Field pathsField = HtmlExtractor.class.getDeclaredField("threadLocalNonNodeSetPaths");
        pathsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final ThreadLocal<Set<String>> tl = (ThreadLocal<Set<String>>) pathsField.get(extractor);
        Assertions.assertTrue(tl.get().contains("string(//TITLE)"), "non-node-set path must be cached: " + tl.get());

        // Second call: takes the cached fast path (no throw) and still yields correct output.
        final ExtractData d2 = extractor.getText(toStream(html2), null);
        assertEquals("Second", d2.getValues("titleStr")[0]);

        // clearXPathCache() also clears the non-node-set classification.
        extractor.clearXPathCache();
        Assertions.assertTrue(tl.get().isEmpty(), "non-node-set cache must be empty after clearXPathCache");
    }

    @Test
    public void test_jsonLdAutoFillNotSuppressedByEmptyCustomRule() {
        // A custom rule targeting jsonld.raw that matches nothing sets the key to an
        // empty array. That empty value must NOT suppress JSON-LD auto-fill; the
        // precedence check uses a non-blank test, matching the default-rule behaviour.
        final HtmlExtractor extractor = newExtractor();
        extractor.setExtractDefaultMetadata(false);
        extractor.addMetadata(HtmlExtractor.JSONLD_RAW_KEY, "//NOSUCHTAG");
        final String html = "<html><head><title>T</title>" + "<script type=\"application/ld+json\">{\"@type\":\"Article\"}</script>"
                + "</head><body>b</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        final String[] raw = data.getValues(HtmlExtractor.JSONLD_RAW_KEY);
        assertNotNull(raw);
        Assertions.assertEquals(1, raw.length, "JSON-LD raw must be auto-filled despite the empty custom rule");
        Assertions.assertTrue(raw[0].contains("Article"), "raw JSON-LD must be retained: " + raw[0]);
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        Assertions.assertTrue(Arrays.asList(types).contains("Article"), "type must be collected: " + Arrays.toString(types));
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
    public void test_jsonLdDisabledByDefault() {
        // JSON-LD extraction is opt-in: a production-default extractor (NOT the
        // JSON-LD-enabled newExtractor() helper) must not populate jsonld.* keys,
        // while default HTML metadata is still extracted.
        final StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("htmlExtractorDefault", HtmlExtractor.class);
        final HtmlExtractor extractor = container.getComponent("htmlExtractorDefault");
        final String html = "<html><head><title>T</title>" + "<script type=\"application/ld+json\">{\"@type\":\"Article\"}</script>"
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        Assertions.assertNull(data.getValues(HtmlExtractor.JSONLD_TYPE_KEY), "jsonld.type must be absent by default (opt-in)");
        Assertions.assertNull(data.getValues(HtmlExtractor.JSONLD_RAW_KEY), "jsonld.raw must be absent by default (opt-in)");
        // Default HTML metadata extraction stays on by default.
        assertEquals("T", data.getValues("title")[0]);
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
        // Replacing the entire defaults map with a custom map: only the custom
        // rule applies; og:title and title rules are gone.
        // Fallback rules are independent and still apply, but since og:title rule
        // is not in the custom map, og:title key is never populated, so the
        // fallback for title (og:title -> title) also does not fire.
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

    @Test
    public void test_jsonLdNestingDepthExceeded_doesNotCrash() {
        final HtmlExtractor extractor = newExtractor();
        // Build JSON-LD with nesting depth far beyond JSONLD_MAX_NESTING_DEPTH (64).
        final int depth = 200;
        final StringBuilder json = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            json.append("{\"a\":");
        }
        json.append("\"deep\"");
        for (int i = 0; i < depth; i++) {
            json.append("}");
        }
        final String html = "<html><head>" //
                + "<title>T</title>" //
                + "<script type=\"application/ld+json\">" + json + "</script>" //
                + "<script type=\"application/ld+json\">{\"@type\":\"Article\"}</script>" //
                + "</head><body>body content</body></html>";

        // Must not throw / abort extraction.
        final ExtractData data = extractor.getText(toStream(html), null);
        assertNotNull(data);
        assertTrue(data.getContent().contains("body content"));
        // Default title still extracted.
        assertEquals("T", data.getValues("title")[0]);

        // Both raw blocks captured (nesting limit only blocks @type collection,
        // not raw capture).
        final String[] raw = data.getValues(HtmlExtractor.JSONLD_RAW_KEY);
        assertNotNull(raw);
        assertEquals(2, raw.length);

        // The deeply nested block must NOT contribute a @type entry; only the
        // shallow second block should have produced one.
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        assertEquals(1, types.length);
        assertEquals("Article", types[0]);
    }

    @Test
    public void test_malformedXPath_doesNotAbortExtraction() {
        // Pre-cache behaviour was: a malformed metadataXpathMap entry logs a
        // warning and yields no value rather than failing the whole
        // extraction. The XPath compile cache must not regress that contract.
        final HtmlExtractor extractor = newExtractor();
        extractor.addMetadata("broken", "//[invalid");
        extractor.addMetadata("ok", "//TITLE");
        final String html = "<html><head><title>OK</title></head><body>body content</body></html>";

        final ExtractData data = extractor.getText(toStream(html), null);
        assertNotNull(data);
        assertTrue(data.getContent().contains("body content"));
        // Valid metadata still populated.
        assertEquals("OK", data.getValues("ok")[0]);
        // Malformed XPath quietly produced no values.
        Assertions.assertTrue(data.getValues("broken") == null || data.getValues("broken").length == 0,
                "malformed XPath must not populate metadata");
    }

    @Test
    public void test_malformedContentXPath_doesNotAbortExtraction() {
        // When contentXpath itself is malformed, pre-cache behaviour was to
        // swallow the failure and return an extract with empty content. The
        // CrawlerSystemException raised by the compile cache must therefore
        // be caught at the same boundary instead of propagating out.
        final HtmlExtractor extractor = newExtractor();
        try {
            final Field f = HtmlExtractor.class.getDeclaredField("contentXpath");
            f.setAccessible(true);
            f.set(extractor, "//[invalid");
        } catch (final Exception e) {
            throw new AssertionError("failed to override contentXpath", e);
        }
        final String html = "<html><head><title>T</title></head><body>body content</body></html>";

        final ExtractData data = extractor.getText(toStream(html), null);
        assertNotNull(data);
        // Default-metadata path still ran (title present).
        assertEquals("T", data.getValues("title")[0]);
    }

    @Test
    public void test_customJsonLdMetadataKey_isPreserved() {
        // If the operator has registered addMetadata("jsonld.raw"/"jsonld.type", ...),
        // the user-supplied value must take precedence over the JSON-LD
        // auto-extraction (mirroring the applyDefaultFieldRules precedence).
        final HtmlExtractor extractor = newExtractor();
        extractor.addMetadata(HtmlExtractor.JSONLD_RAW_KEY, "//META[@name='custom-raw']/@content");
        extractor.addMetadata(HtmlExtractor.JSONLD_TYPE_KEY, "//META[@name='custom-type']/@content");
        final String html = "<html><head>" //
                + "<title>T</title>" //
                + "<meta name=\"custom-raw\" content=\"USER_RAW\">" //
                + "<meta name=\"custom-type\" content=\"USER_TYPE\">" //
                + "<script type=\"application/ld+json\">{\"@type\":\"Article\"}</script>" //
                + "</head><body>body</body></html>";

        final ExtractData data = extractor.getText(toStream(html), null);
        final String[] raw = data.getValues(HtmlExtractor.JSONLD_RAW_KEY);
        assertNotNull(raw);
        assertEquals(1, raw.length);
        assertEquals("USER_RAW", raw[0]);
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        assertEquals(1, types.length);
        assertEquals("USER_TYPE", types[0]);
    }

    @Test
    public void test_jsonLdGraphTypesAreCollected() {
        // Schema.org markup very commonly wraps multiple typed entities in a
        // top-level @graph array. Verify each nested @type contributes.
        final HtmlExtractor extractor = newExtractor();
        final String json = "{\"@context\":\"https://schema.org\"," + "\"@graph\":[{\"@type\":\"Organization\",\"name\":\"Acme\"},"
                + "{\"@type\":[\"WebSite\",\"Thing\"],\"url\":\"https://example.com\"}," + "{\"@type\":\"Article\","
                + "\"author\":{\"@type\":\"Person\",\"name\":\"A\"}}]}";
        final String html = "<html><head>" //
                + "<title>T</title>" //
                + "<script type=\"application/ld+json\">" + json + "</script>" //
                + "</head><body>body</body></html>";

        final ExtractData data = extractor.getText(toStream(html), null);
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        final List<String> typeList = Arrays.asList(types);
        Assertions.assertTrue(typeList.contains("Organization"), "@graph[0] @type missing: " + typeList);
        Assertions.assertTrue(typeList.contains("WebSite"), "@graph[1] @type[0] missing: " + typeList);
        Assertions.assertTrue(typeList.contains("Thing"), "@graph[1] @type[1] missing: " + typeList);
        Assertions.assertTrue(typeList.contains("Article"), "@graph[2] @type missing: " + typeList);
        Assertions.assertTrue(typeList.contains("Person"), "nested author @type missing: " + typeList);
    }

    @Test
    public void test_jsonLdContextObject_typeNotLeaked() {
        // @context can itself be an object with embedded @type term
        // definitions (vocabulary metadata, not real data). Those must NOT
        // appear in jsonld.type.
        final HtmlExtractor extractor = newExtractor();
        final String json = "{\"@context\":{\"name\":{\"@type\":\"@id\"}}," + "\"@type\":\"Article\",\"name\":\"x\"}";
        final String html = "<html><head>" //
                + "<title>T</title>" //
                + "<script type=\"application/ld+json\">" + json + "</script>" //
                + "</head><body>body</body></html>";

        final ExtractData data = extractor.getText(toStream(html), null);
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        // Only the top-level @type (Article) — the @id inside @context must
        // not be collected.
        assertEquals(1, types.length);
        assertEquals("Article", types[0]);
    }

    @Test
    public void test_titleBackfillsFromOgTitle_whenCustomTitleXPathIsEmpty() {
        // Mirrors the standard fess-crawler-lasta config: extractor.xml
        // registers addMetadata("title", "//TITLE"). On a page that has no
        // <title> but does carry og:title, the metadataXpathMap loop unconditionally
        // calls putValues("title", []), leaving the key non-null but blank.
        // The default-rule backfill must still fire so the og:title fallback
        // takes effect.
        final HtmlExtractor extractor = newExtractor();
        extractor.addMetadata("title", "//TITLE");

        final String html = "<html><head>" //
                + "<meta property=\"og:title\" content=\"OG Backfill\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        final String[] title = data.getValues("title");
        assertNotNull(title);
        Assertions.assertTrue(title.length > 0, "title must be backfilled from og:title");
        assertEquals("OG Backfill", title[0]);
    }

    @Test
    public void test_titleNotOverwritten_whenCustomTitleXPathHasValue() {
        // Companion to the backfill test: when the custom rule actually
        // produces a non-blank value, it must win over the default backfill
        // (precedence rule preserved).
        final HtmlExtractor extractor = newExtractor();
        extractor.addMetadata("title", "//TITLE");

        final String html = "<html><head>" //
                + "<title>Page Title</title>" //
                + "<meta property=\"og:title\" content=\"OG Title\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        final String[] title = data.getValues("title");
        assertNotNull(title);
        assertEquals(1, title.length);
        assertEquals("Page Title", title[0]);
    }

    @Test
    public void test_jsonLd_typeAttributeIsCaseInsensitiveAndTrimmed() {
        // RFC 6838 / HTML5: media types are case-insensitive and may carry
        // surrounding whitespace. The XPath must therefore match
        // Application/LD+JSON, application/LD+JSON, and values padded with
        // whitespace, in addition to the canonical lowercase form.
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<title>T</title>" //
                + "<script type=\"Application/LD+JSON\">{\"@type\":\"A\"}</script>" //
                + "<script type=\"  application/ld+json  \">{\"@type\":\"B\"}</script>" //
                + "<script type=\"application/LD+json\">{\"@type\":\"C\"}</script>" //
                + "</head><body>body</body></html>";

        final ExtractData data = extractor.getText(toStream(html), null);
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        final List<String> typeList = Arrays.asList(types);
        Assertions.assertTrue(typeList.contains("A"), "expected A in " + typeList);
        Assertions.assertTrue(typeList.contains("B"), "expected B in " + typeList);
        Assertions.assertTrue(typeList.contains("C"), "expected C in " + typeList);

        final String[] raw = data.getValues(HtmlExtractor.JSONLD_RAW_KEY);
        assertNotNull(raw);
        assertEquals(3, raw.length);
    }

    @Test
    public void test_jsonLd_unrelatedScriptTypeIsIgnored() {
        // Sanity check that the case-insensitive XPath does not over-match —
        // application/javascript and arbitrary mime types must not be parsed
        // as JSON-LD.
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<title>T</title>" //
                + "<script type=\"application/javascript\">var x=1;</script>" //
                + "<script type=\"text/javascript\">var y=2;</script>" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        Assertions.assertNull(data.getValues(HtmlExtractor.JSONLD_TYPE_KEY), "non-JSON-LD scripts must not contribute jsonld.type");
        Assertions.assertNull(data.getValues(HtmlExtractor.JSONLD_RAW_KEY), "non-JSON-LD scripts must not contribute jsonld.raw");
    }

    @Test
    public void test_destroyClearsThreadLocals() throws Exception {
        final HtmlExtractor extractor = newExtractor();
        extractor.setExtractDefaultMetadata(false);
        extractor.setExtractJsonLd(false);
        extractor.addMetadata("custom", "//TITLE");

        // Warm caches on the calling thread.
        final String html = "<html><head><title>T</title></head><body>b</body></html>";
        extractor.getText(toStream(html), null);

        final Field cacheField = HtmlExtractor.class.getDeclaredField("threadLocalXPathCache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final ThreadLocal<Map<String, XPathExpression>> tlCache = (ThreadLocal<Map<String, XPathExpression>>) cacheField.get(extractor);
        final Map<String, XPathExpression> cacheBefore = tlCache.get();
        Assertions.assertFalse(cacheBefore.isEmpty(), "cache must be populated before destroy");

        // Capture identity of the populated cache map.
        final int beforeIdentity = System.identityHashCode(cacheBefore);

        // destroy() must clear the calling thread's ThreadLocals.
        extractor.destroy();

        // After destroy(), the ThreadLocal must initialise a fresh, empty map
        // (different identity from before).
        final Map<String, XPathExpression> cacheAfter = tlCache.get();
        Assertions.assertTrue(cacheAfter.isEmpty(), "cache must be empty after destroy");
        Assertions.assertNotEquals(beforeIdentity, System.identityHashCode(cacheAfter),
                "ThreadLocal must hold a freshly initialised map after destroy");

        // Subsequent extraction still works (lazily reinitialises ThreadLocals).
        final ExtractData data = extractor.getText(toStream(html), null);
        assertNotNull(data);
        assertEquals("T", data.getValues("custom")[0]);
        Assertions.assertFalse(tlCache.get().isEmpty(), "cache must be repopulated after subsequent extraction");
    }

    // ------------------------------------------------------------------
    // New tests: fallback rules, Twitter Card, null-setter, limits, concurrency
    // ------------------------------------------------------------------

    @Test
    public void test_titleBackfillsFromOgTitle_viaDefaultFallback() {
        // Page has og:title but no <title>. Default primary rules populate og:title
        // key; the fallback rule then copies og:title -> title.
        // No addMetadata("title", ...) here — pure defaults path.
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<meta property=\"og:title\" content=\"FB Title\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        final String[] title = data.getValues("title");
        Assertions.assertNotNull(title, "title must be backfilled from og:title via fallback rule");
        Assertions.assertTrue(title.length > 0, "title array must be non-empty");
        assertEquals("FB Title", title[0]);
        // og:title key itself is also present.
        assertEquals("FB Title", data.getValues("og:title")[0]);
    }

    @Test
    public void test_descriptionBackfillsFromOgDescription() {
        // No meta name=description, but og:description present.
        // Fallback rule: description <- og:description.
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<meta property=\"og:description\" content=\"OG Desc Fallback\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        final String[] desc = data.getValues("description");
        Assertions.assertNotNull(desc, "description must be backfilled from og:description");
        assertEquals("OG Desc Fallback", desc[0]);
    }

    @Test
    public void test_titleUsesPrimaryNotOgTitle_whenBothPresent() {
        // Both <title> and og:title present: primary rule wins for title,
        // og:title key stays with its own value.
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<title>Primary Title</title>" //
                + "<meta property=\"og:title\" content=\"OG Title Here\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        final String[] title = data.getValues("title");
        assertNotNull(title);
        Assertions.assertEquals("Primary Title", title[0], "primary <title> must win over og:title fallback");

        // og:title key still holds its own value.
        final String[] ogTitle = data.getValues("og:title");
        assertNotNull(ogTitle);
        assertEquals("OG Title Here", ogTitle[0]);
    }

    @Test
    public void test_extractsTwitterCardFull() {
        // All four new Twitter Card fields should be extracted by default rules.
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<meta name=\"twitter:title\" content=\"TW Title\">" //
                + "<meta name=\"twitter:description\" content=\"TW Desc\">" //
                + "<meta name=\"twitter:image\" content=\"https://example.com/tw.png\">" //
                + "<meta name=\"twitter:site\" content=\"@example\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        assertEquals("TW Title", data.getValues("twitter:title")[0]);
        assertEquals("TW Desc", data.getValues("twitter:description")[0]);
        assertEquals("https://example.com/tw.png", data.getValues("twitter:image")[0]);
        assertEquals("@example", data.getValues("twitter:site")[0]);
    }

    @Test
    public void test_setDefaultFieldRulesWithNull_restoresBuiltins() {
        // setDefaultFieldRules(null) must immediately restore the built-in defaults
        // without requiring another init() call.
        final HtmlExtractor extractor = newExtractor();
        final Map<String, String> custom = new LinkedHashMap<>();
        custom.put("only-this", "//TITLE");
        extractor.setDefaultFieldRules(custom);

        // Verify custom is in effect.
        final String htmlBefore = "<html><head><title>X</title><meta property=\"og:title\" content=\"OG\"></head><body>b</body></html>";
        final ExtractData dataBefore = extractor.getText(toStream(htmlBefore), null);
        assertNotNull(dataBefore.getValues("only-this"));
        Assertions.assertNull(dataBefore.getValues("og:title"), "og:title should not exist with custom rules");

        // Restore defaults.
        extractor.setDefaultFieldRules(null);

        // Now built-ins should be active again.
        final String html = "<html><head><title>Y</title><meta property=\"og:title\" content=\"OG2\"></head><body>b</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        assertEquals("Y", data.getValues("title")[0]);
        assertEquals("OG2", data.getValues("og:title")[0]);
    }

    @Test
    public void test_setDefaultFieldFallbackRules_canBeCustomized() {
        // Replace fallback rules with a custom map and verify only the custom
        // fallback fires.
        final HtmlExtractor extractor = newExtractor();
        final Map<String, String> fallback = new LinkedHashMap<>();
        fallback.put("keywords", "author"); // nonsensical but tests the mechanism
        extractor.setDefaultFieldFallbackRules(fallback);

        // Page with author but no keywords.
        final String html = "<html><head>" //
                + "<meta name=\"author\" content=\"Alice\">" //
                + "</head><body>body</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);

        // Custom fallback: keywords gets value from author.
        final String[] kw = data.getValues("keywords");
        Assertions.assertNotNull(kw, "keywords must be backfilled from author by custom fallback");
        assertEquals("Alice", kw[0]);

        // Default title fallback (og:title -> title) must NOT fire because
        // we replaced the fallback map entirely.
        // (No og:title on this page anyway, so title is simply absent.)
        Assertions.assertNull(data.getValues("title"), "title should not be set when no <title> and no og:title");
    }

    @Test
    public void test_jsonLd_blockCountLimit() {
        // JSONLD_MAX_BLOCK_COUNT (64) blocks: 65 blocks → only 64 captured.
        final HtmlExtractor extractor = newExtractor();
        final StringBuilder sb = new StringBuilder("<html><head><title>T</title>");
        for (int i = 0; i < 65; i++) {
            sb.append("<script type=\"application/ld+json\">{\"@type\":\"X\"}</script>");
        }
        sb.append("</head><body>b</body></html>");
        final ExtractData data = extractor.getText(toStream(sb.toString()), null);
        final String[] raw = data.getValues(HtmlExtractor.JSONLD_RAW_KEY);
        assertNotNull(raw);
        Assertions.assertEquals(HtmlExtractor.JSONLD_MAX_BLOCK_COUNT, raw.length, "raw blocks must be truncated at JSONLD_MAX_BLOCK_COUNT");
    }

    @Test
    public void test_jsonLd_totalRawSizeLimit() {
        // Two blocks each just over 600KB → together they exceed 1MB limit.
        // Only the first block should be captured.
        final HtmlExtractor extractor = newExtractor();
        // 600KB of 'a' characters inside the @type value — well-formed JSON.
        final String bigValue = "a".repeat(600 * 1024);
        final String bigBlock = "{\"@type\":\"" + bigValue + "\"}";
        final StringBuilder sb = new StringBuilder("<html><head><title>T</title>");
        sb.append("<script type=\"application/ld+json\">").append(bigBlock).append("</script>");
        sb.append("<script type=\"application/ld+json\">").append(bigBlock).append("</script>");
        sb.append("</head><body>b</body></html>");
        final ExtractData data = extractor.getText(toStream(sb.toString()), null);
        final String[] raw = data.getValues(HtmlExtractor.JSONLD_RAW_KEY);
        assertNotNull(raw);
        Assertions.assertTrue(raw.length < 2, "second block must be truncated due to total size limit; got " + raw.length + " blocks");
    }

    @Test
    public void test_jsonLd_typesPerBlockLimit() {
        // One block with a @graph containing more than JSONLD_MAX_TYPES_PER_BLOCK
        // typed entities. Collected types must be <= 256.
        final HtmlExtractor extractor = newExtractor();
        final StringBuilder graphItems = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            if (graphItems.length() > 0) {
                graphItems.append(",");
            }
            graphItems.append("{\"@type\":\"Thing").append(i).append("\"}");
        }
        final String json = "{\"@graph\":[" + graphItems + "]}";
        final String html = "<html><head><title>T</title>" + "<script type=\"application/ld+json\">" + json + "</script>"
                + "</head><body>b</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        Assertions.assertTrue(types.length <= HtmlExtractor.JSONLD_MAX_TYPES_PER_BLOCK,
                "types must be bounded at JSONLD_MAX_TYPES_PER_BLOCK; got " + types.length);
    }

    @Test
    public void test_concurrentExtraction_threadSafe() throws Exception {
        // 4 threads × 30 iterations each: all results must be equal to the
        // single-thread result, and no exception must escape.
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<title>Concurrent</title>" //
                + "<meta property=\"og:title\" content=\"OG\">" //
                + "<script type=\"application/ld+json\">{\"@type\":\"Article\"}</script>" //
                + "</head><body>body text</body></html>";

        final ExtractData reference = extractor.getText(toStream(html), null);
        final String refTitle = reference.getValues("title")[0];
        final String refType = reference.getValues(HtmlExtractor.JSONLD_TYPE_KEY)[0];

        final int threads = 4;
        final int iterations = 30;
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        @SuppressWarnings("unchecked")
        final CompletableFuture<Void>[] futures = new CompletableFuture[threads];
        for (int t = 0; t < threads; t++) {
            futures[t] = CompletableFuture.runAsync(() -> {
                for (int i = 0; i < iterations; i++) {
                    final ExtractData d = extractor.getText(toStream(html), null);
                    final String[] titleArr = d.getValues("title");
                    Assertions.assertNotNull(titleArr, "title must not be null");
                    Assertions.assertEquals(refTitle, titleArr[0], "title must match reference");
                    final String[] typeArr = d.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
                    Assertions.assertNotNull(typeArr, "jsonld.type must not be null");
                    Assertions.assertEquals(refType, typeArr[0], "jsonld.type must match reference");
                }
            }, pool);
        }
        CompletableFuture.allOf(futures).join();
        pool.shutdown();
    }

    @Test
    public void test_concurrentExtraction_perThreadCacheIsolation() throws Exception {
        // Two threads each warm their cache, then we verify via reflection that
        // the ThreadLocal holds a different Map instance per thread.
        final HtmlExtractor extractor = newExtractor();
        extractor.setExtractDefaultMetadata(false);
        extractor.setExtractJsonLd(false);
        extractor.addMetadata("custom", "//TITLE");
        final String html = "<html><head><title>T</title></head><body>b</body></html>";

        final Field cacheField = HtmlExtractor.class.getDeclaredField("threadLocalXPathCache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final ThreadLocal<Map<String, XPathExpression>> tlCache = (ThreadLocal<Map<String, XPathExpression>>) cacheField.get(extractor);

        final CountDownLatch warmLatch = new CountDownLatch(2);
        final CountDownLatch checkLatch = new CountDownLatch(1);
        final AtomicReference<Map<String, XPathExpression>> cacheA = new AtomicReference<>();
        final AtomicReference<Map<String, XPathExpression>> cacheB = new AtomicReference<>();
        final AtomicReference<Throwable> err = new AtomicReference<>();

        final Thread threadA = new Thread(() -> {
            try {
                extractor.getText(toStream(html), null);
                cacheA.set(tlCache.get());
                warmLatch.countDown();
                checkLatch.await();
            } catch (Exception e) {
                err.set(e);
            }
        });
        final Thread threadB = new Thread(() -> {
            try {
                extractor.getText(toStream(html), null);
                cacheB.set(tlCache.get());
                warmLatch.countDown();
                checkLatch.await();
            } catch (Exception e) {
                err.set(e);
            }
        });
        threadA.start();
        threadB.start();
        warmLatch.await();
        checkLatch.countDown();
        threadA.join();
        threadB.join();

        Assertions.assertNull(err.get(), "thread error: " + err.get());
        Assertions.assertNotNull(cacheA.get(), "thread A cache must not be null");
        Assertions.assertNotNull(cacheB.get(), "thread B cache must not be null");
        Assertions.assertNotSame(cacheA.get(), cacheB.get(), "each thread must have its own cache Map instance");
    }

    @Test
    public void test_clearXPathCache_doesNotAffectOtherThread() throws Exception {
        // Thread A warms cache; thread B warms and then clears cache.
        // Thread A's cache must remain populated.
        final HtmlExtractor extractor = newExtractor();
        extractor.setExtractDefaultMetadata(false);
        extractor.setExtractJsonLd(false);
        extractor.addMetadata("custom", "//TITLE");
        final String html = "<html><head><title>T</title></head><body>b</body></html>";

        final Field cacheField = HtmlExtractor.class.getDeclaredField("threadLocalXPathCache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final ThreadLocal<Map<String, XPathExpression>> tlCache = (ThreadLocal<Map<String, XPathExpression>>) cacheField.get(extractor);

        final CountDownLatch aWarmed = new CountDownLatch(1);
        final CountDownLatch bCleared = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        final AtomicReference<Map<String, XPathExpression>> cacheA = new AtomicReference<>();
        final AtomicReference<Throwable> err = new AtomicReference<>();

        final Thread threadA = new Thread(() -> {
            try {
                extractor.getText(toStream(html), null);
                cacheA.set(tlCache.get());
                aWarmed.countDown();
                bCleared.await();
                // After B cleared its own cache, A's cache should still be populated.
                done.countDown();
            } catch (Exception e) {
                err.set(e);
                done.countDown();
            }
        });
        final Thread threadB = new Thread(() -> {
            try {
                extractor.getText(toStream(html), null);
                aWarmed.await();
                extractor.clearXPathCache();
                bCleared.countDown();
            } catch (Exception e) {
                err.set(e);
                bCleared.countDown();
            }
        });
        threadA.start();
        threadB.start();
        done.await();
        threadA.join();
        threadB.join();

        Assertions.assertNull(err.get(), "thread error: " + err.get());
        Assertions.assertNotNull(cacheA.get(), "thread A cache reference must not be null");
        Assertions.assertFalse(cacheA.get().isEmpty(), "thread A cache must still be populated after B cleared its own");
    }

    @Test
    public void test_destroy_doesNotClearOtherThreadsCache() throws Exception {
        // Documents the known limitation: destroy() only clears the calling thread's
        // ThreadLocals, not other threads'.
        final HtmlExtractor extractor = newExtractor();
        extractor.setExtractDefaultMetadata(false);
        extractor.setExtractJsonLd(false);
        extractor.addMetadata("custom", "//TITLE");
        final String html = "<html><head><title>T</title></head><body>b</body></html>";

        final Field cacheField = HtmlExtractor.class.getDeclaredField("threadLocalXPathCache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        final ThreadLocal<Map<String, XPathExpression>> tlCache = (ThreadLocal<Map<String, XPathExpression>>) cacheField.get(extractor);

        final CountDownLatch warmed = new CountDownLatch(1);
        final CountDownLatch destroyed = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(1);
        final AtomicReference<Map<String, XPathExpression>> workerCache = new AtomicReference<>();
        final AtomicReference<Boolean> stillPopulated = new AtomicReference<>();
        final AtomicReference<Throwable> err = new AtomicReference<>();

        final Thread worker = new Thread(() -> {
            try {
                extractor.getText(toStream(html), null);
                workerCache.set(tlCache.get());
                warmed.countDown();
                destroyed.await();
                // After main thread called destroy(), worker's own cache is unchanged.
                stillPopulated.set(!tlCache.get().isEmpty());
                done.countDown();
            } catch (Exception e) {
                err.set(e);
                done.countDown();
            }
        });
        worker.start();
        warmed.await();
        extractor.destroy(); // destroys main thread's cache, not worker's
        destroyed.countDown();
        done.await();
        worker.join();

        Assertions.assertNull(err.get(), "thread error: " + err.get());
        Assertions.assertTrue(stillPopulated.get(), "worker thread cache must remain populated after main-thread destroy()");
    }

    @Test
    public void test_jsonLd_topLevelArray() {
        // Top-level JSON-LD array: [{@type:A},{@type:B}].
        // Both types must be collected.
        final HtmlExtractor extractor = newExtractor();
        final String json = "[{\"@type\":\"A\"},{\"@type\":\"B\"}]";
        final String html = "<html><head><title>T</title>" + "<script type=\"application/ld+json\">" + json + "</script>"
                + "</head><body>b</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        final List<String> typeList = Arrays.asList(types);
        Assertions.assertTrue(typeList.contains("A"), "expected A in " + typeList);
        Assertions.assertTrue(typeList.contains("B"), "expected B in " + typeList);
    }

    @Test
    public void test_jsonLd_emptyScriptBlock() {
        // Empty JSON-LD script block: no keys should be populated.
        final HtmlExtractor extractor = newExtractor();
        final String html =
                "<html><head><title>T</title>" + "<script type=\"application/ld+json\">   </script>" + "</head><body>b</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        Assertions.assertNull(data.getValues(HtmlExtractor.JSONLD_RAW_KEY), "empty block must not populate jsonld.raw");
        Assertions.assertNull(data.getValues(HtmlExtractor.JSONLD_TYPE_KEY), "empty block must not populate jsonld.type");
    }

    @Test
    public void test_jsonLd_numericType_ignored() {
        // {@type: 5} — numeric @type is not textual; must not be collected.
        final HtmlExtractor extractor = newExtractor();
        final String json = "{\"@type\":5}";
        final String html = "<html><head><title>T</title>" + "<script type=\"application/ld+json\">" + json + "</script>"
                + "</head><body>b</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        // Raw is captured (it is valid JSON), but type must be absent.
        final String[] raw = data.getValues(HtmlExtractor.JSONLD_RAW_KEY);
        Assertions.assertNotNull(raw, "raw must be captured for valid JSON");
        Assertions.assertNull(data.getValues(HtmlExtractor.JSONLD_TYPE_KEY), "numeric @type must not be collected");
    }

    @Test
    public void test_jsonLd_japaneseType() {
        // UTF-8 encoded Japanese @type value must round-trip correctly.
        final HtmlExtractor extractor = newExtractor();
        final String json = "{\"@type\":\"記事\"}"; // "記事"
        final String html = "<html><head><title>T</title>" + "<script type=\"application/ld+json\">" + json + "</script>"
                + "</head><body>b</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        assertNotNull(types);
        assertEquals(1, types.length);
        Assertions.assertEquals("記事", types[0], "Japanese @type must round-trip correctly");
    }

    @Test
    public void test_multipleOgImage() {
        // Multiple og:image tags → all URLs captured in array.
        final HtmlExtractor extractor = newExtractor();
        final String html = "<html><head>" //
                + "<meta property=\"og:image\" content=\"https://example.com/a.png\">" //
                + "<meta property=\"og:image\" content=\"https://example.com/b.png\">" //
                + "</head><body>b</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        final String[] images = data.getValues("og:image");
        assertNotNull(images);
        Assertions.assertTrue(images.length >= 2, "all og:image URLs must be captured; got " + images.length);
        final List<String> imgList = Arrays.asList(images);
        Assertions.assertTrue(imgList.contains("https://example.com/a.png"), "missing a.png in " + imgList);
        Assertions.assertTrue(imgList.contains("https://example.com/b.png"), "missing b.png in " + imgList);
    }

    @Test
    public void test_jsonLd_corruptJsonContainingNewline_doesNotAbortExtraction() {
        // Malformed JSON with embedded newlines must not abort extraction;
        // the subsequent valid block is still processed.
        final HtmlExtractor extractor = newExtractor();
        final String corrupt = "{\"\n[FAKE]\":\"x"; // deliberately broken
        final String html = "<html><head><title>T</title>" + "<script type=\"application/ld+json\">" + corrupt + "</script>"
                + "<script type=\"application/ld+json\">{\"@type\":\"Article\"}</script>" + "</head><body>body content</body></html>";
        final ExtractData data = extractor.getText(toStream(html), null);
        // Body still extracted.
        assertTrue(data.getContent().contains("body content"));
        // The valid block was still processed.
        final String[] types = data.getValues(HtmlExtractor.JSONLD_TYPE_KEY);
        Assertions.assertNotNull(types, "valid JSON-LD block must still produce types");
        final List<String> typeList = Arrays.asList(types);
        Assertions.assertTrue(typeList.contains("Article"), "Article type must be present: " + typeList);
    }

    @Test
    public void test_getHtml_stringMetadata() {
        // A STRING-typed metadata XPath (string(...)) is extracted as a single trimmed value.
        htmlExtractor.addMetadata("titleStr", "string(//TITLE)");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals(1, data.getValues("titleStr").length);
        assertEquals("タイトル", data.getValues("titleStr")[0]);
    }

    @Test
    public void test_getHtml_booleanMetadata() {
        // A BOOLEAN-typed metadata XPath (boolean(...)) is rendered as "true"/"false".
        htmlExtractor.addMetadata("hasBody", "boolean(//BODY)");
        htmlExtractor.addMetadata("hasTable", "boolean(//TABLE)");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals("true", data.getValues("hasBody")[0]);
        assertEquals("false", data.getValues("hasTable")[0]);
    }

    @Test
    public void test_getHtml_numberMetadata() {
        // A NUMBER-typed metadata XPath (count(...)) is rendered as the number's string form.
        htmlExtractor.addMetadata("divCount", "count(//DIV)");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        CloseableUtil.closeQuietly(in);
        assertEquals("1.0", data.getValues("divCount")[0]);
    }

    @Test
    public void test_getHtml_invalidMetadataXPath() {
        // An invalid metadata XPath must not break content extraction; it yields no values.
        htmlExtractor.addMetadata("bad", "//TITLE[1");
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/test_utf8.html");
        final ExtractData data = htmlExtractor.getText(in, null);
        final String content = data.getContent();
        CloseableUtil.closeQuietly(in);
        assertTrue(content.contains("テスト"));
        assertEquals("タイトル", data.getValues("title")[0]);
        assertEquals(0, data.getValues("bad").length);
    }
}
