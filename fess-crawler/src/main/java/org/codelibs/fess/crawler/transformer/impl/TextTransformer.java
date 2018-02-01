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
package org.codelibs.fess.crawler.transformer.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class TextTransformer extends AbstractTransformer {
    private static final Logger logger = LoggerFactory
            .getLogger(TextTransformer.class);

    @Resource
    protected CrawlerContainer crawlerContainer;

    protected String charsetName = Constants.UTF_8;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.transformer.impl.AbstractTransformer#transform(org.fess.crawler.entity.ResponseData)
     */
    @Override
    public ResultData transform(final ResponseData responseData) {
        if (responseData == null || !responseData.hasResponseBody()) {
            throw new CrawlingAccessException("No response body.");
        }

        final ExtractorFactory extractorFactory = crawlerContainer
                .getComponent("extractorFactory");
        if (extractorFactory == null) {
            throw new CrawlerSystemException("Could not find extractorFactory.");
        }
        final Extractor extractor = extractorFactory.getExtractor(responseData
                .getMimeType());
        final Map<String, String> params = new HashMap<>();
        params.put(TikaMetadataKeys.RESOURCE_NAME_KEY,
                getResourceName(responseData));
        params.put(HttpHeaders.CONTENT_TYPE, responseData.getMimeType());
        String content = null;
        try (final InputStream in = responseData.getResponseBody()) {
            content = extractor.getText(in, params).getContent();
        } catch (final Exception e) {
            throw new CrawlingAccessException("Could not extract data.", e);
        }

        final ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());
        try {
            resultData.setData(content.getBytes(charsetName));
        } catch (final UnsupportedEncodingException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Invalid charsetName: " + charsetName
                        + ". Changed to " + Constants.UTF_8, e);
            }
            charsetName = Constants.UTF_8_CHARSET.name();
            resultData.setData(content.getBytes(Constants.UTF_8_CHARSET));
        }
        resultData.setEncoding(charsetName);
        return resultData;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.transformer.Transformer#getData(org.codelibs.fess.crawler.entity
     * .AccessResultData)
     */
    @Override
    public Object getData(final AccessResultData<?> accessResultData) {
        // check transformer name
        if (!getName().equals(accessResultData.getTransformerName())) {
            throw new CrawlerSystemException("Transformer is invalid. Use "
                    + accessResultData.getTransformerName()
                    + ". This transformer is " + getName() + ".");
        }
        final byte[] data = accessResultData.getData();
        if (data == null) {
            return null;
        }
        try {
            return new String(data, charsetName);
        } catch (final UnsupportedEncodingException e) {
            throw new CrawlingAccessException("Unsupported encoding: "
                    + charsetName, e);
        }
    }

    private String getResourceName(final ResponseData responseData) {
        String name = responseData.getUrl();
        final String enc = responseData.getCharSet();

        if (name == null || enc == null) {
            return null;
        }

        name = name.replaceAll("/+$", "");
        final int idx = name.lastIndexOf('/');
        if (idx >= 0) {
            name = name.substring(idx + 1);
        }
        try {
            return URLDecoder.decode(name, enc);
        } catch (final UnsupportedEncodingException e) {
            return name;
        }
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }

}
