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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
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
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

/**
 * PdfExtractor extracts text content from PDF files using Apache PDFBox.
 * It supports password-protected PDFs and can extract embedded documents and annotations.
 *
 * <p>The extractor runs text extraction in a separate thread with a configurable timeout
 * to prevent hanging on problematic PDF files. It also extracts metadata from the PDF
 * document and includes it in the extraction result.
 *
 * <p>Features:
 * <ul>
 *   <li>Text extraction from PDF pages</li>
 *   <li>Embedded document extraction</li>
 *   <li>Annotation extraction (file attachments)</li>
 *   <li>Metadata extraction</li>
 *   <li>Password-protected PDF support</li>
 *   <li>Configurable timeout for extraction process</li>
 * </ul>
 *
 * @author shinsuke
 */
public class PdfExtractor extends PasswordBasedExtractor {
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(PdfExtractor.class);

    /** Timeout for PDF extraction in milliseconds (default: 30 seconds). */
    protected long timeout = 30000; // 30sec

    /** Whether the extraction thread should be a daemon thread. */
    protected boolean isDaemonThread = false;

    /**
     * Creates a new PdfExtractor instance.
     */
    public PdfExtractor() {
        super();
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
            throw new CrawlerSystemException("PDF input stream is null. Cannot extract text from null input.");
        }

        final String password = getPassword(params);
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(in), password)) {
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
            }
            if (!exceptionSet.isEmpty()) {
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

    /**
     * Extracts text from file attachments in PDF annotations.
     * @param doc the PDF document
     * @param writer the writer to append extracted text to
     */
    protected void extractAnnotations(final PDDocument doc, final StringWriter writer) {
        for (final PDPage page : doc.getPages()) {
            try {
                for (final PDAnnotation annotation : page.getAnnotations()) {
                    if (annotation instanceof final PDAnnotationFileAttachment annotationFileAttachment) {
                        final PDFileSpecification fileSpec = annotationFileAttachment.getFile();
                        if (fileSpec instanceof final PDComplexFileSpecification complexFileSpec) {
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

    /**
     * Extracts text from an embedded file using the appropriate extractor.
     * @param filename the filename of the embedded file
     * @param embeddedFile the embedded file to extract text from
     * @param writer the writer to append extracted text to
     */
    protected void extractFile(final String filename, final PDEmbeddedFile embeddedFile, final StringWriter writer) {
        final MimeTypeHelper mimeTypeHelper = getMimeTypeHelper();
        final ExtractorFactory extractorFactory = getExtractorFactory();
        final String mimeType = mimeTypeHelper.getContentType(null, filename);
        if (mimeType != null) {
            final Extractor extractor = extractorFactory.getExtractor(mimeType);
            if (extractor != null) {
                try (COSInputStream is = embeddedFile.createInputStream()) {
                    final Map<String, String> map = new HashMap<>();
                    map.put(ExtractData.RESOURCE_NAME_KEY, filename);
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

    /**
     * Extracts text from embedded documents in the PDF.
     * @param document the PDF document
     * @param writer the writer to append extracted text to
     */
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

    /**
     * Processes embedded document names and extracts text from them.
     * @param embeddedFileNames the map of embedded file names to specifications
     * @param writer the writer to append extracted text to
     */
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

    /**
     * Gets the embedded file from a file specification, trying different platform-specific variants.
     * @param fileSpec the file specification
     * @return the embedded file, or null if not found
     */
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

    /**
     * Extracts metadata from the PDF document and adds it to the extraction result.
     * @param document the PDF document
     * @param extractData the extraction data to add metadata to
     */
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

    /**
     * Adds metadata to the extraction data if the value is not null.
     * @param extractData the extraction data
     * @param name the metadata name
     * @param value the metadata value
     */
    protected void addMetadata(final ExtractData extractData, final String name, final String value) {
        if (value != null) {
            extractData.putValue(name, value);
        }
    }

    /**
     * Gets the timeout for PDF extraction in milliseconds.
     * @return the timeout in milliseconds
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout for PDF extraction in milliseconds.
     * @param timeout the timeout in milliseconds
     */
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    /**
     * Sets whether the extraction thread should be a daemon thread.
     * @param isDaemonThread true to make it a daemon thread, false otherwise.
     */
    public void setDaemonThread(final boolean isDaemonThread) {
        this.isDaemonThread = isDaemonThread;
    }
}
