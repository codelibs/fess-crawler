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

import org.codelibs.fess.crawler.util.UserInfoUtil;

/**
 * Thrown when the {@code Visit-time} of robots.txt asks the crawler to visit the origin of a URL only in a time of day
 * that it is not in now.
 *
 * <p>Unlike {@link RobotsTxtDisallowedException} it is not a {@link CrawlingAccessException}: the URL is not forbidden,
 * it is only not due, and nothing has failed. A catch of {@code CrawlingAccessException}, which the crawler logs as a
 * failure URL, must not take it. The crawler thread skips the URL and neither retries nor reports it.</p>
 */
public class RobotsTxtVisitTimeException extends CrawlerSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new RobotsTxtVisitTimeException.
     *
     * @param url the URL that is skipped because of the Visit-time of robots.txt
     */
    public RobotsTxtVisitTimeException(final String url) {
        super("Outside the Visit-time of robots.txt: " + UserInfoUtil.removeUserInfo(url));
    }

}
