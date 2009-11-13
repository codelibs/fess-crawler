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
package org.seasar.robot.dbflute.helper.token.file.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.robot.dbflute.helper.token.file.FileMakingCallback;
import org.seasar.robot.dbflute.helper.token.file.FileMakingHeaderInfo;
import org.seasar.robot.dbflute.helper.token.file.FileMakingOption;
import org.seasar.robot.dbflute.helper.token.file.FileMakingRowResource;
import org.seasar.robot.dbflute.helper.token.file.FileToken;
import org.seasar.robot.dbflute.helper.token.file.FileTokenizingCallback;
import org.seasar.robot.dbflute.helper.token.file.FileTokenizingHeaderInfo;
import org.seasar.robot.dbflute.helper.token.file.FileTokenizingOption;
import org.seasar.robot.dbflute.helper.token.file.FileTokenizingRowResource;
import org.seasar.robot.dbflute.helper.token.line.LineMakingOption;
import org.seasar.robot.dbflute.helper.token.line.LineToken;
import org.seasar.robot.dbflute.helper.token.line.LineTokenizingOption;
import org.seasar.robot.dbflute.helper.token.line.impl.LineTokenImpl;
import org.seasar.robot.dbflute.util.DfStringUtil;

/**
 * @author jflute
 */
public class FileTokenImpl implements FileToken {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    /** Line-token for help. */
    protected final LineToken _lineToken = new LineTokenImpl();

