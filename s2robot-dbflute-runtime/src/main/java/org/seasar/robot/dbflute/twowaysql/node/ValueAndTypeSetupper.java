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
package org.seasar.robot.dbflute.twowaysql.node;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanIllegalPropertyException;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentListIndexNotNumberException;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentListIndexOutOfBoundsException;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentNotFoundPropertyException;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentPropertyReadFailureException;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentListIndexNotNumberException;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentListIndexOutOfBoundsException;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentNotFoundPropertyException;
import org.seasar.robot.dbflute.twowaysql.exception.EmbeddedVariableCommentPropertyReadFailureException;
import org.seasar.robot.dbflute.twowaysql.exception.ForCommentListIndexNotNumberException;
import org.seasar.robot.dbflute.twowaysql.exception.ForCommentListIndexOutOfBoundsException;
import org.seasar.robot.dbflute.twowaysql.exception.ForCommentNotFoundPropertyException;
import org.seasar.robot.dbflute.twowaysql.exception.ForCommentPropertyReadFailureException;
import org.seasar.robot.dbflute.twowaysql.pmbean.MapParameterBean;
import org.seasar.robot.dbflute.util.DfReflectionUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 */
public class ValueAndTypeSetupper {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String LIKE_SEARCH_OPTION_SUFFIX = "InternalLikeSearchOption";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<String> _nameList;
    protected String _expression; // for logging only
    protected String _specifiedSql; // for logging only
    protected CommentType _commentType; // for logging only

    public enum CommentType {
        BIND("bind variable comment", "Bind Variable Comment") // bind
        , EMBEDDED("embedded variable comment", "Embedded Variable Comment") // embedded
        , FORCOMMENT("FOR comment", "FOR COMMENT") // for comment
        ;
        private String _textName;
        private String _titleName;

        private CommentType(String commentName, String titleName) {
            _textName = commentName;
            _titleName = titleName;
        }

        public String textName() {
            return _textName;
        }

        public String titleName() {
            return _titleName;
        }
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param nameList The list of property names. (NotNull)
     * @param expression The expression of the comment for logging only. (NotNull)
     * @param specifiedSql The specified SQL for logging only. (NotNull)
     * @param commentType The type of comment for logging only. (NotNull)
     */
    public ValueAndTypeSetupper(List<String> nameList, String expression, String specifiedSql, CommentType commentType) {
        this._nameList = nameList;
        this._expression = expression;
        this._specifiedSql = specifiedSql;
        this._commentType = commentType;
    }

    // ===================================================================================
    //                                                                              Set up
    //                                                                              ======
    public void setupValueAndType(ValueAndType valueAndType) {
        Object value = valueAndType.getFirstValue();
        if (value == null) { // if null, do nothing
            return;
        }
        Class<?> clazz = valueAndType.getFirstType(); // if value is not null, required 

        // LikeSearchOption handling here is for OutsideSql.
        LikeSearchOption likeSearchOption = null;

        for (int pos = 1; pos < _nameList.size(); pos++) {
            if (value == null) {
                break;
            }
            final String currentName = _nameList.get(pos);
            final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(clazz);
            if (hasLikeSearchProperty(beanDesc, currentName, value)) {
                final LikeSearchOption currentOption = getLikeSearchOption(beanDesc, currentName, value);
                if (currentOption != null) { // if exists, override option
                    likeSearchOption = currentOption;
                }
            }
            if (beanDesc.hasPropertyDesc(currentName)) { // main case
                final DfPropertyDesc pd = beanDesc.getPropertyDesc(currentName);
                value = getPropertyValue(clazz, value, currentName, pd);
                clazz = (value != null ? value.getClass() : pd.getPropertyType());
                continue;
            }
            if (MapParameterBean.class.isInstance(value)) { // used by union-query internally
                final Map<?, ?> map = ((MapParameterBean<?>) value).getParameterMap();
                // if the key does not exist, it does not process
                // (different specification with Map)
                if (map.containsKey(currentName)) {
                    value = map.get(currentName);
                    clazz = (value != null ? value.getClass() : null);
                    continue;
                }
            }
            if (Map.class.isInstance(value)) {
                final Map<?, ?> map = (Map<?, ?>) value;
                // if the key does not exist, treated same as a null value
                value = map.get(currentName);
                clazz = (value != null ? value.getClass() : null);
                continue;
            }
            if (List.class.isInstance(value)) {
                if (currentName.startsWith("get(") && currentName.endsWith(")")) {
                    final List<?> list = (List<?>) value;
                    final String exp = Srl.extractScopeFirst(currentName, "get(", ")").getContent();
                    try {
                        final Integer index = DfTypeUtil.toInteger(exp);
                        value = list.get(index);
                    } catch (NumberFormatException e) {
                        throwListIndexNotNumberException(exp, e);
                    } catch (IndexOutOfBoundsException e) {
                        throwListIndexOutOfBoundsException(exp, e);
                    }
                    clazz = (value != null ? value.getClass() : null);
                    continue;
                }
            }
            throwNotFoundPropertyException(clazz, currentName);
        }
        valueAndType.setTargetValue(value);
        valueAndType.setTargetType(clazz);
        valueAndType.setLikeSearchOption(likeSearchOption);
    }

