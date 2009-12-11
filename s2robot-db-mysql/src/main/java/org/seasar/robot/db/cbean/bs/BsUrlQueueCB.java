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
package org.seasar.robot.db.cbean.bs;

import java.util.Map;

import org.seasar.robot.db.allcommon.DBFluteConfig;
import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.allcommon.ImplementedInvokerAssistant;
import org.seasar.robot.db.allcommon.ImplementedSqlClauseCreator;
import org.seasar.robot.db.cbean.UrlQueueCB;
import org.seasar.robot.db.cbean.cq.UrlQueueCQ;
import org.seasar.robot.dbflute.cbean.AbstractConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.OrQuery;
import org.seasar.robot.dbflute.cbean.SpecifyQuery;
import org.seasar.robot.dbflute.cbean.SubQuery;
import org.seasar.robot.dbflute.cbean.UnionQuery;
import org.seasar.robot.dbflute.cbean.chelper.HpAbstractSpecification;
import org.seasar.robot.dbflute.cbean.chelper.HpColQyHandler;
import org.seasar.robot.dbflute.cbean.chelper.HpColQyOperand;
import org.seasar.robot.dbflute.cbean.chelper.HpSpQyCall;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.twowaysql.factory.SqlAnalyzerFactory;

/**
 * The base condition-bean of URL_QUEUE.
 * @author DBFlute(AutoGenerator)
 */
