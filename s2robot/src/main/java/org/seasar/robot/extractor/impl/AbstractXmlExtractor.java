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
package org.seasar.robot.extractor.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
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

    protected boolean ignoreCommentTag = false;

    protected abstract Pattern getEncodingPattern();

    protected abstract Pattern getTagPattern();

    public ExtractData getText(final InputStream in,
            final Map<String, String> params) {
        if (in == null) {
            throw new RobotSystemException("The inputstream is null.");
        }
        try {
            final BufferedInputStream bis = new BufferedInputStream(in);
            final String enc = getEncoding(bis);
            final String content =
                StringEscapeUtils.unescapeHtml4(new String(InputStreamUtil
                    .getBytes(bis), enc));
            return new ExtractData(extractString(content));
        } catch (final Exception e) {
            throw new ExtractException(e);
        }
    }

    protected String getEncoding(final BufferedInputStream bis) {
        final byte[] b = new byte[preloadSizeForCharset];
        try {
            bis.mark(preloadSizeForCharset);
            final int c = bis.read(b);

            if (c == -1) {
                return encoding;
            }

            final String head = new String(b, 0, c, encoding);
            if (StringUtil.isBlank(head)) {
                return encoding;
            }
            final Matcher matcher = getEncodingPattern().matcher(head);
            if (matcher.find()) {
                final String enc = matcher.group(1);
                if (Charset.isSupported(enc)) {
                    return enc;
                }
            }
        } catch (final Exception e) {
            if (logger.isInfoEnabled()) {
                logger.info("Use a default encoding: " + encoding, e);
            }
        } finally {
            try {
                bis.reset();
            } catch (final IOException e) {
                throw new ExtractException(e);
            }
        }

        return encoding;
    }

    protected String extractString(final String content) {
        String input = content.replaceAll("[\\r\\n]", " ");
        if (ignoreCommentTag) {
            input = input.replaceAll("<!--[^>]+-->", "");
        } else {
            input = input.replace("<!--", "").replace("-->", "");
        }
        final Matcher matcher = getTagPattern().matcher(input);
        final StringBuffer sb = new StringBuffer();
        final Pattern attrPattern = Pattern.compile("\\s[^ ]+=\"([^\"]*)\"");
        while (matcher.find()) {
            final String tagStr = matcher.group();
            final Matcher attrMatcher = attrPattern.matcher(tagStr);
            final StringBuilder buf = new StringBuilder();
            while (attrMatcher.find()) {
                buf.append(attrMatcher.group(1)).append(' ');
            }
            matcher.appendReplacement(sb, buf
                .toString()
                .replace("\\", "\\\\")
                .replace("$", "\\$"));
        }
        matcher.appendTail(sb);
        return sb.toString().replaceAll("\\s+", " ").trim();
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /**
     * @return Returns the preloadSizeForCharset.
     */
    public int getPreloadSizeForCharset() {
        return preloadSizeForCharset;
    }

    /**
     * @param preloadSizeForCharset
     *            The preloadSizeForCharset to set.
     */
    public void setPreloadSizeForCharset(final int preloadSizeForCharset) {
        this.preloadSizeForCharset = preloadSizeForCharset;
    }

    public boolean isIgnoreCommentTag() {
        return ignoreCommentTag;
    }

    public void setIgnoreCommentTag(final boolean ignoreCommentTag) {
        this.ignoreCommentTag = ignoreCommentTag;
    }

}
