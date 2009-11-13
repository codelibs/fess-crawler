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
package org.seasar.robot.dbflute.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jflute
 * @since 0.9.4 (2009/03/20 Friday)
 */
public class DfCollectionUtil {

    // ===================================================================================
    //                                                                                 New
    //                                                                                 ===
    public static <ELEMENT> List<ELEMENT> newArrayList() {
        return new ArrayList<ELEMENT>();
    }

    // ===================================================================================
    //                                                                               Split
    //                                                                               =====
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
}
