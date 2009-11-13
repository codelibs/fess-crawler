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

import org.seasar.robot.dbflute.helper.token.file.FileMakingCallback;
import org.seasar.robot.dbflute.helper.token.file.FileMakingOption;
import org.seasar.robot.dbflute.helper.token.file.FileMakingRowResource;
import org.seasar.robot.dbflute.helper.token.file.FileMakingSimpleFacade;
import org.seasar.robot.dbflute.helper.token.file.FileToken;

/**
 * @author jflute
 */
public class FileMakingSimpleFacadeImpl implements FileMakingSimpleFacade {

    protected FileToken _fileToken = new FileTokenImpl();

    public void setFileToken(FileToken fileToken) {
        this._fileToken = fileToken;
    }

    /**
     * Make token-file from row-list.
     * 
     * @param filename Output target file name. (NotNull)
     * @param rowList Row-list composed of value-list. (NotNull)
     * @param fileMakingOption File-making option. (NotNull and Required{encoding and delimiter})
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void makeFromRowList(final String filename, final java.util.List<java.util.List<String>> rowList, final FileMakingOption fileMakingOption) throws java.io.FileNotFoundException, java.io.IOException {
        final FileMakingCallback fileMakingCallback = new FileMakingCallback() {
            protected int rowCount = 0;
            public FileMakingRowResource getRowResource() {
                ++rowCount;
                if (rowList.size() < rowCount) {
                    return null;// The End!
                }
                final java.util.List<String> valueList = (java.util.List<String>)rowList.get(rowCount - 1);
                final FileMakingRowResource fileMakingRowResource = new FileMakingRowResource();
                fileMakingRowResource.setValueList(valueList);
                return fileMakingRowResource;
            }
        };
        _fileToken.make(filename, fileMakingCallback, fileMakingOption);
    }

    /**
     * Make bytes from row-list.
     * 
     * @param rowList Row-list composed of value-list. (NotNull)
     * @param fileMakingOption File-making option. (NotNull and Required{encoding and delimiter})
     * @return Result byte array. (NotNull)
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public byte[] makeFromRowList(final java.util.List<java.util.List<String>> rowList, final FileMakingOption fileMakingOption) throws java.io.FileNotFoundException, java.io.IOException {
        final FileMakingCallback fileMakingCallback = new FileMakingCallback() {
            protected int rowCount = 0;
            public FileMakingRowResource getRowResource() {
                ++rowCount;
                if (rowList.size() < rowCount) {
                    return null;// The End!
                }
                final java.util.List<String> valueList = (java.util.List<String>)rowList.get(rowCount - 1);
                final FileMakingRowResource fileMakingRowResource = new FileMakingRowResource();
                fileMakingRowResource.setValueList(valueList);
                return fileMakingRowResource;
            }
        };
        final java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        _fileToken.make(baos, fileMakingCallback, fileMakingOption);
        return baos.toByteArray();
    }
}
