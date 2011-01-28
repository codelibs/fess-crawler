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
package org.seasar.robot.dbflute.bhv.core.command;

import org.seasar.robot.dbflute.bhv.core.supplement.SequenceCache;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.resource.ResourceContext;

/**
 * The command to select next values of sequence for sub column.
 * @author jflute
 * @param <RESULT> The type of result.
 */
public class SelectNextValSubCommand<RESULT> extends SelectNextValCommand<RESULT> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The information of column. (NotNull) */
    protected ColumnInfo _columnInfo;

    /** The name of sequence. (NotNull) */
    protected String _sequenceName;

    /** The increment size for sequence. (NullAllowed) */
    protected Integer _incrementSize;

    /** The cache size for sequence. (NullAllowed) */
    protected Integer _cacheSize;

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    @Override
    public String buildSqlExecutionKey() {
        assertStatus("buildSqlExecutionKey");
        return buildSequenceKeyName() + ":" + getCommandName() + "()";
    }

    protected String buildSequenceKeyName() {
        return _tableDbName + "." + _columnInfo.getColumnDbName();
    }

    @Override
    protected String getSequenceNextValSql() {
        return ResourceContext.currentDBDef().dbway().buildSequenceNextValSql(_sequenceName);
    }

    @Override
    protected String prepareSequenceCache(String sql, SequenceCache sequenceCache) {
        return doPrepareSequenceCache(sql, sequenceCache, _incrementSize, _cacheSize);
    }

    @Override
    protected SequenceCache findSequenceCache(DBMeta dbmeta) {
        final String tableName = buildSequenceKeyName();
        final String sequenceName = dbmeta.getSequenceName();
        return doFindSequenceCache(tableName, sequenceName, _cacheSize, _incrementSize);
    }

    @Override
    protected void assertTableHasSequence() {
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    @Override
    protected void assertStatus(String methodName) {
        super.assertStatus(methodName);
        if (_columnInfo == null) {
            throw new IllegalStateException(buildAssertMessage("_columnInfo", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setColumnInfo(ColumnInfo columnInfo) {
        _columnInfo = columnInfo;
    }

    public void setSequenceName(String sequenceName) {
        _sequenceName = sequenceName;
    }

    public void setIncrementSize(Integer incrementSize) {
        _incrementSize = incrementSize;
    }

    public void setCacheSize(Integer cacheSize) {
        _cacheSize = cacheSize;
    }
}
