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
import java.util.List;

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

    @Resource
    protected CrawlerContainer crawlerContainer;

    public void register(final List<String> keyList) {
        if (keyList == null || keyList.isEmpty()) {
            throw new IllegalArgumentException("keyList must not be null or empty.");
        }
        getExtractorFactory().addExtractor(keyList, this);
    }

    protected MimeTypeHelper getMimeTypeHelper() {
        final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
        if (mimeTypeHelper == null) {
            throw new CrawlerSystemException("MimeTypeHelper is unavailable.");
        }
        return mimeTypeHelper;
    }

    protected ExtractorFactory getExtractorFactory() {
        final ExtractorFactory extractorFactory = crawlerContainer.getComponent("extractorFactory");
        if (extractorFactory == null) {
            throw new CrawlerSystemException("ExtractorFactory is unavailable.");
        }
        return extractorFactory;
    }

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
}
