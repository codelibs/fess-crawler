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
package org.seasar.robot.dbflute.twowaysql.node;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.exception.BindVariableCommentNotFoundPropertyException;
import org.seasar.robot.dbflute.exception.EmbeddedValueCommentNotFoundPropertyException;
import org.seasar.robot.dbflute.exception.IllegalOutsideSqlOperationException;
import org.seasar.robot.dbflute.exception.RequiredOptionNotFoundException;
import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.twowaysql.pmbean.MapParameterBean;
import org.seasar.robot.dbflute.util.DfStringUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class ValueAndTypeSetupper {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression;
    protected List<String> _nameList;
    protected String _specifiedSql;
    protected boolean _bind;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ValueAndTypeSetupper(String expression, List<String> nameList, String specifiedSql, boolean bind) {
        this._expression = expression;
        this._nameList = nameList;
        this._specifiedSql = specifiedSql;
        this._bind = bind;
    }

    // ===================================================================================
    //                                                                              Set up
    //                                                                              ======
    public void setupValueAndType(ValueAndType valueAndType) {
        Object value = valueAndType.getTargetValue();
        Class<?> clazz = valueAndType.getTargetType();

        // LikeSearchOption handling here is for OutsideSql.
        LikeSearchOption likeSearchOption = null;
        String rearOption = null;

        for (int pos = 1; pos < _nameList.size(); ++pos) {
            if (value == null) {
                break;
            }
            final String currentName = _nameList.get(pos);
            if (pos == 1) {// at the First Loop
                final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(clazz);
                if (hasLikeSearchOption(beanDesc, currentName)) {
                    likeSearchOption = getLikeSearchOption(beanDesc, currentName, value);
                }
            }
            if (Map.class.isInstance(value)) {
                final Map<?, ?> map = (Map<?, ?>) value;
                value = map.get(_nameList.get(pos));
                if (isLastLoop4LikeSearch(pos, likeSearchOption) && isValidStringValue(value)) { // at the Last Loop
                    value = likeSearchOption.generateRealValue((String) value);
                    rearOption = likeSearchOption.getRearOption();
                }
                clazz = (value != null ? value.getClass() : clazz);
                continue;
            }
            final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(clazz);
            if (beanDesc.hasPropertyDesc(currentName)) {
                final DfPropertyDesc pd = beanDesc.getPropertyDesc(currentName);
                value = getPropertyValue(clazz, value, currentName, pd);
                if (isLastLoop4LikeSearch(pos, likeSearchOption) && isValidStringValue(value)) { // at the Last Loop
                    value = likeSearchOption.generateRealValue((String) value);
                    rearOption = likeSearchOption.getRearOption();
                }
                clazz = (value != null ? value.getClass() : pd.getPropertyType());
                continue;
            }
            final String methodName = "get" + initCap(currentName);
            if (beanDesc.hasMethod(methodName)) { // basically unused because of using propertyDesc before
                final Method method = beanDesc.getMethod(methodName);
                value = invokeGetter(method, value);
                clazz = method.getReturnType();
                continue;
            }
            if (pos == 1 && MapParameterBean.class.isAssignableFrom(clazz)) {
                final MapParameterBean pmb = (MapParameterBean) value;
                final Map<String, Object> map = pmb.getParameterMap();
                final Object elementValue = (map != null ? map.get(_nameList.get(pos)) : null);
                if (elementValue != null) {
                    value = elementValue;
                    clazz = value.getClass();
                    continue;
                }
            }
            throwBindOrEmbeddedCommentNotFoundPropertyException(_expression, clazz, currentName, _specifiedSql, _bind);
        }
        valueAndType.setTargetValue(value);
        valueAndType.setTargetType(clazz);
        valueAndType.setRearOption(rearOption);
    }

    // for OutsideSql
    protected boolean isLastLoop4LikeSearch(int pos, LikeSearchOption likeSearchOption) {
        return _nameList.size() == (pos + 1) && likeSearchOption != null;
    }

    protected boolean isValidStringValue(Object value) {
        return value != null && value instanceof String && ((String) value).length() > 0;
    }

    // for OutsideSql
    protected boolean hasLikeSearchOption(DfBeanDesc beanDesc, String currentName) {
        return beanDesc.hasPropertyDesc(currentName + "InternalLikeSearchOption");
    }

    // for OutsideSql
    protected LikeSearchOption getLikeSearchOption(DfBeanDesc beanDesc, String currentName, Object resourceBean) {
        final DfPropertyDesc pb = beanDesc.getPropertyDesc(currentName + "InternalLikeSearchOption");
        final LikeSearchOption option = (LikeSearchOption) pb.getValue(resourceBean);
        if (option == null) {
            throwLikeSearchOptionNotFoundException(resourceBean, currentName);
        }
        if (option.isSplit()) {
            throwOutsideSqlLikeSearchOptionSplitUnavailableException(option, resourceBean, currentName);
        }
        return option;
    }

    // for OutsideSql
    protected void throwLikeSearchOptionNotFoundException(Object resourceBean, String currentName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The likeSearchOption was Not Found! (Should not be null!)" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your method call:" + ln();
        final String beanName = resourceBean.getClass().getSimpleName();
        final String methodName = "set" + initCap(currentName) + "_LikeSearch(value, likeSearchOption);";
        msg = msg + "    " + beanName + "." + methodName + ln();
        msg = msg + ln();
        msg = msg + "[Target ParameterBean]" + ln() + resourceBean + ln();
        msg = msg + "* * * * * * * * * */";
        throw new RequiredOptionNotFoundException(msg);
    }

    // for OutsideSql
    protected void throwOutsideSqlLikeSearchOptionSplitUnavailableException(LikeSearchOption option,
            Object resourceBean, String currentName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The splitByXxx() of LikeSearchOption is unavailable at OutsideSql!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm your method call:" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    before (x):" + ln();
        final String beanName = resourceBean.getClass().getSimpleName();
        final String methodName = "set" + initCap(currentName) + "_LikeSearch(value, likeSearchOption);";
        msg = msg + "      " + beanName + " pmb = new " + beanName + "();" + ln();
        msg = msg + "      LikeSearchOption likeSearchOption = new LikeSearchOption().likeContain();" + ln();
        msg = msg + "      likeSearchOption.splitBySpace(); // *No! Don't invoke this!" + ln();
        msg = msg + "      pmb." + methodName + ln();
        msg = msg + "    after  (o):" + ln();
        msg = msg + "      " + beanName + " pmb = new " + beanName + "();" + ln();
        msg = msg + "      LikeSearchOption likeSearchOption = new LikeSearchOption().likeContain();" + ln();
        msg = msg + "      pmb." + methodName + ln();
        msg = msg + ln();
        msg = msg + "[Target LikeSearchOption]" + ln() + option + ln();
        msg = msg + ln();
        msg = msg + "[Target ParameterBean]" + ln() + resourceBean + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalOutsideSqlOperationException(msg);
    }

    protected Object getPropertyValue(Class<?> beanType, Object beanValue, String currentName, DfPropertyDesc pd) {
        try {
            return pd.getValue(beanValue);
        } catch (RuntimeException e) {
            throwPropertyHandlingFailureException(beanType, beanValue, currentName, _expression, _specifiedSql, _bind,
                    e);
            return null;// unreachable
        }
    }

    protected void throwPropertyHandlingFailureException(Class<?> beanType, Object beanValue, String currentName,
            String expression, String specifiedSql, boolean bind, Exception e) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The handlig of the property was failed!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "This is the Framework Exception!" + ln();
        msg = msg + ln();
        msg = msg + "[" + (bind ? "Bind Variable" : "Embedded Value") + " Comment Expression]" + ln() + expression
                + ln();
        msg = msg + ln();
        msg = msg + "[Bean Type]" + ln() + beanType + ln();
        msg = msg + ln();
        msg = msg + "[Bean Value]" + ln() + beanValue + ln();
        msg = msg + ln();
        msg = msg + "[Property Name]" + ln() + currentName + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new IllegalStateException(msg, e);
    }

    protected Object invokeGetter(Method method, Object target) {
        try {
            return method.invoke(target, (Object[]) null);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected void throwBindOrEmbeddedCommentNotFoundPropertyException(String expression, Class<?> targetType,
            String notFoundProperty, String specifiedSql, boolean bind) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The property on the " + (bind ? "bind variable" : "embedded value") + " comment was Not Found!"
                + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the existence of your property on your arguments." + ln();
        msg = msg + "Abd has the property had misspelling?" + ln();
        msg = msg + ln();
        msg = msg + "[" + (bind ? "Bind Variable" : "Embedded Value") + " Comment Expression]" + ln() + expression
                + ln();
        msg = msg + ln();
        msg = msg + "[NotFound Property]" + ln() + (targetType != null ? targetType.getName() + "#" : "")
                + notFoundProperty + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + specifiedSql + ln();
        msg = msg + "* * * * * * * * * */";
        if (bind) {
            throw new BindVariableCommentNotFoundPropertyException(msg);
        } else {
            throw new EmbeddedValueCommentNotFoundPropertyException(msg);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String initCap(String name) {
        return DfStringUtil.initCap(name);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
