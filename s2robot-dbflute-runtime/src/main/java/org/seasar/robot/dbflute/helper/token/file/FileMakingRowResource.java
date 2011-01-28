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
package org.seasar.robot.dbflute.helper.token.file;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author jflute
 */
public class FileMakingRowResource {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected List<String> _valueList;

    protected LinkedHashMap<String, String> _nameValueMap;

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public List<String> getValueList() {
        return _valueList;
    }

    /**
     * Set the list of value. {Priority One}
     * @param valueList The list of value. (NotNull and NotEmpty)
     */
    public void setValueList(List<String> valueList) {
        this._valueList = valueList;
    }

    public LinkedHashMap<String, String> getNameValueMap() {
        return _nameValueMap;
    }

    /**
     * Set the map of name and value. {Priority Two} <br />
     * If valueList is set, This nameValueMap is ignored.
     * @param nameValueMap The map of name and value. (NotNull and NotEmpty)
     */
    public void setNameValueMap(LinkedHashMap<String, String> nameValueMap) {
        this._nameValueMap = nameValueMap;
    }
}