    // -----------------------------------------------------
    //                             LikeSearch for OutsideSql
    //                             -------------------------
    protected boolean hasLikeSearchProperty(DfBeanDesc beanDesc, String currentName, Object pmb) {
        final String propertyName = buildLikeSearchPropertyName(currentName);
        boolean result = false;
        if (beanDesc.hasPropertyDesc(propertyName)) { // main case
            result = true;
        } else if (MapParameterBean.class.isInstance(pmb)) {
            final Map<?, ?> map = ((MapParameterBean<?>) pmb).getParameterMap();
            result = map.containsKey(propertyName);
        } else if (Map.class.isInstance(pmb)) {
            result = ((Map<?, ?>) pmb).containsKey(propertyName);
        }
        return result;
    }

    protected LikeSearchOption getLikeSearchOption(DfBeanDesc beanDesc, String currentName, Object pmb) {
        final String propertyName = buildLikeSearchPropertyName(currentName);
        final LikeSearchOption option;
        if (beanDesc.hasPropertyDesc(propertyName)) { // main case
            final DfPropertyDesc pb = beanDesc.getPropertyDesc(propertyName);
            option = (LikeSearchOption) pb.getValue(pmb);
        } else if (MapParameterBean.class.isInstance(pmb)) {
            final Map<?, ?> map = ((MapParameterBean<?>) pmb).getParameterMap();
            option = (LikeSearchOption) map.get(propertyName);
        } else if (Map.class.isInstance(pmb)) {
            option = (LikeSearchOption) ((Map<?, ?>) pmb).get(propertyName);
        } else { // no way
            String msg = "Not found the like search property: name=" + propertyName;
            throw new IllegalStateException(msg);
        }
        // no check here for various situation
        // (parameter-bean checks option's existence and wrong operation)
        return option;
    }

    protected String buildLikeSearchPropertyName(String resourceName) {
        return resourceName + LIKE_SEARCH_OPTION_SUFFIX;
    }

    // -----------------------------------------------------
    //                                         Assist Helper
    //                                         -------------
    protected boolean isLastLoop(int pos) {
        return _nameList.size() == (pos + 1);
    }

    protected Object getPropertyValue(Class<?> beanType, Object beanValue, String currentName, DfPropertyDesc pd) {
        try {
            return pd.getValue(beanValue);
        } catch (DfBeanIllegalPropertyException e) {
            throwPropertyReadFailureException(beanType, currentName, e);
            return null; // unreachable
        }
    }

    protected Object invokeGetter(Method method, Object target) {
        return DfReflectionUtil.invoke(method, target, null);
    }

    protected void throwPropertyReadFailureException(Class<?> targetType, String propertyName,
            DfBeanIllegalPropertyException e) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("Failed to read the property on the " + _commentType.textName() + "!");
        br.addItem("Advice");
        br.addElement("Please confirm your comment properties.");
        br.addElement("(readable? accessbile? and so on...)");
        br.addItem(_commentType.titleName());
        br.addElement(_expression);
        br.addItem("Illegal Property");
        br.addElement(DfTypeUtil.toClassTitle(targetType) + "." + propertyName);
        br.addItem("Exception Message");
        br.addElement(e.getClass());
        br.addElement(e.getMessage());
        final Throwable cause = e.getCause();
        if (cause != null) { // basically DfBeanIllegalPropertyException has its cause
            br.addElement(cause.getClass());
            br.addElement(cause.getMessage());
            final Throwable nextCause = cause.getCause();
            if (nextCause != null) {
                br.addElement(nextCause.getClass());
                br.addElement(nextCause.getMessage());
            }
        }
        br.addItem("Specified SQL");
        br.addElement(_specifiedSql);
        final String msg = br.buildExceptionMessage();
        if (CommentType.BIND.equals(_commentType)) {
            throw new BindVariableCommentPropertyReadFailureException(msg, e);
        } else if (CommentType.EMBEDDED.equals(_commentType)) {
            throw new EmbeddedVariableCommentPropertyReadFailureException(msg, e);
        } else if (CommentType.FORCOMMENT.equals(_commentType)) {
            throw new ForCommentPropertyReadFailureException(msg, e);
        } else { // no way
            throw new BindVariableCommentPropertyReadFailureException(msg, e);
        }
    }

