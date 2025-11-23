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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.PasswordProvider;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.SecureContentHandler;
import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.io.PropertiesUtil;
import org.codelibs.core.io.ReaderUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.util.TextUtil;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import jakarta.annotation.PostConstruct;

/**
 * <p>
 * The {@link TikaExtractor} class is responsible for extracting text content and metadata from various file formats
 * using the Apache Tika library. It extends {@link PasswordBasedExtractor} to handle password-protected files.
 * </p>
 *
 * <p>
 * This class provides methods to extract text from an input stream, handling different scenarios such as:
 * </p>
 * <ul>
 *   <li>Normalizing text content</li>
 *   <li>Handling resource names and content types</li>
 *   <li>Retrying extraction without resource name or content type if the initial attempt fails</li>
 *   <li>Extracting text from metadata if the main content extraction fails</li>
 *   <li>Reading content as plain text if all other methods fail</li>
 *   <li>Applying post-extraction filters</li>
 *   <li>Handling Tika exceptions, including zip bomb exceptions</li>
 * </ul>
 *
 * <p>
 * The class also supports configuration options such as:
 * </p>
 * <ul>
 *   <li>Output encoding</li>
 *   <li>Maximum compression ratio and uncompression size</li>
 *   <li>Initial buffer size</li>
 *   <li>Memory size for temporary file storage</li>
 *   <li>Maximum term sizes for alphanumeric and symbolic terms</li>
 *   <li>Custom Tika configuration</li>
 *   <li>Tesseract OCR configuration for image-based documents</li>
 *   <li>PDF Parser configuration for PDF documents</li>
 * </ul>
 *
 * <p>
 * The {@link TikaDetectParser} inner class extends {@link CompositeParser} to provide auto-detection of the MIME type
 * of the document. It also handles zip bomb prevention and embedded document extraction.
 * </p>
 *
 * <p>
 * The {@link ContentWriter} functional interface is used to abstract the process of writing content to a writer.
 * </p>
 *
 * <p>
 * The class uses temporary files for processing large input streams and ensures that these files are deleted after
 * processing.
 * </p>
 *
 */
public class TikaExtractor extends PasswordBasedExtractor {

    private static final Logger logger = LogManager.getLogger(TikaExtractor.class);

    /**
     * Tesseract config file path.
     */
    public static final String TIKA_TESSERACT_CONFIG = "tika.tesseract.config";

    /**
     * PDF config file path.
     */
    public static final String TIKA_PDF_CONFIG = "tika.pdf.config";

    /**
     * A parameter key to normalize a text.
     */
    public static final String NORMALIZE_TEXT = "normalize_text";

    private static final String FILE_PASSWORD = "fess.file.password";

    /**
     * Output encoding.
     */
    protected String outputEncoding = Constants.UTF_8;

    /**
     * If true, read a content as a text when an extraction fails.
     */
    protected boolean readAsTextIfFailed = false;

    /**
     * Max compression ratio.
     */
    protected long maxCompressionRatio = 100;

    /**
     * Max uncompression size.
     */
    protected long maxUncompressionSize = 1000000;

    /**
     * Initial buffer size.
     */
    protected int initialBufferSize = 10000;

    /**
     * If true, duplicated terms are replaced.
     */
    protected boolean replaceDuplication = false;

    /**
     * Space characters. Default includes common space characters.
     */
    protected int[] spaceChars = { '\u0020', '\u00a0', '\u3000', '\ufffd' };

    /**
     * Memory size.
     */
    protected int memorySize = 1024 * 1024; //1mb

    /**
     * Max size of an alpha-numeric term.
     */
    protected int maxAlphanumTermSize = -1;

    /**
     * Max size of a symbol term.
     */
    protected int maxSymbolTermSize = -1;

    /**
     * Tika config.
     */
    protected TikaConfig tikaConfig;

    private final Map<String, TesseractOCRConfig> tesseractOCRConfigMap = new ConcurrentHashMap<>();

    private final Map<String, PDFParserConfig> pdfParserConfigMap = new ConcurrentHashMap<>();

    /**
     * Creates a new TikaExtractor instance.
     */
    public TikaExtractor() {
        super();
    }

    /**
     * Initializes this component.
     */
    @PostConstruct
    public void init() {
        if (tikaConfig == null && crawlerContainer != null) {
            try {
                tikaConfig = crawlerContainer.getComponent("tikaConfig");
            } catch (final Exception e) {
                logger.debug("tikaConfig component is not found.", e);
            }
        }

        if (tikaConfig == null) {
            tikaConfig = TikaConfig.getDefaultConfig();
        }

        if (logger.isDebugEnabled()) {
            final Parser parser = tikaConfig.getParser();
            logger.debug("supportedTypes: {}", parser.getSupportedTypes(new ParseContext()));
        }
    }

