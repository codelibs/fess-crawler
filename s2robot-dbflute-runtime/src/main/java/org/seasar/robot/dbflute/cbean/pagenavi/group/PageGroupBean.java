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
package org.seasar.robot.dbflute.cbean.pagenavi.group;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.robot.dbflute.cbean.pagenavi.PageNumberLink;
import org.seasar.robot.dbflute.cbean.pagenavi.PageNumberLinkSetupper;

/**
 * The bean of page group.
 * @author jflute
 */
public class PageGroupBean implements java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected int _currentPageNumber;
    protected int _allPageCount;
    protected PageGroupOption _pageGroupOption;

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    /**
     * Build the list of page number link.
     * @param <LINK> The type of link.
     * @param pageNumberLinkSetupper Page number link setupper. (NotNull and Required LINK)
     * @return The list of Page number link. (NotNull)
     */
    public <LINK extends PageNumberLink> List<LINK> buildPageNumberLinkList(
            PageNumberLinkSetupper<LINK> pageNumberLinkSetupper) {
        final List<Integer> pageNumberList = createPageNumberList();
        final List<LINK> pageNumberLinkList = new ArrayList<LINK>();
        for (Integer pageNumber : pageNumberList) {
            pageNumberLinkList.add(pageNumberLinkSetupper.setup(pageNumber, pageNumber.equals(_currentPageNumber)));
        }
        return pageNumberLinkList;
    }

    /**
     * Calculate start page number.
     * @return Start page number.
     */
    public int calculateStartPageNumber() {
        assertPageGroupValid();
        final int pageGroupSize = _pageGroupOption.getPageGroupSize();
        final int currentPageNumber = _currentPageNumber;

        int currentPageGroupNumber = (currentPageNumber / pageGroupSize);
        if ((currentPageNumber % pageGroupSize) == 0) {
            currentPageGroupNumber--;
        }
        final int currentPageGroupStartPageNumber = (pageGroupSize * currentPageGroupNumber) + 1;
        if (!(currentPageNumber >= currentPageGroupStartPageNumber)) {
            String msg = "currentPageNumber should be greater equal currentPageGroupStartPageNumber. But:";
            msg = msg + " currentPageNumber=" + currentPageNumber;
            msg = msg + " currentPageGroupStartPageNumber=" + currentPageGroupStartPageNumber;
            throw new IllegalStateException(msg);
        }
        return currentPageGroupStartPageNumber;
    }

    /**
     * Create the list of page number.
     * @return The list of page number. (NotNull)
     */
    public List<Integer> createPageNumberList() {
        assertPageGroupValid();
        final int pageGroupSize = _pageGroupOption.getPageGroupSize();
        final int allPageCount = _allPageCount;
        final int currentPageGroupStartPageNumber = calculateStartPageNumber();
        if (!(currentPageGroupStartPageNumber > 0)) {
            String msg = "currentPageGroupStartPageNumber should be greater than 0. {> 0} But:";
            msg = msg + " currentPageGroupStartPageNumber=" + currentPageGroupStartPageNumber;
            throw new IllegalStateException(msg);
        }
        final int nextPageGroupStartPageNumber = currentPageGroupStartPageNumber + pageGroupSize;

        final List<Integer> resultList = new ArrayList<Integer>();
        for (int i = currentPageGroupStartPageNumber; i < nextPageGroupStartPageNumber && i <= allPageCount; i++) {
            resultList.add(Integer.valueOf(i));
        }
        return resultList;
    }

    /**
     * Create the array of page number.
     * @return The array of page number. (NotNUll)
     */
    public int[] createPageNumberArray() {
        assertPageGroupValid();
        return convertListToIntArray(createPageNumberList());
    }

    /**
     * Is existing previous page-group?
     * Using values are currentPageNumber and pageGroupSize.
     * 
     * @return Determination.
     */
    public boolean isExistPrePageGroup() {
        assertPageGroupValid();
        return (_currentPageNumber > _pageGroupOption.getPageGroupSize());
    }

    /**
     * Is existing next page-group?
     * Using values are currentPageNumber and pageGroupSize and allPageCount.
     * 
     * @return Determination.
     */
    public boolean isExistNextPageGroup() {
        assertPageGroupValid();
        int currentPageGroupStartPageNumber = calculateStartPageNumber();
        if (!(currentPageGroupStartPageNumber > 0)) {
            String msg = "currentPageGroupStartPageNumber should be greater than 0. {> 0} But:";
            msg = msg + " currentPageGroupStartPageNumber=" + currentPageGroupStartPageNumber;
            throw new IllegalStateException(msg);
        }
        int nextPageGroupStartPageNumber = currentPageGroupStartPageNumber + _pageGroupOption.getPageGroupSize();
        return (nextPageGroupStartPageNumber <= _allPageCount);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected int[] convertListToIntArray(List<Integer> ls) {
        final int[] resultArray = new int[ls.size()];
        int arrayIndex = 0;
        for (Iterator<Integer> ite = ls.iterator(); ite.hasNext();) {
            final Integer tmpPageNumber = (Integer) ite.next();
            resultArray[arrayIndex] = tmpPageNumber.intValue();
            arrayIndex++;
        }
        return resultArray;
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

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * @return The view string of all attribute values. (NotNull)
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(" currentPageNumber=").append(_currentPageNumber);
        sb.append(" allPageCount=").append(_allPageCount);
        sb.append(" pageGroupOption=").append(_pageGroupOption);

        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setCurrentPageNumber(int currentPageNumber) {
        this._currentPageNumber = currentPageNumber;
    }

    public void setAllPageCount(int allPageCount) {
        this._allPageCount = allPageCount;
    }

    public void setPageGroupOption(PageGroupOption pageGroupOption) {
        this._pageGroupOption = pageGroupOption;
    }
}
