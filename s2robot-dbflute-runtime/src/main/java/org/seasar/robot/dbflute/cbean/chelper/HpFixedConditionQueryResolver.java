package org.seasar.robot.dbflute.cbean.chelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.cbean.ConditionQuery;
import org.seasar.robot.dbflute.cbean.sqlclause.join.FixedConditionResolver;
import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.dbmeta.info.ColumnInfo;
import org.seasar.robot.dbflute.dbmeta.info.ForeignInfo;
import org.seasar.robot.dbflute.dbmeta.name.TableSqlName;
import org.seasar.robot.dbflute.exception.DBMetaNotFoundException;
import org.seasar.robot.dbflute.exception.IllegalFixedConditionOverRelationException;
import org.seasar.robot.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.Srl;
import org.seasar.robot.dbflute.util.Srl.IndexOfInfo;

/**
 * @author jflute
 * @since 0.9.7.5 (2010/10/11 Monday)
 */
public class HpFixedConditionQueryResolver implements FixedConditionResolver {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final ConditionQuery _localCQ;
    protected final ConditionQuery _foreignCQ;
    protected final DBMetaProvider _dbmetaProvider;
    protected Map<String, InlineViewResource> _inlineViewResourceMap; // internal bridge container

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public HpFixedConditionQueryResolver(ConditionQuery localCQ, ConditionQuery foreignCQ, DBMetaProvider dbmetaProvider) {
        _localCQ = localCQ;
        _foreignCQ = foreignCQ;
        _dbmetaProvider = dbmetaProvider;
    }

    // ===================================================================================
    //                                                                    Resolve Variable
    //                                                                    ================
    /**
     * Resolve variables on fixed condition.
     * @param fixedCondition The plain fixed condition. (NotNull: if null, this is not called)
     * @return The resolved fixed condition. (NotNull)
     */
    public String resolveVariable(String fixedCondition) {
        final String localAliasName = _localCQ.xgetAliasName();
        final String foreignAliasName = _foreignCQ.xgetAliasName();
        fixedCondition = replaceString(fixedCondition, "$$alias$$", foreignAliasName); // for compatibility
        fixedCondition = replaceString(fixedCondition, getLocalAliasMark(), localAliasName);
        fixedCondition = replaceString(fixedCondition, getForeignAliasMark(), foreignAliasName);
        final String locationBase = _localCQ.xgetLocationBase();
        fixedCondition = replaceString(fixedCondition, getLocationBaseMark() + ".", "pmb." + locationBase);
        fixedCondition = resolveFixedConditionOverRelation(fixedCondition);
        return fixedCondition;
    }

