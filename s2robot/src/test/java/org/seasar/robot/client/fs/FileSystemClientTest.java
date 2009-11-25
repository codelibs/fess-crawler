/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.client.fs;

import java.io.File;
import java.util.Date;
import java.util.Set;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ResponseData;

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
        File file = ResourceUtil.getResourceAsFile("test");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        try {
            fsClient.doGet("file://" + path);
            fail();
        } catch (ChildUrlsException e) {
            Set<String> urlSet = e.getChildUrlList();
            for (String url : urlSet.toArray(new String[urlSet.size()])) {
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
        File file = ResourceUtil.getResourceAsFile("test/text1.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        ResponseData responseData = fsClient.doGet("file:" + path);
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals("UTF-8", responseData.getCharSet());
        assertTrue(6 == responseData.getContentLength()
                || 7 == responseData.getContentLength());
        assertNotNull(responseData.getLastModified());
        assertEquals(Constants.GET_METHOD, responseData.getMethod());
        assertEquals("text/plain", responseData.getMimeType());
        assertTrue(responseData.getUrl().endsWith("test/text1.txt"));
        String content = new String(InputStreamUtil.getBytes(responseData
                .getResponseBody()), "UTF-8");
        assertEquals("test1", content.trim());
    }

    public void test_doGet_file_with_space() throws Exception {
        File file = ResourceUtil.getResourceAsFile("test/text 3.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        ResponseData responseData = fsClient.doGet(path);
        assertEquals(200, responseData.getHttpStatusCode());
        assertEquals("UTF-8", responseData.getCharSet());
        assertEquals(6, responseData.getContentLength());
        assertNotNull(responseData.getLastModified());
        assertEquals(Constants.GET_METHOD, responseData.getMethod());
        assertEquals("text/plain", responseData.getMimeType());
        assertTrue(responseData.getUrl().endsWith("test/text%203.txt"));
        String content = new String(InputStreamUtil.getBytes(responseData
                .getResponseBody()), "UTF-8");
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
        } catch (RobotSystemException e) {
        }
        try {
            fsClient.preprocessUri("");
            fail();
        } catch (RobotSystemException e) {
        }
    }

    public void test_doHead_file() throws Exception {
        File file = ResourceUtil.getResourceAsFile("test/text1.txt");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        ResponseData responseData = fsClient.doHead("file:" + path);
        assertNotNull(responseData.getLastModified());
        assertTrue(responseData.getLastModified().getTime() < new Date()
                .getTime());
    }

    public void test_doHead_dir() throws Exception {
        File file = ResourceUtil.getResourceAsFile("test");
        String path = file.getAbsolutePath();
        if (!path.startsWith("/")) {
            path = "/" + path.replace('\\', '/');
        }
        ResponseData responseData = fsClient.doHead("file:" + path);
        assertNull(responseData);
    }
}
