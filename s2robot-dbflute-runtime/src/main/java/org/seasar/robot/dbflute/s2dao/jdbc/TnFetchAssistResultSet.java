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
package org.seasar.robot.dbflute.s2dao.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.cbean.FetchBean;
import org.seasar.robot.dbflute.cbean.FetchNarrowingBean;
import org.seasar.robot.dbflute.exception.DangerousResultSizeException;
import org.seasar.robot.dbflute.jdbc.PlainResultSetWrapper;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.resource.SQLExceptionHandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnFetchAssistResultSet extends PlainResultSetWrapper {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The real result set. (NotNull) */
    protected final ResultSet _resultSet;

    /** The bean of fetch. (NotNull) */
    protected final FetchBean _fetchBean;

    /** The bean of fetch narrowing. (Nullable) */
    protected final FetchNarrowingBean _fetchNarrowingBean;

    /** Does it offset by cursor forcedly? */
    protected final boolean _offsetByCursorForcedly;

    /** Does it limit by cursor forcedly? */
    protected final boolean _limitByCursorForcedly;

    /** The counter of fetch. */
    protected long _fetchCounter;

    /** the counter of request. */
    protected long _requestCounter;

    /** Does it skip to cursor end? */
    protected boolean _skipToCursorEnd;

    /** Is the database DB2? */
    protected final boolean _db2;
    {
        _db2 = ResourceContext.isCurrentDBDef(DBDef.DB2);
    }

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param resultSet Original result set. (NotNull)
     * @param fetchBean The fetch-bean. (NotNull)
     * @param offsetByCursorForcedly Offset by cursor forcedly.
     * @param limitByCursorForcedly Limit by cursor forcedly.
     */
    public TnFetchAssistResultSet(ResultSet resultSet, FetchBean fetchBean, boolean offsetByCursorForcedly,
            boolean limitByCursorForcedly) {
        super(resultSet);

        _resultSet = resultSet;
        _fetchBean = fetchBean;
        _fetchNarrowingBean = fetchBean instanceof FetchNarrowingBean ? (FetchNarrowingBean) fetchBean : null;
        _offsetByCursorForcedly = offsetByCursorForcedly;
        _limitByCursorForcedly = limitByCursorForcedly;

        skip();
    }

    // ===================================================================================
    //                                                                                Skip
    //                                                                                ====
    /**
     * Skip to the point at start index.
     */
    protected void skip() {
        if (!isAvailableSkipRecord()) {
            return;
        }
        final int skipStartIndex = getFetchNarrowingSkipStartIndex();
        if (isScrollableCursor()) {
            try {
                if (0 == skipStartIndex) {
                    _resultSet.beforeFirst();
                } else {
                    _resultSet.absolute(skipStartIndex);
                }
                _fetchCounter = _resultSet.getRow();
            } catch (SQLException e) {
                handleSQLException(e, null);
            }
        } else {
            try {
                while (true) {
                    if (_fetchCounter >= skipStartIndex) {
                        break;
                    }
                    if (!_resultSet.next()) {
                        _skipToCursorEnd = true; // [DBFLUTE-243]
                        break;
                    }
                    ++_fetchCounter;
                }
            } catch (SQLException e) {
                handleSQLException(e, null);
            }
        }
    }

    protected boolean isAvailableSkipRecord() {
        if (!isFetchNarrowingEffective()) {
            return false;
        }
        if (isOffsetByCursorForcedly()) {
            return true;
        }
        if (isFetchNarrowingSkipStartIndexEffective()) {
            return true;
        }
        return false;
    }

    // ===================================================================================
    //                                                                                Next
    //                                                                                ====
    /**
     * Move to the next record.
     * @return Does the result set have next record?
     * @throws SQLException
     */
    @Override
    public boolean next() throws SQLException {
        if (_db2 && _skipToCursorEnd) { // [DBFLUTE-243]
            return false;
        }
        final boolean hasNext = super.next();
        ++_requestCounter;
        if (!isAvailableLimitLoopCount()) {
            checkSafetyResultIfNeed(hasNext);
            return hasNext;
        }

        final int skipStartIndex = getFetchNarrowingSkipStartIndex();
        final int loopCount = getFetchNarrowingLoopCount();
        if (hasNext && _fetchCounter < skipStartIndex + loopCount) {
            ++_fetchCounter;
            checkSafetyResultIfNeed(true);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isAvailableLimitLoopCount() {
        if (!isFetchNarrowingEffective()) {
            return false;
        }
        if (isLimitByCursorForcedly()) {
            return true;
        }
        if (isFetchNarrowingLoopCountEffective()) {
            return true;
        }
        return false;
    }

    protected void checkSafetyResultIfNeed(boolean hasNext) {
        final int safetyMaxResultSize = getSafetyMaxResultSize();
        if (hasNext && safetyMaxResultSize > 0 && _requestCounter > safetyMaxResultSize) {
            String msg = "You've been in Danger Zone:";
            msg = msg + " safetyMaxResultSize=" + safetyMaxResultSize;
            throw new DangerousResultSizeException(msg, safetyMaxResultSize);
        }
    }

    // ===================================================================================
    //                                                                          Fetch Bean
    //                                                                          ==========
    /**
     * @return The max size of safety result.
     */
    public int getSafetyMaxResultSize() {
        return _fetchBean.getSafetyMaxResultSize();
    }

    // ===================================================================================
    //                                                                Fetch Narrowing Bean
    //                                                                ====================
    /**
     * Is the fetch narrowing effective?
     * @return Determination.
     */
    protected boolean isFetchNarrowingEffective() {
        if (_fetchNarrowingBean == null) {
            return false;
        }
        return _fetchNarrowingBean.isFetchNarrowingEffective();
    }

    /**
     * Is the skip start index of fetch narrowing effective?
     * If isFetchNarrowingEffective() is false, this is not called by anyone.
     * @return Determination.
     */
    protected boolean isFetchNarrowingSkipStartIndexEffective() {
        if (_fetchNarrowingBean == null) {
            String msg = "This method should not be called";
            msg = msg + " when isFetchNarrowingEffective() is false!";
            throw new IllegalStateException(msg);
        }
        return _fetchNarrowingBean.isFetchNarrowingSkipStartIndexEffective();
    }

    /**
     * Is the loop count of fetch narrowing effective?
     * If isFetchNarrowingEffective() is false, this is not called by anyone.
     * @return Determination.
     */
    protected boolean isFetchNarrowingLoopCountEffective() {
        if (_fetchNarrowingBean == null) {
            String msg = "This method should not be called";
            msg = msg + " when isFetchNarrowingEffective() is false!";
            throw new IllegalStateException(msg);
        }
        return _fetchNarrowingBean.isFetchNarrowingLoopCountEffective();
    }

    /**
     * Get the skip start index of fetch narrowing.
     * If isFetchNarrowingEffective() is false, this is not called by anyone.
     * @return The skip start index of fetch narrowing.
     */
    protected int getFetchNarrowingSkipStartIndex() {
        if (_fetchNarrowingBean == null) {
            String msg = "This method should not be called";
            msg = msg + " when isFetchNarrowingEffective() is false!";
            throw new IllegalStateException(msg);
        }
        return _fetchNarrowingBean.getFetchNarrowingSkipStartIndex();
    }

    /**
     * Get the loop count of fetch narrowing.
     * If isFetchNarrowingEffective() is false, this is not called by anyone.
     * @return The loop count of fetch narrowing.
     */
    protected int getFetchNarrowingLoopCount() {
        if (_fetchNarrowingBean == null) {
            String msg = "This method should not be called";
            msg = msg + " when isFetchNarrowingEffective() is false!";
            throw new IllegalStateException(msg);
        }
        return _fetchNarrowingBean.getFetchNarrowingLoopCount();
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected boolean isScrollableCursor() {
        try {
            return !(_resultSet.getType() == ResultSet.TYPE_FORWARD_ONLY);
        } catch (SQLException e) {
            handleSQLException(e, null);
            return false;// unreachable
        }
    }

    protected void handleSQLException(SQLException e, Statement statement) {
        new SQLExceptionHandler().handleSQLException(e, statement);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public boolean isOffsetByCursorForcedly() {
        return _offsetByCursorForcedly;
    }

    public boolean isLimitByCursorForcedly() {
        return _limitByCursorForcedly;
    }

    public boolean isSkipToCursorEnd() {
        return _skipToCursorEnd;
    }
}
