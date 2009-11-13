package org.seasar.robot.dbflute.mock;

import java.util.Map;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.OrderByBean;
import org.seasar.robot.dbflute.cbean.PagingBean;
import org.seasar.robot.dbflute.cbean.UnionQuery;
import org.seasar.robot.dbflute.cbean.sqlclause.OrderByClause;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.jdbc.StatementConfig;

public class MockConditionBean implements ConditionBean {

    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap) {
    }

    public void acceptPrimaryKeyMapString(String primaryKeyMapString) {
    }

    public ConditionBean addOrderBy_PK_Asc() {
        return null;
    }

    public ConditionBean addOrderBy_PK_Desc() {
        return null;
    }

    public void configure(StatementConfig statementConfig) {
    }

    public SqlClause getSqlClause() {
        return null;
    }

    public StatementConfig getStatementConfig() {
        return null;
    }

    public String getTableDbName() {
        return null;
    }

    public String getTableSqlName() {
        return null;
    }

    public boolean hasUnionQueryOrUnionAllQuery() {
        return false;
    }

    public boolean isSelectCountIgnoreFetchScope() {
        return false;
    }

    public ConditionQuery localCQ() {
        return null;
    }

    public ConditionBean lockForUpdate() {
        return null;
    }

    public String toDisplaySql() {
        return null;
    }

    public ConditionBean xafterCareSelectCountIgnoreFetchScope() {
        return null;
    }

    public ConditionBean xsetupSelectCountIgnoreFetchScope() {
        return null;
    }

    public boolean canPagingReSelect() {
        return false;
    }

    public void disablePagingReSelect() {
    }

    public PagingBean fetchFirst(int fetchSize) {
        return null;
    }

    public PagingBean fetchPage(int fetchPageNumber) {
        return null;
    }

    public PagingBean fetchScope(int fetchStartIndex, int fetchSize) {
        return null;
    }

    public int getFetchPageNumber() {
        return 0;
    }

    public int getFetchSize() {
        return 0;
    }

    public int getFetchStartIndex() {
        return 0;
    }

    public int getPageEndIndex() {
        return 0;
    }

    public int getPageStartIndex() {
        return 0;
    }

    public boolean isCountLater() {
        return false;
    }

    public boolean isFetchScopeEffective() {
        return false;
    }

    public boolean isPaging() {
        return false;
    }

    public void paging(int pageSize, int pageNumber) {
    }

    public void xsetPaging(boolean paging) {
    }

    public int getFetchNarrowingLoopCount() {
        return 0;
    }

    public int getFetchNarrowingSkipStartIndex() {
        return 0;
    }

    public int getSafetyMaxResultSize() {
        return 0;
    }

    public void ignoreFetchNarrowing() {
    }

    public boolean isFetchNarrowingEffective() {
        return false;
    }

    public boolean isFetchNarrowingLoopCountEffective() {
        return false;
    }

    public boolean isFetchNarrowingSkipStartIndexEffective() {
        return false;
    }

    public void restoreIgnoredFetchNarrowing() {
    }

    public OrderByBean clearOrderBy() {
        return null;
    }

    public String getOrderByClause() {
        return null;
    }

    public OrderByClause getSqlComponentOfOrderByClause() {
        return null;
    }

    public OrderByBean ignoreOrderBy() {
        return null;
    }

    public OrderByBean makeOrderByEffective() {
        return null;
    }

    public void checkSafetyResult(int safetyMaxResultSize) {
    }

    public boolean hasOrderByClause() {
        return false;
    }

    public boolean hasWhereClause() {
        return false;
    }

    public void xregisterFreeParameter(String key, Object value) {
    }

    public void xregisterUnionQuerySynchronizer(UnionQuery<ConditionBean> unionQuerySynchronizer) {
    }
}