    // ===================================================================================
    //                                                                            Tokenize
    //                                                                            ========
    /**
     * Tokenize token-file data of a specified file.
     * @param filename File name. (NotNull)
     * @param fileTokenizingCallback File-tokenizing callback. (NotNull)
     * @param fileTokenizingOption File-tokenizing option. (NotNull and Required{encoding and delimiter})
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void tokenize(String filename, FileTokenizingCallback fileTokenizingCallback,
            FileTokenizingOption fileTokenizingOption) throws java.io.FileNotFoundException, java.io.IOException {
        assertStringNotNullAndNotTrimmedEmpty("filename", filename);

        java.io.FileInputStream fis = null;
        try {
            fis = new java.io.FileInputStream(filename);
            tokenize(fis, fileTokenizingCallback, fileTokenizingOption);
        } catch (java.io.FileNotFoundException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw e;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (java.io.IOException ignored) {
            }
        }
    }

    /**
     * Tokenize token-file data of a specified file.
     * <pre>
     * This method uses java.io.InputStreamReader and java.io.BufferedReader that wrap the argument[inputStream].
     * These objects are closed. (Invoking close() at finally)
     * </pre>
     * @param inputStream Input target stream. (NotNull)
     * @param fileTokenizingCallback File-tokenizing callback. (NotNull)
     * @param fileTokenizingOption File-tokenizing option. (NotNull and Required{encoding and delimiter})
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void tokenize(java.io.InputStream inputStream, FileTokenizingCallback fileTokenizingCallback,
            FileTokenizingOption fileTokenizingOption) throws java.io.FileNotFoundException, java.io.IOException {
        assertObjectNotNull("inputStream", inputStream);
        assertObjectNotNull("fileTokenizingCallback", fileTokenizingCallback);
        assertObjectNotNull("fileTokenizingOption", fileTokenizingOption);
        final String delimiter = fileTokenizingOption.getDelimiter();
        final String encoding = fileTokenizingOption.getEncoding();
        assertStringNotNullAndNotTrimmedEmpty("encoding", encoding);
        assertObjectNotNull("delimiter", delimiter);

        java.io.InputStreamReader ir = null;
        java.io.BufferedReader br = null;

        String lineString = null;
        String preContinueString = "";
        final List<String> temporaryValueList = new ArrayList<String>();
        final List<String> filteredValueList = new ArrayList<String>();

        try {
            ir = new java.io.InputStreamReader(inputStream, encoding);
            br = new java.io.BufferedReader(ir);

            FileTokenizingHeaderInfo fileTokenizingHeaderInfo = null;
            int count = -1;
            int rowNumber = 1;
            int lineNumber = 0;
            while (true) {
                ++count;
                if ("".equals(preContinueString)) {
                    lineNumber = count + 1;
                }

                lineString = br.readLine();
                if (lineString == null) {
                    break;
                }
                if (count == 0) {
                    if (fileTokenizingOption.isBeginFirstLine()) {
                        fileTokenizingHeaderInfo = new FileTokenizingHeaderInfo();// As empty
                    } else {
                        fileTokenizingHeaderInfo = analyzeHeaderInfo(delimiter, lineString);
                        continue;
                    }
                }
                final String rowString;
                if (preContinueString.equals("")) {
                    rowString = lineString;
                } else {
                    final String lineSeparator = "\n";
                    rowString = preContinueString + lineSeparator + lineString;
                }
                final ValueLineInfo valueLineInfo = arrangeValueList(rowString, delimiter);
                final List<String> ls = valueLineInfo.getValueList();
                if (valueLineInfo.isContinueNextLine()) {
                    preContinueString = (String) ls.remove(ls.size() - 1);
                    temporaryValueList.addAll(ls);
                    continue;
                }
                temporaryValueList.addAll(ls);

                try {
                    final FileTokenizingRowResource fileTokenizingRowResource = new FileTokenizingRowResource();
                    fileTokenizingRowResource.setFirstLineInfo(fileTokenizingHeaderInfo);

                    if (fileTokenizingOption.isHandleEmptyAsNull()) {
                        for (final Iterator<String> ite = temporaryValueList.iterator(); ite.hasNext();) {
                            final String value = (String) ite.next();
                            if ("".equals(value)) {
                                filteredValueList.add(null);
                            } else {
                                filteredValueList.add(value);
                            }
                        }
                        fileTokenizingRowResource.setValueList(filteredValueList);
                    } else {
                        fileTokenizingRowResource.setValueList(temporaryValueList);
                    }

                    fileTokenizingRowResource.setRowString(rowString);
                    fileTokenizingRowResource.setRowNumber(rowNumber);
                    fileTokenizingRowResource.setLineNumber(lineNumber);
                    fileTokenizingCallback.handleRowResource(fileTokenizingRowResource);
                } finally {
                    ++rowNumber;
                    temporaryValueList.clear();
                    filteredValueList.clear();
                    preContinueString = "";
                }
            }
        } catch (java.io.FileNotFoundException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw e;
        } finally {
            try {
                if (ir != null) {
                    ir.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (java.io.IOException ignored) {
            }
        }
    }

    protected ValueLineInfo arrangeValueList(final String lineString, String delimiter) {
        final List<String> valueList = new ArrayList<String>();

        // Don't use split!
        //final String[] values = lineString.split(delimiter);
        final LineTokenizingOption tokenizingOption = new LineTokenizingOption();
        tokenizingOption.setDelimiter(delimiter);
        final List<String> list = _lineToken.tokenize(lineString, tokenizingOption);
        final String[] values = (String[]) list.toArray(new String[list.size()]);
        for (int i = 0; i < values.length; i++) {
            valueList.add(values[i]);
        }
        return arrangeValueList(valueList, delimiter);
    }

    protected ValueLineInfo arrangeValueList(List<String> valueList, String delimiter) {
        final ValueLineInfo valueLineInfo = new ValueLineInfo();
        final ArrayList<String> resultList = new ArrayList<String>();
        String preString = "";
        for (int i = 0; i < valueList.size(); i++) {
            final String value = (String) valueList.get(i);
            if (value == null) {
                continue;
            }
            if (i == valueList.size() - 1) { // The last loop
                if (preString.equals("")) {
                    if (isFrontQOnly(value)) {
                        valueLineInfo.setContinueNextLine(true);
                        resultList.add(value);
                    } else if (isRearQOnly(value)) {
                        resultList.add(value);
                    } else if (isNotBothQ(value)) {
                        resultList.add(value);
                    } else {
                        resultList.add(removeDoubleQuotation(value));
                    }
                } else {
                    if (endsQuote(value, false)) {
                        resultList.add(removeDoubleQuotation(connectPreString(preString, delimiter, value)));
                    } else {
                        valueLineInfo.setContinueNextLine(true);
                        resultList.add(connectPreString(preString, delimiter, value));
                    }
                }
                break; // because it's the last loop
            }

            if (preString.equals("")) {
                if (isFrontQOnly(value)) {
                    preString = value;
                    continue;
                } else if (isRearQOnly(value)) {
                    preString = value;
                    continue;
                } else if (isNotBothQ(value)) {
                    resultList.add(value);
                } else {
                    resultList.add(removeDoubleQuotation(value));
                }
            } else {
                if (endsQuote(value, false)) {
                    resultList.add(removeDoubleQuotation(connectPreString(preString, delimiter, value)));
                } else {
                    preString = connectPreString(preString, delimiter, value);
                    continue;
                }
            }
            preString = "";
        }
        valueLineInfo.setValueList(resultList);
        return valueLineInfo;
    }

    protected String connectPreString(String preString, String delimiter, String value) {
        if (preString.equals("")) {
            return value;
        } else {
            return preString + delimiter + value;
        }
    }

    protected boolean isNotBothQ(final String value) {
        return !isQQ(value) && !value.startsWith("\"") && !endsQuote(value, false);
    }

    protected boolean isRearQOnly(final String value) {
        return !isQQ(value) && !value.startsWith("\"") && endsQuote(value, false);
    }

    protected boolean isFrontQOnly(final String value) {
        return !isQQ(value) && value.startsWith("\"") && !endsQuote(value, true);
    }

    protected boolean isQQ(final String value) {
        return value.equals("\"\"");
    }

    protected boolean endsQuote(String value, boolean startsQuote) {
        value = startsQuote ? value.substring(1) : value;
        final int length = value.length();
        int count = 0;
        for (int i = 0; i < length; i++) {
            char ch = value.charAt(length - (i + 1));
            if (ch == '\"') {
                ++count;
            } else {
                break;
            }
        }
        return count > 0 && isOddNumber(count);
    }

    protected boolean isOddNumber(int number) {
        return (number % 2) != 0;
    }

    protected String removeDoubleQuotation(String value) {
        if (!value.startsWith("\"") && !value.endsWith("\"")) {
            return value;
        }
        if (value.startsWith("\"")) {
            value = value.substring(1);
        }
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        value = DfStringUtil.replace(value, "\"\"", "\"");
        return value;
    }

    protected String removeRightDoubleQuotation(String value) {
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        return value;
    }

    protected FileTokenizingHeaderInfo analyzeHeaderInfo(String delimiter, final String lineString) {
        final java.util.List<String> columnNameList = new ArrayList<String>();
        final String[] values = lineString.split(delimiter);
        for (int i = 0; i < values.length; i++) {
            final String value = values[i].trim();// Trimming is Header Only!;
            if (value.startsWith("\"") && value.endsWith("\"")) {
                columnNameList.add(value.substring(1, value.length() - 1));
            } else {
                columnNameList.add(value);
            }
        }
        final FileTokenizingHeaderInfo fileTokenizingHeaderInfo = new FileTokenizingHeaderInfo();
        fileTokenizingHeaderInfo.setColumnNameList(columnNameList);
        fileTokenizingHeaderInfo.setColumnNameRowString(lineString);
        return fileTokenizingHeaderInfo;
    }

    public static class ValueLineInfo {
        protected java.util.List<String> valueList;

        protected boolean continueNextLine;

        public java.util.List<String> getValueList() {
            return valueList;
        }

        public void setValueList(List<String> valueList) {
            this.valueList = valueList;
        }

        public boolean isContinueNextLine() {
            return continueNextLine;
        }

        public void setContinueNextLine(boolean continueNextLine) {
            this.continueNextLine = continueNextLine;
        }
    }

    // ===================================================================================
    //                                                                                Make
    //                                                                                ====
    /**
     * Make token-file from specified row resources.
     * @param filename File name. (NotNull)
     * @param fileMakingCallback File-making callback. (NotNull)
     * @param fileMakingOption File-making option. (NotNull and Required{encoding and delimiter})
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void make(String filename, FileMakingCallback fileMakingCallback, FileMakingOption fileMakingOption)
            throws java.io.FileNotFoundException, java.io.IOException {
        assertStringNotNullAndNotTrimmedEmpty("filename", filename);

        java.io.FileOutputStream fos = null;
        try {
            fos = new java.io.FileOutputStream(filename);
            make(fos, fileMakingCallback, fileMakingOption);
        } catch (java.io.FileNotFoundException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw e;
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Make token-file from specified row resources.
     * <pre>
     * This method uses java.io.BufferedOutputStream and java.io.OutputStreamWriter that wrap the argument[outputStream].
     * These objects are closed. (Invoking close() at finally)
     * </pre>
     * @param outputStream Output target stream. (NotNull)
     * @param fileMakingCallback File-making callback. (NotNull)
     * @param fileMakingOption File-making option. (NotNull and Required{encoding and delimiter})
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void make(java.io.OutputStream outputStream, FileMakingCallback fileMakingCallback,
            FileMakingOption fileMakingOption) throws java.io.FileNotFoundException, java.io.IOException {
        assertObjectNotNull("outputStream", outputStream);
        assertObjectNotNull("fileMakingCallback", fileMakingCallback);
        assertObjectNotNull("fileMakingOption", fileMakingOption);
        final String encoding = fileMakingOption.getEncoding();
        final String delimiter = fileMakingOption.getDelimiter();
        assertStringNotNullAndNotTrimmedEmpty("encoding", encoding);
        assertObjectNotNull("delimiter", delimiter);
        final String lineSeparator;
        if (fileMakingOption.getLineSeparator() != null && !fileMakingOption.getLineSeparator().equals("")) {
            lineSeparator = fileMakingOption.getLineSeparator();
        } else {
            lineSeparator = "\n"; // Default!
        }

        java.io.BufferedOutputStream bos = null;
        java.io.Writer writer = null;
        try {
            bos = new java.io.BufferedOutputStream(outputStream);
            writer = new java.io.OutputStreamWriter(bos, encoding);

            boolean headerDone = false;

            // Make header.
            final FileMakingHeaderInfo fileMakingHeaderInfo = fileMakingOption.getFileMakingHeaderInfo();
            if (fileMakingHeaderInfo != null) {
                final List<String> columnNameList = fileMakingHeaderInfo.getColumnNameList();
                if (columnNameList != null && !columnNameList.isEmpty()) {
                    final LineMakingOption lineMakingOption = new LineMakingOption();
                    lineMakingOption.setDelimiter(delimiter);
                    lineMakingOption.trimSpace();// Trimming is Header Only!
                    final String columnHeaderString = _lineToken.make(columnNameList, lineMakingOption);
                    writer.write(columnHeaderString + lineSeparator);
                    headerDone = true;
                }
            }

            // Make row.
            FileMakingRowResource rowResource = null;
            while (true) {
                rowResource = fileMakingCallback.getRowResource();
                if (rowResource == null) {
                    break;// The End!
                }
                final java.util.List<String> valueList;
                if (rowResource.getValueList() != null) {
                    valueList = rowResource.getValueList();
                } else {
                    final java.util.LinkedHashMap<String, String> nameValueMap = rowResource.getNameValueMap();
                    if (!headerDone) {
                        final java.util.List<String> columnNameList = new java.util.ArrayList<String>(nameValueMap
                                .keySet());
                        final LineMakingOption lineMakingOption = new LineMakingOption();
                        lineMakingOption.setDelimiter(delimiter);
                        lineMakingOption.trimSpace();// Trimming is Header Only!
                        final String columnHeaderString = _lineToken.make(columnNameList, lineMakingOption);
                        writer.write(columnHeaderString + lineSeparator);
                        headerDone = true;
                    }
                    valueList = new ArrayList<String>(nameValueMap.values());
                }
                final LineMakingOption lineMakingOption = new LineMakingOption();
                lineMakingOption.setDelimiter(delimiter);
                if (fileMakingOption.isQuoteMinimally()) {
                    lineMakingOption.quoteMinimally();
                } else {
                    lineMakingOption.quoteAll();
                }
                final String lineString = _lineToken.make(valueList, lineMakingOption);
                writer.write(lineString + lineSeparator);
            }
            writer.flush();
        } catch (java.io.FileNotFoundException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw e;
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    // -----------------------------------------------------
    //                                         Assert Object
    //                                         -------------
    /**
     * Assert that the object is not null.
     * 
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     * @exception IllegalArgumentException
     */
    protected void assertObjectNotNull(String variableName, Object value) {
        if (variableName == null) {
            String msg = "The value should not be null: variableName=null value=" + value;
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            String msg = "The value should not be null: variableName=" + variableName;
            throw new IllegalArgumentException(msg);
        }
    }

    // -----------------------------------------------------
    //                                         Assert String
    //                                         -------------
    /**
     * Assert that the entity is not null and not trimmed empty.
     * 
     * @param variableName Variable name. (NotNull)
     * @param value Value. (NotNull)
     */
    protected void assertStringNotNullAndNotTrimmedEmpty(String variableName, String value) {
        assertObjectNotNull("variableName", variableName);
        assertObjectNotNull(variableName, value);
        if (value.trim().length() == 0) {
            String msg = "The value should not be empty: variableName=" + variableName + " value=" + value;
            throw new IllegalArgumentException(msg);
        }
    }
}