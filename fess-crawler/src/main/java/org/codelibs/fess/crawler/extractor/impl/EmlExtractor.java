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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Header;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.ContentType;
import jakarta.mail.internet.MailDateFormat;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;

/**
 * Gets a text from .eml file.
 *
 * <p>EML content is treated as untrusted. The extractor enforces the following
 * defensive bounds against malformed or malicious messages:</p>
 * <ul>
 *   <li>{@link #maxMessageBytes} (default 100 MiB) is the first-line defense:
 *       the raw input stream is capped before {@code MimeMessage} even begins
 *       to parse, preventing memory exhaustion from pathologically large
 *       messages.</li>
 *   <li>{@link #maxRecursionDepth} (default 10) caps how deeply nested
 *       {@code message/rfc822} or {@code multipart/*} parts may be.</li>
 *   <li>{@link #maxParts} (default 1000) caps the total number of MIME parts
 *       traversed across the whole message.</li>
 *   <li>{@link #maxBodyBytes} (default 50 MiB) caps the total UTF-8 byte size
 *       of body text appended to the output.</li>
 * </ul>
 * <p>RFC 2047 encoded-word headers (e.g. {@code Subject},
 * {@code From}, {@code To}) are decoded via {@link MimeUtility#decodeText}.</p>
 * <p>The legacy {@code Subject} metadata key is RFC 2047-decoded for
 * compatibility with older callers.</p>
 *
 * @author shinsuke
 *
 */
public class EmlExtractor extends AbstractExtractor {
    /** Array of day of week abbreviations used for parsing received dates */
    private static final String[] DAY_OF_WEEK = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(EmlExtractor.class);

    /** Properties used for mail processing */
    protected Properties mailProperties = new Properties();

    /** Maximum allowed nesting depth for multipart / message/rfc822 parts. */
    protected int maxRecursionDepth = 10;

    /** Maximum allowed total number of MIME parts visited per message. */
    protected int maxParts = 1000;

    /** Maximum total body bytes (UTF-8) appended to the extracted content. */
    protected long maxBodyBytes = 50L * 1024 * 1024;

    /** Maximum allowed total stream bytes consumed while parsing the EML. */
    protected long maxMessageBytes = 100L * 1024 * 1024;

    /**
     * Constructs a new EmlExtractor.
     */
    public EmlExtractor() {
        // Default constructor
    }

