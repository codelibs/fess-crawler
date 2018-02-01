/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.transformer.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.codelibs.core.io.CopyUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileTransformer stores WEB data as a file path.
 *
 * @author shinsuke
 *
 */
public class FileTransformer extends HtmlTransformer {
    private static final Logger logger = LoggerFactory
            .getLogger(FileTransformer.class);

    /**
     * A path to store downloaded files. The default path is a current
     * directory.
     */
    protected String path;

    /**
     * A string to replace ?.
     */
    protected String questionStr = "_QUEST_";

    /**
     * A string to replace :.
     */
    protected String colonStr = "_CLN_";

    /**
     * A string to replace ;.
     */
    protected String semicolonStr = "_SCLN_";

    /**
     * A string to replace &amp;.
     */
    protected String ampersandStr = "_AMP_";

    protected int maxDuplicatedPath = 100;

    protected String charsetName = Constants.UTF_8;

    /**
     * A directory to store downloaded files.
     */
    protected File baseDir;

    protected File createFile(final String path) {
        final String[] paths = path.split("/");
        File targetFile = baseDir;
        for (int i = 0; i < paths.length - 1; i++) {
            File file = new File(targetFile, paths[i]);
            if (file.exists()) {
                if (!file.isDirectory()) {
                    for (int j = 0; j < maxDuplicatedPath; j++) {
                        file = new File(targetFile, paths[i] + "_" + j);
                        if (file.exists()) {
                            if (file.isDirectory()) {
                                break;
                            }
                        } else {
                            if (!file.mkdirs()) {
                                throw new CrawlerSystemException(
                                        "Could not create "
                                                + file.getAbsolutePath());
                            }
                            break;
                        }
                    }
                }
            } else {
                if (!file.mkdirs()) {
                    throw new CrawlerSystemException("Could not create "
                            + file.getAbsolutePath());
                }
            }
            targetFile = file;
        }

        File file = new File(targetFile, paths[paths.length - 1]);
        if (file.exists()) {
            for (int i = 0; i < maxDuplicatedPath; i++) {
                file = new File(targetFile, paths[paths.length - 1] + "_" + i);
                if (!file.exists()) {
                    targetFile = file;
                    break;
                }
            }
        } else {
            targetFile = file;
        }
        return targetFile;
    }

    @Override
    public void storeData(final ResponseData responseData,
            final ResultData resultData) {
        resultData.setTransformerName(getName());

        initBaseDir();

        final String url = responseData.getUrl();
        final String path = getFilePath(url);

        synchronized (this) {

            final File file = createFile(path);

            try (final InputStream is = responseData.getResponseBody(); final OutputStream os = new FileOutputStream(file);) {
                CopyUtil.copy(is, os);
            } catch (final IOException e) {
                throw new CrawlerSystemException("Could not store "
                        + file.getAbsolutePath(), e);
            }
        }
        try {
            resultData.setData(path.getBytes(charsetName));
        } catch (final UnsupportedEncodingException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Invalid charsetName: " + charsetName
                        + ". Changed to " + Constants.UTF_8, e);
            }
            charsetName = Constants.UTF_8_CHARSET.name();
            resultData.setData(path.getBytes(Constants.UTF_8_CHARSET));
        }
        resultData.setEncoding(charsetName);
    }

    private void initBaseDir() {
        if (baseDir == null) {
            if (path == null) {
                // current directory
                baseDir = new File(".");
            } else {
                baseDir = new File(path);
                if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
                    throw new CrawlerSystemException("Could not create "
                            + baseDir.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Generate a path from a url.
     *
     * @param url URL
     * @return path File path
     */
    protected String getFilePath(final String url) {
        return url.replaceAll("/+", "/").replaceAll("\\./", "")
                .replaceAll("\\.\\./", "").replaceAll("/$", "/index.html")
                .replaceAll("\\?", questionStr).replaceAll(":", colonStr)
                .replaceAll(";", semicolonStr).replaceAll("&", ampersandStr);
    }

    /**
     * Returns data as a file path of String.
     *
     */
    @Override
    public Object getData(final AccessResultData<?> accessResultData) {
        // check transformer name
        if (!getName().equals(accessResultData.getTransformerName())) {
            throw new CrawlerSystemException("Transformer is invalid. Use "
                    + accessResultData.getTransformerName()
                    + ". This transformer is " + getName() + ".");
        }

        final byte[] data = accessResultData.getData();
        if (data == null) {
            return null;
        }
        final String encoding = accessResultData.getEncoding();
        String filePath;
        try {
            filePath = new String(data, encoding == null ? Constants.UTF_8
                    : encoding);
        } catch (final UnsupportedEncodingException e) {
            filePath = new String(data, Constants.UTF_8_CHARSET);
        }
        return new File(baseDir, filePath);
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getQuestionStr() {
        return questionStr;
    }

    public void setQuestionStr(final String questionStr) {
        this.questionStr = questionStr;
    }

    public String getColonStr() {
        return colonStr;
    }

    public void setColonStr(final String colonStr) {
        this.colonStr = colonStr;
    }

    public String getSemicolonStr() {
        return semicolonStr;
    }

    public void setSemicolonStr(final String semicolonStr) {
        this.semicolonStr = semicolonStr;
    }

    public String getAmpersandStr() {
        return ampersandStr;
    }

    public void setAmpersandStr(final String ampersandStr) {
        this.ampersandStr = ampersandStr;
    }

    public int getMaxDuplicatedPath() {
        return maxDuplicatedPath;
    }

    public void setMaxDuplicatedPath(final int maxDuplicatedPath) {
        this.maxDuplicatedPath = maxDuplicatedPath;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }
}
