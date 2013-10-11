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
import org.seasar.robot.db.exentity.AccessResult;

/**
 * The DB meta of ACCESS_RESULT. (Singleton)
 * 
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDbm extends AbstractDBMeta {

    // ===================================================================================
    // Singleton
    // =========
    private static final AccessResultDbm _instance = new AccessResultDbm();

    private AccessResultDbm() {
    }

    public static AccessResultDbm getInstance() {
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

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setId(ctl(v));
        }
    }

    public static class EpgSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getSessionId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setSessionId((String) v);
        }
    }

    public static class EpgRuleId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getRuleId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setRuleId((String) v);
        }
    }

    public static class EpgUrl implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getUrl();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setUrl((String) v);
        }
    }

    public static class EpgParentUrl implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getParentUrl();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setParentUrl((String) v);
        }
    }

    public static class EpgStatus implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getStatus();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setStatus(cti(v));
        }
    }

    public static class EpgHttpStatusCode implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getHttpStatusCode();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setHttpStatusCode(cti(v));
        }
    }

    public static class EpgMethod implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getMethod();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setMethod((String) v);
        }
    }

    public static class EpgMimeType implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getMimeType();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setMimeType((String) v);
        }
    }

    public static class EpgContentLength implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getContentLength();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setContentLength(ctl(v));
        }
    }

    public static class EpgExecutionTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getExecutionTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setExecutionTime(cti(v));
        }
    }

    public static class EpgLastModified implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getLastModified();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setLastModified((java.sql.Timestamp) v);
        }
    }

    public static class EpgCreateTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResult) e).getCreateTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResult) e).setCreateTime((java.sql.Timestamp) v);
        }
    }

    // ===================================================================================
    // Table Info
    // ==========
    protected final String _tableDbName = "ACCESS_RESULT";

    protected final String _tablePropertyName = "accessResult";

    protected final TableSqlName _tableSqlName = new TableSqlName(
        "ACCESS_RESULT",
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
        true,
        "BIGINT",
        19,
        0,
        null,
        false,
        null,
        null,
        null,
        "",
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

    protected final ColumnInfo _columnRuleId = cci(
        "RULE_ID",
        "RULE_ID",
        null,
        null,
        false,
        "ruleId",
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
        "TEXT",
        65535,
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
        "TEXT",
        65535,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnStatus = cci(
        "STATUS",
        "STATUS",
        null,
        null,
        true,
        "status",
        Integer.class,
        false,
        false,
        "INT",
        10,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnHttpStatusCode = cci(
        "HTTP_STATUS_CODE",
        "HTTP_STATUS_CODE",
        null,
        null,
        true,
        "httpStatusCode",
        Integer.class,
        false,
        false,
        "INT",
        10,
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
        "VARCHAR",
        10,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnMimeType = cci(
        "MIME_TYPE",
        "MIME_TYPE",
        null,
        null,
        true,
        "mimeType",
        String.class,
        false,
        false,
        "VARCHAR",
        100,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnContentLength = cci(
        "CONTENT_LENGTH",
        "CONTENT_LENGTH",
        null,
        null,
        true,
        "contentLength",
        Long.class,
        false,
        false,
        "BIGINT",
        19,
        0,
        null,
        false,
        null,
        null,
        null,
        null,
        null);

    protected final ColumnInfo _columnExecutionTime = cci(
        "EXECUTION_TIME",
        "EXECUTION_TIME",
        null,
        null,
        true,
        "executionTime",
        Integer.class,
        false,
        false,
        "INT",
        10,
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
        true,
        "lastModified",
        java.sql.Timestamp.class,
        false,
        false,
        "DATETIME",
        19,
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
        "DATETIME",
        19,
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

    public ColumnInfo columnSessionId() {
        return _columnSessionId;
    }

    public ColumnInfo columnRuleId() {
        return _columnRuleId;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    public ColumnInfo columnParentUrl() {
        return _columnParentUrl;
    }

    public ColumnInfo columnStatus() {
        return _columnStatus;
    }

    public ColumnInfo columnHttpStatusCode() {
        return _columnHttpStatusCode;
    }

    public ColumnInfo columnMethod() {
        return _columnMethod;
    }

    public ColumnInfo columnMimeType() {
        return _columnMimeType;
    }

    public ColumnInfo columnContentLength() {
        return _columnContentLength;
    }

    public ColumnInfo columnExecutionTime() {
        return _columnExecutionTime;
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
    public ForeignInfo foreignAccessResultDataAsOne() {
        final Map<ColumnInfo, ColumnInfo> map =
            newLinkedHashMap(columnId(), AccessResultDataDbm
                .getInstance()
                .columnId());
        return cfi(
            "ACCESS_RESULT_DATA_ibfk_1",
            "accessResultDataAsOne",
            this,
            AccessResultDataDbm.getInstance(),
            map,
            0,
            true,
            false,
            true,
            false,
            null,
            null,
            false,
            "accessResult");
    }

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
        return "org.seasar.robot.db.exentity.AccessResult";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.seasar.robot.db.cbean.AccessResultCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.seasar.robot.db.exbhv.AccessResultBhv";
    }

    // ===================================================================================
    // Object Type
    // ===========
    @Override
    public Class<AccessResult> getEntityType() {
        return AccessResult.class;
    }

    // ===================================================================================
    // Object Instance
    // ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public AccessResult newMyEntity() {
        return new AccessResult();
    }

    // ===================================================================================
    // Map Communication
    // =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((AccessResult) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((AccessResult) e, m);
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
