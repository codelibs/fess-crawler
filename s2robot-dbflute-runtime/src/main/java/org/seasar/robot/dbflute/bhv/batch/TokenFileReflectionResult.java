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
package org.seasar.robot.dbflute.bhv.batch;

/**
 * @author jflute
 */
public class TokenFileReflectionResult {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected java.util.List<String> _columnNameList;
    protected int _successCount;
    protected java.util.List<TokenFileReflectionFailure> _failureList;

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public void incrementSuccessCount() {
        ++_successCount;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public java.util.List<String> getColumnNameList() {
        return _columnNameList;
    }

    public void setColumnNameList(java.util.List<String> columnNameList) {
        this._columnNameList = columnNameList;
    }

    public int getSuccessCount() {
        return _successCount;
    }

    public void setSuccessCount(int successCount) {
        _successCount = successCount;
    }

    public java.util.List<TokenFileReflectionFailure> getFailureList() {
        return _failureList;
    }

    public void setFailureList(java.util.List<TokenFileReflectionFailure> failureList) {
        this._failureList = failureList;
    }
}
