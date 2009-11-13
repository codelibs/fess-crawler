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
package org.seasar.robot.dbflute.cbean.pagenavi;

/**
 * The class of page number link.
 * @author jflute
 */
public class PageNumberLink implements java.io.Serializable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected int _pageNumberElement;
    protected boolean _current;
    protected String _pageNumberLinkHref;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PageNumberLink() {
    }

    // ===================================================================================
    //                                                                         Initializer
    //                                                                         ===========
    public PageNumberLink initialize(int pageNumberElement, boolean current, String pageNumberLinkHref) {
        setPageNumberElement(pageNumberElement);
        setCurrent(current);
        setPageNumberLinkHref(pageNumberLinkHref);
        return this;
    }

    // ===================================================================================
    //                                                                      Basic Override
    //                                                                      ==============
    /**
     * @return The view string of all attribute values. (NotNull)
     */
	 @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();

        sb.append(" pageNumberElement=").append(_pageNumberElement);
        sb.append(" pageNumberLinkHref=").append(_pageNumberLinkHref);
        sb.append(" current=").append(_current);

        return sb.toString();
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public int getPageNumberElement() {
        return _pageNumberElement;
    }

    public void setPageNumberElement(int pageNumberElement) {
        this._pageNumberElement = pageNumberElement;
    }

    public boolean isCurrent() {
        return _current;
    }

    public void setCurrent(boolean current) {
        this._current = current;
    }

    public String getPageNumberLinkHref() {
        return _pageNumberLinkHref;
    }

    public void setPageNumberLinkHref(String pageNumberLinkHref) {
        this._pageNumberLinkHref = pageNumberLinkHref;
    }
}
