/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

/**
 * The option of page range.
 * @author jflute
 */
public class PageRangeOption implements java.io.Serializable {

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

        sb.append(" pageRangeSize=").append(_pageRangeSize);
        sb.append(" fillLimit=").append(_fillLimit);

        return sb.toString();
    }
	
    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public int getPageRangeSize() {
        return _pageRangeSize;
    }

    public void setPageRangeSize(int pageRangeSize) {
        this._pageRangeSize = pageRangeSize;
    }

    public boolean isFillLimit() {
        return _fillLimit;
    }

    public void setFillLimit(boolean fillLimit) {
        this._fillLimit = fillLimit;
    }
}
