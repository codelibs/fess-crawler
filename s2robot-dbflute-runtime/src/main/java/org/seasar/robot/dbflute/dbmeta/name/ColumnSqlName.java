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
 * The value class for the SQL name of column.
 * @author jflute
 */
public class ColumnSqlName {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _columnSqlName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ColumnSqlName(String columnSqlName) {
        this._columnSqlName = columnSqlName;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int hashCode() {
        return _columnSqlName.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ColumnSqlName)) {
            return false;
        }
        final ColumnSqlName target = (ColumnSqlName) obj;
        return _columnSqlName.equals(target._columnSqlName);
    }

    @Override
    public String toString() {
        return _columnSqlName;
    }
}
