/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.entity;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.codelibs.core.exception.IORuntimeException;
import org.codelibs.fess.crawler.Constants;

/**
 * @author shinsuke
 *
 */
public class ResponseData implements Closeable {

    private String url;

    private int httpStatusCode;

    private String charSet;

    private long contentLength;

    private String mimeType;

    private String method;

    private String parentUrl;

    private String ruleId;

    private String sessionId;

    private long executionTime;

    private Date lastModified;

    private String redirectLocation;

    private int status = Constants.OK_STATUS;

    private final Map<String, Object> metaDataMap = new LinkedHashMap<>();

    private final Set<RequestData> childUrlSet = new LinkedHashSet<>();

    private byte[] responseBodyBytes;

    private File responseBodyFile;

    private boolean isTemporaryFile;

    private boolean noFollow = false;

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(final int statusCode) {
        httpStatusCode = statusCode;
    }

    public boolean hasResponseBody() {
        return responseBodyBytes != null || responseBodyFile != null;
    }

    public InputStream getResponseBody() {
        if (responseBodyBytes != null) {
            return new ByteArrayInputStream(responseBodyBytes);
        } else if (responseBodyFile != null) {
            try {
                return new FileInputStream(responseBodyFile);
            } catch (final FileNotFoundException e) {
                throw new IORuntimeException(e);
            }
        }
        return null;
    }

    public void setResponseBody(final byte[] responseBody) {
        this.responseBodyBytes = responseBody;
    }

    public void setResponseBody(final File responseBody, final boolean isTemporary) {
        this.responseBodyFile = responseBody;
        this.isTemporaryFile = isTemporary;
    }

    public String getCharSet() {
        return charSet;
    }

    public void setCharSet(final String charSet) {
        this.charSet = charSet;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(final long contentLength) {
        this.contentLength = contentLength;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(final String contentType) {
        mimeType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(final String parentUrl) {
        this.parentUrl = parentUrl;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(final String ruleId) {
        this.ruleId = ruleId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(final long executionTime) {
        this.executionTime = executionTime;
    }

    public String getRedirectLocation() {
        return redirectLocation;
    }

    public void setRedirectLocation(final String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public void setNoFollow(final boolean value) {
        noFollow = value;
    }

    public boolean isNoFollow() {
        return noFollow;
    }

    public void addMetaData(final String name, final Object value) {
        metaDataMap.put(name, value);
    }

    public Map<String, Object> getMetaDataMap() {
        return metaDataMap;
    }

    public void addChildUrl(final RequestData url) {
        childUrlSet.add(url);
    }

    public void removeChildUrl(final RequestData url) {
        childUrlSet.remove(url);
    }

    public Set<RequestData> getChildUrlSet() {
        return childUrlSet;
    }

    public RequestData getRequestData() {
        final RequestData requestData = new RequestData();
        requestData.setMethod(method);
        requestData.setUrl(url);
        return requestData;
    }

    @Override
    public void close() throws IOException {
        if (isTemporaryFile && responseBodyFile != null) {
            responseBodyFile.delete();
        }
    }
}
