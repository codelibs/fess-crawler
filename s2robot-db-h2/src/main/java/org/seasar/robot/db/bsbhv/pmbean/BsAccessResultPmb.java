/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.db.bsbhv.pmbean;

import org.seasar.robot.dbflute.cbean.FetchBean;
import org.seasar.robot.dbflute.twowaysql.pmbean.ParameterBean;

/**
 * The parameter-bean of AccessResultPmb.
 * @author DBFlute(AutoGenerator)
 */
public class BsAccessResultPmb implements ParameterBean, FetchBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The value of newSessionId. */
    protected String _newSessionId;

    /** The value of oldSessionId. */
    protected String _oldSessionId;

    /** The max size of safety result. */
    protected int _safetyMaxResultSize;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public BsAccessResultPmb() {
    }

    // ===================================================================================
    //                                                                       Safety Result
    //                                                                       =============
    /**
     * {@inheritDoc}
     */
    public void checkSafetyResult(int safetyMaxResultSize) {
        _safetyMaxResultSize = safetyMaxResultSize;
    }

    /**
     * {@inheritDoc}
     */
    public int getSafetyMaxResultSize() {
        return _safetyMaxResultSize;
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
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
     * @return The display string of all parameters. (NotNull)
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append(":");
        sb.append(xbuildColumnString());
        return sb.toString();
    }

    private String xbuildColumnString() {
        final String delimiter = ",";
        final StringBuilder sb = new StringBuilder();
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
