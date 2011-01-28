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
package org.seasar.robot.dbflute.cbean.pagenavi.group;

import java.io.Serializable;

/**
 * The option of page group.
 * @author jflute
 */
public class PageGroupOption implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected int _pageGroupSize;

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * @return The view string of all attribute values. (NotNull)
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(" pageGroupSize=").append(_pageGroupSize);
        sb.append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the size of paga group.
     * @return The size of paga group.
     */
    public int getPageGroupSize() {
        return _pageGroupSize;
    }

    /**
     * Set the size of paga group.
     * <pre>
     * PageGroupOption option = new PageGroupOption();
     * option.<span style="color: #FD4747">setPageGroupSize</span>(10);
     * page.<span style="color: #FD4747">setPageGroupOption</span>(option);
     * List&lt;Integer&gt; numberList = page.<span style="color: #FD4747">pageGroup()</span>.createPageNumberList();
     * 
     * <span style="color: #3F7E5E">//  8 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">// previous 1 2 3 4 5 6 7 8 9 10 next</span>
     * </pre>
     * @param pageGroupSize The size of paga group.
     */
    public void setPageGroupSize(int pageGroupSize) {
        this._pageGroupSize = pageGroupSize;
    }
}
