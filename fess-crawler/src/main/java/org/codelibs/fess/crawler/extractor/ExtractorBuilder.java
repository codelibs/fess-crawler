/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
import java.util.Map;

import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.poi.util.StringUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtractorBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ExtractorBuilder.class);

    private final InputStream in;

    private final Map<String, String> params;

    private final CrawlerContainer crawlerContainer;

    private String mimeType;

    private String filename;

    private int cacheFileSize = 1_000_000;

    private String extractorName = "tikaExtractor";

    private long maxContentLength = -1;

    protected ExtractorBuilder(CrawlerContainer crawlerContainer, final InputStream in, final Map<String, String> params) {
        this.crawlerContainer = crawlerContainer;
        this.in = in;
        this.params = params;
    }

    public ExtractorBuilder mimeType(final String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public ExtractorBuilder filename(final String filename) {
        this.filename = filename;
        return this;
    }

    public ExtractorBuilder extractorName(final String extractorName) {
        this.extractorName = extractorName;
        return this;
    }

    public ExtractorBuilder maxContentLength(final int maxContentLength) {
        this.maxContentLength = maxContentLength;
        return this;
    }

    public ExtractorBuilder cacheFileSize(final int cacheFileSize) {
        this.cacheFileSize = cacheFileSize;
        return this;
    }

    public ExtractData extract() {
        final ExtractorFactory extractorFactory = crawlerContainer.getComponent("extractorFactory");

        DeferredFileOutputStream dfos = null;
        try (DeferredFileOutputStream out = new DeferredFileOutputStream(cacheFileSize, "fess-extractor-", ".out", null)) {
            dfos = out;
            CopyUtil.copy(in, out);
            out.flush();

            if (maxContentLength >= 0) {
                final long contentLength = getContentLength(out);
                if (contentLength > maxContentLength) {
                    throw new MaxLengthExceededException(
                            "The content length (" + contentLength + " byte) is over " + maxContentLength + " byte.");
                }
            }

            Extractor extractor = StringUtil.isBlank(mimeType) ? null : extractorFactory.getExtractor(mimeType);
            if (extractor == null) {
                final String detectedMimeType = getMimeType(out);
                extractor = extractorFactory.getExtractor(detectedMimeType);
                if (extractor == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("use a defautl extractor as {} by {}", extractorName, mimeType);
                    }
                    extractor = crawlerContainer.getComponent(extractorName);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("use {} from {}, not {}", extractor.getClass().getName(), detectedMimeType, mimeType);
                }
            } else if (logger.isDebugEnabled()) {
                logger.debug("use {} from {}", extractor.getClass().getName(), mimeType);
            }

            try (InputStream is = getContentInputStream(out)) {
                return extractor.getText(is, params);
            }
        } catch (final CrawlingAccessException e) {
            throw e;
        } catch (final Exception e) {
            throw new ExtractException("Failed to extract data.", e);
        } finally {
            if (dfos != null && !dfos.isInMemory()) {
                final File file = dfos.getFile();
                if (!file.delete()) {
                    logger.warn("Failed to delete {}.", file.getAbsolutePath());
                }
            }
        }

    }

    protected String getMimeType(final DeferredFileOutputStream out) throws IOException {
        final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
        try (InputStream is = getContentInputStream(out)) {
            return mimeTypeHelper.getContentType(is, filename);
        }
    }

    protected InputStream getContentInputStream(final DeferredFileOutputStream out) throws IOException {
        if (out.isInMemory()) {
            return new ByteArrayInputStream(out.getData());
        }
        return new FileInputStream(out.getFile());
    }

    protected long getContentLength(final DeferredFileOutputStream out) throws IOException {
        if (out.isInMemory()) {
            return out.getData().length;
        }
        return out.getFile().length();
    }
}
