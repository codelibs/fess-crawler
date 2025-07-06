/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
 * The BinaryTransformer class is responsible for transforming binary data from a ResponseData object
 * into a ResultData object, and for retrieving the binary data from an AccessResultData object.
 * It extends the AbstractTransformer class.
 *
 * <p>
 * This transformer extracts the binary content from the response body of a web resource,
 * stores it as a byte array in the ResultData, and provides a method to retrieve this data
 * as a ByteArrayInputStream.
 * </p>
 *
 * <p>
 * The transform method takes a ResponseData object, checks if it has a response body,
 * and then reads the body into a byte array. This byte array is then set as the data
 * in the ResultData object.
 * </p>
 *
 * <p>
 * The getData method takes an AccessResultData object, checks if the transformer name matches,
 * and then returns the data as a ByteArrayInputStream.
 * </p>
 */
public class BinaryTransformer extends AbstractTransformer {

    /**
     * Constructs a new BinaryTransformer.
     */
    public BinaryTransformer() {
        // NOP
    }

    /**
     * Transforms the given ResponseData into a ResultData containing binary content.
     * @param responseData The response data to be transformed.
     * @return The transformed ResultData.
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

    /**
     * Retrieves data from the given AccessResultData object as a ByteArrayInputStream.
     * @param accessResultData The AccessResultData object.
     * @return A ByteArrayInputStream containing the data.
     */
    @Override
    public Object getData(final AccessResultData<?> accessResultData) {
        // check transformer name
        if (!getName().equals(accessResultData.getTransformerName())) {
            throw new CrawlerSystemException(
                    "Transformer is invalid. Use " + accessResultData.getTransformerName() + ". This transformer is " + getName() + ".");
        }
        final byte[] data = accessResultData.getData();
        if (data == null) {
            return null;
        }
        return new ByteArrayInputStream(data);
    }
}
