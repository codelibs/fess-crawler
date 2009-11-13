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
package org.seasar.robot.dbflute.s2dao.valuetype.plugin;

import java.io.Reader;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.seasar.robot.dbflute.s2dao.valuetype.TnAbstractValueType;
import org.seasar.robot.dbflute.util.DfResourceUtil;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class TnStringClobType extends TnAbstractValueType {

    public TnStringClobType() {
        super(Types.CLOB);
    }

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return convertToString(resultSet.getCharacterStream(index));
    }

    public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
        return convertToString(resultSet.getCharacterStream(columnName));
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return convertToString(cs.getClob(index));
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return convertToString(cs.getClob(parameterName));
    }

    protected String convertToString(Reader reader) {
        if (reader == null) {
            return null;
        }
        return DfResourceUtil.readText(reader);
    }

    protected String convertToString(Clob clob) throws SQLException {
        if (clob == null) {
            return null;
        }
        return convertToString(clob.getCharacterStream());
    }

    public void bindValue(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            final String s = DfTypeUtil.toString(value);
            ps.setCharacterStream(index, new StringReader(s), s.length());
        }
    }

    public void bindValue(CallableStatement cs, String parameterName, Object value) throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            final String s = DfTypeUtil.toString(value);
            cs.setCharacterStream(parameterName, new StringReader(s), s.length());
        }
    }

    public String toText(Object value) {
        if (value == null) {
            return DfTypeUtil.nullText();
        }
        String var = DfTypeUtil.toString(value);
        return DfTypeUtil.toText(var);
    }

}