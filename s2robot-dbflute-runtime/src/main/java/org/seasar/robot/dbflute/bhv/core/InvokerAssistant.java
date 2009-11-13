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
package org.seasar.robot.dbflute.bhv.core;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseCreator;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.resource.ResourceParameter;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaDataFactory;
import org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypeFactory;
import org.seasar.robot.dbflute.twowaysql.factory.SqlAnalyzerFactory;

/**
 * @author jflute
 */
public interface InvokerAssistant {

    /**
     * @return The current database definition. (NotNull)
     */
    DBDef assistCurrentDBDef();

    /**
     * @return The data source. (NotNull)
     */
    DataSource assistDataSource();

    /**
     * @return The provider of DB meta. (NotNull)
     */
    DBMetaProvider assistDBMetaProvider();

    /**
     * @return The create of SQL clause. (NotNull)
     */
    SqlClauseCreator assistSqlClauseCreator();

    /**
     * @return The factory of statement. (NotNull)
     */
    StatementFactory assistStatementFactory();

    /**
     * @return The factory of bean meta data. (NotNull)
     */
    TnBeanMetaDataFactory assistBeanMetaDataFactory();

    /**
     * @return The factory of value type. (NotNull)
     */
    TnValueTypeFactory assistValueTypeFactory();

    /**
     * @return The factory of SQL analyzer. (NotNull)
     */
    SqlAnalyzerFactory assistSqlAnalyzerFactory();

    /**
     * @return The parameter of resource. (NotNull)
     */
    ResourceParameter assistResourceParameter();

    /**
     * @return The encoding of SQL files. (NotNull)
     */
    String assistSqlFileEncoding();

    /**
     * @return The default configuration of statement. (NotNull)
     */
    StatementConfig assistDefaultStatementConfig();

    /**
     * To be disposable.
     */
    void toBeDisposable();
}
