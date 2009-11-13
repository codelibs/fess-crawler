package org.seasar.robot.db.allcommon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.DBDef;

/**
 * @author DBFlute(AutoGenerator)
 */
public class DBCurrent {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DBCurrent.class);

    /** Singleton instance. */
    private static final DBCurrent _instance = new DBCurrent();

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DBDef _currentDBDef;
    {
        _currentDBDef = DBDef.codeOf("H2");
        if (_currentDBDef == null) {
            _currentDBDef = DBDef.Unknown;
        }
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     */
    private DBCurrent() {
    }

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    /**
     * Get singleton instance.
     * @return Singleton instance. (NotNull)
     */
    public static DBCurrent getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    public void initializeCurrentDBDef(DBDef currentDBDef) {
        if (_log.isInfoEnabled()) {
            _log.info("...Setting currentDBDef: " + currentDBDef);
        }
        if (currentDBDef == null) {
            String msg = "The argument 'currentDBDef' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        _currentDBDef = currentDBDef;
    }

    public DBDef currentDBDef() {
        return _currentDBDef;
    }

    public boolean isCurrentDBDef(DBDef currentDBDef) {
        return _currentDBDef.equals(currentDBDef);
    }
}
