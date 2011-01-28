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
package org.seasar.robot.dbflute.bhv;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The option of delete for varying-delete.
 * @author jflute
 * @since 0.9.7.8 (2010/12/16 Thursday)
 * @param <CB> The type of condition-bean for specification.
 */
public class DeleteOption<CB extends ConditionBean> implements WritableOption<CB> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _allowNonQueryDelete;
    protected Integer _batchLoggingDeleteLimit;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     */
    public DeleteOption() {
    }

    // ===================================================================================
    //                                                                    Non Query Delete
    //                                                                    ================
    /**
     * Allow you to non-query-delete (means query-delete without a query condition). <br />
     * Normally it is not allowed, so you can do it by this option if you want.
     * @return The option of delete. (NotNull: returns this)
     */
    public DeleteOption<CB> allowNonQueryDelete() {
        _allowNonQueryDelete = true;
        return this;
    }

    public boolean isNonQueryDeleteAllowed() {
        return _allowNonQueryDelete;
    }

    // ===================================================================================
    //                                                                       Batch Logging
    //                                                                       =============
    /**
     * Limit batch-delete logging by logging size. <br />
     * For example, if you set 3, only 3 records are logged. <br />
     * This also works to SqlLogHandler's call-back and SqlResultInfo's displaySql.
     * @param batchDeleteLoggingLimit The limit size of batch-delete logging. (NullAllowed: if null and minus, means no limit)
     */
    public void limitBatchDeleteLogging(Integer batchDeleteLoggingLimit) {
        this._batchLoggingDeleteLimit = batchDeleteLoggingLimit;
    }

    public Integer getBatchLoggingDeleteLimit() {
        return _batchLoggingDeleteLimit;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (_allowNonQueryDelete) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("NonQueryDeleteAllowed");
        }
        if (sb.length() == 0) {
            sb.append("default");
        }
        return DfTypeUtil.toClassTitle(this) + ":{" + sb.toString() + "}";
    }
}