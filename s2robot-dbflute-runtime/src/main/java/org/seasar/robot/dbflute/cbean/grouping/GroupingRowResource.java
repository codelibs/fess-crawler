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
package org.seasar.robot.dbflute.cbean.grouping;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The class of row resource for grouping making.
 * @param <ENTITY> The type of entity.
 * @author jflute
 */
public class GroupingRowResource<ENTITY> implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<ENTITY> _groupingRowList = new ArrayList<ENTITY>();
    protected int _elementCurrentIndex;
    protected int _breakCount;

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    /**
     * @return Does the list of grouping row size up the break count?
     */
    public boolean isSizeUpBreakCount() {
        return _elementCurrentIndex == (_breakCount - 1);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * @return The list of grouping row. (NotNull and NotEmpty)
     */
    public List<ENTITY> getGroupingRowList() {
        return this._groupingRowList;
    }

    /**
     * Add the element entity to the list of grouping row. {INTERNAL METHOD}
     * @param groupingRow The element entity to the list of grouping row.
     */
    public void addGroupingRowList(ENTITY groupingRow) {
        this._groupingRowList.add(groupingRow);
    }

    /**
     * @return The entity of element current index. (NotNull)
     */
    public ENTITY getCurrentEntity() {
        return _groupingRowList.get(_elementCurrentIndex);
    }

    /**
     * @return The index of current element.
     */
    public int getElementCurrentIndex() {
        return this._elementCurrentIndex;
    }

    /**
     * Set the index of current element. {INTERNAL METHOD}
     * @param elementCurrentIndex The index of current element.
     */
    public void setElementCurrentIndex(int elementCurrentIndex) {
        this._elementCurrentIndex = elementCurrentIndex;
    }

    /**
     * @return The count of break loop.
     */
    public int getBreakCount() {
        return this._breakCount;
    }

    /**
     * Set the count of break loop. {INTERNAL METHOD}
     * @param breakCount The count of break loop.
     */
    public void setBreakCount(int breakCount) {
        this._breakCount = breakCount;
    }
}
