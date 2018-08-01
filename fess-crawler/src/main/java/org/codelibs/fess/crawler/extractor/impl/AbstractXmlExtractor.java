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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;
import org.apache.commons.text.translate.NumericEntityUnescaper;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public abstract class AbstractXmlExtractor extends AbstractExtractor {

    protected static final Logger logger = LoggerFactory
            .getLogger(AbstractXmlExtractor.class);

    protected static final ByteOrderMark BOM_UTF_7 = new ByteOrderMark("UTF-7", 0x2B, 0x2F, 0x76);

    protected static final CharSequenceTranslator UNESCAPE_HTML4 = new AggregateTranslator(new LookupTranslator(
            EntityArrays.BASIC_UNESCAPE), new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE), new LookupTranslator(
            EntityArrays.HTML40_EXTENDED_UNESCAPE), new NumericEntityUnescaper());

    protected String encoding = Constants.UTF_8;

    protected int preloadSizeForCharset = 2048;

    protected boolean ignoreCommentTag = false;

    protected abstract Pattern getEncodingPattern();

    protected abstract Pattern getTagPattern();

    public ExtractData getText(final InputStream in,
            final Map<String, String> params) {
        if (in == null) {
            throw new CrawlerSystemException("The inputstream is null.");
        }
        try {
            final BufferedInputStream bis = new BufferedInputStream(in);
            final String enc = getEncoding(bis);
            final String content = UNESCAPE_HTML4.translate(new String(
                    InputStreamUtil.getBytes(bis), enc));
            return new ExtractData(extractString(content));
        } catch (final Exception e) {
            throw new ExtractException(e);
        }
    }

    protected String getEncoding(final BufferedInputStream bis) {
        final byte[] b = new byte[preloadSizeForCharset];
        try {
            bis.mark(preloadSizeForCharset);
            @SuppressWarnings("resource")
            final BOMInputStream bomIn = new BOMInputStream(bis, false, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_16LE,
                    ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE, BOM_UTF_7);
            if (bomIn.hasBOM()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("BOM: " + bomIn.getBOMCharsetName());
                }
                return bomIn.getBOMCharsetName();
            }
            final int c = bomIn.read(b);

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
            final StringBuilder buf = new StringBuilder(100);
            while (attrMatcher.find()) {
                buf.append(attrMatcher.group(1)).append(' ');
            }
            matcher.appendReplacement(sb, buf.toString().replace("\\", "\\\\")
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
