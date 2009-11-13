package org.seasar.robot.dbflute.helper;

import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/26 Friday)
 */
public class StringSetTest extends PlainTestCase {

    public void test_createAsCaseInsensitive() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsCaseInsensitive();

        // ## Act ##
        set.add("aaa");
        set.add("bbb");
        set.add("ccc");

        // ## Assert ##
        assertTrue(set.contains("AaA"));
        assertTrue(set.contains("Bbb"));
        assertTrue(set.contains("CCC"));
        assertFalse(set.contains("Aa_A"));
        log(set);
    }

    public void test_createAsCaseInsensitiveConcurrent() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsCaseInsensitiveConcurrent();

        // ## Act ##
        set.add("aaa");
        set.add("bbb");
        set.add("ccc");

        // ## Assert ##
        assertTrue(set.contains("AaA"));
        assertTrue(set.contains("Bbb"));
        assertTrue(set.contains("CCC"));
        assertFalse(set.contains("Aa_A"));
        log(set);
    }

    public void test_createAsCaseInsensitiveOrder() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsCaseInsensitiveOrder();

        // ## Act ##
        set.add("aaa");
        set.add("bbb");
        set.add("ccc");

        // ## Assert ##
        assertTrue(set.contains("AaA"));
        assertTrue(set.contains("Bbb"));
        assertTrue(set.contains("CCC"));
        assertFalse(set.contains("Aa_A"));
        List<String> list = new ArrayList<String>(set);
        assertEquals("aaa", list.get(0));
        assertEquals("bbb", list.get(1));
        assertEquals("ccc", list.get(2));
        log(set);
    }

    public void test_createAsFlexible() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsFlexible();

        // ## Act ##
        set.add("aaa");
        set.add("bbb");
        set.add("ccc");

        // ## Assert ##
        assertTrue(set.contains("AaA"));
        assertTrue(set.contains("Bbb"));
        assertTrue(set.contains("CCC"));
        assertTrue(set.contains("Aa_A"));
        log(set);
    }

    public void test_createAsFlexibleConcurrent() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsFlexibleConcurrent();

        // ## Act ##
        set.add("aaa");
        set.add("bbb");
        set.add("ccc");

        // ## Assert ##
        assertTrue(set.contains("AaA"));
        assertTrue(set.contains("Bbb"));
        assertTrue(set.contains("CCC"));
        assertTrue(set.contains("Aa_A"));
        log(set);
    }

    public void test_createAsFlexibleOrder() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsFlexibleOrder();

        // ## Act ##
        set.add("aaa");
        set.add("bbb");
        set.add("ccc");

        // ## Assert ##
        assertTrue(set.contains("AaA"));
        assertTrue(set.contains("Bbb"));
        assertTrue(set.contains("CCC"));
        assertTrue(set.contains("Aa_A"));
        List<String> list = new ArrayList<String>(set);
        assertEquals("aaa", list.get(0));
        assertEquals("bbb", list.get(1));
        assertEquals("ccc", list.get(2));
        log(set);
    }
}
