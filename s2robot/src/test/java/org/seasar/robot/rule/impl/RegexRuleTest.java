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
package org.seasar.robot.rule.impl;

import java.util.regex.Pattern;

import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.entity.ResponseData;

/**
 * @author shinsuke
 *
 */
public class RegexRuleTest extends S2TestCase {
    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    public void test_match_default() {
        RegexRule regexRule = new RegexRule();
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
        RegexRule regexRule = new RegexRule();
        regexRule.defaultRule = false;
        regexRule.allRequired = true;
        regexRule.addRule("url", Pattern.compile("http:.*"));

        assertTrue(regexRule.match(getTestData1()));
        assertFalse(regexRule.match(getTestData2()));
        assertTrue(regexRule.match(getTestData3()));
        assertFalse(regexRule.match(getTestData4()));
    }

    public void test_match_mimeType() {
        RegexRule regexRule = new RegexRule();
        regexRule.defaultRule = false;
        regexRule.allRequired = true;
        regexRule.addRule("mimeType", Pattern.compile("text/plain"));

        assertFalse(regexRule.match(getTestData1()));
        assertFalse(regexRule.match(getTestData2()));
        assertTrue(regexRule.match(getTestData3()));
        assertTrue(regexRule.match(getTestData4()));
    }

    public void test_match_and() {
        RegexRule regexRule = new RegexRule();
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
        RegexRule regexRule = new RegexRule();
        regexRule.defaultRule = false;
        regexRule.allRequired = false;
        regexRule.addRule("url", Pattern.compile("http:.*"));
        regexRule.addRule("mimeType", Pattern.compile("text/html"));

        assertTrue(regexRule.match(getTestData1()));
        assertTrue(regexRule.match(getTestData2()));
        assertTrue(regexRule.match(getTestData3()));
        assertFalse(regexRule.match(getTestData4()));
    }

    private ResponseData getTestData1() {
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");
        responseData.setMimeType("text/html");
        return responseData;
    }

    private ResponseData getTestData2() {
        ResponseData responseData = new ResponseData();
        responseData.setUrl("https://example.com/");
        responseData.setMimeType("text/html");
        return responseData;
    }

    private ResponseData getTestData3() {
        ResponseData responseData = new ResponseData();
        responseData.setUrl("http://example.com/");
        responseData.setMimeType("text/plain");
        return responseData;
    }

    private ResponseData getTestData4() {
        ResponseData responseData = new ResponseData();
        responseData.setUrl("https://example.com/");
        responseData.setMimeType("text/plain");
        return responseData;
    }
}
