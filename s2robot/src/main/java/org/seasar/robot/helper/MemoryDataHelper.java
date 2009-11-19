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
    protected volatile Map<String, Queue<UrlQueue>> urlQueueMap = new HashMap<String, Queue<UrlQueue>>();

    protected volatile Map<String, Map<String, AccessResult>> sessionMap = new HashMap<String, Map<String, AccessResult>>();

    public void clear() {
        urlQueueMap.clear();
        sessionMap.clear();
    }

    public synchronized Queue<UrlQueue> getUrlQueueList(String sessionId) {
        Queue<UrlQueue> urlQueueList = urlQueueMap.get(sessionId);
        if (urlQueueList == null) {
            urlQueueList = new LinkedList<UrlQueue>();
            urlQueueMap.put(sessionId, urlQueueList);
        }
        return urlQueueList;
    }

    public synchronized void addUrlQueueList(String sessionId,
            Queue<UrlQueue> urlQueueList) {
        Queue<UrlQueue> uqList = getUrlQueueList(sessionId);
        uqList.addAll(urlQueueList);
        urlQueueMap.put(sessionId, uqList);
    }

    public synchronized void removeUrlQueueList(String sessionId) {
        urlQueueMap.remove(sessionId);
    }

    public synchronized void clearUrlQueueList() {
        urlQueueMap.clear();
    }

    public synchronized Map<String, AccessResult> getAccessResultMap(
            String sessionId) {
        Map<String, AccessResult> arMap = sessionMap.get(sessionId);
        if (arMap == null) {
            arMap = new HashMap<String, AccessResult>();
            sessionMap.put(sessionId, arMap);
        }
        return arMap;
    }

    public synchronized void deleteAccessResultMap(String sessionId) {
        sessionMap.remove(sessionId);
    }

    public synchronized void deleteAllAccessResultMap() {
        sessionMap.clear();
    }

    public synchronized List<AccessResult> getAccessResultList(String url) {
        List<AccessResult> acList = new ArrayList<AccessResult>();
        for (Map.Entry<String, Map<String, AccessResult>> entry : sessionMap
                .entrySet()) {
            if (entry.getValue() != null) {
                AccessResult ar = entry.getValue().get(url);
                if (ar != null) {
                    acList.add(ar);
                }
            }
        }
        // TODO order
        return acList;
    }

    protected volatile Map<String, List<Pattern>> includeUrlPatternMap = new HashMap<String, List<Pattern>>();

    protected volatile Map<String, List<Pattern>> excludeUrlPatternMap = new HashMap<String, List<Pattern>>();

    public synchronized void addIncludeUrlPattern(String sessionId, String url) {
        List<Pattern> patternList = getIncludeUrlPatternList(sessionId);
        patternList.add(Pattern.compile(url));
    }

    public List<Pattern> getIncludeUrlPatternList(String sessionId) {
        List<Pattern> patternList = includeUrlPatternMap.get(sessionId);
        if (patternList == null) {
            patternList = new ArrayList<Pattern>();
            includeUrlPatternMap.put(sessionId, patternList);
        }
        return patternList;
    }

    public synchronized void addExcludeUrlPattern(String sessionId, String url) {
        List<Pattern> patternList = getExcludeUrlPatternList(sessionId);
        patternList.add(Pattern.compile(url));
    }

    public List<Pattern> getExcludeUrlPatternList(String sessionId) {
        List<Pattern> patternList = excludeUrlPatternMap.get(sessionId);
        if (patternList == null) {
            patternList = new ArrayList<Pattern>();
            excludeUrlPatternMap.put(sessionId, patternList);
        }
        return patternList;
    }

    public synchronized void clearUrlPattern(String sessionId) {
        includeUrlPatternMap.remove(sessionId);
        excludeUrlPatternMap.remove(sessionId);
    }

    public synchronized void clearUrlPattern() {
        includeUrlPatternMap.clear();
        excludeUrlPatternMap.clear();
    }
}
