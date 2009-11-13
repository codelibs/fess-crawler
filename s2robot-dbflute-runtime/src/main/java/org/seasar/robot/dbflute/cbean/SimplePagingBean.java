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

import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.robot.dbflute.cbean.sqlclause.OrderByClause;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClause;
import org.seasar.robot.dbflute.cbean.sqlclause.SqlClauseDefault;
import org.seasar.robot.dbflute.exception.PagingPageSizeNotPlusException;
import org.seasar.robot.dbflute.twowaysql.pmbean.MapParameterBean;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * The simple paging-bean.
 * @author jflute
 */
public class SimplePagingBean implements PagingBean, MapParameterBean {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** SQL clause instance. */
    protected final SqlClause _sqlClause;
    {
        _sqlClause = new SqlClauseDefault("Dummy");
    }

    /** The map of parameter. (Nullable) */
    protected Map<String, Object> _parameterMap;

    /** The max size of safety result. */
    protected int _safetyMaxResultSize;

    /** Is the execution for paging(NOT count)? */
    protected boolean _paging = true;

    /** Is the count executed later? */
    protected boolean _countLater;

    /** Can the paging re-select? */
    protected boolean _canPagingReSelect = true;

    /** Is fetch narrowing valid? */
    protected boolean _fetchNarrowing = true;

    /** The map for parameter. */
    protected Map<String, Object> _map;

    // ===================================================================================
    //                                                        Implementation of PagingBean
    //                                                        ============================
    // -----------------------------------------------------
    //                                  Paging Determination
    //                                  --------------------
    /**
     * {@inheritDoc}
     */
    public boolean isPaging() { // for parameter comment
        return _paging;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isCountLater() { // for framework
        return _countLater;
    }

    // -----------------------------------------------------
    //                                        Paging Setting
    //                                        --------------
    /**
     * {@inheritDoc}
     */
    public void paging(int pageSize, int pageNumber) {
        if (pageSize <= 0) {
            throwPagingPageSizeNotPlusException(pageSize, pageNumber);
        }
        fetchFirst(pageSize);
        fetchPage(pageNumber);
    }

    protected void throwPagingPageSizeNotPlusException(int pageSize, int pageNumber) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "Page size for paging should not be minus or zero!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Confirm the value of your parameter 'pageSize'." + ln();
        msg = msg + "The first parameter of paging() should be a plus value!" + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    (x) - pmb.paging(0, 1);" + ln();
        msg = msg + "    (x) - pmb.paging(-3, 2);" + ln();
        msg = msg + "    (o) - pmb.paging(4, 3);" + ln();
        msg = msg + ln();
        msg = msg + "[Page Size]" + ln();
        msg = msg + pageSize + ln();
        msg = msg + ln();
        msg = msg + "[Page Number]" + ln();
        msg = msg + pageNumber + ln();
        msg = msg + "* * * * * * * * * */";
        throw new PagingPageSizeNotPlusException(msg);
    }

    /**
     * {@inheritDoc}
     */
    public void xsetPaging(boolean paging) {
        if (paging) {
            getSqlClause().makeFetchScopeEffective();
        } else {
            getSqlClause().ignoreFetchScope();
        }
        this._paging = paging;
    }

