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
package org.seasar.robot.dbflute.resource;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.AccessContext;
import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.helper.StringSet;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.factory.SqlAnalyzerFactory;

/**
 * The context of resource.
 * @author jflute
 */
public class ResourceContext {

    // ===================================================================================
    //                                                                        Thread Local
    //                                                                        ============
    /** The thread-local for this. */
    private static final ThreadLocal<ResourceContext> threadLocal = new ThreadLocal<ResourceContext>();

    /**
     * Get the context of resource by the key.
     * @return The context of resource. (Nullable)
     */
    public static ResourceContext getResourceContextOnThread() {
        return threadLocal.get();
    }

    /**
     * Set the context of resource.
     * @param resourceCountext The context of resource. (NotNull)
     */
    public static void setResourceContextOnThread(ResourceContext resourceCountext) {
        threadLocal.set(resourceCountext);
    }

    /**
     * Is existing the context of resource on thread?
     * @return Determination.
     */
    public static boolean isExistResourceContextOnThread() {
        return (threadLocal.get() != null);
    }

    /**
     * Clear the context of resource on thread.
     */
    public static void clearResourceContextOnThread() {
        threadLocal.set(null);
    }

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    /**
     * @return The current database definition. (NotNull)
     */
    public static DBDef currentDBDef() {
        if (!isExistResourceContextOnThread()) {
            return DBDef.Unknown;
        }
        DBDef currentDBDef = getResourceContextOnThread().getCurrentDBDef();
        if (currentDBDef == null) {
            return DBDef.Unknown;
        }
        return currentDBDef;
    }

    public static boolean isCurrentDBDef(DBDef targetDBDef) {
        return currentDBDef().equals(targetDBDef);
    }

    /**
     * @return The provider of DB meta. (NotNull)
     */
    public static DBMetaProvider dbmetaProvider() {
        if (!isExistResourceContextOnThread()) {
            String msg = "The resource context should exist!";
            throw new IllegalStateException(msg);
        }
        DBMetaProvider provider = getResourceContextOnThread().getDBMetaProvider();
        if (provider == null) {
            String msg = "The provider of DB meta should exist!";
            throw new IllegalStateException(msg);
        }
        return provider;
    }

    /**
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (Nullable)
     */
    public static DBMeta provideDBMeta(String tableFlexibleName) {
        if (!isExistResourceContextOnThread()) {
            return null;
        }
        DBMetaProvider provider = getResourceContextOnThread().getDBMetaProvider();
        if (provider == null) {
            return null;
        }
        return provider.provideDBMeta(tableFlexibleName);
    }

    /**
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (NotNull)
     */
    public static DBMeta provideDBMetaChecked(String tableFlexibleName) {
        if (!isExistResourceContextOnThread()) {
            String msg = "The resource context should exist: " + tableFlexibleName;
            throw new IllegalStateException(msg);
        }
        DBMetaProvider provider = getResourceContextOnThread().getDBMetaProvider();
        if (provider == null) {
            String msg = "The provider of DB meta should exist: " + tableFlexibleName;
            throw new IllegalStateException(msg);
        }
        return provider.provideDBMetaChecked(tableFlexibleName);
    }

    public static SqlAnalyzer createSqlAnalyzer(String sql, boolean blockNullParameter) {
        if (!isExistResourceContextOnThread()) {
            String msg = "The resource context should exist!";
            throw new IllegalStateException(msg);
        }
        SqlAnalyzerFactory factory = getResourceContextOnThread().getSqlAnalyzerFactory();
        if (factory == null) {
            String msg = "The provider of SQL analyzer should exist!";
            throw new IllegalStateException(msg);
        }
        return factory.create(sql, blockNullParameter);
    }

    /**
     * Is the SQLException from unique constraint? {Use both SQLState and ErrorCode}
     * @param sqlState SQLState of the SQLException. (Nullable)
     * @param errorCode ErrorCode of the SQLException. (Nullable)
     * @return Is the SQLException from unique constraint?
     */
    public static boolean isUniqueConstraintException(String sqlState, Integer errorCode) {
        if (!isExistResourceContextOnThread()) {
            return false;
        }
        SqlClauseCreator sqlClauseCreator = getResourceContextOnThread().getSqlClauseCreator();
        if (sqlClauseCreator == null) {
            return false;
        }
        return currentDBDef().dbway().isUniqueConstraintException(sqlState, errorCode);
    }

    public static String getOutsideSqlPackage() {
        ResourceParameter resourceParameter = resourceParameter();
        if (resourceParameter == null) {
            return null;
        }
        return resourceParameter.getOutsideSqlPackage();
    }

