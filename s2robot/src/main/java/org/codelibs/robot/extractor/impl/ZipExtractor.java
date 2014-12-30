/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.extractor.impl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.IOUtils;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.container.ComponentContainer;
import org.codelibs.robot.entity.ExtractData;
import org.codelibs.robot.extractor.ExtractException;
import org.codelibs.robot.extractor.Extractor;
import org.codelibs.robot.extractor.ExtractorFactory;
import org.codelibs.robot.helper.MimeTypeHelper;
import org.codelibs.robot.util.IgnoreCloseInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class ZipExtractor implements Extractor {
    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(ZipExtractor.class);

    @Resource
    protected ComponentContainer componentContainer;

    @Resource
    protected ArchiveStreamFactory archiveStreamFactory;

    @Override
    public ExtractData getText(final InputStream in,
            final Map<String, String> params) {
        if (in == null) {
            throw new RobotSystemException("The inputstream is null.");
        }

        final MimeTypeHelper mimeTypeHelper = componentContainer
                .getComponent("mimeTypeHelper");
        if (mimeTypeHelper == null) {
            throw new RobotSystemException("MimeTypeHelper is unavailable.");
        }

        final ExtractorFactory extractorFactory = componentContainer
                .getComponent("extractorFactory");
        if (extractorFactory == null) {
            throw new RobotSystemException("ExtractorFactory is unavailable.");
        }

        final StringBuilder buf = new StringBuilder(1000);

        ArchiveInputStream ais = null;

        try {
            ais = archiveStreamFactory.createArchiveInputStream(in);
            ZipArchiveEntry entry = null;
            while ((entry = (ZipArchiveEntry) ais.getNextEntry()) != null) {
                final String filename = entry.getName();
                final String mimeType = mimeTypeHelper.getContentType(null,
                        filename);
                if (mimeType != null) {
                    final Extractor extractor = extractorFactory
                            .getExtractor(mimeType);
                    if (extractor != null) {
                        try {
                            final Map<String, String> map = new HashMap<String, String>();
                            map.put(TikaMetadataKeys.RESOURCE_NAME_KEY,
                                    filename);
                            buf.append(extractor.getText(
                                    new IgnoreCloseInputStream(ais), map)
                                    .getContent());
                            buf.append('\n');
                        } catch (final Exception e) {
                            if (logger.isDebugEnabled()) {
                                logger.debug(
                                        "Exception in an internal extractor.",
                                        e);
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            if (buf.length() == 0) {
                throw new ExtractException("Could not extract a content.", e);
            }
        } finally {
            IOUtils.closeQuietly(ais);
        }

        return new ExtractData(buf.toString());
    }
}
