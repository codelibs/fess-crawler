package org.seasar.robot.db.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.robot.db.exentity.AccessResultData;

/**
 * The DB meta of ACCESS_RESULT_DATA. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final AccessResultDataDbm _instance = new AccessResultDataDbm();

    private AccessResultDataDbm() {
    }

    public static AccessResultDataDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    public String getTableDbName() {
        return "ACCESS_RESULT_DATA";
    }

    public String getTablePropertyName() {
        return "accessResultData";
    }

    public String getTableSqlName() {
        return "ACCESS_RESULT_DATA";
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected ColumnInfo _columnId = cci("ID", null, "id", Long.class, true,
            false, null, null);

    protected ColumnInfo _columnTransformerName = cci("TRANSFORMER_NAME", null,
            "transformerName", String.class, false, false, 255, 0);

    protected ColumnInfo _columnData = cci("DATA", null, "data", byte[].class,
            false, false, null, null);

    protected ColumnInfo _columnEncoding = cci("ENCODING", null, "encoding",
            String.class, false, false, 20, 0);

    public ColumnInfo columnId() {
        return _columnId;
    }

    public ColumnInfo columnTransformerName() {
        return _columnTransformerName;
    }

    public ColumnInfo columnData() {
        return _columnData;
    }

    public ColumnInfo columnEncoding() {
        return _columnEncoding;
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
    public ForeignInfo foreignAccessResult() {
        Map<ColumnInfo, ColumnInfo> map = newLinkedHashMap(columnId(),
                AccessResultDbm.getInstance().columnId());
        return cfi("accessResult", this, AccessResultDbm.getInstance(), map, 0,
                true);
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
    public String getEntityTypeName() {
        return "org.seasar.robot.db.exentity.AccessResultData";
    }

    public String getConditionBeanTypeName() {
        return "org.seasar.robot.db.cbean.bs.AccessResultDataCB";
    }

    public String getDaoTypeName() {
        return "org.seasar.robot.db.exdao.AccessResultDataDao";
    }

    public String getBehaviorTypeName() {
        return "org.seasar.robot.db.exbhv.AccessResultDataBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<AccessResultData> getEntityType() {
        return AccessResultData.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public Entity newEntity() {
        return newMyEntity();
    }

    public AccessResultData newMyEntity() {
        return new AccessResultData();
    }

    // ===================================================================================
    //                                                                     Entity Handling
    //                                                                     ===============  
    // -----------------------------------------------------
    //                                                Accept
    //                                                ------
    public void acceptPrimaryKeyMap(Entity entity,
            Map<String, ? extends Object> primaryKeyMap) {
        doAcceptPrimaryKeyMap((AccessResultData) entity, primaryKeyMap, _epsMap);
    }

    public void acceptPrimaryKeyMapString(Entity entity,
            String primaryKeyMapString) {
        MapStringUtil.acceptPrimaryKeyMapString(primaryKeyMapString, entity);
    }

    public void acceptColumnValueMap(Entity entity,
            Map<String, ? extends Object> columnValueMap) {
        doAcceptColumnValueMap((AccessResultData) entity, columnValueMap,
                _epsMap);
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
    protected Map<String, Eps<AccessResultData>> _epsMap = StringKeyMap
            .createAsFlexibleConcurrent();
    {
        setupEps(_epsMap, new EpsId(), columnId());
        setupEps(_epsMap, new EpsTransformerName(), columnTransformerName());
        setupEps(_epsMap, new EpsData(), columnData());
        setupEps(_epsMap, new EpsEncoding(), columnEncoding());
    }

    public boolean hasEntityPropertySetupper(String propertyName) {
        return _epsMap.containsKey(propertyName);
    }

    public void setupEntityProperty(String propertyName, Object entity,
            Object value) {
        findEps(_epsMap, propertyName).setup((AccessResultData) entity, value);
    }

    public static class EpsId implements Eps<AccessResultData> {
        public void setup(AccessResultData e, Object v) {
            e.setId((Long) v);
        }
    }

    public static class EpsTransformerName implements Eps<AccessResultData> {
        public void setup(AccessResultData e, Object v) {
            e.setTransformerName((String) v);
        }
    }

    public static class EpsData implements Eps<AccessResultData> {
        public void setup(AccessResultData e, Object v) {
            e.setData((byte[]) v);
        }
    }

    public static class EpsEncoding implements Eps<AccessResultData> {
        public void setup(AccessResultData e, Object v) {
            e.setEncoding((String) v);
        }
    }
}
