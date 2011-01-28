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
package org.seasar.robot.dbflute.outsidesql;

/**
 * The filter call-back for outside-SQL.
 * @author jflute
 * @since 0.9.7.6 (2010/11/24 Wednesday)
 */
public interface OutsideSqlFilter {

    /**
     * Filter the outside-SQL immediately before execution. (for user customization) <br >
     * This filter is executed immediately before execution.
     * (but removing comments and formatting options are after the filter) <br />
     * And whether a procedure's SQL is contained or not is determined by containsProcedure().
     * @param executedSql The string of executed outside-SQL already resolved parameter comments. (NotNull)
     * @param filterType The type of execution filter. (NotNull)
     * @return The filtered SQL that is executed by JDBC directly. (NotNull)
     */
    String filterExecution(String executedSql, ExecutionFilterType filterType);

    /**
     * Filter the outside-SQL immediately after reading a SQL file. (for user customization) <br />
     * This filter is executed immediately after reading.
     * (but removing UTF-8 BOM and adjusting bind mark on comment are before the filter)
     * @param readSql The string of read outside-SQL still remained parameter comments. (NotNull)
     * @return The filtered SQL that is executed by JDBC directly. (NotNull)
     */
    String filterReading(String readSql);

    /**
     * The type of execution filter.
     */
    public enum ExecutionFilterType {
        SELECT, EXECUTE, PROCEDURE
    }
}
