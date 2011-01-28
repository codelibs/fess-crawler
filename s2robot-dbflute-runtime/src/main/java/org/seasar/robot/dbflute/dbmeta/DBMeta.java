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
package org.seasar.robot.dbflute.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.robot.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.robot.dbflute.dbmeta.info.RelationInfo;
import org.seasar.robot.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.robot.dbflute.dbmeta.name.TableSqlName;

/**
 * The interface of DB meta.
 * @author jflute
 */
public interface DBMeta {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Map-string map mark. */
    public static final String MAP_STRING_MAP_MARK = "map:";

    /** Map-string list mark. */
    public static final String MAP_STRING_LIST_MARK = "list:";

    /** Map-string start brace. */
    public static final String MAP_STRING_START_BRACE = "@{";

    /** Map-string end brace. */
    public static final String MAP_STRING_END_BRACE = "@}";

    /** Map-string delimiter. */
    public static final String MAP_STRING_DELIMITER = "@;";

    /** Map-string equal. */
    public static final String MAP_STRING_EQUAL = "@=";

    // ===================================================================================
    //                                                                               DBDef
    //                                                                               =====
    /**
     * Get the current DB definition.
     * @return The current DB definition. (NotNull)
     */
    DBDef getCurrentDBDef();

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    /**
     * Get the DB name of the table.
     * @return The DB name of the table. (NotNull)
     */
    String getTableDbName();

    /**
     * Get the property name(JavaBeansRule) of table.
     * @return The property name(JavaBeansRule) of table. (NotNull)
     */
    String getTablePropertyName();

    /**
     * Get the SQL name of table.
     * @return The SQL name of table. (NotNull)
     */
    TableSqlName getTableSqlName();

    /**
     * Get the alias of the table.
     * @return The alias of the table. (NullAllowed: when it cannot get an alias from meta)
     */
    String getTableAlias();

    /**
     * Get the comment of the table. <br />
     * If the real comment contains the alias,
     * this result does NOT contain it and its delimiter.  
     * @return The comment of the table. (NullAllowed: when it cannot get a comment from meta)
     */
    String getTableComment();

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    /**
     * Has column?
     * @param columnFlexibleName The flexible name of the column. (NotNull)
     * @return Determination.
     */
    boolean hasColumn(String columnFlexibleName);

    /**
     * Find the information of the column by the flexible name of the column.
     * <pre>
     * If the table name is 'BOOK_ID', you can find the dbmeta by ...(as follows)
     *     'BOOK_ID', 'BOok_iD', 'book_id'
     *     , 'BookId', 'bookid', 'bOoKiD'
     * </pre>
     * @param columnFlexibleName The flexible name of the column. (NotNull)
     * @return The information of the column. (NotNull)
     */
    ColumnInfo findColumnInfo(String columnFlexibleName);

    /**
     * Get the list of column information.
     * @return The list of column information. (NotNull and NotEmpty)
     */
    List<ColumnInfo> getColumnInfoList();

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    /**
     * Get primary unique info that means unique info for primary key. <br />
     * If this table does not have primary-key, this method throws UnsupportedOperationException.
     * @return Primary unique info. (NotNull)
     */
    UniqueInfo getPrimaryUniqueInfo();

    /**
     * Has primary-key?
     * @return Determination.
     */
    boolean hasPrimaryKey();

    /**
     * Has compound primary-key? <br />
     * If this table does not have primary-key in the first place,
     * this method returns false. 
     * @return Determination.
     */
    boolean hasCompoundPrimaryKey();

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // -----------------------------------------------------
    //                                      Relation Element
    //                                      ----------------
    /**
     * Find relation info.
     * @param relationPropertyName The flexible name of the relation property. (NotNull)
     * @return Relation info. (NotNull)
     */
    RelationInfo findRelationInfo(String relationPropertyName);

    // -----------------------------------------------------
    //                                       Foreign Element
    //                                       ---------------
    /**
     * Has foreign?
     * @param foreignPropName The flexible name of the foreign property. (NotNull)
     * @return Determination. (NotNull)
     */
    boolean hasForeign(String foreignPropName);

