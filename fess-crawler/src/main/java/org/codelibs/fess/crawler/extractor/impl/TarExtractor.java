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
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

import jakarta.annotation.Resource;

/**
 * Extracts text content from TAR archives.
 *
 * <p>
 * Defends against decompression / many-entry / recursion bombs and Zip Slip
 * style path traversal. Symbolic and hard link entries are skipped because
 * they can reference files outside the archive sandbox.
 * </p>
 */
public class TarExtractor extends AbstractExtractor {
    private static final Logger logger = LogManager.getLogger(TarExtractor.class);

    /**
     * The archive stream factory.
     */
    @Resource
    protected ArchiveStreamFactory archiveStreamFactory;

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
     * Guards against an oversized entry exhausting the JVM heap when
     * buffered into memory. Defaults to 256 MiB. Set to {@code -1} to
     * disable. Enforced independently of {@link #maxBytes}. Only applies to
     * entries that have a registered {@link Extractor}; an unsupported
     * entry is never buffered, so this cap is irrelevant for it.
     */
    protected long maxBytesPerEntry = 256L * 1024L * 1024L;

    /**
     * Maximum allowed number of entries to iterate. Defaults to 100,000.
     * Set to {@code -1} to disable.
     */
    protected int maxEntries = 100_000;

    /**
     * Creates a new TarExtractor instance.
     */
    public TarExtractor() {
        super();
    }

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);
        checkDepth(params, maxArchiveDepth);

        final MimeTypeHelper mimeTypeHelper = getMimeTypeHelper();
        final ExtractorFactory extractorFactory = getExtractorFactory();
        return new ExtractData(getTextInternal(in, mimeTypeHelper, extractorFactory, params));
    }

    /**
     * Returns a text from the input stream.
     *
     * @param in The input stream.
     * @param mimeTypeHelper The mime type helper.
     * @param extractorFactory The extractor factory.
     * @param params Extractor parameters used to track recursion depth.
     * @return A text.
     */
    protected String getTextInternal(final InputStream in, final MimeTypeHelper mimeTypeHelper, final ExtractorFactory extractorFactory,
            final Map<String, String> params) {
        final StringBuilder buf = new StringBuilder(1000);
        int processedEntries = 0;
        int failedEntries = 0;

        try (final ArchiveInputStream ais = archiveStreamFactory.createArchiveInputStream("tar", in)) {
            TarArchiveEntry entry;
            long totalBytes = 0;
            int entryCount = 0;
            while ((entry = (TarArchiveEntry) ais.getNextEntry()) != null) {
                entryCount++;
                if (maxEntries > 0 && entryCount > maxEntries) {
                    throw new MaxLengthExceededException("tar entry count exceeded: count=" + entryCount + " max=" + maxEntries);
                }
                final String filename = entry.getName();
                if (entry.isDirectory()) {
                    continue;
                }
                if (entry.isSymbolicLink() || entry.isLink()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("tar entry skipped: name={} reason=link link={}", filename, entry.getLinkName());
                    }
                    continue;
                }
                if (isPathTraversal(filename)) {
                    logger.warn("tar entry rejected: name={} reason=path-traversal", filename);
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
                    continue;
                }

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
                    final long perEntryReadLimit = maxBytesPerEntry > 0 ? maxBytesPerEntry + 1L : Long.MAX_VALUE;
                    final long readLimit = Math.min(Math.min(totalReadLimit, contentReadLimit), perEntryReadLimit);
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    actualBytes = copyBounded(ais, out, readLimit);
                    entryBytes = out.toByteArray();
                } catch (final IOException ioe) {
                    failedEntries++;
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to read tar entry: name={}", filename, ioe);
                    }
                    continue;
                }

                if (maxBytesPerEntry > 0 && actualBytes > maxBytesPerEntry) {
                    throw new MaxLengthExceededException(
                            "tar per-entry size exceeded: name=" + filename + " size=" + actualBytes + " max=" + maxBytesPerEntry);
                }

                totalBytes += actualBytes;
                if (maxBytes > 0 && totalBytes > maxBytes) {
                    throw new MaxLengthExceededException("tar uncompressed size exceeded: total=" + totalBytes + " max=" + maxBytes);
                }
                if (maxContentSize >= 0 && totalBytes > maxContentSize) {
                    throw new MaxLengthExceededException("Extracted size is " + totalBytes + " > " + maxContentSize);
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
                throw new ExtractException("Failed to extract content from TAR archive. No entries could be processed.", e);
            }
            if (logger.isWarnEnabled()) {
                logger.warn("Partial extraction from TAR archive. processed={} failed={}", processedEntries, failedEntries, e);
            }
        }

        return buf.toString().trim();
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
     * Sets the maximum number of entries that may be iterated.
     * @param maxEntries the maximum entry count (use {@code -1} to disable)
     */
    public void setMaxEntries(final int maxEntries) {
        this.maxEntries = maxEntries;
    }
}
