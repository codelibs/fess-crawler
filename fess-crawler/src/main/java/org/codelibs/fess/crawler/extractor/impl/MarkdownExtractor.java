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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

/**
 * Extracts text content and metadata from Markdown files.
 * This extractor provides better structured data extraction compared to Tika's generic text extraction.
 *
 * <p>Features:
 * <ul>
 *   <li>YAML front matter metadata extraction</li>
 *   <li>Heading structure extraction</li>
 *   <li>Link URL extraction</li>
 *   <li>Code block content extraction</li>
 *   <li>Clean text conversion from Markdown</li>
 *   <li>Configurable encoding with BOM detection (UTF-8/UTF-16/UTF-32)</li>
 *   <li>Reader-based streaming with optional length cap to bound heap usage</li>
 * </ul>
 */
public class MarkdownExtractor extends AbstractExtractor {
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(MarkdownExtractor.class);

    /** Default read buffer size in characters. */
    private static final int READ_BUFFER_SIZE = 8192;

    /** Default encoding for Markdown files. */
    protected String encoding = Constants.UTF_8;

    /** Whether to extract front matter as metadata. */
    protected boolean extractFrontMatter = true;

    /** Whether to extract headings as metadata. */
    protected boolean extractHeadings = true;

    /** Whether to extract link URLs as metadata. */
    protected boolean extractLinks = false;

    /**
     * Maximum number of characters to read from the input. Defaults to
     * {@link Long#MAX_VALUE} (effectively unlimited). Values less than or
     * equal to zero are also interpreted as unlimited.
     */
    protected long maxTextLength = Long.MAX_VALUE;

    /** Markdown parser with extensions. */
    protected Parser parser;

    /** Text content renderer. */
    protected TextContentRenderer textRenderer;

    /**
     * Constructs a new MarkdownExtractor and initializes the parser.
     */
    public MarkdownExtractor() {
        super();
        initializeParser();
    }

    @Override
    public int getWeight() {
        return 2; // Higher priority than TikaExtractor (weight=1)
    }

