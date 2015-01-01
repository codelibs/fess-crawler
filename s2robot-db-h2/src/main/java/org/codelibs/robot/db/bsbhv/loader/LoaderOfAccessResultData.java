package org.codelibs.robot.db.bsbhv.loader;

import java.util.List;

import org.codelibs.robot.db.exbhv.AccessResultDataBhv;
import org.codelibs.robot.db.exentity.AccessResultData;
import org.dbflute.bhv.BehaviorSelector;

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
    public LoaderOfAccessResultData ready(
            final List<AccessResultData> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected AccessResultDataBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(AccessResultDataBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    protected LoaderOfAccessResult _foreignAccessResultLoader;

    public LoaderOfAccessResult pulloutAccessResult() {
        if (_foreignAccessResultLoader == null) {
            _foreignAccessResultLoader = new LoaderOfAccessResult().ready(
                    myBhv().pulloutAccessResult(_selectedList), _selector);
        }
        return _foreignAccessResultLoader;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<AccessResultData> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
