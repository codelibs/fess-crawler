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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.util.DfReflectionUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * The information of referrer relation.
 * @author jflute
 */
public class ReferrerInfo implements RelationInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _referrerPropertyName;
    protected final DBMeta _localDBMeta;
    protected final DBMeta _referrerDBMeta;
    protected final Map<ColumnInfo, ColumnInfo> _localReferrerColumnInfoMap;
    protected final Map<ColumnInfo, ColumnInfo> _referrerLocalColumnInfoMap;
    protected final boolean _oneToOne;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReferrerInfo(String referrerPropertyName, DBMeta localDBMeta, DBMeta referrerDBMeta,
            Map<ColumnInfo, ColumnInfo> localReferrerColumnInfoMap, boolean oneToOne) {
        assertObjectNotNull("referrerPropertyName", referrerPropertyName);
        assertObjectNotNull("localDBMeta", localDBMeta);
        assertObjectNotNull("referrerDBMeta", referrerDBMeta);
        assertObjectNotNull("localReferrerColumnInfoMap", localReferrerColumnInfoMap);
        this._referrerPropertyName = referrerPropertyName;
        this._localDBMeta = localDBMeta;
        this._referrerDBMeta = referrerDBMeta;
        this._localReferrerColumnInfoMap = localReferrerColumnInfoMap;
        final Set<ColumnInfo> keySet = localReferrerColumnInfoMap.keySet();
        _referrerLocalColumnInfoMap = new LinkedHashMap<ColumnInfo, ColumnInfo>();
        for (final Iterator<ColumnInfo> ite = keySet.iterator(); ite.hasNext();) {
            final ColumnInfo key = ite.next();
            final ColumnInfo value = localReferrerColumnInfoMap.get(key);
            _referrerLocalColumnInfoMap.put(value, key);
        }
        this._oneToOne = oneToOne;
    }

    // ===================================================================================
    //                                                                              Finder
    //                                                                              ======
    public ColumnInfo findLocalByReferrer(String referrerColumnDbName) {
        final ColumnInfo keyColumnInfo = _referrerDBMeta.findColumnInfo(referrerColumnDbName);
        final ColumnInfo resultColumnInfo = (ColumnInfo) _referrerLocalColumnInfoMap.get(keyColumnInfo);
        if (resultColumnInfo == null) {
            String msg = "Not found by referrerColumnDbName in referrerLocalColumnInfoMap:";
            msg = msg + " referrerColumnDbName=" + referrerColumnDbName + " referrerLocalColumnInfoMap="
                    + _referrerLocalColumnInfoMap;
            throw new IllegalArgumentException(msg);
        }
        return resultColumnInfo;
    }

    public ColumnInfo findReferrerByLocal(String localColumnDbName) {
        final ColumnInfo keyColumnInfo = _localDBMeta.findColumnInfo(localColumnDbName);
        final ColumnInfo resultColumnInfo = (ColumnInfo) _localReferrerColumnInfoMap.get(keyColumnInfo);
        if (resultColumnInfo == null) {
            String msg = "Not found by localColumnDbName in localReferrerColumnInfoMap:";
            msg = msg + " localColumnDbName=" + localColumnDbName + " localReferrerColumnInfoMap="
                    + _localReferrerColumnInfoMap;
            throw new IllegalArgumentException(msg);
        }
        return resultColumnInfo;
    }

    // ===================================================================================
    //                                                                          Reflection
    //                                                                          ==========
    @SuppressWarnings("unchecked")
    public <PROPERTY extends List> PROPERTY read(Entity localEntity) {
        return (PROPERTY) invokeMethod(reader(), localEntity, new Object[] {});
    }

    public Method reader() {
        final Class<? extends Entity> localType = _localDBMeta.getEntityType();
        final String methodName = buildAccessorName("get");
        final Method method = findMethod(localType, methodName, new Class[] {});
        if (method == null) {
            String msg = "Failed to find the method by the name:";
            msg = msg + " methodName=" + methodName;
            throw new IllegalStateException(msg);
        }
        return method;
    }

    public void write(Entity localEntity, List<? extends Entity> referrerEntityList) {
        invokeMethod(writer(), localEntity, new Object[] { referrerEntityList });
    }

    public Method writer() {
        final Class<? extends Entity> localType = _localDBMeta.getEntityType();
        final String methodName = buildAccessorName("set");
        final Method method = findMethod(localType, methodName, new Class[] { List.class });
        if (method == null) {
            String msg = "Failed to find the method by the name:";
            msg = msg + " methodName=" + methodName;
            throw new IllegalStateException(msg);
        }
        return method;
    }

    protected String buildAccessorName(String prefix) {
        return prefix + initCap(_referrerPropertyName);
    }

    // ===================================================================================
    //                                                                           Implement
    //                                                                           =========
    public String getRelationPropertyName() {
        return getReferrerPropertyName();
    }

    public DBMeta getTargetDBMeta() {
        return getReferrerDBMeta();
    }

    public Map<ColumnInfo, ColumnInfo> getLocalTargetColumnInfoMap() {
        return getLocalReferrerColumnInfoMap();
    }

    public boolean isReferrer() {
        return true;
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
        return _referrerPropertyName.hashCode() + _localDBMeta.hashCode() + _referrerDBMeta.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ReferrerInfo)) {
            return false;
        }
        final ReferrerInfo target = (ReferrerInfo) obj;
        if (!this._referrerPropertyName.equals(target.getReferrerPropertyName())) {
            return false;
        }
        if (!this._localDBMeta.equals(target.getLocalDBMeta())) {
            return false;
        }
        if (!this._referrerDBMeta.equals(target.getReferrerDBMeta())) {
            return false;
        }
        return true;
    }

    public String toString() {
        return _localDBMeta.getTableDbName() + "." + _referrerPropertyName + "<-" + _referrerDBMeta.getTableDbName();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the property name of the foreign relation. <br />
     * For example, if the relation MEMBER and PURCHASE, this returns 'purchaseList'.
     * @return The string for property name. (NotNull)
     */
    public String getReferrerPropertyName() {
        return _referrerPropertyName;
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
     * Get the DB meta of the referrer table. <br />
     * For example, if the relation MEMBER and MEMBER_STATUS, this returns MEMBER_STATUS's one.
     * @return The DB meta singleton instance. (NotNull)
     */
    public DBMeta getReferrerDBMeta() {
        return _referrerDBMeta;
    }

    /**
     * Get the read-only map, key is a local column info, value is a referrer column info.
     * @return The read-only map. (NotNull)
     */
    public Map<ColumnInfo, ColumnInfo> getLocalReferrerColumnInfoMap() {
        return new LinkedHashMap<ColumnInfo, ColumnInfo>(_localReferrerColumnInfoMap); // as snapshot
    }

    /**
     * Get the read-only map, key is a referrer column info, value is a column column info.
     * @return The read-only map. (NotNull)
     */
    public Map<ColumnInfo, ColumnInfo> getReferrerLocalColumnInfoMap() {
        return new LinkedHashMap<ColumnInfo, ColumnInfo>(_referrerLocalColumnInfoMap); // as snapshot
    }

    /**
     * Does the relation is one-to-one? <br />
     * But basically this returns false because DBFlute treats one-to-one relations as a foreign relation.  
     * @return Determination.
     */
    public boolean isOneToOne() {
        return _oneToOne;
    }
}
