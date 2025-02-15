/*
 * Copyright 2012-2024 CodeLibs Project and the Others.
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.fess.crawler.container.CrawlerContainer;

import jakarta.annotation.Resource;

/**
 * Creates and manages crawler clients for web crawling operations.
 * This class handles the registration and loading of crawler client factories and their associated clients.
 * <p>
 * The class maintains a mapping between regular expressions and component names, and manages a list
 * of crawler client factories with a configurable maximum size.
 * </p>
 *
 */
public class CrawlerClientCreator {

    private static final Logger logger = LogManager.getLogger(CrawlerClientCreator.class);

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

    /**
     * Loads a crawler client into the specified crawler client factory.
     *
     * @param crawlerClientFactory the factory to which the client will be added
     * @param regex the regular expression used to match URLs for this client
     * @param componentName the name of the component to be loaded as a client
     */
    protected void load(final CrawlerClientFactory crawlerClientFactory, final String regex, final String componentName) {
        if (logger.isDebugEnabled()) {
            logger.debug("loading {}", componentName);
        }
        try (CrawlerClient client = crawlerContainer.getComponent(componentName)) {
            crawlerClientFactory.addClient(regex, client);
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to create {}.", componentName, e);
            } else {
                logger.info("{} is not available.", componentName);
            }
        }
    }

    public void setMaxClientFactorySize(final int maxClientFactorySize) {
        if (maxClientFactorySize <= 0) {
            throw new IllegalArgumentException("maxClientFactorySize must be positive.");
        }
        this.maxClientFactorySize = maxClientFactorySize;
    }
}
