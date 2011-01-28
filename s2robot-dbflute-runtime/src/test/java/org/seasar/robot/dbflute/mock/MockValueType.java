package org.seasar.robot.dbflute.mock;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.robot.dbflute.jdbc.ValueType;

/**
 * @author jflute
 * @since 0.9.6.4 (2010/01/22 Friday)
 */
public class MockValueType implements ValueType {

    public int getSqlType() {
        return 0;
    }

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return null;
    }

    public Object getValue(ResultSet resultSet, String columnName) throws SQLException {
        return null;
    }

    public Object getValue(CallableStatement cs, int index) throws SQLException {
        return null;
    }

    public Object getValue(CallableStatement cs, String parameterName) throws SQLException {
        return null;
    }

    public void bindValue(Connection conn, PreparedStatement ps, int index, Object value) throws SQLException {
    }

    public void bindValue(Connection conn, CallableStatement cs, String parameterName, Object value)
            throws SQLException {
    }

    public void registerOutParameter(Connection conn, CallableStatement cs, int index) throws SQLException {
    }

    public void registerOutParameter(Connection conn, CallableStatement cs, String parameterName) throws SQLException {
    }
}
