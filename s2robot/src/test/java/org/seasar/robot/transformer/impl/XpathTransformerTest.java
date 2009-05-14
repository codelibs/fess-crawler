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

import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;

/**
 * @author shinsuke
 *
 */
public class XpathTransformerTest extends S2TestCase {
    public XpathTransformer xpathTransformer;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_storeData() {
        String result = "<?xml version=\"1.0\"?>\n"//
                + "<doc>\n"//
                + "<field name=\"title\">タイトル</field>\n"//
                + "<field name=\"body\">第一章 第一節 ほげほげふがふが LINK 第2章 第2節</field>\n"//
                + "</doc>";

        ResponseData responseData = new ResponseData();
        responseData.setResponseBody(ResourceUtil
                .getResourceAsStream("html/test1.html"));
        responseData.setCharSet("UTF-8");
        ResultData resultData = new ResultData();
        xpathTransformer.storeData(responseData, resultData);
        assertEquals(result, resultData.getData());
    }
}
