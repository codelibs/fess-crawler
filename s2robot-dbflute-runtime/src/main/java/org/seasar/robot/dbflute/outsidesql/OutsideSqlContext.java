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
package org.seasar.robot.dbflute.outsidesql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException;
import org.seasar.robot.dbflute.exception.OutsideSqlReadFailureException;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.jdbc.CursorHandler;
import org.seasar.robot.dbflute.jdbc.StatementConfig;
import org.seasar.robot.dbflute.util.DfResourceUtil;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * The context of outside-SQL.
 * @author jflute
 */
public class OutsideSqlContext {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(OutsideSqlContext.class);

    // ===================================================================================
    //                                                                        Thread Local
    //                                                                        ============
    /** The thread-local for this. */
    private static final ThreadLocal<OutsideSqlContext> _threadLocal = new ThreadLocal<OutsideSqlContext>();

    /**
     * Get outside-SQL context on thread.
     * @return The context of outside-SQL. (NullAllowed)
     */
    public static OutsideSqlContext getOutsideSqlContextOnThread() {
        return (OutsideSqlContext) _threadLocal.get();
    }

    /**
     * Set outside-SQL context on thread.
     * @param outsideSqlContext The context of outside-SQL. (NotNull)
     */
    public static void setOutsideSqlContextOnThread(OutsideSqlContext outsideSqlContext) {
        if (outsideSqlContext == null) {
            String msg = "The argument[outsideSqlContext] must not be null.";
            throw new IllegalArgumentException(msg);
        }
        _threadLocal.set(outsideSqlContext);
    }

    /**
     * Is existing the context of outside-SQL on thread?
     * @return Determination.
     */
    public static boolean isExistOutsideSqlContextOnThread() {
        return (_threadLocal.get() != null);
    }

    /**
     * Clear the context of outside-SQL on thread.
     */
    public static void clearOutsideSqlContextOnThread() {
        _threadLocal.set(null);
    }

    // ===================================================================================
    //                                                                          Unique Key
    //                                                                          ==========
    public static String generateSpecifiedOutsideSqlUniqueKey(String methodName, String path, Object pmb,
            OutsideSqlOption option, Class<?> resultType) {
        final String pmbKey = (pmb != null ? pmb.getClass().getName() : "null");
        final String resultKey;
        if (resultType != null) {
            resultKey = resultType.getName();
        } else {
            resultKey = "null";
        }
        final String tableDbName = option.getTableDbName();
        final String generatedUniqueKey = option.generateUniqueKey();
        return tableDbName + ":" + methodName + "():" + path + ":" + pmbKey + ":" + generatedUniqueKey + ":"
                + resultKey;
    }

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
    public static void throwOutsideSqlNotFoundException(String path) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The outsideSql was not found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the existence of your target file of outsideSql on your classpath." + ln();
        msg = msg + "And please confirm the file name and the file path STRICTLY!" + ln();
        msg = msg + ln();
        msg = msg + "[Specified OutsideSql Path]" + ln() + path + ln();
        msg = msg + "* * * * * * * * * */";
        throw new OutsideSqlNotFoundException(msg);
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    /** The provider of DB meta. (NotNull) */
    protected final DBMetaProvider _dbmetaProvider;

    /** The package of outside-SQL. (NullAllowed: If null, use behavior package path) */
    protected final String _outsideSqlPackage;

    /** The path of outside-SQL. (The mark of specified outside-SQL) */
    protected String _outsideSqlPath;

    protected Object _parameterBean;

    protected Class<?> _resultType;

    protected CursorHandler _cursorHandler;

    protected String _methodName;

    /** The configuration of statement. (NullAllowed) */
    protected StatementConfig _statementConfig;

    /** The DB name of table for using behavior-SQL-path. (NullAllowed) */
    protected String _tableDbName;

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    protected boolean _offsetByCursorForcedly;

    protected boolean _limitByCursorForcedly;

    protected boolean _autoPagingLogging; // for logging

    protected OutsideSqlFilter _outsideSqlFilter;

    protected boolean _removeBlockComment;

    protected boolean _removeLineComment;

    protected boolean _formatSql;

