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
package org.seasar.robot.db.allcommon;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.sqlclause.*;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;

/**
 * The creator of SQL clause.
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedSqlClauseCreator implements SqlClauseCreator {

	/**
	 * Create SQL clause. {for condition-bean}
	 * @param cb Condition-bean. (NotNull) 
	 * @return SQL clause. (NotNull)
	 */
    public SqlClause createSqlClause(ConditionBean cb) {
        final String tableSqlName = cb.getTableSqlName();
		final SqlClause sqlClause = createSqlClause(tableSqlName);
        return sqlClause;
    }

	/**
	 * Create SQL clause.
	 * @param tableDbName The DB name of table. (NotNull) 
	 * @return SQL clause. (NotNull)
	 */
    public SqlClause createSqlClause(String tableDbName) {
        DBMetaProvider dbmetaProvider = new DBMetaInstanceHandler();
        SqlClause sqlClause;
        if (isCurrentDBDef(DBDef.MySQL)) {
            sqlClause = new SqlClauseMySql(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.PostgreSQL)) {
            sqlClause = new SqlClausePostgreSql(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.Oracle)) {
            sqlClause = new SqlClauseOracle(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.DB2)) {
            sqlClause = new SqlClauseDb2(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.SQLServer)) {
            sqlClause = new SqlClauseSqlServer(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.FireBird)) {
            sqlClause = new SqlClauseFirebird(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.H2)) {
            sqlClause = new SqlClauseH2(tableDbName).provider(dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.Derby)) {
            sqlClause = new SqlClauseDerby(tableDbName).provider(dbmetaProvider);
        } else {
            sqlClause = new SqlClauseMySql(tableDbName).provider(dbmetaProvider);
        }
        if (isDisableSelectIndex()) {
            sqlClause.disableSelectIndex();
        }
        return sqlClause;
    }

    protected boolean isCurrentDBDef(DBDef currentDBDef) {
	    return DBCurrent.getInstance().isCurrentDBDef(currentDBDef);
    }

    protected boolean isDisableSelectIndex() {
	    return DBFluteConfig.getInstance().isDisableSelectIndex();
    }
}
