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
package org.seasar.robot.dbflute.twowaysql.node;

import org.seasar.robot.dbflute.cbean.coption.LikeSearchOption;

/**
 * @author jflute
 */
public class ValueAndType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Object _firstValue;
    protected Class<?> _firstType;
    protected Object _targetValue;
    protected Class<?> _targetType;
    protected LikeSearchOption _likeSearchOption;

    // ===================================================================================
    //                                                                         Rear Option
    //                                                                         ===========
    public void filterValueByOptionIfNeeds() {
        if (_likeSearchOption == null) {
            return;
        }
        if (_targetValue == null) {
            return;
        }
        if (_targetValue instanceof String) {
            _targetValue = _likeSearchOption.generateRealValue((String) _targetValue);
        }
    }

    public String buildRearOptionOnSql() {
        if (_likeSearchOption == null) {
            return null;
        }
        if (_targetValue == null) {
            return null;
        }
        if (_targetValue instanceof String) {
            final String rearOption = _likeSearchOption.getRearOption();
            return " " + rearOption.trim() + " ";
        } else {
            return null;
        }
    }

    protected void inheritLikeSearchOptionIfNeeds(LoopInfo loopInfo) {
        if (loopInfo == null) {
            return;
        }
        final LikeSearchOption current = getLikeSearchOption();
        if (current != null) {
            return;
        }
        final LikeSearchOption parent = loopInfo.getLikeSearchOption();
        if (parent != null) {
            setLikeSearchOption(parent); // inherit
        }
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public Object getFirstValue() {
        return _firstValue;
    }

    public void setFirstValue(Object firstValue) {
        this._firstValue = firstValue;
    }

    public Class<?> getFirstType() {
        return _firstType;
    }

    public void setFirstType(Class<?> firstType) {
        this._firstType = firstType;
    }

    public Object getTargetValue() {
        return _targetValue;
    }

    public void setTargetValue(Object targetValue) {
        this._targetValue = targetValue;
    }

    public Class<?> getTargetType() {
        return _targetType;
    }

    public void setTargetType(Class<?> targetType) {
        this._targetType = targetType;
    }

    public LikeSearchOption getLikeSearchOption() {
        return _likeSearchOption;
    }

    public void setLikeSearchOption(LikeSearchOption likeSearchOption) {
        this._likeSearchOption = likeSearchOption;
    }
}
