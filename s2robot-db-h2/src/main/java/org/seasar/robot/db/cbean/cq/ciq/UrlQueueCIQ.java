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

import org.seasar.robot.db.cbean.UrlQueueCB;
import org.seasar.robot.db.cbean.cq.UrlQueueCQ;
import org.seasar.robot.db.cbean.cq.bs.AbstractBsUrlQueueCQ;
import org.seasar.robot.db.cbean.cq.bs.BsUrlQueueCQ;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of URL_QUEUE.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class UrlQueueCIQ extends AbstractBsUrlQueueCQ {

    // ===================================================================================
    // Attribute
    // =========
    protected BsUrlQueueCQ _myCQ;

    // ===================================================================================
    // Constructor
    // ===========
    public UrlQueueCIQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel, final BsUrlQueueCQ myCQ) {
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
    protected void setupConditionValueAndRegisterWhereClause(
            final ConditionKey k, final Object v, final ConditionValue cv,
            final String col) {
        regIQ(k, v, cv, col);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(
            final ConditionKey k, final Object v, final ConditionValue cv,
            final String col, final ConditionOption op) {
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
    protected ConditionValue getCValueSessionId() {
        return _myCQ.getSessionId();
    }

    @Override
    protected ConditionValue getCValueMethod() {
        return _myCQ.getMethod();
    }

    @Override
    protected ConditionValue getCValueUrl() {
        return _myCQ.getUrl();
    }

    @Override
    protected ConditionValue getCValueParentUrl() {
        return _myCQ.getParentUrl();
    }

    @Override
    protected ConditionValue getCValueDepth() {
        return _myCQ.getDepth();
    }

    @Override
    protected ConditionValue getCValueLastModified() {
        return _myCQ.getLastModified();
    }

    @Override
    protected ConditionValue getCValueCreateTime() {
        return _myCQ.getCreateTime();
    }

    @Override
    public String keepScalarCondition(final UrlQueueCQ subQuery) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    @Override
    public String keepMyselfInScopeRelation(final UrlQueueCQ subQuery) {
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
        return UrlQueueCB.class.getName();
    }

    protected String xinCQ() {
        return UrlQueueCQ.class.getName();
    }
}
