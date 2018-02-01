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

/**
 * @author shinsuke
 *
 */
public interface AccessResult<IDTYPE> {
    void init(ResponseData responseData, ResultData resultData);

    IDTYPE getId();

    void setId(IDTYPE id);

    String getSessionId();

    void setSessionId(String sessionId);

    String getRuleId();

    void setRuleId(String ruleId);

    String getUrl();

    void setUrl(String url);

    String getParentUrl();

    void setParentUrl(String parentUrl);

    Integer getStatus();

    void setStatus(Integer status);

    Integer getHttpStatusCode();

    void setHttpStatusCode(Integer httpStatusCode);

    String getMethod();

    void setMethod(String method);

    String getMimeType();

    void setMimeType(String mimeType);

    Long getCreateTime();

    void setCreateTime(Long createTime);

    Integer getExecutionTime();

    void setExecutionTime(Integer executionTime);

    AccessResultData<IDTYPE> getAccessResultData();

    void setAccessResultData(AccessResultData<IDTYPE> accessResultData);

    Long getContentLength();

    void setContentLength(Long contentLength);

    Long getLastModified();

    void setLastModified(Long lastModified);
}
