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
package org.codelibs.fess.crawler.exception;

import java.util.Set;

import org.codelibs.fess.crawler.entity.RequestData;

/**
 * ChildUrlsException is thrown when having child urls.
 *
 * @author shinsuke
 *
 */
public class ChildUrlsException extends CrawlerSystemException {

    private static final long serialVersionUID = 1L;

    private final Set<RequestData> childUrlList;

    public ChildUrlsException(final Set<RequestData> childUrlList, final String description) {
        super("Threw child urls(" + childUrlList.size() + "). " + description, false, false);
        this.childUrlList = childUrlList;
    }

    public Set<RequestData> getChildUrlList() {
        return childUrlList;
    }
}
