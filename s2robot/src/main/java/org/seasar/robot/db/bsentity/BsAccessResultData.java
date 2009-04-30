package org.seasar.robot.db.bsentity;

import java.io.Serializable;
import java.util.*;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.Entity;
import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.exentity.*;

/**
 * The entity of ACCESS_RESULT_DATA that the type is TABLE. <br />
 * <pre>
 * [primary-key]
 *     ID
 * 
 * [column]
 *     ID, TRANSFORMER_NAME, DATA
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
    /** ID: {PK : NotNull : BIGINT : FK to ACCESS_RESULT} */
    protected Long _id;

    /** TRANSFORMER_NAME: {NotNull : VARCHAR(255)} */
    protected String _transformerName;

    /** DATA: {CLOB} */
    protected String _data;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The attribute of entity modified properties. (for S2Dao) */
    protected EntityModifiedProperties _modifiedProperties = newEntityModifiedProperties();
    
    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    public String getTableDbName() {
        return "ACCESS_RESULT_DATA";
    }

    public String getTablePropertyName() { // as JavaBeansRule
        return "accessResultData";
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
    /** ACCESS_RESULT as 'accessResult'. */
    protected AccessResult _parentAccessResult;

    /**
     * ACCESS_RESULT as 'accessResult'. {without lazy-load}
     * @return The entity of foreign property 'accessResult'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public AccessResult getAccessResult() {
        return _parentAccessResult;
    }

    /**
     * ACCESS_RESULT as 'accessResult'.
     * @param accessResult The entity of foreign property 'accessResult'. (Nullable)
     */
    public void setAccessResult(AccessResult accessResult) {
        _parentAccessResult = accessResult;
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
        if (other == null || !(other instanceof BsAccessResultData)) { return false; }
        BsAccessResultData otherEntity = (BsAccessResultData)other;
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
        sb.append(delimiter).append(getTransformerName());
        sb.append(delimiter).append(getData());
        if (sb.length() > 0) { sb.delete(0, delimiter.length()); }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * ID: {PK : NotNull : BIGINT : FK to ACCESS_RESULT} <br />
     * @return The value of the column 'ID'. (Nullable)
     */
    public Long getId() {
        return _id;
    }

    /**
     * ID: {PK : NotNull : BIGINT : FK to ACCESS_RESULT} <br />
     * @param id The value of the column 'ID'. (Nullable)
     */
    public void setId(Long id) {
        _modifiedProperties.addPropertyName("id");
        this._id = id;
    }

    /**
     * TRANSFORMER_NAME: {NotNull : VARCHAR(255)} <br />
     * @return The value of the column 'TRANSFORMER_NAME'. (Nullable)
     */
    public String getTransformerName() {
        return _transformerName;
    }

    /**
     * TRANSFORMER_NAME: {NotNull : VARCHAR(255)} <br />
     * @param transformerName The value of the column 'TRANSFORMER_NAME'. (Nullable)
     */
    public void setTransformerName(String transformerName) {
        _modifiedProperties.addPropertyName("transformerName");
        this._transformerName = transformerName;
    }

    /**
     * DATA: {CLOB} <br />
     * @return The value of the column 'DATA'. (Nullable)
     */
    public String getData() {
        return _data;
    }

    /**
     * DATA: {CLOB} <br />
     * @param data The value of the column 'DATA'. (Nullable)
     */
    public void setData(String data) {
        _modifiedProperties.addPropertyName("data");
        this._data = data;
    }
}
