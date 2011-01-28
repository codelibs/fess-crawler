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
package org.seasar.robot.dbflute.s2dao.sqlcommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.bhv.InsertOption;
import org.seasar.robot.dbflute.bhv.UpdateOption;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.helper.StringKeyMap;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnAbstractEntityHandler;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnCommandContextHandler;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnQueryInsertDynamicCommand extends TnAbstractQueryDynamicCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnQueryInsertDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        // analyze arguments
        final Entity entity = extractEntityWithCheck(args);
        final ConditionBean intoCB = extractIntoConditionBeanWithCheck(args);
        final ConditionBean resourceCB = extractResourceConditionBeanWithCheck(args);
        final InsertOption<ConditionBean> option = extractInsertOptionWithCheck(args);

        // arguments for execution (not contains an option)
        final String[] argNames = new String[] { "entity", "pmb" };
        final Class<?>[] argTypes = new Class<?>[] { entity.getClass(), resourceCB.getClass() };
        final Object[] realArgs = new Object[] { entity, resourceCB };

        // prepare context
        final List<TnPropertyType> boundPropTypeList = new ArrayList<TnPropertyType>();
        final CommandContext context;
        {
            final String twoWaySql = buildQueryInsertTwoWaySql(entity, intoCB, resourceCB, option, boundPropTypeList);
            context = createCommandContext(twoWaySql, argNames, argTypes, realArgs);
        }

        // execute
        final TnCommandContextHandler handler = createCommandContextHandler(context);
        handler.setExceptionMessageSqlArgs(context.getBindVariables());
        final boolean identityDisabled = option != null && option.isPrimaryKeyIdentityDisabled();
        if (identityDisabled) {
            disableIdentityGeneration(entity);
        }
        final int rows;
        RuntimeException sqlEx = null;
        try {
            rows = handler.execute(realArgs);
        } catch (RuntimeException e) {
            sqlEx = e;
            throw e;
        } finally {
            if (identityDisabled) {
                try {
                    enableIdentityGeneration(entity);
                } catch (RuntimeException e) {
                    if (sqlEx == null) {
                        throw e;
                    }
                    // ignore the exception when main SQL fails
                    // not to close the main exception
                }
            }
        }
        return Integer.valueOf(rows);
    }

    // ===================================================================================
    //                                                                    Analyze Argument
    //                                                                    ================
    protected Entity extractEntityWithCheck(Object[] args) {
        assertArgument(args);
        final Object fisrtArg = args[0];
        if (!(fisrtArg instanceof Entity)) {
            String msg = "The type of first argument should be " + Entity.class + ":";
            msg = msg + " type=" + fisrtArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        return (Entity) fisrtArg;
    }

    protected ConditionBean extractIntoConditionBeanWithCheck(Object[] args) {
        assertArgument(args);
        final Object secondArg = args[1];
        if (!(secondArg instanceof ConditionBean)) {
            String msg = "The type of second argument should be " + ConditionBean.class + ":";
            msg = msg + " type=" + secondArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        return (ConditionBean) secondArg;
    }

    protected ConditionBean extractResourceConditionBeanWithCheck(Object[] args) {
        assertArgument(args);
        final Object thirdArg = args[2];
        if (!(thirdArg instanceof ConditionBean)) {
            String msg = "The type of third argument should be " + ConditionBean.class + ":";
            msg = msg + " type=" + thirdArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        return (ConditionBean) thirdArg;
    }

    protected InsertOption<ConditionBean> extractInsertOptionWithCheck(Object[] args) {
        assertArgument(args);
        if (args.length < 4) {
            return null;
        }
        final Object fourthArg = args[3];
        if (fourthArg == null) {
            return null;
        }
        if (!(fourthArg instanceof InsertOption<?>)) {
            String msg = "The type of fourth argument should be " + UpdateOption.class + ":";
            msg = msg + " type=" + fourthArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        @SuppressWarnings("unchecked")
        final InsertOption<ConditionBean> option = (InsertOption<ConditionBean>) fourthArg;
        return option;
    }

    protected void assertArgument(Object[] args) {
        if (args == null || args.length <= 1) {
            String msg = "The arguments should have two argument at least! But:";
            msg = msg + " args=" + (args != null ? args.length : null);
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                           Build SQL
    //                                                                           =========
    /**
     * @param entity The entity for fixed values. (NotNull)
     * @param intoCB The condition-bean for insert into. (NotNull)
     * @param resourceCB The condition-bean for resource. (NotNull)
     * @param option The option of insert. (NullAllowed)
     * @param boundPropTypeList The type list of bound property. (NotNull, Empty)
     * @return The two-way SQL of query-insert. (NotNull)
     */
    protected String buildQueryInsertTwoWaySql(Entity entity, ConditionBean intoCB, ConditionBean resourceCB,
            InsertOption<ConditionBean> option, List<TnPropertyType> boundPropTypeList) {
        final StringKeyMap<String> fixedValueQueryExpMap = StringKeyMap.createAsFlexibleOrdered();
        final Set<String> modifiedProperties = entity.modifiedProperties();
        final DBMeta dbmeta = entity.getDBMeta();
        final List<ColumnInfo> columnInfoList = dbmeta.getColumnInfoList();
        for (ColumnInfo columnInfo : columnInfoList) {
            final String propertyName = columnInfo.getPropertyName();
            if (!modifiedProperties.contains(propertyName)) {
                continue;
            }
            final Object value = columnInfo.read(entity);
            final String fixedValueQueryExp;
            if (value != null) {
                fixedValueQueryExp = "/*entity." + propertyName + "*/null";
            } else {
                // it uses null literal on query
                // because the SQL analyzer blocks null parameters
                // (the analyzer should do it for condition-bean)
                fixedValueQueryExp = "null";
            }
            fixedValueQueryExpMap.put(columnInfo.getColumnDbName(), fixedValueQueryExp);
        }
        return intoCB.getSqlClause().getClauseQueryInsert(fixedValueQueryExpMap, resourceCB.getSqlClause());
    }

    // ===================================================================================
    //                                                                            Identity
    //                                                                            ========
    protected void disableIdentityGeneration(Entity entity) {
        final String tableDbName = entity.getTableDbName();
        TnAbstractEntityHandler.delegateDisableIdentityGeneration(tableDbName, _dataSource, _statementFactory);
    }

    protected void enableIdentityGeneration(Entity entity) {
        final String tableDbName = entity.getTableDbName();
        TnAbstractEntityHandler.delegateEnableIdentityGeneration(tableDbName, _dataSource, _statementFactory);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replace(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
