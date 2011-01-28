package org.seasar.robot.dbflute.cbean.chelper;

import junit.framework.TestCase;

/**
 * @author jflute
 */
public class HpCBPurposeTest extends TestCase {

    public void test_spec_NormalUse() {
        // ## Arrange ##
        HpCBPurpose purpose = HpCBPurpose.NORMAL_USE; // all can be used

        // ## Act & Assert ##
        assertFalse(purpose.isNoSetupSelect());
        assertFalse(purpose.isNoSpecify());
        assertFalse(purpose.isNoSpecifyColumnTwoOrMore());
        assertFalse(purpose.isNoSpecifyRelation());
        assertFalse(purpose.isNoSpecifyDerivedReferrer());
        assertFalse(purpose.isNoQuery());
        assertFalse(purpose.isNoOrderBy());
    }

    public void test_toString() {
        assertEquals("NormalUse", HpCBPurpose.NORMAL_USE.toString());
        assertEquals("UnionQuery", HpCBPurpose.UNION_QUERY.toString());
        assertEquals("ExistsReferrer", HpCBPurpose.EXISTS_REFERRER.toString());
        assertEquals("InScopeRelation", HpCBPurpose.IN_SCOPE_RELATION.toString());
        assertEquals("DerivedReferrer", HpCBPurpose.DERIVED_REFERRER.toString());
        assertEquals("ScalarSelect", HpCBPurpose.SCALAR_SELECT.toString());
        assertEquals("ScalarCondition", HpCBPurpose.SCALAR_CONDITION.toString());
        assertEquals("ColumnQuery", HpCBPurpose.COLUMN_QUERY.toString());
        assertEquals("VaryingUpdate", HpCBPurpose.VARYING_UPDATE.toString());
    }
}
