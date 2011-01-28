package org.seasar.robot.dbflute.twowaysql.node;

import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.cbean.SimpleMapPmb;
import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentListIndexNotNumberException;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentListIndexOutOfBoundsException;
import org.seasar.robot.dbflute.twowaysql.exception.BindVariableCommentNotFoundPropertyException;
import org.seasar.robot.dbflute.twowaysql.exception.ForCommentNotFoundPropertyException;
import org.seasar.robot.dbflute.twowaysql.exception.ForCommentPropertyReadFailureException;
import org.seasar.robot.dbflute.twowaysql.node.ValueAndTypeSetupper.CommentType;
import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfCollectionUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 */
public class ValueAndTypeSetupperTest extends PlainTestCase {

    // ===================================================================================
    //                                                                                Bean
    //                                                                                ====
    public void test_setupValueAndType_bean_basic() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberId");
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(3);
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);

        // ## Assert ##
        assertEquals(3, valueAndType.getTargetValue());
        assertEquals(Integer.class, valueAndType.getTargetType());
        assertNull(valueAndType.getLikeSearchOption());
    }

    public void test_setupValueAndType_bean_likeSearch() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberName");
        MockPmb pmb = new MockPmb();
        pmb.setMemberName("f|o%o");
        pmb.setMemberNameInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);
        valueAndType.filterValueByOptionIfNeeds();

        // ## Assert ##
        assertEquals("f||o|%o%", valueAndType.getTargetValue());
        assertEquals(String.class, valueAndType.getTargetType());
        assertEquals(" escape '|'", valueAndType.getLikeSearchOption().getRearOption());
    }

    public void test_setupValueAndType_bean_likeSearch_notFound() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberName");
        MockPmb pmb = new MockPmb();
        pmb.setMemberName("f|o%o");
        //pmb.setMemberNameInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);

        // ## Assert ##
        assertEquals("f|o%o", valueAndType.getTargetValue());
        assertEquals(String.class, valueAndType.getTargetType());
    }

    public void test_setupValueAndType_bean_likeSearch_split() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberName");
        MockPmb pmb = new MockPmb();
        pmb.setMemberName("f|o%o");
        pmb.setMemberNameInternalLikeSearchOption(new LikeSearchOption().likePrefix().splitByPipeLine());
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType); // no check here
        valueAndType.filterValueByOptionIfNeeds();

        // ## Assert ##
        assertEquals("f||o|%o%", valueAndType.getTargetValue());
        assertEquals(String.class, valueAndType.getTargetType());
    }

    public void test_setupValueAndType_bean_nest() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.nestPmb.memberId");
        MockPmb nestPmb = new MockPmb();
        nestPmb.setMemberId(3);
        MockPmb pmb = new MockPmb();
        pmb.setNestPmb(nestPmb);
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);

        // ## Assert ##
        assertEquals(3, valueAndType.getTargetValue());
        assertEquals(Integer.class, valueAndType.getTargetType());
        assertNull(valueAndType.getLikeSearchOption());
    }

    public void test_setupValueAndType_bean_nest_likeSearch_basic() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.nestLikePmb.memberName");
        MockPmb nestLikePmb = new MockPmb();
        nestLikePmb.setMemberName("f|o%o");
        MockPmb pmb = new MockPmb();
        pmb.setNestLikePmb(nestLikePmb);
        pmb.setNestLikePmbInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);
        valueAndType.filterValueByOptionIfNeeds();

        // ## Assert ##
        assertEquals("f||o|%o%", valueAndType.getTargetValue());
        assertEquals(String.class, valueAndType.getTargetType());
        assertEquals(" escape '|'", valueAndType.getLikeSearchOption().getRearOption());
    }

    public void test_setupValueAndType_bean_nest_likeSearch_override() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.nestLikePmb.memberName");
        MockPmb nestLikePmb = new MockPmb();
        nestLikePmb.setMemberName("f|o%o");
        nestLikePmb.setMemberNameInternalLikeSearchOption(new LikeSearchOption().likeContain());
        MockPmb pmb = new MockPmb();
        pmb.setNestLikePmb(nestLikePmb);
        pmb.setNestLikePmbInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);
        valueAndType.filterValueByOptionIfNeeds();

        // ## Assert ##
        assertEquals("%f||o|%o%", valueAndType.getTargetValue());
        assertEquals(String.class, valueAndType.getTargetType());
        assertEquals(" escape '|'", valueAndType.getLikeSearchOption().getRearOption());
    }

    public void test_setupValueAndType_bean_propertyReadFailure() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsForComment("pmb.memberId");
        MockPmb pmb = new MockPmb() {
            @Override
            public Integer getMemberId() { // not accessible
                return super.getMemberId();
            }
        };
        pmb.setMemberId(3);
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        try {
            setupper.setupValueAndType(valueAndType);

            // ## Assert ##
            fail();
        } catch (ForCommentPropertyReadFailureException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_setupValueAndType_bean_notFoundProperty() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsForComment("pmb.memberIo");
        MockPmb pmb = new MockPmb();
        pmb.setMemberId(3);
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        try {
            setupper.setupValueAndType(valueAndType);

            // ## Assert ##
            fail();
        } catch (ForCommentNotFoundPropertyException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                                List
    //                                                                                ====
    public void test_setupValueAndType_list_likeSearch() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberNameList.get(1)");
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("f|oo", "ba%r", "b|a%z"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);
        valueAndType.filterValueByOptionIfNeeds();

        // ## Assert ##
        assertEquals("ba|%r%", valueAndType.getTargetValue());
        assertEquals(String.class, valueAndType.getTargetType());
        assertEquals(" escape '|'", valueAndType.getLikeSearchOption().getRearOption());
    }

    public void test_setupValueAndType_list_notNumber() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberNameList.get(index)");
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("f|oo", "ba%r", "b|a%z"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        try {
            setupper.setupValueAndType(valueAndType);

            // ## Assert ##
            fail();
        } catch (BindVariableCommentListIndexNotNumberException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_setupValueAndType_list_outOfBounds() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberNameList.get(4)");
        MockPmb pmb = new MockPmb();
        pmb.setMemberNameList(DfCollectionUtil.newArrayList("f|oo", "ba%r", "b|a%z"));
        pmb.setMemberNameListInternalLikeSearchOption(new LikeSearchOption().likePrefix());
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        try {
            setupper.setupValueAndType(valueAndType);

            // ## Assert ##
            fail();
        } catch (BindVariableCommentListIndexOutOfBoundsException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                              MapPmb
    //                                                                              ======
    public void test_setupValueAndType_mappmb_basic() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberId");
        SimpleMapPmb<Integer> pmb = new SimpleMapPmb<Integer>();
        pmb.addParameter("memberId", 3);
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);

        // ## Assert ##
        assertEquals(3, valueAndType.getTargetValue());
        assertEquals(Integer.class, valueAndType.getTargetType());
        assertNull(valueAndType.getLikeSearchOption());
    }

    public void test_setupValueAndType_mappmb_likeSearch() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberName");
        SimpleMapPmb<Object> pmb = new SimpleMapPmb<Object>();
        pmb.addParameter("memberId", 3);
        pmb.addParameter("memberName", "f|o%o");
        pmb.addParameter("memberNameInternalLikeSearchOption", new LikeSearchOption().likePrefix());
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);
        valueAndType.filterValueByOptionIfNeeds();

        // ## Assert ##
        assertEquals("f||o|%o%", valueAndType.getTargetValue());
        assertEquals(String.class, valueAndType.getTargetType());
        assertEquals(" escape '|'", valueAndType.getLikeSearchOption().getRearOption());
    }

    public void test_setupValueAndType_mappmb_notKey() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberId");
        SimpleMapPmb<Integer> pmb = new SimpleMapPmb<Integer>();
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        try {
            setupper.setupValueAndType(valueAndType);

            // ## Assert ##
            fail();
        } catch (BindVariableCommentNotFoundPropertyException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                                 Map
    //                                                                                 ===
    public void test_setupValueAndType_map_basic() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberId");
        Map<String, Object> pmb = DfCollectionUtil.newHashMap();
        pmb.put("memberId", 3);
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);

        // ## Assert ##
        assertEquals(3, valueAndType.getTargetValue());
        assertEquals(Integer.class, valueAndType.getTargetType());
        assertNull(valueAndType.getLikeSearchOption());
    }

    public void test_setupValueAndType_map_likeSearch() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberName");
        Map<String, Object> pmb = DfCollectionUtil.newHashMap();
        pmb.put("memberId", 3);
        pmb.put("memberName", "f|o%o");
        pmb.put("memberNameInternalLikeSearchOption", new LikeSearchOption().likePrefix());
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);
        valueAndType.filterValueByOptionIfNeeds();

        // ## Assert ##
        assertEquals("f||o|%o%", valueAndType.getTargetValue());
        assertEquals(String.class, valueAndType.getTargetType());
        assertEquals(" escape '|'", valueAndType.getLikeSearchOption().getRearOption());
    }

    public void test_setupValueAndType_map_notKey() {
        // ## Arrange ##
        ValueAndTypeSetupper setupper = createTargetAsBind("pmb.memberId");
        Map<String, Object> pmb = DfCollectionUtil.newHashMap();
        ValueAndType valueAndType = createTargetAndType(pmb);

        // ## Act ##
        setupper.setupValueAndType(valueAndType);

        // ## Assert ##
        assertEquals(null, valueAndType.getTargetValue());
        assertEquals(null, valueAndType.getTargetType());
        assertNull(valueAndType.getLikeSearchOption());
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    protected ValueAndTypeSetupper createTargetAsBind(String expression) {
        CommentType type = CommentType.BIND;
        return new ValueAndTypeSetupper(Srl.splitList(expression, "."), expression, "select * from ...", type);
    }

    protected ValueAndTypeSetupper createTargetAsForComment(String expression) {
        CommentType type = CommentType.FORCOMMENT;
        return new ValueAndTypeSetupper(Srl.splitList(expression, "."), expression, "select * from ...", type);
    }

    protected ValueAndType createTargetAndType(Object value) {
        ValueAndType valueAndType = new ValueAndType();
        valueAndType.setFirstValue(value);
        valueAndType.setFirstType(value.getClass());
        return valueAndType;
    }

    protected static class MockPmb {
        protected Integer _memberId;
        protected String _memberName;
        protected LikeSearchOption _memberNameInternalLikeSearchOption;
        protected List<String> _memberNameList;
        protected LikeSearchOption _memberNameListInternalLikeSearchOption;
        protected MockPmb _nestPmb;
        protected MockPmb _nestLikePmb;
        protected LikeSearchOption _nestLikePmbInternalLikeSearchOption;

        public Integer getMemberId() {
            return _memberId;
        }

        public void setMemberId(Integer memberId) {
            this._memberId = memberId;
        }

        public String getMemberName() {
            return _memberName;
        }

        public void setMemberName(String memberName) {
            this._memberName = memberName;
        }

        public LikeSearchOption getMemberNameInternalLikeSearchOption() {
            return _memberNameInternalLikeSearchOption;
        }

        public void setMemberNameInternalLikeSearchOption(LikeSearchOption memberNameInternalLikeSearchOption) {
            this._memberNameInternalLikeSearchOption = memberNameInternalLikeSearchOption;
        }

        public List<String> getMemberNameList() {
            return _memberNameList;
        }

        public void setMemberNameList(List<String> memberNameList) {
            this._memberNameList = memberNameList;
        }

        public LikeSearchOption getMemberNameListInternalLikeSearchOption() {
            return _memberNameListInternalLikeSearchOption;
        }

        public void setMemberNameListInternalLikeSearchOption(LikeSearchOption memberNameListInternalLikeSearchOption) {
            this._memberNameListInternalLikeSearchOption = memberNameListInternalLikeSearchOption;
        }

        public MockPmb getNestPmb() {
            return _nestPmb;
        }

        public void setNestPmb(MockPmb nestPmb) {
            this._nestPmb = nestPmb;
        }

        public MockPmb getNestLikePmb() {
            return _nestLikePmb;
        }

        public void setNestLikePmb(MockPmb nestLikePmb) {
            this._nestLikePmb = nestLikePmb;
        }

        public LikeSearchOption getNestLikePmbInternalLikeSearchOption() {
            return _nestLikePmbInternalLikeSearchOption;
        }

        public void setNestLikePmbInternalLikeSearchOption(LikeSearchOption nestLikePmbInternalLikeSearchOption) {
            this._nestLikePmbInternalLikeSearchOption = nestLikePmbInternalLikeSearchOption;
        }
    }
}
