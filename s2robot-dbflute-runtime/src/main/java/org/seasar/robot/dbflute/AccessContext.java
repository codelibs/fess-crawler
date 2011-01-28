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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.seasar.robot.dbflute.exception.AccessContextNoValueException;
import org.seasar.robot.dbflute.exception.AccessContextNotFoundException;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The context of DB access. (basically for CommonColumnAutoSetup)
 * @author jflute
 */
public class AccessContext {

    // ===================================================================================
    //                                                                        Thread Local
    //                                                                        ============
    /** The thread-local for this. */
    private static final ThreadLocal<AccessContext> _threadLocal = new ThreadLocal<AccessContext>();

    /**
     * Get access-context on thread.
     * @return The context of DB access. (NullAllowed)
     */
    public static AccessContext getAccessContextOnThread() {
        return (AccessContext) _threadLocal.get();
    }

    /**
     * Set access-context on thread.
     * @param accessContext The context of DB access. (NotNull)
     */
    public static void setAccessContextOnThread(AccessContext accessContext) {
        if (accessContext == null) {
            String msg = "The argument[accessContext] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(accessContext);
    }

    /**
     * Is existing access-context on thread?
     * @return Determination.
     */
    public static boolean isExistAccessContextOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear access-context on thread.
     */
    public static void clearAccessContextOnThread() {
        _threadLocal.set(null);
    }

    // ===================================================================================
    //                                                                  Access Information
    //                                                                  ==================
    /**
     * Get access date on thread. <br />
     * If it couldn't get access date from access-context, it returns application current date!
     * @return Access date. (NotNull)
     */
    public static Date getAccessDateOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final java.util.Date accessDate = userContextOnThread.getAccessDate();
            if (accessDate != null) {
                return accessDate;
            }
            if (userContextOnThread.getAccessDateProvider() != null) {
                return userContextOnThread.getAccessDateProvider().getAccessDate();
            }
        }
        return new Date();
    }

    /**
     * Get access time-stamp on thread. <br />
     * If it couldn't get access time-stamp from access-context, it returns application current time-stamp!
     * @return Access time-stamp. (NotNull)
     */
    public static Timestamp getAccessTimestampOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final Timestamp accessTimestamp = userContextOnThread.getAccessTimestamp();
            if (accessTimestamp != null) {
                return accessTimestamp;
            }
            if (userContextOnThread.getAccessTimestampProvider() != null) {
                return userContextOnThread.getAccessTimestampProvider().getAccessTimestamp();
            }
        }
        return new Timestamp(DfSystemUtil.currentTimeMillis());
    }

    /**
     * Get access user on thread.
     * @return Access user. (NotNull)
     * @exception IllegalStateException When it couldn't get access user.
     */
    public static String getAccessUserOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessUser = userContextOnThread.getAccessUser();
            if (accessUser != null) {
                return accessUser;
            }
        }
        String methodName = "getAccessUserOnThread()";
        if (isExistAccessContextOnThread()) {
            throwAccessContextNoValueException(methodName, "AccessUser", "user");
        } else {
            throwAccessContextNotFoundException(methodName);
        }
        return null; // unreachable
    }

    /**
     * Get access process on thread.
     * @return Access process. (NotNull)
     * @exception IllegalStateException When it couldn't get access process.
     */
    public static String getAccessProcessOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessProcess = userContextOnThread.getAccessProcess();
            if (accessProcess != null) {
                return accessProcess;
            }
        }
        String methodName = "getAccessProcessOnThread()";
        if (isExistAccessContextOnThread()) {
            throwAccessContextNoValueException(methodName, "AccessProcess", "process");
        } else {
            throwAccessContextNotFoundException(methodName);
        }
        return null; // unreachable
    }

    /**
     * Get access module on thread.
     * @return Access module. (NotNull)
     * @exception IllegalStateException When it couldn't get access module.
     */
    public static String getAccessModuleOnThread() {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final String accessModule = userContextOnThread.getAccessModule();
            if (accessModule != null) {
                return accessModule;
            }
        }
        String methodName = "getAccessModuleOnThread()";
        if (isExistAccessContextOnThread()) {
            throwAccessContextNoValueException(methodName, "AccessModule", "module");
        } else {
            throwAccessContextNotFoundException(methodName);
        }
        return null; // unreachable
    }

    /**
     * Get access value on thread.
     * @param key Key. (NotNull)
     * @return Access value. (NullAllowed: If the key has null value, it returns null)
     * @exception IllegalStateException When it couldn't get access value.
     */
    public static Object getAccessValueOnThread(String key) {
        if (isExistAccessContextOnThread()) {
            final AccessContext userContextOnThread = getAccessContextOnThread();
            final Map<String, Object> accessValueMap = userContextOnThread.getAccessValueMap();
            if (accessValueMap != null) {
                return accessValueMap.get(key);
            }
        }
        String methodName = "getAccessValueOnThread(\"" + key + "\")";
        if (isExistAccessContextOnThread()) {
            throwAccessContextNoValueException(methodName, "AccessValue", "value");
        } else {
            throwAccessContextNotFoundException(methodName);
        }
        return null; // unreachable
    }

    protected static void throwAccessContextNotFoundException(String methodName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The access context was not found on thread!" + ln();
        msg = msg + "{When you used AccessContext." + methodName + "}" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please set up the access context before DB access(using common column auto set-up)." + ln();
        msg = msg + "You should set up it at your application's interceptor or filter." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    try {" + ln();
        msg = msg + "        AccessContext context = new AccessContext();" + ln();
        msg = msg + "        context.setAccessTimestamp(accessTimestamp);" + ln();
        msg = msg + "        context.setAccessUser(accessUser);" + ln();
        msg = msg + "        context.setAccessProcess(accessProcess);" + ln();
        msg = msg + "        AccessContext.setAccessContextOnThread(context);" + ln();
        msg = msg + "        return invocation.proceed();" + ln();
        msg = msg + "    } finally {" + ln();
        msg = msg + "        AccessContext.clearAccessContextOnThread();" + ln();
        msg = msg + "    }" + ln();
        msg = msg + "* * * * * * * * * */";
        throw new AccessContextNotFoundException(msg);
    }

    protected static void throwAccessContextNoValueException(String methodName, String capPropName, String aliasName) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "Failed to get the access " + aliasName + " in access context on thread!" + ln();
        msg = msg + "{When you used AccessContext." + methodName + "}" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please set up the value before DB access(using common column auto set-up)." + ln();
        msg = msg + "You should set up it at your application's interceptor or filter." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    AccessContext context = new AccessContext();" + ln();
        msg = msg + "    context.set" + capPropName + "(...);" + ln();
        msg = msg + "    AccessContext.setAccessContextOnThread(context);" + ln();
        msg = msg + "* * * * * * * * * */";
        throw new AccessContextNoValueException(msg);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Date _accessDate;
    protected AccessDateProvider _accessDateProvider;
    protected Timestamp _accessTimestamp;
    protected AccessTimestampProvider _accessTimestampProvider;
    protected String _accessUser;
    protected String _accessProcess;
    protected String _accessModule;
    protected Map<String, Object> _accessValueMap;

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + _accessDate + ", " + _accessTimestamp + ", " + _accessUser + ", " + _accessProcess + ", "
                + _accessModule + ", " + _accessValueMap + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Date getAccessDate() {
        return _accessDate;
    }

    public void setAccessDate(Date accessDate) {
        this._accessDate = accessDate;
    }

    public AccessDateProvider getAccessDateProvider() {
        return _accessDateProvider;
    }

    public void setAccessDateProvider(AccessDateProvider accessDateProvider) {
        this._accessDateProvider = accessDateProvider;
    }

    public Timestamp getAccessTimestamp() {
        return _accessTimestamp;
    }

    public void setAccessTimestamp(Timestamp accessTimestamp) {
        this._accessTimestamp = accessTimestamp;
    }

    public AccessTimestampProvider getAccessTimestampProvider() {
        return _accessTimestampProvider;
    }

    public void setAccessTimestampProvider(AccessTimestampProvider accessTimestampProvider) {
        this._accessTimestampProvider = accessTimestampProvider;
    }

    public String getAccessUser() {
        return _accessUser;
    }

    public void setAccessUser(String accessUser) {
        this._accessUser = accessUser;
    }

    public String getAccessProcess() {
        return _accessProcess;
    }

    public void setAccessProcess(String accessProcess) {
        this._accessProcess = accessProcess;
    }

    public String getAccessModule() {
        return _accessModule;
    }

    public void setAccessModule(String accessModule) {
        this._accessModule = accessModule;
    }

    public Map<String, Object> getAccessValueMap() {
        return _accessValueMap;
    }

    public void registerAccessValue(String key, Object value) {
        if (_accessValueMap == null) {
            _accessValueMap = new HashMap<String, Object>();
        }
        _accessValueMap.put(key, value);
    }

    // ===================================================================================
    //                                                                  Provider Interface
    //                                                                  ==================
    /**
     * The provider interface of access date.
     */
    public static interface AccessDateProvider {

        /**
         * Get access date.
         * @return Access date. (NotNull)
         */
        public Date getAccessDate();
    }

    /**
     * The provider interface of access date.
     */
    public static interface AccessTimestampProvider {

        /**
         * Get access timestamp.
         * @return Access timestamp. (NotNull)
         */
        public Timestamp getAccessTimestamp();
    }
}
