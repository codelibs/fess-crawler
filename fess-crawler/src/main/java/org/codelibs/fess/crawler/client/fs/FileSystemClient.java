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
package org.codelibs.fess.crawler.client.fs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.AbstractCrawlerClient;
import org.codelibs.fess.crawler.client.AccessTimeoutTarget;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.exception.MaxLengthExceededException;
import org.codelibs.fess.crawler.helper.ContentLengthHelper;
import org.codelibs.fess.crawler.helper.MimeTypeHelper;

import jakarta.annotation.Resource;

/**
 * FileSystemClient is CrawlerClient implementation to crawl files on a file
 * system.
 *
 * @author shinsuke
 *
 */
public class FileSystemClient extends AbstractCrawlerClient {

    private static final Logger logger = LogManager.getLogger(FileSystemClient.class);

    public static final String FILE_ATTRIBUTE_VIEW = "fileAttributeView";

    public static final String FS_FILE_USER = "fsFileUser";

    public static final String FS_FILE_GROUPS = "fsFileGroups";

    protected String charset = Constants.UTF_8;

    @Resource
    protected ContentLengthHelper contentLengthHelper;

    protected AtomicBoolean isInit = new AtomicBoolean(false);

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.client.CrawlerClient#doGet(java.lang.String)
     */
    @Override
    public ResponseData doGet(final String uri) {
        return processRequest(uri, true);
    }

    protected ResponseData processRequest(final String uri, final boolean includeContent) {
        if (!isInit.get()) {
            synchronized (isInit) {
                if (!isInit.get()) {
                    init();
                    isInit.set(true);
                }
            }
        }

        // start
        AccessTimeoutTarget accessTimeoutTarget = null;
        TimeoutTask accessTimeoutTask = null;
        if (accessTimeout != null) {
            accessTimeoutTarget = new AccessTimeoutTarget(Thread.currentThread());
            accessTimeoutTask = TimeoutManager.getInstance().addTimeoutTarget(accessTimeoutTarget, accessTimeout, false);
        }

        try {
            return getResponseData(uri, includeContent);
        } finally {
            if (accessTimeoutTarget != null) {
                accessTimeoutTarget.stop();
                if (!accessTimeoutTask.isCanceled()) {
                    accessTimeoutTask.cancel();
                }
            }
        }
    }

    protected ResponseData getResponseData(final String uri, final boolean includeContent) {
        final ResponseData responseData = new ResponseData();
        try {
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
                // check file size
                responseData.setContentLength(file.length());
                checkMaxContentLength(responseData);

                parseFileOwnerAttribute(responseData, file);

                responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
                responseData.setCharSet(getCharSet(file));
                responseData.setLastModified(new Date(file.lastModified()));
                if (file.canRead()) {
                    final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                    try (final InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(is, file.getName()));
                    } catch (final Exception e) {
                        responseData.setMimeType(mimeTypeHelper.getContentType(null, file.getName()));
                    }

                    if (contentLengthHelper != null) {
                        final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                        if (responseData.getContentLength() > maxLength) {
                            throw new MaxLengthExceededException("The content length (" + responseData.getContentLength()
                                    + " byte) is over " + maxLength + " byte. The url is " + filePath);
                        }
                    }

                    if (includeContent) {
                        if (file.length() < maxCachedContentSize) {
                            try (InputStream contentStream = new BufferedInputStream(new FileInputStream(file))) {
                                responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                            } catch (final Exception e) {
                                logger.warn("I/O Exception.", e);
                                responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                            }
                        } else {
                            responseData.setResponseBody(file, false);
                        }
                    }
                } else {
                    // Forbidden
                    responseData.setHttpStatusCode(Constants.FORBIDDEN_STATUS_CODE);
                    responseData.setMimeType(APPLICATION_OCTET_STREAM);
                }
            } else if (file.isDirectory()) {
                final Set<RequestData> requestDataSet = new HashSet<>();
                if (includeContent) {
                    final File[] files = file.listFiles();
                    if (files != null) {
                        for (final File f : files) {
                            final String childUri = f.toURI().toASCIIString();
                            requestDataSet.add(RequestDataBuilder.newRequestData().get().url(childUri).build());
                        }
                    }
                }
                throw new ChildUrlsException(requestDataSet, this.getClass().getName() + "#getResponseData");
            } else {
                responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
            }
        } catch (final CrawlerSystemException e) {
            CloseableUtil.closeQuietly(responseData);
            throw e;
        } catch (final Exception e) {
            CloseableUtil.closeQuietly(responseData);
            throw new CrawlingAccessException("Could not access " + uri, e);
        }

        return responseData;
    }

    protected FileOwnerAttributeView parseFileOwnerAttribute(final ResponseData responseData, final File file) {
        try {
            final FileOwnerAttributeView ownerAttrView = Files.getFileAttributeView(file.toPath(), FileOwnerAttributeView.class);
            if (ownerAttrView != null) {
                final UserPrincipal owner = ownerAttrView.getOwner();
                if (owner != null) {
                    responseData.addMetaData(FS_FILE_USER, owner.getName());
                }
            }

            final AclFileAttributeView aclFileAttributeView = Files.getFileAttributeView(file.toPath(), AclFileAttributeView.class);
            if (aclFileAttributeView != null) {
                responseData.addMetaData(FILE_ATTRIBUTE_VIEW, aclFileAttributeView);
                responseData.addMetaData(FS_FILE_GROUPS,
                        aclFileAttributeView.getAcl().stream().map(acl -> acl.principal().getName()).toArray(n -> new String[n]));
                return aclFileAttributeView;
            }
            final PosixFileAttributeView posixFileAttributeView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
            if (posixFileAttributeView != null) {
                responseData.addMetaData(FILE_ATTRIBUTE_VIEW, posixFileAttributeView);
                responseData.addMetaData(FS_FILE_GROUPS, new String[] { posixFileAttributeView.readAttributes().group().getName() });
                return posixFileAttributeView;
            }
            return ownerAttrView;
        } catch (final Exception e) {
            throw new CrawlingAccessException("Failed to parse FileAttributeView.", e);
        }
    }

    protected String preprocessUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new CrawlerSystemException("The uri is empty.");
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
                    if (StringUtil.isAsciiPrintable(str)) {
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

    protected String getCharSet(final File file) {
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
     * @see org.codelibs.fess.crawler.client.CrawlerClient#doHead(java.lang.String)
     */
    @Override
    public ResponseData doHead(final String url) {
        try {
            final ResponseData responseData = processRequest(url, false);
            responseData.setMethod(Constants.HEAD_METHOD);
            return responseData;
        } catch (final ChildUrlsException e) {
            return null;
        }
    }

}
