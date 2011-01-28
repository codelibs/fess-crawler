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
package org.seasar.robot.dbflute.s2dao.rshandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.robot.dbflute.jdbc.ValueType;

/**
 * @author jflute
 */
public class TnMapListResultSetHandler extends TnAbstractMapResultSetHandler {

    public Object handle(ResultSet rs) throws SQLException {
        final Map<String, ValueType> propertyTypeMap = createPropertyTypeMap(rs.getMetaData());
        final List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (rs.next()) {
            list.add(createRow(rs, propertyTypeMap));
        }
        return list;
    }
}
