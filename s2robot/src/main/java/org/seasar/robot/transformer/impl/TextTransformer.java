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
package org.seasar.robot.transformer.impl;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.entity.ExtractData;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.extractor.Extractor;
import org.seasar.robot.extractor.ExtractorFactory;

/**
 * @author shinsuke
 *
 */
public class TextTransformer extends AbstractTransformer {

    protected String charsetName = Constants.UTF_8;

    /* (non-Javadoc)
     * @see org.seasar.robot.transformer.impl.AbstractTransformer#transform(org.seasar.robot.entity.ResponseData)
     */
    @Override
    public ResultData transform(ResponseData responseData) {
        if (responseData == null || responseData.getResponseBody() == null) {
            throw new RobotSystemException("No response body.");
        }

        ExtractorFactory extractorFactory = SingletonS2Container
                .getComponent("extractorFactory");
        if (extractorFactory == null) {
            throw new RobotSystemException("Could not find extractorFactory.");
        }
        Extractor extractor = extractorFactory.getExtractor(responseData
                .getMimeType());
        InputStream in = responseData.getResponseBody();
        Map<String, String> params = new HashMap<String, String>();
        params
                .put(ExtractData.RESOURCE_NAME_KEY,
                        getResourceName(responseData));
        params.put(ExtractData.CONTENT_TYPE, responseData.getMimeType());
        String content = null;
        try {
            content = extractor.getText(in, params).getContent();
        } catch (Exception e) {
            throw new RobotSystemException("Could not extract data.", e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());
        try {
            resultData.setData(content.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new RobotSystemException("Unsupported encoding: "
                    + charsetName, e);
        }
        resultData.setEncoding(charsetName);
        return resultData;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.transformer.Transformer#getData(org.seasar.robot.entity.AccessResultData)
     */
    public Object getData(AccessResultData accessResultData) {
        // check transformer name
        if (!getName().equals(accessResultData.getTransformerName())) {
            throw new RobotSystemException("Transformer is invalid. Use "
                    + accessResultData.getTransformerName()
                    + ". This transformer is " + getName() + ".");
        }
        byte[] data = accessResultData.getData();
        if (data == null) {
            return null;
        }
        try {
            return new String(data, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new RobotSystemException("Unsupported encoding: "
                    + charsetName, e);
        }
    }

    private String getResourceName(ResponseData responseData) {
        String name = responseData.getUrl();
        String enc = responseData.getCharSet();

        if (name == null || enc == null) {
            return null;
        }

        name = name.replaceAll("/+$", "");
        int idx = name.lastIndexOf("/");
        if (idx >= 0) {
            name = name.substring(idx + 1);
        }
        try {
            return URLDecoder.decode(name, enc);
        } catch (UnsupportedEncodingException e) {
            return name;
        }
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

}
