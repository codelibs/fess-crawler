package org.codelibs.robot.db.bsentity.dbmeta;

import java.util.List;
import java.util.Map;

import org.codelibs.robot.db.allcommon.DBCurrent;
import org.codelibs.robot.db.allcommon.DBFluteConfig;
import org.codelibs.robot.db.exentity.UrlFilter;
import org.dbflute.Entity;
import org.dbflute.dbmeta.AbstractDBMeta;
import org.dbflute.dbmeta.info.ColumnInfo;
import org.dbflute.dbmeta.info.UniqueInfo;
import org.dbflute.dbmeta.name.TableSqlName;
import org.dbflute.dbmeta.property.PropertyGateway;
import org.dbflute.dbway.DBDef;

/**
 * The DB meta of URL_FILTER. (Singleton)
 * @author DBFlute(AutoGenerator)
 */
public class UrlFilterDbm extends AbstractDBMeta {

    // ===================================================================================
    //                                                                           Singleton
    //                                                                           =========
    private static final UrlFilterDbm _instance = new UrlFilterDbm();

    private UrlFilterDbm() {
    }

    public static UrlFilterDbm getInstance() {
        return _instance;
    }

    // ===================================================================================
    //                                                                       Current DBDef
    //                                                                       =============
    @Override
    public String getProjectName() {
        return DBCurrent.getInstance().projectName();
    }

    @Override
    public String getProjectPrefix() {
        return DBCurrent.getInstance().projectPrefix();
    }

    @Override
    public String getGenerationGapBasePrefix() {
        return DBCurrent.getInstance().generationGapBasePrefix();
    }

    @Override
    public DBDef getCurrentDBDef() {
        return DBCurrent.getInstance().currentDBDef();
    }

    // ===================================================================================
    //                                                                    Property Gateway
    //                                                                    ================
    // -----------------------------------------------------
    //                                       Column Property
    //                                       ---------------
    protected final Map<String, PropertyGateway> _epgMap = newHashMap();
    {
        xsetupEpg();
    }

    protected void xsetupEpg() {
        setupEpg(_epgMap, et -> ((UrlFilter) et).getId(),
                (et, vl) -> ((UrlFilter) et).setId(ctl(vl)), "id");
        setupEpg(_epgMap, et -> ((UrlFilter) et).getSessionId(),
                (et, vl) -> ((UrlFilter) et).setSessionId((String) vl),
                "sessionId");
        setupEpg(_epgMap, et -> ((UrlFilter) et).getUrl(),
                (et, vl) -> ((UrlFilter) et).setUrl((String) vl), "url");
        setupEpg(_epgMap, et -> ((UrlFilter) et).getFilterType(),
                (et, vl) -> ((UrlFilter) et).setFilterType((String) vl),
                "filterType");
        setupEpg(_epgMap, et -> ((UrlFilter) et).getCreateTime(),
                (et, vl) -> ((UrlFilter) et).setCreateTime(ctl(vl)),
                "createTime");
    }

    @Override
    public PropertyGateway findPropertyGateway(final String prop) {
        return doFindEpg(_epgMap, prop);
    }

    // ===================================================================================
    //                                                                          Table Info
    //                                                                          ==========
    protected final String _tableDbName = "URL_FILTER";

    protected final String _tablePropertyName = "urlFilter";

    protected final TableSqlName _tableSqlName = new TableSqlName("URL_FILTER",
            _tableDbName);
    {
        _tableSqlName.xacceptFilter(DBFluteConfig.getInstance()
                .getTableSqlNameFilter());
    }

    @Override
    public String getTableDbName() {
        return _tableDbName;
    }

    @Override
    public String getTablePropertyName() {
        return _tablePropertyName;
    }

    @Override
    public TableSqlName getTableSqlName() {
        return _tableSqlName;
    }

    // ===================================================================================
    //                                                                         Column Info
    //                                                                         ===========
    protected final ColumnInfo _columnId = cci(
            "ID",
            "ID",
            null,
            null,
            Long.class,
            "id",
            null,
            true,
            true,
            true,
            "BIGINT",
            19,
            0,
            "NEXT VALUE FOR PUBLIC.SYSTEM_SEQUENCE_C26206F2_9B61_43A0_B186_06E417075CD9",
            false, null, null, null, null, null, false);

