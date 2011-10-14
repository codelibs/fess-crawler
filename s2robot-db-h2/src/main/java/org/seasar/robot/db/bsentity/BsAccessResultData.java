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
import org.seasar.robot.db.exentity.AccessResult;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;

/**
 * The entity of ACCESS_RESULT_DATA as TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, TRANSFORMER_NAME, DATA, ENCODING
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
 * [foreign-table]
 *     ACCESS_RESULT
 * 
 * [referrer-table]
 *     
 * 
 * [foreign-property]
 *     accessResult
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResultData implements Entity, Serializable {

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
    /** ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT} */
    protected Long _id;

    /** TRANSFORMER_NAME: {NotNull, VARCHAR(255)} */
    protected String _transformerName;

    /** DATA: {BLOB(2147483647)} */
    protected byte[] _data;

    /** ENCODING: {VARCHAR(20)} */
    protected String _encoding;

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
        return "ACCESS_RESULT_DATA";
    }

    /**
     * {@inheritDoc}
     */
    public String getTablePropertyName() { // according to Java Beans rule
        return "accessResultData";
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
    /** ACCESS_RESULT as 'accessResult'. */
    protected AccessResult _accessResult;

    /**
     * ACCESS_RESULT as 'accessResult'.
     * @return The entity of foreign property 'accessResult'. (NullAllowed: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public AccessResult getAccessResult() {
        return _accessResult;
    }

    /**
     * ACCESS_RESULT as 'accessResult'.
     * @param accessResult The entity of foreign property 'accessResult'. (NullAllowed)
     */
    public void setAccessResult(AccessResult accessResult) {
        _accessResult = accessResult;
    }

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
        if (other == null || !(other instanceof BsAccessResultData)) {
            return false;
        }
        BsAccessResultData otherEntity = (BsAccessResultData) other;
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
        String l = "\n  ";
        if (_accessResult != null) {
            sb.append(l).append(xbRDS(_accessResult, "accessResult"));
        }
        return sb.toString();
    }

    protected String xbRDS(Entity e, String name) { // buildRelationDisplayString()
        return e.buildDisplayString(name, true, true);
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
        sb.append(c).append(getTransformerName());
        sb.append(c).append(xfBA(getData()));
        sb.append(c).append(getEncoding());
        if (sb.length() > 0) {
            sb.delete(0, c.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    protected String xfBA(byte[] bytes) { // formatByteArray()
        return InternalUtil.toString(bytes);
    }

    protected String buildRelationString() {
        StringBuilder sb = new StringBuilder();
        String c = ",";
        if (_accessResult != null) {
            sb.append(c).append("accessResult");
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
     * [get] ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT} <br />
     * @return The value of the column 'ID'. (NullAllowed)
     */
    public Long getId() {
        return _id;
    }

    /**
     * [set] ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT} <br />
     * @param id The value of the column 'ID'. (NullAllowed)
     */
    public void setId(Long id) {
        __modifiedProperties.addPropertyName("id");
        this._id = id;
    }

    /**
     * [get] TRANSFORMER_NAME: {NotNull, VARCHAR(255)} <br />
     * @return The value of the column 'TRANSFORMER_NAME'. (NullAllowed)
     */
    public String getTransformerName() {
        return _transformerName;
    }

    /**
     * [set] TRANSFORMER_NAME: {NotNull, VARCHAR(255)} <br />
     * @param transformerName The value of the column 'TRANSFORMER_NAME'. (NullAllowed)
     */
    public void setTransformerName(String transformerName) {
        __modifiedProperties.addPropertyName("transformerName");
        this._transformerName = transformerName;
    }

    /**
     * [get] DATA: {BLOB(2147483647)} <br />
     * @return The value of the column 'DATA'. (NullAllowed)
     */
    public byte[] getData() {
        return _data;
    }

    /**
     * [set] DATA: {BLOB(2147483647)} <br />
     * @param data The value of the column 'DATA'. (NullAllowed)
     */
    public void setData(byte[] data) {
        __modifiedProperties.addPropertyName("data");
        this._data = data;
    }

    /**
     * [get] ENCODING: {VARCHAR(20)} <br />
     * @return The value of the column 'ENCODING'. (NullAllowed)
     */
    public String getEncoding() {
        return _encoding;
    }

    /**
     * [set] ENCODING: {VARCHAR(20)} <br />
     * @param encoding The value of the column 'ENCODING'. (NullAllowed)
     */
    public void setEncoding(String encoding) {
        __modifiedProperties.addPropertyName("encoding");
        this._encoding = encoding;
    }
}
