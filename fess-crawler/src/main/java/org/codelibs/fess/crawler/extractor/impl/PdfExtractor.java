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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

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
 * <p>Text extraction is run on a background worker via an {@link ExecutorService} with a
 * configurable timeout. When a timeout occurs, the worker is cancelled (interrupted) and
 * a short grace period is honoured before the underlying {@link PDDocument} is closed,
 * which avoids the {@code COSStream is closed} race that can otherwise surface as a
 * secondary failure when PDFBox does not honour the interrupt promptly.
 *
 * <p>Features:
 * <ul>
 *   <li>Text extraction from PDF pages</li>
 *   <li>Embedded document extraction</li>
 *   <li>Annotation extraction (file attachments)</li>
 *   <li>Metadata extraction</li>
 *   <li>Password-protected PDF support</li>
 *   <li>Configurable timeout for extraction process</li>
 *   <li>Configurable grace period for orderly cancellation before document close</li>
 * </ul>
 *
 * @author shinsuke
 */
public class PdfExtractor extends PasswordBasedExtractor {
    /** Logger instance for this class. */
    private static final Logger logger = LogManager.getLogger(PdfExtractor.class);

    /** Counter used to give worker threads unique, descriptive names. */
    private static final AtomicLong WORKER_COUNTER = new AtomicLong();

    /**
     * Daemon thread factory used by per-call executors. Daemon threads do not block JVM
     * shutdown, so a runaway PDFBox worker cannot prevent the host process from exiting.
     */
    private static final ThreadFactory DAEMON_THREAD_FACTORY = r -> {
        final Thread t = new Thread(r, "PdfExtractor-worker-" + WORKER_COUNTER.incrementAndGet());
        t.setDaemon(true);
        return t;
    };

    /** Timeout for PDF extraction in milliseconds (default: 30 seconds). */
    protected long timeout = 30000L; // 30sec

    /**
     * Grace period (in milliseconds) to wait after cancellation for the worker thread to
     * stop before the underlying {@link PDDocument} is closed. Default: 2 seconds.
     */
    protected long cancelGracePeriodMs = 2000L;

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
            final PDFTextStripper stripper = createStripper();

            final ExecutorService executor = Executors.newSingleThreadExecutor(DAEMON_THREAD_FACTORY);
            try {
                final Future<?> future = executor.submit(() -> {
                    try {
                        stripper.writeText(document, writer);
                        extractEmbeddedDocuments(document, writer);
                        extractAnnotations(document, writer);
                    } catch (final IOException e) {
                        throw new CrawlerSystemException("Failed to extract PDF text.", e);
                    }
                });
                try {
                    future.get(timeout, TimeUnit.MILLISECONDS);
                } catch (final TimeoutException e) {
                    future.cancel(true);
                    throw new ExtractException("PDFBox process cannot finish in " + timeout + " ms.", e);
                } catch (final ExecutionException e) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if (cause instanceof ExtractException) {
                        throw (ExtractException) cause;
                    }
                    throw new ExtractException("PDF extraction failed.", cause);
                } catch (final InterruptedException e) {
                    future.cancel(true);
                    Thread.currentThread().interrupt();
                    throw new ExtractException("PDF extraction was interrupted.", e);
                }
            } finally {
                // Stop any laggard task and wait briefly so the worker stops touching the
                // PDDocument before try-with-resources closes it. This avoids the
                // "COSStream is closed" secondary failure on cancellation.
                executor.shutdownNow();
                try {
                    if (!executor.awaitTermination(cancelGracePeriodMs, TimeUnit.MILLISECONDS) && logger.isDebugEnabled()) {
                        logger.debug("PdfExtractor worker did not terminate within {} ms grace period.", cancelGracePeriodMs);
                    }
                } catch (final InterruptedException ignore) {
                    Thread.currentThread().interrupt();
                }
            }

            writer.flush();
            final ExtractData extractData = new ExtractData(writer.toString());
            extractMetadata(document, extractData);
            return extractData;
        } catch (final ExtractException e) {
            throw e;
        } catch (final CrawlerSystemException e) {
            throw e;
        } catch (final IOException e) {
            throw new ExtractException("Failed to load PDF.", e);
        } catch (final Exception e) {
            throw new ExtractException(e);
        }
    }

    /**
     * Creates a {@link PDFTextStripper} for the extraction. Subclasses may override this
     * factory method to inject a custom stripper (for example, to simulate slow extraction
     * in tests).
     *
     * @return a newly created text stripper
     * @throws IOException if the stripper cannot be constructed
     */
    protected PDFTextStripper createStripper() throws IOException {
        return new PDFTextStripper();
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
     * Gets the grace period (in milliseconds) used after cancellation, before the
     * underlying {@link PDDocument} is closed.
     * @return the grace period in milliseconds
     */
    public long getCancelGracePeriodMs() {
        return cancelGracePeriodMs;
    }

    /**
     * Sets the grace period (in milliseconds) used after cancellation, before the
     * underlying {@link PDDocument} is closed. The default is 2000 ms.
     * @param cancelGracePeriodMs the grace period in milliseconds; must be non-negative
     */
    public void setCancelGracePeriodMs(final long cancelGracePeriodMs) {
        this.cancelGracePeriodMs = cancelGracePeriodMs;
    }

    /**
     * Sets whether the extraction thread should be a daemon thread.
     *
     * <p>Retained for backwards compatibility. The {@link ExecutorService}-based
     * implementation always uses daemon worker threads, so this setter is effectively a
     * no-op.
     *
     * @param isDaemonThread ignored; daemon worker threads are always used
     * @deprecated Worker threads are always daemon threads now; this setter has no effect.
     */
    @Deprecated
    public void setDaemonThread(final boolean isDaemonThread) {
        // no-op: daemon worker threads are always used
    }
}
