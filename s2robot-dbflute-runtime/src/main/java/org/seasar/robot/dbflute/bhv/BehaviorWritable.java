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
package org.seasar.robot.dbflute.bhv;

import java.util.List;

import org.seasar.robot.dbflute.Entity;

/**
 * The interface of behavior-writable.
 * @author jflute
 */
public interface BehaviorWritable extends BehaviorReadable {

    // =====================================================================================
    //                                                                   Basic Entity Update
    //                                                                   ===================
    /**
     * Create.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    void create(org.seasar.robot.dbflute.Entity entity);

    /**
     * Modify.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    void modify(org.seasar.robot.dbflute.Entity entity);

    /**
     * Modify non-strict.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    void modifyNonstrict(Entity entity);

    /**
     * Create or modify. <br />
     * {modify: modified only} <br />
     * This method is faster than createOrModifyAfterSelect().
     * @param entity Entity. This must contain primary-key value at least(Except use identity). (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    void createOrModify(org.seasar.robot.dbflute.Entity entity);

    /**
     * Create or modify non-strict. <br />
     * {modify: modified only} <br />
     * This method is faster than createOrModifyAfterSelect().
     * @param entity Entity. This must contain primary-key value at least(Except use identity). (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    void createOrModifyNonstrict(org.seasar.robot.dbflute.Entity entity);

    /**
     * Remove.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    void remove(org.seasar.robot.dbflute.Entity entity);

    // =====================================================================================
    //                                                                    Basic Batch Update
    //                                                                    ==================
    /**
     * Lump create the list.
     * @param entityList The list of entity. (NotNull and NotEmpty)
     * @return The array of created count.
     */
    int[] lumpCreate(List<Entity> entityList);

    /**
     * Lump modify the list.
     * @param entityList The list of entity. (NotNull and NotEmpty)
     * @return Modified count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated. And Only when s2dao's version is over 1.0.47 (contains 1.0.47).
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    int[] lumpModify(List<Entity> entityList);

    /**
     * Lump remove the list.
     * @param entityList The list of entity. (NotNull and NotEmpty)
     * @return Removed count.
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated. And Only when s2dao's version is over 1.0.47 (contains 1.0.47).
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    int[] lumpRemove(List<Entity> entityList);
}
