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
package org.seasar.robot.dbflute.dbmeta;

/**
 * The interface of DB meta.
 * @author jflute
 */
public interface DBMetaProvider {

    /**
     * Provide the DB meta.
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (NullAllowed: If the DB meta is not found, it returns null)
     */
    DBMeta provideDBMeta(String tableFlexibleName);

    /**
     * Provide the DB meta.
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.DBMetaNotFoundException When the DB meta is not found.
     */
    DBMeta provideDBMetaChecked(String tableFlexibleName);
}
