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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.output.DeferredFileOutputStream;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.config.OutputLimits;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.DefaultEncodingDetector;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.detect.EncodingResult;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.ReservedNamespaces;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.filter.LegacyKeyMigrationFilter;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.AutoDetectParserConfig;
import org.apache.tika.parser.DefaultParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.PasswordProvider;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.codelibs.core.beans.BeanDesc;
import org.codelibs.core.beans.factory.BeanDescFactory;
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
 *   <li>Tesseract OCR configuration for image-based documents</li>
 *   <li>PDF Parser configuration for PDF documents</li>
 * </ul>
 *
 * <p>
 * The document type is auto-detected by Tika's {@link AutoDetectParser}, which also handles zip bomb prevention
 * and embedded document extraction.
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

    /** The prefix of the metadata Tika 4 adds about how it opened a zip (e.g. zip:integrity-check-result), which is not for the index. */
    private static final String ZIP_METADATA_PREFIX = "zip:";

    /**
     * The metadata the image parsers add about the temp file the source is spooled to (e.g. File Name=tikaExtractor-123.out),
     * which is not about the source.
     */
    private static final Set<String> TEMP_FILE_METADATA = Set.of("File Name", "File Size", "File Modified Date");

    /** The Tesseract timeout property of Tika 3, in seconds. */
    private static final String LEGACY_TIMEOUT_SECONDS = "timeoutSeconds";

    /** The Tesseract properties that Tika 4 reads only from the config of the parser, not from the one of a parse. */
    private static final String[] TESSERACT_PARSER_LEVEL_PATHS = { "tesseractPath", "tessdataPath", "imageMagickPath" };

    /** The Tesseract timeout property of Tika 4, in milliseconds. */
    private static final String TIMEOUT_MILLIS = "timeoutMillis";

    /** Precompiled pattern for stripping {@code <script>...</script>} blocks in {@link #stripHtmlTags(String)}. */
    private static final Pattern SCRIPT_TAG_PATTERN = Pattern.compile("<script[^>]*>.*?</script>");

    /** Precompiled pattern for stripping {@code <style>...</style>} blocks in {@link #stripHtmlTags(String)}. */
    private static final Pattern STYLE_TAG_PATTERN = Pattern.compile("<style[^>]*>.*?</style>");

    /** Precompiled pattern for stripping remaining HTML tags in {@link #stripHtmlTags(String)}. */
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");

    /** Precompiled pattern for collapsing whitespace runs in {@link #stripHtmlTags(String)}. */
    private static final Pattern HTML_WHITESPACE_PATTERN = Pattern.compile("\\s+");

    /** Tesseract OCR config that skips OCR, used when a blank content is parsed again. */
    private static final TesseractOCRConfig SKIP_OCR_CONFIG = new TesseractOCRConfig();

    static {
        SKIP_OCR_CONFIG.setSkipOcr(true);
    }

    /** PDF parser config used when the {@link #TIKA_PDF_CONFIG} parameter is not given. */
    private static final PDFParserConfig DEFAULT_PDF_PARSER_CONFIG = createPdfParserConfig();

    /**
     * Output encoding used when materializing extracted content into a byte
     * stream (e.g. read-as-text fallbacks). This setter intentionally does
     * <em>not</em> influence the JVM-stream capture/replay path used by
     * {@link #muteSystemStreams()} / {@link #replayCapturedBytes(BoundedByteArrayOutputStream, boolean)};
     * see those methods for the rationale.
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
     * Tika parser that detects the type of a document and delegates to the matching parser. Built by {@link #init()}.
     */
    protected Parser parser;

    /**
     * Default Tesseract OCR config, used when the {@link #TIKA_TESSERACT_CONFIG} parameter is not given.
     * If not set, the tesseractOCRConfig component is used when the container has one.
     * If null, Tika's default config is used.
     */
    protected TesseractOCRConfig tesseractOCRConfig;

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

    /** Rewrites the Tika 4 metadata keys back to the Tika 3 ones, which the Fess configuration and the data stores refer to. */
    private final LegacyKeyMigrationFilter legacyKeyMigrationFilter = new LegacyKeyMigrationFilter();

    /**
     * Holds the parser shared by all the extractors, because loading the Tika parsers is slow and memory hungry.
     */
    private static final class DefaultParserHolder {
        static final Parser PARSER = createParser();

        private static Parser createParser() {
            final AutoDetectParserConfig config = new AutoDetectParserConfig();
            // an empty document is not an error, it just has no content
            config.setThrowOnZeroBytes(false);
            return AutoDetectParser.build(new DefaultParser(MediaTypeRegistry.getDefaultRegistry()), new DefaultDetector(), config);
        }
    }

    /**
     * Holds the encoding detector shared by all the extractors.
     */
    private static final class EncodingDetectorHolder {
        static final EncodingDetector DETECTOR = new AlignmentBreakingEncodingDetector(new DefaultEncodingDetector());
    }

    /**
     * An encoding detector that keeps Mojibuster, the statistical detector of the default chain of Tika 4, from taking
     * a text in Shift_JIS for UTF-16.
     *
     * <p>Mojibuster looks at the two columns of the even and the odd positions of the bytes. UTF-16 has a column that
     * is poor in distinct values (the high bytes), and so does Shift_JIS, in the lead bytes (about 60 values against
     * about 190 of the trail bytes). Every ASCII run of the text moves the next characters by its length, so the two
     * columns are mixed unless all the runs have an even length (for example, all the line ends are CRLF). When they
     * are not mixed, Mojibuster returns UTF-16 as the only candidate, which nothing in the chain can check.</p>
     *
     * <p>A UTF-16 text that has an ASCII character has a NUL byte, so a text that has no NUL byte and has a byte of
     * 0x80 or more is not UTF-16. When the columns of such a text are lopsided like the ones of UTF-16, this detector
     * inserts a space in the probe, after an ASCII byte so that no character is split, and then the columns are mixed.
     * The inserted space is only seen by the detectors. The bytes the parser reads are not changed. A text whose
     * columns are not lopsided is not changed, because the space could line up the characters that were not.</p>
     */
    private static final class AlignmentBreakingEncodingDetector implements EncodingDetector {

        private static final long serialVersionUID = 1L;

        /** The size of the probe, the same as the read limit of the junk filter of Tika. */
        private static final int PROBE_SIZE = 16384;

        /** The probe shorter than this is not judged by the columns. */
        private static final int MIN_PROBE_SIZE = 16;

        /** Mojibuster counts the columns in the first 4096 bytes, so the space is inserted before this offset. */
        private static final int MAX_INSERT_OFFSET = 3000;

        /** The number of bytes in which the columns are counted, the same as Mojibuster. */
        private static final int COLUMN_SAMPLE_SIZE = 4096;

        /**
         * The ratio of the distinct values of the two columns that is taken for lopsided. Mojibuster takes 3 and over for
         * UTF-16, and this is lower to leave a margin.
         */
        private static final int LOPSIDED_RATIO = 2;

        private final EncodingDetector delegate;

        AlignmentBreakingEncodingDetector(final EncodingDetector delegate) {
            this.delegate = delegate;
        }

        @Override
        public List<EncodingResult> detect(final TikaInputStream tis, final Metadata metadata, final ParseContext parseContext)
                throws IOException {
            tis.mark(PROBE_SIZE);
            final byte[] probe;
            try {
                probe = tis.readNBytes(PROBE_SIZE);
            } finally {
                tis.reset();
            }
            final int offset = findInsertOffset(probe);
            if (offset < 0) {
                return delegate.detect(tis, metadata, parseContext);
            }
            final byte[] broken = new byte[probe.length + 1];
            System.arraycopy(probe, 0, broken, 0, offset);
            broken[offset] = ' ';
            System.arraycopy(probe, offset, broken, offset + 1, probe.length - offset);
            try (TikaInputStream brokenStream = TikaInputStream.get(broken)) {
                return delegate.detect(brokenStream, metadata, parseContext);
            }
        }

        /**
         * Returns the offset to insert a space at, or -1 when the probe can be UTF-16 or has no safe place.
         */
        private static int findInsertOffset(final byte[] probe) {
            if (probe.length < MIN_PROBE_SIZE || isUtf16Bom(probe)) {
                return -1;
            }
            boolean high = false;
            for (final byte b : probe) {
                if (b == 0) {
                    return -1;
                }
                high |= b < 0;
            }
            if (!high || !isLopsided(probe)) {
                return -1;
            }
            for (int i = Math.min(probe.length / 2, MAX_INSERT_OFFSET / 2); i < Math.min(probe.length, MAX_INSERT_OFFSET); i++) {
                if (probe[i] >= 0) {
                    return i + 1;
                }
            }
            return -1;
        }

        private static boolean isLopsided(final byte[] probe) {
            final boolean[][] seen = new boolean[2][256];
            final int[] distinct = new int[2];
            for (int i = 0; i < Math.min(probe.length, COLUMN_SAMPLE_SIZE); i++) {
                final int column = i & 1;
                final int value = probe[i] & 0xFF;
                if (!seen[column][value]) {
                    seen[column][value] = true;
                    distinct[column]++;
                }
            }
            return Math.min(distinct[0], distinct[1]) > 0
                    && Math.max(distinct[0], distinct[1]) >= LOPSIDED_RATIO * Math.min(distinct[0], distinct[1]);
        }

        private static boolean isUtf16Bom(final byte[] probe) {
            return probe[0] == (byte) 0xFE && probe[1] == (byte) 0xFF || probe[0] == (byte) 0xFF && probe[1] == (byte) 0xFE;
        }
    }

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
        if (parser == null) {
            parser = DefaultParserHolder.PARSER;
        }

        if (tesseractOCRConfig == null && crawlerContainer != null) {
            try {
                tesseractOCRConfig = crawlerContainer.getComponent("tesseractOCRConfig");
            } catch (final Exception e) {
                logger.debug("tesseractOCRConfig component is not found.", e);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("supportedTypes: {}", parser.getSupportedTypes(new ParseContext()));
        }
    }

    /**
     * Returns the Tika parser built by {@link #init()}.
     *
     * @return the Tika parser, or null before {@link #init()} is called.
     */
    public Parser getParser() {
        return parser;
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
        final byte[] bytes;
        final boolean isByteStream = inputStream instanceof ByteArrayInputStream;
        if (isByteStream) {
            inputStream.mark(0); // ByteArrayInputStream
            tempFile = null;
            try {
                // An in-memory source is parsed from a byte[] stream, which knows its full length, so that the zip bomb
                // ratio is computed against the whole source and not against the bytes read so far
                bytes = inputStream.readAllBytes();
                inputStream.reset();
            } catch (final IOException e) {
                throw new ExtractException("Failed to extract via Tika: reason=unexpectedError", e);
            }
        } else {
            bytes = null;
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
                // bytes a second time.
                if (!isByteStream) {
                    try (OutputStream out = new FileOutputStream(tempFile)) {
                        CopyUtil.copy(inputStream, out);
                    }
                }

                final Metadata metadata = createMetadata(resourceName, contentType, contentEncoding, password);

                final ParseContext parseContext = createParseContext(parser, params);

                String content = getContent(writer -> {
                    try (TikaInputStream in = openTikaInput(bytes, tempFile)) {
                        parser.parse(in, new BodyContentHandler(writer), metadata, parseContext);
                    }
                }, contentEncoding, normalizeText);
                if (StringUtil.isBlank(content)) {
                    // OCR is slow and has already run in the first parse, so the retries skip it.
                    final ParseContext retryParseContext = createParseContext(parser, params);
                    retryParseContext.set(TesseractOCRConfig.class, SKIP_OCR_CONFIG);
                    if (resourceName != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("retry without a resource name: resourceName={}", resourceName);
                        }
                        final Metadata metadata2 = createMetadata(null, contentType, contentEncoding, password);
                        content = getContent(writer -> {
                            try (TikaInputStream in = openTikaInput(bytes, tempFile)) {
                                parser.parse(in, new BodyContentHandler(writer), metadata2, retryParseContext);
                            }
                        }, contentEncoding, normalizeText);
                    }
                    if (StringUtil.isBlank(content) && contentType != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("retry without a content type: contentType={}", contentType);
                        }
                        final Metadata metadata3 = createMetadata(null, null, contentEncoding, password);
                        content = getContent(writer -> {
                            try (TikaInputStream in = openTikaInput(bytes, tempFile)) {
                                parser.parse(in, new BodyContentHandler(writer), metadata3, retryParseContext);
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

                // The tk: keys (and the X-TIKA: ones they are migrated to) are Tika's internal bookkeeping, and so are the
                // zip: ones (e.g. zip:detector-zip-file-opened=true on every OOXML file), which would end up in the content
                legacyKeyMigrationFilter.filter(new ArrayList<>(List.of(metadata)));
                final String[] names = metadata.names();
                Arrays.sort(names);
                for (final String name : names) {
                    if (!ReservedNamespaces.isTikaNative(name) && !name.startsWith(ZIP_METADATA_PREFIX)
                            && !TEMP_FILE_METADATA.contains(name)) {
                        extractData.putValues(name, metadata.getValues(name));
                    }
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
     * Opens a {@link TikaInputStream} for parsing the already-materialized source. The
     * stream is created without a {@link Metadata}, so that Tika neither records the
     * temp file name as the resource name nor adds a length to the metadata of the parse.
     *
     * @param bytes    the in-memory source, or {@code null} when the source is {@code tempFile}
     * @param tempFile the materialized on-disk copy (used when {@code bytes} is {@code null})
     * @return a fresh, ready-to-read stream, which the caller must close
     * @throws IOException if the on-disk file cannot be opened
     */
    protected TikaInputStream openTikaInput(final byte[] bytes, final File tempFile) throws IOException {
        return bytes != null ? TikaInputStream.get(bytes) : TikaInputStream.get(tempFile.toPath());
    }

    /**
     * Opens an {@link InputStream} backed by the already-materialized source (the byte
     * stream marked at the start, or the on-disk {@code tempFile}).
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
     *
     * <p>The replacement {@link PrintStream}s are constructed with the auto-flush
     * two-arg form, which the JVM wraps using {@link Charset#defaultCharset()}.
     * The configured {@link #outputEncoding} is intentionally <em>not</em> applied
     * here: third-party libraries (Tika, PDFBox, POI, JBIG2-ImageIO) write
     * diagnostics through {@link System#out}/{@link System#err} using the JVM
     * default charset, and forcing a different encoder would risk character
     * substitution for code points not representable in that encoding. See
     * {@link #replayCapturedBytes(BoundedByteArrayOutputStream, boolean)} for the
     * matching decode side.
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
     * <p>The decode charset is deliberately {@link Charset#defaultCharset()} and
     * <em>not</em> the configurable {@link #outputEncoding}. {@link #muteSystemStreams()}
     * installs {@code PrintStream}s that wrap the JVM default charset, so this
     * choice guarantees a lossless round-trip of whatever Tika/PDFBox/POI wrote
     * to {@link System#out}/{@link System#err}.
     *
     * @param buffer captured byte buffer; ignored when empty
     * @param fromStderr whether the buffer came from {@link System#err}
     */
    protected void replayCapturedBytes(final BoundedByteArrayOutputStream buffer, final boolean fromStderr) {
        if (buffer == null || buffer.size() == 0) {
            return;
        }
        // Must match the encoder used by muteSystemStreams() — PrintStream(out, true)
        // wraps Charset.defaultCharset(). outputEncoding is intentionally not used
        // here; see the Javadoc above.
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
        parseContext.set(EncodingDetector.class, EncodingDetectorHolder.DETECTOR);

        final String tesseractConfigPath = params != null ? params.get(TIKA_TESSERACT_CONFIG) : null;
        if (StringUtil.isNotBlank(tesseractConfigPath)) {
            TesseractOCRConfig tesseractOCRConfig = tesseractOCRConfigMap.get(tesseractConfigPath);
            if (tesseractOCRConfig == null) {
                tesseractOCRConfig = new TesseractOCRConfig();
                copyConfig(tesseractConfigPath, tesseractOCRConfig);
                tesseractOCRConfigMap.put(tesseractConfigPath, tesseractOCRConfig);
            }
            parseContext.set(TesseractOCRConfig.class, tesseractOCRConfig);
        } else if (this.tesseractOCRConfig != null) {
            parseContext.set(TesseractOCRConfig.class, this.tesseractOCRConfig);
        }

        final String pdfParserConfigPath = params != null ? params.get(TIKA_PDF_CONFIG) : null;
        if (StringUtil.isNotBlank(pdfParserConfigPath)) {
            PDFParserConfig pdfParserConfig = pdfParserConfigMap.get(pdfParserConfigPath);
            if (pdfParserConfig == null) {
                pdfParserConfig = createPdfParserConfig();
                copyConfig(pdfParserConfigPath, pdfParserConfig);
                pdfParserConfigMap.put(pdfParserConfigPath, pdfParserConfig);
            }
            parseContext.set(PDFParserConfig.class, pdfParserConfig);
        } else {
            parseContext.set(PDFParserConfig.class, DEFAULT_PDF_PARSER_CONFIG);
        }

        final OutputLimits outputLimits = new OutputLimits();
        outputLimits.setZipBombRatio(maxCompressionRatio);
        outputLimits.setZipBombThreshold(maxUncompressionSize);
        parseContext.set(OutputLimits.class, outputLimits);

        parseContext.set(PasswordProvider.class, metadata -> metadata.get(FILE_PASSWORD));

        return parseContext;
    }

    /**
     * Creates a PDF parser config with the Fess defaults. Tika 4 turned on {@code extractIncrementalUpdateInfo},
     * which scans the xref table and adds metadata of no use for a search index, so it is turned off again.
     *
     * @return a PDF parser config.
     */
    private static PDFParserConfig createPdfParserConfig() {
        final PDFParserConfig config = new PDFParserConfig();
        config.setExtractIncrementalUpdateInfo(false);
        return config;
    }

    /**
     * Copies the properties in a file into a Tika config bean. Tika 4 renamed or removed some of the
     * properties, and a key that is not a settable property is not an error for the bean copy,
     * so it is reported here instead of being dropped silently.
     *
     * @param path A properties file path.
     * @param config A Tika config bean.
     */
    protected void copyConfig(final String path, final Object config) {
        final Properties props = new Properties();
        PropertiesUtil.load(props, path);
        final Map<String, String> propMap =
                props.entrySet().stream().collect(Collectors.toMap(e -> (String) e.getKey(), e -> (String) e.getValue()));
        if (config instanceof TesseractOCRConfig && propMap.containsKey(LEGACY_TIMEOUT_SECONDS)) {
            final String seconds = propMap.remove(LEGACY_TIMEOUT_SECONDS);
            try {
                propMap.putIfAbsent(TIMEOUT_MILLIS, Long.toString(Long.parseLong(seconds.trim()) * 1000));
                logger.warn("{} is renamed to {}, which is in milliseconds. Converted: path={}, {}={}", LEGACY_TIMEOUT_SECONDS,
                        TIMEOUT_MILLIS, path, LEGACY_TIMEOUT_SECONDS, seconds);
            } catch (final NumberFormatException e) {
                logger.warn("Invalid {} is ignored: path={}, value={}", LEGACY_TIMEOUT_SECONDS, path, seconds);
            }
        }
        if (config instanceof TesseractOCRConfig) {
            for (final String key : TESSERACT_PARSER_LEVEL_PATHS) {
                if (propMap.containsKey(key)) {
                    logger.warn("{} is read only when the Tesseract parser is initialized in Tika 4, so it has no effect here: path={}",
                            key, path);
                }
            }
        }
        final BeanDesc beanDesc = BeanDescFactory.getBeanDesc(config.getClass());
        propMap.keySet().removeIf(key -> {
            if (beanDesc.hasPropertyDesc(key) && beanDesc.getPropertyDesc(key).isWritable()) {
                return false;
            }
            logger.warn("{} is not a settable property of {} in this version of Tika, so it is ignored: path={}", key,
                    config.getClass().getSimpleName(), path);
            return true;
        });
        BeanUtil.copyMapToBean(propMap, config);
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
            // The Property is required: Tika 4 ignores a bare "resourceName" key for the name-based hints
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, resourceName);
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
     * Sets the output encoding used for materializing extracted content.
     *
     * <p>Note: this setting does not affect how bytes captured from muted
     * {@link System#out}/{@link System#err} are decoded for replay through the
     * logger. Those bytes are written by Tika/PDFBox/POI through
     * {@link PrintStream}s the JVM wraps with {@link Charset#defaultCharset()},
     * so the capture/replay path always uses the JVM default charset to avoid
     * lossy substitution of non-ASCII diagnostic text. See
     * {@link #replayCapturedBytes(BoundedByteArrayOutputStream, boolean)}.
     *
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
     * Sets the default Tesseract OCR config.
     * It is used when the {@link #TIKA_TESSERACT_CONFIG} parameter is not given.
     * @param tesseractOCRConfig The Tesseract OCR config.
     */
    public void setTesseractOCRConfig(final TesseractOCRConfig tesseractOCRConfig) {
        this.tesseractOCRConfig = tesseractOCRConfig;
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
            result = SCRIPT_TAG_PATTERN.matcher(result).replaceAll(" ");
            result = STYLE_TAG_PATTERN.matcher(result).replaceAll(" ");
            result = HTML_TAG_PATTERN.matcher(result).replaceAll(" ");
            // Decode common HTML entities
            result = result.replace("&nbsp;", " ");
            result = result.replace("&amp;", "&");
            result = result.replace("&lt;", "<");
            result = result.replace("&gt;", ">");
            result = result.replace("&quot;", "\"");
            result = result.replace("&#39;", "'");
            // Normalize whitespace
            result = HTML_WHITESPACE_PATTERN.matcher(result).replaceAll(" ").trim();
            return result;
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to strip HTML tags, returning original content", e);
            }
            return content;
        }
    }
}
