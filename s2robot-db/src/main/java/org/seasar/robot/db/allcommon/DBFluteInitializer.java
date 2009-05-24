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
package org.seasar.robot.db.allcommon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.cbean.ConditionBeanContext;
import org.seasar.dbflute.s2dao.extension.TnSqlLogRegistry;
import org.seasar.dbflute.util.DfSystemUtil;

/**
 * @author DBFlute(AutoGenerator)
 */
public class DBFluteInitializer {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DBFluteInitializer.class);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor. And initialize various components.
     */
    public DBFluteInitializer() {
        handleSqlLogRegistry();
        loadCoolClasses();
        DBFluteConfig.getInstance().lock();
    }

    // ===================================================================================
    //                                                                        Cool Classes
    //                                                                        ============
    protected void loadCoolClasses() { // for S2Container basically 
        ConditionBeanContext.loadCoolClasses(); // Against the ClassLoader Headache!
    }

    // ===================================================================================
    //                                                                    SQL Log Registry
    //                                                                    ================
    protected void handleSqlLogRegistry() {
        if (DBFluteConfig.getInstance().isUseSqlLogRegistry()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("{SqlLog Information}").append(getLineSeparator());
            sb.append("  [SqlLogRegistry]").append(getLineSeparator());
            if (TnSqlLogRegistry.setupSqlLogRegistry()) {
                sb
                        .append(
                                "    ...Setting up sqlLogRegistry(org.seasar.extension.jdbc)!")
                        .append(getLineSeparator());
                sb
                        .append("    Because the property 'useSqlLogRegistry' of the config of DBFlute is true.");
            } else {
                sb
                        .append("    The sqlLogRegistry(org.seasar.extension.jdbc) is not supported at the version!");
            }
            _log.info(sb);
        } else {
            final Object sqlLogRegistry = TnSqlLogRegistry
                    .findContainerSqlLogRegistry();
            if (sqlLogRegistry != null) {
                TnSqlLogRegistry.closeRegistration();
            }
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }
}
