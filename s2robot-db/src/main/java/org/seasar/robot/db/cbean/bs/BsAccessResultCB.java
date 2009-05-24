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
import org.seasar.robot.db.cbean.nss.AccessResultDataNss;

/**
 * The base condition-bean of ACCESS_RESULT.
 * @author DBFlute(AutoGenerator)
 */
public class BsAccessResultCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final DBMetaProvider _dbmetaProvider = new DBMetaInstanceHandler();

    protected AccessResultCQ _conditionQuery;

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
        return "ACCESS_RESULT";
    }

    public String getTableSqlName() {
        return "ACCESS_RESULT";
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
    public AccessResultCQ query() {
        return getConditionQuery();
    }

    public AccessResultCQ getConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = new AccessResultCQ(null, getSqlClause(),
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
     * cb.query().union(new UnionQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void union(UnionQuery<AccessResultCB> unionQuery) {
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForUnion();
        unionQuery.query(cb);
        final AccessResultCQ cq = cb.query();
        query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all'.
     * <pre>
     * cb.query().unionAll(new UnionQuery&lt;AccessResultCB&gt;() {
     *     public void query(AccessResultCB unionCB) {
     *         unionCB.query().setXxx...
     *     }
     * });
     * </pre>
     * @param unionQuery The query of 'union'. (NotNull)
     */
    public void unionAll(UnionQuery<AccessResultCB> unionQuery) {
        final AccessResultCB cb = new AccessResultCB();
        cb.xsetupForUnion();
        unionQuery.query(cb);
        final AccessResultCQ cq = cb.query();
        query().xsetUnionAllQuery(cq);
    }

    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Setup Select
    //                                                                        ============

    protected AccessResultDataNss _nssAccessResultDataAsOne;

    public AccessResultDataNss getNssAccessResultDataAsOne() {
        if (_nssAccessResultDataAsOne == null) {
            _nssAccessResultDataAsOne = new AccessResultDataNss(null);
        }
        return _nssAccessResultDataAsOne;
    }

    public AccessResultDataNss setupSelect_AccessResultDataAsOne() {
        doSetupSelect(new SsCall() {
            public ConditionQuery qf() {
                return query().queryAccessResultDataAsOne();
            }
        });
        if (_nssAccessResultDataAsOne == null
                || !_nssAccessResultDataAsOne.hasConditionQuery()) {
            _nssAccessResultDataAsOne = new AccessResultDataNss(query()
                    .queryAccessResultDataAsOne());
        }
        return _nssAccessResultDataAsOne;
    }

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected Specification _specification;

    public Specification specify() {
        if (_specification == null) {
            _specification = new Specification(this,
                    new SpQyCall<AccessResultCQ>() {
                        public boolean has() {
                            return true;
                        }

                        public AccessResultCQ qy() {
                            return query();
                        }
                    }, _forDerivedReferrer, _forScalarSelect,
                    _forScalarSubQuery, getDBMetaProvider());
        }
        return _specification;
    }

    public static class Specification extends
            AbstractSpecification<AccessResultCQ> {
        protected SpQyCall<AccessResultCQ> _myQyCall;

        protected AccessResultDataCB.Specification _accessResultDataAsOne;

        public Specification(ConditionBean baseCB,
                SpQyCall<AccessResultCQ> qyCall, boolean forDeriveReferrer,
                boolean forScalarSelect, boolean forScalarSubQuery,
                DBMetaProvider dbmetaProvider) {
            super(baseCB, qyCall, forDeriveReferrer, forScalarSelect,
                    forScalarSubQuery, dbmetaProvider);
            _myQyCall = qyCall;
        }

        public void columnId() {
            doColumn("ID");
        }

        public void columnSessionId() {
            doColumn("SESSION_ID");
        }

        public void columnRuleId() {
            doColumn("RULE_ID");
        }

        public void columnUrl() {
            doColumn("URL");
        }

        public void columnParentUrl() {
            doColumn("PARENT_URL");
        }

        public void columnStatus() {
            doColumn("STATUS");
        }

        public void columnHttpStatusCode() {
            doColumn("HTTP_STATUS_CODE");
        }

        public void columnMethod() {
            doColumn("METHOD");
        }

        public void columnMimeType() {
            doColumn("MIME_TYPE");
        }

        public void columnContentLength() {
            doColumn("CONTENT_LENGTH");
        }

        public void columnExecutionTime() {
            doColumn("EXECUTION_TIME");
        }

        public void columnCreateTime() {
            doColumn("CREATE_TIME");
        }

        protected void doSpecifyRequiredColumn() {
            columnId();// PK
        }

        protected String getTableDbName() {
            return "ACCESS_RESULT";
        }

        public AccessResultDataCB.Specification specifyAccessResultDataAsOne() {
            assertForeign("accessResultDataAsOne");
            if (_accessResultDataAsOne == null) {
                _accessResultDataAsOne = new AccessResultDataCB.Specification(
                        _baseCB,
                        new SpQyCall<AccessResultDataCQ>() {
                            public boolean has() {
                                return _myQyCall.has()
                                        && _myQyCall
                                                .qy()
                                                .hasConditionQueryAccessResultDataAsOne();
                            }

                            public AccessResultDataCQ qy() {
                                return _myQyCall.qy()
                                        .queryAccessResultDataAsOne();
                            }
                        }, _forDerivedReferrer, _forScalarSelect,
                        _forScalarSubQuery, _dbmetaProvider);
            }
            return _accessResultDataAsOne;
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
        return AccessResultCB.class.getName();
    }

    protected String getConditionQueryClassNameInternally() {
        return AccessResultCQ.class.getName();
    }

    protected String getSubQueryClassNameInternally() {
        return SubQuery.class.getName();
    }
}
