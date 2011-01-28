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
public class DfBeanMethodNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Class<?> targetClass;

    private String methodName;

    private Class<?>[] methodArgClasses;

    public DfBeanMethodNotFoundException(Class<?> targetClass, String methodName, Object[] methodArgs) {
        super("The method was not found: class=" + targetClass.getName() + " method=" + methodName + " args="
                + getSignature(methodArgs));
        this.targetClass = targetClass;
        this.methodName = methodName;
        if (methodArgs != null) {
            methodArgClasses = new Class[methodArgs.length];
            for (int i = 0; i < methodArgs.length; ++i) {
                if (methodArgs[i] != null) {
                    methodArgClasses[i] = methodArgs[i].getClass();
                }
            }
        }
    }

    public DfBeanMethodNotFoundException(Class<?> targetClass, String methodName, Class<?>[] methodArgClasses) {
        super("The method was not found: class=" + targetClass.getName() + " method=" + methodName + " args="
                + getSignature(methodArgClasses));
        this.targetClass = targetClass;
        this.methodName = methodName;
        this.methodArgClasses = methodArgClasses;
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

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getMethodArgClasses() {
        return methodArgClasses;
    }
}
