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
package org.seasar.robot.dbflute.exception;

/**
 * The exception of when the entity has already been updated by other thread.
 * @author jflute
 */
public class EntityAlreadyUpdatedException extends SQLFailureException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // legacy of S2Dao
    private Object _bean;

    private int _rows;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * Constructor.
     * @param bean The instance of entity. (NotNull)
     * @param rows The row count returned by update process. (basically zero)
     */
    public EntityAlreadyUpdatedException(Object bean, int rows) {
        super("The entity already been updated: rows=" + rows + ", bean=" + bean, null);
        this._bean = bean;
        this._rows = rows;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Object getBean() {
        return _bean;
    }

    public int getRows() {
        return _rows;
    }
}