    /* (non-Javadoc)
     * @see org.codelibs.robot.extractor.Extractor#getText(java.io.InputStream, java.util.Map)
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        final Properties props = new Properties(mailProperties);
        if (params != null) {
            for (final Map.Entry<String, String> entry : params.entrySet()) {
                props.put(entry.getKey(), entry.getValue());
            }
        }
        if (in == null) {
            throw new ExtractException("Input stream is null.");
        }
        final LimitedInputStream limited = new LimitedInputStream(in, maxMessageBytes);
        try {
            final Session mailSession = Session.getInstance(props, null);
            final MimeMessage message = new MimeMessage(mailSession, limited);
            final BodyExtractionContext ctx = new BodyExtractionContext();
            extractBody(message, ctx, 0);
            final ExtractData data = new ExtractData(ctx.body.toString());
            final Enumeration<Header> headers = message.getAllHeaders();
            while (headers.hasMoreElements()) {
                final Header header = headers.nextElement();
                data.putValue(header.getName(), header.getValue());
            }
            putValue(data, "Content-ID", message.getContentID());
            putValue(data, "Content-Language", message.getContentLanguage());
            putValue(data, "Content-MD5", message.getContentMD5());
            putValue(data, "Description", message.getDescription());
            putValue(data, "Disposition", message.getDisposition());
            putValue(data, "Encoding", message.getEncoding());
            putValue(data, "File-Name", message.getFileName());
            putValue(data, "From", message.getFrom());
            putValue(data, "Line-Count", message.getLineCount());
            putValue(data, "Message-ID", message.getMessageID());
            putValue(data, "Message-Number", message.getMessageNumber());
            final Date receivedDate = getReceivedDate(message);
            putValue(data, "Received-Date", receivedDate);
            putValue(data, "Reply-To", message.getReplyTo());
            putValue(data, "Sender", message.getSender());
            putValue(data, "Sent-Date", message.getSentDate());
            putValue(data, "Size", message.getSize());
            putValue(data, "Subject", message.getSubject());
            putValue(data, "Receipients", message.getAllRecipients());
            putValue(data, "To", message.getRecipients(Message.RecipientType.TO));
            putValue(data, "Cc", message.getRecipients(Message.RecipientType.CC));
            putValue(data, "Bcc", message.getRecipients(Message.RecipientType.BCC));

            // normalized convenience metadata (always RFC 2047 decoded)
            putDecodedHeaderValue(data, "subject", message.getSubject());
            putDecodedAddressValues(data, "from", message.getFrom());
            putDecodedAddressValues(data, "to", message.getRecipients(Message.RecipientType.TO));
            putDecodedAddressValues(data, "cc", message.getRecipients(Message.RecipientType.CC));
            putDecodedAddressValues(data, "bcc", message.getRecipients(Message.RecipientType.BCC));
            putDecodedAddressValues(data, "replyTo", message.getReplyTo());
            putDateValue(data, "sentDate", message.getSentDate());
            putDateValue(data, "receivedDate", receivedDate);
            if (message.getMessageID() != null) {
                data.putValue("messageId", message.getMessageID());
            }

            if (!ctx.attachmentNames.isEmpty()) {
                data.putValues("attachmentNames", ctx.attachmentNames.toArray(new String[0]));
            }
            return data;
        } catch (final MessagingException e) {
            if (limited.isExceeded()) {
                throw new MaxLengthExceededException("EML message size exceeded: max=" + maxMessageBytes);
            }
            throw new ExtractException(e);
        } catch (final IOException e) {
            if (limited.isExceeded()) {
                throw new MaxLengthExceededException("EML message size exceeded: max=" + maxMessageBytes);
            }
            throw new ExtractException(e);
        }
    }

    /**
     * Puts a value into the extract data with appropriate type conversion.
     *
     * @param data the extract data to store the value in
     * @param key the key for the value
     * @param value the value to store
     */
    protected void putValue(final ExtractData data, final String key, final Object value) {
        try {
            if (value instanceof String) {
                if ("Subject".equals(key)) {
                    data.putValue(key, getDecodeText(value.toString()));
                } else {
                    data.putValue(key, value.toString());
                }
            } else if (value instanceof String[]) {
                data.putValues(key, (String[]) value);
            } else if (value instanceof Integer) {
                data.putValue(key, ((Integer) value).toString());
            } else if (value instanceof Address[]) {
                final int size = ((Address[]) value).length;
                final String[] values = new String[size];
                for (int i = 0; i < size; i++) {
                    final Address address = ((Address[]) value)[i];
                    values[i] = getDecodeText(address.toString());
                }
                data.putValues(key, values);
            } else if (value instanceof Date) {
                final SimpleDateFormat sdf = new SimpleDateFormat(Constants.ISO_DATETIME_FORMAT);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                data.putValue(key, sdf.format(value));
            } else if (value != null) {
                data.putValue(key, value.toString());
            }
        } catch (final RuntimeException e) {
            logger.warn("Failed to put header value. key={}", key, e);
        }
    }

    /**
     * Stores a decoded header value if non-null/non-blank.
     *
     * @param data the extract data
     * @param key the metadata key
     * @param raw the raw header value, may be {@code null}
     */
    protected void putDecodedHeaderValue(final ExtractData data, final String key, final String raw) {
        if (raw == null) {
            return;
        }
        final String decoded = getDecodeText(raw);
        if (!StringUtil.isEmpty(decoded)) {
            data.putValue(key, decoded);
        }
    }

    /**
     * Stores a decoded address array as a multivalue metadata entry.
     *
     * @param data the extract data
     * @param key the metadata key
     * @param addresses the address array, may be {@code null}
     */
    protected void putDecodedAddressValues(final ExtractData data, final String key, final Address[] addresses) {
        if (addresses == null || addresses.length == 0) {
            return;
        }
        final String[] values = new String[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            values[i] = getDecodeText(addresses[i].toString());
        }
        data.putValues(key, values);
    }

