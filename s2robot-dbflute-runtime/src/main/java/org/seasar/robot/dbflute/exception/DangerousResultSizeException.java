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
 * The exception of when the result size is dangerous.
 * @author jflute
 */
public class DangerousResultSizeException extends RuntimeException {

    /** Serial version UID. (Default) */
    private static final long serialVersionUID = 1L;

    /** The max size of safety result. */
    protected int _safetyMaxResultSize;

    /** The actual size of result. */
    protected int _actualResultSize;

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     * @param safetyMaxResultSize The max size of safety result. (NotZero, ZotMinus)
     */
    public DangerousResultSizeException(String msg, int safetyMaxResultSize) {
        super(msg);
        this._safetyMaxResultSize = safetyMaxResultSize;
    }

    /**
     * Constructor.
     * @param msg Exception message. (NotNull)
     * @param safetyMaxResultSize The max size of safety result. (NotZero, ZotMinus)
     * @param actualResultSize The actual size of result. (NotZero, ZotMinus, GraeterThanMaxSize)
     */
    public DangerousResultSizeException(String msg, int safetyMaxResultSize, int actualResultSize) {
        super(msg);
        this._safetyMaxResultSize = safetyMaxResultSize;
        this._actualResultSize = actualResultSize;
    }

    /**
     * Get the max size of safety result.
     * @return The max size of safety result. (Basically returns a plus value)
     */
    public int getSafetyMaxResultSize() {
        return _safetyMaxResultSize;
    }

    /**
     * Get the actual size of result.
     * @return The actual size of result. (If the value is minus, it means it's unknown)
     */
    public int getActualResultSize() {
        return _actualResultSize;
    }
}
