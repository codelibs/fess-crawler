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

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.image.WebPParser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.ocr.TesseractOCRParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Parser for WebP images that also runs Tesseract OCR on them.
 * <p>
 * Tika hands an image to the OCR parser only from {@code ImageParser} and the other parsers that run the OCR enrichers
 * after their own parsing. {@link WebPParser} is not one of them, so a WebP image is never OCR'd even though Tesseract
 * reads it through Leptonica. This parser parses the metadata with {@link WebPParser}, then passes the image to
 * {@link TesseractOCRParser} unless {@link TesseractOCRConfig#isSkipOcr()} is set in the {@link ParseContext}.
 * Register it in place of {@link WebPParser}.
 * </p>
 * <p>
 * The Tesseract in use must read WebP, i.e. its Leptonica must be built with libwebp (the packages of Alpine and
 * Debian are). Otherwise Tesseract fails to read the image and no text is extracted from it.
 * </p>
 */
public class WebPOcrParser implements Parser {

    private static final long serialVersionUID = 1L;

    private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(MediaType.image("webp"));

    private final WebPParser webPParser = new WebPParser();

    private final TesseractOCRParser ocrParser;

    /**
     * Creates a parser that uses the Tesseract command found in the PATH.
     *
     * @throws TikaConfigException if the OCR parser cannot be created
     */
    public WebPOcrParser() throws TikaConfigException {
        ocrParser = new TesseractOCRParser();
    }

    @Override
    public Set<MediaType> getSupportedTypes(final ParseContext context) {
        return SUPPORTED_TYPES;
    }

    @Override
    public void parse(final TikaInputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context)
            throws IOException, SAXException, TikaException {
        webPParser.parse(stream, handler, metadata, context);
        final TesseractOCRConfig config = context.get(TesseractOCRConfig.class);
        if (config != null && config.isSkipOcr() || !ocrParser.hasTesseract()) {
            return;
        }
        stream.rewind();
        ocrParser.parse(stream, handler, metadata, context);
    }
}
