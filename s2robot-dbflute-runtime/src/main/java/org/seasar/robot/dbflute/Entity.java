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
package org.seasar.robot.dbflute;

import java.util.LinkedHashSet;
import java.util.Set;

import org.seasar.robot.dbflute.dbmeta.DBMeta;

/**
 * The interface of entity.
 * @author jflute
 */
public interface Entity {

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * Get the instance of target dbmeta.
     * @return DBMeta. (NotNull)
     */
    public DBMeta getDBMeta();

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    /**
     * Get table DB name.
     * @return Table DB name. (NotNull)
     */
    public String getTableDbName();

    /**
     * Get table property name.
     * @return Table property name. (NotNull)
     */
    public String getTablePropertyName();

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * Has the value of primary-key?
     * @return Determination.
     */
    public boolean hasPrimaryKeyValue();

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    /**
     * Get modified property names. (JavaBeansRule)
     * @return Modified property names. (NotNull)
     */
    public Set<String> getModifiedPropertyNames();

    /**
     * Clear modified property names.
     */
    public void clearModifiedPropertyNames();

    /**
     * Entity modified properties.
     */
    public static class EntityModifiedProperties implements java.io.Serializable {

        /** Serial version UID. (Default) */
        private static final long serialVersionUID = 1L;

        /** Set of properties. */
        protected Set<String> _propertiesSet = new LinkedHashSet<String>();

        /**
         * Add property name. (JavaBeansRule)
         * @param propertyName Property name. (Nullable)
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
         * Remove property name from the set. (JavaBeansRule)
         * @param propertyName Property name. (Nullable)
         */
        public void remove(String propertyName) {
            _propertiesSet.remove(propertyName);
        }
    }

    // ===================================================================================
    //                                                                      Display String
    //                                                                      ==============
    /**
     * @return The display string of basic informations with one-nested relation values. (NotNull)
     */
    public String toStringWithRelation();

    /**
     * @param name The name for display. (Nullable: If it's null, it does not have a name)
     * @param column Does it contains column values or not?
     * @param relation Does it contains relation existences or not?
     * @return The display string for this entity. (NotNull)
     */
    public String buildDisplayString(String name, boolean column, boolean relation);
}
