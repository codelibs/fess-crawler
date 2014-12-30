/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.extractor.impl;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.container.SimpleComponentContainer;
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
        SimpleComponentContainer container = new SimpleComponentContainer()
                .singleton("zipExtractor", ZipExtractor.class);
        zipExtractor = container.getComponent("zipExtractor");
    }

    public void test_getText() {
        final InputStream in = ResourceUtil
                .getResourceAsStream("extractor/zip/test.zip");
        final String content = zipExtractor.getText(in, null).getContent();
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    public void test_getText_null() {
        try {
            zipExtractor.getText(null, null);
            fail();
        } catch (final RobotSystemException e) {
            // NOP
        }
    }
}
