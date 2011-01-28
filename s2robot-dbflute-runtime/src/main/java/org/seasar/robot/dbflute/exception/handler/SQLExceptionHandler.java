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
package org.seasar.robot.dbflute.exception.handler;

import java.sql.SQLException;
import java.sql.Statement;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.exception.EntityAlreadyExistsException;
import org.seasar.robot.dbflute.exception.SQLFailureException;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.resource.InternalMapContext;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class SQLExceptionHandler {

    // ===================================================================================
    //                                                                              Handle
    //                                                                              ======
    /**
     * @param e The instance of SQLException. (NotNull)
     */
    public void handleSQLException(SQLException e) {
        handleSQLException(e, null, false);
    }

    /**
     * @param e The instance of SQLException. (NotNull)
     * @param st The instance of statement. (NullAllowed)
     */
    public void handleSQLException(SQLException e, Statement st) {
        handleSQLException(e, st, false);
    }

    /**
     * @param e The instance of SQLException. (NotNull)
     * @param st The instance of statement. (NullAllowed)
     * @param uniqueConstraintValid Is unique constraint handling valid?
     */
    public void handleSQLException(SQLException e, Statement st, boolean uniqueConstraintValid) {
        handleSQLException(e, st, uniqueConstraintValid, null, null);
    }

    /**
     * @param e The instance of SQLException. (NotNull)
     * @param st The instance of statement. (NullAllowed)
     * @param uniqueConstraintValid Is unique constraint handling valid?
     * @param executedSql The executed SQL which does not have bind values. (NullAllowed)
     * @param displaySql The SQL for display which has bind values (embedded on SQL). (NullAllowed)
     */
    public void handleSQLException(SQLException e, Statement st, boolean uniqueConstraintValid, String executedSql,
            String displaySql) {
        if (uniqueConstraintValid && isUniqueConstraintException(e)) {
            throwEntityAlreadyExistsException(e, st, executedSql, displaySql);
        }
        throwSQLFailureException(e, st, executedSql, displaySql);
    }

    protected boolean isUniqueConstraintException(SQLException e) {
        if (!ResourceContext.isExistResourceContextOnThread()) {
            return false;
        }
        return ResourceContext.isUniqueConstraintException(extractSQLState(e), e.getErrorCode());
    }

    // ===================================================================================
    //                                                                               Throw
    //                                                                               =====
    protected void throwEntityAlreadyExistsException(SQLException e, Statement st, String executedSql, String displaySql) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The entity already exists on the database!");
        br.addItem("Advice");
        br.addElement("Please confirm the primary key whether it already exists on the database.");
        br.addElement("And confirm the unique constraint for other columns.");
        setupCommonElement(br, e, st, executedSql, displaySql);
        final String msg = br.buildExceptionMessage();
        throw new EntityAlreadyExistsException(msg, e);
    }

    protected void throwSQLFailureException(SQLException e, Statement st, String executedSql, String displaySql) {
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("The SQL failed to execute!");
        br.addItem("Advice");
        br.addElement("Please confirm the SQLException message.");
        setupCommonElement(br, e, st, executedSql, displaySql);
        final String msg = br.buildExceptionMessage();
        throw new SQLFailureException(msg, e);
    }

    protected ExceptionMessageBuilder createExceptionMessageBuilder() {
        return new ExceptionMessageBuilder();
    }

    // ===================================================================================
    //                                                                             Element
    //                                                                             =======
    protected void setupCommonElement(ExceptionMessageBuilder br, SQLException e, Statement st, String executedSql,
            String displaySql) {
        br.addItem("SQLState");
        br.addElement(extractSQLState(e));
        br.addItem("ErrorCode");
        br.addElement(e.getErrorCode());
        setupSQLExceptionElement(br, e);
        setupBehaviorElement(br);
        setupConditionBeanElement(br);
        setupOutsideSqlElement(br);
        setupStatementElement(br, st);
        setupTargetSqlElement(br, executedSql, displaySql);
    }

    protected void setupSQLExceptionElement(ExceptionMessageBuilder br, SQLException e) {
        br.addItem("SQLException");
        br.addElement(e.getClass().getName());
        br.addElement(extractMessage(e));
        final SQLException nextEx = e.getNextException();
        if (nextEx != null) {
            br.addItem("NextException");
            br.addElement(nextEx.getClass().getName());
            br.addElement(extractMessage(nextEx));
            final SQLException nextNextEx = nextEx.getNextException();
            if (nextNextEx != null) {
                br.addItem("NextNextException");
                br.addElement(nextNextEx.getClass().getName());
                br.addElement(extractMessage(nextNextEx));
            }
        }
    }

    protected void setupBehaviorElement(ExceptionMessageBuilder br) {
        final Object invokeName = extractBehaviorInvokeName();
        if (invokeName != null) {
            br.addItem("Behavior");
            br.addElement(invokeName);
        }
    }

    protected void setupConditionBeanElement(ExceptionMessageBuilder br) {
        if (hasConditionBean()) {
            br.addItem("ConditionBean"); // only class name because of already existing displaySql
            br.addElement(getConditionBean().getClass().getName());
        }
    }

    protected void setupOutsideSqlElement(ExceptionMessageBuilder br) {
        if (hasOutsideSqlContext()) {
            br.addItem("OutsideSql");
            br.addElement(getOutsideSqlContext().getOutsideSqlPath());
        }
    }

    // *because displaySql exists instead which is enough to debug the exception
    //  (and for security to application data)
    //protected void setupParameterBeanElement(ExceptionMessageBuilder br) {
    //    if (hasOutsideSqlContext()) {
    //        br.addItem("ParameterBean");
    //        br.addElement(getOutsideSqlContext().getParameterBean());
    //    }
    //}

    protected void setupStatementElement(ExceptionMessageBuilder br, Statement st) {
        if (st != null) {
            br.addItem("Statement");
            br.addElement(st.getClass().getName());
        }
    }

    /**
     * Set up the element of target SQL. <br />
     * It uses displaySql as default.
     * <p>
     * If you want to hide application data on exception message,
     * you should override and use executedSql instead of displaySql or set up nothing.
     * But you should consider the following things:
     * </p>
     * <ul>
     *     <li>Debug process becomes more difficult.</li>
     *     <li>If you use embedded variables in the SQL, executedSql may also have application data.</li>
     *     <li>JDBC driver's message may also have application data about exception's cause.</li>
     * </ul>
     * <p>
     * So if you want to COMPLETELY hide application data on exception message,
     * you should cipher your application logs (files).
     * (If you hide JDBC driver's message too, you'll be at a loss when you debug)
     * </p>
     * @param br The builder of exception message. (NotNull)
     * @param executedSql The executed SQL which does not have bind values. (NullAllowed)
     * @param displaySql The SQL for display which has bind values (embedded on SQL). (NullAllowed)
     */
    protected void setupTargetSqlElement(ExceptionMessageBuilder br, String executedSql, String displaySql) {
        if (displaySql != null) {
            br.addItem("Display SQL");
            br.addElement(displaySql);
        }
        //if (executedSql != null) {
        //    br.addItem("Executed SQL");
        //    br.addElement(executedSql);
        //    br.addElement("*NOT use displaySql for security");
        //}
    }

    // ===================================================================================
    //                                                                             Extract
    //                                                                             =======
    protected String extractMessage(SQLException e) {
        String message = e.getMessage();

        // Because a message of Oracle contains a line separator.
        return message != null ? message.trim() : message;
    }

    protected String extractSQLState(SQLException e) {
        String sqlState = e.getSQLState();
        if (sqlState != null) {
            return sqlState;
        }

        // Next
        SQLException nextEx = e.getNextException();
        if (nextEx == null) {
            return null;
        }
        sqlState = nextEx.getSQLState();
        if (sqlState != null) {
            return sqlState;
        }

        // Next Next
        SQLException nextNextEx = nextEx.getNextException();
        if (nextNextEx == null) {
            return null;
        }
        sqlState = nextNextEx.getSQLState();
        if (sqlState != null) {
            return sqlState;
        }

        // Next Next Next
        SQLException nextNextNextEx = nextNextEx.getNextException();
        if (nextNextNextEx == null) {
            return null;
        }
        sqlState = nextNextNextEx.getSQLState();
        if (sqlState != null) {
            return sqlState;
        }

        // It doesn't use recursive call by design because JDBC is unpredictable fellow.
        return null;
    }

    protected String extractBehaviorInvokeName() {
        final Object behaviorInvokeName = InternalMapContext.getBehaviorInvokeName();
        if (behaviorInvokeName == null) {
            return null;
        }
        final Object clientInvokeName = InternalMapContext.getClientInvokeName();
        final Object byPassInvokeName = InternalMapContext.getByPassInvokeName();
        final StringBuilder sb = new StringBuilder();
        boolean existsPath = false;
        if (clientInvokeName != null) {
            existsPath = true;
            sb.append(clientInvokeName);
        }
        if (byPassInvokeName != null) {
            existsPath = true;
            sb.append(byPassInvokeName);
        }
        sb.append(behaviorInvokeName);
        if (existsPath) {
            sb.append("...");
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected boolean hasConditionBean() {
        return ConditionBeanContext.isExistConditionBeanOnThread();
    }

    protected ConditionBean getConditionBean() {
        return ConditionBeanContext.getConditionBeanOnThread();
    }

    protected boolean hasOutsideSqlContext() {
        return OutsideSqlContext.isExistOutsideSqlContextOnThread();
    }

    protected OutsideSqlContext getOutsideSqlContext() {
        return OutsideSqlContext.getOutsideSqlContextOnThread();
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
