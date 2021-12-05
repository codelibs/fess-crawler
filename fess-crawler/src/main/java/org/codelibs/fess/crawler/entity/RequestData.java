/*
 * Copyright 2012-2021 CodeLibs Project and the Others.
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

import java.util.Objects;

import org.codelibs.fess.crawler.Constants;

/**
 * @author shinsuke
 *
 */
public class RequestData {
    public enum Method {
        GET, POST, HEAD;
    }

    private Method method;

    private String url;

    private String metaData;

    public Method getMethod() {
        return method;
    }

    public void setMethod(final Method method) {
        this.method = method;
    }

    public void setMethod(final String method) {
        if (Constants.GET_METHOD.equals(method)) {
            this.method = Method.GET;
        } else if (Constants.POST_METHOD.equals(method)) {
            this.method = Method.POST;
        } else if (Constants.HEAD_METHOD.equals(method)) {
            this.method = Method.HEAD;
        } else {
            this.method = Method.GET;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(final String metaData) {
        this.metaData = metaData;
    }

    @Override
    public String toString() {
        return "RequestData [method=" + method + ", url=" + url + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(metaData, method, url);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }
        final RequestData other = (RequestData) obj;
        if (!Objects.equals(metaData, other.metaData) || (method != other.method) || !Objects.equals(url, other.url)) {
            return false;
        }
        return true;
    }
}