    /**
     * Find foreign DB meta.
     * @param foreignPropName The flexible name of the foreign property. (NotNull)
     * @return Foreign DBMeta. (NotNull)
     */
    DBMeta findForeignDBMeta(String foreignPropName);

    /**
     * Find foreign info.
     * @param foreignPropName The flexible name of the foreign property. (NotNull)
     * @return Foreign info. (NotNull)
     */
    ForeignInfo findForeignInfo(String foreignPropName);

    /**
     * Get the list of foreign information.
     * @return The list of foreign information. (NotNull)
     */
    List<ForeignInfo> getForeignInfoList();

    // -----------------------------------------------------
    //                                      Referrer Element
    //                                      ----------------
    /**
     * Has referrer?
     * @param referrerPropertyName The flexible name of the referrer property. (NotNull)
     * @return Determination. (NotNull)
     */
    boolean hasReferrer(String referrerPropertyName);

    /**
     * Find referrer DB meta.
     * @param referrerPropertyName The flexible name of the referrer property. (NotNull)
     * @return Referrer DBMeta. (NotNull)
     */
    DBMeta findReferrerDBMeta(String referrerPropertyName);

    /**
     * Find referrer information.
     * @param referrerPropertyName The flexible name of the referrer property. (NotNull)
     * @return Referrer information. (NotNull)
     */
    ReferrerInfo findReferrerInfo(String referrerPropertyName);

    /**
     * Get the list of referrer information.
     * @return The list of referrer information. (NotNull)
     */
    List<ReferrerInfo> getReferrerInfoList();

    // -----------------------------------------------------
    //                                        Relation Trace
    //                                        --------------
    /**
     * Relation trace.
     */
    public static interface RelationTrace {

        /**
         * Get the trace of relation.
         * @return The trace of relation as the list of relation info. (NotNull)
         */
        List<RelationInfo> getTraceRelation();

        /**
         * Get the trace of column.
         * @return The trace of column as column info. (NullAllowed)
         */
        ColumnInfo getTraceColumn();
    }

    public static interface RelationTraceFixHandler {
        void handleFixedTrace(RelationTrace relationTrace);
    }

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    /**
     * Has identity?
     * @return Determination.
     */
    boolean hasIdentity();

    // ===================================================================================
    //                                                                       Sequence Info
    //                                                                       =============
    /**
     * Has sequence?
     * @return Determination.
     */
    boolean hasSequence();

    /**
     * Get the sequence name.
     * @return The sequence name. (NullAllowed: If it does not have sequence, returns null.)
     */
    String getSequenceName();

    /**
     * Get the SQL for next value of sequence.
     * @return The SQL for next value of sequence. (NullAllowed: If it does not have sequence, returns null.)
     */
    String getSequenceNextValSql();

    /**
     * Get the increment size of sequence.
     * @return The increment size of sequence. (NullAllowed: If it is unknown, returns null.)
     */
    Integer getSequenceIncrementSize();

    /**
     * Get the cache size of sequence. (The cache means sequence cache on DBFlute)
     * @return The cache size of sequence. (NullAllowed: If it does not use cache, returns null.)
     */
    Integer getSequenceCacheSize();

    // ===================================================================================
    //                                                                Optimistic Lock Info
    //                                                                ====================
    /**
     * Does the table have a column for version no?
     * @return Determination.
     */
    boolean hasVersionNo();

    /**
     * Get the column information of version no.
     * @return The column information of version no. (NullAllowed: If it doesn't have the column, return null.)
     */
    ColumnInfo getVersionNoColumnInfo();

    /**
     * Does the table have a column for update date?
     * @return Determination.
     */
    boolean hasUpdateDate();

    /**
     * Get the column information of update date.
     * @return The column information of update date. (NullAllowed: If it doesn't have the column, return null.)
     */
    ColumnInfo getUpdateDateColumnInfo();

    // ===================================================================================
    //                                                                  Common Column Info
    //                                                                  ==================
    /**
     * Does the table have common columns?
     * @return Determination.
     */
    boolean hasCommonColumn();

    /**
     * Get the list of common column.
     * @return The list of column info. (NotNull)
     */
    List<ColumnInfo> getCommonColumnInfoList();

