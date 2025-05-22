/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.smb1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.io.CloseableUtil;
import org.codelibs.core.io.FileUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jcifs.smb1.Config;
import jcifs.smb1.smb1.ACE;
import jcifs.smb1.smb1.NtlmPasswordAuthentication;
import jcifs.smb1.smb1.SID;
import jcifs.smb1.smb1.SmbException;
import jcifs.smb1.smb1.SmbFile;
import jcifs.smb1.smb1.SmbFileInputStream;
import jcifs.smb1.util.LogStream;

/**
 * @author shinsuke
 *
 */
public class SmbClient extends AbstractCrawlerClient {
    private static final Logger logger = LoggerFactory.getLogger(SmbClient.class);

    public static final String SMB_AUTHENTICATIONS_PROPERTY = "smb1Authentications";

    public static final String SMB_ACCESS_CONTROL_ENTRIES = "smb1AccessControlEntries";

    public static final String SMB_ALLOWED_SID_ENTRIES = "smb1AllowedSidEntries";

    public static final String SMB_DENIED_SID_ENTRIES = "smb1DeniedSidEntries";

    public static final String SMB_CREATE_TIME = "smb1CreateTime";

    public static final String SMB_OWNER_ATTRIBUTES = "smb1OwnerAttributes";

    static {
        if (Config.getInt("jcifs.smb1.util.loglevel", -1) == -1) {
            if (logger.isTraceEnabled()) {
                LogStream.setLevel(4);
            } else if (logger.isDebugEnabled()) {
                LogStream.setLevel(3);
            } else if (logger.isWarnEnabled()) {
                LogStream.setLevel(2);
            } else if (logger.isErrorEnabled()) {
                LogStream.setLevel(1);
            } else {
                LogStream.setLevel(0);
            }
        }

        LogStream.setInstance(new PrintStream(new OutputStream() {
            private static final int MAX_LEN = 1000;

            private final ByteArrayOutputStream buf = new ByteArrayOutputStream(MAX_LEN);

            @Override
            public void write(final int b) throws IOException {
                if (b == '\n' || b == '\r' || buf.size() >= MAX_LEN) {
                    try {
                        final String msg = buf.toString(Constants.UTF_8);
                        if (StringUtil.isNotBlank(msg)) {
                            if (logger.isTraceEnabled()) {
                                logger.trace(msg);
                            } else if (logger.isDebugEnabled()) {
                                logger.debug(msg);
                            } else if (logger.isWarnEnabled()) {
                                logger.warn(msg);
                            } else if (logger.isErrorEnabled()) {
                                logger.error(msg);
                            }
                        }
                    } catch (final Exception e) {
                        logger.warn(e.getLocalizedMessage());
                    }
                    buf.reset();
                } else {
                    buf.write(b);
                }
            }
        }));
    }

    protected String charset = Constants.UTF_8;

    protected boolean resolveSids = true;

    @Resource
    protected ContentLengthHelper contentLengthHelper;

    protected volatile SmbAuthenticationHolder smbAuthenticationHolder;

    @Override
    public synchronized void init() {
        if (smbAuthenticationHolder != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing SmbClient...");
        }

        super.init();

        // smb auth
        final SmbAuthenticationHolder holder = new SmbAuthenticationHolder();
        final SmbAuthentication[] smbAuthentications =
                getInitParameter(SMB_AUTHENTICATIONS_PROPERTY, new SmbAuthentication[0], SmbAuthentication[].class);
        if (smbAuthentications != null) {
            for (final SmbAuthentication smbAuthentication : smbAuthentications) {
                holder.add(smbAuthentication);
            }
        }
        smbAuthenticationHolder = holder;
    }

