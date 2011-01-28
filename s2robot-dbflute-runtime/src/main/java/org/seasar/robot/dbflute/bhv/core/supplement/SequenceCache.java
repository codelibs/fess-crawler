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
package org.seasar.robot.dbflute.bhv.core.supplement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.XLog;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The handler of sequence cache.
 * @author jflute
 * @since 0.9.6.4 (2010/01/15 Friday)
 */
public class SequenceCache {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Log instance for internal debug. (XLog should be used instead for execute-status log) */
    private static final Log _log = LogFactory.getLog(SequenceCacheHandler.class);

    protected static final BigDecimal INITIAL_ADDED_COUNT = BigDecimal.ZERO;
    protected static final BigDecimal DEFAULT_ADD_SIZE = BigDecimal.ONE;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // /- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // The variables that have a large size are BigDecimal instead of BigInteger,
    // because the BigDecimal is more friendly than BigInteger at least for the author.
    // - - - - - - - - - -/
    /** The result type of sequence next value. (NotNull) */
    protected final Class<?> _resultType;

    /** The cache size of sequence that is used by increment way only. (NotNull) */
    protected final BigDecimal _cacheSize;

    /** The increment size of sequence that is used by batch way only. (NullAllowed: If null, it cannot use batch way) */
    protected final Integer _incrementSize;

    /** The added count. If cached list is valid, this value is unused. (NotNull) */
    protected volatile BigDecimal _addedCount = INITIAL_ADDED_COUNT;

    /** The sequence value as base point. (NullAllowed: only at first null) */
    protected volatile BigDecimal _sequenceValue;

    /** The sequence value as first value for batch way only. (NullAllowed: at first or not batch way) */
    protected volatile BigDecimal _batchFirstValue;

    // should be used in a process synchronized
    protected final List<BigDecimal> _cachedList = new ArrayList<BigDecimal>();
    protected final SortedSet<BigDecimal> _tmpSortedSet = new TreeSet<BigDecimal>(new Comparator<BigDecimal>() {
        public int compare(BigDecimal arg0, BigDecimal arg1) {
            return arg0.compareTo(arg1);
        }
    });

    /** Is the batch way valid? */
    protected volatile boolean _batchWay;

    /** Is the internal debug valid? (should be set when immediately after initialization because of no volatile) */
    protected boolean _internalDebug;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param resultType The result type of sequence next value.
     * @param cacheSize The cache size of sequence that is used by increment way only. (NotNull) 
     * @param incrementSize The increment size of sequence that is used by batch way only. (NullAllowed: If null, it cannot use batch way)
     */
    public SequenceCache(Class<?> resultType, BigDecimal cacheSize, Integer incrementSize) {
        _resultType = resultType;
        _cacheSize = cacheSize;
        _incrementSize = incrementSize;
    }

