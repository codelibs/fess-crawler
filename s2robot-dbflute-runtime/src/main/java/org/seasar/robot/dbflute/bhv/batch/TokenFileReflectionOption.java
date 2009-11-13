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
package org.seasar.robot.dbflute.bhv.batch;

import org.seasar.robot.dbflute.helper.token.file.FileTokenizingOption;

/**
 * @author jflute
 */
public class TokenFileReflectionOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _interruptIfError;
    protected FileTokenizingOption _fileTokenizingOption = new FileTokenizingOption();

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public TokenFileReflectionOption delimitateByComma() {
        _fileTokenizingOption.delimitateByComma();
        return this;
    }

    public TokenFileReflectionOption delimitateByTab() {
        _fileTokenizingOption.delimitateByTab();
        return this;
    }

    public TokenFileReflectionOption encodeAsUTF8() {
        _fileTokenizingOption.encodeAsUTF8();
        return this;
    }

    public TokenFileReflectionOption encodeAsWindows31J() {
        _fileTokenizingOption.encodeAsWindows31J();
        return this;
    }

    public TokenFileReflectionOption handleEmptyAsNull() {
        _fileTokenizingOption.handleEmptyAsNull();
        return this;
    }

    public TokenFileReflectionOption interruptIfError() {
        _interruptIfError = true;
        return this;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getDelimiter() {
        return _fileTokenizingOption.getDelimiter();
    }

    public void setDelimiter(String delimiter) {
        _fileTokenizingOption.setDelimiter(delimiter);
    }

    public String getEncoding() {
        return _fileTokenizingOption.getEncoding();
    }

    public void setEncoding(String encoding) {
        _fileTokenizingOption.setDelimiter(encoding);
    }

    public boolean isHandleEmptyAsNull() {
        return _fileTokenizingOption.isHandleEmptyAsNull();
    }

    public boolean isInterruptIfError() {
        return _interruptIfError;
    }
}
