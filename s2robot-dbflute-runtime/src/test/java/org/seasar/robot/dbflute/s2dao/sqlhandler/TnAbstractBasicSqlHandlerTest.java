package org.seasar.robot.dbflute.s2dao.sqlhandler;

import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.CallbackContext;
import org.seasar.robot.dbflute.jdbc.SqlLogHandler;
import org.seasar.robot.dbflute.jdbc.SqlResultHandler;
import org.seasar.robot.dbflute.jdbc.SqlResultInfo;
import org.seasar.robot.dbflute.resource.InternalMapContext;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.5.1 (2009/06/19 Friday)
 */
public class TnAbstractBasicSqlHandlerTest extends PlainTestCase {

    public void test_logSql_whitebox_nothing() {
        // ## Arrange ##
        TnAbstractBasicSqlHandler handler = new TnAbstractBasicSqlHandler(null, null, null) {
            @Override
            protected String buildDisplaySql(Object[] args) {
                throw new IllegalStateException("log should not be called!");
            }

            @Override
            protected void log(String msg) {
                throw new IllegalStateException("log should not be called!");
            }

            @Override
            protected boolean isLogEnabled() {
                return false;
            }

            @Override
            protected void assertObjectNotNull(String variableName, Object value) {
                // for no check of constructor
            }
        };

        // ## Act & Assert ##
        handler.logSql(null, null); // Expect no exception
    }

    public void test_logSql_whitebox_logEnabledOnly() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        TnAbstractBasicSqlHandler handler = new TnAbstractBasicSqlHandler(null, null, null) {
            @Override
            protected String buildDisplaySql(Object[] args) {
                markList.add("getDisplaySql");
                return "select ...";
            }

            @Override
            protected void logDisplaySql(String displaySql) {
                assertEquals("select ...", displaySql);
                markList.add("log");
            }

            @Override
            protected void saveDisplaySqlForResultInfo(String displaySql) {
                markList.add("saveDisplaySqlForResultInfo");
                super.saveDisplaySqlForResultInfo(displaySql);
            }

            @Override
            protected boolean isLogEnabled() {
                return true;
            }

            @Override
            protected void assertObjectNotNull(String variableName, Object value) {
                // for no check of constructor
            }
        };

        // ## Act ##
        try {
            handler.logSql(null, null);

            assertNull(InternalMapContext.getResultInfoDisplaySql());
        } finally {
            CallbackContext.clearCallbackContextOnThread();
            InternalMapContext.clearInternalMapContextOnThread();
        }

