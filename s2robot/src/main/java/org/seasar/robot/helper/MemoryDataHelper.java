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
package org.seasar.robot.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.entity.UrlQueue;

/**
 * @author shinsuke
 * 
 */
public class MemoryDataHelper {
    protected volatile Map<String, Queue<UrlQueue>> urlQueueMap = // NOPMD
        new HashMap<String, Queue<UrlQueue>>();

    protected volatile Map<String, Map<String, AccessResult>> sessionMap = // NOPMD
        new HashMap<String, Map<String, AccessResult>>();

    protected volatile Map<String, List<Pattern>> includeUrlPatternMap = // NOPMD
        new HashMap<String, List<Pattern>>();

    protected volatile Map<String, List<Pattern>> excludeUrlPatternMap = // NOPMD
        new HashMap<String, List<Pattern>>();

    public void clear() {
        urlQueueMap.clear();
        sessionMap.clear();
    }

    public synchronized Queue<UrlQueue> getUrlQueueList(final String sessionId) {
        Queue<UrlQueue> urlQueueList = urlQueueMap.get(sessionId);
        if (urlQueueList == null) {
            urlQueueList = new LinkedList<UrlQueue>();
            urlQueueMap.put(sessionId, urlQueueList);
        }
        return urlQueueList;
    }

    public synchronized void addUrlQueueList(final String sessionId,
            final Queue<UrlQueue> urlQueueList) {
        final Queue<UrlQueue> uqList = getUrlQueueList(sessionId);
        uqList.addAll(urlQueueList);
        urlQueueMap.put(sessionId, uqList);
    }

    public synchronized void removeUrlQueueList(final String sessionId) {
        urlQueueMap.remove(sessionId);
    }

    public synchronized void clearUrlQueueList() {
        urlQueueMap.clear();
    }

    public synchronized Map<String, AccessResult> getAccessResultMap(
            final String sessionId) {
        Map<String, AccessResult> arMap = sessionMap.get(sessionId);
        if (arMap == null) {
            arMap = new HashMap<String, AccessResult>();
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

    public synchronized List<AccessResult> getAccessResultList(final String url) {
        final List<AccessResult> acList = new ArrayList<AccessResult>();
        for (final Map.Entry<String, Map<String, AccessResult>> entry : sessionMap
            .entrySet()) {
            if (entry.getValue() != null) {
                final AccessResult ar = entry.getValue().get(url);
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
            patternList = new ArrayList<Pattern>();
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
            patternList = new ArrayList<Pattern>();
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
