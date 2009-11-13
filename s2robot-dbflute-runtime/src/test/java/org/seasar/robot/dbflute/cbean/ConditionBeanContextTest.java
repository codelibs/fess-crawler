package org.seasar.robot.dbflute.cbean;

import org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.robot.dbflute.exception.EntityDuplicatedException;
import org.seasar.robot.dbflute.exception.SetupSelectAfterUnionException;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/09 Thursday)
 */
public class ConditionBeanContextTest extends PlainTestCase {

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
    // -----------------------------------------------------
    //                                                Entity
    //                                                ------
    public void test_throwEntityAlreadyDeletedException() {
        try {
            ConditionBeanContext.throwEntityAlreadyDeletedException("foo");
            fail();
        } catch (EntityAlreadyDeletedException e) {
            // OK
            log(e.getMessage());
            assertTrue(e.getMessage().contains("foo"));
        }
    }

    public void test_throwEntityDuplicatedException() {
        try {
            ConditionBeanContext.throwEntityDuplicatedException("123", "foo", new Exception());
            ;
            fail();
        } catch (EntityDuplicatedException e) {
            // OK
            log(e.getMessage());
            assertTrue(e.getMessage().contains("123"));
            assertTrue(e.getMessage().contains("foo"));
        }
    }

    // -----------------------------------------------------
    //                                         Set up Select
    //                                         -------------
    public void test_throwSetupSelectAfterUnionException() {
        try {
            ConditionBeanContext.throwSetupSelectAfterUnionException("foo", "bar", "dondon");
            ;
            fail();
        } catch (SetupSelectAfterUnionException e) {
            // OK
            log(e.getMessage());
            assertTrue(e.getMessage().contains("foo"));
            assertTrue(e.getMessage().contains("setupSelect_Bar()"));
            assertTrue(e.getMessage().contains("dondon"));
        }
    }
}
