package org.seasar.robot.dbflute.helper.mapstring.impl;

import java.util.Arrays;
import java.util.Map;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.6.0 (2008/01/17 Thursday)
 */
public class MapListStringImplTest extends PlainTestCase {

    /**
     * 値にList型が含まれているMapの生成。
     * @throws Exception
     */
    public void test_MapListString_generateMap_Contains_List() throws Exception {
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

    public void test_MapListString_generateMap_Contains_EmptyString_and_Null() throws Exception {
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

    public void test_MapListString_generateMap_Contains_LineSeparator() throws Exception {
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

    public void test_MapListString_generateMap_Contains_DoubleByte() throws Exception {
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
