package org.seasar.robot.dbflute.twowaysql.node;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.seasar.robot.dbflute.exception.IfCommentDifferentTypeComparisonException;
import org.seasar.robot.dbflute.exception.IfCommentEmptyExpressionException;
import org.seasar.robot.dbflute.exception.IfCommentIllegalParameterBeanSpecificationException;
import org.seasar.robot.dbflute.exception.IfCommentNotBooleanResultException;
import org.seasar.robot.dbflute.exception.IfCommentNotFoundMethodException;
import org.seasar.robot.dbflute.exception.IfCommentNotFoundPropertyException;
import org.seasar.robot.dbflute.exception.IfCommentNullPointerException;
import org.seasar.robot.dbflute.exception.IfCommentUnsupportedExpressionException;
import org.seasar.robot.dbflute.exception.IfCommentUnsupportedTypeComparisonException;
import org.seasar.robot.dbflute.jdbc.Classification;
import org.seasar.robot.dbflute.twowaysql.pmbean.ParameterBean;
import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * 
 * @author jflute
 * @since 0.9.5.5 (2009/10/01 Thursday)
 */
public class IfCommentEvaluatorTest extends PlainTestCase {

    // ===================================================================================
    //                                                                             Literal
    //                                                                             =======
    public void test_evaluate_isNotNull() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");
        String expression = "pmb.memberName != null";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
    }

    public void test_evaluate_isNull() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");
        String expression = "pmb.memberName == null";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertFalse(evaluator.evaluate());
        pmb.setMemberName(null);
        assertTrue(evaluator.evaluate());
    }

    public void test_evaluate_string() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");
        String expression = "pmb.memberName == 'foo'";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberName("bar");
        assertFalse(evaluator.evaluate());
    }

    public void test_evaluate_number() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        String expression = "pmb.memberId == 3";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberId(2);
        assertFalse(evaluator.evaluate());
    }

    public void test_evaluate_date() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setBirthdate(DfTypeUtil.toDateFlexibly("2009/11/22"));
        String expression = "pmb.birthdate == date '2009/11/22'";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setBirthdate(DfTypeUtil.toDateFlexibly("2009/10/12"));
        assertFalse(evaluator.evaluate());
    }

    public void test_evaluate_boolean() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "pmb.existsPurchase == true";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setExistsPurchase(false);
        assertFalse(evaluator.evaluate());
    }

    // ===================================================================================
    //                                                                             Boolean
    //                                                                             =======
    public void test_evaluate_boolean_property() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "pmb.existsPurchase";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setExistsPurchase(false);
        assertFalse(evaluator.evaluate());
    }

    public void test_evaluate_boolean_property_not() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "!pmb.existsPurchase";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertFalse(evaluator.evaluate());
        pmb.setExistsPurchase(false);
        assertTrue(evaluator.evaluate());
    }

    public void test_evaluate_boolean_method() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "pmb.isExistsPurchase()";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setExistsPurchase(false);
        assertFalse(evaluator.evaluate());
    }

    public void test_evaluate_boolean_method_not() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setExistsPurchase(true);
        String expression = "!pmb.isExistsPurchase()";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertFalse(evaluator.evaluate());
        pmb.setExistsPurchase(false);
        assertTrue(evaluator.evaluate());
    }

    public void test_evaluate_boolean_literal() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();

        // ## Act && Assert ##
        assertTrue(createEvaluator(pmb, "true").evaluate());
        assertFalse(createEvaluator(pmb, "false").evaluate());
    }

    public void test_evaluate_boolean_literal_not() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();

        // ## Act && Assert ##
        assertFalse(createEvaluator(pmb, "!true").evaluate());
        assertTrue(createEvaluator(pmb, "!false").evaluate());
    }

    // ===================================================================================
    //                                                                              And/Or
    //                                                                              ======
    public void test_evaluate_and() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        String expression = "pmb.memberId != null && pmb.memberName != null";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
        pmb.setMemberId(null);
        pmb.setMemberName("bar");
        assertFalse(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
        pmb.setMemberId(4);
        pmb.setMemberName("bar");
        assertTrue(evaluator.evaluate());
    }

    public void test_evaluate_and_many() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        pmb.setExistsPurchase(true);
        String expression = "pmb.memberId != null && pmb.memberName != null && pmb.existsPurchase";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
        pmb.setMemberName("bar");
        pmb.setExistsPurchase(false);
        assertFalse(evaluator.evaluate());
        pmb.setMemberId(null);
        pmb.setMemberName(null);
        pmb.setExistsPurchase(false);
        assertFalse(evaluator.evaluate());
        pmb.setMemberId(4);
        pmb.setMemberName("bar");
        pmb.setExistsPurchase(true);
        assertTrue(evaluator.evaluate());
    }

    public void test_evaluate_or() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        String expression = "pmb.memberId != null || pmb.memberName != null";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertTrue(evaluator.evaluate());
        pmb.setMemberId(null);
        pmb.setMemberName("bar");
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
        pmb.setMemberId(4);
        pmb.setMemberName("bar");
        assertTrue(evaluator.evaluate());
    }

    public void test_evaluate_or_many() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        NextPmb nextPmb = new NextPmb();
        nextPmb.setExistsLogin(true);
        pmb.setNextPmb(nextPmb);
        String expression = "pmb.memberId != null || pmb.memberName != null || pmb.nextPmb.existsLogin";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.getNextPmb().setExistsLogin(false);
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertTrue(evaluator.evaluate());
        pmb.setMemberId(null);
        assertFalse(evaluator.evaluate());
        pmb.getNextPmb().setExistsLogin(true);
        assertTrue(evaluator.evaluate());
    }

    // ===================================================================================
    //                                                                             Compare
    //                                                                             =======
    public void test_greaterThan_number() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);

        // ## Act && Assert ##
        assertTrue(createEvaluator(pmb, "pmb.memberId > 0").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.memberId > 3").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.memberId > 4").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.memberId > -6").evaluate());
        NextPmb nextPmb = new NextPmb();
        nextPmb.setDisplayOrder(4);
        pmb.setNextPmb(nextPmb);
        assertFalse(createEvaluator(pmb, "pmb.memberId > pmb.nextPmb.displayOrder").evaluate());
        nextPmb.setDisplayOrder(2);
        assertTrue(createEvaluator(pmb, "pmb.memberId > pmb.nextPmb.displayOrder").evaluate());
        pmb.setMemberId(null);
        assertFalse(createEvaluator(pmb, "pmb.memberId > -6").evaluate());
        pmb.setMemberId(3);
        nextPmb.setDisplayOrder(null);
        assertTrue(createEvaluator(pmb, "pmb.memberId > pmb.nextPmb.displayOrder").evaluate());
    }

    public void test_greaterThan_date() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setBirthdate(DfTypeUtil.toDate("2009/12/24 12:34:56"));

        // ## Act && Assert ##
        assertTrue(createEvaluator(pmb, "pmb.birthdate > date '2009/12/23 12:34:56'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate > date '2009/12/24 12:34:57'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate > pmb.birthdate").evaluate());
        pmb.setBirthdate(DfTypeUtil.toDate("2009/09/12"));
        assertTrue(createEvaluator(pmb, "pmb.birthdate > date '2009/09/11'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate > date '2009/09/12'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate > date '2009/09/12 12:34:57'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate > date '2009/09/13'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate > pmb.birthdate").evaluate());
    }

    public void test_lessThan_number() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);

        // ## Act && Assert ##
        assertFalse(createEvaluator(pmb, "pmb.memberId < 0").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.memberId < 3").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.memberId < 4").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.memberId < -6").evaluate());
        NextPmb nextPmb = new NextPmb();
        nextPmb.setDisplayOrder(4);
        pmb.setNextPmb(nextPmb);
        assertTrue(createEvaluator(pmb, "pmb.memberId < pmb.nextPmb.displayOrder").evaluate());
        nextPmb.setDisplayOrder(2);
        assertFalse(createEvaluator(pmb, "pmb.memberId < pmb.nextPmb.displayOrder").evaluate());
        pmb.setMemberId(null);
        assertTrue(createEvaluator(pmb, "pmb.memberId < -6").evaluate());
        pmb.setMemberId(3);
        nextPmb.setDisplayOrder(null);
        assertFalse(createEvaluator(pmb, "pmb.memberId < pmb.nextPmb.displayOrder").evaluate());
    }

    public void test_lessThan_date() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        Date birthdate = DfTypeUtil.toTimestampFlexibly("2009/12/24 12:34:56");
        pmb.setBirthdate(birthdate);

        // ## Act && Assert ##
        assertFalse(createEvaluator(pmb, "pmb.birthdate < date '2009/12/23 12:34:56'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate < date '2009/12/24 12:34:57'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate < pmb.birthdate").evaluate());
        pmb.setBirthdate(DfTypeUtil.toDate("2009/09/12"));
        assertFalse(createEvaluator(pmb, "pmb.birthdate < date '2009/09/11'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate < date '2009/09/12'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate < date '2009/09/12 12:34:57'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate < date '2009/09/13'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate < pmb.birthdate").evaluate());

    }

    public void test_greaterEqual_number() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);

        // ## Act && Assert ##
        assertTrue(createEvaluator(pmb, "pmb.memberId >= 0").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.memberId >= 3").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.memberId >= 4").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.memberId >= -6").evaluate());
        NextPmb nextPmb = new NextPmb();
        nextPmb.setDisplayOrder(4);
        pmb.setNextPmb(nextPmb);
        assertFalse(createEvaluator(pmb, "pmb.memberId >= pmb.nextPmb.displayOrder").evaluate());
        nextPmb.setDisplayOrder(2);
        assertTrue(createEvaluator(pmb, "pmb.memberId >= pmb.nextPmb.displayOrder").evaluate());
        pmb.setMemberId(null);
        assertFalse(createEvaluator(pmb, "pmb.memberId >= -6").evaluate());
        pmb.setMemberId(3);
        nextPmb.setDisplayOrder(null);
        assertTrue(createEvaluator(pmb, "pmb.memberId >= pmb.nextPmb.displayOrder").evaluate());
    }

    public void test_greaterEqual_date() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        Date birthdate = DfTypeUtil.toDate("2009/12/24 12:34:56");
        pmb.setBirthdate(birthdate);

        // ## Act && Assert ##
        assertTrue(createEvaluator(pmb, "pmb.birthdate >= date '2009/12/23 12:34:56'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate >= date '2009/12/24 12:34:57'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate >= pmb.birthdate").evaluate());
        pmb.setBirthdate(DfTypeUtil.toDate("2009/09/12"));
        assertTrue(createEvaluator(pmb, "pmb.birthdate >= date '2009/09/11'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate >= date '2009/09/12'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate >= date '2009/09/12 12:34:57.123'").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.birthdate >= date '2009/09/13'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate >= pmb.birthdate").evaluate());
    }

    public void test_lessEqual_number() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);

        // ## Act && Assert ##
        assertFalse(createEvaluator(pmb, "pmb.memberId <= 0").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.memberId <= 3").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.memberId <= 4").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.memberId <= -6").evaluate());
        NextPmb nextPmb = new NextPmb();
        nextPmb.setDisplayOrder(4);
        pmb.setNextPmb(nextPmb);
        assertTrue(createEvaluator(pmb, "pmb.memberId <= pmb.nextPmb.displayOrder").evaluate());
        nextPmb.setDisplayOrder(2);
        assertFalse(createEvaluator(pmb, "pmb.memberId <= pmb.nextPmb.displayOrder").evaluate());
        pmb.setMemberId(null);
        assertTrue(createEvaluator(pmb, "pmb.memberId <= -6").evaluate());
        pmb.setMemberId(3);
        nextPmb.setDisplayOrder(null);
        assertFalse(createEvaluator(pmb, "pmb.memberId <= pmb.nextPmb.displayOrder").evaluate());
    }

    public void test_lessEqual_date() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        Date birthdate = DfTypeUtil.toTimestampFlexibly("2009/12/24 12:34:56.123");
        pmb.setBirthdate(birthdate);

        // ## Act && Assert ##
        assertFalse(createEvaluator(pmb, "pmb.birthdate <= date '2009/12/23 12:34:56'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate <= date '2009/12/24 12:34:56.123'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate <= date '2009/12/24 12:34:56.124'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate <= date '2009/12/24 12:34:57'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate <= pmb.birthdate").evaluate());
        pmb.setBirthdate(DfTypeUtil.toDate("2009/09/12"));
        assertFalse(createEvaluator(pmb, "pmb.birthdate <= date '2009/09/11'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate <= date '2009/09/12'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate <= date '2009/09/12 12:34:57.123'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate <= date '2009/09/13'").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.birthdate <= pmb.birthdate").evaluate());
    }

    // ===================================================================================
    //                                                                           Exception
    //                                                                           =========
    public void test_evaluate_IfCommentEmptyExpressionException() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();

        // ## Act ##
        try {
            createEvaluator(pmb, "").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentEmptyExpressionException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, " ").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentEmptyExpressionException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, null).evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentEmptyExpressionException e) {
            log(e.getMessage());
        }
    }

    public void test_evaluate_IfCommentUnsupportedExpressionException() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();

        // ## Act ##
        try {
            createEvaluator(pmb, "(pmb.fooId != null || pmb.fooName != null)").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.fooId != null || pmb.fooName != null && pmb.barId != null").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "(pmb.fooId != null || pmb.fooName != null) && pmb.barId != null").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
        try {
            createEvaluator(pmb, "pmb.fooName == \"Pixy\"").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedExpressionException e) {
            log(e.getMessage());
        }
    }

    public void test_evaluate_VariousSituation() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");

        // ## Act ##
        try {
            createEvaluator(pmb, "pm b.getMemberNameNon() != null").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentIllegalParameterBeanSpecificationException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "pmb,memberNameNon != null").evaluate();
            // ## Assert ##
            fail();
        } catch (IfCommentIllegalParameterBeanSpecificationException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "pmb:memberNameNon != null").evaluate();
            // ## Assert ##
            fail();
        } catch (IfCommentIllegalParameterBeanSpecificationException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "pnb.memberNameNon != null").evaluate();
            // ## Assert ##
            fail();
        } catch (IfCommentIllegalParameterBeanSpecificationException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "pmbb != null").evaluate();
            // ## Assert ##
            fail();
        } catch (IfCommentIllegalParameterBeanSpecificationException e) {
            log(e.getMessage());
        }
        createEvaluator("foo", "pmbb != null").evaluate(); // no exception
    }

    public void test_evaluate_notFoundMethodProperty() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");

        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.getMemberNameNon() != null").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNotFoundMethodException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.memberNameNon != null").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNotFoundPropertyException e) {
            log(e.getMessage());
        }
    }

    public void test_evaluate_IfCommentNullPointerException() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();

        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.nextPmb.existsLogin").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNullPointerException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(null, "pmb.nextPmb.existsLogin").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNullPointerException e) {
            log(e.getMessage());
        }
        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.nextPmb.memberStatusCode").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNullPointerException e) {
            log(e.getMessage());
        }
    }

    public void test_evaluate_IfCommentDifferentTypeComparisonException() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        pmb.setBirthdate(new Date());

        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.memberId <= 'Pixy'").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentDifferentTypeComparisonException e) {
            // OK
            log(e.getMessage());
        }
        try {
            createEvaluator(pmb, "pmb.memberId <= 'bar'").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentDifferentTypeComparisonException e) {
            // OK
            log(e.getMessage());
        }
        try {
            createEvaluator(pmb, "pmb.birthdate <= 9").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentDifferentTypeComparisonException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_evaluate_IfCommentUnsupportedTypeComparisonException() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(3);
        pmb.setMemberName("foo");
        pmb.setBirthdate(new Date());

        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.memberName <= 9").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentUnsupportedTypeComparisonException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_evaluate_IfCommentNotBooleanResultException() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        NextPmb nextPmb = new NextPmb();
        pmb.setNextPmb(nextPmb);

        // ## Act ##
        try {
            createEvaluator(pmb, "pmb.nextPmb.memberStatusCode").evaluate();

            // ## Assert ##
            fail();
        } catch (IfCommentNotBooleanResultException e) {
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                             Various
    //                                                                             =======
    public void test_evaluate_trim() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberName("foo");
        String expression = "  pmb.memberName  !=  null ";
        IfCommentEvaluator evaluator = createEvaluator(pmb, expression);

        // ## Act && Assert ##
        assertTrue(evaluator.evaluate());
        pmb.setMemberName(null);
        assertFalse(evaluator.evaluate());
    }

    public void test_evaluate_reverse() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.setMemberId(2);
        pmb.setBirthdate(DfTypeUtil.toDateFlexibly("2008/11/22"));

        // ## Act && Assert ##
        assertTrue(createEvaluator(pmb, "3 > pmb.memberId").evaluate());
        assertTrue(createEvaluator(pmb, "3 > pmb.memberId && pmb.birthdate < date '2009/11/22'").evaluate());
        assertTrue(createEvaluator(pmb, "3 > pmb.memberId && date '2009/11/22' > pmb.birthdate").evaluate());
        assertTrue(createEvaluator(pmb, "pmb.memberId < 3 && date '2009/11/22' > pmb.birthdate").evaluate());
        assertTrue(createEvaluator(pmb, "3 == pmb.memberId || date '2009/11/22' > pmb.birthdate").evaluate());
    }

    public void test_evaluate_map() {
        // ## Arrange ##
        BasePmb pmb = new BasePmb();
        pmb.putMapPmb("fooKey", 3);

        // ## Act && Assert ##
        assertTrue(createEvaluator(pmb, "pmb.mapPmb.fooKey > 2").evaluate());
        assertFalse(createEvaluator(pmb, "pmb.mapPmb.fooKey > 3").evaluate());
    }

    public void test_evaluate_scalarPmb() {
        // ## Arrange & Act && Assert ##
        assertTrue(createEvaluator(3, "pmb > 2").evaluate());
        assertTrue(createEvaluator("Pixy", "pmb == 'Pixy'").evaluate());
    }

    public void test_evaluate_nullPmb() {
        // ## Arrange & Act && Assert ##
        assertTrue(createEvaluator(null, "pmb == null").evaluate());
        assertTrue(createEvaluator(null, "pmb != 'Pixy'").evaluate());
    }

    public void test_evaluate_cdef() {
        // ## Arrange & Act && Assert ##
        Classification cdef = new MyCDef();

        // ## Act && Assert ##
        assertTrue(createEvaluator(cdef, "pmb.code() == 'Pixy'").evaluate());
    }

    protected static class MyCDef implements Classification {
        public String name() {
            return null;
        }

        public DataType dataType() {
            return null;
        }

        public String code() {
            return "Pixy";
        }

        public String alias() {
            return null;
        }
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    protected IfCommentEvaluator createEvaluator(final Object pmb, String expression) {
        return new IfCommentEvaluator(new ParameterFinder() {
            public Object find(String name) {
                return pmb;
            }
        }, expression, "select foo from bar");
    }

    protected static class BasePmb implements ParameterBean {
        private Integer _memberId;
        private String _memberName;
        private boolean _existsPurchase;
        private Date _birthdate;
        private NextPmb _nextPmb;
        private Map<String, Integer> _mapPmb = new HashMap<String, Integer>();

        public void checkSafetyResult(int safetyMaxResultSize) {

        }

        public int getSafetyMaxResultSize() {
            return 0;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName()).append(":");
            sb.append(xbuildColumnString());
            return sb.toString();
        }

        private String xbuildColumnString() {
            final String delimiter = ",";
            final StringBuilder sb = new StringBuilder();
            sb.append(delimiter).append(_memberId);
            sb.append(delimiter).append(_memberName);
            sb.append(delimiter).append(_birthdate);
            sb.append(delimiter).append(_existsPurchase);
            if (sb.length() > 0) {
                sb.delete(0, delimiter.length());
            }
            sb.insert(0, "{").append("}");
            return sb.toString();
        }

        public Integer getMemberId() {
            return _memberId;
        }

        public void setMemberId(Integer memberId) {
            _memberId = memberId;
        }

        public String getMemberName() {
            return _memberName;
        }

        public void setMemberName(String memberName) {
            _memberName = memberName;
        }

        public Date getBirthdate() {
            return _birthdate;
        }

        public void setBirthdate(Date birthdate) {
            this._birthdate = birthdate;
        }

        public boolean isExistsPurchase() {
            return _existsPurchase;
        }

        public void setExistsPurchase(boolean existsPurchase) {
            this._existsPurchase = existsPurchase;
        }

        public NextPmb getNextPmb() {
            return _nextPmb;
        }

        public void setNextPmb(NextPmb nextPmb) {
            this._nextPmb = nextPmb;
        }

        public Map<String, Integer> getMapPmb() {
            return _mapPmb;
        }

        public void putMapPmb(String key, Integer value) {
            this._mapPmb.put(key, value);
        }
    }

    protected static class NextPmb implements ParameterBean {
        private String _memberStatusCode;
        private Integer _displayOrder;
        private boolean _existsLogin;

        public void checkSafetyResult(int safetyMaxResultSize) {
        }

        public int getSafetyMaxResultSize() {
            return 0;
        }

        public String getMemberStatusCode() {
            return _memberStatusCode;
        }

        public void setMemberStatusCode(String memberStatusCode) {
            this._memberStatusCode = memberStatusCode;
        }

        public Integer getDisplayOrder() {
            return _displayOrder;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this._displayOrder = displayOrder;
        }

        public boolean isExistsLogin() {
            return _existsLogin;
        }

        public void setExistsLogin(boolean existsLogin) {
            this._existsLogin = existsLogin;
        }
    }
}
