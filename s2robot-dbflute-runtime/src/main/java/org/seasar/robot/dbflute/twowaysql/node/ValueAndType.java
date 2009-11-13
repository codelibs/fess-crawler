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
package org.seasar.robot.dbflute.twowaysql.node;

/**
 * @author jflute
 */
public class ValueAndType {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected Object _targetValue;
    protected Class<?> _targetType;
    protected String _rearOption;

    // ===================================================================================
    //                                                                         Rear Option
    //                                                                         ===========
    public boolean isValidRearOption() {
        return _targetValue != null && _rearOption != null && _rearOption.trim().length() > 0;
    }

    public String buildRearOptionOnSql() {
        return " " + _rearOption.trim() + " ";
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
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

    public String getRearOption() {
        return _rearOption;
    }

    public void setRearOption(String rearOption) {
        this._rearOption = rearOption;
    }
}
