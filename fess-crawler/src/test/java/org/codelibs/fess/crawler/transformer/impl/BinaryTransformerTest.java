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

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codelibs.fess.crawler.entity.AccessResultDataImpl;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class BinaryTransformerTest extends PlainTestCase {
    public BinaryTransformer binaryTransformer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        binaryTransformer = new BinaryTransformer();
        binaryTransformer.setName("binaryTransformer");
    }

    public void test_name() {
        assertEquals("binaryTransformer", binaryTransformer.getName());
    }

    public void test_transform() {
        final byte[] data = new String("xyz").getBytes();
        final ResponseData responseData = new ResponseData();
        responseData.setResponseBody(data);
        final ResultData resultData = binaryTransformer.transform(responseData);
        assertEquals("xyz", new String(resultData.getData()));
    }

    public void test_transform_null() {
        try {
            binaryTransformer.transform(null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    public void test_getData() throws Exception {
        final AccessResultDataImpl accessResultData = new AccessResultDataImpl();
        accessResultData.setTransformerName("binaryTransformer");
        accessResultData.setData("xyz".getBytes());

        final Object obj = binaryTransformer.getData(accessResultData);
        assertNotNull(obj);
        assertTrue(obj instanceof InputStream);
        assertEquals("xyz", new String(IOUtils.toByteArray((InputStream) obj)));
    }

    public void test_getData_wrongName() throws Exception {
        final AccessResultDataImpl accessResultData = new AccessResultDataImpl();
        accessResultData.setTransformerName("transformer");
        accessResultData.setData("xyz".getBytes());

        try {
            binaryTransformer.getData(accessResultData);
            fail();
        } catch (final CrawlerSystemException e) {
        }
    }

    public void test_getData_nullData() throws Exception {
        final AccessResultDataImpl accessResultData = new AccessResultDataImpl();
        accessResultData.setTransformerName("binaryTransformer");
        accessResultData.setData(null);

        final Object obj = binaryTransformer.getData(accessResultData);
        assertNull(obj);
    }
}
