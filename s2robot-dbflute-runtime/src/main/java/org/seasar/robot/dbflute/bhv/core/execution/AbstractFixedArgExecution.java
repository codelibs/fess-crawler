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
package org.seasar.robot.dbflute.bhv.core.execution;

import java.util.Map;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.sqlcommand.TnAbstractTwoWaySqlCommand;

/**
 * The SQL execution of 2Way-SQL as fixed arguments.
 * @author jflute
 * @since 0.9.7.9 (2010/12/26 Sunday)
 */
public abstract class AbstractFixedArgExecution extends TnAbstractTwoWaySqlCommand {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String[] _argNames;
    protected final Class<?>[] _argTypes;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param dataSource The data source for a database connection. (NotNull)
     * @param statementFactory The factory of statement. (NotNull)
     * @param argNameTypeMap The map of names and types for arguments. (NotNull)
     */
    public AbstractFixedArgExecution(DataSource dataSource, StatementFactory statementFactory,
            Map<String, Class<?>> argNameTypeMap) {
        super(dataSource, statementFactory);
        assertObjectNotNull("argNameTypeMap", argNameTypeMap);
        _argNames = argNameTypeMap.keySet().toArray(new String[] {});
        _argTypes = argNameTypeMap.values().toArray(new Class<?>[] {});
    }

    // ===================================================================================
    //                                                                            Resource
    //                                                                            ========
    @Override
    protected String[] getArgNames(Object[] args) {
        return _argNames;
    }

    @Override
    protected Class<?>[] getArgTypes(Object[] args) {
        return _argTypes;
    }
}
