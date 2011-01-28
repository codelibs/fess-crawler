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
package org.seasar.robot.dbflute.cbean.sqlclause.query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jflute
 */
public class OrScopeQueryInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<QueryClause> _tmpOrWhereList;
    protected List<QueryClause> _tmpOrBaseTableInlineWhereList;
    protected Map<String, List<QueryClause>> _tmpOrAdditionalOnClauseListMap;
    protected Map<String, List<QueryClause>> _tmpOrOuterJoinInlineClauseListMap;
    protected OrScopeQueryInfo _parentInfo; // null means base point
    protected List<OrScopeQueryInfo> _childInfoList;

    // ===================================================================================
    //                                                                            Tmp List
    //                                                                            ========
    public List<QueryClause> getTmpOrAdditionalOnClauseList(String aliasName) {
        List<QueryClause> orClauseList = getTmpOrAdditionalOnClauseListMap().get(aliasName);
        if (orClauseList != null) {
            return orClauseList;
        }
        orClauseList = new ArrayList<QueryClause>();
        _tmpOrAdditionalOnClauseListMap.put(aliasName, orClauseList);
        return orClauseList;
    }

    public List<QueryClause> getTmpOrOuterJoinInlineClauseList(String aliasName) {
        List<QueryClause> orClauseList = getTmpOrOuterJoinInlineClauseListMap().get(aliasName);
        if (orClauseList != null) {
            return orClauseList;
        }
        orClauseList = new ArrayList<QueryClause>();
        _tmpOrOuterJoinInlineClauseListMap.put(aliasName, orClauseList);
        return orClauseList;
    }

    public List<QueryClause> getTmpOrWhereList() {
        if (_tmpOrWhereList == null) {
            _tmpOrWhereList = new ArrayList<QueryClause>(4);
        }
        return _tmpOrWhereList;
    }

    public void setTmpOrWhereList(List<QueryClause> tmpOrWhereList) {
        this._tmpOrWhereList = tmpOrWhereList;
    }

    public List<QueryClause> getTmpOrBaseTableInlineWhereList() {
        if (_tmpOrBaseTableInlineWhereList == null) {
            _tmpOrBaseTableInlineWhereList = new ArrayList<QueryClause>(2);
        }
        return _tmpOrBaseTableInlineWhereList;
    }

    public void setTmpOrBaseTableInlineWhereList(List<QueryClause> tmpOrBaseTableInlineWhereList) {
        this._tmpOrBaseTableInlineWhereList = tmpOrBaseTableInlineWhereList;
    }

    public Map<String, List<QueryClause>> getTmpOrAdditionalOnClauseListMap() {
        if (_tmpOrAdditionalOnClauseListMap == null) {
            _tmpOrAdditionalOnClauseListMap = new LinkedHashMap<String, List<QueryClause>>(2);
        }
        return _tmpOrAdditionalOnClauseListMap;
    }

    public void setTmpOrAdditionalOnClauseListMap(Map<String, List<QueryClause>> tmpOrAdditionalOnClauseListMap) {
        this._tmpOrAdditionalOnClauseListMap = tmpOrAdditionalOnClauseListMap;
    }

    public Map<String, List<QueryClause>> getTmpOrOuterJoinInlineClauseListMap() {
        if (_tmpOrOuterJoinInlineClauseListMap == null) {
            _tmpOrOuterJoinInlineClauseListMap = new LinkedHashMap<String, List<QueryClause>>(2);
        }
        return _tmpOrOuterJoinInlineClauseListMap;
    }

    public void setTmpOrOuterJoinInlineClauseListMap(Map<String, List<QueryClause>> tmpOrOuterJoinInlineClauseListMap) {
        this._tmpOrOuterJoinInlineClauseListMap = tmpOrOuterJoinInlineClauseListMap;
    }

    // ===================================================================================
    //                                                                   Child Parent Info
    //                                                                   =================
    public boolean hasChildInfo() {
        return _childInfoList != null && !_childInfoList.isEmpty();
    }

    public OrScopeQueryInfo getParentInfo() {
        return _parentInfo;
    }

    public void setParentInfo(OrScopeQueryInfo parentInfo) {
        _parentInfo = parentInfo;
    }

    public List<OrScopeQueryInfo> getChildInfoList() {
        if (_childInfoList == null) {
            _childInfoList = new ArrayList<OrScopeQueryInfo>();
        }
        return _childInfoList;
    }

    public void addChildInfo(OrScopeQueryInfo childInfo) {
        childInfo.setParentInfo(this);
        getChildInfoList().add(childInfo);
    }
}
