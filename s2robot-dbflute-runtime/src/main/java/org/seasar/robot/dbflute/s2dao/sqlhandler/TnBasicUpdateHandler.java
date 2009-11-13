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
package org.seasar.robot.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.jdbc.StatementFactory;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnBasicUpdateHandler extends TnBasicHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBasicUpdateHandler(DataSource dataSource, String sql, StatementFactory statementFactory) {
        super(dataSource, sql, statementFactory);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    public int execute(Object[] args) {
        return execute(args, getArgTypes(args));
    }

    public int execute(Object[] args, Class<?>[] argTypes) {
        Connection connection = getConnection();
        try {
            return execute(connection, args, argTypes);
        } finally {
            close(connection);
        }
    }

    public int execute(Connection connection, Object[] args, Class<?>[] argTypes) {
        logSql(args, argTypes);
        PreparedStatement ps = prepareStatement(connection);
        try {
            bindArgs(ps, args, argTypes);
            return executeUpdate(ps);
        } finally {
            close(ps);
        }
    }
}
