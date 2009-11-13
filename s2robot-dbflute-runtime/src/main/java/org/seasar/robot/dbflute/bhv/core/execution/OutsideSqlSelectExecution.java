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
package org.seasar.robot.dbflute.bhv.core.execution;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.s2dao.jdbc.TnResultSetHandler;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnAbstractDynamicCommand;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnBasicSelectHandler;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.util.DfStringUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;



/**
 * @author jflute
 */
public class OutsideSqlSelectExecution extends TnAbstractDynamicCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The handler of resultSet. */
    protected TnResultSetHandler resultSetHandler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param dataSource Data source.
     * @param statementFactory The factory of statement.
     * @param resultSetHandler The handler of resultSet.
     */
    public OutsideSqlSelectExecution(DataSource dataSource, StatementFactory statementFactory, TnResultSetHandler resultSetHandler) {
        super(dataSource, statementFactory);
        this.resultSetHandler = resultSetHandler;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    /**
     * @param args The array of argument. (NotNull, The first element should be the instance of Pmb)
     * @return The object of execution result. (Nullable)
     */
    public Object execute(Object[] args) {
        final OutsideSqlContext outsideSqlContext = OutsideSqlContext.getOutsideSqlContextOnThread();
        if (outsideSqlContext.isDynamicBinding()) {
            return executeOutsideSqlAsDynamic(args, outsideSqlContext);
        } else {
            return executeOutsideSqlAsStatic(args, outsideSqlContext);
        }
    }

    // -----------------------------------------------------
    //                                    OutsideSql Execute
    //                                    ------------------
    /**
     * Execute outside-SQL as Dynamic.
     * @param args The array of argument. (NotNull, The first element should be the instance of Pmb)
     * @param outsideSqlContext The context of outside-SQL. (NotNull)
     * @return Result. (Nullable)
     */
    protected Object executeOutsideSqlAsDynamic(Object[] args, OutsideSqlContext outsideSqlContext) {
        final Object firstArg = args[0];
        String staticSql = getSql();
        if (firstArg != null) {
            final DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(firstArg.getClass());

            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            // Resolve embedded comment for parsing bind variable comment in embedded comment.
            // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
            final List<String> proppertyNameList = beanDesc.getProppertyNameList();
            for (String proppertyName : proppertyNameList) {
                final DfPropertyDesc propertyDesc = beanDesc.getPropertyDesc(proppertyName);
                final Class<?> propertyType = propertyDesc.getPropertyType();
                if (!propertyType.equals(String.class)) {
                    continue;
                }
                final String outsideSqlPiece = (String) propertyDesc.getValue(firstArg);
                if (outsideSqlPiece == null) {
                    continue;
                }
                final String embeddedComment = "/*$pmb." + propertyDesc.getPropertyName() + "*/";
                staticSql = replaceString(staticSql, embeddedComment, outsideSqlPiece);
            }
        }

        final OutsideSqlSelectExecution outsideSqlCommand = createDynamicSqlFactory();
        outsideSqlCommand.setArgNames(getArgNames());
        outsideSqlCommand.setArgTypes(getArgTypes());
        outsideSqlCommand.setSql(staticSql);

        final CommandContext ctx = outsideSqlCommand.apply(args);
        final List<Object> bindVariableList = new ArrayList<Object>();
        final List<Class<?>> bindVariableTypeList = new ArrayList<Class<?>>();
        addBindVariableInfo(ctx, bindVariableList, bindVariableTypeList);
        final TnBasicSelectHandler selectHandler = createBasicSelectHandler(ctx.getSql(), this.resultSetHandler);
        final Object[] bindVariableArray = bindVariableList.toArray();
        selectHandler.setLoggingMessageSqlArgs(bindVariableArray);
        return selectHandler.execute(bindVariableArray, toClassArray(bindVariableTypeList));
    }

    /**
     * Execute outside-SQL as static.
     * @param args The array of argument. (NotNull, The first element should be the instance of Pmb)
     * @param outsideSqlContext The context of outside-SQL. (NotNull)
     * @return Result. (Nullable)
     */
    protected Object executeOutsideSqlAsStatic(Object[] args, OutsideSqlContext outsideSqlContext) {
        final CommandContext ctx = apply(args);
        final TnBasicSelectHandler selectHandler = createBasicSelectHandler(ctx.getSql(), this.resultSetHandler);
        final Object[] bindVariableArray = ctx.getBindVariables();
        selectHandler.setLoggingMessageSqlArgs(bindVariableArray);
        return selectHandler.execute(bindVariableArray, ctx.getBindVariableTypes());
    }

    // ===================================================================================
    //                                                                 Dynamic SQL Factory
    //                                                                 ===================
    protected OutsideSqlSelectExecution createDynamicSqlFactory() {
        return new OutsideSqlSelectExecution(getDataSource(), getStatementFactory(), resultSetHandler);
    }

    // ===================================================================================
    //                                                                      Select Handler
    //                                                                      ==============
    protected TnBasicSelectHandler createBasicSelectHandler(String realSql, TnResultSetHandler rsh) {
        return new TnBasicSelectHandler(getDataSource(), realSql, rsh, getStatementFactory());
    }

    // ===================================================================================
    //                                                                       Parser Option
    //                                                                       =============
    @Override
    protected boolean isBlockNullParameter() {
        return true; // Because the SQL is select.
    }

    // ===================================================================================
    //                                                                        Setup Helper
    //                                                                        ============
    protected Class<?>[] toClassArray(List<Class<?>> bindVariableTypeList) {
        final Class<?>[] bindVariableTypesArray = new Class<?>[bindVariableTypeList.size()];
        for (int i = 0; i < bindVariableTypeList.size(); i++) {
            final Class<?> bindVariableType = (Class<?>) bindVariableTypeList.get(i);
            bindVariableTypesArray[i] = bindVariableType;
        }
        return bindVariableTypesArray;
    }

    protected void addBindVariableInfo(CommandContext ctx, List<Object> bindVariableList, List<Class<?>> bindVariableTypeList) {
        final Object[] bindVariables = ctx.getBindVariables();
        addBindVariableList(bindVariableList, bindVariables);
        final Class<?>[] bindVariableTypes = ctx.getBindVariableTypes();
        addBindVariableTypeList(bindVariableTypeList, bindVariableTypes);
    }

    protected void addBindVariableList(List<Object> bindVariableList, Object[] bindVariables) {
        for (int i=0; i < bindVariables.length; i++) {
            bindVariableList.add(bindVariables[i]);
        }
    }

    protected void addBindVariableTypeList(List<Class<?>> bindVariableTypeList, Class<?>[] bindVariableTypes) {
        for (int i=0; i < bindVariableTypes.length; i++) {
            bindVariableTypeList.add(bindVariableTypes[i]);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }

    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }
}
