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
package org.codelibs.fess.crawler.service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author shinsuke
 *
 */
public interface UrlFilterService {

    void addIncludeUrlFilter(String sessionId, String url);

    void addIncludeUrlFilter(String sessionId, List<String> urlList);

    void addExcludeUrlFilter(String sessionId, String url);

    void addExcludeUrlFilter(String sessionId, List<String> urlList);

    void delete(String sessionId);

    void deleteAll();

    List<Pattern> getIncludeUrlPatternList(String sessionId);

    List<Pattern> getExcludeUrlPatternList(String sessionId);

}
