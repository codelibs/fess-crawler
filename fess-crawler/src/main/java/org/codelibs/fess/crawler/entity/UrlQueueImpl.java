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
public class UrlQueueImpl<IDTYPE> implements UrlQueue<IDTYPE> {
    protected IDTYPE id;

    protected String sessionId;

    protected String method;

    protected String url;

    protected String metaData;

    protected String encoding;

    protected String parentUrl;

    protected Integer depth;

    protected Long lastModified;

    protected Long createTime;

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#getId()
     */
    @Override
    public IDTYPE getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#setId(IDTYPE)
     */
    @Override
    public void setId(final IDTYPE id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#getSessionId()
     */
    @Override
    public String getSessionId() {
        return sessionId;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#setSessionId(java.lang.String)
     */
    @Override
    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#getMethod()
     */
    @Override
    public String getMethod() {
        return method;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#setMethod(java.lang.String)
     */
    @Override
    public void setMethod(final String method) {
        this.method = method;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#getUrl()
     */
    @Override
    public String getUrl() {
        return url;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#setUrl(java.lang.String)
     */
    @Override
    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public String getMetaData() {
        return metaData;
    }

    @Override
    public void setMetaData(final String metaData) {
        this.metaData = metaData;
    }

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#getParentUrl()
     */
    @Override
    public String getParentUrl() {
        return parentUrl;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#setParentUrl(java.lang.String)
     */
    @Override
    public void setParentUrl(final String parentUrl) {
        this.parentUrl = parentUrl;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#getDepth()
     */
    @Override
    public Integer getDepth() {
        return depth;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#setDepth(java.lang.Integer)
     */
    @Override
    public void setDepth(final Integer depth) {
        this.depth = depth;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#getCreateTime()
     */
    @Override
    public Long getCreateTime() {
        return createTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#setCreateTime(java.sql.Long)
     */
    @Override
    public void setCreateTime(final Long createTime) {
        this.createTime = createTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#getLastModified()
     */
    @Override
    public Long getLastModified() {
        return lastModified;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.codelibs.fess.crawler.entity.UrlQueue#setLastModified(java.sql.Long)
     */
    @Override
    public void setLastModified(final Long lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "UrlQueueImpl [id=" + id + ", sessionId=" + sessionId
                + ", method=" + method + ", url=" + url + ", encoding="
                + encoding + ", parentUrl=" + parentUrl + ", depth=" + depth
                + ", lastModified=" + lastModified + ", createTime="
                + createTime + "]";
    }
}
