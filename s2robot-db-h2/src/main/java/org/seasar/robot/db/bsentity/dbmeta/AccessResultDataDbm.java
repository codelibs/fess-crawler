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
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;
import org.seasar.robot.db.allcommon.DBCurrent;
import org.seasar.robot.db.allcommon.DBFluteConfig;
import org.seasar.robot.db.exentity.AccessResultData;

/**
 * The DB meta of ACCESS_RESULT_DATA. (Singleton)
 * 
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataDbm extends AbstractDBMeta {

    // ===================================================================================
    // Singleton
    // =========
    private static final AccessResultDataDbm _instance =
        new AccessResultDataDbm();

    private AccessResultDataDbm() {
    }

    public static AccessResultDataDbm getInstance() {
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
        setupEpg(_epgMap, new EpgTransformerName(), "transformerName");
        setupEpg(_epgMap, new EpgData(), "data");
        setupEpg(_epgMap, new EpgEncoding(), "encoding");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultData) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultData) e).setId(ctl(v));
        }
    }

    public static class EpgTransformerName implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultData) e).getTransformerName();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultData) e).setTransformerName((String) v);
        }
    }

    public static class EpgData implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultData) e).getData();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultData) e).setData((byte[]) v);
        }
    }

    public static class EpgEncoding implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultData) e).getEncoding();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultData) e).setEncoding((String) v);
        }
    }

    // ===================================================================================
    // Table Info
    // ==========
    protected final String _tableDbName = "ACCESS_RESULT_DATA";

    protected final String _tablePropertyName = "accessResultData";

    protected final TableSqlName _tableSqlName = new TableSqlName(
        "ACCESS_RESULT_DATA",
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
        "BIGINT",
        19,
        0,
        null,
        false,
        null,
        null,
        "accessResult",
        null,
        null);

    protected final ColumnInfo _columnTransformerName = cci(
        "TRANSFORMER_NAME",
        "TRANSFORMER_NAME",
        null,
        null,
        true,
        "transformerName",
        String.class,
        false,
        false,
        "VARCHAR",
        255,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnData = cci(
        "DATA",
        "DATA",
        null,
        null,
        false,
        "data",
        byte[].class,
        false,
        false,
        "BLOB",
        2147483647,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnEncoding = cci(
        "ENCODING",
        "ENCODING",
        null,
        null,
        false,
        "encoding",
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

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnTransformerName() {
        return _columnTransformerName;
    }

    public ColumnInfo columnData() {
        return _columnData;
    }

    public ColumnInfo columnEncoding() {
        return _columnEncoding;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnTransformerName());
        ls.add(columnData());
        ls.add(columnEncoding());
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
    public ForeignInfo foreignAccessResult() {
        final Map<ColumnInfo, ColumnInfo> map =
            newLinkedHashMap(columnId(), AccessResultDbm
                .getInstance()
                .columnId());
        return cfi(
            "CONSTRAINT_13",
            "accessResult",
            this,
            AccessResultDbm.getInstance(),
            map,
            0,
            true,
            false,
            false,
            false,
            null,
            null,
            false,
            "accessResultDataAsOne");
    }

    // -----------------------------------------------------
    // Referrer Property
    // -----------------

    // ===================================================================================
    // Various Info
    // ============

    // ===================================================================================
    // Type Name
    // =========
    @Override
    public String getEntityTypeName() {
        return "org.seasar.robot.db.exentity.AccessResultData";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.seasar.robot.db.cbean.AccessResultDataCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.seasar.robot.db.exbhv.AccessResultDataBhv";
    }

    // ===================================================================================
    // Object Type
    // ===========
    @Override
    public Class<AccessResultData> getEntityType() {
        return AccessResultData.class;
    }

    // ===================================================================================
    // Object Instance
    // ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public AccessResultData newMyEntity() {
        return new AccessResultData();
    }

    // ===================================================================================
    // Map Communication
    // =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((AccessResultData) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((AccessResultData) e, m);
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
