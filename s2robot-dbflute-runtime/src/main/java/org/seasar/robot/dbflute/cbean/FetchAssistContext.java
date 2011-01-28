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
package org.seasar.robot.dbflute.cbean;

import org.seasar.robot.dbflute.jdbc.FetchBean;

/**
 * The context for fetch-assist.
 * @author jflute
 */
public class FetchAssistContext {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static ThreadLocal<FetchBean> _threadLocal = new ThreadLocal<FetchBean>();

    // ===================================================================================
    //                                                                          Fetch Bean
    //                                                                          ==========
    /**
     * Get fetch-bean on thread.
     * @return The instance of fetch-bean. (NullAllowed)
     */
    public static FetchBean getFetchBeanOnThread() {
        return _threadLocal.get();
    }

    /**
     * Set fetch-bean on thread.
     * @param fetchBean The instance of fetch-bean. (NotNull)
     */
    public static void setFetchBeanOnThread(FetchBean fetchBean) {
        if (fetchBean == null) {
            String msg = "The argument[fetchBean] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(fetchBean);
    }

    /**
     * Is existing fetch-bean on thread?
     * @return Determination.
     */
    public static boolean isExistFetchBeanOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear fetch-bean on thread.
     */
    public static void clearFetchBeanOnThread() {
        _threadLocal.set(null);
    }

    // ===================================================================================
    //                                                                Fetch Narrowing Bean
    //                                                                ====================
    /**
     * Get fetch-narrowing-bean on thread.
     * @return The instance of fetch-narrowing-bean. (NullAllowed)
     */
    public static FetchNarrowingBean getFetchNarrowingBeanOnThread() {
        if (isExistFetchBeanOnThread()) {
            final FetchBean bean = getFetchBeanOnThread();
            if (bean instanceof FetchNarrowingBean) {
                return (FetchNarrowingBean) bean;
            }
        }
        return null;
    }

    /**
     * Is existing fetch-narrowing-bean on thread?
     * @return Determination.
     */
    public static boolean isExistFetchNarrowingBeanOnThread() {
        return (getFetchNarrowingBeanOnThread() != null);
    }
}
