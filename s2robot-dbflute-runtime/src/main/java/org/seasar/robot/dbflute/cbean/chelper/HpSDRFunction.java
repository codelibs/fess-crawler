package org.seasar.robot.dbflute.cbean.chelper;

import java.lang.reflect.Method;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.SubQuery;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.util.DfStringUtil;

/**
 * The function of specify derived-referrer.
 * @author jflute
 * @param <REFERRER_CB> The type of referrer condition-bean.
 * @param <LOCAL_CQ> The type of local condition-query.
 */
public class HpSDRFunction<REFERRER_CB extends ConditionBean, LOCAL_CQ extends ConditionQuery> {
    protected ConditionBean _baseCB;
    protected LOCAL_CQ _localCQ;
    protected HpSDRSetupper<REFERRER_CB, LOCAL_CQ> _querySetupper;
    protected DBMetaProvider _dbmetaProvider;

    public HpSDRFunction(ConditionBean baseCB, LOCAL_CQ localCQ, HpSDRSetupper<REFERRER_CB, LOCAL_CQ> querySetupper,
            DBMetaProvider dbmetaProvider) {
        _baseCB = baseCB;
        _localCQ = localCQ;
        _querySetupper = querySetupper;
        _dbmetaProvider = dbmetaProvider;
    }

    /**
     * Set up the sub query of referrer for the scalar 'count'.
     * <pre>
     * cb.specify().derivePurchaseList().count(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseId(); // *Point! (Basically PK)
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"PAID_PURCHASE_COUNT\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void count(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        assertAliasName(aliasName);
        _querySetupper.setup("count", subQuery, _localCQ, aliasName.trim());
    }

    /**
     * Set up the sub query of referrer for the scalar 'count(with distinct)'.
     * <pre>
     * cb.specify().derivePurchaseList().countDistinct(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnProductId(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"PAID_PURCHASE_PRODUCT_KIND_COUNT\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void countDistinct(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        assertAliasName(aliasName);
        _querySetupper.setup("count(distinct", subQuery, _localCQ, aliasName.trim());
    }

    /**
     * Set up the sub query of referrer for the scalar 'max'.
     * <pre>
     * cb.specify().derivePurchaseList().max(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"LATEST_PURCHASE_DATETIME\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void max(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        assertAliasName(aliasName);
        _querySetupper.setup("max", subQuery, _localCQ, aliasName.trim());
    }

    /**
     * Set up the sub query of referrer for the scalar 'min'.
     * <pre>
     * cb.specify().derivePurchaseList().min(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchaseDatetime(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"LATEST_PURCHASE_DATETIME\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void min(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        assertAliasName(aliasName);
        _querySetupper.setup("min", subQuery, _localCQ, aliasName.trim());
    }

    /**
     * Set up the sub query of referrer for the scalar 'sum'.
     * <pre>
     * cb.specify().derivePurchaseList().sum(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"SUMMARY_PURCHASE_PRICE\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void sum(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        assertAliasName(aliasName);
        _querySetupper.setup("sum", subQuery, _localCQ, aliasName.trim());
    }

    /**
     * Set up the sub query of referrer for the scalar 'avg'.
     * <pre>
     * cb.specify().derivePurchaseList().avg(new SubQuery&lt;PurchaseCB&gt;() {
     *     public void query(PurchaseCB subCB) {
     *         subCB.specify().columnPurchasePrice(); // *Point!
     *         subCB.query().setPaymentCompleteFlg_Equal_True();
     *     }
     * }, \"AVERAGE_PURCHASE_PRICE\");
     * </pre> 
     * @param subQuery The sub query of referrer. (NotNull)
     * @param aliasName The alias of the name. The property should exists on the entity. (NotNull)
     */
    public void avg(SubQuery<REFERRER_CB> subQuery, String aliasName) {
        assertAliasName(aliasName);
        _querySetupper.setup("avg", subQuery, _localCQ, aliasName.trim());
    }

    protected void assertAliasName(String aliasName) {
        if (aliasName == null || aliasName.trim().length() == 0) {
            throwSpecifyDerivedReferrerInvalidAliasNameException();
        }
        String tableDbName = _baseCB.getTableDbName();
        DBMeta dbmeta = _dbmetaProvider.provideDBMetaChecked(tableDbName);
        Method[] methods = dbmeta.getEntityType().getMethods();
        String targetMethodName = "set" + replaceString(aliasName, "_", "").toLowerCase();
        boolean existsSetterMethod = false;
        for (Method method : methods) {
            if (!method.getName().startsWith("set")) {
                continue;
            }
            if (targetMethodName.equals(method.getName().toLowerCase())) {
                existsSetterMethod = true;
                break;
            }
        }
        if (!existsSetterMethod) {
            throwSpecifyDerivedReferrerEntityPropertyNotFoundException(aliasName, dbmeta.getEntityType());
        }
    }

    protected void throwSpecifyDerivedReferrerInvalidAliasNameException() {
        ConditionBeanContext.throwSpecifyDerivedReferrerInvalidAliasNameException(_localCQ);
    }

    protected void throwSpecifyDerivedReferrerEntityPropertyNotFoundException(String aliasName, Class<?> entityType) {
        ConditionBeanContext.throwSpecifyDerivedReferrerEntityPropertyNotFoundException(aliasName, entityType);
    }

    protected String replaceString(String text, String fromText, String toText) {
        return DfStringUtil.replace(text, fromText, toText);
    }
}
