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
package org.seasar.robot.processor.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.robot.Constants;
import org.seasar.robot.S2RobotContext;
import org.seasar.robot.entity.AccessResult;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.ResultData;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.processor.ResponseProcessor;
import org.seasar.robot.service.UrlQueueService;
import org.seasar.robot.transformer.Transformer;
import org.seasar.robot.util.CrawlingParameterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class DefaultResponseProcessor implements ResponseProcessor {
    private static final Logger logger = LoggerFactory
            .getLogger(DefaultResponseProcessor.class);

    protected Transformer transformer;

    /* (non-Javadoc)
     * @see org.seasar.robot.processor.impl.ResponseProcessor#process(org.seasar.robot.entity.ResponseData)
     */
    public void process(ResponseData responseData) {
        if (transformer != null) {
            ResultData resultData = transformer.transform(responseData);
            if (resultData != null) {
                UrlQueue urlQueue = CrawlingParameterUtil.getUrlQueue();
                processResult(urlQueue, responseData, resultData);
            } else {
                logger.warn("No data for (" + responseData.getUrl() + ", "
                        + responseData.getMimeType() + ")");
            }
        }
    }

    protected void processResult(UrlQueue urlQueue, ResponseData responseData,
            ResultData resultData) {
        AccessResult accessResult = SingletonS2Container
                .getComponent(AccessResult.class);
        accessResult.init(responseData, resultData);

        S2RobotContext robotContext = CrawlingParameterUtil.getRobotContext();
        UrlQueueService urlQueueService = CrawlingParameterUtil
                .getUrlQueueService();
        synchronized (robotContext.getAccessCountLock()) {
            if (!urlQueueService.visited(urlQueue)) {
                if (checkAccessCount(robotContext)) {
                    //  store
                    CrawlingParameterUtil.getDataService().store(accessResult);

                    //  add and filter urls 
                    storeChildUrls(
                            robotContext,
                            resultData.getChildUrlSet(),
                            urlQueue.getUrl(),
                            urlQueue.getDepth() != null ? urlQueue.getDepth() + 1
                                    : 1);

                    // count up
                    if (robotContext.getMaxAccessCount() > 0) {
                        robotContext.setAccessCount(robotContext
                                .getAccessCount() + 1);
                    }
                } else {
                    // cancel crawling
                    List<UrlQueue> newUrlQueueList = new ArrayList<UrlQueue>();
                    newUrlQueueList.add(urlQueue);
                    urlQueueService.offerAll(robotContext.getSessionId(),
                            newUrlQueueList);
                }
            }
        }

    }

    private boolean checkAccessCount(S2RobotContext robotContext) {
        if (robotContext.getMaxAccessCount() > 0) {
            if (robotContext.getAccessCount() < robotContext
                    .getMaxAccessCount()) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void storeChildUrls(S2RobotContext robotContext,
            Set<String> childUrlList, String url, int depth) {
        //  add url and filter 
        List<UrlQueue> childList = new ArrayList<UrlQueue>();
        for (String childUrl : childUrlList) {
            if (robotContext.getUrlFilter().match(childUrl)) {
                UrlQueue uq = SingletonS2Container.getComponent(UrlQueue.class);
                uq.setCreateTime(new Timestamp(new Date().getTime()));
                uq.setDepth(depth);
                uq.setMethod(Constants.GET_METHOD);
                uq.setParentUrl(url);
                uq.setSessionId(robotContext.getSessionId());
                uq.setUrl(childUrl);
                childList.add(uq);
            }
        }
        CrawlingParameterUtil.getUrlQueueService().offerAll(
                robotContext.getSessionId(), childList);
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }
}
