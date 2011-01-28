/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.robot.dbflute.s2dao.extension;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.exception.MappingClassCastException;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.resource.InternalMapContext;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyMapping;
import org.seasar.robot.dbflute.s2dao.rowcreator.impl.TnRowCreatorImpl;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 */
public class TnRowCreatorExtension extends TnRowCreatorImpl {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(TnRowCreatorExtension.class);

    /** The key of DBMeta cache. */
    protected static final String DBMETA_CACHE_KEY = "df:DBMetaCache";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DBMeta _fixedDBMeta;
    protected boolean _creatableByDBMeta;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected TnRowCreatorExtension() {
    }

    /**
     * @param beanClass The class of target bean to find DB-meta. (NullAllowed)
     * @return The instance of internal row creator. (NotNull)
     */
    public static TnRowCreatorExtension createRowCreator(Class<?> beanClass) {
        final TnRowCreatorExtension rowCreator = new TnRowCreatorExtension();
        if (beanClass != null) {
            final DBMeta dbmeta = findDBMetaByClass(beanClass);
            if (dbmeta != null) {
                rowCreator.setFixedDBMeta(dbmeta);
                rowCreator.setCreatableByDBMeta(isCreatableByDBMeta(beanClass, dbmeta.getEntityType()));
            }
        }
        return rowCreator;
    }

    protected static DBMeta findDBMetaByClass(Class<?> beanClass) {
        if (!Entity.class.isAssignableFrom(beanClass)) {
            return null;
        }
        // getting from entity because the bean may be customize entity.
        final Object instance = newInstance(beanClass); // only when initialization
        return ((Entity) instance).getDBMeta();
    }

