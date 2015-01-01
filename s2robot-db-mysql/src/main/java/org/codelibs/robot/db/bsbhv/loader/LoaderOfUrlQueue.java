package org.codelibs.robot.db.bsbhv.loader;

import java.util.List;

import org.codelibs.robot.db.exbhv.UrlQueueBhv;
import org.codelibs.robot.db.exentity.UrlQueue;
import org.dbflute.bhv.BehaviorSelector;

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
    public LoaderOfUrlQueue ready(final List<UrlQueue> selectedList,
            final BehaviorSelector selector) {
        _selectedList = selectedList;
        _selector = selector;
        return this;
    }

    protected UrlQueueBhv myBhv() {
        if (_myBhv != null) {
            return _myBhv;
        } else {
            _myBhv = _selector.select(UrlQueueBhv.class);
            return _myBhv;
        }
    }

    // ===================================================================================
    //                                                                    Pull out Foreign
    //                                                                    ================
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public List<UrlQueue> getSelectedList() {
        return _selectedList;
    }

    public BehaviorSelector getSelector() {
        return _selector;
    }
}
