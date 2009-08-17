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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.seasar.robot.RobotCrawlAccessException;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;

/**
 * BinaryTransformer stores WEB data as binary data.
 * 
 * @author shinsuke
 *
 */
public class BinaryTransformer extends AbstractTransformer {

    /* (non-Javadoc)
     * @see org.seasar.robot.transformer.Transformer#getData(org.seasar.robot.entity.AccessResultData)
     */
    public ResultData transform(ResponseData responseData) {
        if (responseData == null || responseData.getResponseBody() == null) {
            throw new RobotCrawlAccessException("No response body.");
        }

        ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());
        BufferedInputStream bis = null;

        try {
            bis = new BufferedInputStream(responseData.getResponseBody());
            resultData.setData(IOUtils.toByteArray(bis));
            resultData.setEncoding(responseData.getCharSet());
            return resultData;
        } catch (IOException e) {
            throw new RobotSystemException(
                    "Could not convert the input stream.", e);
        } finally {
            IOUtils.closeQuietly(bis);
        }

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
        return new ByteArrayInputStream(data);
    }
}
