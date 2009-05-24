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
package org.seasar.robot.http.impl;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.http.HttpClient;

/**
 * @author shinsuke
 *
 */
public class CommonsHttpClientTest extends S2TestCase {
    public HttpClient httpClient;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_doGet() {
        ResponseData responseData = httpClient.doGet("http://www.yahoo.co.jp/");
        assertEquals(200, responseData.getHttpStatusCode());
    }
}
