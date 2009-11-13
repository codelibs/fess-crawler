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
package org.seasar.robot.dbflute.cbean.cvalue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The value of condition.
 * @author jflute
 */
public class ConditionValue {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _utilDateToSqlDate;

    // ===================================================================================
    //                                                                               Equal
    //                                                                               =====
    /** Value of equal. */
    protected Object _equalValue;

    /**
     * Get the value of equal.
     * @return The value of equal. (Nullable)
     */
    public Object getEqual() {
        return filterValue(_equalValue);
    }

    /**
     * Set the value of equal.
     * @param value The value of equal. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setEqual(Object value) {
        _equalValue = value;
        return this;
    }

    /**
     * Does it has the value of equal?
     * @return Determination. (NotNull)
     */
    public boolean hasEqual() {
        return _equalValue != null;
    }

    /**
     * Does the value equal the value of equal?
     * @param value The value of equal. (Nullable)
     * @return Determination. (NotNull)
     */
    public boolean equalEqual(Object value) {
        return hasEqual() ? _equalValue.equals(value) : value == null;
    }

    /**
     * Override the value of equal.
     * @param value The value of equal. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue overrideEqual(Object value) {
        _equalValue = value;
        return this;
    }

    /** Location of equal. */
    protected String _equalLocation;

    /**
     * Get the location of equal.
     * @return The location of equal. (Nullable)
     */
    public String getEqualLocation() {
        return _equalLocation;
    }

    /**
     * Set the location of equal.
     * @param location The location of equal. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setEqualLocation(String location) {
        _equalLocation = location;
        return this;
    }

    // ===================================================================================
    //                                                                           Not Equal
    //                                                                           =========
    /** Value of notEqual. */
    protected Object _notEqualValue;

    /**
     * Get the value of notEqual.
     * @return The value of notEqual. (Nullable)
     */
    public Object getNotEqual() {
        return filterValue(_notEqualValue);
    }

    /**
     * Set the value of notEqual.
     * @param value The value of notEqual. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setNotEqual(Object value) {
        _notEqualValue = value;
        return this;
    }

    /**
     * Does it has the value of notEqual?
     * @return Determination. (NotNull)
     */
    public boolean hasNotEqual() {
        return _notEqualValue != null;
    }

    /**
     * Does the value equal the value of notEqual?
     * @param value The value of notEqual. (Nullable)
     * @return Determination. (NotNull)
     */
    public boolean equalNotEqual(Object value) {
        return hasNotEqual() ? _notEqualValue.equals(value) : value == null;
    }

    /**
     * Override the value of notEqual.
     * @param value The value of notEqual. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue overrideNotEqual(Object value) {
        _notEqualValue = value;
        return this;
    }

    /** Location of notEqual. */
    protected String _notEqualLocation;

    /**
     * Get the location of notEqual.
     * @return The location of notEqual. (Nullable)
     */
    public String getNotEqualLocation() {
        return _notEqualLocation;
    }

    /**
     * Set the location of notEqual.
     * 
     * @param location The location of notEqual. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setNotEqualLocation(String location) {
        _notEqualLocation = location;
        return this;
    }

    // ===================================================================================
    //                                                                        Greater Than
    //                                                                        ============
    /** Value of greaterThan. */
    protected Object _greaterThanValue;

    /**
     * Get the value of greaterThan.
     * @return The value of greaterThan. (Nullable)
     */
    public Object getGreaterThan() {
        return filterValue(_greaterThanValue);
    }

    /**
     * Set the value of greaterThan.
     * @param value The value of greaterThan. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setGreaterThan(Object value) {
        _greaterThanValue = value;
        return this;
    }

    /**
     * Does it has the value of greaterThan?
     * @return Determination. (NotNull)
     */
    public boolean hasGreaterThan() {
        return _greaterThanValue != null;
    }

    /**
     * Does the value equal the value of greaterThan?
     * @param value The value of greaterThan. (Nullable)
     * @return Determination. (NotNull)
     */
    public boolean equalGreaterThan(Object value) {
        return hasGreaterThan() ? _greaterThanValue.equals(value) : value == null;
    }

    /**
     * Override the value of greaterThan.
     * @param value The value of greaterThan. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue overrideGreaterThan(Object value) {
        _greaterThanValue = value;
        return this;
    }

    /** Location of GreaterThan. */
    protected String _greaterThanLocation;

    /**
     * Get the location of greaterThan.
     * @return The location of greaterThan. (Nullable)
     */
    public String getGreaterThanLocation() {
        return _greaterThanLocation;
    }

