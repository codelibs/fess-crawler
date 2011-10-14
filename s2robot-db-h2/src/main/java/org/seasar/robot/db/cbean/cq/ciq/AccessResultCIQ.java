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

import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.db.cbean.cq.bs.AbstractBsAccessResultCQ;
import org.seasar.robot.db.cbean.cq.bs.BsAccessResultCQ;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;

/**
 * The condition-query for in-line of ACCESS_RESULT.
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultCIQ extends AbstractBsAccessResultCQ {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BsAccessResultCQ _myCQ;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public AccessResultCIQ(ConditionQuery childQuery, SqlClause sqlClause,
            String aliasName, int nestLevel, BsAccessResultCQ myCQ) {
        super(childQuery, sqlClause, aliasName, nestLevel);
        _myCQ = myCQ;
        _foreignPropertyName = _myCQ.xgetForeignPropertyName(); // accept foreign property name
        _relationPath = _myCQ.xgetRelationPath(); // accept relation path
        _inline = true;
    }

    // ===================================================================================
    //                                                             Override about Register
    //                                                             =======================
    @Override
    protected void reflectRelationOnUnionQuery(ConditionQuery bq,
            ConditionQuery uq) {
        String msg = "InlineView must not need UNION method: " + bq + " : "
                + uq;
        throw new IllegalConditionBeanOperationException(msg);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey k,
            Object v, ConditionValue cv, String col) {
        regIQ(k, v, cv, col);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey k,
            Object v, ConditionValue cv, String col, ConditionOption op) {
        regIQ(k, v, cv, col, op);
    }

    @Override
    protected void registerWhereClause(String wc) {
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
    //                                                                Override about Query
    //                                                                ====================
    protected ConditionValue getCValueId() {
        return _myCQ.getId();
    }

    public String keepId_ExistsReferrer_AccessResultDataAsOne(
            AccessResultDataCQ sq) {
        throwIICBOE("ExistsReferrer");
        return null;
    }

    public String keepId_NotExistsReferrer_AccessResultDataAsOne(
            AccessResultDataCQ sq) {
        throwIICBOE("NotExistsReferrer");
        return null;
    }

    public String keepId_InScopeRelation_AccessResultDataAsOne(
            AccessResultDataCQ sq) {
        return _myCQ.keepId_InScopeRelation_AccessResultDataAsOne(sq);
    }

    public String keepId_NotInScopeRelation_AccessResultDataAsOne(
            AccessResultDataCQ sq) {
        return _myCQ.keepId_NotInScopeRelation_AccessResultDataAsOne(sq);
    }

    protected ConditionValue getCValueSessionId() {
        return _myCQ.getSessionId();
    }

    protected ConditionValue getCValueRuleId() {
        return _myCQ.getRuleId();
    }

    protected ConditionValue getCValueUrl() {
        return _myCQ.getUrl();
    }

    protected ConditionValue getCValueParentUrl() {
        return _myCQ.getParentUrl();
    }

    protected ConditionValue getCValueStatus() {
        return _myCQ.getStatus();
    }

    protected ConditionValue getCValueHttpStatusCode() {
        return _myCQ.getHttpStatusCode();
    }

    protected ConditionValue getCValueMethod() {
        return _myCQ.getMethod();
    }

    protected ConditionValue getCValueMimeType() {
        return _myCQ.getMimeType();
    }

    protected ConditionValue getCValueContentLength() {
        return _myCQ.getContentLength();
    }

    protected ConditionValue getCValueExecutionTime() {
        return _myCQ.getExecutionTime();
    }

    protected ConditionValue getCValueLastModified() {
        return _myCQ.getLastModified();
    }

    protected ConditionValue getCValueCreateTime() {
        return _myCQ.getCreateTime();
    }

    public String keepScalarCondition(AccessResultCQ subQuery) {
        throwIICBOE("ScalarCondition");
        return null;
    }

    public String keepMyselfInScopeRelation(AccessResultCQ subQuery) {
        throwIICBOE("MyselfInScopeRelation");
        return null;
    }

    protected void throwIICBOE(String name) { // throwInlineIllegalConditionBeanOperationException()
        throw new IllegalConditionBeanOperationException(name
                + " at InlineView is unsupported.");
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xinCB() {
        return AccessResultCB.class.getName();
    }

    protected String xinCQ() {
        return AccessResultCQ.class.getName();
    }
}
