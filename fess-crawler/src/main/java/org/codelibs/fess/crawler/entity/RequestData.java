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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.Constants;

/**
 * Represents a request data for crawling.
 * This class encapsulates the HTTP method, URL, weight and request headers associated with a crawling request.
 */
public class RequestData {
    /**
     * HTTP methods supported for crawling requests.
     */
    public enum Method {
        /** HTTP GET method. */
        GET,
        /** HTTP POST method. */
        POST,
        /** HTTP HEAD method. */
        HEAD;
    }

    /** The HTTP method for this request. */
    private Method method;

    /** The URL for this request. */
    private String url;

    /** The weight/priority of this request (default: 1.0). */
    private float weight = 1.0f;

    /** The request headers for this request, in the order they were added. */
    private final Map<String, String> headers = new LinkedHashMap<>();

    /**
     * Creates a new RequestData instance.
     */
    public RequestData() {
        super();
    }

    /**
     * Gets the HTTP method for this request.
     * @return the HTTP method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Sets the HTTP method for this request.
     * @param method the HTTP method
     */
    public void setMethod(final Method method) {
        this.method = method;
    }

    /**
     * Sets the HTTP method for this request using a string value.
     * Defaults to GET if the method is not recognized.
     * @param method the HTTP method as a string
     */
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

    /**
     * Gets the URL for this request.
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL for this request.
     * @param url the URL
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Gets the weight/priority of this request.
     * @return the weight
     */
    public float getWeight() {
        return weight;
    }

    /**
     * Sets the weight/priority of this request.
     * @param weight the weight
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * Gets the request headers for this request.
     * Clients that do not support request headers ignore them.
     * @return an unmodifiable view of the headers, never null
     */
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    /**
     * Adds a request header. A header with the same name replaces the previous value.
     * The header is ignored when the name or the value is blank.
     * @param name the header name
     * @param value the header value
     */
    public void addHeader(final String name, final String value) {
        if (StringUtil.isBlank(name) || StringUtil.isBlank(value)) {
            return;
        }
        headers.put(name, value);
    }

    /**
     * Returns the hash code for this RequestData.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(method, url, weight, headers);
    }

    /**
     * Checks if this RequestData is equal to another object.
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RequestData other = (RequestData) obj;
        return method == other.method && Objects.equals(url, other.url)
                && Float.floatToIntBits(weight) == Float.floatToIntBits(other.weight) && headers.equals(other.headers);
    }

    /**
     * Returns a string representation of this object.
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "RequestData [method=" + method + ", url=" + url + ", weight=" + weight + ", headers=" + headers + "]";
    }

}
