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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
 * canonical, keywords, author) and parses {@code <script type="application/ld+json">}
 * blocks. Both subsystems can be disabled via {@link #setExtractDefaultMetadata(boolean)}
 * and {@link #setExtractJsonLd(boolean)}.</p>
 *
 * <p>Compiled {@link XPathExpression} instances are cached per thread to avoid
 * re-parsing every XPath on each extraction.</p>
 */
public class HtmlExtractor extends AbstractXmlExtractor {
    /** Logger for this class. */
    protected static final Logger logger = LogManager.getLogger(HtmlExtractor.class);

    /** Metadata key holding raw JSON-LD strings. */
    public static final String JSONLD_RAW_KEY = "jsonld.raw";

    /** Metadata key holding {@code @type} values from JSON-LD blocks. */
    public static final String JSONLD_TYPE_KEY = "jsonld.type";

    /** XPath expression matching JSON-LD script blocks. */
    protected static final String JSONLD_XPATH = "//SCRIPT[@type='application/ld+json']";

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
    protected Map<String, String> metadataXpathMap = new HashMap<>();

    /** Default metadata field rules (key -&gt; XPath expression). */
    protected Map<String, String> defaultFieldRules;

    /**
     * Whether to extract default HTML metadata (title, description, OpenGraph,
     * etc.). Defaults to {@code true}; new deployments get the default
     * metadata set out of the box. Operators upgrading existing deployments
     * who have configured custom metadata rules are warned at
     * {@link #init()} when a custom rule key collides with a built-in
     * default-rule key (see {@link #defaultFieldRules}).
     */
    protected boolean extractDefaultMetadata = true;

    /** Whether to extract JSON-LD ({@code <script type="application/ld+json">}) blocks. */
    protected boolean extractJsonLd = true;

    /** Thread-local instance of XPathAPI for thread-safe XPath evaluation. */
    private final ThreadLocal<XPathAPI> xpathAPI = new ThreadLocal<>();

    /**
     * Per-thread cache of compiled XPath expressions.
     *
     * <p>Note: ThreadLocals can pin the classloader of the values they hold to
     * the threads that touched them — a known issue when this extractor is
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

    /** Lazily-initialised JSON parser for JSON-LD blocks. */
    private volatile ObjectMapper objectMapper;

    /**
     * Creates a new HtmlExtractor instance.
     */
    public HtmlExtractor() {
        super();
    }

    /**
     * Initialises default metadata field rules if none have been provided and
     * warns operators if any user-configured custom metadata rule key
     * collides with a built-in default-rule key while
     * {@link #extractDefaultMetadata} is enabled.
     */
    @PostConstruct
    public void init() {
        if (defaultFieldRules == null) {
            final Map<String, String> rules = new LinkedHashMap<>();
            rules.put("title", "//TITLE/text() | //META[@property='og:title']/@content");
            rules.put("description", "//META[@name='description']/@content | //META[@property='og:description']/@content");
            rules.put("og:title", "//META[@property='og:title']/@content");
            rules.put("og:description", "//META[@property='og:description']/@content");
            rules.put("og:image", "//META[@property='og:image']/@content");
            rules.put("og:type", "//META[@property='og:type']/@content");
            rules.put("og:url", "//META[@property='og:url']/@content");
            rules.put("twitter:card", "//META[@name='twitter:card']/@content");
            rules.put("canonical", "//LINK[@rel='canonical']/@href");
            rules.put("keywords", "//META[@name='keywords']/@content");
            rules.put("author", "//META[@name='author']/@content");
            defaultFieldRules = rules;
        }
        warnOnMetadataKeyCollisions();
    }

    /**
     * Logs a single WARN entry if {@link #extractDefaultMetadata} is enabled
     * and the operator-configured {@code metadataXpathMap} declares any key
     * that is also defined by {@link #defaultFieldRules}. Per-key custom rules
     * still take precedence (see {@link #applyDefaultFieldRules}); the warning
     * exists only to surface potentially surprising overlap to operators.
     */
    protected void warnOnMetadataKeyCollisions() {
        if (!extractDefaultMetadata || defaultFieldRules == null || defaultFieldRules.isEmpty() || metadataXpathMap == null
                || metadataXpathMap.isEmpty()) {
            return;
        }
        final List<String> collisions =
                metadataXpathMap.keySet().stream().filter(defaultFieldRules::containsKey).sorted().collect(Collectors.toList());
        if (!collisions.isEmpty()) {
            logger.warn(
                    "HtmlExtractor: custom metadata rule key(s) {} collide with default-rule keys; "
                            + "custom rules take precedence, but enabling extractDefaultMetadata may produce duplicates "
                            + "or unexpected fallback behavior. Disable default metadata or rename custom keys to silence this warning.",
                    collisions);
        }
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
     * with extracted values. Existing keys (set via
     * {@link #addMetadata(String, String)}) are preserved.
     *
     * @param document the parsed HTML DOM
     * @param extractData the extract data to populate
     */
    protected void applyDefaultFieldRules(final Document document, final ExtractData extractData) {
        if (defaultFieldRules == null || defaultFieldRules.isEmpty()) {
            return;
        }
        defaultFieldRules.forEach((key, xpath) -> {
            if (extractData.getValues(key) != null) {
                // already populated by a custom rule; do not overwrite
                return;
            }
            final String[] values = getStringsByXPath(document, xpath);
            final String[] nonBlank = Arrays.stream(values).filter(StringUtil::isNotBlank).map(String::trim).toArray(String[]::new);
            if (nonBlank.length > 0) {
                extractData.putValues(key, nonBlank);
            }
        });
    }

    /**
     * Extracts JSON-LD blocks from the document. Each block's {@code @type}
     * value (if present) is appended to {@link #JSONLD_TYPE_KEY}, and the raw
     * JSON content is appended to {@link #JSONLD_RAW_KEY}. Malformed JSON is
     * logged and skipped; the rest of the extraction proceeds normally.
     *
     * <p>Existing values for {@link #JSONLD_RAW_KEY} or {@link #JSONLD_TYPE_KEY}
     * (typically populated by an operator-supplied
     * {@link #addMetadata(String, String)} rule that targets the same key) are
     * preserved and not overwritten — matching the precedence rule used by
     * {@link #applyDefaultFieldRules(Document, ExtractData)}. To re-enable
     * automatic JSON-LD population in that case, drop the colliding custom
     * rule or rename it.</p>
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
            for (int i = 0; i < nodes.getLength(); i++) {
                final String raw = nodes.item(i).getTextContent();
                if (StringUtil.isBlank(raw)) {
                    continue;
                }
                final String trimmed = raw.trim();
                rawList.add(trimmed);
                collectJsonLdTypes(trimmed, typeList);
            }
            if (!rawList.isEmpty() && extractData.getValues(JSONLD_RAW_KEY) == null) {
                extractData.putValues(JSONLD_RAW_KEY, rawList.toArray(new String[0]));
            }
            if (!typeList.isEmpty() && extractData.getValues(JSONLD_TYPE_KEY) == null) {
                extractData.putValues(JSONLD_TYPE_KEY, typeList.toArray(new String[0]));
            }
        } catch (final XPathException e) {
            logger.warn("Failed to evaluate JSON-LD XPath.", e);
        } catch (final CrawlerSystemException e) {
            // JSONLD_XPATH is a constant and should always compile, but keep
            // the recovery symmetric with getStringsByXPath so a future change
            // to the constant cannot abort the whole extraction.
            logger.warn("Failed to compile JSON-LD XPath.", e.getCause() != null ? e.getCause() : e);
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
        if (mapper == null) {
            return;
        }
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
                logger.warn("Skipping malformed JSON-LD block: {}", e.getMessage());
            }
        } catch (final RuntimeException e) {
            logger.warn("Failed to parse JSON-LD block.", e);
        }
    }

    /**
     * Walks the JSON-LD tree and appends every {@code @type} value it finds.
     *
     * <p>Real-world Schema.org markup commonly nests typed entities — for
     * example WordPress/Yoast emit a top-level {@code @graph} array and other
     * pages embed typed entities under {@code mainEntity}, {@code author},
     * {@code publisher}, {@code itemListElement}, etc. The walk therefore
     * recurses into every object child except {@code @context} (which holds
     * vocabulary term definitions, not data). {@code @type} itself is not
     * recursed into because its value is always a string or array of strings
     * per the JSON-LD specification.</p>
     *
     * <p>Recursion depth is implicitly bounded by the parser's
     * {@link #JSONLD_MAX_NESTING_DEPTH}, so a hostile document cannot drive
     * this walk into stack exhaustion.</p>
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
                    typeList.add(value);
                }
            } else if (typeNode.isArray()) {
                typeNode.forEach(t -> {
                    if (t.isTextual()) {
                        final String value = t.asText();
                        if (StringUtil.isNotBlank(value)) {
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
     * Clears the per-thread XPath compilation cache for the calling thread.
     * Use this if {@link #defaultFieldRules}, {@link #metadataXpathMap}, or
     * {@link #contentXpath} change after the extractor has already processed
     * documents on this thread.
     */
    public void clearXPathCache() {
        threadLocalXPathCache.get().clear();
    }

    /**
     * Extracts strings from a document using the specified XPath expression.
     * The compiled {@link XPathExpression} is cached per thread, so repeated
     * calls with the same expression skip recompilation.
     *
     * <p>A malformed XPath expression — whether it fails to compile or fails
     * to evaluate — is logged at {@code WARN} and an empty array is returned,
     * matching the pre-cache behaviour. Callers like
     * {@link #createExtractData(String)} therefore never abort the whole
     * extraction because of a single misconfigured rule in
     * {@link #contentXpath} or {@link #metadataXpathMap}.</p>
     *
     * @param document the DOM document to extract strings from
     * @param path the XPath expression to evaluate
     * @return an array of strings extracted from the document
     */
    protected String[] getStringsByXPath(final Document document, final String path) {
        // Use the cached compiled expression to evaluate as a node-set first;
        // node-set is the common case for both content and metadata XPaths.
        final XPathExpression expr;
        try {
            expr = getXPathExpression(path);
        } catch (final CrawlerSystemException e) {
            // Compile failure of a malformed XPath. Restore the pre-cache
            // behaviour where XPathAPI.eval would have thrown XPathException
            // here and we logged + returned empty rather than propagating the
            // failure out of createExtractData.
            logger.warn("Failed to parse the content by {}", path, e.getCause() != null ? e.getCause() : e);
            return StringUtil.EMPTY_STRINGS;
        }
        try {
            final NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            if (nodes == null) {
                return StringUtil.EMPTY_STRINGS;
            }
            final List<String> strList = new ArrayList<>(nodes.getLength());
            for (int i = 0; i < nodes.getLength(); i++) {
                final Node node = nodes.item(i);
                if (node != null) {
                    final String text = node.getTextContent();
                    strList.add(text == null ? "" : text);
                }
            }
            return strList.toArray(new String[0]);
        } catch (final XPathException e) {
            // Some XPath expressions (boolean(), count(), string(), ...) cannot
            // be evaluated as a node-set. Fall back to the original path that
            // handles every result type via XPathAPI.eval, which preserves the
            // public behaviour for non-node-set expressions.
            return evaluateNonNodeSet(document, path, e);
        }
    }

    /**
     * Fallback path for XPath expressions whose result type is not a node-set
     * (e.g., {@code boolean()}, {@code count()}, {@code string()}). Returns
     * the result coerced to one or more strings.
     *
     * @param document the DOM document to extract strings from
     * @param path the XPath expression to evaluate
     * @param previousFailure the failure raised by the node-set evaluation
     * @return an array of strings extracted from the document
     */
    private String[] evaluateNonNodeSet(final Document document, final String path, final XPathException previousFailure) {
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
                final List<String> strList = new ArrayList<>();
                for (int i = 0; i < nodeList.size(); i++) {
                    final Node node = nodeList.get(i);
                    strList.add(node.getTextContent());
                }
                return strList.toArray(n -> new String[n]);
            case NODE:
                final Node node = (Node) xObj.value();
                return new String[] { node.getTextContent() };
            default:
                Object obj = xObj.value();
                if (obj == null) {
                    obj = "";
                }
                return new String[] { obj.toString() };
            }
        } catch (final XPathException e) {
            logger.warn("Failed to parse the content by {}", path, previousFailure);
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
     * Gets the configured default-field rules (key -&gt; XPath).
     *
     * @return the default-field rules
     */
    public Map<String, String> getDefaultFieldRules() {
        return defaultFieldRules;
    }

    /**
     * Replaces the default-field rules used for default metadata extraction.
     * A defensive copy is taken so subsequent mutations of the supplied map do
     * not affect this extractor.
     *
     * @param defaultFieldRules the rules to use; if {@code null}, defaults are
     *        re-initialised on the next call to {@link #init()}
     */
    public void setDefaultFieldRules(final Map<String, String> defaultFieldRules) {
        this.defaultFieldRules = defaultFieldRules == null ? null : new LinkedHashMap<>(defaultFieldRules);
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
