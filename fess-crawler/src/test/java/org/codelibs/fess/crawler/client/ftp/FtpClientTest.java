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
package org.codelibs.fess.crawler.client.ftp;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.client.ftp.FtpClient.FtpInfo;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class FtpClientTest extends PlainTestCase {
    private static final int FTP_PORT = 10021;

    public FtpClient ftpClient;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("ftpClient", FtpClient.class);
        ftpClient = container.getComponent("ftpClient");
    }

    public FtpServer startFtpServer(int port, String username, String password) throws FtpException {
        FtpServerFactory factory = new FtpServerFactory();
        ListenerFactory lisnerFactory = new ListenerFactory();
        lisnerFactory.setPort(port);
        factory.addListener("default", lisnerFactory.createListener());

        if (username != null) {
            UserManager userManager = factory.getUserManager();
            BaseUser ftpUser = new BaseUser();
            ftpUser.setName(username);
            ftpUser.setPassword(password);
            final File file = ResourceUtil.getResourceAsFile("test");
            String path = file.getAbsolutePath();
            ftpUser.setHomeDirectory(path);

            userManager.save(ftpUser);
        }

        FtpServer server = factory.createServer();
        server.start();
        return server;
    }

    @Test
    public void test_doGet_root_dir() throws FtpException {
        FtpServer server = null;
        try {
            String username = "testuser";
            String password = "testpass";
            server = startFtpServer(FTP_PORT, username, password);
            Map<String, Object> params = new HashMap<String, Object>();
            FtpAuthentication auth = new FtpAuthentication();
            auth.setUsername(username);
            auth.setPassword(password);
            params.put(FtpClient.FTP_AUTHENTICATIONS_PROPERTY, new FtpAuthentication[] { auth });
            ftpClient.setInitParameterMap(params);

            ftpClient.doGet("ftp://localhost:" + FTP_PORT + "/");
            fail();
        } catch (final ChildUrlsException e) {
            final Set<RequestData> urlSet = e.getChildUrlList();
            assertEquals(5, urlSet.size());
            final List<String> urlList = urlSet.stream().map(x -> x.getUrl()).sorted().toList();
            assertEquals("ftp://localhost:10021/dir1", urlList.get(0));
            assertEquals("ftp://localhost:10021/dir2", urlList.get(1));
            assertEquals("ftp://localhost:10021/text 3.txt", urlList.get(2));
            assertEquals("ftp://localhost:10021/text1.txt", urlList.get(3));
            assertEquals("ftp://localhost:10021/text2.txt", urlList.get(4));
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }

    @Test
    public void test_doGet_dir1() throws FtpException {
        FtpServer server = null;
        try {
            String username = "testuser";
            String password = "testpass";
            server = startFtpServer(FTP_PORT, username, password);
            Map<String, Object> params = new HashMap<String, Object>();
            FtpAuthentication auth = new FtpAuthentication();
            auth.setUsername(username);
            auth.setPassword(password);
            params.put(FtpClient.FTP_AUTHENTICATIONS_PROPERTY, new FtpAuthentication[] { auth });
            ftpClient.setInitParameterMap(params);

            ftpClient.doGet("ftp://localhost:" + FTP_PORT + "/dir1");
            fail();
        } catch (final ChildUrlsException e) {
            final Set<RequestData> urlSet = e.getChildUrlList();
            assertEquals(1, urlSet.size());
            for (final RequestData requestData : urlSet.toArray(new RequestData[urlSet.size()])) {
                String url = requestData.getUrl();
                assertTrue(url.contains("dir1/test3.txt"));
            }
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }

    @Test
    public void test_doGet_file() throws Exception {
        FtpServer server = null;
        try {
            String username = "testuser";
            String password = "testpass";
            server = startFtpServer(FTP_PORT, username, password);
            Map<String, Object> params = new HashMap<String, Object>();
            FtpAuthentication auth = new FtpAuthentication();
            auth.setUsername(username);
            auth.setPassword(password);
            params.put(FtpClient.FTP_AUTHENTICATIONS_PROPERTY, new FtpAuthentication[] { auth });
            ftpClient.setInitParameterMap(params);
            final ResponseData responseData = ftpClient.doGet("ftp://localhost:" + FTP_PORT + "/text1.txt");
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals("UTF-8", responseData.getCharSet());
            assertTrue(6 == responseData.getContentLength());
            assertNotNull(responseData.getLastModified());
            assertEquals(Constants.GET_METHOD, responseData.getMethod());
            assertEquals("text/plain", responseData.getMimeType());
            assertTrue(responseData.getUrl().endsWith("text1.txt"));
            final String content = new String(InputStreamUtil.getBytes(responseData.getResponseBody()), "UTF-8");
            assertEquals("test1", content.trim());
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }

    @Test
    public void test_doGet_file_with_space() throws Exception {
        FtpServer server = null;
        try {
            String username = "testuser";
            String password = "testpass";
            server = startFtpServer(FTP_PORT, username, password);
            Map<String, Object> params = new HashMap<String, Object>();
            FtpAuthentication auth = new FtpAuthentication();
            auth.setUsername(username);
            auth.setPassword(password);
            params.put(FtpClient.FTP_AUTHENTICATIONS_PROPERTY, new FtpAuthentication[] { auth });
            ftpClient.setInitParameterMap(params);
            final ResponseData responseData = ftpClient.doGet("ftp://localhost:" + FTP_PORT + "/text 3.txt");
            assertEquals(200, responseData.getHttpStatusCode());
            assertEquals("UTF-8", responseData.getCharSet());
            assertEquals(6L, responseData.getContentLength());
            assertNotNull(responseData.getLastModified());
            assertEquals(Constants.GET_METHOD, responseData.getMethod());
            assertEquals("text/plain", responseData.getMimeType());
            assertTrue(responseData.getUrl().endsWith("text 3.txt"));
            final String content = new String(InputStreamUtil.getBytes(responseData.getResponseBody()), "UTF-8");
            assertEquals("test3\n", content);
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }

    @Test
    public void test_ftpInfo() {
        String value;
        FtpInfo ftpInfo;

        try {
            ftpInfo = new FtpClient.FtpInfo(null, Constants.UTF_8);
            fail();
        } catch (CrawlingAccessException e) {
            // ignore
        }

        try {
            ftpInfo = new FtpClient.FtpInfo("", Constants.UTF_8);
            fail();
        } catch (CrawlingAccessException e) {
            // ignore
        }

        try {
            ftpInfo = new FtpClient.FtpInfo("abc", Constants.UTF_8);
            fail();
        } catch (CrawlingAccessException e) {
            // ignore
        }

        value = "ftp://123.123.123.123:9999/";
        ftpInfo = new FtpClient.FtpInfo(value, Constants.UTF_8);
        assertEquals(value, ftpInfo.toUrl());
        assertEquals("123.123.123.123:9999", ftpInfo.getCacheKey());
        assertEquals("123.123.123.123", ftpInfo.getHost());
        assertEquals(9999, ftpInfo.getPort());
        assertEquals("/", ftpInfo.getParent());
        assertNull(ftpInfo.getName());

        value = "ftp://123.123.123.123/test.txt";
        ftpInfo = new FtpClient.FtpInfo(value, Constants.UTF_8);
        assertEquals(value, ftpInfo.toUrl());
        assertEquals("123.123.123.123:21", ftpInfo.getCacheKey());
        assertEquals("123.123.123.123", ftpInfo.getHost());
        assertEquals(21, ftpInfo.getPort());
        assertEquals("/", ftpInfo.getParent());
        assertEquals("test.txt", ftpInfo.getName());

        value = "ftp://123.123.123.123/aaa/../test.txt";
        ftpInfo = new FtpClient.FtpInfo(value, Constants.UTF_8);
        assertEquals("ftp://123.123.123.123/test.txt", ftpInfo.toUrl());
        assertEquals("123.123.123.123:21", ftpInfo.getCacheKey());
        assertEquals("123.123.123.123", ftpInfo.getHost());
        assertEquals(21, ftpInfo.getPort());
        assertEquals("/", ftpInfo.getParent());
        assertEquals("test.txt", ftpInfo.getName());
        assertEquals("ftp://123.123.123.123/", ftpInfo.toUrl("/"));

        value = "ftp://123.123.123.123:21/test1/test.txt";
        ftpInfo = new FtpClient.FtpInfo(value, Constants.UTF_8);
        assertEquals("ftp://123.123.123.123/test1/test.txt", ftpInfo.toUrl());
        assertEquals("123.123.123.123:21", ftpInfo.getCacheKey());
        assertEquals("123.123.123.123", ftpInfo.getHost());
        assertEquals(21, ftpInfo.getPort());
        assertEquals("/test1", ftpInfo.getParent());
        assertEquals("test.txt", ftpInfo.getName());
        assertEquals("ftp://123.123.123.123/", ftpInfo.toUrl("/"));
        assertEquals("ftp://123.123.123.123/aaa/bbb/ccc.txt", ftpInfo.toUrl("/aaa//bbb/ccc.txt"));
        assertEquals("ftp://123.123.123.123/ccc.txt", ftpInfo.toUrl("/aaa/../ccc.txt"));

        value = "ftp://123.123.123.123/test test.txt";
        ftpInfo = new FtpClient.FtpInfo(value, Constants.UTF_8);
        assertEquals(value, ftpInfo.toUrl());
        assertEquals("123.123.123.123:21", ftpInfo.getCacheKey());
        assertEquals("123.123.123.123", ftpInfo.getHost());
        assertEquals(21, ftpInfo.getPort());
        assertEquals("/", ftpInfo.getParent());
        assertEquals("test test.txt", ftpInfo.getName());

        value = "ftp://123.123.123.123/テスト.txt";
        ftpInfo = new FtpClient.FtpInfo(value, Constants.UTF_8);
        assertEquals(value, ftpInfo.toUrl());
        assertEquals("123.123.123.123:21", ftpInfo.getCacheKey());
        assertEquals("123.123.123.123", ftpInfo.getHost());
        assertEquals(21, ftpInfo.getPort());
        assertEquals("/", ftpInfo.getParent());
        assertEquals("テスト.txt", ftpInfo.getName());

        value = "ftp://123.123.123.123/";
        ftpInfo = new FtpClient.FtpInfo(value, Constants.UTF_8);
        assertEquals(value, ftpInfo.toUrl());
        assertEquals("123.123.123.123:21", ftpInfo.getCacheKey());
        assertEquals("123.123.123.123", ftpInfo.getHost());
        assertEquals(21, ftpInfo.getPort());
        assertEquals("/", ftpInfo.getParent());
        assertNull(ftpInfo.getName());

        value = "ftp://123.123.123.123//";
        ftpInfo = new FtpClient.FtpInfo(value, Constants.UTF_8);
        assertEquals("ftp://123.123.123.123/", ftpInfo.toUrl());
        assertEquals("123.123.123.123:21", ftpInfo.getCacheKey());
        assertEquals("123.123.123.123", ftpInfo.getHost());
        assertEquals(21, ftpInfo.getPort());
        assertEquals("/", ftpInfo.getParent());
        assertNull(ftpInfo.getName());

        value = "ftp://testuser:testpass@123.123.123.123:21/test1/test.txt";
        ftpInfo = new FtpClient.FtpInfo(value, Constants.UTF_8);
        assertEquals("ftp://123.123.123.123/test1/test.txt", ftpInfo.toUrl());
        assertEquals("123.123.123.123:21", ftpInfo.getCacheKey());
        assertEquals("123.123.123.123", ftpInfo.getHost());
        assertEquals(21, ftpInfo.getPort());
        assertEquals("/test1", ftpInfo.getParent());
        assertEquals("test.txt", ftpInfo.getName());

        value = "ftp://123.123.123.123/path with spaces/file.txt";
        ftpInfo = new FtpClient.FtpInfo(value, Constants.UTF_8);
        assertEquals(value, ftpInfo.toUrl());
        assertEquals("123.123.123.123:21", ftpInfo.getCacheKey());
        assertEquals("123.123.123.123", ftpInfo.getHost());
        assertEquals(21, ftpInfo.getPort());
        assertEquals("/path with spaces", ftpInfo.getParent());
        assertEquals("file.txt", ftpInfo.getName());
    }

    @Test
    public void test_doHead_file() throws Exception {
        FtpServer server = null;
        try {
            String username = "testuser";
            String password = "testpass";
            server = startFtpServer(FTP_PORT, username, password);
            Map<String, Object> params = new HashMap<String, Object>();
            FtpAuthentication auth = new FtpAuthentication();
            auth.setUsername(username);
            auth.setPassword(password);
            params.put(FtpClient.FTP_AUTHENTICATIONS_PROPERTY, new FtpAuthentication[] { auth });
            ftpClient.setInitParameterMap(params);
            final ResponseData responseData = ftpClient.doHead("ftp://localhost:" + FTP_PORT + "/text1.txt");
            assertNotNull(responseData.getLastModified());
            assertTrue(responseData.getLastModified().getTime() < new Date().getTime());
            assertNull(responseData.getResponseBody());
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }

    @Test
    public void test_doHead_root_dir() throws Exception {
        FtpServer server = null;
        try {
            String username = "testuser";
            String password = "testpass";
            server = startFtpServer(FTP_PORT, username, password);
            Map<String, Object> params = new HashMap<String, Object>();
            FtpAuthentication auth = new FtpAuthentication();
            auth.setUsername(username);
            auth.setPassword(password);
            params.put(FtpClient.FTP_AUTHENTICATIONS_PROPERTY, new FtpAuthentication[] { auth });
            ftpClient.setInitParameterMap(params);
            final ResponseData responseData = ftpClient.doHead("ftp://localhost:" + FTP_PORT + "/");
            assertNull(responseData);
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }

    @Test
    public void test_doHead_dir1() throws Exception {
        FtpServer server = null;
        try {
            String username = "testuser";
            String password = "testpass";
            server = startFtpServer(FTP_PORT, username, password);
            Map<String, Object> params = new HashMap<String, Object>();
            FtpAuthentication auth = new FtpAuthentication();
            auth.setUsername(username);
            auth.setPassword(password);
            params.put(FtpClient.FTP_AUTHENTICATIONS_PROPERTY, new FtpAuthentication[] { auth });
            ftpClient.setInitParameterMap(params);
            final ResponseData responseData = ftpClient.doHead("ftp://localhost:" + FTP_PORT + "/dir1");
            assertNull(responseData);
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }

    @Test
    public void test_doGet_accessTimeoutTarget() {
        FtpClient client = new FtpClient() {
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
            client.doGet("ftp://localhost/test.txt");
            fail();
        } catch (CrawlingAccessException e) {
            assertTrue(e.getCause() instanceof InterruptedException);
        }
    }

    @Test
    public void test_doHead_accessTimeoutTarget() {
        FtpClient client = new FtpClient() {
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
            client.doHead("ftp://localhost/test.txt");
            fail();
        } catch (CrawlingAccessException e) {
            assertTrue(e.getCause() instanceof InterruptedException);
        }
    }

    @Test
    public void test_directory_child_urls() throws Exception {
        // Test that childUri (not typo chileUri) is correctly used in directory listing
        FtpServer server = null;
        try {
            String username = "testuser";
            String password = "testpass";
            server = startFtpServer(FTP_PORT, username, password);
            Map<String, Object> params = new HashMap<String, Object>();
            FtpAuthentication auth = new FtpAuthentication();
            auth.setUsername(username);
            auth.setPassword(password);
            params.put(FtpClient.FTP_AUTHENTICATIONS_PROPERTY, new FtpAuthentication[] { auth });
            ftpClient.setInitParameterMap(params);

            try {
                ftpClient.doGet("ftp://localhost:" + FTP_PORT + "/dir1");
                fail();
            } catch (final ChildUrlsException e) {
                final Set<RequestData> urlSet = e.getChildUrlList();
                assertEquals(1, urlSet.size());
                String childUrl = urlSet.iterator().next().getUrl();
                assertTrue(childUrl.contains("dir1/test3.txt"));
                assertTrue(childUrl.matches(".*dir1/test3\\.txt"));
            }
        } finally {
            if (server != null) {
                server.stop();
            }
        }
    }

    @Test
    public void test_accessTimeout_null_safety() {
        // Test that accessTimeoutTask null check prevents NPE
        FtpClient client = new FtpClient() {
            @Override
            protected ResponseData getResponseData(final String uri, final boolean includeContent) {
                // Simulate quick completion before timeout
                ResponseData responseData = new ResponseData();
                responseData.setHttpStatusCode(200);
                return responseData;
            }
        };

        // Test with timeout set
        client.setAccessTimeout(10);
        try {
            ResponseData result = client.doGet("ftp://localhost/test.txt");
            assertNotNull(result);
            assertEquals(200, result.getHttpStatusCode());
        } catch (Exception e) {
            fail();
        }

        // Test without timeout (null accessTimeout)
        client.setAccessTimeout(null);
        try {
            ResponseData result = client.doGet("ftp://localhost/test.txt");
            assertNotNull(result);
            assertEquals(200, result.getHttpStatusCode());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test_ftpInfo_toChildUrl() {
        // Test that toChildUrl method works correctly (used with childUri variable)
        FtpInfo ftpInfo = new FtpClient.FtpInfo("ftp://example.com/parent/", Constants.UTF_8);

        String childUrl1 = ftpInfo.toChildUrl("file.txt");
        assertEquals("ftp://example.com/parent/file.txt", childUrl1);

        String childUrl2 = ftpInfo.toChildUrl("subdir/file.txt");
        assertEquals("ftp://example.com/parent/subdir/file.txt", childUrl2);

        // Test that the method properly handles various child paths
        FtpInfo ftpInfo2 = new FtpClient.FtpInfo("ftp://example.com/test", Constants.UTF_8);
        String childUrl3 = ftpInfo2.toChildUrl("child.txt");
        assertEquals("ftp://example.com/test/child.txt", childUrl3);
    }
}
