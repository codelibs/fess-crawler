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
package org.seasar.robot.dbflute.resource;

import java.sql.SQLException;
import java.sql.Statement;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.exception.EntityAlreadyExistsException;
import org.seasar.robot.dbflute.exception.SQLFailureException;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class SQLExceptionHandler {

    /**
     * @param e The instance of SQLException. (NotNull)
     */
    public void handleSQLException(SQLException e) {
        handleSQLException(e, null, false);
    }

    /**
     * @param e The instance of SQLException. (NotNull)
     * @param statement The instance of statement. (Nullable)
     */
    public void handleSQLException(SQLException e, Statement statement) {
        handleSQLException(e, statement, false);
    }

    public void handleSQLException(SQLException e, Statement statement, boolean uniqueConstraintValid) {
        handleSQLException(e, statement, uniqueConstraintValid, null);
    }

    public void handleSQLException(SQLException e, Statement statement, boolean uniqueConstraintValid,
            String completeSql) {
        if (uniqueConstraintValid && isUniqueConstraintException(e)) {
            throwEntityAlreadyExistsException(e, statement, completeSql);
        }
        throwSQLFailureException(e, statement, completeSql);
    }

    protected boolean isUniqueConstraintException(SQLException e) {
        if (!ResourceContext.isExistResourceContextOnThread()) {
            return false;
        }
        return ResourceContext.isUniqueConstraintException(extractSQLState(e), e.getErrorCode());
    }

    protected void throwEntityAlreadyExistsException(SQLException e, Statement statement, String completeSql) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The entity already exists on the database!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the primary key whether it already exists on the database." + ln();
        msg = msg + "And confirm the unique constraint for other columns." + ln();
        msg = msg + ln();
        msg = msg + "[SQLState]" + ln() + extractSQLState(e) + ln();
        msg = msg + ln();
        msg = msg + "[ErrorCode]" + ln() + e.getErrorCode() + ln();
        msg = msg + ln();
        msg = msg + "[SQLException]" + ln() + e.getClass().getName() + ln();
        msg = msg + extractMessage(e) + ln();
        SQLException nextEx = e.getNextException();
        if (nextEx != null) {
            msg = msg + ln();
            msg = msg + "[NextException]" + ln();
            msg = msg + nextEx.getClass().getName() + ln();
            msg = msg + extractMessage(nextEx) + ln();
            SQLException nextNextEx = nextEx.getNextException();
            if (nextNextEx != null) {
                msg = msg + ln();
                msg = msg + "[NextNextException]" + ln();
                msg = msg + nextNextEx.getClass().getName() + ln();
                msg = msg + extractMessage(nextNextEx) + ln();
            }
        }
        Object invokeName = extractBehaviorInvokeName();
        if (invokeName != null) {
            msg = msg + ln();
            msg = msg + "[Behavior]" + ln();
            msg = msg + invokeName + ln();
        }
        if (hasConditionBean()) {
            msg = msg + ln();
            msg = msg + "[ConditionBean]" + ln();
            msg = msg + getConditionBean().getClass().getName() + ln();
        }
        if (hasOutsideSqlContext()) {
            msg = msg + ln();
            msg = msg + "[OutsideSql]" + ln();
            msg = msg + getOutsideSqlContext().getOutsideSqlPath() + ln();
            msg = msg + ln();
            msg = msg + "[ParameterBean]" + ln();
            Object pmb = getOutsideSqlContext().getParameterBean();
            if (pmb != null) {
                msg = msg + pmb.getClass().getName() + ln();
                msg = msg + pmb + ln();
            } else {
                msg = msg + "null" + ln();
            }
        }
        if (statement != null) {
            msg = msg + ln();
            msg = msg + "[Statement]" + ln();
            msg = msg + statement.getClass().getName() + ln();
        }
        if (completeSql != null) {
            msg = msg + ln();
            msg = msg + "[Display SQL]" + ln();
            msg = msg + completeSql + ln();
        }
        msg = msg + "* * * * * * * * * */";
        throw new EntityAlreadyExistsException(msg, e);
    }

    protected void throwSQLFailureException(SQLException e, Statement statement, String completeSql) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The SQL failed to execute!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the SQLException message." + ln();
        msg = msg + ln();
        msg = msg + "[SQLState]" + ln() + extractSQLState(e) + ln();
        msg = msg + ln();
        msg = msg + "[ErrorCode]" + ln() + e.getErrorCode() + ln();
        msg = msg + ln();
        msg = msg + "[SQLException]" + ln() + e.getClass().getName() + ln();
        msg = msg + extractMessage(e) + ln();
        SQLException nextEx = e.getNextException();
        if (nextEx != null) {
            msg = msg + ln();
            msg = msg + "[NextException]" + ln();
            msg = msg + nextEx.getClass().getName() + ln();
            msg = msg + extractMessage(nextEx) + ln();
            SQLException nextNextEx = nextEx.getNextException();
            if (nextNextEx != null) {
                msg = msg + ln();
                msg = msg + "[NextNextException]" + ln();
                msg = msg + nextNextEx.getClass().getName() + ln();
                msg = msg + extractMessage(nextNextEx) + ln();
            }
        }
        Object invokeName = extractBehaviorInvokeName();
        if (invokeName != null) {
            msg = msg + ln();
            msg = msg + "[Behavior]" + ln();
            msg = msg + invokeName + ln();
        }
        if (hasConditionBean()) {
            msg = msg + ln();
            msg = msg + "[ConditionBean]" + ln();
            msg = msg + getConditionBean().getClass().getName() + ln();
        }
        if (hasOutsideSqlContext()) {
            msg = msg + ln();
            msg = msg + "[OutsideSql]" + ln();
            msg = msg + getOutsideSqlContext().getOutsideSqlPath() + ln();
            msg = msg + ln();
            msg = msg + "[ParameterBean]" + ln();
            Object pmb = getOutsideSqlContext().getParameterBean();
            if (pmb != null) {
                msg = msg + pmb.getClass().getName() + ln();
                msg = msg + pmb + ln();
            } else {
                msg = msg + "null" + ln();
            }
        }
        if (statement != null) {
            msg = msg + ln();
            msg = msg + "[Statement]" + ln();
            msg = msg + statement.getClass().getName() + ln();
        }
        if (completeSql != null) {
            msg = msg + ln();
            msg = msg + "[Display SQL]" + ln();
            msg = msg + completeSql + ln();
        }
        msg = msg + "* * * * * * * * * */";
        throw new SQLFailureException(msg, e);
    }

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
        final Object behaviorInvokeName = InternalMapContext.getObject("df:BehaviorInvokeName");
        if (behaviorInvokeName == null) {
            return null;
        }
        final Object clientInvokeName = InternalMapContext.getObject("df:ClientInvokeName");
        final Object byPassInvokeName = InternalMapContext.getObject("df:ByPassInvokeName");
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
