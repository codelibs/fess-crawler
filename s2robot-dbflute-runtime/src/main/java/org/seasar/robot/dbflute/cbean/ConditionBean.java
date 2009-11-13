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
package org.seasar.robot.dbflute.cbean;

import java.util.Map;

import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.jdbc.StatementConfig;

/**
 * The interface of condition-bean.
 * @author jflute
 */
public interface ConditionBean extends PagingBean {

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    /**
     * Get table DB-name.
     * @return Table DB-name. (NotNull)
     */
    public String getTableDbName();

    /**
     * Get table SQL-name.
     * @return Table SQL-name. (NotNull)
     */
    public String getTableSqlName();

    // ===================================================================================
    //                                                                           SqlClause
    //                                                                           =========
    /**
     * Get SQL-clause instance.
     * @return SQL-clause. (NotNull)
     */
    public SqlClause getSqlClause();

    // ===================================================================================
    //                                                                      PrimaryKey Map
    //                                                                      ==============
    /**
     * Accept primary-key map-string.
     * @param primaryKeyMap Primary-key map. (NotNull and NotEmpty)
     */
    public void acceptPrimaryKeyMap(Map<String, ? extends Object> primaryKeyMap);

    /**
     * Accept primary-key map-string. Delimiter is at-mark and semicolon.
     * @param primaryKeyMapString Primary-key map. (NotNull and NotEmpty)
     */
    public void acceptPrimaryKeyMapString(String primaryKeyMapString);

    // ===================================================================================
    //                                                                     OrderBy Setting
    //                                                                     ===============
    /**
     * Add order-by PrimaryKey asc. {order by primaryKey1 asc, primaryKey2 asc...}
     * @return this. (NotNull)
     */
    public ConditionBean addOrderBy_PK_Asc();

    /**
     * Add order-by PrimaryKey desc. {order by primaryKey1 desc, primaryKey2 desc...}
     * @return this. (NotNull)
     */
    public ConditionBean addOrderBy_PK_Desc();

    // ===================================================================================
    //                                                                               Query
    //                                                                               =====
    /**
     * Get the conditionQuery of the local table as interface.
     * @return The conditionQuery of the local table as interface. (NotNull)
     */
    public ConditionQuery localCQ();

    // ===================================================================================
    //                                                                        Lock Setting
    //                                                                        ============
    /**
     * Lock for update.
     * <p>
     * If you invoke this, your SQL lock target records for update.
     * It depends whether this method supports this on the database type.
     * </p>
     * @return this. (NotNull)
     */
    public ConditionBean lockForUpdate();

    // ===================================================================================
    //                                                                        Select Count
    //                                                                        ============
    /**
     * Set up various things for select-count-ignore-fetch-scope. {Internal}
     * This method is for INTERNAL. Don't invoke this!
     * @return this. (NotNull)
     */
    public ConditionBean xsetupSelectCountIgnoreFetchScope();

    /**
     * Do after-care for select-count-ignore-fetch-scope. {Internal}
     * This method is for INTERNAL. Don't invoke this!
     * @return this. (NotNull)
     */
    public ConditionBean xafterCareSelectCountIgnoreFetchScope();

    /**
     * Is set up various things for select-count-ignore-fetch-scope? {Internal}
     * This method is for INTERNAL. Don't invoke this!
     * @return Determination.
     */
    public boolean isSelectCountIgnoreFetchScope();

    // ===================================================================================
    //                                                                    Statement Config
    //                                                                    ================
    /**
     * @param statementConfig The config of statement. (Nullable)
     */
    public void configure(StatementConfig statementConfig);

    /**
     * @return The config of statement. (Nullable)
     */
    public StatementConfig getStatementConfig();

    // ===================================================================================
    //                                                                         Display SQL
    //                                                                         ===========
    /**
     * Convert this conditionBean to SQL for display.
     * @return SQL for display. (NotNull and NotEmpty)
     */
    public String toDisplaySql();

    // ===================================================================================
    //                                                          Basic Status Determination
    //                                                          ==========================
    /**
     * Does it have where clauses? <br />
     * In-line where clause is NOT contained.
     * @return Determination.
     */
    public boolean hasWhereClause();

    /**
     * Does it have order-by clauses? <br />
     * Whether effective or not has no influence.
     * @return Determination.
     */
    public boolean hasOrderByClause();

    /**
     * Has union query or union all query?
     * @return Determination.
     */
    public boolean hasUnionQueryOrUnionAllQuery();

    // ===================================================================================
    //                                                                      Free Parameter
    //                                                                      ==============
    public void xregisterFreeParameter(String key, Object value);

    // ===================================================================================
    //                                                                  Query Synchronizer
    //                                                                  ==================
    public void xregisterUnionQuerySynchronizer(UnionQuery<ConditionBean> unionQuerySynchronizer);
}
