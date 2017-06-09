/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.Extractor;

/**
 * Gets a text from .doc file.
 *
 * @author shinsuke
 *
 */
public class PdfExtractor implements Extractor {
    protected Object pdfBoxLockObj = new Object();

    protected Map<Pattern, String> passwordMap = new HashMap<>();

    protected long timeout = 30000; // 30sec

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.extractor.Extractor#getText(java.io.InputStream,
     * java.util.Map)
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        if (in == null) {
            throw new CrawlerSystemException("The inputstream is null.");
        }

        synchronized (pdfBoxLockObj) {
            final String password = getPassword(params);
            try (PDDocument document = PDDocument.load(in, password)) {
                final StringWriter output = new StringWriter();
                final PDFTextStripper stripper = new PDFTextStripper();
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
                    throw new ExtractException("PDFBox process cannot finish in " + timeout + " sec.");
                } else if (!exceptionSet.isEmpty()) {
                    throw exceptionSet.iterator().next();
                }
                output.flush();
                final ExtractData extractData = new ExtractData(output.toString());
                extractMetadata(document, extractData);
                return extractData;
            } catch (final Exception e) {
                throw new ExtractException(e);
            }
        }
    }

    private void extractMetadata(final PDDocument document, final ExtractData extractData) {
        final PDDocumentInformation info = document.getDocumentInformation();
        if (info == null) {
            return;
        }

        for (final String key : info.getMetadataKeys()) {
            final String value = info.getCustomMetadataValue(key);
            addMetadata(extractData, key, value);
        }
    }

    private void addMetadata(final ExtractData extractData, final String name, final String value) {
        if (value != null) {
            extractData.putValue(name, value);
        }
    }

    public void addPassword(final String regex, final String password) {
        passwordMap.put(Pattern.compile(regex), password);
    }

    protected String getPassword(final Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return StringUtil.EMPTY;
        }
        final String password = params.get(ExtractData.PDF_PASSWORD);
        if (password == null && !passwordMap.isEmpty()) {
            final String url = params.get(ExtractData.URL);
            final String resourceName = params.get(TikaMetadataKeys.RESOURCE_NAME_KEY);

            String value = null;
            if (StringUtil.isNotEmpty(url)) {
                value = url;
            } else if (StringUtil.isNotEmpty(resourceName)) {
                value = resourceName;
            }

            if (value != null) {
                for (final Map.Entry<Pattern, String> entry : passwordMap.entrySet()) {
                    if (entry.getKey().matcher(value).matches()) {
                        return entry.getValue();
                    }
                }
            }
        }
        return password == null ? StringUtil.EMPTY : password;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }
}
