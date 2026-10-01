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
package org.codelibs.fess.crawler.helper;

/**
 * Retrieves a robots.txt over the transport of a crawler client.
 * The implementation must not follow redirects; {@link RobotsTxtHelper} follows them itself.
 */
@FunctionalInterface
public interface RobotsTxtFetcher {

    /**
     * Fetches a robots.txt URL once, without following redirects.
     *
     * @param robotsTxtUrl the URL to fetch
     * @return the response
     * @throws Exception if the request fails; a {@link org.codelibs.fess.crawler.exception.MaxLengthExceededException}
     *         means the file is too large and is treated as allow-all
     */
    RobotsTxtResponse fetch(String robotsTxtUrl) throws Exception;
}
