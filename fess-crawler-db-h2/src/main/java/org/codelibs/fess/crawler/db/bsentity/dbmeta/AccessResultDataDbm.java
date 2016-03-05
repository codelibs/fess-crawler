/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.db.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.*;
import org.dbflute.dbmeta.name.*;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.codelibs.fess.crawler.db.allcommon.*;
import org.codelibs.fess.crawler.db.exentity.*;

/**
 * The DB meta of ACCESS_RESULT_DATA. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final AccessResultDataDbm _instance = new AccessResultDataDbm();
    private AccessResultDataDbm() {}
    public static AccessResultDataDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    public String getProjectName() { return DBCurrent.getInstance().projectName(); }
    public String getProjectPrefix() { return DBCurrent.getInstance().projectPrefix(); }
    public String getGenerationGapBasePrefix() { return DBCurrent.getInstance().generationGapBasePrefix(); }
    public DBDef getCurrentDBDef() { return DBCurrent.getInstance().currentDBDef(); }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    { xsetupEpg(); }
    protected void xsetupEpg() {
        setupEpg(_epgMap, et -> ((AccessResultData)et).getId(), (et, vl) -> ((AccessResultData)et).setId(ctl(vl)), "id");
        setupEpg(_epgMap, et -> ((AccessResultData)et).getTransformerName(), (et, vl) -> ((AccessResultData)et).setTransformerName((String)vl), "transformerName");
        setupEpg(_epgMap, et -> ((AccessResultData)et).getData(), (et, vl) -> ((AccessResultData)et).setData((byte[])vl), "data");
        setupEpg(_epgMap, et -> ((AccessResultData)et).getEncoding(), (et, vl) -> ((AccessResultData)et).setEncoding((String)vl), "encoding");
    }
    public PropertyGateway findPropertyGateway(String prop)
    { return doFindEpg(_epgMap, prop); }

    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    protected final Map<String, PropertyGateway> _efpgMap = newHashMap();
    { xsetupEfpg(); }
    @SuppressWarnings("unchecked")
    protected void xsetupEfpg() {
        setupEfpg(_efpgMap, et -> ((AccessResultData)et).getAccessResult(), (et, vl) -> ((AccessResultData)et).setAccessResult((OptionalEntity<AccessResult>)vl), "accessResult");
    }
    public PropertyGateway findForeignPropertyGateway(String prop)
    { return doFindEfpg(_efpgMap, prop); }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "ACCESS_RESULT_DATA";
    protected final String _tableDispName = "ACCESS_RESULT_DATA";
    protected final String _tablePropertyName = "accessResultData";
    protected final TableSqlName _tableSqlName = new TableSqlName("ACCESS_RESULT_DATA", _tableDbName);
    { _tableSqlName.xacceptFilter(DBFluteConfig.getInstance().getTableSqlNameFilter()); }
    public String getTableDbName() { return _tableDbName; }
    public String getTableDispName() { return _tableDispName; }
    public String getTablePropertyName() { return _tablePropertyName; }
    public TableSqlName getTableSqlName() { return _tableSqlName; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("ID", "ID", null, null, Long.class, "id", null, true, false, true, "BIGINT", 19, 0, null, false, null, null, "accessResult", null, null, false);
    protected final ColumnInfo _columnTransformerName = cci("TRANSFORMER_NAME", "TRANSFORMER_NAME", null, null, String.class, "transformerName", null, false, false, true, "VARCHAR", 255, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnData = cci("DATA", "DATA", null, null, byte[].class, "data", null, false, false, false, "BLOB", 2147483647, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnEncoding = cci("ENCODING", "ENCODING", null, null, String.class, "encoding", null, false, false, false, "VARCHAR", 20, 0, null, false, null, null, null, null, null, false);

    /**
     * ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() { return _columnId; }
    /**
     * TRANSFORMER_NAME: {NotNull, VARCHAR(255)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnTransformerName() { return _columnTransformerName; }
    /**
     * DATA: {BLOB(2147483647)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnData() { return _columnData; }
    /**
     * ENCODING: {VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnEncoding() { return _columnEncoding; }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnTransformerName());
        ls.add(columnData());
        ls.add(columnEncoding());
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
    /**
     * ACCESS_RESULT by my ID, named 'accessResult'.
     * @return The information object of foreign property. (NotNull)
     */
    public ForeignInfo foreignAccessResult() {
        Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(), AccessResultDbm.getInstance().columnId());
        return cfi("CONSTRAINT_13", "accessResult", this, AccessResultDbm.getInstance(), mp, 0, org.dbflute.optional.OptionalEntity.class, true, false, false, false, null, null, false, "accessResultDataAsOne", false);
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
    public String getEntityTypeName() { return "org.codelibs.fess.crawler.db.exentity.AccessResultData"; }
    public String getConditionBeanTypeName() { return "org.codelibs.fess.crawler.db.cbean.AccessResultDataCB"; }
    public String getBehaviorTypeName() { return "org.codelibs.fess.crawler.db.exbhv.AccessResultDataBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<AccessResultData> getEntityType() { return AccessResultData.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public AccessResultData newEntity() { return new AccessResultData(); }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    public void acceptPrimaryKeyMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptPrimaryKeyMap((AccessResultData)et, mp); }
    public void acceptAllColumnMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptAllColumnMap((AccessResultData)et, mp); }
    public Map<String, Object> extractPrimaryKeyMap(Entity et) { return doExtractPrimaryKeyMap(et); }
    public Map<String, Object> extractAllColumnMap(Entity et) { return doExtractAllColumnMap(et); }
}
