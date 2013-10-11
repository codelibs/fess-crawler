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
package org.seasar.robot.db.bsentity.customize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.robot.db.exentity.customize.AccessResultDiff;

/**
 * The entity of AccessResultDiff. <br />
 * 
 * <pre>
 * [primary-key]
 *     
 * 
 * [column]
 *     ID, SESSION_ID, RULE_ID, URL, PARENT_URL, STATUS, HTTP_STATUS_CODE, METHOD, MIME_TYPE, CONTENT_LENGTH, EXECUTION_TIME, CREATE_TIME
 * 
 * [sequence]
 *     
 * 
 * [identity]
 *     
 * 
 * [version-no]
 *     
 * 
 * [foreign table]
 *     
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     
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
 * entity.setCreateTime(createTime);
 * = = = = = = = = = =/
 * </pre>
 * 
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResultDiff implements Entity, Serializable,
        Cloneable {

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
    /** ID: {BIGINT(20), refers to ACCESS_RESULT.ID} */
    protected Long _id;

    /** SESSION_ID: {VARCHAR(20), refers to ACCESS_RESULT.SESSION_ID} */
    protected String _sessionId;

    /** RULE_ID: {VARCHAR(20), refers to ACCESS_RESULT.RULE_ID} */
    protected String _ruleId;

    /** URL: {VARCHAR(21845), refers to ACCESS_RESULT.URL} */
    protected String _url;

    /** PARENT_URL: {VARCHAR(21845), refers to ACCESS_RESULT.PARENT_URL} */
    protected String _parentUrl;

    /** STATUS: {INT(11), refers to ACCESS_RESULT.STATUS} */
    protected Integer _status;

    /** HTTP_STATUS_CODE: {INT(11), refers to ACCESS_RESULT.HTTP_STATUS_CODE} */
    protected Integer _httpStatusCode;

    /** METHOD: {VARCHAR(10), refers to ACCESS_RESULT.METHOD} */
    protected String _method;

    /** MIME_TYPE: {VARCHAR(100), refers to ACCESS_RESULT.MIME_TYPE} */
    protected String _mimeType;

    /** CONTENT_LENGTH: {BIGINT(20), refers to ACCESS_RESULT.CONTENT_LENGTH} */
    protected Long _contentLength;

    /** EXECUTION_TIME: {INT(11), refers to ACCESS_RESULT.EXECUTION_TIME} */
    protected Integer _executionTime;

    /** CREATE_TIME: {DATETIME(19), refers to ACCESS_RESULT.CREATE_TIME} */
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
        return "AccessResultDiff";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTablePropertyName() { // according to Java Beans rule
        return "accessResultDiff";
    }

    // ===================================================================================
    // DBMeta
    // ======
    /**
     * {@inheritDoc}
     */
    @Override
    public DBMeta getDBMeta() {
        return org.seasar.robot.db.bsentity.customize.dbmeta.AccessResultDiffDbm
            .getInstance();
    }

    // ===================================================================================
    // Primary Key
    // ===========
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasPrimaryKeyValue() {
        return false;
    }

    // ===================================================================================
    // Foreign Property
    // ================
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
        if (other == null || !(other instanceof BsAccessResultDiff)) {
            return false;
        }
        final BsAccessResultDiff otherEntity = (BsAccessResultDiff) other;
        if (!xSV(getId(), otherEntity.getId())) {
            return false;
        }
        if (!xSV(getSessionId(), otherEntity.getSessionId())) {
            return false;
        }
        if (!xSV(getRuleId(), otherEntity.getRuleId())) {
            return false;
        }
        if (!xSV(getUrl(), otherEntity.getUrl())) {
            return false;
        }
        if (!xSV(getParentUrl(), otherEntity.getParentUrl())) {
            return false;
        }
        if (!xSV(getStatus(), otherEntity.getStatus())) {
            return false;
        }
        if (!xSV(getHttpStatusCode(), otherEntity.getHttpStatusCode())) {
            return false;
        }
        if (!xSV(getMethod(), otherEntity.getMethod())) {
            return false;
        }
        if (!xSV(getMimeType(), otherEntity.getMimeType())) {
            return false;
        }
        if (!xSV(getContentLength(), otherEntity.getContentLength())) {
            return false;
        }
        if (!xSV(getExecutionTime(), otherEntity.getExecutionTime())) {
            return false;
        }
        if (!xSV(getCreateTime(), otherEntity.getCreateTime())) {
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
        result = xCH(result, getSessionId());
        result = xCH(result, getRuleId());
        result = xCH(result, getUrl());
        result = xCH(result, getParentUrl());
        result = xCH(result, getStatus());
        result = xCH(result, getHttpStatusCode());
        result = xCH(result, getMethod());
        result = xCH(result, getMimeType());
        result = xCH(result, getContentLength());
        result = xCH(result, getExecutionTime());
        result = xCH(result, getCreateTime());
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
        return sb.toString();
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
        sb.append(delimiter).append(getCreateTime());
        if (sb.length() > delimiter.length()) {
            sb.delete(0, delimiter.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected String buildRelationString() {
        return "";
    }

    /**
     * Clone entity instance using super.clone(). (shallow copy)
     * 
     * @return The cloned instance of this entity. (NotNull)
     */
    @Override
    public AccessResultDiff clone() {
        try {
            return (AccessResultDiff) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException("Failed to clone the entity: "
                + toString(), e);
        }
    }

    // ===================================================================================
    // Accessor
    // ========
    /**
     * [get] ID: {BIGINT(20), refers to ACCESS_RESULT.ID} <br />
     * 
     * @return The value of the column 'ID'. (NullAllowed even if selected: for
     *         no constraint)
     */
    public Long getId() {
        return _id;
    }

    /**
     * [set] ID: {BIGINT(20), refers to ACCESS_RESULT.ID} <br />
     * 
     * @param id
     *            The value of the column 'ID'. (NullAllowed: null update
     *            allowed for no constraint)
     */
    public void setId(final Long id) {
        __modifiedProperties.addPropertyName("id");
        _id = id;
    }

    /**
     * [get] SESSION_ID: {VARCHAR(20), refers to ACCESS_RESULT.SESSION_ID} <br />
     * 
     * @return The value of the column 'SESSION_ID'. (NullAllowed even if
     *         selected: for no constraint)
     */
    public String getSessionId() {
        return _sessionId;
    }

    /**
     * [set] SESSION_ID: {VARCHAR(20), refers to ACCESS_RESULT.SESSION_ID} <br />
     * 
     * @param sessionId
     *            The value of the column 'SESSION_ID'. (NullAllowed: null
     *            update allowed for no constraint)
     */
    public void setSessionId(final String sessionId) {
        __modifiedProperties.addPropertyName("sessionId");
        _sessionId = sessionId;
    }

    /**
     * [get] RULE_ID: {VARCHAR(20), refers to ACCESS_RESULT.RULE_ID} <br />
     * 
     * @return The value of the column 'RULE_ID'. (NullAllowed even if selected:
     *         for no constraint)
     */
    public String getRuleId() {
        return _ruleId;
    }

    /**
     * [set] RULE_ID: {VARCHAR(20), refers to ACCESS_RESULT.RULE_ID} <br />
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
     * [get] URL: {VARCHAR(21845), refers to ACCESS_RESULT.URL} <br />
     * 
     * @return The value of the column 'URL'. (NullAllowed even if selected: for
     *         no constraint)
     */
    public String getUrl() {
        return _url;
    }

    /**
     * [set] URL: {VARCHAR(21845), refers to ACCESS_RESULT.URL} <br />
     * 
     * @param url
     *            The value of the column 'URL'. (NullAllowed: null update
     *            allowed for no constraint)
     */
    public void setUrl(final String url) {
        __modifiedProperties.addPropertyName("url");
        _url = url;
    }

    /**
     * [get] PARENT_URL: {VARCHAR(21845), refers to ACCESS_RESULT.PARENT_URL} <br />
     * 
     * @return The value of the column 'PARENT_URL'. (NullAllowed even if
     *         selected: for no constraint)
     */
    public String getParentUrl() {
        return _parentUrl;
    }

    /**
     * [set] PARENT_URL: {VARCHAR(21845), refers to ACCESS_RESULT.PARENT_URL} <br />
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
     * [get] STATUS: {INT(11), refers to ACCESS_RESULT.STATUS} <br />
     * 
     * @return The value of the column 'STATUS'. (NullAllowed even if selected:
     *         for no constraint)
     */
    public Integer getStatus() {
        return _status;
    }

    /**
     * [set] STATUS: {INT(11), refers to ACCESS_RESULT.STATUS} <br />
     * 
     * @param status
     *            The value of the column 'STATUS'. (NullAllowed: null update
     *            allowed for no constraint)
     */
    public void setStatus(final Integer status) {
        __modifiedProperties.addPropertyName("status");
        _status = status;
    }

    /**
     * [get] HTTP_STATUS_CODE: {INT(11), refers to
     * ACCESS_RESULT.HTTP_STATUS_CODE} <br />
     * 
     * @return The value of the column 'HTTP_STATUS_CODE'. (NullAllowed even if
     *         selected: for no constraint)
     */
    public Integer getHttpStatusCode() {
        return _httpStatusCode;
    }

    /**
     * [set] HTTP_STATUS_CODE: {INT(11), refers to
     * ACCESS_RESULT.HTTP_STATUS_CODE} <br />
     * 
     * @param httpStatusCode
     *            The value of the column 'HTTP_STATUS_CODE'. (NullAllowed: null
     *            update allowed for no constraint)
     */
    public void setHttpStatusCode(final Integer httpStatusCode) {
        __modifiedProperties.addPropertyName("httpStatusCode");
        _httpStatusCode = httpStatusCode;
    }

    /**
     * [get] METHOD: {VARCHAR(10), refers to ACCESS_RESULT.METHOD} <br />
     * 
     * @return The value of the column 'METHOD'. (NullAllowed even if selected:
     *         for no constraint)
     */
    public String getMethod() {
        return _method;
    }

    /**
     * [set] METHOD: {VARCHAR(10), refers to ACCESS_RESULT.METHOD} <br />
     * 
     * @param method
     *            The value of the column 'METHOD'. (NullAllowed: null update
     *            allowed for no constraint)
     */
    public void setMethod(final String method) {
        __modifiedProperties.addPropertyName("method");
        _method = method;
    }

    /**
     * [get] MIME_TYPE: {VARCHAR(100), refers to ACCESS_RESULT.MIME_TYPE} <br />
     * 
     * @return The value of the column 'MIME_TYPE'. (NullAllowed even if
     *         selected: for no constraint)
     */
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * [set] MIME_TYPE: {VARCHAR(100), refers to ACCESS_RESULT.MIME_TYPE} <br />
     * 
     * @param mimeType
     *            The value of the column 'MIME_TYPE'. (NullAllowed: null update
     *            allowed for no constraint)
     */
    public void setMimeType(final String mimeType) {
        __modifiedProperties.addPropertyName("mimeType");
        _mimeType = mimeType;
    }

    /**
     * [get] CONTENT_LENGTH: {BIGINT(20), refers to
     * ACCESS_RESULT.CONTENT_LENGTH} <br />
     * 
     * @return The value of the column 'CONTENT_LENGTH'. (NullAllowed even if
     *         selected: for no constraint)
     */
    public Long getContentLength() {
        return _contentLength;
    }

    /**
     * [set] CONTENT_LENGTH: {BIGINT(20), refers to
     * ACCESS_RESULT.CONTENT_LENGTH} <br />
     * 
     * @param contentLength
     *            The value of the column 'CONTENT_LENGTH'. (NullAllowed: null
     *            update allowed for no constraint)
     */
    public void setContentLength(final Long contentLength) {
        __modifiedProperties.addPropertyName("contentLength");
        _contentLength = contentLength;
    }

    /**
     * [get] EXECUTION_TIME: {INT(11), refers to ACCESS_RESULT.EXECUTION_TIME} <br />
     * 
     * @return The value of the column 'EXECUTION_TIME'. (NullAllowed even if
     *         selected: for no constraint)
     */
    public Integer getExecutionTime() {
        return _executionTime;
    }

    /**
     * [set] EXECUTION_TIME: {INT(11), refers to ACCESS_RESULT.EXECUTION_TIME} <br />
     * 
     * @param executionTime
     *            The value of the column 'EXECUTION_TIME'. (NullAllowed: null
     *            update allowed for no constraint)
     */
    public void setExecutionTime(final Integer executionTime) {
        __modifiedProperties.addPropertyName("executionTime");
        _executionTime = executionTime;
    }

    /**
     * [get] CREATE_TIME: {DATETIME(19), refers to ACCESS_RESULT.CREATE_TIME} <br />
     * 
     * @return The value of the column 'CREATE_TIME'. (NullAllowed even if
     *         selected: for no constraint)
     */
    public java.sql.Timestamp getCreateTime() {
        return _createTime;
    }

    /**
     * [set] CREATE_TIME: {DATETIME(19), refers to ACCESS_RESULT.CREATE_TIME} <br />
     * 
     * @param createTime
     *            The value of the column 'CREATE_TIME'. (NullAllowed: null
     *            update allowed for no constraint)
     */
    public void setCreateTime(final java.sql.Timestamp createTime) {
        __modifiedProperties.addPropertyName("createTime");
        _createTime = createTime;
    }
}
