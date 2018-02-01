/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.transformer.impl;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultDataImpl;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class FileTransformerTest extends PlainTestCase {
    public FileTransformer fileTransformer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fileTransformer = new FileTransformer();
        fileTransformer.setName("fileTransformer");
        Map<String, String> featureMap = newHashMap();
        featureMap.put("http://xml.org/sax/features/namespaces", "false");
        fileTransformer.setFeatureMap(featureMap);
        Map<String, String> propertyMap = newHashMap();
        fileTransformer.setPropertyMap(propertyMap);
        Map<String, String> childUrlRuleMap = newHashMap();
        childUrlRuleMap.put("//A", "href");
        childUrlRuleMap.put("//AREA", "href");
        childUrlRuleMap.put("//FRAME", "src");
        childUrlRuleMap.put("//IFRAME", "src");
        childUrlRuleMap.put("//IMG", "src");
        childUrlRuleMap.put("//LINK", "href");
        childUrlRuleMap.put("//SCRIPT", "src");
        fileTransformer.setChildUrlRuleMap(childUrlRuleMap);
    }

    protected void setBaseDir() throws IOException {
        fileTransformer.baseDir = File.createTempFile("crawler-", "");
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
        assertEquals("http_CLN_/www.example.com/index.html",
                fileTransformer.getFilePath(url));

        url = "http://www.example.com/action?a=1";
        assertEquals("http_CLN_/www.example.com/action_QUEST_a=1",
                fileTransformer.getFilePath(url));

        url = "http://www.example.com/action?a=1&b=2";
        assertEquals("http_CLN_/www.example.com/action_QUEST_a=1_AMP_b=2",
                fileTransformer.getFilePath(url));
    }

    public void test_transform() throws Exception {
        final byte[] data = new String("xyz").getBytes();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://www.example.com/submit?a=1&b=2");
        responseData.setResponseBody(data);
        responseData.setCharSet("UTF-8");
        setBaseDir();
        final ResultData resultData = fileTransformer.transform(responseData);
        assertEquals("http_CLN_/www.example.com/submit_QUEST_a=1_AMP_b=2",
                new String(resultData.getData(), "UTF-8"));
        final File file = new File(fileTransformer.baseDir, new String(
                resultData.getData(), "UTF-8"));
        assertEquals("xyz", new String(FileUtil.readBytes(file)));
    }

    public void test_createFile() throws Exception {
        fileTransformer.baseDir = File.createTempFile("crawler-", "");
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
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "foo1/hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path);
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "foo1/foo2/hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path);
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path + "_0");
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, path + "_1");
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html/hoge2.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, "hoge.html_2"
                + File.separator + "hoge2.html");
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html/hoge3.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, "hoge.html_2"
                + File.separator + "hoge3.html");
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());

        path = "hoge.html/hoge2.html";
        file = fileTransformer.createFile(path);
        resultFile = new File(fileTransformer.baseDir, "hoge.html_2"
                + File.separator + "hoge2.html_0");
        assertEquals(resultFile, file);
        FileUtil.writeBytes(file.getAbsolutePath(), "abc".getBytes());
    }

    public void test_getData() throws Exception {
        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData("hoge.txt".getBytes());
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("fileTransformer");

        setBaseDir();

        final Object obj = fileTransformer.getData(accessResultDataImpl);
        assertTrue(obj instanceof File);
        assertEquals(new File(fileTransformer.baseDir, "hoge.txt"), obj);
    }

    public void test_getData_wrongName() throws Exception {
        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData("hoge.txt".getBytes());
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("transformer");

        setBaseDir();

        try {
            final Object obj = fileTransformer.getData(accessResultDataImpl);
            fail();
        } catch (final CrawlerSystemException e) {
        }
    }

    public void test_getData_nullData() throws Exception {
        final AccessResultDataImpl accessResultDataImpl = new AccessResultDataImpl();
        accessResultDataImpl.setData(null);
        accessResultDataImpl.setEncoding(Constants.UTF_8);
        accessResultDataImpl.setTransformerName("fileTransformer");

        setBaseDir();

        final Object obj = fileTransformer.getData(accessResultDataImpl);
        assertNull(obj);
    }
}
