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
package org.codelibs.fess.crawler.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;

/**
 * Utility class for handling response data.
 */
public final class ResponseDataUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ResponseDataUtil() {
    }

    /**
     * Creates a temporary file containing the response body from the given ResponseData.
     *
     * @param responseData the response data containing the response body
     * @return a temporary file containing the response body
     * @throws CrawlingAccessException if an error occurs while reading the response body
     */
    public static File createResponseBodyFile(final ResponseData responseData) {
        File tempFile = null;
        try (final InputStream is = responseData.getResponseBody()) {
            tempFile = File.createTempFile("crawler-", ".tmp");
            tempFile.setReadable(false, false);
            tempFile.setReadable(true, true);
            tempFile.setWritable(false, false);
            tempFile.setWritable(true, true);
            try (final FileOutputStream fos = new FileOutputStream(tempFile)) {
                CopyUtil.copy(is, fos);
            }
        } catch (final Exception e) {
            FileUtil.deleteInBackground(tempFile); // clean up
            throw new CrawlingAccessException("Could not read a response body: " + responseData.getUrl(), e);
        }
        return tempFile;
    }
}
