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
package org.seasar.robot.db.bsbhv.cursor;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.seasar.robot.dbflute.jdbc.CursorHandler;
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
    protected AccessResultDiffCursor createTypeSafeCursor(ResultSet rs) throws SQLException {
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
    abstract protected Object fetchCursor(AccessResultDiffCursor cursor) throws SQLException;
}
