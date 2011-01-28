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
package org.seasar.robot.dbflute.helper;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jflute
 * @param <VALUE> The type of value.
 */
public class StringKeyMap<VALUE> implements Map<String, VALUE>, Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final Map<String, VALUE> _searchMap;

    // these maps always have same size of search map if it's valid
    protected final Map<String, VALUE> _plainMap; // invalid if concurrent
    protected final Map<String, String> _searchPlainKeyMap; // same life-cycle as plainMap

    protected boolean _flexible;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected StringKeyMap(boolean flexible, boolean order, boolean concurrent) {
        if (order && concurrent) {
            String msg = "The 'order' and 'concurrent' should not be both true at the same time!";
            throw new IllegalStateException(msg);
        }
        _flexible = flexible;
        if (concurrent) {
            _searchMap = newConcurrentHashMap();
            _plainMap = null; // invalid if concurrent
            _searchPlainKeyMap = null; // same life-cycle as plainMap
        } else {
            if (order) {
                _searchMap = newLinkedHashMap();
                _plainMap = newLinkedHashMap();
            } else {
                _searchMap = newHashMap();
                _plainMap = newHashMap();
            }
            _searchPlainKeyMap = newHashMap();
        }
    }

    /**
     * Create The map of string key as case insensitive. <br />
     * You can set null key and value. And plain keys to be set is kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as case insensitive. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsCaseInsensitive() {
        return new StringKeyMap<VALUE>(false, false, false);
    }

    /**
     * Create The map of string key as case insensitive and concurrent. <br />
     * You cannot set null key and value. And plain keys to be set is NOT kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as case insensitive and concurrent. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsCaseInsensitiveConcurrent() {
        return new StringKeyMap<VALUE>(false, false, true);
    }

    /**
     * Create The map of string key as case insensitive and ordered. <br />
     * You can set null key and value. And plain keys to be set is kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as case insensitive and ordered. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsCaseInsensitiveOrdered() {
        return new StringKeyMap<VALUE>(false, true, false);
    }

    /**
     * Create The map of string key as flexible. <br />
     * You can set null key and value. And plain keys to be set is kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as flexible. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsFlexible() {
        return new StringKeyMap<VALUE>(true, false, false);
    }

    /**
     * Create The map of string key as flexible and concurrent. <br />
     * You cannot set null key and value. And plain keys to be set is NOT kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as flexible and concurrent. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsFlexibleConcurrent() {
        return new StringKeyMap<VALUE>(true, false, true);
    }

    /**
     * Create The map of string key as flexible and ordered. <br />
     * You can set null key and value. And plain keys to be set is kept.
     * @param <VALUE> The type of value.
     * @return The map of string key as flexible and ordered. (NotNull)
     */
    public static <VALUE> StringKeyMap<VALUE> createAsFlexibleOrdered() {
        return new StringKeyMap<VALUE>(true, true, false);
    }

    // ===================================================================================
    //                                                                        Map Emulator
    //                                                                        ============
    // -----------------------------------------------------
    //                                           Key Related
    //                                           -----------
    public VALUE get(Object key) {
        final String stringKey = convertStringKey(key);
        return _searchMap.get(stringKey);
    }

    public VALUE put(String key, VALUE value) {
        final String stringKey = convertStringKey(key);
        if (_plainMap != null) { // non thread safe
            final String plainKey = generatePlainKey(key, stringKey);
            _plainMap.put(plainKey, value);
            _searchPlainKeyMap.put(stringKey, plainKey);
        }
        return _searchMap.put(stringKey, value);
    }

    public VALUE remove(Object key) {
        final String stringKey = convertStringKey(key);
        if (_plainMap != null) { // non thread safe
            final String plainKey = generatePlainKey(key, stringKey);
            _plainMap.remove(plainKey);
            _searchPlainKeyMap.remove(stringKey);
        }
        return _searchMap.remove(stringKey);
    }

    protected String generatePlainKey(Object key, String stringKey) {
        final String plainKey = _searchPlainKeyMap.get(stringKey);
        return (plainKey != null ? plainKey : (key != null ? key.toString() : null));
    }

    public final void putAll(Map<? extends String, ? extends VALUE> map) {
        final Set<?> entrySet = map.entrySet();
        for (Object entryObj : entrySet) {
            @SuppressWarnings("unchecked")
            final Entry<String, VALUE> entry = (Entry<String, VALUE>) entryObj;
            put(entry.getKey(), entry.getValue());
        }
    }

    public boolean containsKey(Object key) {
        final String stringKey = convertStringKey(key);
        return _searchMap.containsKey(stringKey);
    }

    // -----------------------------------------------------
    //                                              Delegate
    //                                              --------
    public void clear() {
        if (_plainMap != null) {
            _plainMap.clear();
            _searchPlainKeyMap.clear();
        }
        _searchMap.clear();
    }

    public int size() {
        return _searchMap.size();
    }

    public boolean isEmpty() {
        return _searchMap.isEmpty();
    }

    public Set<String> keySet() {
        if (_plainMap != null) {
            return _plainMap.keySet();
        }
        return _searchMap.keySet();
    }

    public Collection<VALUE> values() {
        return _searchMap.values();
    }

    public boolean containsValue(Object obj) {
        return _searchMap.containsValue(obj);
    }

    public Set<Entry<String, VALUE>> entrySet() {
        if (_plainMap != null) {
            return _plainMap.entrySet();
        }
        return _searchMap.entrySet();
    }

    // ===================================================================================
    //                                                                    Original Utility
    //                                                                    ================
    public boolean equalsUnderCharOption(StringKeyMap<VALUE> map) { // ignores ordered
        if (map == null) {
            return false;
        }
        if (size() != map.size()) {
            return false;
        }
        final Set<Entry<String, VALUE>> entrySet = map.entrySet();
        for (Entry<String, VALUE> entry : entrySet) {
            if (!containsKey(entry.getKey())) {
                return false;
            }
            final VALUE value = get(entry.getKey());
            if (value != null) {
                if (!value.equals(entry.getValue())) {
                    return false;
                }
            } else {
                if (entry.getValue() != null) {
                    return false;
                }
            }
        }
        return true;
    }

    // ===================================================================================
    //                                                                       Key Converter
    //                                                                       =============
    protected String convertStringKey(Object key) {
        if (!(key instanceof String)) {
            return null;
        }
        return toLowerCase(removeConnector((String) key));
    }

    protected String removeConnector(String value) {
        if (value == null) {
            return null;
        }
        if (_flexible) {
            // both side quotations
            if (isSingleQuoted(value)) {
                value = unquoteSingle(value);
            } else if (isDoubleQuoted(value)) {
                value = unquoteDouble(value);
            }

            // a main target mark when flexible
            value = replace(value, "_", "");

            // non-compilable marks in Java
            value = replace(value, "-", "");
            value = replace(value, " ", "");
        }
        return value;
    }

    protected String toLowerCase(String value) {
        return value.toLowerCase();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    // copied from DBFlute's utilities for independence of this class
    protected static String replace(String str, String fromStr, String toStr) {
        StringBuilder sb = null; // lazy load
        int pos = 0;
        int pos2 = 0;
        do {
            pos = str.indexOf(fromStr, pos2);
            if (pos2 == 0 && pos < 0) { // first loop and not found
                return str; // without creating StringBuilder 
            }
            if (sb == null) {
                sb = new StringBuilder();
            }
            if (pos == 0) {
                sb.append(toStr);
                pos2 = fromStr.length();
            } else if (pos > 0) {
                sb.append(str.substring(pos2, pos));
                sb.append(toStr);
                pos2 = pos + fromStr.length();
            } else { // (pos < 0) second or after loop only
                sb.append(str.substring(pos2));
                return sb.toString();
            }
        } while (true);
    }

    protected static boolean isSingleQuoted(String str) {
        return str.length() > 1 && str.startsWith("'") && str.endsWith("'");
    }

    protected static boolean isDoubleQuoted(String str) {
        return str.length() > 1 && str.startsWith("\"") && str.endsWith("\"");
    }

    protected static String unquoteSingle(String str) {
        if (!isSingleQuoted(str)) {
            return str;
        }
        return str.substring("'".length(), str.length() - "'".length());
    }

    protected static String unquoteDouble(String str) {
        if (!isDoubleQuoted(str)) {
            return str;
        }
        return str.substring("\"".length(), str.length() - "\"".length());
    }

    protected static <KEY, VALUE> ConcurrentHashMap<KEY, VALUE> newConcurrentHashMap() {
        return new ConcurrentHashMap<KEY, VALUE>();
    }

    protected static <KEY, VALUE> HashMap<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }

    protected static <KEY, VALUE> HashMap<KEY, VALUE> newHashMap() {
        return new HashMap<KEY, VALUE>();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StringKeyMap<?>) {
            @SuppressWarnings("unchecked")
            final StringKeyMap<Object> map = (StringKeyMap<Object>) obj;
            if (size() != map.size()) {
                return false;
            }
            final Map<?, ?> myMap = _plainMap != null ? _plainMap : _searchMap;
            final Map<?, ?> yourMap = map._plainMap != null ? map._plainMap : map._searchMap;
            return myMap.equals(yourMap);
        } else if (obj instanceof Map<?, ?>) {
            @SuppressWarnings("unchecked")
            final Map<Object, Object> map = (Map<Object, Object>) obj;
            if (size() != map.size()) {
                return false;
            }
            return _plainMap != null ? _plainMap.equals(map) : _searchMap.equals(map);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return _plainMap != null ? _plainMap.hashCode() : _searchMap.hashCode();
    }

    @Override
    public String toString() {
        return _plainMap != null ? _plainMap.toString() : _searchMap.toString();
    }
}