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
package org.seasar.robot.dbflute.s2dao.valuetype.plugin;

import java.sql.Types;

import org.seasar.robot.dbflute.s2dao.valuetype.basic.StringType;

/**
 * Basically you don't need to use this
 * because normal StringType can treat CHAR type too.
 * But sometimes you need this, for example, when you use
 * CHAR type as PostgreSQL's procedure parameter which needs
 * to be set CHAR (VARCHAR not allowed).
 * @author jflute
 */
public class FixedLengthStringType extends StringType {

    public FixedLengthStringType() {
        super(Types.CHAR);
    }
}