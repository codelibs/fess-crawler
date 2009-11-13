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
package org.seasar.robot.dbflute.s2dao.valuetype.registered;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.seasar.robot.dbflute.jdbc.Classification;
import org.seasar.robot.dbflute.s2dao.valuetype.TnAbstractValueType;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The value type of classification. (DBFlute original)
 * @author jflute
 */
public class ClassificationType extends TnAbstractValueType {

    public ClassificationType() {
        super(Types.VARCHAR);
    }

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        String msg = "Getting as classification is unsupported: index=" + index;
        throw new UnsupportedOperationException(msg);
    }

    public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
        String msg = "Getting as classification is unsupported: columnName=" + columnName;
        throw new UnsupportedOperationException(msg);
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        String msg = "Getting as classification for Procedure is unsupported: index=" + index;
        throw new UnsupportedOperationException(msg);
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        String msg = "Getting as classification for Procedure is unsupported: parameterName=" + parameterName;
        throw new UnsupportedOperationException(msg);
    }

    public void bindValue(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            if (!(value instanceof Classification)) {
                String msg = "The value should be classification:";
                msg = msg + " value=" + value + " type=" + value.getClass();
                throw new IllegalStateException(msg);
            }
            final Classification cls = (Classification) value;
            if (Classification.DataType.String.equals(cls.dataType())) {
                ps.setString(index, cls.code());
            } else if (Classification.DataType.Number.equals(cls.dataType())) {
                ps.setInt(index, DfTypeUtil.toInteger(cls.code()));
            } else {
                ps.setObject(index, cls.code());
            }
        }
    }

    public void bindValue(CallableStatement cs, String parameterName, Object value) throws SQLException {
        String msg = "Binding as classification for Procedure is unsupported: value=" + value;
        throw new UnsupportedOperationException(msg);
    }

    public String toText(Object value) {
        if (value == null) {
            return DfTypeUtil.nullText();
        }
        if (!(value instanceof Classification)) {
            String msg = "The value should be classification:";
            msg = msg + " value=" + value + " type=" + value.getClass();
            throw new IllegalStateException(msg);
        }
        final Classification cls = (Classification) value;
        return cls.code();
    }
}