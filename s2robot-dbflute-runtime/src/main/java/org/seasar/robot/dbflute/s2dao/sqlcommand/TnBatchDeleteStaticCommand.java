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
package org.seasar.robot.dbflute.s2dao.sqlcommand;

import java.util.List;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnAbstractEntityHandler;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnBatchDeleteHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnBatchDeleteStaticCommand extends TnDeleteEntityStaticCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBatchDeleteStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling) {
        super(dataSource, statementFactory, beanMetaData, targetDBMeta, propertyNames, optimisticLockHandling);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected Object doExecute(Object[] args, TnAbstractEntityHandler handler) {
        if (args == null || args.length == 0) {
            String msg = "The argument 'args' should not be null or empty.";
            throw new IllegalArgumentException(msg);
        }
        final List<?> beanList;
        if (args[0] instanceof List<?>) {
            beanList = (List<?>) args[0];
        } else {
            String msg = "The argument 'args[0]' should be list: " + args[0];
            throw new IllegalArgumentException(msg);
        }
        // because the variable is set when exception occurs if batch 
        //handler.setExceptionMessageSqlArgs(new Object[] { ... });
        return ((TnBatchDeleteHandler) handler).executeBatch(beanList);
    };

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    @Override
    protected TnAbstractEntityHandler newEntityHandler() {
        return new TnBatchDeleteHandler(_dataSource, _statementFactory, _sql, _beanMetaData, _propertyTypes);
    }
}
