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
package org.seasar.robot.db.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.robot.db.allcommon.DBCurrent;
import org.seasar.robot.db.allcommon.DBFluteConfig;
import org.seasar.robot.db.exentity.UrlFilter;
import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.robot.dbflute.dbmeta.name.TableSqlName;
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
    //                                                                       Current DBDef
    //                                                                       =============
    public DBDef getCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "URL_FILTER";

    protected final String _tablePropertyName = "urlFilter";

    protected final TableSqlName _tableSqlName = new TableSqlName("URL_FILTER",
            _tableDbName);
    {
        _tableSqlName.xacceptFilter(DBFluteConfig.getInstance()
                .getTableSqlNameFilter());
    }

    public String getTableDbName() {
        return _tableDbName;
    }

    public String getTablePropertyName() {
        return _tablePropertyName;
    }

    public TableSqlName getTableSqlName() {
        return _tableSqlName;
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("ID", "ID", null, null, true,
            "id", Long.class, true, false, "NUMBER", 12, 0, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnSessionId = cci("SESSION_ID",
            "SESSION_ID", null, null, true, "sessionId", String.class, false,
            false, "VARCHAR2", 20, 0, false, null, null, null, null, null);

    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, true,
            "url", String.class, false, false, "VARCHAR2", 4000, 0, false,
            null, null, null, null, null);

    protected final ColumnInfo _columnFilterType = cci("FILTER_TYPE",
            "FILTER_TYPE", null, null, true, "filterType", String.class, false,
            false, "VARCHAR2", 1, 0, false, null, null, null, null, null);

    protected final ColumnInfo _columnCreateTime = cci("CREATE_TIME",
            "CREATE_TIME", null, null, true, "createTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP(6)", 11, 6,
            false, null, null, null, null, null);

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

    public boolean hasCompoundPrimaryKey() {
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
    public boolean hasSequence() {
        return true;
    }

    public String getSequenceName() {
        return "URL_FILTER_SEQ";
    }

    public Integer getSequenceIncrementSize() {
        return 50;
    }

    public Integer getSequenceCacheSize() {
        return 50;
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
    public void acceptPrimaryKeyMap(Entity e, Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((UrlFilter) e, m, _epsMap);
    }

    public Map<String, Object> extractPrimaryKeyMap(Entity e) {
        return doExtractPrimaryKeyMap(e);
    }

    public Map<String, Object> extractAllColumnMap(Entity e) {
        return doExtractAllColumnMap(e);
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

    public class EpsId implements Eps<UrlFilter> {
        public void setup(UrlFilter e, Object v) {
            e.setId(ctl(v));
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
