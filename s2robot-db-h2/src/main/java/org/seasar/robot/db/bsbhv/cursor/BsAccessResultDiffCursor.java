package org.seasar.robot.db.bsbhv.cursor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes;

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

    protected ValueType _vtId = vt(Long.class);

    protected ValueType _vtSessionId = vt(String.class);

    protected ValueType _vtRuleId = vt(String.class);

    protected ValueType _vtUrl = vt(String.class);

    protected ValueType _vtParentUrl = vt(String.class);

    protected ValueType _vtStatus = vt(Integer.class);

    protected ValueType _vtHttpStatusCode = vt(Integer.class);

    protected ValueType _vtMethod = vt(String.class);

    protected ValueType _vtMimeType = vt(String.class);

    protected ValueType _vtContentLength = vt(Long.class);

    protected ValueType _vtExecutionTime = vt(Integer.class);

    protected ValueType _vtCreateTime = vt(java.sql.Timestamp.class);

    protected ValueType vt(Class<?> type) {
        return TnValueTypes.getValueType(type);
    }

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
        return (Long) _vtId.getValue(_rs, DB_NAME_ID);
    }

    /**
     * Get the value of sessionId.
     * @return The value of sessionId. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getSessionId() throws SQLException {
        return (String) _vtSessionId.getValue(_rs, DB_NAME_SESSION_ID);
    }

    /**
     * Get the value of ruleId.
     * @return The value of ruleId. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getRuleId() throws SQLException {
        return (String) _vtRuleId.getValue(_rs, DB_NAME_RULE_ID);
    }

    /**
     * Get the value of url.
     * @return The value of url. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getUrl() throws SQLException {
        return (String) _vtUrl.getValue(_rs, DB_NAME_URL);
    }

    /**
     * Get the value of parentUrl.
     * @return The value of parentUrl. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getParentUrl() throws SQLException {
        return (String) _vtParentUrl.getValue(_rs, DB_NAME_PARENT_URL);
    }

    /**
     * Get the value of status.
     * @return The value of status. (Nullable)
     * @throws java.sql.SQLException
     */
    public Integer getStatus() throws SQLException {
        return (Integer) _vtStatus.getValue(_rs, DB_NAME_STATUS);
    }

    /**
     * Get the value of httpStatusCode.
     * @return The value of httpStatusCode. (Nullable)
     * @throws java.sql.SQLException
     */
    public Integer getHttpStatusCode() throws SQLException {
        return (Integer) _vtHttpStatusCode.getValue(_rs,
                DB_NAME_HTTP_STATUS_CODE);
    }

    /**
     * Get the value of method.
     * @return The value of method. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getMethod() throws SQLException {
        return (String) _vtMethod.getValue(_rs, DB_NAME_METHOD);
    }

    /**
     * Get the value of mimeType.
     * @return The value of mimeType. (Nullable)
     * @throws java.sql.SQLException
     */
    public String getMimeType() throws SQLException {
        return (String) _vtMimeType.getValue(_rs, DB_NAME_MIME_TYPE);
    }

    /**
     * Get the value of contentLength.
     * @return The value of contentLength. (Nullable)
     * @throws java.sql.SQLException
     */
    public Long getContentLength() throws SQLException {
        return (Long) _vtContentLength.getValue(_rs, DB_NAME_CONTENT_LENGTH);
    }

    /**
     * Get the value of executionTime.
     * @return The value of executionTime. (Nullable)
     * @throws java.sql.SQLException
     */
    public Integer getExecutionTime() throws SQLException {
        return (Integer) _vtExecutionTime.getValue(_rs, DB_NAME_EXECUTION_TIME);
    }

    /**
     * Get the value of createTime.
     * @return The value of createTime. (Nullable)
     * @throws java.sql.SQLException
     */
    public java.sql.Timestamp getCreateTime() throws SQLException {
        return (java.sql.Timestamp) _vtCreateTime.getValue(_rs,
                DB_NAME_CREATE_TIME);
    }

}
