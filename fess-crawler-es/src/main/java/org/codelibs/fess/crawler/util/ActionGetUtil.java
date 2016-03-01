package org.codelibs.fess.crawler.util;


import org.apache.lucene.index.IndexNotFoundException;
import org.elasticsearch.action.ListenableActionFuture;
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