    protected boolean _internalDebug;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param dbmetaProvider The provider of DB meta. (NotNull)
     * @param outsideSqlPackage The package of outside SQL. (NullAllowed: If null, use behavior package path)
     */
    public OutsideSqlContext(DBMetaProvider dbmetaProvider, String outsideSqlPackage) {
        if (dbmetaProvider == null) {
            String msg = "The argument 'dbmetaProvider' should not be null!";
            throw new IllegalArgumentException(msg);
        }
        _dbmetaProvider = dbmetaProvider;
        _outsideSqlPackage = outsideSqlPackage;
    }

    // ===================================================================================
    //                                                                            Read SQL
    //                                                                            ========
    /**
     * Read outside-SQL with filter. Required attribute is 'outsideSqlPath'.
     * @param sqlFileEncoding The encoding of SQL file. (NotNull)
     * @param dbmsSuffix The suffix of DBMS. (NotNull)
     * @return The filtered outside-SQL. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the SQL is not found.
     */
    public String readFilteredOutsideSql(String sqlFileEncoding, String dbmsSuffix) { // entry here
        String sql = readPlainOutsideSql(sqlFileEncoding, dbmsSuffix);
        sql = replaceOutsideSqlBindCharacterOnLineComment(sql);
        if (_outsideSqlFilter != null) {
            sql = _outsideSqlFilter.filterReading(sql);
        }
        return sql;
    }

    protected String replaceOutsideSqlBindCharacterOnLineComment(String sql) {
        final String bindCharacter = "?";
        if (sql.indexOf(bindCharacter) < 0) {
            return sql;
        }
        final String lineSeparator = "\n";
        if (sql.indexOf(lineSeparator) < 0) {
            return sql;
        }
        final String lineCommentMark = "--";
        if (sql.indexOf(lineCommentMark) < 0) {
            return sql;
        }
        final StringBuilder sb = new StringBuilder();
        final String[] lines = sql.split(lineSeparator);
        for (String line : lines) {
            final int lineCommentIndex = line.indexOf("--");
            if (lineCommentIndex < 0) {
                sb.append(line).append(lineSeparator);
                continue;
            }
            final String lineComment = line.substring(lineCommentIndex);
            if (lineComment.contains("ELSE") || !lineComment.contains(bindCharacter)) {
                sb.append(line).append(lineSeparator);
                continue;
            }

            if (_log.isDebugEnabled()) {
                _log.debug("...Replacing bind character on line comment: " + lineComment);
            }
            final String filteredLineComment = replaceString(lineComment, bindCharacter, "Q");
            sb.append(line.substring(0, lineCommentIndex)).append(filteredLineComment).append(lineSeparator);
        }
        return sb.toString();
    }

    /**
     * Read outside-SQL without filter. Required attribute is 'outsideSqlPath'.
     * @param sqlFileEncoding The encoding of SQL file. (NotNull)
     * @param dbmsSuffix The suffix of DBMS. (NotNull)
     * @return The text of SQL. (NotNull)
     * @exception org.seasar.robot.dbflute.exception.OutsideSqlNotFoundException When the SQL is not found.
     */
    protected String readPlainOutsideSql(String sqlFileEncoding, String dbmsSuffix) {
        final String standardPath = _outsideSqlPath;
        final String dbmsPath = buildDbmsPath(standardPath, dbmsSuffix);
        if (_internalDebug && _log.isDebugEnabled()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("...Reading the outside-SQL: ").append(standardPath);
            sb.append(" {").append(sqlFileEncoding).append(", ").append(dbmsSuffix).append("}");
            _log.debug(sb.toString());
        }
        String sql;
        if (isExistResource(dbmsPath)) { // at first
            if (_internalDebug && _log.isDebugEnabled()) {
                _log.debug("Found the outside-SQL for the DBMS: " + dbmsPath);
            }
            sql = readText(dbmsPath, sqlFileEncoding);
        } else {
            final String resolvedSql = doReadOutsideSqlWithAliasSuffix(standardPath, sqlFileEncoding, dbmsSuffix);
            if (resolvedSql != null) {
                sql = resolvedSql;
            } else if (isExistResource(standardPath)) { // main
                sql = readText(standardPath, sqlFileEncoding);
            } else {
                throwOutsideSqlNotFoundException(standardPath);
                return null; // unreachable
            }
        }
        return removeInitialUnicodeBomIfNeeds(sqlFileEncoding, sql);
    }

