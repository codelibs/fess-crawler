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
 * The interface of or-query.
 * <pre>
 * ex) OrScopeQuery
 * cb.orScopeQuery(new OrQuery&lt;FooCB&gt;() {
 *     public void query(FooCB orCB) {
 *         orCB.query().setFoo...
 *         orCB.query().setBar...
 *     }
 * }
 * </pre>
 * @author jflute
 * @param <OR_CB> The type of condition-bean for or-query.
 */
public interface OrQuery<OR_CB extends ConditionBean> {

    /**
     * Set up your query condition for or-query. <br />
     * Don't call the method 'setupSelect_Xxx()' and 'addOrderBy_Xxx...()'
     * and they are ignored if you call.
     * @param orCB The condition-bean for or-query. (NotNull)
     */
    void query(OR_CB orCB);
}
