package org.seasar.robot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.seasar.framework.util.StringUtil;
import org.seasar.robot.config.S2RobotConfig;
import org.seasar.robot.db.exentity.AccessResult;
import org.seasar.robot.db.exentity.UrlQueue;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.filter.UrlFilter;
import org.seasar.robot.http.HttpClient;
import org.seasar.robot.interval.IntervalGenerator;
import org.seasar.robot.rule.Rule;
import org.seasar.robot.rule.RuleManager;
import org.seasar.robot.service.DataService;
import org.seasar.robot.service.UrlQueueService;
import org.seasar.robot.transformer.Transformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S2Robot {

    private static final Logger logger = LoggerFactory.getLogger(S2Robot.class);

    @Resource
    protected UrlQueueService urlQueueService;

    @Resource
    protected DataService dataService;

    @Resource
    protected RuleManager ruleManager;

    @Resource
    protected HttpClient httpClient;

    @Resource
    protected IntervalGenerator intervalGenerator;

    @Resource
    protected UrlFilter urlFilter;

    @Resource
    protected S2RobotConfig robotConfig;

    protected String sessionId;

    private Integer activeThreadCount = 0;

    private Object activeThreadCountLock = new Object();

    private volatile Long accessCount = 0L;

    private Object accessCountLock = new Object();

    public S2Robot() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        sessionId = sdf.format(new Date());
    }

    public void addUrl(String url) {
        urlQueueService.add(sessionId, url);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        if (StringUtil.isNotBlank(sessionId)
                && !sessionId.equals(this.sessionId)) {
            this.sessionId = sessionId;
            urlQueueService.updateSessionId(this.sessionId, sessionId);
        }
    }

    public String execute() {
        ThreadGroup threadGroup = new ThreadGroup("Robot-" + sessionId);
        Thread[] threads = new Thread[robotConfig.getNumOfThread()];
        for (int i = 0; i < robotConfig.getNumOfThread(); i++) {
            threads[i] = new Thread(threadGroup, new S2RobotThread(), "Robot-"
                    + sessionId + "-" + Integer.toString(i + 1));
        }

        // run
        for (int i = 0; i < robotConfig.getNumOfThread(); i++) {
            threads[i].start();
        }

        // join
        for (int i = 0; i < robotConfig.getNumOfThread(); i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                logger.warn("Could not join " + threads[i].getName());
            }
        }

        return sessionId;
    }

    protected boolean isValid(UrlQueue urlQueue) {
        if (urlQueue == null) {
            return false;
        }

        if (StringUtil.isBlank(urlQueue.getUrl())) {
            return false;
        }

        if (robotConfig.getMaxDepth() >= 0
                && urlQueue.getDepth() >= robotConfig.getMaxDepth()) {
            return false;
        }

        //  url filter
        if (urlFilter.match(urlQueue.getUrl())) {
            return true;
        }

        return false;
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

    protected class S2RobotThread implements Runnable {

        protected void startCrawling() {
            synchronized (activeThreadCountLock) {
                activeThreadCount++;
            }
        }

        protected void finishCrawling() {
            synchronized (activeThreadCountLock) {
                activeThreadCount--;
            }
            if (robotConfig.getMaxAccessCount() > 0) {
                synchronized (accessCountLock) {
                    accessCount++;
                }
            }
        }

        protected boolean isContinue(int tcCount) {
            if (tcCount < robotConfig.getMaxThreadCheckCount()) {
                return checkAccessCount();
            }
            return false;
        }

        protected boolean checkAccessCount() {
            if (robotConfig.getMaxAccessCount() > 0) {
                if (accessCount < robotConfig.getMaxAccessCount()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            int threadCheckCount = 0;
            while (isContinue(threadCheckCount)) {
                UrlQueue urlQueue = urlQueueService.poll(sessionId);
                if (isValid(urlQueue)) {
                    try {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Starting " + urlQueue.getUrl());
                        }

                        // TODO debug
                        if (urlQueue.getUrl().indexOf("inquiry.html") > 0) {
                            System.out.println(urlQueue.getUrl());
                        }

                        startCrawling();

                        // access an url
                        ResponseData responseData = httpClient.doGet(urlQueue
                                .getUrl());
                        responseData.setParentUrl(urlQueue.getParentUrl());
                        responseData.setSessionId(sessionId);

                        // get a rule
                        Rule rule = ruleManager.getRule(responseData);
                        if (rule != null) {
                            responseData.setRuleId(rule.getRuleId());
                            Transformer transformer = rule.getTransformer();
                            if (transformer != null) {
                                ResultData resultData = transformer
                                        .transform(responseData);
                                if (resultData != null) {
                                    AccessResult accessResult = new AccessResult(
                                            responseData, resultData);

                                    if (checkAccessCount()) {
                                        //  store
                                        dataService.store(accessResult);
                                    }

                                    //  add url and filter 
                                    storeChildUrls(
                                            resultData.getChildUrlSet(),
                                            urlQueue.getUrl(),
                                            urlQueue.getDepth() != null ? urlQueue
                                                    .getDepth() + 1
                                                    : 1);
                                } else {
                                    logger.warn("No data for ("
                                            + responseData.getUrl() + ", "
                                            + responseData.getMimeType() + ")");
                                }
                            } else {
                                logger.warn("No transformer for ("
                                        + responseData.getUrl() + ", "
                                        + responseData.getMimeType() + ")");
                            }
                        } else {
                            logger.warn("No rule for (" + responseData.getUrl()
                                    + ", " + responseData.getMimeType() + ")");
                        }

                        if (logger.isDebugEnabled()) {
                            logger.debug("Finished " + urlQueue.getUrl());
                        }
                    } catch (Exception e) {
                        logger.error("Crawling Exception at "
                                + urlQueue.getUrl(), e);
                    } finally {
                        threadCheckCount = 0; // clear
                        finishCrawling();
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("No url in a queue. (" + threadCheckCount
                                + ")");
                    }
                    try {
                        Thread.sleep(robotConfig.getThreadCheckInterval());
                    } catch (InterruptedException e) {
                        logger.warn("Could not sleep a thread: "
                                + Thread.currentThread().getName(), e);
                    }
                    threadCheckCount++;
                }

                // interval
                if (intervalGenerator != null) {
                    try {
                        Thread.sleep(intervalGenerator.getTime());
                    } catch (InterruptedException e) {
                        logger.warn("Could not sleep a thread: "
                                + Thread.currentThread().getName(), e);
                    }
                }
            }
        }

        private void storeChildUrls(Set<String> childUrlList, String url,
                int depth) {
            //  add url and filter 
            List<UrlQueue> childList = new ArrayList<UrlQueue>();
            for (String childUrl : childUrlList) {
                if (urlFilter.match(childUrl)) {
                    UrlQueue uq = new UrlQueue();
                    uq.setCreateTime(new Timestamp(new Date().getTime()));
                    uq.setDepth(depth);
                    uq.setMethod(Constants.GET_METHOD);
                    uq.setParentUrl(url);
                    uq.setSessionId(sessionId);
                    uq.setUrl(childUrl);
                    childList.add(uq);
                }
            }
            urlQueueService.offerAll(sessionId, childList);
        }
    }

}
