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

import org.seasar.dbflute.cbean.AbstractConditionBean;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.cbean.ConditionQuery;
import org.seasar.dbflute.cbean.SubQuery;
import org.seasar.dbflute.cbean.UnionQuery;
import org.seasar.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.db.allcommon.DBFluteConfig;
import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.allcommon.ImplementedSqlClauseCreator;
import org.seasar.robot.db.cbean.AccessResultCB;
import org.seasar.robot.db.cbean.AccessResultDataCB;
import org.seasar.robot.db.cbean.cq.AccessResultCQ;
import org.seasar.robot.db.cbean.cq.AccessResultDataCQ;
import org.seasar.robot.db.cbean.nss.AccessResultNss;

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
     * @return The conditionQuery of the local table as interface. (NotNull)
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
        unionQuery.query(cb);
        final AccessResultDataCQ cq = cb.query();
        query().xsetUnionAllQuery(cq);
    }

    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Setup Select
    //                                                                        ============
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
    protected Specification _specification;

    public Specification specify() {
        if (_specification == null) {
            _specification = new Specification(this,
                    new SpQyCall<AccessResultDataCQ>() {
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

    public static class Specification extends
            AbstractSpecification<AccessResultDataCQ> {
        protected SpQyCall<AccessResultDataCQ> _myQyCall;

        protected AccessResultCB.Specification _accessResult;

        public Specification(ConditionBean baseCB,
                SpQyCall<AccessResultDataCQ> qyCall, boolean forDeriveReferrer,
                boolean forScalarSelect, boolean forScalarSubQuery,
                DBMetaProvider dbmetaProvider) {
            super(baseCB, qyCall, forDeriveReferrer, forScalarSelect,
                    forScalarSubQuery, dbmetaProvider);
            _myQyCall = qyCall;
        }

        public void columnId() {
            doColumn("ID");
        }

        public void columnTransformerName() {
            doColumn("TRANSFORMER_NAME");
        }

        public void columnData() {
            doColumn("DATA");
        }

        public void columnEncoding() {
            doColumn("ENCODING");
        }

        protected void doSpecifyRequiredColumn() {
            columnId();// PK
            if (_myQyCall.qy().hasConditionQueryAccessResult()) {
            }
        }

        protected String getTableDbName() {
            return "ACCESS_RESULT_DATA";
        }

        public AccessResultCB.Specification specifyAccessResult() {
            assertForeign("accessResult");
            if (_accessResult == null) {
                _accessResult = new AccessResultCB.Specification(
                        _baseCB,
                        new SpQyCall<AccessResultCQ>() {
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
            }
            return _accessResult;
        }
    }

    // ===================================================================================
    //                                                                         Display SQL
    //                                                                         ===========
    @Override
    protected String getLogDateFormat() {
        return DBFluteConfig.getInstance().getLogDateFormat();
    }

    @Override
    protected String getLogTimestampFormat() {
        return DBFluteConfig.getInstance().getLogTimestampFormat();
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
