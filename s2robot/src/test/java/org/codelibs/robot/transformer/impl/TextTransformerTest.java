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
package org.codelibs.robot.transformer.impl;

import java.io.ByteArrayInputStream;

import org.codelibs.robot.Constants;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.entity.AccessResultDataImpl;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.entity.ResultData;
import org.seasar.extension.unit.S2TestCase;

/**
 * @author shinsuke
 * 
 */
public class TextTransformerTest extends S2TestCase {
    public TextTransformer textTransformer;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_name() {
        assertEquals("textTransformer", textTransformer.getName());
    }

    public void test_transform_text() throws Exception {
        final byte[] data = new String("xyz").getBytes();
        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("file:/test.txt");
        responseData.setCharSet(Constants.UTF_8);
        responseData.setResponseBody(bais);
        responseData.setMimeType("text/plain");
        final ResultData resultData = textTransformer.transform(responseData);
        assertEquals(
            "xyz",
            new String(resultData.getData(), resultData.getEncoding()));
    }

    public void test_transform_html() throws Exception {
        final byte[] data =
            new String("<html><body>xyz</body></html>").getBytes();
        final ByteArrayInputStream bais = new ByteArrayInputStream(data);
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("file:/test.html");
        responseData.setCharSet(Constants.UTF_8);
        responseData.setResponseBody(bais);
        responseData.setMimeType("text/html");
        final ResultData resultData = textTransformer.transform(responseData);
        assertEquals(
            "xyz",
            new String(resultData.getData(), resultData.getEncoding()));
    }

    public void test_transform_null() {
        try {
            textTransformer.transform(null);
            fail();
        } catch (final RobotSystemException e) {
            // NOP
        }
    }

    public void test_getData() throws Exception {
        final AccessResultDataImpl accessResultData =
            new AccessResultDataImpl();
        accessResultData.setTransformerName("textTransformer");
        accessResultData.setData("xyz".getBytes());

        final Object obj = textTransformer.getData(accessResultData);
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        assertEquals("xyz", obj.toString());
    }

    public void test_getData_wrongName() throws Exception {
        final AccessResultDataImpl accessResultData =
            new AccessResultDataImpl();
        accessResultData.setTransformerName("transformer");
        accessResultData.setData("xyz".getBytes());

        try {
            textTransformer.getData(accessResultData);
            fail();
        } catch (final RobotSystemException e) {
        }
    }

    public void test_getData_nullData() throws Exception {
        final AccessResultDataImpl accessResultData =
            new AccessResultDataImpl();
        accessResultData.setTransformerName("textTransformer");
        accessResultData.setData(null);

        final Object obj = textTransformer.getData(accessResultData);
        assertNull(obj);
    }
}