    protected void throwNotFoundPropertyException(Class<?> targetType, String notFoundProperty) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The property on the " + _commentType.textName() + " was not found!");
        br.addItem("Advice");
        br.addElement("Please confirm the existence of your property on your arguments.");
        br.addElement("And has the property had misspelling?");
        br.addItem(_commentType.titleName());
        br.addElement(_expression);
        br.addItem("NotFound Property");
        br.addElement((targetType != null ? targetType.getName() + "#" : "") + notFoundProperty);
        br.addItem("Specified SQL");
        br.addElement(_specifiedSql);
        final String msg = br.buildExceptionMessage();
        if (CommentType.BIND.equals(_commentType)) {
            throw new BindVariableCommentNotFoundPropertyException(msg);
        } else if (CommentType.EMBEDDED.equals(_commentType)) {
            throw new EmbeddedVariableCommentNotFoundPropertyException(msg);
        } else if (CommentType.FORCOMMENT.equals(_commentType)) {
            throw new ForCommentNotFoundPropertyException(msg);
        } else { // no way
            throw new BindVariableCommentNotFoundPropertyException(msg);
        }
    }

    protected void throwListIndexNotNumberException(String notNumberIndex, NumberFormatException e) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The list index on the " + _commentType.textName() + " was not number!");
        br.addItem("Advice");
        br.addElement("Please confirm the index on your comment.");
        br.addItem(_commentType.titleName());
        br.addElement(_expression);
        br.addItem("NotNumber Index");
        br.addElement(notNumberIndex);
        br.addItem("NumberFormatException");
        br.addElement(e.getMessage());
        br.addItem("Specified SQL");
        br.addElement(_specifiedSql);
        final String msg = br.buildExceptionMessage();
        if (CommentType.BIND.equals(_commentType)) {
            throw new BindVariableCommentListIndexNotNumberException(msg, e);
        } else if (CommentType.EMBEDDED.equals(_commentType)) {
            throw new EmbeddedVariableCommentListIndexNotNumberException(msg, e);
        } else if (CommentType.FORCOMMENT.equals(_commentType)) {
            throw new ForCommentListIndexNotNumberException(msg, e);
        } else { // no way
            throw new BindVariableCommentListIndexNotNumberException(msg, e);
        }
    }

    protected void throwListIndexOutOfBoundsException(String numberIndex, IndexOutOfBoundsException e) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The list index on the " + _commentType.textName() + " was out of bounds!");
        br.addItem("Advice");
        br.addElement("Please confirm the index on your comment.");
        br.addItem(_commentType.titleName());
        br.addElement(_expression);
        br.addItem("OutOfBounds Index");
        br.addElement(numberIndex);
        br.addItem("IndexOutOfBoundsException");
        br.addElement(e.getMessage());
        br.addItem("Specified SQL");
        br.addElement(_specifiedSql);
        final String msg = br.buildExceptionMessage();
        if (CommentType.BIND.equals(_commentType)) {
            throw new BindVariableCommentListIndexOutOfBoundsException(msg, e);
        } else if (CommentType.EMBEDDED.equals(_commentType)) {
            throw new EmbeddedVariableCommentListIndexOutOfBoundsException(msg, e);
        } else if (CommentType.FORCOMMENT.equals(_commentType)) {
            throw new ForCommentListIndexOutOfBoundsException(msg, e);
        } else { // no way
            throw new BindVariableCommentListIndexOutOfBoundsException(msg, e);
        }
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ExceptionMessageBuilder createExceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String initCap(String name) {
        return Srl.initCap(name);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
