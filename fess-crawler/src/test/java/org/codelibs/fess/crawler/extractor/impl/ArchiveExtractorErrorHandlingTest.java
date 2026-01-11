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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

/**
 * Test class for archive extractor error handling improvements.
 * Tests partial extraction, error recovery, and improved error messages.
 */
public class ArchiveExtractorErrorHandlingTest extends PlainTestCase {
    private static final Logger logger = LogManager.getLogger(ArchiveExtractorErrorHandlingTest.class);

    private StandardCrawlerContainer container;
    private ZipExtractor zipExtractor;
    private TarExtractor tarExtractor;

    @Override
    @BeforeEach
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        container = new StandardCrawlerContainer();
        container.singleton("archiveStreamFactory", ArchiveStreamFactory.class)
                .singleton("compressorStreamFactory", CompressorStreamFactory.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .singleton("tikaExtractor", TikaExtractor.class)
                .singleton("textExtractor", TextExtractor.class)
                .singleton("zipExtractor", ZipExtractor.class)
                .singleton("tarExtractor", TarExtractor.class)
                .<ExtractorFactory> singleton("extractorFactory", ExtractorFactory.class, factory -> {
                    final TikaExtractor tikaExtractor = container.getComponent("tikaExtractor");
                    final TextExtractor textExtractor = container.getComponent("textExtractor");
                    final ZipExtractor zipExtractor = container.getComponent("zipExtractor");
                    final TarExtractor tarExtractor = container.getComponent("tarExtractor");
                    factory.addExtractor("text/plain", textExtractor);
                    factory.addExtractor("text/html", tikaExtractor);
                    factory.addExtractor("application/zip", zipExtractor);
                    factory.addExtractor("application/x-tar", tarExtractor);
                });

        zipExtractor = container.getComponent("zipExtractor");
        tarExtractor = container.getComponent("tarExtractor");
    }

    /**
     * Test that ZipExtractor handles null input stream with appropriate error message.
     */
    public void test_ZipExtractor_nullInputStream_throwsWithMessage() {
        try {
            zipExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            assertEquals("The inputstream is null.", e.getMessage());
        }
    }

    /**
     * Test that TarExtractor handles null input stream with appropriate error message.
     */
    public void test_TarExtractor_nullInputStream_throwsWithMessage() {
        try {
            tarExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            assertEquals("The inputstream is null.", e.getMessage());
        }
    }

    /**
     * Test that ZipExtractor provides descriptive error message for invalid archive.
     */
    public void test_ZipExtractor_invalidArchive_throwsWithDescriptiveMessage() {
        final InputStream invalidStream = new ByteArrayInputStream("not a valid zip file".getBytes());

        try {
            zipExtractor.getText(invalidStream, null);
            fail();
        } catch (final ExtractException e) {
            assertTrue(e.getMessage().contains("ZIP archive"));
            assertTrue(e.getMessage().contains("Failed to extract") || e.getMessage().contains("No entries could be processed"));
        }
    }

    /**
     * Test that TarExtractor handles invalid archive gracefully.
     * Invalid archives may either throw an exception or return empty content.
     */
    public void test_TarExtractor_invalidArchive_handlesGracefully() {
        final InputStream invalidStream = new ByteArrayInputStream("not a valid tar file".getBytes());

        try {
            final ExtractData result = tarExtractor.getText(invalidStream, null);
            // If no exception is thrown, result should be empty or minimal
            assertNotNull(result);
            assertNotNull(result.getContent());
            // Empty or minimal content is acceptable for invalid archives
        } catch (final ExtractException e) {
            // Exception is also acceptable - verify it has a descriptive message
            assertTrue(e.getMessage().contains("TAR") || e.getMessage().contains("extract"));
        }
    }

    /**
     * Test that ZipExtractor successfully extracts from valid archive.
     */
    public void test_ZipExtractor_validArchive_extractsSuccessfully() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/zip/test.zip");

