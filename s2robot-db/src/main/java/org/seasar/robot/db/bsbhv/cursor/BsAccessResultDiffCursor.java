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
package org.seasar.robot.db.bsbhv.cursor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.dbflute.util.DfTypeUtil;

/**
 * The cursor of AccessResultDiff.
 * @author DBFlute(AutoGenerator)
 */
public class BsAccessResultDiffCursor {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    // -----------------------------------------------------
    //                                        Column DB Name
    //                                        --------------
    /** DB name of ID. */
    public static final String DB_NAME_ID = "ID";

    /** DB name of SESSION_ID. */
    public static final String DB_NAME_SESSION_ID = "SESSION_ID";

    /** DB name of RULE_ID. */
    public static final String DB_NAME_RULE_ID = "RULE_ID";

    /** DB name of URL. */
    public static final String DB_NAME_URL = "URL";

    /** DB name of PARENT_URL. */
    public static final String DB_NAME_PARENT_URL = "PARENT_URL";

    /** DB name of STATUS. */
    public static final String DB_NAME_STATUS = "STATUS";

    /** DB name of HTTP_STATUS_CODE. */
    public static final String DB_NAME_HTTP_STATUS_CODE = "HTTP_STATUS_CODE";

    /** DB name of METHOD. */
    public static final String DB_NAME_METHOD = "METHOD";

    /** DB name of MIME_TYPE. */
    public static final String DB_NAME_MIME_TYPE = "MIME_TYPE";

    /** DB name of CONTENT_LENGTH. */
    public static final String DB_NAME_CONTENT_LENGTH = "CONTENT_LENGTH";

    /** DB name of EXECUTION_TIME. */
    public static final String DB_NAME_EXECUTION_TIME = "EXECUTION_TIME";

    /** DB name of CREATE_TIME. */
    public static final String DB_NAME_CREATE_TIME = "CREATE_TIME";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Wrapped result set. */
    protected ResultSet _rs;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsAccessResultDiffCursor() {
    }

    // ===================================================================================
    //                                                                             Prepare
    //                                                                             =======
    /**
     * Accept result set.
     * @param rs Result set. (NotNull)
     */
    public void accept(ResultSet rs) {
        this._rs = rs;
    }

    // ===================================================================================
    //                                                                              Direct
    //                                                                              ======
    /**
     * Get wrapped result set.
     * @return Wrapped result set. (NotNull)
     */
    public java.sql.ResultSet cursor() {
        return _rs;
    }

    // ===================================================================================
    //                                                                            Delegate
    //                                                                            ========
    /**
     * Move to next result.
     * @return Is exist next result.
     * @throws java.sql.SQLException
     */
    public boolean next() throws SQLException {
        return _rs.next();
    }

    // ===================================================================================
    //                                                                  Type Safe Accessor
    //                                                                  ==================
    /**
     * Get the value of id.
     * @return The value of id. (Nullable)
     * @throws java.sql.SQLException
     */
    public Long getId() throws SQLException {
        return (Long) extractValueAsNumber(Long.class, "ID");
    }

    /**
     * Get the value of sessionId.
     * @return The value of sessionId. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getSessionId() throws SQLException {
        return extractValueAsString("SESSION_ID");
    }

    /**
     * Get the value of ruleId.
     * @return The value of ruleId. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getRuleId() throws SQLException {
        return extractValueAsString("RULE_ID");
    }

    /**
     * Get the value of url.
     * @return The value of url. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getUrl() throws SQLException {
        return extractValueAsString("URL");
    }

    /**
     * Get the value of parentUrl.
     * @return The value of parentUrl. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getParentUrl() throws SQLException {
        return extractValueAsString("PARENT_URL");
    }

    /**
     * Get the value of status.
     * @return The value of status. (Nullable)
     * @throws java.sql.SQLException
     */
    public Integer getStatus() throws SQLException {
        return (Integer) extractValueAsNumber(Integer.class, "STATUS");
    }

    /**
     * Get the value of httpStatusCode.
     * @return The value of httpStatusCode. (Nullable)
     * @throws java.sql.SQLException
     */
    public Integer getHttpStatusCode() throws SQLException {
        return (Integer) extractValueAsNumber(Integer.class, "HTTP_STATUS_CODE");
    }

    /**
     * Get the value of method.
     * @return The value of method. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getMethod() throws SQLException {
        return extractValueAsString("METHOD");
    }

    /**
     * Get the value of mimeType.
     * @return The value of mimeType. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getMimeType() throws SQLException {
        return extractValueAsString("MIME_TYPE");
    }

    /**
     * Get the value of contentLength.
     * @return The value of contentLength. (Nullable)
     * @throws java.sql.SQLException
     */
    public Long getContentLength() throws SQLException {
        return (Long) extractValueAsNumber(Long.class, "CONTENT_LENGTH");
    }

    /**
     * Get the value of executionTime.
     * @return The value of executionTime. (Nullable)
     * @throws java.sql.SQLException
     */
    public Integer getExecutionTime() throws SQLException {
        return (Integer) extractValueAsNumber(Integer.class, "EXECUTION_TIME");
    }

    /**
     * Get the value of createTime.
     * @return The value of createTime. (Nullable)
     * @throws java.sql.SQLException
     */
    public java.sql.Timestamp getCreateTime() throws SQLException {
        return (java.sql.Timestamp) extractValueAsDate(
                java.sql.Timestamp.class, "CREATE_TIME");
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected String extractValueAsString(String name) throws SQLException {
        return _rs.getString(name);
    }

    protected Boolean extractValueAsBoolean(String name) throws SQLException {
        return _rs.getBoolean(name);
    }

    protected Object extractValueAsNumber(Class<?> type, String name)
            throws SQLException {
        return DfTypeUtil.toNumber(type, extractValueAsObject(name));
    }

    protected Object extractValueAsDate(Class<?> type, String name)
            throws SQLException {
        if (type.isAssignableFrom(java.sql.Timestamp.class)) {
            return _rs.getTimestamp(name);
        } else if (type.isAssignableFrom(java.sql.Date.class)) {
            return _rs.getDate(name);
        } else if (type.isAssignableFrom(java.util.Date.class)) {
            return toDate(_rs.getTimestamp(name));
        } else {
            return toDate(extractValueAsObject(name));
        }
    }

    protected java.util.Date toDate(Object object) {
        return DfTypeUtil.toDate(object);
    }

    protected Object extractValueAsObject(String name) throws SQLException {
        return _rs.getObject(name);
    }
}
