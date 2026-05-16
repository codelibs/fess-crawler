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
import org.codelibs.fess.crawler.exception.ExtractException;
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
        // Build a zip whose single SUPPORTED entry exceeds the configured
        // per-entry cap. The extractor must trip the cap before buffering
        // the whole payload. We use a small cap (1 MiB) and a slightly
        // larger payload (2 MiB) so the test stays cheap on parallel /
        // low-memory CI. The extension is .txt so the entry routes through
        // the registered text/plain extractor — only supported entries are
        // buffered (and therefore can hit the per-entry memory cap).
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
            final ZipArchiveEntry entry = new ZipArchiveEntry("big.txt");
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

    // ---------------------------------------------------------------------
    // Unsupported entries must NOT consume the per-entry / total caps —
    // they are skipped without buffering so that supported entries
    // alongside them still extract successfully (regression for PR #161
    // review feedback).
    // ---------------------------------------------------------------------

    @Test
    public void test_zip_unsupportedEntryDoesNotConsumeCaps() throws Exception {
        // A "big.bin" payload that, were it to be buffered, would exceed
        // both the per-entry cap and the total cap. The supported "ok.txt"
        // alongside it must still extract because no extractor is
        // registered for application/octet-stream.
        final byte[] big = new byte[4 * 1024 * 1024];
        final byte[] data = buildZip(new EntrySpec("big.bin", big), new EntrySpec("ok.txt", "good".getBytes(StandardCharsets.UTF_8)));

        zipExtractor.setMaxBytes(64 * 1024); // smaller than big.bin
        zipExtractor.setMaxBytesPerEntry(64 * 1024); // also smaller
        zipExtractor.setMaxContentSize(64 * 1024);
        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = zipExtractor.getText(in, null).getContent();
            assertTrue(content.contains("good"));
        }
    }

    @Test
    public void test_tar_unsupportedEntryDoesNotConsumeCaps() throws Exception {
        final byte[] big = new byte[4 * 1024 * 1024];
        final byte[] data = buildTar(new TarEntrySpec("big.bin", big), new TarEntrySpec("ok.txt", "good".getBytes(StandardCharsets.UTF_8)));

        tarExtractor.setMaxBytes(64 * 1024);
        tarExtractor.setMaxBytesPerEntry(64 * 1024);
        tarExtractor.setMaxContentSize(64 * 1024);
        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = tarExtractor.getText(in, null).getContent();
            assertTrue(content.contains("good"));
        }
    }

    // ---------------------------------------------------------------------
    // maxContentSize is folded into the read budget — a small legacy cap
    // must trip BEFORE the buffer grows to the much larger per-entry cap
    // (regression for PR #161 review feedback).
    // ---------------------------------------------------------------------

    @Test
    public void test_zip_maxContentSize_capsBufferBeforePerEntryCap() throws Exception {
        // 4 MiB supported entry; per-entry cap default is large; legacy
        // maxContentSize is small. Without the fix the buffer would grow
        // up to maxBytesPerEntry+1 before throwing. With the fix the read
        // budget is bounded by maxContentSize+1 so buffering stops early.
        final int legacyCap = 64 * 1024;
        final byte[] payload = new byte[4 * 1024 * 1024];
        final byte[] data = buildZip(new EntrySpec("big.txt", payload));

        zipExtractor.setMaxBytes(-1);
        zipExtractor.setMaxCompressionRatio(-1);
        zipExtractor.setMaxBytesPerEntry(8L * 1024L * 1024L); // intentionally larger than payload
        zipExtractor.setMaxContentSize(legacyCap);
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("Extracted size is"));
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

    // ---------------------------------------------------------------------
    // ZIP signature checks (M1)
    // ---------------------------------------------------------------------

    @Test
    public void test_zip_signatureCheck_rejectsDataDescriptorPrefix() throws Exception {
        // PK\x07\x08 is a data-descriptor signature and must not appear at
        // the start of a valid ZIP; reject it as ExtractException.
        final byte[] data = new byte[] { 'P', 'K', 0x07, 0x08, 0, 0, 0, 0 };
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final ExtractException e) {
            assertTrue(e.getMessage().contains("ZIP"));
        }
    }

    @Test
    public void test_zip_signatureCheck_acceptsEmptyArchive() throws Exception {
        // PK\x05\x06 is a valid empty-archive EOCD; extractor must return
        // empty content rather than throwing.
        final byte[] eocd = new byte[22];
        eocd[0] = 'P';
        eocd[1] = 'K';
        eocd[2] = 0x05;
        eocd[3] = 0x06;
        // Remaining 18 bytes stay 0 (valid minimal EOCD).
        try (InputStream in = new ByteArrayInputStream(eocd)) {
            final String content = zipExtractor.getText(in, null).getContent();
            assertEquals("", content);
        }
    }

    @Test
    public void test_zip_signatureCheck_rejectsTruncatedStream() throws Exception {
        // 2 bytes — not enough for a valid ZIP magic → ExtractException.
        final byte[] data = new byte[] { 'P', 'K' };
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final ExtractException e) {
            assertTrue(e.getMessage().contains("ZIP"));
        }
    }

    @Test
    public void test_zip_signatureCheck_rejectsNonZip() throws Exception {
        // Completely wrong magic.
        final byte[] data = "not a zip file at all".getBytes(StandardCharsets.UTF_8);
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final ExtractException e) {
            assertTrue(e.getMessage().contains("ZIP"));
        }
    }

    // ---------------------------------------------------------------------
    // Overflow: saturating +1L at Long.MAX_VALUE (C2)
    // ---------------------------------------------------------------------

    @Test
    public void test_overflow_saturatingAdd_atLongMaxValue() throws Exception {
        // With maxBytes=Long.MAX_VALUE a small archive must succeed, not
        // silently read 0 bytes due to Long overflow wrapping to negative.
        final byte[] payload = "hello world".getBytes(StandardCharsets.UTF_8);
        final byte[] data = buildZip(new EntrySpec("ok.txt", payload));

        zipExtractor.setMaxBytes(Long.MAX_VALUE);
        zipExtractor.setMaxBytesPerEntry(Long.MAX_VALUE);
        zipExtractor.setMaxContentSize(-1);
        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = zipExtractor.getText(in, null).getContent();
            assertTrue(content.contains("hello world"));
        }
    }

    // ---------------------------------------------------------------------
    // Compression ratio — min(header, measured) (M3)
    // ---------------------------------------------------------------------

    @Test
    public void test_zip_compressionRatio_usesMinOfHeaderAndMeasured() throws Exception {
        // Build a zip where the entry header reports a huge compressedSize.
        // The ratio check must use the minimum of header vs. measured bytes
        // so a lying header cannot suppress the check. We build a highly
        // compressible 2 MiB entry; the measured compressed bytes will be
        // small, making the ratio >> 100 regardless of the header claim.
        final byte[] payload = new byte[2 * 1024 * 1024]; // all zeros

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
            // Set a deliberately inflated compressedSize in the header so
            // that ratio = uncompressed / fakeCompressed would be < threshold.
            // If the code uses min(header, measured) the measured value wins
            // and ratio >> threshold => MaxLengthExceededException fires.
            entry.setCompressedSize(payload.length); // 1:1 — no bomb per header
            entry.setCrc(crc.getValue());
            zos.putArchiveEntry(entry);
            zos.write(payload);
            zos.closeArchiveEntry();
            zos.finish();
        }
        final byte[] data = baos.toByteArray();

        zipExtractor.setMaxBytes(-1);
        zipExtractor.setMaxContentSize(-1);
        // Ratio threshold: 100 — actual ratio >> 100 using measured bytes.
        zipExtractor.setMaxCompressionRatio(100L);
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("compression ratio"));
        }
    }

    // ---------------------------------------------------------------------
    // Nested recursion depth (zip-in-zip) (test 6)
    // ---------------------------------------------------------------------

    @Test
    public void test_zip_nestedRecursionCountsDepth() throws Exception {
        // Build inner zip containing a text file.
        final byte[] innerPayload = buildZip(new EntrySpec("inner.txt", "hello".getBytes(StandardCharsets.UTF_8)));
        // Build outer zip containing the inner zip.
        final byte[] data = buildZip(new EntrySpec("nested.zip", innerPayload));

        // Allow depth=1 only — outer zip processes ok (depth 0→1),
        // inner zip invocation is at depth=1 which == maxArchiveDepth → throws.
        zipExtractor.setMaxArchiveDepth(1);
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().toLowerCase().contains("recursion"));
        }
    }

    // ---------------------------------------------------------------------
    // Per-entry cap fires when total cap disabled (test 7)
    // ---------------------------------------------------------------------

    @Test
    public void test_zip_perEntryCap_whenMaxBytesDisabled() throws Exception {
        final byte[] payload = new byte[2 * 1024 * 1024]; // 2 MiB
        final byte[] data = buildZip(new EntrySpec("big.txt", payload));

        zipExtractor.setMaxBytes(-1);
        zipExtractor.setMaxContentSize(-1);
        zipExtractor.setMaxCompressionRatio(-1);
        zipExtractor.setMaxBytesPerEntry(1024 * 1024); // 1 MiB cap
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("per-entry size exceeded"));
        }
    }

    // ---------------------------------------------------------------------
    // Total cap fires when per-entry cap disabled (test 8)
    // ---------------------------------------------------------------------

    @Test
    public void test_zip_maxBytes_whenPerEntryDisabled() throws Exception {
        final byte[] payload = new byte[64 * 1024]; // 64 KiB each
        final byte[] data = buildZip(new EntrySpec("a.txt", payload), new EntrySpec("b.txt", payload));

        zipExtractor.setMaxBytesPerEntry(-1); // disable per-entry
        zipExtractor.setMaxCompressionRatio(-1);
        zipExtractor.setMaxBytes(64 * 1024); // exactly one entry → second exceeds
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("zip uncompressed size exceeded"));
        }
    }

    // ---------------------------------------------------------------------
    // CP932 filename (m2: use Assumptions instead of silent return)
    // ---------------------------------------------------------------------

    @Test
    public void test_cp932Filename_withAssumption() throws Exception {
        // If MS932 is unavailable, skip with Assumptions rather than a
        // silent return, so the test result is clearly SKIPPED not PASSED.
        org.junit.jupiter.api.Assumptions.assumeTrue(Charset.isSupported("MS932"), "MS932 charset not available on this JVM");

        final Charset cp932 = Charset.forName("MS932");
        final byte[] data = buildZipWithCharset(cp932, new EntrySpec("テスト.txt", "japan".getBytes(StandardCharsets.UTF_8)));

        zipExtractor.setFilenameEncoding("MS932");
        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = zipExtractor.getText(in, null).getContent();
            assertTrue(content.contains("japan"));
        }
    }

    // ---------------------------------------------------------------------
    // Compression-ratio message tightened (m3)
    // ---------------------------------------------------------------------

    @Test
    public void test_compressionRatioExceeded_messageContainsRatio() throws Exception {
        // Same high-ratio archive as the existing test; assert the message
        // specifically contains "compression ratio" (not just "uncompressed
        // size"), because maxBytes=-1 means the total cap is disabled.
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

        zipExtractor.setMaxBytes(-1);
        zipExtractor.setMaxContentSize(-1);
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            // With maxBytes=-1, only the ratio check can fire.
            assertTrue(e.getMessage().contains("compression ratio"));
        }
    }

    // ---------------------------------------------------------------------
    // Tar PAX global header does not consume entry count (m4)
    // ---------------------------------------------------------------------

    @Test
    public void test_tar_paxGlobalHeader_doesNotConsumeEntryCount() throws Exception {
        // Build a tar that, when read by TarArchiveInputStream, produces a
        // PAX global header entry followed by a real text entry.  We use a
        // long filename (>100 chars) with LONGFILE_POSIX mode, which causes
        // Commons Compress to emit a PAX extended header (type 'x') for the
        // long name before the real entry.  isPaxHeader() returns true for
        // type 'x', so the fix must skip it without incrementing entryCount.
        // With maxEntries=1, if the PAX extension header is counted the real
        // entry would push the count to 2 and trigger the cap.
        final String longName = "a".repeat(110) + ".txt"; // > 100-char POSIX limit
        final byte[] content = "hello".getBytes(StandardCharsets.UTF_8);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (TarArchiveOutputStream tos = new TarArchiveOutputStream(baos)) {
            tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
            final TarArchiveEntry real = new TarArchiveEntry(longName);
            real.setSize(content.length);
            tos.putArchiveEntry(real);
            tos.write(content);
            tos.closeArchiveEntry();
            tos.finish();
        }
        final byte[] data = baos.toByteArray();

        tarExtractor.setMaxEntries(1); // only 1 real entry allowed
        try (InputStream in = new ByteArrayInputStream(data)) {
            final String text = tarExtractor.getText(in, null).getContent();
            assertTrue(text.contains("hello"));
        }
    }

    // ---------------------------------------------------------------------
    // Tar per-entry cap enforced (test 13)
    // ---------------------------------------------------------------------

    @Test
    public void test_tar_perEntryCapEnforced() throws Exception {
        final byte[] payload = new byte[2 * 1024 * 1024]; // 2 MiB
        final byte[] data = buildTar(new TarEntrySpec("big.txt", payload));

        tarExtractor.setMaxBytes(-1);
        tarExtractor.setMaxContentSize(-1);
        tarExtractor.setMaxBytesPerEntry(1024 * 1024); // 1 MiB cap
        try (InputStream in = new ByteArrayInputStream(data)) {
            tarExtractor.getText(in, null);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("tar per-entry size exceeded"));
        }
    }

    // ---------------------------------------------------------------------
    // Tar symlink skip now at WARN (m9) — verify it does not throw
    // ---------------------------------------------------------------------

    @Test
    public void test_tar_symlinkSkipped_doesNotThrow() throws Exception {
        // Already covered by test_tar_symlinkSkipped; this confirms the
        // behaviour is unchanged after upgrading the log level to WARN.
        final byte[] data = buildTar(new TarEntrySpec("ok.txt", "regular".getBytes(StandardCharsets.UTF_8)),
                new TarEntrySpec("evil.txt", null, TarArchiveEntry.LF_SYMLINK, "/etc/passwd"));

        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = tarExtractor.getText(in, null).getContent();
            assertTrue(content.contains("regular"));
            assertFalse(content.contains("passwd"));
        }
    }

    // ---------------------------------------------------------------------
    // setMaxArchiveDepth changes threshold (test 16)
    // ---------------------------------------------------------------------

    @Test
    public void test_setMaxArchiveDepth_changesThreshold() throws Exception {
        final byte[] data = buildZip(new EntrySpec("ok.txt", "hi".getBytes(StandardCharsets.UTF_8)));

        // depth=3 at maxArchiveDepth=3 → throws
        zipExtractor.setMaxArchiveDepth(3);
        final Map<String, String> params3 = new HashMap<>();
        params3.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "3");
        try (InputStream in = new ByteArrayInputStream(data)) {
            zipExtractor.getText(in, params3);
            fail();
        } catch (final MaxLengthExceededException e) {
            assertTrue(e.getMessage().toLowerCase().contains("recursion"));
        }

        // depth=11 at maxArchiveDepth=20 → passes
        zipExtractor.setMaxArchiveDepth(20);
        final Map<String, String> params11 = new HashMap<>();
        params11.put(AbstractExtractor.EXTRACTOR_DEPTH_KEY, "11");
        try (InputStream in = new ByteArrayInputStream(data)) {
            final String content = zipExtractor.getText(in, params11).getContent();
            assertTrue(content.contains("hi"));
        }
    }
}
