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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.lang3.SystemUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.PasswordProvider;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.SecureContentHandler;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author shinsuke
 *
 */
public class TikaExtractor extends PasswordBasedExtractor {

    private static final Logger logger = LoggerFactory
            .getLogger(TikaExtractor.class);

    public static final String TIKA_TESSERACT_CONFIG = "tika.tesseract.config";

    public static final String TIKA_PDF_CONFIG = "tika.pdf.config";

    private static final String FILE_PASSWORD = "fess.file.password";

    protected String outputEncoding = Constants.UTF_8;

    protected boolean readAsTextIfFailed = true;

    protected long maxCompressionRatio = 100;

    protected long maxUncompressionSize = 1000000;

    protected int initialBufferSize = 10000;

    protected boolean replaceDuplication = false;

    protected int memorySize = 1024 * 1024; //1mb

    protected int maxAlphanumTermSize = -1;

    protected int maxSymbolTermSize = -1;

    protected TikaConfig tikaConfig;

    private Map<String, TesseractOCRConfig> tesseractOCRConfigMap = new ConcurrentHashMap<>();

    private Map<String, PDFParserConfig> pdfParserConfigMap = new ConcurrentHashMap<>();

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
                String pdfPassword = getPassword(params);

                final Metadata metadata = createMetadata(resourceName,
                        contentType, contentEncoding, pdfPassword);

                final Parser parser = new TikaDetectParser();
                final ParseContext parseContext = createParseContext(parser, params);

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
                        CloseableUtil.closeQuietly(in);
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
                                CloseableUtil.closeQuietly(in);
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
                                CloseableUtil.closeQuietly(in);
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
                                CloseableUtil.closeQuietly(br);
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
                            CloseableUtil.closeQuietly(in);
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

    protected ParseContext createParseContext(final Parser parser, final Map<String, String> params) {
        final ParseContext parseContext = new ParseContext();
        parseContext.set(Parser.class, parser);

        final String tesseractConfigPath = params != null ? params.get(TIKA_TESSERACT_CONFIG) : null;
        if (StringUtil.isNotBlank(tesseractConfigPath)) {
            TesseractOCRConfig tesseractOCRConfig = tesseractOCRConfigMap.get(tesseractConfigPath);
            if (tesseractOCRConfig == null) {
                try (final InputStream in = new FileInputStream(tesseractConfigPath)) {
                    tesseractOCRConfig = new TesseractOCRConfig(in);
                } catch (Exception e) {
                    logger.warn("Could not load " + tesseractConfigPath, e);
                    tesseractOCRConfig = new TesseractOCRConfig();
                }
                tesseractOCRConfigMap.put(tesseractConfigPath, tesseractOCRConfig);
            }
            parseContext.set(TesseractOCRConfig.class, tesseractOCRConfig);
        }

        final String pdfParserConfigPath = params != null ? params.get(TIKA_PDF_CONFIG) : null;
        if (StringUtil.isNotBlank(pdfParserConfigPath)) {
            PDFParserConfig pdfParserConfig = pdfParserConfigMap.get(pdfParserConfigPath);
            if (pdfParserConfig == null) {
                try (final InputStream in = new FileInputStream(pdfParserConfigPath)) {
                    pdfParserConfig = new PDFParserConfig(in);
                } catch (Exception e) {
                    logger.warn("Could not load " + pdfParserConfigPath, e);
                    pdfParserConfig = new PDFParserConfig();
                }
                pdfParserConfigMap.put(pdfParserConfigPath, pdfParserConfig);
            }
            parseContext.set(PDFParserConfig.class, pdfParserConfig);
        }

        parseContext.set(PasswordProvider.class, new PasswordProvider() {
            public String getPassword(Metadata metadata) {
                return metadata.get(FILE_PASSWORD);
            }
        });

        return parseContext;
    }

