package org.codelibs.fess.crawler.db.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.dbflute.Entity;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.*;
import org.dbflute.dbmeta.name.*;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;
import org.codelibs.fess.crawler.db.allcommon.*;
import org.codelibs.fess.crawler.db.exentity.*;

/**
 * The DB meta of ACCESS_RESULT. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final AccessResultDbm _instance = new AccessResultDbm();
    private AccessResultDbm() {}
    public static AccessResultDbm getInstance() { return _instance; }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    public String getProjectName() { return DBCurrent.getInstance().projectName(); }
    public String getProjectPrefix() { return DBCurrent.getInstance().projectPrefix(); }
    public String getGenerationGapBasePrefix() { return DBCurrent.getInstance().generationGapBasePrefix(); }
    public DBDef getCurrentDBDef() { return DBCurrent.getInstance().currentDBDef(); }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    { xsetupEpg(); }
    protected void xsetupEpg() {
        setupEpg(_epgMap, et -> ((AccessResult)et).getId(), (et, vl) -> ((AccessResult)et).setId(ctl(vl)), "id");
        setupEpg(_epgMap, et -> ((AccessResult)et).getSessionId(), (et, vl) -> ((AccessResult)et).setSessionId((String)vl), "sessionId");
        setupEpg(_epgMap, et -> ((AccessResult)et).getRuleId(), (et, vl) -> ((AccessResult)et).setRuleId((String)vl), "ruleId");
        setupEpg(_epgMap, et -> ((AccessResult)et).getUrl(), (et, vl) -> ((AccessResult)et).setUrl((String)vl), "url");
        setupEpg(_epgMap, et -> ((AccessResult)et).getParentUrl(), (et, vl) -> ((AccessResult)et).setParentUrl((String)vl), "parentUrl");
        setupEpg(_epgMap, et -> ((AccessResult)et).getStatus(), (et, vl) -> ((AccessResult)et).setStatus(cti(vl)), "status");
        setupEpg(_epgMap, et -> ((AccessResult)et).getHttpStatusCode(), (et, vl) -> ((AccessResult)et).setHttpStatusCode(cti(vl)), "httpStatusCode");
        setupEpg(_epgMap, et -> ((AccessResult)et).getMethod(), (et, vl) -> ((AccessResult)et).setMethod((String)vl), "method");
        setupEpg(_epgMap, et -> ((AccessResult)et).getMimeType(), (et, vl) -> ((AccessResult)et).setMimeType((String)vl), "mimeType");
        setupEpg(_epgMap, et -> ((AccessResult)et).getContentLength(), (et, vl) -> ((AccessResult)et).setContentLength(ctl(vl)), "contentLength");
        setupEpg(_epgMap, et -> ((AccessResult)et).getExecutionTime(), (et, vl) -> ((AccessResult)et).setExecutionTime(cti(vl)), "executionTime");
        setupEpg(_epgMap, et -> ((AccessResult)et).getLastModified(), (et, vl) -> ((AccessResult)et).setLastModified(ctl(vl)), "lastModified");
        setupEpg(_epgMap, et -> ((AccessResult)et).getCreateTime(), (et, vl) -> ((AccessResult)et).setCreateTime(ctl(vl)), "createTime");
    }
    public PropertyGateway findPropertyGateway(String prop)
    { return doFindEpg(_epgMap, prop); }

    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    protected final Map<String, PropertyGateway> _efpgMap = newHashMap();
    { xsetupEfpg(); }
    @SuppressWarnings("unchecked")
    protected void xsetupEfpg() {
        setupEfpg(_efpgMap, et -> ((AccessResult)et).getAccessResultDataAsOne(), (et, vl) -> ((AccessResult)et).setAccessResultDataAsOne((OptionalEntity<AccessResultData>)vl), "accessResultDataAsOne");
    }
    public PropertyGateway findForeignPropertyGateway(String prop)
    { return doFindEfpg(_efpgMap, prop); }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "ACCESS_RESULT";
    protected final String _tableDispName = "ACCESS_RESULT";
    protected final String _tablePropertyName = "accessResult";
    protected final TableSqlName _tableSqlName = new TableSqlName("ACCESS_RESULT", _tableDbName);
    { _tableSqlName.xacceptFilter(DBFluteConfig.getInstance().getTableSqlNameFilter()); }
    public String getTableDbName() { return _tableDbName; }
    public String getTableDispName() { return _tableDispName; }
    public String getTablePropertyName() { return _tablePropertyName; }
    public TableSqlName getTableSqlName() { return _tableSqlName; }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci("ID", "ID", null, null, Long.class, "id", null, true, true, true, "BIGINT", 19, 0, "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_D2116996_764A_448C_95EA_06F37E6C86A8", false, null, null, null, "", null, false);
    protected final ColumnInfo _columnSessionId = cci("SESSION_ID", "SESSION_ID", null, null, String.class, "sessionId", null, false, false, true, "VARCHAR", 20, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnRuleId = cci("RULE_ID", "RULE_ID", null, null, String.class, "ruleId", null, false, false, false, "VARCHAR", 20, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null, String.class, "url", null, false, false, true, "VARCHAR", 65536, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnParentUrl = cci("PARENT_URL", "PARENT_URL", null, null, String.class, "parentUrl", null, false, false, false, "VARCHAR", 65536, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnStatus = cci("STATUS", "STATUS", null, null, Integer.class, "status", null, false, false, true, "INTEGER", 10, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnHttpStatusCode = cci("HTTP_STATUS_CODE", "HTTP_STATUS_CODE", null, null, Integer.class, "httpStatusCode", null, false, false, true, "INTEGER", 10, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMethod = cci("METHOD", "METHOD", null, null, String.class, "method", null, false, false, true, "VARCHAR", 10, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnMimeType = cci("MIME_TYPE", "MIME_TYPE", null, null, String.class, "mimeType", null, false, false, true, "VARCHAR", 100, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnContentLength = cci("CONTENT_LENGTH", "CONTENT_LENGTH", null, null, Long.class, "contentLength", null, false, false, true, "BIGINT", 19, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnExecutionTime = cci("EXECUTION_TIME", "EXECUTION_TIME", null, null, Integer.class, "executionTime", null, false, false, true, "INTEGER", 10, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnLastModified = cci("LAST_MODIFIED", "LAST_MODIFIED", null, null, Long.class, "lastModified", null, false, false, false, "BIGINT", 19, 0, null, false, null, null, null, null, null, false);
    protected final ColumnInfo _columnCreateTime = cci("CREATE_TIME", "CREATE_TIME", null, null, Long.class, "createTime", null, false, false, true, "BIGINT", 19, 0, null, false, null, null, null, null, null, false);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() { return _columnId; }
    /**
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSessionId() { return _columnSessionId; }
    /**
     * RULE_ID: {VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnRuleId() { return _columnRuleId; }
    /**
     * URL: {IX+, NotNull, VARCHAR(65536)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() { return _columnUrl; }
    /**
     * PARENT_URL: {VARCHAR(65536)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnParentUrl() { return _columnParentUrl; }
    /**
     * STATUS: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnStatus() { return _columnStatus; }
    /**
     * HTTP_STATUS_CODE: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnHttpStatusCode() { return _columnHttpStatusCode; }
    /**
     * METHOD: {NotNull, VARCHAR(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMethod() { return _columnMethod; }
    /**
     * MIME_TYPE: {NotNull, VARCHAR(100)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnMimeType() { return _columnMimeType; }
    /**
     * CONTENT_LENGTH: {NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnContentLength() { return _columnContentLength; }
    /**
     * EXECUTION_TIME: {NotNull, INTEGER(10)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnExecutionTime() { return _columnExecutionTime; }
    /**
     * LAST_MODIFIED: {BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnLastModified() { return _columnLastModified; }
    /**
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreateTime() { return _columnCreateTime; }

    protected List<ColumnInfo> ccil() {
        List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSessionId());
        ls.add(columnRuleId());
        ls.add(columnUrl());
        ls.add(columnParentUrl());
        ls.add(columnStatus());
        ls.add(columnHttpStatusCode());
        ls.add(columnMethod());
        ls.add(columnMimeType());
        ls.add(columnContentLength());
        ls.add(columnExecutionTime());
        ls.add(columnLastModified());
        ls.add(columnCreateTime());
        return ls;
    }

    { initializeInformationResource(); }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    protected UniqueInfo cpui() { return hpcpui(columnId()); }
    public boolean hasPrimaryKey() { return true; }
    public boolean hasCompoundPrimaryKey() { return false; }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // cannot cache because it uses related DB meta instance while booting
    // (instead, cached by super's collection)
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------
    /**
     * ACCESS_RESULT_DATA by ID, named 'accessResultDataAsOne'.
     * @return The information object of foreign property(referrer-as-one). (NotNull)
     */
    public ForeignInfo foreignAccessResultDataAsOne() {
        Map<ColumnInfo, ColumnInfo> mp = newLinkedHashMap(columnId(), AccessResultDataDbm.getInstance().columnId());
        return cfi("CONSTRAINT_13", "accessResultDataAsOne", this, AccessResultDataDbm.getInstance(), mp, 0, org.dbflute.optional.OptionalEntity.class, true, false, true, false, null, null, false, "accessResult", false);
    }

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    public boolean hasIdentity() { return true; }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    public String getEntityTypeName() { return "org.codelibs.fess.crawler.db.exentity.AccessResult"; }
    public String getConditionBeanTypeName() { return "org.codelibs.fess.crawler.db.cbean.AccessResultCB"; }
    public String getBehaviorTypeName() { return "org.codelibs.fess.crawler.db.exbhv.AccessResultBhv"; }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    public Class<AccessResult> getEntityType() { return AccessResult.class; }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    public AccessResult newEntity() { return new AccessResult(); }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    public void acceptPrimaryKeyMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptPrimaryKeyMap((AccessResult)et, mp); }
    public void acceptAllColumnMap(Entity et, Map<String, ? extends Object> mp)
    { doAcceptAllColumnMap((AccessResult)et, mp); }
    public Map<String, Object> extractPrimaryKeyMap(Entity et) { return doExtractPrimaryKeyMap(et); }
    public Map<String, Object> extractAllColumnMap(Entity et) { return doExtractAllColumnMap(et); }
}
