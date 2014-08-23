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
import org.codelibs.robot.db.exentity.AccessResult;
import org.codelibs.robot.db.exentity.AccessResultData;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.dbmeta.name.TableSqlName;

/**
 * The DB meta of ACCESS_RESULT_DATA. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final AccessResultDataDbm _instance =
        new AccessResultDataDbm();

    private AccessResultDataDbm() {
    }

    public static AccessResultDataDbm getInstance() {
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
        setupEpg(_epgMap, new EpgTransformerName(), "transformerName");
        setupEpg(_epgMap, new EpgData(), "data");
        setupEpg(_epgMap, new EpgEncoding(), "encoding");
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultData) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultData) et).setId(ctl(vl));
        }
    }

    public static class EpgTransformerName implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultData) et).getTransformerName();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultData) et).setTransformerName((String) vl);
        }
    }

    public static class EpgData implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultData) et).getData();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultData) et).setData((byte[]) vl);
        }
    }

    public static class EpgEncoding implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultData) et).getEncoding();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultData) et).setEncoding((String) vl);
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    protected final Map<String, PropertyGateway> _efpgMap = newHashMap();
    {
        setupEfpg(_efpgMap, new EfpgAccessResult(), "accessResult");
    }

    public class EfpgAccessResult implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultData) et).getAccessResult();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultData) et).setAccessResult((AccessResult) vl);
        }
    }

    @Override
    public PropertyGateway findForeignPropertyGateway(final String prop) {
        return doFindEfpg(_efpgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
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
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci(
        "ID",
        "ID",
        null,
        null,
        Long.class,
        "id",
        null,
        true,
        false,
        true,
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
        String.class,
        "transformerName",
        null,
        false,
        false,
        true,
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
        byte[].class,
        "data",
        null,
        false,
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
        String.class,
        "encoding",
        null,
        false,
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

    /**
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnTransformerName() {
        return _columnTransformerName;
    }

    /**
     * DATA: {BLOB(2147483647)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnData() {
        return _columnData;
    }

    /**
     * ENCODING: {VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
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
    /**
     * ACCESS_RESULT by my ID, named 'accessResult'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignAccessResult() {
        final Map<ColumnInfo, ColumnInfo> mp =
            newLinkedHashMap(columnId(), AccessResultDbm
                .getInstance()
                .columnId());
        return cfi(
            "CONSTRAINT_13",
            "accessResult",
            this,
            AccessResultDbm.getInstance(),
            mp,
            0,
            null,
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
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "org.codelibs.robot.db.exentity.AccessResultData";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.robot.db.cbean.AccessResultDataCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.robot.db.exbhv.AccessResultDataBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<AccessResultData> getEntityType() {
        return AccessResultData.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public AccessResultData newMyEntity() {
        return new AccessResultData();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((AccessResultData) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((AccessResultData) et, mp);
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
