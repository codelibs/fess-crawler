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
package org.seasar.robot.db.bsentity;

import java.io.Serializable;
import java.util.Set;

import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.exentity.AccessResultData;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;

/**
 * The entity of ACCESS_RESULT that is TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, RULE_ID, URL, PARENT_URL, STATUS, HTTP_STATUS_CODE, METHOD, MIME_TYPE, CONTENT_LENGTH, EXECUTION_TIME, LAST_MODIFIED, CREATE_TIME
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     ID
 * 
 * [version-no]
 *     
 * 
 * [foreign-table]
 *     ACCESS_RESULT_DATA(AsOne)
 * 
 * [referrer-table]
 *     ACCESS_RESULT_DATA
 * 
 * [foreign-property]
 *     accessResultDataAsOne
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResult implements Entity, Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                Column
    //                                                ------
    /** ID: {PK : ID : NotNull : BIGINT(19)} */
    protected Long _id;

    /** SESSION_ID: {NotNull : VARCHAR(20)} */
    protected String _sessionId;

    /** RULE_ID: {VARCHAR(20)} */
    protected String _ruleId;

    /** URL: {NotNull : TEXT(65535)} */
    protected String _url;

    /** PARENT_URL: {TEXT(65535)} */
    protected String _parentUrl;

    /** STATUS: {NotNull : INT(10)} */
    protected Integer _status;

    /** HTTP_STATUS_CODE: {NotNull : INT(10)} */
    protected Integer _httpStatusCode;

    /** METHOD: {NotNull : VARCHAR(10)} */
    protected String _method;

    /** MIME_TYPE: {NotNull : VARCHAR(100)} */
    protected String _mimeType;

    /** CONTENT_LENGTH: {NotNull : BIGINT(19)} */
    protected Long _contentLength;

    /** EXECUTION_TIME: {NotNull : INT(10)} */
    protected Integer _executionTime;

    /** LAST_MODIFIED: {NotNull : DATETIME(19)} */
    protected java.sql.Timestamp _lastModified;

    /** CREATE_TIME: {NotNull : DATETIME(19)} */
    protected java.sql.Timestamp _createTime;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The modified properties for this entity. */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "ACCESS_RESULT";
    }

    public String getTablePropertyName() { // as JavaBeansRule
        return "accessResult";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    public DBMeta getDBMeta() {
        return DBMetaInstanceHandler.findDBMeta(getTableDbName());
    }

    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    /** ACCESS_RESULT_DATA as 'accessResultDataAsOne'. */
    protected AccessResultData _accessResultDataAsOne;

    /**
     * ACCESS_RESULT_DATA as 'accessResultDataAsOne'. {without lazy-load} <br />
     * @return the entity of foreign property(referrer-as-one) 'accessResultDataAsOne'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public AccessResultData getAccessResultDataAsOne() {
        return _accessResultDataAsOne;
    }

    /**
     * ACCESS_RESULT_DATA as 'accessResultDataAsOne'.
     * @param accessResultDataAsOne The entity of foreign property(referrer-as-one) 'accessResultDataAsOne'. (Nullable)
     */
    public void setAccessResultDataAsOne(AccessResultData accessResultDataAsOne) {
        _accessResultDataAsOne = accessResultDataAsOne;
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_id == null) {
            return false;
        }
        return true;
    }

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    public Set<String> getModifiedPropertyNames() {
        return _modifiedProperties.getPropertyNames();
    }

    protected EntityModifiedProperties newEntityModifiedProperties() {
        return new EntityModifiedProperties();
    }

    public void clearModifiedPropertyNames() {
        _modifiedProperties.clear();
    }

    public boolean hasModification() {
        return !_modifiedProperties.isEmpty();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * If the primary-key of the other is same as this one, returns true.
     * @param other Other entity.
     * @return Comparing result.
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof BsAccessResult)) {
            return false;
        }
        BsAccessResult otherEntity = (BsAccessResult) other;
        if (!helpComparingValue(getId(), otherEntity.getId())) {
            return false;
        }
        return true;
    }

    protected boolean helpComparingValue(Object value1, Object value2) {
        if (value1 == null && value2 == null) {
            return true;
        }
        return value1 != null && value2 != null && value1.equals(value2);
    }

    /**
     * Calculates hash-code from primary-key.
     * @return Hash-code from primary-key.
     */
    public int hashCode() {
        int result = 17;
        if (getId() != null) {
            result = (31 * result) + getId().hashCode();
        }
        return result;
    }

    /**
     * @return The display string of all columns and relation existences. (NotNull)
     */
    public String toString() {
        return buildDisplayString(getClass().getSimpleName(), true, true);
    }

    /**
     * @return The display string of basic informations with one-nested relation values. (NotNull)
     */
    public String toStringWithRelation() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString());
        String l = "\n  ";
        if (_accessResultDataAsOne != null) {
            sb.append(l).append(
                    xbRDS(_accessResultDataAsOne, "accessResultDataAsOne"));
        }
        return sb.toString();
    }

    private String xbRDS(Entity e, String name) { // buildRelationDisplayString()
        return e.buildDisplayString(name, true, true);
    }

    /**
     * @param name The name for display. (Nullable: If it's null, it does not have a name)
     * @param column Does it contains column values or not?
     * @param relation Does it contains relation existences or not?
     * @return The display string for this entity. (NotNull)
     */
    public String buildDisplayString(String name, boolean column,
            boolean relation) {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(name).append(column || relation ? ":" : "");
        }
        if (column) {
            sb.append(xbuildColumnString());
        }
        if (relation) {
            sb.append(xbuildRelationString());
        }
        sb.append("@").append(Integer.toHexString(hashCode()));
        return sb.toString();
    }

    private String xbuildColumnString() {
        String c = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(c).append(getId());
        sb.append(c).append(getSessionId());
        sb.append(c).append(getRuleId());
        sb.append(c).append(getUrl());
        sb.append(c).append(getParentUrl());
        sb.append(c).append(getStatus());
        sb.append(c).append(getHttpStatusCode());
        sb.append(c).append(getMethod());
        sb.append(c).append(getMimeType());
        sb.append(c).append(getContentLength());
        sb.append(c).append(getExecutionTime());
        sb.append(c).append(getLastModified());
        sb.append(c).append(getCreateTime());
        if (sb.length() > 0) {
            sb.delete(0, c.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    private String xbuildRelationString() {
        StringBuilder sb = new StringBuilder();
        String c = ",";
        if (_accessResultDataAsOne != null) {
            sb.append(c).append("accessResultDataAsOne");
        }
        if (sb.length() > 0) {
            sb.delete(0, c.length()).insert(0, "(").append(")");
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * ID: {PK : ID : NotNull : BIGINT(19)} <br />
     * @return The value of the column 'ID'. (Nullable)
     */
    public Long getId() {
        return _id;
    }

    /**
     * ID: {PK : ID : NotNull : BIGINT(19)} <br />
     * @param id The value of the column 'ID'. (Nullable)
     */
    public void setId(Long id) {
        _modifiedProperties.addPropertyName("id");
        this._id = id;
    }

    /**
     * SESSION_ID: {NotNull : VARCHAR(20)} <br />
     * @return The value of the column 'SESSION_ID'. (Nullable)
     */
    public String getSessionId() {
        return _sessionId;
    }

    /**
     * SESSION_ID: {NotNull : VARCHAR(20)} <br />
     * @param sessionId The value of the column 'SESSION_ID'. (Nullable)
     */
    public void setSessionId(String sessionId) {
        _modifiedProperties.addPropertyName("sessionId");
        this._sessionId = sessionId;
    }

    /**
     * RULE_ID: {VARCHAR(20)} <br />
     * @return The value of the column 'RULE_ID'. (Nullable)
     */
    public String getRuleId() {
        return _ruleId;
    }

    /**
     * RULE_ID: {VARCHAR(20)} <br />
     * @param ruleId The value of the column 'RULE_ID'. (Nullable)
     */
    public void setRuleId(String ruleId) {
        _modifiedProperties.addPropertyName("ruleId");
        this._ruleId = ruleId;
    }

    /**
     * URL: {NotNull : TEXT(65535)} <br />
     * @return The value of the column 'URL'. (Nullable)
     */
    public String getUrl() {
        return _url;
    }

    /**
     * URL: {NotNull : TEXT(65535)} <br />
     * @param url The value of the column 'URL'. (Nullable)
     */
    public void setUrl(String url) {
        _modifiedProperties.addPropertyName("url");
        this._url = url;
    }

    /**
     * PARENT_URL: {TEXT(65535)} <br />
     * @return The value of the column 'PARENT_URL'. (Nullable)
     */
    public String getParentUrl() {
        return _parentUrl;
    }

    /**
     * PARENT_URL: {TEXT(65535)} <br />
     * @param parentUrl The value of the column 'PARENT_URL'. (Nullable)
     */
    public void setParentUrl(String parentUrl) {
        _modifiedProperties.addPropertyName("parentUrl");
        this._parentUrl = parentUrl;
    }

    /**
     * STATUS: {NotNull : INT(10)} <br />
     * @return The value of the column 'STATUS'. (Nullable)
     */
    public Integer getStatus() {
        return _status;
    }

    /**
     * STATUS: {NotNull : INT(10)} <br />
     * @param status The value of the column 'STATUS'. (Nullable)
     */
    public void setStatus(Integer status) {
        _modifiedProperties.addPropertyName("status");
        this._status = status;
    }

    /**
     * HTTP_STATUS_CODE: {NotNull : INT(10)} <br />
     * @return The value of the column 'HTTP_STATUS_CODE'. (Nullable)
     */
    public Integer getHttpStatusCode() {
        return _httpStatusCode;
    }

    /**
     * HTTP_STATUS_CODE: {NotNull : INT(10)} <br />
     * @param httpStatusCode The value of the column 'HTTP_STATUS_CODE'. (Nullable)
     */
    public void setHttpStatusCode(Integer httpStatusCode) {
        _modifiedProperties.addPropertyName("httpStatusCode");
        this._httpStatusCode = httpStatusCode;
    }

    /**
     * METHOD: {NotNull : VARCHAR(10)} <br />
     * @return The value of the column 'METHOD'. (Nullable)
     */
    public String getMethod() {
        return _method;
    }

    /**
     * METHOD: {NotNull : VARCHAR(10)} <br />
     * @param method The value of the column 'METHOD'. (Nullable)
     */
    public void setMethod(String method) {
        _modifiedProperties.addPropertyName("method");
        this._method = method;
    }

    /**
     * MIME_TYPE: {NotNull : VARCHAR(100)} <br />
     * @return The value of the column 'MIME_TYPE'. (Nullable)
     */
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * MIME_TYPE: {NotNull : VARCHAR(100)} <br />
     * @param mimeType The value of the column 'MIME_TYPE'. (Nullable)
     */
    public void setMimeType(String mimeType) {
        _modifiedProperties.addPropertyName("mimeType");
        this._mimeType = mimeType;
    }

    /**
     * CONTENT_LENGTH: {NotNull : BIGINT(19)} <br />
     * @return The value of the column 'CONTENT_LENGTH'. (Nullable)
     */
    public Long getContentLength() {
        return _contentLength;
    }

    /**
     * CONTENT_LENGTH: {NotNull : BIGINT(19)} <br />
     * @param contentLength The value of the column 'CONTENT_LENGTH'. (Nullable)
     */
    public void setContentLength(Long contentLength) {
        _modifiedProperties.addPropertyName("contentLength");
        this._contentLength = contentLength;
    }

    /**
     * EXECUTION_TIME: {NotNull : INT(10)} <br />
     * @return The value of the column 'EXECUTION_TIME'. (Nullable)
     */
    public Integer getExecutionTime() {
        return _executionTime;
    }

    /**
     * EXECUTION_TIME: {NotNull : INT(10)} <br />
     * @param executionTime The value of the column 'EXECUTION_TIME'. (Nullable)
     */
    public void setExecutionTime(Integer executionTime) {
        _modifiedProperties.addPropertyName("executionTime");
        this._executionTime = executionTime;
    }

    /**
     * LAST_MODIFIED: {NotNull : DATETIME(19)} <br />
     * @return The value of the column 'LAST_MODIFIED'. (Nullable)
     */
    public java.sql.Timestamp getLastModified() {
        return _lastModified;
    }

    /**
     * LAST_MODIFIED: {NotNull : DATETIME(19)} <br />
     * @param lastModified The value of the column 'LAST_MODIFIED'. (Nullable)
     */
    public void setLastModified(java.sql.Timestamp lastModified) {
        _modifiedProperties.addPropertyName("lastModified");
        this._lastModified = lastModified;
    }

    /**
     * CREATE_TIME: {NotNull : DATETIME(19)} <br />
     * @return The value of the column 'CREATE_TIME'. (Nullable)
     */
    public java.sql.Timestamp getCreateTime() {
        return _createTime;
    }

    /**
     * CREATE_TIME: {NotNull : DATETIME(19)} <br />
     * @param createTime The value of the column 'CREATE_TIME'. (Nullable)
     */
    public void setCreateTime(java.sql.Timestamp createTime) {
        _modifiedProperties.addPropertyName("createTime");
        this._createTime = createTime;
    }
}
