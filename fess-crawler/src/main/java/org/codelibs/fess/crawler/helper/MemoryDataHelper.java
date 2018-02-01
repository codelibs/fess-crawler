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
package org.codelibs.fess.crawler.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

import org.codelibs.fess.crawler.entity.AccessResultImpl;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;

/**
 * @author shinsuke
 *
 */
public class MemoryDataHelper {
    protected volatile Map<String, Queue<UrlQueueImpl<Long>>> urlQueueMap = new HashMap<>();

    protected volatile Map<String, Map<String, AccessResultImpl<Long>>> sessionMap = new HashMap<>();

    protected volatile Map<String, List<Pattern>> includeUrlPatternMap = new HashMap<>();

    protected volatile Map<String, List<Pattern>> excludeUrlPatternMap = new HashMap<>();

    public void clear() {
        urlQueueMap.clear();
        sessionMap.clear();
    }

    public synchronized Queue<UrlQueueImpl<Long>> getUrlQueueList(final String sessionId) {
        Queue<UrlQueueImpl<Long>> urlQueueList = urlQueueMap.get(sessionId);
        if (urlQueueList == null) {
            urlQueueList = new LinkedList<>();
            urlQueueMap.put(sessionId, urlQueueList);
        }
        return urlQueueList;
    }

    public synchronized void addUrlQueueList(final String sessionId,
            final Queue<UrlQueueImpl<Long>> urlQueueList) {
        final Queue<UrlQueueImpl<Long>> uqList = getUrlQueueList(sessionId);
        uqList.addAll(urlQueueList);
        urlQueueMap.put(sessionId, uqList);
    }

    public synchronized void removeUrlQueueList(final String sessionId) {
        urlQueueMap.remove(sessionId);
    }

    public synchronized void clearUrlQueueList() {
        urlQueueMap.clear();
    }

    public synchronized Map<String, AccessResultImpl<Long>> getAccessResultMap(
            final String sessionId) {
        Map<String, AccessResultImpl<Long>> arMap = sessionMap.get(sessionId);
        if (arMap == null) {
            arMap = new HashMap<>();
            sessionMap.put(sessionId, arMap);
        }
        return arMap;
    }

    public synchronized void deleteAccessResultMap(final String sessionId) {
        sessionMap.remove(sessionId);
    }

    public synchronized void deleteAllAccessResultMap() {
        sessionMap.clear();
    }

    public synchronized List<AccessResultImpl<Long>> getAccessResultList(final String url) {
        final List<AccessResultImpl<Long>> acList = new ArrayList<>();
        for (final Map.Entry<String, Map<String, AccessResultImpl<Long>>> entry : sessionMap
                .entrySet()) {
            if (entry.getValue() != null) {
                final AccessResultImpl<Long> ar = entry.getValue().get(url);
                if (ar != null) {
                    acList.add(ar);
                }
            }
        }
        // TODO order
        return acList;
    }

    public synchronized void addIncludeUrlPattern(final String sessionId,
            final String url) {
        final List<Pattern> patternList = getIncludeUrlPatternList(sessionId);
        patternList.add(Pattern.compile(url));
    }

    public List<Pattern> getIncludeUrlPatternList(final String sessionId) {
        List<Pattern> patternList = includeUrlPatternMap.get(sessionId);
        if (patternList == null) {
            patternList = new ArrayList<>();
            includeUrlPatternMap.put(sessionId, patternList);
        }
        return patternList;
    }

    public synchronized void addExcludeUrlPattern(final String sessionId,
            final String url) {
        final List<Pattern> patternList = getExcludeUrlPatternList(sessionId);
        patternList.add(Pattern.compile(url));
    }

    public List<Pattern> getExcludeUrlPatternList(final String sessionId) {
        List<Pattern> patternList = excludeUrlPatternMap.get(sessionId);
        if (patternList == null) {
            patternList = new ArrayList<>();
            excludeUrlPatternMap.put(sessionId, patternList);
        }
        return patternList;
    }

    public synchronized void clearUrlPattern(final String sessionId) {
        includeUrlPatternMap.remove(sessionId);
        excludeUrlPatternMap.remove(sessionId);
    }

    public synchronized void clearUrlPattern() {
        includeUrlPatternMap.clear();
        excludeUrlPatternMap.clear();
    }
}
