package org.seasar.robot.dbflute.exception.factory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.seasar.robot.dbflute.util.DfSystemUtil;

/**
 * @author jflute
 * @since 0.9.6.9 (2010/05/01 Saturday)
 */
public class ExceptionMessageBuilder {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<String> _noticeList = new ArrayList<String>();
    protected final Map<String, List<Object>> _elementMap = new LinkedHashMap<String, List<Object>>();
    protected List<Object> _currentList;

    // ===================================================================================
    //                                                                                 Add
    //                                                                                 ===
    public void addNotice(String notice) {
        _noticeList.add(notice);
    }

    public ExceptionMessageBuilder addItem(String item) {
        _currentList = new ArrayList<Object>();
        _elementMap.put(item, _currentList);
        return this;
    }

    public ExceptionMessageBuilder addElement(Object element) {
        if (_currentList == null) {
            addItem("*No Title");
        }
        _currentList.add(element);
        return this;
    }

    // ===================================================================================
    //                                                                               Build
    //                                                                               =====
    public String buildExceptionMessage() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Look! Read the message below.").append(ln());
        sb.append("/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *").append(ln());
        if (!_noticeList.isEmpty()) {
            for (String notice : _noticeList) {
                sb.append(notice).append(ln());
            }
        } else {
            sb.append("*No Notice").append(ln());
        }
        final Set<Entry<String, List<Object>>> entrySet = _elementMap.entrySet();
        for (Entry<String, List<Object>> entry : entrySet) {
            final String item = entry.getKey();
            sb.append(ln());
            sb.append("[").append(item).append("]").append(ln());
            final List<Object> elementList = entry.getValue();
            for (Object element : elementList) {
                sb.append(element).append(ln());
            }
        }
        sb.append("* * * * * * * * * */");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                      General Helper
    //                                                                      ==============
    protected String ln() {
        return DfSystemUtil.getLineSeparator();
    }
}
