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
package org.seasar.robot.dbflute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jflute
 */
public class QLog {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(QLog.class);
    protected static boolean _queryLogLevelInfo;
    protected static boolean _locked = true;

    // ===================================================================================
    //                                                                             Logging
    //                                                                             =======
    public static void log(String sql) { // very Internal
        if (isQueryLogLevelInfo()) {
            _log.info(sql);
        } else {
            _log.debug(sql);
        }
    }

    public static boolean isLogEnabled() { // very internal
        if (isQueryLogLevelInfo()) {
            return _log.isInfoEnabled();
        } else {
            return _log.isDebugEnabled();
        }
    }

    protected static boolean isQueryLogLevelInfo() {
        return _queryLogLevelInfo;
    }

    public static void setQueryLogLevelInfo(boolean queryLogLevelInfo) {
        assertNotLocked();
        if (_log.isInfoEnabled()) {
            _log.info("...Setting queryLogLevelInfo: " + queryLogLevelInfo);
        }
        _queryLogLevelInfo = queryLogLevelInfo;
    }

    // ===================================================================================
    //                                                                                Lock
    //                                                                                ====
    public static boolean isLocked() {
        return _locked;
    }

    public static void lock() {
        if (_log.isInfoEnabled()) {
            _log.info("...Locking the log object for query!");
        }
        _locked = true;
    }

    public static void unlock() {
        if (_log.isInfoEnabled()) {
            _log.info("...Unlocking the log object for query!");
        }
        _locked = false;
    }

    protected static void assertNotLocked() {
        if (!isLocked()) {
            return;
        }
        String msg = "The QLog is locked! Don't access at this timing!";
        throw new IllegalStateException(msg);
    }
}
