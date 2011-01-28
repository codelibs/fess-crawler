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
package org.seasar.robot.dbflute.dbmeta.name;

/**
 * The value class for the SQL name of table. 
 * @author jflute
 */
public class TableSqlName {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _tableSqlName;
    protected final String _correspondingDbName;
    protected SqlNameFilter _sqlNameFilter;
    protected boolean _locked;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TableSqlName(String tableSqlName, String correspondingDbName) {
        _tableSqlName = tableSqlName;
        _correspondingDbName = correspondingDbName;
    }

    public synchronized void xacceptFilter(SqlNameFilter sqlNameFilter) { // called only once
        if (_locked) {
            String msg = "The object has been locked so your setting is invalid: " + sqlNameFilter;
            throw new IllegalStateException(msg);
        }
        _sqlNameFilter = sqlNameFilter;
        _locked = true;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int hashCode() {
        return _tableSqlName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TableSqlName)) {
            return false;
        }
        final TableSqlName target = (TableSqlName) obj;
        return _tableSqlName.equals(target._tableSqlName);
    }

    @Override
    public String toString() {
        if (_sqlNameFilter != null) {
            return _sqlNameFilter.filter(_tableSqlName, _correspondingDbName);
        } else {
            return _tableSqlName;
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getCorrespondingDbName() {
        return _correspondingDbName;
    }
}
