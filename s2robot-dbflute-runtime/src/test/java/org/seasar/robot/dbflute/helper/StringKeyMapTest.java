package org.seasar.robot.dbflute.helper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/20 Saturday)
 */
public class StringKeyMapTest extends PlainTestCase {

    public void test_put_null() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> mapAsFlexible = StringKeyMap.createAsFlexible();
        StringKeyMap<Object> mapAsFlexibleConcurrent = StringKeyMap.createAsFlexibleConcurrent();

        // ## Act ##
        mapAsFlexible.put("aaa", null);
        try {
            mapAsFlexibleConcurrent.put("aaa", null);
            fail();
        } catch (NullPointerException e) {
            // OK
        }

        // ## Assert ##
        assertEquals(null, mapAsFlexibleConcurrent.get("aaa"));
    }

    public void test_putAll() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitive();
        LinkedHashMap<String, Integer> resourceMap = new LinkedHashMap<String, Integer>();
        resourceMap.put("aaa", 1);
        resourceMap.put("bbb", 2);
        resourceMap.put("ccc", 3);

        // ## Act ##
        map.putAll(resourceMap);

        // ## Assert ##
        assertEquals(1, map.get("aaa"));
        assertEquals(2, map.get("bbb"));
        assertEquals(3, map.get("ccc"));
        assertEquals(3, map.size());
    }

    public void test_createAsCaseInsensitive() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitive();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(null, map.get("Aa_A"));
        log(map.keySet());
    }

    public void test_createAsCaseInsensitiveConcurrent() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitiveConcurrent();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(null, map.get("Aa_A"));
        log(map.keySet());
    }

    public void test_createAsCaseInsensitiveOrder() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsCaseInsensitiveOrder();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(null, map.get("Aa_A"));
        Set<String> keySet = map.keySet();
        List<String> list = new ArrayList<String>(keySet);
        assertEquals("aaa", list.get(0));
        assertEquals("bbb", list.get(1));
        assertEquals("ccc", list.get(2));
    }

    public void test_createAsFlexible() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexible();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(1, map.get("Aa_A"));
        log(map.keySet());
    }

    public void test_createAsFlexibleConcurrent() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexibleConcurrent();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(1, map.get("Aa_A"));
        log(map.keySet());
    }

    public void test_createAsFlexibleOrder() throws Exception {
        // ## Arrange ##
        StringKeyMap<Object> map = StringKeyMap.createAsFlexibleOrder();

        // ## Act ##
        map.put("aaa", 1);
        map.put("bbb", 2);
        map.put("ccc", 3);

        // ## Assert ##
        assertEquals(1, map.get("AaA"));
        assertEquals(2, map.get("Bbb"));
        assertEquals(3, map.get("CCC"));
        assertEquals(1, map.get("Aa_A"));
        Set<String> keySet = map.keySet();
        List<String> list = new ArrayList<String>(keySet);
        assertEquals("aaa", list.get(0));
        assertEquals("bbb", list.get(1));
        assertEquals("ccc", list.get(2));
    }
}
