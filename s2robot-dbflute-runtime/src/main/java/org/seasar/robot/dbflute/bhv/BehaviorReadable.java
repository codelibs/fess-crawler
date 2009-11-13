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

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.cbean.ConditionBean;
import org.seasar.robot.dbflute.cbean.ListResultBean;
import org.seasar.robot.dbflute.cbean.PagingResultBean;
import org.seasar.robot.dbflute.dbmeta.DBMeta;

/**
 * The interface of behavior-readable.
 * @author jflute
 */
public interface BehaviorReadable {

    // ===================================================================================
    //                                                                          Table name
    //                                                                          ==========
    /**
     * Get table DB name.
     * @return Table DB name. (NotNull)
     */
    String getTableDbName();

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * Get the instance of DBMeta.
     * @return The instance of DBMeta. (NotNull)
     */
    DBMeta getDBMeta();

    // ===================================================================================
    //                                                                        New Instance
    //                                                                        ============
    /**
     * New entity.
     * @return Entity. (NotNull)
     */
    Entity newEntity();

    /**
     * New condition-bean.
     * @return Condition-bean. (NotNull)
     */
    ConditionBean newConditionBean();

    // ===================================================================================
    //                                                                       Basic Get All
    //                                                                       =============
    /**
     * Get count all.
     * @return Count all.
     * @deprecated Sorry! Please use selectCount(emptyCB)
     */
    int getCountAll();

    // ===================================================================================
    //                                                                    Basic Read Count
    //                                                                    ================
    /**
     * Read count by condition-bean.
     * <pre>
     * If the argument 'condition-bean' is effective about fetch-scope,
     * this method invoke select count ignoring the fetch-scope.
     * </pre>
     * @param cb Condition-bean. This condition-bean should not be set up about fetch-scope. (NotNull)
     * @return Read count. (NotNull)
     */
    int readCount(ConditionBean cb);

    // ===================================================================================
    //                                                                   Basic Read Entity
    //                                                                   =================
    /**
     * Read entity by condition-bean.
     * @param cb Condition-bean. (NotNull)
     * @return Read entity. (Nullalble)
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    Entity readEntity(ConditionBean cb);

    /**
     * Read simple entity by condition-bean with deleted check.
     * @param cb Condition-bean. (NotNull)
     * @return Read entity. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.robot.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    Entity readEntityWithDeletedCheck(ConditionBean cb);

    // ===================================================================================
    //                                                                     Basic Read List
    //                                                                     ===============
    /**
     * Read list as result-bean.
     * @param cb Condition-bean. (NotNull)
     * @return The list of entity as result-bean. If the select result is zero, it returns empty list. (NotNull)
     */
    ListResultBean<? extends Entity> readList(ConditionBean cb);

    /**
     * Read page as result-bean.
     * @param cb Condition-bean. (NotNull)
     * @return The page of entity as result-bean. (NotNull)
     */
    PagingResultBean<? extends Entity> readPage(final ConditionBean cb);

    // ===================================================================================
    //                                                                            Sequence
    //                                                                            ========
    /**
     * Read next value of sequence.
     * @return The next value of sequence. (NotNull)
     */
    java.math.BigDecimal readNextVal();

    // ===================================================================================
    //                                                                             Warm Up
    //                                                                             =======
    /**
     * Warm up the command of behavior.
     */
    void warmUpCommand();
}
