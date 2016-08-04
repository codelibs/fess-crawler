/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.dbflute.utflute.core.PlainTestCase;


/**
 * @author shinsuke
 * 
 */
public class ApiExtractorTest extends PlainTestCase {

    private ApiExtractor extractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // TODO launch API server for testing

        extractor = new ApiExtractor();
        // TODO set parameters
        extractor.setUrl("http://172.24.231.10:8000/post");
        extractor.init();
    }

    @Override
    protected void tearDown() throws Exception {
        // TODO close API server

        extractor.destroy();

        super.tearDown();
    }

    public void test_getText() throws Exception {
        final String content = "./src/test/resources/extractor/image/test.jpg";
        final Map<String, String> params = new HashMap<String, String>();
        /*
        final ExtractData text = extractor.getText(new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(content))), params);
        assertEquals(content, text.getContent());
        for (String key : text.getKeySet()) {
            for (String s : text.getValues(key)) {
                System.out.println(key + "," + s);
            }
        }
        System.out.println(text.getContent());
        */
    }

    // TODO other tests
}
