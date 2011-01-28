package org.seasar.robot.dbflute.twowaysql.node;

import java.util.List;

import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;
import org.seasar.robot.dbflute.util.DfTypeUtil;

/**
 * @author jflute
 * @since 0.9.7.0 (2010/05/29 Saturday)
 */
public class LoopInfo {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _expression; // as loop meta information
    protected String _specifiedSql; // as loop meta information
    protected List<?> _parameterList;
    protected int _loopSize;
    protected LikeSearchOption _likeSearchOption;
    protected int _loopIndex;
    protected LoopInfo _parentLoop;

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    @Override
    public String toString() {
        return DfTypeUtil.toClassTitle(this) + ":{" + _loopIndex + "/" + _loopSize + ", " + _parameterList + "}";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getExpression() {
        return _expression;
    }

    public void setExpression(String expression) {
        this._expression = expression;
    }

    public String getSpecifiedSql() {
        return _specifiedSql;
    }

    public void setSpecifiedSql(String specifiedSql) {
        this._specifiedSql = specifiedSql;
    }

    public List<?> getParameterList() {
        return _parameterList;
    }

    public void setParameterList(List<?> parameterList) {
        this._parameterList = parameterList;
    }

    public int getLoopSize() {
        return _loopSize;
    }

    public void setLoopSize(int loopSize) {
        this._loopSize = loopSize;
    }

    public LikeSearchOption getLikeSearchOption() {
        return _likeSearchOption;
    }

    public void setLikeSearchOption(LikeSearchOption likeSearchOption) {
        this._likeSearchOption = likeSearchOption;
    }

    public int getLoopIndex() {
        return _loopIndex;
    }

    public void setLoopIndex(int loopIndex) {
        this._loopIndex = loopIndex;
    }

    public LoopInfo getParentLoop() {
        return _parentLoop;
    }

    public void setParentLoop(LoopInfo parentLoop) {
        this._parentLoop = parentLoop;
    }

    public Object getCurrentParameter() {
        return _parameterList.get(_loopIndex);
    }

    public Class<?> getCurrentParameterType() {
        final Object parameter = getCurrentParameter();
        return parameter != null ? parameter.getClass() : null;
    }
}
