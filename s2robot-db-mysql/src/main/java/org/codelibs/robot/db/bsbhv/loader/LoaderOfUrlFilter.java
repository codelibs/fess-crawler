package org.codelibs.robot.db.bsbhv.loader;

import java.util.List;

import org.codelibs.robot.db.exbhv.UrlFilterBhv;
import org.codelibs.robot.db.exentity.UrlFilter;
import org.dbflute.bhv.BehaviorSelector;

/**
 * The referrer loader of URL_FILTER as TABLE. <br>
 * <pre>
 * [primary key]
 *     ID
 *
 * [column]
 *     ID, SESSION_ID, URL, FILTER_TYPE, CREATE_TIME
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
public class LoaderOfUrlFilter {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected List<UrlFilter> _selectedList;

    protected BehaviorSelector _selector;

    protected UrlFilterBhv _myBhv; // lazy-loaded

    // ===================================================================================
    //                                                                   Ready for Loading
    //                                                                   =================
    public LoaderOfUrlFilter ready(final List<UrlFilter> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected UrlFilterBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(UrlFilterBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<UrlFilter> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
