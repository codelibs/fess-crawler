/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class DfBeanIllegalPropertyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Class<?> targetClass;

    private String propertyName;

    public DfBeanIllegalPropertyException(Class<?> targetClass, String propertyName, Throwable cause) {
        super("The property was illegal: class=" + targetClass.getName() + " property=" + propertyName, cause);
        this.targetClass = targetClass;
        this.propertyName = propertyName;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getPropertyName() {
        return propertyName;
    }
}