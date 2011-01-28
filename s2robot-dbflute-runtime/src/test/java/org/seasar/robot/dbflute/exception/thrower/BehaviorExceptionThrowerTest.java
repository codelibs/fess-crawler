package org.seasar.robot.dbflute.exception.thrower;

import org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.robot.dbflute.exception.EntityDuplicatedException;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class BehaviorExceptionThrowerTest extends PlainTestCase {

    public void test_throwEntityAlreadyDeletedException() {
        try {
            createTarget().throwSelectEntityAlreadyDeletedException("foo");

            fail();
        } catch (EntityAlreadyDeletedException e) {
            // OK
            log(e.getMessage());
            assertTrue(e.getMessage().contains("foo"));
        }
    }

    public void test_throwEntityDuplicatedException() {
        try {
            createTarget().throwSelectEntityDuplicatedException("123", "foo", new Exception());

            fail();
        } catch (EntityDuplicatedException e) {
            // OK
            log(e.getMessage());
            assertTrue(e.getMessage().contains("123"));
            assertTrue(e.getMessage().contains("foo"));
        }
    }

    protected BehaviorExceptionThrower createTarget() {
        return new BehaviorExceptionThrower();
    }
}
