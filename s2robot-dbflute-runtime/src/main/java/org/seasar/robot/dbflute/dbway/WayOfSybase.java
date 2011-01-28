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
 * The DB way of Sybase.
 * @author jflute
 */
public class WayOfSybase implements DBWay {

    // ===================================================================================
    //                                                                        Sequence Way
    //                                                                        ============
    public String buildSequenceNextValSql(String sequenceName) {
        return null;
    }

    // ===================================================================================
    //                                                                       Identity Info
    //                                                                       =============
    public String getIdentitySelectSql() {
        return "select @@identity";
    }

    public String buildIdentityDisableSql(String tableSqlName) {
        return buildIdentityOnOffSql(tableSqlName, true);
    }

    public String buildIdentityEnableSql(String tableSqlName) {
        return buildIdentityOnOffSql(tableSqlName, false);
    }

    protected String buildIdentityOnOffSql(String tableSqlName, boolean insertOn) {
        final String settingValue = (insertOn ? tableSqlName : "");
        return "set temporary option identity_insert = '" + settingValue + "'";
    }

    // ===================================================================================
    //                                                                         SQL Support
    //                                                                         ===========
    public boolean isBlockCommentSupported() {
        return true;
    }

    public boolean isLineCommentSupported() {
        return true;
    }

    // ===================================================================================
    //                                                                        JDBC Support
    //                                                                        ============
    public boolean isScrollableCursorSupported() {
        return true;
    }

    // ===================================================================================
    //                                                                   SQLException Info
    //                                                                   =================
    public boolean isUniqueConstraintException(String sqlState, Integer errorCode) {
        return "QGA03".equals(sqlState);
    }
}
