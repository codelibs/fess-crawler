package org.codelibs.robot.db.cbean.bs;

import org.codelibs.robot.db.allcommon.DBFluteConfig;
import org.codelibs.robot.db.allcommon.DBMetaInstanceHandler;
import org.codelibs.robot.db.allcommon.ImplementedInvokerAssistant;
import org.codelibs.robot.db.allcommon.ImplementedSqlClauseCreator;
import org.codelibs.robot.db.cbean.UrlQueueCB;
import org.codelibs.robot.db.cbean.cq.UrlQueueCQ;
import org.dbflute.cbean.AbstractConditionBean;
import org.dbflute.cbean.ConditionBean;
import org.dbflute.cbean.ConditionQuery;
import org.dbflute.cbean.chelper.HpAbstractSpecification;
import org.dbflute.cbean.chelper.HpCBPurpose;
import org.dbflute.cbean.chelper.HpColQyOperand;
import org.dbflute.cbean.chelper.HpColumnSpHandler;
import org.dbflute.cbean.chelper.HpSDRFunction;
import org.dbflute.cbean.chelper.HpSDRFunctionFactory;
import org.dbflute.cbean.chelper.HpSpQyCall;
import org.dbflute.cbean.coption.ConditionOption;
import org.dbflute.cbean.coption.DerivedReferrerOption;
import org.dbflute.cbean.dream.SpecifiedColumn;
import org.dbflute.cbean.scoping.AndQuery;
import org.dbflute.cbean.scoping.OrQuery;
import org.dbflute.cbean.scoping.SpecifyQuery;
import org.dbflute.cbean.scoping.SubQuery;
import org.dbflute.cbean.scoping.UnionQuery;
import org.dbflute.cbean.sqlclause.SqlClause;
import org.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.dbflute.dbmeta.DBMetaProvider;
import org.dbflute.twowaysql.factory.SqlAnalyzerFactory;
import org.dbflute.twowaysql.style.BoundDateDisplayTimeZoneProvider;

/**
 * The base condition-bean of URL_QUEUE.
 * @author DBFlute(AutoGenerator)
 */
