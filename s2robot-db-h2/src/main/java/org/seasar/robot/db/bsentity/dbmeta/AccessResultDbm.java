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
import org.seasar.robot.db.exentity.AccessResult;
import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.robot.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.robot.dbflute.dbmeta.name.TableSqlName;
import org.seasar.robot.dbflute.helper.StringKeyMap;

/**
 * The DB meta of ACCESS_RESULT. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final AccessResultDbm _instance = new AccessResultDbm();

    private AccessResultDbm() {
    }

    public static AccessResultDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    public DBDef getCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "ACCESS_RESULT";

    protected final String _tablePropertyName = "accessResult";

    protected final TableSqlName _tableSqlName = new TableSqlName(
            "ACCESS_RESULT", _tableDbName);
    {
        _tableSqlName.xacceptFilter(DBFluteConfig.getInstance()
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
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("ID", "ID", null, null, true,
            "id", Long.class, true, true, "BIGINT", 19, 0, false, null, null,
            null, "", null);

    protected final ColumnInfo _columnSessionId = cci("SESSION_ID",
            "SESSION_ID", null, null, true, "sessionId", String.class, false,
            false, "VARCHAR", 20, 0, false, null, null, null, null, null);

    protected final ColumnInfo _columnRuleId = cci("RULE_ID", "RULE_ID", null,
            null, false, "ruleId", String.class, false, false, "VARCHAR", 20,
            0, false, null, null, null, null, null);

    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, true,
            "url", String.class, false, false, "VARCHAR", 65536, 0, false,
            null, null, null, null, null);

    protected final ColumnInfo _columnParentUrl = cci("PARENT_URL",
            "PARENT_URL", null, null, false, "parentUrl", String.class, false,
            false, "VARCHAR", 65536, 0, false, null, null, null, null, null);

    protected final ColumnInfo _columnStatus = cci("STATUS", "STATUS", null,
            null, true, "status", Integer.class, false, false, "INTEGER", 10,
            0, false, null, null, null, null, null);

    protected final ColumnInfo _columnHttpStatusCode = cci("HTTP_STATUS_CODE",
            "HTTP_STATUS_CODE", null, null, true, "httpStatusCode",
            Integer.class, false, false, "INTEGER", 10, 0, false, null, null,
            null, null, null);

    protected final ColumnInfo _columnMethod = cci("METHOD", "METHOD", null,
            null, true, "method", String.class, false, false, "VARCHAR", 10, 0,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnMimeType = cci("MIME_TYPE", "MIME_TYPE",
            null, null, true, "mimeType", String.class, false, false,
            "VARCHAR", 100, 0, false, null, null, null, null, null);

    protected final ColumnInfo _columnContentLength = cci("CONTENT_LENGTH",
            "CONTENT_LENGTH", null, null, true, "contentLength", Long.class,
            false, false, "BIGINT", 19, 0, false, null, null, null, null, null);

    protected final ColumnInfo _columnExecutionTime = cci("EXECUTION_TIME",
            "EXECUTION_TIME", null, null, true, "executionTime", Integer.class,
            false, false, "INTEGER", 10, 0, false, null, null, null, null, null);

    protected final ColumnInfo _columnLastModified = cci("LAST_MODIFIED",
            "LAST_MODIFIED", null, null, true, "lastModified",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, false,
            null, null, null, null, null);

    protected final ColumnInfo _columnCreateTime = cci("CREATE_TIME",
            "CREATE_TIME", null, null, true, "createTime",
            java.sql.Timestamp.class, false, false, "TIMESTAMP", 23, 10, false,
            null, null, null, null, null);

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

    {
        initializeInformationResource();
    }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
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
    //                                                                       Relation Info
    //                                                                       =============
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    public ForeignInfo foreignAccessResultDataAsOne() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                AccessResultDataDbm.getInstance().columnId());
        return cfi("accessResultDataAsOne", this,
                AccessResultDataDbm.getInstance(), map, 0, true, false);
    }

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    public boolean hasIdentity() {
        return true;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() {
        return "org.seasar.robot.db.exentity.AccessResult";
    }

    public String getConditionBeanTypeName() {
        return "org.seasar.robot.db.cbean.bs.AccessResultCB";
    }

    public String getDaoTypeName() {
        return "org.seasar.robot.db.exdao.AccessResultDao";
    }

    public String getBehaviorTypeName() {
        return "org.seasar.robot.db.exbhv.AccessResultBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<AccessResult> getEntityType() {
        return AccessResult.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() {
        return newMyEntity();
    }

    public AccessResult newMyEntity() {
        return new AccessResult();
    }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    public void acceptPrimaryKeyMap(Entity e, Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((AccessResult) e, m, _epsMap);
    }

    public Map<String, Object> extractPrimaryKeyMap(Entity e) {
        return doExtractPrimaryKeyMap(e);
    }

    public Map<String, Object> extractAllColumnMap(Entity e) {
        return doExtractAllColumnMap(e);
    }

    // ===================================================================================
    //                                                               Entity Property Setup
    //                                                               =====================
    // It's very INTERNAL!
    protected final Map<String, Eps<AccessResult>> _epsMap = StringKeyMap
            .createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsId(), columnId());
        setupEps(_epsMap, new EpsSessionId(), columnSessionId());
        setupEps(_epsMap, new EpsRuleId(), columnRuleId());
        setupEps(_epsMap, new EpsUrl(), columnUrl());
        setupEps(_epsMap, new EpsParentUrl(), columnParentUrl());
        setupEps(_epsMap, new EpsStatus(), columnStatus());
        setupEps(_epsMap, new EpsHttpStatusCode(), columnHttpStatusCode());
        setupEps(_epsMap, new EpsMethod(), columnMethod());
        setupEps(_epsMap, new EpsMimeType(), columnMimeType());
        setupEps(_epsMap, new EpsContentLength(), columnContentLength());
        setupEps(_epsMap, new EpsExecutionTime(), columnExecutionTime());
        setupEps(_epsMap, new EpsLastModified(), columnLastModified());
        setupEps(_epsMap, new EpsCreateTime(), columnCreateTime());
    }

    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity,
            Object value) {
        findEps(_epsMap, propertyName).setup((AccessResult) entity, value);
    }

    public class EpsId implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setId(ctl(v));
        }
    }

    public static class EpsSessionId implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setSessionId((String) v);
        }
    }

    public static class EpsRuleId implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setRuleId((String) v);
        }
    }

    public static class EpsUrl implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setUrl((String) v);
        }
    }

    public static class EpsParentUrl implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setParentUrl((String) v);
        }
    }

    public class EpsStatus implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setStatus(cti(v));
        }
    }

    public class EpsHttpStatusCode implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setHttpStatusCode(cti(v));
        }
    }

    public static class EpsMethod implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setMethod((String) v);
        }
    }

    public static class EpsMimeType implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setMimeType((String) v);
        }
    }

    public class EpsContentLength implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setContentLength(ctl(v));
        }
    }

    public class EpsExecutionTime implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setExecutionTime(cti(v));
        }
    }

    public static class EpsLastModified implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setLastModified((java.sql.Timestamp) v);
        }
    }

    public static class EpsCreateTime implements Eps<AccessResult> {
        public void setup(AccessResult e, Object v) {
            e.setCreateTime((java.sql.Timestamp) v);
        }
    }
}
