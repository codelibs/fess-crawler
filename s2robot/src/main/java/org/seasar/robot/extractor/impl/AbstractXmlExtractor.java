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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.extractor.ExtractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public abstract class AbstractXmlExtractor {

    protected static final Logger logger = LoggerFactory // NOPMD
            .getLogger(AbstractXmlExtractor.class);

    protected String encoding = Constants.UTF_8;

    protected int preloadSizeForCharset = 2048;

    protected abstract Pattern getEncodingPattern();

    protected abstract Pattern getTagPattern();

    public ExtractData getText(InputStream in, Map<String, String> params) {
        if (in == null) {
            throw new RobotSystemException("The inputstream is null.");
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(in);
            String enc = getEncoding(bis);
            String content = StringEscapeUtils.unescapeHtml(new String(
                    InputStreamUtil.getBytes(bis), enc));
            return new ExtractData(extractString(content));
        } catch (Exception e) {
            throw new ExtractException(e);
        }
    }

    protected String getEncoding(BufferedInputStream bis) {
        byte[] b = new byte[preloadSizeForCharset];
        try {
            bis.mark(preloadSizeForCharset);
            int c = bis.read(b);

            if (c == -1) {
                return encoding;
            }

            String head = new String(b, 0, c, encoding);
            if (StringUtil.isBlank(head)) {
                return encoding;
            }
            Matcher matcher = getEncodingPattern().matcher(head);
            if (matcher.find()) {
                String enc = matcher.group(1);
                if (Charset.isSupported(enc)) {
                    return enc;
                }
            }
        } catch (Exception e) {
            if (logger.isInfoEnabled()) {
                logger.info("Use a default encoding: " + encoding, e);
            }
        } finally {
            try {
                bis.reset();
            } catch (IOException e) {
                throw new ExtractException(e);
            }
        }

        return encoding;
    }

    protected String extractString(String content) {
        Matcher matcher = getTagPattern().matcher(
                content.replaceAll("[\\r\\n]", " "));
        StringBuffer sb = new StringBuffer();
        Pattern attrPattern = Pattern.compile("\\s[^ ]+=\"([^\"]*)\"");
        while (matcher.find()) {
            String tagStr = matcher.group();
            Matcher attrMatcher = attrPattern.matcher(tagStr);
            StringBuilder buf = new StringBuilder();
            while (attrMatcher.find()) {
                buf.append(attrMatcher.group(1)).append(' ');
            }
            matcher.appendReplacement(sb, buf.toString().replace("$", "\\$"));
        }
        matcher.appendTail(sb);
        return sb.toString().replaceAll("\\s+", " ").trim();
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * @return Returns the preloadSizeForCharset.
     */
    public int getPreloadSizeForCharset() {
        return preloadSizeForCharset;
    }

    /**
     * @param preloadSizeForCharset The preloadSizeForCharset to set.
     */
    public void setPreloadSizeForCharset(int preloadSizeForCharset) {
        this.preloadSizeForCharset = preloadSizeForCharset;
    }

}