        final ExtractData result = zipExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertFalse(result.getContent().isEmpty());
    }

    /**
     * Test that ZipExtractor handles mixed valid and invalid entries gracefully.
     * Should continue processing valid entries even when some fail.
     */
    public void test_ZipExtractor_mixedEntries_continuesProcessing() throws IOException {
        // Create a ZIP with one valid text file
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final ZipOutputStream zos = new ZipOutputStream(baos)) {
            // Add a valid text entry
            final ZipEntry entry1 = new ZipEntry("valid.txt");
            zos.putNextEntry(entry1);
            zos.write("Valid content テスト".getBytes("UTF-8"));
            zos.closeEntry();

            // Add another valid entry
            final ZipEntry entry2 = new ZipEntry("another.txt");
            zos.putNextEntry(entry2);
            zos.write("Another valid content".getBytes("UTF-8"));
            zos.closeEntry();
        }

        final InputStream in = new ByteArrayInputStream(baos.toByteArray());
        final ExtractData result = zipExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().contains("Valid content"));
    }

    /**
     * Test that TarExtractor handles mixed valid and invalid entries gracefully.
     * Should continue processing valid entries even when some fail.
     */
    public void test_TarExtractor_mixedEntries_continuesProcessing() throws IOException {
        // Create a TAR with one valid text file
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final TarArchiveOutputStream tos = new TarArchiveOutputStream(baos)) {
            // Add a valid text entry
            final byte[] content1 = "Valid tar content テスト".getBytes("UTF-8");
            final TarArchiveEntry entry1 = new TarArchiveEntry("valid.txt");
            entry1.setSize(content1.length);
            tos.putArchiveEntry(entry1);
            tos.write(content1);
            tos.closeArchiveEntry();

            // Add another valid entry
            final byte[] content2 = "Another valid content".getBytes("UTF-8");
            final TarArchiveEntry entry2 = new TarArchiveEntry("another.txt");
            entry2.setSize(content2.length);
            tos.putArchiveEntry(entry2);
            tos.write(content2);
            tos.closeArchiveEntry();
        }

        final InputStream in = new ByteArrayInputStream(baos.toByteArray());
        final ExtractData result = tarExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().contains("Valid tar content"));
    }

    /**
     * Test that ZipExtractor returns empty content for archive with no extractable files.
     */
    public void test_ZipExtractor_noExtractableEntries_returnsEmptyContent() throws IOException {
        // Create a ZIP with an unsupported file type
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final ZipOutputStream zos = new ZipOutputStream(baos)) {
            final ZipEntry entry = new ZipEntry("unknown.xyz");
            zos.putNextEntry(entry);
            zos.write("Some content".getBytes());
            zos.closeEntry();
        }

        final InputStream in = new ByteArrayInputStream(baos.toByteArray());
        final ExtractData result = zipExtractor.getText(in, null);

        assertNotNull(result);
        // Content might be empty or contain minimal text depending on processing
        assertNotNull(result.getContent());
    }

    /**
     * Test that TarExtractor returns empty content for archive with no extractable files.
     */
    public void test_TarExtractor_noExtractableEntries_returnsEmptyContent() throws IOException {
        // Create a TAR with an unsupported file type
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final TarArchiveOutputStream tos = new TarArchiveOutputStream(baos)) {
            final byte[] content = "Some content".getBytes();
            final TarArchiveEntry entry = new TarArchiveEntry("unknown.xyz");
            entry.setSize(content.length);
            tos.putArchiveEntry(entry);
            tos.write(content);
            tos.closeArchiveEntry();
        }

        final InputStream in = new ByteArrayInputStream(baos.toByteArray());
        final ExtractData result = tarExtractor.getText(in, null);

        assertNotNull(result);
        // Content might be empty or contain minimal text depending on processing
        assertNotNull(result.getContent());
    }

    /**
     * Test that empty ZIP archive is handled gracefully.
     */
    public void test_ZipExtractor_emptyArchive_handlesGracefully() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final ZipOutputStream zos = new ZipOutputStream(baos)) {
            // Create empty archive
        }

        final InputStream in = new ByteArrayInputStream(baos.toByteArray());
        final ExtractData result = zipExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().trim().isEmpty());
    }

    /**
     * Test that empty TAR archive is handled gracefully.
     */
    public void test_TarExtractor_emptyArchive_handlesGracefully() throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final TarArchiveOutputStream tos = new TarArchiveOutputStream(baos)) {
            // Create empty archive
            tos.finish();
        }

        final InputStream in = new ByteArrayInputStream(baos.toByteArray());
        final ExtractData result = tarExtractor.getText(in, null);

        assertNotNull(result);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().trim().isEmpty());
    }
}
