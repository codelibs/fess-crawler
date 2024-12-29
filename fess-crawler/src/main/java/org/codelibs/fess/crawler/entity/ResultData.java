/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

import org.codelibs.fess.crawler.exception.CrawlerSystemException;

/**
 * This class represents the result data of a crawl.
 */
public class ResultData implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The name of the transformer. */
    protected String transformerName;

    /** The data of the result. */
    protected byte[] data;

    /** The encoding of the result. */
    protected String encoding;

    /** The set of the child URLs. */
    protected Set<RequestData> childUrlSet = new LinkedHashSet<>();

    /** The raw data. */
    protected Object rawData = null;

    /** The serializer. */
    protected Function<Object, byte[]> serializer;

    /**
     * Set the raw data.
     * @param rawData
     */
    public void setRawData(final Object rawData) {
        this.rawData = rawData;
        if (rawData != null) {
            data = null;
        }
    }

    /**
     * Get the raw data.
     * @return rawData
     */
    public Object getRawData() {
        return rawData;
    }

    /**
     * Set the serializer.
     * @param serializer
     */
    public void setSerializer(final Function<Object, byte[]> serializer) {
        this.serializer = serializer;
    }

    /**
     * Get the data.
     * @return data
     */
    public byte[] getData() {
        if (data == null) {
            if (serializer == null || rawData == null) {
                throw new CrawlerSystemException("serializer or rawData is null");
            }
            data = serializer.apply(rawData);
        }
        return data;
    }

    /**
     * Set the data.
     * @param data
     */
    public void setData(final byte[] data) {
        this.data = data;
    }

    /**
     * Add a child URL.
     * @param url
     */
    public void addUrl(final RequestData url) {
        childUrlSet.add(url);
    }

    /**
     * Add child URLs.
     * @param c
     */
    public void addAllUrl(final Collection<RequestData> c) {
        if (c != null) {
            childUrlSet.addAll(c);
        }
    }

    /**
     * Remove a child URL.
     * @param url
     */
    public void removeUrl(final RequestData url) {
        childUrlSet.remove(url);
    }

    /**
     * Get the transformer name.
     * @return transformer
     */
    public String getTransformerName() {
        return transformerName;
    }

    /**
     * Set the transformer name.
     * @param transformerName
     */
    public void setTransformerName(final String transformerName) {
        this.transformerName = transformerName;
    }

    /**
     * Get the child URL set.
     * @return childUrlSet
     */
    public Set<RequestData> getChildUrlSet() {
        return childUrlSet;
    }

    /**
     * Set the child URL set.
     * @param childUrlSet
     */
    public void setChildUrlSet(final Set<RequestData> childUrlSet) {
        this.childUrlSet = childUrlSet;
    }

    /**
     * Get the encoding.
     * @return encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Set the encoding.
     * @param encoding
     */
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String toString() {
        return "ResultData [transformerName=" + transformerName + ", encoding=" + encoding + ", childUrlSet=" + childUrlSet + "]";
    }

}
