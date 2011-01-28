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
package org.seasar.robot.dbflute;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.jdbc.ParameterUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The interface of entity.
 * @author jflute
 */
public interface Entity {

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * Get the target DB meta.
     * @return The instance of DBMeta type. (NotNull)
     */
    DBMeta getDBMeta();

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    /**
     * Get table DB name.
     * @return The string for name. (NotNull)
     */
    String getTableDbName();

    /**
     * Get table property name according to Java Beans rule.
     * @return The string for name. (NotNull)
     */
    String getTablePropertyName();

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    /**
     * Does it have the value of primary keys?
     * @return Determination. (if all PK values are not null, returns true)
     */
    boolean hasPrimaryKeyValue();

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    /**
     * Get the set of modified properties. (basically for Framework)<br />
     * The properties needs to be according to Java Beans rule.
     * @return The set instance that contains names of modified property. (NotNull)
     */
    Set<String> modifiedProperties();

    /**
     * Clear the information of modified properties. (basically for Framework)
     */
    void clearModifiedInfo();

    /**
     * Does it have modifications of property names. (basically for Framework)
     * @return Determination.
     */
    boolean hasModification();

    /**
     * Entity modified properties. (basically for Framework)
     */
    public static class EntityModifiedProperties implements Serializable {

        /** Serial version UID. (Default) */
        private static final long serialVersionUID = 1L;

        /** Set of properties. */
        protected final Set<String> _propertiesSet = new LinkedHashSet<String>();

        /**
         * Add property name. (according to Java Beans rule)
         * @param propertyName The string for name. (NotNull)
         */
        public void addPropertyName(String propertyName) {
            _propertiesSet.add(propertyName);
        }

        /**
         * Get the set of properties.
         * @return The set of properties. (NotNull)
         */
        public Set<String> getPropertyNames() {
            return _propertiesSet;
        }

        /**
         * Is empty?
         * @return Determination.
         */
        public boolean isEmpty() {
            return _propertiesSet.isEmpty();
        }

        /**
         * Clear the set of properties.
         */
        public void clear() {
            _propertiesSet.clear();
        }

        /**
         * Remove property name from the set. (according to Java Beans rule)
         * @param propertyName The string for name. (NotNull)
         */
        public void remove(String propertyName) {
            _propertiesSet.remove(propertyName);
        }
    }

    // ===================================================================================
    //                                                                      Display String
    //                                                                      ==============
    /**
     * Convert the entity to display string with relation information.
     * @return The display string of basic informations with one-nested relation values. (NotNull)
     */
    String toStringWithRelation();

    /**
     * Build display string flexibly.
     * @param name The name for display. (NullAllowed: If it's null, it does not have a name)
     * @param column Does it contains column values or not?
     * @param relation Does it contains relation existences or not?
     * @return The display string for this entity. (NotNull)
     */
    String buildDisplayString(String name, boolean column, boolean relation);

    // ===================================================================================
    //                                                                      Display String
    //                                                                      ==============
    public static final class InternalUtil {

        @SuppressWarnings("unchecked")
        public static <NUMBER extends Number> NUMBER toNumber(Object obj, Class<NUMBER> type) {
            return (NUMBER) DfTypeUtil.toNumber(obj, type);
        }

        public static Boolean toBoolean(Object obj) {
            return DfTypeUtil.toBoolean(obj);
        }

        public static boolean isSameValue(Object value1, Object value2) {
            if (value1 == null && value2 == null) {
                return true;
            }
            if (value1 == null || value2 == null) {
                return false;
            }
            if (value1 instanceof byte[] && value2 instanceof byte[]) {
                return isSameValueBytes((byte[]) value1, (byte[]) value2);
            }
            return value1.equals(value2);
        }

        public static boolean isSameValueBytes(byte[] bytes1, byte[] bytes2) {
            if (bytes1 == null && bytes2 == null) {
                return true;
            }
            if (bytes1 == null || bytes2 == null) {
                return false;
            }
            if (bytes1.length != bytes2.length) {
                return false;
            }
            for (int i = 0; i < bytes1.length; i++) {
                if (bytes1[i] != bytes2[i]) {
                    return false;
                }
            }
            return true;
        }

        public static int calculateHashcode(int result, Object value) { // calculateHashcode()
            if (value == null) {
                return result;
            }
            return (31 * result) + (value instanceof byte[] ? ((byte[]) value).length : value.hashCode());
        }

        public static String convertEmptyToNull(String value) {
            return ParameterUtil.convertEmptyToNull(value);
        }

        public static String toClassTitle(Entity entity) {
            return DfTypeUtil.toClassTitle(entity);
        }

        public static String toString(Date date, String pattern) {
            if (date == null) {
                return null;
            }
            final String str = DfTypeUtil.toString(date, pattern);
            return (DfTypeUtil.isDateBC(date) ? "BC" : "") + str;
        }

        public static String toString(byte[] bytes) {
            return "byte[" + (bytes != null ? String.valueOf(bytes.length) : "null") + "]";
        }
    }
}
