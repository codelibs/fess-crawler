package org.seasar.robot.dbflute.cbean.sqlclause.join;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.cbean.sqlclause.query.QueryClause;
import org.seasar.robot.dbflute.dbmeta.name.ColumnRealName;

/**
 * @author jflute
 */
public class LeftOuterJoinInfo implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _foreignAliasName;
    protected String _localTableDbName;
    protected String _foreignTableDbName;
    protected final List<QueryClause> _inlineWhereClauseList = new ArrayList<QueryClause>();
    protected final List<QueryClause> _additionalOnClauseList = new ArrayList<QueryClause>();
    protected Map<ColumnRealName, ColumnRealName> _joinOnMap;
    protected String _fixedCondition;
    protected transient FixedConditionResolver _fixedConditionResolver;
    protected boolean _innerJoin;

    // ===================================================================================
    //                                                                        Judge Status
    //                                                                        ============
    public boolean hasInlineOrOnClause() {
        return !_inlineWhereClauseList.isEmpty() || !_additionalOnClauseList.isEmpty();
    }

    public boolean hasFixedCondition() {
        return _fixedCondition != null && _fixedCondition.trim().length() > 0;
    }

    public void resolveFixedCondition() { // required before using fixed-condition
        if (hasFixedCondition() && _fixedConditionResolver != null) {
            _fixedCondition = _fixedConditionResolver.resolveVariable(_fixedCondition);
        }
    }

    public String resolveFixedInlineView(String foreignTableSqlName) {
        if (hasFixedCondition() && _fixedConditionResolver != null) {
            return _fixedConditionResolver.resolveFixedInlineView(foreignTableSqlName);
        }
        return foreignTableSqlName.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getForeignAliasName() {
        return _foreignAliasName;
    }

    public void setForeignAliasName(String foreignAliasName) {
        _foreignAliasName = foreignAliasName;
    }

    public String getLocalTableDbName() {
        return _localTableDbName;
    }

    public void setLocalTableDbName(String localTableDbName) {
        _localTableDbName = localTableDbName;
    }

    public String getForeignTableDbName() {
        return _foreignTableDbName;
    }

    public void setForeignTableDbName(String foreignTableDbName) {
        _foreignTableDbName = foreignTableDbName;
    }

    public List<QueryClause> getInlineWhereClauseList() {
        return _inlineWhereClauseList;
    }

    public void addInlineWhereClause(QueryClause inlineWhereClause) {
        _inlineWhereClauseList.add(inlineWhereClause);
    }

    public List<QueryClause> getAdditionalOnClauseList() {
        return _additionalOnClauseList;
    }

    public void addAdditionalOnClause(QueryClause additionalOnClause) {
        _additionalOnClauseList.add(additionalOnClause);
    }

    public Map<ColumnRealName, ColumnRealName> getJoinOnMap() {
        return _joinOnMap;
    }

    public void setJoinOnMap(Map<ColumnRealName, ColumnRealName> joinOnMap) {
        _joinOnMap = joinOnMap;
    }

    public String getFixedCondition() {
        return _fixedCondition;
    }

    public void setFixedCondition(String fixedCondition) {
        _fixedCondition = fixedCondition;
    }

    public FixedConditionResolver getFixedConditionResolver() {
        return _fixedConditionResolver;
    }

    public void setFixedConditionResolver(FixedConditionResolver fixedConditionResolver) {
        _fixedConditionResolver = fixedConditionResolver;
    }

    public boolean isInnerJoin() {
        return _innerJoin;
    }

    public void setInnerJoin(boolean innerJoin) {
        _innerJoin = innerJoin;
    }
}
