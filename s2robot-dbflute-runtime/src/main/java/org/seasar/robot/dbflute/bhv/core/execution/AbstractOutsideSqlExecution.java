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
package org.seasar.robot.dbflute.bhv.core.execution;

import java.util.Map;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlFilter;
import org.seasar.robot.dbflute.util.Srl;

/**
 * The SQL execution by outside-SQL. <br />
 * This has filter options.
 * @author jflute
 */
public abstract class AbstractOutsideSqlExecution extends AbstractFixedSqlExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _removeBlockComment;
    protected boolean _removeLineComment;
    protected boolean _formatSql;
    protected OutsideSqlFilter _outsideSqlFilter;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param dataSource The data source for a database connection. (NotNull)
     * @param statementFactory The factory of statement. (NotNull)
     * @param argNameTypeMap The map of names and types for arguments. (NotNull)
     * @param twoWaySql The SQL string as 2Way-SQL. (NotNull)
     */
    public AbstractOutsideSqlExecution(DataSource dataSource, StatementFactory statementFactory,
            Map<String, Class<?>> argNameTypeMap, String twoWaySql) {
        super(dataSource, statementFactory, argNameTypeMap, twoWaySql);
    }

    // ===================================================================================
    //                                                                              Filter
    //                                                                              ======
    @Override
    protected String filterExecutedSql(String sql) {
        if (_outsideSqlFilter != null) {
            sql = _outsideSqlFilter.filterExecution(sql, getOutsideSqlExecutionFilterType());
        }
        if (_removeBlockComment) {
            sql = Srl.removeBlockComment(sql);
        }
        if (_removeLineComment) {
            sql = Srl.removeLineComment(sql);
        }
        if (_formatSql) {
            sql = Srl.removeEmptyLine(sql);
        }
        return sql;
    }

    protected abstract OutsideSqlFilter.ExecutionFilterType getOutsideSqlExecutionFilterType();

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isRemoveBlockComment() {
        return _removeBlockComment;
    }

    public void setRemoveBlockComment(boolean removeBlockComment) {
        this._removeBlockComment = removeBlockComment;
    }

    public boolean isRemoveLineComment() {
        return _removeLineComment;
    }

    public void setRemoveLineComment(boolean removeLineComment) {
        this._removeLineComment = removeLineComment;
    }

    public boolean isRemoveEmptyLine() {
        return _formatSql;
    }

    public void setFormatSql(boolean formatSql) {
        this._formatSql = formatSql;
    }

    public OutsideSqlFilter getOutsideSqlFilter() {
        return _outsideSqlFilter;
    }

    public void setOutsideSqlFilter(OutsideSqlFilter outsideSqlFilter) {
        this._outsideSqlFilter = outsideSqlFilter;
    }
}
