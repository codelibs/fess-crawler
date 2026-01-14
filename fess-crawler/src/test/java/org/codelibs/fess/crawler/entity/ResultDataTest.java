/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.entity;

import java.util.function.Function;

import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

public class ResultDataTest extends PlainTestCase {

    @Test
    public void test_setRawData() {
        // ## Arrange ##
        final ResultData resultData = new ResultData();
        final Object rawData = new Object();

        // ## Act ##
        resultData.setRawData(rawData);

        // ## Assert ##
        assertEquals(rawData, resultData.getRawData());

        assertNull(resultData.serializer);
        try {
            resultData.getData();
            fail();
        } catch (CrawlerSystemException e) {
            // nothing
        }
    }

    @Test
    public void test_setSerializer() {
        // ## Arrange ##
        final ResultData resultData = new ResultData();
        final Function<Object, byte[]> serializer = new Function<Object, byte[]>() {
            @Override
            public byte[] apply(Object t) {
                return t.toString().getBytes();
            }
        };

        // ## Act ##
        resultData.setSerializer(serializer);

        // ## Assert ##
        assertNotNull(resultData.serializer);

        resultData.setRawData("hoge");
        assertEquals("hoge", new String(resultData.getData()));
    }
}