    /**
     * Initializes the Markdown parser with extensions.
     */
    protected void initializeParser() {
        final List<org.commonmark.Extension> extensions = Arrays.asList(YamlFrontMatterExtension.create());
        parser = Parser.builder().extensions(extensions).build();
        textRenderer = TextContentRenderer.builder().build();
    }

    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);

        try {
            final String rawMarkdown = readMarkdown(in);
            final Node document = parser.parse(rawMarkdown);

            // Extract plain text
            final String plainText = textRenderer.render(document);

            final ExtractData extractData = new ExtractData(plainText);

            // Extract front matter metadata
            if (extractFrontMatter) {
                extractFrontMatterMetadata(document, extractData);
            }

            // Extract headings
            if (extractHeadings) {
                extractHeadingMetadata(document, extractData);
            }

            // Extract links
            if (extractLinks) {
                extractLinkMetadata(document, extractData);
            }

            return extractData;
        } catch (final Exception e) {
            throw new ExtractException("Failed to extract Markdown content", e);
        }
    }

    /**
     * Reads the entire Markdown source from the supplied input stream, honoring
     * a leading BOM when present and bounding the result by {@link #maxTextLength}.
     *
     * @param in the input stream
     * @return the decoded Markdown source
     * @throws java.io.IOException if reading fails
     */
    protected String readMarkdown(final InputStream in) throws java.io.IOException {
        final BOMInputStream bomIn = BOMInputStream.builder()
                .setInputStream(in)
                .setInclude(false)
                .setByteOrderMarks(ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE,
                        ByteOrderMark.UTF_32BE)
                .get();
        final String detected = bomIn.getBOMCharsetName();
        final String charset = detected != null ? detected : getEncoding();
        try (Reader reader = new InputStreamReader(bomIn, charset); BufferedReader br = new BufferedReader(reader)) {
            final StringBuilder sb = new StringBuilder();
            final char[] buf = new char[READ_BUFFER_SIZE];
            long total = 0;
            int n;
            while ((n = br.read(buf)) >= 0) {
                if (maxTextLength > 0 && total + n > maxTextLength) {
                    final int remaining = (int) (maxTextLength - total);
                    if (remaining > 0) {
                        sb.append(buf, 0, remaining);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Truncating Markdown content at maxTextLength={} characters", maxTextLength);
                    }
                    break;
                }
                sb.append(buf, 0, n);
                total += n;
            }
            return sb.toString();
        }
    }

    /**
     * Extracts YAML front matter metadata from the document.
     *
     * @param document the parsed Markdown document
     * @param extractData the extract data to populate
     */
    protected void extractFrontMatterMetadata(final Node document, final ExtractData extractData) {
        final YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
        document.accept(visitor);

        final Map<String, List<String>> frontMatter = visitor.getData();
        for (final Map.Entry<String, List<String>> entry : frontMatter.entrySet()) {
            final String key = "frontmatter." + entry.getKey();
            final List<String> values = entry.getValue();
            if (values != null && !values.isEmpty()) {
                extractData.putValues(key, values.toArray(new String[0]));
            }
        }
    }

    /**
     * Extracts heading text from the document.
     *
     * @param document the parsed Markdown document
     * @param extractData the extract data to populate
     */
    protected void extractHeadingMetadata(final Node document, final ExtractData extractData) {
        final List<String> headings = new ArrayList<>();

        document.accept(new AbstractVisitor() {
            @Override
            public void visit(final Heading heading) {
                final StringBuilder headingText = new StringBuilder();
                Node child = heading.getFirstChild();
                while (child != null) {
                    if (child instanceof Text) {
                        headingText.append(((Text) child).getLiteral());
                    }
                    child = child.getNext();
                }
                if (headingText.length() > 0) {
                    headings.add(headingText.toString());
                }
                super.visit(heading);
            }
        });

        if (!headings.isEmpty()) {
            extractData.putValues("headings", headings.toArray(new String[0]));
        }
    }

    /**
     * Extracts link URLs from the document.
     *
     * @param document the parsed Markdown document
     * @param extractData the extract data to populate
     */
    protected void extractLinkMetadata(final Node document, final ExtractData extractData) {
        final List<String> links = new ArrayList<>();

        document.accept(new AbstractVisitor() {
            @Override
            public void visit(final Link link) {
                final String destination = link.getDestination();
                if (StringUtil.isNotBlank(destination)) {
                    links.add(destination);
                }
                super.visit(link);
            }
        });

        if (!links.isEmpty()) {
            extractData.putValues("links", links.toArray(new String[0]));
        }
    }

    /**
     * Gets the encoding for Markdown files.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Sets the encoding for Markdown files.
     *
     * @param encoding the encoding
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * Sets whether to extract front matter as metadata.
     *
     * @param extractFrontMatter true to extract front matter
     */
    public void setExtractFrontMatter(final boolean extractFrontMatter) {
        this.extractFrontMatter = extractFrontMatter;
    }

    /**
     * Sets whether to extract headings as metadata.
     *
     * @param extractHeadings true to extract headings
     */
    public void setExtractHeadings(final boolean extractHeadings) {
        this.extractHeadings = extractHeadings;
    }

    /**
     * Sets whether to extract link URLs as metadata.
     *
     * @param extractLinks true to extract links
     */
    public void setExtractLinks(final boolean extractLinks) {
        this.extractLinks = extractLinks;
    }

    /**
     * Returns the maximum number of characters that will be extracted.
     *
     * @return the maximum text length
     */
    public long getMaxTextLength() {
        return maxTextLength;
    }

    /**
     * Sets the maximum number of characters that will be extracted. Values
     * less than or equal to zero, or {@link Long#MAX_VALUE}, are interpreted
     * as unlimited.
     *
     * @param maxTextLength the maximum text length
     */
    public void setMaxTextLength(final long maxTextLength) {
        this.maxTextLength = maxTextLength;
    }
}