    /**
     * Set the location of greaterThan.
     * @param location The location of greaterThan. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setGreaterThanLocation(String location) {
        _greaterThanLocation = location;
        return this;
    }

    // ===================================================================================
    //                                                                           Less Than
    //                                                                           =========
    /** Value of lessThan. */
    protected Object _lessThanValue;

    /**
     * Get the value of lessThan.
     * @return The value of lessThan. (Nullable)
     */
    public Object getLessThan() {
        return filterValue(_lessThanValue);
    }

    /**
     * Set the value of lessThan.
     * @param value The value of lessThan. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setLessThan(Object value) {
        _lessThanValue = value;
        return this;
    }

    /**
     * Does it has the value of lessThan?
     * @return Determination. (NotNull)
     */
    public boolean hasLessThan() {
        return _lessThanValue != null;
    }

    /**
     * Does the value equal the value of lessThan?
     * @param value The value of lessThan. (Nullable)
     * @return Determination. (NotNull)
     */
    public boolean equalLessThan(Object value) {
        return hasLessThan() ? _lessThanValue.equals(value) : value == null;
    }

    /**
     * Override the value of lessThan.
     * @param value The value of lessThan. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue overrideLessThan(Object value) {
        _lessThanValue = value;
        return this;
    }

    /** Location of lessThan. */
    protected String _lessThanLocation;

    /**
     * Get the location of lessThan.
     * @return The location of lessThan. (Nullable)
     */
    public String getLessThanLocation() {
        return _lessThanLocation;
    }

    /**
     * Set the location of lessThan.
     * @param location The location of lessThan. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setLessThanLocation(String location) {
        _lessThanLocation = location;
        return this;
    }

    // ===================================================================================
    //                                                                       Greater Equal
    //                                                                       =============
    /** Value of greaterEqual. */
    protected Object _greaterEqualValue;

    /**
     * Get the value of greaterEqual.
     * @return The value of greaterEqual. (Nullable)
     */
    public Object getGreaterEqual() {
        return filterValue(_greaterEqualValue);
    }

    /**
     * Set the value of greaterEqual.
     * @param value The value of greaterEqual. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setGreaterEqual(Object value) {
        _greaterEqualValue = value;
        return this;
    }

    /**
     * Does it has the value of greaterEqual?
     * @return Determination. (NotNull)
     */
    public boolean hasGreaterEqual() {
        return _greaterEqualValue != null;
    }

    /**
     * Does the value equal the value of greaterEqual?
     * @param value The value of greaterEqual. (Nullable)
     * @return Determination. (NotNull)
     */
    public boolean equalGreaterEqual(Object value) {
        return hasGreaterEqual() ? _greaterEqualValue.equals(value) : value == null;
    }

    /**
     * Override the value of greaterEqual.
     * @param value The value of greaterEqual. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue overrideGreaterEqual(Object value) {
        _greaterEqualValue = value;
        return this;
    }

    /** Location of greaterEqual. */
    protected String _greaterEqualLocation;

    /**
     * Get the location of greaterEqual.
     * @return The location of greaterEqual. (Nullable)
     */
    public String getGreaterEqualLocation() {
        return _greaterEqualLocation;
    }

    /**
     * Set the location of greaterEqual.
     * @param location The location of greaterEqual. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setGreaterEqualLocation(String location) {
        _greaterEqualLocation = location;
        return this;
    }

    // ===================================================================================
    //                                                                          Less Equal
    //                                                                          ==========
    /** Value of lessEqual. */
    protected Object _lessEqualValue;

    /**
     * Get the value of lessEqual.
     * @return The value of lessEqual. (Nullable)
     */
    public Object getLessEqual() {
        return filterValue(_lessEqualValue);
    }

    /**
     * Set the value of lessEqual.
     * @param value The value of lessEqual. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setLessEqual(Object value) {
        _lessEqualValue = value;
        return this;
    }

    /**
     * Does it has the value of lessEqual?
     * @return Determination. (NotNull)
     */
    public boolean hasLessEqual() {
        return _lessEqualValue != null;
    }

    /**
     * Does the value equal the value of lessEqual?
     * @param value The value of lessEqual. (Nullable)
     * @return Determination. (NotNull)
     */
    public boolean equalLessEqual(Object value) {
        return hasLessEqual() ? _lessEqualValue.equals(value) : value == null;
    }

