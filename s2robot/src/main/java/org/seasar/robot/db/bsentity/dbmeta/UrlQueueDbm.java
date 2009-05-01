package org.seasar.robot.db.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.robot.db.exentity.UrlQueue;

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
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() {
        return "URL_QUEUE";
    }

    public String getTablePropertyName() {
        return "urlQueue";
    }

    public String getTableSqlName() {
        return "URL_QUEUE";
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnId = cci("ID", null, "id", Long.class, true,
            true, null, null);

    protected ColumnInfo _columnSessionId = cci("SESSION_ID", null,
            "sessionId", String.class, false, false, 20, 0);

    protected ColumnInfo _columnMethod = cci("METHOD", null, "method",
            String.class, false, false, 10, 0);

    protected ColumnInfo _columnUrl = cci("URL", null, "url", String.class,
            false, false, 65536, 0);

    protected ColumnInfo _columnParentUrl = cci("PARENT_URL", null,
            "parentUrl", String.class, false, false, 65536, 0);

    protected ColumnInfo _columnDepth = cci("DEPTH", null, "depth",
            Integer.class, false, false, null, null);

    protected ColumnInfo _columnCreateTime = cci("CREATE_TIME", null,
            "createTime", java.sql.Timestamp.class, false, false, null, null);

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

    public ColumnInfo columnCreateTime() {
        return _columnCreateTime;
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

    public boolean hasTwoOrMorePrimaryKeys() {
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
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity,
            Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((UrlQueue) entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity,
            String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity,
            Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((UrlQueue) entity, columnValueMap, _epsMap);
    }

    public void acceptColumnValueMapString(Entity entity,
            String columnValueMapString) {
        MapStringUtil.acceptColumnValueMapString(columnValueMapString, entity);
    }

    // -----------------------------------------------------
    //                                               Extract
    //                                               -------
    public String extractPrimaryKeyMapString(Entity entity) {
        return MapStringUtil.extractPrimaryKeyMapString(entity);
    }

    public String extractPrimaryKeyMapString(Entity entity, String startBrace,
            String endBrace, String delimiter, String equal) {
        return doExtractPrimaryKeyMapString(entity, startBrace, endBrace,
                delimiter, equal);
    }

    public String extractColumnValueMapString(Entity entity) {
        return MapStringUtil.extractColumnValueMapString(entity);
    }

    public String extractColumnValueMapString(Entity entity, String startBrace,
            String endBrace, String delimiter, String equal) {
        return doExtractColumnValueMapString(entity, startBrace, endBrace,
                delimiter, equal);
    }

    // -----------------------------------------------------
    //                                               Convert
    //                                               -------
    public List<Object> convertToColumnValueList(Entity entity) {
        return newArrayList(convertToColumnValueMap(entity).values());
    }

    public Map<String, Object> convertToColumnValueMap(Entity entity) {
        return doConvertToColumnValueMap(entity);
    }

    public List<String> convertToColumnStringValueList(Entity entity) {
        return newArrayList(convertToColumnStringValueMap(entity).values());
    }

    public Map<String, String> convertToColumnStringValueMap(Entity entity) {
        return doConvertToColumnStringValueMap(entity);
    }

    // ===================================================================================
    //                                                               Entity Property Setup
    //                                                               =====================
    // It's very INTERNAL!
    protected Map<String, Eps<UrlQueue>> _epsMap = StringKeyMap
            .createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsId(), columnId());
        setupEps(_epsMap, new EpsSessionId(), columnSessionId());
        setupEps(_epsMap, new EpsMethod(), columnMethod());
        setupEps(_epsMap, new EpsUrl(), columnUrl());
        setupEps(_epsMap, new EpsParentUrl(), columnParentUrl());
        setupEps(_epsMap, new EpsDepth(), columnDepth());
        setupEps(_epsMap, new EpsCreateTime(), columnCreateTime());
    }

    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity,
            Object value) {
        findEps(_epsMap, propertyName).setup((UrlQueue) entity, value);
    }

    public static class EpsId implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setId((Long) v);
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

    public static class EpsDepth implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setDepth((Integer) v);
        }
    }

    public static class EpsCreateTime implements Eps<UrlQueue> {
        public void setup(UrlQueue e, Object v) {
            e.setCreateTime((java.sql.Timestamp) v);
        }
    }
}
