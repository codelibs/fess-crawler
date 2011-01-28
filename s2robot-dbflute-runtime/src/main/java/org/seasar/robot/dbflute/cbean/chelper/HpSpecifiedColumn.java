package org.seasar.robot.dbflute.cbean.chelper;

import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;

/**
 * @author jflute
 */
public class HpSpecifiedColumn {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _tableAliasName;
    protected final ColumnInfo _columnInfo;
    protected HpSpecifiedColumn _mappedSpecifiedColumn;
    protected String _mappedDerivedAlias;
    protected String _onQueryName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpSpecifiedColumn(String tableAliasName, ColumnInfo columnInfo) {
        _tableAliasName = tableAliasName;
        _columnInfo = columnInfo;
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    public void mappedFrom(HpSpecifiedColumn mappedSpecifiedInfo) {
        _mappedSpecifiedColumn = mappedSpecifiedInfo;
    }

    public void mappedFromDerived(String mappedDerivedAlias) {
        _mappedDerivedAlias = mappedDerivedAlias;
    }

    public String getValidMappedOnQueryName() {
        if (_mappedSpecifiedColumn != null) {
            return _mappedSpecifiedColumn.getOnQueryName();
        } else if (_mappedDerivedAlias != null) {
            return _mappedDerivedAlias;
        } else {
            return null;
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "{" + _tableAliasName + ", " + _columnInfo + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTableAliasName() {
        return _tableAliasName;
    }

    public ColumnInfo getColumnInfo() {
        return _columnInfo;
    }

    public HpSpecifiedColumn getMappedSpecifiedInfo() {
        return _mappedSpecifiedColumn;
    }

    public String getMappedAliasName() {
        return _mappedDerivedAlias;
    }

    public String getOnQueryName() {
        return _onQueryName;
    }

    public void setOnQueryName(String onQueryName) {
        this._onQueryName = onQueryName;
    }
}
