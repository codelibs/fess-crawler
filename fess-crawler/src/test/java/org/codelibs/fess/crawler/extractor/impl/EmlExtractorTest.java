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
import java.util.Arrays;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
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
            // Raw header preserves RFC 2047 encoded form when present
            final String raw = data.getValues("Subject")[0];
            assertTrue(raw.contains("=?") || raw.equals("こんにちは"));
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
            assertTrue(content.length() <= 64);
            assertTrue(content.length() < 200);
        }
    }

    @Test
    public void test_maxBodyBytes_largeInputIsBounded() throws Exception {
        // Regression: previous binary-search truncation called text.substring(0, mid).getBytes(UTF_8)
        // O(log N) times, each allocating up to ~N bytes. For very large text parts this
        // self-OOMs and is also catastrophically slow. The CharsetEncoder-based path
        // allocates only ~maxBodyBytes worth of memory once.
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
            assertTrue(elapsedMs < 1000);
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
}
