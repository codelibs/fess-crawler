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
package org.seasar.robot;

import java.util.HashSet;
import java.util.Set;

import org.seasar.robot.filter.UrlFilter;
import org.seasar.robot.interval.IntervalController;
import org.seasar.robot.rule.RuleManager;

/**
 * @author shinsuke
 *
 */
public class S2RobotContext {
    protected String sessionId;

    protected Integer activeThreadCount = 0;

    protected Object activeThreadCountLock = new Object();

    protected volatile Long accessCount = 0L;

    protected Object accessCountLock = new Object();

    protected volatile boolean running = false;

    protected UrlFilter urlFilter;

    protected RuleManager ruleManager;

    protected IntervalController intervalController;

    protected Set<String> robotTxtUrlSet = new HashSet<String>(); // TODO size?

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getActiveThreadCount() {
        return activeThreadCount;
    }

    public void setActiveThreadCount(Integer activeThreadCount) {
        this.activeThreadCount = activeThreadCount;
    }

    public Long getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Long accessCount) {
        this.accessCount = accessCount;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public UrlFilter getUrlFilter() {
        return urlFilter;
    }

    public void setUrlFilter(UrlFilter urlFilter) {
        this.urlFilter = urlFilter;
    }

    public RuleManager getRuleManager() {
        return ruleManager;
    }

    public void setRuleManager(RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }

    public IntervalController getIntervalController() {
        return intervalController;
    }

    public void setIntervalController(IntervalController intervalController) {
        this.intervalController = intervalController;
    }

    public Set<String> getRobotTxtUrlSet() {
        return robotTxtUrlSet;
    }

    public void setRobotTxtUrlSet(Set<String> robotTxtUrlSet) {
        this.robotTxtUrlSet = robotTxtUrlSet;
    }
}
