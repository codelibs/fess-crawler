package org.seasar.robot.dbflute;

import java.sql.Timestamp;
import java.util.Date;

import org.seasar.robot.dbflute.exception.AccessContextNoValueException;
import org.seasar.robot.dbflute.exception.AccessContextNotFoundException;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class AccessContextTest extends PlainTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        AccessContext.clearAccessContextOnThread();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        AccessContext.clearAccessContextOnThread();
    }

    public void test_getValue_whenAccessContextExists_Tx() throws Exception {
        // ## Arrange ##
        AccessContext accessContext = new AccessContext();
        accessContext.setAccessUser("accessUser");
        accessContext.setAccessProcess("accessProcess");
        accessContext.setAccessModule("accessModule");
        Date currentDate = currentDate();
        accessContext.setAccessDate(currentDate);
        Timestamp currentTimestamp = currentTimestamp();
        accessContext.setAccessTimestamp(currentTimestamp);
        accessContext.registerAccessValue("foo", "bar");
        AccessContext.setAccessContextOnThread(accessContext);

        // ## Act & Assert ##
        assertEquals("accessUser", AccessContext.getAccessUserOnThread());
        assertEquals("accessProcess", AccessContext.getAccessProcessOnThread());
        assertEquals("accessModule", AccessContext.getAccessModuleOnThread());
        assertNotNull(AccessContext.getAccessDateOnThread());
        assertNotNull(AccessContext.getAccessTimestampOnThread());
        assertEquals("bar", AccessContext.getAccessValueOnThread("foo"));
    }

    public void test_getValue_whenAccessContextNotFound_Tx() throws Exception {
        try {
            AccessContext.getAccessUserOnThread();
            fail();
        } catch (AccessContextNotFoundException e) {
            // OK
            log(e.getMessage());
        }
        try {
            AccessContext.getAccessProcessOnThread();
            fail();
        } catch (AccessContextNotFoundException e) {
            // OK
            log(e.getMessage());
        }
        try {
            AccessContext.getAccessModuleOnThread();
            fail();
        } catch (AccessContextNotFoundException e) {
            // OK
            log(e.getMessage());
        }
        assertNotNull(AccessContext.getAccessDateOnThread());
        assertNotNull(AccessContext.getAccessTimestampOnThread());
        try {
            AccessContext.getAccessValueOnThread("foo");
            fail();
        } catch (AccessContextNotFoundException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_getValue_whenAccessContextEmpty_Tx() throws Exception {
        AccessContext.setAccessContextOnThread(new AccessContext());
        try {
            AccessContext.getAccessUserOnThread();
            fail();
        } catch (AccessContextNoValueException e) {
            // OK
            log(e.getMessage());
        }
        try {
            AccessContext.getAccessProcessOnThread();
            fail();
        } catch (AccessContextNoValueException e) {
            // OK
            log(e.getMessage());
        }
        try {
            AccessContext.getAccessModuleOnThread();
            fail();
        } catch (AccessContextNoValueException e) {
            // OK
            log(e.getMessage());
        }
        assertNotNull(AccessContext.getAccessDateOnThread());
        assertNotNull(AccessContext.getAccessTimestampOnThread());
        try {
            AccessContext.getAccessValueOnThread("foo");
            fail();
        } catch (AccessContextNoValueException e) {
            // OK
            log(e.getMessage());
        }
    }
}
