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
import org.seasar.robot.db.exentity.UrlQueue;

/**
 * The entity of URL_QUEUE as TABLE. <br />
 * 
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, METHOD, URL, PARENT_URL, DEPTH, LAST_MODIFIED, CREATE_TIME
 * 
 * [sequence]
 *     URL_QUEUE_SEQ
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
 * String method = entity.getMethod();
 * String url = entity.getUrl();
 * String parentUrl = entity.getParentUrl();
 * Integer depth = entity.getDepth();
 * java.sql.Timestamp lastModified = entity.getLastModified();
 * java.sql.Timestamp createTime = entity.getCreateTime();
 * entity.setId(id);
 * entity.setSessionId(sessionId);
 * entity.setMethod(method);
 * entity.setUrl(url);
 * entity.setParentUrl(parentUrl);
 * entity.setDepth(depth);
 * entity.setLastModified(lastModified);
 * entity.setCreateTime(createTime);
 * = = = = = = = = = =/
 * </pre>
 * 
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsUrlQueue implements Entity, Serializable, Cloneable {

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
    /** ID: {PK, NotNull, NUMBER(19)} */
    protected Long _id;

    /** SESSION_ID: {IX, NotNull, VARCHAR2(20)} */
    protected String _sessionId;

    /** METHOD: {NotNull, VARCHAR2(10)} */
    protected String _method;

    /** URL: {IX+, NotNull, VARCHAR2(4000)} */
    protected String _url;

    /** PARENT_URL: {VARCHAR2(4000)} */
    protected String _parentUrl;

    /** DEPTH: {NotNull, NUMBER(5)} */
    protected Integer _depth;

    /** LAST_MODIFIED: {TIMESTAMP(6)(11, 6)} */
    protected java.sql.Timestamp _lastModified;

    /** CREATE_TIME: {IX+, NotNull, TIMESTAMP(6)(11, 6)} */
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
        return "URL_QUEUE";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTablePropertyName() { // according to Java Beans rule
        return "urlQueue";
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
        if (other == null || !(other instanceof BsUrlQueue)) {
            return false;
        }
        final BsUrlQueue otherEntity = (BsUrlQueue) other;
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
        sb.append(delimiter).append(getMethod());
        sb.append(delimiter).append(getUrl());
        sb.append(delimiter).append(getParentUrl());
        sb.append(delimiter).append(getDepth());
        sb.append(delimiter).append(getLastModified());
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
    public UrlQueue clone() {
        try {
            return (UrlQueue) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new IllegalStateException("Failed to clone the entity: "
                + toString(), e);
        }
    }

    // ===================================================================================
    // Accessor
    // ========
    /**
     * [get] ID: {PK, NotNull, NUMBER(19)} <br />
     * 
     * @return The value of the column 'ID'. (basically NotNull if selected: for
     *         the constraint)
     */
    public Long getId() {
        return _id;
    }

    /**
     * [set] ID: {PK, NotNull, NUMBER(19)} <br />
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
     * [get] SESSION_ID: {IX, NotNull, VARCHAR2(20)} <br />
     * 
     * @return The value of the column 'SESSION_ID'. (basically NotNull if
     *         selected: for the constraint)
     */
    public String getSessionId() {
        return _sessionId;
    }

    /**
     * [set] SESSION_ID: {IX, NotNull, VARCHAR2(20)} <br />
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
     * [get] METHOD: {NotNull, VARCHAR2(10)} <br />
     * 
     * @return The value of the column 'METHOD'. (basically NotNull if selected:
     *         for the constraint)
     */
    public String getMethod() {
        return _method;
    }

    /**
     * [set] METHOD: {NotNull, VARCHAR2(10)} <br />
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
     * [get] URL: {IX+, NotNull, VARCHAR2(4000)} <br />
     * 
     * @return The value of the column 'URL'. (basically NotNull if selected:
     *         for the constraint)
     */
    public String getUrl() {
        return _url;
    }

    /**
     * [set] URL: {IX+, NotNull, VARCHAR2(4000)} <br />
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
     * [get] PARENT_URL: {VARCHAR2(4000)} <br />
     * 
     * @return The value of the column 'PARENT_URL'. (NullAllowed even if
     *         selected: for no constraint)
     */
    public String getParentUrl() {
        return _parentUrl;
    }

    /**
     * [set] PARENT_URL: {VARCHAR2(4000)} <br />
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
     * [get] DEPTH: {NotNull, NUMBER(5)} <br />
     * 
     * @return The value of the column 'DEPTH'. (basically NotNull if selected:
     *         for the constraint)
     */
    public Integer getDepth() {
        return _depth;
    }

    /**
     * [set] DEPTH: {NotNull, NUMBER(5)} <br />
     * 
     * @param depth
     *            The value of the column 'DEPTH'. (basically NotNull if update:
     *            for the constraint)
     */
    public void setDepth(final Integer depth) {
        __modifiedProperties.addPropertyName("depth");
        _depth = depth;
    }

    /**
     * [get] LAST_MODIFIED: {TIMESTAMP(6)(11, 6)} <br />
     * 
     * @return The value of the column 'LAST_MODIFIED'. (NullAllowed even if
     *         selected: for no constraint)
     */
    public java.sql.Timestamp getLastModified() {
        return _lastModified;
    }

    /**
     * [set] LAST_MODIFIED: {TIMESTAMP(6)(11, 6)} <br />
     * 
     * @param lastModified
     *            The value of the column 'LAST_MODIFIED'. (NullAllowed: null
     *            update allowed for no constraint)
     */
    public void setLastModified(final java.sql.Timestamp lastModified) {
        __modifiedProperties.addPropertyName("lastModified");
        _lastModified = lastModified;
    }

    /**
     * [get] CREATE_TIME: {IX+, NotNull, TIMESTAMP(6)(11, 6)} <br />
     * 
     * @return The value of the column 'CREATE_TIME'. (basically NotNull if
     *         selected: for the constraint)
     */
    public java.sql.Timestamp getCreateTime() {
        return _createTime;
    }

    /**
     * [set] CREATE_TIME: {IX+, NotNull, TIMESTAMP(6)(11, 6)} <br />
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