    protected String resolveFixedConditionOverRelation(String fixedCondition) {
        final String relationBeginMark = getRelationBeginMark();
        final String relationEndMark = getRelationEndMark();
        String remainder = fixedCondition;
        while (true) {
            final IndexOfInfo relationBeginIndex = Srl.indexOfFirst(remainder, relationBeginMark);
            if (relationBeginIndex == null) {
                break;
            }
            remainder = relationBeginIndex.substringRear();
            final IndexOfInfo relationEndIndex = Srl.indexOfFirst(remainder, relationEndMark);
            if (relationEndIndex == null) {
                break;
            }

            // analyze:
            // - $$over($$localTable$$.memberStatus)$$
            // - $$over($$foreignTable$$.memberStatus, DISPLAY_ORDER)$$
            // - $$over(PURCHASE.product.productStatus)$$
            final String relationExp = relationEndIndex.substringFront();
            final String pointTable;
            final String targetRelation;
            final String secondArg;
            {
                final IndexOfInfo separatorIndex = Srl.indexOfFirst(relationExp, ".");
                if (separatorIndex != null) {
                    pointTable = separatorIndex.substringFrontTrimmed();
                    final IndexOfInfo argIndex = Srl.indexOfFirst(separatorIndex.substringRearTrimmed(), ",");
                    targetRelation = argIndex != null ? argIndex.substringFrontTrimmed() : separatorIndex
                            .substringRearTrimmed();
                    secondArg = argIndex != null ? argIndex.substringRearTrimmed() : null;
                } else {
                    final IndexOfInfo argIndex = Srl.indexOfFirst(relationExp, ",");
                    pointTable = argIndex != null ? argIndex.substringFrontTrimmed() : Srl.trim(relationExp);
                    targetRelation = null;
                    secondArg = argIndex != null ? argIndex.substringRearTrimmed() : null;
                }
            }

            final ConditionQuery relationPointCQ;
            final ConditionQuery columnTargetCQ;
            if (Srl.equalsPlain(pointTable, getLocalTableMark())) { // local table
                relationPointCQ = _localCQ;
                if (targetRelation != null) {
                    columnTargetCQ = invokeColumnTargetCQ(relationPointCQ, targetRelation);
                } else {
                    String notice = "The relation on fixed condition is required if the table is not referrer.";
                    throwIllegalFixedConditionOverRelationException(notice, pointTable, null, fixedCondition);
                    return null; // unreachable
                }
            } else if (Srl.equalsPlain(pointTable, getForeignTableMark())) { // foreign table
                relationPointCQ = _foreignCQ;
                columnTargetCQ = relationPointCQ;
                if (targetRelation == null) {
                    String notice = "The relation on fixed condition is required if the table is not referrer.";
                    throwIllegalFixedConditionOverRelationException(notice, pointTable, null, fixedCondition);
                    return null; // unreachable
                }
                // prepare fixed InlineView
                if (_inlineViewResourceMap == null) {
                    _inlineViewResourceMap = new LinkedHashMap<String, InlineViewResource>();
                }
                final InlineViewResource resource;
                if (_inlineViewResourceMap.containsKey(targetRelation)) {
                    resource = _inlineViewResourceMap.get(targetRelation);
                } else {
                    resource = new InlineViewResource();
                    _inlineViewResourceMap.put(targetRelation, resource);
                }
                final String columnName;
                {
                    final IndexOfInfo rearIndex = Srl.indexOfFirst(relationEndIndex.substringRearTrimmed(), ".");
                    if (rearIndex == null || rearIndex.getIndex() > 0) {
                        String notice = "The OverRelation variable should continue to column after the variable.";
                        throwIllegalFixedConditionOverRelationException(notice, pointTable, targetRelation,
                                fixedCondition);
                        return null; // unreachable
                    }
                    final String columnStart = rearIndex.substringRear();
                    final IndexOfInfo indexInfo = Srl.indexOfFirst(columnStart, " ", ",", ")", "\n", "\t");
                    columnName = indexInfo != null ? indexInfo.substringFront() : columnStart;
                }
                // the secondArg should be a column DB name, and then rear column is alias name
                final String resolvedColumn = secondArg != null ? secondArg + " as " + columnName : columnName;
                resource.addAdditionalColumn(resolvedColumn);
                final List<String> splitList = Srl.splitList(targetRelation, ".");
                DBMeta currentDBMeta = _dbmetaProvider.provideDBMeta(_foreignCQ.getTableDbName());
                for (String element : splitList) {
                    final ForeignInfo foreignInfo = currentDBMeta.findForeignInfo(element);
                    resource.addJoinInfo(foreignInfo);
                    currentDBMeta = foreignInfo.getForeignDBMeta();
                }
            } else { // referrer table
                final DBMeta pointDBMeta;
                try {
                    pointDBMeta = _dbmetaProvider.provideDBMeta(pointTable);
                } catch (DBMetaNotFoundException e) {
                    String notice = "The table for relation on fixed condition does not exist.";
                    throwIllegalFixedConditionOverRelationException(notice, pointTable, targetRelation, fixedCondition,
                            e);
                    return null; // unreachable
                }
                ConditionQuery referrerQuery = _localCQ.xgetReferrerQuery();
                while (true) {
                    if (referrerQuery == null) { // means not found
                        break;
                    }
                    if (Srl.equalsPlain(pointDBMeta.getTableDbName(), referrerQuery.getTableDbName())) {
                        break;
                    }
                    referrerQuery = referrerQuery.xgetReferrerQuery();
                }
                relationPointCQ = referrerQuery;
                if (relationPointCQ == null) {
                    String notice = "The table for relation on fixed condition was not found in the scope.";
                    throwIllegalFixedConditionOverRelationException(notice, pointTable, targetRelation, fixedCondition);
                    return null; // unreachable
                }
                if (targetRelation != null) {
                    columnTargetCQ = invokeColumnTargetCQ(relationPointCQ, targetRelation);
                } else {
                    columnTargetCQ = relationPointCQ;
                }
            }

            final String relationVariable = relationBeginMark + relationExp + relationEndMark;
            final String relationAlias = columnTargetCQ.xgetAliasName();
            fixedCondition = replaceString(fixedCondition, relationVariable, relationAlias);

            // after case for loop
            remainder = relationEndIndex.substringRear();

            // for prevent from processing same one
            remainder = replaceString(remainder, relationVariable, relationAlias);
        }
        return fixedCondition;
    }

    protected ConditionQuery invokeColumnTargetCQ(ConditionQuery relationPointCQ, String targetRelation) {
        return relationPointCQ.invokeForeignCQ(targetRelation);
    }

