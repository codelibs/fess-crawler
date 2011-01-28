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
package org.seasar.robot.dbflute.helper.collection.order.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.helper.collection.order.AccordingToOrder;
import org.seasar.robot.dbflute.helper.collection.order.AccordingToOrderIdExtractor;
import org.seasar.robot.dbflute.helper.collection.order.AccordingToOrderOption;

/**
 * @author jflute
 */
public class AccordingToOrderImpl implements AccordingToOrder {

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    /**
     * {@inheritDoc}
     */
    public <ELEMENT_TYPE, ID_TYPE> void order(final List<ELEMENT_TYPE> unorderedList,
            final AccordingToOrderOption<ELEMENT_TYPE, ID_TYPE> option) {
        assertObjectNotNull("unorderedList", unorderedList);
        if (unorderedList.isEmpty()) {
            return;
        }
        assertObjectNotNull("option", option);
        final List<ID_TYPE> orderedUniqueIdList = option.getOrderedUniqueIdList();
        assertObjectNotNull("option.getOrderedUniqueIdList()", orderedUniqueIdList);
        if (orderedUniqueIdList.isEmpty()) {
            return;
        }
        final AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> idExtractor = option.getIdExtractor();
        assertObjectNotNull("option.getIdExtractor()", idExtractor);

        final Map<ID_TYPE, Integer> idIndexMap = new LinkedHashMap<ID_TYPE, Integer>();
        int index = 0;
        for (ID_TYPE id : orderedUniqueIdList) {
            if (idIndexMap.containsKey(id)) {
                String msg = "The id was duplicated: id=" + id + " orderedUniqueIdList=" + orderedUniqueIdList;
                throw new IllegalStateException(msg);
            }
            idIndexMap.put(id, index);
            ++index;
        }
        final Comparator<ELEMENT_TYPE> comp = new Comparator<ELEMENT_TYPE>() {
            public int compare(ELEMENT_TYPE o1, ELEMENT_TYPE o2) {
                final ID_TYPE id1 = idExtractor.extractId(o1);
                final ID_TYPE id2 = idExtractor.extractId(o2);
                assertObjectNotNull("id1 of " + o1, id1);
                assertObjectNotNull("id2 of " + o2, id2);
                final Integer index1 = idIndexMap.get(id1);
                final Integer index2 = idIndexMap.get(id2);
                if (index1 != null && index2 != null) {
                    return index1.compareTo(index2);
                }
                if (index1 == null && index2 == null) {
                    return 0;
                }
                return index1 == null ? 1 : -1;
            }
        };
        Collections.sort(unorderedList, comp);
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    /**
     * Assert that the object is not null.
     * 
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}
