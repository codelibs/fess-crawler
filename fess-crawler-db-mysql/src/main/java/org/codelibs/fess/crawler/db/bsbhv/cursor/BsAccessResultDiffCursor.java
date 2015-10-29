/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.db.bsbhv.cursor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.dbflute.jdbc.ValueType;
import org.dbflute.s2dao.valuetype.TnValueTypes;

/**
 * The cursor of AccessResultDiff. <br>
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
    protected ValueType _vtCreateTime = vt(Long.class);

    protected ValueType vt(Class<?> type) {
        return TnValueTypes.getValueType(type);
    }

    protected ValueType vt(Class<?> type, String name) {
        ValueType valueType = TnValueTypes.getPluginValueType(name);
        return valueType != null ? valueType : vt(type);
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
     * Accept the result set.
     * @param rs The cursor (result set) for the query, which has first pointer. (NotNull)
     */
    public void accept(ResultSet rs) {
        this._rs = rs;
    }

    // ===================================================================================
    //                                                                              Direct
    //                                                                              ======
    /**
     * Get the wrapped cursor (result set).
     * @return The instance of result set. (NotNull)
     */
    public ResultSet cursor() {
        return _rs;
    }

    // ===================================================================================
    //                                                                            Delegate
    //                                                                            ========
    /**
     * Move to next result.
     * @return Is exist next result.
     * @throws SQLException When it fails to move the cursor to next point.
     */
    public boolean next() throws SQLException {
        return _rs.next();
    }

    // ===================================================================================
    //                                                                  Type Safe Accessor
    //                                                                  ==================
    /**
     * [get] ID: {BIGINT(20), refers to ACCESS_RESULT.ID} <br>
     * @return The value of id. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public Long getId() throws SQLException {
        return (Long)_vtId.getValue(_rs, DB_NAME_ID);
    }

    /**
     * [get] SESSION_ID: {VARCHAR(20), refers to ACCESS_RESULT.SESSION_ID} <br>
     * @return The value of sessionId. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public String getSessionId() throws SQLException {
        return (String)_vtSessionId.getValue(_rs, DB_NAME_SESSION_ID);
    }

    /**
     * [get] RULE_ID: {VARCHAR(20), refers to ACCESS_RESULT.RULE_ID} <br>
     * @return The value of ruleId. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public String getRuleId() throws SQLException {
        return (String)_vtRuleId.getValue(_rs, DB_NAME_RULE_ID);
    }

    /**
     * [get] URL: {VARCHAR(21845), refers to ACCESS_RESULT.URL} <br>
     * @return The value of url. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public String getUrl() throws SQLException {
        return (String)_vtUrl.getValue(_rs, DB_NAME_URL);
    }

    /**
     * [get] PARENT_URL: {VARCHAR(21845), refers to ACCESS_RESULT.PARENT_URL} <br>
     * @return The value of parentUrl. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public String getParentUrl() throws SQLException {
        return (String)_vtParentUrl.getValue(_rs, DB_NAME_PARENT_URL);
    }

    /**
     * [get] STATUS: {INT(11), refers to ACCESS_RESULT.STATUS} <br>
     * @return The value of status. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public Integer getStatus() throws SQLException {
        return (Integer)_vtStatus.getValue(_rs, DB_NAME_STATUS);
    }

    /**
     * [get] HTTP_STATUS_CODE: {INT(11), refers to ACCESS_RESULT.HTTP_STATUS_CODE} <br>
     * @return The value of httpStatusCode. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public Integer getHttpStatusCode() throws SQLException {
        return (Integer)_vtHttpStatusCode.getValue(_rs, DB_NAME_HTTP_STATUS_CODE);
    }

    /**
     * [get] METHOD: {VARCHAR(10), refers to ACCESS_RESULT.METHOD} <br>
     * @return The value of method. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public String getMethod() throws SQLException {
        return (String)_vtMethod.getValue(_rs, DB_NAME_METHOD);
    }

    /**
     * [get] MIME_TYPE: {VARCHAR(100), refers to ACCESS_RESULT.MIME_TYPE} <br>
     * @return The value of mimeType. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public String getMimeType() throws SQLException {
        return (String)_vtMimeType.getValue(_rs, DB_NAME_MIME_TYPE);
    }

    /**
     * [get] CONTENT_LENGTH: {BIGINT(20), refers to ACCESS_RESULT.CONTENT_LENGTH} <br>
     * @return The value of contentLength. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public Long getContentLength() throws SQLException {
        return (Long)_vtContentLength.getValue(_rs, DB_NAME_CONTENT_LENGTH);
    }

    /**
     * [get] EXECUTION_TIME: {INT(11), refers to ACCESS_RESULT.EXECUTION_TIME} <br>
     * @return The value of executionTime. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public Integer getExecutionTime() throws SQLException {
        return (Integer)_vtExecutionTime.getValue(_rs, DB_NAME_EXECUTION_TIME);
    }

    /**
     * [get] CREATE_TIME: {BIGINT(20), refers to ACCESS_RESULT.CREATE_TIME} <br>
     * @return The value of createTime. (NullAllowed)
     * @throws SQLException When it fails to get the value from result set.
     */
    public Long getCreateTime() throws SQLException {
        return (Long)_vtCreateTime.getValue(_rs, DB_NAME_CREATE_TIME);
    }

}
