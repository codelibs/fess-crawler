/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import org.seasar.robot.dbflute.cbean.sqlclause.orderby.OrderByClause;

/**
 * SqlClause for SQL Server.
 * @author jflute
 */
public class SqlClauseSqlServer extends AbstractSqlClause {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** String of fetch-first as select-hint. */
    protected String _fetchFirstSelectHint = "";

    /** String of lock as from-hint. */
    protected String _lockFromHint = "";

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param tableDbName The DB name of table. (NotNull)
     **/
    public SqlClauseSqlServer(String tableDbName) {
        super(tableDbName);
    }

    // ===================================================================================
    //                                                                Main Clause Override
    //                                                                ====================
    @Override
    protected boolean isUnionNormalSelectEnclosingRequired() {
        return true;
    }

    // ===================================================================================
    //                                                               Clause Parts Override
    //                                                               =====================
    @Override
    protected void appendSelectHint(StringBuilder sb) {
        if (needsUnionNormalSelectEnclosing()) {
            return; // because clause should be enclosed when union normal select
        }
        super.appendSelectHint(sb);
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
            _fetchFirstSelectHint = " top " + getFetchSize();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void doFetchPage() {
        if (isFetchSizeSupported()) {
            if (isFetchStartIndexSupported()) {
                _fetchFirstSelectHint = " top " + getFetchSize();
            } else {
                _fetchFirstSelectHint = " top " + getPageEndIndex();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void doClearFetchPageClause() {
        _fetchFirstSelectHint = "";
    }

    /**
     * @return Determination.
     */
    public boolean isFetchStartIndexSupported() {
        return false;
    }

    // ===================================================================================
    //                                                                       Lock Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    public SqlClause lockForUpdate() {
        _lockFromHint = " with (updlock)";
        return this;
    }

    // ===================================================================================
    //                                                                       Hint Override
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    protected String createSelectHint() {
        return _fetchFirstSelectHint;
    }

    /**
     * {@inheritDoc}
     */
    protected String createFromBaseTableHint() {
        return _lockFromHint;
    }

    /**
     * {@inheritDoc}
     */
    protected String createFromHint() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    protected String createSqlSuffix() {
        return "";
    }
}
