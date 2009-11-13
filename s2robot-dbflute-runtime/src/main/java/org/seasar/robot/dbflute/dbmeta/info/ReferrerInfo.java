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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.seasar.robot.dbflute.dbmeta.DBMeta;


/**
 * The information of referrer relation.
 * @author jflute
 */
public class ReferrerInfo implements RelationInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String referrerPropertyName;
    protected final DBMeta localDBMeta;
    protected final DBMeta referrerDBMeta;
    protected final Map<ColumnInfo, ColumnInfo> localReferrerColumnInfoMap;
    protected final Map<ColumnInfo, ColumnInfo> referrerLocalColumnInfoMap;
    protected final boolean oneToOne;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ReferrerInfo(String referrerPropertyName, DBMeta localDBMeta, DBMeta referrerDBMeta
                      , Map<ColumnInfo, ColumnInfo> localReferrerColumnInfoMap
                      , boolean oneToOne) {
        assertObjectNotNull("referrerPropertyName", referrerPropertyName);
        assertObjectNotNull("localDBMeta", localDBMeta);
        assertObjectNotNull("referrerDBMeta", referrerDBMeta);
        assertObjectNotNull("localReferrerColumnInfoMap", localReferrerColumnInfoMap);
        this.referrerPropertyName = referrerPropertyName;
        this.localDBMeta = localDBMeta;
        this.referrerDBMeta = referrerDBMeta;
        this.localReferrerColumnInfoMap = localReferrerColumnInfoMap;
        final Set<ColumnInfo> keySet = localReferrerColumnInfoMap.keySet();
        referrerLocalColumnInfoMap = new LinkedHashMap<ColumnInfo, ColumnInfo>();
        for (final Iterator<ColumnInfo> ite = keySet.iterator(); ite.hasNext(); ) {
            final ColumnInfo key = ite.next();
            final ColumnInfo value = localReferrerColumnInfoMap.get(key);
            referrerLocalColumnInfoMap.put(value, key);
        }
        this.oneToOne = oneToOne;
    }
    
    // ===================================================================================
    //                                                                              Finder
    //                                                                              ======
    public ColumnInfo findLocalByReferrer(String referrerColumnDbName) {
        final ColumnInfo keyColumnInfo = referrerDBMeta.findColumnInfo(referrerColumnDbName);
        final ColumnInfo resultColumnInfo = (ColumnInfo)referrerLocalColumnInfoMap.get(keyColumnInfo);
        if (resultColumnInfo == null) {
            String msg = "Not found by referrerColumnDbName in referrerLocalColumnInfoMap:";
            msg = msg + " referrerColumnDbName=" + referrerColumnDbName + " referrerLocalColumnInfoMap=" + referrerLocalColumnInfoMap;
            throw new IllegalArgumentException(msg);
        }
        return resultColumnInfo;
    }

    public ColumnInfo findReferrerByLocal(String localColumnDbName) {
        final ColumnInfo keyColumnInfo = localDBMeta.findColumnInfo(localColumnDbName);
        final ColumnInfo resultColumnInfo = (ColumnInfo)localReferrerColumnInfoMap.get(keyColumnInfo);
        if (resultColumnInfo == null) {
            String msg = "Not found by localColumnDbName in localReferrerColumnInfoMap:";
            msg = msg + " localColumnDbName=" + localColumnDbName + " localReferrerColumnInfoMap=" + localReferrerColumnInfoMap;
            throw new IllegalArgumentException(msg);
        }
        return resultColumnInfo;
    }

    // ===================================================================================
    //                                                                              Finder
    //                                                                              ======
    public java.lang.reflect.Method findSetter() {
        return findMethod(localDBMeta.getEntityType(), "set" + buildInitCapPropertyName(), new Class[] { java.util.List.class });
    }

    public java.lang.reflect.Method findGetter() {
        return findMethod(localDBMeta.getEntityType(), "get" + buildInitCapPropertyName(), new Class[] {});
    }

    protected String buildInitCapPropertyName() {
        return initCap(this.referrerPropertyName);
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
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    protected java.lang.reflect.Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        try {
            return clazz.getMethod(methodName, argTypes);
        } catch (NoSuchMethodException ex) {
            String msg = "class=" + clazz + " method=" + methodName + "-" + Arrays.asList(argTypes);
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
        return referrerPropertyName.hashCode() + localDBMeta.hashCode() + referrerDBMeta.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ReferrerInfo)) {
            return false;
        }
        final ReferrerInfo target = (ReferrerInfo)obj;
        if (!this.referrerPropertyName.equals(target.getReferrerPropertyName())) {
            return false;
        }
        if (!this.localDBMeta.equals(target.getLocalDBMeta())) {
            return false;
        }
        if (!this.referrerDBMeta.equals(target.getReferrerDBMeta())) {
            return false;
        }
        return true;
    }

    public String toString() {
        return localDBMeta.getTableDbName() + "." + referrerPropertyName + "<-" + referrerDBMeta.getTableDbName();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getReferrerPropertyName() {
        return referrerPropertyName;
    }

    public DBMeta getLocalDBMeta() {
        return localDBMeta;
    }

    public DBMeta getReferrerDBMeta() {
        return referrerDBMeta;
    }

    public Map<ColumnInfo, ColumnInfo> getLocalReferrerColumnInfoMap() {
        return new LinkedHashMap<ColumnInfo, ColumnInfo>(localReferrerColumnInfoMap); // as snapshot
    }

    public Map<ColumnInfo, ColumnInfo> getReferrerLocalColumnInfoMap() {
        return new LinkedHashMap<ColumnInfo, ColumnInfo>(referrerLocalColumnInfoMap); // as snapshot
    }

    public boolean isOneToOne() {
        return oneToOne;
    }
}
