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
package org.seasar.robot.extractor.impl;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.ResourceUtil;
import org.seasar.robot.RobotSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class ZipExtractorTest extends S2TestCase {
    private static final Logger logger = LoggerFactory
            .getLogger(ZipExtractorTest.class);

    public ZipExtractor zipExtractor;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_getText() {
        InputStream in = ResourceUtil
                .getResourceAsStream("extractor/zip/test.zip");
        String content = zipExtractor.getText(in);
        IOUtils.closeQuietly(in);
        logger.info(content);
        assertTrue(content.contains("テスト"));
        assertTrue(content.contains("テキスト"));
    }

    public void test_getText_null() {
        try {
            zipExtractor.getText(null);
            fail();
        } catch (RobotSystemException e) {
            // NOP
        }
    }
}
