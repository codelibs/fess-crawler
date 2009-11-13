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
package org.seasar.robot.dbflute.cbean.sqlclause;

/**
 * SqlClause for DB2.
 * @author jflute
 */
public class SqlClauseDb2 extends AbstractSqlClause {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** String of fetch-first as sql-suffix. */
    protected String _fetchFirstSqlSuffix = "";

    /** String of lock as from-hint. */
    protected String _lockSqlSuffix = "";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableName Table name. (NotNull)
     **/
    public SqlClauseDb2(String tableName) {
        super(tableName);
    }

    // ===================================================================================
    //                                                                    OrderBy Override
    //                                                                    ================
	@Override
    protected OrderByClause.OrderByNullsSetupper createOrderByNullsSetupper() {
	    return createOrderByNullsSetupperByCaseWhen();
	}

    // ===================================================================================
    //                                                                 FetchScope Override
    //                                                                 ===================
    /**
     * {@inheritDoc}
     */
    protected void doFetchFirst() {
        if (isFetchSizeSupported()) {
            _fetchFirstSqlSuffix = " fetch first " + getFetchSize() + " rows only";
        }
    }

    /**
     * {@inheritDoc} {Unsupported!}
     */
    protected void doFetchPage() {
        if (isFetchSizeSupported()) {
            if (isFetchStartIndexSupported()) {
                _fetchFirstSqlSuffix = " fetch first " + getFetchSize() + " rows only";
            } else {
                _fetchFirstSqlSuffix = " fetch first " + getPageEndIndex() + " rows only";
            }
        }
    }

    /**
     * {@inheritDoc} {Unsupported!}
     */
    protected void doClearFetchPageClause() {
        _fetchFirstSqlSuffix = "";
    }

    /**
     * The override.
     * @return Determination.
     */
    public boolean isFetchStartIndexSupported() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @return this. (NotNull)
     */
    public SqlClause lockForUpdate() {
        _lockSqlSuffix = " for update with RS";
        return this;
    }

    // ===================================================================================
    //                                                                       Hint Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     * @return Select-hint. (NotNull)
     */
    protected String createSelectHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     * @return From-base-table-hint. {select * from table [from-base-table-hint] where ...} (NotNull)
     */
    protected String createFromBaseTableHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     * @return From-hint. (NotNull)
     */
    protected String createFromHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     * @return Sql-suffix. (NotNull)
     */
    protected String createSqlSuffix() {
        return _fetchFirstSqlSuffix + _lockSqlSuffix;
    }

    // [DBFlute-0.7.9]
    // ===================================================================================
    //                                                                 Database Dependency
    //                                                                 ===================
    public void lockWithRR() {
        _lockSqlSuffix = " with RR";
    }

    public void lockWithRS() {
        _lockSqlSuffix = " with RS";
    }

    public void lockWithCS() {
        _lockSqlSuffix = " with CS";
    }

    public void lockWithUR() {
        _lockSqlSuffix = " with UR";
    }
}
