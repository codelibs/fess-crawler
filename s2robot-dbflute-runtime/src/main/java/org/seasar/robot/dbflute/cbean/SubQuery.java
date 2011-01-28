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

/**
 * The interface of sub-query.
 * <pre>
 * cb.query.existsBarList(new SubQuery&lt;BarCB&gt;() {
 *     public void query(BarCB subCB) {
 *         subCB.query().setBar...
 *     }
 * }
 * </pre>
 * @author jflute
 * @param <SUB_CB> The type of condition-bean for sub-query.
 */
public interface SubQuery<SUB_CB extends ConditionBean> {

    /**
     * Set up your query condition for sub-query. <br />
     * Don't call the method 'setupSelect_Xxx()' and 'addOrderBy_Xxx...()'
     * and they are ignored if you call.
     * @param subCB The condition-bean for sub-query. (NotNull)
     */
    void query(SUB_CB subCB);
}
