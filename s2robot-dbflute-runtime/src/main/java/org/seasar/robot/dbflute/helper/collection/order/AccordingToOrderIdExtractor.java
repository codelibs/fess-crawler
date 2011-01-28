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

/**
 * @author jflute
 * @param <ELEMENT_TYPE> The type of element.
 * @param <ID_TYPE> The type of ID.
 */
public interface AccordingToOrderIdExtractor<ELEMENT_TYPE, ID_TYPE> {

    /**
     * Extract ID from the element instance.
     * 
     * @param element Element instance. (NotNull)
     * @return Extracted ID. (NotNull)
     */
    ID_TYPE extractId(ELEMENT_TYPE element);
}
