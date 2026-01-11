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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

/**
 * @author shinsuke
 *
 */
public class SmbClientTest extends PlainTestCase {

    private static final Logger logger = LogManager.getLogger(SmbClientTest.class);

    private static final String IMAGE_NAME = "dperson/samba:latest";

    private SmbClient smbClient;
    private GenericContainer<?> sambaServer;
    private String baseUrl;
    private String host;
    private Integer port;

    @Override
    @BeforeEach
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        Path tempDir = Files.createTempDirectory("smbdata");
        Path publicDir = tempDir.resolve("public");
        Files.createDirectory(publicDir);
        Path usersDir = tempDir.resolve("users");
        Files.createDirectory(usersDir);
        Path testuser1Dir = tempDir.resolve("testuser1");
        Files.createDirectory(testuser1Dir);
        Path testuser2Dir = tempDir.resolve("testuser2");
        Files.createDirectory(testuser2Dir);

        // Basic files for existing tests
        Files.writeString(usersDir.resolve("file1.txt"), "file1");
        Path dir1 = usersDir.resolve("dir1");
        Files.createDirectory(dir1);
        Files.writeString(dir1.resolve("file2.txt"), "file2");
        Path dir2 = dir1.resolve("dir2");
        Files.createDirectory(dir2);
        Files.writeString(dir2.resolve("file3.txt"), "file3");
        Path dir3 = usersDir.resolve("dir3");
        Files.createDirectory(dir3);
        Files.writeString(dir3.resolve("file4.txt"), "file4");

