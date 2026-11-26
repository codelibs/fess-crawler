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
package org.codelibs.fess.crawler.client.smb;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.codelibs.jcifs.smb.impl.SmbException;
import org.codelibs.jcifs.smb.impl.SmbFile;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Tests the last modified time that {@link SmbClient} gives for a file on a real Windows (NTFS) share.
 * <p>
 * NTFS moves the change time of a file, and not its last write time, when only its metadata changes. The test
 * changes a file on the disk of the share, so it runs only on the machine that hosts the share: the GitHub
 * Actions workflow {@code smb-windows.yml} creates the share on a {@code windows-latest} runner and sets the
 * {@code FESS_CRAWLER_SMB_WINDOWS_*} environment variables. Without them the tests are skipped, unless
 * {@code FESS_CRAWLER_SMB_WINDOWS_REQUIRED} is {@code true}, when they fail.
 * </p>
 */
public class SmbClientWindowsTest extends PlainTestCase {

    private static final Logger logger = LogManager.getLogger(SmbClientWindowsTest.class);

    private static final String ENV_PREFIX = "FESS_CRAWLER_SMB_WINDOWS_";

    /** Longer than the resolution any server gives a time in, so that a moved time is told from an unmoved one. */
    private static final long WAIT_MILLIS = 1500L;

    /** Slack for the clock of the server against the clock of the test; both run on the same machine. */
    private static final long CLOCK_TOLERANCE_MILLIS = 2000L;

    /** The authenticated users (well-known SID), granted read access to change the DACL of a file. */
    private static final String AUTHENTICATED_USERS_SID = "*S-1-5-11";

    private SmbClient smbClient;

    /** Gives the last write time alone, as the client did before it took the change time into account. */
    private SmbClient writeTimeSmbClient;

    private Path sharePath;

    private String baseUrl;

    private Path file;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        final String host = System.getenv(ENV_PREFIX + "HOST");
        final boolean required = Boolean.parseBoolean(System.getenv(ENV_PREFIX + "REQUIRED"));
        if (host == null || host.isBlank()) {
            Assertions.assertFalse(required, ENV_PREFIX + "REQUIRED is set but " + ENV_PREFIX + "HOST is not");
            Assumptions.assumeTrue(false, "Skipping: " + ENV_PREFIX + "HOST is not set");
        }
        sharePath = Path.of(getRequiredEnv("SHARE_PATH"));
        Assertions.assertTrue(Files.isDirectory(sharePath), "The share directory is not on this machine: " + sharePath);
        baseUrl = "smb://" + host + "/" + getRequiredEnv("SHARE") + "/";

        final StandardCrawlerContainer container = new StandardCrawlerContainer()//
                .singleton("smbClient", SmbClient.class)//
                .singleton("writeTimeSmbClient", WriteTimeSmbClient.class)//
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        smbClient = initClient(container.getComponent("smbClient"));
        writeTimeSmbClient = initClient(container.getComponent("writeTimeSmbClient"));

