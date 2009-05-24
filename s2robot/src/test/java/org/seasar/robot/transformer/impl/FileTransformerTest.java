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
package org.seasar.robot.transformer.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.FileUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.AccessResultDataImpl;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;

/**
 * @author shinsuke
 * 
 */
public class FileTransformerTest extends S2TestCase {
    public FileTransformer fileTransformer;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    protected void setBaseDir() throws IOException {
        fileTransformer.baseDir = File.createTempFile("s2robot-", "");
        fileTransformer.baseDir.delete();
        fileTransformer.baseDir.mkdirs();
        fileTransformer.baseDir.deleteOnExit();
    }

    public void test_name() {
        assertEquals("fileTransformer", fileTransformer.getName());
    }

    public void test_getFilePath() {
        String url;

        url = "http://www.example.com/";
        assertEquals("http_CLN_/www.example.com/index.html", fileTransformer
                .getFilePath(url));

        url = "http://www.example.com/action?a=1";
        assertEquals("http_CLN_/www.example.com/action_QUEST_a=1",
                fileTransformer.getFilePath(url));

        url = "http://www.example.com/action?a=1&b=2";
        assertEquals("http_CLN_/www.example.com/action_QUEST_a=1_AMP_b=2",
                fileTransformer.getFilePath(url));
    }

    public void test_transform() throws Exception {
        byte[] data = new String("xyz").getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://www.example.com/submit?a=1&b=2");
        responseData.setResponseBody(bais);
        responseData.setCharSet("UTF-8");
        setBaseDir();
        ResultData resultData = fileTransformer.transform(responseData);
        assertEquals("http_CLN_/www.example.com/submit_QUEST_a=1_AMP_b=2",
                new String(resultData.getData(), "UTF-8"));
        File file = new File(fileTransformer.baseDir, new String(resultData
                .getData(), "UTF-8"));
        assertEquals("xyz", new String(FileUtil.getBytes(file)));
    }

    public void test_createFile() throws Exception {
        fileTransformer.baseDir = File.createTempFile("s2robot-", "");
        fileTransformer.baseDir.delete();
        fileTransformer.baseDir.mkdirs();
        fileTransformer.baseDir.deleteOnExit();

        String path;
        File file;
        File resultFile;

        path = "hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path);
        assertEquals(resultFile, file);
        FileUtil.write(file.getAbsolutePath(), "abc".getBytes());

        path = "foo1/hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path);
        assertEquals(resultFile, file);
        FileUtil.write(file.getAbsolutePath(), "abc".getBytes());

        path = "foo1/foo2/hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path);
        assertEquals(resultFile, file);
        FileUtil.write(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path + "_0");
        assertEquals(resultFile, file);
        FileUtil.write(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path + "_1");
        assertEquals(resultFile, file);
        FileUtil.write(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html/hoge2.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, "hoge.html_2"
                + File.separator + "hoge2.html");
        assertEquals(resultFile, file);
        FileUtil.write(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html/hoge3.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, "hoge.html_2"
                + File.separator + "hoge3.html");
        assertEquals(resultFile, file);
        FileUtil.write(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html/hoge2.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, "hoge.html_2"
                + File.separator + "hoge2.html_0");
        assertEquals(resultFile, file);
        FileUtil.write(file.getAbsolutePath(), "abc".getBytes());
    }

    public void test_getData() throws Exception {
        AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData("hoge.txt".getBytes());
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("fileTransformer");

        setBaseDir();

        Object obj = fileTransformer.getData(accessResultDataImpl);
        assertTrue(obj instanceof File);
        assertEquals(new File(fileTransformer.baseDir, "hoge.txt"), (File) obj);
    }

    public void test_getData_wrongName() throws Exception {
        AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData("hoge.txt".getBytes());
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("transformer");

        setBaseDir();

        try {
            Object obj = fileTransformer.getData(accessResultDataImpl);
            fail();
        } catch (RobotSystemException e) {
        }
    }

    public void test_getData_nullData() throws Exception {
        AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(null);
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("fileTransformer");

        setBaseDir();

        Object obj = fileTransformer.getData(accessResultDataImpl);
        assertNull(obj);
    }
}
