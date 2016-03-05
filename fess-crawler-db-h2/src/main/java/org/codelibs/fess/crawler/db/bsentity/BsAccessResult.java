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
 * The entity of ACCESS_RESULT as TABLE. <br>
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, RULE_ID, URL, PARENT_URL, STATUS, HTTP_STATUS_CODE, METHOD, MIME_TYPE, CONTENT_LENGTH, EXECUTION_TIME, LAST_MODIFIED, CREATE_TIME
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
 *     ACCESS_RESULT_DATA(AsOne)
 * 
 * [referrer table]
 *     ACCESS_RESULT_DATA
 * 
 * [foreign property]
 *     accessResultDataAsOne
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
 * Long lastModified = entity.getLastModified();
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
 * entity.setLastModified(lastModified);
 * entity.setCreateTime(createTime);
 * = = = = = = = = = =/
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResult extends AbstractEntity implements DomainEntity {

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

    /** RULE_ID: {VARCHAR(20)} */
    protected String _ruleId;

    /** URL: {IX+, NotNull, VARCHAR(65536)} */
    protected String _url;

    /** PARENT_URL: {VARCHAR(65536)} */
    protected String _parentUrl;

    /** STATUS: {NotNull, INTEGER(10)} */
    protected Integer _status;

    /** HTTP_STATUS_CODE: {NotNull, INTEGER(10)} */
    protected Integer _httpStatusCode;

    /** METHOD: {NotNull, VARCHAR(10)} */
    protected String _method;

    /** MIME_TYPE: {NotNull, VARCHAR(100)} */
    protected String _mimeType;

    /** CONTENT_LENGTH: {NotNull, BIGINT(19)} */
    protected Long _contentLength;

    /** EXECUTION_TIME: {NotNull, INTEGER(10)} */
    protected Integer _executionTime;

    /** LAST_MODIFIED: {BIGINT(19)} */
    protected Long _lastModified;

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
        return "ACCESS_RESULT";
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
    /** ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'. */
    protected OptionalEntity<AccessResultData> _accessResultDataAsOne;

    /**
     * [get] ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * Optional: alwaysPresent(), ifPresent().orElse(), get(), ...
     * @return the entity of foreign property(referrer-as-one) 'accessResultDataAsOne'. (NotNull, EmptyAllowed: when e.g. no data, no setupSelect)
     */
    public OptionalEntity<AccessResultData> getAccessResultDataAsOne() {
        if (_accessResultDataAsOne == null) { _accessResultDataAsOne = OptionalEntity.relationEmpty(this, "accessResultDataAsOne"); }
        return _accessResultDataAsOne;
    }

    /**
     * [set] ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * @param accessResultDataAsOne The entity of foreign property(referrer-as-one) 'accessResultDataAsOne'. (NullAllowed)
     */
    public void setAccessResultDataAsOne(OptionalEntity<AccessResultData> accessResultDataAsOne) {
        _accessResultDataAsOne = accessResultDataAsOne;
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
        if (obj instanceof BsAccessResult) {
            BsAccessResult other = (BsAccessResult)obj;
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
        if (_accessResultDataAsOne != null && _accessResultDataAsOne.isPresent())
        { sb.append(li).append(xbRDS(_accessResultDataAsOne, "accessResultDataAsOne")); }
        return sb.toString();
    }
    protected <ET extends Entity> String xbRDS(org.dbflute.optional.OptionalEntity<ET> et, String name) { // buildRelationDisplayString()
        return et.get().buildDisplayString(name, true, true);
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
        sb.append(dm).append(xfND(_lastModified));
        sb.append(dm).append(xfND(_createTime));
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    @Override
    protected String doBuildRelationString(String dm) {
        StringBuilder sb = new StringBuilder();
        if (_accessResultDataAsOne != null && _accessResultDataAsOne.isPresent())
        { sb.append(dm).append("accessResultDataAsOne"); }
        if (sb.length() > dm.length()) {
            sb.delete(0, dm.length()).insert(0, "(").append(")");
        }
        return sb.toString();
    }

    @Override
    public AccessResult clone() {
        return (AccessResult)super.clone();
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
     * [get] RULE_ID: {VARCHAR(20)} <br>
     * @return The value of the column 'RULE_ID'. (NullAllowed even if selected: for no constraint)
     */
    public String getRuleId() {
        checkSpecifiedProperty("ruleId");
        return _ruleId;
    }

    /**
     * [set] RULE_ID: {VARCHAR(20)} <br>
     * @param ruleId The value of the column 'RULE_ID'. (NullAllowed: null update allowed for no constraint)
     */
    public void setRuleId(String ruleId) {
        registerModifiedProperty("ruleId");
        _ruleId = ruleId;
    }

    /**
     * [get] URL: {IX+, NotNull, VARCHAR(65536)} <br>
     * @return The value of the column 'URL'. (basically NotNull if selected: for the constraint)
     */
    public String getUrl() {
        checkSpecifiedProperty("url");
        return _url;
    }

    /**
     * [set] URL: {IX+, NotNull, VARCHAR(65536)} <br>
     * @param url The value of the column 'URL'. (basically NotNull if update: for the constraint)
     */
    public void setUrl(String url) {
        registerModifiedProperty("url");
        _url = url;
    }

    /**
     * [get] PARENT_URL: {VARCHAR(65536)} <br>
     * @return The value of the column 'PARENT_URL'. (NullAllowed even if selected: for no constraint)
     */
    public String getParentUrl() {
        checkSpecifiedProperty("parentUrl");
        return _parentUrl;
    }

    /**
     * [set] PARENT_URL: {VARCHAR(65536)} <br>
     * @param parentUrl The value of the column 'PARENT_URL'. (NullAllowed: null update allowed for no constraint)
     */
    public void setParentUrl(String parentUrl) {
        registerModifiedProperty("parentUrl");
        _parentUrl = parentUrl;
    }

    /**
     * [get] STATUS: {NotNull, INTEGER(10)} <br>
     * @return The value of the column 'STATUS'. (basically NotNull if selected: for the constraint)
     */
    public Integer getStatus() {
        checkSpecifiedProperty("status");
        return _status;
    }

    /**
     * [set] STATUS: {NotNull, INTEGER(10)} <br>
     * @param status The value of the column 'STATUS'. (basically NotNull if update: for the constraint)
     */
    public void setStatus(Integer status) {
        registerModifiedProperty("status");
        _status = status;
    }

    /**
     * [get] HTTP_STATUS_CODE: {NotNull, INTEGER(10)} <br>
     * @return The value of the column 'HTTP_STATUS_CODE'. (basically NotNull if selected: for the constraint)
     */
    public Integer getHttpStatusCode() {
        checkSpecifiedProperty("httpStatusCode");
        return _httpStatusCode;
    }

    /**
     * [set] HTTP_STATUS_CODE: {NotNull, INTEGER(10)} <br>
     * @param httpStatusCode The value of the column 'HTTP_STATUS_CODE'. (basically NotNull if update: for the constraint)
     */
    public void setHttpStatusCode(Integer httpStatusCode) {
        registerModifiedProperty("httpStatusCode");
        _httpStatusCode = httpStatusCode;
    }

    /**
     * [get] METHOD: {NotNull, VARCHAR(10)} <br>
     * @return The value of the column 'METHOD'. (basically NotNull if selected: for the constraint)
     */
    public String getMethod() {
        checkSpecifiedProperty("method");
        return _method;
    }

    /**
     * [set] METHOD: {NotNull, VARCHAR(10)} <br>
     * @param method The value of the column 'METHOD'. (basically NotNull if update: for the constraint)
     */
    public void setMethod(String method) {
        registerModifiedProperty("method");
        _method = method;
    }

    /**
     * [get] MIME_TYPE: {NotNull, VARCHAR(100)} <br>
     * @return The value of the column 'MIME_TYPE'. (basically NotNull if selected: for the constraint)
     */
    public String getMimeType() {
        checkSpecifiedProperty("mimeType");
        return _mimeType;
    }

    /**
     * [set] MIME_TYPE: {NotNull, VARCHAR(100)} <br>
     * @param mimeType The value of the column 'MIME_TYPE'. (basically NotNull if update: for the constraint)
     */
    public void setMimeType(String mimeType) {
        registerModifiedProperty("mimeType");
        _mimeType = mimeType;
    }

    /**
     * [get] CONTENT_LENGTH: {NotNull, BIGINT(19)} <br>
     * @return The value of the column 'CONTENT_LENGTH'. (basically NotNull if selected: for the constraint)
     */
    public Long getContentLength() {
        checkSpecifiedProperty("contentLength");
        return _contentLength;
    }

    /**
     * [set] CONTENT_LENGTH: {NotNull, BIGINT(19)} <br>
     * @param contentLength The value of the column 'CONTENT_LENGTH'. (basically NotNull if update: for the constraint)
     */
    public void setContentLength(Long contentLength) {
        registerModifiedProperty("contentLength");
        _contentLength = contentLength;
    }

    /**
     * [get] EXECUTION_TIME: {NotNull, INTEGER(10)} <br>
     * @return The value of the column 'EXECUTION_TIME'. (basically NotNull if selected: for the constraint)
     */
    public Integer getExecutionTime() {
        checkSpecifiedProperty("executionTime");
        return _executionTime;
    }

    /**
     * [set] EXECUTION_TIME: {NotNull, INTEGER(10)} <br>
     * @param executionTime The value of the column 'EXECUTION_TIME'. (basically NotNull if update: for the constraint)
     */
    public void setExecutionTime(Integer executionTime) {
        registerModifiedProperty("executionTime");
        _executionTime = executionTime;
    }

    /**
     * [get] LAST_MODIFIED: {BIGINT(19)} <br>
     * @return The value of the column 'LAST_MODIFIED'. (NullAllowed even if selected: for no constraint)
     */
    public Long getLastModified() {
        checkSpecifiedProperty("lastModified");
        return _lastModified;
    }

    /**
     * [set] LAST_MODIFIED: {BIGINT(19)} <br>
     * @param lastModified The value of the column 'LAST_MODIFIED'. (NullAllowed: null update allowed for no constraint)
     */
    public void setLastModified(Long lastModified) {
        registerModifiedProperty("lastModified");
        _lastModified = lastModified;
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
