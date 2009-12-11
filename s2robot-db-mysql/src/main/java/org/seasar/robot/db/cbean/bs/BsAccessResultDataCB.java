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
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.db.cbean.nss.AccessResultNss;
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
 * The base condition-bean of ACCESS_RESULT_DATA.
 * @author DBFlute(AutoGenerator)
 */
public class BsAccessResultDataCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    protected AccessResultDataCQ _conditionQuery;

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
        return "ACCESS_RESULT_DATA";
    }

    public String getTableSqlName() {
        return "ACCESS_RESULT_DATA";
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
    public AccessResultDataCQ query() {
        return getConditionQuery();
    }

    public AccessResultDataCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new AccessResultDataCQ(null, getSqlClause(),
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
     * cb.query().union(new UnionQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<AccessResultDataCB> unionQuery) {
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForUnion();
        xsyncUQ(cb);
        unionQuery.query(cb);
        final AccessResultDataCQ cq = cb.query();
        query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;AccessResultDataCB&gt;() {
     *     public void query(AccessResultDataCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<AccessResultDataCB> unionQuery) {
        final AccessResultDataCB cb = new AccessResultDataCB();
        cb.xsetupForUnion();
        xsyncUQ(cb);
        unionQuery.query(cb);
        final AccessResultDataCQ cq = cb.query();
        query().xsetUnionAllQuery(cq);
    }

    // ===================================================================================
    //                                                                         SetupSelect
    //                                                                         ===========
    protected AccessResultNss _nssAccessResult;

    public AccessResultNss getNssAccessResult() {
        if (_nssAccessResult == null) {
            _nssAccessResult = new AccessResultNss(null);
        }
        return _nssAccessResult;
    }

    public AccessResultNss setupSelect_AccessResult() {
        doSetupSelect(new SsCall() {
            public ConditionQuery qf() {
                return query().queryAccessResult();
            }
        });
        if (_nssAccessResult == null || !_nssAccessResult.hasConditionQuery()) {
            _nssAccessResult = new AccessResultNss(query().queryAccessResult());
        }
        return _nssAccessResult;
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected HpSpecification _specification;

    public HpSpecification specify() {
        if (_specification == null) {
            _specification = new HpSpecification(this,
                    new HpSpQyCall<AccessResultDataCQ>() {
                        public boolean has() {
                            return true;
                        }

                        public AccessResultDataCQ qy() {
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
            HpAbstractSpecification<AccessResultDataCQ> {
        protected HpSpQyCall<AccessResultDataCQ> _myQyCall;

        protected AccessResultCB.HpSpecification _accessResult;

        public HpSpecification(ConditionBean baseCB,
                HpSpQyCall<AccessResultDataCQ> qyCall,
                boolean forDeriveReferrer, boolean forScalarSelect,
                boolean forScalarSubQuery, DBMetaProvider dbmetaProvider) {
            super(baseCB, qyCall, forDeriveReferrer, forScalarSelect,
                    forScalarSubQuery, dbmetaProvider);
            _myQyCall = qyCall;
        }

        /** ID: {PK : NotNull : BIGINT(19) : FK to ACCESS_RESULT} */
        public void columnId() {
            doColumn("ID");
        }

        /** TRANSFORMER_NAME: {NotNull : VARCHAR(255)} */
        public void columnTransformerName() {
            doColumn("TRANSFORMER_NAME");
        }

        /** DATA: {LONGBLOB(2147483647)} */
        public void columnData() {
            doColumn("DATA");
        }

        /** ENCODING: {VARCHAR(20)} */
        public void columnEncoding() {
            doColumn("ENCODING");
        }

        protected void doSpecifyRequiredColumn() {
            columnId(); // PK
        }

        protected String getTableDbName() {
            return "ACCESS_RESULT_DATA";
        }

        /**
         * ACCESS_RESULT as 'accessResult'.
         * @return Next specification. (NotNull)
         */
        public AccessResultCB.HpSpecification specifyAccessResult() {
            assertForeign("accessResult");
            if (_accessResult == null) {
                _accessResult = new AccessResultCB.HpSpecification(
                        _baseCB,
                        new HpSpQyCall<AccessResultCQ>() {
                            public boolean has() {
                                return _myQyCall.has()
                                        && _myQyCall
                                                .qy()
                                                .hasConditionQueryAccessResult();
                            }

                            public AccessResultCQ qy() {
                                return _myQyCall.qy().queryAccessResult();
                            }
                        }, _forDerivedReferrer, _forScalarSelect,
                        _forScalarSubQuery, _dbmetaProvider);
                if (_forGeneralOneSpecificaion) {
                    _accessResult.xsetupForGeneralOneSpecification(null);
                }
            }
            return _accessResult;
        }

        public void xsetupForGeneralOneSpecification(
                HpSpQyCall<AccessResultDataCQ> qyCall) {
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
    public HpColQyOperand<AccessResultDataCB> columnQuery(
            final SpecifyQuery<AccessResultDataCB> leftSpecifyQuery) {
        return new HpColQyOperand<AccessResultDataCB>(
                new HpColQyHandler<AccessResultDataCB>() {
                    public void handle(
                            SpecifyQuery<AccessResultDataCB> rightSp,
                            String operand) {
                        AccessResultDataCB cb = new AccessResultDataCB();
                        cb.specify().xsetupForGeneralOneSpecification(
                                new HpSpQyCall<AccessResultDataCQ>() {
                                    public boolean has() {
                                        return true;
                                    }

                                    public AccessResultDataCQ qy() {
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
    public void orQuery(OrQuery<AccessResultDataCB> orQuery) {
        xorQ((AccessResultDataCB) this, orQuery);
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
        return AccessResultDataCB.class.getName();
    }

    protected String getConditionQueryClassNameInternally() {
        return AccessResultDataCQ.class.getName();
    }

    protected String getSubQueryClassNameInternally() {
        return SubQuery.class.getName();
    }
}