    protected final ColumnInfo _columnSessionId = cci("SESSION_ID",
            "SESSION_ID", null, null, String.class, "sessionId", null, false,
            false, true, "VARCHAR", 20, 0, null, false, null, null, null, null,
            null, false);

    protected final ColumnInfo _columnUrl = cci("URL", "URL", null, null,
            String.class, "url", null, false, false, true, "VARCHAR", 65536, 0,
            null, false, null, null, null, null, null, false);

    protected final ColumnInfo _columnFilterType = cci("FILTER_TYPE",
            "FILTER_TYPE", null, null, String.class, "filterType", null, false,
            false, true, "VARCHAR", 1, 0, null, false, null, null, null, null,
            null, false);

    protected final ColumnInfo _columnCreateTime = cci("CREATE_TIME",
            "CREATE_TIME", null, null, Long.class, "createTime", null, false,
            false, true, "BIGINT", 19, 0, null, false, null, null, null, null,
            null, false);

    /**
     * ID: {PK, ID, NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnId() {
        return _columnId;
    }

    /**
     * SESSION_ID: {IX+, NotNull, VARCHAR(20)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnSessionId() {
        return _columnSessionId;
    }

    /**
     * URL: {NotNull, VARCHAR(65536)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnUrl() {
        return _columnUrl;
    }

    /**
     * FILTER_TYPE: {NotNull, VARCHAR(1)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnFilterType() {
        return _columnFilterType;
    }

    /**
     * CREATE_TIME: {NotNull, BIGINT(19)}
     * @return The information object of specified column. (NotNull)
     */
    public ColumnInfo columnCreateTime() {
        return _columnCreateTime;
    }

    @Override
    protected List<ColumnInfo> ccil() {
        final List<ColumnInfo> ls = newArrayList();
        ls.add(columnId());
        ls.add(columnSessionId());
        ls.add(columnUrl());
        ls.add(columnFilterType());
        ls.add(columnCreateTime());
        return ls;
    }

    {
        initializeInformationResource();
    }

    // ===================================================================================
    //                                                                         Unique Info
    //                                                                         ===========
    // -----------------------------------------------------
    //                                       Primary Element
    //                                       ---------------
    @Override
    protected UniqueInfo cpui() {
        return hpcpui(columnId());
    }

    @Override
    public boolean hasPrimaryKey() {
        return true;
    }

    @Override
    public boolean hasCompoundPrimaryKey() {
        return false;
    }

    // ===================================================================================
    //                                                                       Relation Info
    //                                                                       =============
    // cannot cache because it uses related DB meta instance while booting
    // (instead, cached by super's collection)
    // -----------------------------------------------------
    //                                      Foreign Property
    //                                      ----------------

    // -----------------------------------------------------
    //                                     Referrer Property
    //                                     -----------------

    // ===================================================================================
    //                                                                        Various Info
    //                                                                        ============
    @Override
    public boolean hasIdentity() {
        return true;
    }

    // ===================================================================================
    //                                                                           Type Name
    //                                                                           =========
    @Override
    public String getEntityTypeName() {
        return "org.codelibs.robot.db.exentity.UrlFilter";
    }

    @Override
    public String getConditionBeanTypeName() {
        return "org.codelibs.robot.db.cbean.UrlFilterCB";
    }

    @Override
    public String getBehaviorTypeName() {
        return "org.codelibs.robot.db.exbhv.UrlFilterBhv";
    }

    // ===================================================================================
    //                                                                         Object Type
    //                                                                         ===========
    @Override
    public Class<UrlFilter> getEntityType() {
        return UrlFilter.class;
    }

    // ===================================================================================
    //                                                                     Object Instance
    //                                                                     ===============
    @Override
    public UrlFilter newEntity() {
        return new UrlFilter();
    }

    // ===================================================================================
    //                                                                   Map Communication
    //                                                                   =================
    @Override
    public void acceptPrimaryKeyMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptPrimaryKeyMap((UrlFilter) et, mp);
    }

    @Override
    public void acceptAllColumnMap(final Entity et,
            final Map<String, ? extends Object> mp) {
        doAcceptAllColumnMap((UrlFilter) et, mp);
    }

    @Override
    public Map<String, Object> extractPrimaryKeyMap(final Entity et) {
        return doExtractPrimaryKeyMap(et);
    }

    @Override
    public Map<String, Object> extractAllColumnMap(final Entity et) {
        return doExtractAllColumnMap(et);
    }
}
