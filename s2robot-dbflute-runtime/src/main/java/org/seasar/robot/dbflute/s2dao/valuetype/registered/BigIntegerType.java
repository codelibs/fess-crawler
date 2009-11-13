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

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.seasar.robot.dbflute.s2dao.valuetype.TnAbstractValueType;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class BigIntegerType extends TnAbstractValueType {

    public BigIntegerType() {
        super(Types.BIGINT);
    }

    public Object getValue(final ResultSet resultSet, final int index) throws SQLException {
        return DfTypeUtil.toBigInteger(resultSet.getBigDecimal(index));
    }

    public Object getValue(final ResultSet resultSet, final String columnName) throws SQLException {
        return DfTypeUtil.toBigInteger(resultSet.getBigDecimal(columnName));
    }

    public Object getValue(final CallableStatement cs, final int index) throws SQLException {
        return DfTypeUtil.toBigInteger(cs.getBigDecimal(index));
    }

    public Object getValue(final CallableStatement cs, final String parameterName) throws SQLException {
        return DfTypeUtil.toBigInteger(cs.getBigDecimal(parameterName));
    }

    public void bindValue(final PreparedStatement ps, final int index, final Object value) throws SQLException {
        if (value == null) {
            setNull(ps, index);
        } else {
            ps.setBigDecimal(index, DfTypeUtil.toBigDecimal(value));
        }
    }

    public void bindValue(final CallableStatement cs, final String parameterName, final Object value)
            throws SQLException {
        if (value == null) {
            setNull(cs, parameterName);
        } else {
            cs.setBigDecimal(parameterName, DfTypeUtil.toBigDecimal(value));
        }
    }

    public String toText(Object value) {
        if (value == null) {
            return DfTypeUtil.nullText();
        }
        BigDecimal var = DfTypeUtil.toBigDecimal(value);
        return DfTypeUtil.toText(var);
    }
}
