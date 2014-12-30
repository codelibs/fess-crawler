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
package org.codelibs.robot.entity;

import java.io.UnsupportedEncodingException;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.robot.Constants;

/**
 * @author shinsuke
 *
 */
public class AccessResultDataImpl implements AccessResultData {
    protected Long id;

    protected String transformerName;

    protected byte[] data;

    protected String encoding;

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.entity.AccessResultData#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.entity.AccessResultData#setId(java.lang.Long)
     */
    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.entity.AccessResultData#getTransformerName()
     */
    @Override
    public String getTransformerName() {
        return transformerName;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.robot.entity.AccessResultData#setTransformerName(java.lang
     * .String)
     */
    @Override
    public void setTransformerName(final String transformerName) {
        this.transformerName = transformerName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.entity.AccessResultData#getData()
     */
    @Override
    public byte[] getData() {
        return data;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.entity.AccessResultData#setData(java.lang.String)
     */
    @Override
    public void setData(final byte[] data) {
        this.data = data;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.robot.entity.AccessResultData#getDataAsString()
     */
    @Override
    public String getDataAsString() {
        if (data == null) {
            return null;
        }
        try {
            return new String(data, StringUtil.isNotBlank(encoding) ? encoding
                    : Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            return new String(data, Constants.UTF_8_CHARSET);
        }
    }

    @Override
    public String toString() {
        return "AccessResultDataImpl [id=" + id + ", transformerName="
                + transformerName + ", encoding=" + encoding + "]";
    }

}
