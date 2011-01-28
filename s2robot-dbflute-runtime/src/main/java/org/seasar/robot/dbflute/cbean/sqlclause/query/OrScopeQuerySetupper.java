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
package org.seasar.robot.dbflute.cbean.sqlclause.query;

import java.util.List;

import org.seasar.robot.dbflute.util.DfSystemUtil;
import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 */
public class OrScopeQuerySetupper {

    // ===================================================================================
    //                                                                              Set up
    //                                                                              ======
    public void setupOrScopeQuery(List<OrScopeQueryClauseGroup> clauseGroupList, List<QueryClause> realList,
            boolean line) {
        if (clauseGroupList == null || clauseGroupList.isEmpty()) {
            return;
        }
        final String or = " or ";
        final String and = " and ";
        final String lnIndentOr = line ? ln() + "    " : "";
        final String lnIndentAnd = ""; // no line separator either way
        final String lnIndentAndLn = line ? ln() + "      " : "";
        final StringBuilder sb = new StringBuilder();
        boolean exists = false;
        int validCount = 0;
        int groupListIndex = 0;
        for (OrScopeQueryClauseGroup clauseGroup : clauseGroupList) {
            final List<QueryClause> orClauseList = clauseGroup.getOrClauseList();
            if (orClauseList == null || orClauseList.isEmpty()) {
                continue; // not increment index
            }
            int listIndex = 0;
            Integer preAndPartIdentity = null;
            for (QueryClause clauseElement : orClauseList) {
                final String orClause = clauseElement.toString();
                OrScopeQueryAndPartQueryClause andPartClause = null;
                if (clauseElement instanceof OrScopeQueryAndPartQueryClause) {
                    andPartClause = (OrScopeQueryAndPartQueryClause) clauseElement;
                }
                final boolean beginAndPart;
                final boolean secondAndPart;
                if (andPartClause != null) {
                    final int identity = andPartClause.getIdentity();
                    if (preAndPartIdentity == null) { // first of and-part
                        preAndPartIdentity = identity;
                        beginAndPart = true;
                        secondAndPart = false;
                    } else if (preAndPartIdentity == identity) { // same and-part
                        beginAndPart = false;
                        secondAndPart = true;
                    } else { // other and-part
                        sb.append(")"); // closing a previous and-part
                        preAndPartIdentity = identity;
                        beginAndPart = true;
                        secondAndPart = false;
                    }
                } else {
                    if (preAndPartIdentity != null) {
                        sb.append(")"); // closing an and-part
                        preAndPartIdentity = null;
                    }
                    beginAndPart = false;
                    secondAndPart = false;
                }
                if (groupListIndex == 0) { // first list
                    if (listIndex == 0) {
                        sb.append("(");
                    } else {
                        final boolean containsLn = orClause.contains(ln());
                        sb.append(secondAndPart ? (containsLn ? lnIndentAndLn : lnIndentAnd) : lnIndentOr);
                        sb.append(secondAndPart ? and : or);
                    }
                } else { // second or more list
                    if (listIndex == 0) {
                        // always 'or' here
                        sb.append(lnIndentOr);
                        sb.append(or);
                        sb.append("(");
                    } else {
                        final boolean containsLn = orClause.contains(ln());
                        sb.append(secondAndPart ? (containsLn ? lnIndentAndLn : lnIndentAnd) : lnIndentOr);
                        sb.append(secondAndPart ? and : or);
                    }
                }
                sb.append(beginAndPart ? "(" : "");
                sb.append(orClause);
                ++validCount;
                if (!exists) {
                    exists = true;
                }
                ++listIndex;
            }
            if (preAndPartIdentity != null) {
                sb.append(")"); // closing an and-part
                preAndPartIdentity = null;
            }
            if (groupListIndex > 0) { // second or more list
                sb.append(")"); // closing an or-scope
            }
            ++groupListIndex;
        }
        if (exists) {
            sb.append(line && validCount > 1 ? ln() + "       " : "").append(")");
            realList.add(new StringQueryClause(sb.toString()));
        }
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String replace(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
