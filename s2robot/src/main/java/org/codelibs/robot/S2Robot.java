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
package org.codelibs.robot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.codelibs.core.lang.StringUtil;
import org.codelibs.robot.client.S2RobotClientFactory;
import org.codelibs.robot.container.RobotContainer;
import org.codelibs.robot.filter.UrlFilter;
import org.codelibs.robot.interval.IntervalController;
import org.codelibs.robot.rule.RuleManager;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.service.UrlQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * S2Robot manages/controls a crawling information.
 *
 * @author shinsuke
 *
 */
public class S2Robot implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(S2Robot.class); // NOPMD

    @Resource
    protected UrlQueueService urlQueueService;

    @Resource
    protected DataService dataService;

    @Resource
    protected UrlFilter urlFilter;

    @Resource
    protected RuleManager ruleManager;

    @Resource
    protected RobotContainer robotContainer;

    @Resource
    protected IntervalController intervalController;

    @Resource
    protected S2RobotClientFactory clientFactory;

    protected S2RobotContext robotContext;

    protected boolean background = false;

    protected boolean daemon = false;

    protected int threadPriority = Thread.NORM_PRIORITY;

    protected Thread parentThread;

    protected ThreadGroup robotThreadGroup;

    public S2Robot() {
        robotContext = new S2RobotContext();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS",
                Locale.ENGLISH);
        robotContext.sessionId = sdf.format(new Date());
    }

    public void addUrl(final String url) {
        urlQueueService.add(robotContext.sessionId, url);
        urlFilter.processUrl(url);
    }

    public String getSessionId() {
        return robotContext.sessionId;
    }

    public void setSessionId(final String sessionId) {
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

    public void awaitTermination(final long millis) {
        if (parentThread != null) {
            try {
                parentThread.join(millis);
            } catch (final InterruptedException e) {
                logger.warn("Interrupted job at " + parentThread.getName());
            }
        }
    }

    public void cleanup(final String sessionId) {
        // TODO transaction?
        urlQueueService.delete(sessionId);
        dataService.delete(sessionId);
        urlFilter.clear();
    }

    public void addIncludeFilter(final String regexp) {
        if (StringUtil.isNotBlank(regexp)) {
            urlFilter.addInclude(regexp);
        }
    }

    public void addExcludeFilter(final String regexp) {
        if (StringUtil.isNotBlank(regexp)) {
            urlFilter.addExclude(regexp);
        }
    }

    public void stop() {
        robotContext.running = false;
        try {
            if (robotThreadGroup != null) {
                robotThreadGroup.interrupt();
            }
        } catch (final Exception e) {
            // ignore
        }
    }

    public UrlFilter getUrlFilter() {
        return urlFilter;
    }

    public void setUrlFilter(final UrlFilter urlFilter) {
        this.urlFilter = urlFilter;
    }

    public RuleManager getRuleManager() {
        return ruleManager;
    }

    public void setRuleManager(final RuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }

    public IntervalController getIntervalController() {
        return intervalController;
    }

    public void setIntervalController(
            final IntervalController intervalController) {
        this.intervalController = intervalController;
    }

    public S2RobotClientFactory getClientFactory() {
        return clientFactory;
    }

    public boolean isBackground() {
        return background;
    }

    public void setBackground(final boolean background) {
        this.background = background;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(final boolean daemon) {
        this.daemon = daemon;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // context
        robotContext.urlFilter = urlFilter;
        robotContext.ruleManager = ruleManager;
        robotContext.intervalController = intervalController;

        urlFilter.init(robotContext.sessionId);

        robotThreadGroup = new ThreadGroup("Robot-" + robotContext.sessionId);
        final Thread[] threads = new Thread[robotContext.getNumOfThread()];
        for (int i = 0; i < robotContext.getNumOfThread(); i++) {
            final S2RobotThread robotThread = robotContainer
                    .getComponent("robotThread");
            robotThread.robotContext = robotContext;
            robotThread.clientFactory = clientFactory;
            threads[i] = new Thread(robotThreadGroup, robotThread, "Robot-"
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
            } catch (final InterruptedException e) {
                logger.warn("Interrupted job at " + threads[i].getName());
            }
        }
        robotContext.running = false;

        urlQueueService.saveSession(robotContext.sessionId);
    }

    public S2RobotContext getRobotContext() {
        return robotContext;
    }

    public void setNumOfThread(final int numOfThread) {
        robotContext.numOfThread = numOfThread;
    }

    public void setMaxThreadCheckCount(final int maxThreadCheckCount) {
        robotContext.maxThreadCheckCount = maxThreadCheckCount;
    }

    public void setMaxDepth(final int maxDepth) {
        robotContext.maxDepth = maxDepth;
    }

    public void setMaxAccessCount(final long maxAccessCount) {
        robotContext.maxAccessCount = maxAccessCount;
    }

    public void setThreadPriority(final int threadPriority) {
        this.threadPriority = threadPriority;
    }
}
