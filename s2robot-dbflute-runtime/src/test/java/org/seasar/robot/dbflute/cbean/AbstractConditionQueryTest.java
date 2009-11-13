package org.seasar.robot.dbflute.cbean;

import org.seasar.robot.dbflute.cbean.ckey.ConditionKey;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/08 Wednesday)
 */
public class AbstractConditionQueryTest extends PlainTestCase {

    public void test_isConditionKeyInScope() throws Exception {
        // ## Arrange & Act & Assert ##
        assertTrue(AbstractConditionQuery.isConditionKeyInScope(ConditionKey.CK_IN_SCOPE));
        assertFalse(AbstractConditionQuery.isConditionKeyInScope(ConditionKey.CK_NOT_IN_SCOPE));
    }
}
