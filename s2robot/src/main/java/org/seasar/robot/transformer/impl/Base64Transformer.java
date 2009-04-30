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
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;

/**
 * @author shinsuke
 *
 */
public class Base64Transformer extends AbstractTransformer {

    public ResultData transform(ResponseData responseData) {
        if (responseData == null || responseData.getResponseBody() == null) {
            throw new RobotSystemException("No response body.");
        }

        ResultData resultData = new ResultData();
        resultData.setTransformerName(getName());
        BufferedInputStream bis = null;

        try {
            bis = new BufferedInputStream(responseData.getResponseBody());
            resultData.setData(new String(Base64.encodeBase64(IOUtils
                    .toByteArray(bis))));
            return resultData;
        } catch (IOException e) {
            throw new RobotSystemException(
                    "Could not convert the input stream.", e);
        } finally {
            IOUtils.closeQuietly(bis);
        }

    }
}
