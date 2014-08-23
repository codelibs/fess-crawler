/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.client.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.codelibs.robot.Constants;
import org.codelibs.robot.MaxLengthExceededException;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.client.AbstractS2RobotClient;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.helper.ContentLengthHelper;
import org.codelibs.robot.helper.MimeTypeHelper;
import org.codelibs.robot.util.TemporaryFileInputStream;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileSystemClient is S2RobotClient implementation to crawl files on a file
 * system.
 * 
 * @author shinsuke
 * 
 */
public class FileSystemClient extends AbstractS2RobotClient {
    private static final Logger logger = LoggerFactory // NOPMD
        .getLogger(FileSystemClient.class);

    protected String charset = Constants.UTF_8;

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    /*
     * (non-Javadoc)
     * 
     * @see org.codelibs.robot.client.S2RobotClient#doGet(java.lang.String)
     */
    @Override
    public ResponseData doGet(final String uri) {
        return getResponseData(uri, true);
    }

    protected ResponseData getResponseData(final String uri,
            final boolean includeContent) {
        final ResponseData responseData = new ResponseData();
        responseData.setMethod(Constants.GET_METHOD);
        final String filePath = preprocessUri(uri);
        responseData.setUrl(filePath);

        File file = null;
        try {
            file = new File(new URI(filePath));
        } catch (final URISyntaxException e) {
            logger.warn("Could not parse url: " + filePath, e);
        }

        if (file == null) {
            responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(0);
        } else if (file.isFile()) {
            final MimeTypeHelper mimeTypeHelper =
                SingletonS2Container.getComponent("mimeTypeHelper");
            InputStream is = null;
            try {
                is = new FileInputStream(file);
                responseData.setMimeType(mimeTypeHelper.getContentType(
                    is,
                    file.getName()));
            } catch (final Exception e) {
                responseData.setMimeType(mimeTypeHelper.getContentType(
                    null,
                    file.getName()));
            } finally {
                IOUtils.closeQuietly(is);
            }

            // check file size
            responseData.setContentLength(file.length());
            if (contentLengthHelper != null) {
                final long maxLength =
                    contentLengthHelper
                        .getMaxLength(responseData.getMimeType());
                if (responseData.getContentLength() > maxLength) {
                    throw new MaxLengthExceededException("The content length ("
                        + responseData.getContentLength() + " byte) is over "
                        + maxLength + " byte. The url is " + filePath);
                }
            }

            responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
            responseData.setCharSet(geCharSet(file));
            responseData.setLastModified(new Date(file.lastModified()));
            if (file.canRead()) {
                if (includeContent) {
                    File outputFile = null;
                    try {
                        outputFile =
                            File.createTempFile(
                                "s2robot-FileSystemClient-",
                                ".out");
                        FileUtil.copy(file, outputFile);
                        responseData
                            .setResponseBody(new TemporaryFileInputStream(
                                outputFile));
                    } catch (final Exception e) {
                        logger.warn("I/O Exception.", e);
                        responseData
                            .setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                        if (outputFile != null && !outputFile.delete()) {
                            logger.warn("Could not delete "
                                + outputFile.getAbsolutePath());
                        }
                    }
                }
            } else {
                // Forbidden
                responseData.setHttpStatusCode(Constants.FORBIDDEN_STATUS_CODE);
            }
        } else if (file.isDirectory()) {
            final Set<RequestData> requestDataSet = new HashSet<>();
            if (includeContent) {
                final File[] files = file.listFiles();
                if (files != null) {
                    for (final File f : files) {
                        final String chileUri = f.toURI().toASCIIString();
                        requestDataSet.add(RequestDataBuilder
                            .newRequestData()
                            .get()
                            .url(chileUri)
                            .build());
                    }
                }
            }
            throw new ChildUrlsException(requestDataSet);
        } else {
            responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(0);
        }

        return responseData;
    }

    protected String preprocessUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new RobotSystemException("The uri is empty.");
        }

        String filePath = uri;
        if (!filePath.startsWith("file:")) {
            filePath = "file://" + filePath;
        }

        final StringBuilder buf = new StringBuilder(filePath.length() + 100);
        try {
            for (final char c : filePath.toCharArray()) {
                if (c == ' ') {
                    buf.append("%20");
                } else {
                    final String str = String.valueOf(c);
                    if (StringUtils.isAsciiPrintable(str)) {
                        buf.append(c);
                    } else {
                        buf.append(URLEncoder.encode(str, charset));
                    }
                }
            }
        } catch (final UnsupportedEncodingException e) {
            return filePath;
        }
        return buf.toString();
    }

    protected String geCharSet(final File file) {
        return charset;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(final String charset) {
        this.charset = charset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.codelibs.robot.client.S2RobotClient#doHead(java.lang.String)
     */
    @Override
    public ResponseData doHead(final String url) {
        try {
            final ResponseData responseData = getResponseData(url, false);
            responseData.setMethod(Constants.HEAD_METHOD);
            return responseData;
        } catch (final ChildUrlsException e) {
            return null;
        }
    }

}
