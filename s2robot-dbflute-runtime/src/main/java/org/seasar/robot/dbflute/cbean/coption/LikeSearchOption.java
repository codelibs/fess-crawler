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
package org.seasar.robot.dbflute.cbean.coption;

import java.util.List;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.cbean.sqlclause.query.QueryClauseArranger;
import org.seasar.robot.dbflute.dbway.ExtensionOperand;
import org.seasar.robot.dbflute.resource.ResourceContext;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * The option of like search.
 * @author jflute
 */
public class LikeSearchOption extends SimpleStringOption {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    protected static final String LIKE_PREFIX = "prefix";
    protected static final String LIKE_SUFFIX = "suffix";
    protected static final String LIKE_CONTAIN = "contain";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _like;
    protected String _escape;
    protected boolean _asOrSplit;

    // ===================================================================================
    //                                                                         Rear Option
    //                                                                         ===========
    @Override
    public String getRearOption() {
        if (_escape == null || _escape.trim().length() == 0) {
            return "";
        }
        return " escape '" + _escape + "'";
    }

    // ===================================================================================
    //                                                                                Like
    //                                                                                ====
    /**
     * Set up prefix-search. {like 'foo%' escape '|'}
     * @return this. (NotNull)
     */
    public LikeSearchOption likePrefix() {
        _like = LIKE_PREFIX;
        doLikeAutoEscape();
        return this;
    }

    /**
     * Set up suffix-search. {like '%foo' escape '|'}
     * @return this. (NotNull)
     */
    public LikeSearchOption likeSuffix() {
        _like = LIKE_SUFFIX;
        doLikeAutoEscape();
        return this;
    }

    /**
     * Set up contain-search. {like '%foo%' escape '|'}
     * @return this. (NotNull)
     */
    public LikeSearchOption likeContain() {
        _like = LIKE_CONTAIN;
        doLikeAutoEscape();
        return this;
    }

    protected void doLikeAutoEscape() {
        escape();
    }

    // ===================================================================================
    //                                                                              Escape
    //                                                                              ======
    /**
     * Escape like search by pipeLine '|'.
     * @return The option of like search. (NotNull)
     */
    public LikeSearchOption escape() {
        return escapeByPipeLine();
    }

    public LikeSearchOption escapeByPipeLine() {
        _escape = "|";
        return this;
    }

    public LikeSearchOption escapeByAtMark() {
        _escape = "@";
        return this;
    }

    public LikeSearchOption escapeBySlash() {
        _escape = "/";
        return this;
    }

    public LikeSearchOption escapeByBackSlash() {
        _escape = "\\";
        return this;
    }

    public LikeSearchOption notEscape() {
        _escape = null;
        return this;
    }

    // ===================================================================================
    //                                                                               Split
    //                                                                               =====
    /**
     * Split a value as several condition by blank (space, full-width space, tab, CR, LF).
     * @return this.
     */
    public LikeSearchOption splitByBlank() {
        return (LikeSearchOption) doSplitByBlank();
    }

    /**
     * Split a value as several condition with limit by blank.
     * @param splitLimitCount The limit count of split. (NotZero, NotMinus)
     * @return this.
     */
    public LikeSearchOption splitByBlank(int splitLimitCount) {
        return (LikeSearchOption) doSplitByBlank(splitLimitCount);
    }

    /**
     * Split a value as several condition by space.
     * @return this.
     */
    public LikeSearchOption splitBySpace() {
        return (LikeSearchOption) doSplitBySpace();
    }

    /**
     * Split a value as several condition with limit by space.
     * @param splitLimitCount The limit count of split. (NotZero, NotMinus)
     * @return this.
     */
    public LikeSearchOption splitBySpace(int splitLimitCount) {
        return (LikeSearchOption) doSplitBySpace(splitLimitCount);
    }

    /**
     * Split a value as several condition by space that contains full-width space.
     * @return this.
     */
    public LikeSearchOption splitBySpaceContainsDoubleByte() {
        return (LikeSearchOption) doSplitBySpaceContainsDoubleByte();
    }

