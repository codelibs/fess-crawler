package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.SubQuery;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public interface HpSSQSetupper<CB extends ConditionBean> {
    void setup(String function, SubQuery<CB> subQuery);
}
