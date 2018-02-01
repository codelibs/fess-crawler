/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.helper.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesFactory;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.MimeTypeException;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

/**
 * @author shinsuke
 *
 */
public class MimeTypeHelperImpl implements MimeTypeHelper {
    private static final String MIME_TYPES_RESOURCE_NAME = "/org/codelibs/fess/crawler/mime/tika-mimetypes.xml";

    private MimeTypes mimeTypes;

    public MimeTypeHelperImpl() {
        try {
            mimeTypes = MimeTypesFactory.create(MIME_TYPES_RESOURCE_NAME);
        } catch (final Exception e) {
            throw new CrawlerSystemException(
                    "Could not initialize MimeTypeHelper.", e);
        }
    }

    @Override
    public String getContentType(final InputStream is, final String filename) {
        if (StringUtil.isEmpty(filename)) {
            throw new MimeTypeException("The filename is empty.");
        }
        final Map<String, String> params = new HashMap<>();
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, filename);
        return getContentType(is, params);
    }

    @Override
    public String getContentType(final InputStream is,
            final Map<String, String> params) {
        final String filename = params.get(TikaMetadataKeys.RESOURCE_NAME_KEY);
        if (StringUtil.isEmpty(filename) && is == null) {
            throw new MimeTypeException(
                    "The filename or input stream is empty.");
        }

        final Metadata metadata = new Metadata();
        metadata.add(TikaMetadataKeys.RESOURCE_NAME_KEY, normalizeFilename(filename));

        try {
            final MediaType mediaType = mimeTypes.detect(
                    is == null || is.markSupported() ? is
                            : new BufferedInputStream(is), metadata);
            return mediaType.getType() + "/" + mediaType.getSubtype();
        } catch (final IOException e) {
            throw new MimeTypeException("Could not detect a content type.", e);
        }
    }

    protected String normalizeFilename(final String filename) {
        if (StringUtil.isBlank(filename)) {
            return filename;
        }
        final StringBuilder buf = new StringBuilder(filename.length() + 10);
        for (int i = 0; i < filename.length(); i++) {
            final char c = filename.charAt(i);
            switch (c) {
            case '?':
                buf.append("%3f");
                break;
            case '#':
                buf.append("%23");
                break;
            case '@':
                buf.append("%40");
                break;
            case ':':
                buf.append("%3a");
                break;
            case '/':
                buf.append("%2f");
                break;
            default:
                buf.append(c);
                break;
            }
        }
        return buf.toString();
    }
}
