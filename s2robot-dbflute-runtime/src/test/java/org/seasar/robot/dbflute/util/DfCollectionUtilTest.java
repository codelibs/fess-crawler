package org.seasar.robot.dbflute.util;

import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.4 (2009/03/20 Friday)
 */
public class DfCollectionUtilTest extends PlainTestCase {

    // ===================================================================================
    //                                                                                List
    //                                                                                ====
    public void test_newArrayList_dynamicArg() {
        // ## Arrange & Act ##
        List<String> list = DfCollectionUtil.newArrayList("foo", "bar");

        // ## Assert ##
        assertEquals(2, list.size());
        assertEquals("foo", list.get(0));
        assertEquals("bar", list.get(1));
    }

    public void test_newArrayList_collection() {
        // ## Arrange ##
        String[] array = new String[] { "foo", "bar" };

        // ## Act ##
        List<String> list = DfCollectionUtil.newArrayList(array);

        // ## Assert ##
        assertEquals(2, list.size());
        assertEquals("foo", list.get(0));
        assertEquals("bar", list.get(1));
    }

    public void test_newArrayList_stringArray() {
        // ## Arrange ##
        List<String> res = DfCollectionUtil.newArrayList("foo", "bar");

        // ## Act ##
        List<String> list = DfCollectionUtil.newArrayList(res);

        // ## Assert ##
        assertEquals(2, list.size());
        assertEquals("foo", list.get(0));
        assertEquals("bar", list.get(1));
    }

    public void test_splitByLimit_basic() {
        // ## Arrange ##
        List<String> value = new ArrayList<String>();
        value.add("1");
        value.add("2");
        value.add("3");
        value.add("4");
        value.add("5");
        value.add("6");
        value.add("7");

        // ## Act ##
        List<List<String>> actual = DfCollectionUtil.splitByLimit(value, 3);

        // ## Assert ##
        log(actual);
        assertEquals(3, actual.size());
        assertEquals(3, actual.get(0).size());
        assertEquals("1", actual.get(0).get(0));
        assertEquals("2", actual.get(0).get(1));
        assertEquals("3", actual.get(0).get(2));
        assertEquals(3, actual.get(1).size());
        assertEquals("4", actual.get(1).get(0));
        assertEquals("5", actual.get(1).get(1));
        assertEquals("6", actual.get(1).get(2));
        assertEquals(1, actual.get(2).size());
        assertEquals("7", actual.get(2).get(0));
    }

    public void test_splitByLimit_just() {
        // ## Arrange ##
        List<String> value = new ArrayList<String>();
        value.add("1");
        value.add("2");
        value.add("3");
        value.add("4");

        // ## Act ##
        List<List<String>> actual = DfCollectionUtil.splitByLimit(value, 4);

        // ## Assert ##
        log(actual);
        assertEquals(1, actual.size());
        assertEquals("1", actual.get(0).get(0));
        assertEquals("2", actual.get(0).get(1));
        assertEquals("3", actual.get(0).get(2));
        assertEquals("4", actual.get(0).get(3));
    }

    public void test_splitByLimit_justPlus() {
        // ## Arrange ##
        List<String> value = new ArrayList<String>();
        value.add("1");
        value.add("2");
        value.add("3");
        value.add("4");
        value.add("5");

        // ## Act ##
        List<List<String>> actual = DfCollectionUtil.splitByLimit(value, 4);

        // ## Assert ##
        log(actual);
        assertEquals(2, actual.size());
        assertEquals(4, actual.get(0).size());
        assertEquals("1", actual.get(0).get(0));
        assertEquals("2", actual.get(0).get(1));
        assertEquals("3", actual.get(0).get(2));
        assertEquals("4", actual.get(0).get(3));
        assertEquals(1, actual.get(1).size());
        assertEquals("5", actual.get(1).get(0));
    }

    public void test_splitByLimit_secondJust() {
        // ## Arrange ##
        List<String> value = new ArrayList<String>();
        value.add("1");
        value.add("2");
        value.add("3");
        value.add("4");
        value.add("5");
        value.add("6");
        value.add("7");
        value.add("8");

        // ## Act ##
        List<List<String>> actual = DfCollectionUtil.splitByLimit(value, 4);

        // ## Assert ##
        log(actual);
        assertEquals(2, actual.size());
        assertEquals(4, actual.get(0).size());
        assertEquals("1", actual.get(0).get(0));
        assertEquals("2", actual.get(0).get(1));
        assertEquals("3", actual.get(0).get(2));
        assertEquals("4", actual.get(0).get(3));
        assertEquals(4, actual.get(1).size());
        assertEquals("5", actual.get(1).get(0));
        assertEquals("6", actual.get(1).get(1));
        assertEquals("7", actual.get(1).get(2));
        assertEquals("8", actual.get(1).get(3));
    }

    public void test_splitByLimit_thirdJust() {
        // ## Arrange ##
        List<String> value = new ArrayList<String>();
        value.add("1");
        value.add("2");
        value.add("3");
        value.add("4");
        value.add("5");
        value.add("6");
        value.add("7");
        value.add("8");
        value.add("9");
        value.add("10");
        value.add("11");
        value.add("12");

        // ## Act ##
        List<List<String>> actual = DfCollectionUtil.splitByLimit(value, 4);

        // ## Assert ##
        assertEquals(3, actual.size());
        assertEquals(4, actual.get(0).size());
        assertEquals("1", actual.get(0).get(0));
        assertEquals("2", actual.get(0).get(1));
        assertEquals("3", actual.get(0).get(2));
        assertEquals("4", actual.get(0).get(3));
        assertEquals(4, actual.get(1).size());
        assertEquals("5", actual.get(1).get(0));
        assertEquals("6", actual.get(1).get(1));
        assertEquals("7", actual.get(1).get(2));
        assertEquals("8", actual.get(1).get(3));
        assertEquals(4, actual.get(2).size());
        assertEquals("9", actual.get(2).get(0));
        assertEquals("10", actual.get(2).get(1));
        assertEquals("11", actual.get(2).get(2));
        assertEquals("12", actual.get(2).get(3));
    }
}
