/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.util;

import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;

/**
 * @param <E>
 *
 */
public class LruHashSet<E> extends AbstractSet<E> implements Set<E>,
        java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final LruHashMap<E, Object> map;

    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();

    public LruHashSet(final int limitSize) {
        map = new LruHashMap<E, Object>(limitSize);
    }

    /**
     * Returns an iterator over the elements in this set. The elements are
     * returned in no particular order.
     *
     * @return an Iterator over the elements in this set.
     * @see ConcurrentModificationException
     */
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     *
     * @return the number of elements in this set (its cardinality).
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * Returns <tt>true</tt> if this set contains no elements.
     *
     * @return <tt>true</tt> if this set contains no elements.
     */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this set contains the specified element.
     *
     * @param o
     *            element whose presence in this set is to be tested.
     * @return <tt>true</tt> if this set contains the specified element.
     */
    @Override
    public boolean contains(final Object o) {
        return map.containsKey(o);
    }

    /**
     * Adds the specified element to this set if it is not already present.
     *
     * @param o
     *            element to be added to this set.
     * @return <tt>true</tt> if the set did not already contain the specified
     *         element.
     */
    @Override
    public boolean add(final E o) {
        return map.put(o, PRESENT) == null;
    }

    /**
     * Removes the specified element from this set if it is present.
     *
     * @param o
     *            object to be removed from this set, if present.
     * @return <tt>true</tt> if the set contained the specified element.
     */
    @Override
    public boolean remove(final Object o) {
        return map.remove(o) == PRESENT;
    }

    /**
     * Removes all of the elements from this set.
     */
    @Override
    public void clear() {
        map.clear();
    }

}
