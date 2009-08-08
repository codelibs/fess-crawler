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
package org.seasar.robot.client.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.util.FileUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.client.S2RobotClient;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.helper.MimeTypeHelper;
import org.seasar.robot.util.TemporaryFileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class FileSystemClient implements S2RobotClient {
    private final Logger logger = LoggerFactory
            .getLogger(FileSystemClient.class);

    protected String charset = Constants.UTF_8;

    /* (non-Javadoc)
     * @see org.seasar.robot.client.S2RobotClient#doGet(java.lang.String)
     */
    public ResponseData doGet(String uri) {
        ResponseData responseData = new ResponseData();
        responseData.setMethod(Constants.GET_METHOD);
        uri = preprocessUri(uri);
        responseData.setUrl(uri);

        String path = null;
        try {
            path = new URI(uri).getPath();
        } catch (URISyntaxException e) {
            logger.warn("Could not parse url: " + uri, e);
        }

        File file = new File(decodeUri(path));
        if (file.isFile()) {
            responseData.setHttpStatusCode(200);
            responseData.setCharSet(geCharSet(file));
            responseData.setContentLength(file.length());
            responseData.setLastModified(new Date(file.lastModified()));
            MimeTypeHelper mimeTypeHelper = SingletonS2Container
                    .getComponent("mimeTypeHelper");
            InputStream is = null;
            try {
                is = new FileInputStream(file);
                responseData.setMimeType(mimeTypeHelper.getContentType(is, file
                        .getName()));
            } catch (Exception e) {
                responseData.setMimeType(mimeTypeHelper.getContentType(null,
                        file.getName()));
            } finally {
                IOUtils.closeQuietly(is);
            }
            if (file.canRead()) {
                File outputFile = null;
                try {
                    outputFile = File.createTempFile(
                            "s2robot-FileSystemClient-", ".out");
                    outputFile.deleteOnExit();
                    FileUtil.copy(file, outputFile);
                    responseData.setResponseBody(new TemporaryFileInputStream(
                            outputFile));
                } catch (Exception e) {
                    logger.warn("I/O Exception.", e);
                    responseData.setHttpStatusCode(500);
                    if (outputFile != null && !outputFile.delete()) {
                        logger.warn("Could not delete "
                                + outputFile.getAbsolutePath());
                    }
                }
            } else {
                // Forbidden
                responseData.setHttpStatusCode(403);
            }
        } else if (file.isDirectory()) {
            Set<String> childUrlSet = new HashSet<String>();
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    String chileUri = f.toURI().toASCIIString();
                    childUrlSet.add(chileUri);
                }
            }
            throw new ChildUrlsException(childUrlSet);
        } else {
            responseData.setHttpStatusCode(404);
            responseData.setCharSet(charset);
            responseData.setContentLength(0);
        }

        return responseData;
    }

    protected String preprocessUri(String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new RobotSystemException("The uri is empty.");
        }

        if (!uri.startsWith("file:")) {
            uri = "file://" + uri;
        }

        try {
            StringBuilder buf = new StringBuilder(uri.length() + 100);
            for (char c : uri.toCharArray()) {
                String str = String.valueOf(c);
                if (StringUtils.isAsciiPrintable(str) && c != ' ') {
                    buf.append(c);
                } else {
                    buf.append(URLEncoder.encode(str, charset));
                }
            }
            return buf.toString();
        } catch (UnsupportedEncodingException e) {
            return uri;
        }
    }

    protected String decodeUri(String uri) {
        if (StringUtil.isBlank(uri)) {
            return uri;
        }

        try {
            return URLDecoder.decode(uri, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RobotSystemException("Unsupported encoding: " + charset,
                    e);
        }
    }

    protected String geCharSet(File file) {
        return charset;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

}
