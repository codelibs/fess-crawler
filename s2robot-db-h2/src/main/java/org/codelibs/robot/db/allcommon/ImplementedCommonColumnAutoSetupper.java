package org.codelibs.robot.db.allcommon;

import org.dbflute.Entity;
import org.dbflute.hook.CommonColumnAutoSetupper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The basic implementation of the auto set-upper of common column.
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedCommonColumnAutoSetupper implements
        CommonColumnAutoSetupper {

    // =====================================================================================
    //                                                                            Definition
    //                                                                            ==========
    /** The logger instance for this class. (NotNull) */
    private static final Logger _log = LoggerFactory
            .getLogger(ImplementedCommonColumnAutoSetupper.class);

    // =====================================================================================
    //                                                                                Set up
    //                                                                                ======
    /** {@inheritDoc} */
    @Override
    public void handleCommonColumnOfInsertIfNeeds(final Entity targetEntity) {
    }

    /** {@inheritDoc} */
    @Override
    public void handleCommonColumnOfUpdateIfNeeds(final Entity targetEntity) {
    }

    // =====================================================================================
    //                                                                               Logging
    //                                                                               =======
    protected boolean isInternalDebugEnabled() {
        return DBFluteConfig.getInstance().isInternalDebug()
                && _log.isDebugEnabled();
    }

    protected void logSettingUp(final EntityDefinedCommonColumn entity,
            final String keyword) {
        _log.debug("...Setting up column columns of " + entity.asTableDbName()
                + " before " + keyword);
    }
}
