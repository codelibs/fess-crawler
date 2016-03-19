/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.SecureContentHandler;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.util.UnsafeStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author shinsuke
 *
 */
public class TikaExtractor implements Extractor {
    private static final Logger logger = LoggerFactory
            .getLogger(TikaExtractor.class);

    @Resource
    protected CrawlerContainer crawlerContainer;

    public String outputEncoding = Constants.UTF_8;

    public boolean readAsTextIfFailed = true;

    public long maxCompressionRatio = 100;

    public long maxUncompressionSize = 1000000;

    public int initialBufferSize = 10000;

    public int memorySize = 1024 * 1024; //1mb

    public int maxAlphanumTermSize = -1;

    public TikaConfig tikaConfig;

    protected Map<String, String> pdfPasswordMap = new HashMap<String, String>();


    @PostConstruct
    public void init() {
        if (tikaConfig == null) {
            tikaConfig = TikaConfig.getDefaultConfig();
        }

        if (logger.isDebugEnabled()) {
            final Parser parser = tikaConfig.getParser();
            logger.debug("supportedTypes: {}",
                    parser.getSupportedTypes(new ParseContext()));
        }
    }

    @Override
    public ExtractData getText(final InputStream inputStream,
            final Map<String, String> params) {
        if (inputStream == null) {
            throw new CrawlerSystemException("The inputstream is null.");
        }

        final File tempFile;
        final boolean isByteStream = inputStream instanceof ByteArrayInputStream;
        if (isByteStream) {
            inputStream.mark(0);
            tempFile = null;
        } else {
            try {
                tempFile = File.createTempFile("tikaExtractor-", ".out");
            } catch (final IOException e) {
                throw new ExtractException("Could not create a temp file.", e);
            }
        }

        try {
            final PrintStream originalOutStream = System.out;
            final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outStream, true));
            final PrintStream originalErrStream = System.err;
            final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errStream, true));
            try {
                final String resourceName = params == null ? null : params
                        .get(TikaMetadataKeys.RESOURCE_NAME_KEY);
                final String contentType = params == null ? null : params
                        .get(HttpHeaders.CONTENT_TYPE);
                String contentEncoding = params == null ? null : params
                        .get(HttpHeaders.CONTENT_ENCODING);

                // password for pdf
                String pdfPassword = params == null ? null : params
                        .get(ExtractData.PDF_PASSWORD);
                if (pdfPassword == null && params != null) {
                    pdfPassword = getPdfPassword(params.get(ExtractData.URL),
                            resourceName);
                }

                final Metadata metadata = createMetadata(resourceName,
                        contentType, contentEncoding, pdfPassword);

                final Parser parser = new DetectParser();
                final ParseContext parseContext = new ParseContext();
                parseContext.set(Parser.class, parser);

                String content = getContent(writer -> {
                    InputStream in = null;
                    try {
                        if (!isByteStream) {
                            try (OutputStream out = new FileOutputStream(tempFile)) {
                                CopyUtil.copy(inputStream, out);
                            }
                            in = new FileInputStream(tempFile);
                        } else {
                            in = inputStream;
                        }
                        parser.parse(in, new BodyContentHandler(writer), metadata, parseContext);
                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                }, contentEncoding);
                if (StringUtil.isBlank(content)) {
                    if (resourceName != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("retry without a resource name: {}", resourceName);
                        }
                        final Metadata metadata2 = createMetadata(null,
                                contentType, contentEncoding, pdfPassword);
                        content = getContent(writer -> {
                            InputStream in = null;
                            try {
                                if (isByteStream) {
                                    inputStream.reset();
                                    in = inputStream;
                                } else {
                                    in = new FileInputStream(tempFile);
                                }
                                parser.parse(in, new BodyContentHandler(writer), metadata2, parseContext);
                            } finally {
                                IOUtils.closeQuietly(in);
                            }
                        }, contentEncoding);
                    }
                    if (StringUtil.isBlank(content) && contentType != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("retry without a content type: {}", contentType);
                        }
                        final Metadata metadata3 = createMetadata(null, null,
                                contentEncoding, pdfPassword);
                        content = getContent(writer -> {
                            InputStream in = null;
                            try {
                                if (isByteStream) {
                                    inputStream.reset();
                                    in = inputStream;
                                } else {
                                    in = new FileInputStream(tempFile);
                                }
                                parser.parse(in, new BodyContentHandler(writer), metadata3, parseContext);
                            } finally {
                                IOUtils.closeQuietly(in);
                            }
                        }, contentEncoding);
                    }

                    if (readAsTextIfFailed && StringUtil.isBlank(content)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("read the content as a text.");
                        }
                        if (contentEncoding == null) {
                            contentEncoding = Constants.UTF_8;
                        }
                        final String enc = contentEncoding;
                        content = getContent(writer -> {
                            BufferedReader br = null;
                            try {
                                if (isByteStream) {
                                    inputStream.reset();
                                    br = new BufferedReader(new InputStreamReader(inputStream, enc));
                                } else {
                                    br = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile), enc));
                                }
                                String line;
                                while ((line = br.readLine()) != null) {
                                    writer.write(line);
                                }
                            } catch (final Exception e) {
                                logger.warn("Could not read " + (tempFile != null ? tempFile.getAbsolutePath() : "a byte stream"), e);
                            } finally {
                                IOUtils.closeQuietly(br);
                            }
                        }, contentEncoding);
                    }
                }
                final ExtractData extractData = new ExtractData(content);

                final String[] names = metadata.names();
                Arrays.sort(names);
                for (final String name : names) {
                    extractData.putValues(name, metadata.getValues(name));
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Result: metadata: {}", metadata);
                }

                return extractData;
            } catch (final TikaException e) {
                if (e.getMessage().indexOf("bomb") >= 0) {
                    throw e;
                }
                final Throwable cause = e.getCause();
                if (cause instanceof SAXException) {
                    final Extractor xmlExtractor = crawlerContainer
                            .getComponent("xmlExtractor");
                    if (xmlExtractor != null) {
                        InputStream in = null;
                        try {
                            if (isByteStream) {
                                inputStream.reset();
                                in = inputStream;
                            } else {
                                in = new FileInputStream(tempFile);
                            }
                            return xmlExtractor.getText(in, params);
                        } finally {
                            IOUtils.closeQuietly(in);
                        }
                    }
                }
                throw e;
            } finally {
                if (originalOutStream != null) {
                    System.setOut(originalOutStream);
                }
                if (originalErrStream != null) {
                    System.setErr(originalErrStream);
                }
                try {
                    if (logger.isInfoEnabled()) {
                        final byte[] bs = outStream.toByteArray();
                        if (bs.length != 0) {
                            logger.info(new String(bs, outputEncoding));
                        }
                    }
                    if (logger.isWarnEnabled()) {
                        final byte[] bs = errStream.toByteArray();
                        if (bs.length != 0) {
                            logger.warn(new String(bs, outputEncoding));
                        }
                    }
                } catch (final Exception e) {
                    // NOP
                }
            }
        } catch (final Exception e) {
            throw new ExtractException("Could not extract a content.", e);
        } finally {
            if (tempFile != null && !tempFile.delete()) {
                logger.warn("Failed to delete " + tempFile.getAbsolutePath());
            }
        }
    }

    protected InputStream getContentStream(DeferredFileOutputStream dfos) throws IOException {
        if (dfos.isInMemory()) {
            return new ByteArrayInputStream(dfos.getData());
        } else {
            return new BufferedInputStream(new FileInputStream(dfos.getFile()));
        }
    }

    protected String getContent(final ContentWriter out, String encoding) throws TikaException {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("tika", ".tmp");
        } catch (IOException e) {
            throw new CrawlerSystemException("Failed to create a temp file.", e);
        }

        try (DeferredFileOutputStream dfos = new DeferredFileOutputStream(memorySize, tempFile)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dfos));
            out.accept(writer);
            writer.flush();

            try (Reader reader = new InputStreamReader(getContentStream(dfos), encoding == null ? Constants.UTF_8 : encoding)) {
                final UnsafeStringBuilder buf = new UnsafeStringBuilder(initialBufferSize);
                boolean isSpace = false;
                int alphanumSize = 0;
                int c;
                while ((c = reader.read()) != -1) {
                    if (Character.isISOControl(c) || c == '\u0020' || c == '\u3000' || c == 65533) {
                        // space
                        if (!isSpace) {
                            buf.append(' ');
                            isSpace = true;
                        }
                        alphanumSize = 0;
                    } else if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                        // alphanum
                        if (maxAlphanumTermSize >= 0) {
                            if (alphanumSize < maxAlphanumTermSize) {
                                buf.appendCodePoint(c);
                            }
                            alphanumSize++;
                        } else {
                            buf.appendCodePoint(c);
                        }
                        isSpace = false;
                    } else {
                        buf.appendCodePoint(c);
                        isSpace = false;
                        alphanumSize = 0;
                    }
                }

                return buf.toUnsafeString().trim();
            }
        } catch (TikaException e) {
            throw e;
        } catch (Exception e) {
            throw new ExtractException("Failed to read a content.", e);
        } finally {
            if (tempFile.exists() && !tempFile.delete()) {
                logger.warn("Failed to delete " + tempFile.getAbsolutePath());
            }
        }
    }

    String getPdfPassword(final String url, final String resourceName) {
        if (pdfPasswordMap.isEmpty()) {
            return null;
        }

        String value = null;
        if (StringUtil.isNotEmpty(url)) {
            value = url;
        } else if (StringUtil.isNotEmpty(resourceName)) {
            value = resourceName;
        }

        if (value != null) {
            for (final Map.Entry<String, String> entry : pdfPasswordMap
                    .entrySet()) {
                if (value.matches(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    private Metadata createMetadata(final String resourceName,
            final String contentType, final String contentEncoding,
            final String pdfPassword) {
        final Metadata metadata = new Metadata();
        if (StringUtil.isNotEmpty(resourceName)) {
            metadata.set(TikaMetadataKeys.RESOURCE_NAME_KEY, resourceName);
        }
        if (StringUtil.isNotBlank(contentType)) {
            metadata.set(HttpHeaders.CONTENT_TYPE, contentType);
        }
        if (StringUtil.isNotBlank(contentEncoding)) {
            metadata.set(HttpHeaders.CONTENT_ENCODING, contentEncoding);
        }
        if (pdfPassword != null) {
            metadata.add(ExtractData.PDF_PASSWORD, pdfPassword);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("metadata: {}", metadata);
        }

        return metadata;
    }

    public void addPdfPassword(final String regex, final String password) {
        pdfPasswordMap.put(regex, password);
    }

    // workaround: Tika does not have extention points.
    protected class DetectParser extends CompositeParser {
        private static final long serialVersionUID = 1L;

        /**
         * The type detector used by this parser to auto-detect the type of a
         * document.
         */
        private final Detector detector; // always set in the constructor

        /**
         * Creates an auto-detecting parser instance using the default Tika
         * configuration.
         */
        public DetectParser() {
            this(tikaConfig);
        }

        public DetectParser(final TikaConfig config) {
            super(config.getMediaTypeRegistry(), config.getParser());
            detector = config.getDetector();
        }

        @Override
        public void parse(final InputStream stream,
                final ContentHandler handler, final Metadata metadata,
                final ParseContext context) throws IOException, SAXException,
                TikaException {
            final TemporaryResources tmp = new TemporaryResources();
            try {
                final TikaInputStream tis = TikaInputStream.get(stream, tmp);

                // Automatically detect the MIME type of the document
                final MediaType type = detector.detect(tis, metadata);
                metadata.set(HttpHeaders.CONTENT_TYPE, type.toString());

                // TIKA-216: Zip bomb prevention
                final SecureContentHandler sch = new SecureContentHandler(
                        handler, tis);

                sch.setMaximumCompressionRatio(maxCompressionRatio);
                sch.setOutputThreshold(maxUncompressionSize);

                if (logger.isDebugEnabled()) {
                    logger.debug(
                            "type: {}, metadata: {}, maxCompressionRatio: {}, maxUncompressionSize: {}",
                            type, metadata, maxCompressionRatio,
                            maxUncompressionSize);

                }

                try {
                    // Parse the document
                    super.parse(tis, sch, metadata, context);
                } catch (final SAXException e) {
                    // Convert zip bomb exceptions to TikaExceptions
                    sch.throwIfCauseOf(e);
                    throw e;
                }
            } finally {
                tmp.dispose();
            }
        }

        @Override
        public void parse(final InputStream stream,
                final ContentHandler handler, final Metadata metadata)
                throws IOException, SAXException, TikaException {
            final ParseContext context = new ParseContext();
            context.set(Parser.class, this);
            parse(stream, handler, metadata, context);
        }

    }

    @FunctionalInterface
    protected interface ContentWriter {
        void accept(Writer writer) throws Exception;
    }
}
