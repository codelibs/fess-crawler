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

import org.seasar.robot.dbflute.jdbc.SqlLogHandler;
import org.seasar.robot.dbflute.jdbc.SqlResultHandler;

/**
 * The context of callback in DBFlute internal logic.
 * @author jflute
 */
public class CallbackContext {

    // ===================================================================================
    //                                                                        Thread Local
    //                                                                        ============
    /** The thread-local for this. */
    private static final ThreadLocal<CallbackContext> _threadLocal = new ThreadLocal<CallbackContext>();

    /**
     * Get callback-context on thread.
     * @return The context of callback. (NullAllowed)
     */
    public static CallbackContext getCallbackContextOnThread() {
        return (CallbackContext) _threadLocal.get();
    }

    /**
     * Set callback-context on thread.
     * @param callbackContext The context of callback. (NotNull)
     */
    public static void setCallbackContextOnThread(CallbackContext callbackContext) {
        if (callbackContext == null) {
            String msg = "The argument[callbackContext] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(callbackContext);
    }

    /**
     * Is existing callback-context on thread?
     * @return Determination.
     */
    public static boolean isExistCallbackContextOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear callback-context on thread.
     */
    public static void clearCallbackContextOnThread() {
        _threadLocal.set(null);
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected SqlLogHandler _sqlLogHandler;
    protected SqlResultHandler _sqlResultHandler;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public SqlLogHandler getSqlLogHandler() {
        return _sqlLogHandler;
    }

    /**
     * Set the handler of SQL log. <br />
     * This handler is called back before executing the SQL. 
     * <pre>
     * context.setSqlLogHandler(new SqlLogHandler() {
     *     public void handle(String executedSql, String displaySql
     *                      , Object[] args, Class&lt;?&gt;[] argTypes) {
     *         // You can get your SQL string here.
     *     }
     * });
     * </pre>
     * @param sqlLogHandler The handler of SQL log. (NullAllowed)
     */
    public void setSqlLogHandler(SqlLogHandler sqlLogHandler) {
        this._sqlLogHandler = sqlLogHandler;
    }

    public SqlResultHandler getSqlResultHandler() {
        return _sqlResultHandler;
    }

    /**
     * Set the handler of SQL result. <br />
     * This handler is called back before executing the SQL. 
     * <pre>
     * context.setSqlResultHandler(new SqlResultHandler() {
     *     public void handle(SqlResultInfo info) {
     *         // You can get your SQL result information here.
     *     }
     * });
     * </pre>
     * @param sqlResultHandler The handler of SQL result. (NullAllowed)
     */
    public void setSqlResultHandler(SqlResultHandler sqlResultHandler) {
        this._sqlResultHandler = sqlResultHandler;
    }
}
