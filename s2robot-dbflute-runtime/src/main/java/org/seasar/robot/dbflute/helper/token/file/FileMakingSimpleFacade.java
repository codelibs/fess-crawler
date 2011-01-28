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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author jflute
 */
public interface FileMakingSimpleFacade {

    /**
     * Make token-file from row-list.
     * @param filename Output target file name. (NotNull)
     * @param rowList Row-list composed of value-list. (NotNull)
     * @param fileMakingOption File-making option. (NotNull and Required{encoding and delimiter})
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void makeFromRowList(final String filename, final java.util.List<java.util.List<String>> rowList,
            final FileMakingOption fileMakingOption) throws FileNotFoundException, IOException;

    /**
     * Make bytes from row-list.
     * @param rowList Row-list composed of value-list. (NotNull)
     * @param fileMakingOption File-making option. (NotNull and Required{encoding and delimiter})
     * @return Result byte array. (NotNull)
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public byte[] makeFromRowList(final List<List<String>> rowList, final FileMakingOption fileMakingOption)
            throws FileNotFoundException, IOException;
}
