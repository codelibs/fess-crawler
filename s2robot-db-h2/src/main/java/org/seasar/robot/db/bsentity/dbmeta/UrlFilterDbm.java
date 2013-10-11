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
import org.seasar.robot.db.exentity.UrlFilter;

/**
 * The DB meta of URL_FILTER. (Singleton)
 * 
 * @author DBFlute(AutoGenerator)
 */
public class UrlFilterDbm extends AbstractDBMeta {

    // ===================================================================================
    // Singleton
    // =========
    private static final UrlFilterDbm _instance = new UrlFilterDbm();

    private UrlFilterDbm() {
    }

    public static UrlFilterDbm getInstance() {
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
        setupEpg(_epgMap, new EpgUrl(), "url");
        setupEpg(_epgMap, new EpgFilterType(), "filterType");
        setupEpg(_epgMap, new EpgCreateTime(), "createTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlFilter) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlFilter) e).setId(ctl(v));
        }
    }

    public static class EpgSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlFilter) e).getSessionId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlFilter) e).setSessionId((String) v);
        }
    }

    public static class EpgUrl implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlFilter) e).getUrl();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlFilter) e).setUrl((String) v);
        }
    }

    public static class EpgFilterType implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlFilter) e).getFilterType();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlFilter) e).setFilterType((String) v);
        }
    }

    public static class EpgCreateTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((UrlFilter) e).getCreateTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((UrlFilter) e).setCreateTime((java.sql.Timestamp) v);
        }
    }

    // ===================================================================================
    // Table Info
    // ==========
    protected final String _tableDbName = "URL_FILTER";

    protected final String _tablePropertyName = "urlFilter";

    protected final TableSqlName _tableSqlName = new TableSqlName(
        "URL_FILTER",
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
    protected final ColumnInfo _columnId =
        cci(
            "ID",
            "ID",
            null,
            null,
            true,
            "id",
            Long.class,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_223573E1_578E_4E43_A97C_C74F44019976",
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
        "VARCHAR",
        20,
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
        "VARCHAR",
        65536,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnFilterType = cci(
        "FILTER_TYPE",
        "FILTER_TYPE",
        null,
        null,
        true,
        "filterType",
        String.class,
        false,
        false,
        "VARCHAR",
        1,
        0,
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
        "TIMESTAMP",
        23,
        10,
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

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    public ColumnInfo columnFilterType() {
        return _columnFilterType;
    }

    public ColumnInfo columnCreateTime() {
        return _columnCreateTime;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
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
    public boolean hasIdentity() {
        return true;
    }

    // ===================================================================================
    // Type Name
    // =========
    @Override
    public String getEntityTypeName() {
        return "org.seasar.robot.db.exentity.UrlFilter";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.seasar.robot.db.cbean.UrlFilterCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.seasar.robot.db.exbhv.UrlFilterBhv";
    }

    // ===================================================================================
    // Object Type
    // ===========
    @Override
    public Class<UrlFilter> getEntityType() {
        return UrlFilter.class;
    }

    // ===================================================================================
    // Object Instance
    // ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public UrlFilter newMyEntity() {
        return new UrlFilter();
    }

    // ===================================================================================
    // Map Communication
    // =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((UrlFilter) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((UrlFilter) e, m);
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
