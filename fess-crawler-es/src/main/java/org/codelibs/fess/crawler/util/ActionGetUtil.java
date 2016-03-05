/*
 * Copyright 2012-2016 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.util;


import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.index.IndexNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ActionGetUtil {
    private static final Logger logger = LoggerFactory.getLogger(ActionGetUtil.class);

    private static long retryInterval = 60 * 1000;

    private static int maxRetryCount = 10;

    private static long actionTimeoutMillis = 180 * 1000;

    public static <T> T actionGet(ListenableActionFuture<T> future) {
        int retryCount = 0;
        while (true) {
            try {
                return future.actionGet(actionTimeoutMillis, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                if (e instanceof IndexNotFoundException) {
                    logger.debug("IndexNotFoundException.");
                    throw e;
                }

                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to actionGet. count:" + retryCount, e);
                }
                if (retryCount > maxRetryCount) {
                    logger.info("Failed to actionGet. All retry failure.", e);
                    throw e;
                }

                retryCount++;
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException ie) {
                    throw e;
                }
            }
        }
    }
}