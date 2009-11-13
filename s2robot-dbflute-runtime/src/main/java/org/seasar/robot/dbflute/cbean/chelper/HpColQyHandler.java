package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.SpecifyQuery;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public interface HpColQyHandler<CB extends ConditionBean> {
    void handle(SpecifyQuery<CB> rightSp, String operand);
}
