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

import java.util.regex.Pattern;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * @author shinsuke
 * 
 */
public class RegexRuleTest extends PlainTestCase {

    public void test_match_default() {
        final RegexRule regexRule = new RegexRule();
        regexRule.defaultRule = true;
        regexRule.allRequired = true;
        regexRule.addRule("url", Pattern.compile("http:.*"));
        regexRule.addRule("mimeType", Pattern.compile("text/html"));

        assertTrue(regexRule.match(getTestData1()));
        assertTrue(regexRule.match(getTestData2()));
        assertTrue(regexRule.match(getTestData3()));
        assertTrue(regexRule.match(getTestData4()));
    }

    public void test_match_url() {
        final RegexRule regexRule = new RegexRule();
        regexRule.defaultRule = false;
        regexRule.allRequired = true;
        regexRule.addRule("url", Pattern.compile("http:.*"));

        assertTrue(regexRule.match(getTestData1()));
        assertFalse(regexRule.match(getTestData2()));
        assertTrue(regexRule.match(getTestData3()));
        assertFalse(regexRule.match(getTestData4()));
    }

    public void test_match_mimeType() {
        final RegexRule regexRule = new RegexRule();
        regexRule.defaultRule = false;
        regexRule.allRequired = true;
        regexRule.addRule("mimeType", Pattern.compile("text/plain"));

        assertFalse(regexRule.match(getTestData1()));
        assertFalse(regexRule.match(getTestData2()));
        assertTrue(regexRule.match(getTestData3()));
        assertTrue(regexRule.match(getTestData4()));
    }

    public void test_match_and() {
        final RegexRule regexRule = new RegexRule();
        regexRule.defaultRule = false;
        regexRule.allRequired = true;
        regexRule.addRule("url", Pattern.compile("http:.*"));
        regexRule.addRule("mimeType", Pattern.compile("text/html"));

        assertTrue(regexRule.match(getTestData1()));
        assertFalse(regexRule.match(getTestData2()));
        assertFalse(regexRule.match(getTestData3()));
        assertFalse(regexRule.match(getTestData4()));
    }

    public void test_match_or() {
        final RegexRule regexRule = new RegexRule();
        regexRule.defaultRule = false;
        regexRule.allRequired = false;
        regexRule.addRule("url", Pattern.compile("http:.*"));
        regexRule.addRule("mimeType", Pattern.compile("text/html"));

        assertTrue(regexRule.match(getTestData1()));
        assertTrue(regexRule.match(getTestData2()));
        assertTrue(regexRule.match(getTestData3()));
        assertFalse(regexRule.match(getTestData4()));
    }

    public void test_match_httpStatus() {
        final RegexRule regexRule = new RegexRule();
        regexRule.defaultRule = false;
        regexRule.allRequired = true;
        regexRule.addRule("httpStatusCode", Pattern.compile("200"));

        assertTrue(regexRule.match(getTestData1()));
        assertTrue(regexRule.match(getTestData2()));
        assertFalse(regexRule.match(getTestData3()));
        assertFalse(regexRule.match(getTestData4()));
    }

    private ResponseData getTestData1() {
        final ResponseData responseData = new ResponseData();
        responseData.setHttpStatusCode(200);
        responseData.setUrl("http://example.com/");
        responseData.setMimeType("text/html");
        return responseData;
    }

    private ResponseData getTestData2() {
        final ResponseData responseData = new ResponseData();
        responseData.setHttpStatusCode(200);
        responseData.setUrl("https://example.com/");
        responseData.setMimeType("text/html");
        return responseData;
    }

    private ResponseData getTestData3() {
        final ResponseData responseData = new ResponseData();
        responseData.setHttpStatusCode(404);
        responseData.setUrl("http://example.com/");
        responseData.setMimeType("text/plain");
        return responseData;
    }

    private ResponseData getTestData4() {
        final ResponseData responseData = new ResponseData();
        responseData.setHttpStatusCode(404);
        responseData.setUrl("https://example.com/");
        responseData.setMimeType("text/plain");
        return responseData;
    }
}
