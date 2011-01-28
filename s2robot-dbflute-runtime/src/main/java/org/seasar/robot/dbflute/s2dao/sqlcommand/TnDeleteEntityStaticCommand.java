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

import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnAbstractEntityHandler;
import org.seasar.robot.dbflute.s2dao.sqlhandler.TnDeleteEntityHandler;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnDeleteEntityStaticCommand extends TnAbstractEntityStaticCommand {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnDeleteEntityStaticCommand(DataSource dataSource, StatementFactory statementFactory,
            TnBeanMetaData beanMetaData, DBMeta targetDBMeta, String[] propertyNames, boolean optimisticLockHandling) {
        super(dataSource, statementFactory, beanMetaData, targetDBMeta, propertyNames, optimisticLockHandling, false);
    }

    // ===================================================================================
    //                                                                            Override
    //                                                                            ========
    @Override
    protected TnAbstractEntityHandler createEntityHandler(Object[] args) {
        final TnAbstractEntityHandler handler = super.createEntityHandler(args);
        handler.setDeleteOption(extractDeleteOption(args));
        return handler;
    }

    @Override
    protected TnAbstractEntityHandler newEntityHandler() {
        return new TnDeleteEntityHandler(_dataSource, _statementFactory, _sql, _beanMetaData, _propertyTypes);
    }

    @Override
    protected void setupSql() {
        setupDeleteSql();
    }
}
