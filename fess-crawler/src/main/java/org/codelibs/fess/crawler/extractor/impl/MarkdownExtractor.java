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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.InputStreamUtil;
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
 *   <li>Configurable encoding</li>
 * </ul>
 */
public class MarkdownExtractor extends AbstractExtractor {
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(MarkdownExtractor.class);

    /** Default encoding for Markdown files. */
    protected String encoding = Constants.UTF_8;

    /** Whether to extract front matter as metadata. */
    protected boolean extractFrontMatter = true;

    /** Whether to extract headings as metadata. */
    protected boolean extractHeadings = true;

    /** Whether to extract link URLs as metadata. */
    protected boolean extractLinks = false;

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
            final String content = new String(InputStreamUtil.getBytes(in), getEncoding());
            final Node document = parser.parse(content);

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
}
