/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.db.cbean.nss;

import org.codelibs.fess.crawler.db.cbean.cq.AccessResultDataCQ;

/**
 * The nest select set-upper of ACCESS_RESULT_DATA.
 * @author DBFlute(AutoGenerator)
 */
public class AccessResultDataNss {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final AccessResultDataCQ _query;
    public AccessResultDataNss(AccessResultDataCQ query) { _query = query; }
    public boolean hasConditionQuery() { return _query != null; }

    // ===================================================================================
    //                                                                     Nested Relation
    //                                                                     ===============
    /**
     * With nested relation columns to select clause. <br>
     * ACCESS_RESULT by my ID, named 'accessResult'.
     * @return The set-upper of more nested relation. {...with[nested-relation].with[more-nested-relation]} (NotNull)
     */
    public AccessResultNss withAccessResult() {
        _query.xdoNss(() -> _query.queryAccessResult());
        return new AccessResultNss(_query.queryAccessResult());
    }
}
