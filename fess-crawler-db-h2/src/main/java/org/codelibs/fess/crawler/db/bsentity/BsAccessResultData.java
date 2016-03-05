/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.db.bsentity;

import java.util.List;
import java.util.ArrayList;

import org.dbflute.Entity;
import org.dbflute.dbmeta.DBMeta;
import org.dbflute.dbmeta.AbstractEntity;
import org.dbflute.dbmeta.accessory.DomainEntity;
import org.dbflute.optional.OptionalEntity;
import org.codelibs.fess.crawler.db.allcommon.DBMetaInstanceHandler;
import org.codelibs.fess.crawler.db.exentity.*;

/**
 * The entity of ACCESS_RESULT_DATA as TABLE. <br>
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
public abstract class BsAccessResultData extends AbstractEntity implements DomainEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The serial version UID for object serialization. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT} */
    protected Long _id;

    /** TRANSFORMER_NAME: {NotNull, VARCHAR(255)} */
    protected String _transformerName;

    /** DATA: {BLOB(2147483647)} */
    protected byte[] _data;

    /** ENCODING: {VARCHAR(20)} */
    protected String _encoding;

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    /** {@inheritDoc} */
    public DBMeta asDBMeta() {
        return DBMetaInstanceHandler.findDBMeta(asTableDbName());
    }

    /** {@inheritDoc} */
    public String asTableDbName() {
        return "ACCESS_RESULT_DATA";
    }

    // ===================================================================================
    //                                                                        Key Handling
    //                                                                        ============
    /** {@inheritDoc} */
    public boolean hasPrimaryKeyValue() {
        if (_id == null) { return false; }
        return true;
    }

    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    /** ACCESS_RESULT by my ID, named 'accessResult'. */
    protected OptionalEntity<AccessResult> _accessResult;

    /**
     * [get] ACCESS_RESULT by my ID, named 'accessResult'. <br>
     * Optional: alwaysPresent(), ifPresent().orElse(), get(), ...
     * @return The entity of foreign property 'accessResult'. (NotNull, EmptyAllowed: when e.g. null FK column, no setupSelect)
     */
    public OptionalEntity<AccessResult> getAccessResult() {
        if (_accessResult == null) { _accessResult = OptionalEntity.relationEmpty(this, "accessResult"); }
        return _accessResult;
    }

    /**
     * [set] ACCESS_RESULT by my ID, named 'accessResult'.
     * @param accessResult The entity of foreign property 'accessResult'. (NullAllowed)
     */
    public void setAccessResult(OptionalEntity<AccessResult> accessResult) {
        _accessResult = accessResult;
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    protected <ELEMENT> List<ELEMENT> newReferrerList() {
        return new ArrayList<ELEMENT>();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    protected boolean doEquals(Object obj) {
        if (obj instanceof BsAccessResultData) {
            BsAccessResultData other = (BsAccessResultData)obj;
            if (!xSV(_id, other._id)) { return false; }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected int doHashCode(int initial) {
        int hs = initial;
        hs = xCH(hs, asTableDbName());
        hs = xCH(hs, _id);
        return hs;
    }

    @Override
    protected String doBuildStringWithRelation(String li) {
        StringBuilder sb = new StringBuilder();
        if (_accessResult != null && _accessResult.isPresent())
        { sb.append(li).append(xbRDS(_accessResult, "accessResult")); }
        return sb.toString();
    }
    protected <ET extends Entity> String xbRDS(org.dbflute.optional.OptionalEntity<ET> et, String name) { // buildRelationDisplayString()
        return et.get().buildDisplayString(name, true, true);
    }

    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(xfND(_id));
        sb.append(dm).append(xfND(_transformerName));
        sb.append(dm).append(xfBA(_data));
        sb.append(dm).append(xfND(_encoding));
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    @Override
    protected String doBuildRelationString(String dm) {
        StringBuilder sb = new StringBuilder();
        if (_accessResult != null && _accessResult.isPresent())
        { sb.append(dm).append("accessResult"); }
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length()).insert(0, "(").append(")");
        }
        return sb.toString();
    }

    @Override
    public AccessResultData clone() {
        return (AccessResultData)super.clone();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * [get] ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT} <br>
     * @return The value of the column 'ID'. (basically NotNull if selected: for the constraint)
     */
    public Long getId() {
        checkSpecifiedProperty("id");
        return _id;
    }

    /**
     * [set] ID: {PK, NotNull, BIGINT(19), FK to ACCESS_RESULT} <br>
     * @param id The value of the column 'ID'. (basically NotNull if update: for the constraint)
     */
    public void setId(Long id) {
        registerModifiedProperty("id");
        _id = id;
    }

    /**
     * [get] TRANSFORMER_NAME: {NotNull, VARCHAR(255)} <br>
     * @return The value of the column 'TRANSFORMER_NAME'. (basically NotNull if selected: for the constraint)
     */
    public String getTransformerName() {
        checkSpecifiedProperty("transformerName");
        return _transformerName;
    }

    /**
     * [set] TRANSFORMER_NAME: {NotNull, VARCHAR(255)} <br>
     * @param transformerName The value of the column 'TRANSFORMER_NAME'. (basically NotNull if update: for the constraint)
     */
    public void setTransformerName(String transformerName) {
        registerModifiedProperty("transformerName");
        _transformerName = transformerName;
    }

    /**
     * [get] DATA: {BLOB(2147483647)} <br>
     * @return The value of the column 'DATA'. (NullAllowed even if selected: for no constraint)
     */
    public byte[] getData() {
        checkSpecifiedProperty("data");
        return _data;
    }

    /**
     * [set] DATA: {BLOB(2147483647)} <br>
     * @param data The value of the column 'DATA'. (NullAllowed: null update allowed for no constraint)
     */
    public void setData(byte[] data) {
        registerModifiedProperty("data");
        _data = data;
    }

    /**
     * [get] ENCODING: {VARCHAR(20)} <br>
     * @return The value of the column 'ENCODING'. (NullAllowed even if selected: for no constraint)
     */
    public String getEncoding() {
        checkSpecifiedProperty("encoding");
        return _encoding;
    }

    /**
     * [set] ENCODING: {VARCHAR(20)} <br>
     * @param encoding The value of the column 'ENCODING'. (NullAllowed: null update allowed for no constraint)
     */
    public void setEncoding(String encoding) {
        registerModifiedProperty("encoding");
        _encoding = encoding;
    }
}
