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
package org.seasar.robot.dbflute.cbean.sqlclause.orderby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author jflute
 */
public class OrderByClause implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<OrderByElement> _orderByList = new ArrayList<OrderByElement>(3);

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     */
    public OrderByClause() {
    }

    // ===================================================================================
    //                                                                        Manipulation
    //                                                                        ============
    /**
     * Add order-by element.
     * @param orderByElement Order-by element. (NotNull)
     */
    public void addOrderByElement(OrderByElement orderByElement) {
        _orderByList.add(orderByElement);
    }

    /**
     * Insert first order-by element .
     * @param orderByElement Order-by element. (NotNull)
     */
    public void insertFirstOrderByElement(OrderByElement orderByElement) {
        _orderByList.add(0, orderByElement);
    }

    public void reverseAll() {
        for (Iterator<OrderByElement> ite = _orderByList.iterator(); ite.hasNext();) {
            ite.next().reverse();
        }
    }

    public void exchangeFirstOrderByElementForLastOne() {
        if (_orderByList.size() > 1) {
            final OrderByElement first = _orderByList.get(0);
            final OrderByElement last = _orderByList.get(_orderByList.size() - 1);
            _orderByList.set(0, last);
            _orderByList.set(_orderByList.size() - 1, first);
        }
    }

    public void addNullsFirstToPreviousOrderByElement(OrderByNullsSetupper filter) {
        if (_orderByList.isEmpty()) {
            return;
        }
        final OrderByElement last = _orderByList.get(_orderByList.size() - 1);
        last.setOrderByNullsSetupper(filter, true);
    }

    public void addNullsLastToPreviousOrderByElement(OrderByNullsSetupper filter) {
        if (_orderByList.isEmpty()) {
            return;
        }
        final OrderByElement last = _orderByList.get(_orderByList.size() - 1);
        last.setOrderByNullsSetupper(filter, false);
    }

    public static interface OrderByNullsSetupper {
        public String setup(String columnName, String orderByElementClause, boolean nullsFirst);
    }

    public void addManualOrderByElement(ManumalOrderInfo manumalOrderInfo) {
        if (_orderByList.isEmpty()) {
            return;
        }
        final OrderByElement last = _orderByList.get(_orderByList.size() - 1);
        last.setManumalOrderInfo(manumalOrderInfo);
    }

    public static class ManumalOrderInfo {

        /** Serial version UID. (Default) */
        private static final long serialVersionUID = 1L;

        protected List<? extends Object> manualValueList;

        public boolean hasManualValueList() {
            return manualValueList != null && !manualValueList.isEmpty();
        }

        public List<? extends Object> getManualValueList() {
            return manualValueList;
        }

        public void setManualValueList(List<? extends Object> manualValueList) {
            if (manualValueList == null) {
                this.manualValueList = null;
                return;
            }
            List<Object> list = new ArrayList<Object>();
            for (Object value : manualValueList) {
                if (value != null) {
                    list.add(value);
                }
            }
            this.manualValueList = list;
        }
    }

    // ===================================================================================
    //                                                                 Order-By Expression
    //                                                                 ===================
    public List<OrderByElement> getOrderByList() {
        return _orderByList;
    }

    public String getOrderByClause() {
        return getOrderByClause(null);
    }

    public String getOrderByClause(Map<String, String> selectClauseRealColumnAliasMap) {
        if (_orderByList.isEmpty()) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        final String delimiter = ", ";
        for (final Iterator<OrderByElement> ite = _orderByList.iterator(); ite.hasNext();) {
            final OrderByElement element = ite.next();
            sb.append(delimiter);
            if (selectClauseRealColumnAliasMap != null) {
                sb.append(element.getElementClause(selectClauseRealColumnAliasMap));
            } else {
                sb.append(element.getElementClause());
            }
        }
        sb.delete(0, delimiter.length()).insert(0, "order by ");
        return sb.toString();
    }

    public boolean isSameOrderByColumn(String orderByProperty) {
        final List<String> orderByList = new ArrayList<String>();
        {
            final StringTokenizer st = new StringTokenizer(orderByProperty, "/");
            while (st.hasMoreElements()) {
                orderByList.add(st.nextToken());
            }
        }
        if (_orderByList.size() != orderByList.size()) {
            return false;
        }
        int count = 0;
        for (final Iterator<String> ite = orderByList.iterator(); ite.hasNext();) {
            final String columnFullName = ite.next();
            final OrderByElement element = (OrderByElement) _orderByList.get(count);
            if (!element.getColumnFullName().equals(columnFullName)) {
                return false;
            }
            count++;
        }
        return true;
    }

    // ===================================================================================
    //                                                                       First Element
    //                                                                       =============
    public boolean isFirstElementAsc() {
        if (isEmpty()) {
            String msg = "This order-by clause is empty: " + toString();
            throw new IllegalStateException(msg);
        }
        final OrderByElement element = (OrderByElement) _orderByList.get(0);
        return element.isAsc();
    }

    public boolean isFirstElementDesc() {
        return !isFirstElementAsc();
    }

    public boolean isSameAsFirstElementAliasName(String expectedAliasName) {
        if (isEmpty()) {
            String msg = "This order-by clause is empty: " + toString();
            throw new RuntimeException(msg);
        }
        OrderByElement element = (OrderByElement) _orderByList.get(0);
        String actualAliasName = element.getAliasName();
        if (actualAliasName != null && expectedAliasName != null) {
            return actualAliasName.equalsIgnoreCase(expectedAliasName);
        } else {
            return false;
        }
    }

    /**
     * @param expectedColumnName Expected column-name. (NullAllowed)
     * @return Determination.
     */
    public boolean isSameAsFirstElementColumnName(String expectedColumnName) {
        if (isEmpty()) {
            String msg = "This order-by clause is empty: " + toString();
            throw new RuntimeException(msg);
        }
        OrderByElement element = (OrderByElement) _orderByList.get(0);
        String actualColumnName = element.getColumnName();
        if (actualColumnName != null && expectedColumnName != null) {
            return actualColumnName.equalsIgnoreCase(expectedColumnName);
        } else {
            return false;
        }
    }

    // ===================================================================================
    //                                                                    Delegate of List
    //                                                                    ================
    /**
     * Is empty?
     * @return Determination.
     */
    public boolean isEmpty() {
        return _orderByList.isEmpty();
    }

    /**
     * Get iterator of order-by list.
     * @return Determination.
     */
    public Iterator<OrderByElement> iterator() {
        return _orderByList.iterator();
    }

    /**
     * Clear order-by list.
     */
    public void clear() {
        _orderByList.clear();
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * This method overrides the method that is declared at super.
     * @return The view string of all-columns value. (NotNUll)
     */
    @Override
    public String toString() {
        return _orderByList.toString();
    }
}
