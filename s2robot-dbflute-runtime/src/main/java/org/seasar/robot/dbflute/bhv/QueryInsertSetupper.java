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

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.cbean.ConditionBean;

/**
 * The set-upper for query-insert.
 * @author jflute
 * @param <ENTITY> The type of entity.
 * @param <CB> The type of condition-bean.
 */
public interface QueryInsertSetupper<ENTITY extends Entity, CB extends ConditionBean> {

    /**
     * Set up your query condition for insert. <br />
     * @param entity The entity of inserted table, to be set fixed values. (NotNull, EmptyEntity)
     * @param intoCB The condition-bean of inserted table, to be specified columns. (NotNull, EmptyCB)
     * @return The condition-bean of resource table, that has queries. (NotNull)
     */
    ConditionBean setup(ENTITY entity, CB intoCB);
}
