package org.seasar.robot.dbflute.cbean.sqlclause.subquery;

import org.seasar.robot.dbflute.util.Srl;

/**
 * @author jflute
 * @since 0.9.7.2 (2010/06/20 Sunday)
 */
public class SubQueryPath {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final String _subQueryPath;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param subQueryPath The property path of sub-query. (NotNull)
     */
    public SubQueryPath(String subQueryPath) {
        _subQueryPath = subQueryPath;
    }

    // ===================================================================================
    //                                                                   Location Resolver
    //                                                                   =================
    public String resolveParameterLocationPath(String clause) {
        return replaceString(clause, ".conditionQuery.", "." + _subQueryPath + ".");
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected final String replaceString(String text, String fromText, String toText) {
        return Srl.replace(text, fromText, toText);
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public int hashCode() {
        return _subQueryPath.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SubQueryPath)) {
            return false;
        }
        final SubQueryPath target = (SubQueryPath) obj;
        return _subQueryPath.equals(target.toString());
    }

    @Override
    public String toString() {
        return _subQueryPath;
    }
}