    protected String doReadOutsideSqlWithAliasSuffix(String standardPath, String sqlFileEncoding, String dbmsSuffix) {
        String anotherPath = null;
        if ("_postgresql".equals(dbmsSuffix)) {
            anotherPath = buildDbmsPath(standardPath, "_postgre");
        } else if ("_sqlserver".equals(dbmsSuffix)) {
            anotherPath = buildDbmsPath(standardPath, "_mssql");
        }
        if (anotherPath != null && isExistResource(anotherPath)) { // patch for name difference
            return readText(anotherPath, sqlFileEncoding);
        } else {
            return null;
        }
    }

    protected String buildDbmsPath(String standardPath, String dbmsSuffix) {
        final String dbmsPath;
        final int lastIndexOfDot = standardPath.lastIndexOf(".");
        if (lastIndexOfDot >= 0 && !standardPath.substring(lastIndexOfDot).contains("/")) {
            final String base = standardPath.substring(0, lastIndexOfDot);
            dbmsPath = base + dbmsSuffix + standardPath.substring(lastIndexOfDot);
        } else {
            dbmsPath = standardPath + dbmsSuffix;
        }
        return dbmsPath;
    }

    protected String removeInitialUnicodeBomIfNeeds(String sqlFileEncoding, String sql) {
        if ("UTF-8".equalsIgnoreCase(sqlFileEncoding) && sql.length() > 0 && sql.charAt(0) == '\uFEFF') {
            sql = sql.substring(1);
        }
        return sql;
    }

    // ===================================================================================
    //                                                                 Behavior Query Path
    //                                                                 ===================
    public void setupBehaviorQueryPathIfNeeds() {
        if (!isBehaviorQueryPathEnabled()) {
            return;
        }
        if (_outsideSqlPath.contains(":")) {
            final String subDirectoryValue = _outsideSqlPath.substring(0, _outsideSqlPath.lastIndexOf(":"));
            final String subDirectoryPath = replaceString(subDirectoryValue, ":", "/");
            final String behaviorQueryPath = _outsideSqlPath.substring(_outsideSqlPath.lastIndexOf(":") + ":".length());
            final String behaviorClassPath = replaceString(buildBehaviorSqlPackageName(), ".", "/");
            final String behaviorPackagePath = behaviorClassPath.substring(0, behaviorClassPath.lastIndexOf("/"));
            final String behaviorClassName = behaviorClassPath.substring(behaviorClassPath.lastIndexOf("/")
                    + "/".length());
            _outsideSqlPath = behaviorPackagePath + "/" + subDirectoryPath + "/" + behaviorClassName + "_"
                    + behaviorQueryPath + ".sql";
        } else {
            _outsideSqlPath = replaceString(buildBehaviorSqlPackageName(), ".", "/") + "_" + _outsideSqlPath + ".sql";
        }
    }

    protected String buildBehaviorSqlPackageName() {
        final DBMeta dbmeta = _dbmetaProvider.provideDBMetaChecked(_tableDbName);
        final String behaviorTypeName = dbmeta.getBehaviorTypeName();
        final String outsideSqlPackage = _outsideSqlPackage;
        if (outsideSqlPackage != null && outsideSqlPackage.trim().length() > 0) {
            final String behaviorClassName = behaviorTypeName.substring(behaviorTypeName.lastIndexOf(".")
                    + ".".length());
            String tmp = behaviorTypeName.substring(0, behaviorTypeName.lastIndexOf("."));
            final String exbhvName = tmp.contains(".") ? tmp.substring(tmp.lastIndexOf(".") + ".".length()) : tmp;
            return outsideSqlPackage + "." + exbhvName + "." + behaviorClassName;
        } else {
            return behaviorTypeName;
        }
    }

