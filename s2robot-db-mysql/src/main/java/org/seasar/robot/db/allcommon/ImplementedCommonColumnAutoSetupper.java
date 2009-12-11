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
package org.seasar.robot.db.allcommon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.bhv.core.CommonColumnAutoSetupper;

/**
 * The basic implementation of the auto set-upper of common column.
 * @author DBFlute(AutoGenerator)
 */
public class ImplementedCommonColumnAutoSetupper implements CommonColumnAutoSetupper {

    // =====================================================================================
    //                                                                            Definition
    //                                                                            ==========
    /** Log instance. */
    private static final Log _log = LogFactory.getLog(ImplementedCommonColumnAutoSetupper.class);

    // =====================================================================================
    //                                                                                Set up
    //                                                                                ======
    /**
     * {@inheritDoc}
     */
    public void handleCommonColumnOfInsertIfNeeds(Entity targetEntity) {
    }

    /**
     * {@inheritDoc}
     */
    public void handleCommonColumnOfUpdateIfNeeds(Entity targetEntity) {
    }

    // =====================================================================================
    //                                                                               Logging
    //                                                                               =======
    protected void log(EntityDefinedCommonColumn entity, String keyword) {
        if (_log.isDebugEnabled()) {
            _log.debug("...Setting up column columns of " + entity.getTableDbName() + " before " + keyword + "!");
        }
    }
}
