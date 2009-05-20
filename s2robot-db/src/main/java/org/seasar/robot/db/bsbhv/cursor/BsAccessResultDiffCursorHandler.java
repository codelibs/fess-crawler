package org.seasar.robot.db.bsbhv.cursor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.dbflute.jdbc.CursorHandler;
import org.seasar.robot.db.exbhv.cursor.AccessResultDiffCursor;

/**
 * The cursor handler of AccessResultDiff.
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResultDiffCursorHandler implements CursorHandler {

    /**
     * Handle.
     * @param rs Result set. (NotNull)
     * @return Result. (Nullable)
     * @throws java.sql.SQLException
     */
    public Object handle(java.sql.ResultSet rs) throws SQLException {
        return fetchCursor(createTypeSafeCursor(rs));
    }

    /**
     * Create type safe cursor.
     * @param rs Result set. (NotNull)
     * @return Type safe cursor. (Nullable)
     * @throws java.sql.SQLException
     */
    protected AccessResultDiffCursor createTypeSafeCursor(ResultSet rs)
            throws SQLException {
        final AccessResultDiffCursor cursor = new AccessResultDiffCursor();
        cursor.accept(rs);
        return cursor;
    }

    /**
     * Fetch cursor.
     * @param cursor Type safe cursor. (NotNull)
     * @return Result. (Nullable)
     * @throws java.sql.SQLException
     */
    abstract protected Object fetchCursor(AccessResultDiffCursor cursor)
            throws SQLException;
}