    /**
     * Override the value of lessEqual.
     * @param value The value of lessEqual. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue overrideLessEqual(Object value) {
        _lessEqualValue = value;
        return this;
    }

    /** Location of lessEqual. */
    protected String _lessEqualLocation;

    /**
     * Get the location of lessEqual.
     * @return The location of lessEqual. (Nullable)
     */
    public String getLessEqualLocation() {
        return _lessEqualLocation;
    }

    /**
     * Set the location of lessEqual.
     * @param location The location of lessEqual. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setLessEqualLocation(String location) {
        _lessEqualLocation = location;
        return this;
    }

    // ===================================================================================
    //                                                                            In Scope
    //                                                                            ========
    /** The value of inScope. */
    protected List<List<?>> _inScope;

    /** The value of inScope for spare. */
    protected List<List<?>> _inScope4Spare;

    /**
     * Get the value of inScope.
     * @return The value of inScope. (Nullable)
     */
    public List<?> getInScope() {
        if (_inScope == null) {
            return null;
        }
        if (_inScope.isEmpty() && !_inScope4Spare.isEmpty()) {
            for (int index = 0; index < _inScope4Spare.size(); index++) {
                _inScope.add(_inScope4Spare.get(index));
            }
        }
        final List<?> inScopeValue = _inScope.remove(0);
        return filterValue(inScopeValue);
    }

    /**
     * Set the value of inScope.
     * @param value The value of inScope. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setInScope(List<?> value) {
        if (_inScope == null) {
            _inScope = new ArrayList<List<?>>();
            _inScope4Spare = new ArrayList<List<?>>();
        }
        if (_inScope.isEmpty() && !_inScope4Spare.isEmpty()) {
            for (int index = 0; index < _inScope4Spare.size(); index++) {
                _inScope.add(_inScope4Spare.get(index));
            }
        }
        _inScope.add(value);
        _inScope4Spare.add(value);
        return this;
    }

    /** Location of InScope. */
    protected String _inScopeLocation;

    /**
     * Get the location of inScope.
     * @return The location of inScope. (Nullable)
     */
    public String getInScopeLocation() {
        return _inScopeLocation;
    }

    /**
     * Set the location of inScope.
     * @param location The location of inScope. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setInScopeLocation(String location) {
        _inScopeLocation = location;
        return this;
    }

    // ===================================================================================
    //                                                                        Not In Scope
    //                                                                        ============
    /** The value of notInScope. */
    protected List<List<?>> _notInScope;

    /** The value of notInScope for spare. */
    protected List<List<?>> _notInScope4Spare;

    /**
     * Get the value of notInScope.
     * @return The value of notInScope. (Nullable)
     */
    public List<?> getNotInScope() {
        if (_notInScope == null) {
            return null;
        }
        if (_notInScope.isEmpty() && !_notInScope4Spare.isEmpty()) {
            for (int index = 0; index < _notInScope4Spare.size(); index++) {
                _notInScope.add(_notInScope4Spare.get(index));
            }
        }
        final List<?> notInScopeValue = _notInScope.remove(0);
        return filterValue(notInScopeValue);
    }

    /**
     * Set the value of notInScope.
     * @param value The value of notInScope. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setNotInScope(List<?> value) {
        if (_notInScope == null) {
            _notInScope = new ArrayList<List<?>>();
            _notInScope4Spare = new ArrayList<List<?>>();
        }
        if (_notInScope.isEmpty() && !_notInScope4Spare.isEmpty()) {
            for (int index = 0; index < _notInScope4Spare.size(); index++) {
                _notInScope.add(_notInScope4Spare.get(index));
            }
        }
        _notInScope.add(value);
        _notInScope4Spare.add(value);
        return this;
    }

    /** Location of InScope. */
    protected String _notInScopeLocation;

    /**
     * Get the location of notInScope.
     * @return The location of notInScope. (Nullable)
     */
    public String getNotInScopeLocation() {
        return _notInScopeLocation;
    }

    /**
     * Set the location of notInScope.
     * @param location The location of notInScope. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setNotInScopeLocation(String location) {
        _notInScopeLocation = location;
        return this;
    }

    // ===================================================================================
    //                                                                         Like Search
    //                                                                         ===========
    /** The value of likeSearch. */
    protected List<LikeSearchValue> _likeSearch;

    /** The value of likeSearch for spare. */
    protected List<LikeSearchValue> _likeSearch4Spare;

