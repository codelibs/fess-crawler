package org.codelibs.fess.crawler.db.bsbhv.loader;

import java.util.List;

import org.dbflute.bhv.*;
import org.codelibs.fess.crawler.db.exbhv.*;
import org.codelibs.fess.crawler.db.exentity.*;

/**
 * The referrer loader of ACCESS_RESULT as TABLE. <br>
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, SESSION_ID, RULE_ID, URL, PARENT_URL, STATUS, HTTP_STATUS_CODE, METHOD, MIME_TYPE, CONTENT_LENGTH, EXECUTION_TIME, LAST_MODIFIED, CREATE_TIME
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
 *     ACCESS_RESULT_DATA(AsOne)
 *
 * [referrer table]
 *     ACCESS_RESULT_DATA
 *
 * [foreign property]
 *     accessResultDataAsOne
 *
 * [referrer property]
 *     
 * </pre>
 * @author DBFlute(AutoGenerator)
 */
public class LoaderOfAccessResult {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<AccessResult> _selectedList;
    protected BehaviorSelector _selector;
    protected AccessResultBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfAccessResult ready(List<AccessResult> selectedList, BehaviorSelector selector)
    { _selectedList = selectedList; _selector = selector; return this; }

    protected AccessResultBhv myBhv()
    { if (_myBhv != null) { return _myBhv; } else { _myBhv = _selector.select(AccessResultBhv.class); return _myBhv; } }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    protected LoaderOfAccessResultData _foreignAccessResultDataAsOneLoader;
    public LoaderOfAccessResultData pulloutAccessResultDataAsOne() {
        if (_foreignAccessResultDataAsOneLoader == null)
        { _foreignAccessResultDataAsOneLoader = new LoaderOfAccessResultData().ready(myBhv().pulloutAccessResultDataAsOne(_selectedList), _selector); }
        return _foreignAccessResultDataAsOneLoader;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<AccessResult> getSelectedList() { return _selectedList; }
    public BehaviorSelector getSelector() { return _selector; }
}
