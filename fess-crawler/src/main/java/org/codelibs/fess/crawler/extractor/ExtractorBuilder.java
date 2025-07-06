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
package org.codelibs.fess.crawler.extractor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

/**
 * {@link ExtractorBuilder} is a builder class for creating and configuring an {@link ExtractData} object.
 * It encapsulates the process of extracting data from an input stream using a specified or detected extractor.
 * The builder allows setting parameters such as MIME type, filename, extractor name, maximum content length,
 * and cache file size to optimize the extraction process.
 *
 * <p>
 * The main purpose of this class is to simplify the extraction process by providing a fluent interface
 * for configuring the extraction parameters and handling the underlying complexities of content processing,
 * such as MIME type detection, extractor selection, and content length validation.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * {@code
 * try (InputStream in = new FileInputStream("example.pdf")) {
 *     ExtractData extractData = new ExtractorBuilder(crawlerContainer, in, new HashMap<>())
 *         .mimeType("application/pdf")
 *         .filename("example.pdf")
 *         .maxContentLength(1024 * 1024)
 *         .extract();
 *
 *     String content = extractData.getContent();
 *     // Process the extracted content
 * } catch (IOException e) {
 *     // Handle exception
 * }
 * }
 * </pre>
 */
public class ExtractorBuilder {

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(ExtractorBuilder.class);

    /** Input stream to extract data from */
    private final InputStream in;

    /** Parameters for the extraction process */
    private final Map<String, String> params;

    /** Container for accessing crawler components */
    private final CrawlerContainer crawlerContainer;

    /** MIME type of the content */
    private String mimeType;

    /** Filename of the content */
    private String filename;

    /** Cache file size threshold in bytes */
    private int cacheFileSize = 1_000_000;

    /** Name of the extractor to use */
    private String extractorName = "tikaExtractor";

    /** Maximum content length allowed */
    private long maxContentLength = -1;

    /**
     * Constructs a new ExtractorBuilder.
     *
     * @param crawlerContainer the crawler container
     * @param in the input stream to extract data from
     * @param params the parameters for the extraction process
     */
    protected ExtractorBuilder(final CrawlerContainer crawlerContainer, final InputStream in, final Map<String, String> params) {
        this.crawlerContainer = crawlerContainer;
        this.in = in;
        this.params = params;
    }

    /**
     * Sets the MIME type of the content to extract.
     *
     * @param mimeType the MIME type to set
     * @return this builder instance for method chaining
     */
    public ExtractorBuilder mimeType(final String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    /**
     * Sets the filename of the content to extract.
     *
     * @param filename the filename to set
     * @return this builder instance for method chaining
     */
    public ExtractorBuilder filename(final String filename) {
        this.filename = filename;
        return this;
    }

    /**
     * Sets the name of the extractor to use.
     *
     * @param extractorName the extractor name to set
     * @return this builder instance for method chaining
     */
    public ExtractorBuilder extractorName(final String extractorName) {
        this.extractorName = extractorName;
        return this;
    }

    /**
     * Sets the maximum content length allowed for extraction.
     *
     * @param maxContentLength the maximum content length to set
     * @return this builder instance for method chaining
     */
    public ExtractorBuilder maxContentLength(final long maxContentLength) {
        this.maxContentLength = maxContentLength;
        return this;
    }

    /**
     * Sets the cache file size threshold in bytes.
     *
     * @param cacheFileSize the cache file size to set
     * @return this builder instance for method chaining
     */
    public ExtractorBuilder cacheFileSize(final int cacheFileSize) {
        this.cacheFileSize = cacheFileSize;
        return this;
    }

    /**
     * Extracts data from the input stream using the configured parameters.
     *
     * @return the extracted data
     * @throws MaxLengthExceededException if content length exceeds maximum allowed
     * @throws ExtractException if extraction fails
     */
    public ExtractData extract() {
        final ExtractorFactory extractorFactory = crawlerContainer.getComponent("extractorFactory");

        DeferredFileOutputStream dfos = null;
        try (DeferredFileOutputStream out = new DeferredFileOutputStream(cacheFileSize, "fess-extractor-", ".out", null)) {
            dfos = out;
            CopyUtil.copy(in, out);
            out.flush();

            Extractor extractor = StringUtil.isBlank(mimeType) ? null : extractorFactory.getExtractor(mimeType);
            if (extractor == null) {
                final String detectedMimeType = getMimeType(out);
                extractor = extractorFactory.getExtractor(detectedMimeType);
                if (extractor == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Using default extractor {} for MIME type {}", extractorName, mimeType);
                    }
                    extractor = crawlerContainer.getComponent(extractorName);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Using {} for detected MIME type {}, not {}", extractor.getClass().getName(), detectedMimeType, mimeType);
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug("Using {} for MIME type {}", extractor.getClass().getName(), mimeType);
            }

            if (maxContentLength < 0) {
                final ContentLengthHelper contentLengthHelper = crawlerContainer.getComponent("contentLengthHelper");
                maxContentLength = contentLengthHelper.getMaxLength(mimeType);
            }
            final long contentLength = getContentLength(out);
            if (contentLength > maxContentLength) {
                throw new MaxLengthExceededException("Content length (" + contentLength + " bytes) exceeds the maximum allowed length ("
                        + maxContentLength + " bytes).");
            }
            if (contentLength == 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug("The content length is 0.");
                }
                return new ExtractData(StringUtil.EMPTY);
            } else {
                try (InputStream is = getContentInputStream(out)) {
                    return extractor.getText(is, params);
                }
            }
        } catch (final CrawlingAccessException e) {
            throw e;
        } catch (final Exception e) {
            throw new ExtractException("Failed to extract data.", e);
        } finally {
            if (dfos != null && !dfos.isInMemory()) {
                final File file = dfos.getFile();
                try {
                    Files.delete(file.toPath());
                } catch (final IOException e) {
                    logger.warn("Failed to delete {}.", file.getAbsolutePath(), e);
                }
            }
        }

    }

    /**
     * Detects the MIME type of the content.
     *
     * @param out the deferred file output stream
     * @return the detected MIME type
     * @throws IOException if an I/O error occurs
     */
    protected String getMimeType(final DeferredFileOutputStream out) throws IOException {
        final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
        try (InputStream is = getContentInputStream(out)) {
            return mimeTypeHelper.getContentType(is, filename);
        }
    }

    /**
     * Gets an input stream for the content in the deferred file output stream.
     *
     * @param out the deferred file output stream
     * @return the input stream for the content
     * @throws IOException if an I/O error occurs
     */
    protected InputStream getContentInputStream(final DeferredFileOutputStream out) throws IOException {
        if (out.isInMemory()) {
            return new ByteArrayInputStream(out.getData());
        }
        return new FileInputStream(out.getFile());
    }

    /**
     * Returns the content length.
     * @param out The DeferredFileOutputStream.
     * @return The content length.
     * @throws IOException if an I/O error occurs.
     */
    protected long getContentLength(final DeferredFileOutputStream out) throws IOException {
        if (out.isInMemory()) {
            return out.getData().length;
        }
        return out.getFile().length();
    }
}
