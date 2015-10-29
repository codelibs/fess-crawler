/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.db.bsbhv.loader;

import java.util.List;

import org.dbflute.bhv.*;
import org.codelibs.fess.crawler.db.exbhv.*;
import org.codelibs.fess.crawler.db.exentity.*;

/**
 * The referrer loader of ACCESS_RESULT_DATA as TABLE. <br>
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, TRANSFORMER_NAME, DATA, ENCODING
 *
 * [sequence]
 *     
 *
 * [identity]
 *     
 *
 * [version-no]
 *     
 *
 * [foreign table]
 *     ACCESS_RESULT
 *
 * [referrer table]
 *     
 *
 * [foreign property]
 *     accessResult
 *
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfAccessResultData {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<AccessResultData> _selectedList;
    protected BehaviorSelector _selector;
    protected AccessResultDataBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfAccessResultData ready(List<AccessResultData> selectedList, BehaviorSelector selector)
    { _selectedList = selectedList; _selector = selector; return this; }

    protected AccessResultDataBhv myBhv()
    { if (_myBhv != null) { return _myBhv; } else { _myBhv = _selector.select(AccessResultDataBhv.class); return _myBhv; } }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    protected LoaderOfAccessResult _foreignAccessResultLoader;
    public LoaderOfAccessResult pulloutAccessResult() {
        if (_foreignAccessResultLoader == null)
        { _foreignAccessResultLoader = new LoaderOfAccessResult().ready(myBhv().pulloutAccessResult(_selectedList), _selector); }
        return _foreignAccessResultLoader;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<AccessResultData> getSelectedList() { return _selectedList; }
    public BehaviorSelector getSelector() { return _selector; }
}
