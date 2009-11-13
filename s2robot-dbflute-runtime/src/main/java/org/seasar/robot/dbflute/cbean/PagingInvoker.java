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

import java.util.List;

import org.seasar.robot.dbflute.exception.DangerousResultSizeException;
import org.seasar.robot.dbflute.exception.PagingStatusInvalidException;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The invoker of paging.
 * @param <ENTITY> The type of entity.
 * @author jflute
 */
public class PagingInvoker<ENTITY> {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _tableDbName;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PagingInvoker(String tableDbName) {
        _tableDbName = tableDbName;
    }

    // ===================================================================================
    //                                                                              Invoke
    //                                                                              ======
    /**
     * Invoke select-page by handler.
     * @param handler The handler of paging. (NotNull)
     * @return The result bean of paging. (NotNull)
     */
    public PagingResultBean<ENTITY> invokePaging(PagingHandler<ENTITY> handler) {
        assertObjectNotNull("handler", handler);
        final PagingBean pagingBean = handler.getPagingBean();
        assertObjectNotNull("handler.getPagingBean()", pagingBean);
        if (!pagingBean.isFetchScopeEffective()) {
            throwPagingStatusInvalidException(pagingBean);
        }
        final int safetyMaxResultSize = pagingBean.getSafetyMaxResultSize();
        final ResultBeanBuilder<ENTITY> builder = createResultBeanBuilder();
        final int allRecordCount;
        final List<ENTITY> selectedList;
        try {
            if (pagingBean.isCountLater()) {
                selectedList = handler.paging();
                if (isCurrentLastPage(selectedList, pagingBean)) {
                    allRecordCount = deriveAllRecordCountFromLastPageValues(selectedList, pagingBean);
                } else {
                    allRecordCount = handler.count();
                }
                checkSafetyResultIfNeed(safetyMaxResultSize, allRecordCount);
            } else { // basically main here
                allRecordCount = handler.count();
                checkSafetyResultIfNeed(safetyMaxResultSize, allRecordCount);
                if (allRecordCount == 0) {
                    selectedList = builder.buildEmptyListResultBean(pagingBean);
                } else {
                    selectedList = handler.paging();
                }
            }
            final PagingResultBean<ENTITY> rb = builder.buildPagingResultBean(pagingBean, allRecordCount, selectedList);
            if (pagingBean.canPagingReSelect() && isNecessaryToReadPageAgain(rb)) {
                pagingBean.fetchPage(rb.getAllPageCount());
                final int reAllRecordCount = handler.count();
                final List<ENTITY> reSelectedList = handler.paging();
                return builder.buildPagingResultBean(pagingBean, reAllRecordCount, reSelectedList);
            } else {
                return rb;
            }
        } finally {
            pagingBean.xsetPaging(true); // restore its paging state finally
        }
    }

    protected void throwPagingStatusInvalidException(PagingBean pagingBean) {
        boolean cbean = pagingBean instanceof ConditionBean;
        String name = cbean ? "condition-bean" : "parameter-bean";
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The status of paging was INVALID!" + ln();
        msg = msg + "(Paging parameters was not found)" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Confirm your logic for paging of " + name + "." + ln();
        msg = msg + "Paging execution needs paging parameters 'pageSize' and 'pageNumber'." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        if (cbean) {
            msg = msg + "    MemberCB cb = new MemberCB();" + ln();
            msg = msg + "    cb.query().set...;" + ln();
            msg = msg + "    ... = memberBhv.selectPage(cb);" + ln();
        } else {
            msg = msg + "    SimpleMemberPmb pmb = new SimpleMemberPmb();" + ln();
            msg = msg + "    pmb.set...;" + ln();
            msg = msg + "    ... = memberBhv.outsideSql().manualPaging().selectPage(...);" + ln();
        }
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "    (o):" + ln();
        msg = msg + "    /- - - - - - - - - - - - - - - - - - - - - - - - - - " + ln();
        if (cbean) {
            msg = msg + "    MemberCB cb = new MemberCB();" + ln();
            msg = msg + "    cb.query().set...;" + ln();
            msg = msg + "    cb.paging(20, 2); // *Point!" + ln();
            msg = msg + "    ... = memberBhv.selectPage(cb);" + ln();
        } else {
            msg = msg + "    SimpleMemberPmb cb = new SimpleMemberPmb();" + ln();
            msg = msg + "    pmb.set...;" + ln();
            msg = msg + "    pmb.paging(20, 2); // *Point!" + ln();
            msg = msg + "    ... = memberBhv.outsideSql().manualPaging().selectPage(...);" + ln();
        }
        msg = msg + "    - - - - - - - - - -/" + ln();
        msg = msg + ln();
        msg = msg + "[Paging Bean]" + ln();
        msg = msg + pagingBean + ln();
        msg = msg + "* * * * * * * * * */";
        throw new PagingStatusInvalidException(msg);
    }

    /**
     * Create the builder of result bean.
     * @return The builder of result bean. (NotNull)
     */
    protected ResultBeanBuilder<ENTITY> createResultBeanBuilder() {
        return new ResultBeanBuilder<ENTITY>(_tableDbName);
    }

    /**
     * Is the current page is last page?
     * @param selectedList The selected list. (NotNull)
     * @param pagingBean The bean of paging. (NotNull)
     * @return Determination.
     */
    protected boolean isCurrentLastPage(List<ENTITY> selectedList, PagingBean pagingBean) {
        // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  
        // It returns true if the size of list is NOT under fetch size(page size).
        // 
        // {For example}
        // If the fetch size is 20 and the size of selected list is 19 or less,
        // the current page must be last page(contains when only one page exists). 
        // it is NOT necessary to read count because the 19 is the hint to derive all record count.
        // 
        // If the fetch size is 20 and the size of selected list is 20,
        // it is necessary to read count because we cannot know whether the next pages exist or not.
        // - - - - - - - - - -/
        return selectedList.size() <= (pagingBean.getFetchSize() - 1);
    }

    /**
     * Derive all record count from last page values.
     * @param selectedList The selected list. (NotNull)
     * @param pagingBean The bean of paging. (NotNull)
     * @return Derived all record count.
     */
    protected int deriveAllRecordCountFromLastPageValues(List<ENTITY> selectedList, PagingBean pagingBean) {
        int baseSize = (pagingBean.getFetchPageNumber() - 1) * pagingBean.getFetchSize();
        return baseSize + selectedList.size();
    }

    /**
     * Is it necessary to read page again?
     * @param rb The result bean of paging. (NotNull)
     * @return Determination.
     */
    protected boolean isNecessaryToReadPageAgain(PagingResultBean<ENTITY> rb) {
        return rb.getAllRecordCount() > 0 && rb.getSelectedList().isEmpty();
    }

    /**
     * Check whether the count of all records is safety or not if it needs.
     * @param safetyMaxResultSize The max size of safety result.
     * @param allRecordCount The count of all records.
     * @throws DangerousResultSizeException When the count of all records is dangerous.
     */
    protected void checkSafetyResultIfNeed(int safetyMaxResultSize, int allRecordCount) {
        if (safetyMaxResultSize > 0 && allRecordCount > safetyMaxResultSize) {
            String msg = "You've been in Danger Zone:";
            msg = msg + " safetyMaxResultSize=" + safetyMaxResultSize;
            msg = msg + " allRecordCount=" + allRecordCount;
            throw new DangerousResultSizeException(msg, safetyMaxResultSize, allRecordCount);
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                       Assert Helper
    //                                                                       =============
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
}
