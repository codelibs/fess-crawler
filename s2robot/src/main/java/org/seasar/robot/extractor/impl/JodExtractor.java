/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.robot.extractor.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.extractor.ExtractException;
import org.seasar.robot.extractor.Extractor;
import org.seasar.robot.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * 
 */
public class JodExtractor implements Extractor {
    private static final Logger logger = LoggerFactory // NOPMD
        .getLogger(JodExtractor.class);

    public OfficeManager officeManager;

    public File tempDir = null;

    public String outputEncoding = Constants.UTF_8;

    private Map<String, String> extensionMap = new HashMap<String, String>();

    private Map<String, Extractor> extractorMap =
        new HashMap<String, Extractor>();

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

    @InitMethod
    public void init() {
        if (officeManager == null) {
            throw new RobotSystemException("officeManager is null.");
        }
        officeManager.start();
    }

    @DestroyMethod
    public void destroy() {
        officeManager.stop();
    }

    public void addConversionRule(String inExt, String outExt) {
        extensionMap.put(inExt, outExt);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.extractor.Extractor#getText(java.io.InputStream,
     * java.util.Map)
     */
    public ExtractData getText(InputStream in, Map<String, String> params) {
        if (in == null) {
            throw new RobotSystemException("in is null.");
        }

        final String resourceName =
            params == null ? null : params.get(ExtractData.RESOURCE_NAME_KEY);

        String extension;
        String filePrefix;
        if (StringUtil.isNotBlank(resourceName)) {
            String name = getFileName(resourceName);
            final String[] strings = name.split("\\.");
            final StringBuilder buf = new StringBuilder();
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
                File.createTempFile(
                    "jodextin_" + filePrefix + "_",
                    StringUtil.isNotBlank(extension) ? "." + extension
                        : extension,
                    tempDir);
            String outExt = getOutputExtension(extension);
            outputFile =
                File.createTempFile("cmdextout_" + filePrefix + "_", "."
                    + outExt, tempDir);

            // store to a file
            StreamUtil.drain(in, inputFile);

            OfficeDocumentConverter converter =
                new OfficeDocumentConverter(officeManager);
            converter.convert(inputFile, outputFile);

            final ExtractData extractData =
                new ExtractData(getOutputContent(outputFile, outExt));
            if (StringUtil.isNotBlank(resourceName)) {
                extractData.putValues(
                    "resourceName",
                    new String[] { resourceName });
            }

            return extractData;
        } catch (IOException e) {
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

    protected String getOutputContent(File outputFile, String outExt)
            throws UnsupportedEncodingException {
        Extractor extractor = getExtractor(outExt);
        if (extractor != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(ExtractData.RESOURCE_NAME_KEY, outputFile.getName());
            FileInputStream in = null;
            try {
                in = new FileInputStream(outputFile);
                ExtractData extractData = extractor.getText(in, params);
                return extractData.getContent();
            } catch (FileNotFoundException e) {
                throw new ExtractException("Could not open "
                    + outputFile.getAbsolutePath(), e);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }
        return new String(FileUtil.getBytes(outputFile), outputEncoding);
    }

    private Extractor getExtractor(String ext) {
        return extractorMap.get(ext);
    }

    /**
     * @param extension
     * @return
     */
    private String getOutputExtension(String extension) {
        String outExt = extensionMap.get(extension);
        return outExt == null ? "txt" : outExt;
    }

    private String getFileName(String resourceName) {
        String name = resourceName.replaceAll("/+$", "");
        int pos = name.lastIndexOf('/');
        if (pos >= 0) {
            return name.substring(pos + 1);
        }
        return name;
    }

}
