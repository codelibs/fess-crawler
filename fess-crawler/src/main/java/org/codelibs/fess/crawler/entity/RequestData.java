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
        final int prime = 31;
        int result = 1;
        result = prime * result + (metaData == null ? 0 : metaData.hashCode());
        result = prime * result + (method == null ? 0 : method.hashCode());
        result = prime * result + (url == null ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RequestData other = (RequestData) obj;
        if (metaData == null) {
            if (other.metaData != null) {
                return false;
            }
        } else if (!metaData.equals(other.metaData)) {
            return false;
        }
        if (method != other.method) {
            return false;
        }
        if (url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!url.equals(other.url)) {
            return false;
        }
        return true;
    }
}
