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
 * The DB meta of ACCESS_RESULT. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final AccessResultDbm _instance = new AccessResultDbm();
    private AccessResultDbm() {}
    public static AccessResultDbm getInstance() { return _instance; }

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
        setupEpg(_epgMap, new EpgRuleId(), "ruleId");
        setupEpg(_epgMap, new EpgUrl(), "url");
        setupEpg(_epgMap, new EpgParentUrl(), "parentUrl");
        setupEpg(_epgMap, new EpgStatus(), "status");
        setupEpg(_epgMap, new EpgHttpStatusCode(), "httpStatusCode");
        setupEpg(_epgMap, new EpgMethod(), "method");
        setupEpg(_epgMap, new EpgMimeType(), "mimeType");
        setupEpg(_epgMap, new EpgContentLength(), "contentLength");
        setupEpg(_epgMap, new EpgExecutionTime(), "executionTime");
        setupEpg(_epgMap, new EpgLastModified(), "lastModified");
        setupEpg(_epgMap, new EpgCreateTime(), "createTime");
    }
    public static class EpgId implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getId(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setId(ctl(vl)); }
    }
    public static class EpgSessionId implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getSessionId(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setSessionId((String)vl); }
    }
    public static class EpgRuleId implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getRuleId(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setRuleId((String)vl); }
    }
    public static class EpgUrl implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getUrl(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setUrl((String)vl); }
    }
    public static class EpgParentUrl implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getParentUrl(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setParentUrl((String)vl); }
    }
    public static class EpgStatus implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getStatus(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setStatus(cti(vl)); }
    }
    public static class EpgHttpStatusCode implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getHttpStatusCode(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setHttpStatusCode(cti(vl)); }
    }
    public static class EpgMethod implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getMethod(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setMethod((String)vl); }
    }
    public static class EpgMimeType implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getMimeType(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setMimeType((String)vl); }
    }
    public static class EpgContentLength implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getContentLength(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setContentLength(ctl(vl)); }
    }
    public static class EpgExecutionTime implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getExecutionTime(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setExecutionTime(cti(vl)); }
    }
    public static class EpgLastModified implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getLastModified(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setLastModified((java.sql.Timestamp)vl); }
    }
    public static class EpgCreateTime implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getCreateTime(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setCreateTime((java.sql.Timestamp)vl); }
    }
    public PropertyGateway findPropertyGateway(String prop)
    { return doFindEpg(_epgMap, prop); }

    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    protected final Map<String, PropertyGateway> _efpgMap = newHashMap();
    {
        setupEfpg(_efpgMap, new EfpgAccessResultDataAsOne(), "accessResultDataAsOne");
    }
    public class EfpgAccessResultDataAsOne implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResult)et).getAccessResultDataAsOne(); }
        public void write(Entity et, Object vl) { ((AccessResult)et).setAccessResultDataAsOne((AccessResultData)vl); }
    }
    public PropertyGateway findForeignPropertyGateway(String prop)
    { return doFindEfpg(_efpgMap, prop); }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "ACCESS_RESULT";
    protected final String _tablePropertyName = "accessResult";
    protected final TableSqlName _tableSqlName = new TableSqlName("ACCESS_RESULT", _tableDbName);
    { _tableSqlName.xacceptFilter(DBFluteConfig.getInstance().getTableSqlNameFilter()); }
    public String getTableDbName() { return _tableDbName; }
    public String getTablePropertyName() { return _tablePropertyName; }
    public TableSqlName getTableSqlName() { return _tableSqlName; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("ID", "ID", null, null, Long.class, "id", null, true, true, true, "BIGINT", 19, 0, null, false, null, null, null, "", null);
    protected final ColumnInfo _columnSessionId = cci("SESSION_ID", "SESSION_ID", null, null, String.class, "sessionId", null, false, false, true, "VARCHAR", 20, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnRuleId = cci("RULE_ID", "RULE_ID", null, null, String.class, "ruleId", null, false, false, false, "VARCHAR", 20, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, String.class, "url", null, false, false, true, "TEXT", 65535, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnParentUrl = cci("PARENT_URL", "PARENT_URL", null, null, String.class, "parentUrl", null, false, false, false, "TEXT", 65535, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnStatus = cci("STATUS", "STATUS", null, null, Integer.class, "status", null, false, false, true, "INT", 10, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnHttpStatusCode = cci("HTTP_STATUS_CODE", "HTTP_STATUS_CODE", null, null, Integer.class, "httpStatusCode", null, false, false, true, "INT", 10, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnMethod = cci("METHOD", "METHOD", null, null, String.class, "method", null, false, false, true, "VARCHAR", 10, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnMimeType = cci("MIME_TYPE", "MIME_TYPE", null, null, String.class, "mimeType", null, false, false, true, "VARCHAR", 100, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnContentLength = cci("CONTENT_LENGTH", "CONTENT_LENGTH", null, null, Long.class, "contentLength", null, false, false, true, "BIGINT", 19, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnExecutionTime = cci("EXECUTION_TIME", "EXECUTION_TIME", null, null, Integer.class, "executionTime", null, false, false, true, "INT", 10, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnLastModified = cci("LAST_MODIFIED", "LAST_MODIFIED", null, null, java.sql.Timestamp.class, "lastModified", null, false, false, true, "DATETIME", 19, 0, null, false, null, null, null, null, null);
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
     * RULE_ID: {VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnRuleId() { return _columnRuleId; }
    /**
     * URL: {IX+, NotNull, TEXT(65535)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() { return _columnUrl; }
    /**
     * PARENT_URL: {TEXT(65535)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnParentUrl() { return _columnParentUrl; }
    /**
     * STATUS: {NotNull, INT(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnStatus() { return _columnStatus; }
    /**
     * HTTP_STATUS_CODE: {NotNull, INT(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnHttpStatusCode() { return _columnHttpStatusCode; }
    /**
     * METHOD: {NotNull, VARCHAR(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMethod() { return _columnMethod; }
    /**
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMimeType() { return _columnMimeType; }
    /**
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnContentLength() { return _columnContentLength; }
    /**
     * EXECUTION_TIME: {NotNull, INT(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnExecutionTime() { return _columnExecutionTime; }
    /**
     * LAST_MODIFIED: {NotNull, DATETIME(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnLastModified() { return _columnLastModified; }
    /**
     * CREATE_TIME: {NotNull, DATETIME(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreateTime() { return _columnCreateTime; }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSessionId());
        ls.add(columnRuleId());
        ls.add(columnUrl());
        ls.add(columnParentUrl());
        ls.add(columnStatus());
        ls.add(columnHttpStatusCode());
        ls.add(columnMethod());
        ls.add(columnMimeType());
        ls.add(columnContentLength());
        ls.add(columnExecutionTime());
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
    /**
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * @return The information object of foreign property(referrer-as-one). (NotNull)
     */
    public ForeignInfo foreignAccessResultDataAsOne() {
        Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(), AccessResultDataDbm.getInstance().columnId());
        return cfi("ACCESS_RESULT_DATA_ibfk_1", "accessResultDataAsOne", this, AccessResultDataDbm.getInstance(), mp, 0, null, true, false, true, false, null, null, false, "accessResult");
    }

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
    public String getEntityTypeName() { return "org.seasar.robot.db.exentity.AccessResult"; }
    public String getConditionBeanTypeName() { return "org.seasar.robot.db.cbean.AccessResultCB"; }
    public String getBehaviorTypeName() { return "org.seasar.robot.db.exbhv.AccessResultBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<AccessResult> getEntityType() { return AccessResult.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public AccessResult newMyEntity() { return new AccessResult(); }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    public void acceptPrimaryKeyMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptPrimaryKeyMap((AccessResult)et, mp); }
    public void acceptAllColumnMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptAllColumnMap((AccessResult)et, mp); }
    public Map<String, Object> extractPrimaryKeyMap(Entity et) { return doExtractPrimaryKeyMap(et); }
    public Map<String, Object> extractAllColumnMap(Entity et) { return doExtractAllColumnMap(et); }
}
