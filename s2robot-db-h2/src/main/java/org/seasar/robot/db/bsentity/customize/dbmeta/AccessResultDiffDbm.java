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
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDiffDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final AccessResultDiffDbm _instance =
        new AccessResultDiffDbm();

    private AccessResultDiffDbm() {
    }

    public static AccessResultDiffDbm getInstance() {
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
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setId(ctl(vl));
        }
    }

    public static class EpgSessionId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getSessionId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setSessionId((String) vl);
        }
    }

    public static class EpgRuleId implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getRuleId();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setRuleId((String) vl);
        }
    }

    public static class EpgUrl implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getUrl();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setUrl((String) vl);
        }
    }

    public static class EpgParentUrl implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getParentUrl();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setParentUrl((String) vl);
        }
    }

    public static class EpgStatus implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getStatus();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setStatus(cti(vl));
        }
    }

    public static class EpgHttpStatusCode implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getHttpStatusCode();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setHttpStatusCode(cti(vl));
        }
    }

    public static class EpgMethod implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getMethod();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setMethod((String) vl);
        }
    }

    public static class EpgMimeType implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getMimeType();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setMimeType((String) vl);
        }
    }

    public static class EpgContentLength implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getContentLength();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setContentLength(ctl(vl));
        }
    }

    public static class EpgExecutionTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getExecutionTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setExecutionTime(cti(vl));
        }
    }

    public static class EpgCreateTime implements PropertyGateway {
        @Override
        public Object read(final Entity et) {
            return ((AccessResultDiff) et).getCreateTime();
        }

        @Override
        public void write(final Entity et, final Object vl) {
            ((AccessResultDiff) et).setCreateTime((java.sql.Timestamp) vl);
        }
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
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
        false,
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
        String.class,
        "ruleId",
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

    protected final ColumnInfo _columnParentUrl = cci(
        "PARENT_URL",
        "PARENT_URL",
        null,
        null,
        String.class,
        "parentUrl",
        null,
        false,
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

    protected final ColumnInfo _columnStatus = cci(
        "STATUS",
        "STATUS",
        null,
        null,
        Integer.class,
        "status",
        null,
        false,
        false,
        false,
        "INTEGER",
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
        Integer.class,
        "httpStatusCode",
        null,
        false,
        false,
        false,
        "INTEGER",
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
        String.class,
        "method",
        null,
        false,
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
        String.class,
        "mimeType",
        null,
        false,
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
        Long.class,
        "contentLength",
        null,
        false,
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
        Integer.class,
        "executionTime",
        null,
        false,
        false,
        false,
        "INTEGER",
        10,
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

    /**
     * ID: {BIGINT(19), refers to ACCESS_RESULT.ID}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * SESSION_ID: {VARCHAR(20), refers to ACCESS_RESULT.SESSION_ID}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSessionId() {
        return _columnSessionId;
    }

    /**
     * RULE_ID: {VARCHAR(20), refers to ACCESS_RESULT.RULE_ID}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnRuleId() {
        return _columnRuleId;
    }

    /**
     * URL: {VARCHAR(65536), refers to ACCESS_RESULT.URL}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    /**
     * PARENT_URL: {VARCHAR(65536), refers to ACCESS_RESULT.PARENT_URL}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnParentUrl() {
        return _columnParentUrl;
    }

    /**
     * STATUS: {INTEGER(10), refers to ACCESS_RESULT.STATUS}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnStatus() {
        return _columnStatus;
    }

    /**
     * HTTP_STATUS_CODE: {INTEGER(10), refers to ACCESS_RESULT.HTTP_STATUS_CODE}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnHttpStatusCode() {
        return _columnHttpStatusCode;
    }

    /**
     * METHOD: {VARCHAR(10), refers to ACCESS_RESULT.METHOD}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMethod() {
        return _columnMethod;
    }

    /**
     * MIME_TYPE: {VARCHAR(100), refers to ACCESS_RESULT.MIME_TYPE}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMimeType() {
        return _columnMimeType;
    }

    /**
     * CONTENT_LENGTH: {BIGINT(19), refers to ACCESS_RESULT.CONTENT_LENGTH}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnContentLength() {
        return _columnContentLength;
    }

    /**
     * EXECUTION_TIME: {INTEGER(10), refers to ACCESS_RESULT.EXECUTION_TIME}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnExecutionTime() {
        return _columnExecutionTime;
    }

    /**
     * CREATE_TIME: {TIMESTAMP(23, 10), refers to ACCESS_RESULT.CREATE_TIME}
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
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
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
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<AccessResultDiff> getEntityType() {
        return AccessResultDiff.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public Entity newEntity() {
        return newMyEntity();
    }

    public AccessResultDiff newMyEntity() {
        return new AccessResultDiff();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((AccessResultDiff) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((AccessResultDiff) et, mp);
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