    /**
     * {@inheritDoc}
     */
    public void disablePagingReSelect() {
        _canPagingReSelect = false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canPagingReSelect() {
        return _canPagingReSelect;
    }

    // -----------------------------------------------------
    //                                         Fetch Setting
    //                                         -------------
    /**
     * {@inheritDoc}
     */
    public PagingBean fetchFirst(int fetchSize) {
        getSqlClause().fetchFirst(fetchSize);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public PagingBean fetchScope(int fetchStartIndex, int fetchSize) {
        getSqlClause().fetchScope(fetchStartIndex, fetchSize);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public PagingBean fetchPage(int fetchPageNumber) {
        getSqlClause().fetchPage(fetchPageNumber);
        return this;
    }

    // -----------------------------------------------------
    //                                        Fetch Property
    //                                        --------------
    /**
     * {@inheritDoc}
     */
    public int getFetchStartIndex() {
        return getSqlClause().getFetchStartIndex();
    }

    /**
     * {@inheritDoc}
     */
    public int getFetchSize() {
        return getSqlClause().getFetchSize();
    }

    /**
     * {@inheritDoc}
     */
    public int getFetchPageNumber() {
        return getSqlClause().getFetchPageNumber();
    }

    /**
     * {@inheritDoc}
     */
    public int getPageStartIndex() {
        return getSqlClause().getPageStartIndex();
    }

    /**
     * {@inheritDoc}
     */
    public int getPageEndIndex() {
        return getSqlClause().getPageEndIndex();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFetchScopeEffective() {
        return getSqlClause().isFetchScopeEffective();
    }

    // ===================================================================================
    //                                                        Implementation of SelectBean
    //                                                        ============================
    /**
     * {@inheritDoc}
     */
    public int getSafetyMaxResultSize() {
        return _safetyMaxResultSize;
    }

    // ===================================================================================
    //                                                Implementation of FetchNarrowingBean
    //                                                ====================================
    /**
     * {@inheritDoc}
     */
    public int getFetchNarrowingSkipStartIndex() {
        return getSqlClause().getFetchNarrowingSkipStartIndex();
    }

    /**
     * {@inheritDoc}
     */
    public int getFetchNarrowingLoopCount() {
        return getSqlClause().getFetchNarrowingLoopCount();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFetchNarrowingSkipStartIndexEffective() {
        return !getSqlClause().isFetchStartIndexSupported();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFetchNarrowingLoopCountEffective() {
        return !getSqlClause().isFetchSizeSupported();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFetchNarrowingEffective() {
        return _fetchNarrowing && getSqlClause().isFetchNarrowingEffective();
    }

    /**
     * {@inheritDoc}
     */
    public void ignoreFetchNarrowing() {
        _fetchNarrowing = false;
    }

    /**
     * {@inheritDoc}
     */
    public void restoreIgnoredFetchNarrowing() {
        _fetchNarrowing = true;
    }

    // ===================================================================================
    //                                                       Implementation of OrderByBean
    //                                                       =============================
    /**
     * {@inheritDoc}
     */
    public OrderByClause getSqlComponentOfOrderByClause() {
        return getSqlClause().getSqlComponentOfOrderByClause();
    }

    /**
     * {@inheritDoc}
     */
    public String getOrderByClause() {
        return getSqlClause().getOrderByClause();
    }

    /**
     * {@inheritDoc}
     */
    public OrderByBean clearOrderBy() {
        getSqlClause().clearOrderBy();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public OrderByBean ignoreOrderBy() {
        getSqlClause().ignoreOrderBy();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public OrderByBean makeOrderByEffective() {
        getSqlClause().makeOrderByEffective();
        return this;
    }

    // ===================================================================================
    //                                                    Implementation of SelectResource
    //                                                    ================================
    /**
     * {@inheritDoc}
     */
    public void checkSafetyResult(int safetyMaxResultSize) {
        this._safetyMaxResultSize = safetyMaxResultSize;
    }

    // ===================================================================================
    //                                                  Implementation of MapParameterBean
    //                                                  ==================================
    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getParameterMap() {
        return _parameterMap;
    }

    /**
     * Add the parameter to the map.
     * @param key The key of parameter. (NotNull)
     * @param value The value of parameter. (Nullable)
     */
    public void addParameter(String key, Object value) {
        if (_parameterMap == null) {
            _parameterMap = new LinkedHashMap<String, Object>();
        }
        _parameterMap.put(key, value);
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                             SqlClause
    //                                             ---------
    /**
     * Get sqlClause.
     * @return SqlClause. (NotNull)
     */
    protected SqlClause getSqlClause() {
        return _sqlClause;
    }
}
