package org.codelibs.robot.db.bsbhv.cursor;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.codelibs.robot.db.exbhv.cursor.AccessResultDiffCursor;
import org.dbflute.jdbc.CursorHandler;

/**
 * The cursor handler of AccessResultDiff.
 * @author DBFlute(AutoGenerator)
 */
public abstract class BsAccessResultDiffCursorHandler implements CursorHandler {

    /**
     * Handle the cursor.
     * @param rs The cursor (result set) for the query, which has first pointer. (NotNull)
     * @return The result object of handling process. (NullAllowed)
     * @throws SQLException When it fails to handle the SQL.
     */
    @Override
    public Object handle(final ResultSet rs) throws SQLException {
        return fetchCursor(createTypeSafeCursor(rs));
    }

    /**
     * Create the type-safe cursor.
     * @param rs The cursor (result set) for the query, which has first pointer. (NotNull)
     * @return The created type-safe cursor. (NotNull)
     * @throws SQLException When it fails to handle the SQL.
     */
    protected AccessResultDiffCursor createTypeSafeCursor(final ResultSet rs)
            throws SQLException {
        final AccessResultDiffCursor cursor = new AccessResultDiffCursor();
        cursor.accept(rs);
        return cursor;
    }

    /**
     * Fetch the cursor.
     * @param cursor The type-safe cursor for the query, which has first pointer. (NotNull)
     * @return The result object of handling process. (NullAllowed)
     * @throws SQLException When it fails to handle the SQL.
     */
    protected abstract Object fetchCursor(AccessResultDiffCursor cursor)
            throws SQLException;
}
