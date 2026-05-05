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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

/**
 * Extracts text content from ZIP archives.
 *
 * <p>
 * The extractor defends against several content-driven attack vectors. The
 * input stream itself is treated as untrusted, while the {@code params} map is
 * assumed to be admin-configured / trusted. Protections include:
 * </p>
 * <ul>
 *   <li>Total uncompressed-size cap ({@link #setMaxBytes(long)})</li>
 *   <li>Maximum number of entries ({@link #setMaxEntries(int)})</li>
 *   <li>Per-entry compression-ratio threshold
 *       ({@link #setMaxCompressionRatio(long)}) to detect zip bombs</li>
 *   <li>Recursion-depth check (via {@link AbstractExtractor#checkDepth})</li>
 *   <li>Zip Slip path-traversal detection (entry names normalised and
 *       rejected when they escape the conceptual extraction root)</li>
 *   <li>Configurable filename encoding (e.g. {@code "CP932"} /
 *       {@code "MS932"} for Japanese filenames)</li>
 * </ul>
 */
public class ZipExtractor extends AbstractExtractor {
    private static final Logger logger = LogManager.getLogger(ZipExtractor.class);

    /** Threshold below which compression-ratio checks are skipped (bytes). */
    private static final long COMPRESSION_RATIO_MIN_BYTES = 1L << 20; // 1 MiB

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
     * this total — unsupported entries are skipped without buffering or
     * draining, mirroring the pre-defence behaviour.
     */
    protected long maxBytes = 1L << 31;

    /**
     * Maximum uncompressed bytes that may be buffered for a SINGLE entry.
     * This guards against a legitimate-looking but oversized entry (e.g. a
     * 1.9 GiB file inside an otherwise small archive) exhausting the JVM
     * heap when buffered into memory. Defaults to 256 MiB. Set to
     * {@code -1} to disable. Enforced independently of {@link #maxBytes}.
     * Only applies to entries that have a registered {@link Extractor}; an
     * unsupported entry is never buffered, so this cap is irrelevant for it.
     */
    protected long maxBytesPerEntry = 256L * 1024L * 1024L;

    /**
     * Maximum allowed compression ratio (uncompressed / compressed). Entries
     * exceeding this ratio AND larger than 1 MiB are rejected as suspected
     * zip bombs. Set to {@code -1} to disable.
     */
    protected long maxCompressionRatio = 100L;

    /**
     * Maximum allowed number of entries to iterate. Defaults to 100,000.
     * Set to {@code -1} to disable.
     */
    protected int maxEntries = 100_000;

    /**
     * Filename encoding used to decode entry names that lack the UTF-8 flag.
     * Defaults to {@code "UTF-8"}; set to {@code "CP932"} or {@code "MS932"}
     * for archives created on Japanese Windows systems.
     */
    protected String filenameEncoding = "UTF-8";

