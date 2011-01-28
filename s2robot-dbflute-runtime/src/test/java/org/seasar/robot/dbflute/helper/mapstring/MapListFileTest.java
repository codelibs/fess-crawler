package org.seasar.robot.dbflute.helper.mapstring;

import java.io.ByteArrayInputStream;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class MapListFileTest extends PlainTestCase {

    public void test_readString_lineComment_removed() throws Exception {
        // ## Arrange ##
        MapListFile file = new MapListFile();
        String text = "foo, \n#bar, \nbaz, qux";
        ByteArrayInputStream ins = new ByteArrayInputStream(text.getBytes("UTF-8"));

        // ## Act ##
        String actual = file.readString(ins);

        // ## Assert ##
        log(actual);
        assertEquals("foo, \nbaz, qux", actual);
    }

    public void test_readString_lineComment_last_removed_add_ln() throws Exception {
        // ## Arrange ##
        MapListFile file = new MapListFile();
        String text = "foo, \n#bar, \nbaz, qux\n#abc";
        ByteArrayInputStream ins = new ByteArrayInputStream(text.getBytes("UTF-8"));

        // ## Act ##
        String actual = file.readString(ins);

        // ## Assert ##
        log(actual);
        assertEquals("foo, \nbaz, qux\n", actual);
    }

    public void test_readString_BOM_removed() throws Exception {
        // ## Arrange ##
        MapListFile file = new MapListFile();
        String text = '\uFEFF' + "foo, bar, baz, qux";
        ByteArrayInputStream ins = new ByteArrayInputStream(text.getBytes("UTF-8"));

        // ## Act ##
        String actual = file.readString(ins);

        // ## Assert ##
        log(actual);
        assertEquals("foo, bar, baz, qux", actual);
    }

    public void test_readString_BOM_notRemoved_ifNotInitial() throws Exception {
        // ## Arrange ##
        MapListFile file = new MapListFile();
        String text = "abc" + '\uFEFF' + "foo, bar, baz, qux";
        ByteArrayInputStream ins = new ByteArrayInputStream(text.getBytes("UTF-8"));

        // ## Act ##
        String actual = file.readString(ins);

        // ## Assert ##
        log(actual);
        assertEquals("abc\uFEFFfoo, bar, baz, qux", actual);
    }
}
