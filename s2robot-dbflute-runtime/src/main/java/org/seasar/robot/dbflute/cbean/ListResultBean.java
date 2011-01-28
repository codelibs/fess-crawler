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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.seasar.robot.dbflute.cbean.grouping.GroupingOption;
import org.seasar.robot.dbflute.cbean.grouping.GroupingRowEndDeterminer;
import org.seasar.robot.dbflute.cbean.grouping.GroupingRowResource;
import org.seasar.robot.dbflute.cbean.grouping.GroupingRowSetupper;
import org.seasar.robot.dbflute.cbean.mapping.EntityDtoMapper;
import org.seasar.robot.dbflute.cbean.sqlclause.orderby.OrderByClause;

/**
 * The result bean for list.
 * @param <ENTITY> The type of entity for the element of selected list.
 * @author jflute
 */
public class ListResultBean<ENTITY> implements List<ENTITY>, Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The value of table db-name. (NullAllowed: If it's null, it means 'Not Selected Yet'.) */
    protected String _tableDbName;

    /** The value of all record count. */
    protected int _allRecordCount;

    /** The list of selected entity. (NotNull) */
    protected List<ENTITY> _selectedList = new ArrayList<ENTITY>();

    /** The clause of order-by. (NotNull) */
    protected OrderByClause _orderByClause = new OrderByClause();

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     */
    public ListResultBean() {
    }

    // ===================================================================================
    //                                                                            Grouping
    //                                                                            ========
    /**
     * Split the list per group. <br />
     * This method needs the property 'selectedList' only.
     * <pre>
     * ex) grouping per three records
     * GroupingOption&lt;Member&gt; groupingOption = new GroupingOption&lt;Member&gt;(<span style="color: #FD4747">3</span>);
     * List&lt;List&lt;Member&gt;&gt; groupingList = memberList.<span style="color: #FD4747">groupingList</span>(new GroupingRowSetupper&lt;List&lt;Member&gt;, Member&gt;() {
     *     public List&lt;Member&gt; setup(GroupingRowResource&lt;Member&gt; groupingRowResource) {
     *         return new ArrayList&lt;Member&gt;(groupingRowResource.getGroupingRowList());
     *     }
     * }, groupingOption);
     * 
     * ex) grouping per initial character of MEMBER_NAME
     * <span style="color: #3F7E5E">// the breakCount is unnecessary in this case</span>
     * GroupingOption&lt;Member&gt; groupingOption = new GroupingOption&lt;Member&gt;();
     * groupingOption.setGroupingRowEndDeterminer(new GroupingRowEndDeterminer&lt;Member&gt;() {
     *     public boolean <span style="color: #FD4747">determine</span>(GroupingRowResource&lt;Member&gt; rowResource, Member nextEntity) {
     *         Member currentEntity = rowResource.getCurrentEntity();
     *         String currentInitChar = currentEntity.getMemberName().substring(0, 1);
     *         String nextInitChar = nextEntity.getMemberName().substring(0, 1);
     *         return !currentInitChar.equalsIgnoreCase(nextInitChar);
     *     }
     * });
     * List&lt;List&lt;Member&gt;&gt; groupingList = memberList.<span style="color: #FD4747">groupingList</span>(new GroupingRowSetupper&lt;List&lt;Member&gt;, Member&gt;() {
     *     public List&lt;Member&gt; setup(GroupingRowResource&lt;Member&gt; groupingRowResource) {
     *         return new ArrayList&lt;Member&gt;(groupingRowResource.getGroupingRowList());
     *     }
     * }, groupingOption);
     * </pre>
     * @param <ROW> The type of row.
     * @param groupingRowSetupper The set-upper of grouping row. (NotNull)
     * @param groupingOption The option of grouping. (NotNull and it requires the breakCount or the determiner)
     * @return The grouped list. (NotNull)
     */
    public <ROW> List<ROW> groupingList(GroupingRowSetupper<ROW, ENTITY> groupingRowSetupper,
            GroupingOption<ENTITY> groupingOption) {
        final List<ROW> groupingList = new ArrayList<ROW>();
        GroupingRowEndDeterminer<ENTITY> rowEndDeterminer = groupingOption.getGroupingRowEndDeterminer();
        if (rowEndDeterminer == null) {
            rowEndDeterminer = new GroupingRowEndDeterminer<ENTITY>() {
                public boolean determine(GroupingRowResource<ENTITY> rowResource, ENTITY nextEntity) {
                    return rowResource.isSizeUpBreakCount();
                }
            }; // as Default
        }
        GroupingRowResource<ENTITY> rowResource = new GroupingRowResource<ENTITY>();
        int breakCount = groupingOption.getElementCount();
        int rowElementIndex = 0;
        int allElementIndex = 0;
        for (ENTITY entity : _selectedList) {
            // Set up row resource.
            rowResource.addGroupingRowList(entity);
            rowResource.setElementCurrentIndex(rowElementIndex);
            rowResource.setBreakCount(breakCount);

            if (_selectedList.size() == (allElementIndex + 1)) { // Last Loop!
                // Set up the object of grouping row!
                final ROW groupingRowObject = groupingRowSetupper.setup(rowResource);

                // Register!
                groupingList.add(groupingRowObject);
                break;
            }

            // Not last loop so the nextElement must exist.
            final ENTITY nextElement = _selectedList.get(allElementIndex + 1);

            // Do at row end.
            if (rowEndDeterminer.determine(rowResource, nextElement)) { // Determine the row end!
                // Set up the object of grouping row!
                final ROW groupingRowObject = groupingRowSetupper.setup(rowResource);

                // Register!
                groupingList.add(groupingRowObject);

                // Initialize!
                rowResource = new GroupingRowResource<ENTITY>();
                rowElementIndex = 0;
                ++allElementIndex;
                continue;
            }
            ++rowElementIndex;
            ++allElementIndex;
        }
        return groupingList;
    }

    // ===================================================================================
    //                                                                             Mapping
    //                                                                             =======
    /**
     * Map the entity list to the list of other object. <br />
     * This method needs the property 'selectedList' only.
     * <pre>
     * ListResultBean&lt;MemberDto&gt; dtoList
     *         = entityList.<span style="color: #FD4747">mappingList</span>(new EntityDtoMapper&lt;Member, MemberDto&gt;() {
     *     public MemberDto map(Member entity) {
     *         MemberDto dto = new MemberDto();
     *         dto.setMemberId(entity.getMemberId());
     *         dto.setMemberName(entity.getMemberName());
     *         ...
     *         return dto;
     *     }
     * });
     * </pre>
     * @param <DTO> The type of DTO.
     * @param entityDtoMapper The map-per for entity and DTO. (NotNull)
     * @return The new mapped list as result bean. (NotNull)
     */
    public <DTO> ListResultBean<DTO> mappingList(EntityDtoMapper<ENTITY, DTO> entityDtoMapper) {
        final ListResultBean<DTO> mappingList = new ListResultBean<DTO>();
        for (ENTITY entity : _selectedList) {
            mappingList.add(entityDtoMapper.map(entity));
        }
        mappingList.setTableDbName(getTableDbName());
        mappingList.setAllRecordCount(getAllRecordCount());
        mappingList.setOrderByClause(getOrderByClause());
        return mappingList;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    /**
     * Has this result selected?
     * @return Determination. {Whether table DB name is not null}
     */
    public boolean isSelectedResult() {
        return _tableDbName != null;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * @return Hash-code from primary-keys.
     */
    public int hashCode() {
        int result = 17;
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
        if (other == null) {
            return false;
        }
        if (!(other instanceof List<?>)) {
            return false;
        }
        if (_selectedList == null) {
            return false; // basically unreachable
        }
        if (other instanceof ListResultBean<?>) {
            return _selectedList.equals(((ListResultBean<?>) other).getSelectedList());
        } else {
            return _selectedList.equals(other);
        }
    }

    /**
     * @return The view string of all attribute values. (NotNull)
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{").append(_tableDbName);
        sb.append(",").append(_allRecordCount);
        sb.append(",").append(_orderByClause != null ? _orderByClause.getOrderByClause() : null);
        sb.append(",").append(_selectedList);
        sb.append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                       List Elements
    //                                                                       =============
    public boolean add(ENTITY o) {
        return _selectedList.add(o);
    }

    public boolean addAll(Collection<? extends ENTITY> c) {
        return _selectedList.addAll(c);
    }

    public void clear() {
        _selectedList.clear();
    }

    public boolean contains(Object o) {
        return _selectedList.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return _selectedList.containsAll(c);
    }

    public boolean isEmpty() {
        return _selectedList.isEmpty();
    }

    public Iterator<ENTITY> iterator() {
        return _selectedList.iterator();
    }

    public boolean remove(Object o) {
        return _selectedList.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return _selectedList.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return _selectedList.retainAll(c);
    }

    public int size() {
        return _selectedList.size();
    }

    public Object[] toArray() {
        return _selectedList.toArray();
    }

    public <TYPE> TYPE[] toArray(TYPE[] a) {
        return _selectedList.toArray(a);
    }

    public void add(int index, ENTITY element) {
        _selectedList.add(index, element);
    }

    public boolean addAll(int index, Collection<? extends ENTITY> c) {
        return _selectedList.addAll(index, c);
    }

    public ENTITY get(int index) {
        return _selectedList.get(index);
    }

    public int indexOf(Object o) {
        return _selectedList.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return _selectedList.lastIndexOf(o);
    }

    public ListIterator<ENTITY> listIterator() {
        return _selectedList.listIterator();
    }

    public ListIterator<ENTITY> listIterator(int index) {
        return _selectedList.listIterator(index);
    }

    public ENTITY remove(int index) {
        return _selectedList.remove(index);
    }

    public ENTITY set(int index, ENTITY element) {
        return _selectedList.set(index, element);
    }

    public List<ENTITY> subList(int fromIndex, int toIndex) {
        return _selectedList.subList(fromIndex, toIndex);
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the value of tableDbName.
     * @return The value of tableDbName. (NullAllowed: If it's null, it means 'Not Selected Yet'.)
     */
    public String getTableDbName() {
        return _tableDbName;
    }

    /**
     * Set the value of tableDbName.
     * @param tableDbName The value of tableDbName. (NotNull)
     */
    public void setTableDbName(String tableDbName) {
        _tableDbName = tableDbName;
    }

    /**
     * Get the value of allRecordCount.
     * @return The value of allRecordCount.
     */
    public int getAllRecordCount() {
        return _allRecordCount;
    }

    /**
     * Set the value of allRecordCount.
     * @param allRecordCount The value of allRecordCount.
     */
    public void setAllRecordCount(int allRecordCount) {
        _allRecordCount = allRecordCount;
    }

    /**
     * Get the value of selectedList.
     * @return Selected list. (NotNull)
     */
    public List<ENTITY> getSelectedList() {
        return _selectedList;
    }

    /**
     * Set the value of selectedList.
     * @param selectedList Selected list. (NotNull: If you set null, it ignores it.)
     */
    public void setSelectedList(List<ENTITY> selectedList) {
        if (selectedList == null) {
            return;
        } // Not allowed to set null value to the selected list
        _selectedList = selectedList;
    }

    /**
     * Get the value of orderByClause.
     * @return The value of orderByClause. (NotNull)
     */
    public OrderByClause getOrderByClause() {
        return _orderByClause;
    }

    /**
     * Set the value of orderByClause.
     * @param orderByClause The value of orderByClause. (NotNull: If you set null, it ignores it.)
     */
    public void setOrderByClause(OrderByClause orderByClause) {
        if (orderByClause == null) {
            return;
        } // Not allowed to set null value to the selected list
        _orderByClause = orderByClause;
    }
}
