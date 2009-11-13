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
package org.seasar.robot.dbflute.cbean.ckey;

import java.util.List;

import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;

/**
 * The abstract class of condition-key.
 * @author jflute
 */
public abstract class ConditionKey {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Mark of replaced value. */
    public static final String MARK_OF_REPLACED_VALUE = "ReplacedValue";

    /** The condition key of equal. */
    public static final ConditionKey CK_EQUAL = new ConditionKeyEqual();

    /** The condition key of notEqual. */
    public static final ConditionKey CK_NOT_EQUAL = new ConditionKeyNotEqual();

    /** The condition key of greaterThan. */
    public static final ConditionKey CK_GREATER_THAN = new ConditionKeyGreaterThan();

    /** The condition key of lessrThan. */
    public static final ConditionKey CK_LESS_THAN = new ConditionKeyLessThan();

    /** The condition key of greaterEqual. */
    public static final ConditionKey CK_GREATER_EQUAL = new ConditionKeyGreaterEqual();

    /** The condition key of lessEqual. */
    public static final ConditionKey CK_LESS_EQUAL = new ConditionKeyLessEqual();

    /** The condition key of inScope. */
    public static final ConditionKey CK_IN_SCOPE = new ConditionKeyInScope();

    /** The condition key of notInScope. */
    public static final ConditionKey CK_NOT_IN_SCOPE = new ConditionKeyNotInScope();

    /** The condition key of likeSearch. */
    public static final ConditionKey CK_LIKE_SEARCH = new ConditionKeyLikeSearch();

    /** The condition key of notLikeSearch. */
    public static final ConditionKey CK_NOT_LIKE_SEARCH = new ConditionKeyNotLikeSearch();

    /** The condition key of isNull. */
    public static final ConditionKey CK_IS_NULL = new ConditionKeyIsNull();

    /** The condition key of isNotNull. */
    public static final ConditionKey CK_IS_NOT_NULL = new ConditionKeyIsNotNull();

    /** Dummy-object for IsNull and IsNotNull and so on... */
    protected static final Object DUMMY_OBJECT = new Object();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Condition-key. */
    protected String _conditionKey;

    /** Operand. */
    protected String _operand;

    // ===================================================================================
    //                                                                          Validation
    //                                                                          ==========
    /**
     * Is valid registration?
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param callerName Caller name. (NotNull)
     * @return Determination.
     */
    abstract public boolean isValidRegistration(ConditionValue conditionValue, Object value, String callerName);

    // ===================================================================================
    //                                                                        Where Clause
    //                                                                        ============
    /**
     * Add where clause.
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     * @return this.
     */
    public ConditionKey addWhereClause(List<String> conditionList, String columnName, ConditionValue value) {
        if (value == null) {
            String msg = "Argument[value] must not be null:";
            throw new IllegalArgumentException(msg + " value=null this.toString()=" + toString());
        }
        doAddWhereClause(conditionList, columnName, value);
        return this;
    }

    /**
     * Add where clause.
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     * @param option Condition option. (NotNull)
     * @return this.
     */
    public ConditionKey addWhereClause(List<String> conditionList, String columnName, ConditionValue value,
            ConditionOption option) {
        if (value == null) {
            String msg = "Argument[value] must not be null:";
            throw new IllegalArgumentException(msg + " value=null this.toString()=" + toString());
        }
        doAddWhereClause(conditionList, columnName, value, option);
        return this;
    }

    /**
     * Do add where clause.
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     */
    abstract protected void doAddWhereClause(List<String> conditionList, String columnName, ConditionValue value);

    /**
     * Do add where clause.
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     * @param option Condition option. (NotNull)
     */
    abstract protected void doAddWhereClause(List<String> conditionList, String columnName, ConditionValue value,
            ConditionOption option);

