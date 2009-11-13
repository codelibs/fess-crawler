package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 * @param <CQ> The type of condition-query.
 */
public abstract class HpAbstractSpecification<CQ extends ConditionQuery> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected ConditionBean _baseCB;
    protected HpSpQyCall<CQ> _qyCall;
    protected CQ _query;
    protected boolean _forDerivedReferrer;
    protected boolean _forScalarSelect;
    protected boolean _forScalarSubQuery;
    protected boolean _alreadySpecifyRequiredColumn;
    protected boolean _forGeneralOneSpecificaion;
    protected DBMetaProvider _dbmetaProvider;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param baseCB The condition-bean of base level. (NotNull)
     * @param qyCall The call-back for condition-query. (NotNull)
     * @param forDerivedReferrer Is this for derive referrer?
     * @param forScalarSelect Is this for scalar select?
     * @param forScalarSubQuery  Is this for scalar sub-query?
     * @param dbmetaProvider The provider of DB meta. (NotNull)
     */
    protected HpAbstractSpecification(ConditionBean baseCB, HpSpQyCall<CQ> qyCall, boolean forDerivedReferrer,
            boolean forScalarSelect, boolean forScalarSubQuery, DBMetaProvider dbmetaProvider) {
        _baseCB = baseCB;
        _qyCall = qyCall;
        _forDerivedReferrer = forDerivedReferrer;
        _forScalarSelect = forScalarSelect;
        _forScalarSubQuery = forScalarSubQuery;
        _dbmetaProvider = dbmetaProvider;
    }

    // ===================================================================================
    //                                                                Column Specification
    //                                                                ====================
    protected void doColumn(String columnName) {
        assertColumn(columnName);
        if (_query == null) {
            _query = _qyCall.qy();
        }
        if (isRequiredColumnSpecificationEnabled()) {
            _alreadySpecifyRequiredColumn = true;
            doSpecifyRequiredColumn();
        }
        String relationPath = _query.getRelationPath() != null ? _query.getRelationPath() : "";
        final String tableAliasName;
        if (_query.isBaseQuery(_query)) {
            tableAliasName = _baseCB.getSqlClause().getLocalTableAliasName();
        } else {
            tableAliasName = _baseCB.getSqlClause().resolveJoinAliasName(relationPath, _query.getNestLevel());
        }
        _baseCB.getSqlClause().specifySelectColumn(tableAliasName, columnName);
    }

    protected boolean isRequiredColumnSpecificationEnabled() {
        return !_forGeneralOneSpecificaion && !_forDerivedReferrer && !_forScalarSelect && !_forScalarSubQuery
                && !_alreadySpecifyRequiredColumn;
    }

    protected void assertColumn(String columnName) {
        if (_forGeneralOneSpecificaion || _forDerivedReferrer) {
            return;
        }
        if (_query == null && !_qyCall.has()) { // setupSelect check!
            throwSpecifyColumnNotSetupSelectColumnException(columnName);
        }
    }

    protected void assertForeign(String foreignPropertyName) {
        if (_forScalarSelect) {
            throwScalarSelectInvalidForeignSpecificationException(foreignPropertyName);
        }
        if (_forScalarSubQuery) {
            throwScalarSubQueryInvalidForeignSpecificationException(foreignPropertyName);
        }
    }

    protected abstract void doSpecifyRequiredColumn();

    protected abstract String getTableDbName();

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
    protected void throwSpecifyColumnNotSetupSelectColumnException(String columnName) {
        ConditionBeanContext.throwSpecifyColumnNotSetupSelectColumnException(_baseCB, getTableDbName(), columnName);
    }

    protected void throwDerivedReferrerInvalidForeignSpecificationException(String foreignPropertyName) {
        ConditionBeanContext.throwDerivedReferrerInvalidForeignSpecificationException(foreignPropertyName);
    }

    protected void throwScalarSelectInvalidForeignSpecificationException(String foreignPropertyName) {
        ConditionBeanContext.throwScalarSelectInvalidForeignSpecificationException(foreignPropertyName);
    }

    protected void throwScalarSubQueryInvalidForeignSpecificationException(String foreignPropertyName) {
        ConditionBeanContext.throwScalarSubQueryInvalidForeignSpecificationException(foreignPropertyName);
    }

    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }
}