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
package org.codelibs.fess.crawler.db.bsentity.customize;

import java.util.List;
import java.util.ArrayList;

import org.dbflute.dbmeta.DBMeta;
import org.dbflute.dbmeta.AbstractEntity;
import org.dbflute.dbmeta.accessory.CustomizeEntity;
import org.codelibs.fess.crawler.db.exentity.customize.*;

/**
 * The entity of AccessResultDiff. <br>
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
 * Long createTime = entity.getCreateTime();
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
public abstract class BsAccessResultDiff extends AbstractEntity implements CustomizeEntity {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The serial version UID for object serialization. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
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

    /** CREATE_TIME: {BIGINT(20), refers to ACCESS_RESULT.CREATE_TIME} */
    protected Long _createTime;

    // ===================================================================================
    //                                                                             DB Meta
    //                                                                             =======
    /** {@inheritDoc} */
    public DBMeta asDBMeta() {
        return org.codelibs.fess.crawler.db.bsentity.customize.dbmeta.AccessResultDiffDbm.getInstance();
    }

    /** {@inheritDoc} */
    public String asTableDbName() {
        return "AccessResultDiff";
    }

    // ===================================================================================
    //                                                                        Key Handling
    //                                                                        ============
    /** {@inheritDoc} */
    public boolean hasPrimaryKeyValue() {
        return false;
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
        if (obj instanceof BsAccessResultDiff) {
            BsAccessResultDiff other = (BsAccessResultDiff)obj;
            if (!xSV(_id, other._id)) { return false; }
            if (!xSV(_sessionId, other._sessionId)) { return false; }
            if (!xSV(_ruleId, other._ruleId)) { return false; }
            if (!xSV(_url, other._url)) { return false; }
            if (!xSV(_parentUrl, other._parentUrl)) { return false; }
            if (!xSV(_status, other._status)) { return false; }
            if (!xSV(_httpStatusCode, other._httpStatusCode)) { return false; }
            if (!xSV(_method, other._method)) { return false; }
            if (!xSV(_mimeType, other._mimeType)) { return false; }
            if (!xSV(_contentLength, other._contentLength)) { return false; }
            if (!xSV(_executionTime, other._executionTime)) { return false; }
            if (!xSV(_createTime, other._createTime)) { return false; }
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
        hs = xCH(hs, _sessionId);
        hs = xCH(hs, _ruleId);
        hs = xCH(hs, _url);
        hs = xCH(hs, _parentUrl);
        hs = xCH(hs, _status);
        hs = xCH(hs, _httpStatusCode);
        hs = xCH(hs, _method);
        hs = xCH(hs, _mimeType);
        hs = xCH(hs, _contentLength);
        hs = xCH(hs, _executionTime);
        hs = xCH(hs, _createTime);
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
        sb.append(dm).append(xfND(_ruleId));
        sb.append(dm).append(xfND(_url));
        sb.append(dm).append(xfND(_parentUrl));
        sb.append(dm).append(xfND(_status));
        sb.append(dm).append(xfND(_httpStatusCode));
        sb.append(dm).append(xfND(_method));
        sb.append(dm).append(xfND(_mimeType));
        sb.append(dm).append(xfND(_contentLength));
        sb.append(dm).append(xfND(_executionTime));
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
    public AccessResultDiff clone() {
        return (AccessResultDiff)super.clone();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * [get] ID: {BIGINT(20), refers to ACCESS_RESULT.ID} <br>
     * @return The value of the column 'ID'. (NullAllowed even if selected: for no constraint)
     */
    public Long getId() {
        checkSpecifiedProperty("id");
        return _id;
    }

    /**
     * [set] ID: {BIGINT(20), refers to ACCESS_RESULT.ID} <br>
     * @param id The value of the column 'ID'. (NullAllowed: null update allowed for no constraint)
     */
    public void setId(Long id) {
        registerModifiedProperty("id");
        _id = id;
    }

    /**
     * [get] SESSION_ID: {VARCHAR(20), refers to ACCESS_RESULT.SESSION_ID} <br>
     * @return The value of the column 'SESSION_ID'. (NullAllowed even if selected: for no constraint)
     */
    public String getSessionId() {
        checkSpecifiedProperty("sessionId");
        return _sessionId;
    }

    /**
     * [set] SESSION_ID: {VARCHAR(20), refers to ACCESS_RESULT.SESSION_ID} <br>
     * @param sessionId The value of the column 'SESSION_ID'. (NullAllowed: null update allowed for no constraint)
     */
    public void setSessionId(String sessionId) {
        registerModifiedProperty("sessionId");
        _sessionId = sessionId;
    }

    /**
     * [get] RULE_ID: {VARCHAR(20), refers to ACCESS_RESULT.RULE_ID} <br>
     * @return The value of the column 'RULE_ID'. (NullAllowed even if selected: for no constraint)
     */
    public String getRuleId() {
        checkSpecifiedProperty("ruleId");
        return _ruleId;
    }

    /**
     * [set] RULE_ID: {VARCHAR(20), refers to ACCESS_RESULT.RULE_ID} <br>
     * @param ruleId The value of the column 'RULE_ID'. (NullAllowed: null update allowed for no constraint)
     */
    public void setRuleId(String ruleId) {
        registerModifiedProperty("ruleId");
        _ruleId = ruleId;
    }

    /**
     * [get] URL: {VARCHAR(21845), refers to ACCESS_RESULT.URL} <br>
     * @return The value of the column 'URL'. (NullAllowed even if selected: for no constraint)
     */
    public String getUrl() {
        checkSpecifiedProperty("url");
        return _url;
    }

    /**
     * [set] URL: {VARCHAR(21845), refers to ACCESS_RESULT.URL} <br>
     * @param url The value of the column 'URL'. (NullAllowed: null update allowed for no constraint)
     */
    public void setUrl(String url) {
        registerModifiedProperty("url");
        _url = url;
    }

    /**
     * [get] PARENT_URL: {VARCHAR(21845), refers to ACCESS_RESULT.PARENT_URL} <br>
     * @return The value of the column 'PARENT_URL'. (NullAllowed even if selected: for no constraint)
     */
    public String getParentUrl() {
        checkSpecifiedProperty("parentUrl");
        return _parentUrl;
    }

    /**
     * [set] PARENT_URL: {VARCHAR(21845), refers to ACCESS_RESULT.PARENT_URL} <br>
     * @param parentUrl The value of the column 'PARENT_URL'. (NullAllowed: null update allowed for no constraint)
     */
    public void setParentUrl(String parentUrl) {
        registerModifiedProperty("parentUrl");
        _parentUrl = parentUrl;
    }

    /**
     * [get] STATUS: {INT(11), refers to ACCESS_RESULT.STATUS} <br>
     * @return The value of the column 'STATUS'. (NullAllowed even if selected: for no constraint)
     */
    public Integer getStatus() {
        checkSpecifiedProperty("status");
        return _status;
    }

    /**
     * [set] STATUS: {INT(11), refers to ACCESS_RESULT.STATUS} <br>
     * @param status The value of the column 'STATUS'. (NullAllowed: null update allowed for no constraint)
     */
    public void setStatus(Integer status) {
        registerModifiedProperty("status");
        _status = status;
    }

    /**
     * [get] HTTP_STATUS_CODE: {INT(11), refers to ACCESS_RESULT.HTTP_STATUS_CODE} <br>
     * @return The value of the column 'HTTP_STATUS_CODE'. (NullAllowed even if selected: for no constraint)
     */
    public Integer getHttpStatusCode() {
        checkSpecifiedProperty("httpStatusCode");
        return _httpStatusCode;
    }

    /**
     * [set] HTTP_STATUS_CODE: {INT(11), refers to ACCESS_RESULT.HTTP_STATUS_CODE} <br>
     * @param httpStatusCode The value of the column 'HTTP_STATUS_CODE'. (NullAllowed: null update allowed for no constraint)
     */
    public void setHttpStatusCode(Integer httpStatusCode) {
        registerModifiedProperty("httpStatusCode");
        _httpStatusCode = httpStatusCode;
    }

    /**
     * [get] METHOD: {VARCHAR(10), refers to ACCESS_RESULT.METHOD} <br>
     * @return The value of the column 'METHOD'. (NullAllowed even if selected: for no constraint)
     */
    public String getMethod() {
        checkSpecifiedProperty("method");
        return _method;
    }

    /**
     * [set] METHOD: {VARCHAR(10), refers to ACCESS_RESULT.METHOD} <br>
     * @param method The value of the column 'METHOD'. (NullAllowed: null update allowed for no constraint)
     */
    public void setMethod(String method) {
        registerModifiedProperty("method");
        _method = method;
    }

    /**
     * [get] MIME_TYPE: {VARCHAR(100), refers to ACCESS_RESULT.MIME_TYPE} <br>
     * @return The value of the column 'MIME_TYPE'. (NullAllowed even if selected: for no constraint)
     */
    public String getMimeType() {
        checkSpecifiedProperty("mimeType");
        return _mimeType;
    }

    /**
     * [set] MIME_TYPE: {VARCHAR(100), refers to ACCESS_RESULT.MIME_TYPE} <br>
     * @param mimeType The value of the column 'MIME_TYPE'. (NullAllowed: null update allowed for no constraint)
     */
    public void setMimeType(String mimeType) {
        registerModifiedProperty("mimeType");
        _mimeType = mimeType;
    }

    /**
     * [get] CONTENT_LENGTH: {BIGINT(20), refers to ACCESS_RESULT.CONTENT_LENGTH} <br>
     * @return The value of the column 'CONTENT_LENGTH'. (NullAllowed even if selected: for no constraint)
     */
    public Long getContentLength() {
        checkSpecifiedProperty("contentLength");
        return _contentLength;
    }

    /**
     * [set] CONTENT_LENGTH: {BIGINT(20), refers to ACCESS_RESULT.CONTENT_LENGTH} <br>
     * @param contentLength The value of the column 'CONTENT_LENGTH'. (NullAllowed: null update allowed for no constraint)
     */
    public void setContentLength(Long contentLength) {
        registerModifiedProperty("contentLength");
        _contentLength = contentLength;
    }

    /**
     * [get] EXECUTION_TIME: {INT(11), refers to ACCESS_RESULT.EXECUTION_TIME} <br>
     * @return The value of the column 'EXECUTION_TIME'. (NullAllowed even if selected: for no constraint)
     */
    public Integer getExecutionTime() {
        checkSpecifiedProperty("executionTime");
        return _executionTime;
    }

    /**
     * [set] EXECUTION_TIME: {INT(11), refers to ACCESS_RESULT.EXECUTION_TIME} <br>
     * @param executionTime The value of the column 'EXECUTION_TIME'. (NullAllowed: null update allowed for no constraint)
     */
    public void setExecutionTime(Integer executionTime) {
        registerModifiedProperty("executionTime");
        _executionTime = executionTime;
    }

    /**
     * [get] CREATE_TIME: {BIGINT(20), refers to ACCESS_RESULT.CREATE_TIME} <br>
     * @return The value of the column 'CREATE_TIME'. (NullAllowed even if selected: for no constraint)
     */
    public Long getCreateTime() {
        checkSpecifiedProperty("createTime");
        return _createTime;
    }

    /**
     * [set] CREATE_TIME: {BIGINT(20), refers to ACCESS_RESULT.CREATE_TIME} <br>
     * @param createTime The value of the column 'CREATE_TIME'. (NullAllowed: null update allowed for no constraint)
     */
    public void setCreateTime(Long createTime) {
        registerModifiedProperty("createTime");
        _createTime = createTime;
    }
}
