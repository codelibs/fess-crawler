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

import org.dbflute.dbmeta.DBMeta;
import org.dbflute.dbmeta.AbstractEntity;
import org.dbflute.dbmeta.accessory.DomainEntity;
import org.codelibs.fess.crawler.db.allcommon.DBMetaInstanceHandler;
import org.codelibs.fess.crawler.db.exentity.*;

/**
 * The entity of URL_FILTER as TABLE. <br>
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
 * String url = entity.getUrl();
 * String filterType = entity.getFilterType();
 * Long createTime = entity.getCreateTime();
 * entity.setId(id);
 * entity.setSessionId(sessionId);
 * entity.setUrl(url);
 * entity.setFilterType(filterType);
 * entity.setCreateTime(createTime);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsUrlFilter extends AbstractEntity implements DomainEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The serial version UID for object serialization. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** ID: {PK, ID, NotNull, BIGINT(19)} */
    protected Long _id;

    /** SESSION_ID: {IX+, NotNull, VARCHAR(20)} */
    protected String _sessionId;

    /** URL: {NotNull, VARCHAR(65536)} */
    protected String _url;

    /** FILTER_TYPE: {NotNull, VARCHAR(1)} */
    protected String _filterType;

    /** CREATE_TIME: {NotNull, BIGINT(19)} */
    protected Long _createTime;

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    /** {@inheritDoc} */
    public DBMeta asDBMeta() {
        return DBMetaInstanceHandler.findDBMeta(asTableDbName());
    }

    /** {@inheritDoc} */
    public String asTableDbName() {
        return "URL_FILTER";
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
        if (obj instanceof BsUrlFilter) {
            BsUrlFilter other = (BsUrlFilter)obj;
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
        return "";
    }

    @Override
    protected String doBuildColumnString(String dm) {
        StringBuilder sb = new StringBuilder();
        sb.append(dm).append(xfND(_id));
        sb.append(dm).append(xfND(_sessionId));
        sb.append(dm).append(xfND(_url));
        sb.append(dm).append(xfND(_filterType));
        sb.append(dm).append(xfND(_createTime));
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    @Override
    protected String doBuildRelationString(String dm) {
        return "";
    }

    @Override
    public UrlFilter clone() {
        return (UrlFilter)super.clone();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * [get] ID: {PK, ID, NotNull, BIGINT(19)} <br>
     * @return The value of the column 'ID'. (basically NotNull if selected: for the constraint)
     */
    public Long getId() {
        checkSpecifiedProperty("id");
        return _id;
    }

    /**
     * [set] ID: {PK, ID, NotNull, BIGINT(19)} <br>
     * @param id The value of the column 'ID'. (basically NotNull if update: for the constraint)
     */
    public void setId(Long id) {
        registerModifiedProperty("id");
        _id = id;
    }

    /**
     * [get] SESSION_ID: {IX+, NotNull, VARCHAR(20)} <br>
     * @return The value of the column 'SESSION_ID'. (basically NotNull if selected: for the constraint)
     */
    public String getSessionId() {
        checkSpecifiedProperty("sessionId");
        return _sessionId;
    }

    /**
     * [set] SESSION_ID: {IX+, NotNull, VARCHAR(20)} <br>
     * @param sessionId The value of the column 'SESSION_ID'. (basically NotNull if update: for the constraint)
     */
    public void setSessionId(String sessionId) {
        registerModifiedProperty("sessionId");
        _sessionId = sessionId;
    }

    /**
     * [get] URL: {NotNull, VARCHAR(65536)} <br>
     * @return The value of the column 'URL'. (basically NotNull if selected: for the constraint)
     */
    public String getUrl() {
        checkSpecifiedProperty("url");
        return _url;
    }

    /**
     * [set] URL: {NotNull, VARCHAR(65536)} <br>
     * @param url The value of the column 'URL'. (basically NotNull if update: for the constraint)
     */
    public void setUrl(String url) {
        registerModifiedProperty("url");
        _url = url;
    }

    /**
     * [get] FILTER_TYPE: {NotNull, VARCHAR(1)} <br>
     * @return The value of the column 'FILTER_TYPE'. (basically NotNull if selected: for the constraint)
     */
    public String getFilterType() {
        checkSpecifiedProperty("filterType");
        return _filterType;
    }

    /**
     * [set] FILTER_TYPE: {NotNull, VARCHAR(1)} <br>
     * @param filterType The value of the column 'FILTER_TYPE'. (basically NotNull if update: for the constraint)
     */
    public void setFilterType(String filterType) {
        registerModifiedProperty("filterType");
        _filterType = filterType;
    }

    /**
     * [get] CREATE_TIME: {NotNull, BIGINT(19)} <br>
     * @return The value of the column 'CREATE_TIME'. (basically NotNull if selected: for the constraint)
     */
    public Long getCreateTime() {
        checkSpecifiedProperty("createTime");
        return _createTime;
    }

    /**
     * [set] CREATE_TIME: {NotNull, BIGINT(19)} <br>
     * @param createTime The value of the column 'CREATE_TIME'. (basically NotNull if update: for the constraint)
     */
    public void setCreateTime(Long createTime) {
        registerModifiedProperty("createTime");
        _createTime = createTime;
    }
}