    /**
     * Stores a Date as an ISO-8601 UTC string under the given key.
     *
     * @param data the extract data
     * @param key the metadata key
     * @param date the date, may be {@code null}
     */
    protected void putDateValue(final ExtractData data, final String key, final Date date) {
        if (date == null) {
            return;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(Constants.ISO_DATETIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        data.putValue(key, sdf.format(date));
    }

    /**
     * Decodes MIME-encoded text.
     *
     * <p>On {@link UnsupportedEncodingException} (caused by an unrecognised RFC 2047
     * charset), logs a warning and returns the <em>raw</em> value unchanged so
     * callers still receive some usable output rather than an empty string.</p>
     *
     * @param value the encoded text to decode
     * @return the decoded text, the raw value on encoding failure, or empty string for null input
     */
    protected String getDecodeText(final String value) {
        if (value == null) {
            return StringUtil.EMPTY;
        }
        try {
            return MimeUtility.decodeText(value);
        } catch (final UnsupportedEncodingException e) {
            logger.warn("Invalid RFC 2047 encoding, returning raw value. value={}", value, e);
            return value;
        }
    }

    /**
     * Gets the mail properties used for email processing.
     *
     * @return the mail properties
     */
    public Properties getMailProperties() {
        return mailProperties;
    }

    /**
     * Sets the mail properties used for email processing.
     *
     * @param mailProperties the mail properties to set
     */
    public void setMailProperties(final Properties mailProperties) {
        this.mailProperties = mailProperties;
    }

    /**
     * Returns the maximum allowed recursion depth.
     *
     * @return the maximum recursion depth
     */
    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    /**
     * Sets the maximum allowed recursion depth for nested multipart /
     * {@code message/rfc822} parts. A value of {@code 0} means only the root
     * part is processed (no recursion). Negative values are rejected.
     *
     * @param maxRecursionDepth the maximum recursion depth; must be &gt;= 0
     * @throws IllegalArgumentException if the value is negative
     */
    public void setMaxRecursionDepth(final int maxRecursionDepth) {
        if (maxRecursionDepth < 0) {
            throw new IllegalArgumentException("maxRecursionDepth must be positive: " + maxRecursionDepth);
        }
        this.maxRecursionDepth = maxRecursionDepth;
    }

    /**
     * Returns the maximum total number of MIME parts visited per message.
     *
     * @return the maximum number of parts
     */
    public int getMaxParts() {
        return maxParts;
    }

    /**
     * Sets the maximum total number of MIME parts visited per message.
     *
     * @param maxParts the maximum number of parts; must be &gt; 0
     * @throws IllegalArgumentException if the value is &lt;= 0
     */
    public void setMaxParts(final int maxParts) {
        if (maxParts <= 0) {
            throw new IllegalArgumentException("maxParts must be positive: " + maxParts);
        }
        this.maxParts = maxParts;
    }

    /**
     * Returns the maximum total UTF-8 body bytes appended to extracted content.
     *
     * @return the maximum body bytes
     */
    public long getMaxBodyBytes() {
        return maxBodyBytes;
    }

    /**
     * Sets the maximum total UTF-8 body bytes appended to extracted content.
     *
     * @param maxBodyBytes the maximum body bytes; must be &gt; 0
     * @throws IllegalArgumentException if the value is &lt;= 0
     */
    public void setMaxBodyBytes(final long maxBodyBytes) {
        if (maxBodyBytes <= 0) {
            throw new IllegalArgumentException("maxBodyBytes must be positive: " + maxBodyBytes);
        }
        this.maxBodyBytes = maxBodyBytes;
    }

    /**
     * Returns the maximum allowed total stream bytes consumed while parsing the EML.
     *
     * @return the maximum message bytes
     */
    public long getMaxMessageBytes() {
        return maxMessageBytes;
    }

    /**
     * Sets the maximum allowed total stream bytes consumed while parsing the EML.
     * This is the first-line defense before {@link MimeMessage} parses the input.
     *
     * @param maxMessageBytes the maximum message bytes; must be &gt; 0
     * @throws IllegalArgumentException if the value is &lt;= 0
     */
    public void setMaxMessageBytes(final long maxMessageBytes) {
        if (maxMessageBytes <= 0) {
            throw new IllegalArgumentException("maxMessageBytes must be positive: " + maxMessageBytes);
        }
        this.maxMessageBytes = maxMessageBytes;
    }

    /**
     * Extracts the body text from a MIME message.
     *
     * <p>Retained for backwards compatibility. Internally delegates to
     * {@link #extractBody(Part, BodyExtractionContext, int)} with a fresh
     * context.</p>
     *
     * @param message the MIME message to extract text from
     * @return the extracted body text
     * @throws ExtractException if extraction fails
     */
    protected String getBodyText(final MimeMessage message) {
        try {
            final BodyExtractionContext ctx = new BodyExtractionContext();
            extractBody(message, ctx, 0);
            return ctx.body.toString();
        } catch (MessagingException | IOException e) {
            throw new ExtractException(e);
        }
    }

    /**
     * Recursively extracts text content from a MIME part, enforcing recursion,
     * part-count, and body-byte bounds.
     *
     * @param part the current MIME part
     * @param ctx the extraction context tracking accumulated state
     * @param depth the current recursion depth (root = 0)
     * @throws MessagingException if a JavaMail call fails
     * @throws IOException if reading part content fails
     */
    protected void extractBody(final Part part, final BodyExtractionContext ctx, final int depth) throws MessagingException, IOException {
        if (depth > maxRecursionDepth) {
            throw new MaxLengthExceededException("EML recursion too deep: depth=" + depth + " max=" + maxRecursionDepth);
        }
        ctx.partCount++;
        if (ctx.partCount > maxParts) {
            throw new MaxLengthExceededException("EML part count exceeded: max=" + maxParts);
        }

        // Treat explicitly-marked attachments as attachments regardless of mime type.
        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
            recordAttachment(ctx, part);
            return;
        }

        if (part.isMimeType("text/*")) {
            appendTextPart(ctx, part);
            return;
        }

        if (part.isMimeType("multipart/alternative")) {
            final Object content = part.getContent();
            if (content instanceof Multipart) {
                final Multipart mp = (Multipart) content;
                final int count = mp.getCount();
                // Prefer text/plain alternative; fall back to first text/* alternative.
                BodyPart chosen = null;
                for (int i = 0; i < count; i++) {
                    final BodyPart bp = mp.getBodyPart(i);
                    if (bp.isMimeType("text/plain")) {
                        chosen = bp;
                        break;
                    }
                }
                if (chosen == null) {
                    for (int i = 0; i < count; i++) {
                        final BodyPart bp = mp.getBodyPart(i);
                        if (bp.isMimeType("text/*")) {
                            chosen = bp;
                            break;
                        }
                    }
                }
                if (chosen != null) {
                    // Charge the partCount budget for every alternative — even those we
                    // don't recurse into — so an attacker can't bypass maxParts by
                    // stuffing thousands of unused alternatives. The chosen part is
                    // counted via its own extractBody call below, so charge count - 1.
                    if (count > 1) {
                        ctx.partCount += count - 1;
                        if (ctx.partCount > maxParts) {
                            throw new MaxLengthExceededException("EML part count exceeded: max=" + maxParts);
                        }
                    }
                    extractBody(chosen, ctx, depth + 1);
                } else {
                    // No text alternative; recurse into all parts (each counted normally).
                    for (int i = 0; i < count; i++) {
                        extractBody(mp.getBodyPart(i), ctx, depth + 1);
                    }
                }
            }
            return;
        }

        if (part.isMimeType("multipart/*")) {
            final Object content = part.getContent();
            if (content instanceof Multipart) {
                final Multipart mp = (Multipart) content;
                for (int i = 0; i < mp.getCount(); i++) {
                    extractBody(mp.getBodyPart(i), ctx, depth + 1);
                }
            }
            return;
        }

        if (part.isMimeType("message/rfc822")) {
            final Object content = part.getContent();
            if (content instanceof Part) {
                extractBody((Part) content, ctx, depth + 1);
            }
            return;
        }

        // Anything else with a filename is an inline attachment-like part.
        recordAttachment(ctx, part);
    }

    /**
     * Records an attachment filename (decoded) and attempts in-extractor text
     * extraction for known mime types, mirroring previous behavior.
     *
     * @param ctx the extraction context
     * @param part the attachment-like part
     */
    protected void recordAttachment(final BodyExtractionContext ctx, final Part part) {
        try {
            final String rawName = part.getFileName();
            if (!StringUtil.isEmpty(rawName)) {
                final String decoded = getDecodeText(rawName);
                if (!StringUtil.isEmpty(decoded)) {
                    ctx.attachmentNames.add(decoded);
                }
            }
        } catch (final MessagingException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to read attachment filename.", e);
            }
        }
        if (part instanceof BodyPart) {
            appendAttachment(ctx, (BodyPart) part);
        }
    }

