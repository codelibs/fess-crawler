package org.seasar.robot.dbflute.s2dao.valuetype;

import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.findByTypeOrValue;
import static org.seasar.robot.dbflute.s2dao.valuetype.TnValueTypes.findByValueOrJdbcDefType;

import java.io.FileFilter;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.jdbc.Classification;
import org.seasar.robot.dbflute.jdbc.ClassificationMeta;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.mock.MockValueType;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.valuetype.basic.BigDecimalType;
import org.seasar.robot.dbflute.s2dao.valuetype.basic.BinaryType;
import org.seasar.robot.dbflute.s2dao.valuetype.basic.IntegerType;
import org.seasar.robot.dbflute.s2dao.valuetype.basic.LongType;
import org.seasar.robot.dbflute.s2dao.valuetype.basic.ObjectType;
import org.seasar.robot.dbflute.s2dao.valuetype.basic.SqlDateType;
import org.seasar.robot.dbflute.s2dao.valuetype.basic.StringType;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 * @since 0.9.6.4 (2010/01/22 Friday)
 */
public class TnValueTypesTest extends PlainTestCase {

    private final DBDef _currentDBDef = DBDef.MySQL;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ResourceContext context = new ResourceContext();
        context.setCurrentDBDef(_currentDBDef);
        ResourceContext.setResourceContextOnThread(context);
    }

    @Override
    protected void tearDown() throws Exception {
        TnValueTypes.restoreDefault(_currentDBDef);
        super.tearDown();
    }

    // ===================================================================================
    //                                                                         DBMS Switch
    //                                                                         ===========
    public void test_DBMS_switch_basic() throws Exception {
        assertEquals(TnValueTypes.UTILDATE_AS_SQLDATE, TnValueTypes.getValueType(java.util.Date.class));
        TnValueTypes.registerBasicValueType(DBDef.Derby, java.util.Date.class, TnValueTypes.BYTE);
        assertEquals(TnValueTypes.UTILDATE_AS_SQLDATE, TnValueTypes.getValueType(java.util.Date.class));
        assertEquals(TnValueTypes.BYTE, TnValueTypes.findValueTypes(DBDef.Derby).getValueType(java.util.Date.class));
    }

    public void test_DBMS_switch_Oracle_date() throws Exception {
        assertEquals(TnValueTypes.UTILDATE_AS_SQLDATE, TnValueTypes.getValueType(java.util.Date.class));
        assertEquals(TnValueTypes.UTILDATE_AS_TIMESTAMP, TnValueTypes.findValueTypes(DBDef.Oracle).getValueType(
                java.util.Date.class));
    }

    public void test_DBMS_switch_SQLServer_uuid() throws Exception {
        assertEquals(TnValueTypes.UUID_AS_DIRECT, TnValueTypes.getValueType(java.util.UUID.class));
        assertEquals(TnValueTypes.UUID_AS_STRING, TnValueTypes.findValueTypes(DBDef.SQLServer).getValueType(
                java.util.UUID.class));
    }

    // ===================================================================================
    //                                                                                Find
    //                                                                                ====
    public void test_findByTypeOrValue_basic() throws Exception {
        assertEquals(StringType.class, findByTypeOrValue(String.class, "foo").getClass());
        assertEquals(StringType.class, findByTypeOrValue(Object.class, "foo").getClass());
        assertEquals(StringType.class, findByTypeOrValue(null, "foo").getClass());
        assertEquals(StringType.class, findByTypeOrValue(String.class, 123).getClass());
        assertEquals(StringType.class, findByTypeOrValue(String.class, null).getClass());
        assertEquals(LongType.class, findByTypeOrValue(Long.class, 123).getClass());

        // binary
        assertEquals(BinaryType.class, findByTypeOrValue(Object.class, "foo".getBytes("UTF-8")).getClass());
        assertEquals(BinaryType.class, findByTypeOrValue(byte[].class, "foo").getClass());
        assertEquals(BinaryType.class, findByTypeOrValue(new byte[0].getClass(), "foo").getClass());

        // object
        assertEquals(TnValueTypes.DEFAULT_OBJECT, findByTypeOrValue(null, null));
        assertEquals(TnValueTypes.DEFAULT_OBJECT, findByTypeOrValue(Object.class, Object.class));
        assertEquals(TnValueTypes.DEFAULT_OBJECT, findByTypeOrValue(FileFilter.class, FileFilter.class));
        assertTrue(((ObjectType) findByTypeOrValue(null, null)).isDefaultObject());
        assertTrue(TnValueTypes.isDefaultObject(findByTypeOrValue(null, null)));
        assertFalse(TnValueTypes.isDynamicObject(findByTypeOrValue(null, null)));
    }

    public void test_findByValueOrJdbcDefType_basic() throws Exception {
        assertEquals(StringType.class, findByValueOrJdbcDefType("foo", Types.OTHER).getClass());
        assertEquals(StringType.class, findByValueOrJdbcDefType("foo", Types.DATE).getClass());
        assertEquals(IntegerType.class, findByValueOrJdbcDefType(123, Types.DATE).getClass());
        assertEquals(SqlDateType.class, findByValueOrJdbcDefType(new Object(), Types.DATE).getClass());
        assertEquals(SqlDateType.class, findByValueOrJdbcDefType(null, Types.DATE).getClass());

        // binary
        assertEquals(BinaryType.class, findByValueOrJdbcDefType("foo".getBytes("UTF-8"), 12345678).getClass());
        assertEquals(BinaryType.class, findByValueOrJdbcDefType(new byte[0], 12345678).getClass());

        // object
        assertEquals(ObjectType.class, findByValueOrJdbcDefType(null, 12345678).getClass());
        assertFalse(((ObjectType) findByValueOrJdbcDefType(null, 12345678)).isDefaultObject());
        assertFalse(TnValueTypes.isDefaultObject(findByValueOrJdbcDefType(null, 12345678)));
        assertTrue(TnValueTypes.isDynamicObject(findByValueOrJdbcDefType(null, 12345678)));
    }

    // ===================================================================================
    //                                                                         byClassType
    //                                                                         ===========
    public void test_getValueType_byClassType_string_basic() throws Exception {
        assertEquals(StringType.class, TnValueTypes.getValueType(String.class).getClass());
    }

    public void test_getValueType_byClassType_number_basic() throws Exception {
        assertEquals(IntegerType.class, TnValueTypes.getValueType(Integer.class).getClass());
        assertEquals(BigDecimalType.class, TnValueTypes.getValueType(BigDecimal.class).getClass());
    }

    public void test_getValueType_byClassType_enum_priority_classification() throws Exception {
        // ## Arrange ##
        Class<?> keyType = TestClassificationStatus.class; // embedded
        MockValueType mockValueType = new MockValueType();

        // ## Act ##
        TnValueTypes.registerBasicValueType(_currentDBDef, TestPlainStatus.class, mockValueType);
        TnValueTypes.registerBasicValueType(_currentDBDef, Enum.class, mockValueType);
        ValueType valueType = TnValueTypes.getValueType(keyType);

        // ## Assert ##
        assertNotSame(mockValueType, valueType);
        assertEquals(TnValueTypes.CLASSIFICATION, valueType);
    }

    public void test_getValueType_byClassType_enum_priority_plain() throws Exception {
        // ## Arrange ##
        Class<?> keyType = TestPlainStatus.class;
        MockValueType mockValueType = new MockValueType();

        // ## Act ##
        TnValueTypes.registerBasicValueType(_currentDBDef, keyType, mockValueType);
        ValueType valueType = TnValueTypes.getValueType(keyType);

        // ## Assert ##
        assertNotSame(TnValueTypes.CLASSIFICATION, valueType);
        assertEquals(mockValueType, valueType);
    }

    public void test_getValueType_byClassType_enum_priority_enumKey() throws Exception {
        // ## Arrange ##
        Class<?> keyType = Enum.class;
        MockValueType mockValueType = new MockValueType();

        // ## Act ##
        TnValueTypes.registerBasicValueType(_currentDBDef, keyType, mockValueType);
        ValueType valueType = TnValueTypes.getValueType(keyType);

        // ## Assert ##
        assertNotSame(TnValueTypes.CLASSIFICATION, valueType);
        assertEquals(mockValueType, valueType);
    }

    public void test_getValueType_byInstance_enum_priority_classification() throws Exception {
        // ## Arrange ##
        MockValueType mockValueType = new MockValueType();

        // ## Act ##
        TnValueTypes.registerBasicValueType(_currentDBDef, TestPlainStatus.class, mockValueType);
        TnValueTypes.registerBasicValueType(_currentDBDef, Enum.class, mockValueType);
        ValueType valueType = TnValueTypes.getValueType(TestClassificationStatus.FML);

        // ## Assert ##
        assertNotSame(mockValueType, valueType);
        assertEquals(TnValueTypes.CLASSIFICATION, valueType);
    }

    public void test_getValueType_byInstance_enum_priority_plain() throws Exception {
        // ## Arrange ##
        Class<?> keyType = TestPlainStatus.class;
        MockValueType mockValueType = new MockValueType();

        // ## Act ##
        TnValueTypes.registerBasicValueType(_currentDBDef, keyType, mockValueType);
        ValueType valueType = TnValueTypes.getValueType(TestPlainStatus.FML);

        // ## Assert ##
        assertNotSame(TnValueTypes.CLASSIFICATION, valueType);
        assertEquals(mockValueType, valueType);
    }

    private static enum TestPlainStatus {
        FML, PRV, WDL
    }

    private static enum TestClassificationStatus implements Classification {
        FML, PRV, WDL;

        public String code() {
            return null;
        }

        public String alias() {
            return null;
        }

        public ClassificationMeta meta() {
            return null;
        }
    }

    public void test_registerBasicValueType_interface_basic() throws Exception {
        // ## Arrange ##
        MockValueType mockValueType = new MockValueType();

        // ## Act ##
        TnValueTypes.registerBasicValueType(_currentDBDef, FilenameFilter.class, mockValueType);
        ValueType interfaceValueType = TnValueTypes.findValueTypes(_currentDBDef).getBasicInterfaceValueType(
                FilenameFilter.class);

        // ## Assert ##
        assertEquals(mockValueType, interfaceValueType);
        assertNull(TnValueTypes.findValueTypes(_currentDBDef).getBasicInterfaceValueType(FileFilter.class));
    }

    public void test_registerBasicValueType_interface_threadSafe() throws Exception {
        // ## Arrange ##
        ExecutionCreator<ValueType> creator = new ExecutionCreator<ValueType>() {
            public Execution<ValueType> create() {
                return new Execution<ValueType>() {
                    public ValueType execute() {
                        MockValueType mockValueType = new MockValueType();
                        if (Thread.currentThread().getId() % 2 == 0) {
                            TnValueTypes.registerBasicValueType(_currentDBDef, FilenameFilter.class, mockValueType);
                            ValueType interfaceValueType = TnValueTypes.findValueTypes(_currentDBDef)
                                    .getBasicInterfaceValueType(FilenameFilter.class);
                            return interfaceValueType;
                        } else {
                            TnValueTypes.registerBasicValueType(_currentDBDef, FileFilter.class, mockValueType);
                            ValueType interfaceValueType = TnValueTypes.findValueTypes(_currentDBDef)
                                    .getBasicInterfaceValueType(FileFilter.class);
                            return interfaceValueType;
                        }
                    }
                };
            }
        };

        // ## Act & Assert ##
        fireSameExecution(creator);
    }

    public void test_getValueType_byClassType_object_basic() throws Exception {
        assertEquals(ObjectType.class, TnValueTypes.getValueType(Object.class).getClass());
        assertEquals(ObjectType.class, TnValueTypes.getValueType(FileFilter.class).getClass());
    }

    // ===================================================================================
    //                                                                          byJdbcType
    //                                                                          ==========
    public void test_getValueType_byJdbcType_dynamicObject_basic() throws Exception {
        assertTrue(TnValueTypes.findValueTypes(_currentDBDef)._dynamicObjectValueTypeMap.isEmpty());
        {
            // ## Arrange & Act ##
            ValueType valueType = TnValueTypes.getValueType(Types.OTHER);

            // ## Assert ##
            assertEquals(ObjectType.class, valueType.getClass());
            assertEquals(Types.OTHER, valueType.getSqlType());
            assertTrue(TnValueTypes.isDynamicObject(valueType));
        }
        assertTrue(TnValueTypes.findValueTypes(_currentDBDef)._dynamicObjectValueTypeMap.containsKey(Types.OTHER));
        {
            // ## Arrange & Act ##
            ValueType valueType = TnValueTypes.getValueType(12345678);

            // ## Assert ##
            assertEquals(ObjectType.class, valueType.getClass());
            assertEquals(12345678, valueType.getSqlType());
            assertTrue(TnValueTypes.isDynamicObject(valueType));
        }
        assertTrue(TnValueTypes.findValueTypes(_currentDBDef)._dynamicObjectValueTypeMap.containsKey(12345678));
        {
            // ## Arrange & Act ##
            ValueType valueType = TnValueTypes.getValueType(12345678);

            // ## Assert ##
            assertEquals(ObjectType.class, valueType.getClass());
            assertEquals(12345678, valueType.getSqlType());
            assertTrue(TnValueTypes.isDynamicObject(valueType));
        }
        assertTrue(TnValueTypes.findValueTypes(_currentDBDef)._dynamicObjectValueTypeMap.containsKey(12345678));
    }

    public void test_getValueType_byJdbcType_dynamicObject_threadSafe_basic() throws Exception {
        // ## Arrange ##
        ExecutionCreator<ValueType> creator = new ExecutionCreator<ValueType>() {
            public Execution<ValueType> create() {
                return new Execution<ValueType>() {
                    int _index = 12345678;

                    public ValueType execute() {
                        if (Thread.currentThread().getId() % 2 == 0) {
                            ++_index;
                        }
                        final int sqlType = _index;
                        log(sqlType);
                        final ValueType valueType = TnValueTypes.getValueType(sqlType);
                        assertEquals(sqlType, valueType.getSqlType());
                        assertTrue(TnValueTypes.isDynamicObject(valueType));
                        return valueType;
                    }
                };
            }
        };

        // ## Act & Assert ##
        fireSameExecution(creator);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
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
        // start!
        start.countDown();
        try {
            // wait until all threads are finished!
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
