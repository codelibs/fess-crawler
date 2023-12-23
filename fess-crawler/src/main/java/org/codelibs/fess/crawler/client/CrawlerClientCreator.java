/*
 * Copyright 2012-2023 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Resource;

public class CrawlerClientCreator {

    private static final Logger logger = LoggerFactory.getLogger(CrawlerClientCreator.class);

    @Resource
    protected CrawlerContainer crawlerContainer;

    protected Map<String, String> clientMap = new LinkedHashMap<>();

    protected List<CrawlerClientFactory> clientFactoryList = new LinkedList<>();

    protected int maxClientFactorySize = 10000;

    public synchronized void register(final CrawlerClientFactory crawlerClientFactory) {
        clientMap.entrySet().stream().forEach(e -> load(crawlerClientFactory, e.getKey(), e.getValue()));
        clientFactoryList.add(crawlerClientFactory);
        if (clientFactoryList.size() > maxClientFactorySize) {
            clientFactoryList.remove(0);
        }
    }

    public synchronized void register(final String regex, final String componentName) {
        clientMap.put(regex, componentName);
        clientFactoryList.forEach(f -> load(f, regex, componentName));
    }

    protected void load(final CrawlerClientFactory crawlerClientFactory, final String regex, final String componentName) {
        if (logger.isDebugEnabled()) {
            logger.debug("loading {}", componentName);
        }
        CrawlerClient client = null;
        try {
            client = crawlerContainer.getComponent(componentName);
            crawlerClientFactory.addClient(regex, client);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to create {}.", componentName, e);
            } else {
                logger.info("{} is not available.", componentName);
            }
            if (client != null) {
                try {
                    client.close();
                } catch (final Exception ex) {
                    logger.warn("Failed to close {}.", componentName, ex);
                }
            }
        }
    }

    public void setMaxClientFactorySize(final int maxClientFactorySize) {
        this.maxClientFactorySize = maxClientFactorySize;
    }
}
