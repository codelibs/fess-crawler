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
package org.seasar.robot.dbflute.outsidesql;

import org.seasar.robot.dbflute.jdbc.StatementConfig;

/**
 * The option of outside-SQL. It contains various information about execution.
 * @author jflute
 */
public class OutsideSqlOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    /** The request type of paging. */
    protected String _pagingRequestType = "non";

    protected boolean _dynamicBinding;

    /** The configuration of statement. (Nullable) */
    protected StatementConfig _statementConfig;
    
    protected String _sourcePagingRequestType = "non";

    // -----------------------------------------------------
    //                                           Information
    //                                           -----------
    /** The DB name of table. It is not related with the options of outside-SQL. */
    protected String _tableDbName;

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public void autoPaging() {
        _pagingRequestType = "auto";
    }

    public void manualPaging() {
        _pagingRequestType = "manual";
    }

    public void dynamicBinding() {
        _dynamicBinding = true;
    }

    // ===================================================================================
    //                                                                          Unique Key
    //                                                                          ==========
    public String generateUniqueKey() {
        return "{" + _pagingRequestType + "/" + _dynamicBinding + "}";
    }

    // ===================================================================================
    //                                                                                Copy
    //                                                                                ====
    public OutsideSqlOption copyOptionWithoutPaging() {
        final OutsideSqlOption copyOption = new OutsideSqlOption();
        copyOption.setPagingSourceRequestType(_pagingRequestType);
        if (isDynamicBinding()) {
            copyOption.dynamicBinding();
        }
        copyOption.setTableDbName(_tableDbName);
        return copyOption;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{paging=" + _pagingRequestType + ", dynamic=" + _dynamicBinding + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    public boolean isAutoPaging() {
        return "auto".equals(_pagingRequestType);
    }

    public boolean isManualPaging() {
        return "manual".equals(_pagingRequestType);
    }

    public boolean isDynamicBinding() {
        return _dynamicBinding;
    }

    public StatementConfig getStatementConfig() {
        return _statementConfig;
    }

    public void setStatementConfig(StatementConfig statementConfig) {
        _statementConfig = statementConfig;
    }

    protected void setPagingSourceRequestType(String sourcePagingRequestType) { // Very Internal
        _sourcePagingRequestType = sourcePagingRequestType;
    }

    public boolean isSourcePagingRequestTypeAuto() { // Very Internal
        return "auto".equals(_sourcePagingRequestType);
    }

    // -----------------------------------------------------
    //                                           Information
    //                                           -----------
    public String getTableDbName() {
        return _tableDbName;
    }

    public void setTableDbName(String tableDbName) {
        _tableDbName = tableDbName;
    }
}
