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
package org.codelibs.fess.crawler.exception;

import java.util.Set;

import org.codelibs.fess.crawler.entity.RequestData;

/**
 * {@link ChildUrlsException} is thrown when child URLs are found during crawling.
 * It extends {@link CrawlerSystemException} and holds a set of {@link RequestData}
 * representing the child URLs that caused the exception.
 *
 */
public class ChildUrlsException extends CrawlerSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * The list of child URLs.
     */
    private final Set<RequestData> childUrlList;

    /**
     * Creates a new instance of ChildUrlsException.
     * @param childUrlList The list of child URLs.
     * @param description The description of the exception.
     */
    public ChildUrlsException(final Set<RequestData> childUrlList, final String description) {
        super("Threw child urls(" + childUrlList.size() + "). " + description, false, false);
        this.childUrlList = childUrlList;
    }

    /**
     * Returns the list of child URLs.
     * @return The list of child URLs.
     */
    public Set<RequestData> getChildUrlList() {
        return childUrlList;
    }
}
