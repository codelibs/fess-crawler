/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.dbflute.bhv.core.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;

/**
 * @author jflute
 */
public abstract class AbstractEntityCommand extends AbstractBehaviorCommand<Integer> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The type of entity. (Required) */
    protected Class<? extends Entity> _entityType;

    /** The instance of condition-bean. (Required) */
    protected Entity _entity;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public Class<?> getCommandReturnType() {
        return Integer.class;
    }

    // ===================================================================================
    //                                                                  Detail Information
    //                                                                  ==================
    public boolean isConditionBean() {
        return false;
    }

    public boolean isOutsideSql() {
        return false;
    }

    public boolean isProcedure() {
        return false;
    }

    public boolean isSelect() {
        return false;
    }

    public boolean isSelectCount() {
        return false;
    }

    // ===================================================================================
    //                                                                             Factory
    //                                                                             =======
    // -----------------------------------------------------
    //                                          BeanMetaData
    //                                          ------------
    protected TnBeanMetaData createBeanMetaData() {
        return _beanMetaDataFactory.createBeanMetaData(_entityType);
    }

    // ===================================================================================
    //                                                                    Process Callback
    //                                                                    ================
    public void beforeGettingSqlExecution() {
    }

    public void afterExecuting() {
    }

    // ===================================================================================
    //                                                               SqlExecution Handling
    //                                                               =====================
    public String buildSqlExecutionKey() {
        assertStatus("buildSqlExecutionKey");
        return _tableDbName + ":" + getCommandName() + "(" + _entityType.getSimpleName() + ")";
    }

    public Object[] getSqlExecutionArgument() {
        assertStatus("getSqlExecutionArgument");
        return new Object[] { _entity };
    }

    // ===================================================================================
    //                                                                Argument Information
    //                                                                ====================
    public ConditionBean getConditionBean() {
        return null;
    }

    public String getOutsideSqlPath() {
        return null;
    }

    public OutsideSqlOption getOutsideSqlOption() {
        return null;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    /**
     * Find DB meta. <br />
     * Basically this method should be called when initializing only.
     * @return DB meta. (Nullable: If the entity does not its DB meta)
     */
    protected DBMeta findDBMeta() {
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - 
        // Cannot use the handler of DBMeta instance
        // because the customize-entity is contained to find here.
        // - - - - - - - - - -/
        //DBMetaInstanceHandler.findDBMeta(_tableDbName);

        final Class<?> beanType = _entityType;
        if (beanType == null) {
            return null;
        }
        if (!Entity.class.isAssignableFrom(beanType)) {
            return null;
        }
        final Entity entity;
        try {
            entity = (Entity) beanType.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return entity.getDBMeta();
    }

    /**
     * Get persistent property names. <br />
     * Basically this method should be called when initializing only.
     * @param bmd The bean meta data. (NotNull)
     * @return Persistent property names. (NotNull)
     */
    protected String[] getPersistentPropertyNames(TnBeanMetaData bmd) {
        final DBMeta dbmeta = findDBMeta();
        if (dbmeta != null) {
            final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
            final List<String> propertyNameList = new ArrayList<String>();
            for (ColumnInfo columnInfo : columnInfoList) {
                propertyNameList.add(columnInfo.getPropertyName());
            }
            return propertyNameList.toArray(new String[] {});
        } else {
            // when the entity does not have its DB meta.
            return createNonOrderedPropertyNames(bmd);
        }
    }

    private String[] createNonOrderedPropertyNames(TnBeanMetaData bmd) {
        final List<String> propertyNameList = new ArrayList<String>();
        final Map<String, TnPropertyType> propertyTypeMap = bmd.getPropertyTypeMap();
        final Set<Entry<String, TnPropertyType>> entrySet = propertyTypeMap.entrySet();
        for (Entry<String, TnPropertyType> entry : entrySet) {
            final TnPropertyType pt = entry.getValue();
            if (pt.isPersistent()) {
                propertyNameList.add(pt.getPropertyName());
            }
        }
        return propertyNameList.toArray(new String[propertyNameList.size()]);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertStatus(String methodName) {
        assertBasicProperty(methodName);
        assertComponentProperty(methodName);
        if (_entityType == null) {
            throw new IllegalStateException(buildAssertMessage("_entityType", methodName));
        }
        if (_entity == null) {
            throw new IllegalStateException(buildAssertMessage("_entity", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setEntityType(Class<? extends Entity> entityType) {
        _entityType = entityType;
    }

    public void setEntity(Entity entity) {
        _entity = entity;
    }
}
