package org.seasar.robot.dbflute.bhv.core.supplement;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.exception.SequenceCacheSizeNotDividedIncrementSizeException;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfStringUtil;

/**
 * @author jflute
 * @since 0.9.6.4 (2010/01/15 Friday)
 */
public class SequenceCacheHandlerTest extends PlainTestCase {

    // ===================================================================================
    //                                                                 findSequenceCache()
    //                                                                 ===================
    public void test_findSequenceCache_defaultKeyGenerator() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        DataSource dataSource = new MyDataSource();
        DataSource dataSource2 = new MyDataSource();

        // ## Act ##
        SequenceCache cache = handler.findSequenceCache("HOO", "FOO", dataSource, Long.class, 50, 1);
        SequenceCache sameCache = handler.findSequenceCache("HOO", "FOO", dataSource, Long.class, 50, 1);
        SequenceCache diffNameCache = handler.findSequenceCache("HOO", "BAR", dataSource, Long.class, 50, 1);
        SequenceCache diffTableCache = handler.findSequenceCache("HEE", "FOO", dataSource2, Long.class, 50, 1);
        SequenceCache diffDsCache = handler.findSequenceCache("HOO", "FOO", dataSource2, Long.class, 50, 1);

        // ## Assert ##
        assertEquals(3, handler._sequenceCacheMap.size());
        assertEquals(Long.class, cache._resultType);
        assertEquals(new BigDecimal(50), cache._cacheSize);
        assertEquals(cache, sameCache);
        assertNotSame(cache, diffNameCache);
        assertNotSame(cache, diffTableCache);
        assertNotSame(diffNameCache, diffTableCache);
        assertEquals(cache, diffDsCache);
        assertNotSame(diffTableCache, diffDsCache);
    }

    public void test_findSequenceCache_dataSourceKeyGenerator() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        handler.setSequenceCacheKeyGenerator(new SequenceCacheKeyGenerator() {
            public String generateKey(String tableName, String sequenceName, DataSource dataSource) {
                return tableName + "." + sequenceName + "@" + dataSource.hashCode();
            }
        });
        DataSource dataSource = new MyDataSource();
        DataSource dataSource2 = new MyDataSource();

        // ## Act ##
        SequenceCache cache = handler.findSequenceCache("HOO", "FOO", dataSource, Long.class, 50, 1);
        SequenceCache sameCache = handler.findSequenceCache("HOO", "FOO", dataSource, Long.class, 50, 1);
        SequenceCache diffNameCache = handler.findSequenceCache("HOO", "BAR", dataSource, Long.class, 50, 1);
        SequenceCache diffDsCache = handler.findSequenceCache("HOO", "FOO", dataSource2, Long.class, 50, 1);

        // ## Assert ##
        assertEquals(3, handler._sequenceCacheMap.size());
        assertEquals(Long.class, cache._resultType);
        assertEquals(new BigDecimal(50), cache._cacheSize);
        assertEquals(cache, sameCache);
        assertNotSame(cache, diffNameCache);
        assertNotSame(cache, diffDsCache);
        assertNotSame(diffNameCache, diffDsCache);
    }

    public void test_findSequenceCache_incrementSize_null() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        DataSource dataSource = new MyDataSource();

        // ## Act ##
        SequenceCache cache = handler.findSequenceCache("HOO", "FOO", dataSource, Integer.class, null, 1);

        // ## Assert ##
        assertEquals(0, handler._sequenceCacheMap.size());
        assertNull(cache);
    }

    public void test_findSequenceCache_incrementSize_zero() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        DataSource dataSource = new MyDataSource();

        // ## Act ##
        SequenceCache cache = handler.findSequenceCache("HOO", "FOO", dataSource, Integer.class, 0, 0);

        // ## Assert ##
        assertEquals(0, handler._sequenceCacheMap.size());
        assertNull(cache);
    }

    public void test_findSequenceCache_incrementSize_one() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        DataSource dataSource = new MyDataSource();

        // ## Act ##
        SequenceCache cache = handler.findSequenceCache("HOO", "FOO", dataSource, Integer.class, 1, 1);

        // ## Assert ##
        assertEquals(0, handler._sequenceCacheMap.size());
        assertNull(cache);
    }

    public void test_findSequenceCache_incrementSize_two() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        DataSource dataSource = new MyDataSource();

        // ## Act ##
        SequenceCache cache = handler.findSequenceCache("HOO", "FOO", dataSource, Integer.class, 2, 2);

        // ## Assert ##
        assertEquals(1, handler._sequenceCacheMap.size());
        assertNotNull(cache);
        assertEquals(Integer.class, cache._resultType);
        assertEquals(new BigDecimal(2), cache._cacheSize);
    }

    public void test_findSequenceCache_threadSafe_onlyOne() {
        // ## Arrange ##
        final SequenceCacheHandler handler = new SequenceCacheHandler();
        final DataSource dataSource = new MyDataSource();

        ExecutionCreator<SequenceCache> creator = new ExecutionCreator<SequenceCache>() {
            public Execution<SequenceCache> create() {
                return new Execution<SequenceCache>() {
                    public SequenceCache execute() {
                        return handler.findSequenceCache("HOO", "FOO", dataSource, Integer.class, 2, 1);
                    }
                };
            }
        };

        // ## Act & Assert ##
        log("...Executing all threads");
        HashSet<SequenceCache> allAllSet = new HashSet<SequenceCache>();
        for (int i = 0; i < 30; i++) {
            List<SequenceCache> resultList = fireSameExecution(creator);
            HashSet<SequenceCache> allSet = new HashSet<SequenceCache>();
            for (SequenceCache set : resultList) {
                allSet.add(set);
            }
            assertEquals(1, allSet.size());
            allAllSet.addAll(allSet);
        }
        assertEquals(1, allAllSet.size());
    }

    public void test_findSequenceCache_threadSafe_perThread() {
        // ## Arrange ##
        final SequenceCacheHandler handler = new SequenceCacheHandler();
        final DataSource dataSource = new MyDataSource();

        ExecutionCreator<SequenceCache> creator = new ExecutionCreator<SequenceCache>() {
            public Execution<SequenceCache> create() {
                return new Execution<SequenceCache>() {
                    public SequenceCache execute() {
                        long threadId = Thread.currentThread().getId();
                        return handler.findSequenceCache("HOO", threadId + "", dataSource, BigDecimal.class, 10, 1);
                    }
                };
            }
        };

        // ## Act & Assert ##
        log("...Executing all threads");
        HashSet<SequenceCache> allAllSet = new HashSet<SequenceCache>();
        for (int i = 0; i < 30; i++) {
            List<SequenceCache> resultList = fireSameExecution(creator);
            HashSet<SequenceCache> allSet = new HashSet<SequenceCache>();
            for (SequenceCache set : resultList) {
                allSet.add(set);
            }
            assertEquals(10, allSet.size());
            allAllSet.addAll(allSet);
        }
        assertEquals(300, allAllSet.size());
    }

    // ===================================================================================
    //                                                                  filterNextValSql()
    //                                                                  ==================
    public void test_filterNextValSql_same() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        String sql = "select next value for SEQ_MEMBER";

        // ## Act ##
        String actual = handler.filterNextValSql(50, 50, sql);

        // ## Assert ##
        log(actual);
        assertEquals(sql, actual);
        assertFalse(actual.contains("order by"));
    }

    public void test_filterNextValSql_same_Oracle() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        String sql = "select SEQ_MEMBER.nextval from dual";
        ResourceContext context = new ResourceContext();
        context.setCurrentDBDef(DBDef.Oracle);
        ResourceContext.setResourceContextOnThread(context);

        try {
            // ## Act ##
            String actual = handler.filterNextValSql(50, 50, sql);

            // ## Assert ##
            log(actual);
            assertEquals(sql, actual);
            assertFalse(actual.contains("order by"));
        } finally {
            ResourceContext.clearResourceContextOnThread();
        }
    }

    public void test_filterNextValSql_half() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        String sql = "select next value for SEQ_MEMBER";

        // ## Act ##
        String actual = handler.filterNextValSql(50, 25, sql);

        // ## Assert ##
        log(actual);
        assertEquals(sql + ln() + " union all " + ln() + sql + ln() + " order by 1 asc", actual);
    }

    public void test_filterNextValSql_half_Oracle() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        String sql = "select SEQ_MEMBER.nextval from dual";
        ResourceContext context = new ResourceContext();
        context.setCurrentDBDef(DBDef.Oracle);
        ResourceContext.setResourceContextOnThread(context);

        try {
            // ## Act ##
            String actual = handler.filterNextValSql(50, 25, sql);

            // ## Assert ##
            log(actual);
            assertTrue(actual.contains("nextval"));
            assertTrue(actual.contains("select * from dual"));
            assertTrue(actual.contains(" union all"));
            assertTrue(actual.contains(" join_1"));
            assertFalse(actual.contains(" join_2"));
            assertEquals(3, DfStringUtil.splitList(actual, "select * from dual").size());
            assertTrue(actual.contains(" rownum <= 2"));
        } finally {
            ResourceContext.clearResourceContextOnThread();
        }
    }

    public void test_filterNextValSql_incrementOne() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        String sql = "select next value for SEQ_MEMBER";

        // ## Act ##
        String actual = handler.filterNextValSql(50, 1, sql);

        // ## Assert ##
        log(actual);
        assertTrue(actual.contains(" union all "));
        String[] split = actual.split(" union all ");
        assertEquals(50, split.length);
        assertTrue(actual.contains("order by 1"));
    }

    public void test_filterNextValSql_incrementOne_Oracle() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        String sql = "select SEQ_MEMBER.nextval from dual";
        ResourceContext context = new ResourceContext();
        context.setCurrentDBDef(DBDef.Oracle);
        ResourceContext.setResourceContextOnThread(context);

        try {
            // ## Act ##
            String actual = handler.filterNextValSql(54, 1, sql);

            // ## Assert ##
            log(actual);
            assertTrue(actual.contains("nextval"));
            assertTrue(actual.contains("select * from dual"));
            assertTrue(actual.contains(" union all"));
            assertTrue(actual.contains(" join_1"));
            assertTrue(actual.contains(" join_2"));
            assertFalse(actual.contains(" join_3"));
            assertTrue(actual.contains(" rownum <= 54"));
        } finally {
            ResourceContext.clearResourceContextOnThread();
        }
    }

    public void test_filterNextValSql_incrementOne_largeSize_Oracle() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        String sql = "select SEQ_MEMBER.nextval from dual";
        ResourceContext context = new ResourceContext();
        context.setCurrentDBDef(DBDef.Oracle);
        ResourceContext.setResourceContextOnThread(context);

        try {
            // ## Act ##
            String actual = handler.filterNextValSql(10000, 1, sql);

            // ## Assert ##
            log(actual);
            assertTrue(actual.contains("nextval"));
            assertTrue(actual.contains("select * from dual"));
            assertTrue(actual.contains(" union all"));
            assertTrue(actual.contains(" join_1"));
            assertTrue(actual.contains(" join_2"));
            assertTrue(actual.contains(" join_3"));
            assertTrue(actual.contains(" join_4"));
            assertFalse(actual.contains(" join_5"));
            assertEquals(41, DfStringUtil.splitList(actual, "select * from dual").size());
            assertTrue(actual.contains(" rownum <= 10000"));
        } finally {
            ResourceContext.clearResourceContextOnThread();
        }
    }

    public void test_filterNextValSql_incrementOne_largeSize_plus_Oracle() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        String sql = "select SEQ_MEMBER.nextval from dual";
        ResourceContext context = new ResourceContext();
        context.setCurrentDBDef(DBDef.Oracle);
        ResourceContext.setResourceContextOnThread(context);

        try {
            // ## Act ##
            String actual = handler.filterNextValSql(10001, 1, sql);

            // ## Assert ##
            log(actual);
            assertTrue(actual.contains("nextval"));
            assertTrue(actual.contains("select * from dual"));
            assertTrue(actual.contains(" union all"));
            assertTrue(actual.contains(" join_1"));
            assertTrue(actual.contains(" join_2"));
            assertTrue(actual.contains(" join_3"));
            assertTrue(actual.contains(" join_4"));
            assertTrue(actual.contains(" join_5"));
            assertFalse(actual.contains(" join_6"));
            assertEquals(43, DfStringUtil.splitList(actual, "select * from dual").size());
            assertTrue(actual.contains(" rownum <= 10001"));
        } finally {
            ResourceContext.clearResourceContextOnThread();
        }
    }

    public void test_filterNextValSql_cannotDivided() {
        // ## Arrange ##
        SequenceCacheHandler handler = new SequenceCacheHandler();
        String sql = "select next value for SEQ_MEMBER";

        // ## Act ##
        try {
            // ## Assert ##
            handler.filterNextValSql(50, 3, sql);
        } catch (SequenceCacheSizeNotDividedIncrementSizeException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    private static class MyDataSource implements DataSource {

        public Connection getConnection() throws SQLException {
            return null;
        }

        public Connection getConnection(String s, String s1) throws SQLException {
            return null;
        }

        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        public void setLogWriter(PrintWriter printwriter) throws SQLException {
        }

        public void setLoginTimeout(int i) throws SQLException {
        }
    }

    private <RESULT> List<RESULT> fireSameExecution(ExecutionCreator<RESULT> creator) {
        // ## Arrange ##
        ExecutorService service = Executors.newCachedThreadPool();
        int threadCount = 10;
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch goal = new CountDownLatch(threadCount);
        Execution<RESULT> execution = creator.create();
        List<Future<RESULT>> futureList = new ArrayList<Future<RESULT>>();
        for (int i = 0; i < threadCount; i++) {
            Future<RESULT> future = service.submit(createCallable(execution, ready, start, goal));
            futureList.add(future);
        }

        // ## Act ##
        // Start!
        start.countDown();
        try {
            // Wait until all threads are finished!
            goal.await();
        } catch (InterruptedException e) {
            String msg = "goal.await() was interrupted!";
            throw new IllegalStateException(msg, e);
        }
        log("All threads are finished!");

        // ## Assert ##
        List<RESULT> resultList = new ArrayList<RESULT>();
        for (Future<RESULT> future : futureList) {
            try {
                RESULT result = future.get();
                assertNotNull(result);
                resultList.add(result);
            } catch (InterruptedException e) {
                String msg = "future.get() was interrupted!";
                throw new IllegalStateException(msg, e);
            } catch (ExecutionException e) {
                String msg = "Failed to execute!";
                throw new IllegalStateException(msg, e.getCause());
            }
        }
        return resultList;
    }

    private static interface ExecutionCreator<RESULT> {
        Execution<RESULT> create();
    }

    private static interface Execution<RESULT> {
        RESULT execute();
    }

    private <RESULT> Callable<RESULT> createCallable(final Execution<RESULT> execution, final CountDownLatch ready,
            final CountDownLatch start, final CountDownLatch goal) {
        return new Callable<RESULT>() {
            public RESULT call() {
                try {
                    ready.countDown();
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        String msg = "start.await() was interrupted!";
                        throw new IllegalStateException(msg, e);
                    }
                    RESULT result = execution.execute();
                    return result;
                } finally {
                    goal.countDown();
                }
            }
        };
    }
}
