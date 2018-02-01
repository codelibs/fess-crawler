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
package org.codelibs.fess.crawler.client;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class AbstractCrawlerClientTest extends PlainTestCase {

    public void test_checkMaxContentLength() {
        AbstractCrawlerClient client = new AbstractCrawlerClient() {
        };
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://test.com/");

        client.checkMaxContentLength(responseData);

        responseData.setContentLength(-1);
        client.checkMaxContentLength(responseData);

        responseData.setContentLength(1000L);
        client.checkMaxContentLength(responseData);

        responseData.setContentLength(1000000L);
        client.checkMaxContentLength(responseData);

        responseData.setContentLength(1000000000L);
        client.checkMaxContentLength(responseData);

        responseData.setContentLength(1000000000000L);
        client.checkMaxContentLength(responseData);
    }

    public void test_checkMaxContentLength_1m() {
        AbstractCrawlerClient client = new AbstractCrawlerClient() {
        };
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://test.com/");
        client.setMaxContentLength(1000000L);

        client.checkMaxContentLength(responseData);

        responseData.setContentLength(-1);
        client.checkMaxContentLength(responseData);

        responseData.setContentLength(1000L);
        client.checkMaxContentLength(responseData);

        responseData.setContentLength(1000000L);
        client.checkMaxContentLength(responseData);

        responseData.setContentLength(1000001L);
        try {
            client.checkMaxContentLength(responseData);
            fail();
        } catch (MaxLengthExceededException e) {
            // ok
        }

        responseData.setContentLength(1000000000L);
        try {
            client.checkMaxContentLength(responseData);
            fail();
        } catch (MaxLengthExceededException e) {
            // ok
        }

        responseData.setContentLength(1000000000000L);
        try {
            client.checkMaxContentLength(responseData);
            fail();
        } catch (MaxLengthExceededException e) {
            // ok
        }
    }
}
