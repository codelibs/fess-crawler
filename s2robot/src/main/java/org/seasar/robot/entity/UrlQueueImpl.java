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

/**
 * @author shinsuke
 *
 */
public class UrlQueueImpl implements UrlQueue {
    protected Long id;

    protected String sessionId;

    protected String method;

    protected String url;

    protected String parentUrl;

    protected Integer depth;

    protected Timestamp createTime;

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#getSessionId()
     */
    public String getSessionId() {
        return sessionId;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#setSessionId(java.lang.String)
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#getMethod()
     */
    public String getMethod() {
        return method;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#setMethod(java.lang.String)
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#getUrl()
     */
    public String getUrl() {
        return url;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#setUrl(java.lang.String)
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#getParentUrl()
     */
    public String getParentUrl() {
        return parentUrl;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#setParentUrl(java.lang.String)
     */
    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#getDepth()
     */
    public Integer getDepth() {
        return depth;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#setDepth(java.lang.Integer)
     */
    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#getCreateTime()
     */
    public Timestamp getCreateTime() {
        return createTime;
    }

    /* (non-Javadoc)
     * @see org.seasar.robot.entity.UrlQueue#setCreateTime(java.sql.Timestamp)
     */
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