    // ===================================================================================
    //                                                            Resolve Fixed InlineView
    //                                                            ========================
    public String resolveFixedInlineView(String foreignTableSqlName) {
        if (_inlineViewResourceMap == null || _inlineViewResourceMap.isEmpty()) {
            return foreignTableSqlName; // not uses InlineView
        }
        // alias is required because foreignTableSqlName may be (normal) InlineView
        final String baseAlias = "dffixedbase";
        final String baseIndent = "                    ";
        final StringBuilder joinSb = new StringBuilder();
        final Map<ForeignInfo, String> relationMap = new HashMap<ForeignInfo, String>();
        final List<String> additionalRealColumnList = new ArrayList<String>();
        int groupIndex = 0;
        for (InlineViewResource resource : _inlineViewResourceMap.values()) {
            final List<ForeignInfo> joinInfoList = resource.getJoinInfoList();
            final String aliasBase = "dffixedjoin";
            String preForeignAlias = null;
            String foreignAlias = null;
            int joinIndex = 0;
            for (ForeignInfo joinInfo : joinInfoList) {
                if (relationMap.containsKey(joinInfo)) { // already joined
                    preForeignAlias = relationMap.get(joinInfo); // update previous alias
                    continue;
                }
                final TableSqlName foreignTable;
                final String localAlias;
                {
                    final DBMeta foreignDBMeta = joinInfo.getForeignDBMeta();
                    foreignTable = foreignDBMeta.getTableSqlName();
                    localAlias = (preForeignAlias != null ? preForeignAlias : baseAlias);
                    foreignAlias = aliasBase + "_" + groupIndex + "_" + joinIndex;
                    preForeignAlias = foreignAlias;
                }
                joinSb.append(ln()).append(baseIndent);
                joinSb.append("     left outer join ").append(foreignTable).append(" ").append(foreignAlias);
                joinSb.append(" on ");
                final Map<ColumnInfo, ColumnInfo> columnInfoMap = joinInfo.getLocalForeignColumnInfoMap();
                int columnIndex = 0;
                for (Entry<ColumnInfo, ColumnInfo> localForeignEntry : columnInfoMap.entrySet()) {
                    final ColumnInfo localColumnInfo = localForeignEntry.getKey();
                    final ColumnInfo foreignColumninfo = localForeignEntry.getValue();
                    if (columnIndex > 0) {
                        joinSb.append(" and ");
                    }
                    joinSb.append(localAlias).append(".").append(localColumnInfo.getColumnSqlName());
                    joinSb.append(" = ").append(foreignAlias).append(".").append(foreignColumninfo.getColumnSqlName());
                    ++columnIndex;
                }
                relationMap.put(joinInfo, foreignAlias);
                ++joinIndex;
            }
            final Set<String> additionalColumnSet = resource.getAdditionalColumnSet();
            for (String columnName : additionalColumnSet) {
                additionalRealColumnList.add(foreignAlias + "." + columnName); // latest alias is target
            }
            ++groupIndex;
        }
        final StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("(select ").append(baseAlias).append(".*");
        for (String columnName : additionalRealColumnList) {
            sqlSb.append(", ").append(columnName);
        }
        sqlSb.append(ln()).append(baseIndent);
        sqlSb.append("   from ").append(foreignTableSqlName).append(" ").append(baseAlias);
        sqlSb.append(joinSb);
        sqlSb.append(ln()).append(baseIndent);
        sqlSb.append(")");
        return sqlSb.toString();
    }

    // ===================================================================================
    //                                                                    InlineView Class
    //                                                                    ================
    protected static class InlineViewResource {
        protected Set<String> _additionalColumnSet;
        protected List<ForeignInfo> _joinInfoList;

        public Set<String> getAdditionalColumnSet() {
            return _additionalColumnSet;
        }

        public void addAdditionalColumn(String additionalColumn) {
            if (_additionalColumnSet == null) {
                _additionalColumnSet = new LinkedHashSet<String>();
            }
            _additionalColumnSet.add(additionalColumn);
        }

        public List<ForeignInfo> getJoinInfoList() {
            return _joinInfoList;
        }

        public void addJoinInfo(ForeignInfo joinInfo) {
            if (_joinInfoList == null) {
                _joinInfoList = new ArrayList<ForeignInfo>();
            }
            _joinInfoList.add(joinInfo);
        }
    }

    // ===================================================================================
    //                                                                  Exception Handling
    //                                                                  ==================
    protected void throwIllegalFixedConditionOverRelationException(String notice, String tableName,
            String relationName, String fixedCondition) {
        throwIllegalFixedConditionOverRelationException(notice, tableName, relationName, fixedCondition, null);
    }

    protected void throwIllegalFixedConditionOverRelationException(String notice, String pointTable,
            String targetRelation, String fixedCondition, Exception e) {
        final ExceptionMessageBuilder br = new ExceptionMessageBuilder();
        br.addNotice(notice);
        br.addItem("Point Table");
        br.addElement(pointTable);
        br.addItem("Target Relation");
        br.addElement(targetRelation);
        br.addItem("Fixed Condition");
        br.addElement(fixedCondition);
        br.addItem("BizOneToOne's Local");
        br.addElement(_localCQ.getTableDbName());
        final String msg = br.buildExceptionMessage();
        throw new IllegalFixedConditionOverRelationException(msg, e);
    }

    // ===================================================================================
    //                                                                       Variable Mark
    //                                                                       =============
    protected String getLocalAliasMark() {
        return "$$localAlias$$";
    }

    protected String getForeignAliasMark() {
        return "$$foreignAlias$$";
    }

    protected String getLocationBaseMark() {
        return "$$locationBase$$";
    }

    protected String getRelationBeginMark() {
        return "$$over(";
    }

    protected String getRelationEndMark() {
        return ")$$";
    }

    protected String getLocalTableMark() {
        return "$localTable";
    }

    protected String getForeignTableMark() {
        return "$foreignTable";
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
