package org.seasar.robot.db.bsbhv.pmbean;

/**
 * The parameter-bean of AccessResultPmb.
 * @author DBFlute(AutoGenerator)
 */
public class BsAccessResultPmb {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The value of newSessionId. */
    protected String _newSessionId;

    /** The value of oldSessionId. */
    protected String _oldSessionId;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsAccessResultPmb() {
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    /**
     * @param value Query value. (Nullable)
     * @return Converted value. (Nullable)
     */
    protected String convertEmptyToNullIfString(String value) {
        return filterRemoveEmptyString(value);
    }

    /**
     * @param value Query value string. (Nullable)
     * @return Removed-empty value. (Nullable)
     */
    protected String filterRemoveEmptyString(String value) {
        return ((value != null && !"".equals(value)) ? value : null);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * {@inheritDoc}
     * @return The view of properties. (NotNull)
     */
    @Override
    public String toString() {
        final String delimiter = ",";
        final StringBuffer sb = new StringBuffer();
        sb.append(delimiter).append(_newSessionId);
        sb.append(delimiter).append(_oldSessionId);
        if (sb.length() > 0) {
            sb.delete(0, delimiter.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the value of newSessionId. (Converted empty to null)
     * @return The value of newSessionId. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public String getNewSessionId() {
        return (String) convertEmptyToNullIfString(_newSessionId);
    }

    /**
     * Set the value of newSessionId.
     * @param newSessionId The value of newSessionId. (Nullable)
     */
    public void setNewSessionId(String newSessionId) {
        _newSessionId = newSessionId;
    }

    /**
     * Get the value of oldSessionId. (Converted empty to null)
     * @return The value of oldSessionId. (Nullable & NotEmptyString: if the value is empty string, returns null)
     */
    public String getOldSessionId() {
        return (String) convertEmptyToNullIfString(_oldSessionId);
    }

    /**
     * Set the value of oldSessionId.
     * @param oldSessionId The value of oldSessionId. (Nullable)
     */
    public void setOldSessionId(String oldSessionId) {
        _oldSessionId = oldSessionId;
    }

}
