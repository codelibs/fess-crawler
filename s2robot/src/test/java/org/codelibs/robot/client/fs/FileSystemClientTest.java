/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.client.fs;

import java.io.File;
import java.util.Date;
import java.util.Set;

import org.codelibs.robot.Constants;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.ResourceUtil;

/**
 * @author shinsuke
 * 
 */
public class FileSystemClientTest extends S2TestCase {
    public FileSystemClient fsClient;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_doGet_dir() {
        final File file = ResourceUtil.getResourceAsFile("test");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        try {
            fsClient.doGet("file://" + path);
            fail();
        } catch (final ChildUrlsException e) {
            final Set<RequestData> urlSet = e.getChildUrlList();
            for (final RequestData requestData : urlSet
                .toArray(new RequestData[urlSet.size()])) {
                String url = requestData.getUrl();
                if (url.indexOf(".svn") < 0) {
                    assertTrue(url.contains("test/dir1")
                        || url.contains("test/dir2")
                        || url.contains("test/text1.txt")
                        || url.contains("test/text2.txt")
                        || url.contains("test/text%203.txt"));
                }
            }
        }

    }

    public void test_doGet_file() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test/text1.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doGet("file:" + path);
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals("UTF-8", responseData.getCharSet());
        assertTrue(6 == responseData.getContentLength()
            || 7 == responseData.getContentLength());
        assertNotNull(responseData.getLastModified());
        assertEquals(Constants.GET_METHOD, responseData.getMethod());
        assertEquals("text/plain", responseData.getMimeType());
        assertTrue(responseData.getUrl().endsWith("test/text1.txt"));
        final String content =
            new String(
                InputStreamUtil.getBytes(responseData.getResponseBody()),
                "UTF-8");
        assertEquals("test1", content.trim());
    }

    public void test_doGet_file_with_space() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test/text 3.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doGet(path);
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals("UTF-8", responseData.getCharSet());
        assertEquals(6, responseData.getContentLength());
        assertNotNull(responseData.getLastModified());
        assertEquals(Constants.GET_METHOD, responseData.getMethod());
        assertEquals("text/plain", responseData.getMimeType());
        assertTrue(responseData.getUrl().endsWith("test/text%203.txt"));
        final String content =
            new String(
                InputStreamUtil.getBytes(responseData.getResponseBody()),
                "UTF-8");
        assertEquals("test3\n", content);
    }

    public void test_preprocessUri() {
        String value;
        String result;

        value = "file://test.txt";
        result = "file://test.txt";
        assertEquals(result, fsClient.preprocessUri(value));

        value = "file://test test.txt";
        result = "file://test%20test.txt";
        assertEquals(result, fsClient.preprocessUri(value));

        value = "file://テスト.txt";
        result = "file://%E3%83%86%E3%82%B9%E3%83%88.txt";
        assertEquals(result, fsClient.preprocessUri(value));

        value = "test.txt";
        result = "file://test.txt";
        assertEquals(result, fsClient.preprocessUri(value));

        value = "テスト.txt";
        result = "file://%E3%83%86%E3%82%B9%E3%83%88.txt";
        assertEquals(result, fsClient.preprocessUri(value));
    }

    public void test_preprocessUri_null() {
        try {
            fsClient.preprocessUri(null);
            fail();
        } catch (final RobotSystemException e) {
        }
        try {
            fsClient.preprocessUri("");
            fail();
        } catch (final RobotSystemException e) {
        }
    }

    public void test_doHead_file() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test/text1.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doHead("file:" + path);
        assertNotNull(responseData.getLastModified());
        assertTrue(responseData.getLastModified().getTime() < new Date()
            .getTime());
        assertNull(responseData.getResponseBody());
    }

    public void test_doHead_dir() throws Exception {
        final File file = ResourceUtil.getResourceAsFile("test");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        final ResponseData responseData = fsClient.doHead("file:" + path);
        assertNull(responseData);
    }
}
