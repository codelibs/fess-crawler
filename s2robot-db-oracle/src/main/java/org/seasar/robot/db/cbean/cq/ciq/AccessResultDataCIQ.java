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
package org.seasar.robot.db.cbean.cq.ciq;

import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.db.cbean.cq.bs.AbstractBsAccessResultDataCQ;
import org.seasar.robot.db.cbean.cq.bs.BsAccessResultDataCQ;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of ACCESS_RESULT_DATA.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataCIQ extends AbstractBsAccessResultDataCQ {

    // ===================================================================================
    // Attribute
    // =========
    protected BsAccessResultDataCQ _myCQ;

    // ===================================================================================
    // Constructor
    // ===========
    public AccessResultDataCIQ(final ConditionQuery childQuery, final SqlClause sqlClause,
            final String aliasName, final int nestLevel, final BsAccessResultDataCQ myCQ) {
        super(childQuery, sqlClause, aliasName, nestLevel);
        _myCQ = myCQ;
        _foreignPropertyName = _myCQ.xgetForeignPropertyName(); // accept
                                                                // foreign
                                                                // property name
        _relationPath = _myCQ.xgetRelationPath(); // accept relation path
        _inline = true;
    }

    // ===================================================================================
    // Override about Register
    // =======================
    @Override
    protected void reflectRelationOnUnionQuery(final ConditionQuery bq,
            final ConditionQuery uq) {
        final String msg =
            "InlineView must not need UNION method: " + bq + " : " + uq;
        throw new IllegalConditionBeanOperationException(msg);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(final ConditionKey k,
            final Object v, final ConditionValue cv, final String col) {
        regIQ(k, v, cv, col);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(final ConditionKey k,
            final Object v, final ConditionValue cv, final String col, final ConditionOption op) {
        regIQ(k, v, cv, col, op);
    }

    @Override
    protected void registerWhereClause(final String wc) {
        registerInlineWhereClause(wc);
    }

    @Override
    protected boolean isInScopeRelationSuppressLocalAliasName() {
        if (_onClause) {
            throw new IllegalConditionBeanOperationException(
                "InScopeRelation on OnClause is unsupported.");
        }
        return true;
    }

    // ===================================================================================
    // Override about Query
    // ====================
    @Override
    protected ConditionValue getCValueId() {
        return _myCQ.getId();
    }

    @Override
    public String keepId_InScopeRelation_AccessResult(final AccessResultCQ sq) {
        return _myCQ.keepId_InScopeRelation_AccessResult(sq);
    }

    @Override
    public String keepId_NotInScopeRelation_AccessResult(final AccessResultCQ sq) {
        return _myCQ.keepId_NotInScopeRelation_AccessResult(sq);
    }

    @Override
    protected ConditionValue getCValueTransformerName() {
        return _myCQ.getTransformerName();
    }

    @Override
    protected ConditionValue getCValueData() {
        return _myCQ.getData();
    }

    @Override
    protected ConditionValue getCValueEncoding() {
        return _myCQ.getEncoding();
    }

    @Override
    public String keepScalarCondition(final AccessResultDataCQ subQuery) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepMyselfInScopeRelation(final AccessResultDataCQ subQuery) {
        throwIICBOE("MyselfInScopeRelation");
        return null;
    }

    protected void throwIICBOE(final String name) { // throwInlineIllegalConditionBeanOperationException()
        throw new IllegalConditionBeanOperationException(name
            + " at InlineView is unsupported.");
    }

    // ===================================================================================
    // Very Internal
    // =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xinCB() {
        return AccessResultDataCB.class.getName();
    }

    protected String xinCQ() {
        return AccessResultDataCQ.class.getName();
    }
}
