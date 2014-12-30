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
package org.codelibs.robot.client.smb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import jcifs.smb.ACE;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.robot.Constants;
import org.codelibs.robot.MaxLengthExceededException;
import org.codelibs.robot.RobotCrawlAccessException;
import org.codelibs.robot.RobotSystemException;
import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.client.AbstractS2RobotClient;
import org.codelibs.robot.client.fs.ChildUrlsException;
import org.codelibs.robot.container.RobotContainer;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.helper.ContentLengthHelper;
import org.codelibs.robot.helper.MimeTypeHelper;
import org.codelibs.robot.util.TemporaryFileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class SmbClient extends AbstractS2RobotClient {
    private static final Logger logger = LoggerFactory // NOPMD
            .getLogger(SmbClient.class);

    public static final String SMB_AUTHENTICATIONS_PROPERTY = "smbAuthentications";

    public static final String SMB_ACCESS_CONTROL_ENTRIES = "smbAccessControlEntries";

    @Resource
    protected RobotContainer robotContainer;

    protected String charset = Constants.UTF_8;

    protected boolean resolveSids = true;

    @Resource
    protected ContentLengthHelper contentLengthHelper;

    public volatile SmbAuthenticationHolder smbAuthenticationHolder;

    public synchronized void init() {
        if (smbAuthenticationHolder != null) {
            return;
        }

        // user agent
        final SmbAuthentication[] smbAuthentications = getInitParameter(
                SMB_AUTHENTICATIONS_PROPERTY, new SmbAuthentication[0]);
        if (smbAuthentications != null) {
            final SmbAuthenticationHolder holder = new SmbAuthenticationHolder();
            for (final SmbAuthentication smbAuthentication : smbAuthentications) {
                holder.add(smbAuthentication);
            }
            smbAuthenticationHolder = holder;
        }
    }

    @PreDestroy
    public void destroy() {
        smbAuthenticationHolder = null;
    }

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
        if (smbAuthenticationHolder == null) {
            init();
        }

        final ResponseData responseData = new ResponseData();
        responseData.setMethod(Constants.GET_METHOD);
        final String filePath = preprocessUri(uri);
        responseData.setUrl(filePath);

        SmbFile file = null;
        final SmbAuthentication smbAuthentication = smbAuthenticationHolder
                .get(filePath);
        try {
            if (smbAuthentication == null) {
                file = new SmbFile(filePath);
            } else {
                file = new SmbFile(filePath,
                        smbAuthentication.getAuthentication());
            }
        } catch (final MalformedURLException e) {
            logger.warn("Could not parse url: " + filePath, e);
        }

        try {
            if (file == null) {
                responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
            } else if (file.isFile()) {
                final MimeTypeHelper mimeTypeHelper = robotContainer
                        .getComponent("mimeTypeHelper");
                InputStream is = null;
                try {
                    is = new SmbFileInputStream(file);
                    responseData.setMimeType(mimeTypeHelper.getContentType(is,
                            file.getName()));
                } catch (final Exception e) {
                    responseData.setMimeType(mimeTypeHelper.getContentType(
                            null, file.getName()));
                } finally {
                    IOUtils.closeQuietly(is);
                }

                // check file size
                responseData.setContentLength(file.length());
                if (contentLengthHelper != null) {
                    final long maxLength = contentLengthHelper
                            .getMaxLength(responseData.getMimeType());
                    if (responseData.getContentLength() > maxLength) {
                        throw new MaxLengthExceededException(
                                "The content length ("
                                        + responseData.getContentLength()
                                        + " byte) is over " + maxLength
                                        + " byte. The url is " + filePath);
                    }
                }

                responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
                responseData.setCharSet(geCharSet(file));
                responseData.setLastModified(new Date(file.lastModified()));

                processAccessControlEntries(responseData, file);
                final Map<String, List<String>> headerFieldMap = file
                        .getHeaderFields();
                if (headerFieldMap != null) {
                    for (final Map.Entry<String, List<String>> entry : headerFieldMap
                            .entrySet()) {
                        responseData.addMetaData(entry.getKey(),
                                entry.getValue());
                    }
                }

                if (file.canRead()) {
                    if (includeContent) {
                        File outputFile = null;
                        try {
                            outputFile = File.createTempFile(
                                    "s2robot-SmbClient-", ".out");
                            copy(file, outputFile);
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
                    responseData
                            .setHttpStatusCode(Constants.FORBIDDEN_STATUS_CODE);
                }
            } else if (file.isDirectory()) {
                final Set<RequestData> requestDataSet = new HashSet<>();
                if (includeContent) {
                    final SmbFile[] files = file.listFiles();
                    if (files != null) {
                        for (final SmbFile f : files) {
                            final String chileUri = f.toString();
                            requestDataSet.add(RequestDataBuilder
                                    .newRequestData().get().url(chileUri)
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
        } catch (final SmbException e) {
            throw new RobotCrawlAccessException("Could not access " + uri, e);
        }

        return responseData;
    }

    protected void processAccessControlEntries(final ResponseData responseData,
            final SmbFile file) {
        try {
            final ACE[] aces = file.getSecurity(resolveSids);
            if (aces != null) {
                responseData.addMetaData(SMB_ACCESS_CONTROL_ENTRIES, aces);
            }
        } catch (final IOException e) {
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

    private void copy(final SmbFile src, final File dest) {
        if (dest.exists() && !dest.canWrite()) {
            return;
        }
        try (BufferedInputStream in = new BufferedInputStream(
                new SmbFileInputStream(src));
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(dest))) {
            final byte[] buf = new byte[1024];
            int length;
            while (-1 < (length = in.read(buf))) {
                out.write(buf, 0, length);
                out.flush();
            }
        } catch (final IOException e) {
            throw new IORuntimeException(e);
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
    public void setResolveSids(final boolean resolveSids) {
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
    public void setCharset(final String charset) {
        this.charset = charset;
    }
}
