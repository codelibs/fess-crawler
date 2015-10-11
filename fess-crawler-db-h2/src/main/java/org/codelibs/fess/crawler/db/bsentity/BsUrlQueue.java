package org.codelibs.fess.crawler.db.bsentity;

import java.util.List;
import java.util.ArrayList;

import org.dbflute.dbmeta.DBMeta;
import org.dbflute.dbmeta.AbstractEntity;
import org.dbflute.dbmeta.accessory.DomainEntity;
import org.codelibs.fess.crawler.db.allcommon.DBMetaInstanceHandler;
import org.codelibs.fess.crawler.db.exentity.*;

/**
 * The entity of URL_QUEUE as TABLE. <br>
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, SESSION_ID, METHOD, URL, META_DATA, ENCODING, PARENT_URL, DEPTH, LAST_MODIFIED, CREATE_TIME
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
 * String method = entity.getMethod();
 * String url = entity.getUrl();
 * String metaData = entity.getMetaData();
 * String encoding = entity.getEncoding();
 * String parentUrl = entity.getParentUrl();
 * Integer depth = entity.getDepth();
 * Long lastModified = entity.getLastModified();
 * Long createTime = entity.getCreateTime();
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
public abstract class BsUrlQueue extends AbstractEntity implements DomainEntity {

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

    /** METHOD: {NotNull, VARCHAR(10)} */
    protected String _method;

    /** URL: {NotNull, VARCHAR(65536)} */
    protected String _url;

    /** META_DATA: {VARCHAR(65536)} */
    protected String _metaData;

    /** ENCODING: {VARCHAR(20)} */
    protected String _encoding;

    /** PARENT_URL: {VARCHAR(65536)} */
    protected String _parentUrl;

    /** DEPTH: {NotNull, INTEGER(10)} */
    protected Integer _depth;

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
        return "URL_QUEUE";
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
        if (obj instanceof BsUrlQueue) {
            BsUrlQueue other = (BsUrlQueue)obj;
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
        sb.append(dm).append(xfND(_method));
        sb.append(dm).append(xfND(_url));
        sb.append(dm).append(xfND(_metaData));
        sb.append(dm).append(xfND(_encoding));
        sb.append(dm).append(xfND(_parentUrl));
        sb.append(dm).append(xfND(_depth));
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
        return "";
    }

    @Override
    public UrlQueue clone() {
        return (UrlQueue)super.clone();
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
     * [get] META_DATA: {VARCHAR(65536)} <br>
     * @return The value of the column 'META_DATA'. (NullAllowed even if selected: for no constraint)
     */
    public String getMetaData() {
        checkSpecifiedProperty("metaData");
        return _metaData;
    }

    /**
     * [set] META_DATA: {VARCHAR(65536)} <br>
     * @param metaData The value of the column 'META_DATA'. (NullAllowed: null update allowed for no constraint)
     */
    public void setMetaData(String metaData) {
        registerModifiedProperty("metaData");
        _metaData = metaData;
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
     * [get] DEPTH: {NotNull, INTEGER(10)} <br>
     * @return The value of the column 'DEPTH'. (basically NotNull if selected: for the constraint)
     */
    public Integer getDepth() {
        checkSpecifiedProperty("depth");
        return _depth;
    }

    /**
     * [set] DEPTH: {NotNull, INTEGER(10)} <br>
     * @param depth The value of the column 'DEPTH'. (basically NotNull if update: for the constraint)
     */
    public void setDepth(Integer depth) {
        registerModifiedProperty("depth");
        _depth = depth;
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
