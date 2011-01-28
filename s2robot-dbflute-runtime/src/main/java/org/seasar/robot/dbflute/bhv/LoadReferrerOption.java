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

import java.util.List;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.cbean.ConditionBean;

/**
 * The class of load referrer option. <br />
 * This option is basically for loading second or more level referrer.
 * @param <REFERRER_CB> The type of referrer condition-bean.
 * @param <REFERRER_ENTITY> The type of referrer entity.
 * @author jflute
 */
public class LoadReferrerOption<REFERRER_CB extends ConditionBean, REFERRER_ENTITY extends Entity> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected ConditionBeanSetupper<REFERRER_CB> _conditionBeanSetupper;
    protected EntityListSetupper<REFERRER_ENTITY> _entityListSetupper;
    protected REFERRER_CB _referrerConditionBean;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor. <br />
     * This option is basically for loading second or more level referrer like this:
     * <pre>
     * <span style="color: #3F7E5E">// base point table is MEMBER_STATUS</span>
     * MemberStatusCB cb = new MemberStatusCB();
     * ListResultBean&lt;MemberStatus> memberStatusList = memberStatusBhv.selectList(cb);
     * 
     * LoadReferrerOption loadReferrerOption = new LoadReferrerOption();
     * 
     * <span style="color: #3F7E5E">// MEMBER (first level referrer)</span>
     * loadReferrerOption.setConditionBeanSetupper(new ConditionBeanSetupper&lt;MemberCB&gt;() {
     *     public void setup(MemberCB cb) {
     *         cb.query().addOrderBy_FormalizedDatetime_Desc();
     *     }
     * });
     * 
     * <span style="color: #3F7E5E">// PURCHASE (second level referrer)</span>
     * loadReferrerOption.<span style="color: #FD4747">setEntityListSetupper</span>(new EntityListSetupper&lt;Member&gt;() {
     *     public void setup(List&lt;Member&gt; entityList) {
     *         memberBhv.loadPurchaseList(entityList, new ConditionBeanSetupper&lt;PurchaseCB&gt;() {
     *             public void setup(PurchaseCB cb) {
     *                 cb.query().addOrderBy_PurchaseCount_Desc();
     *                 cb.query().addOrderBy_ProductId_Desc();
     *             }
     *         });
     *     }
     * });
     * 
     * memberStatusBhv.loadMemberList(memberStatusList, loadReferrerOption);
     * </pre>
     */
    public LoadReferrerOption() {
    }

    public LoadReferrerOption<REFERRER_CB, REFERRER_ENTITY> xinit(
            ConditionBeanSetupper<REFERRER_CB> conditionBeanSetupper) { // internal
        setConditionBeanSetupper(conditionBeanSetupper);
        return this;
    }

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public void delegateConditionBeanSettingUp(REFERRER_CB cb) { // internal
        if (_conditionBeanSetupper != null) {
            _conditionBeanSetupper.setup(cb);
        }
    }

    public void delegateEntitySettingUp(List<REFERRER_ENTITY> entityList) { // internal
        if (_entityListSetupper != null) {
            _entityListSetupper.setup(entityList);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public ConditionBeanSetupper<REFERRER_CB> getConditionBeanSetupper() {
        return _conditionBeanSetupper;
    }

    /**
     * Set the set-upper of condition-bean for a first level referrer. <br />
     * <pre>
     * LoadReferrerOption loadReferrerOption = new LoadReferrerOption();
     * 
     * <span style="color: #3F7E5E">// MEMBER (first level referrer)</span>
     * loadReferrerOption.<span style="color: #FD4747">setConditionBeanSetupper</span>(new ConditionBeanSetupper&lt;MemberCB&gt;() {
     *     public void setup(MemberCB cb) {
     *         cb.query().addOrderBy_FormalizedDatetime_Desc();
     *     }
     * });
     * ...
     * </pre>
     * @param conditionBeanSetupper The set-upper of condition-bean. (NullAllowed: if null, means no condition for a first level referrer)
     */
    public void setConditionBeanSetupper(ConditionBeanSetupper<REFERRER_CB> conditionBeanSetupper) {
        this._conditionBeanSetupper = conditionBeanSetupper;
    }

    public EntityListSetupper<REFERRER_ENTITY> getEntityListSetupper() {
        return _entityListSetupper;
    }

    /**
     * Set the set-upper of entity list for second or more level referrer. <br />
     * <pre>
     * LoadReferrerOption loadReferrerOption = new LoadReferrerOption();
     * ...
     * <span style="color: #3F7E5E">// PURCHASE (second level referrer)</span>
     * loadReferrerOption.<span style="color: #FD4747">setEntityListSetupper</span>(new EntityListSetupper&lt;Member&gt;() {
     *     public void setup(List&lt;Member&gt; entityList) {
     *         memberBhv.loadPurchaseList(entityList, new ConditionBeanSetupper&lt;PurchaseCB&gt;() {
     *             public void setup(PurchaseCB cb) {
     *                 cb.query().addOrderBy_PurchaseCount_Desc();
     *                 cb.query().addOrderBy_ProductId_Desc();
     *             }
     *         });
     *     }
     * });
     * </pre>
     * @param entityListSetupper The set-upper of entity list. (NullAllowed: if null, means no loading for second level referrer)
     */
    public void setEntityListSetupper(EntityListSetupper<REFERRER_ENTITY> entityListSetupper) {
        this._entityListSetupper = entityListSetupper;
    }

    public REFERRER_CB getReferrerConditionBean() {
        return _referrerConditionBean;
    }

    /**
     * Set the original instance of condition-bean for first level referrer. <br />
     * use this, if you want to set the original instance.
     * @param referrerConditionBean The original instance of condition-bean. (NullAllowed: if null, means normal)
     */
    public void setReferrerConditionBean(REFERRER_CB referrerConditionBean) {
        this._referrerConditionBean = referrerConditionBean;
    }
}
