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
package org.seasar.robot.dbflute.s2dao.procedure;

import java.lang.reflect.Field;

import org.seasar.robot.dbflute.jdbc.ValueType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnProcedureParameterType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private String parameterName;
    private Integer parameterIndex;
    private Field field;
    private ValueType valueType;
    private boolean inType;
    private boolean outType;
    private boolean returnType;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnProcedureParameterType(Field field) {
        this.field = field;
        this.parameterName = field.getName();
    }

    // ===================================================================================
    //                                                                         Field Value
    //                                                                         ===========
    public Object getValue(Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            String msg = "The getting of the field threw the exception:";
            msg = msg + " class=" + field.getDeclaringClass().getSimpleName();
            msg = msg + " field=" + field.getName();
            throw new IllegalStateException(msg, e);
        }
    }

    public void setValue(Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            String msg = "The setting of the field threw the exception:";
            msg = msg + " class=" + field.getDeclaringClass().getSimpleName();
            msg = msg + " field=" + field.getName();
            throw new IllegalStateException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getParameterName() {
        return parameterName;
    }
    
    public Integer getParameterIndex() {
        return parameterIndex;
    }
    
    public void setParameterIndex(Integer parameterIndex) {
        this.parameterIndex = parameterIndex;
    }

    public ValueType getValueType() {
        return valueType;
    }

    public void setValueType(final ValueType valueType) {
        this.valueType = valueType;
    }

    public boolean isInType() {
        return inType;
    }

    public void setInType(final boolean inType) {
        this.inType = inType;
    }

    public boolean isOutType() {
        return outType;
    }

    public void setOutType(final boolean outType) {
        this.outType = outType;
    }

    public boolean isReturnType() {
        return returnType;
    }

    public void setReturnType(final boolean returnType) {
        this.returnType = returnType;
    }
}
