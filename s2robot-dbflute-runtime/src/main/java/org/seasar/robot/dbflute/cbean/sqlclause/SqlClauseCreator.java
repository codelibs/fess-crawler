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
package org.seasar.robot.dbflute.cbean.sqlclause;

import org.seasar.robot.dbflute.cbean.ConditionBean;

/**
 * The creator of SQL clause.
 * @author jflute
 */
public interface SqlClauseCreator {

    /**
     * Create SQL clause. {for condition-bean}
     * @param cb Condition-bean. (NotNull) 
     * @return SQL clause. (NotNull)
     */
    SqlClause createSqlClause(ConditionBean cb);

    /**
     * Create SQL clause.
     * @param tableDbName The DB name of table. (NotNull) 
     * @return SQL clause. (NotNull)
     */
    SqlClause createSqlClause(String tableDbName);
}
