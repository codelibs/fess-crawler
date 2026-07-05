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

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathEvaluationResult;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathNodes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.stream.StreamUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.util.XPathAPI;
import org.codelibs.nekohtml.parsers.DOMParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Extracts text content from HTML documents.
 *
 * <p>In addition to body text, this extractor populates {@link ExtractData} with a
 * default set of HTML metadata (title, description, OpenGraph, Twitter Card,
 * canonical, keywords, author); this is enabled by default and can be disabled via
 * {@link #setExtractDefaultMetadata(boolean)}. It can also parse
 * {@code <script type="application/ld+json">} blocks into {@code jsonld.type} and
 * {@code jsonld.raw}; this is opt-in (disabled by default) and enabled via
 * {@link #setExtractJsonLd(boolean)}.</p>
 *
 * <p>Compiled {@link XPathExpression} instances are cached per thread to avoid
 * re-parsing every XPath on each extraction.</p>
 *
 * <p>Default field rules ({@link #defaultFieldRules}) are applied after custom
 * {@link #metadataXpathMap} rules. Keys already populated with a non-blank value
 * by a custom rule take precedence; keys with no value fall through to the default
 * rule. After all primary default rules are applied, fallback rules
 * ({@link #defaultFieldFallbackRules}) copy a value from another already-populated
 * key when the target key is still missing a non-blank value.</p>
 */
public class HtmlExtractor extends AbstractXmlExtractor {
    /** Logger for this class. */
    protected static final Logger logger = LogManager.getLogger(HtmlExtractor.class);

    /** Metadata key holding raw JSON-LD strings. */
    public static final String JSONLD_RAW_KEY = "jsonld.raw";

    /** Metadata key holding {@code @type} values from JSON-LD blocks. */
    public static final String JSONLD_TYPE_KEY = "jsonld.type";

    /**
     * XPath expression matching JSON-LD script blocks.
     *
     * <p>Per RFC 6838 / HTML5, MIME types are case-insensitive and may carry
     * surrounding whitespace, so {@code <script type="Application/LD+JSON">}
     * and {@code <script type=" application/ld+json ">} must also match.
     * NekoHTML uppercases element names but preserves attribute values
     * verbatim, hence the explicit {@code translate(normalize-space(...))}
     * normalisation here.</p>
     */
    protected static final String JSONLD_XPATH = "//SCRIPT[translate(normalize-space(@type),"
            + "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')='application/ld+json']";

    /**
     * Single shared {@link XPathFactory}. {@code XPathFactory} itself is not
     * documented as thread-safe, so calls to {@link XPathFactory#newXPath()}
     * are synchronised on the factory in {@link #newXPath()}. Each thread still
     * owns its own {@link XPath} instance via {@link #threadLocalXPath}, since
     * {@code XPath} is also not guaranteed thread-safe.
     */
    private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();

    /**
     * Maximum nesting depth for parsed JSON-LD objects. Pathological inputs
     * with deeper nesting are rejected to avoid stack/heap exhaustion DoS.
     */
    protected static final int JSONLD_MAX_NESTING_DEPTH = 64;

    /**
     * Maximum total string length (in characters) the JSON-LD parser will
     * accept for any single token / value.
     */
    protected static final int JSONLD_MAX_STRING_LENGTH = 10 * 1024 * 1024;

    /** Maximum number length (in characters) the JSON-LD parser will accept. */
    protected static final int JSONLD_MAX_NUMBER_LENGTH = 1000;

    /**
     * Maximum number of JSON-LD script blocks to process per document.
     * Blocks beyond this limit are silently truncated after a WARN log entry
     * to avoid unbounded memory growth on adversarial pages.
     */
    protected static final int JSONLD_MAX_BLOCK_COUNT = 64;

    /**
     * Maximum total raw JSON-LD size accepted across all script blocks in a
     * single document, measured in characters (UTF-16 code units, not bytes;
     * actual heap use may be roughly twice this value). Processing stops with a
     * WARN when adding the next block would exceed this limit.
     */
    protected static final int JSONLD_MAX_RAW_TOTAL_BYTES = 1 * 1024 * 1024; // 1 MiB (characters)

    /**
     * Maximum number of {@code @type} values collected per document across all
     * JSON-LD blocks. Despite the historical name, the collected-type list is
     * shared across every block in a document, so this bounds the per-document
     * total rather than a per-block quota; recursion into {@code @graph} and
     * nested entities stops once the limit is reached, preventing unbounded
     * list growth.
     */
    protected static final int JSONLD_MAX_TYPES_PER_BLOCK = 256;

    /** Pattern for extracting charset from meta tags. */
    protected Pattern metaCharsetPattern = Pattern.compile("<meta.*content\\s*=\\s*['\"].*;\\s*charset=([\\w\\d\\-_]*)['\"]\\s*/?>",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for HTML tags.
     */
    protected Pattern htmlTagPattern = Pattern.compile("<[^>]+>");

    /** Map of parser features. */
    protected Map<String, String> featureMap = new HashMap<>();

    /** Map of parser properties. */
    protected Map<String, String> propertyMap = new HashMap<>();

    /** XPath expression for extracting content from the document body. */
    protected String contentXpath = "//BODY";

    /** Map of metadata field names to their corresponding XPath expressions. */
    protected Map<String, String> metadataXpathMap = new LinkedHashMap<>();

    /** Default metadata field rules (key to XPath expression). */
    protected Map<String, String> defaultFieldRules;

    /**
     * Fallback rules applied after primary default-field rules. Each entry maps
     * a target key to a source key: when the target key has no non-blank value
     * after primary rules are applied, the value from the source key (if
     * non-blank) is copied into the target. Typical use: fall back from
     * {@code og:title} to populate {@code title} when no {@code <title>} element
     * is present.
     *
     * <p>Fallback rules are independent of {@link #defaultFieldRules}. Replacing
     * {@link #defaultFieldRules} via {@link #setDefaultFieldRules(Map)} does
     * <em>not</em> reset this map; use {@link #setDefaultFieldFallbackRules(Map)}
     * to customise or disable the fallback behaviour.</p>
     */
    protected Map<String, String> defaultFieldFallbackRules;

    /**
     * Whether to extract default HTML metadata (title, description, OpenGraph,
     * etc.). Defaults to {@code true}; new deployments get the default
     * metadata set out of the box.
     */
    protected boolean extractDefaultMetadata = true;

    /**
     * Whether to extract JSON-LD ({@code <script type="application/ld+json">})
     * blocks. Defaults to {@code false}: JSON-LD extraction is opt-in because the
     * raw JSON payload can be large and, on the file-crawl path, would otherwise
     * be folded into the indexed full-text content field. Enable it with
     * {@link #setExtractJsonLd(boolean) setExtractJsonLd(true)}.
     */
    protected boolean extractJsonLd = false;

    /** Thread-local instance of XPathAPI for thread-safe XPath evaluation. */
    private final ThreadLocal<XPathAPI> xpathAPI = new ThreadLocal<>();

    /**
     * Per-thread cache of compiled XPath expressions.
     *
     * <p>Note: ThreadLocals can pin the classloader of the values they hold to
     * the threads that touched them a known issue when this extractor is
     * deployed inside a servlet container (Tomcat, etc.) and the application
     * is undeployed. See {@link #destroy()} for the calling-thread cleanup
     * hook and {@link #clearXPathCache()} for the per-thread cleanup hook
     * callers can invoke from worker threads.</p>
     */
    private final ThreadLocal<Map<String, XPathExpression>> threadLocalXPathCache = ThreadLocal.withInitial(HashMap::new);

    /**
     * Per-thread {@link XPath} used to compile cached expressions. {@code XPath}
     * is not documented as thread-safe; the underlying {@link XPathFactory} is
     * shared statically (see {@link #XPATH_FACTORY}) and accessed via
     * {@link #newXPath()}.
     */
    private final ThreadLocal<XPath> threadLocalXPath = ThreadLocal.withInitial(HtmlExtractor::newXPath);

    /**
     * Per-thread set of XPath expressions already known to evaluate to a
     * non-node-set result (e.g. {@code string(...)}, {@code count(...)},
     * {@code boolean(...)}). The result type of an XPath expression is fixed by
     * the expression itself and is independent of the document, so once an
     * expression has been observed to fail node-set evaluation it is routed
     * straight to {@link #evaluateNonNodeSet} on subsequent documents instead of
     * throwing and catching an {@link XPathExpressionException} per page.
     */
    private final ThreadLocal<Set<String>> threadLocalNonNodeSetPaths = ThreadLocal.withInitial(HashSet::new);

    /** Lazily-initialised JSON parser for JSON-LD blocks. */
    private volatile ObjectMapper objectMapper;

    /**
     * Creates a new HtmlExtractor instance.
     */
    public HtmlExtractor() {
        super();
    }

    /**
     * Initialises default metadata field rules and fallback rules if none have
     * been provided externally.
     */
    @PostConstruct
    public void init() {
        if (defaultFieldRules == null) {
            defaultFieldRules = createDefaultFieldRules();
        }
        if (defaultFieldFallbackRules == null) {
            defaultFieldFallbackRules = createDefaultFieldFallbackRules();
        }
    }

    /**
     * Builds the built-in set of default field rules (key to XPath).
     *
     * <p>Subclasses may override this method to add, remove, or replace rules
     * without requiring a DI configuration change.</p>
     *
     * @return a mutable {@link LinkedHashMap} of default field rules
     */
    protected Map<String, String> createDefaultFieldRules() {
        final Map<String, String> rules = new LinkedHashMap<>();
        rules.put("title", "//TITLE/text()");
        rules.put("description", "//META[@name='description']/@content");
        rules.put("og:title", "//META[@property='og:title']/@content");
        rules.put("og:description", "//META[@property='og:description']/@content");
        rules.put("og:image", "//META[@property='og:image']/@content");
        rules.put("og:type", "//META[@property='og:type']/@content");
        rules.put("og:url", "//META[@property='og:url']/@content");
        rules.put("twitter:card", "//META[@name='twitter:card']/@content");
        rules.put("twitter:title", "//META[@name='twitter:title']/@content");
        rules.put("twitter:description", "//META[@name='twitter:description']/@content");
        rules.put("twitter:image", "//META[@name='twitter:image']/@content");
        rules.put("twitter:site", "//META[@name='twitter:site']/@content");
        rules.put("canonical", "//LINK[@rel='canonical']/@href");
        rules.put("keywords", "//META[@name='keywords']/@content");
        rules.put("author", "//META[@name='author']/@content");
        return rules;
    }

    /**
     * Builds the built-in set of fallback field rules (target key to source key).
     *
     * <p>When a target key has no non-blank value after primary default rules are
     * applied, the value from the mapped source key is copied in provided the
     * source key was populated. The canonical use-case is supplying
     * {@code og:title} as the title when no {@code <title>} element exists.</p>
     *
     * @return a mutable {@link LinkedHashMap} of fallback rules
     */
    protected Map<String, String> createDefaultFieldFallbackRules() {
        final Map<String, String> rules = new LinkedHashMap<>();
        rules.put("title", "og:title");
        rules.put("description", "og:description");
        return rules;
    }

    /**
     * Releases ThreadLocal references held on the calling thread.
     *
     * <p><b>Limitation:</b> a single invocation only clears the thread that
     * actually calls {@code destroy()}. In a typical servlet container
     * lifecycle this is the container management thread, not the worker
     * threads where the cache was populated; those workers must call
     * {@link #clearXPathCache()} (or be retired) to release their entries.
     * Container-driven destroy is still useful because it removes the
     * references owned by the management thread itself, which is often the
     * root cause of classloader pinning at undeploy time.</p>
     */
    @PreDestroy
    public void destroy() {
        threadLocalXPathCache.remove();
        threadLocalXPath.remove();
        threadLocalNonNodeSetPaths.remove();
        xpathAPI.remove();
    }

    /**
     * Returns a freshly created {@link XPath} instance, synchronising on the
     * shared {@link XPathFactory} since {@code XPathFactory} is not documented
     * as thread-safe.
     *
     * @return a new {@link XPath} instance
     */
    private static XPath newXPath() {
        synchronized (XPATH_FACTORY) {
            return XPATH_FACTORY.newXPath();
        }
    }

    @Override
    protected ExtractData createExtractData(final String content) {
        final DOMParser parser = getDomParser();
        try (final Reader reader = new StringReader(content)) {
            parser.parse(new InputSource(reader));
        } catch (final Exception e) {
            logger.warn("Failed to parse the content.", e);
            return new ExtractData(extractString(content));
        }

        final Document document = parser.getDocument();
        try {
            final ExtractData extractData = new ExtractData(
                    StreamUtil.stream(getStringsByXPath(document, contentXpath)).get(stream -> stream.collect(Collectors.joining(" "))));
            metadataXpathMap.entrySet().stream().forEach(e -> {
                extractData.putValues(e.getKey(), getStringsByXPath(document, e.getValue()));
            });
            if (extractDefaultMetadata) {
                applyDefaultFieldRules(document, extractData);
            }
            if (extractJsonLd) {
                extractJsonLd(document, extractData);
            }
            return extractData;
        } finally {
            xpathAPI.remove();
        }
    }

    /**
     * Applies the configured default-field rules, populating {@code extractData}
     * with extracted values. Keys already populated with a non-blank value
     * (set via {@link #addMetadata(String, String)}) are preserved; keys whose
     * custom XPath returned no values or only blank strings fall back to the
     * default rule.
     *
     * <p>After primary rules are applied, {@link #defaultFieldFallbackRules}
     * are evaluated: for each entry (targetKey to sourceKey), if the target key
     * still has no non-blank value but the source key does, the source values are
     * copied into the target. Custom rules always take precedence over both
     * primary default rules and fallback rules.</p>
     *
     * @param document the parsed HTML DOM
     * @param extractData the extract data to populate
     */
    protected void applyDefaultFieldRules(final Document document, final ExtractData extractData) {
        if (defaultFieldRules == null || defaultFieldRules.isEmpty()) {
            return;
        }
        defaultFieldRules.forEach((key, xpath) -> {
            if (hasNonBlankValue(extractData.getValues(key))) {
                // already populated with a non-blank value by a custom rule; do not overwrite
                return;
            }
            final String[] values = getStringsByXPath(document, xpath);
            final String[] nonBlank = Arrays.stream(values).filter(StringUtil::isNotBlank).map(String::trim).toArray(String[]::new);
            if (nonBlank.length > 0) {
                extractData.putValues(key, nonBlank);
            }
        });

        // After primary rules, apply fallbacks for keys still missing non-blank values.
        if (defaultFieldFallbackRules != null) {
            defaultFieldFallbackRules.forEach((key, fallbackKey) -> {
                if (hasNonBlankValue(extractData.getValues(key))) {
                    return;
                }
                final String[] fallbackValues = extractData.getValues(fallbackKey);
                if (hasNonBlankValue(fallbackValues)) {
                    final String[] nonBlank =
                            Arrays.stream(fallbackValues).filter(StringUtil::isNotBlank).map(String::trim).toArray(String[]::new);
                    if (nonBlank.length > 0) {
                        extractData.putValues(key, nonBlank);
                    }
                }
            });
        }
    }

    /**
     * Returns {@code true} if {@code values} contains at least one non-blank
     * entry.
     */
    private static boolean hasNonBlankValue(final String[] values) {
        if (values == null || values.length == 0) {
            return false;
        }
        for (final String value : values) {
            if (StringUtil.isNotBlank(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts JSON-LD blocks from the document. Each block's {@code @type}
     * value (if present) is appended to {@link #JSONLD_TYPE_KEY}, and the raw
     * JSON content is appended to {@link #JSONLD_RAW_KEY}. A block whose JSON is
     * malformed is logged and skipped for {@code @type} collection only; its raw
     * text is still retained in {@link #JSONLD_RAW_KEY} and the rest of the
     * extraction proceeds normally.
     *
     * <p>Existing values for {@link #JSONLD_RAW_KEY} or {@link #JSONLD_TYPE_KEY}
     * (typically populated by an operator-supplied
     * {@link #addMetadata(String, String)} rule that targets the same key) are
     * preserved and not overwritten. To re-enable automatic JSON-LD population
     * in that case, drop the colliding custom rule or rename it.</p>
     *
     * <p>Processing is bounded by {@link #JSONLD_MAX_BLOCK_COUNT},
     * {@link #JSONLD_MAX_RAW_TOTAL_BYTES}, and {@link #JSONLD_MAX_TYPES_PER_BLOCK}
     * to prevent unbounded memory growth on adversarial pages.</p>
     *
     * @param document the parsed HTML DOM
     * @param extractData the extract data to populate
     */
    protected void extractJsonLd(final Document document, final ExtractData extractData) {
        try {
            final XPathExpression expr = getXPathExpression(JSONLD_XPATH);
            final NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            if (nodes == null || nodes.getLength() == 0) {
                return;
            }
            final List<String> rawList = new ArrayList<>();
            final List<String> typeList = new ArrayList<>();
            long totalRawBytes = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                if (rawList.size() >= JSONLD_MAX_BLOCK_COUNT) {
                    logger.warn("JSON-LD block count exceeded {}, truncating.", JSONLD_MAX_BLOCK_COUNT);
                    break;
                }
                try {
                    final String raw = nodes.item(i).getTextContent();
                    if (StringUtil.isBlank(raw)) {
                        continue;
                    }
                    final String trimmed = raw.trim();
                    if (totalRawBytes + trimmed.length() > JSONLD_MAX_RAW_TOTAL_BYTES) {
                        logger.warn("JSON-LD raw total size would exceed {} bytes, truncating.", JSONLD_MAX_RAW_TOTAL_BYTES);
                        break;
                    }
                    totalRawBytes += trimmed.length();
                    rawList.add(trimmed);
                    collectJsonLdTypes(trimmed, typeList);
                } catch (final RuntimeException ex) {
                    // DOMException / NPE / etc. from a single node must not abort other blocks.
                    logger.warn("Skipping JSON-LD block due to DOM error.", ex);
                }
            }
            final boolean rawAlreadySet = hasNonBlankValue(extractData.getValues(JSONLD_RAW_KEY));
            final boolean typeAlreadySet = hasNonBlankValue(extractData.getValues(JSONLD_TYPE_KEY));
            if (!rawList.isEmpty() && !rawAlreadySet) {
                extractData.putValues(JSONLD_RAW_KEY, rawList.toArray(new String[0]));
            } else if (rawAlreadySet && logger.isDebugEnabled()) {
                logger.debug("JSON-LD raw key already populated by custom rule; auto-fill skipped.");
            }
            if (!typeList.isEmpty() && !typeAlreadySet) {
                extractData.putValues(JSONLD_TYPE_KEY, typeList.toArray(new String[0]));
            } else if (typeAlreadySet && logger.isDebugEnabled()) {
                logger.debug("JSON-LD type key already populated by custom rule; auto-fill skipped.");
            }
        } catch (final XPathException e) {
            logger.warn("Failed to evaluate JSON-LD XPath.", e);
        } catch (final CrawlerSystemException e) {
            // Defensive recovery: JSONLD_XPATH is a literal constant and should always
            // compile, but a future edit to the constant must not abort the whole
            // extraction. Keep the recovery symmetric with getStringsByXPath.
            logger.warn("Failed to compile JSON-LD XPath.", e.getCause() != null ? e.getCause() : e);
        } catch (final RuntimeException e) {
            // Defensive: any DOM-layer fault (DOMException, IllegalStateException, ...)
            // must not abort the rest of the extraction (see method javadoc).
            logger.warn("Failed to extract JSON-LD blocks.", e);
        }
    }

    /**
     * Parses the given JSON-LD string and appends any discovered {@code @type}
     * values into {@code typeList}. Malformed JSON is logged and skipped.
     *
     * @param json the raw JSON-LD content
     * @param typeList the list to append discovered {@code @type} values to
     */
    protected void collectJsonLdTypes(final String json, final List<String> typeList) {
        final ObjectMapper mapper = getObjectMapper();
        try {
            final JsonNode root = mapper.readTree(json);
            if (root == null) {
                return;
            }
            collectTypeNodes(root, typeList);
        } catch (final JsonProcessingException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Skipping malformed JSON-LD block.", e);
            } else {
                final String msg = e.getMessage();
                final String safe = msg == null ? "(none)" : msg.replaceAll("[\\r\\n\\t]", " ");
                logger.warn("Skipping malformed JSON-LD block: {}", safe);
            }
        } catch (final RuntimeException e) {
            logger.warn("Failed to parse JSON-LD block.", e);
        }
    }

    /**
     * Walks the JSON-LD tree and appends every {@code @type} value it finds.
     *
     * <p>Real-world Schema.org markup commonly nests typed entities, for
     * example WordPress/Yoast emit a top-level {@code @graph} array and other
     * pages embed typed entities under {@code mainEntity}, {@code author},
     * {@code publisher}, {@code itemListElement}, etc. The walk therefore
     * recurses into every object child except {@code @context} (which holds
     * vocabulary term definitions, not data). {@code @type} itself is not
     * recursed into because its value is always a string or array of strings
     * per the JSON-LD specification.</p>
     *
     * <p>Recursion depth is implicitly bounded by the parser's
     * {@link #JSONLD_MAX_NESTING_DEPTH}. Collection stops once
     * {@link #JSONLD_MAX_TYPES_PER_BLOCK} entries have been added.</p>
     */
    private void collectTypeNodes(final JsonNode node, final List<String> typeList) {
        if (node == null) {
            return;
        }
        if (node.isArray()) {
            node.forEach(child -> collectTypeNodes(child, typeList));
            return;
        }
        if (!node.isObject()) {
            return;
        }
        final JsonNode typeNode = node.get("@type");
        if (typeNode != null) {
            if (typeNode.isTextual()) {
                final String value = typeNode.asText();
                if (StringUtil.isNotBlank(value)) {
                    if (typeList.size() >= JSONLD_MAX_TYPES_PER_BLOCK) {
                        return;
                    }
                    typeList.add(value);
                }
            } else if (typeNode.isArray()) {
                typeNode.forEach(t -> {
                    if (t.isTextual()) {
                        final String value = t.asText();
                        if (StringUtil.isNotBlank(value)) {
                            if (typeList.size() >= JSONLD_MAX_TYPES_PER_BLOCK) {
                                return;
                            }
                            typeList.add(value);
                        }
                    }
                });
            }
        }
        // Recurse into every other child so nested typed entities (notably
        // @graph, mainEntity, author, publisher, itemListElement, ...) also
        // contribute their @type values. Skip @type (already handled) and
        // @context (vocabulary term definitions, not data).
        node.fields().forEachRemaining(field -> {
            final String fieldName = field.getKey();
            if ("@type".equals(fieldName) || "@context".equals(fieldName)) {
                return;
            }
            if (typeList.size() >= JSONLD_MAX_TYPES_PER_BLOCK) {
                return;
            }
            collectTypeNodes(field.getValue(), typeList);
        });
    }

    /**
     * Returns the lazily-initialised {@link ObjectMapper} used to parse
     * JSON-LD blocks. The mapper is configured with strict
     * {@link StreamReadConstraints} so a hostile site cannot drive the
     * extractor into stack/heap exhaustion via deeply nested JSON, oversized
     * strings, or oversized numbers.
     *
     * @return the singleton {@link ObjectMapper}
     */
    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = objectMapper;
        if (mapper == null) {
            synchronized (this) {
                mapper = objectMapper;
                if (mapper == null) {
                    mapper = new ObjectMapper();
                    final StreamReadConstraints constraints = StreamReadConstraints.builder()
                            .maxNestingDepth(JSONLD_MAX_NESTING_DEPTH)
                            .maxStringLength(JSONLD_MAX_STRING_LENGTH)
                            .maxNumberLength(JSONLD_MAX_NUMBER_LENGTH)
                            .build();
                    mapper.getFactory().setStreamReadConstraints(constraints);
                    objectMapper = mapper;
                }
            }
        }
        return mapper;
    }

    /**
     * Returns a compiled, cached {@link XPathExpression} for {@code expression}.
     * Caching is per-thread, which keeps {@link XPathExpression} usage on the
     * same thread that compiled it (the JDK does not document
     * {@code XPathExpression} as thread-safe).
     *
     * @param expression the XPath expression to compile
     * @return the compiled expression
     * @throws CrawlerSystemException if {@code expression} fails to compile
     */
    protected XPathExpression getXPathExpression(final String expression) {
        return threadLocalXPathCache.get().computeIfAbsent(expression, expr -> {
            try {
                return threadLocalXPath.get().compile(expr);
            } catch (final XPathExpressionException e) {
                throw new CrawlerSystemException("Failed to compile XPath: expression=" + expr, e);
            }
        });
    }

    /**
     * Clears the per-thread XPath compilation caches for the calling thread.
     *
     * <p>This is a memory-reclamation hook, not a correctness requirement: the
     * caches are keyed by the XPath expression string, so changing
     * {@link #defaultFieldRules}, {@link #metadataXpathMap}, or
     * {@link #contentXpath} to use different expressions compiles the new
     * expressions automatically on first use. Call this to release the compiled
     * expressions of rules that are no longer in use on this thread.</p>
     */
    public void clearXPathCache() {
        threadLocalXPathCache.get().clear();
        threadLocalNonNodeSetPaths.get().clear();
    }

    /**
     * Extracts strings from a document using the specified XPath expression.
     * The compiled {@link XPathExpression} is cached per thread, so repeated
     * calls with the same expression skip recompilation.
     *
     * <p>A malformed XPath expression is logged at {@code WARN} and an empty
     * array is returned, so callers never abort the whole extraction because
     * of a single misconfigured rule.</p>
     *
     * @param document the DOM document to extract strings from
     * @param path the XPath expression to evaluate
     * @return an array of strings extracted from the document
     */
    protected String[] getStringsByXPath(final Document document, final String path) {
        final XPathExpression expr;
        try {
            expr = getXPathExpression(path);
        } catch (final CrawlerSystemException e) {
            logger.warn("Failed to parse the content by {}", path, e.getCause() != null ? e.getCause() : e);
            return StringUtil.EMPTY_STRINGS;
        }
        if (threadLocalNonNodeSetPaths.get().contains(path)) {
            // Result type is fixed by the expression, not the document: an expression
            // already seen to be non-node-set skips the throwing node-set attempt.
            return evaluateNonNodeSet(document, path, null);
        }
        try {
            final NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            if (nodes == null) {
                return StringUtil.EMPTY_STRINGS;
            }
            final List<String> strList = new ArrayList<>(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                final Node node = nodes.item(i);
                if (node == null) {
                    continue;
                }
                try {
                    final String text = node.getTextContent();
                    strList.add(text == null ? "" : text);
                } catch (final DOMException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to read text from node {} for XPath {}", i, path, ex);
                    }
                }
            }
            return strList.toArray(new String[0]);
        } catch (final XPathException e) {
            // Non-node-set result (string()/count()/boolean()/...): remember the path so
            // subsequent documents skip the failing node-set attempt instead of re-throwing.
            threadLocalNonNodeSetPaths.get().add(path);
            return evaluateNonNodeSet(document, path, e);
        }
    }

    /**
     * Fallback path for XPath expressions whose result type is not a node-set.
     * When {@code previousFailure} is non-null it is added as a suppressed
     * exception to any second failure so callers have full context; it may be
     * {@code null} when this path is entered directly for an expression already
     * known to be non-node-set.
     *
     * @param document the DOM document to extract strings from
     * @param path the XPath expression to evaluate
     * @param previousFailure the failure raised by the node-set evaluation, or
     *            {@code null} if the node-set attempt was skipped
     * @return an array of strings extracted from the document
     */
    private String[] evaluateNonNodeSet(final Document document, final String path, final XPathException previousFailure) {
        if (logger.isDebugEnabled()) {
            logger.debug("XPath '{}' evaluated as non-node-set (fallback path).", path);
        }
        try {
            final XPathEvaluationResult<?> xObj = getXPathAPI().eval(document, path);
            switch (xObj.type()) {
            case BOOLEAN:
                final Boolean b = (Boolean) xObj.value();
                return new String[] { b.toString() };
            case NUMBER:
                final Number d = (Number) xObj.value();
                return new String[] { d.toString() };
            case STRING:
                final String str = (String) xObj.value();
                return new String[] { str.trim() };
            case NODESET:
                final XPathNodes nodeList = (XPathNodes) xObj.value();
                final List<String> strList = new ArrayList<>(nodeList.size());
                for (int i = 0; i < nodeList.size(); i++) {
                    final Node node = nodeList.get(i);
                    if (node == null) {
                        continue;
                    }
                    try {
                        final String text = node.getTextContent();
                        strList.add(text == null ? "" : text);
                    } catch (final DOMException ex) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Failed to read text from node {} for XPath {}", i, path, ex);
                        }
                    }
                }
                return strList.toArray(new String[0]);
            case NODE:
                final Node node = (Node) xObj.value();
                try {
                    final String text = node.getTextContent();
                    return new String[] { text == null ? "" : text };
                } catch (final DOMException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Failed to read text from node for XPath {}", path, ex);
                    }
                    return StringUtil.EMPTY_STRINGS;
                }
            default:
                Object obj = xObj.value();
                if (obj == null) {
                    obj = "";
                }
                return new String[] { obj.toString() };
            }
        } catch (final XPathException e) {
            if (previousFailure != null) {
                e.addSuppressed(previousFailure);
            }
            logger.warn("Failed to parse the content by {}", path, e);
            return StringUtil.EMPTY_STRINGS;
        }
    }

    /**
     * Creates and configures a DOM parser for parsing HTML content.
     *
     * @return a configured DOMParser instance
     * @throws CrawlerSystemException if the parser configuration is invalid
     */
    protected DOMParser getDomParser() {
        try {
            final DOMParser parser = new DOMParser();
            // feature
            for (final Map.Entry<String, String> entry : featureMap.entrySet()) {
                parser.setFeature(entry.getKey(), "true".equalsIgnoreCase(entry.getValue()));
            }

            // property
            for (final Map.Entry<String, String> entry : propertyMap.entrySet()) {
                parser.setProperty(entry.getKey(), entry.getValue());
            }

            return parser;
        } catch (final Exception e) {
            throw new CrawlerSystemException("Invalid parser configuration.", e);
        }
    }

    /**
     * Gets a thread-local XPathAPI instance for thread-safe XPath evaluation.
     *
     * @return the XPathAPI instance for the current thread
     */
    protected XPathAPI getXPathAPI() {
        XPathAPI cachedXPathAPI = xpathAPI.get();
        if (cachedXPathAPI == null) {
            cachedXPathAPI = new XPathAPI();
            xpathAPI.set(cachedXPathAPI);
        }
        return cachedXPathAPI;
    }

    /**
     * Adds a metadata field with its corresponding XPath expression for extraction.
     *
     * @param name the name of the metadata field
     * @param xpath the XPath expression to extract the metadata value
     */
    public void addMetadata(final String name, final String xpath) {
        metadataXpathMap.put(name, xpath);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.extractor.impl.AbstractXmlExtractor#getEncodingPattern()
     */
    @Override
    protected Pattern getEncodingPattern() {
        return metaCharsetPattern;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.extractor.impl.AbstractXmlExtractor#getTagPattern()
     */
    @Override
    protected Pattern getTagPattern() {
        return htmlTagPattern;
    }

    /**
     * Gets the pattern used for extracting charset from meta tags.
     *
     * @return the meta charset pattern
     */
    public Pattern getMetaCharsetPattern() {
        return metaCharsetPattern;
    }

    /**
     * Sets the pattern used for extracting charset from meta tags.
     *
     * @param metaCharsetPattern the meta charset pattern to set
     */
    public void setMetaCharsetPattern(final Pattern metaCharsetPattern) {
        this.metaCharsetPattern = metaCharsetPattern;
    }

    /**
     * Gets the pattern used for matching HTML tags.
     *
     * @return the HTML tag pattern
     */
    public Pattern getHtmlTagPattern() {
        return htmlTagPattern;
    }

    /**
     * Sets the pattern used for matching HTML tags.
     *
     * @param htmlTagPattern the HTML tag pattern to set
     */
    public void setHtmlTagPattern(final Pattern htmlTagPattern) {
        this.htmlTagPattern = htmlTagPattern;
    }

    /**
     * Gets the map of parser features.
     *
     * @return the feature map
     */
    public Map<String, String> getFeatureMap() {
        return featureMap;
    }

    /**
     * Sets the map of parser features.
     *
     * @param featureMap the feature map to set
     */
    public void setFeatureMap(final Map<String, String> featureMap) {
        this.featureMap = featureMap;
    }

    /**
     * Gets the map of parser properties.
     *
     * @return the property map
     */
    public Map<String, String> getPropertyMap() {
        return propertyMap;
    }

    /**
     * Sets the map of parser properties.
     *
     * @param propertyMap the property map to set
     */
    public void setPropertyMap(final Map<String, String> propertyMap) {
        this.propertyMap = propertyMap;
    }

    /**
     * Gets the configured default-field rules (key to XPath).
     *
     * @return the default-field rules
     */
    public Map<String, String> getDefaultFieldRules() {
        return defaultFieldRules;
    }

    /**
     * Replaces the default-field rules used for default metadata extraction.
     * A defensive copy is taken so subsequent mutations of the supplied map do
     * not affect this extractor. Passing {@code null} immediately re-initialises
     * the built-in defaults via {@link #createDefaultFieldRules()}.
     *
     * @param defaultFieldRules the rules to use; if {@code null}, the built-in
     *        defaults are restored immediately
     */
    public void setDefaultFieldRules(final Map<String, String> defaultFieldRules) {
        this.defaultFieldRules = defaultFieldRules == null ? createDefaultFieldRules() : new LinkedHashMap<>(defaultFieldRules);
    }

    /**
     * Gets the configured default-field fallback rules (target key to source key).
     *
     * @return the default-field fallback rules
     */
    public Map<String, String> getDefaultFieldFallbackRules() {
        return defaultFieldFallbackRules;
    }

    /**
     * Replaces the default-field fallback rules. A defensive copy is taken so
     * subsequent mutations of the supplied map do not affect this extractor.
     * Passing {@code null} immediately re-initialises the built-in fallback
     * defaults via {@link #createDefaultFieldFallbackRules()}.
     *
     * @param defaultFieldFallbackRules the fallback rules to use; if {@code null},
     *        the built-in fallback defaults are restored immediately
     */
    public void setDefaultFieldFallbackRules(final Map<String, String> defaultFieldFallbackRules) {
        this.defaultFieldFallbackRules =
                defaultFieldFallbackRules == null ? createDefaultFieldFallbackRules() : new LinkedHashMap<>(defaultFieldFallbackRules);
    }

    /**
     * Returns whether default HTML metadata extraction is enabled.
     *
     * @return {@code true} if default metadata is extracted
     */
    public boolean isExtractDefaultMetadata() {
        return extractDefaultMetadata;
    }

    /**
     * Enables or disables default HTML metadata extraction (title, description,
     * OpenGraph, Twitter Card, canonical, keywords, author).
     *
     * @param extractDefaultMetadata {@code true} to extract default metadata
     */
    public void setExtractDefaultMetadata(final boolean extractDefaultMetadata) {
        this.extractDefaultMetadata = extractDefaultMetadata;
    }

    /**
     * Returns whether JSON-LD extraction is enabled.
     *
     * @return {@code true} if JSON-LD blocks are extracted
     */
    public boolean isExtractJsonLd() {
        return extractJsonLd;
    }

    /**
     * Enables or disables JSON-LD extraction
     * ({@code <script type="application/ld+json">}).
     *
     * @param extractJsonLd {@code true} to extract JSON-LD blocks
     */
    public void setExtractJsonLd(final boolean extractJsonLd) {
        this.extractJsonLd = extractJsonLd;
    }
}
