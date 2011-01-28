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
package org.seasar.robot.dbflute.cbean;

import org.seasar.robot.dbflute.cbean.mapping.EntityDtoMapper;
import org.seasar.robot.dbflute.cbean.pagenavi.group.PageGroupBean;
import org.seasar.robot.dbflute.cbean.pagenavi.group.PageGroupOption;
import org.seasar.robot.dbflute.cbean.pagenavi.range.PageRangeBean;
import org.seasar.robot.dbflute.cbean.pagenavi.range.PageRangeOption;

/**
 * The result bean of paging.
 * @param <ENTITY> The type of entity for the element of selected list.
 * @author jflute
 */
public class PagingResultBean<ENTITY> extends ListResultBean<ENTITY> {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                       Page Basic Info
    //                                       ---------------
    /** The value of page size that means record count in one page. */
    protected int _pageSize;

    /** The value of current page number. */
    protected int _currentPageNumber;

    // -----------------------------------------------------
    //                                            Page Group
    //                                            ----------
    /** The value of page-group bean. */
    protected PageGroupBean _pageGroupBean;

    /** The value of page-group option. */
    protected PageGroupOption _pageGroupOption;

    // -----------------------------------------------------
    //                                            Page Range
    //                                            ----------
    /** The value of page-range bean. */
    protected PageRangeBean _pageRangeBean;

    /** The value of page-range option. */
    protected PageRangeOption _pageRangeOption;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     */
    public PagingResultBean() {
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * Is existing previous page?
     * Using values are currentPageNumber.
     * <pre>
     * ex) range-size=5, current-page=8 
     *  8 / 23 pages (453 records)
     * <span style="color: #FD4747">previous</span> 3 4 5 6 <span style="color: #FD4747">7</span> 8 9 10 11 12 13 next
     * </pre>
     * @return Determination.
     */
    public boolean isExistPrePage() {
        return (_allRecordCount > 0 && _currentPageNumber > 1);
    }

    /**
     * Is existing next page?
     * Using values are currentPageNumber and allPageCount.
     * <pre>
     * ex) range-size=5, current-page=8 
     *  8 / 23 pages (453 records)
     * previous 3 4 5 6 7 8 <span style="color: #FD4747">9</span> 10 11 12 13 <span style="color: #FD4747">next</span>
     * </pre>
     * @return Determination.
     */
    public boolean isExistNextPage() {
        return (_allRecordCount > 0 && _currentPageNumber < getAllPageCount());
    }

    // ===================================================================================
    //                                                                    Page Group/Range
    //                                                                    ================
    protected void initializeCachedBeans() {
        initializePageGroup();
        initializePageRange();
    }

    // -----------------------------------------------------
    //                                            Page Group
    //                                            ----------
    protected void initializePageGroup() {
        _pageGroupBean = null;
    }

    /**
     * Get the value of pageGroupSize.
     * @return The value of pageGroupSize.
     */
    public int getPageGroupSize() {
        return _pageGroupOption != null ? _pageGroupOption.getPageGroupSize() : 0;
    }

    /**
     * Set the value of pageGroupSize. <br />
     * pageGroup() needs this setting before calling. <br />
     * This method is easy-to-use of setPageGroupOption(). (only setting size)
     * <pre>
     * page.<span style="color: #FD4747">setPageGroupSize</span>(10);
     * List&lt;Integer&gt; numberList = page.<span style="color: #FD4747">pageGroup()</span>.createPageNumberList();
     * 
     * <span style="color: #3F7E5E">//  8 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">// previous 1 2 3 4 5 6 7 8 9 10 next</span>
     * </pre>
     * @param pageGroupSize The value of pageGroupSize.
     */
    public void setPageGroupSize(int pageGroupSize) {
        final PageGroupOption option = new PageGroupOption();
        option.setPageGroupSize(pageGroupSize);
        setPageGroupOption(option);
    }

    /**
     * Set the value of pageGroupOption. <br />
     * pageGroup() needs this setting before calling.
     * <pre>
     * PageGroupOption option = new PageGroupOption();
     * option.<span style="color: #FD4747">setPageGroupSize</span>(10);
     * page.<span style="color: #FD4747">setPageGroupOption</span>(option);
     * List&lt;Integer&gt; numberList = page.<span style="color: #FD4747">pageGroup()</span>.createPageNumberList();
     * 
     * <span style="color: #3F7E5E">//  8 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">// previous 1 2 3 4 5 6 7 8 9 10 next</span>
     * </pre>
     * @param pageGroupOption The value of pageGroupOption. (NullAllowed)
     */
    public void setPageGroupOption(PageGroupOption pageGroupOption) {
        initializePageGroup();
        _pageGroupOption = pageGroupOption;
    }

