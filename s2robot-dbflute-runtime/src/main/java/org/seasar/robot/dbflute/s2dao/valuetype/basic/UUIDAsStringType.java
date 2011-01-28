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
package org.seasar.robot.dbflute.s2dao.valuetype.basic;

import java.sql.Types;

/**
 * The value type of UUID as string handling. <br />
 * This value type is available if the JDBC driver
 * does NOT allow UUID type, for example when binding.
 * @author jflute
 */
public class UUIDAsStringType extends UUIDAsDirectType {

    public UUIDAsStringType() {
        super(Types.VARCHAR);
    }

    @Override
    protected Object toBindingValue(Object value) {
        return value.toString();
    }
}