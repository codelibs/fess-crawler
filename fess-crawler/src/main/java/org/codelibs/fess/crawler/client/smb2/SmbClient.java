/*
 * Copyright 2012-2017 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.smb2;

import static java.util.EnumSet.of;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.timer.TimeoutManager;
import org.codelibs.core.timer.TimeoutTask;
import org.codelibs.fess.crawler.Constants;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.client.AbstractCrawlerClient;
import org.codelibs.fess.crawler.client.AccessTimeoutTarget;
import org.codelibs.fess.crawler.container.CrawlerContainer;
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

import com.hierynomus.msdtyp.ACL;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msdtyp.SID;
import com.hierynomus.msdtyp.SecurityDescriptor;
import com.hierynomus.msdtyp.SecurityInformation;
import com.hierynomus.msfscc.fileinformation.FileAllInformation;
import com.hierynomus.msfscc.fileinformation.FileBasicInformation;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.msfscc.fileinformation.FileStandardInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.protocol.commons.EnumWithValue;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskEntry;
import com.hierynomus.smbj.share.DiskShare;
import com.rapid7.client.dcerpc.mslsad.LocalSecurityAuthorityService;
import com.rapid7.client.dcerpc.mslsad.dto.PolicyDomainInfo;
import com.rapid7.client.dcerpc.mslsad.dto.PolicyHandle;
import com.rapid7.client.dcerpc.transport.RPCTransport;
import com.rapid7.client.dcerpc.transport.SMBTransportFactories;

/**
 * @author shinsuke
 * @author kaorufuzita
 *
 */
public class SmbClient extends AbstractCrawlerClient {
    private static final Logger logger = LoggerFactory
            .getLogger(SmbClient.class);

    public static final String SMB_AUTHENTICATIONS_PROPERTY = "SmbAuthentications";

    public static final String SMB_ACCESS_CONTROL_ENTRIES = "smbAccessControlEntries";

    public static final String SMB_CREATE_TIME = "smbCreateTime";;

    public static final String SMB_OWNER_ATTRIBUTES = "smbOwnerAttributes";

    @Resource
    protected CrawlerContainer crawlerContainer;

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

