package org.seasar.robot.dbflute.cbean.sqlclause.select;

import org.seasar.robot.dbflute.dbmeta.name.ColumnSqlName;

/**
 * @author jflute
 */
public class SelectedRelationColumn {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableAliasName;
    protected String _columnDbName;
    protected ColumnSqlName _columnSqlName;
    protected String _columnAliasName;

    // ===================================================================================
    //                                                                              Naming
    //                                                                              ======
    public String buildRealColumnSqlName() {
        if (_tableAliasName != null) {
            return _tableAliasName + "." + _columnSqlName.toString();
        } else {
            return _columnSqlName.toString();
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getTableAliasName() {
        return _tableAliasName;
    }

    public void setTableAliasName(String tableAliasName) {
        this._tableAliasName = tableAliasName;
    }

    public String getColumnDbName() {
        return _columnDbName;
    }

    public void setColumnDbName(String columnName) {
        this._columnDbName = columnName;
    }

    public ColumnSqlName getColumnSqlName() {
        return _columnSqlName;
    }

    public void setColumnSqlName(ColumnSqlName columnSqlName) {
        this._columnSqlName = columnSqlName;
    }

    public String getColumnAliasName() {
        return _columnAliasName;
    }

    public void setColumnAliasName(String columnAliasName) {
        this._columnAliasName = columnAliasName;
    }
}
