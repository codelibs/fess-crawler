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

import javax.sql.DataSource;

import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;
import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.robot.dbflute.bhv.core.InvokerAssistant;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.jdbc.DataSourceHandler;
import org.seasar.robot.dbflute.jdbc.HandlingDataSourceWrapper;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.resource.ResourceParameter;
import org.seasar.robot.dbflute.s2dao.extension.TnBeanMetaDataFactoryExtension;
import org.seasar.robot.dbflute.s2dao.jdbc.TnStatementFactoryImpl;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypeFactory;
import org.seasar.robot.dbflute.s2dao.valuetype.impl.TnValueTypeFactoryImpl;
import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.factory.SqlAnalyzerFactory;

/**
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedInvokerAssistant implements InvokerAssistant {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                          DI component
    //                                          ------------
    protected BehaviorCommandInvoker _behaviorCommandInvoker;

    protected DataSource _dataSource;

    // -----------------------------------------------------
    //                                        Lazy component
    //                                        --------------
    protected volatile DBMetaProvider _dbmetaProvider;

    protected volatile SqlClauseCreator _sqlClauseCreator;

    protected volatile StatementFactory _statementFactory;

    protected volatile TnBeanMetaDataFactory _beanMetaDataFactory;

    protected volatile TnValueTypeFactory _valueTypeFactory;

    // -----------------------------------------------------
    //                                       Disposable Flag
    //                                       ---------------
    protected volatile boolean _disposable;

    // ===================================================================================
    //                                                                 Assistant Main Work
    //                                                                 ===================
    // -----------------------------------------------------
    //                                         Current DBDef
    //                                         -------------
    public DBDef assistCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    // -----------------------------------------------------
    //                                           Data Source
    //                                           -----------
    public DataSource assistDataSource() { // DI component
        DataSourceHandler dataSourceHandler = DBFluteConfig.getInstance()
                .getDataSourceHandler();
        if (dataSourceHandler != null) {
            return new HandlingDataSourceWrapper(_dataSource, dataSourceHandler);
        }
        return _dataSource;
    }

    // -----------------------------------------------------
    //                                       DBMeta Provider
    //                                       ---------------
    public DBMetaProvider assistDBMetaProvider() { // Lazy component
        if (_dbmetaProvider != null) {
            return _dbmetaProvider;
        }
        synchronized (this) {
            if (_dbmetaProvider != null) {
                return _dbmetaProvider;
            }
            _dbmetaProvider = createDBMetaProvider();
        }
        return _dbmetaProvider;
    }

    protected DBMetaProvider createDBMetaProvider() {
        return new DBMetaInstanceHandler();
    }

    // -----------------------------------------------------
    //                                    SQL Clause Creator
    //                                    ------------------
    public SqlClauseCreator assistSqlClauseCreator() { // Lazy component
        if (_sqlClauseCreator != null) {
            return _sqlClauseCreator;
        }
        synchronized (this) {
            if (_sqlClauseCreator != null) {
                return _sqlClauseCreator;
            }
            _sqlClauseCreator = createSqlClauseCreator();
        }
        return _sqlClauseCreator;
    }

    protected SqlClauseCreator createSqlClauseCreator() {
        return new ImplementedSqlClauseCreator();
    }

    // -----------------------------------------------------
    //                                     Statement Factory
    //                                     -----------------
    public StatementFactory assistStatementFactory() { // Lazy component
        if (_statementFactory != null) {
            return _statementFactory;
        }
        synchronized (this) {
            if (_statementFactory != null) {
                return _statementFactory;
            }
            _statementFactory = createStatementFactory();
        }
        return _statementFactory;
    }

    protected StatementFactory createStatementFactory() {
        final TnStatementFactoryImpl factory = new TnStatementFactoryImpl();
        factory.setDefaultStatementConfig(assistDefaultStatementConfig());
        factory.setInternalDebug(DBFluteConfig.getInstance().isInternalDebug());
        return factory;
    }

    // -----------------------------------------------------
    //                                Bean Meta Data Factory
    //                                ----------------------
    public TnBeanMetaDataFactory assistBeanMetaDataFactory() { // Lazy component
        if (_beanMetaDataFactory != null) {
            return _beanMetaDataFactory;
        }
        synchronized (this) {
            if (_beanMetaDataFactory != null) {
                return _beanMetaDataFactory;
            }
            _beanMetaDataFactory = createBeanMetaDataFactory();
        }
        return _beanMetaDataFactory;
    }

    protected TnBeanMetaDataFactory createBeanMetaDataFactory() {
        final TnBeanMetaDataFactoryExtension factory = new TnBeanMetaDataFactoryExtension();
        factory.setDataSource(_dataSource);
        factory.setValueTypeFactory(assistValueTypeFactory());
        return factory;
    }

    // -----------------------------------------------------
    //                                    Value Type Factory
    //                                    ------------------
    public TnValueTypeFactory assistValueTypeFactory() { // Lazy component
        if (_valueTypeFactory != null) {
            return _valueTypeFactory;
        }
        synchronized (this) {
            if (_valueTypeFactory != null) {
                return _valueTypeFactory;
            }
            _valueTypeFactory = createValueTypeFactory();
        }
        return _valueTypeFactory;
    }

    protected TnValueTypeFactory createValueTypeFactory() {
        return new TnValueTypeFactoryImpl();
    }

    // -----------------------------------------------------
    //                                  SQL Analyzer Factory
    //                                  --------------------
    /**
     * Assist the factory of SQL analyzer. <br />
     * This factory is also used on ConditionBean.toDisplaySql().
     * So this method should not depend on the state of this class. 
     * @return The factory of SQL analyzer. (NotNull)
     */
    public SqlAnalyzerFactory assistSqlAnalyzerFactory() {
        return new SqlAnalyzerFactory() {
            public SqlAnalyzer create(String sql, boolean blockNullParameter) {
                return new SqlAnalyzer(sql, blockNullParameter);
            }
        };
    }

    // -----------------------------------------------------
    //                                    Resource Parameter
    //                                    ------------------
    public ResourceParameter assistResourceParameter() {
        ResourceParameter resourceParameter = new ResourceParameter();
        resourceParameter.setOutsideSqlPackage(DBFluteConfig.getInstance()
                .getOutsideSqlPackage());
        resourceParameter.setLogDateFormat(DBFluteConfig.getInstance()
                .getLogDateFormat());
        resourceParameter.setLogTimestampFormat(DBFluteConfig.getInstance()
                .getLogTimestampFormat());
        return resourceParameter;
    }

    // -----------------------------------------------------
    //                                     SQL File Encoding
    //                                     -----------------
    public String assistSqlFileEncoding() {
        return "UTF-8";
    }

    // -----------------------------------------------------
    //                               Statement Configuration
    //                               -----------------------
    public StatementConfig assistDefaultStatementConfig() {
        return DBFluteConfig.getInstance().getDefaultStatementConfig();
    }

    // ===================================================================================
    //                                                                             Dispose
    //                                                                             =======
    public void toBeDisposable() { // for HotDeploy
        if (_disposable) {
            return;
        }
        synchronized (this) {
            if (_disposable) {
                return;
            }
            // Register for BehaviorCommandInvoker
            DisposableUtil.add(new Disposable() {
                public void dispose() {
                    if (_behaviorCommandInvoker != null) {
                        _behaviorCommandInvoker.clearExecutionCache();
                    }
                    _disposable = false;
                }
            });
            // Register for BeanDescFactory
            DisposableUtil.add(new Disposable() {
                public void dispose() {
                    DfBeanDescFactory.clear();
                }
            });
            // Register for ValueTypes
            DisposableUtil.add(new Disposable() {
                public void dispose() {
                    ValueTypes.clear();
                }
            });
            _disposable = true;
        }
    }

    public boolean isDisposable() {
        return _disposable;
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertBehaviorCommandInvoker() {
        if (_behaviorCommandInvoker == null) {
            String msg = "The attribute 'behaviorCommandInvoker' should not be null!";
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setBehaviorCommandInvoker(
            BehaviorCommandInvoker behaviorCommandInvoker) {
        _behaviorCommandInvoker = behaviorCommandInvoker;
    }

    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }
}
