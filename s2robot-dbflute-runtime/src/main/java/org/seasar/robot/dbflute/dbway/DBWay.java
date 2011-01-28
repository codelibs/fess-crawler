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
package org.seasar.robot.dbflute.dbway;

/**
 * The interface of DB way.
 * @author jflute
 */
public interface DBWay {

    // ===================================================================================
    //                                                                        Sequence Way
    //                                                                        ============
    /**
     * Build the SQL for next value of sequence with a sequence name.
     * @param sequenceName The sequence name. (NotNull)
     * @return The SQL for next value of sequence. (NullAllowed: if sequence is unsupported)
     */
    String buildSequenceNextValSql(String sequenceName);

    // ===================================================================================
    //                                                                        Identity Way
    //                                                                        ============
    /**
     * Get the SQL for getting inserted value of identity.
     * @return The SQL for getting inserted value of sequence. (NullAllowed: If it does not have identity, returns null.)
     */
    String getIdentitySelectSql();

    // ===================================================================================
    //                                                                         SQL Support
    //                                                                         ===========
    boolean isBlockCommentSupported();

    boolean isLineCommentSupported();

    // ===================================================================================
    //                                                                        JDBC Support
    //                                                                        ============
    boolean isScrollableCursorSupported();

    // ===================================================================================
    //                                                                   SQLException Info
    //                                                                   =================
    /**
     * Is the SQLException from unique constraint? {Use both SQLState and ErrorCode}
     * @param sqlState SQLState of the SQLException. (NullAllowed)
     * @param errorCode ErrorCode of the SQLException. (NullAllowed)
     * @return Is the SQLException from unique constraint?
     */
    boolean isUniqueConstraintException(String sqlState, Integer errorCode);
}
