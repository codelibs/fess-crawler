/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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

    Long getId();

    void setId(Long id);

    String getSessionId();

    void setSessionId(String sessionId);

    String getMethod();

    void setMethod(String method);

    String getUrl();

    void setUrl(String url);

    String getMetaData();

    void setMetaData(String metaData);

    String getEncoding();

    void setEncoding(String encoding);

    String getParentUrl();

    void setParentUrl(String parentUrl);

    Integer getDepth();

    void setDepth(Integer depth);

    Timestamp getLastModified();

    void setLastModified(Timestamp lastModified);

    Timestamp getCreateTime();

    void setCreateTime(Timestamp createTime);

}