    protected InputStream getContentStream(final DeferredFileOutputStream dfos) throws IOException {
        if (dfos.isInMemory()) {
            return new ByteArrayInputStream(dfos.getData());
        } else {
            return new BufferedInputStream(new FileInputStream(dfos.getFile()));
        }
    }

    protected String getContent(final ContentWriter out, final String encoding) throws TikaException {
        File tempFile = null;
        final String enc = encoding == null ? Constants.UTF_8 : encoding;
        try (DeferredFileOutputStream dfos = new DeferredFileOutputStream(memorySize, "tika", ".tmp", SystemUtils.getJavaIoTmpDir())) {
            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(dfos, enc));
            out.accept(writer);
            writer.flush();

            if (!dfos.isInMemory()) {
                tempFile = dfos.getFile();
            }

            try (Reader reader = new InputStreamReader(getContentStream(dfos), enc)) {
                return TextUtil.normalizeText(reader).initialCapacity(initialBufferSize).maxAlphanumTermSize(maxAlphanumTermSize)
                        .maxSymbolTermSize(maxSymbolTermSize).duplicateTermRemoved(replaceDuplication).execute();
            }
        } catch (final TikaException e) {
            throw e;
        } catch (final Exception e) {
            throw new ExtractException("Failed to read a content.", e);
        } finally {
            if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                logger.warn("Failed to delete " + tempFile.getAbsolutePath());
            }
        }
    }

    protected Metadata createMetadata(final String resourceName,
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
            metadata.add(FILE_PASSWORD, pdfPassword);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("metadata: {}", metadata);
        }

        return metadata;
    }

    // workaround: Tika does not have extention points.
    protected class TikaDetectParser extends CompositeParser {
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
        public TikaDetectParser() {
            this(tikaConfig);
        }

        public TikaDetectParser(final TikaConfig config) {
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
                final SecureContentHandler sch = new SecureContentHandler(handler, tis);

                sch.setMaximumCompressionRatio(maxCompressionRatio);
                sch.setOutputThreshold(maxUncompressionSize);

                //pass self to handle embedded documents if
                //the caller hasn't specified one.
                if (context.get(EmbeddedDocumentExtractor.class) == null) {
                    Parser p = context.get(Parser.class);
                    if (p == null) {
                        context.set(Parser.class, this);
                    }
                    context.set(EmbeddedDocumentExtractor.class,
                            new ParsingEmbeddedDocumentExtractor(context));
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("type: {}, metadata: {}, maxCompressionRatio: {}, maxUncompressionSize: {}", type, metadata,
                            maxCompressionRatio, maxUncompressionSize);
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
        void accept(Writer writer) throws IOException, TikaException, SAXException;
    }

    public void setOutputEncoding(final String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    public void setReadAsTextIfFailed(final boolean readAsTextIfFailed) {
        this.readAsTextIfFailed = readAsTextIfFailed;
    }

    public void setMaxCompressionRatio(final long maxCompressionRatio) {
        this.maxCompressionRatio = maxCompressionRatio;
    }

    public void setMaxUncompressionSize(final long maxUncompressionSize) {
        this.maxUncompressionSize = maxUncompressionSize;
    }

    public void setInitialBufferSize(final int initialBufferSize) {
        this.initialBufferSize = initialBufferSize;
    }

    public void setReplaceDuplication(final boolean replaceDuplication) {
        this.replaceDuplication = replaceDuplication;
    }

    public void setMemorySize(final int memorySize) {
        this.memorySize = memorySize;
    }

    public void setMaxAlphanumTermSize(final int maxAlphanumTermSize) {
        this.maxAlphanumTermSize = maxAlphanumTermSize;
    }

    public void setMaxSymbolTermSize(final int maxSymbolTermSize) {
        this.maxSymbolTermSize = maxSymbolTermSize;
    }

    public void setTikaConfig(final TikaConfig tikaConfig) {
        this.tikaConfig = tikaConfig;
    }
}
