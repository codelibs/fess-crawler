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
package org.seasar.robot.dbflute.jdbc;

/**
 * The handler of SQL result. <br />
 * This handler is called back after executing the SQL and mapping entities. <br />
 * (before you get the result)
 * <pre>
 * context.setSqlResultHandler(new SqlResultHandler() {
 *     public void handle(SqlResultInfo info) {
 *         // You can get your SQL result information here.
 *     }
 * });
 * </pre>
 * @author jflute
 */
public interface SqlResultHandler {

    /**
     * Handle the SQL result.
     * <pre>
     * [SqlResultInfo]
     * o result : The result(mapped object) of executed SQL. (NullAllowed)
     * o tableDbName : The DB name of table of executed behavior. (NotNull)
     * o commandName : The name of executed command. (for display only) (NotNull)
     * o displaySql : The latest executed SQL for display. (for display only) (NullAllowed: if the SQL would be not executed)
     * o beforeTimeMillis : The time in millisecond before executing command(after initializing executions).
     * o afterTimeMillis : The time in millisecond after executing command(after mapping entities).
     * </pre>
     * <p>
     * Attention: <br />
     * If the SQL would be not executed, the displaySql in the information is null.
     * For example, update() that the entity has no modification. <br />
     * And if the command would be for batch, this is called back only once in a command.
     * So the displaySql is the latest SQL in a command at that time.
     * </p>
     * @param info The information of executed SQL result. (NotNull)
     */
    void handle(SqlResultInfo info);
}
