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
package org.seasar.robot.dbflute.s2dao.metadata;

import java.util.List;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.exception.PluginValueTypeNotFoundException;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * @author jflute
 */
public class TnProcedureValueTypeProvider {

    // ===================================================================================
    //                                                                             Provide
    //                                                                             =======
    /**
     * Provide value type for procedure from registered types.
     * @param pmbType The type of ProcdurePmb for the parameter. (NotNull)
     * @param paramName The name of parameter. (NotNull)
     * @param paramType The type of parameter. (NotNull)
     * @param keyName The key name for plug-in value type. (NullAllowed) 
     * @param dbdef The current DB definition. (NotNull)
     * @return The instance of value type. (NotNull: if not found by (not-null) valueTypeName, exception)
     */
    public ValueType provide(Class<?> pmbType, String paramName, Class<?> paramType, String keyName, DBDef dbdef) {
        if (keyName != null) {
            return findValueTypeByName(pmbType, paramName, paramType, keyName);
        }
        if (List.class.isAssignableFrom(paramType)) { // is for out parameter cursor.
            return findCursorValueType(dbdef);
        }
        return findValueTypeByType(paramType);
    }

    // ===================================================================================
    //                                                                             By Name
    //                                                                             =======
    protected ValueType findValueTypeByName(Class<?> pmbType, String paramName, Class<?> paramType, String keyName) {
        final ValueType valueType = TnValueTypes.getPluginValueType(keyName);
        if (valueType != null) {
            return valueType;
        }
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found a plug-in value type by the name.");
        br.addItem("ProcedurePmb");
        br.addElement(pmbType.getName());
        br.addItem("Parameter");
        br.addElement(paramName);
        br.addElement(paramType.getName());
        br.addItem("Key Name");
        br.addElement(keyName);
        final String msg = br.buildExceptionMessage();
        throw new PluginValueTypeNotFoundException(msg);
    }

    // ===================================================================================
    //                                                                              Cursor
    //                                                                              ======
    protected ValueType findCursorValueType(DBDef dbdef) {
        if (DBDef.PostgreSQL.equals(dbdef)) {
            return TnValueTypes.POSTGRESQL_RESULT_SET;
        } else if (DBDef.Oracle.equals(dbdef)) {
            return TnValueTypes.ORACLE_RESULT_SET;
        } else {
            return TnValueTypes.SERIALIZABLE_BYTE_ARRAY;
        }
    }

    // ===================================================================================
    //                                                                             By Type
    //                                                                             =======
    protected ValueType findValueTypeByType(Class<?> paramType) {
        return TnValueTypes.getValueType(paramType);
    }
}
