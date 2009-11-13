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
    /** The value of current page number. */
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
    //                                                                 Calculated Property
    //                                                                 ===================
    /**
     * Get the value of allPageCount that is calculated.
     * @return The value of allPageCount.
     */
    public int getAllPageCount() {
        return calculateAllPageCount(_allRecordCount, _pageSize);
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

    // ===================================================================================
    //                                                                    Page Group/Range
    //                                                                    ================
    // -----------------------------------------------------
    //                                            Page Group
    //                                            ----------
    /**
     * Get the value of pageGroupSize.
     * @return The value of pageGroupSize.
     */
    public int getPageGroupSize() {
        return _pageGroupOption != null ? _pageGroupOption.getPageGroupSize() : 0;
    }

    /**
     * Set the value of pageGroupSize.
     * @param pageGroupSize The value of pageGroupSize.
     */
    public void setPageGroupSize(int pageGroupSize) {
        final PageGroupOption option = new PageGroupOption();
        option.setPageGroupSize(pageGroupSize);
        setPageGroupOption(option);
    }

    /**
     * Set the value of pageGroupOption.
     * @param pageGroupOption The value of pageGroupOption. (Nullable)
     */
    public void setPageGroupOption(PageGroupOption pageGroupOption) {
        _pageGroupOption = pageGroupOption;
    }

    /**
     * Get the value of pageGroupBean.
     * @return The value of pageGroupBean. (NotNull)
     */
    public PageGroupBean pageGroup() {
        assertPageGroupValid();
        if (_pageGroupBean == null) {
            _pageGroupBean = new PageGroupBean();
        }
        _pageGroupBean.setPageGroupOption(_pageGroupOption);
        _pageGroupBean.setCurrentPageNumber(getCurrentPageNumber());
        _pageGroupBean.setAllPageCount(getAllPageCount());
        return _pageGroupBean;
    }

    protected void assertPageGroupValid() {
        if (_pageGroupOption == null) {
            String msg = "The pageGroupOption should not be null. Please invoke setPageGroupOption().";
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
    /**
     * Get the value of pageRangeSize.
     * @return The value of pageRangeSize.
     */
    public int getPageRangeSize() {
        return _pageRangeOption != null ? _pageRangeOption.getPageRangeSize() : 0;
    }

    /**
     * Set the value of pageRangeSize.
     * @param pageRangeSize The value of pageRangeSize.
     */
    public void setPageRangeSize(int pageRangeSize) {
        final PageRangeOption option = new PageRangeOption();
        option.setPageRangeSize(pageRangeSize);
        setPageRangeOption(option);
    }

    /**
     * Set the value of pageRangeOption.
     * @param pageRangeOption The value of pageRangeOption. (Nullable)
     */
    public void setPageRangeOption(PageRangeOption pageRangeOption) {
        this._pageRangeOption = pageRangeOption;
    }

    /**
     * Get the value of pageRangeBean.
     * @return The value of pageRangeBean. (NotNull)
     */
    public PageRangeBean pageRange() {
        assertPageRangeValid();
        if (_pageRangeBean == null) {
            _pageRangeBean = new PageRangeBean();
        }
        _pageRangeBean.setPageRangeOption(_pageRangeOption);
        _pageRangeBean.setCurrentPageNumber(getCurrentPageNumber());
        _pageRangeBean.setAllPageCount(getAllPageCount());
        return _pageRangeBean;
    }

    protected void assertPageRangeValid() {
        if (_pageRangeOption == null) {
            String msg = "The pageRangeOption should not be null. Please invoke setPageRangeOption().";
            throw new IllegalStateException(msg);
        }
        final int pageRangeSize = _pageRangeOption.getPageRangeSize();
        if (pageRangeSize == 0) {
            String msg = "The pageRangeSize should be greater than 1. But the value is zero.";
            throw new IllegalStateException(msg);
        }
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * Is existing previous page?
     * Using values are currentPageNumber.
     * @return Determination.
     */
    public boolean isExistPrePage() {
        return (_allRecordCount > 0 && _currentPageNumber > 1);
    }

    /**
     * Is existing next page?
     * Using values are currentPageNumber and allPageCount.
     * @return Determination.
     */
    public boolean isExistNextPage() {
        return (_allRecordCount > 0 && _currentPageNumber < getAllPageCount());
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
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
     * @param other Other entity. (Nullable)
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
     * Get the value of pageSize.
     * @return The value of pageSize.
     */
    public int getPageSize() {
        return _pageSize;
    }

    /**
     * Set the value of pageSize.
     * @param pageSize The value of pageSize.
     */
    public void setPageSize(int pageSize) {
        _pageSize = pageSize;
    }

    /**
     * Get the value of currentPageNumber.
     * @return The value of currentPageNumber.
     */
    public int getCurrentPageNumber() {
        return _currentPageNumber;
    }

    /**
     * Set the value of currentPageNumber.
     * @param currentPageNumber The value of currentPageNumber.
     */
    public void setCurrentPageNumber(int currentPageNumber) {
        _currentPageNumber = currentPageNumber;
    }
}