    /**
     * Split a value as several condition by space that contains full-width space.
     * @param splitLimitCount The limit count of split. (NotZero, NotMinus)
     * @return this.
     */
    public LikeSearchOption splitBySpaceContainsDoubleByte(int splitLimitCount) {
        return (LikeSearchOption) doSplitBySpaceContainsDoubleByte(splitLimitCount);
    }

    /**
     * Split a value as several condition by pipeline.
     * @return this.
     */
    public LikeSearchOption splitByPipeLine() {
        return (LikeSearchOption) doSplitByPipeLine();
    }

    /**
     * Split a value as several condition by pipeline.
     * @param splitLimitCount The limit count of split. (NotZero, NotMinus)
     * @return this.
     */
    public LikeSearchOption splitByPipeLine(int splitLimitCount) {
        return (LikeSearchOption) doSplitByPipeLine(splitLimitCount);
    }

    /**
     * Split a value as several condition by specified various delimiters.
     * @param delimiterList The list of delimiter for split. (NotNull, NotEmpty)
     * @return this.
     */
    public LikeSearchOption splitByVarious(List<String> delimiterList) {
        return (LikeSearchOption) doSplitByVarious(delimiterList);
    }

    /**
     * Split a value as several condition by specified various delimiters.
     * @param delimiterList The list of delimiter for split. (NotNull, NotEmpty)
     * @param splitLimitCount The limit count of split. (NotZero, NotMinus)
     * @return this.
     */
    public LikeSearchOption splitByVarious(List<String> delimiterList, int splitLimitCount) {
        return (LikeSearchOption) doSplitByVarious(delimiterList, splitLimitCount);
    }

    /**
     * Split as OR condition. <br >
     * You should call this with a splitByXxx method.
     * @return this.
     */
    public LikeSearchOption asOrSplit() {
        _asOrSplit = true;
        return this;
    }

    public boolean isAsOrSplit() {
        return _asOrSplit;
    }

    // ===================================================================================
    //                                                                          Real Value
    //                                                                          ==========
    @Override
    public String generateRealValue(String value) {
        value = super.generateRealValue(value);

        // Escape
        if (_escape != null && _escape.trim().length() != 0) {
            String tmp = replace(value, _escape, _escape + _escape);
            tmp = replace(tmp, "%", _escape + "%");
            tmp = replace(tmp, "_", _escape + "_");

            // escape double-byte wild-cards
            // Oracle and DB2 treat these symbols as wild-card
            // but other DBMS ignore unused escape characters
            // so if-statement does not exist here
            tmp = replace(tmp, "\uff05", _escape + "\uff05");
            tmp = replace(tmp, "\uff3f", _escape + "\uff3f");

            value = tmp;
        }
        final String wildCard = "%";
        if (_like == null || _like.trim().length() == 0) {
            return value;
        } else if (_like.equals(LIKE_PREFIX)) {
            return value + wildCard;
        } else if (_like.equals(LIKE_SUFFIX)) {
            return wildCard + value;
        } else if (_like.equals(LIKE_CONTAIN)) {
            return wildCard + value + wildCard;
        } else {
            String msg = "The like was wrong string: " + _like;
            throw new IllegalStateException(msg);
        }
    }

    protected boolean isCurrentDBDef(DBDef currentDBDef) {
        return ResourceContext.isCurrentDBDef(currentDBDef);
    }

    @Override
    protected SimpleStringOption newDeepCopyInstance() {
        return new LikeSearchOption();
    }

    // ===================================================================================
    //                                                                   Extension Operand
    //                                                                   =================
    /**
     * Get the operand for extension.
     * @return The operand for extension. (NullAllowed: If the value is null, it means no extension)
     */
    public ExtensionOperand getExtensionOperand() { // for application extension
        return null; // as default
    }

    // ===================================================================================
    //                                                               Where Clause Arranger
    //                                                               =====================
    /**
     * Get the arranger of where clause.
     * @return The arranger of where clause. (NullAllowed: If the value is null, it means no arrangement)
     */
    public QueryClauseArranger getWhereClauseArranger() { // for application extension
        return null; // as default
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        final String title = DfTypeUtil.toClassTitle(this);
        final String split = (isSplit() ? (_asOrSplit ? "true(or)" : "true(and)") : "false");
        return title + ":{like=" + _like + ", escape=" + _escape + ", split=" + split + "}";
    }
}