    /**
     * Get the value of pageGroupBean.
     * <pre>
     * ex) group-size=10, current-page=8 
     * page.<span style="color: #FD4747">setPageGroupSize</span>(10);
     * List&lt;Integer&gt; numberList = page.<span style="color: #FD4747">pageGroup()</span>.createPageNumberList();
     * 
     * <span style="color: #3F7E5E">//  8 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">//</span> previous <span style="color: #FD4747">1 2 3 4 5 6 7 8 9 10</span> <span style="color: #3F7E5E">next</span>
     * </pre>
     * @return The value of pageGroupBean. (NotNull)
     */
    public PageGroupBean pageGroup() {
        assertPageGroupValid();
        if (_pageGroupBean == null) {
            _pageGroupBean = new PageGroupBean();
            _pageGroupBean.setPageGroupOption(_pageGroupOption);
            _pageGroupBean.setCurrentPageNumber(getCurrentPageNumber());
            _pageGroupBean.setAllPageCount(getAllPageCount());
        }
        return _pageGroupBean;
    }

    protected void assertPageGroupValid() {
        if (_pageGroupOption == null) {
            String msg = "The pageGroupOption should not be null. Please call setPageGroupOption().";
            throw new IllegalStateException(msg);
        }
        if (_pageGroupOption.getPageGroupSize() == 0) {
            String msg = "The pageGroupSize should be greater than 1. But the value is zero.";
            msg = msg + " pageGroupSize=" + _pageGroupOption.getPageGroupSize();
            throw new IllegalStateException(msg);
        }
        if (_pageGroupOption.getPageGroupSize() == 1) {
            String msg = "The pageGroupSize should be greater than 1. But the value is one.";
            msg = msg + " pageGroupSize=" + _pageGroupOption.getPageGroupSize();
            throw new IllegalStateException(msg);
        }
    }

    // -----------------------------------------------------
    //                                            Page Range
    //                                            ----------
    protected void initializePageRange() {
        _pageRangeBean = null;
    }

    /**
     * Get the value of pageRangeSize.
     * @return The value of pageRangeSize.
     */
    public int getPageRangeSize() {
        return _pageRangeOption != null ? _pageRangeOption.getPageRangeSize() : 0;
    }

    /**
     * Set the value of pageRangeSize. <br />
     * pageRange() needs this setting before calling. <br />
     * This method is easy-to-use of setPageRangeOption(). (only setting size)
     * <pre>
     * ex) range-size=5, current-page=8 
     * page.<span style="color: #FD4747">setPageRangeSize</span>(5);
     * List&lt;Integer&gt; numberList = page.<span style="color: #FD4747">pageRange()</span>.createPageNumberList();
     * 
     * <span style="color: #3F7E5E">//  8 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">// previous</span> <span style="color: #FD4747">3 4 5 6 7 8 9 10 11 12 13</span> <span style="color: #3F7E5E">next</span>
     * </pre>
     * @param pageRangeSize The value of pageRangeSize.
     */
    public void setPageRangeSize(int pageRangeSize) {
        final PageRangeOption option = new PageRangeOption();
        option.setPageRangeSize(pageRangeSize);
        setPageRangeOption(option);
    }

    /**
     * Set the value of pageRangeOption. <br />
     * pageRange() needs this setting before calling. <br />
     * If you want to use fill-limit option, use this instead of setPageRangeSize()
     * <pre>
     * ex) range-size=5, current-page=8 
     * PageRangeOption option = new PageRangeOption();
     * option.<span style="color: #FD4747">setPageRangeSize</span>(5);
     * option.<span style="color: #FD4747">setFillLimit</span>(true);
     * page.<span style="color: #FD4747">setPageRangeOption</span>(option);
     * List&lt;Integer&gt; numberList = page.<span style="color: #FD4747">pageRange()</span>.createPageNumberList();
     * 
     * <span style="color: #3F7E5E">//  8 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">// previous</span> <span style="color: #FD4747">3 4 5 6 7 8 9 10 11 12 13</span> <span style="color: #3F7E5E">next</span>
     * 
     * <span style="color: #3F7E5E">// ex) fillLimit=true, current-page=3</span>
     * <span style="color: #3F7E5E">//  3 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">//</span> <span style="color: #FD4747">1 2 3 4 5 6 7 8 9 10 11</span> <span style="color: #3F7E5E">next</span>
     * </pre>
     * @param pageRangeOption The value of pageRangeOption. (NullAllowed)
     */
    public void setPageRangeOption(PageRangeOption pageRangeOption) {
        initializePageRange();
        _pageRangeOption = pageRangeOption;
    }

