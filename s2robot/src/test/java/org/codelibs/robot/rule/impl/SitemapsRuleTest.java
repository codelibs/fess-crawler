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
package org.codelibs.robot.rule.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.util.TemporaryFileInputStream;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.framework.util.ResourceUtil;

/**
 * @author shinsuke
 * 
 */
public class SitemapsRuleTest extends S2TestCase {
    public SitemapsRule sitemapsRule;

    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
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
        assertTrue(is instanceof TemporaryFileInputStream);
        File temporaryFile =
            ((TemporaryFileInputStream) is).getTemporaryFile();
        assertTrue(temporaryFile.exists());
        IOUtils.closeQuietly(is);
        assertFalse(temporaryFile.exists());
    }

    private void assertMatchFalse(ResponseData responseData) {
        sitemapsRule.addRule("url", Pattern.compile(".*sitemap.*"));
        assertFalse(sitemapsRule.match(responseData));
        InputStream is = responseData.getResponseBody();
        if (is instanceof TemporaryFileInputStream) {
            File temporaryFile =
                ((TemporaryFileInputStream) is).getTemporaryFile();
            assertTrue(temporaryFile.exists());
            IOUtils.closeQuietly(is);
            assertFalse(temporaryFile.exists());
        } else {
            IOUtils.closeQuietly(is);
        }
    }

    private ResponseData getTestData1_OK() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/sitemap.xml");
        InputStream is =
            ResourceUtil.getResourceAsStream("sitemaps/sitemap1.xml");
        responseData.setResponseBody(is);
        return responseData;
    }

    private ResponseData getTestData2_OK() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/sitemap.xml.gz");
        InputStream is =
            ResourceUtil.getResourceAsStream("sitemaps/sitemap1.xml.gz");
        responseData.setResponseBody(is);
        return responseData;
    }

    private ResponseData getTestData3_OK() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/sitemap.txt");
        InputStream is =
            ResourceUtil.getResourceAsStream("sitemaps/sitemap1.txt");
        responseData.setResponseBody(is);
        return responseData;
    }

    private ResponseData getTestData4_OK() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/sitemap/");
        InputStream is =
            ResourceUtil.getResourceAsStream("sitemaps/sitemap1.xml");
        responseData.setResponseBody(is);
        return responseData;
    }

    private ResponseData getTestData1_FAIL() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/test.xml");
        InputStream is =
            ResourceUtil.getResourceAsStream("sitemaps/sitemap1.xml");
        responseData.setResponseBody(is);
        return responseData;
    }

    private ResponseData getTestData2_FAIL() {
        final ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/sitemap.xml");
        responseData.setResponseBody(new ByteArrayInputStream(new byte[0]));
        return responseData;
    }
}
