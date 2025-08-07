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
package org.codelibs.fess.crawler.client.smb;

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
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import jakarta.annotation.Resource;
import jcifs.ACE;
import jcifs.CIFSContext;
import jcifs.CIFSException;
import jcifs.SID;
import jcifs.config.PropertyConfiguration;
import jcifs.context.BaseContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

/**
 * The {@link SmbClient} class is a crawler client implementation for accessing files and directories
 * on SMB (Server Message Block) shares. It extends {@link AbstractCrawlerClient} and utilizes the JCIFS library
 * to interact with SMB resources.
 *
 * <p>
 * This client supports authentication, content retrieval, and metadata extraction from SMB files.
 * It handles file access, directory listing, and access control entries (ACEs) processing.
 * </p>
 *
 * <p>
 * The class provides methods to:
 * </p>
 * <ul>
 *   <li>Initialize the client with SMB authentication details.</li>
 *   <li>Retrieve content and metadata from SMB files.</li>
 *   <li>Process access control entries to determine allowed and denied SIDs (Security Identifiers).</li>
 *   <li>Handle timeouts during SMB operations.</li>
 * </ul>
 *
 * <p>
 * The client uses a {@link SmbAuthenticationHolder} to manage SMB authentication credentials.
 * It also integrates with other Fess Crawler components, such as {@link ContentLengthHelper} and
 * {@link MimeTypeHelper}, to handle content length checks and MIME type detection.
 * </p>
 *
 * <p>
 * The class uses JCIFS properties to configure the SMB connection.
 * </p>
 *
 * <p>
 * Usage example:
 * </p>
 *
 * <pre>
 * {@code
 * SmbClient smbClient = new SmbClient();
 * smbClient.init();
 * ResponseData responseData = smbClient.doGet("smb://example.com/share/file.txt");
 * // Process the responseData
 * smbClient.close();
 * }
 * </pre>
 *
 * @author shinsuke
 */
public class SmbClient extends AbstractCrawlerClient {
    private static final Logger logger = LogManager.getLogger(SmbClient.class);

    /** Property key for SMB authentications configuration. */
    public static final String SMB_AUTHENTICATIONS_PROPERTY = "smbAuthentications";

    /** Property key for SMB allowed SID entries. */
    public static final String SMB_ALLOWED_SID_ENTRIES = "smbAllowedSidEntries";

    /** Property key for SMB denied SID entries. */
    public static final String SMB_DENIED_SID_ENTRIES = "smbDeniedSidEntries";

    /** Property key for SMB file creation time. */
    public static final String SMB_CREATE_TIME = "smbCreateTime";

    /** Property key for SMB owner attributes. */
    public static final String SMB_OWNER_ATTRIBUTES = "smbOwnerAttributes";

    /** Character set used for encoding SMB content. */
    protected String charset = Constants.UTF_8;

    /** Flag indicating whether to resolve SIDs to names. */
    protected boolean resolveSids = true;

    /** Helper for content length operations. */
    @Resource
    protected ContentLengthHelper contentLengthHelper;

    /**
     * The SMB authentication holder.
     */
    protected volatile SmbAuthenticationHolder smbAuthenticationHolder;

    /**
     * The CIFS context.
     */
    protected CIFSContext cifsContext;

    /**
     * Creates a new SmbClient instance.
     */
    public SmbClient() {
        super();
    }

