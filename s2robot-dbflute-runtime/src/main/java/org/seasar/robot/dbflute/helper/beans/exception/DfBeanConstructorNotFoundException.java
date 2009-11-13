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
public class DfBeanConstructorNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Class<?> targetClass;

    private Object[] methodArgs;

    private Class<?>[] paramTypes;

    public DfBeanConstructorNotFoundException(Class<?> targetClass, Object[] methodArgs) {
        super("The constructor was not found: class=" + targetClass.getName() + " args=" + getSignature(methodArgs));
        this.targetClass = targetClass;
        this.methodArgs = methodArgs;
    }

    public DfBeanConstructorNotFoundException(Class<?> targetClass, Class<?>[] paramTypes) {
        super("The constructor was not found: class=" + targetClass.getName() + " args=" + getSignature(paramTypes));
        this.targetClass = targetClass;
        this.paramTypes = paramTypes;
    }

    private static String getSignature(Object[] methodArgs) {
        StringBuffer buf = new StringBuffer(100);
        if (methodArgs != null) {
            for (int i = 0; i < methodArgs.length; ++i) {
                if (i > 0) {
                    buf.append(", ");
                }
                if (methodArgs[i] != null) {
                    buf.append(methodArgs[i].getClass().getName());
                } else {
                    buf.append("null");
                }
            }
        }
        return buf.toString();
    }

    private static String getSignature(Class<?>[] paramTypes) {
        StringBuffer buf = new StringBuffer(100);
        if (paramTypes != null) {
            for (int i = 0; i < paramTypes.length; ++i) {
                if (i > 0) {
                    buf.append(", ");
                }
                if (paramTypes[i] != null) {
                    buf.append(paramTypes[i].getName());
                } else {
                    buf.append("null");
                }
            }
        }
        return buf.toString();
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Object[] getMethodArgs() {
        return methodArgs;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }
}
