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
package org.seasar.robot.dbflute.helper.beans.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.impl.DfBeanDescImpl;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class DfBeanDescFactory {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    private static final Map<Class<?>, DfBeanDesc> beanDescCache = new ConcurrentHashMap<Class<?>, DfBeanDesc>(1024);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected DfBeanDescFactory() {
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    public static DfBeanDesc getBeanDesc(Class<?> clazz) {
        DfBeanDesc beanDesc = beanDescCache.get(clazz);
        if (beanDesc == null) {
            beanDesc = new DfBeanDescImpl(clazz);
            beanDescCache.put(clazz, beanDesc);
        }
        return beanDesc;
    }

    public static void clear() {
        beanDescCache.clear();
    }
}
