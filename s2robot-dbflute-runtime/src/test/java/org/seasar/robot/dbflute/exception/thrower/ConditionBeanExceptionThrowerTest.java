package org.seasar.robot.dbflute.exception.thrower;

import org.seasar.robot.dbflute.exception.SetupSelectAfterUnionException;
import org.seasar.robot.dbflute.mock.MockConditionBean;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class ConditionBeanExceptionThrowerTest extends PlainTestCase {

    public void test_throwSetupSelectAfterUnionException() {
        // ## Arrange ##
        MockConditionBean mock = new MockConditionBean();
        String foreignPropertyName = "bar";

        // ## Act ##
        try {
            createTarget().throwSetupSelectAfterUnionException(mock, foreignPropertyName);

            // ## Assert ##
            fail();
        } catch (SetupSelectAfterUnionException e) {
            // OK
            log(e.getMessage());
        }
    }

    protected ConditionBeanExceptionThrower createTarget() {
        return new ConditionBeanExceptionThrower();
    }
}
