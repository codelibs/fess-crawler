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
package org.seasar.robot.dbflute.dbmeta.hierarchy;

import java.util.List;

import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.RelationInfo;

/**
 * @author jflute
 */
public class HierarchyRequestElement {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Top key. */
    public static final String TOP_KEY = "$top$";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The column of source. */
    protected HierarchySourceColumn sourceColumn;

    /** The dbmeta of destination. */
    protected DBMeta destinationDBMeta;

    /** The column info of destination. */
    protected ColumnInfo destinationColumnInfo;

    /** The list of relation info. */
    protected java.util.List<RelationInfo> relationInfoList = new java.util.ArrayList<RelationInfo>();

    /** the list of relation property name. */
    protected java.util.List<String> relationPropertyNameList = new java.util.ArrayList<String>();

    /** Relation property key. Default value is TOP_KEY. */
    protected String relationPropertyKey = TOP_KEY;

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /**
     * Make mapping between the source column and the destination one.
     * 
     * @param sourceColumn The column of source. (NotNull)
     * @param destinationColumnInfo The column info of destination. (NotNull)
     */
    public void mapping(HierarchySourceColumn sourceColumn, ColumnInfo destinationColumnInfo) {
        this.sourceColumn = sourceColumn;
        this.destinationColumnInfo = destinationColumnInfo;
    }

    /**
     * Make relatetion by relation info.
     * 
     * @param relationInfo Relation info. (NotNull)
     */
    public void relation(RelationInfo relationInfo) {
        addRelationInfoList(relationInfo);
    }

    protected void addRelationInfoList(RelationInfo relationInfo) {
        relationInfoList.add(relationInfo);
        addRelationPropertyNameList(relationInfo.getRelationPropertyName());
    }

    protected void addRelationPropertyNameList(String relationPropertyName) {
        relationPropertyNameList.add(relationPropertyName);
        setupRelationPropertyKey();
    }

    protected void setupRelationPropertyKey() {
        final StringBuilder sb = new StringBuilder();
        for (String relationPropertyName : relationPropertyNameList) {
            if (sb.length() > 0) {
                sb.append("_");
            }
            sb.append(relationPropertyName);
        }
        this.relationPropertyKey = sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public HierarchySourceColumn getSourceColumnInfo() {
        return sourceColumn;
    }

    public void setSourceColumnInfo(HierarchySourceColumn sourceColumn) {
        this.sourceColumn = sourceColumn;
    }

    public void setDestinationDBMeta(DBMeta destinationDBMeta) {
        this.destinationDBMeta = destinationDBMeta;
    }

    public DBMeta getDestinationDBMeta() {
        return destinationDBMeta;
    }

    public ColumnInfo getDestinationColumnInfo() {
        return destinationColumnInfo;
    }

    public java.util.List<RelationInfo> getRelationInfoList() {
        return relationInfoList;
    }

    public List<String> getRelationPropertyNameList() {
        return relationPropertyNameList;
    }

    public String getRelationPropertyKey() {
        return relationPropertyKey;
    }

    public String toString() {
        return sourceColumn + "," + destinationColumnInfo;
    }
}
