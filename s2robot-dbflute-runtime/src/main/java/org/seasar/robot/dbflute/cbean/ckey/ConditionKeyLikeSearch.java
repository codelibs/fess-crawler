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
package org.seasar.robot.dbflute.cbean.ckey;

import java.util.List;

import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.query.QueryClause;
import org.seasar.robot.dbflute.cbean.sqlclause.query.QueryClauseArranger;
import org.seasar.robot.dbflute.cbean.sqlclause.query.StringQueryClause;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealName;
import org.seasar.robot.dbflute.dbway.ExtensionOperand;

/**
 * The condition-key of likeSearch.
 * @author jflute
 */
public class ConditionKeyLikeSearch extends ConditionKey {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     */
    protected ConditionKeyLikeSearch() {
        _conditionKey = defineConditionKey();
        _operand = defineOperand();
    }

    protected String defineConditionKey() {
        return "likeSearch";
    }

    protected String defineOperand() {
        return "like";
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    /**
     * {@inheritDoc}
     */
    protected boolean doIsValidRegistration(ConditionValue cvalue, Object value, ColumnRealName callerName) {
        return value != null;
    }

    /**
     * {@inheritDoc}
     */
    protected void doAddWhereClause(List<QueryClause> conditionList, ColumnRealName columnRealName, ConditionValue value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    protected void doAddWhereClause(List<QueryClause> conditionList, ColumnRealName columnRealName,
            ConditionValue value, ConditionOption option) {
        assertWhereClauseArgument(columnRealName, value, option);
        final String location = getLocation(value);
        final LikeSearchOption myOption = (LikeSearchOption) option;
        final String rearOption = myOption.getRearOption();
        final String realOperand = getRealOperand(myOption);
        final QueryClauseArranger arranger = myOption.getWhereClauseArranger();
        final QueryClause clause;
        if (arranger != null) {
            final String bindExpression = buildBindExpression(location, null);
            final String arranged = arranger.arrange(columnRealName, realOperand, bindExpression, rearOption);
            clause = new StringQueryClause(arranged);
        } else {
            clause = buildBindClause(columnRealName, realOperand, location, rearOption);
        }
        conditionList.add(clause);
    }

    protected void assertWhereClauseArgument(ColumnRealName columnRealName, ConditionValue value, ConditionOption option) {
        if (option == null) {
            String msg = "The argument 'option' should not be null:";
            msg = msg + " columnName=" + columnRealName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (!(option instanceof LikeSearchOption)) {
            String msg = "The argument 'option' should be LikeSearchOption:";
            msg = msg + " columnName=" + columnRealName + " value=" + value;
            msg = msg + " option=" + option;
            throw new IllegalArgumentException(msg);
        }
    }

    protected String getLocation(ConditionValue value) {
        return value.getLikeSearchLatestLocation();
    }

    protected String getRealOperand(LikeSearchOption option) {
        final ExtensionOperand extOperand = option.getExtensionOperand();
        final String operand = extOperand != null ? extOperand.operand() : null;
        return operand != null ? operand : getOperand();
    }

    /**
     * {@inheritDoc}
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location,
            ConditionOption option) {
        conditionValue.setupLikeSearch((String) value, (LikeSearchOption) option, location);
    }
}
