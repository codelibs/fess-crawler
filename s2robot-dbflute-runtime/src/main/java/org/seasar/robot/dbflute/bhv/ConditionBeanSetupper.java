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
package org.seasar.robot.dbflute.bhv;

import org.seasar.robot.dbflute.cbean.ConditionBean;

/**
 * The set-upper of condition-bean.
 * @param <CONDITION_BEAN> The type of condition-bean.
 * @author jflute
 */
public interface ConditionBeanSetupper<CONDITION_BEAN extends ConditionBean> {

    /**
     * Set up condition-bean.
     * @param cb Condition-bean. (NotNull)
     */
    public void setup(CONDITION_BEAN cb);
}
