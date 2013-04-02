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
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import jcifs.smb.ACE;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import org.apache.commons.io.IOUtils;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.container.annotation.tiger.DestroyMethod;
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

    public static final String SMB_AUTHENTICATIONS_PROPERTY =
        "smbAuthentications";

    public static final String SMB_ACCESS_CONTROL_ENTRIES =
        "smbAccessControlEntries";

    protected String charset = Constants.UTF_8;

    protected boolean resolveSids = true;

    @Binding(bindingType = BindingType.MAY)
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    public volatile SmbAuthenticationHolder smbAuthenticationHolder;

    public synchronized void init() {
        if (smbAuthenticationHolder != null) {
            return;
        }

        // user agent
        final SmbAuthentication[] smbAuthentications =
            getInitParameter(
                SMB_AUTHENTICATIONS_PROPERTY,
                new SmbAuthentication[0]);
        if (smbAuthentications != null) {
            SmbAuthenticationHolder smbAuthenticationHolder =
                new SmbAuthenticationHolder();
            for (SmbAuthentication smbAuthentication : smbAuthentications) {
                smbAuthenticationHolder.add(smbAuthentication);
            }
            this.smbAuthenticationHolder = smbAuthenticationHolder;
        }
    }

    @DestroyMethod
    public void destroy() {
        smbAuthenticationHolder = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.client.S2RobotClient#doGet(java.lang.String)
     */
    public ResponseData doGet(final String uri) {
        return getResponseData(uri, true);
    }

    protected ResponseData getResponseData(final String uri,
            boolean includeContent) {
        if (smbAuthenticationHolder == null) {
            init();
        }

        final ResponseData responseData = new ResponseData();
        responseData.setMethod(Constants.GET_METHOD);
        final String filePath = preprocessUri(uri);
        responseData.setUrl(filePath);

        SmbFile file = null;
        SmbAuthentication smbAuthentication =
            smbAuthenticationHolder.get(filePath);
        try {
            if (smbAuthentication == null) {
                file = new SmbFile(filePath);
            } else {
                file =
                    new SmbFile(filePath, smbAuthentication.getAuthentication());
            }
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

                processAccessControlEntries(responseData, file);
                Map<String, List<String>> headerFieldMap =
                    file.getHeaderFields();
                if (headerFieldMap != null) {
                    for (Map.Entry<String, List<String>> entry : headerFieldMap
                        .entrySet()) {
                        responseData.addMetaData(
                            entry.getKey(),
                            entry.getValue());
                    }
                }

                if (file.canRead()) {
                    if (includeContent) {
                        File outputFile = null;
                        try {
                            outputFile =
                                File.createTempFile(
                                    "s2robot-SmbClient-",
                                    ".out");
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
                    }
                } else {
                    // Forbidden
                    responseData.setHttpStatusCode(403);
                }
            } else if (file.isDirectory()) {
                final Set<String> childUrlSet = new HashSet<String>();
                if (includeContent) {
                    final SmbFile[] files = file.listFiles();
                    if (files != null) {
                        for (SmbFile f : files) {
                            final String chileUri = f.toString();
                            childUrlSet.add(chileUri);
                        }
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

    protected void processAccessControlEntries(final ResponseData responseData,
            final SmbFile file) {
        try {
            ACE[] aces = file.getSecurity(resolveSids);
            if (aces != null) {
                responseData.addMetaData(SMB_ACCESS_CONTROL_ENTRIES, aces);
            }
        } catch (IOException e) {
            throw new RobotCrawlAccessException("Could not access "
                + file.getPath(), e);
        }
    }

    protected String preprocessUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new RobotSystemException("The uri is empty.");
        }

        return uri;
    }

    protected String geCharSet(final SmbFile file) {
        return charset;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.seasar.robot.client.S2RobotClient#doHead(java.lang.String)
     */
    public ResponseData doHead(final String url) {
        try {
            final ResponseData responseData = getResponseData(url, false);
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

    /**
     * @return the resolveSids
     */
    public boolean isResolveSids() {
        return resolveSids;
    }

    /**
     * @param resolveSids
     *            the resolveSids to set
     */
    public void setResolveSids(boolean resolveSids) {
        this.resolveSids = resolveSids;
    }

    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * @param charset
     *            the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }
}
