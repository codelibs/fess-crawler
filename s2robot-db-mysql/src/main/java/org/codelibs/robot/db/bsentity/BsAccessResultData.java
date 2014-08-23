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
package org.codelibs.robot.db.bsentity;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.codelibs.robot.db.allcommon.DBMetaInstanceHandler;
import org.codelibs.robot.db.exentity.*;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.Entity;

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
 * [foreign table]
 *     ACCESS_RESULT
 * 
 * [referrer table]
 *     
 * 
 * [foreign property]
 *     accessResult
 * 
 * [referrer property]
 *     
 * 
 * [get/set template]
 * /= = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
 * Long id = entity.getId();
 * String transformerName = entity.getTransformerName();
 * byte[] data = entity.getData();
 * String encoding = entity.getEncoding();
 * entity.setId(id);
 * entity.setTransformerName(transformerName);
 * entity.setData(data);
 * entity.setEncoding(encoding);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResultData implements Entity, Serializable, Cloneable {

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

    /** DATA: {LONGBLOB(2147483647)} */
    protected byte[] _data;

    /** ENCODING: {VARCHAR(20)} */
    protected String _encoding;

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
    /** ACCESS_RESULT by my ID, named 'accessResult'. */
    protected AccessResult _accessResult;

    /**
     * ACCESS_RESULT by my ID, named 'accessResult'.
     * @return The entity of foreign property 'accessResult'. (NullAllowed: when e.g. null FK column, no setupSelect)
     */
    public AccessResult getAccessResult() {
        return _accessResult;
    }

    /**
     * ACCESS_RESULT by my ID, named 'accessResult'.
     * @param accessResult The entity of foreign property 'accessResult'. (NullAllowed)
     */
    public void setAccessResult(AccessResult accessResult) {
        _accessResult = accessResult;
    }

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
        if (obj == null || !(obj instanceof BsAccessResultData)) { return false; }
        BsAccessResultData other = (BsAccessResultData)obj;
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
        String li = "\n  ";
        if (_accessResult != null)
        { sb.append(li).append(xbRDS(_accessResult, "accessResult")); }
        return sb.toString();
    }
    protected String xbRDS(Entity et, String name) { // buildRelationDisplayString()
        return et.buildDisplayString(name, true, true);
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
        sb.append(dm).append(getTransformerName());
        sb.append(dm).append(xfBA(getData()));
        sb.append(dm).append(getEncoding());
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }
    protected String xfBA(byte[] bytes) { // formatByteArray()
        return FunCustodial.toString(bytes);
    }
    protected String buildRelationString() {
        StringBuilder sb = new StringBuilder();
        String cm = ",";
        if (_accessResult != null) { sb.append(cm).append("accessResult"); }
        if (sb.length() > cm.length()) {
            sb.delete(0, cm.length()).insert(0, "(").append(")");
        }
        return sb.toString();
    }

    /**
     * Clone entity instance using super.clone(). (shallow copy) 
     * @return The cloned instance of this entity. (NotNull)
     */
    public AccessResultData clone() {
        try {
            return (AccessResultData)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Failed to clone the entity: " + toString(), e);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * [get] ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT} <br />
     * @return The value of the column 'ID'. (basically NotNull if selected: for the constraint)
     */
    public Long getId() {
        return _id;
    }

    /**
     * [set] ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT} <br />
     * @param id The value of the column 'ID'. (basically NotNull if update: for the constraint)
     */
    public void setId(Long id) {
        __modifiedProperties.addPropertyName("id");
        this._id = id;
    }

    /**
     * [get] TRANSFORMER_NAME: {NotNull, VARCHAR(255)} <br />
     * @return The value of the column 'TRANSFORMER_NAME'. (basically NotNull if selected: for the constraint)
     */
    public String getTransformerName() {
        return _transformerName;
    }

    /**
     * [set] TRANSFORMER_NAME: {NotNull, VARCHAR(255)} <br />
     * @param transformerName The value of the column 'TRANSFORMER_NAME'. (basically NotNull if update: for the constraint)
     */
    public void setTransformerName(String transformerName) {
        __modifiedProperties.addPropertyName("transformerName");
        this._transformerName = transformerName;
    }

    /**
     * [get] DATA: {LONGBLOB(2147483647)} <br />
     * @return The value of the column 'DATA'. (NullAllowed even if selected: for no constraint)
     */
    public byte[] getData() {
        return _data;
    }

    /**
     * [set] DATA: {LONGBLOB(2147483647)} <br />
     * @param data The value of the column 'DATA'. (NullAllowed: null update allowed for no constraint)
     */
    public void setData(byte[] data) {
        __modifiedProperties.addPropertyName("data");
        this._data = data;
    }

    /**
     * [get] ENCODING: {VARCHAR(20)} <br />
     * @return The value of the column 'ENCODING'. (NullAllowed even if selected: for no constraint)
     */
    public String getEncoding() {
        return _encoding;
    }

    /**
     * [set] ENCODING: {VARCHAR(20)} <br />
     * @param encoding The value of the column 'ENCODING'. (NullAllowed: null update allowed for no constraint)
     */
    public void setEncoding(String encoding) {
        __modifiedProperties.addPropertyName("encoding");
        this._encoding = encoding;
    }
}
