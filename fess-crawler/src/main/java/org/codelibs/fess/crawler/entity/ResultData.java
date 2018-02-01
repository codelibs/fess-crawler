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
package org.codelibs.fess.crawler.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author shinsuke
 *
 */
public class ResultData implements Serializable {
    private static final long serialVersionUID = 1L;

    private String transformerName;

    private byte[] data;

    private String encoding;

    private Set<RequestData> childUrlSet = new LinkedHashSet<>();

    public byte[] getData() {
        return data;
    }

    public void setData(final byte[] data) {
        this.data = data;
    }

    public void addUrl(final RequestData url) {
        childUrlSet.add(url);
    }

    public void addAllUrl(final Collection<RequestData> c) {
        if (c != null) {
            childUrlSet.addAll(c);
        }
    }

    public void removeUrl(final RequestData url) {
        childUrlSet.remove(url);
    }

    public String getTransformerName() {
        return transformerName;
    }

    public void setTransformerName(final String transformerName) {
        this.transformerName = transformerName;
    }

    public Set<RequestData> getChildUrlSet() {
        return childUrlSet;
    }

    public void setChildUrlSet(final Set<RequestData> childUrlSet) {
        this.childUrlSet = childUrlSet;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    @Override
    public String toString() {
        return "ResultData [transformerName=" + transformerName + ", encoding="
                + encoding + ", childUrlSet=" + childUrlSet + "]";
    }

}