    // ===================================================================================
    //                                                                          Next Value
    //                                                                          ==========
    /**
     * Get a next value of sequence.
     * @param executor The real executor of sequence. (NotNull)
     * @return The next value of sequence as result type. (NotNull)
     */
    public synchronized Object nextval(SequenceRealExecutor executor) {
        if (_batchWay) {
            if (_incrementSize == null) {
                String msg = "The increment size should not be null if it uses batch way!";
                throw new IllegalStateException(msg); // basically unreachable
            }
            if (_incrementSize >= 2) {
                _addedCount = _addedCount.add(getAddSize());
                if (_addedCount.intValue() < _incrementSize) {
                    if (isLogEnabled()) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("...Getting next value from (cached-size) added count:");
                        sb.append(" (").append(_sequenceValue).append(" + ").append(_addedCount).append(":");
                        sb.append(" cache-point=").append(_batchFirstValue).append(")");
                        log(sb.toString());
                    }
                    return toResultType(_sequenceValue.add(_addedCount));
                }
                _addedCount = INITIAL_ADDED_COUNT;
            }
            if (!_cachedList.isEmpty()) {
                _sequenceValue = _cachedList.remove(0);
                if (isLogEnabled()) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("...Getting next value from cached list:");
                    sb.append(" (").append(_sequenceValue).append(":");
                    sb.append(" cache-point=").append(_batchFirstValue).append(")");
                    log(sb.toString());
                }
                return toResultType(_sequenceValue);
            }
        } else { // incrementWay
            _addedCount = _addedCount.add(getAddSize());
            if (_sequenceValue != null && _addedCount.compareTo(_cacheSize) < 0) {
                if (isLogEnabled()) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("...Getting next value from added count:");
                    sb.append(" (").append(_sequenceValue).append(" + ").append(_addedCount).append(")");
                    log(sb.toString());
                }
                return toResultType(_sequenceValue.add(_addedCount));
            }
        }
        if (isLogEnabled()) {
            log("...Selecting next value and cache values: cacheSize=" + _cacheSize);
        }
        setupSequence(executor);
        return toResultType(_sequenceValue);
    }

    protected BigDecimal getAddSize() {
        return DEFAULT_ADD_SIZE;
    }

    protected void setupSequence(SequenceRealExecutor executor) { // should be called in a process synchronized
        initialize();
        if (isInternalDebugEnabled()) {
            _log.debug("...Executing sequence cache: " + executor);
        }
        final Object obj = executor.execute();
        assertSequenceRealExecutorReturnsNotNull(obj, executor);
        if (obj instanceof List<?>) { // batchWay
            final List<?> selectedList = (List<?>) obj; // no guarantee of order
            assertSequenceRealExecutorReturnsNotEmptyList(selectedList, executor);
            for (Object element : selectedList) {
                _tmpSortedSet.add(toInternalType(element)); // order ascend
            }
            _cachedList.addAll(_tmpSortedSet); // setting up cached list (ordered)
            _sequenceValue = _cachedList.remove(0);
            _batchFirstValue = _sequenceValue;
            _batchWay = true;
            if (isInternalDebugEnabled()) {
                final int size = selectedList.size();
                final String exp = selectedList.get(0) + " to " + selectedList.get(size - 1);
                _log.debug("Cached sequence values by batch way: " + exp);
            }
        } else { // incrementWay
            _sequenceValue = toInternalType(obj);
            _batchWay = false;
        }
    }

    protected void initialize() { // should be called in a process synchronized
        _addedCount = INITIAL_ADDED_COUNT;
        _cachedList.clear();
        _tmpSortedSet.clear();
    }

    // -----------------------------------------------------
    //                                                Assert
    //                                                ------
    protected void assertSequenceRealExecutorReturnsNotNull(Object obj, SequenceRealExecutor executor) {
        if (obj == null) {
            String msg = "The sequence real executor should not return null:";
            msg = msg + " executor=" + executor;
            throw new IllegalStateException(msg);
        }
    }

    protected void assertSequenceRealExecutorReturnsNotEmptyList(List<?> selectedList, SequenceRealExecutor executor) {
        if (selectedList.isEmpty()) {
            String msg = "The sequence real executor should not return empty list:";
            msg = msg + " executor=" + executor;
            throw new IllegalStateException(msg);
        }
    }

    // -----------------------------------------------------
    //                                               Convert
    //                                               -------
    protected BigDecimal toInternalType(Object value) {
        return DfTypeUtil.toBigDecimal(value);
    }

    protected Object toResultType(BigDecimal value) {
        return DfTypeUtil.toNumber(value, _resultType);
    }

    // ===================================================================================
    //                                                              Sequence Real Executor
    //                                                              ======================
    public static interface SequenceRealExecutor {
        Object execute();
    }

    // ===================================================================================
    //                                                                  Execute Status Log
    //                                                                  ==================
    protected boolean isLogEnabled() {
        return XLog.isLogEnabled();
    }

    protected void log(String msg) {
        XLog.log(msg);
    }

    // ===================================================================================
    //                                                                      Internal Debug
    //                                                                      ==============
    private boolean isInternalDebugEnabled() { // because log instance is private
        return _internalDebug && _log.isDebugEnabled();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String hash = Integer.toHexString(hashCode());
        return "{" + "type=" + _resultType + ", cache=" + _cacheSize + ", increment=" + _incrementSize + "}@" + hash;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setInternalDebug(boolean internalDebug) {
        _internalDebug = internalDebug;
    }
}
