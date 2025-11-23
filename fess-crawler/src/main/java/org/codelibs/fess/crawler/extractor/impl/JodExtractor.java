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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Extracts text content from various document formats using JODConverter.
 */
public class JodExtractor extends AbstractExtractor {
    /** Logger for this class. */
    private static final Logger logger = LogManager.getLogger(JodExtractor.class);

    /** Office manager for document conversion. */
    protected OfficeManager officeManager;

    /** Temporary directory for file operations. */
    protected File tempDir = null;

    /** Output encoding for extracted text. */
    protected String outputEncoding = Constants.UTF_8;

    /** Map of input file extensions to output extensions. */
    private final Map<String, String> extensionMap = new HashMap<>();

    /** Map of output extensions to their corresponding extractors. */
    private final Map<String, Extractor> extractorMap = new HashMap<>();

    /**
     * Constructs a new JodExtractor and initializes the extension and extractor mappings.
     */
    public JodExtractor() {
        extensionMap.put("", "txt");
        // Text Formats
        extensionMap.put("odt", "txt");
        extensionMap.put("ott", "txt");
        extensionMap.put("sxw", "txt");
        extensionMap.put("rtf", "txt");
        extensionMap.put("doc", "txt");
        extensionMap.put("docx", "txt");
        extensionMap.put("wpd", "txt");
        extensionMap.put("txt", "txt");
        extensionMap.put("html", "txt");
        // Spreadsheet Formats
        extensionMap.put("ods", "tsv");
        extensionMap.put("ots", "tsv");
        extensionMap.put("sxc", "tsv");
        extensionMap.put("xls", "tsv");
        extensionMap.put("xlsx", "tsv");
        extensionMap.put("csv", "tsv");
        extensionMap.put("tsv", "tsv");
        // Presentation Formats
        extensionMap.put("odp", "pdf");
        extensionMap.put("otp", "pdf");
        extensionMap.put("sxi", "pdf");
        extensionMap.put("ppt", "pdf");
        extensionMap.put("pptx", "pdf");
        // Drawing Formats
        extensionMap.put("odg", "svg");
        extensionMap.put("otg", "svg");

        extractorMap.put("pdf", new PdfExtractor());
        extractorMap.put("svg", new XmlExtractor());
    }

    /**
     * Initializes the extractor by starting the office manager.
     *
     * @throws CrawlerSystemException if the office manager is null or fails to start
     */
    @PostConstruct
    public void init() {
        if (officeManager == null) {
            throw new CrawlerSystemException("officeManager is null.");
        }
        try {
            officeManager.start();
        } catch (final OfficeException e) {
            throw new CrawlerSystemException("Failed to start officeManager.", e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(officeManager.getClass().getSimpleName() + " is started.");
        }
    }

    /**
     * Destroys the extractor by stopping the office manager.
     *
     * @throws CrawlerSystemException if the office manager fails to stop
     */
    @PreDestroy
    public void destroy() {
        try {
            officeManager.stop();
        } catch (final OfficeException e) {
            throw new CrawlerSystemException("Failed to stop officeManager.", e);
        }
    }

    /**
     * Adds a conversion rule for mapping input file extensions to output extensions.
     *
     * @param inExt the input file extension
     * @param outExt the output file extension
     */
    public void addConversionRule(final String inExt, final String outExt) {
        extensionMap.put(inExt, outExt);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.extractor.Extractor#getText(java.io.InputStream,
     * java.util.Map)
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        if (in == null) {
            throw new CrawlerSystemException("JODConverter input stream is null. Cannot extract text from null input.");
        }

        final String resourceName = params == null ? null : params.get(ExtractData.RESOURCE_NAME_KEY);

        String extension;
        String filePrefix;
        if (StringUtil.isNotBlank(resourceName)) {
            final String name = getFileName(resourceName);
            final String[] strings = name.split("\\.");
            final StringBuilder buf = new StringBuilder(100);
            if (strings.length > 1) {
                for (int i = 0; i < strings.length - 1; i++) {
                    if (buf.length() != 0) {
                        buf.append('.');
                    }
                    buf.append(strings[i]);
                }
                filePrefix = buf.toString();
                extension = strings[strings.length - 1];
            } else {
                filePrefix = name;
                extension = "";
            }
        } else {
            filePrefix = "none";
            extension = "";
        }
        File inputFile = null;
        File outputFile = null;
        try {
            inputFile =
                    createTempFile("jodextin_" + filePrefix + "_", StringUtil.isNotBlank(extension) ? "." + extension : extension, tempDir);
            final String outExt = getOutputExtension(extension);
            outputFile = createTempFile("cmdextout_" + filePrefix + "_", "." + outExt, tempDir);

            // store to a file
            CopyUtil.copy(in, inputFile);

            LocalConverter.make(officeManager).convert(inputFile).to(outputFile).execute();

            final ExtractData extractData = new ExtractData(getOutputContent(outputFile, outExt));
            if (StringUtil.isNotBlank(resourceName)) {
                extractData.putValues("resourceName", new String[] { resourceName });
            }

            return extractData;
        } catch (final OfficeException e) {
            throw new ExtractException("Could not extract a content.", e);
        } finally {
            FileUtil.deleteInBackground(inputFile);
            FileUtil.deleteInBackground(outputFile);
        }
    }