        // ## Assert ##
        assertEquals(2, markList.size());
        assertEquals("getDisplaySql", markList.get(0));
        assertEquals("log", markList.get(1));
    }

    public void test_logSql_whitebox_sqlLogHandlerOnly() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        final Object[] args = new Object[] {};
        final Class<?>[] argsTypes = new Class<?>[] {};
        TnAbstractBasicSqlHandler handler = new TnAbstractBasicSqlHandler(null, null, null) {
            @Override
            protected String buildDisplaySql(Object[] args) {
                markList.add("getDisplaySql");
                return "select ...";
            }

            @Override
            protected void logDisplaySql(String displaySql) {
                throw new IllegalStateException("log should not be called!");
            }

            @Override
            protected void log(String msg) {
                throw new IllegalStateException("log should not be called!");
            }

            @Override
            protected void saveDisplaySqlForResultInfo(String displaySql) {
                markList.add("saveDisplaySqlForResultInfo");
                super.saveDisplaySqlForResultInfo(displaySql);
            }

            @Override
            protected boolean isLogEnabled() {
                return false;
            }

            @Override
            protected void assertObjectNotNull(String variableName, Object value) {
                // for no check of constructor
            }
        };

        // ## Act ##
        try {
            CallbackContext callbackContext = new CallbackContext();
            callbackContext.setSqlLogHandler(new SqlLogHandler() {
                public void handle(String executedSql, String displaySql, Object[] actualArgs, Class<?>[] actualArgTypes) {
                    markList.add("handle");
                    assertEquals("select ...", displaySql);
                    assertEquals(args, actualArgs);
                    assertEquals(argsTypes, actualArgTypes);
                }
            });
            CallbackContext.setCallbackContextOnThread(callbackContext);
            handler.logSql(args, argsTypes);

            assertNull(InternalMapContext.getResultInfoDisplaySql());
        } finally {
            CallbackContext.clearCallbackContextOnThread();
            InternalMapContext.clearInternalMapContextOnThread();
        }

        // ## Assert ##
        assertEquals(2, markList.size());
        assertEquals("getDisplaySql", markList.get(0));
        assertEquals("handle", markList.get(1));
    }

    public void test_logSql_whitebox_sqlResultHandlerOnly() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        TnAbstractBasicSqlHandler handler = new TnAbstractBasicSqlHandler(null, null, null) {
            @Override
            protected String buildDisplaySql(Object[] args) {
                markList.add("getDisplaySql");
                return "select ...";
            }

            @Override
            protected void logDisplaySql(String displaySql) {
                throw new IllegalStateException("log should not be called!");
            }

            @Override
            protected void log(String msg) {
                throw new IllegalStateException("log should not be called!");
            }

            @Override
            protected void saveDisplaySqlForResultInfo(String displaySql) {
                markList.add("saveDisplaySqlForResultInfo");
                super.saveDisplaySqlForResultInfo(displaySql);
            }

            @Override
            protected boolean isLogEnabled() {
                return false;
            }

            @Override
            protected void assertObjectNotNull(String variableName, Object value) {
                // for no check of constructor
            }
        };

        // ## Act ##
        try {
            CallbackContext callbackContext = new CallbackContext();
            callbackContext.setSqlResultHandler(new SqlResultHandler() {
                public void handle(SqlResultInfo sqlResultInfo) {
                    throw new IllegalStateException("handle should not be called!");
                }
            });
            CallbackContext.setCallbackContextOnThread(callbackContext);
            handler.logSql(null, null);

            assertEquals("select ...", InternalMapContext.getResultInfoDisplaySql());
        } finally {
            CallbackContext.clearCallbackContextOnThread();
            InternalMapContext.clearInternalMapContextOnThread();
        }

        // ## Assert ##
        assertEquals(2, markList.size());
        assertEquals("getDisplaySql", markList.get(0));
        assertEquals("saveDisplaySqlForResultInfo", markList.get(1));
    }

    public void test_logSql_whitebox_bigThree() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        final Object[] args = new Object[] {};
        final Class<?>[] argsTypes = new Class<?>[] {};
        TnAbstractBasicSqlHandler handler = new TnAbstractBasicSqlHandler(null, null, null) {
            @Override
            protected String buildDisplaySql(Object[] args) {
                markList.add("getDisplaySql");
                return "select ..." + ln() + "  from ...";
            }

            @Override
            protected void logDisplaySql(String displaySql) {
                markList.add("logDisplaySql");
                assertEquals("select ..." + ln() + "  from ...", displaySql);
                super.logDisplaySql(displaySql);
            }

            @Override
            protected void log(String msg) {
                markList.add("log");
                assertEquals(ln() + "select ..." + ln() + "  from ...", msg);
            }

            @Override
            protected void saveDisplaySqlForResultInfo(String displaySql) {
                markList.add("saveDisplaySqlForResultInfo");
                super.saveDisplaySqlForResultInfo(displaySql);
            }

            @Override
            protected boolean isLogEnabled() {
                return true;
            }

            @Override
            protected void assertObjectNotNull(String variableName, Object value) {
                // for no check of constructor
            }
        };

        // ## Act ##
        try {
            CallbackContext callbackContext = new CallbackContext();
            callbackContext.setSqlLogHandler(new SqlLogHandler() {
                public void handle(String executedSql, String displaySql, Object[] actualArgs, Class<?>[] actualArgTypes) {
                    markList.add("handle");
                    assertEquals("select ..." + ln() + "  from ...", displaySql);
                    assertEquals(args, actualArgs);
                    assertEquals(argsTypes, actualArgTypes);
                }
            });
            callbackContext.setSqlResultHandler(new SqlResultHandler() {
                public void handle(SqlResultInfo sqlResultInfo) {
                    throw new IllegalStateException("handle should not be called!");
                }
            });
            CallbackContext.setCallbackContextOnThread(callbackContext);
            handler.logSql(args, argsTypes);

            assertEquals("select ..." + ln() + "  from ...", InternalMapContext.getResultInfoDisplaySql());
        } finally {
            CallbackContext.clearCallbackContextOnThread();
            InternalMapContext.clearInternalMapContextOnThread();
        }

        // ## Assert ##
        assertEquals(5, markList.size());
        assertEquals("getDisplaySql", markList.get(0));
        assertEquals("logDisplaySql", markList.get(1));
        assertEquals("log", markList.get(2));
        assertEquals("handle", markList.get(3));
        assertEquals("saveDisplaySqlForResultInfo", markList.get(4));
    }
}
