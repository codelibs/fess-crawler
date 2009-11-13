package org.seasar.robot.db.bsentity;

import java.io.Serializable;
import java.util.Set;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.db.allcommon.DBMetaInstanceHandler;
import org.seasar.robot.db.exentity.AccessResult;

/**
 * The entity of ACCESS_RESULT_DATA that is TABLE. <br />
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
    /** ID: {PK : NotNull : BIGINT(19) : FK to ACCESS_RESULT} */
    protected Long _id;

    /** TRANSFORMER_NAME: {NotNull : VARCHAR(255)} */
    protected String _transformerName;

    /** DATA: {BLOB(2147483647)} */
    protected byte[] _data;

    /** ENCODING: {VARCHAR(20)} */
    protected String _encoding;

    // -----------------------------------------------------
    //                                              Internal
    //                                              --------
    /** The modified properties for this entity. */
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
    //                                                                    Foreign Property
    //                                                                    ================
    /** ACCESS_RESULT as 'accessResult'. */
    protected AccessResult _accessResult;

    /**
     * ACCESS_RESULT as 'accessResult'. {without lazy-load}
     * @return The entity of foreign property 'accessResult'. (Nullable: If the foreign key does not have 'NotNull' constraint, please check null.)
     */
    public AccessResult getAccessResult() {
        return _accessResult;
    }

    /**
     * ACCESS_RESULT as 'accessResult'.
     * @param accessResult The entity of foreign property 'accessResult'. (Nullable)
     */
    public void setAccessResult(AccessResult accessResult) {
        _accessResult = accessResult;
    }

    // ===================================================================================
    //                                                                   Referrer Property
    //                                                                   =================
    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean hasPrimaryKeyValue() {
        if (_id == null) {
            return false;
        }
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
        if (other == null || !(other instanceof BsAccessResultData)) {
            return false;
        }
        BsAccessResultData otherEntity = (BsAccessResultData) other;
        if (!helpComparingValue(getId(), otherEntity.getId())) {
            return false;
        }
        return true;
    }

    protected boolean helpComparingValue(Object value1, Object value2) {
        if (value1 == null && value2 == null) {
            return true;
        }
        return value1 != null && value2 != null && value1.equals(value2);
    }

    /**
     * Calculates hash-code from primary-key.
     * @return Hash-code from primary-key.
     */
    public int hashCode() {
        int result = 17;
        if (getId() != null) {
            result = (31 * result) + getId().hashCode();
        }
        return result;
    }

    /**
     * @return The display string of all columns and relation existences. (NotNull)
     */
    public String toString() {
        return buildDisplayString(getClass().getSimpleName(), true, true);
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

    private String xbRDS(Entity e, String name) { // buildRelationDisplayString()
        return e.buildDisplayString(name, true, true);
    }

    /**
     * @param name The name for display. (Nullable: If it's null, it does not have a name)
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
            sb.append(xbuildColumnString());
        }
        if (relation) {
            sb.append(xbuildRelationString());
        }
        sb.append("@").append(Integer.toHexString(hashCode()));
        return sb.toString();
    }

    private String xbuildColumnString() {
        String c = ",";
        StringBuilder sb = new StringBuilder();
        sb.append(c).append(getId());
        sb.append(c).append(getTransformerName());
        sb.append(c).append(getData());
        sb.append(c).append(getEncoding());
        if (sb.length() > 0) {
            sb.delete(0, c.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    private String xbuildRelationString() {
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
     * ID: {PK : NotNull : BIGINT(19) : FK to ACCESS_RESULT} <br />
     * @return The value of the column 'ID'. (Nullable)
     */
    public Long getId() {
        return _id;
    }

    /**
     * ID: {PK : NotNull : BIGINT(19) : FK to ACCESS_RESULT} <br />
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
     * DATA: {BLOB(2147483647)} <br />
     * @return The value of the column 'DATA'. (Nullable)
     */
    public byte[] getData() {
        return _data;
    }

    /**
     * DATA: {BLOB(2147483647)} <br />
     * @param data The value of the column 'DATA'. (Nullable)
     */
    public void setData(byte[] data) {
        _modifiedProperties.addPropertyName("data");
        this._data = data;
    }

    /**
     * ENCODING: {VARCHAR(20)} <br />
     * @return The value of the column 'ENCODING'. (Nullable)
     */
    public String getEncoding() {
        return _encoding;
    }

    /**
     * ENCODING: {VARCHAR(20)} <br />
     * @param encoding The value of the column 'ENCODING'. (Nullable)
     */
    public void setEncoding(String encoding) {
        _modifiedProperties.addPropertyName("encoding");
        this._encoding = encoding;
    }
}
