package org.seasar.robot.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seasar.robot.dbflute.mock.MockResultSet;
import org.seasar.robot.dbflute.mock.MockValueType;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.6.4 (2010/01/22 Friday)
 */
public class TnScalarDynamicResultSetHandlerTest extends PlainTestCase {

    public void test_handle_scalar() throws SQLException {
        // ## Arrange ##
        MockValueType valueType = new MockValueType() {
            @Override
            public Object getValue(ResultSet resultSet, int index) throws SQLException {
                assertEquals(1, index);
                return 99;
            }
        };
        TnScalarDynamicResultSetHandler handler = new TnScalarDynamicResultSetHandler(valueType) {
            @Override
            protected <ELEMENT> List<ELEMENT> newArrayList() {
                fail("should not be called");
                return null;
            }
        };
        MockResultSet rs = new MockResultSet() {
            private boolean called;

            @Override
            public boolean next() throws SQLException {
                try {
                    return !called;
                } finally {
                    called = true;
                }
            }
        };

        // ## Act ##
        Object actual = handler.handle(rs);

        // ## Assert ##
        log("actual=" + actual);
        assertEquals(99, actual);
    }

    public void test_handle_scalarList() throws SQLException {
        // ## Arrange ##
        MockValueType valueType = new MockValueType() {
            private int count = 0;

            @Override
            public Object getValue(ResultSet resultSet, int index) throws SQLException {
                return ++count; // 1, 2, 3, ...
            }
        };
        final String mark = "called";
        final Set<String> markSet = new HashSet<String>();
        TnScalarDynamicResultSetHandler handler = new TnScalarDynamicResultSetHandler(valueType) {
            @Override
            protected <ELEMENT> List<ELEMENT> newArrayList() {
                if (markSet.contains(mark)) {
                    fail("should not be called twice");
                }
                markSet.add(mark);
                return super.newArrayList();
            }
        };
        MockResultSet rs = new MockResultSet() {
            private List<Integer> countList = new ArrayList<Integer>();
            {
                countList.add(1);
                countList.add(2);
                countList.add(3);
            }

            @Override
            public boolean next() throws SQLException {
                if (countList.isEmpty()) {
                    return false;
                }
                countList.remove(0);
                return true;
            }
        };

        // ## Act ##
        Object actual = handler.handle(rs);

        // ## Assert ##
        log("actual=" + actual);
        assertEquals(Arrays.asList(new Integer[] { 1, 2, 3 }), actual);
        assertTrue(markSet.contains(mark));
    }
}
