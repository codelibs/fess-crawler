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
package org.seasar.robot.dbflute.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jflute
 * @since 0.9.4 (2009/03/20 Friday)
 */
public class DfCollectionUtil {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final List<?> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<Object>());
    private static final Map<?, ?> EMPTY_MAP = Collections.unmodifiableMap(new HashMap<Object, Object>());
    private static final Set<?> EMPTY_SET = Collections.unmodifiableSet(new HashSet<Object>());

    // ===================================================================================
    //                                                                          Collection
    //                                                                          ==========
    public static Class<?> findFirstElementType(Collection<?> collection) {
        for (Object object : collection) {
            if (object != null) {
                return object.getClass();
            }
        }
        return null;
    }

    public static boolean hasValidElement(Collection<?> collection) {
        for (Object object : collection) {
            if (object != null) {
                return true;
            }
        }
        return false;
    }

    // ===================================================================================
    //                                                                                List
    //                                                                                ====
    public static <ELEMENT> List<ELEMENT> newArrayList() {
        return new ArrayList<ELEMENT>();
    }

    public static <ELEMENT> List<ELEMENT> newArrayList(Collection<ELEMENT> elements) {
        final List<ELEMENT> list = newArrayList();
        list.addAll(elements);
        return list;
    }

    public static <ELEMENT> List<ELEMENT> newArrayList(ELEMENT... elements) {
        final List<ELEMENT> list = newArrayList();
        for (ELEMENT element : elements) {
            list.add(element);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public static <ELEMENT> List<ELEMENT> emptyList() {
        return (List<ELEMENT>) EMPTY_LIST;
    }

    public static <ELEMENT extends Object> List<List<ELEMENT>> splitByLimit(List<ELEMENT> elementList, int limit) {
        final List<List<ELEMENT>> valueList = newArrayList();
        final int valueSize = elementList.size();
        int index = 0;
        int remainderSize = valueSize;
        do {
            final int beginIndex = limit * index;
            final int endPoint = beginIndex + limit;
            final int endIndex = limit <= remainderSize ? endPoint : valueSize;
            final List<ELEMENT> splitList = newArrayList();
            splitList.addAll(elementList.subList(beginIndex, endIndex));
            valueList.add(splitList);
            remainderSize = valueSize - endIndex;
            ++index;
        } while (remainderSize > 0);
        return valueList;
    }

    // ===================================================================================
    //                                                                                 Map
    //                                                                                 ===
    public static <KEY, VALUE> Map<KEY, VALUE> newHashMap() {
        return new HashMap<KEY, VALUE>();
    }

    public static <KEY, VALUE> Map<KEY, VALUE> newLinkedHashMap() {
        return new LinkedHashMap<KEY, VALUE>();
    }

    public static <KEY, VALUE> Map<KEY, VALUE> newConcurrentHashMap() {
        return new ConcurrentHashMap<KEY, VALUE>();
    }

    @SuppressWarnings("unchecked")
    public static <KEY, VALUE> Map<KEY, VALUE> emptyMap() {
        return (Map<KEY, VALUE>) EMPTY_MAP;
    }

    // ===================================================================================
    //                                                                                 Set
    //                                                                                 ===
    public static <ELEMENT> Set<ELEMENT> newHashSet() {
        return new HashSet<ELEMENT>();
    }

    public static <ELEMENT> Set<ELEMENT> newHashSet(Collection<ELEMENT> elements) {
        final Set<ELEMENT> set = newHashSet();
        set.addAll(elements);
        return set;
    }

    public static <ELEMENT> Set<ELEMENT> newHashSet(ELEMENT... elements) {
        final Set<ELEMENT> set = newHashSet();
        for (ELEMENT element : elements) {
            set.add(element);
        }
        return set;
    }

    public static <ELEMENT> Set<ELEMENT> newLinkedHashSet() {
        return new LinkedHashSet<ELEMENT>();
    }

    public static <ELEMENT> Set<ELEMENT> newLinkedHashSet(Collection<ELEMENT> elements) {
        final Set<ELEMENT> set = newLinkedHashSet();
        set.addAll(elements);
        return set;
    }

    public static <ELEMENT> Set<ELEMENT> newLinkedHashSet(ELEMENT... elements) {
        final Set<ELEMENT> set = newLinkedHashSet();
        for (ELEMENT element : elements) {
            set.add(element);
        }
        return set;
    }

    @SuppressWarnings("unchecked")
    public static <ELEMENT> Set<ELEMENT> emptySet() {
        return (Set<ELEMENT>) EMPTY_SET;
    }
}