public class BsUrlQueueCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    protected UrlQueueCQ _conditionQuery;

    // ===================================================================================
    //                                                                           SqlClause
    //                                                                           =========
    @Override
    protected SqlClause createSqlClause() {
        return new ImplementedSqlClauseCreator().createSqlClause(this);
    }

    // ===================================================================================
    //                                                                     DBMeta Provider
    //                                                                     ===============
    @Override
    protected DBMetaProvider getDBMetaProvider() {
        return _dbmetaProvider;
    }

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "URL_QUEUE";
    }

    public String getTableSqlName() {
        return "URL_QUEUE";
    }

    // ===================================================================================
    //                                                                      PrimaryKey Map
    //                                                                      ==============
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
        assertPrimaryKeyMap(primaryKeyMap);
        {
            Object obj = primaryKeyMap.get("ID");
            if (obj instanceof Long) {
                query().setId_Equal((Long) obj);
            } else {
                query().setId_Equal(new Long((String) obj));
            }
        }

    }

    // ===================================================================================
    //                                                                     OrderBy Setting
    //                                                                     ===============
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_Id_Asc();
        return this;
    }

    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_Id_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    public UrlQueueCQ query() {
        return getConditionQuery();
    }

    public UrlQueueCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new UrlQueueCQ(null, getSqlClause(),
                    getSqlClause().getLocalTableAliasName(), 0);
        }
        return _conditionQuery;
    }

    /**
     * {@inheritDoc}
     */
    public ConditionQuery localCQ() {
        return getConditionQuery();
    }

    // ===================================================================================
    //                                                                               Union
    //                                                                               =====
    /**
     * Set up 'union'.
     * <pre>
     * cb.query().union(new UnionQuery&lt;UrlQueueCB&gt;() {
     *     public void query(UrlQueueCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<UrlQueueCB> unionQuery) {
        final UrlQueueCB cb = new UrlQueueCB();
        cb.xsetupForUnion();
        xsyncUQ(cb);
        unionQuery.query(cb);
        final UrlQueueCQ cq = cb.query();
        query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;UrlQueueCB&gt;() {
     *     public void query(UrlQueueCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<UrlQueueCB> unionQuery) {
        final UrlQueueCB cb = new UrlQueueCB();
        cb.xsetupForUnion();
        xsyncUQ(cb);
        unionQuery.query(cb);
        final UrlQueueCQ cq = cb.query();
        query().xsetUnionAllQuery(cq);
    }

    // ===================================================================================
    //                                                                         SetupSelect
    //                                                                         ===========

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected HpSpecification _specification;

    public HpSpecification specify() {
        if (_specification == null) {
            _specification = new HpSpecification(this,
                    new HpSpQyCall<UrlQueueCQ>() {
                        public boolean has() {
                            return true;
                        }

                        public UrlQueueCQ qy() {
                            return query();
                        }
                    }, _forDerivedReferrer, _forScalarSelect,
                    _forScalarSubQuery, getDBMetaProvider());
        }
        return _specification;
    }

    protected HpAbstractSpecification<? extends ConditionQuery> localSp() {
        return specify();
    }

    public static class HpSpecification extends
            HpAbstractSpecification<UrlQueueCQ> {
        protected HpSpQyCall<UrlQueueCQ> _myQyCall;

        public HpSpecification(ConditionBean baseCB,
                HpSpQyCall<UrlQueueCQ> qyCall, boolean forDeriveReferrer,
                boolean forScalarSelect, boolean forScalarSubQuery,
                DBMetaProvider dbmetaProvider) {
            super(baseCB, qyCall, forDeriveReferrer, forScalarSelect,
                    forScalarSubQuery, dbmetaProvider);
            _myQyCall = qyCall;
        }

        /** ID: {PK : ID : NotNull : BIGINT(19)} */
        public void columnId() {
            doColumn("ID");
        }

        /** SESSION_ID: {NotNull : VARCHAR(20)} */
        public void columnSessionId() {
            doColumn("SESSION_ID");
        }

        /** METHOD: {NotNull : VARCHAR(10)} */
        public void columnMethod() {
            doColumn("METHOD");
        }

        /** URL: {NotNull : TEXT(65535)} */
        public void columnUrl() {
            doColumn("URL");
        }

        /** PARENT_URL: {TEXT(65535)} */
        public void columnParentUrl() {
            doColumn("PARENT_URL");
        }

        /** DEPTH: {NotNull : INT(10)} */
        public void columnDepth() {
            doColumn("DEPTH");
        }

        /** LAST_MODIFIED: {DATETIME(19)} */
        public void columnLastModified() {
            doColumn("LAST_MODIFIED");
        }

        /** CREATE_TIME: {NotNull : DATETIME(19)} */
        public void columnCreateTime() {
            doColumn("CREATE_TIME");
        }

        protected void doSpecifyRequiredColumn() {
            columnId(); // PK
        }

        protected String getTableDbName() {
            return "URL_QUEUE";
        }

        public void xsetupForGeneralOneSpecification(
                HpSpQyCall<UrlQueueCQ> qyCall) {
            if (qyCall != null) {
                _myQyCall = qyCall;
                _qyCall = qyCall;
            }
            _forGeneralOneSpecificaion = true;
        }
    }

    // [DBFlute-0.9.5.3]
    // ===================================================================================
    //                                                                         ColumnQuery
    //                                                                         ===========
    /**
     * @param leftSpecifyQuery The specify-query for left column. (NotNull)
     * @return The object for setting up operand and right column. (NotNull)
     */
    public HpColQyOperand<UrlQueueCB> columnQuery(
            final SpecifyQuery<UrlQueueCB> leftSpecifyQuery) {
        return new HpColQyOperand<UrlQueueCB>(new HpColQyHandler<UrlQueueCB>() {
            public void handle(SpecifyQuery<UrlQueueCB> rightSp, String operand) {
                UrlQueueCB cb = new UrlQueueCB();
                cb.specify().xsetupForGeneralOneSpecification(
                        new HpSpQyCall<UrlQueueCQ>() {
                            public boolean has() {
                                return true;
                            }

                            public UrlQueueCQ qy() {
                                return query();
                            }
                        });
                xcolqy(cb, leftSpecifyQuery, rightSp, operand);
            }
        });
    }

    // [DBFlute-0.9.5.5]
    // ===================================================================================
    //                                                                             OrQuery
    //                                                                             =======
    public void orQuery(OrQuery<UrlQueueCB> orQuery) {
        xorQ((UrlQueueCB) this, orQuery);
    }

    // ===================================================================================
    //                                                                          DisplaySQL
    //                                                                          ==========
    @Override
    protected SqlAnalyzerFactory getSqlAnalyzerFactory() {
        return new ImplementedInvokerAssistant().assistSqlAnalyzerFactory();
    }

    @Override
    protected String getLogDateFormat() {
        return DBFluteConfig.getInstance().getLogDateFormat();
    }

    @Override
    protected String getLogTimestampFormat() {
        return DBFluteConfig.getInstance().getLogTimestampFormat();
    }

    // ===================================================================================
    //                                                          Basic Status Determination
    //                                                          ==========================
    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                            Internal
    //                                                                            ========
    // Very Internal (for Suppressing Warn about 'Not Use Import')
    protected String getConditionBeanClassNameInternally() {
        return UrlQueueCB.class.getName();
    }

    protected String getConditionQueryClassNameInternally() {
        return UrlQueueCQ.class.getName();
    }

    protected String getSubQueryClassNameInternally() {
        return SubQuery.class.getName();
    }
}
