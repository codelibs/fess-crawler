package org.seasar.robot.dbflute.mock;

import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.AbstractDBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.robot.dbflute.dbmeta.info.ReferrerInfo;
import org.seasar.robot.dbflute.dbmeta.info.RelationInfo;
import org.seasar.robot.dbflute.dbmeta.info.UniqueInfo;
import org.seasar.robot.dbflute.dbmeta.name.TableSqlName;

public class MockDBMeta extends AbstractDBMeta {

    private static final long serialVersionUID = 1L;

    public DBDef getCurrentDBDef() {
        return null;
    }

    public void acceptPrimaryKeyMap(Entity entity, Map<String, ? extends Object> primaryKeyMap) {
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

    public UniqueInfo getPrimaryUniqueInfo() {
        return null;
    }

    public String getTableDbName() {
        return null;
    }

    public String getTablePropertyName() {
        return null;
    }

    public TableSqlName getTableSqlName() {
        return null;
    }

    public boolean hasEntityPropertySetupper(String propertyName) {
        return false;
    }

    public boolean hasPrimaryKey() {
        return false;
    }

    public boolean hasCompoundPrimaryKey() {
        return false;
    }

    public Entity newEntity() {
        return null;
    }

    public void setupEntityProperty(String propertyName, Object entity, Object value) {
    }

    @Override
    protected List<ColumnInfo> ccil() {
        return null;
    }

    public Map<String, Object> extractPrimaryKeyMap(Entity entity) {
        return null;
    }

    public Map<String, Object> extractAllColumnMap(Entity entity) {
        return null;
    }
}
