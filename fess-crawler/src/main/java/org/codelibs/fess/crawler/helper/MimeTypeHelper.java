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
package org.codelibs.fess.crawler.helper;

import java.io.InputStream;
import java.util.Map;

/**
 * MimeTypeHelper provides methods to determine the content type of a given input stream or file.
 * It allows content type detection based on the stream's content and/or filename.
 */
public interface MimeTypeHelper {
    /**
     * Determines the content type of the given input stream and filename.
     * @param is the input stream to analyze
     * @param filename the filename to help determine the content type
     * @return the detected content type
     */
    String getContentType(InputStream is, String filename);

    /**
     * Determines the content type of the given input stream using the provided parameters.
     * @param is the input stream to analyze
     * @param params the parameters containing additional information like filename
     * @return the detected content type
     */
    String getContentType(InputStream is, Map<String, String> params);
}
