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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.XLog;
import org.seasar.robot.dbflute.exception.BatchEntityAlreadyUpdatedException;
import org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.robot.dbflute.exception.EntityDuplicatedException;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractBatchHandler extends TnAbstractEntityHandler {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(TnAbstractBatchHandler.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // non-thread-safe because handler is created per execution
    protected StringBuilder _batchLoggingSb;
    protected int _loggingRecordCount;
    protected int _loggingScopeSize;
    protected boolean _existsSkippedLogging;
    protected boolean _alreadySavedToResultInfo;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnAbstractBatchHandler(DataSource dataSource, StatementFactory statementFactory, String sql,
            TnBeanMetaData beanMetaData, TnPropertyType[] boundPropTypes) {
        super(dataSource, statementFactory, sql, beanMetaData, boundPropTypes);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    public int execute(Object[] args) {
        String msg = "This method should not be called when BatchUpdate.";
        throw new IllegalStateException(msg);
    }

    public int[] executeBatch(List<?> beanList) {
        if (beanList == null) {
            String msg = "The argument 'beanList' should not be null";
            throw new IllegalArgumentException(msg);
        }
        if (beanList.isEmpty()) {
            if (_log.isDebugEnabled()) {
                _log.debug("Skip executeBatch() bacause of the empty list.");
            }
            return new int[0];
        }
        processBefore(beanList);
        final Connection conn = getConnection();
        try {
            RuntimeException sqlEx = null;
            final PreparedStatement ps = prepareStatement(conn);
            int[] result = null;
            try {
                for (Object bean : beanList) {
                    processBatchBefore(bean);
                    prepareBatchElement(conn, ps, bean);
                }
                handleBatchLogging();
                result = executeBatch(ps, beanList);
                handleBatchUpdateResultWithOptimisticLock(ps, beanList, result);
            } catch (RuntimeException e) {
                // not SQLFailureException because
                // a wrapper of JDBC may throw an other exception
                sqlEx = e;
                throw e;
            } finally {
                close(ps);
                processFinally(beanList, sqlEx);
            }
            // a value of exclusive control column should be synchronized
            // after handling optimistic lock
            int index = 0;
            for (Object bean : beanList) {
                processBatchSuccess(bean, index);
                ++index;
            }
            processSuccess(beanList, result.length);
            return result;
        } finally {
            close(conn);
        }
    }

    protected void prepareBatchElement(Connection conn, PreparedStatement ps, Object bean) {
        setupBindVariables(bean);
        final Object[] bindVariables = _bindVariables;
        logSql(bindVariables, getArgTypes(bindVariables));
        bindArgs(conn, ps, bindVariables, _bindVariableValueTypes);
        addBatch(ps);
    }

    protected void logSql(Object[] args, Class<?>[] argTypes) {
        if (isBatchLoggingOver()) {
            _existsSkippedLogging = true;
            return;
        }
        super.logSql(args, argTypes);
    };

    @Override
    protected void logDisplaySql(String displaySql) {
        if (_batchLoggingSb == null) {
            _batchLoggingSb = new StringBuilder(1000);
        }
        saveBatchLoggingSql(displaySql);
        if (needsBreakLoggingScope()) {
            handleBatchLogging(); // and also cleared
        }
    }

    protected boolean isBatchLoggingOver() {
        final Integer batchLoggingLimit = getBatchLoggingLimit();
        if (batchLoggingLimit == null || batchLoggingLimit < 0) {
            return false;
        }
        return _loggingRecordCount >= batchLoggingLimit;
    }

    protected void noticeBatchLoggingOver() {
        if (_existsSkippedLogging) {
            if (XLog.isLogEnabled()) {
                final Integer batchLoggingLimit = getBatchLoggingLimit();
                XLog.log("...Skipping several loggings by the limit option: " + batchLoggingLimit);
            }
        }
    }

    protected abstract Integer getBatchLoggingLimit();

    protected void saveBatchLoggingSql(String displaySql) {
        ++_loggingRecordCount;
        ++_loggingScopeSize;
        _batchLoggingSb.append(ln()).append(displaySql).append(";");
    }

    protected boolean needsBreakLoggingScope() {
        return _loggingScopeSize >= 100; // per 100 statements
    }

    @Override
    protected void saveDisplaySqlForResultInfo(String displaySql) {
        // do nothing but it saves batch SQL in batch logging process
    }

    protected String handleBatchLogging() {
        if (_batchLoggingSb == null) {
            return null;
        }
        final String batchSql = _batchLoggingSb.toString();
        if (isLogEnabled()) {
            log(batchSql); // batch logging (always starts with a line separator)
        }
        clearBatchLogging();
        if (!_alreadySavedToResultInfo && hasSqlResultHandler()) {
            final String savedDisplaySql = batchSql.trim(); // remove first line separator
            super.saveDisplaySqlForResultInfo(savedDisplaySql); // only first scope is saved)
            _alreadySavedToResultInfo = true;
        }
        return batchSql;
    }

    protected void clearBatchLogging() {
        _batchLoggingSb = null;
        _loggingScopeSize = 0;
    }

    // ===================================================================================
    //                                                                   Extension Process
    //                                                                   =================
    @Override
    protected void processBefore(Object beanList) {
        super.processBefore(beanList);
    }

    @Override
    protected void processFinally(Object beanList, RuntimeException sqlEx) {
        super.processFinally(beanList, sqlEx);
        noticeBatchLoggingOver();

        // clear just in case
        _existsSkippedLogging = false;
        _alreadySavedToResultInfo = false;
    }

    @Override
    protected void processSuccess(Object beanList, int ret) {
        super.processSuccess(beanList, ret);
    }

    protected void processBatchBefore(Object bean) {
    }

    // *after case about identity is unsupported at Batch Update   
    protected void processBatchSuccess(Object bean, int index) {
        updateTimestampIfNeed(bean, index);
        updateVersionNoIfNeed(bean, index);
    }

    // ===================================================================================
    //                                                                     Optimistic Lock
    //                                                                     ===============
    protected void handleBatchUpdateResultWithOptimisticLock(PreparedStatement ps, List<?> list, int[] result) {
        if (isCurrentDBDef(DBDef.Oracle)) {
            final int updateCount;
            try {
                updateCount = ps.getUpdateCount();
            } catch (SQLException e) {
                handleSQLException(e, ps);
                return; // unreachable
            }
            handleBatchUpdateResultWithOptimisticLockByUpdateCount(list, updateCount);
        } else {
            handleBatchUpdateResultWithOptimisticLockByResult(list, result);
        }
    }

    protected boolean isCurrentDBDef(DBDef currentDBDef) {
        return ResourceContext.isCurrentDBDef(currentDBDef);
    }

    protected void handleBatchUpdateResultWithOptimisticLockByUpdateCount(List<?> list, int updateCount) {
        if (list.isEmpty()) {
            return; // for safety
        }
        if (updateCount < 0) {
            return; // for safety
        }
        final int entityCount = list.size();
        if (updateCount < entityCount) {
            if (_optimisticLockHandling) {
                throw new BatchEntityAlreadyUpdatedException(list.get(0), 0, updateCount);
            } else {
                String msg = "The entity was NOT found! it has already been deleted.";
                msg = msg + " updateCount=" + updateCount;
                msg = msg + " entityCount=" + entityCount;
                msg = msg + " allEntities=" + list;
                throw new EntityAlreadyDeletedException(msg);
            }
        }
    }

    protected void handleBatchUpdateResultWithOptimisticLockByResult(List<?> list, int[] result) {
        if (list.isEmpty()) {
            return; // for safety
        }
        final int[] updatedCountArray = result;
        final int entityCount = list.size();
        int index = 0;
        boolean alreadyUpdated = false;
        for (int oneUpdateCount : updatedCountArray) {
            if (entityCount <= index) {
                break; // for safety
            }
            if (oneUpdateCount == 0) {
                alreadyUpdated = true;
                break;
            } else if (oneUpdateCount > 1) {
                String msg = "The entity updated two or more records in batch update:";
                msg = msg + " entity=" + list.get(index);
                msg = msg + " updatedCount=" + oneUpdateCount;
                msg = msg + " allEntities=" + list;
                throw new EntityDuplicatedException(msg);
            }
            ++index;
        }
        if (alreadyUpdated) {
            int updateCount = 0;
            for (int oneUpdateCount : updatedCountArray) {
                updateCount = updateCount + oneUpdateCount;
            }
            if (_optimisticLockHandling) {
                throw new BatchEntityAlreadyUpdatedException(list.get(index), 0, updateCount);
            } else {
                String msg = "The entity was NOT found! it has already been deleted:";
                msg = msg + " entity=" + list.get(index);
                msg = msg + " updateCount=" + updateCount;
                msg = msg + " allEntities=" + list;
                throw new EntityAlreadyDeletedException(msg);
            }
        }
    }

    // ===================================================================================
    //                                                                         SQL Logging
    //                                                                         ===========
    @Override
    protected String buildExceptionMessageSql() {
        if (_exceptionMessageSqlArgs == null && _bindVariables != null) {
            _exceptionMessageSqlArgs = _bindVariables; // as current bean
        }
        return super.buildExceptionMessageSql();
    }

    // ===================================================================================
    //                                                                      JDBC Delegator
    //                                                                      ==============
    protected int[] executeBatch(PreparedStatement ps, List<?> list) {
        try {
            return ps.executeBatch();
        } catch (SQLException e) {
            handleSQLException(e, ps, true);
            return null; // unreachable
        }
    }

    protected void addBatch(PreparedStatement ps) {
        try {
            ps.addBatch();
        } catch (SQLException e) {
            handleSQLException(e, ps);
        }
    }
}
