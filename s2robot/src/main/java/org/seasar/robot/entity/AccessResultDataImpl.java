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
package org.seasar.robot.entity;

import java.io.UnsupportedEncodingException;

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;

/**
 * @author shinsuke
 *
 */
public class AccessResultDataImpl implements AccessResultData {
    protected Long id;

    protected String transformerName;

    protected byte[] data;

    protected String encoding;

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResultData#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResultData#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResultData#getTransformerName()
     */
    public String getTransformerName() {
        return transformerName;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResultData#setTransformerName(java.lang.String)
     */
    public void setTransformerName(String transformerName) {
        this.transformerName = transformerName;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResultData#getData()
     */
    public byte[] getData() {
        return data;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResultData#setData(java.lang.String)
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResultData#getDataAsString()
     */
    public String getDataAsString() {
        if (data == null) {
            return null;
        }
        try {
            return new String(data, StringUtil.isNotBlank(encoding) ? encoding
                    : Constants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            try {
                return new String(data, Constants.UTF_8);
            } catch (UnsupportedEncodingException e1) {
                throw new RobotSystemException("Unexpected exception.", e1);
            }
        }
    }

}
