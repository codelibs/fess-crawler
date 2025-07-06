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
package org.codelibs.fess.crawler.entity;

import java.io.UnsupportedEncodingException;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;

/**
 * Implementation of the {@link AccessResultData} interface.
 *
 * @param <IDTYPE> the type of the identifier
 */
public class AccessResultDataImpl<IDTYPE> implements AccessResultData<IDTYPE> {
    /** The unique identifier for the access result data. */
    protected IDTYPE id;

    /** The name of the transformer associated with this access result data. */
    protected String transformerName;

    /** The data as a byte array. */
    protected byte[] data;

    /** The encoding used for the access result data. */
    protected String encoding;

    /**
     * Constructs a new AccessResultDataImpl instance.
     */
    public AccessResultDataImpl() {
        // NOP
    }

    @Override
    public IDTYPE getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.AccessResultData#setId(IDTYPE)
     */
    @Override
    public void setId(final IDTYPE id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.AccessResultData#getTransformerName()
     */
    @Override
    public String getTransformerName() {
        return transformerName;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.entity.AccessResultData#setTransformerName(java.lang
     * .String)
     */
    @Override
    public void setTransformerName(final String transformerName) {
        this.transformerName = transformerName;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.AccessResultData#getData()
     */
    @Override
    public byte[] getData() {
        return data;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.AccessResultData#setData(java.lang.String)
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
     * @see org.codelibs.fess.crawler.entity.AccessResultData#getDataAsString()
     */
    @Override
    public String getDataAsString() {
        if (data == null) {
            return null;
        }
        try {
            return new String(data, StringUtil.isNotBlank(encoding) ? encoding : Constants.UTF_8);
        } catch (final UnsupportedEncodingException e) {
            return new String(data, Constants.UTF_8_CHARSET);
        }
    }

    /**
     * Returns a string representation of this object.
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "AccessResultDataImpl [id=" + id + ", transformerName=" + transformerName + ", encoding=" + encoding + "]";
    }

}
