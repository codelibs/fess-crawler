/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.SecureContentHandler;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.extractor.ExtractException;
import org.seasar.robot.extractor.Extractor;
import org.seasar.robot.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author shinsuke
 * 
 */
public class TikaExtractor implements Extractor {
    private static final Logger logger = LoggerFactory // NOPMD
        .getLogger(TikaExtractor.class);

    public String outputEncoding = Constants.UTF_8;

    public boolean readAsTextIfFailed = true;

    public long maxCompressionRatio = 100;

    protected Map<String, String> pdfPasswordMap =
        new HashMap<String, String>();

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.extractor.Extractor#getText(java.io.InputStream,
     * java.util.Map)
     */
    public ExtractData getText(final InputStream inputStream,
            final Map<String, String> params) {
        if (inputStream == null) {
            throw new RobotSystemException("The inputstream is null.");
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("tikaExtractor-", ".out");
        } catch (IOException e) {
            throw new ExtractException("Could not create a temp file.", e);
        }

        try {
            OutputStream out = null;
            try {
                out = new FileOutputStream(tempFile);
                StreamUtil.drain(inputStream, out);
            } finally {
                IOUtils.closeQuietly(out);
            }

            InputStream in = new FileInputStream(tempFile);

            final PrintStream originalOutStream = System.out;
            final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outStream, true));
            final PrintStream originalErrStream = System.err;
            final ByteArrayOutputStream errStream = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errStream, true));
            try {
                final String resourceName =
                    params == null ? null : params
                        .get(ExtractData.RESOURCE_NAME_KEY);
                final String contentType =
                    params == null ? null : params
                        .get(ExtractData.CONTENT_TYPE);
                String contentEncoding =
                    params == null ? null : params
                        .get(ExtractData.CONTENT_ENCODING);

                final Metadata metadata =
                    createMetadata(resourceName, contentType, contentEncoding);

                // password for pdf
                String pdfPassword =
                    params == null ? null : params
                        .get(ExtractData.PDF_PASSWORD);
                if (pdfPassword == null && params != null) {
                    pdfPassword =
                        getPdfPassword(
                            params.get(ExtractData.URL),
                            resourceName);
                }
                if (pdfPassword != null) {
                    metadata.add(ExtractData.PDF_PASSWORD, pdfPassword);
                }

                final Parser parser = new DetectParser();
                final ParseContext parseContext = new ParseContext();
                parseContext.set(Parser.class, parser);

                final StringWriter writer = new StringWriter();
                parser.parse(
                    in,
                    new BodyContentHandler(writer),
                    metadata,
                    parseContext);

                String content = normalizeContent(writer);
                if (StringUtil.isBlank(content)) {
                    if (resourceName != null) {
                        // retry without a resource name
                        IOUtils.closeQuietly(in);
                        in = new FileInputStream(tempFile);
                        final Metadata metadata2 =
                            createMetadata(null, contentType, contentEncoding);
                        final StringWriter writer2 = new StringWriter();
                        parser.parse(
                            in,
                            new BodyContentHandler(writer2),
                            metadata2,
                            parseContext);
                        content = normalizeContent(writer2);
                    }
                    if (StringUtil.isBlank(content) && contentType != null) {
                        // retry without a content type
                        IOUtils.closeQuietly(in);
                        in = new FileInputStream(tempFile);
                        final Metadata metadata3 =
                            createMetadata(null, null, contentEncoding);
                        final StringWriter writer3 = new StringWriter();
                        parser.parse(
                            in,
                            new BodyContentHandler(writer3),
                            metadata3,
                            parseContext);
                        content = normalizeContent(writer3);
                    }

                    if (readAsTextIfFailed && StringUtil.isBlank(content)) {
                        IOUtils.closeQuietly(in);
                        if (contentEncoding == null) {
                            contentEncoding = Constants.UTF_8;
                        }
                        BufferedReader br = null;
                        try {
                            br =
                                new BufferedReader(new InputStreamReader(
                                    new FileInputStream(tempFile),
                                    contentEncoding));
                            final StringWriter writer4 = new StringWriter();
                            String line;
                            while ((line = br.readLine()) != null) {
                                writer4.write(line
                                    .replaceAll("\\p{Cntrl}", " ")
                                    .replaceAll("\\s+", " ")
                                    .trim());
                                writer4.write(' ');
                            }
                            content = writer4.toString().trim();
                        } catch (Exception e) {
                            logger.warn(
                                "Could not read " + tempFile.getAbsolutePath(),
                                e);
                        } finally {
                            IOUtils.closeQuietly(br);
                        }
                    }
                }
                final ExtractData extractData = new ExtractData(content);

                final String[] names = metadata.names();
                Arrays.sort(names);
                for (String name : names) {
                    extractData.putValues(name, metadata.getValues(name));
                }

                return extractData;
            } catch (TikaException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof SAXException) {
                    final Extractor xmlExtractor =
                        SingletonS2Container.getComponent("xmlExtractor");
                    if (xmlExtractor != null) {
                        IOUtils.closeQuietly(in);
                        in = new FileInputStream(tempFile);
                        return xmlExtractor.getText(in, params);
                    }
                }
                throw e;
            } finally {
                IOUtils.closeQuietly(in);
                if (originalOutStream != null) {
                    System.setOut(originalOutStream);
                }
                if (originalErrStream != null) {
                    System.setErr(originalErrStream);
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
                } catch (Exception e) {
                    // NOP
                }
            }
        } catch (Exception e) {
            throw new ExtractException("Could not extract a content.", e);
        } finally {
            if (tempFile != null && !tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }

    private String normalizeContent(final StringWriter writer) {
        return writer.toString().replaceAll("\\s+", " ").trim();
    }

    String getPdfPassword(String url, String resourceName) {
        if (pdfPasswordMap.size() == 0) {
            return null;
        }

        String value = null;
        if (StringUtil.isNotEmpty(url)) {
            value = url;
        } else if (StringUtil.isNotEmpty(resourceName)) {
            value = resourceName;
        }

        if (value != null) {
            for (Map.Entry<String, String> entry : pdfPasswordMap.entrySet()) {
                if (value.matches(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    private Metadata createMetadata(final String resourceName,
            final String contentType, final String contentEncoding) {
        final Metadata metadata = new Metadata();
        if (StringUtil.isNotEmpty(resourceName)) {
            metadata.set(Metadata.RESOURCE_NAME_KEY, resourceName);
        }
        if (StringUtil.isNotBlank(contentType)) {
            metadata.set(Metadata.CONTENT_TYPE, contentType);
        }
        if (StringUtil.isNotBlank(contentEncoding)) {
            metadata.set(Metadata.CONTENT_ENCODING, contentEncoding);
        }
        return metadata;
    }

    public void addPdfPassword(String regex, String password) {
        pdfPasswordMap.put(regex, password);
    }

    // workaround: Tika does not have extention points.
    protected class DetectParser extends CompositeParser {

        /**
         * The type detector used by this parser to auto-detect the type of a
         * document.
         */
        private Detector detector; // always set in the constructor

        /**
         * Creates an auto-detecting parser instance using the default Tika
         * configuration.
         */
        public DetectParser() {
            this(TikaConfig.getDefaultConfig());
        }

        public DetectParser(TikaConfig config) {
            super(config.getMediaTypeRegistry(), config.getParser());
            detector = config.getDetector();
        }

        public void parse(InputStream stream, ContentHandler handler,
                Metadata metadata, ParseContext context) throws IOException,
                SAXException, TikaException {
            TemporaryResources tmp = new TemporaryResources();
            try {
                TikaInputStream tis = TikaInputStream.get(stream, tmp);

                // Automatically detect the MIME type of the document
                MediaType type = detector.detect(tis, metadata);
                metadata.set(Metadata.CONTENT_TYPE, type.toString());

                // TIKA-216: Zip bomb prevention
                SecureContentHandler sch =
                    new SecureContentHandler(handler, tis);

                sch.setMaximumCompressionRatio(maxCompressionRatio);

                try {
                    // Parse the document
                    super.parse(tis, sch, metadata, context);
                } catch (SAXException e) {
                    // Convert zip bomb exceptions to TikaExceptions
                    sch.throwIfCauseOf(e);
                    throw e;
                }
            } finally {
                tmp.dispose();
            }
        }

        public void parse(InputStream stream, ContentHandler handler,
                Metadata metadata) throws IOException, SAXException,
                TikaException {
            ParseContext context = new ParseContext();
            context.set(Parser.class, this);
            parse(stream, handler, metadata, context);
        }

    }

}
