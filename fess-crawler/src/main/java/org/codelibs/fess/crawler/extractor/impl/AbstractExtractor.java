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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

import jakarta.annotation.Resource;

/**
 * An abstract base class for implementing Extractor interfaces.
 * Provides common functionality such as access to CrawlerContainer components
 * and registration with the ExtractorFactory.
 *
 * <p>
 * This class handles the retrieval of essential crawler components like
 * {@link MimeTypeHelper} and {@link ExtractorFactory} from the
 * {@link CrawlerContainer}. It also provides a convenient method for
 * registering the extractor with the {@link ExtractorFactory}.
 * </p>
 *
 * <p>
 * Subclasses should implement the actual extraction logic in their own
 * methods, leveraging the helper methods provided by this abstract class.
 * </p>
 *
 */
public abstract class AbstractExtractor implements Extractor {

    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(AbstractExtractor.class);

    /** Default read buffer size in characters when streaming reader content. */
    protected static final int READ_BUFFER_SIZE = 8192;

    /** The crawler container. */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /** The weight of this extractor. */
    protected int weight = 1;

    /**
     * Constructs a new AbstractExtractor.
     */
    public AbstractExtractor() {
        // NOP
    }

    @Override
    public int getWeight() {
        return weight;
    }

    /**
     * Sets the weight of this extractor.
     * @param weight The weight to set.
     */
    public void setWeight(final int weight) {
        this.weight = weight;
    }

    /**
     * Registers this extractor with the ExtractorFactory.
     * @param keyList The list of keys to register this extractor under.
     */
    public void register(final List<String> keyList) {
        if (keyList == null || keyList.isEmpty()) {
            throw new IllegalArgumentException("keyList must not be null or empty.");
        }
        getExtractorFactory().addExtractor(keyList, this);
    }

    /**
     * Returns the MimeTypeHelper instance from the CrawlerContainer.
     * @return The MimeTypeHelper instance.
     */
    protected MimeTypeHelper getMimeTypeHelper() {
        final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
        if (mimeTypeHelper == null) {
            throw new CrawlerSystemException("MimeTypeHelper is unavailable.");
        }
        return mimeTypeHelper;
    }

    /**
     * Returns the ExtractorFactory instance from the CrawlerContainer.
     * @return The ExtractorFactory instance.
     */
    protected ExtractorFactory getExtractorFactory() {
        final ExtractorFactory extractorFactory = crawlerContainer.getComponent("extractorFactory");
        if (extractorFactory == null) {
            throw new CrawlerSystemException("ExtractorFactory is unavailable.");
        }
        return extractorFactory;
    }

    /**
     * Creates a temporary file.
     * @param prefix The prefix string to be used in generating the file's name.
     * @param suffix The suffix string to be used in generating the file's name.
     * @param directory The directory in which the file is to be created, or null if the default temporary-file directory is to be used.
     * @return The created temporary file.
     */
    protected File createTempFile(final String prefix, final String suffix, final File directory) {
        try {
            final File tempFile = File.createTempFile(prefix, suffix, directory);
            tempFile.setReadable(false, false);
            tempFile.setReadable(true, true);
            tempFile.setWritable(false, false);
            tempFile.setWritable(true, true);
            return tempFile;
        } catch (final IOException e) {
            throw new CrawlerSystemException("Could not create a temp file.", e);
        }
    }

    /**
     * Validates that the input stream is not null.
     * This is a common validation performed by most extractors.
     *
     * @param in The input stream to validate
     * @throws CrawlerSystemException if the input stream is null
     */
    protected void validateInputStream(final InputStream in) {
        if (in == null) {
            throw new CrawlerSystemException("The inputstream is null.");
        }
    }

    /**
     * Holder for the result of {@link #readWithLimit(Reader, long)}.
     */
    protected static final class TextReadResult {
        /** The decoded content (possibly truncated). */
        public final String content;
        /** Whether the content was truncated at the configured limit. */
        public final boolean truncated;

        /**
         * Creates a new result holder.
         * @param content the decoded content
         * @param truncated whether truncation occurred
         */
        TextReadResult(final String content, final boolean truncated) {
            this.content = content;
            this.truncated = truncated;
        }
    }

    /**
     * Reads characters from the supplied reader into a string, bounding the number
     * of characters by {@code maxTextLength}. When the limit is reached the read
     * stops early, a WARN-level message is logged, and the result is flagged as
     * truncated. A {@code maxTextLength} less than or equal to zero disables the
     * limit. At the truncation boundary a trailing unpaired high surrogate is
     * dropped so the returned string is always a valid UTF-16 sequence.
     *
     * <p>The supplied reader is not closed by this method; the caller retains
     * ownership.
     *
     * @param reader the reader to consume
     * @param maxTextLength the maximum number of characters ({@code char} units) to read
     * @return a {@link TextReadResult} with the decoded content and the truncation flag
     * @throws IOException if reading fails
     */
    protected TextReadResult readWithLimit(final Reader reader, final long maxTextLength) throws IOException {
        final BufferedReader br = reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
        final StringBuilder sb = new StringBuilder();
        final char[] buf = new char[READ_BUFFER_SIZE];
        long total = 0;
        boolean truncated = false;
        int n;
        while ((n = br.read(buf)) >= 0) {
            if (maxTextLength > 0 && total + n > maxTextLength) {
                final int remaining = (int) (maxTextLength - total);
                if (remaining > 0) {
                    sb.append(buf, 0, remaining);
                }
                // Avoid leaving an unpaired high surrogate at the end.
                if (sb.length() > 0 && Character.isHighSurrogate(sb.charAt(sb.length() - 1))) {
                    sb.setLength(sb.length() - 1);
                }
                logger.warn("Extracted content truncated: extractor={} maxTextLength={} totalChars={}", getClass().getSimpleName(),
                        maxTextLength, total + n);
                truncated = true;
                break;
            }
            sb.append(buf, 0, n);
            total += n;
        }
        return new TextReadResult(sb.toString(), truncated);
    }
}
