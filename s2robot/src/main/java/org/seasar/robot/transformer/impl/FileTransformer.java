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
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.entity.AccessResultData;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.util.StreamUtil;

/**
 * FileTransformer stores WEB data as a file path.
 * 
 * @author shinsuke
 * 
 */
public class FileTransformer extends HtmlTransformer {
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
     * A string to replace &.
     */
    protected String ampersandStr = "_AMP_";

    protected int maxDuplicatedPath = 100;

    protected String charsetName = Constants.UTF_8;

    /**
     * A directory to store downloaded files.
     */
    protected File baseDir;

    protected File createFile(String path) {
        String[] paths = path.split("/");
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
                                throw new RobotSystemException(
                                        "Could not create "
                                                + file.getAbsolutePath());
                            }
                            break;
                        }
                    }
                }
            } else {
                if (!file.mkdirs()) {
                    throw new RobotSystemException("Could not create "
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
    public void storeData(ResponseData responseData, ResultData resultData) {
        resultData.setTransformerName(getName());

        initBaseDir();

        String url = responseData.getUrl();
        String path = getFilePath(url);

        synchronized (this) {

            File file = createFile(path);

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
        }
        try {
            resultData.setData(path.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            throw new RobotSystemException("Invalid charsetName: "
                    + charsetName, e);
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
                .replaceAll("\\./", "")//
                .replaceAll("\\.\\./", "")//
                .replaceAll("/$", "/index.html")//
                .replaceAll("\\?", questionStr)//
                .replaceFirst(":", colonStr)//
                .replaceAll(";", semicolonStr)//
                .replaceAll("&", ampersandStr)//
        ;
    }

    /**
     * Returns data as a file path of String.
     * 
     */
    @Override
    public Object getData(AccessResultData accessResultData) {
        // check transformer name
        if (!getName().equals(accessResultData.getTransformerName())) {
            throw new RobotSystemException("Transformer is invalid. Use "
                    + accessResultData.getTransformerName()
                    + ". This transformer is " + getName() + ".");
        }

        byte[] data = accessResultData.getData();
        if (data == null) {
            return null;
        }
        String encoding = accessResultData.getEncoding();
        String filePath;
        try {
            filePath = new String(data, encoding != null ? encoding
                    : Constants.UTF_8);
        } catch (UnsupportedEncodingException e) {
            try {
                filePath = new String(data, Constants.UTF_8);
            } catch (UnsupportedEncodingException e1) {
                throw new RobotSystemException("Unexpected exception.");
            }
        }
        return new File(baseDir, filePath);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQuestionStr() {
        return questionStr;
    }

    public void setQuestionStr(String questionStr) {
        this.questionStr = questionStr;
    }

    public String getColonStr() {
        return colonStr;
    }

    public void setColonStr(String colonStr) {
        this.colonStr = colonStr;
    }

    public String getSemicolonStr() {
        return semicolonStr;
    }

    public void setSemicolonStr(String semicolonStr) {
        this.semicolonStr = semicolonStr;
    }

    public String getAmpersandStr() {
        return ampersandStr;
    }

    public void setAmpersandStr(String ampersandStr) {
        this.ampersandStr = ampersandStr;
    }

    public int getMaxDuplicatedPath() {
        return maxDuplicatedPath;
    }

    public void setMaxDuplicatedPath(int maxDuplicatedPath) {
        this.maxDuplicatedPath = maxDuplicatedPath;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }
}
