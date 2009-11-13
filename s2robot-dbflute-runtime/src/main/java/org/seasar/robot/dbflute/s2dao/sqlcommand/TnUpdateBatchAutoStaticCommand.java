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
package org.seasar.robot.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnAbstractAutoHandler;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnAbstractBatchAutoHandler;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnUpdateBatchAutoHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnUpdateBatchAutoStaticCommand extends TnAbstractBatchAutoStaticCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateBatchAutoStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling,
            boolean versionNoAutoIncrementOnMemory) {
        super(dataSource, statementFactory, beanMetaData, targetDBMeta, propertyNames, optimisticLockHandling,
                versionNoAutoIncrementOnMemory);
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    @Override
    protected TnAbstractAutoHandler createAutoHandler() {
        return createBatchAutoHandler();
    }

    @Override
    protected TnAbstractBatchAutoHandler createBatchAutoHandler() {
        TnUpdateBatchAutoHandler handler = newInternalBatchAutoHandler();
        handler.setVersionNoAutoIncrementOnMemory(versionNoAutoIncrementOnMemory);
        return handler;
    }

    protected TnUpdateBatchAutoHandler newInternalBatchAutoHandler() {
        return new TnUpdateBatchAutoHandler(getDataSource(), getStatementFactory(), getBeanMetaData(),
                getPropertyTypes());
    }

    @Override
    protected void setupSql() {
        setupUpdateSql();
    }

    @Override
    protected void setupPropertyTypes(String[] propertyNames) {
        setupUpdatePropertyTypes(propertyNames);
    }
}
