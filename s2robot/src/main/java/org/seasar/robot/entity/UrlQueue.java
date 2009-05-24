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
public interface UrlQueue {

    public abstract Long getId();

    public abstract void setId(Long id);

    public abstract String getSessionId();

    public abstract void setSessionId(String sessionId);

    public abstract String getMethod();

    public abstract void setMethod(String method);

    public abstract String getUrl();

    public abstract void setUrl(String url);

    public abstract String getParentUrl();

    public abstract void setParentUrl(String parentUrl);

    public abstract Integer getDepth();

    public abstract void setDepth(Integer depth);

    public abstract Timestamp getCreateTime();

    public abstract void setCreateTime(Timestamp createTime);

}