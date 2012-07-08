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
package org.seasar.robot.db.allcommon;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;
import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.robot.dbflute.bhv.core.InvokerAssistant;
import org.seasar.robot.dbflute.bhv.core.supplement.SequenceCacheHandler;
import org.seasar.robot.dbflute.bhv.core.supplement.SequenceCacheKeyGenerator;
import org.seasar.robot.dbflute.bhv.outsidesql.factory.DefaultOutsideSqlExecutorFactory;
import org.seasar.robot.dbflute.bhv.outsidesql.factory.OutsideSqlExecutorFactory;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.exception.factory.DefaultSQLExceptionHandlerFactory;
import org.seasar.robot.dbflute.exception.factory.SQLExceptionHandlerFactory;
import org.seasar.robot.dbflute.exception.thrower.BehaviorExceptionThrower;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.jdbc.DataSourceHandler;
import org.seasar.robot.dbflute.jdbc.HandlingDataSourceWrapper;
import org.seasar.robot.dbflute.jdbc.SQLExceptionDigger;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.resource.ResourceParameter;
import org.seasar.robot.dbflute.s2dao.extension.TnBeanMetaDataFactoryExtension;
import org.seasar.robot.dbflute.s2dao.jdbc.TnStatementFactoryImpl;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.robot.dbflute.twowaysql.factory.DefaultSqlAnalyzerFactory;
import org.seasar.robot.dbflute.twowaysql.factory.SqlAnalyzerFactory;

