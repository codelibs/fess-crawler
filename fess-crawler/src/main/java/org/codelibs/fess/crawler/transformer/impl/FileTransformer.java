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
package org.codelibs.fess.crawler.transformer.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CopyUtil;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.entity.AccessResultData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.ResultData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;

/**
 * <p>
 * FileTransformer stores the content of a crawled resource as a file on the file system.
 * It extends HtmlTransformer and provides functionality to:
 * </p>
 * <ul>
 *     <li>Specify a base directory for storing files.</li>
 *     <li>Generate file paths from URLs, replacing special characters.</li>
 *     <li>Handle duplicated file paths by appending a counter.</li>
 *     <li>Store the file path in the result data.</li>
 *     <li>Retrieve the stored file as a File object.</li>
 * </ul>
 *
 * <p>
 * The class uses several configurable properties to customize the file storage behavior,
 * such as the base path, replacement strings for special characters in URLs,
 * the maximum number of duplicated paths to attempt, and the character set for encoding the file path.
 * </p>
 *
 * <p>
 * It handles potential exceptions during file creation and storage, throwing
 * {@link org.codelibs.fess.crawler.exception.CrawlerSystemException} in case of errors.
 * </p>
 *
 * <p>
 * The {@link #storeData(ResponseData, ResultData)} method is the main entry point for storing
 * the content of a crawled resource. The {@link #getData(AccessResultData)} method retrieves
 * the stored file path as a File object.
 * </p>
 */
public class FileTransformer extends HtmlTransformer {
    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(FileTransformer.class);

    /**
     * Constructs a new FileTransformer.
     */
    public FileTransformer() {
        // Default constructor
    }

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

    /** Maximum number of duplicated paths to attempt */
    protected int maxDuplicatedPath = 100;

    /** Character set name for encoding file paths */
    protected String charsetName = Constants.UTF_8;

    /**
     * A directory to store downloaded files.
     */
    protected File baseDir;

