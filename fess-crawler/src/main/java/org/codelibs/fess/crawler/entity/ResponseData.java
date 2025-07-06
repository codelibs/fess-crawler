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
import org.codelibs.core.io.FileUtil;
import org.codelibs.fess.crawler.Constants;

/**
 * Represents the response data obtained from a crawled resource.
 * This class encapsulates various details of an HTTP response, including
 * status code, content type, content length, and the response body.
 * It also provides methods for managing metadata, child URLs, and temporary files.
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

    /**
     * Creates a new ResponseData instance.
     */
    public ResponseData() {
        super();
    }

    /**
     * Gets the HTTP status code of the response.
     *
     * @return the HTTP status code
     */
    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    /**
     * Sets the HTTP status code of the response.
     *
     * @param statusCode the HTTP status code to set
     */
    public void setHttpStatusCode(final int statusCode) {
        httpStatusCode = statusCode;
    }

    /**
     * Checks if this response has a response body.
     *
     * @return true if the response has a body (either as bytes or file), false otherwise
     */
    public boolean hasResponseBody() {
        return responseBodyBytes != null || responseBodyFile != null;
    }

    /**
     * Gets the response body as an InputStream.
     * If the response body is stored as bytes, returns a ByteArrayInputStream.
     * If the response body is stored as a file, returns a FileInputStream.
     *
     * @return the response body as an InputStream, or null if no response body is available
     * @throws IORuntimeException if an I/O error occurs while reading the response body file
     */
    public InputStream getResponseBody() {
        if (responseBodyBytes != null) {
            return new ByteArrayInputStream(responseBodyBytes);
        }
        if (responseBodyFile != null) {
            try {
                return new FileInputStream(responseBodyFile);
            } catch (final FileNotFoundException e) {
                throw new IORuntimeException(e);
            }
        }
        return null;
    }

    /**
     * Sets the response body from a byte array.
     *
     * @param responseBody the byte array containing the response body
     */
    public void setResponseBody(final byte[] responseBody) {
        responseBodyBytes = responseBody;
    }

    /**
     * Sets the response body from a file.
     *
     * @param responseBody the file containing the response body
     * @param isTemporary true if the file is temporary and should be deleted when closing
     */
    public void setResponseBody(final File responseBody, final boolean isTemporary) {
        responseBodyFile = responseBody;
        isTemporaryFile = isTemporary;
    }

    /**
     * Gets the character set of the response.
     *
     * @return the character set, or null if not specified
     */
    public String getCharSet() {
        return charSet;
    }

    /**
     * Sets the character set of the response.
     *
     * @param charSet the character set to set
     */
    public void setCharSet(final String charSet) {
        this.charSet = charSet;
    }

    /**
     * Gets the content length of the response.
     *
     * @return the content length in bytes
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * Sets the content length of the response.
     *
     * @param contentLength the content length in bytes
     */
    public void setContentLength(final long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * Gets the MIME type of the response.
     *
     * @return the MIME type, or null if not specified
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the MIME type of the response.
     *
     * @param contentType the MIME type to set
     */
    public void setMimeType(final String contentType) {
        mimeType = contentType;
    }

    /**
     * Gets the URL of the crawled resource.
     *
     * @return the URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the crawled resource.
     *
     * @param url the URL to set
     */
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Gets the HTTP method used for the request.
     *
     * @return the HTTP method (e.g., GET, POST)
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the HTTP method used for the request.
     *
     * @param method the HTTP method to set (e.g., GET, POST)
     */
    public void setMethod(final String method) {
        this.method = method;
    }

    /**
     * Gets the parent URL from which this resource was discovered.
     *
     * @return the parent URL, or null if this is a root URL
     */
    public String getParentUrl() {
        return parentUrl;
    }

    /**
     * Sets the parent URL from which this resource was discovered.
     *
     * @param parentUrl the parent URL to set
     */
    public void setParentUrl(final String parentUrl) {
        this.parentUrl = parentUrl;
    }

    /**
     * Gets the rule ID associated with this response.
     *
     * @return the rule ID, or null if not specified
     */
    public String getRuleId() {
        return ruleId;
    }

    /**
     * Sets the rule ID associated with this response.
     *
     * @param ruleId the rule ID to set
     */
    public void setRuleId(final String ruleId) {
        this.ruleId = ruleId;
    }

    /**
     * Gets the session ID associated with this crawling session.
     *
     * @return the session ID, or null if not specified
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the session ID associated with this crawling session.
     *
     * @param sessionId the session ID to set
     */
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the execution time for processing this response.
     *
     * @return the execution time in milliseconds
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * Sets the execution time for processing this response.
     *
     * @param executionTime the execution time in milliseconds
     */
    public void setExecutionTime(final long executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * Gets the redirect location if the response is a redirect.
     *
     * @return the redirect location URL, or null if not a redirect
     */
    public String getRedirectLocation() {
        return redirectLocation;
    }

    /**
     * Sets the redirect location if the response is a redirect.
     *
     * @param redirectLocation the redirect location URL to set
     */
    public void setRedirectLocation(final String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    /**
     * Gets the last modified date of the resource.
     *
     * @return the last modified date, or null if not available
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Sets the last modified date of the resource.
     *
     * @param lastModified the last modified date to set
     */
    public void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Gets the processing status of this response.
     *
     * @return the processing status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Sets the processing status of this response.
     *
     * @param status the processing status to set
     */
    public void setStatus(final int status) {
        this.status = status;
    }

    /**
     * Sets whether this response should not be followed for further crawling.
     *
     * @param value true if this response should not be followed, false otherwise
     */
    public void setNoFollow(final boolean value) {
        noFollow = value;
    }

    /**
     * Checks if this response should not be followed for further crawling.
     *
     * @return true if this response should not be followed, false otherwise
     */
    public boolean isNoFollow() {
        return noFollow;
    }

    /**
     * Adds metadata to this response.
     *
     * @param name the name of the metadata
     * @param value the value of the metadata
     */
    public void addMetaData(final String name, final Object value) {
        metaDataMap.put(name, value);
    }

    /**
     * Gets the metadata map containing all metadata associated with this response.
     *
     * @return the metadata map
     */
    public Map<String, Object> getMetaDataMap() {
        return metaDataMap;
    }

    /**
     * Adds a child URL discovered from this response.
     *
     * @param url the child URL to add
     */
    public void addChildUrl(final RequestData url) {
        childUrlSet.add(url);
    }

    /**
     * Removes a child URL from this response.
     *
     * @param url the child URL to remove
     */
    public void removeChildUrl(final RequestData url) {
        childUrlSet.remove(url);
    }

    /**
     * Gets the set of child URLs discovered from this response.
     *
     * @return the set of child URLs
     */
    public Set<RequestData> getChildUrlSet() {
        return childUrlSet;
    }

    /**
     * Creates a RequestData object from this response's URL and method.
     *
     * @return a new RequestData object with the URL and method from this response
     */
    public RequestData getRequestData() {
        final RequestData requestData = new RequestData();
        requestData.setMethod(method);
        requestData.setUrl(url);
        return requestData;
    }

    /**
     * Closes the response data, deleting temporary files if any.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        if (isTemporaryFile) {
            FileUtil.deleteInBackground(responseBodyFile);
        }
    }
}
