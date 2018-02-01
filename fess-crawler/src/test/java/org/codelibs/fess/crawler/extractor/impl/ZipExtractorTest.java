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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class ZipExtractorTest extends PlainTestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(ZipExtractorTest.class);

    public ZipExtractor zipExtractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer();
        container
                .singleton("archiveStreamFactory", ArchiveStreamFactory.class)
                .singleton("compressorStreamFactory",
                        CompressorStreamFactory.class)
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)
                .singleton("tikaExtractor", TikaExtractor.class)
                .singleton("zipExtractor", ZipExtractor.class)
                .<ExtractorFactory> singleton(
                        "extractorFactory",
                        ExtractorFactory.class,
                        factory -> {
                            TikaExtractor tikaExtractor = container
                                    .getComponent("tikaExtractor");
                            ZipExtractor zipExtractor = container
                                    .getComponent("zipExtractor");
                            factory.addExtractor("text/plain", tikaExtractor);
                            factory.addExtractor("text/html", tikaExtractor);
                            factory.addExtractor("application/zip",
                                    zipExtractor);

                        })//
        ;

        zipExtractor = container.getComponent("zipExtractor");
    }

    public void test_getText() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/zip/test.zip");
        final String content = zipExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    public void test_getText_maxSize() throws IOException {
        try (final InputStream in = ResourceUtil.getResourceAsStream("extractor/zip/test.zip")) {
            zipExtractor.setMaxContentSize(100);
            zipExtractor.getText(in, null);
            fail();
        } catch (MaxLengthExceededException e) {
            // pass
        }
        zipExtractor.setMaxContentSize(-1);
    }

    public void test_getText_null() {
        try {
            zipExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }
}
