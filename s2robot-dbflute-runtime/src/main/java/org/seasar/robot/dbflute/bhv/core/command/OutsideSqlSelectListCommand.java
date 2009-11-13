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

import java.util.List;

import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * The behavior command for OutsideSql.selectList().
 * @author jflute
 * @param <ENTITY> The type of entity.
 */
public class OutsideSqlSelectListCommand<ENTITY> extends AbstractOutsideSqlSelectCommand<List<ENTITY>> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The type of entity. (Required) */
    protected Class<ENTITY> _entityType;

    // ===================================================================================
    //                                                                   Basic Information
    //                                                                   =================
    public String getCommandName() {
        return "selectList";
    }

    public Class<?> getCommandReturnType() {
        return List.class;
    }

    // ===================================================================================
    //                                                                             Factory
    //                                                                             =======
    // -----------------------------------------------------
    //                                          BeanMetaData
    //                                          ------------
    protected TnBeanMetaData createBeanMetaData() {
        assertEntityType("createBeanMetaData");
        return _beanMetaDataFactory.createBeanMetaData(_entityType);
    }

    // -----------------------------------------------------
    //                                      ResultSetHandler
    //                                      ----------------
    protected TnResultSetHandler createOutsideSqlBeanListResultSetHandler(TnBeanMetaData bmd) {
        final ValueType valueType = TnValueTypes.getValueType(_entityType);
        if (valueType == null || !valueType.equals(TnValueTypes.OBJECT)) {
            return createObjectListResultSetHandler(valueType);
        }
        return createBeanListMetaDataResultSetHandler(bmd);
    }

    // ===================================================================================
    //                                                                     Extension Point
    //                                                                     ===============
    @Override
    protected TnResultSetHandler createOutsideSqlSelectResultSetHandler() {
        final TnBeanMetaData bmd = createBeanMetaData();
        final TnResultSetHandler handler = createOutsideSqlBeanListResultSetHandler(bmd);
        return handler;
    }

    @Override
    protected Class<?> getResultType() {
        return _entityType;
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertEntityType(String methodName) {
        if (_entityType == null) {
            throw new IllegalStateException(buildAssertMessage("_entityType", methodName));
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setEntityType(Class<ENTITY> entityType) {
        _entityType = entityType;
    }
}
