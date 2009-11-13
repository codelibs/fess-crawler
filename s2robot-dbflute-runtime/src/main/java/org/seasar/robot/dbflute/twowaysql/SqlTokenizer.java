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
package org.seasar.robot.dbflute.twowaysql;

import org.seasar.robot.dbflute.exception.CommentEndNotFoundException;
import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 */
public class SqlTokenizer {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    public static final int SQL = 1;
    public static final int COMMENT = 2;
    public static final int ELSE = 3;
    public static final int BIND_VARIABLE = 4;
    public static final int EOF = 99;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String sql;
    protected int position = 0;
    protected String token;
    protected int tokenType = SQL;
    protected int nextTokenType = SQL;
    protected int bindVariableNum = 0;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public SqlTokenizer(String sql) {
        this.sql = sql;
    }

    // ===================================================================================
    //                                                                            Tokenize
    //                                                                            ========
    public int next() {
        if (position >= sql.length()) {
            token = null;
            tokenType = EOF;
            nextTokenType = EOF;
            return tokenType;
        }
        switch (nextTokenType) {
        case SQL:
            parseSql();
            break;
        case COMMENT:
            parseComment();
            break;
        case ELSE:
            parseElse();
            break;
        case BIND_VARIABLE:
            parseBindVariable();
            break;
        default:
            parseEof();
            break;
        }
        return tokenType;
    }

    protected void parseSql() {
        int commentStartPos = sql.indexOf("/*", position);
        int commentStartPos2 = sql.indexOf("#*", position);
        if (0 < commentStartPos2 && commentStartPos2 < commentStartPos) {
            commentStartPos = commentStartPos2;
        }
        int lineCommentStartPos = sql.indexOf("--", position);
        int bindVariableStartPos = sql.indexOf("?", position);
        int elseCommentStartPos = -1;
        int elseCommentLength = -1;
        if (lineCommentStartPos >= 0) {
            int skipPos = skipWhitespace(lineCommentStartPos + 2);
            if (skipPos + 4 < sql.length() && "ELSE".equals(sql.substring(skipPos, skipPos + 4))) {
                elseCommentStartPos = lineCommentStartPos;
                elseCommentLength = skipPos + 4 - lineCommentStartPos;
            }
        }
        int nextStartPos = getNextStartPos(commentStartPos, elseCommentStartPos, bindVariableStartPos);
        if (nextStartPos < 0) {
            token = sql.substring(position);
            nextTokenType = EOF;
            position = sql.length();
            tokenType = SQL;
        } else {
            token = sql.substring(position, nextStartPos);
            tokenType = SQL;
            boolean needNext = nextStartPos == position;
            if (nextStartPos == commentStartPos) {
                nextTokenType = COMMENT;
                position = commentStartPos + 2;
            } else if (nextStartPos == elseCommentStartPos) {
                nextTokenType = ELSE;
                position = elseCommentStartPos + elseCommentLength;
            } else if (nextStartPos == bindVariableStartPos) {
                nextTokenType = BIND_VARIABLE;
                position = bindVariableStartPos;
            }
            if (needNext) {
                next();
            }
        }
    }

    protected int getNextStartPos(int commentStartPos, int elseCommentStartPos, int bindVariableStartPos) {
        int nextStartPos = -1;
        if (commentStartPos >= 0) {
            nextStartPos = commentStartPos;
        }
        if (elseCommentStartPos >= 0 && (nextStartPos < 0 || elseCommentStartPos < nextStartPos)) {
            nextStartPos = elseCommentStartPos;
        }
        if (bindVariableStartPos >= 0 && (nextStartPos < 0 || bindVariableStartPos < nextStartPos)) {
            nextStartPos = bindVariableStartPos;
        }
        return nextStartPos;
    }

    protected String nextBindVariableName() {
        return "$" + ++bindVariableNum;
    }

    protected void parseComment() {
        int commentEndPos = sql.indexOf("*/", position);
        int commentEndPos2 = sql.indexOf("*#", position);
        if (0 < commentEndPos2 && commentEndPos2 < commentEndPos) {
            commentEndPos = commentEndPos2;
        }
        if (commentEndPos < 0) {
            throwEndCommentNotFoundException(sql.substring(position));
        }
        token = sql.substring(position, commentEndPos);
        nextTokenType = SQL;
        position = commentEndPos + 2;
        tokenType = COMMENT;
    }

    protected void throwEndCommentNotFoundException(String expression) {
        String msg = "Look! Read the message below." + ln();
        msg = msg + "/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *" + ln();
        msg = msg + "The comment end was NOT found!" + ln();
        msg = msg + ln();
        msg = msg + "[Advice]" + ln();
        msg = msg + "Please confirm the SQL comment writing." + ln();
        msg = msg + "It may exist the comment that DOESN'T have a comment end." + ln();
        msg = msg + "  For example:" + ln();
        msg = msg + "    before (x) -- /*pmb.xxxId3" + ln();
        msg = msg + "    after  (o) -- /*pmb.xxxId*/3" + ln();
        msg = msg + ln();
        msg = msg + "[Comment End Expected Place]" + ln() + expression + ln();
        msg = msg + ln();
        msg = msg + "[Specified SQL]" + ln() + sql + ln();
        msg = msg + "* * * * * * * * * */";
        throw new CommentEndNotFoundException(msg);
    }

