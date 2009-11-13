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
package org.seasar.robot.dbflute;

/**
 * @author jflute
 */
public class XLog {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final org.apache.commons.logging.Log _log = org.apache.commons.logging.LogFactory.getLog(XLog.class);
	protected static boolean _executeStatusLogLevelInfo;
	protected static boolean _locked = true;

    // ===================================================================================
    //                                                                             Logging
    //                                                                             =======
	public static void log(String msg) {// Very Internal
		if (isExecuteStatusLogLevelInfo()) {
	        _log.info(msg);
		} else {
	        _log.debug(msg);
		}
	}
	
    public static boolean isLogEnabled() {// Very Internal
		if (isExecuteStatusLogLevelInfo()) {
	        return _log.isInfoEnabled();
		} else {
	        return _log.isDebugEnabled();
		}
	}
	
	protected static boolean isExecuteStatusLogLevelInfo() {
	    return _executeStatusLogLevelInfo;
	}

	public static void setExecuteStatusLogLevelInfo(boolean executeStatusLogLevelInfo) {
	    assertNotLocked();
		if (_log.isInfoEnabled()) {
		    _log.info("...Setting executeStatusLogLevelInfo: " + executeStatusLogLevelInfo);
		}
	    _executeStatusLogLevelInfo = executeStatusLogLevelInfo;
	}
	
    // ===================================================================================
    //                                                                                Lock
    //                                                                                ====
	public static boolean isLocked() {
	    return _locked;
	}
	
	public static void lock() {
		if (_log.isInfoEnabled()) {
		    _log.info("...Locking the log object for execute status!");
		}
	    _locked = true;
	}
	
	public static void unlock() {
		if (_log.isInfoEnabled()) {
		    _log.info("...Unlocking the log object for execute status!");
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
