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
import org.seasar.robot.db.exentity.AccessResultData;
import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.robot.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.robot.dbflute.dbmeta.name.TableSqlName;
import org.seasar.robot.dbflute.helper.StringKeyMap;

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
    public DBDef getCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
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
        "LONGBLOB",
        2147483647,
        0,
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
            "accessResult",
            this,
            AccessResultDbm.getInstance(),
            map,
            0,
            true,
            false);
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
    public String getEntityTypeName() {
        return "org.seasar.robot.db.exentity.AccessResultData";
    }

    public String getConditionBeanTypeName() {
        return "org.seasar.robot.db.cbean.bs.AccessResultDataCB";
    }

    public String getDaoTypeName() {
        return "org.seasar.robot.db.exdao.AccessResultDataDao";
    }

    public String getBehaviorTypeName() {
        return "org.seasar.robot.db.exbhv.AccessResultDataBhv";
    }

    // ===================================================================================
    // Object Type
    // ===========
    public Class<AccessResultData> getEntityType() {
        return AccessResultData.class;
    }

    // ===================================================================================
    // Object Instance
    // ===============
    public Entity newEntity() {
        return newMyEntity();
    }

    public AccessResultData newMyEntity() {
        return new AccessResultData();
    }

    // ===================================================================================
    // Entity Handling
    // ===============
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((AccessResultData) e, m, _epsMap);
    }

    public Map<String, Object> extractPrimaryKeyMap(final Entity e) {
        return doExtractPrimaryKeyMap(e);
    }

    public Map<String, Object> extractAllColumnMap(final Entity e) {
        return doExtractAllColumnMap(e);
    }

    // ===================================================================================
    // Entity Property Setup
    // =====================
    // It's very INTERNAL!
    protected final Map<String, Eps<AccessResultData>> _epsMap = StringKeyMap
        .createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsId(), columnId());
        setupEps(_epsMap, new EpsTransformerName(), columnTransformerName());
        setupEps(_epsMap, new EpsData(), columnData());
        setupEps(_epsMap, new EpsEncoding(), columnEncoding());
    }

    public boolean hasEntityPropertySetupper(final String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(final String propertyName,
            final Object entity, final Object value) {
        findEps(_epsMap, propertyName).setup((AccessResultData) entity, value);
    }

    public class EpsId implements Eps<AccessResultData> {
        public void setup(final AccessResultData e, final Object v) {
            e.setId(ctl(v));
        }
    }

    public static class EpsTransformerName implements Eps<AccessResultData> {
        public void setup(final AccessResultData e, final Object v) {
            e.setTransformerName((String) v);
        }
    }

    public static class EpsData implements Eps<AccessResultData> {
        public void setup(final AccessResultData e, final Object v) {
            e.setData((byte[]) v);
        }
    }

    public static class EpsEncoding implements Eps<AccessResultData> {
        public void setup(final AccessResultData e, final Object v) {
            e.setEncoding((String) v);
        }
    }
}
