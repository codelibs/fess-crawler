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
package org.codelibs.fess.crawler.client.http;

import java.io.Serializable;

import org.codelibs.core.lang.StringUtil;

/**
 * Represents an HTTP request header with a name and value.
 * This class is used to encapsulate the header information for HTTP requests.
 * It provides methods to get and set the name and value of the header,
 * as well as a method to validate the header.
 *
 * <p>Example usage:</p>
 * <pre>
 *     RequestHeader header = new RequestHeader("Content-Type", "application/json");
 *     String name = header.getName();
 *     String value = header.getValue();
 *     boolean isValid = header.isValid();
 * </pre>
 *
 * <p>Note: The name should not be blank and the value should not be null for the header to be considered valid.</p>
 *
 * @see java.io.Serializable
 */
public class RequestHeader implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The name of the request header.
     */
    private String name;

    /**
     * The value of the request header.
     */
    private String value;

    /**
     * Constructs a new RequestHeader with the specified name and value.
     *
     * @param name  the name of the request header
     * @param value the value of the request header
     */
    public RequestHeader(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Returns the name of the request header.
     * @return The name of the request header.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the request header.
     * @param name The name of the request header.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the value of the request header.
     * @return The value of the request header.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the request header.
     * @param value The value of the request header.
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * Checks if the request header is valid.
     * A header is considered valid if its name is not blank and its value is not null.
     * @return true if the header is valid, false otherwise.
     */
    public boolean isValid() {
        if (StringUtil.isBlank(name) || value == null) {
            return false;
        }

        return true;
    }

}
