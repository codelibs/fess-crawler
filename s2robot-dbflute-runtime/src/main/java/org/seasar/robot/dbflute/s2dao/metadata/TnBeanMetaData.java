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

import java.util.Set;

import org.seasar.robot.dbflute.s2dao.identity.TnIdentifierGenerator;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public interface TnBeanMetaData extends TnDtoMetaData {

    String getTableName();

    TnPropertyType getVersionNoPropertyType();

    String getVersionNoPropertyName();

    boolean hasVersionNoPropertyType();

    TnPropertyType getTimestampPropertyType();

    String getTimestampPropertyName();

    boolean hasTimestampPropertyType();

    String convertFullColumnName(String alias);

    TnPropertyType getPropertyTypeByAliasName(String aliasName);

    TnPropertyType getPropertyTypeByColumnName(String columnName);

    boolean hasPropertyTypeByColumnName(String columnName);

    boolean hasPropertyTypeByAliasName(String aliasName);

    int getRelationPropertyTypeSize();

    TnRelationPropertyType getRelationPropertyType(int index);

    TnRelationPropertyType getRelationPropertyType(String propertyName);

    int getPrimaryKeySize();

    String getPrimaryKey(int index);

    int getIdentifierGeneratorSize();

    TnIdentifierGenerator getIdentifierGenerator(int index);

    TnIdentifierGenerator getIdentifierGenerator(String propertyName);

    Set<String> getModifiedPropertyNames(Object bean);
}
