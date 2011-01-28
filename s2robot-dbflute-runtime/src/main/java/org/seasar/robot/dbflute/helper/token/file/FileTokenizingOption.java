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
package org.seasar.robot.dbflute.helper.token.file;

/**
 * @author jflute
 */
public class FileTokenizingOption {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected String _encoding;

    protected String _delimiter;

    protected boolean _beginFirstLine;

    protected boolean _handleEmptyAsNull;

    // =====================================================================================
    //                                                                           Easy-to-Use
    //                                                                           ===========
    public FileTokenizingOption delimitateByComma() {
        _delimiter = ",";
        return this;
    }

    public FileTokenizingOption delimitateByTab() {
        _delimiter = "\t";
        return this;
    }

    public FileTokenizingOption encodeAsUTF8() {
        _encoding = "UTF-8";
        return this;
    }

    public FileTokenizingOption encodeAsWindows31J() {
        _encoding = "Windows-31J";
        return this;
    }

    public FileTokenizingOption beginFirstLine() {
        _beginFirstLine = true;
        return this;
    }

    public FileTokenizingOption handleEmptyAsNull() {
        _handleEmptyAsNull = true;
        return this;
    }

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public String getDelimiter() {
        return _delimiter;
    }

    public void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    public String getEncoding() {
        return _encoding;
    }

    public void setEncoding(String encoding) {
        _encoding = encoding;
    }

    public boolean isBeginFirstLine() {
        return _beginFirstLine;
    }

    public boolean isHandleEmptyAsNull() {
        return _handleEmptyAsNull;
    }
}