package org.seasar.robot.db.bsentity;

import java.io.Serializable;
import java.util.*;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.Entity;
import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.exentity.*;

/**
 * The entity of ACCESS_RESULT that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, RULE_ID, URL, PARENT_URL, STATUS, HTTP_STATUS_CODE, METHOD, MIME_TYPE, CREATE_TIME
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
 *     ACCESS_RESULT_DATA(AsOne)
 * 
 * [referrer-table]
 *     ACCESS_RESULT_DATA
 * 
 * [foreign-property]
 *     accessResultDataAsOne
 * 
 * [referrer-property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResult implements Entity, Serializable {

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
    /** ID: {PK : ID : NotNull : BIGINT} */
    protected Long _id;

    /** SESSION_ID: {NotNull : VARCHAR(20)} */
    protected String _sessionId;

    /** RULE_ID: {VARCHAR(20)} */
    protected String _ruleId;

    /** URL: {NotNull : VARCHAR(65536)} */
    protected String _url;

    /** PARENT_URL: {VARCHAR(65536)} */
    protected String _parentUrl;

    /** STATUS: {NotNull : INTEGER} */
    protected Integer _status;

    /** HTTP_STATUS_CODE: {NotNull : INTEGER} */
    protected Integer _httpStatusCode;

    /** METHOD: {NotNull : VARCHAR(10)} */
    protected String _method;

    /** MIME_TYPE: {NotNull : VARCHAR(100)} */
    protected String _mimeType;

    /** CREATE_TIME: {NotNull : TIMESTAMP} */
    protected java.sql.Timestamp _createTime;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "ACCESS_RESULT";
    }

    public String getTablePropertyName() { // as JavaBeansRule
        return "accessResult";
    }

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    public DBMeta getDBMeta() {
        return DBMetaInstanceHandler.findDBMeta(getTableDbName());
    }

    // ===================================================================================
    //                                                          Classification Classifying
    //                                                          ==========================
    // ===================================================================================
    //                                                        Classification Determination
    //                                                        ============================
    // ===================================================================================
    //                                                           Classification Name/Alias
    //                                                           =========================
    // ===================================================================================
    //                                                                    Foreign Property
    //                                                                    ================
    /** ACCESS_RESULT_DATA as 'accessResultDataAsOne'. */
    protected AccessResultData _childrenaccessResultDataAsOne;

    /**
     * ACCESS_RESULT_DATA as 'accessResultDataAsOne'. {without lazy-load} <br />
     * @return the entity of foreign property(referrer-as-one) 'accessResultDataAsOne'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public AccessResultData getAccessResultDataAsOne() {
        return _childrenaccessResultDataAsOne;
    }

    /**
     * ACCESS_RESULT_DATA as 'accessResultDataAsOne'.
     * @param accessResultDataAsOne The entity of foreign property(referrer-as-one) 'accessResultDataAsOne'. (Nullable)
     */
    public void setAccessResultDataAsOne(AccessResultData accessResultDataAsOne) {
        _childrenaccessResultDataAsOne = accessResultDataAsOne;
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_id == null) { return false; }
        return true;
    }

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    public Set<String> getModifiedPropertyNames() {
        return _modifiedProperties.getPropertyNames();
    }

    protected EntityModifiedProperties newEntityModifiedProperties() {
        return new EntityModifiedProperties();
    }

    public void clearModifiedPropertyNames() {
        _modifiedProperties.clear();
    }

    public boolean hasModification() {
        return !_modifiedProperties.isEmpty();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * If the primary-key of the other is same as this one, returns true.
     * @param other Other entity.
     * @return Comparing result.
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof BsAccessResult)) { return false; }
        BsAccessResult otherEntity = (BsAccessResult)other;
        if (!helpComparingValue(getId(), otherEntity.getId())) { return false; }
        return true;
    }

    protected boolean helpComparingValue(Object value1, Object value2) {
        if (value1 == null && value2 == null) { return true; }
        return value1 != null && value2 != null && value1.equals(value2);
    }

    /**
     * Calculates hash-code from primary-key.
     * @return Hash-code from primary-key.
     */
    public int hashCode() {
        int result = 17;
        if (getId() != null) { result = (31*result) + getId().hashCode(); }
        return result;
    }

    /**
     * @return The view string of all columns. (NotNull)
     */
    public String toString() {
        String delimiter = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(delimiter).append(getId());
        sb.append(delimiter).append(getSessionId());
        sb.append(delimiter).append(getRuleId());
        sb.append(delimiter).append(getUrl());
        sb.append(delimiter).append(getParentUrl());
        sb.append(delimiter).append(getStatus());
        sb.append(delimiter).append(getHttpStatusCode());
        sb.append(delimiter).append(getMethod());
        sb.append(delimiter).append(getMimeType());
        sb.append(delimiter).append(getCreateTime());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * ID: {PK : ID : NotNull : BIGINT} <br />
     * @return The value of the column 'ID'. (Nullable)
     */
    public Long getId() {
        return _id;
    }

    /**
     * ID: {PK : ID : NotNull : BIGINT} <br />
     * @param id The value of the column 'ID'. (Nullable)
     */
    public void setId(Long id) {
        _modifiedProperties.addPropertyName("id");
        this._id = id;
    }

    /**
     * SESSION_ID: {NotNull : VARCHAR(20)} <br />
     * @return The value of the column 'SESSION_ID'. (Nullable)
     */
    public String getSessionId() {
        return _sessionId;
    }

    /**
     * SESSION_ID: {NotNull : VARCHAR(20)} <br />
     * @param sessionId The value of the column 'SESSION_ID'. (Nullable)
     */
    public void setSessionId(String sessionId) {
        _modifiedProperties.addPropertyName("sessionId");
        this._sessionId = sessionId;
    }

    /**
     * RULE_ID: {VARCHAR(20)} <br />
     * @return The value of the column 'RULE_ID'. (Nullable)
     */
    public String getRuleId() {
        return _ruleId;
    }

    /**
     * RULE_ID: {VARCHAR(20)} <br />
     * @param ruleId The value of the column 'RULE_ID'. (Nullable)
     */
    public void setRuleId(String ruleId) {
        _modifiedProperties.addPropertyName("ruleId");
        this._ruleId = ruleId;
    }

    /**
     * URL: {NotNull : VARCHAR(65536)} <br />
     * @return The value of the column 'URL'. (Nullable)
     */
    public String getUrl() {
        return _url;
    }

    /**
     * URL: {NotNull : VARCHAR(65536)} <br />
     * @param url The value of the column 'URL'. (Nullable)
     */
    public void setUrl(String url) {
        _modifiedProperties.addPropertyName("url");
        this._url = url;
    }

    /**
     * PARENT_URL: {VARCHAR(65536)} <br />
     * @return The value of the column 'PARENT_URL'. (Nullable)
     */
    public String getParentUrl() {
        return _parentUrl;
    }

    /**
     * PARENT_URL: {VARCHAR(65536)} <br />
     * @param parentUrl The value of the column 'PARENT_URL'. (Nullable)
     */
    public void setParentUrl(String parentUrl) {
        _modifiedProperties.addPropertyName("parentUrl");
        this._parentUrl = parentUrl;
    }

    /**
     * STATUS: {NotNull : INTEGER} <br />
     * @return The value of the column 'STATUS'. (Nullable)
     */
    public Integer getStatus() {
        return _status;
    }

    /**
     * STATUS: {NotNull : INTEGER} <br />
     * @param status The value of the column 'STATUS'. (Nullable)
     */
    public void setStatus(Integer status) {
        _modifiedProperties.addPropertyName("status");
        this._status = status;
    }

    /**
     * HTTP_STATUS_CODE: {NotNull : INTEGER} <br />
     * @return The value of the column 'HTTP_STATUS_CODE'. (Nullable)
     */
    public Integer getHttpStatusCode() {
        return _httpStatusCode;
    }

    /**
     * HTTP_STATUS_CODE: {NotNull : INTEGER} <br />
     * @param httpStatusCode The value of the column 'HTTP_STATUS_CODE'. (Nullable)
     */
    public void setHttpStatusCode(Integer httpStatusCode) {
        _modifiedProperties.addPropertyName("httpStatusCode");
        this._httpStatusCode = httpStatusCode;
    }

    /**
     * METHOD: {NotNull : VARCHAR(10)} <br />
     * @return The value of the column 'METHOD'. (Nullable)
     */
    public String getMethod() {
        return _method;
    }

    /**
     * METHOD: {NotNull : VARCHAR(10)} <br />
     * @param method The value of the column 'METHOD'. (Nullable)
     */
    public void setMethod(String method) {
        _modifiedProperties.addPropertyName("method");
        this._method = method;
    }

    /**
     * MIME_TYPE: {NotNull : VARCHAR(100)} <br />
     * @return The value of the column 'MIME_TYPE'. (Nullable)
     */
    public String getMimeType() {
        return _mimeType;
    }

    /**
     * MIME_TYPE: {NotNull : VARCHAR(100)} <br />
     * @param mimeType The value of the column 'MIME_TYPE'. (Nullable)
     */
    public void setMimeType(String mimeType) {
        _modifiedProperties.addPropertyName("mimeType");
        this._mimeType = mimeType;
    }

    /**
     * CREATE_TIME: {NotNull : TIMESTAMP} <br />
     * @return The value of the column 'CREATE_TIME'. (Nullable)
     */
    public java.sql.Timestamp getCreateTime() {
        return _createTime;
    }

    /**
     * CREATE_TIME: {NotNull : TIMESTAMP} <br />
     * @param createTime The value of the column 'CREATE_TIME'. (Nullable)
     */
    public void setCreateTime(java.sql.Timestamp createTime) {
        _modifiedProperties.addPropertyName("createTime");
        this._createTime = createTime;
    }
}
