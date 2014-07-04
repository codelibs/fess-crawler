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
package org.seasar.robot.db.bsentity;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.Entity;
import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.exentity.*;

/**
 * The entity of URL_QUEUE as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, METHOD, URL, META_DATA, ENCODING, PARENT_URL, DEPTH, LAST_MODIFIED, CREATE_TIME
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
 * String metaData = entity.getMetaData();
 * String encoding = entity.getEncoding();
 * String parentUrl = entity.getParentUrl();
 * Integer depth = entity.getDepth();
 * java.sql.Timestamp lastModified = entity.getLastModified();
 * java.sql.Timestamp createTime = entity.getCreateTime();
 * entity.setId(id);
 * entity.setSessionId(sessionId);
 * entity.setMethod(method);
 * entity.setUrl(url);
 * entity.setMetaData(metaData);
 * entity.setEncoding(encoding);
 * entity.setParentUrl(parentUrl);
 * entity.setDepth(depth);
 * entity.setLastModified(lastModified);
 * entity.setCreateTime(createTime);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsUrlQueue implements Entity, Serializable, Cloneable {

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
    /** ID: {PK, NotNull, NUMBER(19)} */
    protected Long _id;

    /** SESSION_ID: {IX+, NotNull, VARCHAR2(20)} */
    protected String _sessionId;

    /** METHOD: {NotNull, VARCHAR2(10)} */
    protected String _method;

    /** URL: {NotNull, VARCHAR2(4000)} */
    protected String _url;

    /** META_DATA: {VARCHAR2(4000)} */
    protected String _metaData;

    /** ENCODING: {VARCHAR2(20)} */
    protected String _encoding;

    /** PARENT_URL: {VARCHAR2(4000)} */
    protected String _parentUrl;

    /** DEPTH: {NotNull, NUMBER(5)} */
    protected Integer _depth;

    /** LAST_MODIFIED: {TIMESTAMP(6)(11, 6)} */
    protected java.sql.Timestamp _lastModified;

    /** CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)} */
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
        return "URL_QUEUE";
    }

    /**
     * {@inheritDoc}
     */
    public String getTablePropertyName() { // according to Java Beans rule
        return "urlQueue";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * {@inheritDoc}
     */
    public DBMeta getDBMeta() {
        return DBMetaInstanceHandler.findDBMeta(getTableDbName());
    }

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    /**
     * {@inheritDoc}
     */
    public boolean hasPrimaryKeyValue() {
        if (getId() == null) { return false; }
        return true;
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
        if (obj == null || !(obj instanceof BsUrlQueue)) { return false; }
        BsUrlQueue other = (BsUrlQueue)obj;
        if (!xSV(getId(), other.getId())) { return false; }
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
        sb.append(dm).append(getMethod());
        sb.append(dm).append(getUrl());
        sb.append(dm).append(getMetaData());
        sb.append(dm).append(getEncoding());
        sb.append(dm).append(getParentUrl());
        sb.append(dm).append(getDepth());
        sb.append(dm).append(getLastModified());
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
    public UrlQueue clone() {
        try {
            return (UrlQueue)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Failed to clone the entity: " + toString(), e);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * [get] ID: {PK, NotNull, NUMBER(19)} <br />
     * @return The value of the column 'ID'. (basically NotNull if selected: for the constraint)
     */
    public Long getId() {
        return _id;
    }

    /**
     * [set] ID: {PK, NotNull, NUMBER(19)} <br />
     * @param id The value of the column 'ID'. (basically NotNull if update: for the constraint)
     */
    public void setId(Long id) {
        __modifiedProperties.addPropertyName("id");
        this._id = id;
    }

    /**
     * [get] SESSION_ID: {IX+, NotNull, VARCHAR2(20)} <br />
     * @return The value of the column 'SESSION_ID'. (basically NotNull if selected: for the constraint)
     */
    public String getSessionId() {
        return _sessionId;
    }

    /**
     * [set] SESSION_ID: {IX+, NotNull, VARCHAR2(20)} <br />
     * @param sessionId The value of the column 'SESSION_ID'. (basically NotNull if update: for the constraint)
     */
    public void setSessionId(String sessionId) {
        __modifiedProperties.addPropertyName("sessionId");
        this._sessionId = sessionId;
    }

    /**
     * [get] METHOD: {NotNull, VARCHAR2(10)} <br />
     * @return The value of the column 'METHOD'. (basically NotNull if selected: for the constraint)
     */
    public String getMethod() {
        return _method;
    }

    /**
     * [set] METHOD: {NotNull, VARCHAR2(10)} <br />
     * @param method The value of the column 'METHOD'. (basically NotNull if update: for the constraint)
     */
    public void setMethod(String method) {
        __modifiedProperties.addPropertyName("method");
        this._method = method;
    }

    /**
     * [get] URL: {NotNull, VARCHAR2(4000)} <br />
     * @return The value of the column 'URL'. (basically NotNull if selected: for the constraint)
     */
    public String getUrl() {
        return _url;
    }

    /**
     * [set] URL: {NotNull, VARCHAR2(4000)} <br />
     * @param url The value of the column 'URL'. (basically NotNull if update: for the constraint)
     */
    public void setUrl(String url) {
        __modifiedProperties.addPropertyName("url");
        this._url = url;
    }

    /**
     * [get] META_DATA: {VARCHAR2(4000)} <br />
     * @return The value of the column 'META_DATA'. (NullAllowed even if selected: for no constraint)
     */
    public String getMetaData() {
        return _metaData;
    }

    /**
     * [set] META_DATA: {VARCHAR2(4000)} <br />
     * @param metaData The value of the column 'META_DATA'. (NullAllowed: null update allowed for no constraint)
     */
    public void setMetaData(String metaData) {
        __modifiedProperties.addPropertyName("metaData");
        this._metaData = metaData;
    }

    /**
     * [get] ENCODING: {VARCHAR2(20)} <br />
     * @return The value of the column 'ENCODING'. (NullAllowed even if selected: for no constraint)
     */
    public String getEncoding() {
        return _encoding;
    }

    /**
     * [set] ENCODING: {VARCHAR2(20)} <br />
     * @param encoding The value of the column 'ENCODING'. (NullAllowed: null update allowed for no constraint)
     */
    public void setEncoding(String encoding) {
        __modifiedProperties.addPropertyName("encoding");
        this._encoding = encoding;
    }

    /**
     * [get] PARENT_URL: {VARCHAR2(4000)} <br />
     * @return The value of the column 'PARENT_URL'. (NullAllowed even if selected: for no constraint)
     */
    public String getParentUrl() {
        return _parentUrl;
    }

    /**
     * [set] PARENT_URL: {VARCHAR2(4000)} <br />
     * @param parentUrl The value of the column 'PARENT_URL'. (NullAllowed: null update allowed for no constraint)
     */
    public void setParentUrl(String parentUrl) {
        __modifiedProperties.addPropertyName("parentUrl");
        this._parentUrl = parentUrl;
    }

    /**
     * [get] DEPTH: {NotNull, NUMBER(5)} <br />
     * @return The value of the column 'DEPTH'. (basically NotNull if selected: for the constraint)
     */
    public Integer getDepth() {
        return _depth;
    }

    /**
     * [set] DEPTH: {NotNull, NUMBER(5)} <br />
     * @param depth The value of the column 'DEPTH'. (basically NotNull if update: for the constraint)
     */
    public void setDepth(Integer depth) {
        __modifiedProperties.addPropertyName("depth");
        this._depth = depth;
    }

    /**
     * [get] LAST_MODIFIED: {TIMESTAMP(6)(11, 6)} <br />
     * @return The value of the column 'LAST_MODIFIED'. (NullAllowed even if selected: for no constraint)
     */
    public java.sql.Timestamp getLastModified() {
        return _lastModified;
    }

    /**
     * [set] LAST_MODIFIED: {TIMESTAMP(6)(11, 6)} <br />
     * @param lastModified The value of the column 'LAST_MODIFIED'. (NullAllowed: null update allowed for no constraint)
     */
    public void setLastModified(java.sql.Timestamp lastModified) {
        __modifiedProperties.addPropertyName("lastModified");
        this._lastModified = lastModified;
    }

    /**
     * [get] CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)} <br />
     * @return The value of the column 'CREATE_TIME'. (basically NotNull if selected: for the constraint)
     */
    public java.sql.Timestamp getCreateTime() {
        return _createTime;
    }

    /**
     * [set] CREATE_TIME: {NotNull, TIMESTAMP(6)(11, 6)} <br />
     * @param createTime The value of the column 'CREATE_TIME'. (basically NotNull if update: for the constraint)
     */
    public void setCreateTime(java.sql.Timestamp createTime) {
        __modifiedProperties.addPropertyName("createTime");
        this._createTime = createTime;
    }
}
