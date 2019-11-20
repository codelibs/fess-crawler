/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gets a text from .doc file.
 *
 * @author shinsuke
 *
 */
public class PdfExtractor extends PasswordBasedExtractor {
    private static final Logger logger = LoggerFactory.getLogger(PdfExtractor.class);

    protected long timeout = 30000; // 30sec

    protected boolean isDaemonThread = false;

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

        final String password = getPassword(params);
        try (PDDocument document = PDDocument.load(in, password == null ? null : password)) {
            final StringWriter writer = new StringWriter();
            final PDFTextStripper stripper = new PDFTextStripper();
            final AtomicBoolean done = new AtomicBoolean(false);
            final PDDocument doc = document;
            final Set<Exception> exceptionSet = new HashSet<>();
            final Thread task = new Thread(() -> {
                try {
                    stripper.writeText(doc, writer);
                    extractEmbeddedDocuments(doc, writer);
                    extractAnnotations(doc, writer);
                } catch (final Exception e) {
                    exceptionSet.add(e);
                } finally {
                    done.set(true);
                }
            }, Thread.currentThread().getName() + "-pdf");
            task.setDaemon(isDaemonThread);
            task.start();
            task.join(timeout);
            if (!done.get()) {
                for (int i = 0; i < 100 && !done.get(); i++) {
                    task.interrupt();
                    ThreadUtil.sleep(100L);
                }
                throw new ExtractException("PDFBox process cannot finish in " + timeout + " sec.");
            } else if (!exceptionSet.isEmpty()) {
                throw exceptionSet.iterator().next();
            }
            writer.flush();
            final ExtractData extractData = new ExtractData(writer.toString());
            extractMetadata(document, extractData);
            return extractData;
        } catch (final Exception e) {
            throw new ExtractException(e);
        }
    }

    protected void extractAnnotations(final PDDocument doc, final StringWriter writer) {
        for (final PDPage page : doc.getPages()) {
            try {
                for (final PDAnnotation annotation : page.getAnnotations()) {
                    if (annotation instanceof PDAnnotationFileAttachment) {
                        final PDAnnotationFileAttachment annotationFileAttachment = (PDAnnotationFileAttachment) annotation;
                        final PDFileSpecification fileSpec = annotationFileAttachment.getFile();
                        if (fileSpec instanceof PDComplexFileSpecification) {
                            final PDComplexFileSpecification complexFileSpec = (PDComplexFileSpecification) fileSpec;
                            final PDEmbeddedFile embeddedFile = getEmbeddedFile(complexFileSpec);
                            extractFile(complexFileSpec.getFilename(), embeddedFile, writer);
                        }
                    }
                }
            } catch (final IOException e) {
                logger.warn("Failed to parse annotation.", e);
            }
        }
    }

    protected void extractFile(final String filename, final PDEmbeddedFile embeddedFile, final StringWriter writer) {
        final MimeTypeHelper mimeTypeHelper = getMimeTypeHelper();
        final ExtractorFactory extractorFactory = getExtractorFactory();
        final String mimeType = mimeTypeHelper.getContentType(null, filename);
        if (mimeType != null) {
            final Extractor extractor = extractorFactory.getExtractor(mimeType);
            if (extractor != null) {
                try (COSInputStream is = embeddedFile.createInputStream()) {
                    final Map<String, String> map = new HashMap<>();
                    map.put(TikaMetadataKeys.RESOURCE_NAME_KEY, filename);
                    final String content = extractor.getText(is, map).getContent();
                    writer.write(content);
                    writer.write('\n');
                } catch (final Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Exception in an internal extractor.", e);
                    }
                }
            }
        }
    }

    protected void extractEmbeddedDocuments(final PDDocument document, final StringWriter writer) {
        final PDDocumentNameDictionary namesDictionary = new PDDocumentNameDictionary(document.getDocumentCatalog());
        final PDEmbeddedFilesNameTreeNode efTree = namesDictionary.getEmbeddedFiles();
        if (efTree == null) {
            return;
        }

        try {
            final Map<String, PDComplexFileSpecification> embeddedFileNames = efTree.getNames();
            if (embeddedFileNames != null) {
                processEmbeddedDocNames(embeddedFileNames, writer);
            } else {
                final List<PDNameTreeNode<PDComplexFileSpecification>> kids = efTree.getKids();
                if (kids == null) {
                    return;
                }
                for (final PDNameTreeNode<PDComplexFileSpecification> node : kids) {
                    processEmbeddedDocNames(node.getNames(), writer);
                }
            }
        } catch (final IOException e) {
            logger.warn("Failed to parse embedded documents.", e);
        }
    }

    protected void processEmbeddedDocNames(final Map<String, PDComplexFileSpecification> embeddedFileNames, final StringWriter writer) {
        if (embeddedFileNames == null || embeddedFileNames.isEmpty()) {
            return;
        }

        for (final Map.Entry<String, PDComplexFileSpecification> ent : embeddedFileNames.entrySet()) {
            final PDComplexFileSpecification spec = ent.getValue();
            if (spec != null) {
                final PDEmbeddedFile embeddedFile = getEmbeddedFile(spec);
                extractFile(ent.getKey(), embeddedFile, writer);
            }
        }
    }

    protected PDEmbeddedFile getEmbeddedFile(final PDComplexFileSpecification fileSpec) {
        // search for the first available alternative of the embedded file
        PDEmbeddedFile embeddedFile = null;
        if (fileSpec != null) {
            embeddedFile = fileSpec.getEmbeddedFileUnicode();
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileDos();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileMac();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFileUnix();
            }
            if (embeddedFile == null) {
                embeddedFile = fileSpec.getEmbeddedFile();
            }
        }
        return embeddedFile;
    }

    protected void extractMetadata(final PDDocument document, final ExtractData extractData) {
        final PDDocumentInformation info = document.getDocumentInformation();
        if (info == null) {
            return;
        }

        for (final String key : info.getMetadataKeys()) {
            final String value = info.getCustomMetadataValue(key);
            addMetadata(extractData, key, value);
        }
    }

    protected void addMetadata(final ExtractData extractData, final String name, final String value) {
        if (value != null) {
            extractData.putValue(name, value);
        }
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    public void setDaemonThread(final boolean isDaemonThread) {
        this.isDaemonThread = isDaemonThread;
    }
}
