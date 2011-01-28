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

import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.bhv.core.CommonColumnAutoSetupper;
import org.seasar.robot.dbflute.bhv.core.command.AbstractListEntityCommand;
import org.seasar.robot.dbflute.bhv.core.command.BatchDeleteCommand;
import org.seasar.robot.dbflute.bhv.core.command.BatchDeleteNonstrictCommand;
import org.seasar.robot.dbflute.bhv.core.command.BatchInsertCommand;
import org.seasar.robot.dbflute.bhv.core.command.BatchUpdateCommand;
import org.seasar.robot.dbflute.bhv.core.command.BatchUpdateNonstrictCommand;
import org.seasar.robot.dbflute.bhv.core.command.DeleteEntityCommand;
import org.seasar.robot.dbflute.bhv.core.command.DeleteNonstrictEntityCommand;
import org.seasar.robot.dbflute.bhv.core.command.QueryDeleteCBCommand;
import org.seasar.robot.dbflute.bhv.core.command.QueryInsertCBCommand;
import org.seasar.robot.dbflute.bhv.core.command.QueryUpdateCBCommand;
import org.seasar.robot.dbflute.bhv.core.command.UpdateEntityCommand;
import org.seasar.robot.dbflute.bhv.core.command.UpdateNonstrictEntityCommand;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.SpecifyQuery;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.robot.dbflute.exception.EntityAlreadyUpdatedException;
import org.seasar.robot.dbflute.exception.IllegalBehaviorStateException;
import org.seasar.robot.dbflute.exception.IllegalConditionBeanOperationException;
import org.seasar.robot.dbflute.exception.OptimisticLockColumnValueNullException;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.resource.ResourceContext;

/**
 * The abstract class of writable behavior.
 * @author jflute
 */
public abstract class AbstractBehaviorWritable extends AbstractBehaviorReadable implements BehaviorWritable {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The auto-set-upper of common column. (NotNull) */
    protected CommonColumnAutoSetupper _commonColumnAutoSetupper;

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    // -----------------------------------------------------
    //                                                Create
    //                                                ------
    /**
     * {@inheritDoc}
     */
    public void create(Entity entity, InsertOption<? extends ConditionBean> option) {
        doCreate(entity, option);
    }

    protected abstract void doCreate(Entity entity, InsertOption<? extends ConditionBean> option);

    // -----------------------------------------------------
    //                                                Modify
    //                                                ------
    /**
     * {@inheritDoc}
     */
    public void modify(Entity entity, UpdateOption<? extends ConditionBean> option) {
        doModify(entity, option);
    }

    protected abstract void doModify(Entity entity, UpdateOption<? extends ConditionBean> option);

    /**
     * {@inheritDoc}
     */
    public void modifyNonstrict(Entity entity, UpdateOption<? extends ConditionBean> option) {
        doModifyNonstrict(entity, option);
    }

    protected abstract void doModifyNonstrict(Entity entity, UpdateOption<? extends ConditionBean> option);

    /**
     * {@inheritDoc}
     */
    public void createOrModify(Entity entity, InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption) {
        doCreateOrModify(entity, insertOption, updateOption);
    }

    protected abstract void doCreateOrModify(Entity entity, InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption);

    /**
     * {@inheritDoc}
     */
    public void createOrModifyNonstrict(Entity entity, InsertOption<? extends ConditionBean> insertOption,
            UpdateOption<? extends ConditionBean> updateOption) {
        doCreateOrModifyNonstrict(entity, insertOption, updateOption);
    }

    protected abstract void doCreateOrModifyNonstrict(Entity entity,
            InsertOption<? extends ConditionBean> insertOption, UpdateOption<? extends ConditionBean> updateOption);

    // -----------------------------------------------------
    //                                                Remove
    //                                                ------
    /**
     * {@inheritDoc}
     */
    public void remove(Entity entity, DeleteOption<? extends ConditionBean> option) {
        doRemove(entity, option);
    }

    protected abstract void doRemove(Entity entity, DeleteOption<? extends ConditionBean> option);

    /**
     * {@inheritDoc}
     */
    public void removeNonstrict(Entity entity, DeleteOption<? extends ConditionBean> option) {
        doRemoveNonstrict(entity, option);
    }

    protected abstract void doRemoveNonstrict(Entity entity, DeleteOption<? extends ConditionBean> option);

