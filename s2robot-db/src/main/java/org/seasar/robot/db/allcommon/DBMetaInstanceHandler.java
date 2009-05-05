package org.seasar.robot.db.allcommon;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.dbmeta.DBMetaProvider;
import org.seasar.dbflute.exception.DBMetaNotFoundException;
import org.seasar.dbflute.helper.StringKeyMap;
import org.seasar.dbflute.util.DfAssertUtil;

/**
 * The handler of the instance of DB meta.
 * @author DBFlute(AutoGenerator)
 */
public class DBMetaInstanceHandler implements DBMetaProvider {

    // ===================================================================================
    //                                                                        Resource Map
    //                                                                        ============
    /** Table DB-name instance map. */
    protected static final Map<String, DBMeta> _tableDbNameInstanceMap = newConcurrentHashMap();

    /** The map of table DB name and class name. This is for initialization. */
    protected static final Map<String, String> _tableDbNameClassNameMap;
    static {
        final Map<String, String> tmpMap = newConcurrentHashMap();
        tmpMap.put("ACCESS_RESULT",
                "org.seasar.robot.db.bsentity.dbmeta.AccessResultDbm");
        tmpMap.put("ACCESS_RESULT_DATA",
                "org.seasar.robot.db.bsentity.dbmeta.AccessResultDataDbm");
        tmpMap.put("URL_QUEUE",
                "org.seasar.robot.db.bsentity.dbmeta.UrlQueueDbm");
        _tableDbNameClassNameMap = Collections.unmodifiableMap(tmpMap);
    }

    /** The flexible map of table DB name. This is for conversion at finding. */
    protected static final Map<String, String> _tableDbNameFlexibleMap = StringKeyMap
            .createAsFlexibleConcurrent();
    static {
        final Set<String> tableDbNameSet = _tableDbNameClassNameMap.keySet();
        for (String tableDbName : tableDbNameSet) {
            _tableDbNameFlexibleMap.put(tableDbName, tableDbName);
        }
    }

    /**
     * @return The initialized map that contains all instances of DB meta. (NotNull & NotEmpty)
     */
    public static Map<String, DBMeta> getDBMetaMap() {
        initializeDBMetaMap();
        return _tableDbNameInstanceMap;
    }

    /**
     * Initialize the map of DB meta.
     */
    protected static void initializeDBMetaMap() {
        if (isInitialized()) {
            return;
        }
        final Set<String> tableDbNameSet = _tableDbNameClassNameMap.keySet();
        for (String tableDbName : tableDbNameSet) {
            findDBMeta(tableDbName); // Initialize!
        }
        if (!isInitialized()) {
            String msg = "Failed to initialize tableDbNameInstanceMap:";
            msg = msg + " tableDbNameInstanceMap=" + _tableDbNameInstanceMap;
            throw new IllegalStateException(msg);
        }
    }

    protected static boolean isInitialized() {
        return _tableDbNameInstanceMap.size() == _tableDbNameClassNameMap
                .size();
    }

    // ===================================================================================
    //                                                                         Main Method
    //                                                                         ===========
    /**
     * Find DB meta by table flexible name.
     * <pre>
     * If the table name is 'ORDER_DETAIL', you can find the DB meta by ...(as follows)
     *     'ORDER_DETAIL', 'ORDer_DeTAiL', 'order_detail'
     *     , 'OrderDetail', 'orderdetail', 'oRderDetaIl'
     * </pre>
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (NotNull)
     * @exception ${glPackageBaseCommonException}.${glDBMetaNotFoundException} When the DB meta is not found.
     */
    public static DBMeta findDBMeta(String tableFlexibleName) {
        DBMeta dbmeta = byTableFlexibleName(tableFlexibleName);
        if (dbmeta == null) {
            String msg = "The DB meta was not found by the table flexible name: "
                    + tableFlexibleName;
            msg = msg + " key=" + tableFlexibleName + " instanceMap="
                    + _tableDbNameInstanceMap;
            throw new DBMetaNotFoundException(msg);
        }
        return dbmeta;
    }

    /**
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (Nullable: If the DB meta is not found, it returns null)
     */
    protected static DBMeta byTableFlexibleName(String tableFlexibleName) {
        assertStringNotNullAndNotTrimmedEmpty("tableFlexibleName",
                tableFlexibleName);
        final int dotLastIndex = tableFlexibleName.lastIndexOf(".");
        if (dotLastIndex >= 0) {
            tableFlexibleName = tableFlexibleName.substring(dotLastIndex
                    + ".".length());
        }
        final String tableDbName = _tableDbNameFlexibleMap
                .get(tableFlexibleName);
        if (tableDbName != null) {
            return byTableDbName(tableDbName);
        }
        return null;
    }

    /**
     * @param tableDbName The DB name of table. (NotNull)
     * @return The instance of DB meta. (Nullable: If the DB meta is not found, it returns null)
     */
    protected static DBMeta byTableDbName(String tableDbName) {
        assertStringNotNullAndNotTrimmedEmpty("tableDbName", tableDbName);
        return getCachedDBMeta(tableDbName);
    }

    protected static DBMeta getCachedDBMeta(String tableName) {// For lazy-load! Thank you koyak!
        if (_tableDbNameInstanceMap.containsKey(tableName)) {
            return _tableDbNameInstanceMap.get(tableName);
        }
        synchronized (_tableDbNameInstanceMap) {
            if (_tableDbNameInstanceMap.containsKey(tableName)) {
                return _tableDbNameInstanceMap.get(tableName);
            }
            String entityName = _tableDbNameClassNameMap.get(tableName);
            _tableDbNameInstanceMap.put(tableName, getDBMeta(entityName));
        }
        return _tableDbNameInstanceMap.get(tableName);
    }

    protected static DBMeta getDBMeta(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Method methoz = clazz.getMethod("getInstance", (Class[]) null);
            Object result = methoz.invoke(null, (Object[]) null);
            return (DBMeta) result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ===================================================================================
    //                                                             Provider Implementation
    //                                                             =======================
    /**
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (Nullable: If the DB meta is not found, it returns null)
     */
    public DBMeta provideDBMeta(String tableFlexibleName) {
        return byTableFlexibleName(tableFlexibleName);
    }

    /**
     * @param tableFlexibleName The flexible name of table. (NotNull)
     * @return The instance of DB meta. (NotNull)
     * @exception ${glPackageBaseCommonException}.${glDBMetaNotFoundException} When the DB meta is not found.
     */
    public DBMeta provideDBMetaChecked(String tableFlexibleName) {
        return findDBMeta(tableFlexibleName);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected static <KEY, VALUE> ConcurrentHashMap<KEY, VALUE> newConcurrentHashMap() {
        return new ConcurrentHashMap<KEY, VALUE>();
    }

    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    protected static void assertObjectNotNull(String variableName, Object value) {
        DfAssertUtil.assertObjectNotNull(variableName, value);
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    protected static void assertStringNotNullAndNotTrimmedEmpty(
            String variableName, String value) {
        DfAssertUtil.assertStringNotNullAndNotTrimmedEmpty(variableName, value);
    }
}
