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
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypeFactory;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnProcedureMetaDataFactory {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected TnValueTypeFactory valueTypeFactory;
    protected InternalFieldProcedureAnnotationReader annotationReader = new InternalFieldProcedureAnnotationReader();

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public TnProcedureMetaData createProcedureMetaData(final String procedureName, final Class<?> pmbType) {
        final TnProcedureMetaData metaData = new TnProcedureMetaData(procedureName);
        if (pmbType == null) {
            return metaData;
        } else {
            if (!isDtoType(pmbType)) {
                throw new IllegalStateException("The pmb type was Not DTO type: " + pmbType.getName());
            }
        }
        final DfBeanDesc pmbDesc = DfBeanDescFactory.getBeanDesc(pmbType);

        // *Point
        final Stack<Class<?>> stack = new Stack<Class<?>>();
        for (Class<?> clazz = pmbType; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            stack.push(clazz);
        }
        for (; !stack.isEmpty();) {
            final Class<?> clazz = stack.pop();
            registerParameterType(metaData, pmbDesc, clazz.getDeclaredFields());
        }

        return metaData;
    }

    protected void registerParameterType(TnProcedureMetaData metaData, DfBeanDesc pmbDesc, Field[] fields) {
        for (Field field : fields) {
            if (!isInstanceField(field)) {
                continue;
            }
            final TnProcedureParameterType ppt = getProcedureParameterType(pmbDesc, field);
            if (ppt == null) {
                continue;
            }
            metaData.addParameterType(ppt);
        }
    }

    protected TnProcedureParameterType getProcedureParameterType(final DfBeanDesc dtoDesc, final Field field) {
        final String procedureParameter = annotationReader.getProcedureParameter(dtoDesc, field);
        if (procedureParameter == null) {
            return null;
        }
        final String type = extractParameterType(procedureParameter);
        field.setAccessible(true);
        final TnProcedureParameterType ppt = new TnProcedureParameterType(field);
        if (type.equalsIgnoreCase("in")) {
            ppt.setInType(true);
        } else if (type.equalsIgnoreCase("out")) {
            ppt.setOutType(true);
        } else if (type.equalsIgnoreCase("inout")) {
            ppt.setInType(true);
            ppt.setOutType(true);
        } else if (type.equalsIgnoreCase("return")) {
            ppt.setOutType(true);
            ppt.setReturnType(true);
        } else {
            String msg = "The parameter type should be 'in' or 'out' or 'inout' or 'return':";
            msg = msg + " class=" + field.getDeclaringClass().getSimpleName();
            msg = msg + " field=" + field.getName();
            msg = msg + " parameterType=" + type;
            throw new IllegalStateException(msg);
        }
        final Integer index = extractParameterIndex(procedureParameter, field);
        ppt.setParameterIndex(index);
        final ValueType valueType = getValueType(dtoDesc, field);
        ppt.setValueType(valueType);
        return ppt;
    }

    protected String extractParameterType(String procedureParameter) {
        if (procedureParameter.contains(",")) {
            return procedureParameter.substring(0, procedureParameter.indexOf(",")).trim();
        }
        return procedureParameter.trim();
    }

    protected Integer extractParameterIndex(String procedureParameter, Field field) {
        if (procedureParameter.contains(",")) {
            String tmp = procedureParameter.substring(procedureParameter.indexOf(",") + ",".length()).trim();
            try {
                return Integer.valueOf(tmp);
            } catch (NumberFormatException e) {
                String msg = "The parameter index should be number:";
                msg = msg + " class=" + field.getDeclaringClass().getSimpleName();
                msg = msg + " field=" + field.getName();
                msg = msg + " parameterIndex=" + tmp + " procedureParameter=" + procedureParameter;
                throw new IllegalStateException(msg, e);
            }
        }
        return null;
    }

    protected ValueType getValueType(final DfBeanDesc dtoDesc, final Field field) {
        final String name = annotationReader.getValueType(dtoDesc, field);
        if (name != null) {
            return valueTypeFactory.getValueTypeByName(name);
        }
        final Class<?> type = field.getType();
        if (List.class.isAssignableFrom(type)) {// is for out parameter cursor.
            if (isCurrentDBDef(DBDef.Oracle)) {
                return TnValueTypes.ORACLE_RESULT_SET;
            } else if (isCurrentDBDef(DBDef.PostgreSQL)) {
                return TnValueTypes.POSTGRE_RESULT_SET;
            } else {
                return TnValueTypes.SERIALIZABLE_BYTE_ARRAY;
            }
        }
        return valueTypeFactory.getValueTypeByClass(type);
    }

    protected boolean isCurrentDBDef(DBDef currentDBDef) {
        return ResourceContext.isCurrentDBDef(currentDBDef);
    }

    protected boolean isInstanceField(final Field field) {
        final int mod = field.getModifiers();
        return !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
    }

    protected boolean isDtoType(final Class<?> clazz) {
        return !isSimpleType(clazz) && !isContainerType(clazz);
    }

    protected boolean isSimpleType(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        return clazz == String.class || clazz.isPrimitive() || clazz == Boolean.class || clazz == Character.class
                || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
                || Calendar.class.isAssignableFrom(clazz) || clazz == byte[].class;
    }

    protected boolean isContainerType(final Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        return Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz) || clazz.isArray();
    }

    public void setValueTypeFactory(final TnValueTypeFactory valueTypeFactory) {
        this.valueTypeFactory = valueTypeFactory;
    }

    protected static class InternalFieldProcedureAnnotationReader {
        protected String PROCEDURE_PARAMETER_SUFFIX;
        protected String VALUE_TYPE_SUFFIX;

        public InternalFieldProcedureAnnotationReader() {
            PROCEDURE_PARAMETER_SUFFIX = "_PROCEDURE_PARAMETER";
            VALUE_TYPE_SUFFIX = "_VALUE_TYPE";
        }

        public String getProcedureParameter(DfBeanDesc dtoDesc, Field field) {
            String fieldName = removeInstanceVariablePrefix(field.getName());// *Point
            String annotationName = fieldName + PROCEDURE_PARAMETER_SUFFIX;
            if (dtoDesc.hasField(annotationName)) {
                Field f = dtoDesc.getField(annotationName);
                return (String) getValue(f, null);
            } else {
                return null;
            }
        }

        public String getValueType(DfBeanDesc dtoDesc, Field field) {
            String fieldName = removeInstanceVariablePrefix(field.getName());// *Point
            String annotationName = fieldName + VALUE_TYPE_SUFFIX;
            if (dtoDesc.hasField(annotationName)) {
                Field f = dtoDesc.getField(annotationName);
                return (String) getValue(f, null);
            } else {
                return null;
            }
        }

        protected String removeInstanceVariablePrefix(String fieldName) {
            return fieldName.startsWith("_") ? fieldName.substring("_".length()) : fieldName;
        }

        protected Object getValue(Field field, Object target) {
            try {
                return field.get(target);
            } catch (IllegalAccessException e) {
                String msg = "The getting of the field threw the exception:";
                msg = msg + " class=" + field.getDeclaringClass().getSimpleName();
                msg = msg + " field=" + field.getName();
                throw new IllegalStateException(msg, e);
            }
        }
    }
}
