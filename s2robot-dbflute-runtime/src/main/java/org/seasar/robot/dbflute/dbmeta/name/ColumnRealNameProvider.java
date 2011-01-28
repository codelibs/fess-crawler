package org.seasar.robot.dbflute.dbmeta.name;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public interface ColumnRealNameProvider {

    ColumnRealName provide(String columnDbName);
}
