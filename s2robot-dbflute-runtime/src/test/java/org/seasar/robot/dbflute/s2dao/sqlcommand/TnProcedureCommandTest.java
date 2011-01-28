package org.seasar.robot.dbflute.s2dao.sqlcommand;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class TnProcedureCommandTest extends PlainTestCase {

    public void test_doBuildSqlAsCalledBySelect_Basic() throws Exception {
        // ## Arrange ##
        TnProcedureCommand target = createTarget();

        // ## Act ##
        String sql = target.doBuildSqlAsCalledBySelect("SP_FOO", 3);

        // ## Assert ##
        log(sql);
        assertEquals("select * from SP_FOO(?, ?, ?)", sql);
    }

    public void test_doBuildSqlAsProcedureCall_kakou() throws Exception {
        // ## Arrange ##
        TnProcedureCommand target = createTarget();

        // ## Act ##
        String sql = target.doBuildSqlAsProcedureCall("SP_FOO", 3, true, true);

        // ## Assert ##
        log(sql);
        assertEquals("{? = call SP_FOO(?, ?)}", sql);
    }

    public void test_doBuildSqlAsProcedureCall_kakowanai() throws Exception {
        // ## Arrange ##
        TnProcedureCommand target = createTarget();

        // ## Act ##
        String sql = target.doBuildSqlAsProcedureCall("SP_FOO", 3, true, false);

        // ## Assert ##
        log(sql);
        assertEquals("? = call SP_FOO(?, ?)", sql);
    }

    protected TnProcedureCommand createTarget() {
        return new TnProcedureCommand(null, null, null, null) {
            @Override
            protected void assertObjectNotNull(String variableName, Object value) {
                // for no check of constructor
            }
        };
    }
}
