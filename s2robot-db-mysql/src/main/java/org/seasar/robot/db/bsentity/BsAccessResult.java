/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.exentity.AccessResult;
import org.seasar.robot.db.exentity.AccessResultData;

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
 * [foreign table]
 *     ACCESS_RESULT_DATA(AsOne)
 * 
 * [referrer table]
 *     ACCESS_RESULT_DATA
 * 
 * [foreign property]
 *     accessResultDataAsOne
 * 
 * [referrer property]
 *     
 * 
 * [get/set template]
 * /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 * Long id = entity.getId();
 * String sessionId = entity.getSessionId();
 * String ruleId = entity.getRuleId();
 * String url = entity.getUrl();
 * String parentUrl = entity.getParentUrl();
 * Integer status = entity.getStatus();
 * Integer httpStatusCode = entity.getHttpStatusCode();
 * String method = entity.getMethod();
 * String mimeType = entity.getMimeType();
 * Long contentLength = entity.getContentLength();
 * Integer executionTime = entity.getExecutionTime();
 * java.sql.Timestamp lastModified = entity.getLastModified();
 * java.sql.Timestamp createTime = entity.getCreateTime();
 * entity.setId(id);
 * entity.setSessionId(sessionId);
 * entity.setRuleId(ruleId);
 * entity.setUrl(url);
 * entity.setParentUrl(parentUrl);
 * entity.setStatus(status);
 * entity.setHttpStatusCode(httpStatusCode);
 * entity.setMethod(method);
 * entity.setMimeType(mimeType);
 * entity.setContentLength(contentLength);
 * entity.setExecutionTime(executionTime);
 * entity.setLastModified(lastModified);
 * entity.setCreateTime(createTime);
 * = = = = = = = = = =/
 * </pre>
 * 
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResult implements Entity, Serializable, Cloneable {

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

    /** URL: {IX, NotNull, TEXT(65535)} */
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
    /** The modified properties for this entity. (NotNull) */
    protected final EntityModifiedProperties __modifiedProperties =
        newModifiedProperties();

    /** Is the entity created by DBFlute select process? */
    protected boolean __createdBySelect;

    // ===================================================================================
    // Table Name
    // ==========
    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableDbName() {
        return "ACCESS_RESULT";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTablePropertyName() { // according to Java Beans rule
        return "accessResult";
    }

    // ===================================================================================
    // DBMeta
    // ======
    /**
     * {@inheritDoc}
     */
    @Override
    public DBMeta getDBMeta() {
        return DBMetaInstanceHandler.findDBMeta(getTableDbName());
    }

    // ===================================================================================
    // Primary Key
    // ===========
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPrimaryKeyValue() {
        if (getId() == null) {
            return false;
        }
        return true;
    }

    // ===================================================================================
    // Foreign Property
    // ================
    /** ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'. */
    protected AccessResultData _accessResultDataAsOne;

    /**
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * 
     * @return the entity of foreign property(referrer-as-one)
     *         'accessResultDataAsOne'. (NullAllowed: when e.g. no data, no
     *         setupSelect)
     */
    public AccessResultData getAccessResultDataAsOne() {
        return _accessResultDataAsOne;
    }

    /**
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
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
    protected <ELEMENT> List<ELEMENT> newReferrerList() {
        return new ArrayList<ELEMENT>();
    }

    // ===================================================================================
    // Modified Properties
    // ===================
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> modifiedProperties() {
        return __modifiedProperties.getPropertyNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearModifiedInfo() {
        __modifiedProperties.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasModification() {
        return !__modifiedProperties.isEmpty();
    }

    protected EntityModifiedProperties newModifiedProperties() {
        return new EntityModifiedProperties();
    }

    // ===================================================================================
    // Birthplace Mark
    // ===============
    /**
     * {@inheritDoc}
     */
    @Override
    public void markAsSelect() {
        __createdBySelect = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createdBySelect() {
        return __createdBySelect;
    }

    // ===================================================================================
    // Basic Override
    // ==============
    /**
     * Determine the object is equal with this. <br />
     * If primary-keys or columns of the other are same as this one, returns
     * true.
     * 
     * @param other
     *            The other entity. (NullAllowed: if null, returns false
     *            fixedly)
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
     * Calculate the hash-code from primary-keys or columns.
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
     * {@inheritDoc}
     */
    @Override
    public int instanceHash() {
        return super.hashCode();
    }

    /**
     * Convert to display string of entity's data. (no relation data)
     * 
     * @return The display string of all columns and relation existences.
     *         (NotNull)
     */
    @Override
    public String toString() {
        return buildDisplayString(InternalUtil.toClassTitle(this), true, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
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
        final StringBuilder sb = new StringBuilder();
        final String delimiter = ", ";
        sb.append(delimiter).append(getId());
        sb.append(delimiter).append(getSessionId());
        sb.append(delimiter).append(getRuleId());
        sb.append(delimiter).append(getUrl());
        sb.append(delimiter).append(getParentUrl());
        sb.append(delimiter).append(getStatus());
        sb.append(delimiter).append(getHttpStatusCode());
        sb.append(delimiter).append(getMethod());
        sb.append(delimiter).append(getMimeType());
        sb.append(delimiter).append(getContentLength());
        sb.append(delimiter).append(getExecutionTime());
        sb.append(delimiter).append(getLastModified());
        sb.append(delimiter).append(getCreateTime());
        if (sb.length() > delimiter.length()) {
            sb.delete(0, delimiter.length());
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
        if (sb.length() > c.length()) {
            sb.delete(0, c.length()).insert(0, "(").append(")");
        }
        return sb.toString();
    }

    /**
     * Clone entity instance using super.clone(). (shallow copy)
     * 
     * @return The cloned instance of this entity. (NotNull)
     */
    @Override
    public AccessResult clone() {
        try {
            return (AccessResult) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException("Failed to clone the entity: "
                + toString(), e);
        }
    }

    // ===================================================================================
    // Accessor
    // ========
    /**
     * [get] ID: {PK, ID, NotNull, BIGINT(19)} <br />
     * 
     * @return The value of the column 'ID'. (basically NotNull if selected: for
     *         the constraint)
     */
    public Long getId() {
        return _id;
    }

    /**
     * [set] ID: {PK, ID, NotNull, BIGINT(19)} <br />
     * 
     * @param id
     *            The value of the column 'ID'. (basically NotNull if update:
     *            for the constraint)
     */
    public void setId(final Long id) {
        __modifiedProperties.addPropertyName("id");
        _id = id;
    }

    /**
     * [get] SESSION_ID: {IX, NotNull, VARCHAR(20)} <br />
     * 
     * @return The value of the column 'SESSION_ID'. (basically NotNull if
     *         selected: for the constraint)
     */
    public String getSessionId() {
        return _sessionId;
    }

    /**
     * [set] SESSION_ID: {IX, NotNull, VARCHAR(20)} <br />
     * 
     * @param sessionId
     *            The value of the column 'SESSION_ID'. (basically NotNull if
     *            update: for the constraint)
     */
    public void setSessionId(final String sessionId) {
        __modifiedProperties.addPropertyName("sessionId");
        _sessionId = sessionId;
    }

    /**
     * [get] RULE_ID: {VARCHAR(20)} <br />
     * 
     * @return The value of the column 'RULE_ID'. (NullAllowed even if selected:
     *         for no constraint)
     */
    public String getRuleId() {
        return _ruleId;
    }

    /**
     * [set] RULE_ID: {VARCHAR(20)} <br />
     * 
     * @param ruleId
     *            The value of the column 'RULE_ID'. (NullAllowed: null update
     *            allowed for no constraint)
     */
    public void setRuleId(final String ruleId) {
        __modifiedProperties.addPropertyName("ruleId");
        _ruleId = ruleId;
    }

    /**
     * [get] URL: {IX, NotNull, TEXT(65535)} <br />
     * 
     * @return The value of the column 'URL'. (basically NotNull if selected:
     *         for the constraint)
     */
    public String getUrl() {
        return _url;
    }

    /**
     * [set] URL: {IX, NotNull, TEXT(65535)} <br />
     * 
     * @param url
     *            The value of the column 'URL'. (basically NotNull if update:
     *            for the constraint)
     */
    public void setUrl(final String url) {
        __modifiedProperties.addPropertyName("url");
        _url = url;
    }

    /**
     * [get] PARENT_URL: {TEXT(65535)} <br />
     * 
     * @return The value of the column 'PARENT_URL'. (NullAllowed even if
     *         selected: for no constraint)
     */
    public String getParentUrl() {
        return _parentUrl;
    }

    /**
     * [set] PARENT_URL: {TEXT(65535)} <br />
     * 
     * @param parentUrl
     *            The value of the column 'PARENT_URL'. (NullAllowed: null
     *            update allowed for no constraint)
     */
    public void setParentUrl(final String parentUrl) {
        __modifiedProperties.addPropertyName("parentUrl");
        _parentUrl = parentUrl;
    }

    /**
     * [get] STATUS: {NotNull, INT(10)} <br />
     * 
     * @return The value of the column 'STATUS'. (basically NotNull if selected:
     *         for the constraint)
     */
    public Integer getStatus() {
        return _status;
    }

    /**
     * [set] STATUS: {NotNull, INT(10)} <br />
     * 
     * @param status
     *            The value of the column 'STATUS'. (basically NotNull if
     *            update: for the constraint)
     */
    public void setStatus(final Integer status) {
        __modifiedProperties.addPropertyName("status");
        _status = status;
    }

    /**
     * [get] HTTP_STATUS_CODE: {NotNull, INT(10)} <br />
     * 
     * @return The value of the column 'HTTP_STATUS_CODE'. (basically NotNull if
     *         selected: for the constraint)
     */
    public Integer getHttpStatusCode() {
        return _httpStatusCode;
    }

    /**
     * [set] HTTP_STATUS_CODE: {NotNull, INT(10)} <br />
     * 
     * @param httpStatusCode
     *            The value of the column 'HTTP_STATUS_CODE'. (basically NotNull
     *            if update: for the constraint)
     */
    public void setHttpStatusCode(final Integer httpStatusCode) {
        __modifiedProperties.addPropertyName("httpStatusCode");
        _httpStatusCode = httpStatusCode;
    }

    /**
     * [get] METHOD: {NotNull, VARCHAR(10)} <br />
     * 
     * @return The value of the column 'METHOD'. (basically NotNull if selected:
     *         for the constraint)
     */
    public String getMethod() {
        return _method;
    }

    /**
     * [set] METHOD: {NotNull, VARCHAR(10)} <br />
     * 
     * @param method
     *            The value of the column 'METHOD'. (basically NotNull if
     *            update: for the constraint)
     */
    public void setMethod(final String method) {
        __modifiedProperties.addPropertyName("method");
        _method = method;
    }

    /**
     * [get] MIME_TYPE: {NotNull, VARCHAR(100)} <br />
     * 
     * @return The value of the column 'MIME_TYPE'. (basically NotNull if
     *         selected: for the constraint)
     */
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * [set] MIME_TYPE: {NotNull, VARCHAR(100)} <br />
     * 
     * @param mimeType
     *            The value of the column 'MIME_TYPE'. (basically NotNull if
     *            update: for the constraint)
     */
    public void setMimeType(final String mimeType) {
        __modifiedProperties.addPropertyName("mimeType");
        _mimeType = mimeType;
    }

    /**
     * [get] CONTENT_LENGTH: {NotNull, BIGINT(19)} <br />
     * 
     * @return The value of the column 'CONTENT_LENGTH'. (basically NotNull if
     *         selected: for the constraint)
     */
    public Long getContentLength() {
        return _contentLength;
    }

    /**
     * [set] CONTENT_LENGTH: {NotNull, BIGINT(19)} <br />
     * 
     * @param contentLength
     *            The value of the column 'CONTENT_LENGTH'. (basically NotNull
     *            if update: for the constraint)
     */
    public void setContentLength(final Long contentLength) {
        __modifiedProperties.addPropertyName("contentLength");
        _contentLength = contentLength;
    }

    /**
     * [get] EXECUTION_TIME: {NotNull, INT(10)} <br />
     * 
     * @return The value of the column 'EXECUTION_TIME'. (basically NotNull if
     *         selected: for the constraint)
     */
    public Integer getExecutionTime() {
        return _executionTime;
    }

    /**
     * [set] EXECUTION_TIME: {NotNull, INT(10)} <br />
     * 
     * @param executionTime
     *            The value of the column 'EXECUTION_TIME'. (basically NotNull
     *            if update: for the constraint)
     */
    public void setExecutionTime(final Integer executionTime) {
        __modifiedProperties.addPropertyName("executionTime");
        _executionTime = executionTime;
    }

    /**
     * [get] LAST_MODIFIED: {NotNull, DATETIME(19)} <br />
     * 
     * @return The value of the column 'LAST_MODIFIED'. (basically NotNull if
     *         selected: for the constraint)
     */
    public java.sql.Timestamp getLastModified() {
        return _lastModified;
    }

    /**
     * [set] LAST_MODIFIED: {NotNull, DATETIME(19)} <br />
     * 
     * @param lastModified
     *            The value of the column 'LAST_MODIFIED'. (basically NotNull if
     *            update: for the constraint)
     */
    public void setLastModified(final java.sql.Timestamp lastModified) {
        __modifiedProperties.addPropertyName("lastModified");
        _lastModified = lastModified;
    }

    /**
     * [get] CREATE_TIME: {IX+, NotNull, DATETIME(19)} <br />
     * 
     * @return The value of the column 'CREATE_TIME'. (basically NotNull if
     *         selected: for the constraint)
     */
    public java.sql.Timestamp getCreateTime() {
        return _createTime;
    }

    /**
     * [set] CREATE_TIME: {IX+, NotNull, DATETIME(19)} <br />
     * 
     * @param createTime
     *            The value of the column 'CREATE_TIME'. (basically NotNull if
     *            update: for the constraint)
     */
    public void setCreateTime(final java.sql.Timestamp createTime) {
        __modifiedProperties.addPropertyName("createTime");
        _createTime = createTime;
    }
}
