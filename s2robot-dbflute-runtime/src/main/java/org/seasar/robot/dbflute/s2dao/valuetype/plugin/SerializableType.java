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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public class SerializableType extends BytesType {

    public SerializableType(Trait trait) {
        super(trait);
    }

    public Object getValue(final ResultSet resultSet, final int index) throws SQLException {
        return deserialize(super.getValue(resultSet, index));
    }

    public Object getValue(final ResultSet resultSet, final String columnName) throws SQLException {
        return deserialize(super.getValue(resultSet, columnName));
    }

    public Object getValue(final CallableStatement cs, final int index) throws SQLException {
        return deserialize(super.getValue(cs, index));
    }

    public Object getValue(final CallableStatement cs, final String parameterName) throws SQLException {
        return deserialize(super.getValue(cs, parameterName));
    }

    public void bindValue(final PreparedStatement ps, final int index, final Object value) throws SQLException {
        super.bindValue(ps, index, serialize(value));
    }

    public void bindValue(final CallableStatement cs, final String parameterName, final Object value)
            throws SQLException {
        super.bindValue(cs, parameterName, serialize(value));
    }

    protected byte[] serialize(final Object o) throws SQLException {
        if (o == null) {
            return null;
        }
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            try {
                return baos.toByteArray();
            } finally {
                oos.close();
            }
        } catch (final Exception e) {
            String msg = "The Exception occurred: object=" + o;
            throw new IllegalStateException(msg, e);
        }
    }

    protected Object deserialize(final Object bytes) throws SQLException {
        if (bytes == null) {
            return null;
        }
        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) bytes);
            final ObjectInputStream ois = new ObjectInputStream(bais);
            try {
                return ois.readObject();
            } finally {
                ois.close();
            }
        } catch (final Exception e) {
            String msg = "The Exception occurred: object=" + bytes;
            throw new IllegalStateException(msg, e);
        }
    }

    public String toText(Object value) {
        if (value == null) {
            return DfTypeUtil.nullText();
        }
        return DfTypeUtil.toText(value);
    }
}
