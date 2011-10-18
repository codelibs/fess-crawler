/*
 * Copyright 2004-2010 the Seasar Foundation and the Others.
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
package org.seasar.robot.interval.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.time.StopWatch;
import org.seasar.extension.unit.S2TestCase;
import org.seasar.robot.entity.UrlQueue;
import org.seasar.robot.entity.UrlQueueImpl;
import org.seasar.robot.util.CrawlingParameterUtil;

/**
 * @author hayato
 * 
 */
public class HostIntervalControllerTest extends S2TestCase {
    @Override
    protected String getRootDicon() throws Throwable {
        return "app.dicon";
    }

    /**
     * 同一ホストに対するクローリングのインターバルが正しく動作すること
     */
    public void test_delayBeforeProcessing() {
        // 同時実行数
        int numTasks = 100;
        // インターバル
        Long waittime = 100L;

        CrawlingParameterUtil.setUrlQueue(new UrlQueueImpl());
        final UrlQueue q = CrawlingParameterUtil.getUrlQueue();
        for (int i = 0; i < numTasks; i++) {
            q.setUrl("http://example.com");
        }

        final HostIntervalController controller = new HostIntervalController();
        controller.delayMillisBeforeProcessing = waittime;
        controller.delayMillisAfterProcessing = 0L;
        controller.delayMillisForWaitingNewUrl = 0L;
        controller.delayMillisAtNoUrlInQueue = 0L;

        Callable<Integer> testCallable = new Callable<Integer>() {
            public Integer call() throws Exception {
                CrawlingParameterUtil.setUrlQueue(q);
                controller.delayBeforeProcessing();
                return 0;
            }
        };

        // Callableタスクを複数生成
        List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(testCallable);
        }

        // 時間取得
        StopWatch watch = new StopWatch();
        watch.start();

        // Callableタスク(複数)を実行する
        ExecutorService executor = Executors.newFixedThreadPool(numTasks);
        try {
            List<Future<Integer>> futures = executor.invokeAll(tasks);
            for (Future<Integer> future : futures) {
                future.get();
            }
        } catch (InterruptedException e) {
            // no thing to do
        } catch (ExecutionException e) {
            // no thing to do
        }

        assertTrue(watch.getTime() > waittime * (numTasks - 1));
    }
}