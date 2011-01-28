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
package org.seasar.robot.dbflute.s2dao.sqlhandler;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.identity.TnIdentifierGenerator;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnInsertEntityHandler extends TnAbstractEntityHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnInsertEntityHandler(DataSource dataSource, StatementFactory statementFactory, String sql,
            TnBeanMetaData beanMetaData, TnPropertyType[] boundPropTypes) {
        super(dataSource, statementFactory, sql, beanMetaData, boundPropTypes);
        setOptimisticLockHandling(false);
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    @Override
    protected void setupBindVariables(Object bean) {
        setupInsertBindVariables(bean);
        setExceptionMessageSqlArgs(_bindVariables);
    }

    @Override
    protected void processBefore(Object bean) {
        super.processBefore(bean);
        doProcessIdentity(new IdentityProcessCallback() {
            public void callback(TnIdentifierGenerator generator) {
                if (generator.isPrimaryKey() && isPrimaryKeyIdentityDisabled()) {
                    disableIdentityGeneration();
                }
            }
        });
    }

    @Override
    protected void processFinally(Object bean, RuntimeException sqlEx) {
        super.processFinally(bean, sqlEx);
        try {
            doProcessIdentity(new IdentityProcessCallback() {
                public void callback(TnIdentifierGenerator generator) {
                    if (generator.isPrimaryKey() && isPrimaryKeyIdentityDisabled()) {
                        enableIdentityGeneration();
                    }
                }
            });
        } catch (RuntimeException e) {
            if (sqlEx == null) {
                throw e;
            }
            // ignore the exception when main SQL fails
            // not to close the main exception
        }

    }

    @Override
    protected void processSuccess(final Object bean, int ret) {
        super.processSuccess(bean, ret);
        doProcessIdentity(new IdentityProcessCallback() {
            public void callback(TnIdentifierGenerator generator) {
                if (generator.isPrimaryKey() && isPrimaryKeyIdentityDisabled()) {
                    return;
                }
                generator.setIdentifier(bean, _dataSource);
            }
        });
        updateVersionNoIfNeed(bean);
        updateTimestampIfNeed(bean);
    }

    protected void doProcessIdentity(IdentityProcessCallback callback) {
        final TnBeanMetaData bmd = getBeanMetaData();
        for (int i = 0; i < bmd.getIdentifierGeneratorSize(); i++) {
            final TnIdentifierGenerator generator = bmd.getIdentifierGenerator(i);
            if (!generator.isSelfGenerate()) { // identity
                callback.callback(generator);
            }
        }
    }

    protected static interface IdentityProcessCallback {
        void callback(TnIdentifierGenerator generator);
    }
}
