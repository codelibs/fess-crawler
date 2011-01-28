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
package org.seasar.robot.dbflute.s2dao.identity;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Created with reference to S2Container's utility and extended for DBFlute}
 * @author jflute
 */
public class TnIdentifierIdentityGenerator extends TnIdentifierAbstractGenerator {

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnIdentifierIdentityGenerator(TnPropertyType propertyType) {
        super(propertyType);
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public void setIdentifier(Object bean, DataSource ds) {
        final String identitySelectSql = ResourceContext.currentDBDef().dbway().getIdentitySelectSql();
        if (identitySelectSql == null) {
            String msg = "Identity is unsupported at the DB: " + ResourceContext.currentDBDef();
            throw new IllegalStateException(msg);
        }
        final Object value = executeSql(ds, identitySelectSql, null);
        reflectIdentifier(bean, value);
    }

    public boolean isSelfGenerate() {
        return false;
    }
}