    // ===================================================================================
    //                                                       Entity Update Internal Helper
    //                                                       =============================
    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected <ENTITY extends Entity> void helpUpdateInternally(ENTITY entity, InternalUpdateCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        assertEntityHasOptimisticLockValue(entity);
        final int updatedCount = callback.callbackDelegateUpdate(entity);
        if (updatedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (updatedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, updatedCount);
        }
    }

    protected static interface InternalUpdateCallback<ENTITY extends Entity> {
        public int callbackDelegateUpdate(ENTITY entity);
    }

    protected <ENTITY extends Entity> void helpUpdateNonstrictInternally(ENTITY entity,
            InternalUpdateNonstrictCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        final int updatedCount = callback.callbackDelegateUpdateNonstrict(entity);
        if (updatedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (updatedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, updatedCount);
        }
    }

    protected static interface InternalUpdateNonstrictCallback<ENTITY extends Entity> {
        public int callbackDelegateUpdateNonstrict(ENTITY entity);
    }

    protected <ENTITY extends Entity> void throwUpdateEntityAlreadyDeletedException(ENTITY entity) {
        createBhvExThrower().throwUpdateEntityAlreadyDeletedException(entity);
    }

    protected <ENTITY extends Entity> void throwUpdateEntityDuplicatedException(ENTITY entity, int count) {
        createBhvExThrower().throwUpdateEntityDuplicatedException(entity, count);
    }

    // -----------------------------------------------------
    //                                        InsertOrUpdate
    //                                        --------------
    protected <ENTITY extends Entity, CB_TYPE extends ConditionBean> void helpInsertOrUpdateInternally(ENTITY entity,
            InternalInsertOrUpdateCallback<ENTITY, CB_TYPE> callback) {
        assertEntityNotNull(entity);
        if (!entity.hasPrimaryKeyValue()) {
            callback.callbackInsert(entity);
        } else {
            RuntimeException updateException = null;
            try {
                callback.callbackUpdate(entity);
            } catch (EntityAlreadyUpdatedException e) { // already updated (or means not found)
                updateException = e;
            } catch (EntityAlreadyDeletedException e) { // means not found
                updateException = e;
            } catch (OptimisticLockColumnValueNullException e) { // means insert?
                updateException = e;
            }
            if (updateException != null) {
                final CB_TYPE cb = callback.callbackNewMyConditionBean();
                cb.acceptPrimaryKeyMap(getDBMeta().extractPrimaryKeyMap(entity));
                if (callback.callbackSelectCount(cb) == 0) { // anyway if not found, insert
                    callback.callbackInsert(entity);
                } else {
                    throw updateException;
                }
            }
        }
    }

    protected static interface InternalInsertOrUpdateCallback<ENTITY extends Entity, CB_TYPE extends ConditionBean> {
        public void callbackInsert(ENTITY entity);

        public void callbackUpdate(ENTITY entity);

        public CB_TYPE callbackNewMyConditionBean();

        public int callbackSelectCount(CB_TYPE cb);
    }

    protected <ENTITY extends Entity> void helpInsertOrUpdateInternally(ENTITY entity,
            InternalInsertOrUpdateNonstrictCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        if (!entity.hasPrimaryKeyValue()) {
            callback.callbackInsert(entity);
        } else {
            try {
                callback.callbackUpdateNonstrict(entity);
            } catch (EntityAlreadyDeletedException ignored) { // means not found
                callback.callbackInsert(entity);
            }
        }
    }

    protected static interface InternalInsertOrUpdateNonstrictCallback<ENTITY extends Entity> {
        public void callbackInsert(ENTITY entity);

        public void callbackUpdateNonstrict(ENTITY entity);
    }

    // -----------------------------------------------------
    //                                                Delete
    //                                                ------
    protected <ENTITY extends Entity> void helpDeleteInternally(ENTITY entity, InternalDeleteCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        assertEntityHasOptimisticLockValue(entity);
        final int deletedCount = callback.callbackDelegateDelete(entity);
        if (deletedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (deletedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, deletedCount);
        }
    }

    protected static interface InternalDeleteCallback<ENTITY extends Entity> {
        public int callbackDelegateDelete(ENTITY entity);
    }

    protected <ENTITY extends Entity> void helpDeleteNonstrictInternally(ENTITY entity,
            InternalDeleteNonstrictCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        final int deletedCount = callback.callbackDelegateDeleteNonstrict(entity);
        if (deletedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (deletedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, deletedCount);
        }
    }

