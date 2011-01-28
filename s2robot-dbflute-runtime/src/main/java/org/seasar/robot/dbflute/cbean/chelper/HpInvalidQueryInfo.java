package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.util.DfTypeUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 */
public class HpInvalidQueryInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _locationBase;
    protected final ColumnInfo _targetColumn;
    protected final ConditionKey _conditionKey;
    protected final Object _invalidValue;
    protected boolean _inlineView;
    protected boolean _onClause;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpInvalidQueryInfo(String locationBase, ColumnInfo targetColumn, ConditionKey conditionKey,
            Object invalidValue) {
        assertObjectNotNull("locationBase", locationBase);
        assertObjectNotNull("targetColumn", targetColumn);
        assertObjectNotNull("conditionKey", conditionKey);
        _locationBase = locationBase;
        _targetColumn = targetColumn;
        _conditionKey = conditionKey;
        _invalidValue = invalidValue;
    }

    public HpInvalidQueryInfo inlineView() {
        this._inlineView = true;
        return this;
    }

    public HpInvalidQueryInfo onClause() {
        this._onClause = true;
        return this;
    }

    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                             Display
    //                                                                             =======
    public String buildDisplay() {
        final StringBuilder sb = new StringBuilder();
        final String tableDbName = _targetColumn.getDBMeta().getTableDbName();
        final String columnDbName = _targetColumn.getColumnDbName();
        sb.append(tableDbName).append(".").append(columnDbName);
        sb.append(" ").append(_conditionKey.getConditionKey());
        sb.append(" {value=").append(_invalidValue).append("}");
        sb.append(" : ").append(buildLocationDisp());
        if (_inlineView) {
            sb.append("(").append("inlineView").append(")");
        } else if (_onClause) {
            sb.append("(").append("onClause").append(")");
        }
        return sb.toString();
    }

    protected String buildLocationDisp() {
        // you should throw an exception if specification of locationBase changes
        String locationExp = Srl.replace(_locationBase, ".", "().");
        locationExp = Srl.replace(locationExp, "conditionQuery()", "query()");
        locationExp = Srl.replace(locationExp, ".conditionQuery", ".query");
        locationExp = Srl.rtrim(locationExp, ".");
        return locationExp;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + buildDisplay() + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getLocationBase() {
        return _locationBase;
    }

    public ColumnInfo getTargetColumn() {
        return _targetColumn;
    }

    public ConditionKey getConditionKey() {
        return _conditionKey;
    }

    public Object getInvalidValue() {
        return _invalidValue;
    }

    public boolean isInlineView() {
        return _inlineView;
    }

    public boolean isOnClause() {
        return _onClause;
    }
}
