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
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnCommandContextHandler extends TnAbstractBasicSqlHandler {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The context of command. (NotNull) */
    protected final CommandContext _commandContext;

    /** The list of bound property type in first scope. (NullAllowed) */
    protected List<TnPropertyType> _firstBoundPropTypeList;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnCommandContextHandler(DataSource dataSource, StatementFactory statementFactory, String sql,
            CommandContext commandContext) {
        super(dataSource, statementFactory, sql);
        assertObjectNotNull("commandContext", commandContext);
        _commandContext = commandContext;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public int execute(Object[] args) {
        final Connection conn = getConnection();
        try {
            return doExecute(conn, _commandContext);
        } finally {
            close(conn);
        }
    }

    protected int doExecute(Connection conn, CommandContext ctx) {
        logSql(ctx.getBindVariables(), getArgTypes(ctx.getBindVariables()));
        final PreparedStatement ps = prepareStatement(conn);
        int ret = -1;
        try {
            final Object[] bindVariables = ctx.getBindVariables();
            final Class<?>[] bindVariableTypes = ctx.getBindVariableTypes();
            if (hasBoundPropertyTypeList()) { // basically for queryUpdate()
                final int index = bindFirstScope(conn, ps, bindVariables, bindVariableTypes);
                bindSecondScope(conn, ps, bindVariables, bindVariableTypes, index);
            } else {
                bindArgs(conn, ps, bindVariables, bindVariableTypes);
            }
            ret = executeUpdate(ps);
        } finally {
            close(ps);
        }
        return ret;
    }

    protected boolean hasBoundPropertyTypeList() {
        return _firstBoundPropTypeList != null && !_firstBoundPropTypeList.isEmpty();
    }

    // ===================================================================================
    //                                                                          Bind Scope
    //                                                                          ==========
    protected int bindFirstScope(Connection conn, PreparedStatement ps, Object[] bindVariables,
            Class<?>[] bindVariableTypes) {
        final List<Object> firstVariableList = new ArrayList<Object>();
        final List<ValueType> firstValueTypeList = new ArrayList<ValueType>();
        int index = 0;
        for (TnPropertyType propertyType : _firstBoundPropTypeList) {
            firstVariableList.add(bindVariables[index]);
            firstValueTypeList.add(propertyType.getValueType());
            ++index;
        }
        bindArgs(conn, ps, firstVariableList.toArray(), firstValueTypeList.toArray(new ValueType[0]));
        return index;
    }

    protected void bindSecondScope(Connection conn, PreparedStatement ps, Object[] bindVariables,
            Class<?>[] bindVariableTypes, int index) {
        bindArgs(conn, ps, bindVariables, bindVariableTypes, index);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Set the list of bound property type in first scope. <br />
     * You can specify original value types for properties in first scope.
     * @param firstBoundPropTypeList The list of bound property type. (NullAllowed)
     */
    public void setFirstBoundPropTypeList(List<TnPropertyType> firstBoundPropTypeList) {
        _firstBoundPropTypeList = firstBoundPropTypeList;
    }
}