    protected boolean isBehaviorQueryPathEnabled() {
        if (isProcedure()) { // [DBFlute-0.7.5]
            return false;
        }
        return _outsideSqlPath != null && !_outsideSqlPath.contains("/") && !_outsideSqlPath.contains(".")
                && _tableDbName != null;
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public boolean isSpecifiedOutsideSql() {
        return _outsideSqlPath != null;
    }

    // [DBFlute-0.7.5]
    public boolean isProcedure() {
        return _methodName != null && _methodName.startsWith("call");
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected boolean isExistResource(String path) {
        return DfResourceUtil.isExist(path);
    }

    protected String readText(final String path, String sqlFileEncoding) {
        final InputStream ins = DfResourceUtil.getResourceStream(path);
        Reader reader = null;
        try {
            reader = createInputStreamReader(ins, sqlFileEncoding);
            return readText(reader);
        } catch (IOException e) {
            final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
            br.addNotice("Failed to read the text for outside-SQL.");
            br.addItem("OutsideSql Path");
            br.addElement(path);
            br.addItem("SQL File Encoding");
            br.addElement(sqlFileEncoding);
            final String msg = br.buildExceptionMessage();
            throw new OutsideSqlReadFailureException(msg);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                    if (_internalDebug && _log.isDebugEnabled()) {
                        _log.debug("Failed to close the reader: path=" + path, ignored);
                    }
                }
            }
        }
    }

    protected Reader createInputStreamReader(InputStream ins, String encoding) throws IOException {
        return new InputStreamReader(ins, encoding);
    }

    public String readText(Reader reader) throws IOException {
        final StringBuilder sb = new StringBuilder(100);
        BufferedReader br = null;
        try {
            br = new BufferedReader(reader);
            final char[] buf = new char[8192];
            int n;
            while ((n = br.read(buf)) >= 0) {
                sb.append(buf, 0, n);
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
        }
        return sb.toString();
    }

    protected static String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    // -----------------------------------------------------
    //                                                 Basic
    //                                                 -----
    public String getOutsideSqlPath() {
        return _outsideSqlPath;
    }

    public void setOutsideSqlPath(String outsideSqlPath) {
        this._outsideSqlPath = outsideSqlPath;
    }

    public Object getParameterBean() {
        return _parameterBean;
    }

    public void setParameterBean(Object parameterBean) {
        this._parameterBean = parameterBean;
    }

    public Class<?> getResultType() {
        return _resultType;
    }

    public void setResultType(Class<?> resultType) {
        this._resultType = resultType;
    }

    public CursorHandler getCursorHandler() {
        return _cursorHandler;
    }

    public void setCursorHandler(CursorHandler handler) {
        _cursorHandler = handler;
    }

    public String getMethodName() {
        return _methodName;
    }

    public void setMethodName(String methodName) {
        this._methodName = methodName;
    }

    public StatementConfig getStatementConfig() {
        return _statementConfig;
    }

    public void setStatementConfig(StatementConfig statementConfig) {
        this._statementConfig = statementConfig;
    }

    public String getTableDbName() {
        return _tableDbName;
    }

    public void setTableDbName(String tableDbName) {
        this._tableDbName = tableDbName;
    }

    // -----------------------------------------------------
    //                                                Option
    //                                                ------
    public boolean isOffsetByCursorForcedly() {
        return _offsetByCursorForcedly;
    }

    public void setOffsetByCursorForcedly(boolean offsetByCursorForcedly) {
        this._offsetByCursorForcedly = offsetByCursorForcedly;
    }

    public boolean isLimitByCursorForcedly() {
        return _limitByCursorForcedly;
    }

    public void setLimitByCursorForcedly(boolean limitByCursorForcedly) {
        this._limitByCursorForcedly = limitByCursorForcedly;
    }

    public boolean isAutoPagingLogging() { // for logging
        return _autoPagingLogging;
    }

    public void setAutoPagingLogging(boolean autoPagingLogging) { // for logging
        this._autoPagingLogging = autoPagingLogging;
    }

    public OutsideSqlFilter getOutsideSqlFilter() {
        return _outsideSqlFilter;
    }

    public void setOutsideSqlFilter(OutsideSqlFilter outsideSqlFilter) {
        this._outsideSqlFilter = outsideSqlFilter;
    }

    public boolean isRemoveBlockComment() {
        return _removeBlockComment;
    }

    public void setRemoveBlockComment(boolean removeBlockComment) {
        this._removeBlockComment = removeBlockComment;
    }

    public boolean isRemoveLineComment() {
        return _removeLineComment;
    }

    public void setRemoveLineComment(boolean removeLineComment) {
        this._removeLineComment = removeLineComment;
    }

    public boolean isFormatSql() {
        return _formatSql;
    }

    public void setFormatSql(boolean formatSql) {
        this._formatSql = formatSql;
    }

    public boolean isInternalDebug() {
        return _internalDebug;
    }

    public void setInternalDebug(boolean internalDebug) {
        this._internalDebug = internalDebug;
    }
}
