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

import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResultDataImpl;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.extractor.impl.TikaExtractor;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class TextTransformerTest extends PlainTestCase {
    public TextTransformer textTransformer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("extractorFactory", ExtractorFactory.class)
                .singleton("textTransformer", TextTransformer.class)
                .singleton("tikaExtractor", TikaExtractor.class);
        textTransformer = container.getComponent("textTransformer");
        textTransformer.setName("textTransformer");
        ExtractorFactory extractorFactory = container
                .getComponent("extractorFactory");
        TikaExtractor tikaExtractor = container.getComponent("tikaExtractor");
        extractorFactory.addExtractor("text/plain", tikaExtractor);
        extractorFactory.addExtractor("text/html", tikaExtractor);
    }

    public void test_name() {
        assertEquals("textTransformer", textTransformer.getName());
    }

    public void test_transform_text() throws Exception {
        final byte[] data = new String("xyz").getBytes();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("file:/test.txt");
        responseData.setCharSet(Constants.UTF_8);
        responseData.setResponseBody(data);
        responseData.setMimeType("text/plain");
        final ResultData resultData = textTransformer.transform(responseData);
        assertEquals("xyz",
                new String(resultData.getData(), resultData.getEncoding()));
    }

    public void test_transform_html() throws Exception {
        final byte[] data = new String("<html><body>xyz</body></html>")
                .getBytes();
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("file:/test.html");
        responseData.setCharSet(Constants.UTF_8);
        responseData.setResponseBody(data);
        responseData.setMimeType("text/html");
        final ResultData resultData = textTransformer.transform(responseData);
        assertEquals("xyz",
                new String(resultData.getData(), resultData.getEncoding()));
    }

    public void test_transform_null() {
        try {
            textTransformer.transform(null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }

    public void test_getData() throws Exception {
        final AccessResultDataImpl accessResultData = new AccessResultDataImpl();
        accessResultData.setTransformerName("textTransformer");
        accessResultData.setData("xyz".getBytes());

        final Object obj = textTransformer.getData(accessResultData);
        assertNotNull(obj);
        assertTrue(obj instanceof String);
        assertEquals("xyz", obj.toString());
    }

    public void test_getData_wrongName() throws Exception {
        final AccessResultDataImpl accessResultData = new AccessResultDataImpl();
        accessResultData.setTransformerName("transformer");
        accessResultData.setData("xyz".getBytes());

        try {
            textTransformer.getData(accessResultData);
            fail();
        } catch (final CrawlerSystemException e) {
        }
    }

    public void test_getData_nullData() throws Exception {
        final AccessResultDataImpl accessResultData = new AccessResultDataImpl();
        accessResultData.setTransformerName("textTransformer");
        accessResultData.setData(null);

        final Object obj = textTransformer.getData(accessResultData);
        assertNull(obj);
    }
}
