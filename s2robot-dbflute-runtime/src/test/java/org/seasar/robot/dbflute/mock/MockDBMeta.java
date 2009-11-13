package org.seasar.robot.dbflute.mock;

import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.robot.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.robot.dbflute.dbmeta.info.RelationInfo;
import org.seasar.robot.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.robot.dbflute.helper.mapstring.MapListString;
import org.seasar.robot.dbflute.helper.mapstring.MapStringBuilder;

public class MockDBMeta implements DBMeta {

    public void acceptColumnValueMap(Entity entity, Map<String, ? extends Object> columnValueMap) {
    }

    public void acceptColumnValueMapString(Entity entity, String columnValueMapString) {

    }

    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {

    }

    public void acceptPrimaryKeyMapString(Entity entity, String primaryKeyMapString) {

    }

    public List<String> convertToColumnStringValueList(Entity entity) {

        return null;
    }

    public Map<String, String> convertToColumnStringValueMap(Entity entity) {

        return null;
    }

    public List<Object> convertToColumnValueList(Entity entity) {

        return null;
    }

    public Map<String, Object> convertToColumnValueMap(Entity entity) {

        return null;
    }

    public MapListString createMapListString() {

        return null;
    }

    public MapStringBuilder createMapStringBuilder() {

        return null;
    }

    public String extractColumnValueMapString(Entity entity) {

        return null;
    }

    public String extractColumnValueMapString(Entity entity, String startBrace, String endBrace, String delimiter,
            String equal) {

        return null;
    }

    public String extractPrimaryKeyMapString(Entity entity) {

        return null;
    }

    public String extractPrimaryKeyMapString(Entity entity, String startBrace, String endBrace, String delimiter,
            String equal) {

        return null;
    }

    public ColumnInfo findColumnInfo(String columnFlexibleName) {

        return null;
    }

    public String findDbName(String flexibleName) {

        return null;
    }

    public DBMeta findForeignDBMeta(String foreignPropName) {

        return null;
    }

    public ForeignInfo findForeignInfo(String foreignPropName) {

        return null;
    }

    public String findPropertyName(String flexibleName) {

        return null;
    }

    public DBMeta findReferrerDBMeta(String referrerPropertyName) {

        return null;
    }

    public ReferrerInfo findReferrerInfo(String referrerPropertyName) {

        return null;
    }

    public RelationInfo findRelationInfo(String relationPropertyName) {

        return null;
    }

    public String getBehaviorTypeName() {

        return null;
    }

    public List<ColumnInfo> getColumnInfoList() {

        return null;
    }

    public String getConditionBeanTypeName() {

        return null;
    }

    public String getDaoTypeName() {

        return null;
    }

    public Class<? extends Entity> getEntityType() {

        return null;
    }

    public String getEntityTypeName() {

        return null;
    }

    public List<ForeignInfo> getForeignInfoList() {

        return null;
    }

    public UniqueInfo getPrimaryUniqueInfo() {

        return null;
    }

    public List<ReferrerInfo> getReferrerInfoList() {

        return null;
    }

    public String getSequenceNextValSql() {

        return null;
    }

    public String getTableDbName() {

        return null;
    }

    public String getTablePropertyName() {

        return null;
    }

    public String getTableSqlName() {

        return null;
    }

    public ColumnInfo getUpdateDateColumnInfo() {

        return null;
    }

    public ColumnInfo getVersionNoColumnInfo() {

        return null;
    }

    public boolean hasColumn(String columnFlexibleName) {

        return false;
    }

    public boolean hasCommonColumn() {

        return false;
    }

    public boolean hasEntityPropertySetupper(String propertyName) {

        return false;
    }

    public boolean hasFlexibleName(String flexibleName) {

        return false;
    }

    public boolean hasForeign(String foreignPropName) {

        return false;
    }

    public boolean hasIdentity() {

        return false;
    }

    public boolean hasPrimaryKey() {

        return false;
    }

    public boolean hasReferrer(String referrerPropertyName) {

        return false;
    }

    public boolean hasSequence() {

        return false;
    }

    public boolean hasTwoOrMorePrimaryKeys() {

        return false;
    }

    public boolean hasUpdateDate() {

        return false;
    }

    public boolean hasVersionNo() {

        return false;
    }

    public Entity newEntity() {

        return null;
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {

    }

}
