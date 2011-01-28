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
package org.seasar.robot.dbflute.dbmeta.info;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.util.DfReflectionUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * The information of foreign relation.
 * @author jflute
 */
public class ForeignInfo implements RelationInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _foreignPropertyName;
    protected final DBMeta _localDBMeta;
    protected final DBMeta _foreignDBMeta;
    protected final Map<ColumnInfo, ColumnInfo> _localForeignColumnInfoMap;
    protected final Map<ColumnInfo, ColumnInfo> _foreignLocalColumnInfoMap;
    protected final int _relationNo;
    protected final boolean _oneToOne;
    protected final boolean _bizOneToOne;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ForeignInfo(String foreignPropertyName, DBMeta localDBMeta, DBMeta foreignDBMeta,
            Map<ColumnInfo, ColumnInfo> localForeignColumnInfoMap, int relationNo, boolean oneToOne, boolean bizOneToOne) {
        assertObjectNotNull("foreignPropertyName", foreignPropertyName);
        assertObjectNotNull("localDBMeta", localDBMeta);
        assertObjectNotNull("foreignDBMeta", foreignDBMeta);
        assertObjectNotNull("localForeignColumnInfoMap", localForeignColumnInfoMap);
        this._foreignPropertyName = foreignPropertyName;
        this._localDBMeta = localDBMeta;
        this._foreignDBMeta = foreignDBMeta;
        this._localForeignColumnInfoMap = localForeignColumnInfoMap;
        final Set<ColumnInfo> keySet = localForeignColumnInfoMap.keySet();
        _foreignLocalColumnInfoMap = new LinkedHashMap<ColumnInfo, ColumnInfo>();
        for (final Iterator<ColumnInfo> ite = keySet.iterator(); ite.hasNext();) {
            final ColumnInfo key = ite.next();
            final ColumnInfo value = localForeignColumnInfoMap.get(key);
            _foreignLocalColumnInfoMap.put(value, key);
        }
        this._relationNo = relationNo;
        this._oneToOne = oneToOne;
        this._bizOneToOne = bizOneToOne;
    }

    // ===================================================================================
    //                                                                              Finder
    //                                                                              ======
    public ColumnInfo findLocalByForeign(String foreignColumnDbName) {
        final ColumnInfo keyColumnInfo = _foreignDBMeta.findColumnInfo(foreignColumnDbName);
        final ColumnInfo resultColumnInfo = (ColumnInfo) _foreignLocalColumnInfoMap.get(keyColumnInfo);
        if (resultColumnInfo == null) {
            String msg = "Not found by foreignColumnDbName in foreignLocalColumnInfoMap:";
            msg = msg + " foreignColumnDbName=" + foreignColumnDbName + " foreignLocalColumnInfoMap="
                    + _foreignLocalColumnInfoMap;
            throw new IllegalArgumentException(msg);
        }
        return resultColumnInfo;
    }

    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    @SuppressWarnings("unchecked")
    public <PROPERTY extends Entity> PROPERTY read(Entity localEntity) {
        return (PROPERTY) invokeMethod(reader(), localEntity, new Object[] {});
    }

    public Method reader() {
        final Class<? extends Entity> localType = _localDBMeta.getEntityType();
        final String methodName = buildAccessorName("get");
        final Method method = findMethod(localType, buildAccessorName("get"), new Class[] {});
        if (method == null) {
            String msg = "Failed to find the method by the name:";
            msg = msg + " methodName=" + methodName;
            throw new IllegalStateException(msg);
        }
        return method;
    }

    public void write(Entity localEntity, Entity foreignEntity) {
        invokeMethod(writer(), localEntity, new Object[] { foreignEntity });
    }

    public Method writer() {
        final Class<? extends Entity> localType = _localDBMeta.getEntityType();
        final Class<? extends Entity> foreignType = _foreignDBMeta.getEntityType();
        final String methodName = buildAccessorName("set");
        final Method method = findMethod(localType, methodName, new Class[] { foreignType });
        if (method == null) {
            String msg = "Failed to find the method by the name:";
            msg = msg + " methodName=" + methodName;
            msg = msg + " foreignType=" + foreignType;
            throw new IllegalStateException(msg);
        }
        return method;
    }

    protected String buildAccessorName(String prefix) {
        return prefix + initCap(_foreignPropertyName);
    }

    // ===================================================================================
    //                                                                           Implement
    //                                                                           =========
    public String getRelationPropertyName() {
        return getForeignPropertyName();
    }

    public DBMeta getTargetDBMeta() {
        return getForeignDBMeta();
    }

    public Map<ColumnInfo, ColumnInfo> getLocalTargetColumnInfoMap() {
        return getLocalForeignColumnInfoMap();
    }

    public boolean isReferrer() {
        return false;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String initCap(final String name) {
        return Srl.initCap(name);
    }

    protected Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        return DfReflectionUtil.getAccessibleMethod(clazz, methodName, argTypes);
    }

    protected Object invokeMethod(Method method, Object target, Object[] args) {
        return DfReflectionUtil.invoke(method, target, args);
    }

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
        return _foreignPropertyName.hashCode() + _localDBMeta.hashCode() + _foreignDBMeta.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ForeignInfo)) {
            return false;
        }
        final ForeignInfo target = (ForeignInfo) obj;
        if (!this._foreignPropertyName.equals(target.getForeignPropertyName())) {
            return false;
        }
        if (!this._localDBMeta.equals(target.getLocalDBMeta())) {
            return false;
        }
        if (!this._foreignDBMeta.equals(target.getForeignDBMeta())) {
            return false;
        }
        return true;
    }

    public String toString() {
        return _localDBMeta.getTableDbName() + "." + _foreignPropertyName + "->" + _foreignDBMeta.getTableDbName();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the property name of the foreign relation. <br />
     * For example, if the member entity has getMemberStatus(), this returns 'memberStatus'.
     * @return The string for property name. (NotNull)
     */
    public String getForeignPropertyName() {
        return _foreignPropertyName;
    }

    /**
     * Get the DB meta of the local table. <br />
     * For example, if the relation MEMBER and MEMBER_STATUS, this returns MEMBER's one.
     * @return The DB meta singleton instance. (NotNull)
     */
    public DBMeta getLocalDBMeta() {
        return _localDBMeta;
    }

    /**
     * Get the DB meta of the foreign table. <br />
     * For example, if the relation MEMBER and MEMBER_STATUS, this returns MEMBER_STATUS's one.
     * @return The DB meta singleton instance. (NotNull)
     */
    public DBMeta getForeignDBMeta() {
        return _foreignDBMeta;
    }

    /**
     * Get the read-only map, key is a local column info, value is a foreign column info.
     * @return The read-only map. (NotNull)
     */
    public Map<ColumnInfo, ColumnInfo> getLocalForeignColumnInfoMap() {
        return Collections.unmodifiableMap(_localForeignColumnInfoMap); // as read-only
    }

    /**
     * Get the read-only map, key is a foreign column info, value is a local column info.
     * @return The read-only map. (NotNull)
     */
    public Map<ColumnInfo, ColumnInfo> getForeignLocalColumnInfoMap() {
        return Collections.unmodifiableMap(_foreignLocalColumnInfoMap); // as read-only
    }

    /**
     * Get the number of a relation. (internal property)
     * @return The number of a relation. (NotNull, NotMinus)
     */
    public int getRelationNo() {
        return _relationNo;
    }

    /**
     * Does the relation is one-to-one?
     * @return Determination.
     */
    public boolean isOneToOne() {
        return _oneToOne;
    }

    /**
     * Does the relation is biz-one-to-one?
     * @return Determination.
     */
    public boolean isBizOneToOne() {
        return _bizOneToOne;
    }
}