    protected void parseBindVariable() {
        token = nextBindVariableName();
        nextTokenType = SQL;
        position += 1;
        tokenType = BIND_VARIABLE;
    }

    protected void parseElse() {
        token = null;
        nextTokenType = SQL;
        tokenType = ELSE;
    }

    protected void parseEof() {
        token = null;
        tokenType = EOF;
        nextTokenType = EOF;
    }

    public String skipToken() {
        return skipToken(false);
    }

    public String skipToken(boolean testValue) {
        int index = sql.length(); // last index as default

        final String dateLiteralPrefix = extractDateLiteralPrefix(testValue, sql, position);
        if (dateLiteralPrefix != null) {
            position = position + dateLiteralPrefix.length();
        }

        final char quote;
        {
            final char firstChar = (position < sql.length() ? sql.charAt(position) : '\0');
            quote = (firstChar == '(' ? ')' : firstChar);
        }
        final boolean quoting = quote == '\'' || quote == ')';

        for (int i = quoting ? position + 1 : position; i < sql.length(); ++i) {
            final char c = sql.charAt(i);
            if (isNotQuoteEndPoint(quoting, c)) {
                index = i;
                break;
            } else if (isBlockCommentBeginPoint(sql, c, i)) {
                index = i;
                break;
            } else if (isLineCommentBeginPoint(sql, c, i)) {
                index = i;
                break;
            } else if (quoting && isSingleQuoteEndPoint(sql, quote, c, i)) {
                index = i + 1;
                break;
            } else if (quoting && isQuoteEndPoint(sql, quote, c, i)) {
                index = i + 1;
                break;
            }
        }
        token = sql.substring(position, index);
        if (dateLiteralPrefix != null) {
            token = dateLiteralPrefix + token;
        }
        tokenType = SQL;
        nextTokenType = SQL;
        position = index;
        return token;
    }

    protected String extractDateLiteralPrefix(boolean testValue, String currentSql, int position) {
        if (!testValue) {
            return null;
        }
        if (position >= currentSql.length()) {
            return null;
        }
        final char firstChar = currentSql.charAt(position);
        if (firstChar != 'd' && firstChar != 'D' && firstChar != 't' && firstChar != 'T') {
            return null;
        }
        final String rear;
        {
            final String tmpRear = currentSql.substring(position);
            final int maxlength = "timestamp '".length();
            if (tmpRear.length() > maxlength) {
                // get only the quantity needed for performance
                rear = tmpRear.substring(0, maxlength);
            } else {
                rear = tmpRear;
            }
        }
        final String lowerRear = rear.toLowerCase();
        String literalPrefix = null;
        if (lowerRear.startsWith("date '")) {
            literalPrefix = rear.substring(0, "date ".length());
        } else if (lowerRear.startsWith("date'")) {
            literalPrefix = rear.substring(0, "date".length());
        } else if (lowerRear.startsWith("timestamp '")) { // has max length
            literalPrefix = rear.substring(0, "timestamp ".length());
        } else if (lowerRear.startsWith("timestamp'")) {
            literalPrefix = rear.substring(0, "timestamp".length());
        }
        return literalPrefix;
    }

    protected boolean isNotQuoteEndPoint(boolean quoting, char c) {
        return !quoting && (Character.isWhitespace(c) || c == ',' || c == ')' || c == '(');
    }

    protected boolean isBlockCommentBeginPoint(String currentSql, char c, int i) {
        return c == '/' && isNextCharacter(currentSql, i, '*');
    }

    protected boolean isLineCommentBeginPoint(String currentSql, char c, int i) {
        return c == '-' && isNextCharacter(currentSql, i, '-');
    }

    protected boolean isSingleQuoteEndPoint(String currentSql, char quote, char c, int i) {
        final int sqlLen = currentSql.length();
        final boolean endSqlOrNotEscapeQuote = (i + 1 >= sqlLen || currentSql.charAt(i + 1) != '\'');
        return quote == '\'' && c == '\'' && endSqlOrNotEscapeQuote;
    }

    protected boolean isQuoteEndPoint(String currentSql, char quote, char c, int i) {
        return c == quote;
    }

    protected boolean isNextCharacter(String currentSql, int i, char targetChar) {
        return i + 1 < currentSql.length() && currentSql.charAt(i + 1) == targetChar;
    }

    public String skipWhitespace() {
        int index = skipWhitespace(position);
        token = sql.substring(position, index);
        position = index;
        return token;
    }

    protected int skipWhitespace(int position) {
        int index = sql.length();
        for (int i = position; i < sql.length(); ++i) {
            char c = sql.charAt(i);
            if (!Character.isWhitespace(c)) {
                index = i;
                break;
            }
        }
        return index;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public int getPosition() {
        return position;
    }

    public String getToken() {
        return token;
    }

    public String getBefore() {
        return sql.substring(0, position);
    }

    public String getAfter() {
        return sql.substring(position);
    }

    public int getTokenType() {
        return tokenType;
    }

    public int getNextTokenType() {
        return nextTokenType;
    }
}
