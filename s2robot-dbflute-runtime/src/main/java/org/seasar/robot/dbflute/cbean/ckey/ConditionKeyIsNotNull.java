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
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.query.QueryClause;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealName;

/**
 * The condition-key of isNotNull.
 * @author jflute
 */
public class ConditionKeyIsNotNull extends ConditionKey {

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
    protected ConditionKeyIsNotNull() {
        _conditionKey = "isNotNull";
        _operand = "is not null";
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    /**
     * {@inheritDoc}
     */
    protected boolean doIsValidRegistration(ConditionValue cvalue, Object value, ColumnRealName callerName) {
        if (cvalue.isFixedQuery() && cvalue.hasIsNotNull()) {
            noticeRegistered(callerName, value);
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected void doAddWhereClause(List<QueryClause> conditionList, ColumnRealName columnRealName, ConditionValue value) {
        conditionList.add(buildClauseWithoutValue(columnRealName));
    }

    /**
     * {@inheritDoc}
     */
    protected void doAddWhereClause(List<QueryClause> conditionList, ColumnRealName columnRealName,
            ConditionValue value, ConditionOption option) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location) {
        conditionValue.setIsNotNull(DUMMY_OBJECT);
    }

    /**
     * {@inheritDoc}
     */
    protected void doSetupConditionValue(ConditionValue conditionValue, Object value, String location,
            ConditionOption option) {
        throw new UnsupportedOperationException();
    }
}
