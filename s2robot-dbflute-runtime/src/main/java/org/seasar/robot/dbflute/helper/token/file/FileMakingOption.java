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
package org.seasar.robot.dbflute.helper.token.file;

import java.util.List;

/**
 * @author jflute
 */
public class FileMakingOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Encoding. (Required) */
    protected String _encoding;

    /** Delimiter. (Required) */
    protected String _delimiter;

    /** Line separator. (NotRequired) */
    protected String _lineSeparator;

    /** Quote minimally. (NotRequired) */
    protected boolean _quoteMinimally;

    /** File-making header information. (NotRequired) */
    protected FileMakingHeaderInfo _fileMakingHeaderInfo;

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public FileMakingOption delimitateByComma() {
        _delimiter = ",";
        return this;
    }

    public FileMakingOption delimitateByTab() {
        _delimiter = "\t";
        return this;
    }

    public FileMakingOption encodeAsUTF8() {
        _encoding = "UTF-8";
        return this;
    }

    public FileMakingOption encodeAsWindows31J() {
        _encoding = "Windows-31J";
        return this;
    }

    public FileMakingOption separateCrLf() {
        _lineSeparator = "\r\n";
        return this;
    }

    public FileMakingOption separateLf() {
        _lineSeparator = "\n";
        return this;
    }

    public FileMakingOption quoteMinimally() {
        _quoteMinimally = true;
        return this;
    }

    public FileMakingOption headerInfo(List<String> columnNameList) {
        final FileMakingHeaderInfo fileMakingHeaderInfo = new FileMakingHeaderInfo();
        fileMakingHeaderInfo.setColumnNameList(columnNameList);
        _fileMakingHeaderInfo = fileMakingHeaderInfo;
        return this;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getEncoding() {
        return _encoding;
    }

    public void setEncoding(String encoding) {
        _encoding = encoding;
    }

    public String getDelimiter() {
        return _delimiter;
    }

    public void setDelimiter(String delimiter) {
        _delimiter = delimiter;
    }

    public String getLineSeparator() {
        return _lineSeparator;
    }

    public void setLineSeparator(String lineSeparator) {
        _lineSeparator = lineSeparator;
    }

    public boolean isQuoteMinimally() {
        return _quoteMinimally;
    }

    public FileMakingHeaderInfo getFileMakingHeaderInfo() {
        return _fileMakingHeaderInfo;
    }

    public void setFileMakingHeaderInfo(FileMakingHeaderInfo fileMakingHeaderInfo) {
        _fileMakingHeaderInfo = fileMakingHeaderInfo;
    }
}