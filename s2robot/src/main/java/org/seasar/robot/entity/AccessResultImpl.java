/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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

import java.sql.Timestamp;
import java.util.Date;

import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.robot.Constants;

/**
 * @author shinsuke
 *
 */
public class AccessResultImpl implements AccessResult {
    protected Long id;

    protected String sessionId;

    protected String ruleId;

    protected String url;

    protected String parentUrl;

    protected Integer status;

    protected Integer httpStatusCode;

    protected String method;

    protected String mimeType;

    protected Timestamp createTime;

    protected Integer executionTime;

    @Binding(bindingType = BindingType.NONE)
    protected AccessResultData accessResultData;

    public void init(ResponseData responseData, ResultData resultData) {

        setCreateTime(new Timestamp(new Date().getTime())); // TODO response time
        Beans.copy(responseData, this).execute();
        setStatus(Constants.OK_STATUS);

        AccessResultData accessResultData = new AccessResultDataImpl();
        Beans.copy(resultData, accessResultData).execute();
        setAccessResultData(accessResultData);
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getSessionId()
     */
    public String getSessionId() {
        return sessionId;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setSessionId(java.lang.String)
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getRuleId()
     */
    public String getRuleId() {
        return ruleId;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setRuleId(java.lang.String)
     */
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getUrl()
     */
    public String getUrl() {
        return url;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setUrl(java.lang.String)
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getParentUrl()
     */
    public String getParentUrl() {
        return parentUrl;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setParentUrl(java.lang.String)
     */
    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getStatus()
     */
    public Integer getStatus() {
        return status;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setStatus(java.lang.Integer)
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getHttpStatusCode()
     */
    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setHttpStatusCode(java.lang.Integer)
     */
    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getMethod()
     */
    public String getMethod() {
        return method;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setMethod(java.lang.String)
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getMimeType()
     */
    public String getMimeType() {
        return mimeType;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setMimeType(java.lang.String)
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getCreateTime()
     */
    public Timestamp getCreateTime() {
        return createTime;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setCreateTime(java.sql.Timestamp)
     */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getAccessResultDataAsOne()
     */
    public AccessResultData getAccessResultData() {
        return accessResultData;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setAccessResultDataAsOne(org.seasar.robot.db.exentity.AccessResultData)
     */
    public void setAccessResultData(AccessResultData accessResultDataAsOne) {
        this.accessResultData = accessResultDataAsOne;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#getExecutionTime()
     */
    public Integer getExecutionTime() {
        return executionTime;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.AccessResult#setExecutionTime(java.lang.Integer)
     */
    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

}
