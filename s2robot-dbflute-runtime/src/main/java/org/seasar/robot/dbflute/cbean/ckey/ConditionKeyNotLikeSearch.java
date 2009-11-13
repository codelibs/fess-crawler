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
import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.WhereClauseArranger;
import org.seasar.robot.dbflute.dbway.ExtensionOperand;

/**
 * The condition-key of notLikeSearch.
 * @author jflute
 */
public class ConditionKeyNotLikeSearch extends ConditionKey {

    /**
     * Constructor.
     */
    protected ConditionKeyNotLikeSearch() {
        _conditionKey = "notLikeSearch";
        _operand = "not like";
    }

    /**
     * Is valid registration?
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param callerName Caller name. (NotNull)
     * @return Determination.
     */
    public boolean isValidRegistration(ConditionValue conditionValue, Object value, String callerName) {
        if (value == null) {
            return false;
        }
        return true;
    }

    /**
     * This method implements super#doAddWhereClause().
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     */
    protected void doAddWhereClause(List<String> conditionList, String columnName, ConditionValue value) {
        throw new UnsupportedOperationException("doAddWhereClause without condition-option is unsupported!!!");
    }

    /**
     * This method implements super#doAddWhereClause().
     * @param conditionList Condition list. (NotNull)
     * @param columnName Column name. (NotNull)
     * @param value Condition value. (NotNull)
     * @param option Condition option. (NotNull)
     */
    protected void doAddWhereClause(List<String> conditionList, String columnName, ConditionValue value, ConditionOption option) {
        if (option == null) {
            String msg = "The argument[option] should not be null: columnName=" + columnName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (!(option instanceof LikeSearchOption)) {
            String msg = "The argument[option] should be LikeSearchOption: columnName=" + columnName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        final String location = value.getNotLikeSearchLocation(); // from NotLikeSearch
        final LikeSearchOption myOption = (LikeSearchOption)option;
        final String rearOption = myOption.getRearOption();
        final ExtensionOperand extOperand = myOption.getExtensionOperand();
        String operand = extOperand != null ? extOperand.operand() : null;
        if (operand == null || operand.trim().length() == 0) {
            operand = getOperand();
        } else {
            operand = "not " + operand; // because this is for NotLikeSearch
        }
        final WhereClauseArranger arranger = myOption.getWhereClauseArranger();
        final String clause;
        if (arranger != null) {
            clause = arranger.arrange(columnName, operand, buildBindExpression(location, null), rearOption);
        } else {
            clause = buildBindClauseWithRearOption(columnName, operand, location, rearOption);
        }
        conditionList.add(clause);
    }

    /**
     * This method implements super#doSetupConditionValue().
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param location Location. (NotNull)
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location) {
        throw new UnsupportedOperationException("doSetupConditionValue without condition-option is unsupported!!!");
    }

    /**
     * This method implements super#doSetupConditionValue().
     * @param conditionValue Condition value. (NotNull)
     * @param value Value. (NotNull)
     * @param location Location. (NotNull)
     * @param option Condition option. (NotNull)
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location, ConditionOption option) {
        conditionValue.setNotLikeSearch((String)value, (LikeSearchOption)option).setNotLikeSearchLocation(location);
    }
}
