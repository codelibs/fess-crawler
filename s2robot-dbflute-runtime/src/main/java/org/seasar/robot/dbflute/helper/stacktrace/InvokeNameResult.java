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
package org.seasar.robot.dbflute.helper.stacktrace;

/**
 * @author jflute
 */
public class InvokeNameResult {

    // ==========================================================================================
    //                                                                                  Attribute
    //                                                                                  =========
    protected String _simpleClassName;
    protected String _methodName;
    protected String _invokeName;
    protected int _foundIndex;
    protected int _foundFirstIndex;

    // ==========================================================================================
    //                                                                               Manipulation
    //                                                                               ============
    public int getNextStartIndex() {
        return _foundIndex + 1;
    }

    // ==========================================================================================
    //                                                                              Determination
    //                                                                              =============
    public boolean isEmptyResult() {
        return _simpleClassName == null;
    }

    // ==========================================================================================
    //                                                                                   Accessor
    //                                                                                   ========
    public String getSimpleClassName() {
        return _simpleClassName;
    }

    public void setSimpleClassName(String simpleClassName) {
        _simpleClassName = simpleClassName;
    }

    public String getMethodName() {
        return _methodName;
    }

    public void setMethodName(String methodName) {
        _methodName = methodName;
    }

    public String getInvokeName() {
        return _invokeName;
    }

    public void setInvokeName(String invokeName) {
        _invokeName = invokeName;
    }

    public int getFoundIndex() {
        return _foundIndex;
    }

    public void setFoundIndex(int foundIndex) {
        _foundIndex = foundIndex;
    }

    public int getFoundFirstIndex() {
        return _foundFirstIndex;
    }

    public void setFoundFirstIndex(int foundFirstIndex) {
        _foundFirstIndex = foundFirstIndex;
    }
}