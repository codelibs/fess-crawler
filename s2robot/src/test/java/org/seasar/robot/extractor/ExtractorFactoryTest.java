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
package org.seasar.robot.extractor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.extractor.impl.TikaExtractor;

/**
 * @author shinsuke
 *
 */
public class ExtractorFactoryTest extends S2TestCase {
    public ExtractorFactory extractorFactory;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_addExtractor() {
        ExtractorFactory extractorFactory = new ExtractorFactory();
        Extractor extractor = new Extractor() {
            public ExtractData getText(InputStream in,
                    Map<String, String> params) {
                return null;
            }
        };

        assertNull(extractorFactory.getExtractor("test"));
        extractorFactory.addExtractor("test", extractor);
        assertEquals(extractor, extractorFactory.getExtractor("test"));
    }

    public void test_addExtractor_list() {
        ExtractorFactory extractorFactory = new ExtractorFactory();
        Extractor extractor = new Extractor() {
            public ExtractData getText(InputStream in,
                    Map<String, String> params) {
                return null;
            }
        };

        assertNull(extractorFactory.getExtractor("test"));
        List<String> list = new ArrayList<String>();
        list.add("test");
        extractorFactory.addExtractor(list, extractor);
        assertEquals(extractor, extractorFactory.getExtractor("test"));
    }

    public void test_getExtractor() {
        String key;

        key = "application/msword";
        assertTrue(extractorFactory.getExtractor(key) instanceof TikaExtractor);

        key = "application/vnd.ms-excel";
        assertTrue(extractorFactory.getExtractor(key) instanceof TikaExtractor);

        key = "application/vnd.ms-powerpoint";
        assertTrue(extractorFactory.getExtractor(key) instanceof TikaExtractor);

        key = "application/vnd.visio";
        assertTrue(extractorFactory.getExtractor(key) instanceof TikaExtractor);

        //        key = "application/vnd.ms-publisher";
        //        assertTrue(extractorFactory.getExtractor(key) instanceof TikaExtractor);

        key = "application/pdf";
        assertTrue(extractorFactory.getExtractor(key) instanceof TikaExtractor);

    }
}
