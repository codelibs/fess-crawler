package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.SubQuery;

/**
 * @author jflute
 * @param <CB> The type of condition-bean.
 */
public class HpSSQFunction<CB extends ConditionBean> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected HpSSQSetupper<CB> _setupper;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpSSQFunction(HpSSQSetupper<CB> setupper) {
        _setupper = setupper;
    }

    // ===================================================================================
    //                                                                            Function
    //                                                                            ========
    /**
     * Set up the sub query of myself for the scalar 'max'.
     * <pre>
     * cb.query().scalar_Equal().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull) 
     */
    public void max(SubQuery<CB> subQuery) {
        _setupper.setup("max", subQuery);
    }

    /**
     * Set up the sub query of myself for the scalar 'min'.
     * <pre>
     * cb.query().scalar_Equal().min(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull) 
     */
    public void min(SubQuery<CB> subQuery) {
        _setupper.setup("min", subQuery);
    }

    /**
     * Set up the sub query of myself for the scalar 'sum'.
     * <pre>
     * cb.query().scalar_Equal().sum(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull) 
     */
    public void sum(SubQuery<CB> subQuery) {
        _setupper.setup("sum", subQuery);
    }

    /**
     * Set up the sub query of myself for the scalar 'avg'.
     * <pre>
     * cb.query().scalar_Equal().avg(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * });
     * </pre> 
     * @param subQuery The sub query of myself. (NotNull) 
     */
    public void avg(SubQuery<CB> subQuery) {
        _setupper.setup("avg", subQuery);
    }
}
