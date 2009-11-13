package org.seasar.robot.dbflute.dbmeta;

import java.util.HashMap;
import java.util.Map;

import org.seasar.robot.dbflute.dbmeta.AbstractDBMeta.MapStringValueAnalyzer;
import org.seasar.robot.dbflute.jdbc.Classification;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/30 Tuesday)
 */
public class AbstractDBMetaTest extends PlainTestCase {

    public void test_MapStringValueAnalyzer_analyzeOther_normalValue() throws Exception {
        // ## Arrange ##
        Map<String, Object> valueMap = new HashMap<String, Object>();
        Object value = new Object();
        valueMap.put("FOO_NAME", value);
        MapStringValueAnalyzer analyzer = new MapStringValueAnalyzer(valueMap);
        analyzer.init("FOO_NAME", "fooName", "FooName");

        // ## Act ##
        Object actual = analyzer.analyzeOther(Object.class);

        // ## Assert ##
        assertEquals(value, actual);
    }

    public void test_MapStringValueAnalyzer_analyzeOther_classification() throws Exception {
        // ## Arrange ##
        Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("FOO_NAME", "bar");
        MapStringValueAnalyzer analyzer = new MapStringValueAnalyzer(valueMap);
        analyzer.init("FOO_NAME", "fooName", "FooName");

        // ## Act ##
        MockClassification actual = analyzer.analyzeOther(MockClassification.class);

        // ## Assert ##
        assertEquals(MockClassification.BAR, actual);
    }

    protected static enum MockClassification implements Classification {
        FOO, BAR;
        public String alias() {
            return null;
        }

        public String code() {
            return null;
        }

        public static MockClassification codeOf(Object obj) {
            return obj instanceof String && obj.equals("bar") ? BAR : null;
        }

        public DataType dataType() {
            return null;
        }
    }
}
