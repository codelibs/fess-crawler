/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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

import java.io.InputStream;

import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class MsPowerPointExtractorTest extends PlainTestCase {
    private static final Logger logger = LoggerFactory.getLogger(MsPowerPointExtractorTest.class);

    public MsPowerPointExtractor msPowerPointExtractor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("msPowerPointExtractor", MsPowerPointExtractor.class);
        msPowerPointExtractor = container.getComponent("msPowerPointExtractor");
    }

    public void test_getText() {
        final InputStream in = ResourceUtil.getResourceAsStream("extractor/msoffice/test.ppt");
        final String content = msPowerPointExtractor.getText(in, null).getContent();
        CloseableUtil.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
    }

    public void test_getText_null() {
        try {
            msPowerPointExtractor.getText(null, null);
            fail();
        } catch (final CrawlerSystemException e) {
            // NOP
        }
    }
}
