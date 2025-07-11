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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;
import org.codelibs.fess.crawler.util.IgnoreCloseInputStream;

import jakarta.annotation.Resource;

/**
 * Extracts text content from ZIP archives.
 */
public class ZipExtractor extends AbstractExtractor {
    private static final Logger logger = LogManager.getLogger(ZipExtractor.class);

    /**
     * The archive stream factory.
     */
    @Resource
    protected ArchiveStreamFactory archiveStreamFactory;

    /**
     * The maximum content size.
     */
    protected long maxContentSize = -1;

    /**
     * Creates a new ZipExtractor instance.
     */
    public ZipExtractor() {
        super();
    }

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        if (in == null) {
            throw new CrawlerSystemException("The inputstream is null.");
        }

        final MimeTypeHelper mimeTypeHelper = getMimeTypeHelper();
        final ExtractorFactory extractorFactory = getExtractorFactory();
        final StringBuilder buf = new StringBuilder(1000);

        try (final ArchiveInputStream ais =
                archiveStreamFactory.createArchiveInputStream(in.markSupported() ? in : new BufferedInputStream(in))) {
            ZipArchiveEntry entry = null;
            long contentSize = 0;
            while ((entry = (ZipArchiveEntry) ais.getNextEntry()) != null) {
                contentSize += entry.getSize();
                if (maxContentSize != -1 && contentSize > maxContentSize) {
                    throw new MaxLengthExceededException("Extracted size is " + contentSize + " > " + maxContentSize);
                }
                final String filename = entry.getName();
                final String mimeType = mimeTypeHelper.getContentType(null, filename);
                if (mimeType != null) {
                    final Extractor extractor = extractorFactory.getExtractor(mimeType);
                    if (extractor != null) {
                        try {
                            final Map<String, String> map = new HashMap<>();
                            map.put(ExtractData.RESOURCE_NAME_KEY, filename);
                            buf.append(extractor.getText(new IgnoreCloseInputStream(ais), map).getContent());
                            buf.append('\n');
                        } catch (final Exception e) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Exception in an internal extractor.", e);
                            }
                        }
                    }
                }
            }
        } catch (final MaxLengthExceededException e) {
            throw e;
        } catch (final Exception e) {
            if (buf.length() == 0) {
                throw new ExtractException("Could not extract a content.", e);
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
}