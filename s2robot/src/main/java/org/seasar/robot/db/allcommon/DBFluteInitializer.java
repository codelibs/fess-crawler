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
                sb.append("    ...Setting up sqlLogRegistry(org.seasar.extension.jdbc)!").append(getLineSeparator());
                sb.append("    Because the property 'useSqlLogRegistry' of the config of DBFlute is true.");
            } else {
                sb.append("    The sqlLogRegistry(org.seasar.extension.jdbc) is not supported at the version!");
            }
           _log.info(sb);
        } else {
            final Object sqlLogRegistry = TnSqlLogRegistry.findContainerSqlLogRegistry();
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
