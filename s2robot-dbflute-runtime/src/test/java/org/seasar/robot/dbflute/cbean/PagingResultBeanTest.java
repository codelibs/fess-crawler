package org.seasar.robot.dbflute.cbean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.cbean.pagenavi.group.PageGroupBean;
import org.seasar.robot.dbflute.cbean.pagenavi.range.PageRangeBean;
import org.seasar.robot.dbflute.unit.PlainTestCase;
import org.seasar.robot.dbflute.util.DfReflectionUtil;

/**
 * @author jflute
 */
public class PagingResultBeanTest extends PlainTestCase {

    // ===================================================================================
    //                                                                      All Page Count
    //                                                                      ==============
    public void test_getAllPageCount_basic() {
        assertEquals(5, createTarget(4, 3, 20).getAllPageCount());
        assertEquals(5, createTarget(4, 3, 19).getAllPageCount());
        assertEquals(6, createTarget(4, 3, 21).getAllPageCount());
    }

    // ===================================================================================
    //                                                                         Page Number
    //                                                                         ===========
    public void test_getCurrentPageNumber_basic() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 3, 20);

        // ## Act ##
        int currentPageNumber = page.getCurrentPageNumber();

        // ## Assert ##
        assertEquals(3, currentPageNumber);
    }

    public void test_getPrePageNumber_basic() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 3, 20);

        // ## Act ##
        int prePageNumber = page.getPrePageNumber();

        // ## Assert ##
        assertEquals(2, prePageNumber);
    }

    public void test_getPrePageNumber_noExist() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 1, 20);

        // ## Act ##
        try {
            int prePageNumber = page.getPrePageNumber();

            // ## Assert ##
            fail("prePageNumber=" + prePageNumber);
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_getNextPageNumber_basic() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 3, 20);

        // ## Act ##
        int nextPageNumber = page.getNextPageNumber();

        // ## Assert ##
        assertEquals(4, nextPageNumber);
    }

    public void test_getNextPageNumber_noExist() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 5, 20);

        // ## Act ##
        try {
            int nextPageNumber = page.getNextPageNumber();

            // ## Assert ##
            fail("nextPageNumber=" + nextPageNumber);
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public void test_isExistPrePage_basic() {
        assertTrue(createTarget(4, 5, 20).isExistPrePage());
        assertTrue(createTarget(4, 4, 20).isExistPrePage());
        assertTrue(createTarget(4, 3, 20).isExistPrePage());
        assertTrue(createTarget(4, 2, 20).isExistPrePage());
        assertFalse(createTarget(4, 1, 20).isExistPrePage());
    }

    public void test_isExistNextPage_basic() {
        assertTrue(createTarget(4, 1, 20).isExistNextPage());
        assertTrue(createTarget(4, 2, 20).isExistNextPage());
        assertTrue(createTarget(4, 3, 20).isExistNextPage());
        assertTrue(createTarget(4, 4, 20).isExistNextPage());
        assertFalse(createTarget(4, 5, 20).isExistNextPage());
    }

    // ===================================================================================
    //                                                                          Page Group
    //                                                                          ==========
    public void test_pageGroup_createPageNumberList_firstGroup() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 3, 20);
        page.setPageGroupSize(3);

        // ## Act ##
        List<Integer> ls = page.pageGroup().createPageNumberList();

        // ## Assert ##
        assertEquals(3, ls.size());
        assertEquals(1, ls.get(0).intValue());
        assertEquals(2, ls.get(1).intValue());
        assertEquals(3, ls.get(2).intValue());
        assertEquals(3, page.pageGroup().createPageNumberList().size()); // once more call
    }

    public void test_pageGroup_createPageNumberList_lastGroup() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 4, 20);
        page.setPageGroupSize(3);

        // ## Act ##
        List<Integer> ls = page.pageGroup().createPageNumberList();

        // ## Assert ##
        assertEquals(2, ls.size());
        assertEquals(4, ls.get(0).intValue());
        assertEquals(5, ls.get(1).intValue());
        assertEquals(2, page.pageGroup().createPageNumberList().size()); // once more call
    }

    public void test_pageGroup_createPageNumberList_dynamic() {
        // ## Arrange ##
        String fieldName = "_cachedPageNumberList";
        Field cachedField = DfReflectionUtil.getAccessibleField(PageGroupBean.class, fieldName);
        cachedField.setAccessible(true);
        PagingResultBean<String> page = createTarget(4, 3, 20);
        page.setPageGroupSize(3);
        assertNull(DfReflectionUtil.getValue(cachedField, page.pageGroup()));

        // ## Act ##
        List<Integer> ls = page.pageGroup().createPageNumberList();

        // ## Assert ##
        assertEquals(3, ls.size());
        assertEquals(1, ls.get(0).intValue());
        assertEquals(2, ls.get(1).intValue());
        assertEquals(3, ls.get(2).intValue());
        assertEquals(3, page.pageGroup().createPageNumberList().size()); // once more call
        assertNotNull(DfReflectionUtil.getValue(cachedField, page.pageGroup()));

        // ## Act ##
        page.setPageGroupSize(2);
        ls = page.pageGroup().createPageNumberList();

        // ## Assert ##
        assertEquals(2, ls.size());
        assertEquals(3, ls.get(0).intValue());
        assertEquals(4, ls.get(1).intValue());

        // ## Act ##
        page.setCurrentPageNumber(5);
        ls = page.pageGroup().createPageNumberList();

        // ## Assert ##
        assertEquals(1, ls.size());
        assertEquals(5, ls.get(0).intValue());
    }

    public void test_pageGroup_getPreGroupNearestPageNumber_lastGroup() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 4, 20);
        page.setPageGroupSize(3);

        // ## Act ##
        int pageNumber = page.pageGroup().getPreGroupNearestPageNumber();

        // ## Assert ##
        assertEquals(3, pageNumber);
    }

    public void test_pageGroup_getPreGroupNearestPageNumber_noExist() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 3, 20);
        page.setPageGroupSize(3);

        // ## Act ##
        try {
            int pageNumber = page.pageGroup().getPreGroupNearestPageNumber();

            // ## Assert ##
            fail("pageNumber=" + pageNumber);
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_pageGroup_getNextGroupNearestPageNumber_firstGroup() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 3, 20);
        page.setPageGroupSize(3);

        // ## Act ##
        int pageNumber = page.pageGroup().getNextGroupNearestPageNumber();

        // ## Assert ##
        assertEquals(4, pageNumber);
    }

    public void test_pageGroup_getNextGroupNearestPageNumber_noExist() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 4, 20);
        page.setPageGroupSize(3);

        // ## Act ##
        try {
            int pageNumber = page.pageGroup().getNextGroupNearestPageNumber();

            // ## Assert ##
            fail("pageNumber=" + pageNumber);
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                          Page Range
    //                                                                          ==========
    public void test_pageRange_createPageNumberList_nearFirstRange() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 3, 40);
        page.setPageRangeSize(3);

        // ## Act ##
        List<Integer> ls = page.pageRange().createPageNumberList();

        // ## Assert ##
        assertEquals(6, ls.size());
        assertEquals(1, ls.get(0).intValue());
        assertEquals(2, ls.get(1).intValue());
        assertEquals(3, ls.get(2).intValue());
        assertEquals(4, ls.get(3).intValue());
        assertEquals(5, ls.get(4).intValue());
        assertEquals(6, ls.get(5).intValue());
        assertEquals(6, page.pageRange().createPageNumberList().size()); // once more call
    }

    public void test_pageRange_createPageNumberList_nearLastRange() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 8, 40);
        page.setPageRangeSize(3);

        // ## Act ##
        List<Integer> ls = page.pageRange().createPageNumberList();

        // ## Assert ##
        assertEquals(6, ls.size());
        assertEquals(5, ls.get(0).intValue());
        assertEquals(6, ls.get(1).intValue());
        assertEquals(7, ls.get(2).intValue());
        assertEquals(8, ls.get(3).intValue());
        assertEquals(9, ls.get(4).intValue());
        assertEquals(10, ls.get(5).intValue());
        assertEquals(6, page.pageRange().createPageNumberList().size()); // once more call
    }

    public void test_pageRange_createPageNumberList_dynamic() {
        // ## Arrange ##
        String fieldName = "_cachedPageNumberList";
        Field cachedField = DfReflectionUtil.getAccessibleField(PageRangeBean.class, fieldName);
        cachedField.setAccessible(true);
        PagingResultBean<String> page = createTarget(4, 3, 40);
        page.setPageRangeSize(3);
        assertNull(DfReflectionUtil.getValue(cachedField, page.pageRange()));

        // ## Act ##
        List<Integer> ls = page.pageRange().createPageNumberList();

        // ## Assert ##
        assertEquals(6, ls.size());
        assertEquals(1, ls.get(0).intValue());
        assertEquals(2, ls.get(1).intValue());
        assertEquals(3, ls.get(2).intValue());
        assertEquals(4, ls.get(3).intValue());
        assertEquals(5, ls.get(4).intValue());
        assertEquals(6, ls.get(5).intValue());
        assertEquals(6, page.pageRange().createPageNumberList().size()); // once more call
        assertNotNull(DfReflectionUtil.getValue(cachedField, page.pageRange()));

        // ## Act ##
        page.setPageRangeSize(2);
        ls = page.pageRange().createPageNumberList();

        // ## Assert ##
        assertEquals(5, ls.size());
        assertEquals(1, ls.get(0).intValue());
        assertEquals(2, ls.get(1).intValue());
        assertEquals(3, ls.get(2).intValue());
        assertEquals(4, ls.get(3).intValue());
        assertEquals(5, ls.get(4).intValue());

        // ## Act ##
        page.setCurrentPageNumber(8);
        ls = page.pageRange().createPageNumberList();

        // ## Assert ##
        assertEquals(5, ls.size());
        assertEquals(6, ls.get(0).intValue());
        assertEquals(7, ls.get(1).intValue());
        assertEquals(8, ls.get(2).intValue());
        assertEquals(9, ls.get(3).intValue());
        assertEquals(10, ls.get(4).intValue());
    }

    public void test_pageRange_getPreRangeNearestPageNumber_nearLastRange() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 8, 40);
        page.setPageRangeSize(3);

        // ## Act ##
        int pageNumber = page.pageRange().getPreRangeNearestPageNumber();

        // ## Assert ##
        assertEquals(4, pageNumber);
    }

    public void test_pageRange_getPreRangeNearestPageNumber_noExist() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 3, 40);
        page.setPageRangeSize(3);

        // ## Act ##
        try {
            int pageNumber = page.pageRange().getPreRangeNearestPageNumber();

            // ## Assert ##
            fail("pageNumber=" + pageNumber);
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    public void test_pageRange_getNextRangeNearestPageNumber_nearFirstRange() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 3, 40);
        page.setPageRangeSize(3);

        // ## Act ##
        int pageNumber = page.pageRange().getNextRangeNearestPageNumber();

        // ## Assert ##
        assertEquals(7, pageNumber);
    }

    public void test_pageRange_getNextRangeNearestPageNumber_noExist() {
        // ## Arrange ##
        PagingResultBean<String> page = createTarget(4, 8, 40);
        page.setPageRangeSize(3);

        // ## Act ##
        try {
            int pageNumber = page.pageRange().getNextRangeNearestPageNumber();

            // ## Assert ##
            fail("pageNumber=" + pageNumber);
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }

    // ===================================================================================
    //                                                                         Test Helper
    //                                                                         ===========
    protected PagingResultBean<String> createTarget(int pageSize, int currentPageNumber, int allRecordCount) {
        PagingResultBean<String> page = new PagingResultBean<String>();
        page.setPageSize(pageSize);
        page.setCurrentPageNumber(currentPageNumber);
        page.setTableDbName("MEMBER");
        page.setAllRecordCount(allRecordCount);
        List<String> selectedList = new ArrayList<String>();
        for (int i = 0; i < allRecordCount; i++) {
            selectedList.add("No." + i);
        }
        page.setSelectedList(selectedList);
        return page;
    }
}
