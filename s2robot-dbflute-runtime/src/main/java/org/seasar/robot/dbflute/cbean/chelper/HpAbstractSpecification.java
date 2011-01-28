package org.seasar.robot.dbflute.cbean.chelper;

import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.exception.thrower.ConditionBeanExceptionThrower;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 * @param <CQ> The type of condition-query.
 */
public abstract class HpAbstractSpecification<CQ extends ConditionQuery> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ConditionBean _baseCB;
    protected final HpSpQyCall<CQ> _qyCall;
    protected HpSpQyCall<CQ> _syncQyCall;
    protected final HpCBPurpose _purpose;
    protected final DBMetaProvider _dbmetaProvider;
    protected CQ _query; // lazy-loaded
    protected boolean _alreadySpecifiedRequiredColumn; // also means specification existence
    protected Map<String, HpSpecifiedColumn> _specifiedColumnMap; // saves specified columns (lazy-loaded)

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param baseCB The condition-bean of base level. (NotNull)
     * @param qyCall The call-back for condition-query. (NotNull)
     * @param purpose The purpose of condition-bean. (NotNull)
     * @param dbmetaProvider The provider of DB meta. (NotNull)
     */
    protected HpAbstractSpecification(ConditionBean baseCB, HpSpQyCall<CQ> qyCall, HpCBPurpose purpose,
            DBMetaProvider dbmetaProvider) {
        _baseCB = baseCB;
        _qyCall = qyCall;
        _purpose = purpose;
        _dbmetaProvider = dbmetaProvider;
    }

    // ===================================================================================
    //                                                                Column Specification
    //                                                                ====================
    protected HpSpecifiedColumn doColumn(String columnName) {
        if (isSpecifiedColumn(columnName)) {
            // returns the same instance as the specified before
            return getSpecifiedColumn(columnName);
        }
        assertColumn(columnName);
        if (_query == null) {
            _query = qyCall().qy();
        }
        if (isRequiredColumnSpecificationEnabled()) {
            _alreadySpecifiedRequiredColumn = true;
            doSpecifyRequiredColumn();
        }
        final String relationPath = _query.xgetRelationPath() != null ? _query.xgetRelationPath() : "";
        final SqlClause sqlClause = _baseCB.getSqlClause();
        final String tableAliasName;
        if (_query.isBaseQuery()) {
            tableAliasName = sqlClause.getBasePointAliasName();
        } else {
            tableAliasName = sqlClause.resolveJoinAliasName(relationPath, _query.xgetNestLevel());
        }
        final HpSpecifiedColumn specifiedColumn = createSpecifiedColumn(columnName, tableAliasName);
        sqlClause.specifySelectColumn(specifiedColumn);
        saveSpecifiedColumn(columnName, specifiedColumn);
        return specifiedColumn;
    }

    /**
     * Get the query call with sync. <br />
     * This method is basically for SpecifyColumn.
     * Don't set this (or call-back that uses this) to other objects.
     * @return The instance of query call. (NotNull)
     */
    protected HpSpQyCall<CQ> qyCall() { // basically for SpecifyColumn (NOT DerivedReferrer)
        return _syncQyCall != null ? _syncQyCall : _qyCall;
    }

    protected boolean isRequiredColumnSpecificationEnabled() {
        if (_alreadySpecifiedRequiredColumn) {
            return false;
        }
        return isNormalUse(); // only normal purpose needs
    }

    protected abstract void doSpecifyRequiredColumn();

    protected abstract String getTableDbName();

    protected HpSpecifiedColumn createSpecifiedColumn(String columnName, String tableAliasName) {
        final DBMeta dbmeta = _dbmetaProvider.provideDBMetaChecked(_query.getTableDbName());
        final ColumnInfo columnInfo = dbmeta.findColumnInfo(columnName);
        return new HpSpecifiedColumn(tableAliasName, columnInfo);
    }

    protected HpSpecifiedColumn getSpecifiedColumn(String columnName) {
        return _specifiedColumnMap != null ? _specifiedColumnMap.get(columnName) : null;
    }

    protected boolean isSpecifiedColumn(String columnName) {
        return _specifiedColumnMap != null && _specifiedColumnMap.containsKey(columnName);
    }

    protected void saveSpecifiedColumn(String columnName, HpSpecifiedColumn specifiedColumn) {
        if (_specifiedColumnMap == null) {
            _specifiedColumnMap = new LinkedHashMap<String, HpSpecifiedColumn>();
        }
        _specifiedColumnMap.put(columnName, specifiedColumn);
    }

    // ===================================================================================
    //                                                                      Purpose Assert
    //                                                                      ==============
    protected void assertColumn(String columnName) {
        if (_purpose.isNoSpecifyColumnTwoOrMore()) {
            if (_specifiedColumnMap != null && _specifiedColumnMap.size() > 0) {
                throwSpecifyColumnTwoOrMoreColumnException(columnName);
            }
            // no specification is checked at an other timing
        }
        if (_purpose.isNoSpecifyColumnWithDerivedReferrer()) {
            if (hasDerivedReferrer()) {
                throwSpecifyColumnWithDerivedReferrerException(columnName, null);
            }
        }
        if (isNormalUse()) { // only normal purpose needs
            if (_query == null && !qyCall().has()) { // setupSelect check!
                throwSpecifyColumnNotSetupSelectColumnException(columnName);
            }
        }
    }

    protected void assertRelation(String relationName) {
        if (_purpose.isNoSpecifyRelation()) {
            throwSpecifyRelationIllegalPurposeException(relationName);
        }
    }

    protected void assertDerived(String referrerName) {
        if (_purpose.isNoSpecifyDerivedReferrer()) {
            throwSpecifyDerivedReferrerIllegalPurposeException(referrerName);
        }
        if (_purpose.isNoSpecifyDerivedReferrerTwoOrMore()) {
            if (hasDerivedReferrer()) {
                throwSpecifyDerivedReferrerTwoOrMoreException(referrerName);
            }
        }
        if (_purpose.isNoSpecifyColumnWithDerivedReferrer()) {
            if (_specifiedColumnMap != null && _specifiedColumnMap.size() > 0) {
                throwSpecifyColumnWithDerivedReferrerException(null, referrerName);
            }
        }
    }

    protected boolean isNormalUse() {
        return HpCBPurpose.NORMAL_USE.equals(_purpose);
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isAlreadySpecifiedRequiredColumn() {
        return _alreadySpecifiedRequiredColumn;
    }

    protected boolean hasDerivedReferrer() {
        return !_baseCB.getSqlClause().getSpecifiedDerivingAliasList().isEmpty();
    }

    // ===================================================================================
    //                                                              Synchronization QyCall
    //                                                              ======================
    // synchronize Query(Relation)
    public HpSpQyCall<CQ> xsyncQyCall() {
        return _syncQyCall;
    }

    public void xsetSyncQyCall(HpSpQyCall<CQ> qyCall) {
        _syncQyCall = qyCall;
    }

    public boolean xhasSyncQyCall() {
        return _syncQyCall != null;
    }

    // ===================================================================================
    //                                                                  Exception Throwing
    //                                                                  ==================
    protected void throwSpecifyColumnTwoOrMoreColumnException(String columnName) {
        createCBExThrower().throwSpecifyColumnTwoOrMoreColumnException(_purpose, _baseCB, columnName);
    }

    protected void throwSpecifyColumnNotSetupSelectColumnException(String columnName) {
        createCBExThrower().throwSpecifyColumnNotSetupSelectColumnException(_baseCB, columnName);
    }

    protected void throwSpecifyColumnWithDerivedReferrerException(String columnName, String referrerName) {
        createCBExThrower().throwSpecifyColumnWithDerivedReferrerException(_purpose, _baseCB, columnName, referrerName);
    }

    protected void throwSpecifyRelationIllegalPurposeException(String relationName) {
        createCBExThrower().throwSpecifyRelationIllegalPurposeException(_purpose, _baseCB, relationName);
    }

    protected void throwSpecifyDerivedReferrerIllegalPurposeException(String referrerName) {
        createCBExThrower().throwSpecifyDerivedReferrerIllegalPurposeException(_purpose, _baseCB, referrerName);
    }

    protected void throwSpecifyDerivedReferrerTwoOrMoreException(String referrerName) {
        createCBExThrower().throwSpecifyDerivedReferrerTwoOrMoreException(_purpose, _baseCB, referrerName);
    }

    // ===================================================================================
    //                                                                    Exception Helper
    //                                                                    ================
    protected ConditionBeanExceptionThrower createCBExThrower() {
        return new ConditionBeanExceptionThrower();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}