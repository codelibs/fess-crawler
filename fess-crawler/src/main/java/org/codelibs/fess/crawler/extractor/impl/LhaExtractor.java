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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

import jp.gr.java_conf.dangan.util.lha.LhaFile;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;

/**
 * Extractor implementation for LHA (LZH) archive files.
 * This extractor can extract text content from files within LHA archives
 * by using appropriate extractors for each contained file type.
 *
 * <p>
 * Defends against decompression / many-entry / recursion bombs and Zip Slip
 * style path traversal in entry names.
 * </p>
 *
 * @author shinsuke
 */
public class LhaExtractor extends AbstractExtractor {
    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(LhaExtractor.class);

    /**
     * Legacy total cap on uncompressed bytes actually buffered from
     * supported entries. The cap is also folded into the read budget so a
     * single oversized entry cannot be buffered up to
     * {@link #maxBytesPerEntry} when the user only asked for a much smaller
     * total. Set to {@code -1} to disable.
     */
    protected long maxContentSize = -1;

    /**
     * Maximum total uncompressed bytes that may be read from all entries
     * combined. Defaults to 2 GiB. Set to {@code -1} to disable. Only bytes
     * from entries that have a registered {@link Extractor} contribute to
     * this total — unsupported entries are skipped without buffering.
     */
    protected long maxBytes = 1L << 31;

    /**
     * Maximum uncompressed bytes that may be buffered for a SINGLE entry.
     * Enforced against the actual bytes read from the entry stream (NOT the
     * header-reported size, which is attacker-controlled). Defaults to
     * 256 MiB. Set to {@code -1} to disable. Enforced independently of
     * {@link #maxBytes}. Only applies to entries that have a registered
     * {@link Extractor}; an unsupported entry is never buffered.
     */
    protected long maxBytesPerEntry = 256L * 1024L * 1024L;

    /**
     * Maximum bytes copied from the input stream to the local temporary file
     * before {@link LhaFile} is opened. The LHA library requires a seekable
     * file, so the entire archive must be staged on disk; this cap prevents a
     * hostile producer from filling local storage. Defaults to 1 GiB. Set to
     * {@code -1} to disable.
     */
    protected long maxInputBytes = 1L << 30;

    /**
     * Maximum allowed number of entries to iterate. Defaults to 100,000.
     * Set to {@code -1} to disable.
     */
    protected int maxEntries = 100_000;

    /**
     * Creates a new LhaExtractor instance.
     */
    public LhaExtractor() {
        super();
    }

