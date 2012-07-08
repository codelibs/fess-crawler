/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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

import java.io.Serializable;
import java.util.Date;

import org.seasar.robot.db.allcommon.DBFluteConfig;
import org.seasar.robot.dbflute.jdbc.FetchBean;
import org.seasar.robot.dbflute.jdbc.ParameterUtil;
import org.seasar.robot.dbflute.jdbc.ParameterUtil.ShortCharHandlingMode;
import org.seasar.robot.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The parameter-bean of AccessResultPmb.
 * 
 * @author DBFlute(AutoGenerator)
 */
public class BsAccessResultPmb implements ParameterBean, FetchBean,
        Serializable {

    // ===================================================================================
    // Definition
    // ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    // Attribute
    // =========
    /** newSessionId */
    protected String _newSessionId;

    /** oldSessionId */
    protected String _oldSessionId;

    /** The max size of safety result. */
    protected int _safetyMaxResultSize;

    // ===================================================================================
    // Constructor
    // ===========
    public BsAccessResultPmb() {
    }

    // ===================================================================================
    // Safety Result
    // =============
    /**
     * {@inheritDoc}
     */
    public void checkSafetyResult(final int safetyMaxResultSize) {
        _safetyMaxResultSize = safetyMaxResultSize;
    }

    /**
     * {@inheritDoc}
     */
    public int getSafetyMaxResultSize() {
        return _safetyMaxResultSize;
    }

    // ===================================================================================
    // Assist Helper
    // =============
    protected String filterStringParameter(final String value) {
        if (isEmptyStringParameterAllowed()) {
            return value;
        }
        return convertEmptyToNull(value);
    }

    protected boolean isEmptyStringParameterAllowed() {
        return DBFluteConfig.getInstance().isEmptyStringParameterAllowed();
    }

    protected String convertEmptyToNull(final String value) {
        return ParameterUtil.convertEmptyToNull(value);
    }

    protected String handleShortChar(final String propertyName,
            final String value, final Integer size) {
        final ShortCharHandlingMode mode =
            getShortCharHandlingMode(propertyName, value, size);
        return ParameterUtil.handleShortChar(propertyName, value, size, mode);
    }

    protected ShortCharHandlingMode getShortCharHandlingMode(
            final String propertyName, final String value, final Integer size) {
        return ShortCharHandlingMode.NONE;
    }

    @SuppressWarnings("unchecked")
    protected <NUMBER extends Number> NUMBER toNumber(final Object obj,
            final Class<NUMBER> type) {
        return (NUMBER) DfTypeUtil.toNumber(obj, type);
    }

    protected Boolean toBoolean(final Object obj) {
        return DfTypeUtil.toBoolean(obj);
    }

    protected Date toUtilDate(final Date date) {
        return DfTypeUtil.toDate(date); // if sub class, re-create as pure date
    }

    protected String formatUtilDate(final Date date) {
        final String pattern = "yyyy-MM-dd";
        return DfTypeUtil.toString(date, pattern);
    }

    protected String formatByteArray(final byte[] bytes) {
        return "byte["
            + (bytes != null ? String.valueOf(bytes.length) : "null") + "]";
    }

    // ===================================================================================
    // Basic Override
    // ==============
    /**
     * @return The display string of all parameters. (NotNull)
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(DfTypeUtil.toClassTitle(this)).append(":");
        sb.append(xbuildColumnString());
        return sb.toString();
    }

    private String xbuildColumnString() {
        final String c = ", ";
        final StringBuilder sb = new StringBuilder();
        sb.append(c).append(_newSessionId);
        sb.append(c).append(_oldSessionId);
        if (sb.length() > 0) {
            sb.delete(0, c.length());
        }
        sb.insert(0, "{").append("}");
        return sb.toString();
    }

    // ===================================================================================
    // Accessor
    // ========
    /**
     * [get] newSessionId
     * 
     * @return The value of newSessionId. (Nullable, NotEmptyString(when
     *         String): if empty string, returns null)
     */
    public String getNewSessionId() {
        return filterStringParameter(_newSessionId);
    }

    /**
     * [set] newSessionId
     * 
     * @param newSessionId
     *            The value of newSessionId. (NullAllowed)
     */
    public void setNewSessionId(final String newSessionId) {
        _newSessionId = newSessionId;
    }

    /**
     * [get] oldSessionId
     * 
     * @return The value of oldSessionId. (Nullable, NotEmptyString(when
     *         String): if empty string, returns null)
     */
    public String getOldSessionId() {
        return filterStringParameter(_oldSessionId);
    }

    /**
     * [set] oldSessionId
     * 
     * @param oldSessionId
     *            The value of oldSessionId. (NullAllowed)
     */
    public void setOldSessionId(final String oldSessionId) {
        _oldSessionId = oldSessionId;
    }

}
