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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.bhv.UpdateOption;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnBatchUpdateHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnBatchUpdateDynamicCommand extends TnUpdateEntityDynamicCommand {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The result for no batch-update as normal execution. */
    private static final int[] NON_BATCH_UPDATE = new int[] {};

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBatchUpdateDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected Object doExecute(Object bean, TnPropertyType[] propertyTypes, String sql,
            UpdateOption<ConditionBean> option) {
        final List<?> beanList;
        if (bean instanceof List<?>) {
            beanList = (List<?>) bean;
        } else {
            String msg = "The argument 'args[0]' should be list: " + bean;
            throw new IllegalArgumentException(msg);
        }
        final TnBatchUpdateHandler handler = createBatchUpdateHandler(propertyTypes, sql, option);
        // because the variable is set when exception occurs if batch
        //handler.setExceptionMessageSqlArgs(new Object[] { beanList });
        return handler.executeBatch(beanList);
    }

    // ===================================================================================
    //                                                                       Update Column
    //                                                                       =============
    // Batch Update does not use modified properties
    @Override
    protected Set<?> getModifiedPropertyNames(Object bean) {
        return Collections.EMPTY_SET;
    }

    @Override
    protected boolean isModifiedProperty(Set<?> modifiedSet, TnPropertyType pt) {
        return true; // as default (all columns are updated)
    }

    // ===================================================================================
    //                                                                             Handler
    //                                                                             =======
    protected TnBatchUpdateHandler createBatchUpdateHandler(TnPropertyType[] boundPropTypes, String sql,
            UpdateOption<ConditionBean> option) {
        final TnBatchUpdateHandler handler = new TnBatchUpdateHandler(_dataSource, _statementFactory, sql,
                _beanMetaData, boundPropTypes);
        handler.setOptimisticLockHandling(_optimisticLockHandling);
        handler.setVersionNoAutoIncrementOnMemory(_versionNoAutoIncrementOnMemory);
        handler.setUpdateOption(option);
        return handler;
    }

    // ===================================================================================
    //                                                                  Non Update Message
    //                                                                  ==================
    @Override
    protected String createNonUpdateLogMessage(final Object bean) {
        final StringBuilder sb = new StringBuilder();
        final String tableDbName = _targetDBMeta.getTableDbName();
        sb.append("...Skipping batch update because of non-specified: table=").append(tableDbName);
        if (bean instanceof List<?>) {
            final List<?> entityList = (List<?>) bean;
            sb.append(", batchSize=").append(entityList.size());
        }
        return sb.toString();
    }

    @Override
    protected Object getNonUpdateReturn() {
        return NON_BATCH_UPDATE;
    }
}