        // Large file for test_doGet_largeFile
        Path largeDir = publicDir.resolve("large");
        Files.createDirectory(largeDir);
        byte[] largeContent = new byte[10 * 1024 * 1024]; // 10MB
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }
        Files.write(largeDir.resolve("largefile.bin"), largeContent);

        // Special character files for test_specialCharactersInFileName
        Path specialDir = publicDir.resolve("special");
        Files.createDirectory(specialDir);
        Files.writeString(specialDir.resolve("file with spaces.txt"), "spaces");
        Files.writeString(specialDir.resolve("file-with-dashes.txt"), "dashes");
        Files.writeString(specialDir.resolve("file_with_underscores.txt"), "underscores");
        Files.writeString(specialDir.resolve("file.multiple.dots.txt"), "dots");

        // Empty directory for test_emptyDirectory
        Path emptyDir = publicDir.resolve("empty");
        Files.createDirectory(emptyDir);

        // Deeply nested directories for test_deeplyNestedDirectories
        Path deepDir = publicDir.resolve("deep");
        Files.createDirectory(deepDir);
        Path currentDir = deepDir;
        for (int i = 1; i <= 5; i++) {
            currentDir = currentDir.resolve("level" + i);
            Files.createDirectory(currentDir);
        }
        Files.writeString(currentDir.resolve("deepfile.txt"), "deep content");

        // Zero byte file for test_zeroByteFile
        Path zeroDir = publicDir.resolve("zero");
        Files.createDirectory(zeroDir);
        Files.createFile(zeroDir.resolve("empty.txt"));

        MountableFile mountablePublic = MountableFile.forHostPath(publicDir.toAbsolutePath().toString());
        MountableFile mountableUsers = MountableFile.forHostPath(usersDir.toAbsolutePath().toString());
        MountableFile mountableTestuser1 = MountableFile.forHostPath(testuser1Dir.toAbsolutePath().toString());
        MountableFile mountableTestuser2 = MountableFile.forHostPath(testuser2Dir.toAbsolutePath().toString());

        sambaServer = new GenericContainer<>(IMAGE_NAME).withExposedPorts(139, 445)//
                .withCopyFileToContainer(mountablePublic, "/share")//
                .withCopyFileToContainer(mountableUsers, "/srv")
                .withCopyFileToContainer(mountableTestuser1, "/testuser1")//
                .withCopyFileToContainer(mountableTestuser2, "/testuser2")//
                .withCommand(//
                        "-u", "testuser1;test123", //
                        "-u", "testuser2;test123", //
                        "-u", "testuser;test123", // Additional user for various tests
                        "-s", "public;/share;yes;no;no;testuser1,testuser", //
                        "-s", "users;/srv;no;no;no;testuser1,testuser2", //
                        "-s", "testuser1 private share;/testuser1;no;no;no;testuser1", //
                        "-s", "testuser2 private share;/testuser2;no;no;no;testuser2", //
                        "-g", "log level = 3");
        logger.info("Starting Samba container with image {}", IMAGE_NAME);
        sambaServer.start();
        logger.info("Samba container started");

        host = sambaServer.getContainerIpAddress();
        port = sambaServer.getMappedPort(445);
        baseUrl = "smb://" + host + ":" + port + "/users/";
        logger.info("Base URL: {}", baseUrl);

        StandardCrawlerContainer container = new StandardCrawlerContainer()//
                .singleton("smbClient", SmbClient.class)//
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        smbClient = container.getComponent("smbClient");
        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth1 = new SmbAuthentication();
        auth1.setUsername("testuser1");
        auth1.setPassword("test123");
        SmbAuthentication[] auths = new SmbAuthentication[] { auth1 };
        params.put("smbAuthentications", auths);
        smbClient.setInitParameterMap(params);
        smbClient.init();

        boolean connected = false;
        for (int i = 0; i < 30; i++) {
            try {
                smbClient.doGet(baseUrl);
            } catch (final ChildUrlsException e) {
                connected = true;
                break;
            } catch (final Exception e) {
                logger.info("[{}] {}", i + 1, e.getMessage());
            }
            ThreadUtil.sleep(200L);
        }
        if (!connected) {
            throw new IllegalStateException("Could not connect to the Samba server");
        }
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        if (sambaServer != null) {
            sambaServer.stop();
        }
        super.tearDown();
    }

    public void test_doGet() throws Exception {
        try (final ResponseData responseData = smbClient.doGet(baseUrl + "file1.txt")) {
            assertEquals(0, responseData.getStatus());
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals(baseUrl + "file1.txt", responseData.getUrl());
            assertEquals("file1", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
            assertEquals(5, responseData.getContentLength());
        }
        try (final ResponseData responseData = smbClient.doGet(baseUrl + "dir1/file2.txt")) {
            assertEquals(0, responseData.getStatus());
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals(baseUrl + "dir1/file2.txt", responseData.getUrl());
            assertEquals("file2", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
            assertEquals(5, responseData.getContentLength());
        }
        try (final ResponseData responseData = smbClient.doGet(baseUrl + "dir1/dir2/file3.txt")) {
            assertEquals(baseUrl + "dir1/dir2/file3.txt", responseData.getUrl());
            assertEquals(0, responseData.getStatus());
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals("file3", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
            assertEquals(5, responseData.getContentLength());
        }
        try (final ResponseData responseData = smbClient.doGet(baseUrl + "dir3/file4.txt")) {
            assertEquals(0, responseData.getStatus());
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals(baseUrl + "dir3/file4.txt", responseData.getUrl());
            assertEquals("file4", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
            assertEquals(5, responseData.getContentLength());
        }

        try {
            smbClient.doGet(baseUrl);
            fail();
        } catch (final ChildUrlsException e) {
            String[] urls = e.getChildUrlList().stream().map(r -> r.getUrl()).sorted().toArray(String[]::new);
            assertEquals(3, urls.length);
            assertEquals(baseUrl + "dir1/", urls[0]);
            assertEquals(baseUrl + "dir3/", urls[1]);
            assertEquals(baseUrl + "file1.txt", urls[2]);
        }
        try {
            smbClient.doGet(baseUrl + "dir1/");
            fail();
        } catch (final ChildUrlsException e) {
            String[] urls = e.getChildUrlList().stream().map(r -> r.getUrl()).sorted().toArray(String[]::new);
            assertEquals(2, urls.length);
            assertEquals(baseUrl + "dir1/dir2/", urls[0]);
            assertEquals(baseUrl + "dir1/file2.txt", urls[1]);
        }
        try {
            smbClient.doGet(baseUrl + "dir1/dir2/");
            fail();
        } catch (final ChildUrlsException e) {
            String[] urls = e.getChildUrlList().stream().map(r -> r.getUrl()).sorted().toArray(String[]::new);
            assertEquals(1, urls.length);
            assertEquals(baseUrl + "dir1/dir2/file3.txt", urls[0]);
        }
        try {
            smbClient.doGet(baseUrl + "dir3/");
            fail();
        } catch (final ChildUrlsException e) {
            String[] urls = e.getChildUrlList().stream().map(r -> r.getUrl()).sorted().toArray(String[]::new);
            assertEquals(1, urls.length);
            assertEquals(baseUrl + "dir3/file4.txt", urls[0]);
        }

        try (final ResponseData responseData = smbClient.doGet(baseUrl + "none")) {
            assertEquals(0, responseData.getStatus());
            assertEquals(404, responseData.getHttpStatusCode());
            assertEquals(0, responseData.getContentLength());
            assertNull(responseData.getResponseBody());
            assertEquals(baseUrl + "none", responseData.getUrl());
        }

        try {
            smbClient.doGet("");
            fail();
        } catch (final CrawlerSystemException e) {
            // nothing
        }
    }

    public void test_doHead() throws Exception {
        try (final ResponseData responseData = smbClient.doHead(baseUrl + "file1.txt")) {
            assertEquals(0, responseData.getStatus());
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals(5, responseData.getContentLength());
            assertEquals(baseUrl + "file1.txt", responseData.getUrl());
            assertEquals("text/plain", responseData.getMimeType());
            assertNull(responseData.getResponseBody());
        }
        try (final ResponseData responseData = smbClient.doHead(baseUrl + "dir1/file2.txt")) {
            assertEquals(0, responseData.getStatus());
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals(5, responseData.getContentLength());
            assertEquals(baseUrl + "dir1/file2.txt", responseData.getUrl());
            assertEquals("text/plain", responseData.getMimeType());
            assertNull(responseData.getResponseBody());
        }
        try (final ResponseData responseData = smbClient.doHead(baseUrl + "dir1/dir2/file3.txt")) {
            assertEquals(0, responseData.getStatus());
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals(5, responseData.getContentLength());
            assertEquals(baseUrl + "dir1/dir2/file3.txt", responseData.getUrl());
            assertEquals("text/plain", responseData.getMimeType());
            assertNull(responseData.getResponseBody());
        }
        try (final ResponseData responseData = smbClient.doHead(baseUrl + "dir3/file4.txt")) {
            assertEquals(0, responseData.getStatus());
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals(5, responseData.getContentLength());
            assertEquals(baseUrl + "dir3/file4.txt", responseData.getUrl());
            assertEquals("text/plain", responseData.getMimeType());
            assertNull(responseData.getResponseBody());
        }

        try (final ResponseData responseData = smbClient.doHead(baseUrl)) {
            assertNull(responseData);
        }
        try (final ResponseData responseData = smbClient.doHead(baseUrl + "dir1/")) {
            assertNull(responseData);
        }
        try (final ResponseData responseData = smbClient.doHead(baseUrl + "dir1/dir2/")) {
            assertNull(responseData);
        }
        try (final ResponseData responseData = smbClient.doHead(baseUrl + "dir3/")) {
            assertNull(responseData);
        }
        try (final ResponseData responseData = smbClient.doHead(baseUrl + "none")) {
            assertEquals(404, responseData.getHttpStatusCode());
        }
        try {
            smbClient.doHead("");
            fail();
        } catch (final CrawlerSystemException e) {
            // nothing
        }
    }

    public void test_doGet_accessTimeoutTarget() {
        SmbClient client = new SmbClient() {
            @Override
            protected ResponseData getResponseData(final String uri, final boolean includeContent) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new CrawlingAccessException(e);
                }
                return null;
            }
        };
        client.setAccessTimeout(1);
        try {
            client.doGet("smb://localhost/test.txt");
            fail();
        } catch (CrawlingAccessException e) {
            assertTrue(e.getCause() instanceof InterruptedException);
        }
    }

    public void test_doHead_accessTimeoutTarget() {
        SmbClient client = new SmbClient() {
            @Override
            protected ResponseData getResponseData(final String uri, final boolean includeContent) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new CrawlingAccessException(e);
                }
                return new ResponseData();
            }
        };
        client.setAccessTimeout(1);
        try {
            client.doHead("smb://localhost/test.txt");
            fail();
        } catch (CrawlingAccessException e) {
            assertTrue(e.getCause() instanceof InterruptedException);
        }
    }

    public void test_authenticationWithDomain() throws Exception {
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("smbClient", SmbClient.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        SmbClient client = container.getComponent("smbClient");

        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername("testuser1");
        auth.setPassword("test123");
        auth.setDomain("WORKGROUP");
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.init();

        boolean connected = false;
        for (int i = 0; i < 30; i++) {
            try {
                client.doGet(baseUrl);
            } catch (final ChildUrlsException e) {
                connected = true;
                break;
            } catch (final Exception e) {
                logger.info("[{}] {}", i + 1, e.getMessage());
            }
            ThreadUtil.sleep(200L);
        }
        if (!connected) {
            throw new IllegalStateException("Could not connect to the Samba server");
        }

        try (ResponseData responseData = client.doGet(baseUrl + "file1.txt")) {
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals("file1", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
        }
    }

    public void test_wrongCredentials() throws Exception {
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("smbClient", SmbClient.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        SmbClient client = container.getComponent("smbClient");

        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername("wronguser");
        auth.setPassword("wrongpass");
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.init();

        try {
            client.doGet(baseUrl + "file1.txt");
            fail();
        } catch (CrawlingAccessException e) {
            // Expected
        }
    }

    public void test_malformedUrl() throws Exception {
        ResponseData responseData = smbClient.doGet("not-a-valid-smb-url");
        assertEquals(0, responseData.getStatus());
        assertEquals(404, responseData.getHttpStatusCode());

        responseData = smbClient.doGet("smb://[invalid]:445/share");
        assertEquals(0, responseData.getStatus());
        assertEquals(404, responseData.getHttpStatusCode());
    }

    public void test_maxContentLengthExceeded() throws Exception {
        ContentLengthHelper helper = new ContentLengthHelper();
        helper.setDefaultMaxLength(3L);
        smbClient.contentLengthHelper = helper;

        try {
            smbClient.doGet(baseUrl + "file1.txt");
            fail();
        } catch (MaxLengthExceededException e) {
            assertTrue(e.getMessage().contains("over 3 byte"));
        }
    }

    public void test_doGet_largeFile() throws Exception {
        String largeFileUrl = "smb://" + host + ":" + port + "/public/large/largefile.bin";

        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("smbClient", SmbClient.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        SmbClient client = container.getComponent("smbClient");

        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername("testuser");
        auth.setPassword("test123");
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.setMaxCachedContentSize(1024 * 1024); // 1MB
        client.init();

        boolean connected = false;
        for (int i = 0; i < 30; i++) {
            try {
                client.doGet("smb://" + host + ":" + port + "/public/");
            } catch (final ChildUrlsException e) {
                connected = true;
                break;
            } catch (final Exception e) {
                logger.info("[{}] {}", i + 1, e.getMessage());
            }
            ThreadUtil.sleep(200L);
        }
        if (!connected) {
            throw new IllegalStateException("Could not connect to the Samba server");
        }

        byte[] largeContent = new byte[10 * 1024 * 1024]; // 10MB
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }

        try (ResponseData responseData = client.doGet(largeFileUrl)) {
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals(largeContent.length, responseData.getContentLength());
            assertNotNull(responseData.getResponseBody());
            byte[] result = InputStreamUtil.getBytes(responseData.getResponseBody());
            assertEquals(largeContent.length, result.length);
        }
    }

    public void test_specialCharactersInFileName() throws Exception {
        String shareUrl = "smb://" + host + ":" + port + "/public/special/";

        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("smbClient", SmbClient.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        SmbClient client = container.getComponent("smbClient");

        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername("testuser");
        auth.setPassword("test123");
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.init();

        boolean connected = false;
        for (int i = 0; i < 30; i++) {
            try {
                client.doGet(shareUrl);
            } catch (final ChildUrlsException e) {
                connected = true;
                break;
            } catch (final Exception e) {
                logger.info("[{}] {}", i + 1, e.getMessage());
            }
            ThreadUtil.sleep(200L);
        }
        if (!connected) {
            throw new IllegalStateException("Could not connect to the Samba server");
        }

        try (ResponseData responseData = client.doGet(shareUrl + "file with spaces.txt")) {
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals("spaces", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
        }

        try (ResponseData responseData = client.doGet(shareUrl + "file-with-dashes.txt")) {
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals("dashes", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
        }

        try (ResponseData responseData = client.doGet(shareUrl + "file_with_underscores.txt")) {
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals("underscores", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
        }

        try (ResponseData responseData = client.doGet(shareUrl + "file.multiple.dots.txt")) {
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals("dots", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
        }
    }

    public void test_emptyDirectory() throws Exception {
        String emptyUrl = "smb://" + host + ":" + port + "/public/empty/";

        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("smbClient", SmbClient.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        SmbClient client = container.getComponent("smbClient");

        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername("testuser");
        auth.setPassword("test123");
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.init();

        boolean connected = false;
        for (int i = 0; i < 30; i++) {
            try {
                client.doGet(emptyUrl);
                break;
            } catch (final ChildUrlsException e) {
                connected = true;
                assertEquals(0, e.getChildUrlList().size());
                break;
            } catch (final Exception e) {
                logger.info("[{}] {}", i + 1, e.getMessage());
            }
            ThreadUtil.sleep(200L);
        }
        if (!connected) {
            throw new IllegalStateException("Could not connect to the Samba server");
        }
    }

    public void test_deeplyNestedDirectories() throws Exception {
        String deepUrl = "smb://" + host + ":" + port + "/public/deep/level1/level2/level3/level4/level5/deepfile.txt";

        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("smbClient", SmbClient.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        SmbClient client = container.getComponent("smbClient");

        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername("testuser");
        auth.setPassword("test123");
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.init();

        boolean connected = false;
        for (int i = 0; i < 30; i++) {
            try {
                client.doGet("smb://" + host + ":" + port + "/public/deep/");
            } catch (final ChildUrlsException e) {
                connected = true;
                break;
            } catch (final Exception e) {
                logger.info("[{}] {}", i + 1, e.getMessage());
            }
            ThreadUtil.sleep(200L);
        }
        if (!connected) {
            throw new IllegalStateException("Could not connect to the Samba server");
        }

        try (ResponseData responseData = client.doGet(deepUrl)) {
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals("deep content", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
        }
    }

    public void test_zeroByteFile() throws Exception {
        String emptyFileUrl = "smb://" + host + ":" + port + "/public/zero/empty.txt";

        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("smbClient", SmbClient.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        SmbClient client = container.getComponent("smbClient");

        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername("testuser");
        auth.setPassword("test123");
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.init();

        boolean connected = false;
        for (int i = 0; i < 30; i++) {
            try {
                client.doGet("smb://" + host + ":" + port + "/public/");
            } catch (final ChildUrlsException e) {
                connected = true;
                break;
            } catch (final Exception e) {
                logger.info("[{}] {}", i + 1, e.getMessage());
            }
            ThreadUtil.sleep(200L);
        }
        if (!connected) {
            throw new IllegalStateException("Could not connect to the Samba server");
        }

        try (ResponseData responseData = client.doGet(emptyFileUrl)) {
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals(0, responseData.getContentLength());
            assertEquals("", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
        }

        try (ResponseData responseData = client.doHead(emptyFileUrl)) {
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals(0, responseData.getContentLength());
            assertNull(responseData.getResponseBody());
        }
    }

    public void test_resolveSidsDisabled() throws Exception {
        SmbClient client = new SmbClient();
        client.setResolveSids(false);
        assertFalse(client.isResolveSids());

        StandardCrawlerContainer container =
                new StandardCrawlerContainer().singleton("smbClient", client).singleton("mimeTypeHelper", MimeTypeHelperImpl.class);

        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername("testuser1");
        auth.setPassword("test123");
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.init();

        boolean connected = false;
        for (int i = 0; i < 30; i++) {
            try {
                client.doGet(baseUrl);
            } catch (final ChildUrlsException e) {
                connected = true;
                break;
            } catch (final Exception e) {
                logger.info("[{}] {}", i + 1, e.getMessage());
            }
            ThreadUtil.sleep(200L);
        }
        if (!connected) {
            throw new IllegalStateException("Could not connect to the Samba server");
        }

        try (ResponseData responseData = client.doGet(baseUrl + "file1.txt")) {
            assertEquals(200, responseData.getHttpStatusCode());
        }
    }

    public void test_customCharset() throws Exception {
        SmbClient client = new SmbClient();
        client.setCharset("ISO-8859-1");
        assertEquals("ISO-8859-1", client.getCharset());

        StandardCrawlerContainer container =
                new StandardCrawlerContainer().singleton("smbClient", client).singleton("mimeTypeHelper", MimeTypeHelperImpl.class);

        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername("testuser1");
        auth.setPassword("test123");
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.init();

        boolean connected = false;
        for (int i = 0; i < 30; i++) {
            try {
                client.doGet(baseUrl);
            } catch (final ChildUrlsException e) {
                connected = true;
                break;
            } catch (final Exception e) {
                logger.info("[{}] {}", i + 1, e.getMessage());
            }
            ThreadUtil.sleep(200L);
        }
        if (!connected) {
            throw new IllegalStateException("Could not connect to the Samba server");
        }

        try (ResponseData responseData = client.doGet(baseUrl + "file1.txt")) {
            assertEquals("ISO-8859-1", responseData.getCharSet());
        }
    }

    public void test_fileMetadata() throws Exception {
        try (ResponseData responseData = smbClient.doGet(baseUrl + "file1.txt")) {
            assertEquals(200, responseData.getHttpStatusCode());
            assertNotNull(responseData.getLastModified());
            assertNotNull(responseData.getMetaDataMap().get(SmbClient.SMB_CREATE_TIME));
            assertEquals("text/plain", responseData.getMimeType());
        }
    }

    public void test_closeResources() throws Exception {
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("smbClient", SmbClient.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class);
        SmbClient client = container.getComponent("smbClient");

        Map<String, Object> params = new HashMap<>();
        SmbAuthentication auth = new SmbAuthentication();
        auth.setUsername("testuser1");
        auth.setPassword("test123");
        params.put("smbAuthentications", new SmbAuthentication[] { auth });
        client.setInitParameterMap(params);
        client.init();

        assertNotNull(client.smbAuthenticationHolder);
        assertNotNull(client.cifsContext);

        client.close();

        assertNull(client.smbAuthenticationHolder);
    }

}
