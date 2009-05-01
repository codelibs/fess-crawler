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
package org.seasar.robot.transformer.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.util.StreamUtil;

/**
 * @author shinsuke
 * 
 */
public class FileTransformer extends HtmlTransformer {
    /**
     * A path to store downloaded files. The default path is a current
     * directory.
     */
    public String path;

    /**
     * A file separator.
     */
    public String fileSeparator = File.separator;

    /**
     * A string to replace ?.
     */
    public String questionStr = "_QUEST_";

    /**
     * A string to replace :.
     */
    public String colonStr = "_CLN_";

    /**
     * A string to replace ;.
     */
    public String semicolonStr = "_SCLN_";

    /**
     * A string to replace &.
     */
    public String ampersandStr = "_AMP_";

    /**
     * A directory to store downloaded files.
     */
    protected File baseDir;

    @Override
    public void storeData(ResponseData responseData, ResultData resultData) {
        resultData.setTransformerName(getName());

        initBaseDir();

        String url = responseData.getUrl();
        String path = getFilePath(url);

        File file = new File(baseDir, path);
        if (file.exists()) {
            path = getFilePath(url + "/");
            file = new File(baseDir, path);
            if (file.exists()) {
                throw new RobotSystemException(file.getAbsolutePath()
                        + " already exists. The url is " + url);
            }
        }

        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new RobotSystemException("Could not create "
                        + parentDir.getAbsolutePath());
            }
        } else {
            if (!parentDir.isDirectory()) {
                throw new RobotSystemException(parentDir.getAbsolutePath()
                        + " is not a directory.");
            }
        }

        InputStream is = responseData.getResponseBody();
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            StreamUtil.drain(is, os);
        } catch (IOException e) {
            throw new RobotSystemException("Could not store "
                    + file.getAbsolutePath(), e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }

        resultData.setData(path);

    }

    private void initBaseDir() {
        if (baseDir == null) {
            if (path == null) {
                // current directory
                baseDir = new File(".");
            } else {
                baseDir = new File(path);
                if (!baseDir.isDirectory()) {
                    if (!baseDir.mkdirs()) {
                        throw new RobotSystemException("Could not create "
                                + baseDir.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Generate a path from a url.
     * 
     * @param url
     * @return
     */
    protected String getFilePath(String url) {
        return url.replaceAll("/+", "/")//
                .replaceAll("/$", "/index.html")//
                .replaceAll("/", "\\" + fileSeparator)//
                .replaceAll("\\?", questionStr)//
                .replace(":", colonStr)//
                .replaceAll(";", semicolonStr)//
                .replaceAll("&", ampersandStr)//
        ;
    }
}
