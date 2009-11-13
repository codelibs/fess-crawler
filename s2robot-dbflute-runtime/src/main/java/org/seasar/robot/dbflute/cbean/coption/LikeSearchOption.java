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
package org.seasar.robot.dbflute.cbean.coption;

import org.seasar.robot.dbflute.DBDef;
import org.seasar.robot.dbflute.cbean.coption.parts.local.JapaneseOptionPartsAgent;
import org.seasar.robot.dbflute.cbean.sqlclause.WhereClauseArranger;
import org.seasar.robot.dbflute.dbway.ExtensionOperand;
import org.seasar.robot.dbflute.resource.ResourceContext;

/**
 * The option of like search.
 * @author jflute
 */
public class LikeSearchOption extends SimpleStringOption {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
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
    public String getRearOption() {
        if (_escape == null || _escape.trim().length() == 0) {
            return "";
        }
        return " escape '" + _escape + "'";
    }

    // ===================================================================================
    //                                                                                Like
    //                                                                                ====
    public LikeSearchOption likePrefix() {
        _like = LIKE_PREFIX;
        doLikeAutoEscape();
        return this;
    }

    public LikeSearchOption likeSuffix() {
        _like = LIKE_SUFFIX;
        doLikeAutoEscape();
        return this;
    }

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
     * Escape like search by PipeLine '|'.
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
    public LikeSearchOption splitBySpace() {
        return (LikeSearchOption) doSplitBySpace();
    }

    public LikeSearchOption splitBySpace(int splitLimitCount) {
        return (LikeSearchOption) doSplitBySpace(splitLimitCount);
    }

    public LikeSearchOption splitBySpaceContainsDoubleByte() {
        return (LikeSearchOption) doSplitBySpaceContainsDoubleByte();
    }

    public LikeSearchOption splitBySpaceContainsDoubleByte(int splitLimitCount) {
        return (LikeSearchOption) doSplitBySpaceContainsDoubleByte(splitLimitCount);
    }

    public LikeSearchOption splitByPipeLine() {
        return (LikeSearchOption) doSplitByPipeLine();
    }

    public LikeSearchOption splitByPipeLine(int splitLimitCount) {
        return (LikeSearchOption) doSplitByPipeLine(splitLimitCount);
    }

    public LikeSearchOption asOrSplit() {
        _asOrSplit = true;
        return this;
    }

    public boolean isAsOrSplit() {
        return _asOrSplit;
    }

    // ===================================================================================
    //                                                                 To Upper/Lower Case
    //                                                                 ===================
    public LikeSearchOption toUpperCase() {
        return (LikeSearchOption) doToUpperCase();
    }

    public LikeSearchOption toLowerCase() {
        return (LikeSearchOption) doToLowerCase();
    }

    // ===================================================================================
    //                                                                      To Single Byte
    //                                                                      ==============
    public LikeSearchOption toSingleByteSpace() {
        return (LikeSearchOption) doToSingleByteSpace();
    }

    public LikeSearchOption toSingleByteAlphabetNumber() {
        return (LikeSearchOption) doToSingleByteAlphabetNumber();
    }

    public LikeSearchOption toSingleByteAlphabetNumberMark() {
        return (LikeSearchOption) doToSingleByteAlphabetNumberMark();
    }

    // ===================================================================================
    //                                                                      To Double Byte
    //                                                                      ==============

    // ===================================================================================
    //                                                                            Japanese
    //                                                                            ========
    public JapaneseOptionPartsAgent localJapanese() {
        return doLocalJapanese();
    }

    // ===================================================================================
    //                                                                          Real Value
    //                                                                          ==========
    public String generateRealValue(String value) {
        value = super.generateRealValue(value);

        // Escape
        if (_escape != null && _escape.trim().length() != 0) {
            String tmp = replace(value, _escape, _escape + _escape);
            tmp = replace(tmp, "%", _escape + "%");
            tmp = replace(tmp, "_", _escape + "_");
            if (isCurrentDBDef(DBDef.Oracle)) {
                tmp = replace(tmp, "\uff05", _escape + "\uff05");
                tmp = replace(tmp, "\uff3f", _escape + "\uff3f");
            }
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

    protected SimpleStringOption newDeepCopyInstance() {
        return new LikeSearchOption();
    }

    // ===================================================================================
    //                                                                   Extension Operand
    //                                                                   =================
    /**
     * Get the operand for extension.
     * @return The operand for extension. (Nullable: If the value is null, it means no extension)
     */
    public ExtensionOperand getExtensionOperand() { // for application extension
        return null; // as default
    }

    // ===================================================================================
    //                                                               Where Clause Arranger
    //                                                               =====================
    /**
     * Get the arranger of where clause.
     * @return The arranger of where clause. (Nullable: If the value is null, it means no arrangement)
     */
    public WhereClauseArranger getWhereClauseArranger() { // for application extension
        return null; // as default
    }
    
    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return "like=" + _like + ", escape=" + _escape + ", split=" + isSplit() + ", asOrSplit = " + _asOrSplit;
    }
}
