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

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;

/**
 * @author shinsuke
 * 
 */
public class Base64TransformerTest extends S2TestCase {
    public Base64Transformer base64Transformer;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_name() {
        assertEquals("base64Transformer", base64Transformer.getName());
    }

    public void test_transform() {
        byte[] data = new String("xyz").getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ResponseData responseData = new ResponseData();
        responseData.setResponseBody(bais);
        ResultData resultData = base64Transformer.transform(responseData);
        assertEquals("eHl6", resultData.getData());
    }

    public void test_transform_null() {
        try {
            base64Transformer.transform(null);
            fail();
        } catch (RobotSystemException e) {
            // NOP
        }
    }
}
