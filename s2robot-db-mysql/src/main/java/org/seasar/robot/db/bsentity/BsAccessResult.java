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
package org.seasar.robot.db.bsentity;

import java.io.Serializable;
import java.util.Set;

import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.exentity.AccessResultData;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;

/**
 * The entity of ACCESS_RESULT as TABLE. <br />
 * 
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
 * 
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResult implements Entity, Serializable {

    // ===================================================================================
    // Definition
    // ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    // Attribute
    // =========
    // -----------------------------------------------------
    // Column
    // ------
    /** ID: {PK, ID, NotNull, BIGINT(19)} */
    protected Long _id;

    /** SESSION_ID: {IX, NotNull, VARCHAR(20)} */
    protected String _sessionId;

    /** RULE_ID: {VARCHAR(20)} */
    protected String _ruleId;

    /** URL: {IX+, NotNull, TEXT(65535)} */
    protected String _url;

    /** PARENT_URL: {TEXT(65535)} */
    protected String _parentUrl;

    /** STATUS: {NotNull, INT(10)} */
    protected Integer _status;

    /** HTTP_STATUS_CODE: {NotNull, INT(10)} */
    protected Integer _httpStatusCode;

    /** METHOD: {NotNull, VARCHAR(10)} */
    protected String _method;

    /** MIME_TYPE: {NotNull, VARCHAR(100)} */
    protected String _mimeType;

    /** CONTENT_LENGTH: {NotNull, BIGINT(19)} */
    protected Long _contentLength;

    /** EXECUTION_TIME: {NotNull, INT(10)} */
    protected Integer _executionTime;

    /** LAST_MODIFIED: {NotNull, DATETIME(19)} */
    protected java.sql.Timestamp _lastModified;

    /** CREATE_TIME: {IX+, NotNull, DATETIME(19)} */
    protected java.sql.Timestamp _createTime;

    // -----------------------------------------------------
    // Internal
    // --------
    /** The modified properties for this entity. */
    protected final EntityModifiedProperties __modifiedProperties =
        newModifiedProperties();

    // ===================================================================================
    // Table Name
    // ==========
    /**
     * {@inheritDoc}
     */
    public String getTableDbName() {
        return "ACCESS_RESULT";
    }

    /**
     * {@inheritDoc}
     */
    public String getTablePropertyName() { // according to Java Beans rule
        return "accessResult";
    }

    // ===================================================================================
    // DBMeta
    // ======
    /**
     * {@inheritDoc}
     */
    public DBMeta getDBMeta() {
        return DBMetaInstanceHandler.findDBMeta(getTableDbName());
    }

    // ===================================================================================
    // Primary Key
    // ===========
    /**
     * {@inheritDoc}
     */
    public boolean hasPrimaryKeyValue() {
        if (getId() == null) {
            return false;
        }
        return true;
    }

    // ===================================================================================
    // Foreign Property
    // ================
    /** ACCESS_RESULT_DATA as 'accessResultDataAsOne'. */
    protected AccessResultData _accessResultDataAsOne;

    /**
     * ACCESS_RESULT_DATA as 'accessResultDataAsOne'.
     * 
     * @return the entity of foreign property(referrer-as-one)
     *         'accessResultDataAsOne'. (NullAllowed: If the foreign key does
     *         not have 'NotNull' constraint, please check null.)
     */
    public AccessResultData getAccessResultDataAsOne() {
        return _accessResultDataAsOne;
    }

    /**
     * ACCESS_RESULT_DATA as 'accessResultDataAsOne'.
     * 
     * @param accessResultDataAsOne
     *            The entity of foreign property(referrer-as-one)
     *            'accessResultDataAsOne'. (NullAllowed)
     */
    public void setAccessResultDataAsOne(
            final AccessResultData accessResultDataAsOne) {
        _accessResultDataAsOne = accessResultDataAsOne;
    }

    // ===================================================================================
    // Referrer Property
    // =================
    // ===================================================================================
    // Modified Properties
    // ===================
    /**
     * {@inheritDoc}
     */
    public Set<String> modifiedProperties() {
        return __modifiedProperties.getPropertyNames();
    }

    /**
     * {@inheritDoc}
     */
    public void clearModifiedInfo() {
        __modifiedProperties.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasModification() {
        return !__modifiedProperties.isEmpty();
    }

    protected EntityModifiedProperties newModifiedProperties() {
        return new EntityModifiedProperties();
    }

    // ===================================================================================
    // Basic Override
    // ==============
    /**
     * If primary-keys or columns of the other are same as this one, returns
     * true.
     * 
     * @param other
     *            The other entity. (NullAllowed)
     * @return Comparing result.
     */
    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof BsAccessResult)) {
            return false;
        }
        final BsAccessResult otherEntity = (BsAccessResult) other;
        if (!xSV(getId(), otherEntity.getId())) {
            return false;
        }
        return true;
    }

    protected boolean xSV(final Object value1, final Object value2) { // isSameValue()
        return InternalUtil.isSameValue(value1, value2);
    }

    /**
     * Calculates the hash-code from primary-keys or columns.
     * 
     * @return The hash-code from primary-key or columns.
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = xCH(result, getTableDbName());
        result = xCH(result, getId());
        return result;
    }

    protected int xCH(final int result, final Object value) { // calculateHashcode()
        return InternalUtil.calculateHashcode(result, value);
    }

    /**
     * @return The display string of all columns and relation existences.
     *         (NotNull)
     */
    @Override
    public String toString() {
        return buildDisplayString(InternalUtil.toClassTitle(this), true, true);
    }

    /**
     * @return The display string of basic informations with one-nested relation
     *         values. (NotNull)
     */
    public String toStringWithRelation() {
        final StringBuilder sb = new StringBuilder();
        sb.append(toString());
        final String l = "\n  ";
        if (_accessResultDataAsOne != null) {
            sb.append(l).append(
                xbRDS(_accessResultDataAsOne, "accessResultDataAsOne"));
        }
        return sb.toString();
    }

    protected String xbRDS(final Entity e, final String name) { // buildRelationDisplayString()
        return e.buildDisplayString(name, true, true);
    }

    /**
     * @param name
     *            The name for display. (NullAllowed: If it's null, it does not
     *            have a name)
     * @param column
     *            Does it contains column values or not?
     * @param relation
     *            Does it contains relation existences or not?
     * @return The display string for this entity. (NotNull)
     */
    public String buildDisplayString(final String name, final boolean column,
            final boolean relation) {
        final StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(name).append(column || relation ? ":" : "");
        }
        if (column) {
            sb.append(buildColumnString());
        }
        if (relation) {
            sb.append(buildRelationString());
        }
        sb.append("@").append(Integer.toHexString(hashCode()));
        return sb.toString();
    }

    protected String buildColumnString() {
        final String c = ", ";
        final StringBuilder sb = new StringBuilder();
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

    protected String buildRelationString() {
        final StringBuilder sb = new StringBuilder();
        final String c = ",";
        if (_accessResultDataAsOne != null) {
            sb.append(c).append("accessResultDataAsOne");
        }
        if (sb.length() > 0) {
            sb.delete(0, c.length()).insert(0, "(").append(")");
        }
        return sb.toString();
    }

    // ===================================================================================
    // Accessor
    // ========
    /**
     * [get] ID: {PK, ID, NotNull, BIGINT(19)} <br />
     * 
     * @return The value of the column 'ID'. (NullAllowed)
     */
    public Long getId() {
        return _id;
    }

    /**
     * [set] ID: {PK, ID, NotNull, BIGINT(19)} <br />
     * 
     * @param id
     *            The value of the column 'ID'. (NullAllowed)
     */
    public void setId(final Long id) {
        __modifiedProperties.addPropertyName("id");
        this._id = id;
    }

    /**
     * [get] SESSION_ID: {IX, NotNull, VARCHAR(20)} <br />
     * 
     * @return The value of the column 'SESSION_ID'. (NullAllowed)
     */
    public String getSessionId() {
        return _sessionId;
    }

    /**
     * [set] SESSION_ID: {IX, NotNull, VARCHAR(20)} <br />
     * 
     * @param sessionId
     *            The value of the column 'SESSION_ID'. (NullAllowed)
     */
    public void setSessionId(final String sessionId) {
        __modifiedProperties.addPropertyName("sessionId");
        this._sessionId = sessionId;
    }

    /**
     * [get] RULE_ID: {VARCHAR(20)} <br />
     * 
     * @return The value of the column 'RULE_ID'. (NullAllowed)
     */
    public String getRuleId() {
        return _ruleId;
    }

    /**
     * [set] RULE_ID: {VARCHAR(20)} <br />
     * 
     * @param ruleId
     *            The value of the column 'RULE_ID'. (NullAllowed)
     */
    public void setRuleId(final String ruleId) {
        __modifiedProperties.addPropertyName("ruleId");
        this._ruleId = ruleId;
    }

    /**
     * [get] URL: {IX+, NotNull, TEXT(65535)} <br />
     * 
     * @return The value of the column 'URL'. (NullAllowed)
     */
    public String getUrl() {
        return _url;
    }

    /**
     * [set] URL: {IX+, NotNull, TEXT(65535)} <br />
     * 
     * @param url
     *            The value of the column 'URL'. (NullAllowed)
     */
    public void setUrl(final String url) {
        __modifiedProperties.addPropertyName("url");
        this._url = url;
    }

    /**
     * [get] PARENT_URL: {TEXT(65535)} <br />
     * 
     * @return The value of the column 'PARENT_URL'. (NullAllowed)
     */
    public String getParentUrl() {
        return _parentUrl;
    }

    /**
     * [set] PARENT_URL: {TEXT(65535)} <br />
     * 
     * @param parentUrl
     *            The value of the column 'PARENT_URL'. (NullAllowed)
     */
    public void setParentUrl(final String parentUrl) {
        __modifiedProperties.addPropertyName("parentUrl");
        this._parentUrl = parentUrl;
    }

    /**
     * [get] STATUS: {NotNull, INT(10)} <br />
     * 
     * @return The value of the column 'STATUS'. (NullAllowed)
     */
    public Integer getStatus() {
        return _status;
    }

    /**
     * [set] STATUS: {NotNull, INT(10)} <br />
     * 
     * @param status
     *            The value of the column 'STATUS'. (NullAllowed)
     */
    public void setStatus(final Integer status) {
        __modifiedProperties.addPropertyName("status");
        this._status = status;
    }

    /**
     * [get] HTTP_STATUS_CODE: {NotNull, INT(10)} <br />
     * 
     * @return The value of the column 'HTTP_STATUS_CODE'. (NullAllowed)
     */
    public Integer getHttpStatusCode() {
        return _httpStatusCode;
    }

    /**
     * [set] HTTP_STATUS_CODE: {NotNull, INT(10)} <br />
     * 
     * @param httpStatusCode
     *            The value of the column 'HTTP_STATUS_CODE'. (NullAllowed)
     */
    public void setHttpStatusCode(final Integer httpStatusCode) {
        __modifiedProperties.addPropertyName("httpStatusCode");
        this._httpStatusCode = httpStatusCode;
    }

    /**
     * [get] METHOD: {NotNull, VARCHAR(10)} <br />
     * 
     * @return The value of the column 'METHOD'. (NullAllowed)
     */
    public String getMethod() {
        return _method;
    }

    /**
     * [set] METHOD: {NotNull, VARCHAR(10)} <br />
     * 
     * @param method
     *            The value of the column 'METHOD'. (NullAllowed)
     */
    public void setMethod(final String method) {
        __modifiedProperties.addPropertyName("method");
        this._method = method;
    }

    /**
     * [get] MIME_TYPE: {NotNull, VARCHAR(100)} <br />
     * 
     * @return The value of the column 'MIME_TYPE'. (NullAllowed)
     */
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * [set] MIME_TYPE: {NotNull, VARCHAR(100)} <br />
     * 
     * @param mimeType
     *            The value of the column 'MIME_TYPE'. (NullAllowed)
     */
    public void setMimeType(final String mimeType) {
        __modifiedProperties.addPropertyName("mimeType");
        this._mimeType = mimeType;
    }

    /**
     * [get] CONTENT_LENGTH: {NotNull, BIGINT(19)} <br />
     * 
     * @return The value of the column 'CONTENT_LENGTH'. (NullAllowed)
     */
    public Long getContentLength() {
        return _contentLength;
    }

    /**
     * [set] CONTENT_LENGTH: {NotNull, BIGINT(19)} <br />
     * 
     * @param contentLength
     *            The value of the column 'CONTENT_LENGTH'. (NullAllowed)
     */
    public void setContentLength(final Long contentLength) {
        __modifiedProperties.addPropertyName("contentLength");
        this._contentLength = contentLength;
    }

    /**
     * [get] EXECUTION_TIME: {NotNull, INT(10)} <br />
     * 
     * @return The value of the column 'EXECUTION_TIME'. (NullAllowed)
     */
    public Integer getExecutionTime() {
        return _executionTime;
    }

    /**
     * [set] EXECUTION_TIME: {NotNull, INT(10)} <br />
     * 
     * @param executionTime
     *            The value of the column 'EXECUTION_TIME'. (NullAllowed)
     */
    public void setExecutionTime(final Integer executionTime) {
        __modifiedProperties.addPropertyName("executionTime");
        this._executionTime = executionTime;
    }

    /**
     * [get] LAST_MODIFIED: {NotNull, DATETIME(19)} <br />
     * 
     * @return The value of the column 'LAST_MODIFIED'. (NullAllowed)
     */
    public java.sql.Timestamp getLastModified() {
        return _lastModified;
    }

    /**
     * [set] LAST_MODIFIED: {NotNull, DATETIME(19)} <br />
     * 
     * @param lastModified
     *            The value of the column 'LAST_MODIFIED'. (NullAllowed)
     */
    public void setLastModified(final java.sql.Timestamp lastModified) {
        __modifiedProperties.addPropertyName("lastModified");
        this._lastModified = lastModified;
    }

    /**
     * [get] CREATE_TIME: {IX+, NotNull, DATETIME(19)} <br />
     * 
     * @return The value of the column 'CREATE_TIME'. (NullAllowed)
     */
    public java.sql.Timestamp getCreateTime() {
        return _createTime;
    }

    /**
     * [set] CREATE_TIME: {IX+, NotNull, DATETIME(19)} <br />
     * 
     * @param createTime
     *            The value of the column 'CREATE_TIME'. (NullAllowed)
     */
    public void setCreateTime(final java.sql.Timestamp createTime) {
        __modifiedProperties.addPropertyName("createTime");
        this._createTime = createTime;
    }
}