    /**
     * Gets the output content from the converted file.
     *
     * @param outputFile the converted output file
     * @param outExt the output file extension
     * @return the extracted text content
     * @throws ExtractException if an error occurs while reading the file
     */
    protected String getOutputContent(final File outputFile, final String outExt) {
        final Extractor extractor = getExtractor(outExt);
        if (extractor != null) {
            final Map<String, String> params = new HashMap<>();
            params.put(ExtractData.RESOURCE_NAME_KEY, outputFile.getName());
            try (final FileInputStream in = new FileInputStream(outputFile)) {
                final ExtractData extractData = extractor.getText(in, params);
                return extractData.getContent();
            } catch (final IOException e) {
                throw new ExtractException("Could not open " + outputFile.getAbsolutePath(), e);
            }
        }
        try {
            return new String(FileUtil.readBytes(outputFile), outputEncoding);
        } catch (final UnsupportedEncodingException e) {
            return new String(FileUtil.readBytes(outputFile), Constants.UTF_8_CHARSET);
        }
    }

    /**
     * Gets the extractor for the specified file extension.
     *
     * @param ext the file extension
     * @return the extractor for the extension, or null if not found
     */
    private Extractor getExtractor(final String ext) {
        return extractorMap.get(ext);
    }

    /**
     * Gets the output extension for the specified input extension.
     *
     * @param extension the input file extension
     * @return the output extension, or "txt" if not found
     */
    private String getOutputExtension(final String extension) {
        final String outExt = extensionMap.get(extension);
        return outExt == null ? "txt" : outExt;
    }

    /**
     * Extracts the file name from a resource name.
     *
     * @param resourceName the resource name
     * @return the file name
     */
    private String getFileName(final String resourceName) {
        final String name = resourceName.replaceAll("/+$", "");
        final int pos = name.lastIndexOf('/');
        if (pos >= 0) {
            return name.substring(pos + 1);
        }
        return name;
    }

    /**
     * Sets the office manager for document conversion.
     *
     * @param officeManager the office manager to set
     */
    public void setOfficeManager(final OfficeManager officeManager) {
        this.officeManager = officeManager;
    }

    /**
     * Sets the temporary directory for file operations.
     *
     * @param tempDir the temporary directory to set
     */
    public void setTempDir(final File tempDir) {
        this.tempDir = tempDir;
    }

    /**
     * Sets the output encoding for extracted text.
     *
     * @param outputEncoding the output encoding to set
     */
    public void setOutputEncoding(final String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

}
