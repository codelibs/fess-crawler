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
package org.seasar.robot.db.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.robot.db.exentity.UrlFilter;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.robot.dbflute.helper.StringKeyMap;

/**
 * The DB meta of URL_FILTER. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class UrlFilterDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final UrlFilterDbm _instance = new UrlFilterDbm();

    private UrlFilterDbm() {
    }

    public static UrlFilterDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() {
        return "URL_FILTER";
    }

    public String getTablePropertyName() {
        return "urlFilter";
    }

    public String getTableSqlName() {
        return "URL_FILTER";
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnId = cci("ID", null, "id", Long.class, true,
            true, 19, 0, false, null);

    protected ColumnInfo _columnSessionId = cci("SESSION_ID", null,
            "sessionId", String.class, false, false, 20, 0, false, null);

    protected ColumnInfo _columnUrl = cci("URL", null, "url", String.class,
            false, false, 65536, 0, false, null);

    protected ColumnInfo _columnFilterType = cci("FILTER_TYPE", null,
            "filterType", String.class, false, false, 1, 0, false, null);

    protected ColumnInfo _columnCreateTime = cci("CREATE_TIME", null,
            "createTime", java.sql.Timestamp.class, false, false, 23, 10,
            false, null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnSessionId() {
        return _columnSessionId;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    public ColumnInfo columnFilterType() {
        return _columnFilterType;
    }

    public ColumnInfo columnCreateTime() {
        return _columnCreateTime;
    }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSessionId());
        ls.add(columnUrl());
        ls.add(columnFilterType());
        ls.add(columnCreateTime());
        return ls;
    }

    {
        initializeInformationResource();
    }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    public UniqueInfo getPrimaryUniqueInfo() {
        return cpui(columnId());
    }

    public boolean hasPrimaryKey() {
        return true;
    }

    public boolean hasTwoOrMorePrimaryKeys() {
        return false;
    }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    public boolean hasIdentity() {
        return true;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() {
        return "org.seasar.robot.db.exentity.UrlFilter";
    }

    public String getConditionBeanTypeName() {
        return "org.seasar.robot.db.cbean.bs.UrlFilterCB";
    }

    public String getDaoTypeName() {
        return "org.seasar.robot.db.exdao.UrlFilterDao";
    }

    public String getBehaviorTypeName() {
        return "org.seasar.robot.db.exbhv.UrlFilterBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<UrlFilter> getEntityType() {
        return UrlFilter.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() {
        return newMyEntity();
    }

    public UrlFilter newMyEntity() {
        return new UrlFilter();
    }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity,
            Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((UrlFilter) entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity,
            String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity,
            Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((UrlFilter) entity, columnValueMap, _epsMap);
    }

    public void acceptColumnValueMapString(Entity entity,
            String columnValueMapString) {
        MapStringUtil.acceptColumnValueMapString(columnValueMapString, entity);
    }

    // -----------------------------------------------------
    //                                               Extract
    //                                               -------
    public String extractPrimaryKeyMapString(Entity entity) {
        return MapStringUtil.extractPrimaryKeyMapString(entity);
    }

    public String extractPrimaryKeyMapString(Entity entity, String startBrace,
            String endBrace, String delimiter, String equal) {
        return doExtractPrimaryKeyMapString(entity, startBrace, endBrace,
                delimiter, equal);
    }

    public String extractColumnValueMapString(Entity entity) {
        return MapStringUtil.extractColumnValueMapString(entity);
    }

    public String extractColumnValueMapString(Entity entity, String startBrace,
            String endBrace, String delimiter, String equal) {
        return doExtractColumnValueMapString(entity, startBrace, endBrace,
                delimiter, equal);
    }

    // -----------------------------------------------------
    //                                               Convert
    //                                               -------
    public List<Object> convertToColumnValueList(Entity entity) {
        return newArrayList(convertToColumnValueMap(entity).values());
    }

    public Map<String, Object> convertToColumnValueMap(Entity entity) {
        return doConvertToColumnValueMap(entity);
    }

    public List<String> convertToColumnStringValueList(Entity entity) {
        return newArrayList(convertToColumnStringValueMap(entity).values());
    }

    public Map<String, String> convertToColumnStringValueMap(Entity entity) {
        return doConvertToColumnStringValueMap(entity);
    }

    // ===================================================================================
    //                                                               Entity Property Setup
    //                                                               =====================
    // It's very INTERNAL!
    protected final Map<String, Eps<UrlFilter>> _epsMap = StringKeyMap
            .createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsId(), columnId());
        setupEps(_epsMap, new EpsSessionId(), columnSessionId());
        setupEps(_epsMap, new EpsUrl(), columnUrl());
        setupEps(_epsMap, new EpsFilterType(), columnFilterType());
        setupEps(_epsMap, new EpsCreateTime(), columnCreateTime());
    }

    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity,
            Object value) {
        findEps(_epsMap, propertyName).setup((UrlFilter) entity, value);
    }

    public static class EpsId implements Eps<UrlFilter> {
        public void setup(UrlFilter e, Object v) {
            e.setId((Long) v);
        }
    }

    public static class EpsSessionId implements Eps<UrlFilter> {
        public void setup(UrlFilter e, Object v) {
            e.setSessionId((String) v);
        }
    }

    public static class EpsUrl implements Eps<UrlFilter> {
        public void setup(UrlFilter e, Object v) {
            e.setUrl((String) v);
        }
    }

    public static class EpsFilterType implements Eps<UrlFilter> {
        public void setup(UrlFilter e, Object v) {
            e.setFilterType((String) v);
        }
    }

    public static class EpsCreateTime implements Eps<UrlFilter> {
        public void setup(UrlFilter e, Object v) {
            e.setCreateTime((java.sql.Timestamp) v);
        }
    }
}
