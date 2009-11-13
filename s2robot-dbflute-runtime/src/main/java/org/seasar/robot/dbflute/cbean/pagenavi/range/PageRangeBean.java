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
package org.seasar.robot.dbflute.cbean.pagenavi.range;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.robot.dbflute.cbean.pagenavi.PageNumberLink;
import org.seasar.robot.dbflute.cbean.pagenavi.PageNumberLinkSetupper;

/**
 * The bean of page range.
 * @author jflute
 */
public class PageRangeBean implements java.io.Serializable {

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
    protected PageRangeOption _pageRangeOption;

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
     * Create the list of page number.
     * @return The list of page number. (NotNull)
     */
    public List<Integer> createPageNumberList() {
        assertPageRangeValid();
        final int pageRangeSize = _pageRangeOption.getPageRangeSize();
        final int allPageCount = _allPageCount;
        final int currentPageNumber = _currentPageNumber;

        final List<Integer> resultList = new ArrayList<Integer>();
        for (int i = currentPageNumber - pageRangeSize; i < currentPageNumber; i++) {
            if (i < 1) {
                continue;
            }
            resultList.add(Integer.valueOf(i));
        }

        resultList.add(Integer.valueOf(currentPageNumber));

        final int endPageNumber = (currentPageNumber + pageRangeSize);
        for (int i = currentPageNumber + 1; i <= endPageNumber && i <= allPageCount; i++) {
            resultList.add(Integer.valueOf(i));
        }

        final boolean fillLimit = _pageRangeOption.isFillLimit();
        final int limitSize = (pageRangeSize * 2) + 1;
        if (fillLimit && !resultList.isEmpty() && resultList.size() < limitSize) {
            final Integer firstElements = (Integer) resultList.get(0);
            final Integer lastElements = (Integer) resultList.get(resultList.size() - 1);
            if (firstElements.intValue() > 1) {
                for (int i = firstElements.intValue() - 1; resultList.size() < limitSize && i > 0; i--) {
                    resultList.add(0, Integer.valueOf(i));
                }
            }
            for (int i = lastElements.intValue() + 1; resultList.size() < limitSize && i <= allPageCount; i++) {
                resultList.add(Integer.valueOf(i));
            }
        }
        return resultList;
    }

    /**
     * Get the array of page number.
     * @return The array of page number. (NotNull)
     */
    public int[] createPageNumberArray() {
        assertPageRangeValid();
        return convertListToIntArray(createPageNumberList());
    }

    /**
     * Is existing previous page range?
     * @return Determination.
     */
    public boolean isExistPrePageRange() {
        assertPageRangeValid();
        final int[] array = createPageNumberArray();
        if (array.length == 0) {
            return false;
        }
        return array[0] > 1;
    }

    /**
     * Is existing next page range?
     * @return Determination.
     */
    public boolean isExistNextPageRange() {
        assertPageRangeValid();
        final int[] array = createPageNumberArray();
        if (array.length == 0) {
            return false;
        }
        return array[array.length - 1] < _allPageCount;
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
        sb.append(" pageRangeOption=").append(_pageRangeOption);

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

    public void setPageRangeOption(PageRangeOption pageRangeOption) {
        this._pageRangeOption = pageRangeOption;
    }
}