    public static String getLogDateFormat() {
        ResourceParameter resourceParameter = resourceParameter();
        if (resourceParameter == null) {
            return null;
        }
        return resourceParameter.getLogDateFormat();
    }

    public static String getLogTimestampFormat() {
        ResourceParameter resourceParameter = resourceParameter();
        if (resourceParameter == null) {
            return null;
        }
        return resourceParameter.getLogTimestampFormat();
    }

    protected static ResourceParameter resourceParameter() {
        if (!isExistResourceContextOnThread()) {
            return null;
        }
        ResourceParameter resourceParameter = getResourceContextOnThread().getResourceParameter();
        if (resourceParameter == null) {
            return null;
        }
        return resourceParameter;
    }

    // -----------------------------------------------------
    //                                           Access Date
    //                                           -----------
    public static Date getAccessDate() {
        return AccessContext.getAccessDateOnThread();
    }

    public static Timestamp getAccessTimestamp() {
        return AccessContext.getAccessTimestampOnThread();
    }

    // -----------------------------------------------------
    //                                          Select Index
    //                                          ------------
    public static Set<String> createSelectColumnNames(ResultSet rs) throws SQLException {
        final ResultSetMetaData rsmd = rs.getMetaData();
        final int count = rsmd.getColumnCount();
        final Set<String> columnNames = StringSet.createAsCaseInsensitive();
        for (int i = 0; i < count; ++i) {
            final String columnName = rsmd.getColumnLabel(i + 1);
            final int pos = columnName.lastIndexOf('.'); // for SQLite
            if (-1 < pos) {
                columnNames.add(columnName.substring(pos + 1));
            } else {
                columnNames.add(columnName);
            }
        }
        Map<String, Integer> selectIndexMap = getSelectIndexMap();
        if (selectIndexMap == null) {
            return columnNames;
        }
        Map<String, String> selectIndexReverseMap = getSelectIndexReverseMap();
        final Set<String> realColumnNames = StringSet.createAsCaseInsensitive();
        for (String columnName : columnNames) {
            String uniqueName = selectIndexReverseMap.get(columnName);
            if (uniqueName != null) {
                realColumnNames.add(uniqueName);
            } else {
                realColumnNames.add(columnName);
            }
        }
        return realColumnNames;
    }

    public static Map<String, Integer> getSelectIndexMap() {
        if (!ConditionBeanContext.isExistConditionBeanOnThread()) {
            return null;
        }
        ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
        if (cb == null) {
            return null;
        }
        return cb.getSqlClause().getSelectIndexMap();
    }

    protected static Map<String, String> getSelectIndexReverseMap() {
        if (!ConditionBeanContext.isExistConditionBeanOnThread()) {
            return null;
        }
        ConditionBean cb = ConditionBeanContext.getConditionBeanOnThread();
        if (cb == null) {
            return null;
        }
        return cb.getSqlClause().getSelectIndexReverseMap();
    }

    public static Object getValue(ResultSet rs, String columnName, ValueType valueType,
            Map<String, Integer> selectIndexMap) throws SQLException { // No check!
        Integer selectIndex = selectIndexMap.get(columnName);
        if (selectIndex != null) {
            return valueType.getValue(rs, selectIndex);
        } else {
            return valueType.getValue(rs, columnName);
        }
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DBDef _currentDBDef;
    protected DBMetaProvider _dbmetaProvider;
    protected SqlClauseCreator _sqlClauseCreator;
    protected ResourceParameter _resourceParameter;
    protected SqlAnalyzerFactory _sqlAnalyzerFactory;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DBDef getCurrentDBDef() {
        return _currentDBDef;
    }

    public void setCurrentDBDef(DBDef currentDBDef) {
        _currentDBDef = currentDBDef;
    }

    public DBMetaProvider getDBMetaProvider() {
        return _dbmetaProvider;
    }

    public void setDBMetaProvider(DBMetaProvider dbmetaProvider) {
        _dbmetaProvider = dbmetaProvider;
    }

    public SqlClauseCreator getSqlClauseCreator() {
        return _sqlClauseCreator;
    }

    public void setSqlClauseCreator(SqlClauseCreator sqlClauseCreator) {
        _sqlClauseCreator = sqlClauseCreator;
    }

    public ResourceParameter getResourceParameter() {
        return _resourceParameter;
    }

    public void setResourceParameter(ResourceParameter resourceParameter) {
        _resourceParameter = resourceParameter;
    }

    public SqlAnalyzerFactory getSqlAnalyzerFactory() {
        return _sqlAnalyzerFactory;
    }

    public void setSqlAnalyzerFactory(SqlAnalyzerFactory sqlAnalyzerFactory) {
        _sqlAnalyzerFactory = sqlAnalyzerFactory;
    }
}