    /**
     * Get the list of common column auto-setup before insert.
     * @return The list of column info. (NotNull)
     */
    List<ColumnInfo> getCommonColumnInfoBeforeInsertList();

    /**
     * Get the list of common column auto-setup before update.
     * @return The list of column info. (NotNull)
     */
    List<ColumnInfo> getCommonColumnInfoBeforeUpdateList();

    // ===================================================================================
    //                                                                       Name Handling
    //                                                                       =============
    /**
     * Does the table have an object for the flexible name? {Target objects are TABLE and COLUMN}
     * @param flexibleName The flexible name. (NotNull and NotEmpty)
     * @return Determination.
     */
    boolean hasFlexibleName(String flexibleName);

    /**
     * Find DB name by flexible name. {Target objects are TABLE and COLUMN}
     * @param flexibleName The flexible name. (NotNull and NotEmpty)
     * @return The DB name of anything. (NotNull and NotEmpty)
     */
    String findDbName(String flexibleName);

    /**
     * Find property name(JavaBeansRule) by flexible name. {Target objects are TABLE and COLUMN}
     * @param flexibleName The flexible name. (NotNull and NotEmpty)
     * @return The DB name of anything. (NotNull and NotEmpty)
     */
    String findPropertyName(String flexibleName);

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    /**
     * Get the type name of entity.
     * @return The type name of entity. (NotNull)
     */
    String getEntityTypeName();

    /**
     * Get the type name of condition-bean.
     * @return The type name of condition-bean. (NullAllowed: If the condition-bean does not exist)
     */
    String getConditionBeanTypeName();

    /**
     * Get the type name of DAO.
     * @return The type name of DAO. (NullAllowed: If the DAO does not exist)
     */
    String getDaoTypeName();

    /**
     * Get the type name of behavior.
     * @return The type name of behavior. (NullAllowed: If the behavior does not exist)
     */
    String getBehaviorTypeName();

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    /**
     * Get the type of entity.
     * @return The type of entity. (NotNull)
     */
    Class<? extends Entity> getEntityType();

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    /**
     * New the instance of entity.
     * @return The instance of entity. (NotNull)
     */
    Entity newEntity();

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    /**
     * Accept the map of primary-keys. map:{[column-name] = [value]}
     * @param entity Target entity. (NotNull)
     * @param primaryKeyMap The value map of primary-keys. (NotNull and NotEmpty)
     */
    void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap);

    // -----------------------------------------------------
    //                                               Extract
    //                                               -------
    /**
     * Extract the map of primary-keys. map:{[column-name] = [value]}
     * @param entity Target entity. (NotNull)
     * @return The value map of primary-keys. (NotNull)
     */
    Map<String, Object> extractPrimaryKeyMap(Entity entity);

    /**
     * Extract The map of all columns. map:{[column-name] = [value]}
     * @param entity Target entity. (NotNull)
     * @return The map of all columns. (NotNull)
     */
    Map<String, Object> extractAllColumnMap(Entity entity);

    // ===================================================================================
    //                                                               Entity Property Setup
    //                                                               =====================
    // It's very INTERNAL!
    /**
     * Has the set-upper of entity property by the name of property? <br />
     * Comparing is so flexible. {Ignore cases and underscore}
     * @param propertyName The name of the property. (NotNull)
     * @return Determination.
     */
    boolean hasEntityPropertySetupper(String propertyName);

    /**
     * Set up entity property. (for INTERNAL)
     * @param propertyName The name of the property. (NotNull)
     * @param entity The entity for the property. (NotNull)
     * @param value The value of the property. (NullAllowed)
     */
    void setupEntityProperty(String propertyName, Object entity, Object value);

    /**
     * The set-upper of entity property. <br />
     * This class is for Internal. Don't use this!
     * @param <ENTITY_TYPE> The type of entity.
     */
    public interface Eps<ENTITY_TYPE extends Entity> {

        /**
         * @param entity Entity. (NotNull)
         * @param value Value. (NullAllowed)
         */
        void setup(ENTITY_TYPE entity, Object value);
    }

    // ===================================================================================
    //                                                                Optimistic Lock Type
    //                                                                ====================
    public static enum OptimisticLockType {
        NONE, VERSION_NO, UPDATE_DATE
    }
}
