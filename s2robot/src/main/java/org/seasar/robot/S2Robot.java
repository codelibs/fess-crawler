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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.seasar.framework.container.S2Container;
import org.seasar.framework.util.StringUtil;
import org.seasar.robot.client.S2RobotClientFactory;
import org.seasar.robot.filter.UrlFilter;
import org.seasar.robot.interval.IntervalController;
import org.seasar.robot.rule.RuleManager;
import org.seasar.robot.service.DataService;
import org.seasar.robot.service.UrlQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * S2Robot manages/controls a crawling information.
 * 
 * @author shinsuke
 *
 */
public class S2Robot implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(S2Robot.class);

    @Resource
    protected UrlQueueService urlQueueService;

    @Resource
    protected DataService dataService;

    @Resource
    protected UrlFilter urlFilter;

    @Resource
    protected RuleManager ruleManager;

    @Resource
    protected S2Container container;

    @Resource
    protected IntervalController intervalController;

    @Resource
    protected S2RobotClientFactory clientFactory;

    protected S2RobotContext robotContext;

    protected boolean background = false;

    protected boolean daemon = false;

    protected int threadPriority = Thread.NORM_PRIORITY;

    protected Thread parentThread;

    public S2Robot() {
        robotContext = new S2RobotContext();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        robotContext.sessionId = sdf.format(new Date());
    }

    public void addUrl(String url) {
        urlQueueService.add(robotContext.sessionId, url);
        urlFilter.processUrl(url);
    }

    public String getSessionId() {
        return robotContext.sessionId;
    }

    public void setSessionId(String sessionId) {
        if (StringUtil.isNotBlank(sessionId)
                && !sessionId.equals(robotContext.sessionId)) {
            urlQueueService.updateSessionId(robotContext.sessionId, sessionId);
            robotContext.sessionId = sessionId;
        }
    }

    public String execute() {
        parentThread = new Thread(this, "Robot-" + robotContext.sessionId);
        parentThread.setDaemon(daemon);
        parentThread.start();
        if (!background) {
            awaitTermination();
        }
        return robotContext.sessionId;
    }

    public void awaitTermination() {
        awaitTermination(0);
    }

    public void awaitTermination(long millis) {
        if (parentThread != null) {
            try {
                parentThread.join(millis);
            } catch (InterruptedException e) {
                logger.warn("Interrupted job at " + parentThread.getName());
            }
        }
    }

    public void cleanup(String sessionId) {
        // TODO transaction?
        urlQueueService.delete(sessionId);
        dataService.delete(sessionId);
        urlFilter.clear();
    }

    public void addIncludeFilter(String regexp) {
        if (StringUtil.isNotBlank(regexp)) {
            urlFilter.addInclude(regexp);
        }
    }

    public void addExcludeFilter(String regexp) {
        if (StringUtil.isNotBlank(regexp)) {
            urlFilter.addExclude(regexp);
        }
    }

    public void stop() {
        robotContext.running = false;
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

    public S2RobotClientFactory getClientFactory() {
        return clientFactory;
    }

    public boolean isBackground() {
        return background;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        // context
        robotContext.urlFilter = urlFilter;
        robotContext.ruleManager = ruleManager;
        robotContext.intervalController = intervalController;

        urlFilter.init(robotContext.sessionId);

        ThreadGroup threadGroup = new ThreadGroup("Robot-"
                + robotContext.sessionId);
        Thread[] threads = new Thread[robotContext.getNumOfThread()];
        for (int i = 0; i < robotContext.getNumOfThread(); i++) {
            S2RobotThread robotThread = (S2RobotThread) container
                    .getComponent("robotThread");
            robotThread.robotContext = robotContext;
            robotThread.clientFactory = clientFactory;
            threads[i] = new Thread(threadGroup, robotThread, "Robot-"
                    + robotContext.sessionId + "-" + Integer.toString(i + 1));
            threads[i].setDaemon(daemon);
            threads[i].setPriority(threadPriority);
        }

        // run
        robotContext.running = true;
        for (int i = 0; i < robotContext.numOfThread; i++) {
            threads[i].start();
        }

        // join
        for (int i = 0; i < robotContext.numOfThread; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                logger.warn("Interrupted job at " + threads[i].getName());
            }
        }
        robotContext.running = false;

        urlQueueService.saveSession(robotContext.sessionId);
    }

    public S2RobotContext getRobotContext() {
        return robotContext;
    }

    public void setNumOfThread(int numOfThread) {
        robotContext.numOfThread = numOfThread;
    }

    public void setMaxThreadCheckCount(int maxThreadCheckCount) {
        robotContext.maxThreadCheckCount = maxThreadCheckCount;
    }

    public void setMaxDepth(int maxDepth) {
        robotContext.maxDepth = maxDepth;
    }

    public void setMaxAccessCount(long maxAccessCount) {
        robotContext.maxAccessCount = maxAccessCount;
    }

    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }
}
