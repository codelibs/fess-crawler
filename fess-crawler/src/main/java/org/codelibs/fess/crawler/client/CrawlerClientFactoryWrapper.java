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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A wrapper class for CrawlerClientFactory that delegates operations to an underlying factory instance.
 * This wrapper allows for parameter management and initialization before delegating to the wrapped factory.
 *
 * The wrapper maintains its own parameter map which can be initialized separately from the wrapped factory,
 * and provides access to the wrapped factory instance through getParent().
 *
 * All crawler client management operations (adding, getting clients, setting client map) are delegated
 * to the wrapped factory instance.
 *
 * This class implements the decorator pattern to extend CrawlerClientFactory functionality while
 * maintaining the same interface.
 *
 */
public class CrawlerClientFactoryWrapper extends CrawlerClientFactory {
    /**
     * The underlying CrawlerClientFactory instance to which operations are delegated.
     */
    private final CrawlerClientFactory factory;

    private final Map<String, Object> params = new HashMap<>();

    /**
     * Constructs a CrawlerClientFactoryWrapper with the specified factory.
     *
     * @param factory the underlying CrawlerClientFactory instance to delegate operations to
     */
    public CrawlerClientFactoryWrapper(final CrawlerClientFactory factory) {
        this.factory = factory;
    }

    /**
     * Returns the parent CrawlerClientFactory.
     * @return The parent CrawlerClientFactory.
     */
    public CrawlerClientFactory getParent() {
        return factory;
    }

    /**
     * Sets the initialization parameter map for this wrapper.
     * These parameters will be applied to the wrapped factory upon calling initParameterMap().
     * @param params The map of parameters.
     */
    @Override
    public void setInitParameterMap(final Map<String, Object> params) {
        this.params.putAll(params);
    }

    /**
     * Initializes the parameter map of the wrapped factory with the parameters stored in this wrapper.
     */
    public void initParameterMap() {
        factory.setInitParameterMap(params);
    }

    /**
     * Initializes the wrapped factory.
     */
    @Override
    public void init() {
        factory.init();
    }

    /**
     * Adds a client to the wrapped factory.
     * @param regex The regular expression for the client.
     * @param client The CrawlerClient instance.
     */
    @Override
    public void addClient(final String regex, final CrawlerClient client) {
        factory.addClient(regex, client);
    }

    /**
     * Adds a client to the wrapped factory at a specific position.
     * @param regex The regular expression for the client.
     * @param client The CrawlerClient instance.
     * @param pos The position to add the client.
     */
    @Override
    public void addClient(final String regex, final CrawlerClient client, final int pos) {
        factory.addClient(regex, client, pos);
    }

    /**
     * Returns the hash code of the wrapped factory.
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return factory.hashCode();
    }

    /**
     * Adds a list of clients to the wrapped factory.
     * @param regexList The list of regular expressions for the clients.
     * @param client The CrawlerClient instance.
     */
    @Override
    public void addClient(final List<String> regexList, final CrawlerClient client) {
        factory.addClient(regexList, client);
    }

    /**
     * Retrieves a client from the wrapped factory that matches the given URL.
     * @param url The URL to match.
     * @return The matching CrawlerClient instance.
     */
    @Override
    public CrawlerClient getClient(final String url) {
        return factory.getClient(url);
    }

    /**
     * Sets the client map for the wrapped factory.
     * @param clientMap The map of clients.
     */
    @Override
    public void setClientMap(final Map<Pattern, CrawlerClient> clientMap) {
        factory.setClientMap(clientMap);
    }

    /**
     * Compares this wrapper to another object for equality.
     * Equality is determined by the wrapped factory.
     * @param obj The object to compare.
     * @return true if equal, false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        return factory.equals(obj);
    }

    /**
     * Returns a string representation of this wrapper.
     * @return A string representation.
     */
    @Override
    public String toString() {
        return "CrawlerClientFactoryWrapper{" + "factory=" + factory + '}';
    }
}
