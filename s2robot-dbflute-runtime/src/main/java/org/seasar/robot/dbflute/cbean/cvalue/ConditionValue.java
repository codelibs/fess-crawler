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
package org.seasar.robot.dbflute.cbean.cvalue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The value of condition.
 * @author jflute
 */
public class ConditionValue implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    public static final String FIXED_KEY_QUERY = "query";
    public static final String FIXED_KEY_INLINE = "inline";
    public static final String FIXED_KEY_ONCLAUSE = "onClause";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The map of fixed values. map:{[query or inline or onClause] = map:{[condition-key] = [value]}} */
    protected Map<String, Map<String, Object>> _fixedValueMap;

    /** The map of varying values. map:{[condition-key] = map:{[varying-key] = [value]}} */
    protected Map<String, Map<String, Object>> _varyingValueMap;

    // temporary query modes:
    protected boolean _orScopeQuery; // completely independent
    protected boolean _inline;
    protected boolean _onClause; // can be true, when in-line is true

    // ===================================================================================
    //                                                                               Equal
    //                                                                               =====
    protected String _equalLatestLocation;
    protected transient ValueHandler _equalValueHandler;

    protected ValueHandler getEqualValueHandler() {
        if (_equalValueHandler == null) {
            _equalValueHandler = new StandardValueHandler(ConditionKey.CK_EQUAL);
        }
        return _equalValueHandler;
    }

    /**
     * Does it has the value of equal?
     * @return Determination. (NotNull)
     */
    public boolean hasEqual() {
        return getEqualValueHandler().hasValue();
    }

    /**
     * Does the value equal the value of equal?
     * @param value The value of equal. (NullAllowed)
     * @return Determination. (NotNull)
     */
    public boolean equalEqual(Object value) {
        return getEqualValueHandler().equalValue(value);
    }

    /**
     * Override the value of equal.
     * @param value The value of equal. (NullAllowed)
     */
    public void overrideEqual(Object value) {
        getEqualValueHandler().overrideValue(value);
    }

    /**
     * Set up the value of equal.
     * @param value The value of equal. (NullAllowed)
     * @param location The base location of equal. (NotNull)
     */
    public void setupEqual(Object value, String location) {
        _equalLatestLocation = location + "." + getEqualValueHandler().setValue(value);
    }

    /**
     * Get the latest location of equal.
     * @return The latest location of equal. (NullAllowed)
     */
    public String getEqualLatestLocation() {
        return _equalLatestLocation;
    }

    // ===================================================================================
    //                                                                           Not Equal
    //                                                                           =========
    protected String _notEqualLatestLocation;
    protected transient ValueHandler _notEqualValueHandler;

    protected ValueHandler getNotEqualValueHandler() {
        if (_notEqualValueHandler == null) {
            _notEqualValueHandler = new StandardValueHandler(ConditionKey.CK_NOT_EQUAL_STANDARD);
        }
        return _notEqualValueHandler;
    }

    /**
     * Does it has the value of notEqual?
     * @return Determination. (NotNull)
     */
    public boolean hasNotEqual() {
        return getNotEqualValueHandler().hasValue();
    }

    /**
     * Does the value equal the value of notEqual?
     * @param value The value of notEqual. (NullAllowed)
     * @return Determination. (NotNull)
     */
    public boolean equalNotEqual(Object value) {
        return getNotEqualValueHandler().equalValue(value);
    }

    /**
     * Override the value of notEqual.
     * @param value The value of notEqual. (NullAllowed)
     */
    public void overrideNotEqual(Object value) {
        getNotEqualValueHandler().overrideValue(value);
    }

    /**
     * Set up the value of notEqual.
     * @param value The value of notEqual. (NullAllowed)
     * @param location The base location of notEqual. (NotNull)
     */
    public void setupNotEqual(Object value, String location) {
        _notEqualLatestLocation = location + "." + getNotEqualValueHandler().setValue(value);
    }

    /**
     * Get the latest location of notEqual.
     * @return The latest location of notEqual. (NullAllowed)
     */
    public String getNotEqualLatestLocation() {
        return _notEqualLatestLocation;
    }

    // ===================================================================================
    //                                                                        Greater Than
    //                                                                        ============
    protected String _greaterThanLatestLocation;
    protected transient ValueHandler _greaterThanValueHandler;

    protected ValueHandler getGreaterThanValueHandler() {
        if (_greaterThanValueHandler == null) {
            _greaterThanValueHandler = new StandardValueHandler(ConditionKey.CK_GREATER_THAN);
        }
        return _greaterThanValueHandler;
    }

    /**
     * Does it has the value of greaterThan?
     * @return Determination. (NotNull)
     */
    public boolean hasGreaterThan() {
        return getGreaterThanValueHandler().hasValue();
    }

    /**
     * Does the value equal the value of greaterThan?
     * @param value The value of greaterThan. (NullAllowed)
     * @return Determination. (NotNull)
     */
    public boolean equalGreaterThan(Object value) {
        return getGreaterThanValueHandler().equalValue(value);
    }

    /**
     * Override the value of greaterThan.
     * @param value The value of greaterThan. (NullAllowed)
     */
    public void overrideGreaterThan(Object value) {
        getGreaterThanValueHandler().overrideValue(value);
    }

    /**
     * Set up the value of greaterThan.
     * @param value The value of greaterThan. (NullAllowed)
     * @param location The base location of greaterThan. (NotNull)
     */
    public void setupGreaterThan(Object value, String location) {
        _greaterThanLatestLocation = location + "." + getGreaterThanValueHandler().setValue(value);
    }

    /**
     * Get the latest location of greaterThan.
     * @return The latest location of greaterThan. (NullAllowed)
     */
    public String getGreaterThanLatestLocation() {
        return _greaterThanLatestLocation;
    }

    // ===================================================================================
    //                                                                           Less Than
    //                                                                           =========
    protected String _lessThanLatestLocation;
    protected transient ValueHandler _lessThanValueHandler;

    protected ValueHandler getLessThanValueHandler() {
        if (_lessThanValueHandler == null) {
            _lessThanValueHandler = new StandardValueHandler(ConditionKey.CK_LESS_THAN);
        }
        return _lessThanValueHandler;
    }

    /**
     * Does it has the value of lessThan?
     * @return Determination. (NotNull)
     */
    public boolean hasLessThan() {
        return getLessThanValueHandler().hasValue();
    }

    /**
     * Does the value equal the value of lessThan?
     * @param value The value of lessThan. (NullAllowed)
     * @return Determination. (NotNull)
     */
    public boolean equalLessThan(Object value) {
        return getLessThanValueHandler().equalValue(value);
    }

    /**
     * Override the value of lessThan.
     * @param value The value of lessThan. (NullAllowed)
     */
    public void overrideLessThan(Object value) {
        getLessThanValueHandler().overrideValue(value);
    }

    /**
     * Set up the value of lessThan.
     * @param value The value of lessThan. (NullAllowed)
     * @param location The base location of lessThan. (NotNull)
     */
    public void setupLessThan(Object value, String location) {
        _lessThanLatestLocation = location + "." + getLessThanValueHandler().setValue(value);
    }

    /**
     * Get the latest location of lessThan.
     * @return The latest location of lessThan. (NullAllowed)
     */
    public String getLessThanLatestLocation() {
        return _lessThanLatestLocation;
    }

    // ===================================================================================
    //                                                                       Greater Equal
    //                                                                       =============
    protected String _greaterEqualLatestLocation;
    protected transient ValueHandler _greaterEqualValueHandler;

    protected ValueHandler getGreaterEqualValueHandler() {
        if (_greaterEqualValueHandler == null) {
            _greaterEqualValueHandler = new StandardValueHandler(ConditionKey.CK_GREATER_EQUAL);
        }
        return _greaterEqualValueHandler;
    }

    /**
     * Does it has the value of greaterEqual?
     * @return Determination. (NotNull)
     */
    public boolean hasGreaterEqual() {
        return getGreaterEqualValueHandler().hasValue();
    }

    /**
     * Does the value equal the value of greaterEqual?
     * @param value The value of greaterEqual. (NullAllowed)
     * @return Determination. (NotNull)
     */
    public boolean equalGreaterEqual(Object value) {
        return getGreaterEqualValueHandler().equalValue(value);
    }

    /**
     * Override the value of greaterEqual.
     * @param value The value of greaterEqual. (NullAllowed)
     */
    public void overrideGreaterEqual(Object value) {
        getGreaterEqualValueHandler().overrideValue(value);
    }

    /**
     * Set up the value of greaterEqual.
     * @param value The value of greaterEqual. (NullAllowed)
     * @param location The base location of greaterEqual. (NotNull)
     */
    public void setupGreaterEqual(Object value, String location) {
        _greaterEqualLatestLocation = location + "." + getGreaterEqualValueHandler().setValue(value);
    }

    /**
     * Get the latest location of greaterEqual.
     * @return The latest location of greaterEqual. (NullAllowed)
     */
    public String getGreaterEqualLatestLocation() {
        return _greaterEqualLatestLocation;
    }

    // ===================================================================================
    //                                                                          Less Equal
    //                                                                          ==========
    protected String _lessEqualLatestLocation;
    protected transient ValueHandler _lessEqualValueHandler;

    protected ValueHandler getLessEqualValueHandler() {
        if (_lessEqualValueHandler == null) {
            _lessEqualValueHandler = new StandardValueHandler(ConditionKey.CK_LESS_EQUAL);
        }
        return _lessEqualValueHandler;
    }

    /**
     * Does it has the value of lessEqual?
     * @return Determination. (NotNull)
     */
    public boolean hasLessEqual() {
        return getLessEqualValueHandler().hasValue();
    }

    /**
     * Does the value equal the value of lessEqual?
     * @param value The value of lessEqual. (NullAllowed)
     * @return Determination. (NotNull)
     */
    public boolean equalLessEqual(Object value) {
        return getLessEqualValueHandler().equalValue(value);
    }

    /**
     * Override the value of lessEqual.
     * @param value The value of lessEqual. (NullAllowed)
     */
    public void overrideLessEqual(Object value) {
        getLessEqualValueHandler().overrideValue(value);
    }

    /**
     * Set up the value of lessEqual.
     * @param value The value of lessEqual. (NullAllowed)
     * @param location The base location of lessEqual. (NotNull)
     */
    public void setupLessEqual(Object value, String location) {
        _lessEqualLatestLocation = location + "." + getLessEqualValueHandler().setValue(value);
    }

    /**
     * Get the latest location of lessEqual.
     * @return The latest location of lessEqual. (NullAllowed)
     */
    public String getLessEqualLatestLocation() {
        return _lessEqualLatestLocation;
    }

    // ===================================================================================
    //                                                                            In Scope
    //                                                                            ========
    protected String _inScopeLatestLocation;
    protected transient ValueHandler _inScopeValueHandler;

    protected ValueHandler getInScopeValueHandler() {
        if (_inScopeValueHandler == null) {
            _inScopeValueHandler = new VaryingValueHandler(ConditionKey.CK_IN_SCOPE);
        }
        return _inScopeValueHandler;
    }

    /**
     * Set up the value of inScope.
     * @param value The value of inScope. (NullAllowed)
     * @param location The base location of inScope. (NotNull)
     */
    public void setupInScope(Object value, String location) {
        final String key = getInScopeValueHandler().setValue(value);
        _inScopeLatestLocation = location + "." + key;
    }

    /**
     * Get the latest location of inScope.
     * @return The latest location of inScope. (NullAllowed)
     */
    public String getInScopeLatestLocation() {
        return _inScopeLatestLocation;
    }

    // ===================================================================================
    //                                                                        Not In Scope
    //                                                                        ============
    protected String _notInScopeLatestLocation;
    protected transient ValueHandler _notInScopeValueHandler;

    protected ValueHandler getNotInScopeValueHandler() {
        if (_notInScopeValueHandler == null) {
            _notInScopeValueHandler = new VaryingValueHandler(ConditionKey.CK_NOT_IN_SCOPE);
        }
        return _notInScopeValueHandler;
    }

    /**
     * Set up the value of notInScope.
     * @param value The value of notInScope. (NullAllowed)
     * @param location The base location of notInScope. (NotNull)
     */
    public void setupNotInScope(Object value, String location) {
        final String key = getNotInScopeValueHandler().setValue(value);
        _notInScopeLatestLocation = location + "." + key;
    }

    /**
     * Get the latest location of notInScope.
     * @return The latest location of notInScope. (NullAllowed)
     */
    public String getNotInScopeLatestLocation() {
        return _notInScopeLatestLocation;
    }

    // ===================================================================================
    //                                                                         Like Search
    //                                                                         ===========
    protected String _likeSearchLatestLocation;
    protected transient VaryingValueHandler _likeSearchValueHandler;

    protected VaryingValueHandler getLikeSearchValueHandler() {
        if (_likeSearchValueHandler == null) {
            _likeSearchValueHandler = new VaryingValueHandler(ConditionKey.CK_LIKE_SEARCH);
        }
        return _likeSearchValueHandler;
    }

    /**
     * Set up the value of likeSearch.
     * @param value The value of likeSearch. (NullAllowed)
     * @param option The option of likeSearch. (NotNull)
     * @param location The base location of likeSearch. (NotNull)
     */
    public void setupLikeSearch(String value, final LikeSearchOption option, String location) {
        final String key = getLikeSearchValueHandler().setValue(option.generateRealValue(value));
        _likeSearchLatestLocation = location + "." + key;
    }

    /**
     * Get the latest location of likeSearch.
     * @return The latest location of likeSearch. (NullAllowed)
     */
    public String getLikeSearchLatestLocation() {
        return _likeSearchLatestLocation;
    }

    // ===================================================================================
    //                                                                     Not Like Search
    //                                                                     ===============
    protected String _notLikeSearchLatestLocation;
    protected transient VaryingValueHandler _notLikeSearchValueHandler;

    protected VaryingValueHandler getNotLikeSearchValueHandler() {
        if (_notLikeSearchValueHandler == null) {
            _notLikeSearchValueHandler = new VaryingValueHandler(ConditionKey.CK_NOT_LIKE_SEARCH);
        }
        return _notLikeSearchValueHandler;
    }

    /**
     * Set up the value of notLikeSearch.
     * @param value The value of notLikeSearch. (NullAllowed)
     * @param option The option of notLikeSearch. (NotNull)
     * @param location The base location of notLikeSearch. (NotNull)
     */
    public void setupNotLikeSearch(String value, final LikeSearchOption option, String location) {
        final String key = getNotLikeSearchValueHandler().setValue(option.generateRealValue(value));
        _notLikeSearchLatestLocation = location + "." + key;
    }

    /**
     * Get the latest location of notLikeSearch.
     * @return The latest location of notLikeSearch. (NullAllowed)
     */
    public String getNotLikeSearchLatestLocation() {
        return _notLikeSearchLatestLocation;
    }

    // ===================================================================================
    //                                                                         Is Not Null
    //                                                                         ===========
    protected transient ValueHandler _isNotNullValueHandler;

    protected ValueHandler getIsNotNullValueHandler() {
        if (_isNotNullValueHandler == null) {
            _isNotNullValueHandler = new StandardValueHandler(ConditionKey.CK_IS_NOT_NULL);
        }
        return _isNotNullValueHandler;
    }

    /**
     * Set the value of isNotNull.
     * @param value The value of isNotNull. (NullAllowed)
     * @return The key of value. (NotNull)
     */
    public String setIsNotNull(Object value) {
        return getIsNotNullValueHandler().setValue(value);
    }

    /**
     * Does it has the value of isNotNull?
     * @return Determination. (NotNull)
     */
    public boolean hasIsNotNull() {
        return getIsNotNullValueHandler().hasValue();
    }

    // ===================================================================================
    //                                                                             Is Null
    //                                                                             =======
    protected transient ValueHandler _isNullValueHandler;

    protected ValueHandler getIsNullValueHandler() {
        if (_isNullValueHandler == null) {
            _isNullValueHandler = new StandardValueHandler(ConditionKey.CK_IS_NULL);
        }
        return _isNullValueHandler;
    }

    /**
     * Set the value of isNull.
     * @param value The value of isNull. (NullAllowed)
     * @return The key of value. (NotNull)
     */
    public String setIsNull(Object value) {
        return getIsNullValueHandler().setValue(value);
    }

    /**
     * Does it has the value of isNull?
     * @return Determination. (NotNull)
     */
    public boolean hasIsNull() {
        return getIsNullValueHandler().hasValue();
    }

    // ===================================================================================
    //                                                                      Value Handling
    //                                                                      ==============
    // -----------------------------------------------------
    //                                                 Fixed
    //                                                 -----
    protected Object getFixedValue(ConditionKey conditionKey) {
        if (!hasFixedValue(conditionKey)) {
            return null;
        }
        return _fixedValueMap.get(getFixedValueKey()).get(conditionKey.getConditionKey());
    }

    protected String setupFixedValue(ConditionKey conditionKey, Object value) {
        if (_fixedValueMap == null) {
            // query or in-line or on-clause
            _fixedValueMap = new HashMap<String, Map<String, Object>>(3);
        }
        final String fixedValueKey = getFixedValueKey();
        Map<String, Object> elementMap = _fixedValueMap.get(fixedValueKey);
        if (elementMap == null) {
            elementMap = new HashMap<String, Object>(8);
            _fixedValueMap.put(fixedValueKey, elementMap);
        }
        final String key = conditionKey.getConditionKey();
        elementMap.put(key, value);
        return "fixed." + fixedValueKey + "." + key;
    }

    protected String getFixedValueKey() {
        if (_inline) {
            if (_onClause) {
                return FIXED_KEY_ONCLAUSE;
            } else {
                return FIXED_KEY_INLINE;
            }
        } else { // normal query
            return FIXED_KEY_QUERY;
        }
    }

    protected boolean hasFixedValue(ConditionKey conditionKey) {
        if (_fixedValueMap == null) {
            return false;
        }
        final Map<String, Object> elementMap = _fixedValueMap.get(getFixedValueKey());
        if (elementMap == null) {
            return false;
        }
        return elementMap.containsKey(conditionKey.getConditionKey());
    }

    // -----------------------------------------------------
    //                                               Varying
    //                                               -------
    protected Object getVaryingValue(ConditionKey conditionKey) {
        throw new IllegalStateException();
    }

    protected String setupVaryingValue(ConditionKey conditionKey, Object value) {
        if (_varyingValueMap == null) {
            _varyingValueMap = new HashMap<String, Map<String, Object>>(4);
        }
        final String key = conditionKey.getConditionKey();
        Map<String, Object> elementMap = _varyingValueMap.get(key);
        if (elementMap == null) {
            elementMap = new LinkedHashMap<String, Object>();
            _varyingValueMap.put(key, elementMap);
        }
        final String elementKey = key + elementMap.size();
        elementMap.put(elementKey, value);
        return "varying." + key + "." + elementKey;
    }

    protected boolean hasVaryingValue(ConditionKey conditionKey) {
        throw new IllegalStateException();
    }

    // -----------------------------------------------------
    //                                               Handler
    //                                               -------
    protected static interface ValueHandler {
        Object getValue();

        String setValue(Object value);

        boolean hasValue();

        boolean equalValue(Object value);

        void overrideValue(Object value);
    }

    protected class StandardValueHandler implements ValueHandler {
        protected final ConditionKey _conditionKey;

        public StandardValueHandler(ConditionKey conditionKey) {
            _conditionKey = conditionKey;
        }

        public Object getValue() {
            return getStandardValue(_conditionKey);
        }

        public String setValue(Object value) {
            return setupStandardValue(_conditionKey, value);
        }

        public boolean hasValue() {
            return hasStandardValue(_conditionKey);
        }

        public boolean equalValue(Object value) {
            return hasValue() ? getValue().equals(value) : value == null;
        }

        public void overrideValue(Object value) {
            setValue(value);
        }

        protected Object getStandardValue(ConditionKey conditionKey) {
            return _orScopeQuery ? getVaryingValue(conditionKey) : getFixedValue(conditionKey);
        }

        protected String setupStandardValue(ConditionKey conditionKey, Object value) {
            if (_orScopeQuery) {
                return setupVaryingValue(conditionKey, value);
            } else {
                return setupFixedValue(conditionKey, value);
            }
        }

        protected boolean hasStandardValue(ConditionKey conditionKey) {
            return _orScopeQuery ? hasVaryingValue(conditionKey) : hasFixedValue(conditionKey);
        }
    }

    protected class VaryingValueHandler implements ValueHandler {
        protected final ConditionKey _conditionKey;

        public VaryingValueHandler(ConditionKey conditionKey) {
            _conditionKey = conditionKey;
        }

        public Object getValue() {
            return getVaryingValue(_conditionKey);
        }

        public String setValue(Object value) {
            return setupVaryingValue(_conditionKey, value);
        }

        public boolean hasValue() {
            return hasVaryingValue(_conditionKey);
        }

        public boolean equalValue(Object value) {
            return hasValue() ? getValue().equals(value) : value == null;
        }

        public void overrideValue(Object value) {
            setValue(value);
        }
    }

    // ===================================================================================
    //                                                                             Process
    //                                                                             =======
    public static interface CallbackProcessor<RESULT> {
        RESULT process();

        QueryModeProvider getProvider();
    }

    public static interface QueryModeProvider {
        boolean isOrScopeQuery(); // completely independent

        boolean isInline();

        boolean isOnClause(); // can be true, when in-line is true
    }

    public <RESULT> RESULT process(CallbackProcessor<RESULT> processor) {
        try {
            final QueryModeProvider provider = processor.getProvider();
            _orScopeQuery = provider.isOrScopeQuery();
            _inline = provider.isInline();
            _onClause = provider.isOnClause();
            return processor.process();
        } finally {
            _orScopeQuery = false;
            _inline = false;
            _onClause = false;
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String title = DfTypeUtil.toClassTitle(this);
        final StringBuilder sb = new StringBuilder();
        sb.append(title).append(":{");
        sb.append("fixed=").append(_fixedValueMap);
        sb.append(", varying=").append(_varyingValueMap);
        sb.append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isFixedQuery() {
        // only or-scope query is NOT fixed
        return !_orScopeQuery;
    }

    /**
     * Get the map of fixed values. {basically for parameter-comment} <br />
     * @return The map of fixed values. map:{[query or inline or onClause] = map:{[condition-key] = [value]}} (NullAllowed)
     */
    public Map<String, Map<String, Object>> getFixed() {
        return _fixedValueMap;
    }

    /**
     * Get the map of fixed values for query. {basically for internal tests} <br />
     * @return A map instance. map:{[condition-key] = [value]} (NullAllowed)
     */
    public Map<String, Object> getFixedQuery() {
        return _fixedValueMap != null ? _fixedValueMap.get(FIXED_KEY_QUERY) : null;
    }

    /**
     * Get the map of fixed values for in-line. {basically for internal tests} <br />
     * @return A map instance. map:{[condition-key] = [value]} (NullAllowed)
     */
    public Map<String, Object> getFixedInline() {
        return _fixedValueMap != null ? _fixedValueMap.get(FIXED_KEY_INLINE) : null;
    }

    /**
     * Get the map of fixed values for on-clause. {basically for internal tests} <br />
     * @return A map instance. map:{[condition-key] = [value]} (NullAllowed)
     */
    public Map<String, Object> getFixedOnClause() {
        return _fixedValueMap != null ? _fixedValueMap.get(FIXED_KEY_ONCLAUSE) : null;
    }

    /**
     * Get the map of varying values. {basically for parameter-comment} <br />
     * @return The map of varying values. map:{[condition-key] = map:{[varying-key] = [value]}} (NullAllowed)
     */
    public Map<String, Map<String, Object>> getVarying() {
        return _varyingValueMap;
    }
}