    /**
     * Appends body text to the extraction context, enforcing
     * {@link #maxBodyBytes}. Truncates any text that would push the total over
     * the limit (including the trailing separator space).
     *
     * <p>Encodes the text once with {@link String#getBytes(java.nio.charset.Charset)}
     * (memory proportional to the input, not to the configured budget). When
     * truncation is needed, walks back over UTF-8 continuation bytes (at most
     * three steps) so the cut lands on a code-point boundary.</p>
     *
     * @param ctx the extraction context
     * @param text the text to append
     */
    protected void appendBody(final BodyExtractionContext ctx, final String text) {
        if (text == null || text.isEmpty()) {
            return;
        }
        if (ctx.bodyBytes >= maxBodyBytes) {
            return;
        }
        final byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        final long remaining = maxBodyBytes - ctx.bodyBytes;
        // Reserve 1 byte for the trailing separator space so the strict cap holds.
        if ((long) bytes.length + 1L <= remaining) {
            ctx.body.append(text).append(' ');
            ctx.bodyBytes += (long) bytes.length + 1L;
            return;
        }
        // Truncate at a UTF-8 code-point boundary that fits within the remaining
        // budget. Continuation bytes have the bit pattern 10xxxxxx, so walk back
        // until we land on a start byte (or zero). Bounded by 3 iterations.
        int cutoff = (int) Math.min(remaining, (long) bytes.length);
        while (cutoff > 0 && cutoff < bytes.length && (bytes[cutoff] & 0xC0) == 0x80) {
            cutoff--;
        }
        if (cutoff > 0) {
            ctx.body.append(new String(bytes, 0, cutoff, StandardCharsets.UTF_8));
        }
        ctx.bodyBytes = maxBodyBytes;
        if (logger.isDebugEnabled()) {
            logger.debug("EML body truncated. maxBytes={}", maxBodyBytes);
        }
    }