    // ===================================================================================
    //                                                                     Condition Value
    //                                                                     ===============
    /**
     * Setup condition value.
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (Nullable)
     * @param location Location. (Nullable)
     * @return Condition value. (The same as argument[conditionValue]) (NotNull)
     */
    public ConditionValue setupConditionValue(ConditionValue conditionValue, Object value, String location) {
        if (conditionValue == null) {
            String msg = "Argument[conditionValue] must not be null:";
            throw new IllegalArgumentException(msg + " value=" + value + " this.toString()=" + toString());
        }
        doSetupConditionValue(conditionValue, value, location);
        return conditionValue;
    }

    /**
     * Setup condition value.
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (Nullable)
     * @param location Location. (Nullable)
     * @param option Condition option. (NotNull)
     * @return Condition value. (The same as argument[conditionValue]) (NotNull)
     */
    public ConditionValue setupConditionValue(ConditionValue conditionValue, Object value, String location,
            ConditionOption option) {
        if (conditionValue == null) {
            String msg = "Argument[conditionValue] must not be null:";
            throw new IllegalArgumentException(msg + " value=" + value + " this.toString()=" + toString());
        }
        doSetupConditionValue(conditionValue, value, location, option);
        return conditionValue;
    }

    /**
     * Do setup condition value.
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param location Location. (NotNull)
     */
    abstract protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location);

    /**
     * Do setup condition value.
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param location Location. (NotNull)
     * @param option Condition option. (NotNull)
     */
    abstract protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location,
            ConditionOption option);

    // ===================================================================================
    //                                                                         Bind Clause
    //                                                                         ===========
    /**
     * Build bind clause.
     * @param columnName Column name. (NotNull)
     * @param location Location. (NotNull)
     * @return Bind clause. (NotNull)
     */
    protected String buildBindClause(String columnName, String location) {
        return columnName + " " + getOperand() + " " + buildBindExpression(location, null);
    }

    /**
     * Build bind clause. (basically for like-search)
     * @param columnName Column name. (NotNull)
     * @param operand Operand. (NotNull)
     * @param location Location. (NotNull)
     * @param rearOption Rear option. (NotNull)
     * @return Bind clause. (NotNull)
     */
    protected String buildBindClauseWithRearOption(String columnName, String operand, String location, String rearOption) {
        return columnName + " " + operand + " " + buildBindExpression(location, null) + rearOption;
    }

    /**
     * Build bind clause.
     * @param columnName Column name. (NotNull)
     * @param location Location. (NotNull)
     * @param dummyValue Dummy value. (NotNull)
     * @return Bind clause. (NotNull)
     */
    protected String buildBindClause(String columnName, String location, String dummyValue) {
        return columnName + " " + getOperand() + " " + buildBindExpression(location, dummyValue);
    }

    /**
     * Build clause without value.
     * @param columnName Column name. (NotNull)
     * @return Clause without value. (NotNull)
     */
    protected String buildClauseWithoutValue(String columnName) {
        return columnName + " " + getOperand();
    }

    protected String buildBindExpression(String location, String dummyValue) {
        return "/*pmb." + location + "*/" + dummyValue;
    }

    /**
     * Get wild-card.
     * @return Wild-card.
     */
    protected String getWildCard() {
        return "%";
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * The override.
     * Returns hash-code of this condition-key string.
     * @return HashCode.
     */
    public int hashCode() {
        return getConditionKey().hashCode();
    }

    /**
     * The override.
     * If the condition-key of the other is same as this one, returns true.
     * @param other Other entity. (Nullable)
     * @return Comparing result. If other is null, returns false.
     */
    public boolean equals(Object other) {
        if (other instanceof ConditionKey) {
            if (this.getConditionKey().equals(((ConditionKey) other).getConditionKey())) {
                return true;
            }
        }
        return false;
    }

    /**
     * The override.
     * @return View-string of condition key information.
     */
    public String toString() {
        return "ConditionKey: " + getConditionKey() + " " + getOperand() + " wild-card=[" + getWildCard() + "]";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get condition-key.
     * @return Condition-key.
     */
    public String getConditionKey() {
        return _conditionKey;
    }

    /**
     * Get operand.
     * @return Operand.
     */
    public String getOperand() {
        return _operand;
    }
}
