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

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.FileUtil;
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

    public void test_name() {
        assertEquals("fileTransformer", fileTransformer.getName());
    }

    public void test_getFilePath_unix() {
        String url;

        fileTransformer.fileSeparator = "/";

        url = "http://www.n2sm.net/";
        assertEquals("http_CLN_/www.n2sm.net/index.html", fileTransformer
                .getFilePath(url));

        url = "http://www.n2sm.net/action?a=1";
        assertEquals("http_CLN_/www.n2sm.net/action_QUEST_a=1", fileTransformer
                .getFilePath(url));

        url = "http://www.n2sm.net/action?a=1&b=2";
        assertEquals("http_CLN_/www.n2sm.net/action_QUEST_a=1_AMP_b=2",
                fileTransformer.getFilePath(url));
    }

    public void test_getFilePath_windows() {
        String url;

        fileTransformer.fileSeparator = "\\";

        url = "http://www.n2sm.net/";
        assertEquals("http_CLN_\\www.n2sm.net\\index.html", fileTransformer
                .getFilePath(url));

        url = "http://www.n2sm.net/action?a=1";
        assertEquals("http_CLN_\\www.n2sm.net\\action_QUEST_a=1",
                fileTransformer.getFilePath(url));

        url = "http://www.n2sm.net/action?a=1&b=2";
        assertEquals("http_CLN_\\www.n2sm.net\\action_QUEST_a=1_AMP_b=2",
                fileTransformer.getFilePath(url));
    }

    public void test_transform() throws Exception {
        byte[] data = new String("xyz").getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://www.hoge.com/submit?a=1&b=2");
        responseData.setResponseBody(bais);
        fileTransformer.baseDir = File.createTempFile("s2robot-", "");
        fileTransformer.baseDir.delete();
        fileTransformer.baseDir.mkdirs();
        fileTransformer.baseDir.deleteOnExit();
        ResultData resultData = fileTransformer.transform(responseData);
        assertEquals("http_CLN_" + File.separator + "www.hoge.com"
                + File.separator + "submit_QUEST_a=1_AMP_b=2", resultData
                .getData());
        File file = new File(fileTransformer.baseDir, resultData.getData());
        assertEquals("xyz", new String(FileUtil.getBytes(file)));
    }
}
