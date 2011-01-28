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
package org.seasar.robot.dbflute.jdbc;

import java.io.Serializable;
import java.sql.ResultSet;

/**
 * The config of statement.
 * @author jflute
 */
public class StatementConfig implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                        ResultSet TYPE
    //                                        --------------
    protected Integer _resultSetType;

    // -----------------------------------------------------
    //                                      Statement Option
    //                                      ----------------
    protected Integer _queryTimeout;
    protected Integer _fetchSize;
    protected Integer _maxRows;

    // ===================================================================================
    //                                                                   Setting Interface
    //                                                                   =================
    // -----------------------------------------------------
    //                                        ResultSet TYPE
    //                                        --------------
    public StatementConfig typeForwardOnly() {
        _resultSetType = ResultSet.TYPE_FORWARD_ONLY;
        return this;
    }

    public StatementConfig typeScrollInsensitive() {
        _resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
        return this;
    }

    public StatementConfig typeScrollSensitive() {
        _resultSetType = ResultSet.TYPE_SCROLL_SENSITIVE;
        return this;
    }

    // -----------------------------------------------------
    //                                      Statement Option
    //                                      ----------------
    public StatementConfig queryTimeout(int queryTimeout) {
        this._queryTimeout = queryTimeout;
        return this;
    }

    public StatementConfig fetchSize(int fetchSize) {
        this._fetchSize = fetchSize;
        return this;
    }

    public StatementConfig maxRows(int maxRows) {
        this._maxRows = maxRows;
        return this;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    // -----------------------------------------------------
    //                                        ResultSet TYPE
    //                                        --------------
    public boolean hasResultSetType() {
        return _resultSetType != null;
    }

    // -----------------------------------------------------
    //                                      Statement Option
    //                                      ----------------
    public boolean hasStatementOptions() {
        return hasQueryTimeout() || hasFetchSize() || hasMaxRows();
    }

    public boolean hasQueryTimeout() {
        return _queryTimeout != null;
    }

    public boolean hasFetchSize() {
        return _fetchSize != null;
    }

    public boolean hasMaxRows() {
        return _maxRows != null;
    }

    // ===================================================================================
    //                                                                             Display
    //                                                                             =======
    public String buildResultSetTypeDisp() {
        if (_resultSetType == null) {
            return "default";
        }
        final String typeDisp;
        if (_resultSetType == ResultSet.TYPE_FORWARD_ONLY) {
            typeDisp = "forward";
        } else if (_resultSetType == ResultSet.TYPE_SCROLL_INSENSITIVE) {
            typeDisp = "scroll(ins)";
        } else if (_resultSetType == ResultSet.TYPE_SCROLL_SENSITIVE) {
            typeDisp = "scroll(sen)";
        } else {
            typeDisp = "unknown";
        }
        return typeDisp;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + buildResultSetTypeDisp() + ", " + _queryTimeout + ", " + _fetchSize + ", " + _maxRows + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                        ResultSet TYPE
    //                                        --------------
    public Integer getResultSetType() {
        return _resultSetType;
    }

    // -----------------------------------------------------
    //                                      Statement Option
    //                                      ----------------
    public Integer getQueryTimeout() {
        return _queryTimeout;
    }

    public Integer getFetchSize() {
        return _fetchSize;
    }

    public Integer getMaxRows() {
        return _maxRows;
    }
}
