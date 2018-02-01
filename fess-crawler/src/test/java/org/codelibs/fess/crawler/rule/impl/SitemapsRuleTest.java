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
package org.codelibs.fess.crawler.rule.impl;

import java.io.File;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.ResourceUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.helper.SitemapsHelper;
import org.codelibs.fess.crawler.util.TemporaryFileInputStream;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class SitemapsRuleTest extends PlainTestCase {
    public SitemapsRule sitemapsRule;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        StandardCrawlerContainer container = new StandardCrawlerContainer()
                .singleton("sitemapsHelper", SitemapsHelper.class)//
                .singleton("sitemapsRule", SitemapsRule.class);
        sitemapsRule = container.getComponent("sitemapsRule");
    }

    public void test_match() {
        assertMatchTrue(getTestData1_OK());
        assertMatchTrue(getTestData2_OK());
        assertMatchTrue(getTestData3_OK());
        assertMatchTrue(getTestData4_OK());
        assertMatchFalse(getTestData1_FAIL());
        assertMatchFalse(getTestData2_FAIL());
    }

    private void assertMatchTrue(ResponseData responseData) {
        sitemapsRule.addRule("url", Pattern.compile(".*sitemap.*"));
        assertTrue(sitemapsRule.match(responseData));
        InputStream is = responseData.getResponseBody();
        assertTrue(is instanceof InputStream);
        CloseableUtil.closeQuietly(responseData);
    }

    private void assertMatchFalse(ResponseData responseData) {
        sitemapsRule.addRule("url", Pattern.compile(".*sitemap.*"));
        assertFalse(sitemapsRule.match(responseData));
        InputStream is = responseData.getResponseBody();
        if (is instanceof TemporaryFileInputStream) {
            File temporaryFile = ((TemporaryFileInputStream) is)
                    .getTemporaryFile();
            assertTrue(temporaryFile.exists());
            CloseableUtil.closeQuietly(is);
            assertFalse(temporaryFile.exists());
        } else {
            CloseableUtil.closeQuietly(is);
        }
    }

    private ResponseData getTestData1_OK() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/sitemap.xml");
        File file = ResourceUtil
                .getResourceAsFile("sitemaps/sitemap1.xml");
        responseData.setResponseBody(file, false);
        return responseData;
    }

    private ResponseData getTestData2_OK() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/sitemap.xml.gz");
        File file = ResourceUtil
                .getResourceAsFile("sitemaps/sitemap1.xml.gz");
        responseData.setResponseBody(file, false);
        return responseData;
    }

    private ResponseData getTestData3_OK() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/sitemap.txt");
        File file = ResourceUtil
                .getResourceAsFile("sitemaps/sitemap1.txt");
        responseData.setResponseBody(file, false);
        return responseData;
    }

    private ResponseData getTestData4_OK() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/sitemap/");
        File file = ResourceUtil
                .getResourceAsFile("sitemaps/sitemap1.xml");
        responseData.setResponseBody(file, false);
        return responseData;
    }

    private ResponseData getTestData1_FAIL() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/test.xml");
        File file = ResourceUtil
                .getResourceAsFile("sitemaps/sitemap1.xml");
        responseData.setResponseBody(file, false);
        return responseData;
    }

    private ResponseData getTestData2_FAIL() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/sitemap.xml");
        responseData.setResponseBody(new byte[0]);
        return responseData;
    }
}