    /**
     * Get the value of likeSearch.
     * @return The value of likeSearch. (Nullable)
     */
    public String getLikeSearch() {
        if (_likeSearch == null) {
            return null;
        }
        if (_likeSearch.isEmpty() && !_likeSearch4Spare.isEmpty()) {
            for (int index = 0; index < _likeSearch4Spare.size(); index++) {
                _likeSearch.add(_likeSearch4Spare.get(index));
            }
        }
        final LikeSearchValue likeSearchValue = _likeSearch.remove(0);
        return (String) filterValue(likeSearchValue.generateRealValue());
    }

    /**
     * Set the value of likeSearch.
     * @param value The value of likeSearch. (Nullable)
     * @param option The option of likeSearch. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setLikeSearch(String value, LikeSearchOption option) {
        if (_likeSearch == null) {
            _likeSearch = new ArrayList<LikeSearchValue>();
            _likeSearch4Spare = new ArrayList<LikeSearchValue>();
        }
        if (_likeSearch.isEmpty() && !_likeSearch4Spare.isEmpty()) {
            for (int index = 0; index < _likeSearch4Spare.size(); index++) {
                _likeSearch.add(_likeSearch4Spare.get(index));
            }
        }
        LikeSearchValue likeSearchValue = new LikeSearchValue(value, option);
        _likeSearch.add(likeSearchValue);
        _likeSearch4Spare.add(likeSearchValue);
        return this;
    }

    /** Location of likeSearch. */
    protected String _likeSearchLocation;

    /**
     * Get the location of likeSearch.
     * @return The location of likeSearch. (Nullable)
     */
    public String getLikeSearchLocation() {
        return _likeSearchLocation;
    }

    /**
     * Set the location of likeSearch.
     * @param location The location of likeSearch. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setLikeSearchLocation(String location) {
        _likeSearchLocation = location;
        return this;
    }

    protected static class LikeSearchValue {
        protected String _value;
        protected LikeSearchOption _option;

        public LikeSearchValue(String value, LikeSearchOption option) {
            _value = value;
            _option = option;
        }

        public String getValue() {
            return _value;
        }

        public LikeSearchOption getOption() {
            return _option;
        }

        public String generateRealValue() {
            if (_option == null) {
                return _value;
            }
            return _option.generateRealValue(_value);
        }
    }

    // ===================================================================================
    //                                                                     Not Like Search
    //                                                                     ===============
    /** The value of notLikeSearch. */
    protected List<NotLikeSearchValue> _notLikeSearch;

    /** The value of notLikeSearch for spare. */
    protected List<NotLikeSearchValue> _notLikeSearch4Spare;

    /**
     * Get the value of notLikeSearch.
     * @return The value of notLikeSearch. (Nullable)
     */
    public String getNotLikeSearch() {
        if (_notLikeSearch == null) {
            return null;
        }
        if (_notLikeSearch.isEmpty() && !_notLikeSearch4Spare.isEmpty()) {
            for (int index = 0; index < _notLikeSearch4Spare.size(); index++) {
                _notLikeSearch.add(_notLikeSearch4Spare.get(index));
            }
        }
        final NotLikeSearchValue notLikeSearchValue = _notLikeSearch.remove(0);
        return (String) filterValue(notLikeSearchValue.generateRealValue());
    }

    /**
     * Set the value of notLikeSearch.
     * @param value The value of notLikeSearch. (Nullable)
     * @param option The option of notLikeSearch. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setNotLikeSearch(String value, LikeSearchOption option) {
        if (_notLikeSearch == null) {
            _notLikeSearch = new ArrayList<NotLikeSearchValue>();
            _notLikeSearch4Spare = new ArrayList<NotLikeSearchValue>();
        }
        if (_notLikeSearch.isEmpty() && !_notLikeSearch4Spare.isEmpty()) {
            for (int index = 0; index < _notLikeSearch4Spare.size(); index++) {
                _notLikeSearch.add(_notLikeSearch4Spare.get(index));
            }
        }
        NotLikeSearchValue notLikeSearchValue = new NotLikeSearchValue(value, option);
        _notLikeSearch.add(notLikeSearchValue);
        _notLikeSearch4Spare.add(notLikeSearchValue);
        return this;
    }

    /** Location of notLikeSearch. */
    protected String _notLikeSearchLocation;

    /**
     * Get the location of notLikeSearch.
     * @return The location of notLikeSearch. (Nullable)
     */
    public String getNotLikeSearchLocation() {
        return _notLikeSearchLocation;
    }

