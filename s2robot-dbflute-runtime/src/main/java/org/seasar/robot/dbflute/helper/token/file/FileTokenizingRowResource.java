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

/**
 * @author jflute
 */
public class FileTokenizingRowResource {

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected FileTokenizingHeaderInfo fileTokenizingHeaderInfo;

    protected java.util.List<String> valueList;

    protected String rowString;

    /** The row number. */
    protected int _rowNumber;

    /** The line number. */
    protected int _lineNumber;

    // =====================================================================================
    //                                                                              Accessor
    //                                                                              ========
    public FileTokenizingHeaderInfo getFileTokenizingHeaderInfo() {
        return fileTokenizingHeaderInfo;
    }

    public void setFirstLineInfo(FileTokenizingHeaderInfo fileTokenizingHeaderInfo) {
        this.fileTokenizingHeaderInfo = fileTokenizingHeaderInfo;
    }

    public java.util.List<String> getValueList() {
        return valueList;
    }

    public void setValueList(java.util.List<String> valueList) {
        this.valueList = valueList;
    }

    public String getRowString() {
        return rowString;
    }

    public void setRowString(String rowString) {
        this.rowString = rowString;
    }

    public int getRowNumber() {
        return _rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        _rowNumber = rowNumber;
    }

    public int getLineNumber() {
        return _lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        _lineNumber = lineNumber;
    }
}