        file = sharePath.resolve(testInfo.getTestMethod().map(m -> m.getName()).orElse("test") + ".txt");
        Files.writeString(file, "content");
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        if (file != null) {
            Files.deleteIfExists(file);
        }
        super.tearDown(testInfo);
    }

    @Test
    public void test_unchangedFile_keepsLastModified() throws Exception {
        final long first = headLastModified(smbClient);
        ThreadUtil.sleep(WAIT_MILLIS);
        assertEquals(first, headLastModified(smbClient));
        assertEquals(first, getLastModified(smbClient));
    }

    @Test
    public void test_daclChange_movesLastModified() throws Exception {
        assertMetadataChangeMovesLastModified("icacls", file.toString(), "/grant", AUTHENTICATED_USERS_SID + ":(R)");
    }

    @Test
    public void test_archiveAttributeCleared_movesLastModified() throws Exception {
        // a backup tool clears the archive attribute of each file it has copied
        assertMetadataChangeMovesLastModified("attrib", "-A", file.toString());
    }

    @Test
    public void test_contentWrite_movesLastModified() throws Exception {
        final long before = headLastModified(smbClient);
        final long writeTimeBefore = headLastModified(writeTimeSmbClient);
        ThreadUtil.sleep(WAIT_MILLIS);

        final long start = System.currentTimeMillis();
        Files.writeString(file, "new content");
        final long end = System.currentTimeMillis();

        final long after = headLastModified(smbClient);
        logger.info("[contentWrite] before={}, after={}, start={}, end={}", before, after, start, end);
        assertWithin(start, end, after);
        assertEquals(after, getLastModified(smbClient));
        Assertions.assertTrue(headLastModified(writeTimeSmbClient) > writeTimeBefore, "the last write time moves");
    }

    private void assertMetadataChangeMovesLastModified(final String... command) throws Exception {
        final long before = headLastModified(smbClient);
        final long writeTimeBefore = headLastModified(writeTimeSmbClient);
        final long localBefore = Files.getLastModifiedTime(file).toMillis();
        ThreadUtil.sleep(WAIT_MILLIS);

        final long start = System.currentTimeMillis();
        run(command);
        final long end = System.currentTimeMillis();

        final long after = headLastModified(smbClient);
        final long writeTimeAfter = headLastModified(writeTimeSmbClient);
        logger.info("[{}] before={}, after={}, writeTimeBefore={}, writeTimeAfter={}, start={}, end={}", command[0], before, after,
                writeTimeBefore, writeTimeAfter, start, end);
        assertWithin(start, end, after);
        assertEquals(after, getLastModified(smbClient));
        // the change touched the metadata alone, so the last write time, which the client gave before, stays
        Assertions.assertEquals(writeTimeBefore, writeTimeAfter, "the last write time");
        Assertions.assertEquals(localBefore, Files.getLastModifiedTime(file).toMillis(), "the local last modified time");
    }

    private static void assertWithin(final long start, final long end, final long time) {
        Assertions.assertTrue(time >= start - CLOCK_TOLERANCE_MILLIS && time <= end + CLOCK_TOLERANCE_MILLIS,
                "the time " + time + " is not within [" + start + ", " + end + "]");
    }

    private long headLastModified(final SmbClient client) throws Exception {
        try (final ResponseData responseData = client.doHead(url())) {
            assertNotNull(responseData);
            return responseData.getLastModified().getTime();
        }
    }

    private long getLastModified(final SmbClient client) throws Exception {
        try (final ResponseData responseData = client.doGet(url())) {
            return responseData.getLastModified().getTime();
        }
    }

    private String url() {
        return baseUrl + file.getFileName();
    }

    private SmbClient initClient(final SmbClient client) {
        final SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername(getRequiredEnv("USER"));
        auth.setPassword(getRequiredEnv("PASSWORD"));
        final Map<String, Object> params = new HashMap<>();
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.init();
        return client;
    }

    private static String getRequiredEnv(final String name) {
        final String value = System.getenv(ENV_PREFIX + name);
        Assertions.assertTrue(value != null && !value.isBlank(), ENV_PREFIX + name + " is not set");
        return value;
    }

    private static void run(final String... command) throws Exception {
        final Process process = new ProcessBuilder(command).redirectErrorStream(true).start();
        final String output;
        try (InputStream in = process.getInputStream()) {
            output = new String(in.readAllBytes(), Charset.defaultCharset());
        }
        Assertions.assertEquals(0, process.waitFor(), command[0] + " failed: " + output);
    }

    /** Gives the last write time alone, as {@link SmbClient} did before it took the change time into account. */
    public static class WriteTimeSmbClient extends SmbClient {
        @Override
        protected long getLastModifiedOf(final SmbFile file) throws SmbException {
            return file.lastModified();
        }
    }
}