        final SmbAuthenticationHolder holder = new SmbAuthenticationHolder();
        final SmbAuthentication[] smbAuthentications = getInitParameter(
                SMB_AUTHENTICATIONS_PROPERTY, new SmbAuthentication[0],
                SmbAuthentication[].class);
        if (smbAuthentications != null) {
            for (final SmbAuthentication smbAuthentication : smbAuthentications) {
                holder.add(smbAuthentication);
            }
        }
        smbAuthenticationHolder = holder;
    }

    @PreDestroy
    public void destroy() {
        if (logger.isDebugEnabled()) {
            logger.debug("Destroying SmbClient...");
        }

        for (SmbAuthentication smbAuthentication : smbAuthenticationHolder) {
            smbAuthentication.disconnect();
        }
        smbAuthenticationHolder = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.client.CrawlerClient#doGet(java.lang.String)
     */
    @Override
    public ResponseData doGet(final String uri) {
        return processRequest(uri, true);
    }

    protected ResponseData processRequest(final String uri,
            final boolean includeContent) {
        if (smbAuthenticationHolder == null) {
            init();
        }

        // start
        AccessTimeoutTarget accessTimeoutTarget = null;
        TimeoutTask accessTimeoutTask = null;
        if (accessTimeout != null) {
            accessTimeoutTarget = new AccessTimeoutTarget(
                    Thread.currentThread());
            accessTimeoutTask = TimeoutManager.getInstance().addTimeoutTarget(
                    accessTimeoutTarget, accessTimeout.intValue(), false);
        }

        try {
            return getResponseData(uri, includeContent);
        } finally {
            if (accessTimeout != null) {
                accessTimeoutTarget.stop();
                if (!accessTimeoutTask.isCanceled()) {
                    accessTimeoutTask.cancel();
                }
            }
        }
    }

    protected ResponseData getResponseData(final String uri,
            final boolean includeContent) {
        final ResponseData responseData = new ResponseData();
        responseData.setMethod(Constants.GET_METHOD);
        String fineUri = preprocessUri(uri);
        responseData.setUrl(fineUri);

        final SmbAuthentication smbAuthentication = smbAuthenticationHolder
                .get(fineUri);

        if (logger.isDebugEnabled()) {
            logger.debug("Processing SmbFile: " + fineUri);
        }

        String decodedPath;
        String path;
        String shareName;
        try {
            final URI uriComponents = new URI(fineUri);
            decodedPath = uriComponents.getPath();
            final List<String> pathComponents = new LinkedList<String>(
                    Arrays.asList(decodedPath.split("/")));
            pathComponents.remove(0);
            shareName = pathComponents.remove(0);
            path = String.join("\\", pathComponents);
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "UNC shareName: " + shareName + ", path: " + path);
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid URI: " + uri, e);
            }
            throw new CrawlingAccessException("Invalid URI: " + uri, e);
        }

        Session session;
        try {
            session = smbAuthentication.getSession();
        } catch (IOException ioe) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cannot get session: " + uri, ioe);
            }
            throw new CrawlingAccessException("Cannot get session: " + uri,
                    ioe);
        }

        try (final DiskShare share = (DiskShare) session
                .connectShare(shareName)) {
            final DiskEntry entry = share.open(path,
                    EnumSet.of(AccessMask.GENERIC_READ), null,
                    SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null);

            if (logger.isDebugEnabled()) {
                logger.debug("SMB entry: " + entry);
            }
            if (entry == null) {
                responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
                responseData.setCharSet(charset);
                responseData.setContentLength(0);
            } else {
                final FileAllInformation fai = entry.getFileInformation();
                final FileStandardInformation fsi = fai
                        .getStandardInformation();
                final FileBasicInformation fbi = fai.getBasicInformation();

                if (fai.getStandardInformation().isDirectory()) {

                    if (logger.isDebugEnabled()) {
                        logger.debug("SMB Directory: " + fineUri);
                    }
                    final Set<RequestData> requestDataSet = new HashSet<>(100);
                    if (includeContent) {
                        final List<FileIdBothDirectoryInformation> list = share
                                .list(path);
                        for (FileIdBothDirectoryInformation fdi : list) {
                            final String fileName = fdi.getFileName();
                            if (fileName.equals(".") || fileName.equals("..")) {
                                continue;
                            }
                            if (!StringUtil.endsWithIgnoreCase(fineUri, "/")) {
                                fineUri = new StringBuilder(fineUri).append("/")
                                        .toString();
                            }
                            final String childUrl = new StringBuilder(fineUri)
                                    .append(URLEncoder.encode(fileName, "UTF-8")
                                            .replaceAll("\\+", "%20"))
                                    .toString();
                            requestDataSet
                                    .add(RequestDataBuilder.newRequestData()
                                            .get().url(childUrl).build());
                        }
                    }
                    throw new ChildUrlsException(requestDataSet,
                            this.getClass().getName()
                                    + "#getResponseData(String, boolean)");
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("SMB File: " + fineUri);
                    }
                    final com.hierynomus.smbj.share.File file = (com.hierynomus.smbj.share.File) entry;
                    responseData.setContentLength(fsi.getEndOfFile());
                    checkMaxContentLength(responseData);
                    responseData.setHttpStatusCode(Constants.OK_STATUS_CODE);
                    responseData.setCharSet(getCharSet(file));
                    responseData
                            .setLastModified(fbi.getLastWriteTime().toDate());
                    responseData.addMetaData(SMB_CREATE_TIME,
                            fbi.getCreationTime().toDate());

                    final RPCTransport transport = SMBTransportFactories.LSASVC
                            .getTransport(session);
                    final LocalSecurityAuthorityService lsas = new LocalSecurityAuthorityService(
                            transport);
                    final PolicyHandle policyHandle = lsas.openPolicyHandle();

                    final EnumSet<SecurityInformation> si = of(
                            SecurityInformation.OWNER_SECURITY_INFORMATION);
                    final SecurityDescriptor sd = file
                            .getSecurityInformation(si);
                    final SID ownerSid = sd.getOwnerSid();
                    if (ownerSid != null) {
                        final com.rapid7.client.dcerpc.dto.SID sid = new com.rapid7.client.dcerpc.dto.SID(
                                ownerSid.getRevision(),
                                ownerSid.getSidIdentifierAuthority(),
                                ownerSid.getSubAuthorities());
                        final String[] accountNames = lsas
                                .lookupNamesForSIDs(policyHandle, sid);
                        final PolicyDomainInfo pdi = lsas
                                .getPolicyPrimaryDomainInformation(
                                        policyHandle);
                        if (logger.isDebugEnabled()) {
                            logger.debug("SMB accountName: " + accountNames[0]
                                    + ", domainName: " + pdi.getDomainName());
                        }
                        final String[] ownerAttributes = { accountNames[0],
                                pdi.getDomainName() };
                        responseData.addMetaData(SMB_OWNER_ATTRIBUTES,
                                ownerAttributes);
                    }

                    processAccessControlEntries(responseData, file,
                            smbAuthentication);

                    // TODO: get header fields
                    // final Map<String, List<String>> headerFieldMap = file
                    // .getHeaderFields();
                    // if (headerFieldMap != null) {
                    // for (final Map.Entry<String, List<String>> entry :
                    // headerFieldMap
                    // .entrySet()) {
                    // responseData.addMetaData(entry.getKey(),
                    // entry.getValue());
                    // }
                    // }

                    if (EnumWithValue.EnumUtils.isSet(
                            fai.getAccessInformation().getAccessFlags(),
                            AccessMask.FILE_READ_DATA)) {
                        final MimeTypeHelper mimeTypeHelper = crawlerContainer
                                .getComponent("mimeTypeHelper");
                        if (includeContent) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("SMB Content: " + fineUri);
                            }
                            if (fsi.getEndOfFile() < maxCachedContentSize) {
                                try (final InputStream contentStream = new BufferedInputStream(
                                        file.getInputStream())) {
                                    responseData.setResponseBody(InputStreamUtil
                                            .getBytes(contentStream));
                                } catch (final Exception e) {
                                    logger.warn("I/O Exception.", e);
                                    responseData.setHttpStatusCode(
                                            Constants.SERVER_ERROR_STATUS_CODE);
                                }
                            } else {
                                File outputFile = null;
                                try {
                                    outputFile = File.createTempFile(
                                            "crawler-SmbClient-", ".out");
                                    copy(file, outputFile);
                                    responseData.setResponseBody(outputFile,
                                            true);
                                } catch (final Exception e) {
                                    logger.warn("I/O Exception.", e);
                                    responseData.setHttpStatusCode(
                                            Constants.SERVER_ERROR_STATUS_CODE);
                                    if (outputFile != null
                                            && !outputFile.delete()) {
                                        logger.warn("Could not delete "
                                                + outputFile.getAbsolutePath());
                                    }
                                }
                            }
                            if (logger.isDebugEnabled()) {
                                logger.debug("SMB MIME Type: " + fineUri);
                            }
                            try (final InputStream is = responseData
                                    .getResponseBody()) {
                                responseData.setMimeType(
                                        mimeTypeHelper.getContentType(is,
                                                file.getFileInformation()
                                                        .getNameInformation()));
                            } catch (final Exception e) {
                                responseData.setMimeType(
                                        mimeTypeHelper.getContentType(null,
                                                file.getFileInformation()
                                                        .getNameInformation()));
                            }
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.debug("SMB MIME Type: " + fineUri);
                            }
                            try (final InputStream is = file.getInputStream()) {
                                responseData.setMimeType(
                                        mimeTypeHelper.getContentType(is,
                                                file.getFileInformation()
                                                        .getNameInformation()));
                            } catch (final Exception e) {
                                responseData.setMimeType(
                                        mimeTypeHelper.getContentType(null,
                                                file.getFileInformation()
                                                        .getNameInformation()));
                            }
                        }
                        if (contentLengthHelper != null) {
                            final long maxLength = contentLengthHelper
                                    .getMaxLength(responseData.getMimeType());
                            if (responseData.getContentLength() > maxLength) {
                                throw new MaxLengthExceededException(
                                        "The content length ("
                                                + responseData
                                                        .getContentLength()
                                                + " byte) is over " + maxLength
                                                + " byte. The url is "
                                                + fineUri);
                            }
                        }
                    } else {
                        // Forbidden
                        responseData.setHttpStatusCode(
                                Constants.FORBIDDEN_STATUS_CODE);
                        responseData.setMimeType(APPLICATION_OCTET_STREAM);
                    }
                }
            }
        } catch (final SMBApiException | IOException e) {
            logger.warn("SMB Exception: ", e);
            responseData.setHttpStatusCode(Constants.NOT_FOUND_STATUS_CODE);
            responseData.setCharSet(charset);
            responseData.setContentLength(0);
        } catch (final CrawlerSystemException cse) {
            logger.warn("CrawlerSystemException: ", cse);
            IOUtils.closeQuietly(responseData);
            throw cse;
        }

        return responseData;
    }

    protected void processAccessControlEntries(final ResponseData responseData,
            final com.hierynomus.smbj.share.File file,
            final SmbAuthentication smbAuthentication) {

        if (logger.isDebugEnabled()) {
            logger.debug("SMB parsing ACE: " + file);
        }

        try {
            final LocalSecurityAuthorityService lsas = new LocalSecurityAuthorityService(
                    SMBTransportFactories.LSASVC
                            .getTransport(smbAuthentication.getSession()));
            final PolicyHandle policyHandle = lsas.openPolicyHandle();
            final EnumSet<SecurityInformation> si = of(
                    SecurityInformation.DACL_SECURITY_INFORMATION);
            final SecurityDescriptor sd = file.getSecurityInformation(si);
            if (logger.isDebugEnabled()) {
                logger.debug("SMB SecurityDescriptor: " + sd);
            }

            final ACL acl = sd.getDacl();
            if (acl != null) {
                final List<ACE> aces = new ArrayList<ACE>();
                if (logger.isDebugEnabled()) {
                    logger.debug("SMB ACEs: " + acl.getAces());
                }
                for (final com.hierynomus.msdtyp.ace.ACE e : acl.getAces()) {
                    final com.rapid7.client.dcerpc.dto.SID sid = new com.rapid7.client.dcerpc.dto.SID(
                            e.getSid().getRevision(),
                            e.getSid().getSidIdentifierAuthority(),
                            e.getSid().getSubAuthorities());

                    final String[] accountNames = lsas
                            .lookupNamesForSIDs(policyHandle, sid);
                    // Translate SID to get SID_NAME_USE
                    final com.rapid7.client.dcerpc.dto.SID[] sids = lsas
                            .lookupSIDsForNames(policyHandle, accountNames);
                    final ACE ace = new ACE(e);
                    if (logger.isDebugEnabled()) {
                        logger.debug("SID: " + sids[0] + ", ACE: " + e);
                    }
                    ace.setType(sids[0].getType());
                    ace.setAccountName(accountNames[0]);
                    if (logger.isDebugEnabled()) {
                        logger.debug("ACE Type: " + ace.getType()
                                + ", AccountName: " + ace.getAccountName());
                    }
                    aces.add(ace);
                }
                responseData.addMetaData(SMB_ACCESS_CONTROL_ENTRIES,
                        aces.toArray(new ACE[aces.size()]));
            }
        } catch (final SMBApiException | IOException e) {
            logger.warn("Could not get ACE " + file.toString(), e);
            throw new CrawlingAccessException(
                    "Could not get ACE " + file.toString(), e);
        }
    }

    protected String preprocessUri(final String uri) {
        if (StringUtil.isEmpty(uri)) {
            throw new CrawlerSystemException("The uri is empty.");
        }

        return uri;
    }

    // TODO: To be impremented
    protected String getCharSet(final com.hierynomus.smbj.share.File file) {
        return charset;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.codelibs.fess.crawler.client.CrawlerClient#doHead(java.lang.String)
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

    private void copy(final com.hierynomus.smbj.share.File src,
            final File dest) {
        if (dest.exists() && !dest.canWrite()) {
            return;
        }
        try (BufferedInputStream in = new BufferedInputStream(
                src.getInputStream());
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

    public void setSmbAuthenticationHolder(
            final SmbAuthenticationHolder smbAuthenticationHolder) {
        this.smbAuthenticationHolder = smbAuthenticationHolder;
    }
}
