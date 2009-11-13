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
package org.seasar.robot.dbflute.cbean;

import java.util.ArrayList;
import java.util.List;

/**
 * The builder of result bean.
 * @param <ENTITY> The type of entity.
 * @author jflute
 */
public class ResultBeanBuilder<ENTITY> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableDbName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public ResultBeanBuilder(String tableDbName) {
        _tableDbName = tableDbName;
    }

    // ===================================================================================
    //                                                                             Builder
    //                                                                             =======
    /**
     * Build the result bean of list without order-by clause. {for Various}
     * @param selectedList Selected list. (NotNull)
     * @return The result bean of list. (NotNull)
     */
    public ListResultBean<ENTITY> buildListResultBean(List<ENTITY> selectedList) {
        ListResultBean<ENTITY> rb = new ListResultBean<ENTITY>();
        rb.setTableDbName(_tableDbName);
        rb.setAllRecordCount(selectedList.size());
        rb.setSelectedList(selectedList);
        return rb;
    }

    /**
     * Build the result bean of list. {for CB}
     * @param cb The condition-bean. (NotNull)
     * @param selectedList Selected list. (NotNull)
     * @return The result bean of list. (NotNull)
     */
    public ListResultBean<ENTITY> buildListResultBean(ConditionBean cb, List<ENTITY> selectedList) {
        ListResultBean<ENTITY> rb = new ListResultBean<ENTITY>();
        rb.setTableDbName(_tableDbName);
        rb.setAllRecordCount(selectedList.size());
        rb.setSelectedList(selectedList);
        rb.setOrderByClause(cb.getSqlComponentOfOrderByClause());
        return rb;
    }
    
    /**
     * Build the result bean of list as empty. {for CB}
     * @param pb The bean of paging. (NotNull)
     * @return The result bean of list as empty. (NotNull)
     */
    public ListResultBean<ENTITY> buildEmptyListResultBean(PagingBean pb) {
        ListResultBean<ENTITY> rb = new ListResultBean<ENTITY>();
        rb.setTableDbName(_tableDbName);
        rb.setAllRecordCount(0);
        rb.setSelectedList(newEmptyList());
        rb.setOrderByClause(pb.getSqlComponentOfOrderByClause());
        return rb;
    }

    protected List<ENTITY> newEmptyList() {
        return new ArrayList<ENTITY>();
    }

    /**
     * Build the result bean of paging. {for Paging}
     * @param pb The bean of paging. (NotNull)
     * @param allRecordCount All record count.
     * @param selectedList The list of selected entity. (NotNull)
     * @return The result bean of paging. (NotNull)
     */
    public PagingResultBean<ENTITY> buildPagingResultBean(PagingBean pb, int allRecordCount, List<ENTITY> selectedList) {
        PagingResultBean<ENTITY> rb = new PagingResultBean<ENTITY>();
        rb.setTableDbName(_tableDbName);
        rb.setAllRecordCount(allRecordCount);
        rb.setSelectedList(selectedList);
        rb.setOrderByClause(pb.getSqlComponentOfOrderByClause());
        rb.setPageSize(pb.getFetchSize());
        rb.setCurrentPageNumber(pb.getFetchPageNumber());
        return rb;
    }
}
