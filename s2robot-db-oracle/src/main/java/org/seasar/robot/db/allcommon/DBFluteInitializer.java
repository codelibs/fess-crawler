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
package org.seasar.robot.db.allcommon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.jdbc.DataSourceHandler;
import org.seasar.robot.dbflute.s2dao.extension.TnSqlLogRegistry;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author DBFlute(AutoGenerator)
 */
public class DBFluteInitializer {

    // ===================================================================================
    // Definition
    // ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DBFluteInitializer.class);

    // ===================================================================================
    // Attribute
    // =========

    // ===================================================================================
    // Constructor
    // ===========
    /**
     * Constructor, which initializes various components.
     */
    public DBFluteInitializer() {
        announce();
        prologue();
        standBy();
    }

    protected void announce() {
        _log.info("...Initializing DBFlute components");
    }

    protected void prologue() {
        handleSqlLogRegistry();
        loadCoolClasses();
    }

    protected void standBy() {
        if (!DBFluteConfig.getInstance().isLocked()) {
            DBFluteConfig.getInstance().lock();
        }
    }

    protected void handleSqlLogRegistry() { // for S2Container
        if (DBFluteConfig.getInstance().isUseSqlLogRegistry()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("{SqlLog Information}").append(ln());
            sb.append("  [SqlLogRegistry]").append(ln());
            if (TnSqlLogRegistry.setupSqlLogRegistry()) {
                sb
                    .append(
                        "    ...Setting up sqlLogRegistry(org.seasar.extension.jdbc)")
                    .append(ln());
                sb
                    .append("    because the property 'useSqlLogRegistry' of the config of DBFlute is true");
            } else {
                sb
                    .append("    The sqlLogRegistry(org.seasar.extension.jdbc) is not supported at the version");
            }
            _log.info(sb);
        } else {
            final Object sqlLogRegistry =
                TnSqlLogRegistry.findContainerSqlLogRegistry();
            if (sqlLogRegistry != null) {
                TnSqlLogRegistry.closeRegistration();
            }
        }
    }

    protected void loadCoolClasses() { // for S2Container
        ConditionBeanContext.loadCoolClasses(); // against the ClassLoader
                                                // Headache!
    }

    /**
     * Set up the handler of data source to the configuration of DBFlute. <br />
     * If it uses commons-DBCP, it needs to arrange some for transaction.
     * <ul>
     * <li>A. To use DataSourceUtils which is Spring Framework class.</li>
     * <li>B. To use TransactionConnection that is original class and doesn't
     * close really.</li>
     * </ul>
     * If you use a transaction library which has a data source which supports
     * transaction, It doesn't need these arrangement. (For example, the
     * framework 'Atomikos') <br />
     * This method should be executed when application is initialized.
     * 
     * @param dataSourceFqcn
     *            The FQCN of data source. (NotNull)
     */
    protected void setupDataSourceHandler(final String dataSourceFqcn) { // for Spring
        final DBFluteConfig config = DBFluteConfig.getInstance();
        final DataSourceHandler dataSourceHandler =
            config.getDataSourceHandler();
        if (dataSourceHandler != null) {
            return;
        }
        if (dataSourceFqcn.startsWith("org.apache.commons.dbcp.")) {
            config.unlock();
            config
                .setDataSourceHandler(new DBFluteConfig.SpringDBCPDataSourceHandler());
        }
    }

    // ===================================================================================
    // Assist Helper
    // =============
    protected boolean isCurrentDBDef(final DBDef currentDBDef) {
        return DBCurrent.getInstance().isCurrentDBDef(currentDBDef);
    }

    // ===================================================================================
    // General Helper
    // ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
