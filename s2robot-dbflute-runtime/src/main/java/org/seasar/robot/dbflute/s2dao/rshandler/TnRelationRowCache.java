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
package org.seasar.robot.dbflute.s2dao.rshandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnRelationRowCache {

    /** The list of row map. */
    private final List<Map<TnRelationKey, Object>> rowMapList;

    /**
     * @param size The size of relation.
     */
    public TnRelationRowCache(int size) {
        rowMapList = new ArrayList<Map<TnRelationKey, Object>>();
        for (int i = 0; i < size; ++i) {
            rowMapList.add(new HashMap<TnRelationKey, Object>());
        }
    }

    protected void initializeRowMapList() {

    }

    /**
     * @param relno The relation number.
     * @param key The key of relation. (NotNull)
     * @return The relation row. (NullAllowed)
     */
    public Object getRelationRow(int relno, TnRelationKey key) {
        return getRowMap(relno).get(key);
    }

    /**
     * @param relno The relation number.
     * @param key The key of relation. (NotNull)
     * @param row The relation row. (NullAllowed)
     */
    public void addRelationRow(int relno, TnRelationKey key, Object row) {
        getRowMap(relno).put(key, row);
    }

    protected Map<TnRelationKey, Object> getRowMap(int relno) {
        return (Map<TnRelationKey, Object>) rowMapList.get(relno);
    }
}
