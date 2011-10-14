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
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;

/**
 * The entity of URL_FILTER as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, URL, FILTER_TYPE, CREATE_TIME
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
 *     
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsUrlFilter implements Entity, Serializable {

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
    /** ID: {PK, ID, NotNull, BIGINT(19)} */
    protected Long _id;

    /** SESSION_ID: {IX, NotNull, VARCHAR(20)} */
    protected String _sessionId;

    /** URL: {NotNull, TEXT(65535)} */
    protected String _url;

    /** FILTER_TYPE: {IX+, NotNull, VARCHAR(1)} */
    protected String _filterType;

    /** CREATE_TIME: {IX+, NotNull, DATETIME(19)} */
    protected java.sql.Timestamp _createTime;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The modified properties for this entity. */
    protected final EntityModifiedProperties __modifiedProperties = newModifiedProperties();

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    /**
     * {@inheritDoc}
     */
    public String getTableDbName() {
        return "URL_FILTER";
    }

    /**
     * {@inheritDoc}
     */
    public String getTablePropertyName() { // according to Java Beans rule
        return "urlFilter";
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
        if (getId() == null) {
            return false;
        }
        return true;
    }

    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
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
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * If primary-keys or columns of the other are same as this one, returns true.
     * @param other The other entity. (NullAllowed)
     * @return Comparing result.
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof BsUrlFilter)) {
            return false;
        }
        BsUrlFilter otherEntity = (BsUrlFilter) other;
        if (!xSV(getId(), otherEntity.getId())) {
            return false;
        }
        return true;
    }

    protected boolean xSV(Object value1, Object value2) { // isSameValue()
        return InternalUtil.isSameValue(value1, value2);
    }

    /**
     * Calculates the hash-code from primary-keys or columns.
     * @return The hash-code from primary-key or columns.
     */
    public int hashCode() {
        int result = 17;
        result = xCH(result, getTableDbName());
        result = xCH(result, getId());
        return result;
    }

    protected int xCH(int result, Object value) { // calculateHashcode()
        return InternalUtil.calculateHashcode(result, value);
    }

    /**
     * @return The display string of all columns and relation existences. (NotNull)
     */
    public String toString() {
        return buildDisplayString(InternalUtil.toClassTitle(this), true, true);
    }

    /**
     * @return The display string of basic informations with one-nested relation values. (NotNull)
     */
    public String toStringWithRelation() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString());
        return sb.toString();
    }

    /**
     * @param name The name for display. (NullAllowed: If it's null, it does not have a name)
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
            sb.append(buildColumnString());
        }
        if (relation) {
            sb.append(buildRelationString());
        }
        sb.append("@").append(Integer.toHexString(hashCode()));
        return sb.toString();
    }

    protected String buildColumnString() {
        String c = ", ";
        StringBuilder sb = new StringBuilder();
        sb.append(c).append(getId());
        sb.append(c).append(getSessionId());
        sb.append(c).append(getUrl());
        sb.append(c).append(getFilterType());
        sb.append(c).append(getCreateTime());
        if (sb.length() > 0) {
            sb.delete(0, c.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected String buildRelationString() {
        return "";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * [get] ID: {PK, ID, NotNull, BIGINT(19)} <br />
     * @return The value of the column 'ID'. (NullAllowed)
     */
    public Long getId() {
        return _id;
    }

    /**
     * [set] ID: {PK, ID, NotNull, BIGINT(19)} <br />
     * @param id The value of the column 'ID'. (NullAllowed)
     */
    public void setId(Long id) {
        __modifiedProperties.addPropertyName("id");
        this._id = id;
    }

    /**
     * [get] SESSION_ID: {IX, NotNull, VARCHAR(20)} <br />
     * @return The value of the column 'SESSION_ID'. (NullAllowed)
     */
    public String getSessionId() {
        return _sessionId;
    }

    /**
     * [set] SESSION_ID: {IX, NotNull, VARCHAR(20)} <br />
     * @param sessionId The value of the column 'SESSION_ID'. (NullAllowed)
     */
    public void setSessionId(String sessionId) {
        __modifiedProperties.addPropertyName("sessionId");
        this._sessionId = sessionId;
    }

    /**
     * [get] URL: {NotNull, TEXT(65535)} <br />
     * @return The value of the column 'URL'. (NullAllowed)
     */
    public String getUrl() {
        return _url;
    }

    /**
     * [set] URL: {NotNull, TEXT(65535)} <br />
     * @param url The value of the column 'URL'. (NullAllowed)
     */
    public void setUrl(String url) {
        __modifiedProperties.addPropertyName("url");
        this._url = url;
    }

    /**
     * [get] FILTER_TYPE: {IX+, NotNull, VARCHAR(1)} <br />
     * @return The value of the column 'FILTER_TYPE'. (NullAllowed)
     */
    public String getFilterType() {
        return _filterType;
    }

    /**
     * [set] FILTER_TYPE: {IX+, NotNull, VARCHAR(1)} <br />
     * @param filterType The value of the column 'FILTER_TYPE'. (NullAllowed)
     */
    public void setFilterType(String filterType) {
        __modifiedProperties.addPropertyName("filterType");
        this._filterType = filterType;
    }

    /**
     * [get] CREATE_TIME: {IX+, NotNull, DATETIME(19)} <br />
     * @return The value of the column 'CREATE_TIME'. (NullAllowed)
     */
    public java.sql.Timestamp getCreateTime() {
        return _createTime;
    }

    /**
     * [set] CREATE_TIME: {IX+, NotNull, DATETIME(19)} <br />
     * @param createTime The value of the column 'CREATE_TIME'. (NullAllowed)
     */
    public void setCreateTime(java.sql.Timestamp createTime) {
        __modifiedProperties.addPropertyName("createTime");
        this._createTime = createTime;
    }
}
