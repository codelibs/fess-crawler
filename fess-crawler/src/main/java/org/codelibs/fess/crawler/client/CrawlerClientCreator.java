/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
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
    /**
     * Constructs a new CrawlerClientCreator.
     */
    public CrawlerClientCreator() {
        // Default constructor
    }

    /** Logger instance for this class */
    private static final Logger logger = LogManager.getLogger(CrawlerClientCreator.class);

    /** Container for managing crawler components */
    @Resource
    protected CrawlerContainer crawlerContainer;

    /** Map of regular expressions to component names */
    protected Map<String, String> clientMap = new LinkedHashMap<>();

    /** List of registered crawler client factories */
    protected List<CrawlerClientFactory> clientFactoryList = new LinkedList<>();

    /**
     * The maximum size of the client factory list.
     */
    protected int maxClientFactorySize = 10000;

    /**
     * Registers a CrawlerClientFactory with this creator.
     * All existing client mappings will be loaded into the new factory.
     * @param crawlerClientFactory The CrawlerClientFactory to register.
     */
    public synchronized void register(final CrawlerClientFactory crawlerClientFactory) {
        clientMap.entrySet().stream().forEach(e -> load(crawlerClientFactory, e.getKey(), e.getValue()));
        clientFactoryList.add(crawlerClientFactory);
        if (clientFactoryList.size() > maxClientFactorySize) {
            clientFactoryList.remove(0);
        }
    }

    /**
     * Registers a client component with a regular expression.
     * The component will be loaded into all registered CrawlerClientFactories.
     * @param regex The regular expression to match URLs.
     * @param componentName The name of the component to register.
     */
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

    /**
     * Sets the maximum client factory size.
     * @param maxClientFactorySize The maximum size.
     */
    public void setMaxClientFactorySize(final int maxClientFactorySize) {
        if (maxClientFactorySize <= 0) {
            throw new IllegalArgumentException("maxClientFactorySize must be positive.");
        }
        this.maxClientFactorySize = maxClientFactorySize;
    }
}
