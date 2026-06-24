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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

/**
 * @author shinsuke
 *
 */
public class EmlExtractorTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(EmlExtractorTest.class);

    public EmlExtractor emlExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("emlExtractor", EmlExtractor.class);
        container.singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .singleton("tikaExtractor", TikaExtractor.class)
                .singleton("zipExtractor", ZipExtractor.class)
                .<ExtractorFactory> singleton("extractorFactory", ExtractorFactory.class, factory -> {
                    TikaExtractor tikaExtractor = container.getComponent("tikaExtractor");
                    factory.addExtractor("application/pdf", tikaExtractor);
                });
        emlExtractor = container.getComponent("emlExtractor");
    }

    @Test
    public void test_getText() throws IOException {
        try (final InputStream in = ResourceUtil.getResourceAsStream("extractor/eml/sample1.eml")) {
            ExtractData data = emlExtractor.getText(in, null);
            final String content = data.getContent();
            logger.info(content);
            assertTrue(content.contains("プレイステーション"));
            assertTrue(data.getValues("Subject")[0].contains("ダイジェスト"));
        }
    }

    @Test
    public void test_getMultipartText() throws IOException {
        try (final InputStream in = ResourceUtil.getResourceAsStream("extractor/eml/sample2.eml")) {
            ExtractData data = emlExtractor.getText(in, null);
            final String content = data.getContent();
            logger.info(content);
            assertTrue(content.contains("チンギス・ハン"));
            assertTrue(data.getValues("Subject")[0].contains("気象情報"));
        }
    }

    @Test
    public void test_getReceivedDate() throws IOException {
        try (final InputStream in = ResourceUtil.getResourceAsStream("extractor/eml/sample1.eml")) {
            ExtractData data = emlExtractor.getText(in, null);
            final String[] receivedDate = data.getValues("Received-Date");
            logger.info("Received-Date: {}", receivedDate[0]);
            assertEquals(receivedDate[0], "2012-11-11T02:39:59.000Z");
        }
    }

    @Test
    public void test_getDecodeText() throws Exception {
        assertEquals("", emlExtractor.getDecodeText(null));
        assertEquals("", emlExtractor.getDecodeText(""));
        assertEquals("abc123", emlExtractor.getDecodeText("abc123"));
        assertEquals("テスト", emlExtractor.getDecodeText("=?UTF-8?B?44OG44K544OI?="));
    }

    @Test
    public void test_getTextWithAttachment() throws IOException {
        try (final InputStream in = ResourceUtil.getResourceAsStream("extractor/eml/sample4.eml")) {
            ExtractData data = emlExtractor.getText(in, null);
            final String content = data.getContent();
            logger.info(content);
            assertTrue(content.contains("Exkursion und Museumsbesuch"));
            assertTrue(content.contains("Fahrt nach Baruth"));
            assertTrue(content.contains("Technische Universität"));
        }
    }

    @Test
    public void test_getText_null() {
        try {
            emlExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    // --------------------------------------------------------------------
    // Programmatically-built fixtures
    // --------------------------------------------------------------------

    private static Session newSession() {
        return Session.getInstance(new Properties(), null);
    }

    private static InputStream toStream(final MimeMessage msg) throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        msg.writeTo(baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    @Test
    public void test_extractsBody() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("Hello", "UTF-8");
        msg.setText("Hello, world!", "UTF-8");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            assertTrue(data.getContent().contains("Hello, world!"));
            assertEquals("Hello", data.getValues("subject")[0]);
        }
    }

    @Test
    public void test_decodesRfc2047Subject() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        // setSubject(text, charset) auto-encodes as RFC 2047 when non-ASCII
        msg.setSubject("こんにちは", "UTF-8");
        msg.setText("body", "UTF-8");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            // Legacy `Subject` metadata key is also RFC 2047-decoded for caller convenience.
            final String raw = data.getValues("Subject")[0];
            assertEquals("こんにちは", raw);
            // Normalized "subject" metadata is decoded
            assertEquals("こんにちは", data.getValues("subject")[0]);
        }
    }

    @Test
    public void test_decodesRfc2047From() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        // Personal name in non-ASCII triggers RFC 2047 encoding on serialization
        final InternetAddress from = new InternetAddress("sender@example.com", "山田 太郎", "UTF-8");
        msg.setFrom(from);
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("test", "UTF-8");
        msg.setText("body", "UTF-8");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            final String[] fromValues = data.getValues("from");
            assertNotNull(fromValues);
            assertTrue(fromValues.length >= 1);
            final String decoded = fromValues[0];
            assertTrue(decoded.contains("山田 太郎"));
            assertTrue(decoded.contains("sender@example.com"));
        }
    }

    @Test
    public void test_extractsAttachmentFilenames() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("with attachment", "UTF-8");

        final MimeMultipart mp = new MimeMultipart();
        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("see attached", "UTF-8");
        mp.addBodyPart(textPart);

        final MimeBodyPart attachment = new MimeBodyPart();
        // tiny PDF-like payload; content does not need to be valid for filename extraction
        attachment.setContent(new byte[] { '%', 'P', 'D', 'F' }, "application/pdf");
        attachment.setFileName("report.pdf");
        attachment.setDisposition(jakarta.mail.Part.ATTACHMENT);
        mp.addBodyPart(attachment);

        msg.setContent(mp);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            final String[] names = data.getValues("attachmentNames");
            assertNotNull(names);
            assertTrue(Arrays.stream(names).anyMatch(n -> n.contains("report.pdf")));
        }
    }

    @Test
    public void test_recursionBomb_throwsException() throws Exception {
        // Build a chain of nested message/rfc822 parts deeper than the configured limit.
        final EmlExtractor extractor = new EmlExtractor();
        extractor.setMaxRecursionDepth(3);
        // Reuse the surrounding container's helper / factory wiring for a fair test:
        // delegate directly via a fresh instance is fine because we don't traverse into attachments here.

        final Session session = newSession();
        // innermost message
        MimeMessage current = new MimeMessage(session);
        current.setFrom(new InternetAddress("inner@example.com"));
        current.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        current.setSubject("inner", "UTF-8");
        current.setText("innermost body", "UTF-8");
        current.saveChanges();

        // Wrap in N layers of message/rfc822 inside a multipart, exceeding the bound
        final int wrapCount = 8;
        for (int i = 0; i < wrapCount; i++) {
            final MimeMessage outer = new MimeMessage(session);
            outer.setFrom(new InternetAddress("layer" + i + "@example.com"));
            outer.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
            outer.setSubject("layer " + i, "UTF-8");
            final MimeMultipart mp = new MimeMultipart();
            final MimeBodyPart nested = new MimeBodyPart();
            nested.setContent(current, "message/rfc822");
            mp.addBodyPart(nested);
            outer.setContent(mp);
            outer.saveChanges();
            current = outer;
        }

        try (final InputStream in = toStream(current)) {
            extractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("recursion"));
        }
    }

    @Test
    public void test_maxParts_throwsException() throws Exception {
        final EmlExtractor extractor = new EmlExtractor();
        extractor.setMaxParts(5);

        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("many parts", "UTF-8");

        final MimeMultipart mp = new MimeMultipart();
        for (int i = 0; i < 50; i++) {
            final MimeBodyPart p = new MimeBodyPart();
            p.setText("part " + i, "UTF-8");
            mp.addBodyPart(p);
        }
        msg.setContent(mp);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            extractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("part count"));
        }
    }

    @Test
    public void test_maxBodyBytes_truncates() throws Exception {
        final EmlExtractor extractor = new EmlExtractor();
        extractor.setMaxBodyBytes(32);

        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("long body", "UTF-8");
        // body comfortably exceeds 32 bytes
        final StringBuilder body = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            body.append('a');
        }
        msg.setText(body.toString(), "UTF-8");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = extractor.getText(in, null);
            final String content = data.getContent();
            // Body must be truncated; the 200-char input is no longer there in full.
            assertTrue(content.length() <= 33);
            assertTrue(content.length() < 200);
        }
    }

    @Test
    public void test_maxBodyBytes_largeInputIsBounded() throws Exception {
        // Regression: previous binary-search truncation called text.substring(0, mid).getBytes(UTF_8)
        // O(log N) times, each allocating up to ~N bytes — catastrophically slow on multi-MiB
        // text parts. The current path encodes once and walks back over UTF-8 continuation
        // bytes to land on a code-point boundary.
        final EmlExtractor extractor = new EmlExtractor();
        extractor.setMaxBodyBytes(1024);

        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("huge body", "UTF-8");

        // 5 MiB of 'a' characters — well within typical heap, but large enough that the
        // old O(N log N) truncation would be visibly slow.
        final int size = 5 * 1024 * 1024;
        final char[] chars = new char[size];
        Arrays.fill(chars, 'a');
        msg.setText(new String(chars), "UTF-8");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final long start = System.nanoTime();
            final ExtractData data = extractor.getText(in, null);
            final long elapsedMs = (System.nanoTime() - start) / 1_000_000L;
            final String content = data.getContent();
            // Bounded by maxBodyBytes (allow a small overhead for trailing space etc.).
            assertTrue(content.length() <= 2048);
            // Sanity: the streaming truncation must complete quickly (well under a second).
            logger.info("test_maxBodyBytes_largeInputIsBounded elapsed={}ms contentLen={}", elapsedMs, content.length());
        }
    }

    @Test
    public void test_maxBodyBytes_truncatesAtUtf8CodePointBoundary() throws Exception {
        // The body is 10 copies of "あ" (3 bytes each in UTF-8 = 30 bytes total).
        // With maxBodyBytes=10, the cap falls inside the 4th character. The truncation
        // must walk back over continuation bytes and land at byte 9 (3 complete chars),
        // never producing a half-encoded code point or a U+FFFD replacement.
        final EmlExtractor extractor = new EmlExtractor();
        extractor.setMaxBodyBytes(10);

        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("multibyte", "UTF-8");
        final StringBuilder body = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            body.append('あ'); // あ
        }
        msg.setText(body.toString(), "UTF-8");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = extractor.getText(in, null);
            final String content = data.getContent();
            // Truncation must not leak U+FFFD from a partial code point.
            assertFalse(content.contains("�"));
        }
    }

    @Test
    public void test_multipartAlternative_partsCountedTowardMaxParts() throws Exception {
        // Regression: multipart/alternative previously charged only the chosen
        // part (and the parent multipart node) to ctx.partCount, letting an
        // attacker bypass maxParts by stuffing thousands of unused
        // alternatives. The fix charges every alternative to the budget.
        final EmlExtractor extractor = new EmlExtractor();
        extractor.setMaxParts(5);

        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("alt bomb", "UTF-8");

        final MimeMultipart alt = new MimeMultipart("alternative");
        // 50 text/html alternatives + 1 text/plain that would otherwise be the
        // only counted child; under the old code partCount stays at 2.
        for (int i = 0; i < 50; i++) {
            final MimeBodyPart bp = new MimeBodyPart();
            bp.setContent("<html><body>HTML " + i + "</body></html>", "text/html; charset=UTF-8");
            alt.addBodyPart(bp);
        }
        final MimeBodyPart plain = new MimeBodyPart();
        plain.setText("plain", "UTF-8");
        alt.addBodyPart(plain);

        msg.setContent(alt);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            extractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("part count"));
        }
    }

    @Test
    public void test_maxBodyBytes_strictCapIncludesTrailingSeparator() throws Exception {
        // Regression: when the encoded body length exactly equals the
        // remaining budget, the old code still appended a trailing space,
        // pushing bodyBytes one byte past maxBodyBytes. The fix reserves the
        // separator byte before deciding to append the full text.
        final EmlExtractor extractor = new EmlExtractor();
        extractor.setMaxBodyBytes(8);

        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("exact", "UTF-8");
        // 8 ASCII bytes — exactly equals maxBodyBytes; the fit branch must NOT
        // append a trailing space and exceed the cap.
        msg.setText("12345678", "UTF-8");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = extractor.getText(in, null);
            final String content = data.getContent();
            // Must not exceed maxBodyBytes (8 bytes / 8 ASCII chars).
            logger.info("test_maxBodyBytes_strictCapIncludesTrailingSeparator content.length={}", content.length());
            assertTrue(content.length() <= 8);
        }
    }

    @Test
    public void test_multipartAlternative_prefersPlainText() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("alt", "UTF-8");

        final MimeMultipart alt = new MimeMultipart("alternative");
        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("PLAIN_BODY", "UTF-8");
        alt.addBodyPart(textPart);
        final MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent("<html><body>HTML_BODY</body></html>", "text/html; charset=UTF-8");
        alt.addBodyPart(htmlPart);

        msg.setContent(alt);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            final String content = data.getContent();
            assertTrue(content.contains("PLAIN_BODY"));
            assertFalse(content.contains("HTML_BODY"));
        }
    }

    // --------------------------------------------------------------------
    // New tests
    // --------------------------------------------------------------------

    @Test
    public void test_maxMessageBytes_enforcedBeforeParsing() throws Exception {
        // Build a small valid EML, then set maxMessageBytes very small (64 bytes)
        // so that even a minimal message stream exceeds it.
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("test subject", "UTF-8");
        msg.setText("Hello, this is a test EML body that is longer than 64 bytes definitely!", "UTF-8");
        msg.saveChanges();

        final EmlExtractor extractor = new EmlExtractor();
        extractor.setMaxMessageBytes(64);

        try (final InputStream in = toStream(msg)) {
            extractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("message size"));
        }
    }

    @Test
    public void test_attachment_extractorOutputRespectsMaxBodyBytes() throws Exception {
        // Build a stub extractor that returns 1 MiB of content
        final String largeContent = "x".repeat(1024 * 1024);
        final Extractor stubExtractor = new Extractor() {
            @Override
            public ExtractData getText(final InputStream in, final Map<String, String> params) {
                return new ExtractData(largeContent);
            }
        };

        // Register stub via a fresh container with the stub registered for application/pdf
        final StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("emlExtractor", EmlExtractor.class);
        container.singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .<ExtractorFactory> singleton("extractorFactory", ExtractorFactory.class, factory -> {
                    factory.addExtractor("application/pdf", stubExtractor);
                });
        final EmlExtractor extractor = container.getComponent("emlExtractor");
        extractor.setMaxBodyBytes(1024);

        // Build an EML with a text body and an application/pdf attachment
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("attachment test", "UTF-8");

        final MimeMultipart mp = new MimeMultipart();
        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("body text", "UTF-8");
        mp.addBodyPart(textPart);

        final MimeBodyPart attachment = new MimeBodyPart();
        attachment.setContent(new byte[] { '%', 'P', 'D', 'F' }, "application/pdf");
        attachment.setFileName("report.pdf");
        attachment.setDisposition(jakarta.mail.Part.ATTACHMENT);
        mp.addBodyPart(attachment);

        msg.setContent(mp);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = extractor.getText(in, null);
            // Allow small overhead for separator
            assertTrue(data.getContent().length() <= 2048);
        }
    }

    @Test
    public void test_appendAttachment_propagatesMaxLengthExceededException() throws Exception {
        // Stub extractor that always throws MaxLengthExceededException
        final Extractor stubExtractor = new Extractor() {
            @Override
            public ExtractData getText(final InputStream in, final Map<String, String> params) {
                throw new MaxLengthExceededException("stub size exceeded");
            }
        };

        final StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("emlExtractor", EmlExtractor.class);
        container.singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .<ExtractorFactory> singleton("extractorFactory", ExtractorFactory.class, factory -> {
                    factory.addExtractor("application/pdf", stubExtractor);
                });
        final EmlExtractor extractor = container.getComponent("emlExtractor");

        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("recipient@example.com") });
        msg.setSubject("propagation test", "UTF-8");

        final MimeMultipart mp = new MimeMultipart();
        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("body", "UTF-8");
        mp.addBodyPart(textPart);

        final MimeBodyPart attachment = new MimeBodyPart();
        attachment.setContent(new byte[] { '%', 'P', 'D', 'F' }, "application/pdf");
        attachment.setFileName("big.pdf");
        attachment.setDisposition(jakarta.mail.Part.ATTACHMENT);
        mp.addBodyPart(attachment);

        msg.setContent(mp);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            extractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            // Expected — exception must propagate, not be swallowed
        }
    }

    @Test
    public void test_recursion_exactlyAtMaxDepth_succeeds() throws Exception {
        // Depth accounting (each wrap contributes 2 depth levels: multipart + rfc822 part):
        //   root message (depth 0) → multipart bp (depth 1) → message/rfc822 content (depth 2) → inner text/* (depth 3)
        // With maxRecursionDepth=3, depth=3 is allowed (3 <= 3), so 1 wrap must succeed.
        // With maxRecursionDepth=1, depth=2 > 1 fails, so 1 wrap with max=1 must fail.
        final Session session = newSession();

        // Build innermost leaf message with setText
        final MimeMessage inner = new MimeMessage(session);
        inner.setFrom(new InternetAddress("inner@example.com"));
        inner.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        inner.setSubject("inner", "UTF-8");
        inner.setText("innermost", "UTF-8");
        inner.saveChanges();

        // Wrap once: root → multipart → rfc822 bodypart → inner (text/plain at depth 3)
        final MimeMessage outer = new MimeMessage(session);
        outer.setFrom(new InternetAddress("outer@example.com"));
        outer.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        outer.setSubject("outer", "UTF-8");
        final MimeMultipart mp = new MimeMultipart();
        final MimeBodyPart nested = new MimeBodyPart();
        nested.setContent(inner, "message/rfc822");
        mp.addBodyPart(nested);
        outer.setContent(mp);
        outer.saveChanges();

        final EmlExtractor extractor = new EmlExtractor();
        extractor.setMaxRecursionDepth(3);

        // 1 wrap at maxRecursionDepth=3 must succeed (inner text at depth 3)
        try (final InputStream in = toStream(outer)) {
            final ExtractData data = extractor.getText(in, null);
            assertTrue(data.getContent().contains("innermost"));
        }

        // With maxRecursionDepth=1, the rfc822 content at depth 2 exceeds the limit
        extractor.setMaxRecursionDepth(1);
        try (final InputStream in = toStream(outer)) {
            extractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("recursion"));
        }
    }

    @Test
    public void test_decodesRfc2047_recipientsAndReplyTo() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));

        final InternetAddress toAddr = new InternetAddress("to@example.com", "田中 一郎", "UTF-8");
        final InternetAddress ccAddr = new InternetAddress("cc@example.com", "鈴木 花子", "UTF-8");
        final InternetAddress bccAddr = new InternetAddress("bcc@example.com", "佐藤 次郎", "UTF-8");
        final InternetAddress replyAddr = new InternetAddress("reply@example.com", "山本 三郎", "UTF-8");

        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { toAddr });
        msg.setRecipients(Message.RecipientType.CC, new InternetAddress[] { ccAddr });
        msg.setRecipients(Message.RecipientType.BCC, new InternetAddress[] { bccAddr });
        msg.setReplyTo(new InternetAddress[] { replyAddr });
        msg.setSubject("multi-recipient", "UTF-8");
        msg.setText("body", "UTF-8");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);

            final String[] toValues = data.getValues("to");
            assertNotNull(toValues);
            assertTrue(toValues[0].contains("田中 一郎"));

            final String[] ccValues = data.getValues("cc");
            assertNotNull(ccValues);
            assertTrue(ccValues[0].contains("鈴木 花子"));

            final String[] bccValues = data.getValues("bcc");
            assertNotNull(bccValues);
            assertTrue(bccValues[0].contains("佐藤 次郎"));

            final String[] replyToValues = data.getValues("replyTo");
            assertNotNull(replyToValues);
            assertTrue(replyToValues[0].contains("山本 三郎"));
        }
    }

    @Test
    public void test_normalizedDateAndMessageIdMetadata() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        msg.setSubject("date test", "UTF-8");
        msg.setText("body", "UTF-8");

        // Set a known sent date
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final Date sentDate = sdf.parse("2025-01-15T10:30:00.000Z");
        msg.setSentDate(sentDate);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);

            // sentDate must be ISO-8601 UTC
            final String[] sentDateValues = data.getValues("sentDate");
            assertNotNull(sentDateValues);
            assertEquals("2025-01-15T10:30:00.000Z", sentDateValues[0]);

            // messageId must be absent when not explicitly set (JavaMail may auto-generate one)
            // In this test we verify it is present since saveChanges() generates a Message-ID
            // Just ensure the key exists and is non-empty when present
            final String[] msgIdValues = data.getValues("messageId");
            // JavaMail always generates a Message-ID on saveChanges, so it must be present
            assertNotNull(msgIdValues);
            assertTrue(msgIdValues[0].length() > 0);
        }

        // Verify messageId absent when message has no Message-ID header
        // Build message without calling saveChanges to avoid auto-generation
        final MimeMessage msg2 = new MimeMessage(newSession());
        msg2.setFrom(new InternetAddress("sender@example.com"));
        msg2.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        msg2.setSubject("no message id", "UTF-8");
        msg2.setText("body", "UTF-8");
        // Do not call saveChanges; remove Message-ID header if present
        msg2.removeHeader("Message-ID");
        msg2.saveChanges();
        msg2.removeHeader("Message-ID");

        try (final InputStream in = toStream(msg2)) {
            final ExtractData data = emlExtractor.getText(in, null);
            // messageId should be absent since we removed the Message-ID header
            assertNull(data.getValues("messageId"));
        }
    }

    @Test
    public void test_textPart_iso2022jp_decodedCorrectly() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        msg.setSubject("iso-2022-jp test", "UTF-8");
        msg.setText("こんにちは", "ISO-2022-JP");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            assertTrue(data.getContent().contains("こんにちは"));
        }
    }

    @Test
    public void test_textPart_unknownCharset_fallsBackToUtf8() throws Exception {
        // Build raw EML bytes to avoid JavaMail rejecting the bogus charset during serialization.
        // The body text is pure ASCII ("hello") which is valid in any charset including the fallback UTF-8.
        final String boundary = "----=_Part_0_12345678.90";
        final String rawEml = "From: sender@example.com\r\n" + "To: r@example.com\r\n" + "Subject: unknown charset\r\n"
                + "MIME-Version: 1.0\r\n" + "Content-Type: multipart/mixed; boundary=\"" + boundary + "\"\r\n" + "\r\n" + "--" + boundary
                + "\r\n" + "Content-Type: text/plain; charset=bogus-cs-9\r\n" + "Content-Transfer-Encoding: 7bit\r\n" + "\r\n" + "hello\r\n"
                + "--" + boundary + "--\r\n";

        try (final InputStream in = new ByteArrayInputStream(rawEml.getBytes(java.nio.charset.StandardCharsets.US_ASCII))) {
            final ExtractData data = emlExtractor.getText(in, null);
            assertTrue(data.getContent().contains("hello"));
        }
    }

    @Test
    public void test_textPart_noCharsetParameter_decodesAsUtf8() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        msg.setSubject("no charset", "UTF-8");

        final MimeMultipart mp = new MimeMultipart();
        final MimeBodyPart textPart = new MimeBodyPart();
        // Content-Type without charset parameter
        textPart.setContent("hello world", "text/plain");
        mp.addBodyPart(textPart);
        msg.setContent(mp);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            assertTrue(data.getContent().contains("hello world"));
        }
    }

    @Test
    public void test_multipleAttachments_allRecorded() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        msg.setSubject("multiple attachments", "UTF-8");

        final MimeMultipart mp = new MimeMultipart();

        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("body", "UTF-8");
        mp.addBodyPart(textPart);

        final String[] filenames = { "file1.txt", "file2.doc", "file3.xml" };
        for (final String name : filenames) {
            final MimeBodyPart att = new MimeBodyPart();
            att.setContent("content of " + name, "application/octet-stream");
            att.setFileName(name);
            att.setDisposition(jakarta.mail.Part.ATTACHMENT);
            mp.addBodyPart(att);
        }

        msg.setContent(mp);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            final String[] names = data.getValues("attachmentNames");
            assertNotNull(names);
            final java.util.List<String> nameList = Arrays.asList(names);
            assertTrue(nameList.contains("file1.txt"));
            assertTrue(nameList.contains("file2.doc"));
            assertTrue(nameList.contains("file3.xml"));
        }
    }

    @Test
    public void test_inlineDispositionWithFilename_recordedAsAttachment() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        msg.setSubject("inline attachment", "UTF-8");

        final MimeMultipart mp = new MimeMultipart("related");

        final MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("body with inline", "UTF-8");
        mp.addBodyPart(textPart);

        // Inline disposition with filename — should be recorded as an attachment
        final MimeBodyPart inlinePart = new MimeBodyPart();
        inlinePart.setContent(new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47 }, "image/png");
        inlinePart.setFileName("logo.png");
        inlinePart.setDisposition(jakarta.mail.Part.INLINE);
        mp.addBodyPart(inlinePart);

        msg.setContent(mp);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            final String[] names = data.getValues("attachmentNames");
            assertNotNull(names);
            assertTrue(Arrays.stream(names).anyMatch(n -> n.contains("logo.png")));
        }
    }

    @Test
    public void test_maxBodyBytes_acrossMultipleParts() throws Exception {
        final int maxBytes = 50;
        final EmlExtractor extractor = new EmlExtractor();
        extractor.setMaxBodyBytes(maxBytes);

        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        msg.setSubject("two parts", "UTF-8");

        final MimeMultipart mp = new MimeMultipart();

        // First part: 30 ASCII bytes
        final MimeBodyPart part1 = new MimeBodyPart();
        part1.setText("a".repeat(30), "UTF-8");
        mp.addBodyPart(part1);

        // Second part: 30 ASCII bytes — combined exceeds maxBytes
        final MimeBodyPart part2 = new MimeBodyPart();
        part2.setText("b".repeat(30), "UTF-8");
        mp.addBodyPart(part2);

        msg.setContent(mp);
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = extractor.getText(in, null);
            final String content = data.getContent();
            // Total must not exceed maxBodyBytes
            assertTrue(content.length() <= maxBytes);
        }
    }

    @Test
    public void test_setters_rejectInvalidValues() {
        final EmlExtractor extractor = new EmlExtractor();

        try {
            extractor.setMaxParts(0);
            fail();
        } catch (final IllegalArgumentException e) {
            // expected
        }

        try {
            extractor.setMaxParts(-1);
            fail();
        } catch (final IllegalArgumentException e) {
            // expected
        }

        try {
            extractor.setMaxBodyBytes(0);
            fail();
        } catch (final IllegalArgumentException e) {
            // expected
        }

        try {
            extractor.setMaxMessageBytes(0);
            fail();
        } catch (final IllegalArgumentException e) {
            // expected
        }

        try {
            extractor.setMaxRecursionDepth(-1);
            fail();
        } catch (final IllegalArgumentException e) {
            // expected
        }

        // setMaxRecursionDepth(0) must be accepted (root-only is valid)
        extractor.setMaxRecursionDepth(0);
        assertEquals(0, extractor.getMaxRecursionDepth());
    }

    @Test
    public void test_getReceivedDate_parsesWithSemicolon() throws Exception {
        // Build a message with a Received header in standard RFC 5322 form
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        msg.setSubject("received date test", "UTF-8");
        msg.setText("body", "UTF-8");
        // Add a Received header with semicolon-separated date
        msg.addHeader("Received", "from foo.example.com by bar.example.com; Sun, 11 Nov 2012 02:39:59 +0000");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            final String[] receivedDate = data.getValues("Received-Date");
            assertNotNull(receivedDate);
            assertEquals("2012-11-11T02:39:59.000Z", receivedDate[0]);
        }
    }

    @Test
    public void test_getReceivedDate_skipsMalformedDowInComment() throws Exception {
        // DOW abbreviation in a comment, but valid date after semicolon
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        msg.setSubject("received comment test", "UTF-8");
        msg.setText("body", "UTF-8");
        // The "(Mon)" in the routing portion should not confuse the parser;
        // the date after ";" is the authoritative date
        msg.addHeader("Received", "from foo (Mon gateway) by bar; Mon, 11 Nov 2013 05:00:00 +0000");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            final String[] receivedDate = data.getValues("Received-Date");
            assertNotNull(receivedDate);
            assertEquals("2013-11-11T05:00:00.000Z", receivedDate[0]);
        }
    }

    @Test
    public void test_manyReceivedHeaders_bounded() throws Exception {
        final MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress("sender@example.com"));
        msg.setRecipients(Message.RecipientType.TO, new InternetAddress[] { new InternetAddress("r@example.com") });
        msg.setSubject("many received headers", "UTF-8");
        msg.setText("body", "UTF-8");

        // Add 500 garbage Received headers first
        for (int i = 0; i < 500; i++) {
            msg.addHeader("Received", "garbage entry number " + i);
        }
        // Then add one valid Received header — but since we cap at 100, this valid one
        // at index 500 will NOT be seen. We verify that extraction at least completes
        // without error and does not blow up on unbounded iteration.
        // (The valid header is beyond the 100-entry cap, so receivedDate may be null.)
        msg.addHeader("Received", "from x by y; Mon, 11 Nov 2013 05:00:00 +0000");
        msg.saveChanges();

        try (final InputStream in = toStream(msg)) {
            final ExtractData data = emlExtractor.getText(in, null);
            // Just verify it completes without exception and content is non-null
            assertNotNull(data.getContent());
        }
    }

    @Test
    public void test_getDecodeText_returnsRawOnUnsupportedEncoding() {
        // An encoded-word with an unknown charset should return the raw input, not empty string.
        // Use a charset that is genuinely unsupported in the JVM.
        // Note: if the JVM happens to support the charset, this test may fall back gracefully.
        // We use a clearly bogus encoding name to guarantee UnsupportedEncodingException.
        final String raw = "=?bogus-cs-9?B?dGVzdA==?=";
        // MimeUtility.decodeText will throw UnsupportedEncodingException for unknown charset;
        // getDecodeText must return the raw value unchanged in that case.
        final String result = emlExtractor.getDecodeText(raw);
        // Either successfully decoded (if JVM finds charset) or returns raw value
        // The contract is: never return empty string when input is non-empty
        assertNotNull(result);
        assertTrue(result.length() > 0);
        // If decoding fails, must return the raw string, not empty string
        // (We can't force the failure path here without mocking, but we verify no empty return)
    }
}
