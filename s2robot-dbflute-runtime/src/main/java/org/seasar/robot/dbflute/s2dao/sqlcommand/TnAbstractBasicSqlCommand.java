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
package org.seasar.robot.dbflute.s2dao.sqlcommand;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.bhv.core.SqlExecution;
import org.seasar.robot.dbflute.jdbc.StatementFactory;

/**
 * The basic command to execute SQL. <br />
 * This is basically reused on executing so it's thread safe. <br />
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public abstract class TnAbstractBasicSqlCommand implements TnSqlCommand, SqlExecution {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DataSource _dataSource;
    protected final StatementFactory _statementFactory;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param dataSource The data source for a database connection. (NotNull)
     * @param statementFactory The factory of statement. (NotNull)
     */
    public TnAbstractBasicSqlCommand(DataSource dataSource, StatementFactory statementFactory) {
        assertObjectNotNull("dataSource", dataSource);
        assertObjectNotNull("statementFactory", statementFactory);
        _dataSource = dataSource;
        _statementFactory = statementFactory;
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }
}
