/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.db.bsentity.customize;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.codelibs.robot.db.exentity.customize.*;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.Entity;

/**
 * The entity of AccessResultDiff. <br />
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
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResultDiff implements Entity, Serializable, Cloneable {

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
    //                                              Internal
    //                                              --------
    /** The unique-driven properties for this entity. (NotNull) */
    protected final EntityUniqueDrivenProperties __uniqueDrivenProperties = newUniqueDrivenProperties();

    /** The modified properties for this entity. (NotNull) */
    protected final EntityModifiedProperties __modifiedProperties = newModifiedProperties();

    /** Is the entity created by DBFlute select process? */
    protected boolean __createdBySelect;

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    /**
     * {@inheritDoc}
     */
    public String getTableDbName() {
        return "AccessResultDiff";
    }

    /**
     * {@inheritDoc}
     */
    public String getTablePropertyName() { // according to Java Beans rule
        return "accessResultDiff";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * {@inheritDoc}
     */
    public DBMeta getDBMeta() {
        return org.codelibs.robot.db.bsentity.customize.dbmeta.AccessResultDiffDbm.getInstance();
    }

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    public boolean hasPrimaryKeyValue() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public Set<String> myuniqueDrivenProperties() {
        return __uniqueDrivenProperties.getPropertyNames();
    }

    protected EntityUniqueDrivenProperties newUniqueDrivenProperties() {
        return new EntityUniqueDrivenProperties();
    }

    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    protected <ELEMENT> List<ELEMENT> newReferrerList() {
        return new ArrayList<ELEMENT>();
    }

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
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
    //                                                                     Birthplace Mark
    //                                                                     ===============
    /**
     * {@inheritDoc}
     */
    public void markAsSelect() {
        __createdBySelect = true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean createdBySelect() {
        return __createdBySelect;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * Determine the object is equal with this. <br />
     * If primary-keys or columns of the other are same as this one, returns true.
     * @param obj The object as other entity. (NullAllowed: if null, returns false fixedly)
     * @return Comparing result.
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BsAccessResultDiff)) { return false; }
        BsAccessResultDiff other = (BsAccessResultDiff)obj;
        if (!xSV(getId(), other.getId())) { return false; }
        if (!xSV(getSessionId(), other.getSessionId())) { return false; }
        if (!xSV(getRuleId(), other.getRuleId())) { return false; }
        if (!xSV(getUrl(), other.getUrl())) { return false; }
        if (!xSV(getParentUrl(), other.getParentUrl())) { return false; }
        if (!xSV(getStatus(), other.getStatus())) { return false; }
        if (!xSV(getHttpStatusCode(), other.getHttpStatusCode())) { return false; }
        if (!xSV(getMethod(), other.getMethod())) { return false; }
        if (!xSV(getMimeType(), other.getMimeType())) { return false; }
        if (!xSV(getContentLength(), other.getContentLength())) { return false; }
        if (!xSV(getExecutionTime(), other.getExecutionTime())) { return false; }
        if (!xSV(getCreateTime(), other.getCreateTime())) { return false; }
        return true;
    }
    protected boolean xSV(Object v1, Object v2) {
        return FunCustodial.isSameValue(v1, v2);
    }

    /**
     * Calculate the hash-code from primary-keys or columns.
     * @return The hash-code from primary-key or columns.
     */
    public int hashCode() {
        int hs = 17;
        hs = xCH(hs, getTableDbName());
        hs = xCH(hs, getId());
        hs = xCH(hs, getSessionId());
        hs = xCH(hs, getRuleId());
        hs = xCH(hs, getUrl());
        hs = xCH(hs, getParentUrl());
        hs = xCH(hs, getStatus());
        hs = xCH(hs, getHttpStatusCode());
        hs = xCH(hs, getMethod());
        hs = xCH(hs, getMimeType());
        hs = xCH(hs, getContentLength());
        hs = xCH(hs, getExecutionTime());
        hs = xCH(hs, getCreateTime());
        return hs;
    }
    protected int xCH(int hs, Object vl) {
        return FunCustodial.calculateHashcode(hs, vl);
    }

    /**
     * {@inheritDoc}
     */
    public int instanceHash() {
        return super.hashCode();
    }

    /**
     * Convert to display string of entity's data. (no relation data)
     * @return The display string of all columns and relation existences. (NotNull)
     */
    public String toString() {
        return buildDisplayString(FunCustodial.toClassTitle(this), true, true);
    }

    /**
     * {@inheritDoc}
     */
    public String toStringWithRelation() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString());
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    public String buildDisplayString(String name, boolean column, boolean relation) {
        StringBuilder sb = new StringBuilder();
        if (name != null) { sb.append(name).append(column || relation ? ":" : ""); }
        if (column) { sb.append(buildColumnString()); }
        if (relation) { sb.append(buildRelationString()); }
        sb.append("@").append(Integer.toHexString(hashCode()));
        return sb.toString();
    }
    protected String buildColumnString() {
        StringBuilder sb = new StringBuilder();
        String dm = ", ";
        sb.append(dm).append(getId());
        sb.append(dm).append(getSessionId());
        sb.append(dm).append(getRuleId());
        sb.append(dm).append(getUrl());
        sb.append(dm).append(getParentUrl());
        sb.append(dm).append(getStatus());
        sb.append(dm).append(getHttpStatusCode());
        sb.append(dm).append(getMethod());
        sb.append(dm).append(getMimeType());
        sb.append(dm).append(getContentLength());
        sb.append(dm).append(getExecutionTime());
        sb.append(dm).append(getCreateTime());
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }
    protected String buildRelationString() {
        return "";
    }

    /**
     * Clone entity instance using super.clone(). (shallow copy) 
     * @return The cloned instance of this entity. (NotNull)
     */
    public AccessResultDiff clone() {
        try {
            return (AccessResultDiff)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Failed to clone the entity: " + toString(), e);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * [get] ID: {BIGINT(20), refers to ACCESS_RESULT.ID} <br />
     * @return The value of the column 'ID'. (NullAllowed even if selected: for no constraint)
     */
    public Long getId() {
        return _id;
    }

    /**
     * [set] ID: {BIGINT(20), refers to ACCESS_RESULT.ID} <br />
     * @param id The value of the column 'ID'. (NullAllowed: null update allowed for no constraint)
     */
    public void setId(Long id) {
        __modifiedProperties.addPropertyName("id");
        this._id = id;
    }

    /**
     * [get] SESSION_ID: {VARCHAR(20), refers to ACCESS_RESULT.SESSION_ID} <br />
     * @return The value of the column 'SESSION_ID'. (NullAllowed even if selected: for no constraint)
     */
    public String getSessionId() {
        return _sessionId;
    }

    /**
     * [set] SESSION_ID: {VARCHAR(20), refers to ACCESS_RESULT.SESSION_ID} <br />
     * @param sessionId The value of the column 'SESSION_ID'. (NullAllowed: null update allowed for no constraint)
     */
    public void setSessionId(String sessionId) {
        __modifiedProperties.addPropertyName("sessionId");
        this._sessionId = sessionId;
    }

    /**
     * [get] RULE_ID: {VARCHAR(20), refers to ACCESS_RESULT.RULE_ID} <br />
     * @return The value of the column 'RULE_ID'. (NullAllowed even if selected: for no constraint)
     */
    public String getRuleId() {
        return _ruleId;
    }

    /**
     * [set] RULE_ID: {VARCHAR(20), refers to ACCESS_RESULT.RULE_ID} <br />
     * @param ruleId The value of the column 'RULE_ID'. (NullAllowed: null update allowed for no constraint)
     */
    public void setRuleId(String ruleId) {
        __modifiedProperties.addPropertyName("ruleId");
        this._ruleId = ruleId;
    }

    /**
     * [get] URL: {VARCHAR(21845), refers to ACCESS_RESULT.URL} <br />
     * @return The value of the column 'URL'. (NullAllowed even if selected: for no constraint)
     */
    public String getUrl() {
        return _url;
    }

    /**
     * [set] URL: {VARCHAR(21845), refers to ACCESS_RESULT.URL} <br />
     * @param url The value of the column 'URL'. (NullAllowed: null update allowed for no constraint)
     */
    public void setUrl(String url) {
        __modifiedProperties.addPropertyName("url");
        this._url = url;
    }

    /**
     * [get] PARENT_URL: {VARCHAR(21845), refers to ACCESS_RESULT.PARENT_URL} <br />
     * @return The value of the column 'PARENT_URL'. (NullAllowed even if selected: for no constraint)
     */
    public String getParentUrl() {
        return _parentUrl;
    }

    /**
     * [set] PARENT_URL: {VARCHAR(21845), refers to ACCESS_RESULT.PARENT_URL} <br />
     * @param parentUrl The value of the column 'PARENT_URL'. (NullAllowed: null update allowed for no constraint)
     */
    public void setParentUrl(String parentUrl) {
        __modifiedProperties.addPropertyName("parentUrl");
        this._parentUrl = parentUrl;
    }

    /**
     * [get] STATUS: {INT(11), refers to ACCESS_RESULT.STATUS} <br />
     * @return The value of the column 'STATUS'. (NullAllowed even if selected: for no constraint)
     */
    public Integer getStatus() {
        return _status;
    }

    /**
     * [set] STATUS: {INT(11), refers to ACCESS_RESULT.STATUS} <br />
     * @param status The value of the column 'STATUS'. (NullAllowed: null update allowed for no constraint)
     */
    public void setStatus(Integer status) {
        __modifiedProperties.addPropertyName("status");
        this._status = status;
    }

    /**
     * [get] HTTP_STATUS_CODE: {INT(11), refers to ACCESS_RESULT.HTTP_STATUS_CODE} <br />
     * @return The value of the column 'HTTP_STATUS_CODE'. (NullAllowed even if selected: for no constraint)
     */
    public Integer getHttpStatusCode() {
        return _httpStatusCode;
    }

    /**
     * [set] HTTP_STATUS_CODE: {INT(11), refers to ACCESS_RESULT.HTTP_STATUS_CODE} <br />
     * @param httpStatusCode The value of the column 'HTTP_STATUS_CODE'. (NullAllowed: null update allowed for no constraint)
     */
    public void setHttpStatusCode(Integer httpStatusCode) {
        __modifiedProperties.addPropertyName("httpStatusCode");
        this._httpStatusCode = httpStatusCode;
    }

    /**
     * [get] METHOD: {VARCHAR(10), refers to ACCESS_RESULT.METHOD} <br />
     * @return The value of the column 'METHOD'. (NullAllowed even if selected: for no constraint)
     */
    public String getMethod() {
        return _method;
    }

    /**
     * [set] METHOD: {VARCHAR(10), refers to ACCESS_RESULT.METHOD} <br />
     * @param method The value of the column 'METHOD'. (NullAllowed: null update allowed for no constraint)
     */
    public void setMethod(String method) {
        __modifiedProperties.addPropertyName("method");
        this._method = method;
    }

    /**
     * [get] MIME_TYPE: {VARCHAR(100), refers to ACCESS_RESULT.MIME_TYPE} <br />
     * @return The value of the column 'MIME_TYPE'. (NullAllowed even if selected: for no constraint)
     */
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * [set] MIME_TYPE: {VARCHAR(100), refers to ACCESS_RESULT.MIME_TYPE} <br />
     * @param mimeType The value of the column 'MIME_TYPE'. (NullAllowed: null update allowed for no constraint)
     */
    public void setMimeType(String mimeType) {
        __modifiedProperties.addPropertyName("mimeType");
        this._mimeType = mimeType;
    }

    /**
     * [get] CONTENT_LENGTH: {BIGINT(20), refers to ACCESS_RESULT.CONTENT_LENGTH} <br />
     * @return The value of the column 'CONTENT_LENGTH'. (NullAllowed even if selected: for no constraint)
     */
    public Long getContentLength() {
        return _contentLength;
    }

    /**
     * [set] CONTENT_LENGTH: {BIGINT(20), refers to ACCESS_RESULT.CONTENT_LENGTH} <br />
     * @param contentLength The value of the column 'CONTENT_LENGTH'. (NullAllowed: null update allowed for no constraint)
     */
    public void setContentLength(Long contentLength) {
        __modifiedProperties.addPropertyName("contentLength");
        this._contentLength = contentLength;
    }

    /**
     * [get] EXECUTION_TIME: {INT(11), refers to ACCESS_RESULT.EXECUTION_TIME} <br />
     * @return The value of the column 'EXECUTION_TIME'. (NullAllowed even if selected: for no constraint)
     */
    public Integer getExecutionTime() {
        return _executionTime;
    }

    /**
     * [set] EXECUTION_TIME: {INT(11), refers to ACCESS_RESULT.EXECUTION_TIME} <br />
     * @param executionTime The value of the column 'EXECUTION_TIME'. (NullAllowed: null update allowed for no constraint)
     */
    public void setExecutionTime(Integer executionTime) {
        __modifiedProperties.addPropertyName("executionTime");
        this._executionTime = executionTime;
    }

    /**
     * [get] CREATE_TIME: {DATETIME(19), refers to ACCESS_RESULT.CREATE_TIME} <br />
     * @return The value of the column 'CREATE_TIME'. (NullAllowed even if selected: for no constraint)
     */
    public java.sql.Timestamp getCreateTime() {
        return _createTime;
    }

    /**
     * [set] CREATE_TIME: {DATETIME(19), refers to ACCESS_RESULT.CREATE_TIME} <br />
     * @param createTime The value of the column 'CREATE_TIME'. (NullAllowed: null update allowed for no constraint)
     */
    public void setCreateTime(java.sql.Timestamp createTime) {
        __modifiedProperties.addPropertyName("createTime");
        this._createTime = createTime;
    }
}
