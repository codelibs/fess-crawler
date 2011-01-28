package org.seasar.robot.dbflute.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/26 Friday)
 */
public class StringSetTest extends PlainTestCase {

    // ===================================================================================
    //                                                                    Case Insensitive
    //                                                                    ================
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

    public void test_createAsCaseInsensitive_plainValue_kept() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsCaseInsensitive();

        // ## Act ##
        set.add("AaA");
        set.add("Bbb");
        set.add("C_cc");

        // ## Assert ##
        List<String> list = new ArrayList<String>(set);
        log(list);
        assertTrue(list.contains("AaA"));
        assertTrue(list.contains("Bbb"));
        assertTrue(list.contains("C_cc"));
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

    public void test_createAsCaseInsensitiveConcurrent_plainValue_notKept() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsCaseInsensitiveConcurrent();

        // ## Act ##
        set.add("AaA");
        set.add("Bbb");
        set.add("C_cc");

        // ## Assert ##
        List<String> list = new ArrayList<String>(set);
        log(list);
        assertTrue(list.contains("aaa"));
        assertTrue(list.contains("bbb"));
        assertTrue(list.contains("c_cc"));
    }

    public void test_createAsCaseInsensitiveOrdered() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsCaseInsensitiveOrdered();

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

    public void test_createAsCaseInsensitiveOrdered_plainValue_kept() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsCaseInsensitiveOrdered();

        // ## Act ##
        set.add("AaA");
        set.add("Bbb");
        set.add("C_cc");

        // ## Assert ##
        List<String> list = new ArrayList<String>(set);
        log(list);
        assertEquals("AaA", list.get(0));
        assertEquals("Bbb", list.get(1));
        assertEquals("C_cc", list.get(2));
    }

    // ===================================================================================
    //                                                                            Flexible
    //                                                                            ========
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

    public void test_createAsFlexible_plainValue_kept() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsFlexible();

        // ## Act ##
        set.add("AaA");
        set.add("Bbb");
        set.add("C_cc");

        // ## Assert ##
        List<String> list = new ArrayList<String>(set);
        log(list);
        assertTrue(list.contains("AaA"));
        assertTrue(list.contains("Bbb"));
        assertTrue(list.contains("C_cc"));
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

    public void test_createAsFlexibleConcurrent_plainValue_notKept() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsFlexibleConcurrent();

        // ## Act ##
        set.add("AaA");
        set.add("Bbb");
        set.add("C_cc");

        // ## Assert ##
        List<String> list = new ArrayList<String>(set);
        log(list);
        assertTrue(list.contains("aaa"));
        assertTrue(list.contains("bbb"));
        assertTrue(list.contains("ccc"));
    }

    public void test_createAsFlexibleOrdered() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsFlexibleOrdered();

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

    public void test_createAsFlexibleOrdered_plainValue_kept() throws Exception {
        // ## Arrange ##
        StringSet set = StringSet.createAsFlexibleOrdered();

        // ## Act ##
        set.add("AaA");
        set.add("Bbb");
        set.add("C_cc");

        // ## Assert ##
        List<String> list = new ArrayList<String>(set);
        log(list);
        assertEquals("AaA", list.get(0));
        assertEquals("Bbb", list.get(1));
        assertEquals("C_cc", list.get(2));
    }

    // ===================================================================================
    //                                                                    Original Utility
    //                                                                    ================
    public void test_equalsUnderCharOption_basic() throws Exception {
        // ## Arrange ##
        StringSet set1 = StringSet.createAsCaseInsensitive();
        set1.add("aaa");
        set1.add("bbb");
        set1.add("ccc");
        StringSet set2 = StringSet.createAsCaseInsensitive();
        set2.add("aaa");
        set2.add("ccc");
        set2.add("bbb");

        // ## Act ##
        assertTrue(set1.equalsUnderCharOption(set2));
        assertTrue(set2.equalsUnderCharOption(set1));
        set2.add("ddd");
        assertFalse(set1.equalsUnderCharOption(set2));
        assertFalse(set2.equalsUnderCharOption(set1));
        set1.add("DDD");
        assertTrue(set1.equalsUnderCharOption(set2));
        assertTrue(set2.equalsUnderCharOption(set1));
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public void test_equals_basic() throws Exception {
        // ## Arrange ##
        StringSet set1 = StringSet.createAsCaseInsensitive();
        set1.add("aaa");
        set1.add("bbb");
        set1.add("ccc");
        StringSet set2 = StringSet.createAsCaseInsensitive();
        set2.add("aaa");
        set2.add("ccc");
        set2.add("bbb");

        // ## Act & Assert ##
        assertEquals(set1, set2);
        set2.add("bbb");
        assertEquals(set1, set2);
        set2.add("bBB");
        assertEquals(set1, set2);
        set2.add("ddd");
        assertNotSame(set1, set2);
    }

    public void test_equals_diff() throws Exception {
        // ## Arrange ##
        StringSet set1 = StringSet.createAsCaseInsensitive();
        set1.add("aaa");
        set1.add("bbb");
        set1.add("ccc");
        StringSet set2 = StringSet.createAsCaseInsensitive();
        set2.add("AAA");
        set2.add("ccc");
        set2.add("bbb");

        // ## Act & Assert ##
        assertNotSame(set1, set2);
    }

    public void test_equals_with_normalSet() throws Exception {
        // ## Arrange ##
        StringSet set1 = StringSet.createAsCaseInsensitive();
        set1.add("aaa");
        set1.add("bbb");
        set1.add("ccc");
        Set<String> set2 = DfCollectionUtil.newHashSet();
        set2.add("aaa");
        set2.add("ccc");
        set2.add("bbb");

        // ## Act & Assert ##
        assertEquals(set1, set2);
        set2.add("bbb");
        assertEquals(set1, set2);
        set2.add("bBB");
        assertNotSame(set1, set2);
        set2.add("ddd");
        assertNotSame(set1, set2);
    }

    public void test_equals_with_normalMap() throws Exception {
        // ## Arrange ##
        StringSet set1 = StringSet.createAsCaseInsensitive();
        set1.add("aaa");
        set1.add("bbb");
        set1.add("ccc");
        Map<String, String> map2 = DfCollectionUtil.newHashMap();
        map2.put("aaa", "AAA");
        map2.put("bbb", "BBB");
        map2.put("ccc", "CCC");

        // ## Act & Assert ##
        assertNotSame(set1, map2);
    }
}
