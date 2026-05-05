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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
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

    /**
     * Parameter key used to track the recursion depth across nested archive
     * extraction. Callers/recursive extractor invocations may set this to
     * limit how deeply nested archives are unpacked. The value is parsed as
     * an integer; missing or unparseable values are treated as depth 0.
     */
    public static final String EXTRACTOR_DEPTH_KEY = "extractorDepth";

    /** The crawler container. */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /** The weight of this extractor. */
    protected int weight = 1;

    /**
     * Maximum allowed depth for recursive archive extraction. When the depth
     * value parsed from {@link #EXTRACTOR_DEPTH_KEY} reaches this threshold,
     * {@link #checkDepth(Map, int)} aborts further recursion to defend
     * against recursion-bomb archives.
     */
    protected int maxArchiveDepth = 10;

    /**
     * Constructs a new AbstractExtractor.
     */
    public AbstractExtractor() {
        // NOP
    }

    /**
     * Sets the maximum allowed recursion depth for nested archive extraction.
     * @param maxArchiveDepth the new maximum depth (non-negative)
     */
    public void setMaxArchiveDepth(final int maxArchiveDepth) {
        this.maxArchiveDepth = maxArchiveDepth;
    }

    /**
     * Returns the current recursion depth recorded in the extractor params.
     * Missing, blank, or unparseable values are treated as {@code 0}.
     *
     * @param params the extractor parameters (may be {@code null})
     * @return the parsed depth, or {@code 0} if not set
     */
    protected int getCurrentDepth(final Map<String, String> params) {
        if (params == null) {
            return 0;
        }
        final String value = params.get(EXTRACTOR_DEPTH_KEY);
        if (value == null || value.isBlank()) {
            return 0;
        }
        try {
            final int depth = Integer.parseInt(value.trim());
            return depth < 0 ? 0 : depth;
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Returns a NEW parameter map (the original is not mutated) with the
     * recursion depth incremented by one. Useful when an archive extractor
     * recursively delegates to another extractor for a nested archive entry.
     *
     * @param params the current extractor parameters (may be {@code null})
     * @return a new map containing all original entries plus an incremented
     *         depth
     */
    protected Map<String, String> incrementDepth(final Map<String, String> params) {
        final Map<String, String> next = new HashMap<>();
        if (params != null) {
            next.putAll(params);
        }
        next.put(EXTRACTOR_DEPTH_KEY, Integer.toString(getCurrentDepth(params) + 1));
        return next;
    }

    /**
     * Validates that the recursion depth recorded in {@code params} does not
     * meet or exceed {@code maxDepth}. Throws {@link MaxLengthExceededException}
     * (a {@link org.codelibs.fess.crawler.exception.CrawlingAccessException
     * CrawlingAccessException}) when the threshold is reached so that the
     * surrounding crawler treats it as a data-driven access failure rather
     * than a system error.
     *
     * @param params the extractor parameters (may be {@code null})
     * @param maxDepth the (exclusive) maximum allowed depth
     * @throws MaxLengthExceededException when {@code currentDepth >= maxDepth}
     */
    protected void checkDepth(final Map<String, String> params, final int maxDepth) {
        final int current = getCurrentDepth(params);
        if (current >= maxDepth) {
            throw new MaxLengthExceededException("Archive recursion depth exceeded: depth=" + current + " max=" + maxDepth);
        }
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
     * Returns true when the supplied entry name escapes the conceptual
     * extraction root via path-traversal segments. The check is performed on
     * a normalised form of the path and is shared between the archive
     * extractors (Zip / Tar / Lha) so the rejection rules stay in lock step.
     *
     * <p>
     * An entry is rejected when it is null/empty, when it is rooted at
     * {@code /} or {@code \}, when it begins with a Windows drive letter
     * (e.g. {@code C:}), when its normalised form contains a {@code ..}
     * segment, or when {@link Paths#get} treats it as malformed.
     * </p>
     *
     * @param name the entry name as reported by the archive
     * @return {@code true} if the name should be rejected
     */
    protected static boolean isPathTraversal(final String name) {
        if (name == null || name.isEmpty()) {
            return true;
        }
        // Absolute paths (Unix or Windows-style) are unsafe in the
        // context of an archive extracted into a sandbox root.
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
     * Copies up to {@code limit} bytes from {@code in} to {@code out}, returning
     * the actual number of bytes copied. Used by archive extractors to bound
     * the amount of memory consumed when buffering an entry's uncompressed
     * payload.
     *
     * @param in the source stream
     * @param out the sink stream
     * @param limit the maximum number of bytes to copy (inclusive). Values
     *              {@code <= 0} cause the method to return without reading.
     * @return the number of bytes actually copied
     * @throws IOException if reading from {@code in} or writing to {@code out}
     *                     fails
     */
    protected static long copyBounded(final InputStream in, final OutputStream out, final long limit) throws IOException {
        if (limit <= 0) {
            return 0;
        }
        final byte[] buffer = new byte[8192];
        long total = 0;
        int read;
        while (total < limit && (read = in.read(buffer, 0, (int) Math.min(buffer.length, limit - total))) != IOUtils.EOF) {
            out.write(buffer, 0, read);
            total += read;
        }
        return total;
    }
}
