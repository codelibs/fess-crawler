/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;
import org.codelibs.fess.crawler.util.IgnoreCloseInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;

/**
 * @author shinsuke
 *
 */
public class TarExtractor extends AbstractExtractor {
    private static final Logger logger = LoggerFactory.getLogger(TarExtractor.class);

    @Resource
    protected ArchiveStreamFactory archiveStreamFactory;

    protected long maxContentSize = -1;

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        if (in == null) {
            throw new CrawlerSystemException("The inputstream is null.");
        }

        final MimeTypeHelper mimeTypeHelper = getMimeTypeHelper();
        final ExtractorFactory extractorFactory = getExtractorFactory();
        return new ExtractData(getTextInternal(in, mimeTypeHelper, extractorFactory));
    }

    protected String getTextInternal(final InputStream in, final MimeTypeHelper mimeTypeHelper, final ExtractorFactory extractorFactory) {

        final StringBuilder buf = new StringBuilder(1000);

        ArchiveInputStream ais = null;

        try {
            ais = archiveStreamFactory.createArchiveInputStream("tar", in);
            TarArchiveEntry entry = null;
            long contentSize = 0;
            while ((entry = (TarArchiveEntry) ais.getNextEntry()) != null) {
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
        } finally {
            CloseableUtil.closeQuietly(ais);
        }

        return buf.toString().trim();
    }

    public void setMaxContentSize(final long maxContentSize) {
        this.maxContentSize = maxContentSize;
    }
}
