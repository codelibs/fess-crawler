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
package org.seasar.robot.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnCommandContextHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected CommandContext commandContext;
    protected List<TnPropertyType> propertyTypeList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnCommandContextHandler(DataSource dataSource, StatementFactory statementFactory,
            CommandContext commandContext) {
        super(dataSource, statementFactory);
        this.commandContext = commandContext;
        setSql(commandContext.getSql());
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public int execute(Object[] args) {
        final Connection connection = getConnection();
        try {
            return execute(connection, commandContext);
        } finally {
            close(connection);
        }
    }

    protected int execute(Connection connection, CommandContext context) {
        logSql(context.getBindVariables(), getArgTypes(context.getBindVariables()));
        final PreparedStatement ps = prepareStatement(connection);
        int ret = -1;
        try {
            final Object[] bindVariables = context.getBindVariables();
            final Class<?>[] bindVariableTypes = context.getBindVariableTypes();
            if (hasPropertyTypeList()) {
                final int index = bindFirstScope(ps, bindVariables, bindVariableTypes);
                bindSecondScope(ps, bindVariables, bindVariableTypes, index);
            } else {
                bindArgs(ps, bindVariables, bindVariableTypes);
            }
            ret = executeUpdate(ps);
        } finally {
            close(ps);
        }
        return ret;
    }

    protected boolean hasPropertyTypeList() {
        return propertyTypeList != null && !propertyTypeList.isEmpty();
    }

    protected int bindFirstScope(PreparedStatement ps, Object[] bindVariables, Class<?>[] bindVariableTypes) {
        final List<Object> firstVariableList = new ArrayList<Object>();
        final List<ValueType> firstValueTypeList = new ArrayList<ValueType>();
        int index = 0;
        for (TnPropertyType propertyType : propertyTypeList) {
            firstVariableList.add(bindVariables[index]);
            firstValueTypeList.add(propertyType.getValueType());
            ++index;
        }
        bindArgs(ps, firstVariableList.toArray(), firstValueTypeList.toArray(new ValueType[0]));
        return index;
    }

    protected void bindSecondScope(PreparedStatement ps, Object[] bindVariables, Class<?>[] bindVariableTypes, int index) {
        bindArgs(ps, bindVariables, bindVariableTypes, index);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<TnPropertyType> getPropertyTypeList() {
        return propertyTypeList;
    }

    public void setPropertyTypeList(List<TnPropertyType> propertyTypeList) {
        this.propertyTypeList = propertyTypeList;
    }
}
