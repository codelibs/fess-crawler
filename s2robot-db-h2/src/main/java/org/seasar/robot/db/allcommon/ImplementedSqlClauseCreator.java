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
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseDb2;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseDefault;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseDerby;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseFirebird;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseH2;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseMsAccess;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseMySql;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseOracle;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClausePostgreSql;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseSqlServer;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseSqlite;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseSybase;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;

/**
 * The creator of SQL clause.
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedSqlClauseCreator implements SqlClauseCreator {

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    /**
     * Create SQL clause. {for condition-bean}
     * @param cb Condition-bean. (NotNull) 
     * @return SQL clause. (NotNull)
     */
    public SqlClause createSqlClause(ConditionBean cb) {
        String tableDbName = cb.getTableDbName();
        SqlClause sqlClause = createSqlClause(tableDbName);
        return sqlClause;
    }

    /**
     * Create SQL clause.
     * @param tableDbName The DB name of table. (NotNull) 
     * @return SQL clause. (NotNull)
     */
    public SqlClause createSqlClause(String tableDbName) {
        DBMetaProvider dbmetaProvider = DBMetaInstanceHandler.getProvider();
        SqlClause sqlClause = doCreateSqlClause(tableDbName, dbmetaProvider);
        setupSqlClauseOption(sqlClause);
        return sqlClause;
    }

    // ===================================================================================
    //                                                                            Creation
    //                                                                            ========
    protected SqlClause doCreateSqlClause(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        SqlClause sqlClause; // dynamic resolution but no perfect (almost static)
        if (isCurrentDBDef(DBDef.MySQL)) {
            sqlClause = createSqlClauseMySql(tableDbName, dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.PostgreSQL)) {
            sqlClause = createSqlClausePostgreSql(tableDbName, dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.Oracle)) {
            sqlClause = createSqlClauseOracle(tableDbName, dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.DB2)) {
            sqlClause = createSqlClauseDb2(tableDbName, dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.SQLServer)) {
            sqlClause = createSqlClauseSqlServer(tableDbName, dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.H2)) {
            sqlClause = createSqlClauseH2(tableDbName, dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.Derby)) {
            sqlClause = createSqlClauseDerby(tableDbName, dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.SQLite)) {
            sqlClause = createSqlClauseSqlite(tableDbName, dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.MSAccess)) {
            sqlClause = createSqlClauseMsAccess(tableDbName, dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.FireBird)) {
            sqlClause = createSqlClauseFirebird(tableDbName, dbmetaProvider);
        } else if (isCurrentDBDef(DBDef.Sybase)) {
            sqlClause = createSqlClauseSybase(tableDbName, dbmetaProvider);
        } else {
            // as the database when generating
            sqlClause = createSqlClauseH2(tableDbName, dbmetaProvider);
        }
        return sqlClause;
    }

    protected SqlClause createSqlClauseMySql(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseMySql(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClausePostgreSql(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClausePostgreSql(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClauseOracle(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseOracle(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClauseDb2(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseDb2(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClauseSqlServer(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseSqlServer(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClauseH2(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseH2(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClauseDerby(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseDerby(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClauseSqlite(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseSqlite(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClauseMsAccess(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseMsAccess(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClauseFirebird(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseFirebird(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClauseSybase(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseSybase(tableDbName).provider(dbmetaProvider);
    }

    protected SqlClause createSqlClauseDefault(String tableDbName,
            DBMetaProvider dbmetaProvider) {
        return new SqlClauseDefault(tableDbName).provider(dbmetaProvider);
    }

    // ===================================================================================
    //                                                                              Option
    //                                                                              ======
    protected void setupSqlClauseOption(SqlClause sqlClause) {
        if (isDisableSelectIndex()) {
            sqlClause.disableSelectIndex();
        }
        if (isEmptyStringQueryAllowed()) {
            sqlClause.allowEmptyStringQuery();
        }
        if (isInvalidQueryChecked()) {
            sqlClause.checkInvalidQuery();
        }
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    protected boolean isCurrentDBDef(DBDef currentDBDef) {
        return DBCurrent.getInstance().isCurrentDBDef(currentDBDef);
    }

    protected boolean isDisableSelectIndex() {
        return DBFluteConfig.getInstance().isDisableSelectIndex();
    }

    protected boolean isEmptyStringQueryAllowed() {
        return DBFluteConfig.getInstance().isEmptyStringQueryAllowed();
    }

    protected boolean isInvalidQueryChecked() {
        return DBFluteConfig.getInstance().isInvalidQueryChecked();
    }
}