    protected static interface InternalDeleteNonstrictCallback<ENTITY extends Entity> {
        public int callbackDelegateDeleteNonstrict(ENTITY entity);
    }

    protected <ENTITY extends Entity> void helpDeleteNonstrictIgnoreDeletedInternally(ENTITY entity,
            InternalDeleteNonstrictIgnoreDeletedCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        final int deletedCount = callback.callbackDelegateDeleteNonstrict(entity);
        if (deletedCount == 0) {
            return;
        } else if (deletedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, deletedCount);
        }
    }

    protected static interface InternalDeleteNonstrictIgnoreDeletedCallback<ENTITY extends Entity> {
        public int callbackDelegateDeleteNonstrict(ENTITY entity);
    }

    // ===================================================================================
    //                                                                        Batch Update
    //                                                                        ============
    /**
     * {@inheritDoc}
     */
    public int[] lumpCreate(List<Entity> entityList, InsertOption<? extends ConditionBean> option) {
        return doLumpCreate(entityList, option);
    }

    protected abstract int[] doLumpCreate(List<Entity> entityList, InsertOption<? extends ConditionBean> option);

    /**
     * {@inheritDoc}
     */
    public int[] lumpModify(List<Entity> entityList, UpdateOption<? extends ConditionBean> option) {
        return doLumpModify(entityList, option);
    }

    protected abstract int[] doLumpModify(List<Entity> entityList, UpdateOption<? extends ConditionBean> option);

    /**
     * {@inheritDoc}
     */
    public int[] lumpModifyNonstrict(List<Entity> entityList, UpdateOption<? extends ConditionBean> option) {
        return doLumpModifyNonstrict(entityList, option);
    }

    protected abstract int[] doLumpModifyNonstrict(List<Entity> entityList, UpdateOption<? extends ConditionBean> option);

    /**
     * {@inheritDoc}
     */
    public int[] lumpRemove(List<Entity> entityList, DeleteOption<? extends ConditionBean> option) {
        return doLumpRemove(entityList, option);
    }

    protected abstract int[] doLumpRemove(List<Entity> entityList, DeleteOption<? extends ConditionBean> option);

    /**
     * {@inheritDoc}
     */
    public int[] lumpRemoveNonstrict(List<Entity> entityList, DeleteOption<? extends ConditionBean> option) {
        return doLumpRemoveNonstrict(entityList, option);
    }

    protected abstract int[] doLumpRemoveNonstrict(List<Entity> entityList, DeleteOption<? extends ConditionBean> option);

    // =====================================================================================
    //                                                                          Query Update
    //                                                                          ============
    /**
     * {@inheritDoc}
     */
    public int rangeCreate(QueryInsertSetupper<? extends Entity, ? extends ConditionBean> setupper,
            InsertOption<? extends ConditionBean> option) {
        return doRangeCreate(setupper, option);
    }

    protected abstract int doRangeCreate(QueryInsertSetupper<? extends Entity, ? extends ConditionBean> setupper,
            InsertOption<? extends ConditionBean> option);

    /**
     * {@inheritDoc}
     */
    public int rangeModify(Entity entity, ConditionBean cb, UpdateOption<? extends ConditionBean> option) {
        return doRangeModify(entity, cb, option);
    }

    protected abstract int doRangeModify(Entity entity, ConditionBean cb, UpdateOption<? extends ConditionBean> option);

    /**
     * {@inheritDoc}
     */
    public int rangeRemove(ConditionBean cb, DeleteOption<? extends ConditionBean> option) {
        return doRangeRemove(cb, option);
    }

    protected abstract int doRangeRemove(ConditionBean cb, DeleteOption<? extends ConditionBean> option);

    // =====================================================================================
    //                                                                        Process Method
    //                                                                        ==============
    // -----------------------------------------------------
    //                                                Insert
    //                                                ------
    /**
     * Process before insert. </br >
     * You can stop the process by your extension.
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (NullAllowed)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeInsert(Entity entity, InsertOption<? extends ConditionBean> option) {
        assertEntityNotNull(entity); // primary key is checked later
        frameworkFilterEntityOfInsert(entity, option);
        filterEntityOfInsert(entity, option);
        assertEntityOfInsert(entity, option);
        // check primary key after filtering at an insert process
        // because a primary key value may be set in filtering process
        // (for example, sequence)
        if (!entity.getDBMeta().hasIdentity()) { // identity does not need primary key value here
            assertEntityNotNullAndHasPrimaryKeyValue(entity);
        }
        return true;
    }

    /**
     * Process before query-insert. </br >
     * You can stop the process by your extension.
     * @param entity The entity for query-insert. (NotNull)
     * @param intoCB The condition-bean for inserted table. (NotNull)
     * @param resourceCB The condition-bean for resource table. (NotNull)
     * @param option The option of insert. (NullAllowed)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeQueryInsert(Entity entity, ConditionBean intoCB, ConditionBean resourceCB,
            InsertOption<? extends ConditionBean> option) {
        assertEntityNotNull(entity); // query-insert doesn't need to check primary key
        assertObjectNotNull("intoCB", intoCB);
        if (resourceCB == null) {
            String msg = "The set-upper of query-insert should return a condition-bean for resource table:";
            msg = msg + " inserted=" + entity.getTableDbName();
            throw new IllegalConditionBeanOperationException(msg);
        }
        frameworkFilterEntityOfInsert(entity, option);
        setupExclusiveControlColumnOfQueryInsert(entity);
        filterEntityOfInsert(entity, option);
        assertEntityOfInsert(entity, option);
        return true;
    }

    protected void setupExclusiveControlColumnOfQueryInsert(Entity entity) {
        final DBMeta dbmeta = getDBMeta();
        if (dbmeta.hasVersionNo()) {
            final ColumnInfo columnInfo = dbmeta.getVersionNoColumnInfo();
            final String propertyName = columnInfo.getPropertyName();
            dbmeta.setupEntityProperty(propertyName, entity, InsertOption.VERSION_NO_FIRST_VALUE);
        }
        if (dbmeta.hasUpdateDate()) {
            final ColumnInfo columnInfo = dbmeta.getUpdateDateColumnInfo();
            final String propertyName = columnInfo.getPropertyName();
            dbmeta.setupEntityProperty(propertyName, entity, ResourceContext.getAccessTimestamp());
        }
    }

    /**
     * {Framework Method} Filter the entity of insert.
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (NullAllowed)
     */
    protected void frameworkFilterEntityOfInsert(Entity entity, InsertOption<? extends ConditionBean> option) {
        injectSequenceToPrimaryKeyIfNeeds(entity);
        setupCommonColumnOfInsertIfNeeds(entity, option);
    }

    /**
     * Set up common columns of insert if it needs.
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (NullAllowed)
     */
    protected void setupCommonColumnOfInsertIfNeeds(Entity entity, InsertOption<? extends ConditionBean> option) {
        if (option != null && option.isCommonColumnAutoSetupDisabled()) {
            return;
        }
        final CommonColumnAutoSetupper setupper = getCommonColumnAutoSetupper();
        assertCommonColumnAutoSetupperNotNull();
        setupper.handleCommonColumnOfInsertIfNeeds(entity);
    }

    private void assertCommonColumnAutoSetupperNotNull() {
        if (_commonColumnAutoSetupper != null) {
            return;
        }
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("Not found the auto set-upper of common column in the behavior!");
        br.addItem("Advice");
        br.addElement("Please confirm the definition of the set-upper at your component configuration of DBFlute.");
        br.addItem("Behavior");
        br.addElement("Behavior for " + getTableDbName());
        br.addItem("Attribute");
        br.addElement("behaviorCommandInvoker   : " + _behaviorCommandInvoker);
        br.addElement("behaviorSelector         : " + _behaviorSelector);
        br.addElement("commonColumnAutoSetupper : " + _commonColumnAutoSetupper);
        final String msg = br.buildExceptionMessage();
        throw new IllegalBehaviorStateException(msg);
    }

    /**
     * Filter the entity of insert. (for extension)
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (NullAllowed)
     */
    protected void filterEntityOfInsert(Entity entity, InsertOption<? extends ConditionBean> option) {
    }

    /**
     * Assert the entity of insert. (for extension)
     * @param entity The entity for insert. (NotNull)
     * @param option The option of insert. (NullAllowed)
     */
    protected void assertEntityOfInsert(Entity entity, InsertOption<? extends ConditionBean> option) {
    }

    /**
     * Assert that the insert option is not null.
     * @param option The option of insert. (NotNull)
     */
    protected void assertInsertOptionNotNull(InsertOption<? extends ConditionBean> option) {
        assertObjectNotNull("option (for insert)", option);
    }

    /**
     * Assert that the insert option is legal status.
     * @param option The option of insert. (NotNull)
     */
    protected void assertInsertOptionStatus(InsertOption<? extends ConditionBean> option) {
        if (option.isCommonColumnAutoSetupDisabled() && !getDBMeta().hasCommonColumn()) {
            String msg = "The common column auto-setup disabling was set to the table not defined common columns:";
            msg = msg + " table=" + getTableDbName() + " option=" + option;
            throw new IllegalStateException(msg);
        }
        if (option.isPrimaryKeyIdentityDisabled() && !getDBMeta().hasIdentity()) {
            String msg = "The identity disabling was set to the table not defined identity:";
            msg = msg + " table=" + getTableDbName() + " option=" + option;
            throw new IllegalStateException(msg);
        }
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    /**
     * Process before update. </br >
     * You can stop the process by your extension.
     * @param entity The entity for update that has primary key. (NotNull)
     * @param option The option of update. (NullAllowed)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeUpdate(Entity entity, UpdateOption<? extends ConditionBean> option) {
        assertEntityNotNullAndHasPrimaryKeyValue(entity);
        frameworkFilterEntityOfUpdate(entity, option);
        filterEntityOfUpdate(entity, option);
        assertEntityOfUpdate(entity, option);
        return true;
    }

    /**
     * Process before query-update. </br >
     * You can stop the process by your extension.
     * @param entity The entity for update that is not needed primary key. (NotNull)
     * @param cb The condition-bean for query. (NotNull)
     * @param option The option of update. (NullAllowed)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeQueryUpdate(Entity entity, ConditionBean cb,
            UpdateOption<? extends ConditionBean> option) {
        assertEntityNotNull(entity); // query-update doesn't need to check primary key
        assertCBNotNull(cb);
        frameworkFilterEntityOfUpdate(entity, option);
        filterEntityOfUpdate(entity, option);
        assertEntityOfUpdate(entity, option);
        assertQueryUpdateStatus(entity, cb, option);
        return true;
    }

    /**
     * {Framework Method} Filter the entity of update.
     * @param entity The entity for update. (NotNull)
     * @param option The option of update. (NullAllowed)
     */
    protected void frameworkFilterEntityOfUpdate(Entity entity, UpdateOption<? extends ConditionBean> option) {
        setupCommonColumnOfUpdateIfNeeds(entity, option);
    }

    /**
     * Set up common columns of update if it needs.
     * @param entity The entity for update. (NotNull)
     * @param option The option of update. (NullAllowed)
     */
    protected void setupCommonColumnOfUpdateIfNeeds(Entity entity, UpdateOption<? extends ConditionBean> option) {
        if (option != null && option.isCommonColumnAutoSetupDisabled()) {
            return;
        }
        final CommonColumnAutoSetupper setupper = getCommonColumnAutoSetupper();
        assertCommonColumnAutoSetupperNotNull();
        setupper.handleCommonColumnOfUpdateIfNeeds(entity);
    }

    /**
     * Filter the entity of update. (for extension)
     * @param entity The entity for update. (NotNull)
     * @param option The option of update. (NullAllowed)
     */
    protected void filterEntityOfUpdate(Entity entity, UpdateOption<? extends ConditionBean> option) {
    }

    /**
     * Assert the entity of update. (for extension)
     * @param entity The entity for update. (NotNull)
     * @param option The option of update. (NullAllowed)
     */
    protected void assertEntityOfUpdate(Entity entity, UpdateOption<? extends ConditionBean> option) {
    }

    /**
     * Assert that the update column specification is not null.
     * @param updateColumnSpec The SpecifyQuery implementation for update columns. (NotNull)
     */
    protected void assertUpdateColumnSpecificationNotNull(SpecifyQuery<? extends ConditionBean> updateColumnSpec) {
        assertObjectNotNull("updateColumnSpec", updateColumnSpec);
    }

    /**
     * Assert that the update option is not null.
     * @param option The option of update. (NotNull)
     */
    protected void assertUpdateOptionNotNull(UpdateOption<? extends ConditionBean> option) {
        assertObjectNotNull("option (for update)", option);
    }

    /**
     * Assert that the update option is legal status.
     * @param option The option of update. (NotNull)
     */
    protected void assertUpdateOptionStatus(UpdateOption<? extends ConditionBean> option) {
        if (option.isCommonColumnAutoSetupDisabled() && !getDBMeta().hasCommonColumn()) {
            String msg = "The common column auto-setup disabling was set to the table not defined common columns:";
            msg = msg + " table=" + getTableDbName() + " option=" + option;
            throw new IllegalStateException(msg);
        }
    }

    /**
     * Assert that the query-update is legal status.
     * @param entity The entity for query-update. (NotNull)
     * @param cb The condition-bean for query-update. (NotNull)
     * @param option The option of update. (NullAllowed)
     */
    protected void assertQueryUpdateStatus(Entity entity, ConditionBean cb, UpdateOption<? extends ConditionBean> option) {
        if (option != null && option.isNonQueryUpdateAllowed()) {
            return;
        }
        if (!cb.hasWhereClause()) {
            createBhvExThrower().throwNonQueryUpdateNotAllowedException(entity, cb, option);
        }
    }

    // -----------------------------------------------------
    //                                                Delete
    //                                                ------
    /**
     * Process before delete. </br >
     * You can stop the process by your extension.
     * @param entity The entity for delete that has primary key. (NotNull)
     * @param option The option of delete. (NullAllowed)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeDelete(Entity entity, DeleteOption<? extends ConditionBean> option) {
        assertEntityNotNullAndHasPrimaryKeyValue(entity);
        frameworkFilterEntityOfDelete(entity, option);
        filterEntityOfDelete(entity, option);
        assertEntityOfDelete(entity, option);
        return true;
    }

    /**
     * Process before query-delete. </br >
     * You can stop the process by your extension.
     * @param cb The condition-bean for query. (NotNull)
     * @param option The option of delete. (NullAllowed)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeQueryDelete(ConditionBean cb, DeleteOption<? extends ConditionBean> option) {
        assertCBNotNull(cb);
        assertQueryDeleteStatus(cb, option);
        return true;
    }

    /**
     * {Framework Method} Filter the entity of delete. {not called if query-delete}
     * @param entity The entity for delete that has primary key. (NotNull)
     * @param option The option of delete. (NullAllowed)
     */
    protected void frameworkFilterEntityOfDelete(Entity entity, DeleteOption<? extends ConditionBean> option) {
    }

    /**
     * Filter the entity of delete. (for extension) {not called if query-delete}
     * @param entity The entity for delete that has primary key. (NotNull)
     * @param option The option of delete. (NullAllowed)
     */
    protected void filterEntityOfDelete(Entity entity, DeleteOption<? extends ConditionBean> option) {
    }

    /**
     * Assert the entity of delete. (for extension) {not called if query-delete}
     * @param entity The entity for delete that has primary key. (NotNull)
     * @param option The option of delete. (NullAllowed)
     */
    protected void assertEntityOfDelete(Entity entity, DeleteOption<? extends ConditionBean> option) {
    }

    /**
     * Assert that the delete option is not null.
     * @param option The option of delete. (NotNull)
     */
    protected void assertDeleteOptionNotNull(DeleteOption<? extends ConditionBean> option) {
        assertObjectNotNull("option (for delete)", option);
    }

    /**
     * Assert that the delete option is legal status.
     * @param option The option of delete. (NotNull)
     */
    protected void assertDeleteOptionStatus(DeleteOption<? extends ConditionBean> option) {
    }

    /**
     * Assert that the query-delete is legal status.
     * @param cb The condition-bean for query-delete. (NotNull)
     * @param option The option of delete. (NullAllowed)
     */
    protected void assertQueryDeleteStatus(ConditionBean cb, DeleteOption<? extends ConditionBean> option) {
        if (option != null && option.isNonQueryDeleteAllowed()) {
            return;
        }
        if (!cb.hasWhereClause()) {
            createBhvExThrower().throwNonQueryDeleteNotAllowedException(cb, option);
        }
    }

    // -----------------------------------------------------
    //                                                Common
    //                                                ------
    protected void injectSequenceToPrimaryKeyIfNeeds(Entity entity) {
        final DBMeta dbmeta = entity.getDBMeta();
        if (!dbmeta.hasSequence() || dbmeta.hasCompoundPrimaryKey() || entity.hasPrimaryKeyValue()) {
            return;
        }
        // basically property(column) type is same as next value type
        // so there is NOT type conversion cost when writing to the entity
        dbmeta.getPrimaryUniqueInfo().getFirstColumn().write(entity, readNextVal());
    }

    protected <CB extends ConditionBean> UpdateOption<CB> createSpecifiedUpdateOption(SpecifyQuery<CB> updateColumnSpec) {
        assertUpdateColumnSpecificationNotNull(updateColumnSpec);
        final UpdateOption<CB> option = new UpdateOption<CB>();
        option.specify(updateColumnSpec);
        return option;
    }

    protected void assertEntityHasOptimisticLockValue(Entity entity) {
        assertEntityHasVersionNoValue(entity);
        assertEntityHasUpdateDateValue(entity);
    }

    protected void assertEntityHasVersionNoValue(Entity entity) {
        if (!getDBMeta().hasVersionNo()) {
            return;
        }
        if (hasVersionNoValue(entity)) {
            return;
        }
        throwVersionNoValueNullException(entity);
    }

    protected void throwVersionNoValueNullException(Entity entity) {
        createBhvExThrower().throwVersionNoValueNullException(entity);
    }

    protected void assertEntityHasUpdateDateValue(Entity entity) {
        if (!getDBMeta().hasUpdateDate()) {
            return;
        }
        if (hasUpdateDateValue(entity)) {
            return;
        }
        throwUpdateDateValueNullException(entity);
    }

    protected void throwUpdateDateValueNullException(Entity entity) {
        createBhvExThrower().throwUpdateDateValueNullException(entity);
    }

    // ===================================================================================
    //                                                     Delegate Method Internal Helper
    //                                                     ===============================
    protected <ENTITY extends Entity> List<ENTITY> processBatchInternally(List<ENTITY> entityList,
            InsertOption<? extends ConditionBean> option) {
        assertObjectNotNull("entityList", entityList);
        final List<ENTITY> filteredList = new ArrayList<ENTITY>();
        for (ENTITY entity : entityList) {
            if (!processBeforeInsert(entity, option)) {
                continue;
            }
            filteredList.add(entity);
        }
        return filteredList;
    }

    protected <ENTITY extends Entity> List<ENTITY> processBatchInternally(List<ENTITY> entityList,
            UpdateOption<? extends ConditionBean> option, boolean nonstrict) {
        assertObjectNotNull("entityList", entityList);
        final List<ENTITY> filteredList = new ArrayList<ENTITY>();
        for (ENTITY entity : entityList) {
            if (!processBeforeUpdate(entity, option)) {
                continue;
            }
            if (!nonstrict) {
                assertEntityHasOptimisticLockValue(entity);
            }
            filteredList.add(entity);
        }
        return filteredList;
    }

    protected <ENTITY extends Entity> List<ENTITY> processBatchInternally(List<ENTITY> entityList,
            DeleteOption<? extends ConditionBean> option, boolean nonstrict) {
        assertObjectNotNull("entityList", entityList);
        final List<ENTITY> filteredList = new ArrayList<ENTITY>();
        for (ENTITY entity : entityList) {
            if (!processBeforeDelete(entity, option)) {
                continue;
            }
            if (!nonstrict) {
                assertEntityHasOptimisticLockValue(entity);
            }
            filteredList.add(entity);
        }
        return filteredList;
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    // an insert command creation is defined on the readable interface for non-primary key value

    protected UpdateEntityCommand createUpdateEntityCommand(Entity entity, UpdateOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createUpdateEntityCommand");
        final UpdateEntityCommand cmd = xsetupEntityCommand(new UpdateEntityCommand(), entity);
        cmd.setUpdateOption(option);
        return cmd;
    }

    protected UpdateNonstrictEntityCommand createUpdateNonstrictEntityCommand(Entity entity,
            UpdateOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createUpdateNonstrictEntityCommand");
        final UpdateNonstrictEntityCommand cmd = xsetupEntityCommand(new UpdateNonstrictEntityCommand(), entity);
        cmd.setUpdateOption(option);
        return cmd;
    }

    protected DeleteEntityCommand createDeleteEntityCommand(Entity entity, DeleteOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createDeleteEntityCommand");
        final DeleteEntityCommand cmd = xsetupEntityCommand(new DeleteEntityCommand(), entity);
        cmd.setDeleteOption(option);
        return cmd;
    }

    protected DeleteNonstrictEntityCommand createDeleteNonstrictEntityCommand(Entity entity,
            DeleteOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createDeleteNonstrictEntityCommand");
        final DeleteNonstrictEntityCommand cmd = xsetupEntityCommand(new DeleteNonstrictEntityCommand(), entity);
        cmd.setDeleteOption(option);
        return cmd;
    }

    // -----------------------------------------------------
    //                                                 Batch
    //                                                 -----
    protected BatchInsertCommand createBatchInsertCommand(List<? extends Entity> entityList,
            InsertOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createBatchInsertCommand");
        final BatchInsertCommand cmd = xsetupListEntityCommand(new BatchInsertCommand(), entityList);
        cmd.setInsertOption(option);
        return cmd;
    }

    protected BatchUpdateCommand createBatchUpdateCommand(List<? extends Entity> entityList,
            UpdateOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createBatchUpdateCommand");
        final BatchUpdateCommand cmd = xsetupListEntityCommand(new BatchUpdateCommand(), entityList);
        cmd.setUpdateOption(option);
        return cmd;
    }

    protected BatchUpdateNonstrictCommand createBatchUpdateNonstrictCommand(List<? extends Entity> entityList,
            UpdateOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createBatchUpdateNonstrictCommand");
        final BatchUpdateNonstrictCommand cmd = xsetupListEntityCommand(new BatchUpdateNonstrictCommand(), entityList);
        cmd.setUpdateOption(option);
        return cmd;
    }

    protected BatchDeleteCommand createBatchDeleteCommand(List<? extends Entity> entityList,
            DeleteOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createBatchDeleteCommand");
        final BatchDeleteCommand cmd = xsetupListEntityCommand(new BatchDeleteCommand(), entityList);
        cmd.setDeleteOption(option);
        return cmd;
    }

    protected BatchDeleteNonstrictCommand createBatchDeleteNonstrictCommand(List<? extends Entity> entityList,
            DeleteOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createBatchDeleteNonstrictCommand");
        final BatchDeleteNonstrictCommand cmd = xsetupListEntityCommand(new BatchDeleteNonstrictCommand(), entityList);
        cmd.setDeleteOption(option);
        return cmd;
    }

    /**
     * @param <COMMAND> The type of behavior command for list entity.
     * @param command The command of behavior. (NotNull)
     * @param entityList The list of entity. (NotNull, NotEmpty)
     * @return The command of behavior. (NotNull)
     */
    protected <COMMAND extends AbstractListEntityCommand> COMMAND xsetupListEntityCommand(COMMAND command,
            List<? extends Entity> entityList) {
        if (entityList.isEmpty()) {
            String msg = "The argument 'entityList' should not be empty: " + entityList;
            throw new IllegalStateException(msg);
        }
        command.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(command);
        command.setEntityType(entityList.get(0).getClass()); // *The list should not be empty!
        command.setEntityList(entityList);
        return command;
    }

    // -----------------------------------------------------
    //                                                 Query
    //                                                 -----
    protected QueryInsertCBCommand createQueryInsertCBCommand(Entity entity, ConditionBean intoCB,
            ConditionBean resourceCB, InsertOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createQueryInsertCBCommand");
        final QueryInsertCBCommand cmd = new QueryInsertCBCommand();
        cmd.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(cmd);
        cmd.setEntity(entity);
        cmd.setIntoConditionBean(intoCB);
        cmd.setConditionBean(resourceCB);
        cmd.setInsertOption(option);
        return cmd;
    }

    protected QueryUpdateCBCommand createQueryUpdateCBCommand(Entity entity, ConditionBean cb,
            UpdateOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createQueryUpdateCBCommand");
        final QueryUpdateCBCommand cmd = new QueryUpdateCBCommand();
        cmd.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(cmd);
        cmd.setEntity(entity);
        cmd.setConditionBean(cb);
        cmd.setUpdateOption(option);
        return cmd;
    }

    protected QueryDeleteCBCommand createQueryDeleteCBCommand(ConditionBean cb,
            DeleteOption<? extends ConditionBean> option) {
        assertBehaviorCommandInvoker("createQueryDeleteCBCommand");
        final QueryDeleteCBCommand cmd = new QueryDeleteCBCommand();
        cmd.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(cmd);
        cmd.setConditionBean(cb);
        cmd.setDeleteOption(option);
        return cmd;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the auto set-upper of common column.
     * @return The auto set-upper of common column. (NullAllowed: But normally NotNull)
     */
    protected CommonColumnAutoSetupper getCommonColumnAutoSetupper() {
        return _commonColumnAutoSetupper;
    }

    /**
     * Set the auto set-upper of common column.
     * @param commonColumnAutoSetupper The auto set-upper of common column. (NotNull)
     */
    public void setCommonColumnAutoSetupper(CommonColumnAutoSetupper commonColumnAutoSetupper) {
        this._commonColumnAutoSetupper = commonColumnAutoSetupper;
    }
}
