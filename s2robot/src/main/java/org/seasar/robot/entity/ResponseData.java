/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
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
package org.seasar.robot.entity;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.robot.Constants;

/**
 * @author shinsuke
 * 
 */
public class ResponseData {

    private String url;

    private int httpStatusCode;

    private InputStream responseBody;

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

    private final Map<String, Object> metaDataMap =
        new LinkedHashMap<String, Object>();

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(final int statusCode) {
        httpStatusCode = statusCode;
    }

    public InputStream getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(final InputStream responseBody) {
        this.responseBody = responseBody;
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

    public void addMetaData(final String name, final Object value) {
        metaDataMap.put(name, value);
    }

    public Map<String, Object> getMetaDataMap() {
        return metaDataMap;
    }

    @Deprecated
    public void addHeader(final String name, final String value) {
        metaDataMap.put(name, value);
    }

    @Deprecated
    public Map<String, String> getHeaderMap() {
        final Map<String, String> headerMap = new HashMap<String, String>();
        for (final Map.Entry<String, Object> entry : metaDataMap.entrySet()) {
            if (entry.getValue() instanceof String) {
                headerMap.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return headerMap;
    }

}
