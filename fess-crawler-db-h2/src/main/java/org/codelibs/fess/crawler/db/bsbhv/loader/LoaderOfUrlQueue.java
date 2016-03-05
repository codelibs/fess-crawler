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
package org.codelibs.fess.crawler.db.bsbhv.loader;

import java.util.List;

import org.dbflute.bhv.*;
import org.codelibs.fess.crawler.db.exbhv.*;
import org.codelibs.fess.crawler.db.exentity.*;

/**
 * The referrer loader of URL_QUEUE as TABLE. <br>
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, SESSION_ID, METHOD, URL, META_DATA, ENCODING, PARENT_URL, DEPTH, LAST_MODIFIED, CREATE_TIME
 *
 * [sequence]
 *     
 *
 * [identity]
 *     ID
 *
 * [version-no]
 *     
 *
 * [foreign table]
 *     
 *
 * [referrer table]
 *     
 *
 * [foreign property]
 *     
 *
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfUrlQueue {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<UrlQueue> _selectedList;
    protected BehaviorSelector _selector;
    protected UrlQueueBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfUrlQueue ready(List<UrlQueue> selectedList, BehaviorSelector selector)
    { _selectedList = selectedList; _selector = selector; return this; }

    protected UrlQueueBhv myBhv()
    { if (_myBhv != null) { return _myBhv; } else { _myBhv = _selector.select(UrlQueueBhv.class); return _myBhv; } }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<UrlQueue> getSelectedList() { return _selectedList; }
    public BehaviorSelector getSelector() { return _selector; }
}
