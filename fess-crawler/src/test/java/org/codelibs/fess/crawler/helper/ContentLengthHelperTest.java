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
package org.codelibs.fess.crawler.helper;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class ContentLengthHelperTest extends PlainTestCase {
    private static long DEFAULT_MAX_LENGTH = 10L * 1024L * 1024L;

    public ContentLengthHelper contentLengthHelper;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("contentLengthHelper", ContentLengthHelper.class);
        contentLengthHelper = container.getComponent("contentLengthHelper");
    }

    @Test
    public void test_getMaxLength() {
        final String mimeType = "text/plain";

        assertEquals(DEFAULT_MAX_LENGTH, contentLengthHelper.getMaxLength(mimeType));
        contentLengthHelper.addMaxLength(mimeType, 1000L);
        assertEquals(1000L, contentLengthHelper.getMaxLength(mimeType));
    }

    @Test
    public void test_getMaxLength_blank() {
        String mimeType;

        mimeType = null;
        assertEquals(DEFAULT_MAX_LENGTH, contentLengthHelper.getMaxLength(mimeType));

        mimeType = "";
        assertEquals(DEFAULT_MAX_LENGTH, contentLengthHelper.getMaxLength(mimeType));

        mimeType = " ";
        assertEquals(DEFAULT_MAX_LENGTH, contentLengthHelper.getMaxLength(mimeType));
    }

    @Test
    public void test_addMaxLength_blankMimeType() {
        try {
            contentLengthHelper.addMaxLength(null, 1000L);
            fail();
        } catch (final CrawlerSystemException e) {
            assertTrue(e.getMessage().contains("blank"));
        }

        try {
            contentLengthHelper.addMaxLength("", 1000L);
            fail();
        } catch (final CrawlerSystemException e) {
            assertTrue(e.getMessage().contains("blank"));
        }

        try {
            contentLengthHelper.addMaxLength("   ", 1000L);
            fail();
        } catch (final CrawlerSystemException e) {
            assertTrue(e.getMessage().contains("blank"));
        }
    }

    @Test
    public void test_addMaxLength_negativeValue() {
        try {
            contentLengthHelper.addMaxLength("text/plain", -1L);
            fail();
        } catch (final CrawlerSystemException e) {
            assertTrue(e.getMessage().contains("invalid"));
        }
    }

    @Test
    public void test_addMaxLength_zero() {
        // Zero should be valid
        contentLengthHelper.addMaxLength("text/zero", 0L);
        assertEquals(0L, contentLengthHelper.getMaxLength("text/zero"));
    }

    @Test
    public void test_getDefaultMaxLength() {
        assertEquals(DEFAULT_MAX_LENGTH, contentLengthHelper.getDefaultMaxLength());
    }

    @Test
    public void test_setDefaultMaxLength() {
        final long newDefault = 20L * 1024L * 1024L;
        contentLengthHelper.setDefaultMaxLength(newDefault);
        assertEquals(newDefault, contentLengthHelper.getDefaultMaxLength());

        // Verify that unmapped mime types use the new default
        assertEquals(newDefault, contentLengthHelper.getMaxLength("application/unknown"));
    }

    @Test
    public void test_setDefaultMaxLength_negativeValue() {
        try {
            contentLengthHelper.setDefaultMaxLength(-1L);
            fail();
        } catch (final CrawlerSystemException e) {
            assertTrue(e.getMessage().contains("invalid"));
        }
    }

    @Test
    public void test_setDefaultMaxLength_zero() {
        // Zero should be valid
        contentLengthHelper.setDefaultMaxLength(0L);
        assertEquals(0L, contentLengthHelper.getDefaultMaxLength());
    }

    @Test
    public void test_multipleMimeTypes() {
        contentLengthHelper.addMaxLength("text/html", 5000L);
        contentLengthHelper.addMaxLength("text/plain", 3000L);
        contentLengthHelper.addMaxLength("application/pdf", 50000000L);

        assertEquals(5000L, contentLengthHelper.getMaxLength("text/html"));
        assertEquals(3000L, contentLengthHelper.getMaxLength("text/plain"));
        assertEquals(50000000L, contentLengthHelper.getMaxLength("application/pdf"));
        assertEquals(DEFAULT_MAX_LENGTH, contentLengthHelper.getMaxLength("image/jpeg"));
    }

    @Test
    public void test_overwriteMaxLength() {
        final String mimeType = "text/css";

        contentLengthHelper.addMaxLength(mimeType, 1000L);
        assertEquals(1000L, contentLengthHelper.getMaxLength(mimeType));

        contentLengthHelper.addMaxLength(mimeType, 2000L);
        assertEquals(2000L, contentLengthHelper.getMaxLength(mimeType));
    }

    @Test
    public void test_constructor() {
        final ContentLengthHelper helper = new ContentLengthHelper();
        assertNotNull(helper);
        assertEquals(DEFAULT_MAX_LENGTH, helper.getDefaultMaxLength());
    }
}