    /**
     * Creates a new ZipExtractor instance.
     */
    public ZipExtractor() {
        super();
    }

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);
        checkDepth(params, maxArchiveDepth);

        final MimeTypeHelper mimeTypeHelper = getMimeTypeHelper();
        final ExtractorFactory extractorFactory = getExtractorFactory();
        final StringBuilder buf = new StringBuilder(1000);
        int processedEntries = 0;
        int failedEntries = 0;

        final InputStream wrapped = in.markSupported() ? in : new BufferedInputStream(in);
        // Early-validate the ZIP magic so a clearly non-zip blob is reported
        // as ExtractException rather than silently returning empty text.
        wrapped.mark(4);
        try {
            final byte[] sig = new byte[4];
            int read = 0;
            while (read < 4) {
                final int n = wrapped.read(sig, read, 4 - read);
                if (n < 0) {
                    break;
                }
                read += n;
            }
            wrapped.reset();
            if (read != 4 || sig[0] != 'P' || sig[1] != 'K' || (sig[2] != 0x03 && sig[2] != 0x05 && sig[2] != 0x07)) {
                throw new ExtractException("Failed to extract content from ZIP archive. Not a recognised ZIP signature.");
            }
        } catch (final IOException ioe) {
            throw new ExtractException("Failed to extract content from ZIP archive. No entries could be processed.", ioe);
        }
        // CountingInputStream lets us measure the compressed bytes consumed
        // from the underlying stream per entry, which is the only reliable
        // signal in streaming mode (ZipArchiveEntry#getCompressedSize() is
        // often -1 when entries use a data descriptor).
        final CountingInputStream counter = new CountingInputStream(wrapped);
        try (final ZipArchiveInputStream ais = new ZipArchiveInputStream(counter, filenameEncoding, true, true)) {
            ZipArchiveEntry entry;
            long totalBytes = 0;
            long lastCompressedBytes = counter.getByteCount();
            int entryCount = 0;
            while ((entry = ais.getNextEntry()) != null) {
                entryCount++;
                if (maxEntries > 0 && entryCount > maxEntries) {
                    throw new MaxLengthExceededException("zip entry count exceeded: count=" + entryCount + " max=" + maxEntries);
                }
                final String filename = entry.getName();
                if (entry.isDirectory()) {
                    lastCompressedBytes = counter.getByteCount();
                    continue;
                }
                if (isPathTraversal(filename)) {
                    logger.warn("zip entry rejected: name={} reason=path-traversal", filename);
                    // Keep the compressed-bytes anchor in step with the
                    // stream so the next supported entry's ratio is
                    // computed against ITS own compressed bytes, not also
                    // those of the rejected entry.
                    lastCompressedBytes = counter.getByteCount();
                    continue;
                }

                // Decide MIME / extractor up front. An unsupported entry
                // (e.g. a video alongside a small .txt) is skipped without
                // buffering, so a large irrelevant entry does not consume
                // the per-entry / total caps that should be reserved for
                // entries the crawler actually wants to extract.
                final String mimeType = mimeTypeHelper.getContentType(null, filename);
                final Extractor extractor = mimeType != null ? extractorFactory.getExtractor(mimeType) : null;
                if (extractor == null) {
                    lastCompressedBytes = counter.getByteCount();
                    continue;
                }

                // Read entry into bounded buffer while counting actual bytes.
                final long actualBytes;
                final byte[] entryBytes;
                try {
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
                    // Enforce a per-entry cap independently of the total
                    // cap so that a single oversized entry cannot exhaust
                    // the JVM heap. We read one byte beyond the cap so the
                    // explicit overflow check below can distinguish
                    // "exactly at the cap" from "exceeds the cap".
                    final long perEntryReadLimit = maxBytesPerEntry > 0 ? maxBytesPerEntry + 1L : Long.MAX_VALUE;
                    final long readLimit = Math.min(Math.min(totalReadLimit, contentReadLimit), perEntryReadLimit);
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    actualBytes = copyBounded(ais, out, readLimit);
                    entryBytes = out.toByteArray();
                } catch (final IOException ioe) {
                    failedEntries++;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to read zip entry: name={}", filename, ioe);
                    }
                    lastCompressedBytes = counter.getByteCount();
                    continue;
                }

                if (maxBytesPerEntry > 0 && actualBytes > maxBytesPerEntry) {
                    throw new MaxLengthExceededException(
                            "zip per-entry size exceeded: name=" + filename + " size=" + actualBytes + " max=" + maxBytesPerEntry);
                }

                totalBytes += actualBytes;
                if (maxBytes > 0 && totalBytes > maxBytes) {
                    throw new MaxLengthExceededException("zip uncompressed size exceeded: total=" + totalBytes + " max=" + maxBytes);
                }
                if (maxContentSize >= 0 && totalBytes > maxContentSize) {
                    throw new MaxLengthExceededException("Extracted size is " + totalBytes + " > " + maxContentSize);
                }

                // Compression-ratio check (only meaningful for non-tiny entries).
                // Prefer the entry header's compressed size when present;
                // otherwise fall back to the bytes actually consumed from the
                // underlying stream during this entry's read.
                long compressed = entry.getCompressedSize();
                if (compressed <= 0) {
                    final long now = counter.getByteCount();
                    compressed = Math.max(0L, now - lastCompressedBytes);
                    lastCompressedBytes = now;
                } else {
                    lastCompressedBytes = counter.getByteCount();
                }
                if (maxCompressionRatio > 0 && compressed > 0 && actualBytes > COMPRESSION_RATIO_MIN_BYTES
                        && actualBytes / compressed > maxCompressionRatio) {
                    throw new MaxLengthExceededException("zip compression ratio exceeded: name=" + filename + " ratio="
                            + (actualBytes / compressed) + " max=" + maxCompressionRatio);
                }

                try {
                    final Map<String, String> map = incrementDepth(params);
                    map.put(ExtractData.RESOURCE_NAME_KEY, filename);
                    buf.append(extractor.getText(new ByteArrayInputStream(entryBytes), map).getContent());
                    buf.append('\n');
                    processedEntries++;
                } catch (final MaxLengthExceededException e) {
                    throw e;
                } catch (final Exception e) {
                    failedEntries++;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to extract content from archive entry: name={}", filename, e);
                    }
                }
            }
        } catch (final MaxLengthExceededException e) {
            throw e;
        } catch (final Exception e) {
            if (buf.length() == 0) {
                throw new ExtractException("Failed to extract content from ZIP archive. No entries could be processed.", e);
            }
            if (logger.isWarnEnabled()) {
                logger.warn("Partial extraction from ZIP archive. processed={} failed={}", processedEntries, failedEntries, e);
            }
        }

        return new ExtractData(buf.toString().trim());
    }

    /**
     * Sets the maximum content size.
     * @param maxContentSize The maximum content size to set.
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
     * Sets the per-entry cap on uncompressed bytes buffered in memory. Set
     * to {@code -1} to disable.
     *
     * @param maxBytesPerEntry the per-entry maximum
     */
    public void setMaxBytesPerEntry(final long maxBytesPerEntry) {
        this.maxBytesPerEntry = maxBytesPerEntry;
    }

    /**
     * Sets the maximum permitted uncompressed/compressed ratio per entry.
     * @param maxCompressionRatio the threshold (use {@code -1} to disable)
     */
    public void setMaxCompressionRatio(final long maxCompressionRatio) {
        this.maxCompressionRatio = maxCompressionRatio;
    }

    /**
     * Sets the maximum number of entries that may be iterated.
     * @param maxEntries the maximum entry count (use {@code -1} to disable)
     */
    public void setMaxEntries(final int maxEntries) {
        this.maxEntries = maxEntries;
    }

    /**
     * Sets the filename encoding used to decode entry names that lack the
     * UTF-8 flag (e.g. {@code "CP932"} / {@code "MS932"} for Japanese
     * archives).
     *
     * @param filenameEncoding the charset name
     */
    public void setFilenameEncoding(final String filenameEncoding) {
        this.filenameEncoding = filenameEncoding;
    }
}
