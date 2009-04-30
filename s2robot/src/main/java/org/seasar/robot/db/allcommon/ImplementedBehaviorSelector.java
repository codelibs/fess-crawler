package org.seasar.robot.db.allcommon;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.BehaviorSelector;
import org.seasar.dbflute.bhv.BehaviorReadable;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.util.TraceViewUtil;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.ComponentNotFoundRuntimeException;

/**
 * The implementation of behavior selector.
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedBehaviorSelector implements BehaviorSelector {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(ImplementedBehaviorSelector.class);

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The cache of behavior. (It's the generic hell!) */
    protected Map<Class<? extends BehaviorReadable>, BehaviorReadable> _behaviorCache = new ConcurrentHashMap<Class<? extends BehaviorReadable>, BehaviorReadable>();

    /** The container of Seasar. */
    protected S2Container _container;

    // ===================================================================================
    //                                                                           Component
    //                                                                           =========
    @SuppressWarnings("unchecked")
    public <COMPONENT> COMPONENT getComponent(Class<COMPONENT> componentType) {
        assertObjectNotNull("componentType", componentType);
        assertObjectNotNull("_container", _container);
        try {
		    return (COMPONENT)_container.getComponent(componentType);
		} catch (ComponentNotFoundRuntimeException e) { // Normally it doesn't come.
		    final COMPONENT component;
		    try {
		        // for HotDeploy Mode
		        component = (COMPONENT)_container.getRoot().getComponent(componentType);
		    } catch (ComponentNotFoundRuntimeException ignored) {
		        throw e;
		    }
		    _container = _container.getRoot(); // Change container.
		    return component;
		}
    }

    // ===================================================================================
    //                                                                          Initialize
    //                                                                          ==========
    /**
     * Initialize condition-bean meta data. <br />
     */
    public void initializeConditionBeanMetaData() {
        final Map<String, DBMeta> dbmetaMap = DBMetaInstanceHandler.getDBMetaMap();
        final Collection<DBMeta> dbmetas = dbmetaMap.values();
        long before = 0;
        if (_log.isInfoEnabled()) {
            before = System.currentTimeMillis();
            _log.info("/= = = = = = = = = = = = = = = = = initializeConditionBeanMetaData()");
        }
        for (DBMeta dbmeta : dbmetas) {
            final BehaviorReadable bhv = byName(dbmeta.getTableDbName());
            bhv.warmUpCommand();
        }
        if (_log.isInfoEnabled()) {
            long after = System.currentTimeMillis();
            _log.info("Initialized Count: " + dbmetas.size());
            _log.info("= = = = = = = = = =/ [" + TraceViewUtil.convertToPerformanceView(after - before) + "]");
        }
    }

    // ===================================================================================
    //                                                                            Selector
    //                                                                            ========
    /**
     * Select behavior.
     * @param <BEHAVIOR> The type of behavior.
     * @param behaviorType Behavior type. (NotNull)
     * @return Behavior. (NotNull)
     */
    @SuppressWarnings("unchecked")
    public <BEHAVIOR extends BehaviorReadable> BEHAVIOR select(Class<BEHAVIOR> behaviorType) {
        if (_behaviorCache.containsKey(behaviorType)) {
            return (BEHAVIOR) _behaviorCache.get(behaviorType);
        }
        synchronized (_behaviorCache) {
            if (_behaviorCache.containsKey(behaviorType)) {
                return (BEHAVIOR) _behaviorCache.get(behaviorType);
            }
            final BEHAVIOR bhv = (BEHAVIOR) getComponent(behaviorType);
            _behaviorCache.put(behaviorType, bhv);
            return bhv;
        }
    }

    /**
     * Select behavior-readable by name.
     * @param tableFlexibleName Table flexible-name. (NotNull)
     * @return Behavior-readable. (NotNull)
     */
    public BehaviorReadable byName(String tableFlexibleName) {
        assertStringNotNullAndNotTrimmedEmpty("tableFlexibleName", tableFlexibleName);
        final DBMeta dbmeta = DBMetaInstanceHandler.findDBMeta(tableFlexibleName);
        return select(getBehaviorType(dbmeta));
    }

    /**
     * Get behavior-type by DB meta.
     * @param dbmeta DB meta. (NotNull)
     * @return Behavior-type. (NotNull)
     */
    @SuppressWarnings("unchecked")
    protected Class<BehaviorReadable> getBehaviorType(DBMeta dbmeta) {
        final String behaviorTypeName = dbmeta.getBehaviorTypeName();
        if (behaviorTypeName == null) {
            String msg = "The dbmeta.getBehaviorTypeName() should not return null: dbmeta=" + dbmeta;
            throw new IllegalStateException(msg);
        }
        final Class<BehaviorReadable> behaviorType;
        try {
            behaviorType = (Class<BehaviorReadable>) Class.forName(behaviorTypeName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("The class does not exist: " + behaviorTypeName, e);
        }
        return behaviorType;
    }

    // ===================================================================================
    //                                                                              Helper
    //                                                                              ======
    protected String initUncap(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    // ===================================================================================
    //                                                                              Assert
    //                                                                              ======
    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    /**
     * Assert that the entity is not null and not trimmed empty.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     */
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        assertObjectNotNull("variableName", variableName);
        assertObjectNotNull("value", value);
        if (value.trim().length() == 0) {
            String msg = "The value should not be empty: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setContainer(S2Container container) {
        this._container = container;
    }
}
