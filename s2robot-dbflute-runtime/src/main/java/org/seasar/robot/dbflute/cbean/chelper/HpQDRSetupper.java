package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.SubQuery;
import org.seasar.robot.dbflute.cbean.coption.DerivedReferrerOption;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public interface HpQDRSetupper<CB extends ConditionBean> {

    void setup(String function, SubQuery<CB> subQuery, String operand, Object value, DerivedReferrerOption option);
}
