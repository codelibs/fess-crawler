package org.seasar.robot.dbflute.bhv.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.seasar.robot.dbflute.CallbackContext;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.cbean.FetchAssistContext;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.cbean.EntityRowHandler;
import org.seasar.robot.dbflute.jdbc.SqlResultHandler;
import org.seasar.robot.dbflute.jdbc.SqlResultInfo;
import org.seasar.robot.dbflute.mock.MockConditionBean;
import org.seasar.robot.dbflute.mock.MockOutsideSqlContext;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlOption;
import org.seasar.robot.dbflute.resource.InternalMapContext;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.1 (2009/02/03 Tuesday)
 */
public class BehaviorCommandInvokerTest extends PlainTestCase {

    public void test_dispatchInvoking_whitebox_logEnabled() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        final Object result = new Object();
        final Object[] args = new Object[] { "foo", "bar" };
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker() {
            @Override
            protected void setupResourceContext() {
                markList.add("setupResourceContext");
            }

            @Override
            protected boolean isLogEnabled() {
                markList.add("isLogEnabled");
                return true;
            }

            @Override
            protected <RESULT> void initializeSqlExecution(BehaviorCommand<RESULT> behaviorCommand) {
                throw new IllegalStateException("initializeSqlExecution should not be called!");
            }

            @Override
            protected <RESULT> SqlExecution findSqlExecution(BehaviorCommand<RESULT> behaviorCommand) {
                markList.add("findSqlExecution");
                return new SqlExecution() {
                    public Object execute(Object[] actualArgs) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }
                        markList.add("SqlExecution.execute");
                        assertEquals(args[0], actualArgs[0]);
                        assertEquals(args[1], actualArgs[1]);
                        return result;
                    }
                };
            }

            @Override
            protected long deriveCommandBeforeAfterTimeIfNeeds(boolean logEnabled, boolean existsSqlResultHandler) {
                if (markList.contains("deriveCommandBeforeAfterTimeIfNeeds")) {
                    markList.add("deriveCommandBeforeAfterTimeIfNeeds2");
                } else {
                    markList.add("deriveCommandBeforeAfterTimeIfNeeds");
                }
                return super.deriveCommandBeforeAfterTimeIfNeeds(logEnabled, existsSqlResultHandler);
            }

            @Override
            protected <RESULT> void logReturn(BehaviorCommand<RESULT> behaviorCommand, Class<?> retType, Object ret,
                    long before, long after) {
                markList.add("logReturn");
                log("before=" + before + ", after=" + after);
                assertTrue(before > 0);
                assertTrue(after > 0);
                assertTrue((after - before) > 999);
            }
        };

        // ## Act ##
        final Object actualResult = invoker.dispatchInvoking(new MockBehaviorCommand() {
            @Override
            public Object[] getSqlExecutionArgument() {
                return args;
            }
        });

        // ## Assert ##
        assertEquals(result, actualResult);
        assertEquals("setupResourceContext", markList.get(0));
        assertEquals("isLogEnabled", markList.get(1));
        assertEquals("findSqlExecution", markList.get(2));
        assertEquals("deriveCommandBeforeAfterTimeIfNeeds", markList.get(3));
        assertEquals("SqlExecution.execute", markList.get(4));
        assertEquals("deriveCommandBeforeAfterTimeIfNeeds2", markList.get(5));
        assertEquals("logReturn", markList.get(6));
        assertEquals(7, markList.size());
    }

    public void test_dispatchInvoking_whitebox_logDisabled() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        final Object result = new Object();
        final Object[] args = new Object[] { "foo", "bar" };
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker() {
            @Override
            protected void setupResourceContext() {
                markList.add("setupResourceContext");
            }

            @Override
            protected boolean isLogEnabled() {
                markList.add("isLogEnabled");
                return false;
            }

            @Override
            protected <RESULT> void initializeSqlExecution(BehaviorCommand<RESULT> behaviorCommand) {
                throw new IllegalStateException("initializeSqlExecution should not be called!");
            }

            @Override
            protected <RESULT> SqlExecution findSqlExecution(BehaviorCommand<RESULT> behaviorCommand) {
                markList.add("findSqlExecution");
                return new SqlExecution() {
                    public Object execute(Object[] actualArgs) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }
                        markList.add("SqlExecution.execute");
                        assertEquals(args[0], actualArgs[0]);
                        assertEquals(args[1], actualArgs[1]);
                        return result;
                    }
                };
            }

            @Override
            protected long deriveCommandBeforeAfterTimeIfNeeds(boolean logEnabled, boolean existsSqlResultHandler) {
                if (markList.contains("deriveCommandBeforeAfterTimeIfNeeds")) {
                    markList.add("deriveCommandBeforeAfterTimeIfNeeds2");
                } else {
                    markList.add("deriveCommandBeforeAfterTimeIfNeeds");
                }
                return super.deriveCommandBeforeAfterTimeIfNeeds(logEnabled, existsSqlResultHandler);
            }

            @Override
            protected <RESULT> void logReturn(BehaviorCommand<RESULT> behaviorCommand, Class<?> retType, Object ret,
                    long before, long after) {
                throw new IllegalStateException("logReturn() should not be called!");
            }

            @Override
            protected long systemTime() {
                throw new IllegalStateException("systemTime() should not be called!");
            }
        };

        // ## Act ##
        final Object actualResult = invoker.dispatchInvoking(new MockBehaviorCommand() {
            @Override
            public Object[] getSqlExecutionArgument() {
                return args;
            }
        });

        // ## Assert ##
        assertEquals(result, actualResult);
        assertEquals("setupResourceContext", markList.get(0));
        assertEquals("isLogEnabled", markList.get(1));
        assertEquals("findSqlExecution", markList.get(2));
        assertEquals("deriveCommandBeforeAfterTimeIfNeeds", markList.get(3));
        assertEquals("SqlExecution.execute", markList.get(4));
        assertEquals("deriveCommandBeforeAfterTimeIfNeeds2", markList.get(5));
        assertEquals(6, markList.size());
    }

    public void test_dispatchInvoking_whitebox_logDisabled_sqlResultHandler() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        final Object result = new Object();
        final Object[] args = new Object[] { "foo", "bar" };
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker() {
            @Override
            protected void setupResourceContext() {
                markList.add("setupResourceContext");
            }

            @Override
            protected boolean isLogEnabled() {
                markList.add("isLogEnabled");
                return false;
            }

            @Override
            protected <RESULT> void initializeSqlExecution(BehaviorCommand<RESULT> behaviorCommand) {
                throw new IllegalStateException("initializeSqlExecution should not be called!");
            }

            @Override
            protected <RESULT> SqlExecution findSqlExecution(BehaviorCommand<RESULT> behaviorCommand) {
                markList.add("findSqlExecution");
                return new SqlExecution() {
                    public Object execute(Object[] actualArgs) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new IllegalStateException(e);
                        }
                        markList.add("SqlExecution.execute");
                        assertEquals(args[0], actualArgs[0]);
                        assertEquals(args[1], actualArgs[1]);
                        return result;
                    }
                };
            }

            @Override
            protected long deriveCommandBeforeAfterTimeIfNeeds(boolean logEnabled, boolean existsSqlResultHandler) {
                if (markList.contains("deriveCommandBeforeAfterTimeIfNeeds")) {
                    markList.add("deriveCommandBeforeAfterTimeIfNeeds2");
                } else {
                    markList.add("deriveCommandBeforeAfterTimeIfNeeds");
                }
                return super.deriveCommandBeforeAfterTimeIfNeeds(logEnabled, existsSqlResultHandler);
            }

            @Override
            protected <RESULT> void logReturn(BehaviorCommand<RESULT> behaviorCommand, Class<?> retType, Object ret,
                    long before, long after) {
                throw new IllegalStateException("logReturn() should not be called!");
            }
        };

        // ## Act ##
        final Object actualResult;
        try {
            CallbackContext callbackContext = new CallbackContext();
            callbackContext.setSqlResultHandler(new SqlResultHandler() {
                public void handle(SqlResultInfo info) {
                    markList.add("handle");
                    long before = info.getBeforeTimeMillis();
                    long after = info.getAfterTimeMillis();
                    log("before=" + before + ", after=" + after);
                    assertTrue(before > 0);
                    assertTrue(after > 0);
                    assertTrue((after - before) > 999);
                }
            });
            CallbackContext.setCallbackContextOnThread(callbackContext);
            InternalMapContext.setObject("df:DisplaySql", "select ...");
            actualResult = invoker.dispatchInvoking(new MockBehaviorCommand() {
                @Override
                public Object[] getSqlExecutionArgument() {
                    return args;
                }
            });
        } finally {
            CallbackContext.clearCallbackContextOnThread();
            InternalMapContext.clearInternalMapContextOnThread();
        }

        // ## Assert ##
        assertEquals(result, actualResult);
        assertEquals("setupResourceContext", markList.get(0));
        assertEquals("isLogEnabled", markList.get(1));
        assertEquals("findSqlExecution", markList.get(2));
        assertEquals("deriveCommandBeforeAfterTimeIfNeeds", markList.get(3));
        assertEquals("SqlExecution.execute", markList.get(4));
        assertEquals("deriveCommandBeforeAfterTimeIfNeeds2", markList.get(5));
        assertEquals("handle", markList.get(6));
        assertEquals(7, markList.size());
    }

    public void test_dispatchInvoking_initializeOnly() {
        // ## Arrange ##
        final List<String> markList = new ArrayList<String>();
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker() {
            @Override
            protected void setupResourceContext() {
                markList.add("setupResourceContext");
            }

            @Override
            protected boolean isLogEnabled() {
                markList.add("isLogEnabled");
                return true;
            }

            @Override
            protected <RESULT> void initializeSqlExecution(BehaviorCommand<RESULT> behaviorCommand) {
                markList.add("initializeSqlExecution");
            }

            @Override
            protected <RESULT> SqlExecution findSqlExecution(BehaviorCommand<RESULT> behaviorCommand) {
                throw new IllegalStateException("findSqlExecution should not be called!");
            }

            @Override
            protected long deriveCommandBeforeAfterTimeIfNeeds(boolean logEnabled, boolean existsSqlResultHandler) {
                throw new IllegalStateException("deriveCommandBeforeAfterTimeIfNeeds should not be called!");
            }

            @Override
            protected <RESULT> void logReturn(BehaviorCommand<RESULT> behaviorCommand, Class<?> retType, Object ret,
                    long before, long after) {
                throw new IllegalStateException("logReturn should not be called!");
            }
        };

        // ## Act ##
        final Object actualResult = invoker.dispatchInvoking(new MockBehaviorCommand() {
            @Override
            public boolean isInitializeOnly() {
                return true;
            }
        });

        // ## Assert ##
        assertNull(actualResult);
        assertEquals("setupResourceContext", markList.get(0));
        assertEquals("isLogEnabled", markList.get(1));
        assertEquals("initializeSqlExecution", markList.get(2));
        assertEquals(3, markList.size());
    }

    protected static class MockBehaviorCommand implements BehaviorCommand<Object> {
        public void afterExecuting() {
        }

        public void beforeGettingSqlExecution() {
        }

        public String buildSqlExecutionKey() {
            throw new UnsupportedOperationException();
        }

        public SqlExecutionCreator createSqlExecutionCreator() {
            throw new UnsupportedOperationException();
        }

        public String getCommandName() {
            return "FooCommand";
        }

        public Class<?> getCommandReturnType() {
            return Object.class;
        }

        public ConditionBean getConditionBean() {
            throw new UnsupportedOperationException();
        }

        public OutsideSqlOption getOutsideSqlOption() {
            throw new UnsupportedOperationException();
        }

        public String getOutsideSqlPath() {
            throw new UnsupportedOperationException();
        }

        public Object[] getSqlExecutionArgument() {
            return new Object[] {};
        }

        public String getTableDbName() {
            return "FooTable";
        }

        public boolean isConditionBean() {
            return false;
        }

        public boolean isInitializeOnly() {
            return false;
        }

        public boolean isOutsideSql() {
            return false;
        }

        public boolean isProcedure() {
            return false;
        }

        public boolean isSelect() {
            return false;
        }

        public boolean isSelectCount() {
            return false;
        }
    }

    public void test_getSqlExecution_threadSafe() {
        // ## Arrange & Act & Assert ##
        // Try Five Times!
        // Expect no exception
        getSqlExecution_on_multiple_thread();
        getSqlExecution_on_multiple_thread();
        getSqlExecution_on_multiple_thread();
        getSqlExecution_on_multiple_thread();
        getSqlExecution_on_multiple_thread();
    }

    protected void getSqlExecution_on_multiple_thread() {
        // ## Arrange ##
        final ExecutorService service = Executors.newCachedThreadPool();
        final Set<String> markSet = new HashSet<String>();
        final BehaviorCommandInvoker invoker = new BehaviorCommandInvoker() {
            @Override
            protected void toBeDisposable() {
                markSet.add("toBeDisposable");
            }
        };
        final ExecutionCreationCount count = new ExecutionCreationCount();

        // ## Act ##
        Future<?> future1 = service.submit(createTestCallable(invoker, count));
        Future<?> future2 = service.submit(createTestCallable(invoker, count));
        Future<?> future3 = service.submit(createTestCallable(invoker, count));
        Future<?> future4 = service.submit(createTestCallable(invoker, count));
        Future<?> future5 = service.submit(createTestCallable(invoker, count));
        Future<?> future6 = service.submit(createTestCallable(invoker, count));
        Future<?> future7 = service.submit(createTestCallable(invoker, count));
        Future<?> future8 = service.submit(createTestCallable(invoker, count));
        Future<?> future9 = service.submit(createTestCallable(invoker, count));
        Future<?> future10 = service.submit(createTestCallable(invoker, count));

        waitFor(future1, future2, future3, future4, future5, future6, future7, future8, future9, future10);
        log("All threads are finished!");

        // ## Assert ##
        int expectedCreatedCount = 100;
        assertEquals(expectedCreatedCount, invoker._executionMap.size());
        assertEquals(expectedCreatedCount, count.count());
        assertTrue(markSet.contains("toBeDisposable"));
    }

    protected void waitFor(Future<?>... futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            } catch (ExecutionException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    protected Callable<List<SqlExecution>> createTestCallable(final BehaviorCommandInvoker invoker,
            final ExecutionCreationCount count) {
        return new Callable<List<SqlExecution>>() {
            public List<SqlExecution> call() {
                SqlExecutionCreator creator = new SqlExecutionCreator() {
                    public SqlExecution createSqlExecution() {
                        count.increment();
                        return new SqlExecution() {
                            public Object execute(Object[] args) {
                                return null;
                            }
                        };
                    }
                };
                final List<SqlExecution> resultList = new ArrayList<SqlExecution>();
                for (int i = 0; i < 100; i++) {
                    resultList.add(invoker.getSqlExecution("key" + i, creator));
                }
                return resultList;
            }
        };
    }

    protected static class ExecutionCreationCount {
        protected int _count;

        public void increment() {
            ++_count;
        }

        public int count() {
            return _count;
        }
    }

    public void test_clearContext() {
        // ## Arrange ##
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker();
        OutsideSqlContext.setOutsideSqlContextOnThread(new MockOutsideSqlContext());
        FetchAssistContext.setFetchBeanOnThread(new MockConditionBean());
        ConditionBeanContext.setConditionBeanOnThread(new MockConditionBean());
        ConditionBeanContext.setEntityRowHandlerOnThread(new EntityRowHandler<Entity>() {
            public void handle(Entity entity) {
            }
        });
        InternalMapContext.setObject("dummy", new Object());
        ResourceContext.setResourceContextOnThread(new ResourceContext());

        assertTrue(OutsideSqlContext.isExistOutsideSqlContextOnThread());
        assertTrue(FetchAssistContext.isExistFetchNarrowingBeanOnThread());
        assertTrue(ConditionBeanContext.isExistConditionBeanOnThread());
        assertTrue(ConditionBeanContext.isExistEntityRowHandlerOnThread());
        assertTrue(InternalMapContext.isExistInternalMapContextOnThread());
        assertTrue(ResourceContext.isExistResourceContextOnThread());

        // ## Act ##
        invoker.clearContext();

        // ## Assert ##
        assertFalse(OutsideSqlContext.isExistOutsideSqlContextOnThread());
        assertFalse(FetchAssistContext.isExistFetchNarrowingBeanOnThread());
        assertFalse(ConditionBeanContext.isExistConditionBeanOnThread());
        assertFalse(ConditionBeanContext.isExistEntityRowHandlerOnThread());
        assertFalse(InternalMapContext.isExistInternalMapContextOnThread());
        assertFalse(ResourceContext.isExistResourceContextOnThread());
    }

    public void test_deriveCommandBeforeAfterTimeIfNeeds() {
        // ## Arrange ##
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker();

        // ## Act & Assert ##
        assertEquals(0, invoker.deriveCommandBeforeAfterTimeIfNeeds(false, false));
        assertTrue(invoker.deriveCommandBeforeAfterTimeIfNeeds(true, false) > 0);
        assertTrue(invoker.deriveCommandBeforeAfterTimeIfNeeds(false, true) > 0);
        assertTrue(invoker.deriveCommandBeforeAfterTimeIfNeeds(true, true) > 0);
    }

    public void test_callbackSqlResultHanler_basic() {
        // ## Arrange ##
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker();
        final long before = 123;
        final long after = 456;
        final Object ret = new Object();
        final HashSet<String> markSet = new HashSet<String>();
        try {
            InternalMapContext.setObject("df:DisplaySql", "select ...");

            // ## Act & Assert ##
            invoker.callbackSqlResultHanler(new MockBehaviorCommand() {
                @Override
                public String getTableDbName() {
                    return "FOO";
                }

                @Override
                public String getCommandName() {
                    return "BAR";
                }
            }, true, new SqlResultHandler() {
                public void handle(SqlResultInfo info) {
                    long actualBefore = info.getBeforeTimeMillis();
                    long actualAfter = info.getAfterTimeMillis();
                    assertEquals(ret, info.getResult());
                    assertEquals("FOO", info.getTableDbName());
                    assertEquals("BAR", info.getCommandName());
                    assertEquals("select ...", info.getDisplaySql());
                    assertEquals(before, actualBefore);
                    assertEquals(after, actualAfter);
                    markSet.add("handle()");
                    log(info.getResult() + ":" + info.getDisplaySql() + ":" + actualBefore + ":" + actualAfter);
                }
            }, ret, before, after);
            assertTrue(markSet.size() == 1);
            assertTrue(markSet.contains("handle()"));
        } finally {
            InternalMapContext.clearInternalMapContextOnThread();
        }
    }

    public void test_callbackSqlResultHanler_notExistsDisplaySql() {
        // ## Arrange ##
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker();
        final long before = 123;
        final long after = 456;
        final Object ret = new Object();
        final HashSet<String> markSet = new HashSet<String>();
        try {
            // ## Act & Assert ##
            invoker.callbackSqlResultHanler(new MockBehaviorCommand() {
                @Override
                public String getTableDbName() {
                    return "FOO";
                }

                @Override
                public String getCommandName() {
                    return "BAR";
                }
            }, true, new SqlResultHandler() {
                public void handle(SqlResultInfo info) {
                    long actualBefore = info.getBeforeTimeMillis();
                    long actualAfter = info.getAfterTimeMillis();
                    assertEquals(ret, info.getResult());
                    assertEquals("FOO", info.getTableDbName());
                    assertEquals("BAR", info.getCommandName());
                    assertEquals(null, info.getDisplaySql());
                    assertEquals(before, actualBefore);
                    assertEquals(after, actualAfter);
                    markSet.add("handle()");
                    log(info.getResult() + ":" + info.getDisplaySql() + ":" + actualBefore + ":" + actualAfter);
                }
            }, ret, before, after);
            assertTrue(markSet.size() == 1);
            assertTrue(markSet.contains("handle()"));
        } finally {
            InternalMapContext.clearInternalMapContextOnThread();
        }
    }

    public void test_systemTime() {
        // ## Arrange ##
        BehaviorCommandInvoker invoker = new BehaviorCommandInvoker();

        // ## Act & Assert ##
        assertTrue(invoker.systemTime() > 0);
    }
}
