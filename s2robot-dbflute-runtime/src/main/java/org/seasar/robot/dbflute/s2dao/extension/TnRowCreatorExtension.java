/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.jdbc.ValueType;
import org.seasar.robot.dbflute.resource.InternalMapContext;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;
import org.seasar.robot.dbflute.s2dao.rowcreator.impl.TnRowCreatorImpl;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class TnRowCreatorExtension extends TnRowCreatorImpl {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance. */
    private static final org.apache.commons.logging.Log _log = org.apache.commons.logging.LogFactory
            .getLog(TnRowCreatorExtension.class);

    /** The key of DBMeta cache. */
    protected static final String DBMETA_CACHE_KEY = "df:DBMetaCache";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected DBMeta _dbmeta;
    protected boolean _beanAssignable;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    protected TnRowCreatorExtension() {
    }

    /**
     * @param beanClass The class of target bean to find DB-meta. (Nullable)
     * @return The instance of internal row creator. (NotNull)
     */
    public static TnRowCreatorExtension createRowCreator(Class<?> beanClass) {
        final TnRowCreatorExtension rowCreator = new TnRowCreatorExtension();
        if (beanClass != null) {
            final DBMeta dbmeta = findDBMetaByClass(beanClass);
            if (dbmeta != null) {
                rowCreator.setDBMeta(dbmeta);
                rowCreator.setBeanAssignable(isBeanAssignableFromEntity(beanClass, dbmeta.getEntityType()));
            }
        }
        return rowCreator;
    }

    protected static boolean isBeanAssignableFromEntity(Class<?> beanClass, Class<?> entityType) {
        return beanClass.isAssignableFrom(entityType);
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    /**
     * @param rs Result set. (NotNull)
     * @param propertyCache The map of property cache. Map{String(columnName), PropertyType} (NotNull)
     * @param beanClass Bean class. (NotNull)
     * @return Created row. (NotNull)
     * @throws SQLException
     */
    public Object createRow(ResultSet rs, Map<String, TnPropertyType> propertyCache, Class<?> beanClass)
            throws SQLException {
        if (propertyCache.isEmpty()) {
            String msg = "The propertyCache should not be empty: bean=" + beanClass.getName();
            throw new IllegalStateException(msg);
        }
        final Set<String> columnNameSet = propertyCache.keySet();
        String columnName = null;
        TnPropertyType pt = null;
        String propertyName = null;
        final Map<String, Integer> selectIndexMap = ResourceContext.getSelectIndexMap();
        final Object row;
        final DBMeta dbmeta;
        if (_dbmeta != null) {
            dbmeta = _dbmeta;
            if (_beanAssignable) {
                row = dbmeta.newEntity();
            } else {
                row = newBean(beanClass);
            }
        } else {
            row = newBean(beanClass);
            dbmeta = findDBMeta(row);
        }
        try {
            if (dbmeta != null) {
                for (final Iterator<String> ite = columnNameSet.iterator(); ite.hasNext();) {
                    columnName = ite.next();
                    pt = (TnPropertyType) propertyCache.get(columnName);
                    propertyName = pt.getPropertyName();
                    if (dbmeta.hasEntityPropertySetupper(propertyName)) {
                        final ValueType valueType = pt.getValueType();
                        final Object value = getValue(rs, columnName, valueType, selectIndexMap);
                        dbmeta.setupEntityProperty(propertyName, row, value);
                    } else {
                        registerValueByReflection(rs, row, pt, columnName, selectIndexMap);
                    }
                }
            } else {
                for (final Iterator<String> ite = columnNameSet.iterator(); ite.hasNext();) {
                    columnName = ite.next();
                    pt = (TnPropertyType) propertyCache.get(columnName);
                    propertyName = pt.getPropertyName();
                    registerValueByReflection(rs, row, pt, columnName, selectIndexMap);
                }
            }
            return row;
        } catch (ClassCastException e) {
            if (_log.isWarnEnabled()) {
                String msg = ClassCastException.class.getSimpleName() + " occurred while ResultSet Handling:";
                _log.warn(msg + " target=" + beanClass.getSimpleName() + "." + propertyName + " dbmeta");
            }
            throwNonsenseClassCastException(row, dbmeta, e);
            return null; // unreachable
        } catch (SQLException e) {
            if (_log.isWarnEnabled()) {
                String msg = SQLException.class.getSimpleName() + " occurred while ResultSet Handling:";
                _log.warn(msg + " target=" + beanClass.getSimpleName() + "." + propertyName);
            }
            throw e;
        }
    }

    protected void registerValueByReflection(ResultSet rs, Object row, TnPropertyType pt, String columnName,
            Map<String, Integer> selectIndexMap) throws SQLException {
        final ValueType valueType = pt.getValueType();
        final Object value = getValue(rs, columnName, valueType, selectIndexMap);
        final DfPropertyDesc pd = pt.getPropertyDesc();
        pd.setValue(row, value);
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

    protected void throwNonsenseClassCastException(Object entity, DBMeta dbmeta, ClassCastException e) {
        String msg = "Look! Read the message below." + getLineSeparator();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + getLineSeparator();
        msg = msg + "Nonsense ClassCastException occured!" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Advice]" + getLineSeparator();
        msg = msg + "This exception may be from ClassLoader Headache about HotDeploy." + getLineSeparator();
        msg = msg + "Please add the ignore-package setting to convention.dicon like as follows:" + getLineSeparator();
        msg = msg + "  For example:" + getLineSeparator();
        msg = msg + "    <initMethod name=”addIgnorePackageName”>" + getLineSeparator();
        msg = msg + "        <arg>”com.example.xxx.dbflute”</arg>" + getLineSeparator();
        msg = msg + "    </initMethod>" + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Exception Message]" + getLineSeparator() + e.getMessage() + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Target Entity]" + getLineSeparator() + entity + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Target Entity Class Loader]" + getLineSeparator() + entity.getClass().getClassLoader()
                + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Target DBMeta]" + getLineSeparator() + dbmeta + getLineSeparator();
        msg = msg + getLineSeparator();
        msg = msg + "[Target DBMeta Class Loader]" + getLineSeparator() + dbmeta.getClass().getClassLoader()
                + getLineSeparator();
        msg = msg + "* * * * * * * * * */";
        throw new NonsenseClassCastException(msg, e);
    }

    public static class NonsenseClassCastException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public NonsenseClassCastException(String msg, ClassCastException e) {
            super(msg, e);
        }
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    /**
     * @param row The instance of row. (NotNull)
     * @return The interface of DBMeta. (Nullable: If it's null, it means NotFound.)
     */
    public static DBMeta findDBMeta(Object row) {
        return DBMetaCacheHandler.findDBMeta(row);
    }

    /**
     * @param rowType The type of row. (NotNull)
     * @param tableName The name of table. (NotNull)
     * @return The interface of DBMeta. (Nullable: If it's null, it means NotFound.)
     */
    public static DBMeta findDBMeta(Class<?> rowType, String tableName) {
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
            DBMeta dbmeta = findCachedDBMeta(entity.getClass());
            if (dbmeta != null) {
                return dbmeta;
            }
            dbmeta = entity.getDBMeta();
            cacheDBMeta(entity, dbmeta);
            return dbmeta;
        }

        public static DBMeta findDBMeta(Class<?> rowType, String tableName) {
            DBMeta dbmeta = findCachedDBMeta(rowType);
            if (dbmeta != null) {
                return dbmeta;
            }
            // No check because the table name is not always for domain.
            dbmeta = ResourceContext.provideDBMeta(tableName);
            cacheDBMeta(rowType, dbmeta);
            return dbmeta;
        }

        protected static DBMeta findCachedDBMeta(Class<?> rowType) {
            Map<Class<?>, DBMeta> dbmetaCache = findDBMetaCache();
            if (dbmetaCache == null) {
                dbmetaCache = new HashMap<Class<?>, DBMeta>();
                InternalMapContext.setObject(DBMETA_CACHE_KEY, dbmetaCache);
            }
            return dbmetaCache.get(rowType);
        }

        protected static void cacheDBMeta(Entity entity, DBMeta dbmeta) {
            cacheDBMeta(entity.getClass(), dbmeta);
        }

        protected static void cacheDBMeta(Class<?> type, DBMeta dbmeta) {
            final Map<Class<?>, DBMeta> dbmetaCache = findDBMetaCache();
            dbmetaCache.put(type, dbmeta);
        }

        @SuppressWarnings("unchecked")
        protected static Map<Class<?>, DBMeta> findDBMetaCache() {
            return (Map<Class<?>, DBMeta>) InternalMapContext.getObject(DBMETA_CACHE_KEY);
        }
    }

    protected static DBMeta findDBMetaByClass(Class<?> beanClass) {
        final Object instance = newInstance(beanClass);
        if (!(instance instanceof Entity)) {
            return null;
        }
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

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String getLineSeparator() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setDBMeta(DBMeta dbmeta) {
        this._dbmeta = dbmeta;
    }
    
    public void setBeanAssignable(boolean beanAssignable) {
        this._beanAssignable = beanAssignable;
    }
}
