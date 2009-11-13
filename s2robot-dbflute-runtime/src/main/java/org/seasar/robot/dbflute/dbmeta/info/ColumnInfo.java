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
package org.seasar.robot.dbflute.dbmeta.info;

import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMeta.OptimisticLockType;

/**
 * The information of column.
 * @author jflute
 */
public class ColumnInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final DBMeta dbmeta;
    protected final String columnDbName;
    protected final String columnAlias;
    protected final String propertyName;
    protected final Class<?> propertyType;
    protected final boolean primary;
    protected final boolean autoIncrement;
    protected final Integer columnSize;
    protected final Integer columnDecimalDigits;
    protected final boolean commonColumn;
    protected final OptimisticLockType optimisticLockType;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ColumnInfo(DBMeta dbmeta, String columnDbName, String columnAlias, String propertyName,
            Class<?> propertyType, boolean primary, boolean autoIncrement, Integer columnSize,
            Integer columnDecimalDigits, boolean commonColumn, OptimisticLockType optimisticLockType) {
        assertObjectNotNull("dbmeta", dbmeta);
        assertObjectNotNull("columnDbName", columnDbName);
        assertObjectNotNull("propertyName", propertyName);
        assertObjectNotNull("propertyType", propertyType);
        this.dbmeta = dbmeta;
        this.columnDbName = columnDbName;
        this.columnAlias = columnAlias;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.primary = primary;
        this.autoIncrement = autoIncrement;
        this.columnSize = columnSize;
        this.columnDecimalDigits = columnDecimalDigits;
        this.commonColumn = commonColumn;
        this.optimisticLockType = optimisticLockType != null ? optimisticLockType : OptimisticLockType.NONE;
    }

    // ===================================================================================
    //                                                                              Finder
    //                                                                              ======
    public java.lang.reflect.Method findSetter() {
        return findMethod(dbmeta.getEntityType(), "set" + buildInitCapPropertyName(),
                new Class<?>[] { this.propertyType });
    }

    public java.lang.reflect.Method findGetter() {
        return findMethod(dbmeta.getEntityType(), "get" + buildInitCapPropertyName(), new Class<?>[] {});
    }

    protected String buildInitCapPropertyName() {
        return initCap(this.propertyName);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String initCap(final String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    protected java.lang.reflect.Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        try {
            return clazz.getMethod(methodName, argTypes);
        } catch (NoSuchMethodException ex) {
            String msg = "class=" + clazz + " method=" + methodName + "-" + java.util.Arrays.asList(argTypes);
            throw new RuntimeException(msg, ex);
        }
    }

    /**
     * Assert that the object is not null.
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    public int hashCode() {
        return dbmeta.hashCode() + columnDbName.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ColumnInfo)) {
            return false;
        }
        final ColumnInfo target = (ColumnInfo) obj;
        if (!this.dbmeta.equals(target.getDBMeta())) {
            return false;
        }
        if (!this.columnDbName.equals(target.getColumnDbName())) {
            return false;
        }
        return true;
    }

    public String toString() {
        return dbmeta.getTableDbName() + "." + columnDbName;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public DBMeta getDBMeta() {
        return dbmeta;
    }

    /**
     * Get the DB name of the column.
     * @return The DB name of the column. (NotNull)
     */
    public String getColumnDbName() {
        return this.columnDbName;
    }

    /**
     * Get the alias of the column.
     * @return The alias of the column. (Nullable: If the definition about aliases, it returns null.)
     */
    public String getColumnAlias() {
        return this.columnAlias;
    }

    /**
     * Get the name of property for the column. (JavaBeansRule)
     * @return The name of property for the column. (NotNull)
     */
    public String getPropertyName() {
        return this.propertyName;
    }

    /**
     * Get the type of property for the column.
     * @return The type of property for the column. (NotNull)
     */
    public Class<?> getPropertyType() {
        return this.propertyType;
    }

    /**
     * Is the column a part of primary keys?
     * @return Determination.
     */
    public boolean isPrimary() {
        return this.primary;
    }

    /**
     * Is the column auto increment?
     * @return Determination.
     */
    public boolean isAutoIncrement() {
        return this.autoIncrement;
    }

    /**
     * Get the size of the column.
     * @return The size of the column. (Nullable: If the type does not have size, it returns null.)
     */
    public Integer getColumnSize() {
        return this.columnSize;
    }

    /**
     * Get the decimal digits of the column.
     * @return The decimal digits of the column. (Nullable: If the type does not have disits, it returns null.)
     */
    public Integer getColumnDecimalDigits() {
        return this.columnDecimalDigits;
    }

    /**
     * Is the column a part of common columns?
     * @return Determination.
     */
    public boolean isCommonColumn() {
        return this.commonColumn;
    }

    /**
     * Is the column for optimistic lock?
     * @return Determination.
     */
    public boolean isOptimisticLock() {
        return isVersionNo() || isUpdateDate();
    }

    /**
     * Is the column version-no for optimistic lock?
     * @return Determination.
     */
    public boolean isVersionNo() {
        return OptimisticLockType.VERSION_NO == optimisticLockType;
    }

    /**
     * Is the column update-date for optimistic lock?
     * @return Determination.
     */
    public boolean isUpdateDate() {
        return OptimisticLockType.UPDATE_DATE == optimisticLockType;
    }
}
