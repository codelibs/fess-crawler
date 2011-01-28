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
package org.seasar.robot.dbflute.cbean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;
import org.seasar.robot.dbflute.twowaysql.factory.SqlAnalyzerFactory;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The context of condition-bean.
 * @author jflute
 */
public class ConditionBeanContext {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(ConditionBeanContext.class);

    // ===================================================================================
    //                                                             ConditionBean on Thread
    //                                                             =======================
    /** The thread-local for condition-bean. */
    private static final ThreadLocal<ConditionBean> _conditionBeanLocal = new ThreadLocal<ConditionBean>();

    /**
     * Get condition-bean on thread.
     * @return Condition-bean. (NullAllowed)
     */
    public static ConditionBean getConditionBeanOnThread() {
        return (ConditionBean) _conditionBeanLocal.get();
    }

    /**
     * Set condition-bean on thread.
     * @param cb Condition-bean. (NotNull)
     */
    public static void setConditionBeanOnThread(ConditionBean cb) {
        if (cb == null) {
            String msg = "The argument[cb] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _conditionBeanLocal.set(cb);
    }

    /**
     * Is existing condition-bean on thread?
     * @return Determination.
     */
    public static boolean isExistConditionBeanOnThread() {
        return (_conditionBeanLocal.get() != null);
    }

    /**
     * Clear condition-bean on thread.
     */
    public static void clearConditionBeanOnThread() {
        _conditionBeanLocal.set(null);
    }

    // ===================================================================================
    //                                                          EntityRowHandler on Thread
    //                                                          ==========================
    /** The thread-local for entity row handler. */
    private static final ThreadLocal<EntityRowHandler<? extends Entity>> _entityRowHandlerLocal = new ThreadLocal<EntityRowHandler<? extends Entity>>();

    /**
     * Get the handler of entity row. on thread.
     * @return The handler of entity row. (NullAllowed)
     */
    public static EntityRowHandler<? extends Entity> getEntityRowHandlerOnThread() {
        return (EntityRowHandler<? extends Entity>) _entityRowHandlerLocal.get();
    }

    /**
     * Set the handler of entity row on thread.
     * @param handler The handler of entity row. (NotNull)
     */
    public static void setEntityRowHandlerOnThread(EntityRowHandler<? extends Entity> handler) {
        if (handler == null) {
            String msg = "The argument[handler] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _entityRowHandlerLocal.set(handler);
    }

    /**
     * Is existing the handler of entity row on thread?
     * @return Determination.
     */
    public static boolean isExistEntityRowHandlerOnThread() {
        return (_entityRowHandlerLocal.get() != null);
    }

    /**
     * Clear the handler of entity row on thread.
     */
    public static void clearEntityRowHandlerOnThread() {
        _entityRowHandlerLocal.set(null);
    }

    // ===================================================================================
    //                                                                  Type Determination
    //                                                                  ==================
    /**
     * Is the argument condition-bean?
     * @param dtoInstance DTO instance.
     * @return Determination.
     */
    public static boolean isTheArgumentConditionBean(final Object dtoInstance) {
        return dtoInstance instanceof ConditionBean;
    }

    /**
     * Is the type condition-bean?
     * @param dtoClass DtoClass.
     * @return Determination.
     */
    public static boolean isTheTypeConditionBean(final Class<?> dtoClass) {
        return ConditionBean.class.isAssignableFrom(dtoClass);
    }

    // ===================================================================================
    //                                                                        Cool Classes
    //                                                                        ============
    public static void loadCoolClasses() {
        boolean debugEnabled = false; // If you watch the log, set this true.
        // Against the ClassLoader Headache for S2Container's HotDeploy!
        // However, These classes are in Library since 0.9.0
        // so this process may not be needed...
        final StringBuilder sb = new StringBuilder();
        {
            final Class<?> clazz = org.seasar.robot.dbflute.cbean.SimplePagingBean.class;
            if (debugEnabled) {
                sb.append("  ...Loading class of " + clazz.getName() + " by " + clazz.getClassLoader().getClass())
                        .append(ln());
            }
        }
        {
            loadClass(org.seasar.robot.dbflute.AccessContext.class);
            loadClass(org.seasar.robot.dbflute.CallbackContext.class);
            loadClass(org.seasar.robot.dbflute.cbean.EntityRowHandler.class);
            loadClass(org.seasar.robot.dbflute.cbean.coption.FromToOption.class);
            loadClass(org.seasar.robot.dbflute.cbean.coption.LikeSearchOption.class);
            loadClass(org.seasar.robot.dbflute.cbean.grouping.GroupingOption.class);
            loadClass(org.seasar.robot.dbflute.cbean.grouping.GroupingRowEndDeterminer.class);
            loadClass(org.seasar.robot.dbflute.cbean.grouping.GroupingRowResource.class);
            loadClass(org.seasar.robot.dbflute.cbean.grouping.GroupingRowSetupper.class);
            loadClass(org.seasar.robot.dbflute.cbean.pagenavi.PageNumberLink.class);
            loadClass(org.seasar.robot.dbflute.cbean.pagenavi.PageNumberLinkSetupper.class);
            loadClass(org.seasar.robot.dbflute.jdbc.CursorHandler.class);
            if (debugEnabled) {
                sb.append("  ...Loading class of ...and so on");
            }
        }
        if (debugEnabled) {
            _log.debug("{Initialize against the ClassLoader Headache}" + ln() + sb);
        }
    }

    protected static void loadClass(Class<?> clazz) { // for avoiding Find-Bugs warnings
        // do nothing
    }

    // ===================================================================================
    //                                                                          DisplaySql
    //                                                                          ==========
    public static String convertConditionBean2DisplaySql(SqlAnalyzerFactory factory, ConditionBean cb,
            String logDateFormat, String logTimestampFormat) {
        final String twoWaySql = cb.getSqlClause().getClause();
        return SqlAnalyzer.convertTwoWaySql2DisplaySql(factory, twoWaySql, cb, logDateFormat, logTimestampFormat);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
