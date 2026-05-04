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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;
import org.codelibs.fess.crawler.util.IgnoreCloseInputStream;

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

    /** Maximum content size for extraction. -1 means no limit. */
    protected long maxContentSize = -1;

    /**
     * Maximum total uncompressed bytes that may be read from all entries
     * combined. Defaults to 2 GiB. Set to {@code -1} to disable.
     */
    protected long maxBytes = 1L << 31;

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
                CopyUtil.copy(in, fos);
            }

            lhaFile = new LhaFile(tempFile);
            @SuppressWarnings("unchecked")
            final Enumeration<LhaHeader> entries = lhaFile.entries();
            long contentSize = 0;
            int entryCount = 0;
            while (entries.hasMoreElements()) {
                final LhaHeader head = entries.nextElement();
                entryCount++;
                if (maxEntries > 0 && entryCount > maxEntries) {
                    throw new MaxLengthExceededException("lha entry count exceeded: count=" + entryCount + " max=" + maxEntries);
                }
                contentSize += head.getOriginalSize();
                if (maxBytes > 0 && contentSize > maxBytes) {
                    throw new MaxLengthExceededException("lha uncompressed size exceeded: total=" + contentSize + " max=" + maxBytes);
                }
                if (maxContentSize != -1 && contentSize > maxContentSize) {
                    throw new MaxLengthExceededException("Extracted size is " + contentSize + " > " + maxContentSize);
                }
                final String filename = head.getPath();
                if (isPathTraversal(filename)) {
                    logger.warn("lha entry rejected: name={} reason=path-traversal", filename);
                    continue;
                }
                final String mimeType = mimeTypeHelper.getContentType(null, filename);
                if (mimeType != null) {
                    final Extractor extractor = extractorFactory.getExtractor(mimeType);
                    if (extractor != null) {
                        InputStream is = null;
                        try {
                            is = lhaFile.getInputStream(head);
                            final Map<String, String> map = incrementDepth(params);
                            map.put(ExtractData.RESOURCE_NAME_KEY, filename);
                            buf.append(extractor.getText(new IgnoreCloseInputStream(is), map).getContent());
                            buf.append('\n');
                        } catch (final MaxLengthExceededException e) {
                            throw e;
                        } catch (final Exception e) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Exception in an internal extractor.", e);
                            }
                        } finally {
                            CloseableUtil.closeQuietly(is);
                        }
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
     * Returns true when the supplied entry name escapes the conceptual
     * extraction root via path-traversal segments.
     *
     * @param name the entry name as reported by the archive
     * @return {@code true} if the name should be rejected
     */
    protected boolean isPathTraversal(final String name) {
        if (name == null || name.isEmpty()) {
            return true;
        }
        if (name.startsWith("/") || name.startsWith("\\")) {
            return true;
        }
        if (name.length() >= 2 && name.charAt(1) == ':') {
            return true;
        }
        try {
            final Path normalised = Paths.get(name).normalize();
            final String normStr = normalised.toString().replace('\\', '/');
            if (normStr.equals("..") || normStr.startsWith("../") || normStr.contains("/../")) {
                return true;
            }
            for (final Path part : normalised) {
                if ("..".equals(part.toString())) {
                    return true;
                }
            }
        } catch (final InvalidPathException ipe) {
            return true;
        }
        return false;
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
     * Sets the maximum number of entries that may be iterated.
     * @param maxEntries the maximum entry count (use {@code -1} to disable)
     */
    public void setMaxEntries(final int maxEntries) {
        this.maxEntries = maxEntries;
    }
}
