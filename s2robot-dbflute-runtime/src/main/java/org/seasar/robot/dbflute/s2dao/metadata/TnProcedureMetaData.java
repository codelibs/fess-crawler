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
package org.seasar.robot.dbflute.s2dao.metadata;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnProcedureMetaData {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    private final String _procedureName;
    private final Map<String, TnProcedureParameterType> _parameterTypeMap = createParameterTypeMap();
    private final SortedSet<TnProcedureParameterType> _parameterTypeSortedSet = createParameterTypeSet();
    private List<TnProcedureParameterType> _bindParameterTypeList; // lazy load
    private List<TnProcedureParameterType> _notParamResultTypeList; // lazy load
    private TnProcedureParameterType _returnParameterType;
    private boolean _fixed;

    protected Map<String, TnProcedureParameterType> createParameterTypeMap() {
        return new HashMap<String, TnProcedureParameterType>(); // unordered
    }

    protected SortedSet<TnProcedureParameterType> createParameterTypeSet() {
        return new TreeSet<TnProcedureParameterType>(new Comparator<TnProcedureParameterType>() {
            public int compare(TnProcedureParameterType o1, TnProcedureParameterType o2) {
                return o1.getParameterOrder().compareTo(o2.getParameterOrder());
            }
        });
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnProcedureMetaData(final String procedureName) {
        this._procedureName = procedureName;
    }

    // ===================================================================================
    //                                                                             Factory
    //                                                                             =======
    public String createSql() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        final int argSize;
        {
            final int bindSize = getBindParameterTypeList().size();
            if (hasReturnParameterType()) {
                sb.append("? = ");
                argSize = bindSize - 1;
            } else {
                argSize = bindSize;
            }
        }
        sb.append("call ").append(getProcedureName()).append("(");
        for (int i = 0; i < argSize; i++) {
            sb.append("?, ");
        }
        if (argSize > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append(")}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                                 Fix
    //                                                                                 ===
    public void fix() {
        _fixed = true;
        getBindParameterTypeList(); // for lazy-loading
        getNotParamResultTypeList(); // for lazy-loading
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getProcedureName() {
        return _procedureName;
    }

    private SortedSet<TnProcedureParameterType> getParameterTypeSortedSet() {
        return _parameterTypeSortedSet;
    }

    public List<TnProcedureParameterType> getBindParameterTypeList() {
        if (_bindParameterTypeList != null) {
            return _bindParameterTypeList;
        }
        final SortedSet<TnProcedureParameterType> parameterTypeSortedSet = getParameterTypeSortedSet();
        final List<TnProcedureParameterType> bindList = new ArrayList<TnProcedureParameterType>();
        for (TnProcedureParameterType ppt : parameterTypeSortedSet) {
            if (!ppt.isNotParamResultType()) {
                bindList.add(ppt);
            }
        }
        _bindParameterTypeList = bindList;
        return bindList;
    }

    public List<TnProcedureParameterType> getNotParamResultTypeList() {
        if (_notParamResultTypeList != null) {
            return _notParamResultTypeList;
        }
        final SortedSet<TnProcedureParameterType> parameterTypeSortedSet = getParameterTypeSortedSet();
        final List<TnProcedureParameterType> resultList = new ArrayList<TnProcedureParameterType>();
        for (TnProcedureParameterType ppt : parameterTypeSortedSet) {
            if (ppt.isNotParamResultType()) {
                resultList.add(ppt);
            }
        }
        _notParamResultTypeList = resultList;
        return resultList;
    }

    public boolean hasReturnParameterType() {
        return _returnParameterType != null;
    }

    public TnProcedureParameterType getReturnParameterType() {
        return _returnParameterType;
    }

    public void addParameterType(TnProcedureParameterType parameterType) {
        if (parameterType == null) {
            String msg = "The argument 'parameterType' should not be null!";
            throw new IllegalStateException(msg);
        }
        if (_fixed) {
            String msg = "This object has already been fixed:";
            msg = msg + " added=" + parameterType.getParameterName();
            throw new IllegalStateException(msg);
        }
        final String name = parameterType.getParameterName();
        _parameterTypeMap.put(name, parameterType);
        _parameterTypeSortedSet.add(parameterType);
        if (parameterType.isReturnType()) {
            _returnParameterType = parameterType;
        }
    }
}
