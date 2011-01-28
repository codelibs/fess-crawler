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
package org.seasar.robot.dbflute;

import java.util.HashMap;
import java.util.Map;

import org.seasar.robot.dbflute.dbway.DBWay;
import org.seasar.robot.dbflute.dbway.WayOfDB2;
import org.seasar.robot.dbflute.dbway.WayOfDerby;
import org.seasar.robot.dbflute.dbway.WayOfFirebird;
import org.seasar.robot.dbflute.dbway.WayOfH2;
import org.seasar.robot.dbflute.dbway.WayOfMSAccess;
import org.seasar.robot.dbflute.dbway.WayOfMySQL;
import org.seasar.robot.dbflute.dbway.WayOfOracle;
import org.seasar.robot.dbflute.dbway.WayOfPostgreSQL;
import org.seasar.robot.dbflute.dbway.WayOfSQLServer;
import org.seasar.robot.dbflute.dbway.WayOfSQLite;
import org.seasar.robot.dbflute.dbway.WayOfSybase;
import org.seasar.robot.dbflute.dbway.WayOfUnknown;

/**
 * The definition of database.
 * @author jflute
 */
public enum DBDef {

    // ===================================================================================
    //                                                                                ENUM
    //                                                                                ====
    MySQL("mysql", null, new WayOfMySQL()) // supported
    , PostgreSQL("postgresql", "postgre", new WayOfPostgreSQL()) // supported
    , Oracle("oracle", null, new WayOfOracle()) // supported
    , DB2("db2", null, new WayOfDB2()) // supported
    , SQLServer("sqlserver", "mssql", new WayOfSQLServer()) // supported
    , H2("h2", null, new WayOfH2()) // supported
    , Derby("derby", null, new WayOfDerby()) // supported
    , SQLite("sqlite", null, new WayOfSQLite()) // sub supported
    , MSAccess("msaccess", null, new WayOfMSAccess()) // sub supported
    , FireBird("firebird", null, new WayOfFirebird()) // a-little-bit supported
    , Sybase("sybase", null, new WayOfSybase()) // a-little-bit supported
    , Unknown("unknown", null, new WayOfUnknown());

    // ===================================================================================
    //                                                                    Static Reference
    //                                                                    ================
    // -----------------------------------------------------
    //                                            Code Value
    //                                            ----------
    private static final Map<String, DBDef> _codeValueMap = new HashMap<String, DBDef>();
    static {
        for (DBDef value : values()) {
            _codeValueMap.put(value.code().toLowerCase(), value);
        }
    }
    private static final Map<String, DBDef> _codeAliasValueMap = new HashMap<String, DBDef>();
    static {
        for (DBDef value : values()) {
            if (value.codeAlias() != null) {
                _codeAliasValueMap.put(value.codeAlias().toLowerCase(), value);
            }
        }
    }

    /**
     * @param code The code of the DB. (NullAllowed: If the code is null, it returns null)
     * @return The instance that has the code. (NullAllowed)
     */
    public static DBDef codeOf(String code) {
        if (code == null) {
            return null;
        }
        final String lowerCaseCode = code.toLowerCase();
        DBDef def = _codeValueMap.get(lowerCaseCode);
        if (def == null) {
            def = _codeAliasValueMap.get(lowerCaseCode);
        }
        return def;
    }

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** The code of the DB. (NotNull) */
    private String _code;

    /** The code alias of the DB. (NullAllowed) */
    private String _codeAlias;

    /** The way of the DB. (NotNull) */
    private DBWay _dbway;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param code The code of the DB. (NotNull)
     * @param codeAlias The code alias of the DB. (NullAllowed)
     */
    private DBDef(String code, String codeAlias, DBWay dbway) {
        _code = code;
        _codeAlias = codeAlias;
        _dbway = dbway;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * @return The code of the DB. (NotNull)
     */
    public String code() {
        return _code;
    }

    /**
     * @return The code alias of the DB. (NullAllowed)
     */
    private String codeAlias() {
        return _codeAlias;
    }

    /**
     * @return The way of the DB. (NotNull)
     */
    public DBWay dbway() {
        return _dbway;
    }
}
