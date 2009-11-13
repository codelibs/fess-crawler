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
package org.seasar.robot.dbflute.s2dao.sqlcommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.seasar.robot.dbflute.jdbc.StatementFactory;
import org.seasar.robot.dbflute.s2dao.metadata.TnBeanMetaData;
import org.seasar.robot.dbflute.s2dao.metadata.TnPropertyType;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnUpdateModifiedOnlyCommand extends TnUpdateAutoDynamicCommand {

	// ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public TnUpdateModifiedOnlyCommand(DataSource dataSource, StatementFactory statementFactory) {
        super(dataSource, statementFactory);
    }

	// ===================================================================================
    //                                                                 No.1 Point Override
    //                                                                 ===================
    @Override
    protected TnPropertyType[] createUpdatePropertyTypes(final TnBeanMetaData bmd, final Object bean, final String[] propertyNames) {
        final Set<?> modifiedPropertyNames = getBeanMetaData().getModifiedPropertyNames(bean);
        final List<TnPropertyType> types = new ArrayList<TnPropertyType>();
        final String timestampPropertyName = bmd.getTimestampPropertyName();
        final String versionNoPropertyName = bmd.getVersionNoPropertyName();
        for (int i = 0; i < propertyNames.length; ++i) {
            final TnPropertyType pt = bmd.getPropertyType(propertyNames[i]);
            if (pt.isPrimaryKey() == false) {
                final String propertyName = pt.getPropertyName();
                if (propertyName.equalsIgnoreCase(timestampPropertyName)
                        || propertyName.equalsIgnoreCase(versionNoPropertyName)
                        || modifiedPropertyNames.contains(propertyName)) {
                    types.add(pt);
                }
            }
        }
        final TnPropertyType[] propertyTypes = (TnPropertyType[]) types.toArray(new TnPropertyType[types.size()]);
        return propertyTypes;
    }
}