    /**
     * Get the value of pageRangeBean.
     * <pre>
     * ex) range-size=5, current-page=8 
     * page.<span style="color: #FD4747">setPageRangeSize</span>(5);
     * List&lt;Integer&gt; numberList = page.<span style="color: #FD4747">pageRange()</span>.createPageNumberList();
     * 
     * <span style="color: #3F7E5E">//  8 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">// previous</span> <span style="color: #FD4747">3 4 5 6 7 8 9 10 11 12 13</span> <span style="color: #3F7E5E">next</span>
     * </pre>
     * @return The value of pageRangeBean. (NotNull)
     */
    public PageRangeBean pageRange() {
        assertPageRangeValid();
        if (_pageRangeBean == null) {
            _pageRangeBean = new PageRangeBean();
            _pageRangeBean.setPageRangeOption(_pageRangeOption);
            _pageRangeBean.setCurrentPageNumber(getCurrentPageNumber());
            _pageRangeBean.setAllPageCount(getAllPageCount());
        }
        return _pageRangeBean;
    }

    protected void assertPageRangeValid() {
        if (_pageRangeOption == null) {
            String msg = "The pageRangeOption should not be null. Please call setPageRangeOption().";
            throw new IllegalStateException(msg);
        }
        final int pageRangeSize = _pageRangeOption.getPageRangeSize();
        if (pageRangeSize == 0) {
            String msg = "The pageRangeSize should be greater than 1. But the value is zero.";
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                 Calculate(Internal)
    //                                                                 ===================
    /**
     * Calculate all page count.
     * @param allRecordCount All record count.
     * @param pageSize Fetch-size.
     * @return All page count.
     */
    protected int calculateAllPageCount(int allRecordCount, int pageSize) {
        if (allRecordCount == 0) {
            return 1;
        }
        int pageCountBase = (allRecordCount / pageSize);
        if (allRecordCount % pageSize > 0) {
            pageCountBase++;
        }
        return pageCountBase;
    }

    protected int calculateCurrentStartRecordNumber(int currentPageNumber, int pageSize) {
        return ((currentPageNumber - 1) * pageSize) + 1;
    }

    protected int calculateCurrentEndRecordNumber(int currentPageNumber, int pageSize) {
        return calculateCurrentStartRecordNumber(currentPageNumber, pageSize) + _selectedList.size() - 1;
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    @Override
    public <DTO> PagingResultBean<DTO> mappingList(EntityDtoMapper<ENTITY, DTO> entityDtoMapper) {
        final ListResultBean<DTO> ls = super.mappingList(entityDtoMapper);
        final PagingResultBean<DTO> mappingList = new PagingResultBean<DTO>();
        mappingList.setSelectedList(ls.getSelectedList());
        mappingList.setTableDbName(getTableDbName());
        mappingList.setAllRecordCount(getAllRecordCount());
        mappingList.setOrderByClause(getOrderByClause());
        mappingList.setPageSize(getPageSize());
        mappingList.setCurrentPageNumber(getCurrentPageNumber());
        mappingList.setPageRangeOption(_pageRangeOption);
        mappingList.setPageGroupOption(_pageGroupOption);
        return mappingList;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * @return Hash-code from primary-keys.
     */
    public int hashCode() {
        int result = 17;
        result = (31 * result) + _pageSize;
        result = (31 * result) + _currentPageNumber;
        if (_selectedList != null) {
            result = (31 * result) + _selectedList.hashCode();
        }
        return result;
    }

    /**
     * @param other Other entity. (NullAllowed)
     * @return Comparing result. If other is null, returns false.
     */
    public boolean equals(Object other) {
        boolean equals = super.equals(other);
        if (!equals) {
            return false;
        }
        if (!(other instanceof PagingResultBean<?>)) {
            return false;
        }
        PagingResultBean<?> otherBean = (PagingResultBean<?>) other;
        if (_pageSize != otherBean.getPageSize()) {
            return false;
        }
        if (_currentPageNumber != otherBean.getCurrentPageNumber()) {
            return false;
        }
        return true;
    }

    /**
     * @return The view string of all attribute values. (NotNull)
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{").append(getCurrentPageNumber()).append("/").append(getAllPageCount());
        sb.append(" of ").append(getAllRecordCount());
        sb.append(" ").append(isExistPrePage()).append("/").append(isExistNextPage());
        if (_pageGroupOption != null) {
            sb.append(" group:{").append(getPageGroupSize()).append(",").append(pageGroup().createPageNumberList())
                    .append("}");
        }
        if (_pageRangeOption != null) {
            sb.append(" range:{").append(getPageRangeSize()).append(",").append(_pageRangeOption.isFillLimit());
            sb.append(",").append(pageRange().createPageNumberList()).append("}");
        }
        sb.append(" list=").append(getSelectedList() != null ? Integer.valueOf(getSelectedList().size()) : null);
        sb.append(" page=").append(getPageSize());
        sb.append("}");
        sb.append(":selectedList=").append(getSelectedList());
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the value of allRecordCount when no paging. <br />
     * This is not same as size() basically.
     * <pre>
     * ex) range-size=5, current-page=8 
     *  8 / 23 pages (<span style="color: #FD4747">453</span> records)
     * previous 3 4 5 6 7 8 9 10 11 12 13 next
     * </pre>
     * @return allRecordCount The value of allRecordCount.
     */
    @Override
    public int getAllRecordCount() { // override for java-doc
        return super.getAllRecordCount();
    }

    /**
     * Set the value of allRecordCount when no paging with initializing cached beans.
     * @param allRecordCount The value of allRecordCount.
     */
    @Override
    public void setAllRecordCount(int allRecordCount) {
        initializeCachedBeans();
        super.setAllRecordCount(allRecordCount);
    }

    /**
     * Get the value of pageSize that means record size in a page.
     * @return The value of pageSize.
     */
    public int getPageSize() {
        return _pageSize;
    }

    /**
     * Set the value of pageSize with initializing cached beans.
     * @param pageSize The value of pageSize.
     */
    public void setPageSize(int pageSize) {
        initializeCachedBeans();
        _pageSize = pageSize;
    }

    /**
     * Get the value of currentPageNumber.
     * <pre>
     * ex) range-size=5, current-page=8 
     *  <span style="color: #FD4747">8</span> / 23 pages (453 records)
     * previous 3 4 5 6 7 8 9 10 11 12 13 next
     * </pre>
     * @return The value of currentPageNumber.
     */
    public int getCurrentPageNumber() {
        return _currentPageNumber;
    }

    /**
     * Set the value of currentPageNumber with initializing cached beans.
     * @param currentPageNumber The value of currentPageNumber.
     */
    public void setCurrentPageNumber(int currentPageNumber) {
        initializeCachedBeans();
        _currentPageNumber = currentPageNumber;
    }

    // -----------------------------------------------------
    //                                   Calculated Property
    //                                   -------------------
    /**
     * Get the value of allPageCount that is calculated.
     * <pre>
     * ex) range-size=5, current-page=8 
     *  8 / <span style="color: #FD4747">23</span> pages (453 records)
     * previous 3 4 5 6 7 8 9 10 11 12 13 next
     * </pre>
     * @return The value of allPageCount.
     */
    public int getAllPageCount() {
        return calculateAllPageCount(_allRecordCount, _pageSize);
    }

    /**
     * Get the value of prePageNumber that is calculated. <br />
     * You should use this.isExistPrePage() before calling this. (call only when true)
     * <pre>
     * ex) range-size=5, current-page=8 
     *  8 / 23 pages (453 records)
     * <span style="color: #FD4747">previous</span> 3 4 5 6 <span style="color: #FD4747">7</span> 8 9 10 11 12 13 next
     * </pre>
     * @return The value of prePageNumber.
     */
    public int getPrePageNumber() {
        if (!isExistPrePage()) {
            String msg = "The previous page should exist when you use prePageNumber:";
            msg = msg + " currentPageNumber=" + _currentPageNumber;
            throw new IllegalStateException(msg);
        }
        return _currentPageNumber - 1;
    }

    /**
     * Get the value of nextPageNumber that is calculated. <br />
     * You should use this.isExistNextPage() before calling this. (call only when true)
     * <pre>
     * ex) range-size=5, current-page=8 
     *  8 / 23 pages (453 records)
     * previous 3 4 5 6 7 8 <span style="color: #FD4747">9</span> 10 11 12 13 <span style="color: #FD4747">next</span>
     * </pre>
     * @return The value of nextPageNumber.
     */
    public int getNextPageNumber() {
        if (!isExistNextPage()) {
            String msg = "The next page should exist when you use nextPageNumber:";
            msg = msg + " currentPageNumber=" + _currentPageNumber;
            throw new IllegalStateException(msg);
        }
        return _currentPageNumber + 1;
    }

    /**
     * Get the value of currentStartRecordNumber that is calculated.
     * @return The value of currentStartRecordNumber.
     */
    public int getCurrentStartRecordNumber() {
        return calculateCurrentStartRecordNumber(_currentPageNumber, _pageSize);
    }

    /**
     * Get the value of currentEndRecordNumber that is calculated.
     * @return The value of currentEndRecordNumber.
     */
    public int getCurrentEndRecordNumber() {
        return calculateCurrentEndRecordNumber(_currentPageNumber, _pageSize);
    }
}
