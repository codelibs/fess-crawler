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
import org.seasar.robot.db.cbean.UrlQueueCB;
import org.seasar.robot.db.cbean.cq.UrlQueueCQ;

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
        unionQuery.query(cb);
        final UrlQueueCQ cq = cb.query();
        query().xsetUnionAllQuery(cq);
    }

    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Setup Select
    //                                                                        ============

    // [DBFlute-0.7.4]
    // ===================================================================================
    //                                                                             Specify
    //                                                                             =======
    protected Specification _specification;

    public Specification specify() {
        if (_specification == null) {
            _specification = new Specification(this,
                    new SpQyCall<UrlQueueCQ>() {
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

    public static class Specification extends AbstractSpecification<UrlQueueCQ> {
        protected SpQyCall<UrlQueueCQ> _myQyCall;

        public Specification(ConditionBean baseCB, SpQyCall<UrlQueueCQ> qyCall,
                boolean forDeriveReferrer, boolean forScalarSelect,
                boolean forScalarSubQuery, DBMetaProvider dbmetaProvider) {
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

        public void columnMethod() {
            doColumn("METHOD");
        }

        public void columnUrl() {
            doColumn("URL");
        }

        public void columnParentUrl() {
            doColumn("PARENT_URL");
        }

        public void columnDepth() {
            doColumn("DEPTH");
        }

        public void columnCreateTime() {
            doColumn("CREATE_TIME");
        }

        protected void doSpecifyRequiredColumn() {
            columnId();// PK
        }

        protected String getTableDbName() {
            return "URL_QUEUE";
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
        return UrlQueueCB.class.getName();
    }

    protected String getConditionQueryClassNameInternally() {
        return UrlQueueCQ.class.getName();
    }

    protected String getSubQueryClassNameInternally() {
        return SubQuery.class.getName();
    }
}
