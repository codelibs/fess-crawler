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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Security-oriented tests that validate the archive-bomb / recursion / Zip
 * Slip / link-skipping defences added to the Zip / Tar / Lha extractors.
 *
 * <p>
 * Synthetic archives are constructed in-memory with Apache Commons Compress
 * so the tests are fully self-contained.
 * </p>
 */
public class ArchiveExtractorSecurityTest extends PlainTestCase {

    private ZipExtractor zipExtractor;
    private TarExtractor tarExtractor;
    private LhaExtractor lhaExtractor;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        final StandardCrawlerContainer container = new StandardCrawlerContainer();
        container.singleton("archiveStreamFactory", ArchiveStreamFactory.class)
                .singleton("compressorStreamFactory", CompressorStreamFactory.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .singleton("textExtractor", TextExtractor.class)
                .singleton("zipExtractor", ZipExtractor.class)
                .singleton("tarExtractor", TarExtractor.class)
                .singleton("lhaExtractor", LhaExtractor.class)
                .<ExtractorFactory> singleton("extractorFactory", ExtractorFactory.class, factory -> {
                    final TextExtractor textExtractor = container.getComponent("textExtractor");
                    final ZipExtractor zip = container.getComponent("zipExtractor");
                    final TarExtractor tar = container.getComponent("tarExtractor");
                    final LhaExtractor lha = container.getComponent("lhaExtractor");
                    factory.addExtractor("text/plain", textExtractor);
                    factory.addExtractor("application/zip", zip);
                    factory.addExtractor("application/x-tar", tar);
                    factory.addExtractor("application/x-lha", lha);
                });

        zipExtractor = container.getComponent("zipExtractor");
        tarExtractor = container.getComponent("tarExtractor");
        lhaExtractor = container.getComponent("lhaExtractor");
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private byte[] buildZip(final EntrySpec... specs) throws IOException {
        return buildZipWithCharset(StandardCharsets.UTF_8, specs);
    }

    private byte[] buildZipWithCharset(final Charset charset, final EntrySpec... specs) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos)) {
            zos.setEncoding(charset.name());
            // Disable the UTF-8 flag so the encoding parameter is honoured by
            // ZipArchiveInputStream during read.
            zos.setUseLanguageEncodingFlag(false);
            zos.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.NEVER);
            for (final EntrySpec spec : specs) {
                final ZipArchiveEntry entry = new ZipArchiveEntry(spec.name);
                zos.putArchiveEntry(entry);
                if (spec.content != null) {
                    zos.write(spec.content);
                }
                zos.closeArchiveEntry();
            }
            zos.finish();
        }
        return baos.toByteArray();
    }

    private byte[] buildTar(final TarEntrySpec... specs) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (TarArchiveOutputStream tos = new TarArchiveOutputStream(baos)) {
            tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            for (final TarEntrySpec spec : specs) {
                final TarArchiveEntry entry;
                if (spec.linkType != 0) {
                    entry = new TarArchiveEntry(spec.name, spec.linkType);
                    if (spec.linkName != null) {
                        entry.setLinkName(spec.linkName);
                    }
                } else {
                    entry = new TarArchiveEntry(spec.name);
                    entry.setSize(spec.content == null ? 0 : spec.content.length);
                }
                tos.putArchiveEntry(entry);
                if (spec.linkType == 0 && spec.content != null) {
                    tos.write(spec.content);
                }
                tos.closeArchiveEntry();
            }
            tos.finish();
        }
        return baos.toByteArray();
    }

    private static final class EntrySpec {
        final String name;
        final byte[] content;

        EntrySpec(final String name, final byte[] content) {
            this.name = name;
            this.content = content;
        }
    }

    private static final class TarEntrySpec {
        final String name;
        final byte[] content;
        final byte linkType;
        final String linkName;

        TarEntrySpec(final String name, final byte[] content) {
            this(name, content, (byte) 0, null);
        }

        TarEntrySpec(final String name, final byte[] content, final byte linkType, final String linkName) {
            this.name = name;
            this.content = content;
            this.linkType = linkType;
            this.linkName = linkName;
        }
    }

    // ---------------------------------------------------------------------
    // Zip — byte-limit bomb
    // ---------------------------------------------------------------------

    @Test
    public void test_zipBomb_byteLimit() throws Exception {
        final byte[] payload = new byte[64 * 1024];
        final byte[] data = buildZip(new EntrySpec("a.txt", payload), new EntrySpec("b.txt", payload), new EntrySpec("c.txt", payload));

        zipExtractor.setMaxBytes(64 * 1024); // exactly one entry's worth -> 2nd should fail
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("zip uncompressed size exceeded"));
        }
    }

    // ---------------------------------------------------------------------
    // Zip — many-entry bomb
    // ---------------------------------------------------------------------

    @Test
    public void test_zipBomb_entryLimit() throws Exception {
        final EntrySpec[] specs = new EntrySpec[20];
        for (int i = 0; i < specs.length; i++) {
            specs[i] = new EntrySpec("e" + i + ".txt", new byte[0]);
        }
        final byte[] data = buildZip(specs);

        zipExtractor.setMaxEntries(5);
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("zip entry count exceeded"));
        }
    }

    // ---------------------------------------------------------------------
    // Zip — Zip Slip path traversal
    // ---------------------------------------------------------------------

    @Test
    public void test_zipSlip_pathTraversal() throws Exception {
        final byte[] data = buildZip(new EntrySpec("../../etc/passwd", "evil".getBytes(StandardCharsets.UTF_8)),
                new EntrySpec("ok.txt", "good".getBytes(StandardCharsets.UTF_8)));

        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = zipExtractor.getText(in, null).getContent();
            // Bad entry must be skipped; good entry must still be processed.
            assertFalse(content.contains("evil"));
            assertTrue(content.contains("good"));
        }
    }

    @Test
    public void test_zipSlip_absolutePath() throws Exception {
        final byte[] data = buildZip(new EntrySpec("/etc/passwd", "evil".getBytes(StandardCharsets.UTF_8)),
                new EntrySpec("ok.txt", "good".getBytes(StandardCharsets.UTF_8)));

        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = zipExtractor.getText(in, null).getContent();
            assertFalse(content.contains("evil"));
            assertTrue(content.contains("good"));
        }
    }

    // ---------------------------------------------------------------------
    // Recursion-depth bomb
    // ---------------------------------------------------------------------

    @Test
    public void test_recursionDepth_exceeded() throws Exception {
        final byte[] data = buildZip(new EntrySpec("ok.txt", "hello".getBytes(StandardCharsets.UTF_8)));
        final Map<String, String> params = new HashMap<>();
        params.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "10"); // == default max

        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, params);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().toLowerCase().contains("recursion"));
        }
    }

    @Test
    public void test_recursionDepth_belowLimit_succeeds() throws Exception {
        final byte[] data = buildZip(new EntrySpec("ok.txt", "hello".getBytes(StandardCharsets.UTF_8)));
        final Map<String, String> params = new HashMap<>();
        params.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "3");

        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = zipExtractor.getText(in, params).getContent();
            assertTrue(content.contains("hello"));
        }
        // Original params must be unchanged.
        assertEquals("3", params.get(AbstractExtractor.EXTRACTOR_DEPTH_KEY));
    }

    // ---------------------------------------------------------------------
    // CP932 / non-UTF-8 filename encoding
    // ---------------------------------------------------------------------

    @Test
    public void test_cp932Filename() throws Exception {
        final Charset cp932;
        try {
            cp932 = Charset.forName("MS932");
        } catch (final Exception e) {
            // CP932/MS932 not available on this JVM; skip.
            return;
        }

        final byte[] data = buildZipWithCharset(cp932, new EntrySpec("テスト.txt", "japan".getBytes(StandardCharsets.UTF_8)));

        // Default UTF-8 encoding may mojibake the filename, but once we set
        // CP932 the filename should round-trip cleanly. We assert by
        // inspecting the entry list directly via the public API: setting the
        // proper encoding allows the .txt suffix to be detected and the
        // entry's content extracted.
        zipExtractor.setFilenameEncoding("MS932");
        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = zipExtractor.getText(in, null).getContent();
            assertTrue(content.contains("japan"));
        }
    }

    // ---------------------------------------------------------------------
    // Tar — symlink / hardlink entries are skipped
    // ---------------------------------------------------------------------

    @Test
    public void test_tar_symlinkSkipped() throws Exception {
        final byte[] data = buildTar(new TarEntrySpec("ok.txt", "regular".getBytes(StandardCharsets.UTF_8)),
                new TarEntrySpec("evil.txt", null, TarArchiveEntry.LF_SYMLINK, "/etc/passwd"));

        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = tarExtractor.getText(in, null).getContent();
            assertTrue(content.contains("regular"));
            // Symlink target text must NOT leak into the output.
            assertFalse(content.contains("/etc/passwd"));
        }
    }

    @Test
    public void test_tar_hardlinkSkipped() throws Exception {
        final byte[] data = buildTar(new TarEntrySpec("ok.txt", "regular".getBytes(StandardCharsets.UTF_8)),
                new TarEntrySpec("evil.txt", null, TarArchiveEntry.LF_LINK, "ok.txt"));

        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = tarExtractor.getText(in, null).getContent();
            assertTrue(content.contains("regular"));
            // The hardlink should not have introduced a duplicate of the
            // referenced entry's content.
            assertEquals(content.indexOf("regular"), content.lastIndexOf("regular"));
        }
    }

    @Test
    public void test_tar_pathTraversal() throws Exception {
        final byte[] data = buildTar(new TarEntrySpec("../../etc/passwd", "evil".getBytes(StandardCharsets.UTF_8)),
                new TarEntrySpec("ok.txt", "good".getBytes(StandardCharsets.UTF_8)));

        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = tarExtractor.getText(in, null).getContent();
            assertFalse(content.contains("evil"));
            assertTrue(content.contains("good"));
        }
    }

    // ---------------------------------------------------------------------
    // Compression-ratio bomb — produce a highly-compressible big entry
    // ---------------------------------------------------------------------

    @Test
    public void test_compressionRatioExceeded() throws Exception {
        // 2 MiB of zeroes compresses extremely well, well above the 100:1
        // default threshold. Build the entry with explicit method/size/crc so
        // the local file header carries the compressed size (otherwise a
        // streaming DEFLATED entry uses a data descriptor, leaving
        // ZipArchiveEntry#getCompressedSize() as -1 and bypassing the ratio
        // check).
        final byte[] payload = new byte[2 * 1024 * 1024];
        final java.util.zip.Deflater def = new java.util.zip.Deflater(java.util.zip.Deflater.BEST_COMPRESSION);
        def.setInput(payload);
        def.finish();
        final ByteArrayOutputStream compBuf = new ByteArrayOutputStream();
        final byte[] tmpBuf = new byte[8192];
        while (!def.finished()) {
            final int n = def.deflate(tmpBuf);
            compBuf.write(tmpBuf, 0, n);
        }
        def.end();
        final java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(payload);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos)) {
            final ZipArchiveEntry entry = new ZipArchiveEntry("zeros.txt");
            entry.setMethod(ZipArchiveEntry.DEFLATED);
            entry.setSize(payload.length);
            entry.setCompressedSize(compBuf.size());
            entry.setCrc(crc.getValue());
            zos.putArchiveEntry(entry);
            zos.write(payload);
            zos.closeArchiveEntry();
            zos.finish();
        }
        final byte[] data = baos.toByteArray();

        // Disable the byte cap so the compression-ratio check is the one that
        // fires.
        zipExtractor.setMaxBytes(-1);
        zipExtractor.setMaxContentSize(-1);
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("compression ratio") || e.getMessage().contains("uncompressed size"));
        }
    }

    // ---------------------------------------------------------------------
    // Tar byte/entry limits
    // ---------------------------------------------------------------------

    @Test
    public void test_tarBomb_byteLimit() throws Exception {
        final byte[] payload = new byte[64 * 1024];
        final byte[] data = buildTar(new TarEntrySpec("a.txt", payload), new TarEntrySpec("b.txt", payload));

        tarExtractor.setMaxBytes(64 * 1024);
        try (InputStream in = new ByteArrayInputStream(data)) {
            tarExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("tar uncompressed size exceeded"));
        }
    }

    @Test
    public void test_tarBomb_entryLimit() throws Exception {
        final TarEntrySpec[] specs = new TarEntrySpec[20];
        for (int i = 0; i < specs.length; i++) {
            specs[i] = new TarEntrySpec("e" + i + ".txt", new byte[0]);
        }
        final byte[] data = buildTar(specs);

        tarExtractor.setMaxEntries(5);
        try (InputStream in = new ByteArrayInputStream(data)) {
            tarExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("tar entry count exceeded"));
        }
    }

    @Test
    public void test_tar_recursionDepth_exceeded() throws Exception {
        final byte[] data = buildTar(new TarEntrySpec("ok.txt", "hi".getBytes(StandardCharsets.UTF_8)));
        final Map<String, String> params = new HashMap<>();
        params.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "10");

        try (InputStream in = new ByteArrayInputStream(data)) {
            tarExtractor.getText(in, params);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().toLowerCase().contains("recursion"));
        }
    }

    // ---------------------------------------------------------------------
    // Lha recursion-depth check (uses isPathTraversal helper too)
    // ---------------------------------------------------------------------

    // ---------------------------------------------------------------------
    // Per-entry size cap — guards against a single oversized entry
    // ---------------------------------------------------------------------

    @Test
    public void test_perEntryCapEnforced() throws Exception {
        // Build a zip whose single entry exceeds the configured per-entry
        // cap. The extractor must trip the cap before buffering the whole
        // payload. We use a small cap (1 MiB) and a slightly larger payload
        // (2 MiB) so the test stays cheap on parallel/low-memory CI.
        final int perEntryCap = 1024 * 1024;
        final int entrySize = 2 * perEntryCap;
        final byte[] payload = new byte[entrySize];

        final java.util.zip.Deflater def = new java.util.zip.Deflater(java.util.zip.Deflater.BEST_COMPRESSION);
        def.setInput(payload);
        def.finish();
        final ByteArrayOutputStream compBuf = new ByteArrayOutputStream();
        final byte[] tmpBuf = new byte[8192];
        while (!def.finished()) {
            final int n = def.deflate(tmpBuf);
            compBuf.write(tmpBuf, 0, n);
        }
        def.end();
        final java.util.zip.CRC32 crc = new java.util.zip.CRC32();
        crc.update(payload);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(baos)) {
            final ZipArchiveEntry entry = new ZipArchiveEntry("big.bin");
            entry.setMethod(ZipArchiveEntry.DEFLATED);
            entry.setSize(payload.length);
            entry.setCompressedSize(compBuf.size());
            entry.setCrc(crc.getValue());
            zos.putArchiveEntry(entry);
            zos.write(payload);
            zos.closeArchiveEntry();
            zos.finish();
        }
        final byte[] data = baos.toByteArray();

        // Disable the total-size and ratio checks so only the per-entry cap
        // can trigger.
        zipExtractor.setMaxBytes(-1);
        zipExtractor.setMaxContentSize(-1);
        zipExtractor.setMaxCompressionRatio(-1);
        zipExtractor.setMaxBytesPerEntry(perEntryCap);
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("per-entry size exceeded"));
        }
    }

    @Test
    public void test_lha_recursionDepth_exceeded() {
        final Map<String, String> params = new HashMap<>();
        params.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "10");
        // We pass a tiny non-archive stream; the depth check fires before
        // the LHA library is invoked.
        try (InputStream in = new ByteArrayInputStream("dummy".getBytes(StandardCharsets.UTF_8))) {
            lhaExtractor.getText(in, params);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().toLowerCase().contains("recursion"));
        } catch (final IOException e) {
            fail();
        }
    }

    @Test
    public void test_lha_maxInputBytes_capsStaging() {
        // Stage cap is enforced during the temp-file copy, before LhaFile
        // is opened. Any blob larger than the cap must be rejected — we use
        // arbitrary bytes since the failure precedes archive parsing.
        lhaExtractor.setMaxInputBytes(1024L);
        final byte[] payload = new byte[4 * 1024];
        try (InputStream in = new ByteArrayInputStream(payload)) {
            lhaExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("input size exceeded"));
        } catch (final IOException e) {
            fail();
        }
    }
}