    /**
     * Creates a file with the specified path, handling directory creation and duplicate names.
     *
     * @param path the file path to create
     * @return the created file
     * @throws CrawlerSystemException if directory creation fails
     */
    protected File createFile(final String path) {
        final String[] paths = path.split("/");
        File targetFile = baseDir;
        for (int i = 0; i < paths.length - 1; i++) {
            File file = new File(targetFile, paths[i]);
            if (file.exists()) {
                if (!file.isDirectory()) {
                    for (int j = 0; j < maxDuplicatedPath; j++) {
                        file = new File(targetFile, paths[i] + "_" + j);
                        if (!file.exists()) {
                            if (!file.mkdirs()) {
                                throw new CrawlerSystemException("Could not create " + file.getAbsolutePath());
                            }
                            break;
                        }
                        if (file.isDirectory()) {
                            break;
                        }
                    }
                }
            } else if (!file.mkdirs()) {
                throw new CrawlerSystemException("Could not create " + file.getAbsolutePath());
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

    /**
     * Stores response data as a file on the file system.
     *
     * @param responseData the response data to store
     * @param resultData the result data to populate with file information
     * @throws CrawlerSystemException if file storage fails
     */
    @Override
    public void storeData(final ResponseData responseData, final ResultData resultData) {
        resultData.setTransformerName(getName());

        initBaseDir();

        final String url = responseData.getUrl();
        final String path = getFilePath(url);

        synchronized (this) {

            final File file = createFile(path);

            try (final InputStream is = responseData.getResponseBody(); final OutputStream os = new FileOutputStream(file);) {
                CopyUtil.copy(is, os);
            } catch (final IOException e) {
                throw new CrawlerSystemException("Could not store " + file.getAbsolutePath(), e);
            }
        }
        try {
            resultData.setData(path.getBytes(charsetName));
        } catch (final UnsupportedEncodingException e) {
            if (logger.isInfoEnabled()) {
                logger.info("Invalid charsetName: " + charsetName + ". Changed to " + Constants.UTF_8, e);
            }
            charsetName = Constants.UTF_8_CHARSET.name();
            resultData.setData(path.getBytes(Constants.UTF_8_CHARSET));
        }
        resultData.setEncoding(charsetName);
    }

    /**
     * Initializes the base directory for storing files.
     *
     * @throws CrawlerSystemException if directory creation fails
     */
    private void initBaseDir() {
        if (baseDir == null) {
            if (path == null) {
                // current directory
                baseDir = new File(".");
            } else {
                baseDir = new File(path);
                if (!baseDir.isDirectory() && !baseDir.mkdirs()) {
                    throw new CrawlerSystemException("Could not create " + baseDir.getAbsolutePath());
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
        return url.replaceAll("/+", "/")
                .replace("./", "")
                .replace("../", "")
                .replaceAll("/$", "/index.html")
                .replaceAll("\\?", questionStr)
                .replaceAll(":", colonStr)
                .replaceAll(";", semicolonStr)
                .replaceAll("&", ampersandStr);
    }

    /**
     * Returns data as a file path of String.
     *
     */
    @Override
    public Object getData(final AccessResultData<?> accessResultData) {
        // check transformer name
        if (!getName().equals(accessResultData.getTransformerName())) {
            throw new CrawlerSystemException(
                    "Transformer is invalid. Use " + accessResultData.getTransformerName() + ". This transformer is " + getName() + ".");
        }

        final byte[] data = accessResultData.getData();
        if (data == null) {
            return null;
        }
        final String encoding = accessResultData.getEncoding();
        String filePath;
        try {
            filePath = new String(data, encoding == null ? Constants.UTF_8 : encoding);
        } catch (final UnsupportedEncodingException e) {
            filePath = new String(data, Constants.UTF_8_CHARSET);
        }
        return new File(baseDir, filePath);
    }

    /**
     * Gets the base path for storing files.
     *
     * @return the base path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the base path for storing files.
     *
     * @param path the base path to set
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * Gets the replacement string for question marks in URLs.
     *
     * @return the question mark replacement string
     */
    public String getQuestionStr() {
        return questionStr;
    }

    /**
     * Sets the replacement string for question marks in URLs.
     *
     * @param questionStr the question mark replacement string to set
     */
    public void setQuestionStr(final String questionStr) {
        this.questionStr = questionStr;
    }

    /**
     * Gets the replacement string for colons in URLs.
     *
     * @return the colon replacement string
     */
    public String getColonStr() {
        return colonStr;
    }

    /**
     * Sets the replacement string for colons in URLs.
     *
     * @param colonStr the colon replacement string to set
     */
    public void setColonStr(final String colonStr) {
        this.colonStr = colonStr;
    }

    /**
     * Gets the replacement string for semicolons in URLs.
     *
     * @return the semicolon replacement string
     */
    public String getSemicolonStr() {
        return semicolonStr;
    }

    /**
     * Sets the replacement string for semicolons in URLs.
     *
     * @param semicolonStr the semicolon replacement string to set
     */
    public void setSemicolonStr(final String semicolonStr) {
        this.semicolonStr = semicolonStr;
    }

    /**
     * Gets the replacement string for ampersands in URLs.
     *
     * @return the ampersand replacement string
     */
    public String getAmpersandStr() {
        return ampersandStr;
    }

    /**
     * Sets the replacement string for ampersands in URLs.
     *
     * @param ampersandStr the ampersand replacement string to set
     */
    public void setAmpersandStr(final String ampersandStr) {
        this.ampersandStr = ampersandStr;
    }

    /**
     * Gets the maximum number of duplicated paths to attempt.
     *
     * @return the maximum duplicated path count
     */
    public int getMaxDuplicatedPath() {
        return maxDuplicatedPath;
    }

    /**
     * Sets the maximum number of duplicated paths to attempt.
     *
     * @param maxDuplicatedPath the maximum duplicated path count to set
     */
    public void setMaxDuplicatedPath(final int maxDuplicatedPath) {
        this.maxDuplicatedPath = maxDuplicatedPath;
    }

    /**
     * Gets the character set name used for encoding file paths.
     *
     * @return the character set name
     */
    public String getCharsetName() {
        return charsetName;
    }

    /**
     * Sets the charset name.
     * @param charsetName The charset name to set.
     */
    public void setCharsetName(final String charsetName) {
        this.charsetName = charsetName;
    }
}
