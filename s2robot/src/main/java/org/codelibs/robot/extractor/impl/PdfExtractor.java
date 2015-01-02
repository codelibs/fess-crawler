/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.robot.extractor.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.robot.entity.ExtractData;
import org.codelibs.robot.exception.ExtractException;
import org.codelibs.robot.exception.RobotSystemException;
import org.codelibs.robot.extractor.Extractor;

/**
 * Gets a text from .doc file.
 *
 * @author shinsuke
 *
 */
public class PdfExtractor implements Extractor {
    protected String encoding = "UTF-8";

    /**
     * When true, the parser will skip corrupt pdf objects and will continue
     * parsing at the next object in the file
     */
    protected boolean force = false;

    protected Map<String, String> passwordMap = new HashMap<String, String>();

    protected Object pdfBoxLockObj = new Object();

    protected long timeout = 30000; // 30sec

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.extractor.Extractor#getText(java.io.InputStream,
     * java.util.Map)
     */
    @Override
    public ExtractData getText(final InputStream in,
            final Map<String, String> params) {
        if (in == null) {
            throw new RobotSystemException("The inputstream is null.");
        }
        synchronized (pdfBoxLockObj) {
            PDDocument document = null;
            try {
                document = PDDocument.load(in, null, force);
                if (document.isEncrypted() && params != null) {
                    String password = params.get(ExtractData.PDF_PASSWORD);
                    if (password == null) {
                        password = getPassword(params.get(ExtractData.URL),
                                params.get(TikaMetadataKeys.RESOURCE_NAME_KEY));
                    }
                    if (password != null) {
                        final StandardDecryptionMaterial sdm = new StandardDecryptionMaterial(
                                password);
                        document.openProtection(sdm);
                        final AccessPermission ap = document
                                .getCurrentAccessPermission();

                        if (!ap.canExtractContent()) {
                            throw new IOException(
                                    "You do not have permission to extract text.");
                        }
                    }
                }

                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final Writer output = new OutputStreamWriter(baos, encoding);
                final PDFTextStripper stripper = new PDFTextStripper(encoding);
                stripper.setForceParsing(force);
                final AtomicBoolean done = new AtomicBoolean(false);
                final PDDocument doc = document;
                final Set<Exception> exceptionSet = new HashSet<>();
                final Thread task = new Thread(() -> {
                    try {
                        stripper.writeText(doc, output);
                    } catch (final Exception e) {
                        exceptionSet.add(e);
                    } finally {
                        done.set(true);
                    }
                });
                task.setDaemon(true);
                task.start();
                task.join(timeout);
                if (!done.get()) {
                    for (int i = 0; i < 100 && !done.get(); i++) {
                        task.interrupt();
                        Thread.sleep(50);
                    }
                    throw new ExtractException(
                            "PDFBox process cannot finish in " + timeout
                                    + " sec.");
                } else if (!exceptionSet.isEmpty()) {
                    throw exceptionSet.iterator().next();
                }
                output.flush();
                final ExtractData extractData = new ExtractData(
                        baos.toString(encoding));
                extractMetadata(document, extractData);
                return extractData;
            } catch (final Exception e) {
                throw new ExtractException(e);
            } finally {
                if (document != null) {
                    try {
                        document.close();
                    } catch (final IOException e) {
                        // NOP
                    }
                }
            }
        }
    }

    private void extractMetadata(final PDDocument document,
            final ExtractData extractData) {
        final PDDocumentInformation info = document.getDocumentInformation();
        if (info == null) {
            return;
        }

        for (final String key : info.getMetadataKeys()) {
            final String value = info.getCustomMetadataValue(key);
            addMetadata(extractData, key, value);
        }
    }

    private void addMetadata(final ExtractData extractData, final String name,
            final String value) {
        if (value != null) {
            extractData.putValue(name, value);
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(final boolean force) {
        this.force = force;
    }

    public void addPassword(final String regex, final String password) {
        passwordMap.put(regex, password);
    }

    String getPassword(final String url, final String resourceName) {
        if (passwordMap.isEmpty()) {
            return null;
        }

        String value = null;
        if (StringUtil.isNotEmpty(url)) {
            value = url;
        } else if (StringUtil.isNotEmpty(resourceName)) {
            value = resourceName;
        }

        if (value != null) {
            for (final Map.Entry<String, String> entry : passwordMap.entrySet()) {
                if (value.matches(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }
}
