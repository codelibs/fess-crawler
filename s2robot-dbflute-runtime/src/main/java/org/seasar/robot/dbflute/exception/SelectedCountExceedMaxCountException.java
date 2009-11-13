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
package org.seasar.robot.dbflute.exception;

/**
 * The exception when selected count exceeds max count.
 * @author jflute
 */
public class SelectedCountExceedMaxCountException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** Selected count. */
    protected int _selectedCount;

    /** Max count. */
    protected int _maxCount;

    /**
     * Constructor.
     * 
     * @param msg Exception message. (NotNull)
     * @param maxCount Max count.
     * @param selectedCount Selected count.
     */
    public SelectedCountExceedMaxCountException(String msg, int selectedCount, int maxCount) {
        super(msg);
        _selectedCount = selectedCount;
        _maxCount = maxCount;
    }

    /**
     * Get selected count.
     * @return Selected count.
     */
    public int getSelectedCount() {
        return _selectedCount;
    }

    /**
     * Get max count.
     * @return Max count.
     */
    public int getMaxCount() {
        return _maxCount;
    }
}
