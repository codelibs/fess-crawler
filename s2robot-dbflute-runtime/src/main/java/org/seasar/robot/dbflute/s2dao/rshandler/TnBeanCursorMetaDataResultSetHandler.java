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
package org.seasar.robot.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.robot.dbflute.Entity;
import org.seasar.robot.dbflute.cbean.ConditionBeanContext;
import org.seasar.robot.dbflute.cbean.EntityRowHandler;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRelationRowCreator;
import org.seasar.robot.dbflute.s2dao.rowcreator.TnRowCreator;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnBeanCursorMetaDataResultSetHandler extends TnBeanListMetaDataResultSetHandler {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param beanMetaData Bean meta data. (NotNull)
     * @param rowCreator Row creator. (NotNull)
     * @param relationRowCreator Relation row creator. (NotNul)
     */
    public TnBeanCursorMetaDataResultSetHandler(TnBeanMetaData beanMetaData, TnRowCreator rowCreator,
            TnRelationRowCreator relationRowCreator) {
        super(beanMetaData, rowCreator, relationRowCreator);
    }

    // ===================================================================================
    //                                                                              Handle
    //                                                                              ======
    public Object handle(ResultSet rs) throws SQLException {
        if (!hasEntityRowHandler()) {
            String msg = "Bean cursor handling should have condition-bean!";
            throw new IllegalStateException(msg);
        }
        final EntityRowHandler<Entity> entityRowHandler = getEntityRowHandler();
        mappingBean(rs, new BeanRowHandler() {
            public void handle(Object row) throws SQLException {
                if (!(row instanceof Entity)) {
                    String msg = "The row object should be an entity at bean cursor handling:";
                    msg = msg + " row=" + (row != null ? row.getClass().getName() + ":" + row : null);
                    throw new IllegalStateException(msg);
                }
                entityRowHandler.handle((Entity) row);
            }
        });
        return null;
    }

    @Override
    protected TnRelationRowCache createRelationRowCache(int relSize) {
        // Override for non cache.
        // Cursor select is for save memory so it should not cache instances.
        return new TnRelationRowNonCache(relSize);
    }

    // ===================================================================================
    //                                                                       Assist Helper
    //                                                                       =============
    protected boolean hasEntityRowHandler() {
        return ConditionBeanContext.isExistEntityRowHandlerOnThread();
    }

    protected EntityRowHandler<Entity> getEntityRowHandler() {
        EntityRowHandler<? extends Entity> handlerOnThread = ConditionBeanContext.getEntityRowHandlerOnThread();
        @SuppressWarnings("unchecked")
        EntityRowHandler<Entity> entityRowHandler = (EntityRowHandler<Entity>) handlerOnThread;
        return entityRowHandler;
    }
}
