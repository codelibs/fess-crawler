package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.SubQuery;

/**
 * @author jflute
 * @param <REFERRER_CB> The type of referrer condition-bean.
 * @param <LOCAL_CQ> The type of local condition-query.
 */
public interface HpSDRSetupper<REFERRER_CB extends ConditionBean, LOCAL_CQ extends ConditionQuery> {
    void setup(String function, SubQuery<REFERRER_CB> subQuery, LOCAL_CQ cq, String aliasName);
}
