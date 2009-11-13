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
public class TnBasicHandlerTest extends PlainTestCase {

    public void test_logSql_whitebox_nothing() {
        // ## Arrange ##
        TnBasicHandler handler = new TnBasicHandler(null, null) {
            @Override
            protected String getDisplaySql(Object[] args) {
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
        };

        // ## Act & Assert ##
        handler.logSql(null, null); // Expect no exception
    }

    public void test_logSql_whitebox_logEnabledOnly() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        TnBasicHandler handler = new TnBasicHandler(null, null) {
            @Override
            protected String getDisplaySql(Object[] args) {
                markList.add("getDisplaySql");
                return "select ...";
            }

            @Override
            protected void log(String msg) {
                assertEquals("select ...", msg);
                markList.add("log");
            }

            @Override
            protected void putObjectToMapContext(String key, Object value) {
                markList.add("putObjectToMapContext");
                super.putObjectToMapContext(key, value);
            }

            @Override
            protected boolean isLogEnabled() {
                return true;
            }
        };

        // ## Act ##
        try {
            handler.logSql(null, null);

            assertEquals("select ...", InternalMapContext.getObject("df:DisplaySql"));
        } finally {
            CallbackContext.clearCallbackContextOnThread();
            InternalMapContext.clearInternalMapContextOnThread();
        }

        // ## Assert ##
        assertEquals("getDisplaySql", markList.get(0));
        assertEquals("log", markList.get(1));
        assertEquals("putObjectToMapContext", markList.get(2));
    }

    public void test_logSql_whitebox_sqlLogHandlerOnly() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        final Object[] args = new Object[] {};
        final Class<?>[] argsTypes = new Class<?>[] {};
        TnBasicHandler handler = new TnBasicHandler(null, null) {
            @Override
            protected String getDisplaySql(Object[] args) {
                markList.add("getDisplaySql");
                return "select ...";
            }

            @Override
            protected void log(String msg) {
                throw new IllegalStateException("log should not be called!");
            }

            @Override
            protected void putObjectToMapContext(String key, Object value) {
                markList.add("putObjectToMapContext");
                super.putObjectToMapContext(key, value);
            }

            @Override
            protected boolean isLogEnabled() {
                return false;
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

            assertEquals("select ...", InternalMapContext.getObject("df:DisplaySql"));
        } finally {
            CallbackContext.clearCallbackContextOnThread();
            InternalMapContext.clearInternalMapContextOnThread();
        }

        // ## Assert ##
        assertEquals("getDisplaySql", markList.get(0));
        assertEquals("handle", markList.get(1));
        assertEquals("putObjectToMapContext", markList.get(2));
    }

    public void test_logSql_whitebox_sqlResultHandlerOnly() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        TnBasicHandler handler = new TnBasicHandler(null, null) {
            @Override
            protected String getDisplaySql(Object[] args) {
                markList.add("getDisplaySql");
                return "select ...";
            }

            @Override
            protected void log(String msg) {
                throw new IllegalStateException("log should not be called!");
            }

            @Override
            protected void putObjectToMapContext(String key, Object value) {
                markList.add("putObjectToMapContext");
                super.putObjectToMapContext(key, value);
            }

            @Override
            protected boolean isLogEnabled() {
                return false;
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

            assertEquals("select ...", InternalMapContext.getObject("df:DisplaySql"));
        } finally {
            CallbackContext.clearCallbackContextOnThread();
            InternalMapContext.clearInternalMapContextOnThread();
        }

        // ## Assert ##
        assertEquals("getDisplaySql", markList.get(0));
        assertEquals("putObjectToMapContext", markList.get(1));
    }

    public void test_logSql_whitebox_bigThree() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        final Object[] args = new Object[] {};
        final Class<?>[] argsTypes = new Class<?>[] {};
        TnBasicHandler handler = new TnBasicHandler(null, null) {
            @Override
            protected String getDisplaySql(Object[] args) {
                markList.add("getDisplaySql");
                return "select ..." + ln() + "  from ...";
            }

            @Override
            protected boolean isContainsLineSeparatorInSql() {
                markList.add("isContainsLineSeparatorInSql");
                return true;
            }

            @Override
            protected void log(String msg) {
                assertEquals(ln() + "select ..." + ln() + "  from ...", msg);
                markList.add("log");
            }

            @Override
            protected void putObjectToMapContext(String key, Object value) {
                markList.add("putObjectToMapContext");
                super.putObjectToMapContext(key, value);
            }

            @Override
            protected boolean isLogEnabled() {
                return true;
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

            assertEquals("select ..." + ln() + "  from ...", InternalMapContext.getObject("df:DisplaySql"));
        } finally {
            CallbackContext.clearCallbackContextOnThread();
            InternalMapContext.clearInternalMapContextOnThread();
        }

        // ## Assert ##
        assertEquals("getDisplaySql", markList.get(0));
        assertEquals("isContainsLineSeparatorInSql", markList.get(1));
        assertEquals("log", markList.get(2));
        assertEquals("handle", markList.get(3));
        assertEquals("putObjectToMapContext", markList.get(4));
        assertEquals(5, markList.size());
    }
}
