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
package org.codelibs.fess.crawler.extractor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.extractor.impl.LhaExtractor;
import org.codelibs.fess.crawler.extractor.impl.PasswordBasedExtractor;
import org.codelibs.fess.crawler.extractor.impl.PdfExtractor;
import org.codelibs.fess.crawler.extractor.impl.TikaExtractor;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 *
 */
public class ExtractorFactoryTest extends PlainTestCase {
    public ExtractorFactory extractorFactory;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("tikaExtractor", TikaExtractor.class)//
                .singleton("pdfExtractor", PdfExtractor.class)//
                .singleton("lhaExtractor", LhaExtractor.class)//
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("contentLengthHelper", ContentLengthHelper.class)//
                .singleton("extractorFactory", ExtractorFactory.class);
        extractorFactory = container.getComponent("extractorFactory");
        TikaExtractor tikaExtractor = container.getComponent("tikaExtractor");
        LhaExtractor lhaExtractor = container.getComponent("lhaExtractor");
        PasswordBasedExtractor pdfExtractor = container.getComponent("pdfExtractor");
        extractorFactory.addExtractor("application/msword", tikaExtractor);
        extractorFactory.addExtractor("application/vnd.ms-excel", tikaExtractor);
        extractorFactory.addExtractor("application/vnd.ms-powerpoint", tikaExtractor);
        extractorFactory.addExtractor("application/vnd.visio", tikaExtractor);
        extractorFactory.addExtractor("application/pdf", pdfExtractor);
        extractorFactory.addExtractor("application/x-lha", lhaExtractor);
        extractorFactory.addExtractor("application/x-lharc", lhaExtractor);

    }

    public void test_addExtractor() {
        final ExtractorFactory extractorFactory = new ExtractorFactory();
        final Extractor extractor = new Extractor() {
            public ExtractData getText(final InputStream in, final Map<String, String> params) {
                return null;
            }
        };

        assertNull(extractorFactory.getExtractor("test"));
        extractorFactory.addExtractor("test", extractor);
        assertEquals(extractor, extractorFactory.getExtractor("test"));
    }

    public void test_addExtractor_list() {
        final ExtractorFactory extractorFactory = new ExtractorFactory();
        final Extractor extractor = new Extractor() {
            public ExtractData getText(final InputStream in, final Map<String, String> params) {
                return null;
            }
        };

        assertNull(extractorFactory.getExtractor("test"));
        final List<String> list = new ArrayList<String>();
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

        // key = "application/vnd.ms-publisher";
        // assertTrue(extractorFactory.getExtractor(key) instanceof
        // TikaExtractor);

        key = "application/pdf";
        assertTrue(extractorFactory.getExtractor(key) instanceof PdfExtractor);

        key = "application/x-lha";
        assertTrue(extractorFactory.getExtractor(key) instanceof LhaExtractor);
        key = "application/x-lharc";
        assertTrue(extractorFactory.getExtractor(key) instanceof LhaExtractor);

    }

    public void test_builder() {
        assertEquals("test", extractorFactory.builder(new ByteArrayInputStream("test".getBytes()), null).extract().getContent());
        assertEquals("test",
                extractorFactory.builder(new ByteArrayInputStream("test".getBytes()), null).filename("test.txt").extract().getContent());
    }

    public void test_addExtractor_weight() {
        final String key = "application/test";
        assertNull(extractorFactory.getExtractor(key));
        extractorFactory.addExtractor(key, new Extractor() {
            @Override
            public ExtractData getText(InputStream in, Map<String, String> params) {
                return null;
            }
        });
        assertEquals(1, extractorFactory.getExtractor(key).getWeight());
        assertEquals(1, extractorFactory.getExtractors(key).length);
        extractorFactory.addExtractor(key, new Extractor() {
            @Override
            public ExtractData getText(InputStream in, Map<String, String> params) {
                return null;
            }

            @Override
            public int getWeight() {
                return 10;
            }
        });
        assertEquals(10, extractorFactory.getExtractor(key).getWeight());
        assertEquals(1, extractorFactory.getExtractors(key)[1].getWeight());
        assertEquals(2, extractorFactory.getExtractors(key).length);
        extractorFactory.addExtractor(key, new Extractor() {
            @Override
            public ExtractData getText(InputStream in, Map<String, String> params) {
                return null;
            }

            @Override
            public int getWeight() {
                return 5;
            }
        });
        assertEquals(10, extractorFactory.getExtractor(key).getWeight());
        assertEquals(5, extractorFactory.getExtractors(key)[1].getWeight());
        assertEquals(1, extractorFactory.getExtractors(key)[2].getWeight());
        assertEquals(3, extractorFactory.getExtractors(key).length);
    }
}