    @Override
    public ExtractData getText(final InputStream inputStream, final Map<String, String> params) {
        return getText(inputStream, params, null);
    }

    /**
     * Returns an extracted text.
     *
     * @param inputStream An input stream.
     * @param params A map of parameters.
     * @param postFilter A post filter.
     * @return An extracted data.
     */
    protected ExtractData getText(final InputStream inputStream, final Map<String, String> params,
            final BiConsumer<ExtractData, InputStream> postFilter) {
        if (inputStream == null) {
            throw new CrawlerSystemException("Tika input stream is null. Cannot extract text from null input.");
        }

        final File tempFile;
        final boolean isByteStream = inputStream instanceof ByteArrayInputStream;
        if (isByteStream) {
            inputStream.mark(0); // ByteArrayInputStream
            tempFile = null;
        } else {
            tempFile = createTempFile("tikaExtractor-", ".out", null);
        }

        try {
            final PrintStream originalOutStream = System.out;
            final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outStream, true));
            final PrintStream originalErrStream = System.err;
            final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errStream, true));
            try {
                final String resourceName = params == null ? null : params.get(ExtractData.RESOURCE_NAME_KEY);
                final String contentType = params == null ? null : params.get(ExtractData.CONTENT_TYPE);
                String contentEncoding = params == null ? null : params.get(ExtractData.CONTENT_ENCODING);
                final boolean normalizeText = params == null ? true : !Constants.FALSE.equalsIgnoreCase(params.get(NORMALIZE_TEXT));
                final String password = getPassword(params);

                final Metadata metadata = createMetadata(resourceName, contentType, contentEncoding, password);

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
                }, contentEncoding, normalizeText);
                if (StringUtil.isBlank(content)) {
                    if (resourceName != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("retry without a resource name: {}", resourceName);
                        }
                        final Metadata metadata2 = createMetadata(null, contentType, contentEncoding, password);
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
                        }, contentEncoding, normalizeText);
                    }
                    if (StringUtil.isBlank(content) && contentType != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("retry without a content type: {}", contentType);
                        }
                        final Metadata metadata3 = createMetadata(null, null, contentEncoding, password);
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
                        }, contentEncoding, normalizeText);
                    }

                    if (StringUtil.isBlank(content)) {
                        final List<String> list = new ArrayList<>();
                        for (final String name : metadata.names()) {
                            final String lowerName = name.toLowerCase(Locale.ROOT);
                            if (lowerName.contains("comment") || lowerName.contains("text")) {
                                final String[] values = metadata.getValues(name);
                                if (values != null) {
                                    Collections.addAll(list, values);
                                }
                            }
                        }
                        if (!list.isEmpty()) {
                            content = list.stream().filter(StringUtil::isNotBlank).collect(Collectors.joining(" "));
                        }
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
                        }, contentEncoding, normalizeText);
                    }
                }
                final ExtractData extractData = new ExtractData(content);
                final long contentLength;
                if (isByteStream) {
                    contentLength = ((ByteArrayInputStream) inputStream).available();
                } else {
                    contentLength = tempFile != null ? tempFile.length() : 0;
                }
                extractData.putValue("Content-Length", Long.toString(contentLength));

                final String[] names = metadata.names();
                Arrays.sort(names);
                for (final String name : names) {
                    extractData.putValues(name, metadata.getValues(name));
                }

                if (postFilter != null) {
                    InputStream in = null;
                    try {
                        if (isByteStream) {
                            inputStream.reset();
                            in = inputStream;
                        } else {
                            in = new FileInputStream(tempFile);
                        }
                        postFilter.accept(extractData, in);
                    } finally {
                        CloseableUtil.closeQuietly(in);
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Result: metadata: {}", metadata);
                }

                return extractData;
            } catch (final TikaException e) {
                if (e.getMessage().indexOf("bomb") >= 0) {
                    throw new ExtractException("Zip bomb detected.", e);
                }
                final Throwable cause = e.getCause();
                if (cause instanceof SAXException) {
                    final Extractor xmlExtractor = crawlerContainer.getComponent("xmlExtractor");
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
                    try {
                        System.setOut(originalOutStream);
                    } catch (Exception e) {
                        logger.warn("Failed to set originalOutStream.", e);
                    }
                }
                if (originalErrStream != null) {
                    try {
                        System.setErr(originalErrStream);
                    } catch (Exception e) {
                        logger.warn("Failed to set originalErrStream.", e);
                    }
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
            FileUtil.deleteInBackground(tempFile);
        }
    }

    /**
     * Creates a parse context.
     *
     * @param parser A parser.
     * @param params A map of parameters.
     * @return a parse context.
     */
    protected ParseContext createParseContext(final Parser parser, final Map<String, String> params) {
        final ParseContext parseContext = new ParseContext();
        parseContext.set(Parser.class, parser);

        final String tesseractConfigPath = params != null ? params.get(TIKA_TESSERACT_CONFIG) : null;
        if (StringUtil.isNotBlank(tesseractConfigPath)) {
            TesseractOCRConfig tesseractOCRConfig = tesseractOCRConfigMap.get(tesseractConfigPath);
            if (tesseractOCRConfig == null) {
                final Properties props = new Properties();
                PropertiesUtil.load(props, tesseractConfigPath);
                final Map<String, String> propMap =
                        props.entrySet().stream().collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
                tesseractOCRConfig = new TesseractOCRConfig();
                BeanUtil.copyMapToBean(propMap, tesseractOCRConfig);
                tesseractOCRConfigMap.put(tesseractConfigPath, tesseractOCRConfig);
            }
            parseContext.set(TesseractOCRConfig.class, tesseractOCRConfig);
        }

        final String pdfParserConfigPath = params != null ? params.get(TIKA_PDF_CONFIG) : null;
        if (StringUtil.isNotBlank(pdfParserConfigPath)) {
            PDFParserConfig pdfParserConfig = pdfParserConfigMap.get(pdfParserConfigPath);
            if (pdfParserConfig == null) {
                final Properties props = new Properties();
                PropertiesUtil.load(props, pdfParserConfigPath);
                final Map<String, String> propMap =
                        props.entrySet().stream().collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
                pdfParserConfig = new PDFParserConfig();
                BeanUtil.copyMapToBean(propMap, pdfParserConfig);
                pdfParserConfigMap.put(pdfParserConfigPath, pdfParserConfig);
            }
            parseContext.set(PDFParserConfig.class, pdfParserConfig);
        }

        parseContext.set(PasswordProvider.class, metadata -> metadata.get(FILE_PASSWORD));

        return parseContext;
    }

    /**
     * Returns an input stream from a deferred file output stream.
     *
     * @param dfos A deferred file output stream.
     * @return An input stream.
     * @throws IOException if an I/O error occurs.
     */
    protected InputStream getContentStream(final DeferredFileOutputStream dfos) throws IOException {
        if (dfos.isInMemory()) {
            return new ByteArrayInputStream(dfos.getData());
        }
        return new BufferedInputStream(new FileInputStream(dfos.getFile()));
    }

    /**
     * Returns a content from a writer.
     *
     * @param out A content writer.
     * @param encoding An encoding.
     * @param normalizeText If true, normalize a text.
     * @return a content.
     * @throws TikaException if a Tika exception occurs.
     */
    protected String getContent(final ContentWriter out, final String encoding, final boolean normalizeText) throws TikaException {
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
                if (normalizeText) {
                    return TextUtil.normalizeText(reader)
                            .initialCapacity(initialBufferSize)
                            .maxAlphanumTermSize(maxAlphanumTermSize)
                            .maxSymbolTermSize(maxSymbolTermSize)
                            .duplicateTermRemoved(replaceDuplication)
                            .spaceChars(spaceChars)
                            .execute();
                }
                return ReaderUtil.readText(reader);
            }
        } catch (final TikaException e) {
            throw e;
        } catch (final Exception e) {
            throw new ExtractException("Failed to read a content.", e);
        } finally {
            FileUtil.deleteInBackground(tempFile);
        }
    }

    /**
     * Creates a metadata.
     *
     * @param resourceName A resource name.
     * @param contentType A content type.
     * @param contentEncoding A content encoding.
     * @param pdfPassword A password for a PDF.
     * @return a metadata.
     */
    protected Metadata createMetadata(final String resourceName, final String contentType, final String contentEncoding,
            final String pdfPassword) {
        final Metadata metadata = new Metadata();
        if (StringUtil.isNotEmpty(resourceName)) {
            metadata.set(ExtractData.RESOURCE_NAME_KEY, resourceName);
        }
        if (StringUtil.isNotBlank(contentType)) {
            metadata.set(ExtractData.CONTENT_TYPE, contentType);
        }
        if (StringUtil.isNotBlank(contentEncoding)) {
            metadata.set(ExtractData.CONTENT_ENCODING, contentEncoding);
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
    /**
     * This class is a parser that detects the document type.
     */
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

        /**
         * Constructor.
         * @param config Tika config.
         */
        public TikaDetectParser(final TikaConfig config) {
            super(config.getMediaTypeRegistry(), config.getParser());
            detector = config.getDetector();
        }

        @Override
        public void parse(final InputStream stream, final ContentHandler handler, final Metadata metadata, final ParseContext context)
                throws IOException, SAXException, TikaException {
            final TemporaryResources tmp = new TemporaryResources();
            try {
                final TikaInputStream tis = TikaInputStream.get(stream, tmp, metadata);

                // Automatically detect the MIME type of the document
                final MediaType type = detector.detect(tis, metadata);
                metadata.set(ExtractData.CONTENT_TYPE, type.toString());

                // TIKA-216: Zip bomb prevention
                final SecureContentHandler sch = new SecureContentHandler(handler, tis);

                sch.setMaximumCompressionRatio(maxCompressionRatio);
                sch.setOutputThreshold(maxUncompressionSize);

                //pass self to handle embedded documents if
                //the caller hasn't specified one.
                if (context.get(EmbeddedDocumentExtractor.class) == null) {
                    final Parser p = context.get(Parser.class);
                    if (p == null) {
                        context.set(Parser.class, this);
                    }
                    context.set(EmbeddedDocumentExtractor.class, new ParsingEmbeddedDocumentExtractor(context));
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
    }

    /**
     * This interface is for writing a content.
     */
    @FunctionalInterface
    protected interface ContentWriter {
        /**
         * Accepts a writer.
         * @param writer A writer.
         * @throws IOException if an I/O error occurs.
         * @throws TikaException if a Tika exception occurs.
         * @throws SAXException if a SAX exception occurs.
         */
        void accept(Writer writer) throws IOException, TikaException, SAXException;
    }

    /**
     * Sets the output encoding.
     * @param outputEncoding The output encoding.
     */
    public void setOutputEncoding(final String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    /**
     * Sets whether to read content as text if extraction fails.
     * @param readAsTextIfFailed If true, read a content as a text when an extraction fails.
     */
    public void setReadAsTextIfFailed(final boolean readAsTextIfFailed) {
        this.readAsTextIfFailed = readAsTextIfFailed;
    }

    /**
     * Sets the maximum compression ratio.
     * @param maxCompressionRatio The max compression ratio.
     */
    public void setMaxCompressionRatio(final long maxCompressionRatio) {
        this.maxCompressionRatio = maxCompressionRatio;
    }

    /**
     * Sets the maximum uncompression size.
     * @param maxUncompressionSize The max uncompression size.
     */
    public void setMaxUncompressionSize(final long maxUncompressionSize) {
        this.maxUncompressionSize = maxUncompressionSize;
    }

    /**
     * Sets the initial buffer size.
     * @param initialBufferSize The initial buffer size.
     */
    public void setInitialBufferSize(final int initialBufferSize) {
        this.initialBufferSize = initialBufferSize;
    }

    /**
     * Sets whether duplicated terms are replaced.
     * @param replaceDuplication If true, duplicated terms are replaced.
     */
    public void setReplaceDuplication(final boolean replaceDuplication) {
        this.replaceDuplication = replaceDuplication;
    }

    /**
     * Sets the memory size.
     * @param memorySize The memory size.
     */
    public void setMemorySize(final int memorySize) {
        this.memorySize = memorySize;
    }

    /**
     * Sets the maximum size of an alpha-numeric term.
     * @param maxAlphanumTermSize The max size of an alpha-numeric term.
     */
    public void setMaxAlphanumTermSize(final int maxAlphanumTermSize) {
        this.maxAlphanumTermSize = maxAlphanumTermSize;
    }

    /**
     * Sets the maximum size of a symbol term.
     * @param maxSymbolTermSize The max size of a symbol term.
     */
    public void setMaxSymbolTermSize(final int maxSymbolTermSize) {
        this.maxSymbolTermSize = maxSymbolTermSize;
    }

    /**
     * Sets the space characters.
     * @param spaceChars The space characters.
     */
    public void setSpaceChars(final int[] spaceChars) {
        this.spaceChars = spaceChars;
    }

    /**
     * Sets the Tika configuration.
     * @param tikaConfig The Tika config.
     */
    public void setTikaConfig(final TikaConfig tikaConfig) {
        this.tikaConfig = tikaConfig;
    }
}
