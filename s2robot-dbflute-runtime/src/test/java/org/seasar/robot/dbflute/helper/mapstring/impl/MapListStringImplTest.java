package org.seasar.robot.dbflute.helper.mapstring.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.6.0 (2008/01/17 Thursday)
 */
public class MapListStringImplTest extends PlainTestCase {

    // ===================================================================================
    //                                                                               Build
    //                                                                               =====
    public void test_buildMapString_basic() {
        // ## Arrange ##
        final MapListStringImpl maplist = new MapListStringImpl();
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        {
            Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
            valueMap.put("key3-1", "value3-1");
            valueMap.put("key3-2", "value3-2");
            List<Object> valueList = new ArrayList<Object>();
            valueList.add("value3-3-1");
            valueList.add("value3-3-2");
            valueMap.put("key3-3", valueList);
            map.put("key3", valueMap);
        }
        {
            List<Object> valueList = new ArrayList<Object>();
            valueList.add("value4-1");
            valueList.add("value4-2");
            Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
            valueMap.put("key4-3-1", "value4-3-1");
            valueMap.put("key4-3-2", "value4-3-2");
            valueList.add(valueMap);
            map.put("key4", valueList);
        }

        // ## Act ##
        String actual = maplist.buildMapString(map);

        // ## Assert ##
        log(ln() + actual);
        assertTrue(actual.contains("; key1 = value1" + ln()));
        assertTrue(actual.contains("; key2 = value2" + ln()));
        assertTrue(actual.contains("; key3 = map:{" + ln()));
        assertTrue(actual.contains("    ; key3-1 = value3-1" + ln()));
        Map<String, Object> generateMap = maplist.generateMap(actual);
        log(ln() + generateMap);
        assertEquals(map, generateMap);
    }

    // ===================================================================================
    //                                                                            Generate
    //                                                                            ========
    public void test_generateMap_contains_List() throws Exception {
        // ## Arrange ##
        final MapListStringImpl maplist = new MapListStringImpl();
        final String mapString = "map:{key1=value1;key2=list:{value2-1;value2-2;value2-3};key3=value3}";

        // ## Act ##
        final Map<String, Object> resultMap = maplist.generateMap(mapString);

        // ## Assert ##
        showGeneratedMap(resultMap);
        assertEquals(resultMap.get("key1"), "value1");
        assertEquals(resultMap.get("key2"), Arrays.asList(new String[] { "value2-1", "value2-2", "value2-3" }));
        assertEquals(resultMap.get("key3"), "value3");
    }

    public void test_generateMap_contains_EmptyString_and_Null() throws Exception {
        // ## Arrange ##
        final MapListStringImpl maplist = new MapListStringImpl();
        final String mapString = "map:{key1=value1;key2=;key3=list:{null;value3-2;null;null};key4=null}";

        // ## Act ##
        final Map<String, Object> resultMap = maplist.generateMap(mapString);

        // ## Assert ##
        showGeneratedMap(resultMap);
        assertEquals(resultMap.get("key1"), "value1");
        assertEquals(resultMap.get("key2"), null);
        assertEquals(resultMap.get("key3"), Arrays.asList(new String[] { null, "value3-2", null, null }));
        assertEquals(resultMap.get("key4"), null);
    }

    public void test_generateMap_contains_LineSeparator() throws Exception {
        // ## Arrange ##
        final MapListStringImpl maplist = new MapListStringImpl();
        final String mapString = "map:{key1=value1;key2=value2;key3=val\nue3;key4=value4}";

        // ## Act ##
        final Map<String, Object> generatedMap = maplist.generateMap(mapString);

        // ## Assert ##
        showGeneratedMap(generatedMap);
        assertEquals(generatedMap.get("key1"), "value1");
        assertEquals(generatedMap.get("key2"), "value2");
        assertEquals(generatedMap.get("key3"), "val\nue3");
        assertEquals(generatedMap.get("key4"), "value4");
    }

    public void test_generateMap_contains_DoubleByte() throws Exception {
        // ## Arrange ##
        final MapListStringImpl maplist = new MapListStringImpl();
        final String mapString = "map:{key1=value1;key2=値２;キー３=このあと改行\nした;key4=あと全角セミコロン；とかね}";

        // ## Act ##
        final Map<String, Object> generatedMap = maplist.generateMap(mapString);

        // ## Assert ##
        showGeneratedMap(generatedMap);
    }

    protected void showGeneratedMap(Map<String, Object> generatedMap) {
        final String targetString = generatedMap.toString();
        final StringBuilder sb = new StringBuilder();
        sb.append(ln());
        sb.append("/* * * * * * * * * * * * * * * * * * * * * * * * * * * ").append(ln());
        sb.append(targetString).append(ln());
        sb.append("* * * * * * * * * */");
        log(sb);
    }
}
