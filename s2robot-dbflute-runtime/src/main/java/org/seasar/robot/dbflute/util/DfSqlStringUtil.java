package org.seasar.robot.dbflute.util;

/**
 * @author jflute
 * @since 0.9.5 (2009/04/10 Friday)
 */
public abstract class DfSqlStringUtil {

    public static String removeBlockComment(final String sql) {
        if (sql == null) {
            return null;
        }
        final String beginMark = "/*";
        final String endMark = "*/";
        final StringBuilder sb = new StringBuilder();
        String tmp = sql;
        while (true) {
            if (tmp.indexOf(beginMark) < 0) {
                sb.append(tmp);
                break;
            }
            if (tmp.indexOf(endMark) < 0) {
                sb.append(tmp);
                break;
            }
            if (tmp.indexOf(beginMark) > tmp.indexOf(endMark)) {
                final int borderIndex = tmp.indexOf(endMark) + endMark.length();
                sb.append(tmp.substring(0, borderIndex));
                tmp = tmp.substring(borderIndex);
                continue;
            }
            sb.append(tmp.substring(0, tmp.indexOf(beginMark)));
            tmp = tmp.substring(tmp.indexOf(endMark) + endMark.length());
        }
        return sb.toString();
    }

    public static String removeLineComment(final String sql) { // With removing CR!
        if (sql == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        final String[] lines = sql.split("\n");
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            line = removeCR(line); // Remove CR!
            if (line.startsWith("--")) {
                continue;
            }
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    protected static String removeCR(String str) {
        if (str == null) {
            return null;
        }
        return str.replaceAll("\r", "");
    }
}