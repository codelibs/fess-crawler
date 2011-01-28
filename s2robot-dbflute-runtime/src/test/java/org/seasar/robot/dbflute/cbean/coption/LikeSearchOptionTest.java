package org.seasar.robot.dbflute.cbean.coption;

import java.util.Arrays;
import java.util.List;

import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfCollectionUtil;

/**
 * @author jflute
 * @since 0.5.9 (2007/12/20 Thursday)
 */
public class LikeSearchOptionTest extends PlainTestCase {

    // ===================================================================================
    //                                                                         Rear Option
    //                                                                         ===========
    public void test_autoEscape_default() {
        // ## Arrange ##
        final LikeSearchOption option = new LikeSearchOption();

        // ## Act ##
        final String rearOption = option.getRearOption();

        // ## Assert ##
        assertEquals("", rearOption.trim());
    }

    public void test_autoEscape_likeXxx() {
        // ## Arrange ##
        final LikeSearchOption option = new LikeSearchOption();
        option.likeContain();

        // ## Act ##
        final String rearOption = option.getRearOption();

        // ## Assert ##
        assertEquals("escape '|'", rearOption.trim());
    }

    public void test_getRearOption() {
        // ## Arrange ##
        final LikeSearchOption option = new LikeSearchOption();
        option.escapeByPipeLine();

        // ## Act ##
        final String rearOption = option.getRearOption();

        // ## Assert ##
        assertEquals("escape '|'", rearOption.trim());
    }

    public void test_autoEscape_likeXxx_escapeBySlash() {
        // ## Arrange ##
        final LikeSearchOption option = new LikeSearchOption();
        option.likeContain().escapeBySlash();

        // ## Act ##
        final String rearOption = option.getRearOption();

        // ## Assert ##
        assertEquals("escape '/'", rearOption.trim());
    }

    // ===================================================================================
    //                                                                            Split By
    //                                                                            ========
    public void test_splitBy_blank() {
        // ## Arrange ##
        final LikeSearchOption option = new LikeSearchOption();

        // ## Act ##
        option.splitByBlank();
        List<String> actual = Arrays.asList(option.generateSplitValueArray("FOO B　AR\tQU\rX\nQUU\r\nX"));

        // ## Assert ##
        assertEquals(Arrays.asList("FOO", "B", "AR", "QU", "X", "QUU", "X"), actual);
    }

    public void test_splitBy_blank_limit() {
        // ## Arrange ##
        final LikeSearchOption option = new LikeSearchOption();

        // ## Act ##
        option.splitByBlank(3);
        List<String> actual = Arrays.asList(option.generateSplitValueArray("FOO B　AR\tQU\rX\nQUU\r\nX"));

        // ## Assert ##
        assertEquals(Arrays.asList("FOO", "B", "AR"), actual);
    }

    public void test_splitBy_space() {
        // ## Arrange ##
        final LikeSearchOption option = new LikeSearchOption();

        // ## Act ##
        option.splitBySpace();
        String[] actual = option.generateSplitValueArray("FOO B　AR\tQUX\nQUUX");

        // ## Assert ##
        assertEquals(Arrays.asList("FOO", "B　AR\tQUX\nQUUX"), Arrays.asList(actual));
    }

    public void test_splitBy_spaceContainsDoubleByte() {
        // ## Arrange ##
        final LikeSearchOption option = new LikeSearchOption();

        // ## Act ##
        option.splitBySpaceContainsDoubleByte();
        String[] actual = option.generateSplitValueArray("FOO B　AR\tQUX\nQUUX");

        // ## Assert ##
        assertEquals(Arrays.asList("FOO", "B", "AR\tQUX\nQUUX"), Arrays.asList(actual));
    }

    public void test_splitBy_various_onlyone() {
        // ## Arrange ##
        final LikeSearchOption option = new LikeSearchOption();

        // ## Act ##
        option.splitByVarious(DfCollectionUtil.newArrayList("\t"));
        String[] actual = option.generateSplitValueArray("FOO B　AR\tQUX\nQUUX");

        // ## Assert ##
        assertEquals(Arrays.asList("FOO B　AR", "QUX\nQUUX"), Arrays.asList(actual));
    }

    public void test_splitBy_various_several() {
        // ## Arrange ##
        final LikeSearchOption option = new LikeSearchOption();

        // ## Act ##
        option.splitByVarious(DfCollectionUtil.newArrayList("\t", "X"));
        String[] actual = option.generateSplitValueArray("FOO B　AR\tQUX\nQUUX");

        // ## Assert ##
        assertEquals(Arrays.asList("FOO B　AR", "QU", "\nQUU"), Arrays.asList(actual));
    }

    // ===================================================================================
    //                                                                          Real Value
    //                                                                          ==========
    /**
     * OptionでValueが変化する場合の“本当の値”を生成するメソッド。<br />
     * 基本的には内部メソッドではあるが、単なるユーティリティとしても利用可能である。
     */
    public void test_generateRealValue() {
        final String inputValue = "abc%def_ghi";
        {
            // ## Arrange ##
            final LikeSearchOption option = new LikeSearchOption();
            option.escapeByPipeLine();

            // ## Act ##
            final String realValue = option.generateRealValue(inputValue);

            // ## Assert ##
            log("realValue=" + realValue);
            assertEquals("abc|%def|_ghi", realValue);
        }
        {
            // ## Arrange ##
            final LikeSearchOption option = new LikeSearchOption();
            option.likePrefix().escapeBySlash();

            // ## Act ##
            final String realValue = option.generateRealValue(inputValue);

            // ## Assert ##
            log("realValue=" + realValue);
            assertEquals("abc/%def/_ghi%", realValue);
        }
        {
            // ## Arrange ##
            final LikeSearchOption option = new LikeSearchOption();
            option.likeContain().escapeByAtMark();

            // ## Act ##
            final String realValue = option.generateRealValue(inputValue);

            // ## Assert ##
            log("realValue=" + realValue);
            assertEquals("%abc@%def@_ghi%", realValue);
        }
        {
            // ## Arrange ##
            final LikeSearchOption option = new LikeSearchOption();
            option.likeSuffix().escapeByBackSlash();

            // ## Act ##
            final String realValue = option.generateRealValue(inputValue);

            // ## Assert ##
            log("realValue=" + realValue);
            assertEquals("%abc\\%def\\_ghi", realValue);
        }
        {
            // ## Arrange ##
            final LikeSearchOption option = new LikeSearchOption();
            option.escapeByPipeLine();

            // ## Act ##
            final String realValue = option.generateRealValue(inputValue + "jk|l");

            // ## Assert ##
            log("realValue=" + realValue);
            assertEquals("abc|%def|_ghijk||l", realValue);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public void test_toString_basic() {
        // ## Arrange ##
        LikeSearchOption option = createOption();
        option.likePrefix();

        // ## Act ##
        String actual = option.toString();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("escape=|"));
    }

    public void test_toString_split_basic() {
        // ## Arrange ##
        LikeSearchOption option = createOption();
        option.likePrefix().splitByPipeLine();

        // ## Act ##
        String actual = option.toString();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("split=true(and)"));
    }

    public void test_toString_split_or() {
        // ## Arrange ##
        LikeSearchOption option = createOption();
        option.likePrefix().splitByPipeLine().asOrSplit();

        // ## Act ##
        String actual = option.toString();

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains("split=true(or)"));
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    protected LikeSearchOption createOption() {
        return new LikeSearchOption();
    }
}
