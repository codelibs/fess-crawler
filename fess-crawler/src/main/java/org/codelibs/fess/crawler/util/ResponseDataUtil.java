/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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

import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;

/**
 * @author shinsuke
 *
 */
public final class ResponseDataUtil {

    private ResponseDataUtil() {
    }

    public static File createResponseBodyFile(final ResponseData responseData) {
        File tempFile = null;
        FileOutputStream fos = null;
        try (final InputStream is = responseData.getResponseBody()) {
            tempFile = File.createTempFile("crawler-", ".tmp");
            fos = new FileOutputStream(tempFile);
            CopyUtil.copy(is, fos);
        } catch (final Exception e) {
            CloseableUtil.closeQuietly(fos); // for deleting file
            FileUtil.deleteInBackground(tempFile); // clean up
            throw new CrawlingAccessException("Could not read a response body: " + responseData.getUrl(), e);
        } finally {
            CloseableUtil.closeQuietly(fos);
        }
        return tempFile;
    }
}
