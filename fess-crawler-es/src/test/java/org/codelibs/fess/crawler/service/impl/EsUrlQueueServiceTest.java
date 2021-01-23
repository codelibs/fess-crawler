/*
 * Copyright 2012-2020 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.service.impl;

import static org.codelibs.fesen.runner.FesenRunner.newConfigs;

import java.util.UUID;

import javax.annotation.Resource;

import org.codelibs.fesen.index.query.QueryBuilders;
import org.codelibs.fesen.runner.FesenRunner;
import org.codelibs.fess.crawler.client.FesenClient;
import org.codelibs.fess.crawler.entity.EsUrlQueue;
import org.dbflute.utflute.lastadi.LastaDiTestCase;

/**
 * @author shinsuke
 * 
 */
public class EsUrlQueueServiceTest extends LastaDiTestCase {
    @Resource
    private  EsUrlQueueService urlQueueService;

    @Resource
    private  FesenClient fesenClient;

    private FesenRunner runner;

    @Override
    protected String prepareConfigFile() {
        return "app.xml";
    }

    @Override
    protected boolean isUseOneTimeContainer() {
        return true;
    }

    @Override
    public void setUp() throws Exception {
        // create runner instance
        runner = new FesenRunner();
        // create ES nodes
        final String clusterName = UUID.randomUUID().toString();
        runner.onBuild((number, settingsBuilder) -> {
            settingsBuilder.put("http.cors.enabled", true);
            settingsBuilder.put("discovery.type", "single-node");
        }).build(newConfigs().clusterName(clusterName).numOfNode(1));

        // wait for yellow status
        runner.ensureYellow();

        System.setProperty(FesenClient.HTTP_ADDRESS, "localhost:" + runner.node().settings().get("http.port", "9201"));

        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        // close runner
        runner.close();
        // delete all files
        runner.clean();
    }

    public void test_insert_update_deleteTx() {
        final EsUrlQueue urlQueue = new EsUrlQueue();
        urlQueue.setCreateTime(System.currentTimeMillis());
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId("sessionId");
        urlQueue.setUrl("http://www.example.com/");

        urlQueueService.insert(urlQueue);
        assertTrue(fesenClient.prepareSearch(".crawler.queue").setTypes("queue").setQuery(QueryBuilders.termQuery("sessionId", "sessionId"))
                .setSize(0).execute().actionGet().getHits().getTotalHits().value > 0);

        urlQueueService.delete("sessionId");
        assertFalse(fesenClient.prepareSearch(".crawler.queue").setTypes("queue").setQuery(QueryBuilders.termQuery("sessionId", "sessionId"))
                .setSize(0).execute().actionGet().getHits().getTotalHits().value > 0);

    }

    public void test_insert_update_delete_multiTx() {
        final EsUrlQueue urlQueue = new EsUrlQueue();
        urlQueue.setCreateTime(System.currentTimeMillis());
        urlQueue.setDepth(1);
        urlQueue.setMethod("GET");
        urlQueue.setSessionId("id1");
        urlQueue.setUrl("http://www.id1.com/");

        urlQueueService.insert(urlQueue);

        final EsUrlQueue urlQueue2 = new EsUrlQueue();
        urlQueue2.setCreateTime(System.currentTimeMillis());
        urlQueue2.setDepth(1);
        urlQueue2.setMethod("GET");
        urlQueue2.setSessionId("id2");
        urlQueue2.setUrl("http://www.id2.com/");

        urlQueueService.insert(urlQueue2);
        assertTrue(fesenClient.prepareSearch(".crawler.queue").setTypes("queue").setQuery(QueryBuilders.termQuery("sessionId", "id1")).execute()
                .actionGet().getHits().getTotalHits().value > 0);
        assertTrue(fesenClient.prepareSearch(".crawler.queue").setTypes("queue").setQuery(QueryBuilders.termQuery("sessionId", "id2")).execute()
                .actionGet().getHits().getTotalHits().value > 0);

        urlQueueService.delete("id1");
        assertFalse(fesenClient.prepareSearch(".crawler.queue").setTypes("queue").setQuery(QueryBuilders.termQuery("sessionId", "id1")).execute()
                .actionGet().getHits().getTotalHits().value > 0);
        assertTrue(fesenClient.prepareSearch(".crawler.queue").setTypes("queue").setQuery(QueryBuilders.termQuery("sessionId", "id2")).execute()
                .actionGet().getHits().getTotalHits().value > 0);

        urlQueueService.deleteAll();
        assertFalse(fesenClient.prepareSearch(".crawler.queue").setTypes("queue").setQuery(QueryBuilders.termQuery("sessionId", "id1")).execute()
                .actionGet().getHits().getTotalHits().value > 0);
        assertFalse(fesenClient.prepareSearch(".crawler.queue").setTypes("queue").setQuery(QueryBuilders.termQuery("sessionId", "id2")).execute()
                .actionGet().getHits().getTotalHits().value > 0);
    }
}
