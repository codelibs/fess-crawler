package org.seasar.robot.db.allcommon;

import javax.sql.DataSource;

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.bhv.core.BehaviorCommandInvoker;
import org.seasar.dbflute.bhv.core.InvokerAssistant;
import org.seasar.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.jdbc.DataSourceHandler;
import org.seasar.dbflute.jdbc.DataSourceWrapper;
import org.seasar.dbflute.jdbc.StatementFactory;
import org.seasar.dbflute.resource.ResourceParameter;
import org.seasar.extension.jdbc.types.ValueTypes;
import org.seasar.dbflute.s2dao.beans.factory.TnBeanDescFactory;
import org.seasar.dbflute.s2dao.extension.TnBeanMetaDataFactoryExtension;
import org.seasar.dbflute.s2dao.jdbc.TnStatementFactoryImpl;
import org.seasar.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.dbflute.s2dao.valuetype.TnValueTypeFactory;
import org.seasar.dbflute.s2dao.valuetype.impl.TnValueTypeFactoryImpl;

import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

/**
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedInvokerAssistant implements InvokerAssistant {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected BehaviorCommandInvoker _behaviorCommandInvoker;
    protected DataSource _dataSource;
    protected DBMetaProvider _dbmetaProvider;
    protected SqlClauseCreator _sqlClauseCreator;
    protected StatementFactory _statementFactory;
    protected TnBeanMetaDataFactory _beanMetaDataFactory;
    protected TnValueTypeFactory _valueTypeFactory;
    
    protected boolean _disposable;

    // ===================================================================================
    //                                                                 Assistant Main Work
    //                                                                 ===================
    public DBDef assistCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    public DataSource assistDataSource() {
        DataSourceHandler dataSourceHandler = DBFluteConfig.getInstance().getDataSourceHandler();
        if (dataSourceHandler != null) {
            return new DataSourceWrapper(_dataSource, dataSourceHandler);
        }
        return _dataSource;
    }

    public DBMetaProvider assistDBMetaProvider() {
        if (_dbmetaProvider != null) {
            return _dbmetaProvider;
        }
        _dbmetaProvider = createDBMetaProvider();
        return _dbmetaProvider;
    }

    protected DBMetaProvider createDBMetaProvider() {
        return new DBMetaInstanceHandler();
    }

    public SqlClauseCreator assistSqlClauseCreator() {
        if (_sqlClauseCreator != null) {
            return _sqlClauseCreator;
        }
        _sqlClauseCreator = createSqlClauseCreator();
        return _sqlClauseCreator;
    }

    protected SqlClauseCreator createSqlClauseCreator() {
        return new ImplementedSqlClauseCreator();
    }

    public StatementFactory assistStatementFactory() {
        if (_statementFactory != null) {
            return _statementFactory;
        }
        _statementFactory = createStatementFactory();
        return _statementFactory;
    }

    protected StatementFactory createStatementFactory() {
        final TnStatementFactoryImpl factory = new TnStatementFactoryImpl();
        factory.setDefaultStatementConfig(DBFluteConfig.getInstance().getDefaultStatementConfig());
        factory.setInternalDebug(DBFluteConfig.getInstance().isInternalDebug());
        return factory;
    }

    public TnBeanMetaDataFactory assistBeanMetaDataFactory() {
        if (_beanMetaDataFactory != null) {
            return _beanMetaDataFactory;
        }
        _beanMetaDataFactory = createBeanMetaDataFactory();
        return _beanMetaDataFactory;
    }

    protected TnBeanMetaDataFactory createBeanMetaDataFactory() {
        final TnBeanMetaDataFactoryExtension factory = new TnBeanMetaDataFactoryExtension();
        factory.setDataSource(_dataSource);
        factory.setValueTypeFactory(assistValueTypeFactory());
        return factory;
    }
    
    public TnValueTypeFactory assistValueTypeFactory() {
        if (_valueTypeFactory != null) {
            return _valueTypeFactory;
        }
        _valueTypeFactory = createValueTypeFactory();
        return _valueTypeFactory;
    }

    protected TnValueTypeFactory createValueTypeFactory() {
        return new TnValueTypeFactoryImpl();
    }

    public ResourceParameter assistResourceParameter() {
        ResourceParameter resourceParameter = new ResourceParameter();
        resourceParameter.setOutsideSqlPackage(DBFluteConfig.getInstance().getOutsideSqlPackage());
        resourceParameter.setLogDateFormat(DBFluteConfig.getInstance().getLogDateFormat());
        resourceParameter.setLogTimestampFormat(DBFluteConfig.getInstance().getLogTimestampFormat());
        return resourceParameter;
    }

    public String assistSqlFileEncoding() {
        return "UTF-8";
    }

    // ===================================================================================
    //                                                                             Dispose
    //                                                                             =======
    public void toBeDisposable() { // for HotDeploy
        if (!_disposable) {
            synchronized (this) {
                if (!_disposable) {
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
                            TnBeanDescFactory.clear();
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
    public void setBehaviorCommandInvoker(BehaviorCommandInvoker behaviorCommandInvoker) {
        _behaviorCommandInvoker = behaviorCommandInvoker;
    }

    public void setDataSource(DataSource dataSource) {
        _dataSource = dataSource;
    }
}
