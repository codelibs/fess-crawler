package org.seasar.robot.dbflute.cbean.sqlclause.subquery;

import java.io.Serializable;

import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public class SubQueryIndentProcessor implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    public static final String BEGIN_MARK_PREFIX = "--#df:sqbegin#";
    public static final String END_MARK_PREFIX = "--#df:sqend#";
    public static final String IDENTITY_TERMINAL = "#df:idterm#";

    // ===================================================================================
    //                                                                    Resolve Identity
    //                                                                    ================
    public String resolveSubQueryBeginMark(String subQueryIdentity) {
        return BEGIN_MARK_PREFIX + subQueryIdentity + IDENTITY_TERMINAL;
    }

    public String resolveSubQueryEndMark(String subQueryIdentity) {
        return END_MARK_PREFIX + subQueryIdentity + IDENTITY_TERMINAL;
    }

    // ===================================================================================
    //                                                                      Process Indent
    //                                                                      ==============
    public String processSubQueryIndent(final String sql, final String preIndent, final String originalSql) {
        final String beginMarkPrefix = BEGIN_MARK_PREFIX;
        if (!sql.contains(beginMarkPrefix)) {
            return sql;
        }
        final String[] lines = sql.split(ln());
        final String endMarkPrefix = END_MARK_PREFIX;
        final String identityTerminal = IDENTITY_TERMINAL;
        final int terminalLength = identityTerminal.length();
        final StringBuilder mainSb = new StringBuilder();
        StringBuilder subSb = null;
        boolean throughBegin = false;
        boolean throughBeginFirst = false;
        String subQueryIdentity = null;
        String indent = null;
        String preRemainder = null;
        for (String line : lines) {
            final boolean existsPreRemainder = Srl.is_NotNull_and_NotTrimmedEmpty(preRemainder);
            if (existsPreRemainder) {
                line = preRemainder + ln() + (indent != null ? indent : "") + line;
                preRemainder = null;
            }
            if (!throughBegin) {
                if (line.contains(beginMarkPrefix)) {
                    throughBegin = true;
                    subSb = new StringBuilder();
                    final int markIndex = line.indexOf(beginMarkPrefix);
                    final int terminalIndex = line.indexOf(identityTerminal);
                    if (terminalIndex < 0) {
                        String msg = "Identity terminal was not found at the begin line: [" + line + "]";
                        throw new SubQueryIndentFailureException(msg);
                    }
                    final String clause = line.substring(0, markIndex) + line.substring(terminalIndex + terminalLength);
                    subQueryIdentity = line.substring(markIndex + beginMarkPrefix.length(), terminalIndex);
                    subSb.append(clause);
                    if (existsPreRemainder) {
                        subSb.append(ln());
                        throughBeginFirst = true;
                        indent = buildSpaceBar(indent.length() - preIndent.length());
                    } else {
                        indent = buildSpaceBar(markIndex - preIndent.length());
                    }
                } else {
                    mainSb.append(line).append(ln());
                }
            } else {
                // - - - - - - - -
                // In begin to end
                // - - - - - - - -
                if (line.contains(endMarkPrefix + subQueryIdentity)) { // the end
                    final int markIndex = line.indexOf(endMarkPrefix);
                    final int terminalIndex = line.indexOf(identityTerminal);
                    if (terminalIndex < 0) {
                        String msg = "Identity terminal was not found at the begin line: [" + line + "]";
                        throw new SubQueryIndentFailureException(msg);
                    }
                    final String clause = line.substring(0, markIndex);
                    preRemainder = line.substring(terminalIndex + terminalLength);
                    subSb.append(clause).append(Srl.is_Null_or_TrimmedEmpty(preRemainder) ? ln() : "");
                    final String currentSql = processSubQueryIndent(subSb.toString(), preIndent + indent, originalSql);
                    mainSb.append(currentSql);
                    throughBegin = false;
                    throughBeginFirst = false;
                } else {
                    if (!throughBeginFirst) {
                        subSb.append(line.trim()).append(ln());
                        throughBeginFirst = true;
                    } else {
                        subSb.append(indent).append(line).append(ln());
                    }
                }
            }
        }
        if (Srl.is_NotNull_and_NotTrimmedEmpty(preRemainder)) {
            mainSb.append(preRemainder);
        }
        final String filteredSql = Srl.rtrim(mainSb.toString()); // removed latest line separator

        if (throughBegin) {
            throwSubQueryNotFoundEndMarkException(subQueryIdentity, sql, filteredSql, originalSql);
        }
        if (filteredSql.contains(beginMarkPrefix)) {
            throwSubQueryAnyBeginMarkNotHandledException(subQueryIdentity, sql, filteredSql, originalSql);
        }
        return filteredSql;
    }

    protected void throwSubQueryNotFoundEndMarkException(String subQueryIdentity, String sql, String filteredSql,
            String originalSql) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Not found the end mark for sub-query.");
        br.addItem("SubQueryIdentity");
        br.addElement(subQueryIdentity);
        br.addItem("Before Filter");
        br.addElement(sql);
        br.addItem("After Filter");
        br.addElement(filteredSql);
        br.addItem("Original SQL");
        br.addElement(originalSql);
        final String msg = br.buildExceptionMessage();
        throw new SubQueryIndentFailureException(msg);
    }

    protected void throwSubQueryAnyBeginMarkNotHandledException(String subQueryIdentity, String sql,
            String filteredSql, String originalSql) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice("Any begin marks are not handled.");
        br.addItem("SubQueryIdentity");
        br.addElement(subQueryIdentity);
        br.addItem("Before Filter");
        br.addElement(sql);
        br.addItem("After Filter");
        br.addElement(filteredSql);
        br.addItem("Original SQL");
        br.addElement(originalSql);
        final String msg = br.buildExceptionMessage();
        throw new SubQueryIndentFailureException(msg);
    }

    public static class SubQueryIndentFailureException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public SubQueryIndentFailureException(String msg) {
            super(msg);
        }
    }

    // ===================================================================================
    //                                                                        Space Helper
    //                                                                        ============
    protected String buildSpaceBar(int size) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    // ===================================================================================
    //                                                                       Determination
    //                                                                       =============
    public static boolean hasSubQueryBeginOnFirstLine(String exp) {
        final String sqbegin = BEGIN_MARK_PREFIX;
        if (exp.contains(ln())) {
            final String firstLine = Srl.substringFirstFront(exp, ln());
            if (firstLine.contains(sqbegin)) {
                return true; // a first line has sub-query end mark
            }
        }
        return false;
    }

    public static boolean hasSubQueryEndOnLastLine(String exp) {
        final String sqend = END_MARK_PREFIX;
        if (exp.contains(ln())) {
            final String lastLine = Srl.substringLastRear(exp, ln());
            if (lastLine.contains(sqend)) {
                return true; // a last line has sub-query end mark
            }
        }
        return false;
    }

    public static String insertSubQueryEndOnLastLine(String exp, String inserted) {
        final String sqend = END_MARK_PREFIX;
        final String front = Srl.substringLastFront(exp, sqend);
        final String rear = Srl.substringLastRear(exp, sqend);
        return front + inserted + sqend + rear;
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected static String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
