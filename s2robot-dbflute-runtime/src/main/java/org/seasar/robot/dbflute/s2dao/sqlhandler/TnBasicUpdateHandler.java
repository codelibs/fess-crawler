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
package org.seasar.robot.dbflute.s2dao.sqlhandler;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.jdbc.StatementFactory;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnBasicUpdateHandler extends TnBasicParameterHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnBasicUpdateHandler(DataSource dataSource, StatementFactory statementFactory, String sql) {
        super(dataSource, statementFactory, sql);
    }

    // ===================================================================================
    //                                                                             Execute
    //                                                                             =======
    @Override
    protected Object doExecute(Connection conn, Object[] args, Class<?>[] argTypes) {
        logSql(args, argTypes);
        final PreparedStatement ps = prepareStatement(conn);
        try {
            bindArgs(conn, ps, args, argTypes);
            return executeUpdate(ps);
        } finally {
            close(ps);
        }
    }
}
