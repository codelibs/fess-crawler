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
package org.seasar.robot.dbflute.helper.collection.order;

import java.util.List;

/**
 * @author jflute
 * @param <ELEMENT_TYPE> The type of element.
 * @param <ID_TYPE> The type of ID.
 */
public class AccordingToOrderOption<ELEMENT_TYPE, ID_TYPE> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<ID_TYPE> _orderedUniqueIdList;

    protected AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> _idExtractor;

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public void setupOrderedResource(List<ID_TYPE> orderedUniqueIdList,
            AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> idExtractor) {
        setOrderedUniqueIdList(orderedUniqueIdList);
        setIdExtractor(idExtractor);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<ID_TYPE> getOrderedUniqueIdList() {
        return _orderedUniqueIdList;
    }

    public void setOrderedUniqueIdList(List<ID_TYPE> orderedUniqueIdList) {
        this._orderedUniqueIdList = orderedUniqueIdList;
    }

    public AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> getIdExtractor() {
        return _idExtractor;
    }

    public void setIdExtractor(AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> idExtractor) {
        _idExtractor = idExtractor;
    }
}
