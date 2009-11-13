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
package org.seasar.robot.dbflute.s2dao.sqlcommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.bhv.core.SqlExecution;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.exception.QueryUpdateFailureException;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnCommandContextHandler;
import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.context.CommandContext;
import org.seasar.robot.dbflute.twowaysql.context.CommandContextCreator;
import org.seasar.robot.dbflute.twowaysql.node.Node;
import org.seasar.robot.dbflute.util.DfStringUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnUpdateQueryAutoDynamicCommand implements TnSqlCommand, SqlExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DataSource dataSource;
    protected StatementFactory statementFactory;
    private TnBeanMetaData beanMetaData;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateQueryAutoDynamicCommand(DataSource dataSource, StatementFactory statementFactory) {
        this.dataSource = dataSource;
        this.statementFactory = statementFactory;
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public Object execute(Object[] args) {
        ConditionBean cb = extractConditionBeanWithCheck(args);
        Entity entity = extractEntityWithCheck(args);
        String[] argNames = new String[] { "pmb", "entity" };
        Class<?>[] argTypes = new Class<?>[] { cb.getClass(), entity.getClass() };
        List<TnPropertyType> propertyTypeList = new ArrayList<TnPropertyType>();
        String twoWaySql = buildQueryUpdateTwoWaySql(entity, cb, propertyTypeList);
        if (twoWaySql == null) {
            return 0; // No execute!
        }
        CommandContext context = createCommandContext(twoWaySql, argNames, argTypes, args);
        TnCommandContextHandler handler = createCommandContextHandler(context);
        handler.setLoggingMessageSqlArgs(context.getBindVariables());
        handler.setPropertyTypeList(propertyTypeList);
        int rows = handler.execute(args);
        return Integer.valueOf(rows);
    }

    protected ConditionBean extractConditionBeanWithCheck(Object[] args) {
        assertArgument(args);
        Object fisrtArg = args[0];
        if (!(fisrtArg instanceof ConditionBean)) {
            String msg = "The type of first argument should be " + ConditionBean.class + "! But:";
            msg = msg + " type=" + fisrtArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        return (ConditionBean) fisrtArg;
    }

    protected Entity extractEntityWithCheck(Object[] args) {
        assertArgument(args);
        Object secondArg = args[1];
        if (!(secondArg instanceof Entity)) {
            String msg = "The type of second argument should be " + Entity.class + "! But:";
            msg = msg + " type=" + secondArg.getClass();
            throw new IllegalArgumentException(msg);
        }
        return (Entity) secondArg;
    }

    protected void assertArgument(Object[] args) {
        if (args == null || args.length <= 1) {
            String msg = "The arguments should have two argument! But:";
            msg = msg + " args=" + (args != null ? args.length : null);
            throw new IllegalArgumentException(msg);
        }
    }

    protected TnCommandContextHandler createCommandContextHandler(CommandContext context) {
        return new TnCommandContextHandler(dataSource, statementFactory, context);
    }

    /**
     * @param entity Entity. (NotNull)
     * @param cb Condition-bean. (NotNull)
     * @param propertyTypeList The list of property type. (NotNull, ShouldBeEmpty)
     * @return The two-way SQL of query update. (Nullable: If the set of modified properties is empty, return null.)
     */
    protected String buildQueryUpdateTwoWaySql(Entity entity, ConditionBean cb, List<TnPropertyType> propertyTypeList) {
        Map<String, String> columnParameterMap = new LinkedHashMap<String, String>();
        DBMeta dbmeta = entity.getDBMeta();
        Set<String> modifiedPropertyNames = entity.getModifiedPropertyNames();
        if (modifiedPropertyNames.isEmpty()) {
            return null;
        }
        Map<String, TnPropertyType> propertyTypeMap = beanMetaData.getPropertyTypeMap();
        String currentPropertyName = null;
        try {
            for (String propertyName : modifiedPropertyNames) {
                currentPropertyName = propertyName;
                ColumnInfo columnInfo = dbmeta.findColumnInfo(propertyName);
                String columnName = columnInfo.getColumnDbName();
                Method getter = columnInfo.findGetter();
                Object value = getter.invoke(entity, (Object[]) null);
                if (value != null) {
                    columnParameterMap.put(columnName, "/*entity." + propertyName + "*/null");

                    // Add property type
                    TnPropertyType propertyType = propertyTypeMap.get(propertyName);
                    propertyTypeList.add(propertyType);
                } else {
                    columnParameterMap.put(columnName, "null");
                }
            }
            if (dbmeta.hasVersionNo()) {
                ColumnInfo columnInfo = dbmeta.getVersionNoColumnInfo();
                String columnName = columnInfo.getColumnDbName();
                columnParameterMap.put(columnName, columnName + " + 1");
            }
            if (dbmeta.hasUpdateDate()) {
                ColumnInfo columnInfo = dbmeta.getUpdateDateColumnInfo();
                Method setter = columnInfo.findSetter();
                setter.invoke(entity, ResourceContext.getAccessTimestamp());
                String columnName = columnInfo.getColumnDbName();
                String propertyName = columnInfo.getPropertyName();
                columnParameterMap.put(columnName, "/*entity." + propertyName + "*/null");

                // Add property type
                TnPropertyType propertyType = propertyTypeMap.get(propertyName);
                propertyTypeList.add(propertyType);
            }
        } catch (RuntimeException e) {
            throwQueryUpdateFailureException(cb, entity, currentPropertyName, e);
        } catch (IllegalAccessException e) {
            throwQueryUpdateFailureException(cb, entity, currentPropertyName, e);
        } catch (InvocationTargetException e) {
            throwQueryUpdateFailureException(cb, entity, currentPropertyName, e.getCause());
        }
        return cb.getSqlClause().getClauseQueryUpdate(columnParameterMap);
    }

    protected void throwQueryUpdateFailureException(ConditionBean cb, Entity entity, String propertyName, Throwable e) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "Failed to execute query-update!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the parameter comment logic." + ln();
        msg = msg + "It may exist the parameter comment that DOESN'T have an end comment." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    before (x) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3" + ln();
        msg = msg + "    after  (o) -- /*IF pmb.xxxId != null*/XXX_ID = /*pmb.xxxId*/3/*END*/" + ln();
        msg = msg + ln();
        msg = msg + "[Doubtful Property Name]" + ln() + propertyName + ln();
        msg = msg + ln();
        msg = msg + "[ConditionBean]" + ln() + cb + ln();
        msg = msg + ln();
        msg = msg + "[Entity]" + ln() + entity + ln();
        msg = msg + ln();
        msg = msg + "[Exception Message]" + ln() + e.getMessage() + ln();
        msg = msg + "* * * * * * * * * */";
        throw new QueryUpdateFailureException(msg, e);
    }

    protected CommandContext createCommandContext(String twoWaySql, String[] argNames, Class<?>[] argTypes,
            Object[] args) {
        CommandContext context;
        {
            SqlAnalyzer analyzer = createSqlAnalyzer(twoWaySql);
            Node node = analyzer.analyze();
            CommandContextCreator creator = new CommandContextCreator(argNames, argTypes);
            context = creator.createCommandContext(args);
            node.accept(context);
        }
        return context;
    }

    protected SqlAnalyzer createSqlAnalyzer(String sql) {
        return ResourceContext.createSqlAnalyzer(sql, true);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replace(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public TnBeanMetaData getBeanMetaData() {
        return beanMetaData;
    }

    public void setBeanMetaData(TnBeanMetaData beanMetaData) {
        this.beanMetaData = beanMetaData;
    }
}
