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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.QLog;
import org.seasar.dbflute.XLog;
import org.seasar.dbflute.jdbc.DataSourceHandler;
import org.seasar.dbflute.jdbc.StatementConfig;
import org.seasar.dbflute.jdbc.ValueType;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * @author DBFlute(AutoGenerator)
 */
public class DBFluteConfig {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DBFluteConfig.class);

    /** Singleton instance. */
    private static final DBFluteConfig _instance = new DBFluteConfig();

    /** The default package of outside SQL. */
    private static final String DEFAULT_OUTSIDE_SQL_PACKAGE = null;

    /** The default value of whether it uses SQL Log Registry. The default value is false. */
    private static final boolean DEFAULT_USE_SQL_LOG_REGISTRY = false;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                         Configuration
    //                                         -------------
    protected StatementConfig _defaultStatementConfig;

    protected boolean _queryLogLevelInfo;

    protected boolean _executeStatusLogLevelInfo;

    protected String _logDateFormat;

    protected String _logTimestampFormat;

    protected DataSourceHandler _dataSourceHandler;

    protected String _outsideSqlPackage = DEFAULT_OUTSIDE_SQL_PACKAGE;

    protected boolean _useSqlLogRegistry = DEFAULT_USE_SQL_LOG_REGISTRY;

    protected boolean _disableSelectIndex;

    protected boolean _internalDebug;

    // -----------------------------------------------------
    //                                   Database Dependency
    //                                   -------------------

    // -----------------------------------------------------
    //                                                  Lock
    //                                                  ----
    protected boolean _locked = true;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     */
    private DBFluteConfig() {
    }

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    /**
     * Get singleton instance.
     * @return Singleton instance. (NotNull)
     */
    public static DBFluteConfig getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                            Default Statement Config
    //                                                            ========================
    public StatementConfig getDefaultStatementConfig() {
        return _defaultStatementConfig;
    }

    public void setDefaultStatementConfig(StatementConfig defaultStatementConfig) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting defaultStatementConfig: "
                    + defaultStatementConfig);
        }
        _defaultStatementConfig = defaultStatementConfig;
    }

    // ===================================================================================
    //                                                                Query Log Level Info
    //                                                                ====================
    public void setQueryLogLevelInfo(boolean queryLogLevelInfo) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting queryLogLevelInfo: " + queryLogLevelInfo);
        }
        QLog.unlock();
        QLog.setQueryLogLevelInfo(queryLogLevelInfo);
        QLog.lock();
    }

    // ===================================================================================
    //                                                       Execute Status Log Level Info
    //                                                       =============================
    public void setExecuteStatusLogLevelInfo(boolean executeStatusLogLevelInfo) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting executeStatusLogLevelInfo: "
                    + executeStatusLogLevelInfo);
        }
        XLog.unlock();
        XLog.setExecuteStatusLogLevelInfo(executeStatusLogLevelInfo);
        XLog.lock();
    }

    // ===================================================================================
    //                                                                          Log Format
    //                                                                          ==========
    public String getLogDateFormat() {
        return _logDateFormat;
    }

    public void setLogDateFormat(String logDateFormat) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting logDateFormat: " + logDateFormat);
        }
        _logDateFormat = logDateFormat;
    }

    public String getLogTimestampFormat() {
        return _logTimestampFormat;
    }

    public void setLogTimestampFormat(String logTimestampFormat) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting logTimestampFormat: " + logTimestampFormat);
        }
        _logTimestampFormat = logTimestampFormat;
    }

    // [DBFlute-0.9.0]
    // ===================================================================================
    //                                                                  DataSource Handler
    //                                                                  ==================
    /**
     * @return The handler of data source. (Nullable)
     */
    public DataSourceHandler getDataSourceHandler() {
        return _dataSourceHandler;
    }

    /**
     * @param dataSourceHandler The handler of data source. (Nullable)
     */
    public void setDataSourceHandler(DataSourceHandler dataSourceHandler) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting dataSourceHandler: " + dataSourceHandler);
        }
        _dataSourceHandler = dataSourceHandler;
    }

    // ===================================================================================
    //                                                                  OutsideSql Package
    //                                                                  ==================
    /**
     * @return The package of outside SQL. (Nullable)
     */
    public String getOutsideSqlPackage() {
        return _outsideSqlPackage;
    }

    /**
     * @param outsideSqlPackage The package of outside SQL. (Nullable)
     */
    public void setOutsideSqlPackage(String outsideSqlPackage) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting outsideSqlPackage: " + outsideSqlPackage);
        }
        _outsideSqlPackage = outsideSqlPackage;
    }

    // [DBFlute-0.8.2]
    // ===================================================================================
    //                                                                    SQL Log Registry
    //                                                                    ================
    public boolean isUseSqlLogRegistry() {
        return _useSqlLogRegistry;
    }

    public void setUseSqlLogRegistry(boolean useSqlLogRegistry) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting useSqlLogRegistry: " + useSqlLogRegistry);
        }
        _useSqlLogRegistry = useSqlLogRegistry;
    }

    // [DBFlute-0.9.0]
    // ===================================================================================
    //                                                                        Select Index
    //                                                                        ============
    public boolean isDisableSelectIndex() {
        return _disableSelectIndex;
    }

    public void setDisableSelectIndex(boolean disableSelectIndex) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting disableSelectIndex: " + disableSelectIndex);
        }
        _disableSelectIndex = disableSelectIndex;
    }

    // ===================================================================================
    //                                                                      Internal Debug
    //                                                                      ==============
    public boolean isInternalDebug() {
        return _internalDebug;
    }

    public void setInternalDebug(boolean internalDebug) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting internalDebug: " + internalDebug);
        }
        _internalDebug = internalDebug;
    }

    // ===================================================================================
    //                                                                          Value Type
    //                                                                          ==========
    public void registerValueType(Class<?> keyType, ValueType valueType) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Registering valueType: keyType=" + keyType
                    + " valueType=" + valueType);
        }
        TnValueTypes.registerValueType(keyType, valueType);
    }

    public void removeValueType(Class<?> keyType) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Removing valueType: keyType=" + keyType);
        }
        TnValueTypes.unregisterValueType(keyType);
    }

    // ===================================================================================
    //                                                                 Database Dependency
    //                                                                 ===================

    // ===================================================================================
    //                                                                  Configuration Lock
    //                                                                  ==================
    public boolean isLocked() {
        return _locked;
    }

    public void lock() {
        if (_log.isInfoEnabled()) {
            _log.info("...Locking the configuration of DBFlute!");
        }
        _locked = true;
    }

    public void unlock() {
        if (_log.isInfoEnabled()) {
            _log.info("...Unlocking the configuration of DBFlute!");
        }
        _locked = false;
    }

    protected void assertNotLocked() {
        if (!isLocked()) {
            return;
        }
        String msg = "The configuration of DBFlute is locked! Don't access at this timing!";
        throw new IllegalStateException(msg);
    }

    // ===================================================================================
    //                                                                 Configuration Clear
    //                                                                 ===================
    public void clear() { // the only properties that update OK while executing
        _defaultStatementConfig = null;
        _queryLogLevelInfo = false;
        _executeStatusLogLevelInfo = false;
        _logDateFormat = null;
        _logTimestampFormat = null;
        _dataSourceHandler = null;
        _outsideSqlPackage = DEFAULT_OUTSIDE_SQL_PACKAGE;
        _useSqlLogRegistry = DEFAULT_USE_SQL_LOG_REGISTRY;
        _disableSelectIndex = false;
        _internalDebug = false;
    }
}
