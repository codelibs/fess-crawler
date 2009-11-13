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
package org.seasar.robot.dbflute.helper.beans.exception;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class DfBeanConverterException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String propertyName;

    private Object value;

    public DfBeanConverterException(String propertyName, Object value, Throwable cause) {
        super("Failed to convert: property=" + propertyName + " value=" + value, cause);
        this.propertyName = propertyName;
        this.value = value;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getValue() {
        return value;
    }
}