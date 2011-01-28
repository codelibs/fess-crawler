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
package org.seasar.robot.dbflute.bhv;

import java.util.List;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.cbean.ConditionBean;

/**
 * The interface of behavior-writable.
 * @author jflute
 */
public interface BehaviorWritable extends BehaviorReadable {

    // =====================================================================================
    //                                                                         Entity Update
    //                                                                         =============
    /**
     * Create the entity. <br />
     * An interface dispatch for insert() or varyingInsert().
     * @param entity The instance of corresponding entity. (NotNull)
     * @param option The option of insert. (NullAllowed: if null, same as insert())
     */
    void create(Entity entity, InsertOption<? extends ConditionBean> option);

    /**
     * Modify the entity. <br />
     * An interface dispatch for update() or varyingUpdate().
     * @param entity The instance of corresponding entity. (NotNull)
     * @param option The option of update. (NullAllowed: if null, same as update())
     */
    void modify(Entity entity, UpdateOption<? extends ConditionBean> option);

    /**
     * Modify the entity non-strictly. <br />
     * An interface dispatch for updateNonstrict() or varyingUpdateNonstrict(). <br />
     * However if it's non optimistic lock table, this is same as modify().
     * @param entity The instance of corresponding entity. (NotNull)
     * @param option The option of update. (NullAllowed: if null, same as updateNonstrict())
     */
    void modifyNonstrict(Entity entity, UpdateOption<? extends ConditionBean> option);

    /**
     * Create or modify the entity. <br />
     * An interface dispatch for insertOrUpdate() or varyingInsertOrUpdate().
     * @param entity The instance of corresponding entity. (NotNull)
     * @param insertOption The option of insert. (NullAllowed)
     * @param updateOption The option of update. (NullAllowed)
     */
    void createOrModify(Entity entity, InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption);

    /**
     * Create or modify the entity non-strictly. <br />
     * An interface dispatch for insertOrUpdateNonstrict() or varyingInsertOrUpdateNonstrict(). <br />
     * However if it's non optimistic lock table, this is same as createOrModify().
     * @param entity The instance of corresponding entity. (NotNull)
     * @param insertOption The option of insert. (NullAllowed)
     * @param updateOption The option of update. (NullAllowed)
     */
    void createOrModifyNonstrict(Entity entity, InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption);

    /**
     * Remove the entity. <br />
     * An interface dispatch for delete() or varyingDelete().
     * @param entity The instance of corresponding entity. (NotNull)
     * @param option The option of delete. (NullAllowed: if null, same as delete())
     */
    void remove(Entity entity, DeleteOption<? extends ConditionBean> option);

    /**
     * Remove the entity non-strictly. <br />
     * An interface dispatch for deleteNonstrict() or varyingDeleteNonstrict().
     * @param entity The instance of corresponding entity. (NotNull)
     * @param option The option of delete. (NullAllowed: if null, same as deleteNonstrict())
     */
    void removeNonstrict(Entity entity, DeleteOption<? extends ConditionBean> option);

    // =====================================================================================
    //                                                                          Batch Update
    //                                                                          ============
    /**
     * Lump-create the list. <br />
     * An interface dispatch for batchInsert() or varyingBatchInsert().
     * @param entityList The list of corresponding entity. (NotNull and NotEmpty)
     * @param option The option of insert. (NullAllowed: if null, same as batchInsert())
     * @return The array of created count.
     */
    int[] lumpCreate(List<Entity> entityList, InsertOption<? extends ConditionBean> option);

    /**
     * Lump-modify the list. <br />
     * An interface dispatch for batchUpdate() or varyingBatchUpdate().
     * @param entityList The list of corresponding entity. (NotNull and NotEmpty)
     * @param option The option of update. (NullAllowed: if null, same as batchUpdate())
     * @return Modified count.
     */
    int[] lumpModify(List<Entity> entityList, UpdateOption<? extends ConditionBean> option);

    /**
     * Lump-modify the list non-strictly. <br />
     * An interface dispatch for batchUpdateNonstrict() or varyingBatchUpdateNonstrict().
     * @param entityList The list of corresponding entity. (NotNull and NotEmpty)
     * @param option The option of update. (NullAllowed: if null, same as batchUpdateNonstrict())
     * @return Modified count.
     */
    int[] lumpModifyNonstrict(List<Entity> entityList, UpdateOption<? extends ConditionBean> option);

    /**
     * Lump-remove the list. <br />
     * An interface dispatch for batchDelete() or varyingBatchDelete().
     * @param entityList The list of entity. (NotNull and NotEmpty)
     * @param option The option of delete. (NullAllowed: if null, same as batchDelete())
     * @return Removed count.
     */
    int[] lumpRemove(List<Entity> entityList, DeleteOption<? extends ConditionBean> option);

    /**
     * Lump-remove the list non-strictly. <br />
     * An interface dispatch for batchDeleteNonstrict() or varyingBatchDeleteNonstrict().
     * @param entityList The list of corresponding entity. (NotNull and NotEmpty)
     * @param option The option of delete. (NullAllowed: if null, same as batchDeleteNonstrict())
     * @return Removed count.
     */
    int[] lumpRemoveNonstrict(List<Entity> entityList, DeleteOption<? extends ConditionBean> option);

    // =====================================================================================
    //                                                                          Query Update
    //                                                                          ============
    /**
     * Range-create entities. <br />
     * An interface dispatch for queryInsert() or varyingQueryInsert().
     * @param setupper The set-upper for query-insert. (NotNull)
     * @param option The option of update. (NullAllowed: if null, same as queryUpdate())
     * @return Modified count.
     */
    int rangeCreate(QueryInsertSetupper<? extends Entity, ? extends ConditionBean> setupper,
            InsertOption<? extends ConditionBean> option);

    /**
     * Range-modify entities. <br />
     * An interface dispatch for queryUpdate() or varyingQueryUpdate().
     * @param entity The instance of corresponding entity. (NotNull)
     * @param cb The corresponding condition-bean for query. (NotNull)
     * @param option The option of update. (NullAllowed: if null, same as queryUpdate())
     * @return Modified count.
     */
    int rangeModify(Entity entity, ConditionBean cb, UpdateOption<? extends ConditionBean> option);

    /**
     * Range-modify entities. <br />
     * An interface dispatch for queryDelete() or varyingQueryDelete().
     * @param cb The corresponding condition-bean for query. (NotNull)
     * @param option The option of delete. (NullAllowed: if null, same as queryDelete())
     * @return Removed count.
     */
    int rangeRemove(ConditionBean cb, DeleteOption<? extends ConditionBean> option);
}
