package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.SpecifyQuery;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public interface HpColQyHandler<CB extends ConditionBean> {

    /**
     * @param rightSp The specification for right column. (NotNull)
     * @param operand The operand for column comparison. (NotNull)
     * @return The calculator for right column. (NotNull)
     */
    HpCalculator handle(SpecifyQuery<CB> rightSp, String operand);
}
