package org.seasar.robot.dbflute.cbean.sqlclause;

import java.util.HashSet;
import java.util.Set;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.1 (2009/02/08 Sunday)
 */
public class SqlClauseOracleTest extends PlainTestCase {

    public void test_lockForUpdateNoWait() {
        // ## Arrange ##
        final Set<String> markSet = new HashSet<String>();
        SqlClauseOracle target = new SqlClauseOracle("test") {
            @Override
            public SqlClause lockForUpdate() {
                _lockSqlSuffix = " for update of dummy";
                markSet.add("lockForUpdate");
                return this;
            }
        };

        // ## Act ##
        target.lockForUpdateNoWait();

        // ## Assert ##
        log(target._lockSqlSuffix);
        assertTrue(target._lockSqlSuffix.endsWith(" nowait"));
        assertTrue(markSet.contains("lockForUpdate"));

        // Should be overridden lockSqlSuffix.
        target.lockForUpdateWait(123);
        log(target._lockSqlSuffix);
        assertTrue(target._lockSqlSuffix.endsWith(" wait 123"));
    }

    public void test_lockForUpdateWait() {
        // ## Arrange ##
        final Set<String> markSet = new HashSet<String>();
        SqlClauseOracle target = new SqlClauseOracle("test") {
            @Override
            public SqlClause lockForUpdate() {
                _lockSqlSuffix = " for update of dummy";
                markSet.add("lockForUpdate");
                return this;
            }
        };

        // ## Act ##
        target.lockForUpdateWait(123);

        // ## Assert ##
        log(target._lockSqlSuffix);
        assertTrue(target._lockSqlSuffix.endsWith(" wait 123"));
        assertTrue(markSet.contains("lockForUpdate"));

        // Should be overridden lockSqlSuffix.
        target.lockForUpdateNoWait();
        log(target._lockSqlSuffix);
        assertTrue(target._lockSqlSuffix.endsWith(" nowait"));
    }

    public void test_escapeFullTextSearchValue() {
        // ## Arrange ##
        SqlClauseOracle target = new SqlClauseOracle("test");

        // ## Act & Assert ##
        assertEquals("{foo}", target.escapeFullTextSearchValue("foo"));
        assertEquals("{f{oo}", target.escapeFullTextSearchValue("f{oo"));
        assertEquals("{fo}}o}", target.escapeFullTextSearchValue("fo}o"));
        assertEquals("{f{o}}o}", target.escapeFullTextSearchValue("f{o}o"));
    }
}
