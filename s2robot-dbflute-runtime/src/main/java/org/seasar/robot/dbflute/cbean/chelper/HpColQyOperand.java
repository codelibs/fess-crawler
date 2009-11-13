package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.SpecifyQuery;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public class HpColQyOperand<CB extends ConditionBean> {
    protected HpColQyHandler<CB> _handler;

    public HpColQyOperand(HpColQyHandler<CB> handler) {
        _handler = handler;
    }

    /**
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void equal(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, "=");
    }

    /**
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void greaterThan(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, ">");
    }

    /**
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void greaterEqual(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, ">=");
    }

    /**
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void lessThan(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, "<");
    }

    /**
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     */
    public void lessEqual(SpecifyQuery<CB> rightSpecifyQuery) {
        _handler.handle(rightSpecifyQuery, "<=");
    }
}
