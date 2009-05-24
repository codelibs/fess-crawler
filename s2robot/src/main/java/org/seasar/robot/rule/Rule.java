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
package org.seasar.robot.rule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.transformer.Transformer;

/**
 * @author shinsuke
 * 
 */
public class Rule {
    public boolean defaultRule = false;

    protected String ruleId;

    protected Pattern urlPattern;

    protected String mimeType;

    protected Transformer transformer;

    public Rule() {

    }

    public boolean match(ResponseData responseData) {

        if (defaultRule) {
            return true;
        }

        String url = responseData.getUrl();
        String contentType = responseData.getMimeType();

        if (urlPattern != null && url != null) {
            Matcher matcher = urlPattern.matcher(url);
            if (matcher.matches()) {
                return true;
            }
        }

        if (this.mimeType != null && this.mimeType.equalsIgnoreCase(mimeType)) {
            return true;
        }

        return false;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Pattern getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(Pattern urlPattern) {
        this.urlPattern = urlPattern;
    }

    public void setUrlPatternRegex(String urlPatternRegrex) {
        urlPattern = Pattern.compile(urlPatternRegrex);
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }
}
