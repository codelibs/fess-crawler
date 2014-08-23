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
package org.codelibs.robot.db.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.robot.db.allcommon.DBCurrent;
import org.codelibs.robot.db.allcommon.DBFluteConfig;
import org.codelibs.robot.db.exentity.UrlFilter;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

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
    @Override
    public DBDef getCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

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
        @Override
        public Object read(final Entity et) {
            return ((UrlFilter) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((UrlFilter) et).setId(ctl(vl));
        }
    }

    public static class EpgSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((UrlFilter) et).getSessionId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((UrlFilter) et).setSessionId((String) vl);
        }
    }

    public static class EpgUrl implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((UrlFilter) et).getUrl();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((UrlFilter) et).setUrl((String) vl);
        }
    }

    public static class EpgFilterType implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((UrlFilter) et).getFilterType();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((UrlFilter) et).setFilterType((String) vl);
        }
    }

    public static class EpgCreateTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((UrlFilter) et).getCreateTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((UrlFilter) et).setCreateTime((java.sql.Timestamp) vl);
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
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
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId =
        cci(
            "ID",
            "ID",
            null,
            null,
            Long.class,
            "id",
            null,
            true,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_293A77F6_0984_4A81_9565_24CE6C668E7C",
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
        String.class,
        "sessionId",
        null,
        false,
        false,
        true,
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
        String.class,
        "url",
        null,
        false,
        false,
        true,
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
        String.class,
        "filterType",
        null,
        false,
        false,
        true,
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
        java.sql.Timestamp.class,
        "createTime",
        null,
        false,
        false,
        true,
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

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSessionId() {
        return _columnSessionId;
    }

    /**
     * URL: {NotNull, VARCHAR(65536)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    /**
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnFilterType() {
        return _columnFilterType;
    }

    /**
     * CREATE_TIME: {NotNull, TIMESTAMP(23, 10)}
     * @return The information object of specified column. (NotNull)
     */
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
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
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
    @Override
    public boolean hasIdentity() {
        return true;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "org.codelibs.robot.db.exentity.UrlFilter";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.robot.db.cbean.UrlFilterCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.robot.db.exbhv.UrlFilterBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<UrlFilter> getEntityType() {
        return UrlFilter.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public UrlFilter newMyEntity() {
        return new UrlFilter();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((UrlFilter) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((UrlFilter) et, mp);
    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(final Entity et) {
        return doExtractPrimaryKeyMap(et);
    }

    @Override
    public Map<String, Object> extractAllColumnMap(final Entity et) {
        return doExtractAllColumnMap(et);
    }
}
