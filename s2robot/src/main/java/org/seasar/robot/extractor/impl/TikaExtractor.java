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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.extractor.ExtractException;
import org.seasar.robot.extractor.Extractor;
import org.seasar.robot.util.StreamUtil;
import org.xml.sax.SAXException;

/**
 * @author shinsuke
 *
 */
public class TikaExtractor implements Extractor {

    /* (non-Javadoc)
     * @see org.seasar.robot.extractor.Extractor#getText(java.io.InputStream, java.util.Map)
     */
    public ExtractData getText(InputStream in, Map<String, String> params) {
        if (in == null) {
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
                StreamUtil.drain(in, out);
            } finally {
                IOUtils.closeQuietly(out);
            }

            in = new FileInputStream(tempFile);

            try {
                String resourceName = params != null ? params
                        .get(ExtractData.RESOURCE_NAME_KEY) : null;
                String contentType = params != null ? params
                        .get(ExtractData.CONTENT_TYPE) : null;

                Metadata metadata = createMetadata(resourceName, contentType);

                Parser parser = new AutoDetectParser();
                Map<String, Object> context = new HashMap<String, Object>();
                context.put(Parser.class.getName(), parser);

                StringWriter writer = new StringWriter();
                parser.parse(in, new BodyContentHandler(writer), metadata,
                        context);

                String content = normalizeContent(writer);
                if (StringUtil.isBlank(content)) {
                    // retry without a resource name
                    IOUtils.closeQuietly(in);
                    in = new FileInputStream(tempFile);
                    Metadata metadata2 = createMetadata(null, contentType);
                    StringWriter writer2 = new StringWriter();
                    parser.parse(in, new BodyContentHandler(writer2),
                            metadata2, context);
                    content = normalizeContent(writer2);
                }
                ExtractData extractData = new ExtractData(content);

                String[] names = metadata.names();
                Arrays.sort(names);
                for (String name : names) {
                    extractData.putValues(name, metadata.getValues(name));
                }

                return extractData;
            } catch (TikaException e) {
                Throwable cause = e.getCause();
                if (cause instanceof SAXException) {
                    Extractor xmlExtractor = SingletonS2Container
                            .getComponent("xmlExtractor");
                    if (xmlExtractor != null) {
                        IOUtils.closeQuietly(in);
                        in = new FileInputStream(tempFile);
                        return xmlExtractor.getText(in, params);
                    }
                }
                throw e;
            } finally {
                IOUtils.closeQuietly(in);
            }
        } catch (Exception e) {
            throw new ExtractException("Could not extract a content.", e);
        } finally {
            if (tempFile != null && !tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }

    private String normalizeContent(StringWriter writer2) {
        return writer2.toString().replaceAll("\\s+$", " ").trim();
    }

    private Metadata createMetadata(String resourceName, String contentType) {
        Metadata metadata = new Metadata();
        if (StringUtil.isNotEmpty(resourceName)) {
            metadata.set(Metadata.RESOURCE_NAME_KEY, resourceName);
        }
        if (StringUtil.isNotBlank(contentType)) {
            metadata.set(Metadata.CONTENT_TYPE, contentType);
        }
        return metadata;
    }

}
