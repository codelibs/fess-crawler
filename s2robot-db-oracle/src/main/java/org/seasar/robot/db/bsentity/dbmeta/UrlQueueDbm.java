/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;
import org.seasar.robot.db.allcommon.DBCurrent;
import org.seasar.robot.db.allcommon.DBFluteConfig;
import org.seasar.robot.db.exentity.UrlQueue;

/**
 * The DB meta of URL_QUEUE. (Singleton)
 * 
 * @author DBFlute(AutoGenerator)
 */
public class UrlQueueDbm extends AbstractDBMeta {

    // ===================================================================================
    // Singleton
    // =========
    private static final UrlQueueDbm _instance = new UrlQueueDbm();

    private UrlQueueDbm() {
    }

    public static UrlQueueDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    // Current DBDef
    // =============
    @Override
    public DBDef getCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    // ===================================================================================
    // Property Gateway
    // ================
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        setupEpg(_epgMap, new EpgId(), "id");
        setupEpg(_epgMap, new EpgSessionId(), "sessionId");
        setupEpg(_epgMap, new EpgMethod(), "method");
        setupEpg(_epgMap, new EpgUrl(), "url");
        setupEpg(_epgMap, new EpgParentUrl(), "parentUrl");
        setupEpg(_epgMap, new EpgDepth(), "depth");
        setupEpg(_epgMap, new EpgLastModified(), "lastModified");
        setupEpg(_epgMap, new EpgCreateTime(), "createTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlQueue) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlQueue) e).setId(ctl(v));
        }
    }

    public static class EpgSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlQueue) e).getSessionId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlQueue) e).setSessionId((String) v);
        }
    }

    public static class EpgMethod implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlQueue) e).getMethod();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlQueue) e).setMethod((String) v);
        }
    }

    public static class EpgUrl implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlQueue) e).getUrl();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlQueue) e).setUrl((String) v);
        }
    }

    public static class EpgParentUrl implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlQueue) e).getParentUrl();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlQueue) e).setParentUrl((String) v);
        }
    }

    public static class EpgDepth implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlQueue) e).getDepth();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlQueue) e).setDepth(cti(v));
        }
    }

    public static class EpgLastModified implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlQueue) e).getLastModified();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlQueue) e).setLastModified((java.sql.Timestamp) v);
        }
    }

    public static class EpgCreateTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlQueue) e).getCreateTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlQueue) e).setCreateTime((java.sql.Timestamp) v);
        }
    }

    // ===================================================================================
    // Table Info
    // ==========
    protected final String _tableDbName = "URL_QUEUE";

    protected final String _tablePropertyName = "urlQueue";

    protected final TableSqlName _tableSqlName = new TableSqlName(
        "URL_QUEUE",
        _tableDbName);
    {
        _tableSqlName.xacceptFilter(DBFluteConfig
            .getInstance()
            .getTableSqlNameFilter());
    }

    @Override
    public String getTableDbName() {
        return _tableDbName;
    }

    @Override
    public String getTablePropertyName() {
        return _tablePropertyName;
    }

    @Override
    public TableSqlName getTableSqlName() {
        return _tableSqlName;
    }

    // ===================================================================================
    // Column Info
    // ===========
    protected final ColumnInfo _columnId = cci(
        "ID",
        "ID",
        null,
        null,
        true,
        "id",
        Long.class,
        true,
        false,
        "NUMBER",
        19,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnSessionId = cci(
        "SESSION_ID",
        "SESSION_ID",
        null,
        null,
        true,
        "sessionId",
        String.class,
        false,
        false,
        "VARCHAR2",
        20,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnMethod = cci(
        "METHOD",
        "METHOD",
        null,
        null,
        true,
        "method",
        String.class,
        false,
        false,
        "VARCHAR2",
        10,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnUrl = cci(
        "URL",
        "URL",
        null,
        null,
        true,
        "url",
        String.class,
        false,
        false,
        "VARCHAR2",
        4000,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnParentUrl = cci(
        "PARENT_URL",
        "PARENT_URL",
        null,
        null,
        false,
        "parentUrl",
        String.class,
        false,
        false,
        "VARCHAR2",
        4000,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnDepth = cci(
        "DEPTH",
        "DEPTH",
        null,
        null,
        true,
        "depth",
        Integer.class,
        false,
        false,
        "NUMBER",
        5,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnLastModified = cci(
        "LAST_MODIFIED",
        "LAST_MODIFIED",
        null,
        null,
        false,
        "lastModified",
        java.sql.Timestamp.class,
        false,
        false,
        "TIMESTAMP(6)",
        11,
        6,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnCreateTime = cci(
        "CREATE_TIME",
        "CREATE_TIME",
        null,
        null,
        true,
        "createTime",
        java.sql.Timestamp.class,
        false,
        false,
        "TIMESTAMP(6)",
        11,
        6,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnSessionId() {
        return _columnSessionId;
    }

    public ColumnInfo columnMethod() {
        return _columnMethod;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    public ColumnInfo columnParentUrl() {
        return _columnParentUrl;
    }

    public ColumnInfo columnDepth() {
        return _columnDepth;
    }

    public ColumnInfo columnLastModified() {
        return _columnLastModified;
    }

    public ColumnInfo columnCreateTime() {
        return _columnCreateTime;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSessionId());
        ls.add(columnMethod());
        ls.add(columnUrl());
        ls.add(columnParentUrl());
        ls.add(columnDepth());
        ls.add(columnLastModified());
        ls.add(columnCreateTime());
        return ls;
    }

    {
        initializeInformationResource();
    }

    // ===================================================================================
    // Unique Info
    // ===========
    // -----------------------------------------------------
    // Primary Element
    // ---------------
    @Override
    protected UniqueInfo cpui() {
        return hpcpui(columnId());
    }

    @Override
    public boolean hasPrimaryKey() {
        return true;
    }

    @Override
    public boolean hasCompoundPrimaryKey() {
        return false;
    }

    // ===================================================================================
    // Relation Info
    // =============
    // -----------------------------------------------------
    // Foreign Property
    // ----------------

    // -----------------------------------------------------
    // Referrer Property
    // -----------------

    // ===================================================================================
    // Various Info
    // ============
    @Override
    public boolean hasSequence() {
        return true;
    }

    @Override
    public String getSequenceName() {
        return "URL_QUEUE_SEQ";
    }

    @Override
    public Integer getSequenceIncrementSize() {
        return 50;
    }

    @Override
    public Integer getSequenceCacheSize() {
        return 50;
    }

    // ===================================================================================
    // Type Name
    // =========
    @Override
    public String getEntityTypeName() {
        return "org.seasar.robot.db.exentity.UrlQueue";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.seasar.robot.db.cbean.UrlQueueCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.seasar.robot.db.exbhv.UrlQueueBhv";
    }

    // ===================================================================================
    // Object Type
    // ===========
    @Override
    public Class<UrlQueue> getEntityType() {
        return UrlQueue.class;
    }

    // ===================================================================================
    // Object Instance
    // ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public UrlQueue newMyEntity() {
        return new UrlQueue();
    }

    // ===================================================================================
    // Map Communication
    // =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((UrlQueue) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((UrlQueue) e, m);
    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(final Entity e) {
        return doExtractPrimaryKeyMap(e);
    }

    @Override
    public Map<String, Object> extractAllColumnMap(final Entity e) {
        return doExtractAllColumnMap(e);
    }
}
