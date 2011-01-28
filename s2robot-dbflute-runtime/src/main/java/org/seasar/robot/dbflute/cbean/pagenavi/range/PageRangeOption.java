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
package org.seasar.robot.dbflute.cbean.pagenavi.range;

import java.io.Serializable;

/**
 * The option of page range.
 * @author jflute
 */
public class PageRangeOption implements Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected int _pageRangeSize;
    protected boolean _fillLimit;

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
        sb.append("pageRangeSize=").append(_pageRangeSize);
        sb.append(", fillLimit=").append(_fillLimit);
        sb.append("}");
        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the size of page range.
     * @return The size of page range.
     */
    public int getPageRangeSize() {
        return _pageRangeSize;
    }

    /**
     * Set the size of page range.
     * <pre>
     * ex) range-size=5, current-page=8 
     * PageRangeOption option = new PageRangeOption();
     * option.<span style="color: #FD4747">setPageRangeSize</span>(5);
     * page.<span style="color: #FD4747">setPageRangeOption</span>(option);
     * List&lt;Integer&gt; numberList = page.<span style="color: #FD4747">pageRange()</span>.createPageNumberList();
     * 
     * <span style="color: #3F7E5E">//  8 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">// previous</span> <span style="color: #FD4747">3 4 5 6 7 8 9 10 11 12 13</span> <span style="color: #3F7E5E">next</span>
     * </pre>
     * @param pageRangeSize The size of page range.
     */
    public void setPageRangeSize(int pageRangeSize) {
        this._pageRangeSize = pageRangeSize;
    }

    /**
     * Is fill-limit valid?
     * @return Determination.
     */
    public boolean isFillLimit() {
        return _fillLimit;
    }

    /**
     * Set fill-limit option.
     * <pre>
     * ex) range-size=5, current-page=8 
     * PageRangeOption option = new PageRangeOption();
     * option.<span style="color: #FD4747">setPageRangeSize</span>(5);
     * option.<span style="color: #FD4747">setFillLimit</span>(true);
     * page.<span style="color: #FD4747">setPageRangeOption</span>(option);
     * List&lt;Integer&gt; numberList = page.<span style="color: #FD4747">pageRange()</span>.createPageNumberList();
     * 
     * <span style="color: #3F7E5E">//  8 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">// previous</span> <span style="color: #FD4747">3 4 5 6 7 8 9 10 11 12 13</span> <span style="color: #3F7E5E">next</span>
     * 
     * <span style="color: #3F7E5E">// ex) fillLimit=true, current-page=3</span>
     * <span style="color: #3F7E5E">//  3 / 23 pages (453 records)</span>
     * <span style="color: #3F7E5E">//</span> <span style="color: #FD4747">1 2 3 4 5 6 7 8 9 10 11</span> <span style="color: #3F7E5E">next</span>
     * </pre>
     * @param fillLimit Is fill-limit valid?
     */
    public void setFillLimit(boolean fillLimit) {
        this._fillLimit = fillLimit;
    }
}
