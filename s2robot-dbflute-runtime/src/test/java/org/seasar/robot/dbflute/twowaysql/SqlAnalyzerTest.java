package org.seasar.robot.dbflute.twowaysql;

import org.seasar.robot.dbflute.twowaysql.node.SqlPartsNode;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/08 Wednesday)
 */
public class SqlAnalyzerTest extends PlainTestCase {

    public void test_createSqlNode() {
        // ## Arrange ##
        SqlAnalyzer analyzer = new SqlAnalyzer("foobar", false);

        // ## Act ##
        SqlPartsNode node = analyzer.createSqlPartsNode("foo");

        // ## Assert ##
        assertEquals("foo", node.getSqlParts());
    }

    // *detail tests for analyze() are moved to node tests
}
