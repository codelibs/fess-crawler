package org.seasar.robot.dbflute.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

/**
 * The connection wrapper that is not closed in the method 'close()'. <br />
 * The method 'close()' do not close really. Only gets out an actual connection.
 * @author jflute
 * @since 0.9.5 (2009/04/29 Wednesday)
 */
public class NotClosingConnectionWrapper implements Connection {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Connection _actualConnection;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public NotClosingConnectionWrapper(Connection actualConnection) {
        _actualConnection = actualConnection;
    }

    // ===================================================================================
    //                                                                   Actual Connection
    //                                                                   =================
    public Connection getActualConnection() {
        return _actualConnection;
    }

    // ===================================================================================
    //                                                                      Implementation
    //                                                                      ==============
    public void clearWarnings() throws SQLException {
        _actualConnection.clearWarnings();
    }

    public void close() throws SQLException {
        _actualConnection = null;

        // *Point
        //_actualConnection.close();
    }

    public void commit() throws SQLException {
        _actualConnection.commit();
    }

    public Statement createStatement() throws SQLException {
        return _actualConnection.createStatement();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        return _actualConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return _actualConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    public boolean getAutoCommit() throws SQLException {
        return _actualConnection.getAutoCommit();
    }

    public String getCatalog() throws SQLException {
        return _actualConnection.getCatalog();
    }

    public int getHoldability() throws SQLException {
        return _actualConnection.getHoldability();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return _actualConnection.getMetaData();
    }

    public int getTransactionIsolation() throws SQLException {
        return _actualConnection.getTransactionIsolation();
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return _actualConnection.getTypeMap();
    }

    public SQLWarning getWarnings() throws SQLException {
        return _actualConnection.getWarnings();
    }

    public boolean isClosed() throws SQLException {
        return _actualConnection.isClosed();
    }

    public boolean isReadOnly() throws SQLException {
        return _actualConnection.isReadOnly();
    }

    public String nativeSQL(String sql) throws SQLException {
        return _actualConnection.nativeSQL(sql);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return _actualConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return _actualConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return _actualConnection.prepareCall(sql);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return _actualConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return _actualConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return _actualConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return _actualConnection.prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return _actualConnection.prepareStatement(sql, columnNames);
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return _actualConnection.prepareStatement(sql);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        _actualConnection.releaseSavepoint(savepoint);
    }

    public void rollback() throws SQLException {
        _actualConnection.rollback();
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        _actualConnection.rollback(savepoint);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        _actualConnection.setAutoCommit(autoCommit);
    }

    public void setCatalog(String catalog) throws SQLException {
        _actualConnection.setCatalog(catalog);
    }

    public void setHoldability(int holdability) throws SQLException {
        _actualConnection.setHoldability(holdability);
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        _actualConnection.setReadOnly(readOnly);
    }

    public Savepoint setSavepoint() throws SQLException {
        return _actualConnection.setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException {
        return _actualConnection.setSavepoint(name);
    }

    public void setTransactionIsolation(int level) throws SQLException {
        _actualConnection.setTransactionIsolation(level);
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        _actualConnection.setTypeMap(map);
    }
}
