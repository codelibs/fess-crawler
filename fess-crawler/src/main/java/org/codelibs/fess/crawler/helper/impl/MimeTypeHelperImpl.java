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
package org.codelibs.fess.crawler.helper.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.mime.MimeTypesFactory;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.MimeTypeException;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

/**
 * MimeTypeHelperImpl is a helper class that detects the MIME type of a given input stream or filename.
 * It uses the Apache Tika library to detect the MIME type.
 *
 * <p>
 * This class provides methods to:
 * </p>
 * <ul>
 *   <li>Detect the MIME type based on the input stream and filename.</li>
 *   <li>Normalize the filename to handle special characters.</li>
 *   <li>Configure whether to use the filename for MIME type detection.</li>
 *   <li>Configure whether to use the filename for MIME type detection when the stream is octet-stream.</li>
 * </ul>
 *
 * <p>
 * The MIME type detection is based on the {@code tika-mimetypes.xml} resource, which is loaded during initialization.
 * </p>
 *
 * <p>
 * Usage:
 * </p>
 * <pre>
 * MimeTypeHelperImpl mimeTypeHelper = new MimeTypeHelperImpl();
 * String contentType = mimeTypeHelper.getContentType(inputStream, filename);
 * </pre>
 */
public class MimeTypeHelperImpl implements MimeTypeHelper {
    /** The resource name for the MIME types configuration file. */
    protected static final String MIME_TYPES_RESOURCE_NAME = "/org/codelibs/fess/crawler/mime/tika-mimetypes.xml";

    /** The MimeTypes instance for detecting MIME types. */
    protected MimeTypes mimeTypes;

    /** Whether to use the filename for MIME type detection. */
    protected boolean useFilename = false;

    /** Whether to use the filename for MIME type detection when the stream is detected as octet-stream. */
    protected boolean useFilenameOnOctetStream = true;

    /**
     * Creates a new MimeTypeHelperImpl instance.
     * Initializes the MimeTypes instance using the default configuration.
     * @throws CrawlerSystemException if the MIME types configuration cannot be loaded
     */
    public MimeTypeHelperImpl() {
        try {
            mimeTypes = MimeTypesFactory.create(MIME_TYPES_RESOURCE_NAME);
        } catch (final Exception e) {
            throw new CrawlerSystemException("Could not initialize MimeTypeHelper.", e);
        }
    }

    @Override
    public String getContentType(final InputStream is, final String filename) {
        final Map<String, String> params = new HashMap<>();
        params.put(ExtractData.RESOURCE_NAME_KEY, filename);
        return getContentType(is, params);
    }

    @Override
    public String getContentType(final InputStream is, final Map<String, String> params) {
        final String filename = params.get(ExtractData.RESOURCE_NAME_KEY);
        if (StringUtil.isEmpty(filename) && is == null) {
            throw new MimeTypeException("Cannot detect MIME type: both filename and input stream are empty. At least one is required.");
        }

        final Metadata metadata = new Metadata();
        metadata.add(ExtractData.RESOURCE_NAME_KEY, normalizeFilename(filename));

        try {
            if (useFilename) {
                final MediaType mediaType = mimeTypes.detect(null, metadata);
                if (!MediaType.OCTET_STREAM.equals(mediaType)) {
                    return mediaType.getType() + "/" + mediaType.getSubtype();
                }
            }

            final MediaType mediaType = mimeTypes.detect(is == null || is.markSupported() ? is : new BufferedInputStream(is), metadata);
            if (useFilenameOnOctetStream && MediaType.OCTET_STREAM.equals(mediaType)) {
                final MediaType mediaTypeFromFilename = mimeTypes.detect(null, metadata);
                return mediaTypeFromFilename.getType() + "/" + mediaTypeFromFilename.getSubtype();
            }
            return mediaType.getType() + "/" + mediaType.getSubtype();
        } catch (final IOException e) {
            throw new MimeTypeException("Could not detect a content type.", e);
        }
    }

    /**
     * Normalizes the filename by replacing special characters.
     * @param filename The filename to normalize.
     * @return The normalized filename.
     */
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

    /**
     * Sets whether to use the filename for MIME type detection.
     *
     * @param useFilename true to use the filename for MIME type detection, false otherwise
     */
    public void setUseFilename(final boolean useFilename) {
        this.useFilename = useFilename;
    }

    /**
     * Sets whether to use the filename for MIME type detection when the stream is octet-stream.
     *
     * @param useFilenameOnOctetStream true to use the filename for MIME type detection when the stream is octet-stream; false otherwise.
     */
    public void setUseFilenameOnOctetStream(final boolean useFilenameOnOctetStream) {
        this.useFilenameOnOctetStream = useFilenameOnOctetStream;
    }
}
