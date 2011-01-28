package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.SpecifyQuery;
import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public class HpColQyOperand<CB extends ConditionBean> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final HpColQyHandler<CB> _handler;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpColQyOperand(HpColQyHandler<CB> handler) {
        _handler = handler;
    }

    // ===================================================================================
    //                                                                          Comparison
    //                                                                          ==========
    /**
     * Equal(=).
     * <pre>
     * <span style="color: #3F7E5E">// where FOO = BAR</span>
     * cb.<span style="color: #FD4747">columnQuery</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFoo()</span>; <span style="color: #3F7E5E">// left column</span>
     *     }
     * }).<span style="color: #FD4747">equal</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnBar()</span>; <span style="color: #3F7E5E">// right column</span>
     *     }
     * }); <span style="color: #3F7E5E">// you can calculate for right column like '}).plus(3);'</span>
     * </pre>
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     * @return The calculator for right column. (NotNull)
     */
    public HpCalculator equal(SpecifyQuery<CB> rightSpecifyQuery) {
        return _handler.handle(rightSpecifyQuery, ConditionKey.CK_EQUAL.getOperand());
    }

    /**
     * NotEqual(&lt;&gt;).
     * <pre>
     * <span style="color: #3F7E5E">// where FOO &lt;&gt; BAR</span>
     * cb.<span style="color: #FD4747">columnQuery</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFoo()</span>; <span style="color: #3F7E5E">// left column</span>
     *     }
     * }).<span style="color: #FD4747">notEqual</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnBar()</span>; <span style="color: #3F7E5E">// right column</span>
     *     }
     * }); <span style="color: #3F7E5E">// you can calculate for right column like '}).plus(3);'</span>
     * </pre>
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     * @return The calculator for right column. (NotNull)
     */
    public HpCalculator notEqual(SpecifyQuery<CB> rightSpecifyQuery) {
        return _handler.handle(rightSpecifyQuery, ConditionKey.CK_NOT_EQUAL_STANDARD.getOperand());
    }

    /**
     * GreaterThan(&gt;).
     * <pre>
     * <span style="color: #3F7E5E">// where FOO &gt; BAR</span>
     * cb.<span style="color: #FD4747">columnQuery</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFoo()</span>; <span style="color: #3F7E5E">// left column</span>
     *     }
     * }).<span style="color: #FD4747">greaterThan</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnBar()</span>; <span style="color: #3F7E5E">// right column</span>
     *     }
     * }); <span style="color: #3F7E5E">// you can calculate for right column like '}).plus(3);'</span>
     * </pre>
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     * @return The calculator for right column. (NotNull)
     */
    public HpCalculator greaterThan(SpecifyQuery<CB> rightSpecifyQuery) {
        return _handler.handle(rightSpecifyQuery, ConditionKey.CK_GREATER_THAN.getOperand());
    }

    /**
     * LessThan(&lt;).
     * <pre>
     * <span style="color: #3F7E5E">// where FOO &lt; BAR</span>
     * cb.<span style="color: #FD4747">columnQuery</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFoo()</span>; <span style="color: #3F7E5E">// left column</span>
     *     }
     * }).<span style="color: #FD4747">lessThan</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnBar()</span>; <span style="color: #3F7E5E">// right column</span>
     *     }
     * }); <span style="color: #3F7E5E">// you can calculate for right column like '}).plus(3);'</span>
     * </pre>
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     * @return The calculator for right column. (NotNull)
     */
    public HpCalculator lessThan(SpecifyQuery<CB> rightSpecifyQuery) {
        return _handler.handle(rightSpecifyQuery, ConditionKey.CK_LESS_THAN.getOperand());
    }

    /**
     * GreaterEqual(&gt;=).
     * <pre>
     * <span style="color: #3F7E5E">// where FOO &gt;= BAR</span>
     * cb.<span style="color: #FD4747">columnQuery</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFoo()</span>; <span style="color: #3F7E5E">// left column</span>
     *     }
     * }).<span style="color: #FD4747">greaterEqual</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnBar()</span>; <span style="color: #3F7E5E">// right column</span>
     *     }
     * }); <span style="color: #3F7E5E">// you can calculate for right column like '}).plus(3);'</span>
     * </pre>
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     * @return The calculator for right column. (NotNull)
     */
    public HpCalculator greaterEqual(SpecifyQuery<CB> rightSpecifyQuery) {
        return _handler.handle(rightSpecifyQuery, ConditionKey.CK_GREATER_EQUAL.getOperand());
    }

    /**
     * LessThan(&lt;=).
     * <pre>
     * <span style="color: #3F7E5E">// where FOO &lt;= BAR</span>
     * cb.<span style="color: #FD4747">columnQuery</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnFoo()</span>; <span style="color: #3F7E5E">// left column</span>
     *     }
     * }).<span style="color: #FD4747">lessEqual</span>(new SpecifyQuery&lt;MemberCB&gt;() {
     *     public void query(MemberCB cb) {
     *         cb.specify().<span style="color: #FD4747">columnBar()</span>; <span style="color: #3F7E5E">// right column</span>
     *     }
     * }); <span style="color: #3F7E5E">// you can calculate for right column like '}).plus(3);'</span>
     * </pre>
     * @param rightSpecifyQuery The specify-query for right column. (NotNull)
     * @return The calculator for right column. (NotNull)
     */
    public HpCalculator lessEqual(SpecifyQuery<CB> rightSpecifyQuery) {
        return _handler.handle(rightSpecifyQuery, ConditionKey.CK_LESS_EQUAL.getOperand());
    }
}