    /**
     * Set the location of notLikeSearch.
     * @param location The location of notLikeSearch. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setNotLikeSearchLocation(String location) {
        _notLikeSearchLocation = location;
        return this;
    }

    protected static class NotLikeSearchValue {
        protected String _value;
        protected LikeSearchOption _option;

        public NotLikeSearchValue(String value, LikeSearchOption option) {
            _value = value;
            _option = option;
        }

        public String getValue() {
            return _value;
        }

        public LikeSearchOption getOption() {
            return _option;
        }

        public String generateRealValue() {
            if (_option == null) {
                return _value;
            }
            return _option.generateRealValue(_value);
        }
    }

    // ===================================================================================
    //                                                                             Is Null
    //                                                                             =======
    /** Value of isNull. */
    protected Object _isNullValue;

    /**
     * Get the value of isNull.
     * @return The value of isNull. (Nullable)
     */
    public Object getIsNull() {
        return _isNullValue;
    }

    /**
     * Set the value of isNull.
     * @param value The value of isNull. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setIsNull(Object value) {
        _isNullValue = value;
        return this;
    }

    /**
     * Does it has the value of isNull?
     * @return Determination. (NotNull)
     */
    public boolean hasIsNull() {
        return _isNullValue != null;
    }

    /** Location of isNull. */
    protected String _isNullLocation;

    /**
     * Get the location of isNull.
     * @return The location of isNull. (Nullable)
     */
    public String getIsNullLocation() {
        return _isNullLocation;
    }

    /**
     * Set the location of isNull.
     * @param location The location of isNull. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setIsNullLocation(String location) {
        _isNullLocation = location;
        return this;
    }

    // ===================================================================================
    //                                                                         Is Not Null
    //                                                                         ===========
    /** Value of isNotNull. */
    protected Object _isNotNullValue;

    /**
     * Get the value of isNotNull.
     * @return The value of isNotNull. (Nullable)
     */
    public Object getIsNotNull() {
        return _isNotNullValue;
    }

    /**
     * Set the value of isNotNull.
     * @param value The value of isNotNull. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setIsNotNull(Object value) {
        _isNotNullValue = value;
        return this;
    }

    /**
     * Does it has the value of isNotNull?
     * @return Determination. (NotNull)
     */
    public boolean hasIsNotNull() {
        return _isNotNullValue != null;
    }

    /** Location of isNotNull. */
    protected String _isNotNullLocation;

    /**
     * Get the location of isNotNull.
     * @return The location of isNotNull. (Nullable)
     */
    public String getIsNotNullLocation() {
        return _isNotNullLocation;
    }

    /**
     * Set the location of isNotNull.
     * @param location The location of isNotNull. (Nullable)
     * @return this. (NotNull)
     */
    public ConditionValue setIsNotNullLocation(String location) {
        _isNotNullLocation = location;
        return this;
    }

    // =====================================================================================
    //                                                                                Option
    //                                                                                ======
    public ConditionValue enableUtilDateToSqlDate() {
        _utilDateToSqlDate = true;
        return this;
    }

    // =====================================================================================
    //                                                                                Filter
    //                                                                                ======
    /**
     * Filter value.
     * If the value is instance of java.util.Date or java.util.Calendar, returns value as java.sql.Date.
     * @param value The value. (Nullable)
     * @return The filtered value. (Nullable)
     */
    protected Object filterValue(Object value) {
        if (value == null) {
            return value;
        }
        if (value instanceof java.sql.Time) {
            return value;
        }
        if (value instanceof java.sql.Timestamp) {
            return value;
        }
        if (value instanceof java.util.Date) {
            if (_utilDateToSqlDate) {
                return DfTypeUtil.toSqlDate(value);
            } else {
                return value;
            }
        }
        if (value instanceof java.util.Calendar) {
            return DfTypeUtil.toTimestamp(value);
        }
        return value;
    }

    /**
     * Filter the list of value.
     * If the value is instance of java.util.Date or java.util.Calendar, returns value as java.sql.Date.
     * @param valueList The list of value. (Nullable)
     * @return The filtered list of value. (Nullable)
     */
    protected List<?> filterValue(List<?> valueList) {
        if (valueList == null || valueList.isEmpty()) {
            return valueList;
        }
        final List<Object> resultList = new ArrayList<Object>();
        for (Iterator<?> ite = valueList.iterator(); ite.hasNext();) {
            Object value = ite.next();
            resultList.add(filterValue(value));
        }
        return resultList;
    }
}
