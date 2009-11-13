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

import org.seasar.robot.dbflute.helper.token.file.FileMakingOption;

/**
 * @author jflute
 */
public class TokenFileOutputOption {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected FileMakingOption _fileMakingOption = new FileMakingOption();

    // ===================================================================================
    //                                                                         Easy-to-Use
    //                                                                         ===========
    public TokenFileOutputOption delimitateByComma() {
        _fileMakingOption.delimitateByComma();
        return this;
    }

    public TokenFileOutputOption delimitateByTab() {
        _fileMakingOption.delimitateByTab();
        return this;
    }

    public TokenFileOutputOption encodeAsUTF8() {
        _fileMakingOption.encodeAsUTF8();
        return this;
    }

    public TokenFileOutputOption encodeAsWindows31J() {
        _fileMakingOption.encodeAsWindows31J();
        return this;
    }

    public TokenFileOutputOption separateCrLf() {
        _fileMakingOption.separateCrLf();
        return this;
    }

    public TokenFileOutputOption separateLf() {
        _fileMakingOption.separateLf();
        return this;
    }

    public TokenFileOutputOption quoteMinimally() {
        _fileMakingOption.quoteMinimally();
        return this;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getEncoding() {
        return _fileMakingOption.getEncoding();
    }

    public void setEncoding(String encoding) {
        _fileMakingOption.setDelimiter(encoding);
    }

    public String getDelimiter() {
        return _fileMakingOption.getDelimiter();
    }

    public void setDelimiter(String delimiter) {
        _fileMakingOption.setDelimiter(delimiter);
    }

    public String getLineSeparator() {
        return _fileMakingOption.getLineSeparator();
    }

    public void setLineSeparator(String lineSeparator) {
        _fileMakingOption.setLineSeparator(lineSeparator);
    }

    public boolean isQuoteMinimally() {
        return _fileMakingOption.isQuoteMinimally();
    }

    public FileMakingOption getFileMakingOption() {
        return _fileMakingOption;
    }
}