/**
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedInvokerAssistant implements InvokerAssistant {

    // ===================================================================================
    // Attribute
    // =========
    // -----------------------------------------------------
    // DI Component
    // ------------
    protected BehaviorCommandInvoker _behaviorCommandInvoker;

    protected DataSource _dataSource;

    // -----------------------------------------------------
    // Lazy Component
    // --------------
    protected volatile DBMetaProvider _dbmetaProvider;

    protected volatile SqlClauseCreator _sqlClauseCreator;

    protected volatile StatementFactory _statementFactory;

    protected volatile TnBeanMetaDataFactory _beanMetaDataFactory;

    protected volatile SqlAnalyzerFactory _sqlAnalyzerFactory;

    protected volatile OutsideSqlExecutorFactory _outsideSqlExecutorFactory;

    protected volatile SQLExceptionHandlerFactory _sqlExceptionHandlerFactory;

    protected volatile SequenceCacheHandler _sequenceCacheHandler;

    // -----------------------------------------------------
    // Disposable Flag
    // ---------------
    protected volatile boolean _disposable;

    // ===================================================================================
    // Assistant Main Work
    // ===================
    // -----------------------------------------------------
    // Current DBDef
    // -------------
    public DBDef assistCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    // -----------------------------------------------------
    // Data Source
    // -----------
    public DataSource assistDataSource() { // DI component
        final DataSourceHandler dataSourceHandler =
            DBFluteConfig.getInstance().getDataSourceHandler();
        if (dataSourceHandler != null) {
            return new HandlingDataSourceWrapper(_dataSource, dataSourceHandler);
        }
        return _dataSource;
    }

    // -----------------------------------------------------
    // DBMeta Provider
    // ---------------
    public DBMetaProvider assistDBMetaProvider() { // lazy component
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
        return DBMetaInstanceHandler.getProvider();
    }

    // -----------------------------------------------------
    // SQL Clause Creator
    // ------------------
    public SqlClauseCreator assistSqlClauseCreator() { // lazy component
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
        final SqlClauseCreator creator =
            DBFluteConfig.getInstance().getSqlClauseCreator();
        if (creator != null) {
            return creator;
        }
        return new ImplementedSqlClauseCreator(); // as default
    }

    // -----------------------------------------------------
    // Statement Factory
    // -----------------
    public StatementFactory assistStatementFactory() { // lazy component
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
    // Bean Meta Data Factory
    // ----------------------
    public TnBeanMetaDataFactory assistBeanMetaDataFactory() { // lazy component
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
        final TnBeanMetaDataFactoryExtension factory =
            new TnBeanMetaDataFactoryExtension();
        factory.setDataSource(_dataSource);
        factory.setInternalDebug(DBFluteConfig.getInstance().isInternalDebug());
        return factory;
    }

    // -----------------------------------------------------
    // SQL Analyzer Factory
    // --------------------
    /**
     * {@inheritDoc}
     */
    public SqlAnalyzerFactory assistSqlAnalyzerFactory() { // lazy component
        if (_sqlAnalyzerFactory != null) {
            return _sqlAnalyzerFactory;
        }
        synchronized (this) {
            if (_sqlAnalyzerFactory != null) {
                return _sqlAnalyzerFactory;
            }
            _sqlAnalyzerFactory = createSqlAnalyzerFactory();
        }
        return _sqlAnalyzerFactory;
    }

    protected SqlAnalyzerFactory createSqlAnalyzerFactory() {
        return new DefaultSqlAnalyzerFactory();
    }

    // -----------------------------------------------------
    // OutsideSql Executor Factory
    // ---------------------------
    /**
     * {@inheritDoc}
     */
    public OutsideSqlExecutorFactory assistOutsideSqlExecutorFactory() {
        if (_outsideSqlExecutorFactory != null) {
            return _outsideSqlExecutorFactory;
        }
        synchronized (this) {
            if (_outsideSqlExecutorFactory != null) {
                return _outsideSqlExecutorFactory;
            }
            _outsideSqlExecutorFactory = createOutsideSqlExecutorFactory();
        }
        return _outsideSqlExecutorFactory;
    }

    protected OutsideSqlExecutorFactory createOutsideSqlExecutorFactory() {
        final OutsideSqlExecutorFactory factory =
            DBFluteConfig.getInstance().getOutsideSqlExecutorFactory();
        if (factory != null) {
            return factory;
        }
        return new DefaultOutsideSqlExecutorFactory();
    }

    // -----------------------------------------------------
    // SQLException Digger
    // -------------------
    /**
     * {@inheritDoc}
     */
    public SQLExceptionDigger assistSQLExceptionDigger() {
        return DBFluteConfig.getInstance().getSQLExceptionDigger();
    }

    // -----------------------------------------------------
    // SQLException Handler Factory
    // ----------------------------
    /**
     * {@inheritDoc}
     */
    public SQLExceptionHandlerFactory assistSQLExceptionHandlerFactory() { // lazy
                                                                           // component
        if (_sqlExceptionHandlerFactory != null) {
            return _sqlExceptionHandlerFactory;
        }
        synchronized (this) {
            if (_sqlExceptionHandlerFactory != null) {
                return _sqlExceptionHandlerFactory;
            }
            _sqlExceptionHandlerFactory = createSQLExceptionHandlerFactory();
        }
        return _sqlExceptionHandlerFactory;
    }

    protected SQLExceptionHandlerFactory createSQLExceptionHandlerFactory() {
        return new DefaultSQLExceptionHandlerFactory();
    }

    // -----------------------------------------------------
    // Sequence Cache Handler
    // ----------------------
    /**
     * {@inheritDoc}
     */
    public SequenceCacheHandler assistSequenceCacheHandler() { // lazy component
        if (_sequenceCacheHandler != null) {
            return _sequenceCacheHandler;
        }
        synchronized (this) {
            if (_sequenceCacheHandler != null) {
                return _sequenceCacheHandler;
            }
            _sequenceCacheHandler = createSequenceCacheHandler();
        }
        return _sequenceCacheHandler;
    }

    protected SequenceCacheHandler createSequenceCacheHandler() {
        final SequenceCacheHandler handler = new SequenceCacheHandler();
        final SequenceCacheKeyGenerator generator =
            DBFluteConfig.getInstance().getSequenceCacheKeyGenerator();
        if (generator != null) {
            handler.setSequenceCacheKeyGenerator(generator);
        }
        handler.setInternalDebug(DBFluteConfig.getInstance().isInternalDebug());
        return handler;
    }

    // -----------------------------------------------------
    // SQL File Encoding
    // -----------------
    public String assistSqlFileEncoding() {
        return "UTF-8";
    }

    // -----------------------------------------------------
    // Statement Configuration
    // -----------------------
    public StatementConfig assistDefaultStatementConfig() {
        return DBFluteConfig.getInstance().getDefaultStatementConfig();
    }

    // -----------------------------------------------------
    // Behavior Exception Thrower
    // --------------------------
    public BehaviorExceptionThrower assistBehaviorExceptionThrower() {
        return new BehaviorExceptionThrower();
    }

    // -----------------------------------------------------
    // Resource Parameter
    // ------------------
    public ResourceParameter assistResourceParameter() {
        final ResourceParameter resourceParameter = new ResourceParameter();
        resourceParameter.setOutsideSqlPackage(DBFluteConfig
            .getInstance()
            .getOutsideSqlPackage());
        resourceParameter.setLogDateFormat(DBFluteConfig
            .getInstance()
            .getLogDateFormat());
        resourceParameter.setLogTimestampFormat(DBFluteConfig
            .getInstance()
            .getLogTimestampFormat());
        resourceParameter.setInternalDebug(DBFluteConfig
            .getInstance()
            .isInternalDebug());
        return resourceParameter;
    }

    // ===================================================================================
    // Dispose
    // =======
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
    // Assert Helper
    // =============
    protected void assertBehaviorCommandInvoker() {
        if (_behaviorCommandInvoker == null) {
            final String msg =
                "The attribute 'behaviorCommandInvoker' should not be null!";
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    // Accessor
    // ========
    public void setBehaviorCommandInvoker(
            final BehaviorCommandInvoker behaviorCommandInvoker) {
        _behaviorCommandInvoker = behaviorCommandInvoker;
    }

    public void setDataSource(final DataSource dataSource) {
        _dataSource = dataSource;
    }
}