    /**
     * Extracts text content from an LHA archive input stream.
     *
     * @param in the input stream containing the LHA archive
     * @param params extraction parameters
     * @return the extracted text data
     * @throws CrawlerSystemException if the input stream is null
     * @throws ExtractException if an error occurs during extraction
     * @throws MaxLengthExceededException if the extracted content size exceeds the maximum limit
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        if (in == null) {
            throw new CrawlerSystemException("LHA archive input stream is null. Cannot extract text from null input.");
        }
        checkDepth(params, maxArchiveDepth);

        final MimeTypeHelper mimeTypeHelper = getMimeTypeHelper();
        final ExtractorFactory extractorFactory = getExtractorFactory();
        final StringBuilder buf = new StringBuilder(1000);

        File tempFile = null;
        LhaFile lhaFile = null;
        try {
            tempFile = createTempFile("crawler-", ".lzh", null);
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                // Stage the (untrusted) archive bytes to disk under a hard
                // cap so a hostile producer cannot exhaust local storage by
                // streaming an arbitrarily large body.
                final long inputReadLimit = maxInputBytes > 0 ? maxInputBytes + 1L : Long.MAX_VALUE;
                final long staged = copyBounded(in, fos, inputReadLimit);
                if (maxInputBytes > 0 && staged > maxInputBytes) {
                    throw new MaxLengthExceededException("lha input size exceeded: bytes=" + staged + " max=" + maxInputBytes);
                }
            }

            lhaFile = new LhaFile(tempFile);
            @SuppressWarnings("unchecked")
            final Enumeration<LhaHeader> entries = lhaFile.entries();
            long totalBytes = 0;
            int entryCount = 0;
            while (entries.hasMoreElements()) {
                final LhaHeader head = entries.nextElement();
                entryCount++;
                if (maxEntries > 0 && entryCount > maxEntries) {
                    throw new MaxLengthExceededException("lha entry count exceeded: count=" + entryCount + " max=" + maxEntries);
                }
                final String filename = head.getPath();
                if (isPathTraversal(filename)) {
                    logger.warn("lha entry rejected: name={} reason=path-traversal", filename);
                    continue;
                }

                // Decide MIME / extractor up front so an unsupported entry
                // is skipped without opening its decompressor at all. This
                // mirrors the legacy behaviour and keeps a large irrelevant
                // entry from consuming the per-entry / total caps reserved
                // for entries the crawler actually wants to extract.
                final String mimeType = mimeTypeHelper.getContentType(null, filename);
                final Extractor extractor = mimeType != null ? extractorFactory.getExtractor(mimeType) : null;
                if (extractor == null) {
                    continue;
                }

                // Read the entry payload through copyBounded so the cap is
                // enforced against bytes actually decompressed, not the
                // header-reported size (which is attacker-controlled).
                final long actualBytes;
                final byte[] entryBytes;
                InputStream is = null;
                try {
                    is = lhaFile.getInputStream(head);
                    final long totalReadLimit;
                    if (maxBytes > 0) {
                        totalReadLimit = Math.max(0L, maxBytes - totalBytes) + 1L;
                    } else {
                        totalReadLimit = Long.MAX_VALUE;
                    }
                    // Fold maxContentSize into the read budget so a small
                    // legacy cap is honoured before a large per-entry cap
                    // can buffer hundreds of MiB into memory.
                    final long contentReadLimit;
                    if (maxContentSize >= 0) {
                        contentReadLimit = Math.max(0L, maxContentSize - totalBytes) + 1L;
                    } else {
                        contentReadLimit = Long.MAX_VALUE;
                    }
                    final long perEntryReadLimit = maxBytesPerEntry > 0 ? maxBytesPerEntry + 1L : Long.MAX_VALUE;
                    final long readLimit = Math.min(Math.min(totalReadLimit, contentReadLimit), perEntryReadLimit);
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    actualBytes = copyBounded(is, out, readLimit);
                    entryBytes = out.toByteArray();
                } catch (final IOException ioe) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to read lha entry: name={}", filename, ioe);
                    }
                    continue;
                } finally {
                    CloseableUtil.closeQuietly(is);
                }

                if (maxBytesPerEntry > 0 && actualBytes > maxBytesPerEntry) {
                    throw new MaxLengthExceededException(
                            "lha per-entry size exceeded: name=" + filename + " size=" + actualBytes + " max=" + maxBytesPerEntry);
                }

                totalBytes += actualBytes;
                if (maxBytes > 0 && totalBytes > maxBytes) {
                    throw new MaxLengthExceededException("lha uncompressed size exceeded: total=" + totalBytes + " max=" + maxBytes);
                }
                if (maxContentSize >= 0 && totalBytes > maxContentSize) {
                    throw new MaxLengthExceededException("Extracted size is " + totalBytes + " > " + maxContentSize);
                }

                try {
                    final Map<String, String> map = incrementDepth(params);
                    map.put(ExtractData.RESOURCE_NAME_KEY, filename);
                    buf.append(extractor.getText(new ByteArrayInputStream(entryBytes), map).getContent());
                    buf.append('\n');
                } catch (final MaxLengthExceededException e) {
                    throw e;
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Exception in an internal extractor.", e);
                    }
                }
            }
        } catch (final MaxLengthExceededException e) {
            throw e;
        } catch (final Exception e) {
            throw new ExtractException("Could not extract a content.", e);
        } finally {
            if (lhaFile != null) {
                try {
                    lhaFile.close();
                } catch (final IOException e) {
                    // ignore
                }
            }
            FileUtil.deleteInBackground(tempFile);
        }

        return new ExtractData(buf.toString().trim());
    }

    /**
     * Sets the maximum content size for extraction.
     *
     * @param maxContentSize the maximum content size to set (-1 for no limit)
     */
    public void setMaxContentSize(final long maxContentSize) {
        this.maxContentSize = maxContentSize;
    }

    /**
     * Sets the cap on total uncompressed bytes read from all entries.
     * @param maxBytes the maximum total bytes (use {@code -1} to disable)
     */
    public void setMaxBytes(final long maxBytes) {
        this.maxBytes = maxBytes;
    }

    /**
     * Sets the per-entry cap on uncompressed bytes buffered in memory. The
     * cap is enforced against bytes actually decompressed (not the
     * header-reported size). Set to {@code -1} to disable.
     *
     * @param maxBytesPerEntry the per-entry maximum
     */
    public void setMaxBytesPerEntry(final long maxBytesPerEntry) {
        this.maxBytesPerEntry = maxBytesPerEntry;
    }

    /**
     * Sets the cap on the number of input bytes staged to a temporary file
     * before {@link LhaFile} is opened. Set to {@code -1} to disable.
     *
     * @param maxInputBytes the input-stage maximum
     */
    public void setMaxInputBytes(final long maxInputBytes) {
        this.maxInputBytes = maxInputBytes;
    }

    /**
     * Sets the maximum number of entries that may be iterated.
     * @param maxEntries the maximum entry count (use {@code -1} to disable)
     */
    public void setMaxEntries(final int maxEntries) {
        this.maxEntries = maxEntries;
    }
}
