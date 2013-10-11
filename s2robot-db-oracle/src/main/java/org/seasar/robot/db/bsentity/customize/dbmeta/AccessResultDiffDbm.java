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
package org.seasar.robot.db.bsentity.customize.dbmeta;

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
import org.seasar.robot.db.exentity.customize.AccessResultDiff;

/**
 * The DB meta of AccessResultDiff. (Singleton)
 * 
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDiffDbm extends AbstractDBMeta {

    // ===================================================================================
    // Singleton
    // =========
    private static final AccessResultDiffDbm _instance =
        new AccessResultDiffDbm();

    private AccessResultDiffDbm() {
    }

    public static AccessResultDiffDbm getInstance() {
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
        setupEpg(_epgMap, new EpgCreateTime(), "createTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String propertyName) {
        return doFindEpg(_epgMap, propertyName);
    }

    public static class EpgId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setId(ctb(v));
        }
    }

    public static class EpgSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getSessionId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setSessionId((String) v);
        }
    }

    public static class EpgRuleId implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getRuleId();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setRuleId((String) v);
        }
    }

    public static class EpgUrl implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getUrl();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setUrl((String) v);
        }
    }

    public static class EpgParentUrl implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getParentUrl();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setParentUrl((String) v);
        }
    }

    public static class EpgStatus implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getStatus();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setStatus(cti(v));
        }
    }

    public static class EpgHttpStatusCode implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getHttpStatusCode();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setHttpStatusCode(cti(v));
        }
    }

    public static class EpgMethod implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getMethod();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setMethod((String) v);
        }
    }

    public static class EpgMimeType implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getMimeType();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setMimeType((String) v);
        }
    }

    public static class EpgContentLength implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getContentLength();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setContentLength(ctb(v));
        }
    }

    public static class EpgExecutionTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getExecutionTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setExecutionTime(cti(v));
        }
    }

    public static class EpgCreateTime implements PropertyGateway {
        @Override
        public Object read(final Entity e) {
            return ((AccessResultDiff) e).getCreateTime();
        }

        @Override
        public void write(final Entity e, final Object v) {
            ((AccessResultDiff) e).setCreateTime((java.sql.Timestamp) v);
        }
    }

    // ===================================================================================
    // Table Info
    // ==========
    protected final String _tableDbName = "AccessResultDiff";

    protected final String _tablePropertyName = "accessResultDiff";

    protected final TableSqlName _tableSqlName = new TableSqlName(
        "AccessResultDiff",
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
        false,
        "id",
        java.math.BigDecimal.class,
        false,
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
        false,
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

    protected final ColumnInfo _columnUrl = cci(
        "URL",
        "URL",
        null,
        null,
        false,
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

    protected final ColumnInfo _columnStatus = cci(
        "STATUS",
        "STATUS",
        null,
        null,
        false,
        "status",
        Integer.class,
        false,
        false,
        "NUMBER",
        4,
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
        false,
        "httpStatusCode",
        Integer.class,
        false,
        false,
        "NUMBER",
        4,
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
        false,
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

    protected final ColumnInfo _columnMimeType = cci(
        "MIME_TYPE",
        "MIME_TYPE",
        null,
        null,
        false,
        "mimeType",
        String.class,
        false,
        false,
        "VARCHAR2",
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
        false,
        "contentLength",
        java.math.BigDecimal.class,
        false,
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

    protected final ColumnInfo _columnExecutionTime = cci(
        "EXECUTION_TIME",
        "EXECUTION_TIME",
        null,
        null,
        false,
        "executionTime",
        Integer.class,
        false,
        false,
        "NUMBER",
        9,
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
        false,
        "createTime",
        java.sql.Timestamp.class,
        false,
        false,
        "TIMESTAMP",
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
        throw new UnsupportedOperationException(
            "The table does not have primary key: " + getTableDbName());
    }

    @Override
    public boolean hasPrimaryKey() {
        return false;
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

    // ===================================================================================
    // Type Name
    // =========
    @Override
    public String getEntityTypeName() {
        return "org.seasar.robot.db.exentity.customize.AccessResultDiff";
    }

    @Override
    public String getConditionBeanTypeName() {
        return null;
    }

    @Override
    public String getBehaviorTypeName() {
        return null;
    }

    // ===================================================================================
    // Object Type
    // ===========
    @Override
    public Class<AccessResultDiff> getEntityType() {
        return AccessResultDiff.class;
    }

    // ===================================================================================
    // Object Instance
    // ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public AccessResultDiff newMyEntity() {
        return new AccessResultDiff();
    }

    // ===================================================================================
    // Map Communication
    // =================
    @Override
    public void acceptPrimaryKeyMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((AccessResultDiff) e, m);
    }

    @Override
    public void acceptAllColumnMap(final Entity e,
            final Map<String, ? extends Object> m) {
        doAcceptAllColumnMap((AccessResultDiff) e, m);
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