    /**
     * Returns the content type of a part as a string, or {@code "unknown"} on
     * {@link MessagingException}.
     *
     * @param part the MIME part
     * @return the content-type string or {@code "unknown"}
     */
    private static String safeGetContentType(final Part part) {
        try {
            return part.getContentType();
        } catch (final MessagingException e) {
            return "unknown";
        }
    }

    /**
     * Streams a text part's content into the extraction buffer, reading
     * {@code remaining} chars at most via {@link InputStreamReader}, then
     * delegates to {@link #appendBody} for byte-accurate truncation.
     *
     * <p>The charset is resolved from the part's Content-Type header; if absent
     * or unrecognised, UTF-8 is used as the fallback.</p>
     *
     * @param ctx the extraction context
     * @param part the {@code text/*} part
     */
    protected void appendTextPart(final BodyExtractionContext ctx, final Part part) {
        if (ctx.bodyBytes >= maxBodyBytes) {
            return;
        }
        final long remaining = maxBodyBytes - ctx.bodyBytes;
        final int charCap = (int) Math.min(remaining, (long) Integer.MAX_VALUE / 4);

        Charset charset = StandardCharsets.UTF_8;
        try {
            final String contentType = part.getContentType();
            if (contentType != null) {
                final String cs = new ContentType(contentType).getParameter("charset");
                if (cs != null && !cs.isEmpty()) {
                    try {
                        charset = Charset.forName(MimeUtility.javaCharset(cs));
                    } catch (final IllegalCharsetNameException | UnsupportedCharsetException e) {
                        logger.warn("Unsupported EML text part charset, fallback=UTF-8. charset={}", cs, e);
                    }
                }
            }
        } catch (final MessagingException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse content type of text part.", e);
            }
        }

