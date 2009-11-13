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
package org.seasar.robot.dbflute.s2dao.metadata;

import java.util.Map;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public interface TnDtoMetaData {

    Class<?> getBeanClass();

    /**
     * Get the map of property type. The key is property name as case insensitive.
     * @return The map of property type. (NotNull)
     */
    Map<String, TnPropertyType> getPropertyTypeMap();

    /**
     * Get the property type by the key as case insensitive.
     * @param propertyName The name of property. (NotNull)
     * @return The type of property. (Nullable)
     */
    TnPropertyType getPropertyType(String propertyName);

    /**
     * Does it has the property type by the key as case insensitive.
     * @param propertyName The name of property. (NotNull)
     * @return Determination.
     */
    boolean hasPropertyType(String propertyName);
}