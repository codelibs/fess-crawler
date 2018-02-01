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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.tika.metadata.TikaMetadataKeys;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class JodExtractor extends AbstractExtractor {
    private static final Logger logger = LoggerFactory
            .getLogger(JodExtractor.class);

    protected OfficeManager officeManager;

    protected File tempDir = null;

    protected String outputEncoding = Constants.UTF_8;

    private final Map<String, String> extensionMap = new HashMap<>();

    private final Map<String, Extractor> extractorMap = new HashMap<>();

    public JodExtractor() {
        extensionMap.put("", "txt");
        // Text Formats
        extensionMap.put("odt", "txt");
        extensionMap.put("sxw", "txt");
        extensionMap.put("rtf", "txt");
        extensionMap.put("doc", "txt");
        extensionMap.put("docx", "pdf");
        extensionMap.put("wpd", "txt");
        // Spreadsheet Formats
        extensionMap.put("ods", "pdf");
        extensionMap.put("sxc", "tsv");
        extensionMap.put("xls", "pdf");
        extensionMap.put("xlsx", "pdf");
        extensionMap.put("csv", "tsv");
        extensionMap.put("tsv", "tsv");
        // Presentation Formats
        extensionMap.put("odp", "pdf");
        extensionMap.put("sxi", "pdf");
        extensionMap.put("ppt", "pdf");
        extensionMap.put("pptx", "pdf");
        // Drawing Formats
        extensionMap.put("odg", "svg");

        extractorMap.put("pdf", new PdfExtractor());
        extractorMap.put("svg", new XmlExtractor());
    }

    @PostConstruct
    public void init() {
        if (officeManager == null) {
            throw new CrawlerSystemException("officeManager is null.");
        }
        officeManager.start();
    }

    @PreDestroy
    public void destroy() {
        officeManager.stop();
    }

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
    public ExtractData getText(final InputStream in,
            final Map<String, String> params) {
        if (in == null) {
            throw new CrawlerSystemException("in is null.");
        }

        final String resourceName = params == null ? null : params
                .get(TikaMetadataKeys.RESOURCE_NAME_KEY);

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
            inputFile = File.createTempFile("jodextin_" + filePrefix + "_",
                    StringUtil.isNotBlank(extension) ? "." + extension
                            : extension, tempDir);
            final String outExt = getOutputExtension(extension);
            outputFile = File.createTempFile("cmdextout_" + filePrefix + "_",
                    "." + outExt, tempDir);

            // store to a file
            CopyUtil.copy(in, inputFile);

            final OfficeDocumentConverter converter = new OfficeDocumentConverter(
                    officeManager);
            converter.convert(inputFile, outputFile);

            final ExtractData extractData = new ExtractData(getOutputContent(
                    outputFile, outExt));
            if (StringUtil.isNotBlank(resourceName)) {
                extractData.putValues("resourceName",
                        new String[] { resourceName });
            }

            return extractData;
        } catch (final IOException e) {
            throw new ExtractException("Could not extract a content.", e);
        } finally {
            if (inputFile != null && !inputFile.delete()) {
                logger.info("Failed to delete " + inputFile.getAbsolutePath());
            }
            if (outputFile != null && !outputFile.delete()) {
                logger.info("Failed to delete " + outputFile.getAbsolutePath());
            }
        }
    }

    protected String getOutputContent(final File outputFile, final String outExt) {
        final Extractor extractor = getExtractor(outExt);
        if (extractor != null) {
            final Map<String, String> params = new HashMap<>();
            params.put(TikaMetadataKeys.RESOURCE_NAME_KEY, outputFile.getName());
            FileInputStream in = null;
            try {
                in = new FileInputStream(outputFile);
                final ExtractData extractData = extractor.getText(in, params);
                return extractData.getContent();
            } catch (final FileNotFoundException e) {
                throw new ExtractException("Could not open "
                        + outputFile.getAbsolutePath(), e);
            } finally {
                CloseableUtil.closeQuietly(in);
            }
        }
        try {
            return new String(FileUtil.readBytes(outputFile), outputEncoding);
        } catch (final UnsupportedEncodingException e) {
            return new String(FileUtil.readBytes(outputFile),
                    Constants.UTF_8_CHARSET);
        }
    }

    private Extractor getExtractor(final String ext) {
        return extractorMap.get(ext);
    }

    /**
     * @param extension
     * @return
     */
    private String getOutputExtension(final String extension) {
        final String outExt = extensionMap.get(extension);
        return outExt == null ? "txt" : outExt;
    }

    private String getFileName(final String resourceName) {
        final String name = resourceName.replaceAll("/+$", "");
        final int pos = name.lastIndexOf('/');
        if (pos >= 0) {
            return name.substring(pos + 1);
        }
        return name;
    }

    public void setOfficeManager(final OfficeManager officeManager) {
        this.officeManager = officeManager;
    }

    public void setTempDir(final File tempDir) {
        this.tempDir = tempDir;
    }

    public void setOutputEncoding(final String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

}
