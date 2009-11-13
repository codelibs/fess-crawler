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
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnAbstractBatchAutoHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public abstract class TnAbstractBatchAutoStaticCommand extends TnAbstractAutoStaticCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractBatchAutoStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling,
            boolean versionNoAutoIncrementOnMemory) {
        super(dataSource, statementFactory, beanMetaData, targetDBMeta, propertyNames, optimisticLockHandling,
                versionNoAutoIncrementOnMemory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    public Object execute(Object[] args) {
        final TnAbstractBatchAutoHandler handler = createBatchAutoHandler();
        handler.setOptimisticLockHandling(optimisticLockHandling);
        handler.setVersionNoAutoIncrementOnMemory(versionNoAutoIncrementOnMemory);
        handler.setSql(getSql());
        // The logging message SQL of procedure is unnecessary.
        // handler.setLoggingMessageSqlArgs(args);
        return handler.executeBatch(args);
    }

    protected abstract TnAbstractBatchAutoHandler createBatchAutoHandler();
}
