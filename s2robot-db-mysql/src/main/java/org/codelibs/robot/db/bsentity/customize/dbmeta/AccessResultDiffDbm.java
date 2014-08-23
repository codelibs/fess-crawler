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
package org.codelibs.robot.db.bsentity.customize.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.robot.db.allcommon.*;
import org.codelibs.robot.db.exentity.customize.*;
import org.seasar.dbflute.DBDef;
import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.PropertyGateway;
import org.seasar.dbflute.dbmeta.info.*;
import org.seasar.dbflute.dbmeta.name.*;

/**
 * The DB meta of AccessResultDiff. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDiffDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final AccessResultDiffDbm _instance = new AccessResultDiffDbm();
    private AccessResultDiffDbm() {}
    public static AccessResultDiffDbm getInstance() { return _instance; }

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
        setupEpg(_epgMap, new EpgCreateTime(), "createTime");
    }
    public static class EpgId implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getId(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setId(ctl(vl)); }
    }
    public static class EpgSessionId implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getSessionId(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setSessionId((String)vl); }
    }
    public static class EpgRuleId implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getRuleId(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setRuleId((String)vl); }
    }
    public static class EpgUrl implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getUrl(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setUrl((String)vl); }
    }
    public static class EpgParentUrl implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getParentUrl(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setParentUrl((String)vl); }
    }
    public static class EpgStatus implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getStatus(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setStatus(cti(vl)); }
    }
    public static class EpgHttpStatusCode implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getHttpStatusCode(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setHttpStatusCode(cti(vl)); }
    }
    public static class EpgMethod implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getMethod(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setMethod((String)vl); }
    }
    public static class EpgMimeType implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getMimeType(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setMimeType((String)vl); }
    }
    public static class EpgContentLength implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getContentLength(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setContentLength(ctl(vl)); }
    }
    public static class EpgExecutionTime implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getExecutionTime(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setExecutionTime(cti(vl)); }
    }
    public static class EpgCreateTime implements PropertyGateway {
        public Object read(Entity et) { return ((AccessResultDiff)et).getCreateTime(); }
        public void write(Entity et, Object vl) { ((AccessResultDiff)et).setCreateTime((java.sql.Timestamp)vl); }
    }
    public PropertyGateway findPropertyGateway(String prop)
    { return doFindEpg(_epgMap, prop); }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "AccessResultDiff";
    protected final String _tablePropertyName = "accessResultDiff";
    protected final TableSqlName _tableSqlName = new TableSqlName("AccessResultDiff", _tableDbName);
    { _tableSqlName.xacceptFilter(DBFluteConfig.getInstance().getTableSqlNameFilter()); }
    public String getTableDbName() { return _tableDbName; }
    public String getTablePropertyName() { return _tablePropertyName; }
    public TableSqlName getTableSqlName() { return _tableSqlName; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("ID", "ID", null, null, Long.class, "id", null, false, false, false, "BIGINT", 20, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnSessionId = cci("SESSION_ID", "SESSION_ID", null, null, String.class, "sessionId", null, false, false, false, "VARCHAR", 20, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnRuleId = cci("RULE_ID", "RULE_ID", null, null, String.class, "ruleId", null, false, false, false, "VARCHAR", 20, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, String.class, "url", null, false, false, false, "VARCHAR", 21845, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnParentUrl = cci("PARENT_URL", "PARENT_URL", null, null, String.class, "parentUrl", null, false, false, false, "VARCHAR", 21845, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnStatus = cci("STATUS", "STATUS", null, null, Integer.class, "status", null, false, false, false, "INT", 11, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnHttpStatusCode = cci("HTTP_STATUS_CODE", "HTTP_STATUS_CODE", null, null, Integer.class, "httpStatusCode", null, false, false, false, "INT", 11, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnMethod = cci("METHOD", "METHOD", null, null, String.class, "method", null, false, false, false, "VARCHAR", 10, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnMimeType = cci("MIME_TYPE", "MIME_TYPE", null, null, String.class, "mimeType", null, false, false, false, "VARCHAR", 100, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnContentLength = cci("CONTENT_LENGTH", "CONTENT_LENGTH", null, null, Long.class, "contentLength", null, false, false, false, "BIGINT", 20, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnExecutionTime = cci("EXECUTION_TIME", "EXECUTION_TIME", null, null, Integer.class, "executionTime", null, false, false, false, "INT", 11, 0, null, false, null, null, null, null, null);
    protected final ColumnInfo _columnCreateTime = cci("CREATE_TIME", "CREATE_TIME", null, null, java.sql.Timestamp.class, "createTime", null, false, false, false, "DATETIME", 19, 0, null, false, null, null, null, null, null);

    /**
     * ID: {BIGINT(20), refers to ACCESS_RESULT.ID}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() { return _columnId; }
    /**
     * SESSION_ID: {VARCHAR(20), refers to ACCESS_RESULT.SESSION_ID}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSessionId() { return _columnSessionId; }
    /**
     * RULE_ID: {VARCHAR(20), refers to ACCESS_RESULT.RULE_ID}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnRuleId() { return _columnRuleId; }
    /**
     * URL: {VARCHAR(21845), refers to ACCESS_RESULT.URL}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() { return _columnUrl; }
    /**
     * PARENT_URL: {VARCHAR(21845), refers to ACCESS_RESULT.PARENT_URL}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnParentUrl() { return _columnParentUrl; }
    /**
     * STATUS: {INT(11), refers to ACCESS_RESULT.STATUS}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnStatus() { return _columnStatus; }
    /**
     * HTTP_STATUS_CODE: {INT(11), refers to ACCESS_RESULT.HTTP_STATUS_CODE}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnHttpStatusCode() { return _columnHttpStatusCode; }
    /**
     * METHOD: {VARCHAR(10), refers to ACCESS_RESULT.METHOD}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMethod() { return _columnMethod; }
    /**
     * MIME_TYPE: {VARCHAR(100), refers to ACCESS_RESULT.MIME_TYPE}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMimeType() { return _columnMimeType; }
    /**
     * CONTENT_LENGTH: {BIGINT(20), refers to ACCESS_RESULT.CONTENT_LENGTH}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnContentLength() { return _columnContentLength; }
    /**
     * EXECUTION_TIME: {INT(11), refers to ACCESS_RESULT.EXECUTION_TIME}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnExecutionTime() { return _columnExecutionTime; }
    /**
     * CREATE_TIME: {DATETIME(19), refers to ACCESS_RESULT.CREATE_TIME}
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
    protected UniqueInfo cpui() {
        throw new UnsupportedOperationException("The table does not have primary key: " + getTableDbName());
    }
    public boolean hasPrimaryKey() { return false; }
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

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "org.codelibs.robot.db.exentity.customize.AccessResultDiff"; }
    public String getConditionBeanTypeName() { return null; }
    public String getBehaviorTypeName() { return null; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<AccessResultDiff> getEntityType() { return AccessResultDiff.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() { return newMyEntity(); }
    public AccessResultDiff newMyEntity() { return new AccessResultDiff(); }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    public void acceptPrimaryKeyMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptPrimaryKeyMap((AccessResultDiff)et, mp); }
    public void acceptAllColumnMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptAllColumnMap((AccessResultDiff)et, mp); }
    public Map<String, Object> extractPrimaryKeyMap(Entity et) { return doExtractPrimaryKeyMap(et); }
    public Map<String, Object> extractAllColumnMap(Entity et) { return doExtractAllColumnMap(et); }
}