    protected static Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    protected static boolean isCreatableByDBMeta(Class<?> beanClass, Class<?> entityType) {
        // Returns false when the bean is not related to the entity or is a sub class of the entity.
        return beanClass.isAssignableFrom(entityType);
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    /**
     * {@inheritDoc}
     */
    public Object createRow(ResultSet rs, Map<String, TnPropertyMapping> propertyCache, Class<?> beanClass)
            throws SQLException {
        if (propertyCache.isEmpty()) {
            String msg = "The propertyCache should not be empty: bean=" + beanClass.getName();
            throw new IllegalStateException(msg);
        }
        String columnName = null;
        TnPropertyMapping mapping = null;
        String propertyName = null;
        Object selectedValue = null;
        final Map<String, Integer> selectIndexMap = ResourceContext.getSelectIndexMap();
        final Object row;
        final DBMeta dbmeta;
        if (_fixedDBMeta != null) {
            if (_creatableByDBMeta) {
                row = _fixedDBMeta.newEntity();
            } else {
                row = newBean(beanClass);
            }
            dbmeta = _fixedDBMeta;
        } else {
            row = newBean(beanClass);
            dbmeta = findCachedDBMeta(row);
        }
        try {
            if (dbmeta != null) {
                final Set<Entry<String, TnPropertyMapping>> entrySet = propertyCache.entrySet();
                for (Entry<String, TnPropertyMapping> entry : entrySet) {
                    columnName = entry.getKey();
                    mapping = entry.getValue();
                    propertyName = mapping.getPropertyName();
                    selectedValue = getValue(rs, columnName, mapping.getValueType(), selectIndexMap);
                    if (dbmeta.hasEntityPropertySetupper(propertyName)) {
                        dbmeta.setupEntityProperty(propertyName, row, selectedValue);
                    } else {
                        mapping.getPropertyAccessor().setValue(row, selectedValue);
                    }
                }
            } else {
                final Set<Entry<String, TnPropertyMapping>> entrySet = propertyCache.entrySet();
                for (Entry<String, TnPropertyMapping> entry : entrySet) {
                    columnName = entry.getKey();
                    mapping = entry.getValue();
                    propertyName = mapping.getPropertyName();
                    selectedValue = getValue(rs, columnName, mapping.getValueType(), selectIndexMap);
                    mapping.getPropertyAccessor().setValue(row, selectedValue);
                }
            }
            return row;
        } catch (ClassCastException e) {
            throwMappingClassCastException(row, dbmeta, mapping, selectedValue, e);
            return null; // unreachable
        } catch (SQLException e) {
            if (_log.isDebugEnabled()) {
                String msg = "Failed to get selected values while resultSet handling:";
                msg = msg + " target=" + DfTypeUtil.toClassTitle(beanClass) + "." + propertyName;
                _log.debug(msg);
            }
            throw e;
        }
    }

    protected Object getValue(ResultSet rs, String columnName, ValueType valueType, Map<String, Integer> selectIndexMap)
            throws SQLException {
        final Object value;
        if (selectIndexMap != null) {
            value = ResourceContext.getValue(rs, columnName, valueType, selectIndexMap);
        } else {
            value = valueType.getValue(rs, columnName);
        }
        return value;
    }

    protected void throwMappingClassCastException(Object entity, DBMeta dbmeta, TnPropertyMapping mapping,
            Object selectedValue, ClassCastException e) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "Failed to cast a class while data mapping!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "If you use Seasar(S2Container), this exception may be" + ln();
        msg = msg + "from ClassLoader Headache about HotDeploy." + ln();
        msg = msg + "Add the ignore-package setting to convention.dicon like this:" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    <initMethod name=”addIgnorePackageName”>" + ln();
        msg = msg + "        <arg>”com.example.xxx.dbflute”</arg>" + ln();
        msg = msg + "    </initMethod>" + ln();
        msg = msg + "If you use an other DI container, this exception may be" + ln();
        msg = msg + "from illegal state about your settings of DBFlute." + ln();
        msg = msg + "Confirm your settings: for example, typeMappingMap.dfprop." + ln();
        msg = msg + ln();
        msg = msg + "[Exception Message]" + ln() + e.getMessage() + ln();
        msg = msg + ln();
        msg = msg + "[Target Entity]" + ln() + entity + ln();
        msg = msg + "classLoader: " + entity.getClass().getClassLoader() + ln();
        msg = msg + ln();
        msg = msg + "[Target DBMeta]" + ln() + dbmeta + ln();
        msg = msg + "classLoader: " + dbmeta.getClass().getClassLoader() + ln();
        msg = msg + ln();
        msg = msg + "[Property Mapping]" + ln() + mapping + ln();
        msg = msg + "type: " + (mapping != null ? mapping.getClass() : null) + ln();
        msg = msg + ln();
        msg = msg + "[Selected Value]" + ln() + selectedValue + ln();
        msg = msg + "type: " + (selectedValue != null ? selectedValue.getClass() : null) + ln();
        msg = msg + "* * * * * * * * * */";
        throw new MappingClassCastException(msg, e);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    /**
     * @param row The instance of row. (NotNull)
     * @return The interface of DBMeta. (NullAllowed: If it's null, it means NotFound.)
     */
    public static DBMeta findCachedDBMeta(Object row) {
        return DBMetaCacheHandler.findDBMeta(row);
    }

    /**
     * @param rowType The type of row. (NotNull)
     * @param tableName The name of table. (NotNull)
     * @return The interface of DBMeta. (NullAllowed: If it's null, it means NotFound.)
     */
    public static DBMeta findCachedDBMeta(Class<?> rowType, String tableName) {
        return DBMetaCacheHandler.findDBMeta(rowType, tableName);
    }

    protected static class DBMetaCacheHandler {

        /** The key of DBMeta cache. */
        protected static final String DBMETA_CACHE_KEY = "df:DBMetaCache";

        public static DBMeta findDBMeta(Object row) {
            if (!(row instanceof Entity)) {
                return null;
            }
            final Entity entity = (Entity) row;
            DBMeta dbmeta = getCachedDBMeta(entity.getClass());
            if (dbmeta != null) {
                return dbmeta;
            }
            dbmeta = entity.getDBMeta();
            cacheDBMeta(entity, dbmeta);
            return dbmeta;
        }

        public static DBMeta findDBMeta(Class<?> rowType, String tableName) {
            DBMeta dbmeta = getCachedDBMeta(rowType);
            if (dbmeta != null) {
                return dbmeta;
            }
            // No check because the table name is not always for domain.
            dbmeta = ResourceContext.provideDBMeta(tableName);
            cacheDBMeta(rowType, dbmeta);
            return dbmeta;
        }

        protected static DBMeta getCachedDBMeta(Class<?> rowType) {
            Map<Class<?>, DBMeta> contextCacheMap = getDBMetaContextCacheMap();
            if (contextCacheMap == null) {
                contextCacheMap = new HashMap<Class<?>, DBMeta>();
                InternalMapContext.setObject(DBMETA_CACHE_KEY, contextCacheMap);
            }
            return contextCacheMap.get(rowType);
        }

        protected static void cacheDBMeta(Entity entity, DBMeta dbmeta) {
            cacheDBMeta(entity.getClass(), dbmeta);
        }

        protected static void cacheDBMeta(Class<?> type, DBMeta dbmeta) {
            final Map<Class<?>, DBMeta> dbmetaCache = getDBMetaContextCacheMap();
            dbmetaCache.put(type, dbmeta);
        }

        @SuppressWarnings("unchecked")
        protected static Map<Class<?>, DBMeta> getDBMetaContextCacheMap() {
            return (Map<Class<?>, DBMeta>) InternalMapContext.getObject(DBMETA_CACHE_KEY);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setFixedDBMeta(DBMeta fixedDBMeta) {
        this._fixedDBMeta = fixedDBMeta;
    }

    public void setCreatableByDBMeta(boolean creatableByDBMeta) {
        this._creatableByDBMeta = creatableByDBMeta;
    }
}
