package org.seasar.robot.dbflute.cbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class SimpleMapPmbTest extends PlainTestCase {

    public void test_size() {
        // ## Arrange ##
        SimpleMapPmb<Object> pmb = createTarget();
        pmb.addParameter("foo", "value");

        // ## Act ##
        int size = pmb.size();

        // ## Assert ##
        assertEquals(1, size);
    }

    public void test_isEmpty() {
        // ## Arrange ##
        SimpleMapPmb<Object> pmb = createTarget();
        assertTrue(pmb.isEmpty());
        pmb.addParameter("foo", "value");

        // ## Act ##
        boolean empty = pmb.isEmpty();

        // ## Assert ##
        assertFalse(empty);
    }

    public void test_values() {
        // ## Arrange ##
        SimpleMapPmb<Object> pmb = createTarget();
        assertTrue(pmb.isEmpty());
        pmb.addParameter("foo", "value1");
        pmb.addParameter("bar", "value2");

        // ## Act ##
        Collection<Object> values = pmb.values();

        // ## Assert ##
        assertEquals(2, values.size());
        List<Object> ls = new ArrayList<Object>(values);
        assertEquals("value1", ls.get(0));
        assertEquals("value2", ls.get(1));
    }

    protected SimpleMapPmb<Object> createTarget() {
        return new SimpleMapPmb<Object>();
    }
}
