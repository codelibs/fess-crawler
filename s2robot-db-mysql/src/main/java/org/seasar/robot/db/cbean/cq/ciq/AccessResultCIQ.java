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
package org.seasar.robot.db.cbean.cq.ciq;

import org.seasar.robot.dbflute.cbean.*;
import org.seasar.robot.dbflute.cbean.ckey.*;
import org.seasar.robot.dbflute.cbean.coption.ConditionOption;
import org.seasar.robot.dbflute.cbean.cvalue.ConditionValue;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.robot.db.cbean.*;
import org.seasar.robot.db.cbean.cq.bs.*;
import org.seasar.robot.db.cbean.cq.*;

/**
 * The condition-inline-query of ACCESS_RESULT.
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
    public AccessResultCIQ(ConditionQuery childQuery, SqlClause sqlClause
                        , String aliasName, int nestLevel, BsAccessResultCQ myCQ) {
        super(childQuery, sqlClause, aliasName, nestLevel);
        _myCQ = myCQ;
        _foreignPropertyName = _myCQ.getForeignPropertyName();// Accept foreign property name.
        _relationPath = _myCQ.getRelationPath();// Accept relation path.
    }

    // ===================================================================================
    //                                                             Override about Register
    //                                                             =======================
    @Override
    protected void reflectRelationOnUnionQuery(ConditionQuery baseQueryAsSuper, ConditionQuery unionQueryAsSuper) {
        String msg = "InlineQuery must not need UNION method: " + baseQueryAsSuper + " : " + unionQueryAsSuper;
        throw new IllegalConditionBeanOperationException(msg);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey k, Object v, ConditionValue cv, String col) {
        regIQ(k, v, cv, col);
    }

    @Override
    protected void setupConditionValueAndRegisterWhereClause(ConditionKey k, Object v, ConditionValue cv, String col, ConditionOption op) {
        regIQ(k, v, cv, col, op);
    }

    @Override
    protected void registerWhereClause(String whereClause) {
        registerInlineWhereClause(whereClause);
    }

    @Override
    protected String getInScopeSubQueryRealColumnName(String columnName) {
        if (_onClauseInline) {
            String msg = "Sorry! InScopeSubQuery of on-clause is unavailable";
            throw new IllegalConditionBeanOperationException(msg);
        }
        return _onClauseInline ? getRealAliasName() + "." + columnName : columnName;
    }

    @Override
    protected void registerExistsSubQuery(ConditionQuery subQuery, String columnName, String relatedColumnName, String propertyName) {
        String msg = "Sorry! ExistsSubQuery at in-line view is unavailable. So please use InScopeSubQyery.";
        throw new IllegalConditionBeanOperationException(msg);
    }

    // ===================================================================================
    //                                                                Override about Query
    //                                                                ====================
    protected ConditionValue getCValueId() { return _myCQ.getId(); }
    public String keepId_InScopeSubQuery_AccessResultDataAsOne(AccessResultDataCQ sq)
    { return _myCQ.keepId_InScopeSubQuery_AccessResultDataAsOne(sq); }
    public String keepId_NotInScopeSubQuery_AccessResultDataAsOne(AccessResultDataCQ sq)
    { return _myCQ.keepId_NotInScopeSubQuery_AccessResultDataAsOne(sq); }
    public String keepId_ExistsSubQuery_AccessResultDataAsOne(AccessResultDataCQ sq)
    { throwIICBOE("ExistsSubQuery"); return null; }
    public String keepId_NotExistsSubQuery_AccessResultDataAsOne(AccessResultDataCQ sq)
    { throwIICBOE("NotExistsSubQuery"); return null; }
    protected ConditionValue getCValueSessionId() { return _myCQ.getSessionId(); }
    protected ConditionValue getCValueRuleId() { return _myCQ.getRuleId(); }
    protected ConditionValue getCValueUrl() { return _myCQ.getUrl(); }
    protected ConditionValue getCValueParentUrl() { return _myCQ.getParentUrl(); }
    protected ConditionValue getCValueStatus() { return _myCQ.getStatus(); }
    protected ConditionValue getCValueHttpStatusCode() { return _myCQ.getHttpStatusCode(); }
    protected ConditionValue getCValueMethod() { return _myCQ.getMethod(); }
    protected ConditionValue getCValueMimeType() { return _myCQ.getMimeType(); }
    protected ConditionValue getCValueContentLength() { return _myCQ.getContentLength(); }
    protected ConditionValue getCValueExecutionTime() { return _myCQ.getExecutionTime(); }
    protected ConditionValue getCValueLastModified() { return _myCQ.getLastModified(); }
    protected ConditionValue getCValueCreateTime() { return _myCQ.getCreateTime(); }
    public String keepScalarSubQuery(AccessResultCQ subQuery)
    { throwIICBOE("ScalarSubQuery"); return null; }
    public String keepMyselfInScopeSubQuery(AccessResultCQ subQuery)
    { throwIICBOE("MyselfInScopeSubQuery"); return null;}

    protected void throwIICBOE(String name) { // throwInlineIllegalConditionBeanOperationException()
        throw new IllegalConditionBeanOperationException("Sorry! " + name + " at in-line view is unavailable!");
    }

    // ===================================================================================
    //                                                                       Very Internal
    //                                                                       =============
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    String xiCB() { return AccessResultCB.class.getName(); }
    String xiCQ() { return AccessResultCQ.class.getName(); }
}