    @Override
    public void close() {
        smbAuthenticationHolder = null;
    }

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
        if (smbAuthenticationHolder == null) {
            init();
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
                if (accessTimeoutTask != null && !accessTimeoutTask.isCanceled()) {
                    accessTimeoutTask.cancel();
                }
            }
        }
    }

    protected ResponseData getResponseData(final String uri, final boolean includeContent) {
        final ResponseData responseData = new ResponseData();
        responseData.setMethod(Constants.GET_METHOD);
        final String filePath = preprocessUri(uri);
        responseData.setUrl(filePath);

        SmbFile file = null;
        final SmbAuthentication smbAuthentication = smbAuthenticationHolder.get(filePath);
        if (logger.isDebugEnabled()) {
            logger.debug("Creating SmbFile: {}", filePath);
        }
        try {
            if (smbAuthentication == null) {
                file = new SmbFile(filePath);
            } else {
                file = new SmbFile(filePath, smbAuthentication.getAuthentication());
            }
        } catch (final MalformedURLException e) {
            logger.warn("Could not parse url: " + filePath, e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Processing SmbFile: {}", filePath);
        }

        try {
            if (file == null) {
                responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
            } else if (file.isFile()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Checking SmbFile Size: {}", filePath);
                }
                responseData.setContentLength(file.length());
                checkMaxContentLength(responseData);
                responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
                responseData.setCharSet(geCharSet(file));
                responseData.setLastModified(new Date(file.lastModified()));
                responseData.addMetaData(SMB_CREATE_TIME, new Date(file.createTime()));
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Parsing SmbFile Owner: {}", filePath);
                    }
                    final SID ownerUser = file.getOwnerUser();
                    if (ownerUser != null) {
                        final String[] ownerAttributes = { ownerUser.getAccountName(), ownerUser.getDomainName() };
                        responseData.addMetaData(SMB_OWNER_ATTRIBUTES, ownerAttributes);
                    }
                } catch (final IOException e) {
                    logger.warn("Cannot get owner of the file: {}", filePath);
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Parsing SmbFile ACL: {}", filePath);
                }
                processAccessControlEntries(responseData, file);
                final Map<String, List<String>> headerFieldMap = file.getHeaderFields();
                if (headerFieldMap != null) {
                    for (final Map.Entry<String, List<String>> entry : headerFieldMap.entrySet()) {
                        responseData.addMetaData(entry.getKey(), entry.getValue());
                    }
                }

                if (file.canRead()) {
                    final MimeTypeHelper mimeTypeHelper = crawlerContainer.getComponent("mimeTypeHelper");
                    if (includeContent) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Parsing SmbFile Content: {}", filePath);
                        }
                        if (file.getContentLength() < maxCachedContentSize) {
                            try (InputStream contentStream = new BufferedInputStream(new SmbFileInputStream(file))) {
                                responseData.setResponseBody(InputStreamUtil.getBytes(contentStream));
                            } catch (final Exception e) {
                                logger.warn("I/O Exception.", e);
                                responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                            }
                        } else {
                            File outputFile = null;
                            try {
                                outputFile = createTempFile("crawler-SmbClient-", ".out", null);
                                copy(file, outputFile);
                                responseData.setResponseBody(outputFile, true);
                            } catch (final Exception e) {
                                logger.warn("I/O Exception.", e);
                                responseData.setHttpStatusCode(Constants.SERVER_ERROR_STATUS_CODE);
                                FileUtil.deleteInBackground(outputFile);
                            }
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("Parsing SmbFile MIME Type: {}", filePath);
                        }
                        try (final InputStream is = responseData.getResponseBody()) {
                            responseData.setMimeType(mimeTypeHelper.getContentType(is, file.getName()));
                        } catch (final Exception e) {
                            responseData.setMimeType(mimeTypeHelper.getContentType(null, file.getName()));
                        }
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Parsing SmbFile MIME Type: {}", filePath);
                        }
                        try (final InputStream is = new SmbFileInputStream(file)) {
                            responseData.setMimeType(mimeTypeHelper.getContentType(is, file.getName()));
                        } catch (final Exception e) {
                            responseData.setMimeType(mimeTypeHelper.getContentType(null, file.getName()));
                        }
                    }
                    if (contentLengthHelper != null) {
                        final long maxLength = contentLengthHelper.getMaxLength(responseData.getMimeType());
                        if (responseData.getContentLength() > maxLength) {
                            throw new MaxLengthExceededException("The content length (" + responseData.getContentLength()
                                    + " byte) is over " + maxLength + " byte. The url is " + filePath);
                        }
                    }
                } else {
                    // Forbidden
                    responseData.setHttpStatusCode(Constants.FORBIDDEN_STATUS_CODE);
                    responseData.setMimeType(APPLICATION_OCTET_STREAM);
                }
            } else if (file.isDirectory()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Parsing SmbFile Directory: {}", filePath);
                }
                final Set<RequestData> requestDataSet = new HashSet<>(100);
                if (includeContent) {
                    final SmbFile[] files = file.listFiles();
                    if (files != null) {
                        for (final SmbFile f : files) {
                            final String chileUri = f.toString();
                            requestDataSet.add(RequestDataBuilder.newRequestData().get().url(chileUri).build());
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
        } catch (final SmbException e) {
            CloseableUtil.closeQuietly(responseData);
            throw new CrawlingAccessException("Could not access " + uri, e);
        }

        return responseData;
    }

    protected void processAccessControlEntries(final ResponseData responseData, final SmbFile file) {
        try {
            final ACE[] aces = file.getSecurity(resolveSids);
            if (aces != null) {
                responseData.addMetaData(SMB_ACCESS_CONTROL_ENTRIES, aces); // backward compatibility
                final Set<SID> sidAllowSet = new HashSet<>();
                final Set<SID> sidDenySet = new HashSet<>();
                for (final ACE ace : aces) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("ACE:{}", ace);
                    }
                    processAllowedSIDs(file, ace.getSID(), ace.isAllow() ? sidAllowSet : sidDenySet);
                }
                responseData.addMetaData(SMB_ALLOWED_SID_ENTRIES, sidAllowSet.toArray(new SID[sidAllowSet.size()]));
                responseData.addMetaData(SMB_DENIED_SID_ENTRIES, sidDenySet.toArray(new SID[sidDenySet.size()]));
            }
        } catch (final IOException e) {
            throw new CrawlingAccessException("Could not access " + file.getPath(), e);
        }
    }

    protected void processAllowedSIDs(final SmbFile file, final SID sid, final Set<SID> sidSet) {
        if (logger.isDebugEnabled()) {
            logger.debug("SID:{}", sid);
        }
        final int type = sid.getType();
        sidSet.add(sid);
        if (type == SID.SID_TYPE_DOM_GRP || type == SID.SID_TYPE_ALIAS) {
            try {
                final SID[] children = sid.getGroupMemberSids(file.getServer(), (NtlmPasswordAuthentication) file.getPrincipal(),
                        SID.SID_FLAG_RESOLVE_SIDS);
                for (final SID child : children) {
                    if (!sidSet.contains(child)) {
                        processAllowedSIDs(file, child, sidSet);
                    }
                }
            } catch (final Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Exception on SID processing.", e);
                }
            }
        }
    }

    protected String preprocessUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new CrawlerSystemException("The uri is empty.");
        }

        return uri;
    }

    protected String geCharSet(final SmbFile file) {
        return charset;
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

    private void copy(final SmbFile src, final File dest) {
        if (dest.exists() && !dest.canWrite()) {
            return;
        }
        try (BufferedInputStream in = new BufferedInputStream(new SmbFileInputStream(src));
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
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

    public void setSmbAuthenticationHolder(final SmbAuthenticationHolder smbAuthenticationHolder) {
        this.smbAuthenticationHolder = smbAuthenticationHolder;
    }
}
