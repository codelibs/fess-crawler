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
package org.seasar.robot.client.smb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
import org.seasar.framework.container.annotation.tiger.InitMethod;
import org.seasar.framework.exception.IORuntimeException;
import org.seasar.framework.util.FileOutputStreamUtil;
import org.seasar.framework.util.InputStreamUtil;
import org.seasar.framework.util.OutputStreamUtil;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.Constants;
import org.seasar.robot.MaxLengthExceededException;
import org.seasar.robot.RobotCrawlAccessException;
import org.seasar.robot.RobotSystemException;
import org.seasar.robot.client.AbstractS2RobotClient;
import org.seasar.robot.client.fs.ChildUrlsException;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.helper.ContentLengthHelper;
import org.seasar.robot.helper.MimeTypeHelper;
import org.seasar.robot.util.TemporaryFileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 * 
 */
public class SmbClient extends AbstractS2RobotClient {
    private static final Logger logger = LoggerFactory // NOPMD
        .getLogger(SmbClient.class);

    public static final String NTLM_PASSWORD_AUTHENTICATION_PROPERTY =
        "ntlmPasswordAuthentication";

    protected String charset = Constants.UTF_8;

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    public NtlmPasswordAuthentication ntlmPasswordAuthentication;

    @InitMethod
    public void init() {
        // user agent
        final NtlmPasswordAuthentication ntlmPasswordAuthentication =
            getInitParameter(
                NTLM_PASSWORD_AUTHENTICATION_PROPERTY,
                this.ntlmPasswordAuthentication);
        if (ntlmPasswordAuthentication != null) {
            this.ntlmPasswordAuthentication = ntlmPasswordAuthentication;
        }
    }

    @DestroyMethod
    public void destroy() {
        ntlmPasswordAuthentication = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.client.S2RobotClient#doGet(java.lang.String)
     */
    public ResponseData doGet(final String uri) {
        final ResponseData responseData = new ResponseData();
        responseData.setMethod(Constants.GET_METHOD);
        final String filePath = preprocessUri(uri);
        responseData.setUrl(filePath);

        SmbFile file = null;
        try {
            file = new SmbFile(filePath, ntlmPasswordAuthentication);
        } catch (MalformedURLException e) {
            logger.warn("Could not parse url: " + filePath, e);
        }

        try {
            if (file == null) {
                responseData.setHttpStatusCode(404);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
            } else if (file.isFile()) {
                final MimeTypeHelper mimeTypeHelper =
                    SingletonS2Container.getComponent("mimeTypeHelper");
                InputStream is = null;
                try {
                    is = new SmbFileInputStream(file);
                    responseData.setMimeType(mimeTypeHelper.getContentType(
                        is,
                        file.getName()));
                } catch (Exception e) {
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
                        contentLengthHelper.getMaxLength(responseData
                            .getMimeType());
                    if (responseData.getContentLength() > maxLength) {
                        throw new MaxLengthExceededException(
                            "The content length ("
                                + responseData.getContentLength()
                                + " byte) is over " + maxLength
                                + " byte. The url is " + filePath);
                    }
                }

                responseData.setHttpStatusCode(200);
                responseData.setCharSet(geCharSet(file));
                responseData.setLastModified(new Date(file.lastModified()));
                if (file.canRead()) {
                    File outputFile = null;
                    try {
                        outputFile =
                            File.createTempFile("s2robot-SmbClient-", ".out");
                        copy(file, outputFile);
                        responseData
                            .setResponseBody(new TemporaryFileInputStream(
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
                final Set<String> childUrlSet = new HashSet<String>();
                final SmbFile[] files = file.listFiles();
                if (files != null) {
                    for (SmbFile f : files) {
                        final String chileUri = f.toString();
                        childUrlSet.add(chileUri);
                    }
                }
                throw new ChildUrlsException(childUrlSet);
            } else {
                responseData.setHttpStatusCode(404);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
            }
        } catch (SmbException e) {
            throw new RobotCrawlAccessException("Could not access " + uri, e);
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
            for (char c : filePath.toCharArray()) {
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
        } catch (UnsupportedEncodingException e) {
            return filePath;
        }
        return buf.toString();
    }

    protected String geCharSet(final SmbFile file) {
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
     * @see org.seasar.robot.client.S2RobotClient#doHead(java.lang.String)
     */
    public ResponseData doHead(final String url) {
        try {
            final ResponseData responseData = doGet(url);
            responseData.setMethod(Constants.HEAD_METHOD);
            return responseData;
        } catch (ChildUrlsException e) {
            return null;
        }
    }

    private void copy(final SmbFile src, final File dest) {
        if (dest.exists() && !dest.canWrite()) {
            return;
        }
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new SmbFileInputStream(src));
            out = new BufferedOutputStream(FileOutputStreamUtil.create(dest));
            final byte[] buf = new byte[1024];
            int length;
            while (-1 < (length = in.read(buf))) {
                out.write(buf, 0, length);
                out.flush();
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } finally {
            InputStreamUtil.close(in);
            OutputStreamUtil.close(out);
        }
    }
}
