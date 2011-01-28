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

import java.util.HashMap;
import java.util.Map;

/**
 * The DB way of MySQL.
 * @author jflute
 */
public class WayOfMySQL implements DBWay {

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
        return "SELECT LAST_INSERT_ID()";
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
        return errorCode != null && errorCode == 1062;
    }

    // ===================================================================================
    //                                                                     ENUM Definition
    //                                                                     ===============
    public enum FullTextSearchModifier {
        InBooleanMode("IN BOOLEAN MODE"), InNaturalLanguageMode("IN NATURAL LANGUAGE MODE"), InNaturalLanguageModeWithQueryExpansion(
                "IN NATURAL LANGUAGE MODE WITH QUERY EXPANSION"), WithQueryExpansion("WITH QUERY EXPANSION");
        private static final Map<String, FullTextSearchModifier> _codeValueMap = new HashMap<String, FullTextSearchModifier>();
        static {
            for (FullTextSearchModifier value : values()) {
                _codeValueMap.put(value.code().toLowerCase(), value);
            }
        }
        private String _code;

        private FullTextSearchModifier(String code) {
            _code = code;
        }

        public String code() {
            return _code;
        }

        public static FullTextSearchModifier codeOf(Object code) {
            if (code == null) {
                return null;
            }
            return _codeValueMap.get(code.toString().toLowerCase());
        }
    }
}