public class BsUrlQueueCB extends AbstractConditionBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected UrlQueueCQ _conditionQuery;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsUrlQueueCB() {
        if (DBFluteConfig.getInstance().isPagingCountLater()) {
            enablePagingCountLater();
        }
        if (DBFluteConfig.getInstance().isPagingCountLeastJoin()) {
            enablePagingCountLeastJoin();
        }
        if (DBFluteConfig.getInstance().isNonSpecifiedColumnAccessAllowed()) {
            enableNonSpecifiedColumnAccess();
        }
        if (DBFluteConfig.getInstance().isQueryUpdateCountPreCheck()) {
            enableQueryUpdateCountPreCheck();
        }
    }

    // ===================================================================================
    //                                                                           SqlClause
    //                                                                           =========
    @Override
    protected SqlClause createSqlClause() {
        final SqlClauseCreator creator = DBFluteConfig.getInstance()
                .getSqlClauseCreator();
        if (creator != null) {
            return creator.createSqlClause(this);
        }
        return new ImplementedSqlClauseCreator().createSqlClause(this); // as default
    }

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    @Override
    protected DBMetaProvider getDBMetaProvider() {
        return DBMetaInstanceHandler.getProvider(); // as default
    }

    @Override
    public String asTableDbName() {
        return "URL_QUEUE";
    }

    // ===================================================================================
    //                                                                 PrimaryKey Handling
    //                                                                 ===================
    /**
     * Accept the query condition of primary key as equal.
     * @param id : PK, ID, NotNull, BIGINT(19). (NotNull)
     * @return this. (NotNull)
     */
    public UrlQueueCB acceptPK(final Long id) {
        assertObjectNotNull("id", id);
        final BsUrlQueueCB cb = this;
        cb.query().setId_Equal(id);
        return (UrlQueueCB) this;
    }

    @Override
    public ConditionBean addOrderBy_PK_Asc() {
        query().addOrderBy_Id_Asc();
        return this;
    }

    @Override
    public ConditionBean addOrderBy_PK_Desc() {
        query().addOrderBy_Id_Desc();
        return this;
    }

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    /**
     * Prepare for various queries. <br>
     * Examples of main functions are following:
     * <pre>
     * <span style="color: #3F7E5E">// Basic Queries</span>
     * cb.query().setMemberId_Equal(value);        <span style="color: #3F7E5E">// =</span>
     * cb.query().setMemberId_NotEqual(value);     <span style="color: #3F7E5E">// !=</span>
     * cb.query().setMemberId_GreaterThan(value);  <span style="color: #3F7E5E">// &gt;</span>
     * cb.query().setMemberId_LessThan(value);     <span style="color: #3F7E5E">// &lt;</span>
     * cb.query().setMemberId_GreaterEqual(value); <span style="color: #3F7E5E">// &gt;=</span>
     * cb.query().setMemberId_LessEqual(value);    <span style="color: #3F7E5E">// &lt;=</span>
     * cb.query().setMemberName_InScope(valueList);    <span style="color: #3F7E5E">// in ('a', 'b')</span>
     * cb.query().setMemberName_NotInScope(valueList); <span style="color: #3F7E5E">// not in ('a', 'b')</span>
     * <span style="color: #3F7E5E">// LikeSearch with various options: (versatile)</span>
     * <span style="color: #3F7E5E">// {like ... [options]}</span>
     * cb.query().setMemberName_LikeSearch(value, option);
     * cb.query().setMemberName_NotLikeSearch(value, option); <span style="color: #3F7E5E">// not like ...</span>
     * <span style="color: #3F7E5E">// FromTo with various options: (versatile)</span>
     * <span style="color: #3F7E5E">// {(default) fromDatetime &lt;= BIRTHDATE &lt;= toDatetime}</span>
     * cb.query().setBirthdate_FromTo(fromDatetime, toDatetime, option);
     * <span style="color: #3F7E5E">// DateFromTo: (Date means yyyy/MM/dd)</span>
     * <span style="color: #3F7E5E">// {fromDate &lt;= BIRTHDATE &lt; toDate + 1 day}</span>
     * cb.query().setBirthdate_IsNull();    <span style="color: #3F7E5E">// is null</span>
     * cb.query().setBirthdate_IsNotNull(); <span style="color: #3F7E5E">// is not null</span>
     *
     * <span style="color: #3F7E5E">// ExistsReferrer: (correlated sub-query)</span>
     * <span style="color: #3F7E5E">// {where exists (select PURCHASE_ID from PURCHASE where ...)}</span>
     * cb.query().existsPurchase(purchaseCB <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     purchaseCB.query().set... <span style="color: #3F7E5E">// referrer sub-query condition</span>
     * });
     * cb.query().notExistsPurchase...
     *
     * <span style="color: #3F7E5E">// (Query)DerivedReferrer: (correlated sub-query)</span>
     * cb.query().derivedPurchaseList().max(purchaseCB <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     purchaseCB.specify().columnPurchasePrice(); <span style="color: #3F7E5E">// derived column for function</span>
     *     purchaseCB.query().set... <span style="color: #3F7E5E">// referrer sub-query condition</span>
     * }).greaterEqual(value);
     *
     * <span style="color: #3F7E5E">// ScalarCondition: (self-table sub-query)</span>
     * cb.query().scalar_Equal().max(scalarCB <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     scalarCB.specify().columnBirthdate(); <span style="color: #3F7E5E">// derived column for function</span>
     *     scalarCB.query().set... <span style="color: #3F7E5E">// scalar sub-query condition</span>
     * });
     *
     * <span style="color: #3F7E5E">// OrderBy</span>
     * cb.query().addOrderBy_MemberName_Asc();
     * cb.query().addOrderBy_MemberName_Desc().withManualOrder(option);
     * cb.query().addOrderBy_MemberName_Desc().withNullsFirst();
     * cb.query().addOrderBy_MemberName_Desc().withNullsLast();
     * cb.query().addSpecifiedDerivedOrderBy_Desc(aliasName);
     *
     * <span style="color: #3F7E5E">// Query(Relation)</span>
     * cb.query().queryMemberStatus()...;
     * cb.query().queryMemberAddressAsValid(targetDate)...;
     * </pre>
     * @return The instance of condition-query for base-point table to set up query. (NotNull)
     */
    public UrlQueueCQ query() {
        assertQueryPurpose(); // assert only when user-public query
        return doGetConditionQuery();
    }

    public UrlQueueCQ xdfgetConditionQuery() { // public for parameter comment and internal
        return doGetConditionQuery();
    }

    protected UrlQueueCQ doGetConditionQuery() {
        if (_conditionQuery == null) {
            _conditionQuery = createLocalCQ();
        }
        return _conditionQuery;
    }

    protected UrlQueueCQ createLocalCQ() {
        return xcreateCQ(null, getSqlClause(), getSqlClause()
                .getBasePointAliasName(), 0);
    }

    protected UrlQueueCQ xcreateCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        final UrlQueueCQ cq = xnewCQ(childQuery, sqlClause, aliasName,
                nestLevel);
        cq.xsetBaseCB(this);
        return cq;
    }

    protected UrlQueueCQ xnewCQ(final ConditionQuery childQuery,
            final SqlClause sqlClause, final String aliasName,
            final int nestLevel) {
        return new UrlQueueCQ(childQuery, sqlClause, aliasName, nestLevel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConditionQuery localCQ() {
        return doGetConditionQuery();
    }

    // ===================================================================================
    //                                                                               Union
    //                                                                               =====
    /**
     * Set up 'union' for base-point table. <br>
     * You don't need to call SetupSelect in union-query,
     * because it inherits calls before. (Don't call SetupSelect after here)
     * <pre>
     * cb.query().<span style="color: #CC4747">union</span>(<span style="color: #553000">unionCB</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     <span style="color: #553000">unionCB</span>.query().set...
     * });
     * </pre>
     * @param unionCBLambda The callback for query of 'union'. (NotNull)
     */
    public void union(final UnionQuery<UrlQueueCB> unionCBLambda) {
        final UrlQueueCB cb = new UrlQueueCB();
        cb.xsetupForUnion(this);
        xsyncUQ(cb);
        try {
            lock();
            unionCBLambda.query(cb);
        } finally {
            unlock();
        }
        xsaveUCB(cb);
        final UrlQueueCQ cq = cb.query();
        query().xsetUnionQuery(cq);
    }

    /**
     * Set up 'union all' for base-point table. <br>
     * You don't need to call SetupSelect in union-query,
     * because it inherits calls before. (Don't call SetupSelect after here)
     * <pre>
     * cb.query().<span style="color: #CC4747">unionAll</span>(<span style="color: #553000">unionCB</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     <span style="color: #553000">unionCB</span>.query().set...
     * });
     * </pre>
     * @param unionCBLambda The callback for query of 'union all'. (NotNull)
     */
    public void unionAll(final UnionQuery<UrlQueueCB> unionCBLambda) {
        final UrlQueueCB cb = new UrlQueueCB();
        cb.xsetupForUnion(this);
        xsyncUQ(cb);
        try {
            lock();
            unionCBLambda.query(cb);
        } finally {
            unlock();
        }
        xsaveUCB(cb);
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

    /**
     * Prepare for SpecifyColumn, (Specify)DerivedReferrer. <br>
     * This method should be called after SetupSelect.
     * <pre>
     * <span style="color: #0000C0">memberBhv</span>.selectEntity(<span style="color: #553000">cb</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     <span style="color: #553000">cb</span>.setupSelect_MemberStatus(); <span style="color: #3F7E5E">// should be called before specify()</span>
     *     <span style="color: #553000">cb</span>.specify().columnMemberName();
     *     <span style="color: #553000">cb</span>.specify().specifyMemberStatus().columnMemberStatusName();
     *     <span style="color: #553000">cb</span>.specify().derivedPurchaseList().max(<span style="color: #553000">purchaseCB</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *         <span style="color: #553000">purchaseCB</span>.specify().columnPurchaseDatetime();
     *         <span style="color: #553000">purchaseCB</span>.query().set...
     *     }, aliasName);
     * }).alwaysPresent(<span style="color: #553000">member</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     ...
     * });
     * </pre>
     * @return The instance of specification. (NotNull)
     */
    public HpSpecification specify() {
        assertSpecifyPurpose();
        if (_specification == null) {
            _specification = new HpSpecification(this, xcreateSpQyCall(
                    () -> true, () -> xdfgetConditionQuery()), _purpose,
                    getDBMetaProvider(), xcSDRFnFc());
        }
        return _specification;
    }

    @Override
    public HpColumnSpHandler localSp() {
        return specify();
    }

    @Override
    public boolean hasSpecifiedColumn() {
        return _specification != null
                && _specification.isAlreadySpecifiedRequiredColumn();
    }

    public static class HpSpecification extends
            HpAbstractSpecification<UrlQueueCQ> {
        public HpSpecification(final ConditionBean baseCB,
                final HpSpQyCall<UrlQueueCQ> qyCall, final HpCBPurpose purpose,
                final DBMetaProvider dbmetaProvider,
                final HpSDRFunctionFactory sdrFuncFactory) {
            super(baseCB, qyCall, purpose, dbmetaProvider, sdrFuncFactory);
        }

        /**
         * ID: {PK, ID, NotNull, BIGINT(19)}
         * @return The information object of specified column. (NotNull)
         */
        public SpecifiedColumn columnId() {
            return doColumn("ID");
        }

        /**
         * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
         * @return The information object of specified column. (NotNull)
         */
        public SpecifiedColumn columnSessionId() {
            return doColumn("SESSION_ID");
        }

        /**
         * METHOD: {NotNull, VARCHAR(10)}
         * @return The information object of specified column. (NotNull)
         */
        public SpecifiedColumn columnMethod() {
            return doColumn("METHOD");
        }

        /**
         * URL: {NotNull, TEXT(65535)}
         * @return The information object of specified column. (NotNull)
         */
        public SpecifiedColumn columnUrl() {
            return doColumn("URL");
        }

        /**
         * META_DATA: {TEXT(65535)}
         * @return The information object of specified column. (NotNull)
         */
        public SpecifiedColumn columnMetaData() {
            return doColumn("META_DATA");
        }

        /**
         * ENCODING: {VARCHAR(20)}
         * @return The information object of specified column. (NotNull)
         */
        public SpecifiedColumn columnEncoding() {
            return doColumn("ENCODING");
        }

        /**
         * PARENT_URL: {TEXT(65535)}
         * @return The information object of specified column. (NotNull)
         */
        public SpecifiedColumn columnParentUrl() {
            return doColumn("PARENT_URL");
        }

        /**
         * DEPTH: {NotNull, INT(10)}
         * @return The information object of specified column. (NotNull)
         */
        public SpecifiedColumn columnDepth() {
            return doColumn("DEPTH");
        }

        /**
         * LAST_MODIFIED: {BIGINT(19)}
         * @return The information object of specified column. (NotNull)
         */
        public SpecifiedColumn columnLastModified() {
            return doColumn("LAST_MODIFIED");
        }

        /**
         * CREATE_TIME: {NotNull, BIGINT(19)}
         * @return The information object of specified column. (NotNull)
         */
        public SpecifiedColumn columnCreateTime() {
            return doColumn("CREATE_TIME");
        }

        @Override
        public void everyColumn() {
            doEveryColumn();
        }

        @Override
        public void exceptRecordMetaColumn() {
            doExceptRecordMetaColumn();
        }

        @Override
        protected void doSpecifyRequiredColumn() {
            columnId(); // PK
        }

        @Override
        protected String getTableDbName() {
            return "URL_QUEUE";
        }

        /**
         * Prepare for (Specify)MyselfDerived (SubQuery).
         * @return The object to set up a function for myself table. (NotNull)
         */
        public HpSDRFunction<UrlQueueCB, UrlQueueCQ> myselfDerived() {
            assertDerived("myselfDerived");
            if (xhasSyncQyCall()) {
                xsyncQyCall().qy();
            } // for sync (for example, this in ColumnQuery)
            return cHSDRF(_baseCB, _qyCall.qy(),
                    (final String fn, final SubQuery<UrlQueueCB> sq,
                            final UrlQueueCQ cq, final String al,
                            final DerivedReferrerOption op) -> cq
                            .xsmyselfDerive(fn, sq, al, op), _dbmetaProvider);
        }
    }

    // ===================================================================================
    //                                                                        Dream Cruise
    //                                                                        ============
    /**
     * Welcome to the Dream Cruise for condition-bean deep world. <br>
     * This is very specialty so you can get the frontier spirit. Bon voyage!
     * @return The condition-bean for dream cruise, which is linked to main condition-bean.
     */
    public UrlQueueCB dreamCruiseCB() {
        final UrlQueueCB cb = new UrlQueueCB();
        cb.xsetupForDreamCruise(this);
        return cb;
    }

    @Override
    protected ConditionBean xdoCreateDreamCruiseCB() {
        return dreamCruiseCB();
    }

    // [DBFlute-0.9.5.3]
    // ===================================================================================
    //                                                                        Column Query
    //                                                                        ============
    /**
     * Set up column-query. {column1 = column2}
     * <pre>
     * <span style="color: #3F7E5E">// where FOO &lt; BAR</span>
     * cb.<span style="color: #CC4747">columnQuery</span>(<span style="color: #553000">colCB</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     <span style="color: #553000">colCB</span>.specify().<span style="color: #CC4747">columnFoo()</span>; <span style="color: #3F7E5E">// left column</span>
     * }).lessThan(<span style="color: #553000">colCB</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     <span style="color: #553000">colCB</span>.specify().<span style="color: #CC4747">columnBar()</span>; <span style="color: #3F7E5E">// right column</span>
     * }); <span style="color: #3F7E5E">// you can calculate for right column like '}).plus(3);'</span>
     * </pre>
     * @param colCBLambda The callback for specify-query of left column. (NotNull)
     * @return The object for setting up operand and right column. (NotNull)
     */
    public HpColQyOperand<UrlQueueCB> columnQuery(
            final SpecifyQuery<UrlQueueCB> colCBLambda) {
        return xcreateColQyOperand((rightSp, operand) -> {
            return xcolqy(xcreateColumnQueryCB(), xcreateColumnQueryCB(),
                    colCBLambda, rightSp, operand);
        });
    }

    protected UrlQueueCB xcreateColumnQueryCB() {
        final UrlQueueCB cb = new UrlQueueCB();
        cb.xsetupForColumnQuery(this);
        return cb;
    }

    // [DBFlute-0.9.6.3]
    // ===================================================================================
    //                                                                       OrScope Query
    //                                                                       =============
    /**
     * Set up the query for or-scope. <br>
     * (Same-column-and-same-condition-key conditions are allowed in or-scope)
     * <pre>
     * <span style="color: #3F7E5E">// where (FOO = '...' or BAR = '...')</span>
     * cb.<span style="color: #CC4747">orScopeQuery</span>(<span style="color: #553000">orCB</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     <span style="color: #553000">orCB</span>.query().setFoo...
     *     <span style="color: #553000">orCB</span>.query().setBar...
     * });
     * </pre>
     * @param orCBLambda The callback for query of or-condition. (NotNull)
     */
    public void orScopeQuery(final OrQuery<UrlQueueCB> orCBLambda) {
        xorSQ((UrlQueueCB) this, orCBLambda);
    }

    /**
     * Set up the and-part of or-scope. <br>
     * (However nested or-scope query and as-or-split of like-search in and-part are unsupported)
     * <pre>
     * <span style="color: #3F7E5E">// where (FOO = '...' or (BAR = '...' and QUX = '...'))</span>
     * cb.<span style="color: #994747">orScopeQuery</span>(<span style="color: #553000">orCB</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *     <span style="color: #553000">orCB</span>.query().setFoo...
     *     <span style="color: #553000">orCB</span>.<span style="color: #CC4747">orScopeQueryAndPart</span>(<span style="color: #553000">andCB</span> <span style="color: #90226C; font-weight: bold"><span style="font-size: 120%">-</span>&gt;</span> {
     *         <span style="color: #553000">andCB</span>.query().setBar...
     *         <span style="color: #553000">andCB</span>.query().setQux...
     *     });
     * });
     * </pre>
     * @param andCBLambda The callback for query of and-condition. (NotNull)
     */
    public void orScopeQueryAndPart(final AndQuery<UrlQueueCB> andCBLambda) {
        xorSQAP((UrlQueueCB) this, andCBLambda);
    }

    // ===================================================================================
    //                                                                          DisplaySQL
    //                                                                          ==========
    @Override
    protected SqlAnalyzerFactory getSqlAnalyzerFactory() {
        return new ImplementedInvokerAssistant().assistSqlAnalyzerFactory();
    }

    @Override
    protected String getConfiguredLogDatePattern() {
        return DBFluteConfig.getInstance().getLogDatePattern();
    }

    @Override
    protected String getConfiguredLogTimestampPattern() {
        return DBFluteConfig.getInstance().getLogTimestampPattern();
    }

    @Override
    protected String getConfiguredLogTimePattern() {
        return DBFluteConfig.getInstance().getLogTimePattern();
    }

    @Override
    protected BoundDateDisplayTimeZoneProvider getConfiguredLogTimeZoneProvider() {
        return DBFluteConfig.getInstance().getLogTimeZoneProvider();
    }

    // ===================================================================================
    //                                                                       Meta Handling
    //                                                                       =============
    @Override
    public boolean hasUnionQueryOrUnionAllQuery() {
        return query().hasUnionQueryOrUnionAllQuery();
    }

    // ===================================================================================
    //                                                                        Purpose Type
    //                                                                        ============
    @Override
    protected void xprepareSyncQyCall(final ConditionBean mainCB) {
        final UrlQueueCB cb;
        if (mainCB != null) {
            cb = (UrlQueueCB) mainCB;
        } else {
            cb = new UrlQueueCB();
        }
        specify().xsetSyncQyCall(xcreateSpQyCall(() -> true, () -> cb.query()));
    }

    // ===================================================================================
    //                                                                            Internal
    //                                                                            ========
    // very internal (for suppressing warn about 'Not Use Import')
    protected String xgetConditionBeanClassNameInternally() {
        return UrlQueueCB.class.getName();
    }

    protected String xgetConditionQueryClassNameInternally() {
        return UrlQueueCQ.class.getName();
    }

    protected String xgetSubQueryClassNameInternally() {
        return SubQuery.class.getName();
    }

    protected String xgetConditionOptionClassNameInternally() {
        return ConditionOption.class.getName();
    }
}
