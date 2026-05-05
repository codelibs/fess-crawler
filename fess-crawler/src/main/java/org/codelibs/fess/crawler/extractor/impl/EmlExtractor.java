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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
 *   <li>{@link #maxRecursionDepth} (default 10) caps how deeply nested
 *       {@code message/rfc822} or {@code multipart/*} parts may be.</li>
 *   <li>{@link #maxParts} (default 1000) caps the total number of MIME parts
 *       traversed across the whole message.</li>
 *   <li>{@link #maxBodyBytes} (default 50 MiB) caps the total UTF-8 byte size
 *       of body text appended to the output.</li>
 * </ul>
 * <p>RFC 2047 encoded-word headers (e.g. {@code Subject},
 * {@code From}, {@code To}) are decoded via {@link MimeUtility#decodeText}.</p>
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
        try {
            final Session mailSession = Session.getDefaultInstance(props, null);
            final MimeMessage message = new MimeMessage(mailSession, in);
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
            putValue(data, "Received-Date", getReceivedDate(message));
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
            putDateValue(data, "receivedDate", getReceivedDate(message));
            if (message.getMessageID() != null) {
                data.putValue("messageId", message.getMessageID());
            }

            if (!ctx.attachmentNames.isEmpty()) {
                data.putValues("attachmentNames", ctx.attachmentNames.toArray(new String[0]));
            }
            return data;
        } catch (final MessagingException e) {
            throw new ExtractException(e);
        } catch (final IOException e) {
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
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to put {}:{}", key, value, e);
            }
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
     * @param value the encoded text to decode
     * @return the decoded text or empty string if decoding fails
     */
    protected String getDecodeText(final String value) {
        if (value == null) {
            return StringUtil.EMPTY;
        }
        try {
            return MimeUtility.decodeText(value);
        } catch (final UnsupportedEncodingException e) {
            logger.warn("Invalid encoding.", e);
            return StringUtil.EMPTY;
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
     * {@code message/rfc822} parts.
     *
     * @param maxRecursionDepth the maximum recursion depth
     */
    public void setMaxRecursionDepth(final int maxRecursionDepth) {
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
     * @param maxParts the maximum number of parts
     */
    public void setMaxParts(final int maxParts) {
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
     * @param maxBodyBytes the maximum body bytes
     */
    public void setMaxBodyBytes(final long maxBodyBytes) {
        this.maxBodyBytes = maxBodyBytes;
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
            logger.debug("EML body truncated at {} bytes.", maxBodyBytes);
        }
    }

    /**
     * Streams a text part's content into the extraction buffer with a hard
     * memory cap, then delegates to {@link #appendBody} for byte-accurate
     * truncation.
     *
     * <p>The previous implementation called {@link Part#getContent()}, which
     * fully decoded the part into a Java {@code String}. A multi-GB
     * {@code text/plain} part would peak heap usage at multiples of its raw
     * size before any {@link #maxBodyBytes} check ran, defeating the DoS
     * guard at the memory layer.</p>
     *
     * <p>This implementation reads from {@link Part#getInputStream} (which
     * already decodes Content-Transfer-Encoding) capped at {@code 4 *
     * remaining UTF-8 budget + 16} bytes — enough to fill any UTF-8 budget
     * regardless of source charset (UTF-8 uses at most 4 bytes per code
     * point), but bounded relative to {@link #maxBodyBytes} rather than to
     * the part size.</p>
     *
     * @param ctx the extraction context
     * @param part the {@code text/*} part
     */
    protected void appendTextPart(final BodyExtractionContext ctx, final Part part) {
        if (ctx.bodyBytes >= maxBodyBytes) {
            return;
        }
        final long remaining = maxBodyBytes - ctx.bodyBytes;
        // Cap source reads at 4× the remaining UTF-8 budget plus a small pad,
        // clamped to a sane upper bound so we never allocate a buffer larger
        // than is needed to fill the UTF-8 cap.
        final long sourceCapL;
        if (remaining > (Integer.MAX_VALUE - 16L) / 4L) {
            sourceCapL = Integer.MAX_VALUE - 16L;
        } else {
            sourceCapL = remaining * 4L + 16L;
        }
        final int sourceCap = (int) sourceCapL;

        Charset charset = StandardCharsets.UTF_8;
        try {
            final String contentType = part.getContentType();
            if (contentType != null) {
                final String cs = new ContentType(contentType).getParameter("charset");
                if (cs != null && !cs.isEmpty()) {
                    try {
                        charset = Charset.forName(MimeUtility.javaCharset(cs));
                    } catch (final Exception ignored) {
                        // Unknown / unsupported charset → fall back to UTF-8
                    }
                }
            }
        } catch (final MessagingException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to parse content type of text part.", e);
            }
        }

        try (InputStream is = part.getInputStream()) {
            // Initial buffer scaled to the source cap, but never larger than 64KiB
            // to keep small messages cheap. ByteArrayOutputStream grows on demand.
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(Math.min(sourceCap, 64 * 1024));
            final byte[] buf = new byte[Math.min(sourceCap, 8 * 1024)];
            int total = 0;
            int n;
            while (total < sourceCap && (n = is.read(buf, 0, Math.min(buf.length, sourceCap - total))) > 0) {
                baos.write(buf, 0, n);
                total += n;
            }
            if (total > 0) {
                appendBody(ctx, new String(baos.toByteArray(), charset));
            }
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to read text part content.", e);
            }
        } catch (final MessagingException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to access text part input stream.", e);
            }
        }
    }

    /**
     * Backwards-compatible attachment text extraction. Kept for subclasses that
     * may have overridden it; new code should prefer
     * {@link #appendAttachment(BodyExtractionContext, BodyPart)}.
     *
     * @param buf the buffer to append content to
     * @param bodyPart the body part containing the attachment
     */
    protected void appendAttachment(final StringBuilder buf, final BodyPart bodyPart) {
        final BodyExtractionContext ctx = new BodyExtractionContext();
        ctx.body = buf;
        appendAttachment(ctx, bodyPart);
    }

    /**
     * Attempts to extract text from an attachment using a registered
     * {@link Extractor} for its detected MIME type. Failures are silently
     * swallowed.
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
                    try (final InputStream in = bodyPart.getInputStream()) {
                        final Map<String, String> map = new HashMap<>();
                        map.put(ExtractData.RESOURCE_NAME_KEY, filename);
                        final String content = extractor.getText(in, map).getContent();
                        if (content != null) {
                            appendBody(ctx, content);
                        }
                    } catch (final Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Exception in an internal extractor.", e);
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
     *
     * @param message the message to get the received date from
     * @return the received date or null if not found
     * @throws MessagingException if message access fails
     */
    protected static Date getReceivedDate(final Message message) throws MessagingException {
        final Date today = new Date();
        final String[] received = message.getHeader("received");
        if (received != null) {
            for (final String v : received) {
                String dateStr = null;
                try {
                    dateStr = getDateString(v);
                    final Date receivedDate = new MailDateFormat().parse(dateStr);
                    if (!receivedDate.after(today)) {
                        return receivedDate;
                    }
                } catch (final ParseException e) {
                    // ignore
                }
            }
        }
        return null;
    }

    /**
     * Extracts a date string from the received header text.
     *
     * @param text the received header text
     * @return the date string starting from the day of week, or null if not found
     */
    private static String getDateString(final String text) {
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
}
