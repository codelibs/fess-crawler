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
package org.seasar.robot.dbflute.dbmeta.info;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.robot.dbflute.dbmeta.DBMeta;


/**
 * The information of unique constraint.
 * @author jflute
 */
public class UniqueInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMeta dbmeta;
    protected final List<ColumnInfo> uniqueColumnList;
    protected final boolean primary;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public UniqueInfo(DBMeta dbmeta, List<ColumnInfo> uniqueColumnList, boolean primary) {
        assertObjectNotNull("dbmeta", dbmeta);
        assertObjectNotNull("uniqueColumnList", uniqueColumnList);
        this.dbmeta = dbmeta;
        this.uniqueColumnList = uniqueColumnList;
        this.primary = primary;
    }

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public boolean containsColumn(ColumnInfo columnInfo) {
        return containsColumn(columnInfo.getColumnDbName());
    }

    protected boolean containsColumn(String columnName) {
        for (final Iterator<ColumnInfo> ite = uniqueColumnList.iterator(); ite.hasNext(); ) {
            final ColumnInfo columnInfo = ite.next();
            if (columnInfo.getColumnDbName().equals(columnName)) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public int hashCode() {
        return dbmeta.hashCode() + uniqueColumnList.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UniqueInfo)) {
            return false;
        }
        final UniqueInfo target = (UniqueInfo)obj;
        if (!this.dbmeta.equals(target.getDBMeta())) {
            return false;
        }
        if (!this.uniqueColumnList.equals(target.getUniqueColumnList())) {
            return false;
        }
        return true;
    }

    public String toString() {
        return dbmeta.getTableDbName() + "." + uniqueColumnList;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DBMeta getDBMeta() {
        return dbmeta;
    }

    /**
     * Get the list of unique column.
     * @return The list of unique column. (NotNull)
     */
    public List<ColumnInfo> getUniqueColumnList() {
        return new ArrayList<ColumnInfo>(uniqueColumnList); // as snapshot
    }

    /**
     * Get the column information of the first in primary columns.
     * @return The column information of the first in primary columns. (NotNull)
     */
    public ColumnInfo getFirstColumn() {
        return this.uniqueColumnList.get(0);
    }

    public boolean isTwoOrMore() {
        return this.uniqueColumnList.size() > 1;
    }

    public boolean isPrimary() {
        return this.primary;
    }
}
