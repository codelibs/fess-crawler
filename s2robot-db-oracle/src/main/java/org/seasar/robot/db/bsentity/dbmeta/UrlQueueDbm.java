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
 * The DB meta of URL_QUEUE. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class UrlQueueDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final UrlQueueDbm _instance = new UrlQueueDbm();
    private UrlQueueDbm() {}
    public static UrlQueueDbm getInstance() { return _instance; }

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
        setupEpg(_epgMap, new EpgMethod(), "method");
        setupEpg(_epgMap, new EpgUrl(), "url");
        setupEpg(_epgMap, new EpgMetaData(), "metaData");
        setupEpg(_epgMap, new EpgEncoding(), "encoding");
        setupEpg(_epgMap, new EpgParentUrl(), "parentUrl");
        setupEpg(_epgMap, new EpgDepth(), "depth");
        setupEpg(_epgMap, new EpgLastModified(), "lastModified");
        setupEpg(_epgMap, new EpgCreateTime(), "createTime");
    }
    public static class EpgId implements PropertyGateway {
        public Object read(Entity et) { return ((UrlQueue)et).getId(); }
        public void write(Entity et, Object vl) { ((UrlQueue)et).setId(ctl(vl)); }
    }
    public static class EpgSessionId implements PropertyGateway {
        public Object read(Entity et) { return ((UrlQueue)et).getSessionId(); }
        public void write(Entity et, Object vl) { ((UrlQueue)et).setSessionId((String)vl); }
    }
    public static class EpgMethod implements PropertyGateway {
        public Object read(Entity et) { return ((UrlQueue)et).getMethod(); }
        public void write(Entity et, Object vl) { ((UrlQueue)et).setMethod((String)vl); }
    }
    public static class EpgUrl implements PropertyGateway {
        public Object read(Entity et) { return ((UrlQueue)et).getUrl(); }
        public void write(Entity et, Object vl) { ((UrlQueue)et).setUrl((String)vl); }
    }
    public static class EpgMetaData implements PropertyGateway {
        public Object read(Entity et) { return ((UrlQueue)et).getMetaData(); }
        public void write(Entity et, Object vl) { ((UrlQueue)et).setMetaData((String)vl); }
    }
    public static class EpgEncoding implements PropertyGateway {
        public Object read(Entity et) { return ((UrlQueue)et).getEncoding(); }
        public void write(Entity et, Object vl) { ((UrlQueue)et).setEncoding((String)vl); }
    }
    public static class EpgParentUrl implements PropertyGateway {
        public Object read(Entity et) { return ((UrlQueue)et).getParentUrl(); }
        public void write(Entity et, Object vl) { ((UrlQueue)et).setParentUrl((String)vl); }
    }
    public static class EpgDepth implements PropertyGateway {
        public Object read(Entity et) { return ((UrlQueue)et).getDepth(); }
        public void write(Entity et, Object vl) { ((UrlQueue)et).setDepth(cti(vl)); }
    }
    public static class EpgLastModified implements PropertyGateway {
        public Object read(Entity et) { return ((UrlQueue)et).getLastModified(); }
        public void write(Entity et, Object vl) { ((UrlQueue)et).setLastModified((java.sql.Timestamp)vl); }
    }
    public static class EpgCreateTime implements PropertyGateway {
        public Object read(Entity et) { return ((UrlQueue)et).getCreateTime(); }
        public void write(Entity et, Object vl) { ((UrlQueue)et).setCreateTime((java.sql.Timestamp)vl); }
    }
    public PropertyGateway findPropertyGateway(String prop)
    { return doFindEpg(_epgMap, prop); }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "URL_QUEUE";
    protected final String _tablePropertyName = "urlQueue";
    protected final TableSqlName _tableSqlName = new TableSqlName("URL_QUEUE", _tableDbName);
    { _tableSqlName.xacceptFilter(DBFluteConfig.getInstance().getTableSqlNameFilter()); }
    public String getTableDbName() { return _tableDbName; }
    public String getTablePropertyName() { return _tablePropertyName; }
    public TableSqlName getTableSqlName() { return _tableSqlName; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("ID", "ID", null, null, Long.class, "id", null, true, false, true, "NUMBER", 19, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnSessionId = cci("SESSION_ID", "SESSION_ID", null, null, String.class, "sessionId", null, false, false, true, "VARCHAR2", 20, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnMethod = cci("METHOD", "METHOD", null, null, String.class, "method", null, false, false, true, "VARCHAR2", 10, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, String.class, "url", null, false, false, true, "VARCHAR2", 4000, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnMetaData = cci("META_DATA", "META_DATA", null, null, String.class, "metaData", null, false, false, false, "VARCHAR2", 4000, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnEncoding = cci("ENCODING", "ENCODING", null, null, String.class, "encoding", null, false, false, false, "VARCHAR2", 20, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnParentUrl = cci("PARENT_URL", "PARENT_URL", null, null, String.class, "parentUrl", null, false, false, false, "VARCHAR2", 4000, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnDepth = cci("DEPTH", "DEPTH", null, null, Integer.class, "depth", null, false, false, true, "NUMBER", 5, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnLastModified = cci("LAST_MODIFIED", "LAST_MODIFIED", null, null, java.sql.Timestamp.class, "lastModified", null, false, false, false, "TIMESTAMP(6)", 11, 6, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnCreateTime = cci("CREATE_TIME", "CREATE_TIME", null, null, java.sql.Timestamp.class, "createTime", null, false, false, true, "TIMESTAMP(6)", 11, 6, null, false, null, null, null, null, null);

    /**
     * ID: {PK, NotNull, NUMBER(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() { return _columnId; }
    /**
     * SESSION_ID: {IX+, NotNull, VARCHAR2(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSessionId() { return _columnSessionId; }
    /**
     * METHOD: {NotNull, VARCHAR2(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMethod() { return _columnMethod; }
    /**
     * URL: {NotNull, VARCHAR2(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() { return _columnUrl; }
    /**
     * META_DATA: {VARCHAR2(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMetaData() { return _columnMetaData; }
    /**
     * ENCODING: {VARCHAR2(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnEncoding() { return _columnEncoding; }
    /**
     * PARENT_URL: {VARCHAR2(4000)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnParentUrl() { return _columnParentUrl; }
    /**
     * DEPTH: {NotNull, NUMBER(5)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnDepth() { return _columnDepth; }
    /**
     * LAST_MODIFIED: {TIMESTAMP(6)(11, 6)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnLastModified() { return _columnLastModified; }
    /**
     * CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreateTime() { return _columnCreateTime; }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSessionId());
        ls.add(columnMethod());
        ls.add(columnUrl());
        ls.add(columnMetaData());
        ls.add(columnEncoding());
        ls.add(columnParentUrl());
        ls.add(columnDepth());
        ls.add(columnLastModified());
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
    public boolean hasSequence() { return true; }
    public String getSequenceName() { return "URL_QUEUE_SEQ"; }
    public Integer getSequenceIncrementSize() { return 50; }
    public Integer getSequenceCacheSize() { return 50; }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "org.seasar.robot.db.exentity.UrlQueue"; }
    public String getConditionBeanTypeName() { return "org.seasar.robot.db.cbean.UrlQueueCB"; }
    public String getBehaviorTypeName() { return "org.seasar.robot.db.exbhv.UrlQueueBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<UrlQueue> getEntityType() { return UrlQueue.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public UrlQueue newMyEntity() { return new UrlQueue(); }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    public void acceptPrimaryKeyMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptPrimaryKeyMap((UrlQueue)et, mp); }
    public void acceptAllColumnMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptAllColumnMap((UrlQueue)et, mp); }
    public Map<String, Object> extractPrimaryKeyMap(Entity et) { return doExtractPrimaryKeyMap(et); }
    public Map<String, Object> extractAllColumnMap(Entity et) { return doExtractAllColumnMap(et); }
}