    /**
    * Initializes the SMB client.
    * @see org.codelibs.fess.crawler.client.AbstractCrawlerClient#init()
    */
    @Override
    public synchronized void init() {
        if (smbAuthenticationHolder != null) {
            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing SmbClient...");
        }

        super.init();

        final Properties props = new Properties();
        System.getProperties().entrySet().stream().filter(e -> e.getKey().toString().startsWith("jcifs.")).forEach(e -> {
            props.setProperty((String) e.getKey(), (String) e.getValue());
        });
        try {
            cifsContext = new BaseContext(new PropertyConfiguration(props));
        } catch (final CIFSException e) {
            throw new CrawlingAccessException(e);
        }

        // smb auth
        final SmbAuthenticationHolder holder = new SmbAuthenticationHolder();
        final SmbAuthentication[] smbAuthentications =
                getInitParameter(SMB_AUTHENTICATIONS_PROPERTY, new SmbAuthentication[0], SmbAuthentication[].class);
        if (smbAuthentications != null) {
            for (final SmbAuthentication smbAuthentication : smbAuthentications) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding SmbAuthentication: {}", smbAuthentication);
                }
                holder.add(smbAuthentication);
            }
        }
        smbAuthenticationHolder = holder;
    }

    @Override
    public void close() throws Exception {
        smbAuthenticationHolder = null;
        if (cifsContext != null) {
            cifsContext.close();
        }
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

    /**
     * Processes an SMB request for the given URI.
     *
     * @param uri the URI to process
     * @param includeContent whether to include content in the response
     * @return the response data
     */
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

    /**
     * Creates an NTLM password authenticator from the given SMB authentication.
     *
     * @param smbAuthentication the SMB authentication information
     * @return the NTLM password authenticator
     */
    protected NtlmPasswordAuthenticator getAuthenticator(final SmbAuthentication smbAuthentication) {
        return new NtlmPasswordAuthenticator(smbAuthentication.getDomain() == null ? "" : smbAuthentication.getDomain(),
                smbAuthentication.getUsername(), smbAuthentication.getPassword());
    }

    /**
     * Retrieves response data for the given URI.
     *
     * @param uri the URI to retrieve data from
     * @param includeContent whether to include content in the response
     * @return the response data
     */
    protected ResponseData getResponseData(final String uri, final boolean includeContent) {
        final ResponseData responseData = new ResponseData();
        responseData.setMethod(Constants.GET_METHOD);
        final String filePath = preprocessUri(uri);
        responseData.setUrl(filePath);

        SmbFile file = null;
        final SmbAuthentication smbAuthentication = smbAuthenticationHolder.get(filePath);
        if (logger.isDebugEnabled()) {
            logger.debug("Creating SmbFile: {} - {}", filePath, smbAuthentication);
        }
        try {
            if (smbAuthentication == null) {
                file = new SmbFile(filePath, cifsContext);
            } else {
                file = new SmbFile(filePath, cifsContext.withCredentials(getAuthenticator(smbAuthentication)));
            }
        } catch (final MalformedURLException e) {
            logger.warn("Could not parse url: {}", filePath, e);
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
                responseData.setCharSet(getCharSet(file));
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
                    throw new CrawlingAccessException("Cannot get owner of the file: " + filePath, e);
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
                        if (file.getContentLengthLong() < maxCachedContentSize) {
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
                            final String childUri = f.getURL().toExternalForm();
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
        } catch (final SmbException e) {
            CloseableUtil.closeQuietly(responseData);
            throw new CrawlingAccessException("Could not access " + uri, e);
        }

        return responseData;
    }

    /**
     * Processes access control entries (ACEs) for the given SMB file and adds them to the response data.
     *
     * @param responseData the response data to update
     * @param file the SMB file to process
     */
    protected void processAccessControlEntries(final ResponseData responseData, final SmbFile file) {
        try {
            final ACE[] aces = file.getSecurity(resolveSids);
            if (aces != null) {
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

    /**
     * Processes allowed SIDs (Security Identifiers) and adds them to the SID set.
     * If the SID is a group, it recursively processes all group members.
     *
     * @param file the SMB file
     * @param sid the SID to process
     * @param sidSet the set of SIDs to add to
     */
    protected void processAllowedSIDs(final SmbFile file, final SID sid, final Set<SID> sidSet) {
        if (logger.isDebugEnabled()) {
            logger.debug("SID:{}", sid);
        }
        final int type = sid.getType();
        sidSet.add(sid);
        if (type == SID.SID_TYPE_DOM_GRP || type == SID.SID_TYPE_ALIAS) {
            try {
                final CIFSContext context = file.getContext();
                final SID[] children = context.getSIDResolver()
                        .getGroupMemberSids(context, file.getServer(), sid.getDomainSid(), sid.getRid(),
                                jcifs.smb.SID.SID_FLAG_RESOLVE_SIDS);
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

    /**
     * Preprocesses the URI before processing the request.
     *
     * @param uri the URI to preprocess
     * @return the preprocessed URI
     * @throws CrawlerSystemException if the URI is empty
     */
    protected String preprocessUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new CrawlerSystemException("The uri is empty.");
        }

        return uri;
    }

    /**
     * Returns the character set for the given SMB file.
     *
     * @param file the SMB file
     * @return the character set
     */
    protected String getCharSet(final SmbFile file) {
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

    /**
     * Copies content from an SmbFile to a File.
     * @param src The source SmbFile.
     * @param dest The destination File.
     */
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
     * Returns whether SIDs (Security Identifiers) should be resolved.
     * @return true if SIDs should be resolved, false otherwise
     */
    public boolean isResolveSids() {
        return resolveSids;
    }

    /**
     * Sets whether SIDs (Security Identifiers) should be resolved.
     * @param resolveSids true to resolve SIDs, false otherwise
     */
    public void setResolveSids(final boolean resolveSids) {
        this.resolveSids = resolveSids;
    }

    /**
     * Returns the character set used for SMB operations.
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }

    /**
     * Sets the character set used for SMB operations.
     * @param charset the charset to set
     */
    public void setCharset(final String charset) {
        this.charset = charset;
    }

    /**
     * Sets the SMB authentication holder.
     *
     * @param smbAuthenticationHolder the SMB authentication holder to set
     */
    public void setSmbAuthenticationHolder(final SmbAuthenticationHolder smbAuthenticationHolder) {
        this.smbAuthenticationHolder = smbAuthenticationHolder;
    }
}
