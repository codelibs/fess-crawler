/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.extractor;

import java.io.InputStream;
import java.util.Map;

import org.codelibs.fess.crawler.entity.ExtractData;

/**
 * The Extractor interface defines methods for extracting text data from an input stream.
 * Implementations of this interface should provide the logic for extracting text and
 * optionally override the default weight value.
 */
public interface Extractor {

    /**
     * Extracts text data from the given input stream.
     *
     * @param in the input stream to extract text from
     * @param params a map of parameters to be used during extraction
     * @return an ExtractData object containing the extracted text
     */
    ExtractData getText(InputStream in, Map<String, String> params);

    /**
     * Returns the weight of the extractor.
     * The default implementation returns a weight of 1.
     *
     * @return the weight of the extractor
     */
    default int getWeight() {
        return 1;
    }

}
