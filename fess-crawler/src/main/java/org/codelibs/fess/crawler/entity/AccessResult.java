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

/**
 * Represents the result of accessing a resource.
 *
 * @param <IDTYPE> the type of the identifier for the access result
 */
public interface AccessResult<IDTYPE> {

    /**
     * Initializes the access result with the given response and result data.
     *
     * @param responseData the response data
     * @param resultData the result data
     */
    void init(ResponseData responseData, ResultData resultData);

    /**
     * Returns the identifier of the access result.
     *
     * @return the identifier
     */
    IDTYPE getId();

    /**
     * Sets the identifier of the access result.
     *
     * @param id the identifier
     */
    void setId(IDTYPE id);

    /**
     * Returns the session identifier.
     *
     * @return the session identifier
     */
    String getSessionId();

    /**
     * Sets the session identifier.
     *
     * @param sessionId the session identifier
     */
    void setSessionId(String sessionId);

    /**
     * Returns the rule identifier.
     *
     * @return the rule identifier
     */
    String getRuleId();

    /**
     * Sets the rule identifier.
     *
     * @param ruleId the rule identifier
     */
    void setRuleId(String ruleId);

    /**
     * Returns the URL of the accessed resource.
     *
     * @return the URL
     */
    String getUrl();

    /**
     * Sets the URL of the accessed resource.
     *
     * @param url the URL
     */
    void setUrl(String url);

    /**
     * Returns the parent URL of the accessed resource.
     *
     * @return the parent URL
     */
    String getParentUrl();

    /**
     * Sets the parent URL of the accessed resource.
     *
     * @param parentUrl the parent URL
     */
    void setParentUrl(String parentUrl);

    /**
     * Returns the status of the access result.
     *
     * @return the status
     */
    Integer getStatus();

    /**
     * Sets the status of the access result.
     *
     * @param status the status
     */
    void setStatus(Integer status);

    /**
     * Returns the HTTP status code of the access result.
     *
     * @return the HTTP status code
     */
    Integer getHttpStatusCode();

    /**
     * Sets the HTTP status code of the access result.
     *
     * @param httpStatusCode the HTTP status code
     */
    void setHttpStatusCode(Integer httpStatusCode);

    /**
     * Returns the HTTP method used for the access.
     *
     * @return the HTTP method
     */
    String getMethod();

    /**
     * Sets the HTTP method used for the access.
     *
     * @param method the HTTP method
     */
    void setMethod(String method);

    /**
     * Returns the MIME type of the accessed resource.
     *
     * @return the MIME type
     */
    String getMimeType();

    /**
     * Sets the MIME type of the accessed resource.
     *
     * @param mimeType the MIME type
     */
    void setMimeType(String mimeType);

    /**
     * Returns the creation time of the access result.
     *
     * @return the creation time
     */
    Long getCreateTime();

    /**
     * Sets the creation time of the access result.
     *
     * @param createTime the creation time
     */
    void setCreateTime(Long createTime);

    /**
     * Returns the execution time of the access.
     *
     * @return the execution time
     */
    Integer getExecutionTime();

    /**
     * Sets the execution time of the access.
     *
     * @param executionTime the execution time
     */
    void setExecutionTime(Integer executionTime);

    /**
     * Returns the data associated with the access result.
     *
     * @return the access result data
     */
    AccessResultData<IDTYPE> getAccessResultData();

    /**
     * Sets the data associated with the access result.
     *
     * @param accessResultData the access result data
     */
    void setAccessResultData(AccessResultData<IDTYPE> accessResultData);

    /**
     * Returns the content length of the accessed resource.
     *
     * @return the content length
     */
    Long getContentLength();

    /**
     * Sets the content length of the accessed resource.
     *
     * @param contentLength the content length
     */
    void setContentLength(Long contentLength);

    /**
     * Returns the last modified time of the accessed resource.
     *
     * @return the last modified time
     */
    Long getLastModified();

    /**
     * Sets the last modified time of the accessed resource.
     *
     * @param lastModified the last modified time
     */
    /**
     * Sets the last modified time of the accessed resource.
     *
     * @param lastModified the last modified time
     */
    void setLastModified(Long lastModified);
}
