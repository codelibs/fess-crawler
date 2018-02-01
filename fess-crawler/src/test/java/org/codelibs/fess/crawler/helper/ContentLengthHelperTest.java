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
package org.codelibs.fess.crawler.helper;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 *
 */
public class ContentLengthHelperTest extends PlainTestCase {
    private static long DEFAULT_MAX_LENGTH = 10L * 1024L * 1024L;

    public ContentLengthHelper contentLengthHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("contentLengthHelper", ContentLengthHelper.class);
        contentLengthHelper = container.getComponent("contentLengthHelper");
    }

    public void test_getMaxLength() {
        final String mimeType = "text/plain";

        assertEquals(DEFAULT_MAX_LENGTH,
                contentLengthHelper.getMaxLength(mimeType));
        contentLengthHelper.addMaxLength(mimeType, 1000L);
        assertEquals(1000L, contentLengthHelper.getMaxLength(mimeType));
    }

    public void test_getMaxLength_blank() {
        String mimeType;

        mimeType = null;
        assertEquals(DEFAULT_MAX_LENGTH,
                contentLengthHelper.getMaxLength(mimeType));

        mimeType = "";
        assertEquals(DEFAULT_MAX_LENGTH,
                contentLengthHelper.getMaxLength(mimeType));

        mimeType = " ";
        assertEquals(DEFAULT_MAX_LENGTH,
                contentLengthHelper.getMaxLength(mimeType));
    }
}
