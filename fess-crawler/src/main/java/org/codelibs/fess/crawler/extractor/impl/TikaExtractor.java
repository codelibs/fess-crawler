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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    /**
     * A parameter key to strip HTML tags from content when detected as HTML.
     */
    public static final String STRIP_HTML_TAGS = "tika.stripHtmlTags";

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

    /**
     * If true, System.out/System.err are muted during Tika parsing to suppress
     * stray output produced by some bundled parsers. Defaults to {@code true}
     * to preserve existing behavior. Disable when debugging or when callers
     * want to keep the original streams intact.
     */
    protected boolean muteSystemStreams = true;

    /**
     * Class-wide lock guarding {@link #muteRefCount}, {@link #savedOut},
     * {@link #savedErr} and the capture buffers. We never hold this lock during
     * extraction itself, so concurrent extractions are not serialized; we only
     * synchronize the (cheap) swap and restore of the JVM streams plus the
     * post-run replay of captured bytes.
     */
    private static final Object SYSTEM_STREAM_LOCK = new Object();

    /**
     * Hard cap on the number of bytes captured from each muted stream
     * (System.out and System.err) while extractions are running. Once the cap
     * is reached, additional writes are discarded and a single truncation
     * marker is appended to the buffer so the operator can tell the output was
     * cut short.
     */
    private static final int CAPTURE_BUFFER_SIZE = 64 * 1024;

    /** Truncation marker appended once when a capture buffer fills up. */
    private static final byte[] CAPTURE_TRUNCATION_MARKER = "\n[truncated; further output discarded]\n".getBytes(StandardCharsets.UTF_8);

    /** Number of extractions currently running with muted streams. */
    private static int muteRefCount = 0;

    /** Original {@link System#out}, captured by the first muting thread. */
    private static PrintStream savedOut;

    /** Original {@link System#err}, captured by the first muting thread. */
    private static PrintStream savedErr;

    /** Buffer capturing bytes written to the muted {@link System#out}. */
    private static BoundedByteArrayOutputStream capturedOut;

    /** Buffer capturing bytes written to the muted {@link System#err}. */
    private static BoundedByteArrayOutputStream capturedErr;

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

        final boolean muted = muteSystemStreams;
        if (muted) {
            muteSystemStreams();
        }
        try {
            try {
                final String resourceName = params == null ? null : params.get(ExtractData.RESOURCE_NAME_KEY);
                final String contentType = params == null ? null : params.get(ExtractData.CONTENT_TYPE);
                String contentEncoding = params == null ? null : params.get(ExtractData.CONTENT_ENCODING);
                final boolean normalizeText = params == null ? true : !Constants.FALSE.equalsIgnoreCase(params.get(NORMALIZE_TEXT));
                final boolean stripHtmlTags = params != null && "true".equalsIgnoreCase(params.get(STRIP_HTML_TAGS));
                final String password = getPassword(params);

                // Materialize the input stream once into the existing tempFile (or keep the
                // byte buffer in memory). Subsequent retries reuse the same on-disk file
                // through TikaInputStream.get(Path), which prevents Tika from spooling the
                // bytes a second time inside TikaDetectParser.
                if (!isByteStream) {
                    try (OutputStream out = new FileOutputStream(tempFile)) {
                        CopyUtil.copy(inputStream, out);
                    }
                }

                final Metadata metadata = createMetadata(resourceName, contentType, contentEncoding, password);

                final Parser parser = new TikaDetectParser();
                final ParseContext parseContext = createParseContext(parser, params);

                String content = getContent(writer -> {
                    try (InputStream in = openMaterializedInput(inputStream, tempFile, isByteStream)) {
                        parser.parse(in, new BodyContentHandler(writer), metadata, parseContext);
                    }
                }, contentEncoding, normalizeText);
                if (StringUtil.isBlank(content)) {
                    if (resourceName != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("retry without a resource name: resourceName={}", resourceName);
                        }
                        final Metadata metadata2 = createMetadata(null, contentType, contentEncoding, password);
                        content = getContent(writer -> {
                            try (InputStream in = openMaterializedInput(inputStream, tempFile, isByteStream)) {
                                parser.parse(in, new BodyContentHandler(writer), metadata2, parseContext);
                            }
                        }, contentEncoding, normalizeText);
                    }
                    if (StringUtil.isBlank(content) && contentType != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("retry without a content type: contentType={}", contentType);
                        }
                        final Metadata metadata3 = createMetadata(null, null, contentEncoding, password);
                        content = getContent(writer -> {
                            try (InputStream in = openMaterializedInput(inputStream, tempFile, isByteStream)) {
                                parser.parse(in, new BodyContentHandler(writer), metadata3, parseContext);
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
                                logger.warn("Could not read source: source={}",
                                        tempFile != null ? tempFile.getAbsolutePath() : "byteStream", e);
                            } finally {
                                CloseableUtil.closeQuietly(br);
                            }
                        }, contentEncoding, normalizeText);
                    }
                }
                // Strip HTML tags if explicitly requested via params
                // Note: Content type check is intentionally omitted because Tika may detect
                // HTML fragments (without proper <html><body> structure) as text/plain
                if (stripHtmlTags && StringUtil.isNotBlank(content)) {
                    content = stripHtmlTags(content);
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
                    try (InputStream in = openMaterializedInput(inputStream, tempFile, isByteStream)) {
                        postFilter.accept(extractData, in);
                    }
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Result: metadata: {}", metadata);
                }

                return extractData;
            } catch (final TikaException e) {
                if (e.getMessage() != null && e.getMessage().indexOf("bomb") >= 0) {
                    throw new ExtractException("Failed to extract via Tika: reason=zipBombDetected", e);
                }
                final Throwable cause = e.getCause();
                if (cause instanceof SAXException) {
                    final Extractor xmlExtractor = crawlerContainer.getComponent("xmlExtractor");
                    if (xmlExtractor != null) {
                        try (InputStream in = openMaterializedInput(inputStream, tempFile, isByteStream)) {
                            return xmlExtractor.getText(in, params);
                        }
                    }
                }
                throw e;
            }
        } catch (final ExtractException e) {
            throw e;
        } catch (final Exception e) {
            throw new ExtractException("Failed to extract via Tika: reason=unexpectedError", e);
        } finally {
            try {
                FileUtil.deleteInBackground(tempFile);
            } finally {
                if (muted) {
                    unmuteSystemStreams();
                }
            }
        }
    }

    /**
     * Opens an {@link InputStream} backed by the already-materialized source (the byte
     * stream marked at the start, or the on-disk {@code tempFile}). When a temp file is
     * present we wrap it as a {@link TikaInputStream} so that
     * {@code TikaInputStream.get(stream, ...)} inside {@link TikaDetectParser#parse}
     * can reuse the existing file path instead of spooling the bytes a second time.
     *
     * @param inputStream the original byte stream (used when {@code isByteStream})
     * @param tempFile    the materialized on-disk copy (used otherwise); may be {@code null}
     * @param isByteStream {@code true} iff the caller passed a {@link ByteArrayInputStream}
     * @return a fresh, ready-to-read input stream
     * @throws IOException if the on-disk file cannot be opened
     */
    protected InputStream openMaterializedInput(final InputStream inputStream, final File tempFile, final boolean isByteStream)
            throws IOException {
        if (isByteStream) {
            inputStream.reset();
            return inputStream;
        }
        return TikaInputStream.get(tempFile.toPath());
    }

    /**
     * Mutes {@link System#out} and {@link System#err} for the duration of the current
     * extraction. Concurrent extractions share a single muted state via a reference
     * count — the first caller saves the original streams and swaps in bounded
     * capture buffers; subsequent callers just bump the count. Captured bytes are
     * not silently discarded: when the last outstanding mute is released the
     * captured contents are emitted via the configured logger so operators still
     * see Tika parser warnings (PDFBox font warnings, JBIG2 warnings, legacy POI
     * debug, etc.). The lock is released as soon as the swap is recorded so that
     * extractions never serialize on it.
     */
    protected void muteSystemStreams() {
        synchronized (SYSTEM_STREAM_LOCK) {
            if (muteRefCount == 0) {
                savedOut = System.out;
                savedErr = System.err;
                capturedOut = new BoundedByteArrayOutputStream(CAPTURE_BUFFER_SIZE);
                capturedErr = new BoundedByteArrayOutputStream(CAPTURE_BUFFER_SIZE);
                System.setOut(new PrintStream(capturedOut, true));
                System.setErr(new PrintStream(capturedErr, true));
            }
            muteRefCount++;
        }
    }

    /**
     * Releases one reference acquired by {@link #muteSystemStreams()}. When the last
     * outstanding mute is released, the original streams are restored and any
     * bytes captured while the streams were muted are replayed via the logger
     * (info for stdout, warn for stderr) so they are not silently lost. Calling
     * this without a matching {@link #muteSystemStreams()} is a programming
     * error and is tolerated only defensively (the count is clamped at zero).
     */
    protected void unmuteSystemStreams() {
        BoundedByteArrayOutputStream outToReplay = null;
        BoundedByteArrayOutputStream errToReplay = null;
        synchronized (SYSTEM_STREAM_LOCK) {
            if (muteRefCount <= 0) {
                logger.warn("unmuteSystemStreams called without a matching mute; muteRefCount={}", muteRefCount);
                return;
            }
            muteRefCount--;
            if (muteRefCount == 0) {
                if (savedOut != null) {
                    try {
                        System.setOut(savedOut);
                    } catch (final Exception e) {
                        logger.warn("Failed to restore System.out.", e);
                    }
                }
                if (savedErr != null) {
                    try {
                        System.setErr(savedErr);
                    } catch (final Exception e) {
                        logger.warn("Failed to restore System.err.", e);
                    }
                }
                savedOut = null;
                savedErr = null;
                outToReplay = capturedOut;
                errToReplay = capturedErr;
                capturedOut = null;
                capturedErr = null;
            }
        }
        // Emit captured output outside the lock so logging back-pressure
        // never blocks future muteSystemStreams() callers.
        if (outToReplay != null) {
            replayCapturedBytes(outToReplay, false);
        }
        if (errToReplay != null) {
            replayCapturedBytes(errToReplay, true);
        }
    }

    /**
     * Decodes the bytes captured from a muted JVM stream and re-emits them via
     * the logger. Stdout-origin bytes are logged at INFO; stderr-origin bytes at
     * WARN, matching the severity of the original Tika diagnostic channels.
     * Visible for testing so that subclasses can intercept the replayed text.
     *
     * @param buffer captured byte buffer; ignored when empty
     * @param fromStderr whether the buffer came from {@link System#err}
     */
    protected void replayCapturedBytes(final BoundedByteArrayOutputStream buffer, final boolean fromStderr) {
        if (buffer == null || buffer.size() == 0) {
            return;
        }
        // Default JVM stream encoding is platform-dependent; PrintStream(true)
        // wraps Charset.defaultCharset(), so decode with the same charset for
        // round-trip fidelity.
        final Charset charset = Charset.defaultCharset();
        final String text = new String(buffer.toByteArray(), charset);
        if (text.isEmpty()) {
            return;
        }
        onReplayCaptured(text, fromStderr);
        if (fromStderr) {
            logger.warn("Captured System.err output during Tika extraction:\n{}", text);
        } else {
            logger.info("Captured System.out output during Tika extraction:\n{}", text);
        }
    }

    /**
     * Hook invoked just before the captured bytes from a muted JVM stream are
     * emitted via the logger. Default implementation is a no-op; tests and
     * subclasses can override to observe (or otherwise act on) the replayed
     * text.
     *
     * @param text       captured text decoded with the JVM default charset
     * @param fromStderr {@code true} if the bytes were captured from
     *                   {@link System#err}, {@code false} for {@link System#out}
     */
    protected void onReplayCaptured(final String text, final boolean fromStderr) {
        // hook for subclasses; intentionally empty
    }

    /**
     * A {@link ByteArrayOutputStream} with a hard upper bound. Once the bound is
     * reached a single truncation marker is appended and any further writes are
     * silently dropped. This prevents a runaway parser from consuming arbitrary
     * heap while still preserving an operator-visible record that output was
     * truncated.
     */
    static final class BoundedByteArrayOutputStream extends ByteArrayOutputStream {
        private final int capacity;
        private boolean truncated;

        BoundedByteArrayOutputStream(final int capacity) {
            super(Math.min(1024, capacity));
            this.capacity = capacity;
        }

        @Override
        public synchronized void write(final int b) {
            if (truncated) {
                return;
            }
            if (size() >= capacity) {
                appendTruncationMarker();
                return;
            }
            super.write(b);
        }

        @Override
        public synchronized void write(final byte[] b, final int off, final int len) {
            if (truncated) {
                return;
            }
            final int remaining = capacity - size();
            if (remaining <= 0) {
                appendTruncationMarker();
                return;
            }
            if (len <= remaining) {
                super.write(b, off, len);
                return;
            }
            super.write(b, off, remaining);
            appendTruncationMarker();
        }

        private void appendTruncationMarker() {
            if (truncated) {
                return;
            }
            truncated = true;
            // Direct super.write to bypass our cap (the marker itself is small).
            super.write(CAPTURE_TRUNCATION_MARKER, 0, CAPTURE_TRUNCATION_MARKER.length);
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

    /**
     * Sets whether to mute {@link System#out} and {@link System#err} during extraction.
     * Some Tika-bundled parsers print to the JVM streams; muting suppresses that noise.
     * Defaults to {@code true}. Disable when debugging or when callers depend on
     * application output remaining on the original streams.
     *
     * @param muteSystemStreams {@code true} to mute, {@code false} to leave streams intact
     */
    public void setMuteSystemStreams(final boolean muteSystemStreams) {
        this.muteSystemStreams = muteSystemStreams;
    }

    /**
     * Strips HTML tags from the given content using regex.
     *
     * @param content The content to strip HTML tags from.
     * @return The content with HTML tags removed, or the original content if stripping fails.
     */
    protected String stripHtmlTags(final String content) {
        if (StringUtil.isBlank(content)) {
            return content;
        }
        try {
            // Use regex to strip HTML tags
            // First, handle common HTML entities
            String result = content;
            result = result.replaceAll("<script[^>]*>.*?</script>", " ");
            result = result.replaceAll("<style[^>]*>.*?</style>", " ");
            result = result.replaceAll("<[^>]+>", " ");
            // Decode common HTML entities
            result = result.replace("&nbsp;", " ");
            result = result.replace("&amp;", "&");
            result = result.replace("&lt;", "<");
            result = result.replace("&gt;", ">");
            result = result.replace("&quot;", "\"");
            result = result.replace("&#39;", "'");
            // Normalize whitespace
            result = result.replaceAll("\\s+", " ").trim();
            return result;
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to strip HTML tags, returning original content", e);
            }
            return content;
        }
    }
}