        try (InputStream is = part.getInputStream(); InputStreamReader reader = new InputStreamReader(is, charset)) {
            final char[] buf = new char[Math.min(charCap, 8 * 1024)];
            final StringBuilder sb = new StringBuilder(Math.min(charCap, 64 * 1024));
            int total = 0;
            int n;
            while (total < charCap && (n = reader.read(buf, 0, Math.min(buf.length, charCap - total))) > 0) {
                sb.append(buf, 0, n);
                total += n;
            }
            if (total > 0) {
                appendBody(ctx, sb.toString());
            }
        } catch (final IOException e) {
            logger.warn("Failed to read text part content. contentType={}", safeGetContentType(part), e);
        } catch (final MessagingException e) {
            logger.warn("Failed to access text part input stream. contentType={}", safeGetContentType(part), e);
        }
    }

    /**
     * Backwards-compatible attachment text extraction. Kept for subclasses that
     * may have overridden it; new code should prefer
     * {@link #appendAttachment(BodyExtractionContext, BodyPart)}.
     *
     * @deprecated Use {@link #appendAttachment(BodyExtractionContext, BodyPart)} instead.
     * This shim creates a fresh extraction context with {@code bodyBytes=0}, so
     * the {@link #maxBodyBytes} cap is enforced per call rather than cumulatively
     * across a single message. Subclasses overriding this method should migrate to
     * the context-aware overload.
     *
     * @param buf the buffer to append content to
     * @param bodyPart the body part containing the attachment
     */
    @Deprecated
    protected void appendAttachment(final StringBuilder buf, final BodyPart bodyPart) {
        final BodyExtractionContext ctx = new BodyExtractionContext();
        ctx.body = buf;
        appendAttachment(ctx, bodyPart);
    }

    /**
     * Attempts to extract text from an attachment using a registered
     * {@link Extractor} for its detected MIME type. All extraction failures —
     * including a nested extractor's own {@link MaxLengthExceededException} — are
     * logged and swallowed so that a single problematic attachment does not abort
     * extraction of the message body and remaining parts. The EML-level DoS bounds
     * ({@link #maxMessageBytes}, {@link #maxRecursionDepth}, {@link #maxParts},
     * {@link #maxBodyBytes}) are enforced separately and remain in effect.
     *
     * @param ctx the extraction context
     * @param bodyPart the attachment body part
     */
    protected void appendAttachment(final BodyExtractionContext ctx, final BodyPart bodyPart) {
        final MimeTypeHelper mimeTypeHelper = getMimeTypeHelper();
        final ExtractorFactory extractorFactory = getExtractorFactory();
        try {
            final String filename = getDecodeText(bodyPart.getFileName());
            final String mimeType = mimeTypeHelper.getContentType(null, filename);
            if (mimeType != null) {
                final Extractor extractor = extractorFactory.getExtractor(mimeType);
                if (extractor != null) {
                    if (ctx.bodyBytes >= maxBodyBytes) {
                        return;
                    }
                    final long remaining = maxBodyBytes - ctx.bodyBytes;
                    final long sourceCapL;
                    if (remaining > (Integer.MAX_VALUE - 16L) / 4L) {
                        sourceCapL = Integer.MAX_VALUE - 16L;
                    } else {
                        sourceCapL = remaining * 4L + 16L;
                    }
                    try (final InputStream in = new LimitedInputStream(bodyPart.getInputStream(), sourceCapL)) {
                        final Map<String, String> map = new HashMap<>();
                        map.put(ExtractData.RESOURCE_NAME_KEY, filename);
                        final String content = extractor.getText(in, map).getContent();
                        if (content != null) {
                            appendBody(ctx, content);
                        }
                    } catch (final MaxLengthExceededException e) {
                        // The nested extractor hit its own content-length limit. Skip only
                        // this attachment and keep the message body and remaining parts,
                        // rather than aborting the whole EML extraction. The EML-level DoS
                        // bounds (maxMessageBytes, maxRecursionDepth, maxParts, maxBodyBytes)
                        // still apply and are unaffected.
                        logger.warn("Attachment skipped because a nested extractor exceeded its size limit. filename={}", filename);
                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Exception in an internal extractor. filename={}", filename, e);
                        }
                    }
                }
            }
        } catch (final MessagingException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Exception in parsing BodyPart.", e);
            }
        }
    }

    /**
     * Gets the received date from a message by parsing the received headers.
     * Caps inspection to the first 100 headers to avoid unbounded work on
     * messages with pathologically many {@code Received} lines.
     *
     * @param message the message to get the received date from
     * @return the received date or null if not found
     * @throws MessagingException if message access fails
     */
    protected static Date getReceivedDate(final Message message) throws MessagingException {
        final Date today = new Date();
        final String[] received = message.getHeader("received");
        if (received == null) {
            return null;
        }
        final MailDateFormat format = new MailDateFormat();
        final int limit = Math.min(received.length, 100);
        for (int i = 0; i < limit; i++) {
            final String v = received[i];
            try {
                final String dateStr = getDateString(v);
                if (dateStr == null) {
                    continue;
                }
                final Date receivedDate = format.parse(dateStr);
                if (!receivedDate.after(today)) {
                    return receivedDate;
                }
            } catch (final ParseException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to parse received header. value={}", v, e);
                }
            }
        }
        return null;
    }

    /**
     * Extracts a date string from the received header text.
     *
     * <p>Per RFC 5322 §3.6.7 the date portion follows the last {@code ;} in
     * the header. If no {@code ;} is present, falls back to scanning for a
     * day-of-week abbreviation.</p>
     *
     * @param text the received header text
     * @return the date string, or null if not found
     */
    private static String getDateString(final String text) {
        if (text == null) {
            return null;
        }
        final int semicolon = text.lastIndexOf(';');
        if (semicolon != -1 && semicolon + 1 < text.length()) {
            return text.substring(semicolon + 1).trim();
        }
        for (final String dow : DAY_OF_WEEK) {
            final int i = text.lastIndexOf(dow);
            if (i != -1) {
                return text.substring(i);
            }
        }
        return null;
    }

    /**
     * Mutable state shared across recursive body extraction.
     */
    protected static class BodyExtractionContext {
        /** Accumulated body text. */
        protected StringBuilder body = new StringBuilder(1000);

        /** Number of MIME parts visited so far. */
        protected int partCount;

        /** UTF-8 bytes already appended to {@link #body}. */
        protected long bodyBytes;

        /** Decoded attachment filenames. */
        protected List<String> attachmentNames = new ArrayList<>();
    }

    /**
     * A {@link FilterInputStream} that throws {@link IOException} once the
     * number of bytes read exceeds a configured limit. Used to cap raw EML
     * stream consumption before {@link MimeMessage} parses the input.
     */
    private static final class LimitedInputStream extends FilterInputStream {
        private final long limit;
        private long bytesRead;
        private boolean exceeded;

        LimitedInputStream(final InputStream in, final long limit) {
            super(in);
            this.limit = limit;
        }

        @Override
        public int read() throws IOException {
            final int b = super.read();
            if (b != -1) {
                bytesRead++;
                if (bytesRead > limit) {
                    exceeded = true;
                    throw new IOException("EML message size exceeded.");
                }
            }
            return b;
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            final int n = super.read(b, off, len);
            if (n > 0) {
                bytesRead += n;
                if (bytesRead > limit) {
                    exceeded = true;
                    throw new IOException("EML message size exceeded.");
                }
            }
            return n;
        }

        /** Returns {@code true} if the limit was exceeded during reading. */
        boolean isExceeded() {
            return exceeded;
        }
    }
}
