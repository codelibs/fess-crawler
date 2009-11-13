package org.seasar.robot.db.cbean.bs;

import java.util.Map;

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
import org.seasar.robot.db.allcommon.DBFluteConfig;
import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.allcommon.ImplementedInvokerAssistant;
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
        xsyncUQ(cb);
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
        xsyncUQ(cb);
        unionQuery.query(cb);
        final AccessResultCQ cq = cb.query();
        query().xsetUnionAllQuery(cq);
    }

    // ===================================================================================
    //                                                                         SetupSelect
    //                                                                         ===========

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
    protected HpSpecification _specification;

    public HpSpecification specify() {
        if (_specification == null) {
            _specification = new HpSpecification(this,
                    new HpSpQyCall<AccessResultCQ>() {
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

    protected HpAbstractSpecification<? extends ConditionQuery> localSp() {
        return specify();
    }

    public static class HpSpecification extends
            HpAbstractSpecification<AccessResultCQ> {
        protected HpSpQyCall<AccessResultCQ> _myQyCall;

        protected AccessResultDataCB.HpSpecification _accessResultDataAsOne;

        public HpSpecification(ConditionBean baseCB,
                HpSpQyCall<AccessResultCQ> qyCall, boolean forDeriveReferrer,
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

        /** RULE_ID: {VARCHAR(20)} */
        public void columnRuleId() {
            doColumn("RULE_ID");
        }

        /** URL: {NotNull : VARCHAR(65536)} */
        public void columnUrl() {
            doColumn("URL");
        }

        /** PARENT_URL: {VARCHAR(65536)} */
        public void columnParentUrl() {
            doColumn("PARENT_URL");
        }

        /** STATUS: {NotNull : INTEGER(10)} */
        public void columnStatus() {
            doColumn("STATUS");
        }

        /** HTTP_STATUS_CODE: {NotNull : INTEGER(10)} */
        public void columnHttpStatusCode() {
            doColumn("HTTP_STATUS_CODE");
        }

        /** METHOD: {NotNull : VARCHAR(10)} */
        public void columnMethod() {
            doColumn("METHOD");
        }

        /** MIME_TYPE: {NotNull : VARCHAR(100)} */
        public void columnMimeType() {
            doColumn("MIME_TYPE");
        }

        /** CONTENT_LENGTH: {NotNull : BIGINT(19)} */
        public void columnContentLength() {
            doColumn("CONTENT_LENGTH");
        }

        /** EXECUTION_TIME: {NotNull : INTEGER(10)} */
        public void columnExecutionTime() {
            doColumn("EXECUTION_TIME");
        }

        /** LAST_MODIFIED: {NotNull : TIMESTAMP(23, 10)} */
        public void columnLastModified() {
            doColumn("LAST_MODIFIED");
        }

        /** CREATE_TIME: {NotNull : TIMESTAMP(23, 10)} */
        public void columnCreateTime() {
            doColumn("CREATE_TIME");
        }

        protected void doSpecifyRequiredColumn() {
            columnId(); // PK
        }

        protected String getTableDbName() {
            return "ACCESS_RESULT";
        }

        /**
         * ACCESS_RESULT_DATA as 'accessResultDataAsOne'.
         * @return Next specification. (NotNull)
         */
        public AccessResultDataCB.HpSpecification specifyAccessResultDataAsOne() {
            assertForeign("accessResultDataAsOne");
            if (_accessResultDataAsOne == null) {
                _accessResultDataAsOne = new AccessResultDataCB.HpSpecification(
                        _baseCB,
                        new HpSpQyCall<AccessResultDataCQ>() {
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
                if (_forGeneralOneSpecificaion) {
                    _accessResultDataAsOne
                            .xsetupForGeneralOneSpecification(null);
                }
            }
            return _accessResultDataAsOne;
        }

        public void xsetupForGeneralOneSpecification(
                HpSpQyCall<AccessResultCQ> qyCall) {
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
    public HpColQyOperand<AccessResultCB> columnQuery(
            final SpecifyQuery<AccessResultCB> leftSpecifyQuery) {
        return new HpColQyOperand<AccessResultCB>(
                new HpColQyHandler<AccessResultCB>() {
                    public void handle(SpecifyQuery<AccessResultCB> rightSp,
                            String operand) {
                        AccessResultCB cb = new AccessResultCB();
                        cb.specify().xsetupForGeneralOneSpecification(
                                new HpSpQyCall<AccessResultCQ>() {
                                    public boolean has() {
                                        return true;
                                    }

                                    public AccessResultCQ qy() {
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
    public void orQuery(OrQuery<AccessResultCB> orQuery) {
        xorQ((AccessResultCB) this, orQuery);
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
        return AccessResultCB.class.getName();
    }

    protected String getConditionQueryClassNameInternally() {
        return AccessResultCQ.class.getName();
    }

    protected String getSubQueryClassNameInternally() {
        return SubQuery.class.getName();
    }
}
