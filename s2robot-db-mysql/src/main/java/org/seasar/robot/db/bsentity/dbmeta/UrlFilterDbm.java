/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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

import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.*;
import org.seasar.dbflute.dbmeta.name.*;
import org.seasar.robot.db.allcommon.*;
import org.seasar.robot.db.exentity.*;

/**
 * The DB meta of URL_FILTER. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class UrlFilterDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final UrlFilterDbm _instance = new UrlFilterDbm();
    private UrlFilterDbm() {}
    public static UrlFilterDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    public DBDef getCurrentDBDef() { return DBCurrent.getInstance().currentDBDef(); }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, new EpgId(), "id");
        setupEpg(_epgMap, new EpgSessionId(), "sessionId");
        setupEpg(_epgMap, new EpgUrl(), "url");
        setupEpg(_epgMap, new EpgFilterType(), "filterType");
        setupEpg(_epgMap, new EpgCreateTime(), "createTime");
    }
    public static class EpgId implements PropertyGateway {
        public Object read(Entity et) { return ((UrlFilter)et).getId(); }
        public void write(Entity et, Object vl) { ((UrlFilter)et).setId(ctl(vl)); }
    }
    public static class EpgSessionId implements PropertyGateway {
        public Object read(Entity et) { return ((UrlFilter)et).getSessionId(); }
        public void write(Entity et, Object vl) { ((UrlFilter)et).setSessionId((String)vl); }
    }
    public static class EpgUrl implements PropertyGateway {
        public Object read(Entity et) { return ((UrlFilter)et).getUrl(); }
        public void write(Entity et, Object vl) { ((UrlFilter)et).setUrl((String)vl); }
    }
    public static class EpgFilterType implements PropertyGateway {
        public Object read(Entity et) { return ((UrlFilter)et).getFilterType(); }
        public void write(Entity et, Object vl) { ((UrlFilter)et).setFilterType((String)vl); }
    }
    public static class EpgCreateTime implements PropertyGateway {
        public Object read(Entity et) { return ((UrlFilter)et).getCreateTime(); }
        public void write(Entity et, Object vl) { ((UrlFilter)et).setCreateTime((java.sql.Timestamp)vl); }
    }
    public PropertyGateway findPropertyGateway(String prop)
    { return doFindEpg(_epgMap, prop); }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "URL_FILTER";
    protected final String _tablePropertyName = "urlFilter";
    protected final TableSqlName _tableSqlName = new TableSqlName("URL_FILTER", _tableDbName);
    { _tableSqlName.xacceptFilter(DBFluteConfig.getInstance().getTableSqlNameFilter()); }
    public String getTableDbName() { return _tableDbName; }
    public String getTablePropertyName() { return _tablePropertyName; }
    public TableSqlName getTableSqlName() { return _tableSqlName; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("ID", "ID", null, null, Long.class, "id", null, true, true, true, "BIGINT", 19, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnSessionId = cci("SESSION_ID", "SESSION_ID", null, null, String.class, "sessionId", null, false, false, true, "VARCHAR", 20, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, String.class, "url", null, false, false, true, "TEXT", 65535, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnFilterType = cci("FILTER_TYPE", "FILTER_TYPE", null, null, String.class, "filterType", null, false, false, true, "VARCHAR", 1, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnCreateTime = cci("CREATE_TIME", "CREATE_TIME", null, null, java.sql.Timestamp.class, "createTime", null, false, false, true, "DATETIME", 19, 0, null, false, null, null, null, null, null);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() { return _columnId; }
    /**
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSessionId() { return _columnSessionId; }
    /**
     * URL: {NotNull, TEXT(65535)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() { return _columnUrl; }
    /**
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnFilterType() { return _columnFilterType; }
    /**
     * CREATE_TIME: {NotNull, DATETIME(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreateTime() { return _columnCreateTime; }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSessionId());
        ls.add(columnUrl());
        ls.add(columnFilterType());
        ls.add(columnCreateTime());
        return ls;
    }

    { initializeInformationResource(); }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    protected UniqueInfo cpui() { return hpcpui(columnId()); }
    public boolean hasPrimaryKey() { return true; }
    public boolean hasCompoundPrimaryKey() { return false; }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // cannot cache because it uses related DB meta instance while booting
    // (instead, cached by super's collection)
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    public boolean hasIdentity() { return true; }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "org.seasar.robot.db.exentity.UrlFilter"; }
    public String getConditionBeanTypeName() { return "org.seasar.robot.db.cbean.UrlFilterCB"; }
    public String getBehaviorTypeName() { return "org.seasar.robot.db.exbhv.UrlFilterBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<UrlFilter> getEntityType() { return UrlFilter.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public UrlFilter newMyEntity() { return new UrlFilter(); }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    public void acceptPrimaryKeyMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptPrimaryKeyMap((UrlFilter)et, mp); }
    public void acceptAllColumnMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptAllColumnMap((UrlFilter)et, mp); }
    public Map<String, Object> extractPrimaryKeyMap(Entity et) { return doExtractPrimaryKeyMap(et); }
    public Map<String, Object> extractAllColumnMap(Entity et) { return doExtractAllColumnMap(et); }
}
