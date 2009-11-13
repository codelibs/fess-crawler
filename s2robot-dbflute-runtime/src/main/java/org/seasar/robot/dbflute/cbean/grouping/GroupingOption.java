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
package org.seasar.robot.dbflute.cbean.grouping;

/**
 * The class of option for grouping.
 * @param  <ENTITY> The type of entity.
 * @author jflute
 */
public class GroupingOption<ENTITY> {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected int _elementCount;

    protected GroupingRowEndDeterminer<ENTITY> _groupingRowEndDeterminer;

    // =====================================================================================
    //                                                                           Constructor
    //                                                                           ===========
    /**
     * Constructor. You should set the determiner of grouping row end after you create the instance.
     */
    public GroupingOption() {
    }

    /**
     * Constructor.
     * @param elementCount The count of row element in a group.
     */
    public GroupingOption(int elementCount) {
        _elementCount = elementCount;
    }

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public int getElementCount() {
        return this._elementCount;
    }

    public GroupingRowEndDeterminer<ENTITY> getGroupingRowEndDeterminer() {
        return this._groupingRowEndDeterminer;
    }

    public void setGroupingRowEndDeterminer(GroupingRowEndDeterminer<ENTITY> groupingRowEndDeterminer) {
        this._groupingRowEndDeterminer = groupingRowEndDeterminer;
    }
}
