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
import org.seasar.robot.db.cbean.UrlFilterCB;
import org.seasar.robot.db.cbean.cq.UrlFilterCQ;
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
 * The base condition-bean of URL_FILTER.
 * @author DBFlute(AutoGenerator)
 */
public class BsUrlFilterCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    protected UrlFilterCQ _conditionQuery;

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
        return "URL_FILTER";
    }

    public String getTableSqlName() {
        return "URL_FILTER";
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
    public UrlFilterCQ query() {
        return getConditionQuery();
    }

    public UrlFilterCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new UrlFilterCQ(null, getSqlClause(),
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
     * cb.query().union(new UnionQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<UrlFilterCB> unionQuery) {
        final UrlFilterCB cb = new UrlFilterCB();
        cb.xsetupForUnion();
        xsyncUQ(cb);
        unionQuery.query(cb);
        final UrlFilterCQ cq = cb.query();
        query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;UrlFilterCB&gt;() {
     *     public void query(UrlFilterCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<UrlFilterCB> unionQuery) {
        final UrlFilterCB cb = new UrlFilterCB();
        cb.xsetupForUnion();
        xsyncUQ(cb);
        unionQuery.query(cb);
        final UrlFilterCQ cq = cb.query();
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
                    new HpSpQyCall<UrlFilterCQ>() {
                        public boolean has() {
                            return true;
                        }

                        public UrlFilterCQ qy() {
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
            HpAbstractSpecification<UrlFilterCQ> {
        protected HpSpQyCall<UrlFilterCQ> _myQyCall;

        public HpSpecification(ConditionBean baseCB,
                HpSpQyCall<UrlFilterCQ> qyCall, boolean forDeriveReferrer,
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

        /** URL: {NotNull : VARCHAR(65536)} */
        public void columnUrl() {
            doColumn("URL");
        }

        /** FILTER_TYPE: {NotNull : VARCHAR(1)} */
        public void columnFilterType() {
            doColumn("FILTER_TYPE");
        }

        /** CREATE_TIME: {NotNull : TIMESTAMP(23, 10)} */
        public void columnCreateTime() {
            doColumn("CREATE_TIME");
        }

        protected void doSpecifyRequiredColumn() {
            columnId(); // PK
        }

        protected String getTableDbName() {
            return "URL_FILTER";
        }

        public void xsetupForGeneralOneSpecification(
                HpSpQyCall<UrlFilterCQ> qyCall) {
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
    public HpColQyOperand<UrlFilterCB> columnQuery(
            final SpecifyQuery<UrlFilterCB> leftSpecifyQuery) {
        return new HpColQyOperand<UrlFilterCB>(
                new HpColQyHandler<UrlFilterCB>() {
                    public void handle(SpecifyQuery<UrlFilterCB> rightSp,
                            String operand) {
                        UrlFilterCB cb = new UrlFilterCB();
                        cb.specify().xsetupForGeneralOneSpecification(
                                new HpSpQyCall<UrlFilterCQ>() {
                                    public boolean has() {
                                        return true;
                                    }

                                    public UrlFilterCQ qy() {
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
    public void orQuery(OrQuery<UrlFilterCB> orQuery) {
        xorQ((UrlFilterCB) this, orQuery);
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
        return UrlFilterCB.class.getName();
    }

    protected String getConditionQueryClassNameInternally() {
        return UrlFilterCQ.class.getName();
    }

    protected String getSubQueryClassNameInternally() {
        return SubQuery.class.getName();
    }
}
