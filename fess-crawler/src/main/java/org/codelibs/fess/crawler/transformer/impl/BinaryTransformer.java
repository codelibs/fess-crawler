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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;

/**
 * BinaryTransformer stores WEB data as binary data.
 *
 * @author shinsuke
 *
 */
public class BinaryTransformer extends AbstractTransformer {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.transformer.Transformer#getData(org.codelibs.fess.crawler.entity
     * .AccessResultData)
     */
    @Override
    public ResultData transform(final ResponseData responseData) {
        if (responseData == null || !responseData.hasResponseBody()) {
            throw new CrawlingAccessException("No response body.");
        }

        final ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());

        try (BufferedInputStream bis = new BufferedInputStream(responseData.getResponseBody())) {
            resultData.setData(IOUtils.toByteArray(bis));
            resultData.setEncoding(responseData.getCharSet());
            return resultData;
        } catch (final IOException e) {
            throw new CrawlerSystemException("Could not convert the input stream.", e);
        }

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
        return new ByteArrayInputStream(data);
    }
}
