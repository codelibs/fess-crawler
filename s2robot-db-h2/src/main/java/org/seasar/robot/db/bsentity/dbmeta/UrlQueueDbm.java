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
import org.seasar.robot.db.exentity.UrlQueue;
import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.robot.dbflute.dbmeta.name.TableSqlName;
import org.seasar.robot.dbflute.helper.StringKeyMap;

/**
 * The DB meta of URL_QUEUE. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class UrlQueueDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final UrlQueueDbm _instance = new UrlQueueDbm();

    private UrlQueueDbm() {
    }

    public static UrlQueueDbm getInstance() {
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
    protected final String _tableDbName = "URL_QUEUE";

    protected final String _tablePropertyName = "urlQueue";

    protected final TableSqlName _tableSqlName = new TableSqlName("URL_QUEUE",
            _tableDbName);
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
            null, null, null);

    protected final ColumnInfo _columnSessionId = cci("SESSION_ID",
            "SESSION_ID", null, null, true, "sessionId", String.class, false,
            false, "VARCHAR", 20, 0, false, null, null, null, null, null);

    protected final ColumnInfo _columnMethod = cci("METHOD", "METHOD", null,
            null, true, "method", String.class, false, false, "VARCHAR", 10, 0,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, true,
            "url", String.class, false, false, "VARCHAR", 65536, 0, false,
            null, null, null, null, null);

    protected final ColumnInfo _columnParentUrl = cci("PARENT_URL",
            "PARENT_URL", null, null, false, "parentUrl", String.class, false,
            false, "VARCHAR", 65536, 0, false, null, null, null, null, null);

    protected final ColumnInfo _columnDepth = cci("DEPTH", "DEPTH", null, null,
            true, "depth", Integer.class, false, false, "INTEGER", 10, 0,
            false, null, null, null, null, null);

    protected final ColumnInfo _columnLastModified = cci("LAST_MODIFIED",
            "LAST_MODIFIED", null, null, false, "lastModified",
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

    public ColumnInfo columnMethod() {
        return _columnMethod;
    }

    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    public ColumnInfo columnParentUrl() {
        return _columnParentUrl;
    }

    public ColumnInfo columnDepth() {
        return _columnDepth;
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
        ls.add(columnMethod());
        ls.add(columnUrl());
        ls.add(columnParentUrl());
        ls.add(columnDepth());
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
        return "org.seasar.robot.db.exentity.UrlQueue";
    }

    public String getConditionBeanTypeName() {
        return "org.seasar.robot.db.cbean.bs.UrlQueueCB";
    }

    public String getDaoTypeName() {
        return "org.seasar.robot.db.exdao.UrlQueueDao";
    }

    public String getBehaviorTypeName() {
        return "org.seasar.robot.db.exbhv.UrlQueueBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<UrlQueue> getEntityType() {
        return UrlQueue.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() {
        return newMyEntity();
    }

    public UrlQueue newMyEntity() {
        return new UrlQueue();
    }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    public void acceptPrimaryKeyMap(Entity e, Map<String, ? extends Object> m) {
        doAcceptPrimaryKeyMap((UrlQueue) e, m, _epsMap);
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
    protected final Map<String, Eps<UrlQueue>> _epsMap = StringKeyMap
            .createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsId(), columnId());
        setupEps(_epsMap, new EpsSessionId(), columnSessionId());
        setupEps(_epsMap, new EpsMethod(), columnMethod());
        setupEps(_epsMap, new EpsUrl(), columnUrl());
        setupEps(_epsMap, new EpsParentUrl(), columnParentUrl());
        setupEps(_epsMap, new EpsDepth(), columnDepth());
        setupEps(_epsMap, new EpsLastModified(), columnLastModified());
        setupEps(_epsMap, new EpsCreateTime(), columnCreateTime());
    }

    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity,
            Object value) {
        findEps(_epsMap, propertyName).setup((UrlQueue) entity, value);
    }

    public class EpsId implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setId(ctl(v));
        }
    }

    public static class EpsSessionId implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setSessionId((String) v);
        }
    }

    public static class EpsMethod implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setMethod((String) v);
        }
    }

    public static class EpsUrl implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setUrl((String) v);
        }
    }

    public static class EpsParentUrl implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setParentUrl((String) v);
        }
    }

    public class EpsDepth implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setDepth(cti(v));
        }
    }

    public static class EpsLastModified implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setLastModified((java.sql.Timestamp) v);
        }
    }

    public static class EpsCreateTime implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setCreateTime((java.sql.Timestamp) v);
        }
    }
}
