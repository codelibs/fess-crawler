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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CrawlerClientFactoryWrapper extends CrawlerClientFactory {
    private final CrawlerClientFactory factory;

    private final Map<String, Object> params = new HashMap<>();

    public CrawlerClientFactoryWrapper(final CrawlerClientFactory factory) {
        this.factory = factory;
    }

    public CrawlerClientFactory getParent() {
        return factory;
    }

    @Override
    public void setInitParameterMap(final Map<String, Object> params) {
        this.params.putAll(params);
    }

    public void initParameterMap() {
        factory.setInitParameterMap(params);
    }

    @Override
    public void init() {
        factory.init();
    }

    @Override
    public void addClient(final String regex, final CrawlerClient client) {
        factory.addClient(regex, client);
    }

    @Override
    public void addClient(final String regex, final CrawlerClient client, final int pos) {
        factory.addClient(regex, client, pos);
    }

    @Override
    public int hashCode() {
        return factory.hashCode();
    }

    @Override
    public void addClient(final List<String> regexList, final CrawlerClient client) {
        factory.addClient(regexList, client);
    }

    @Override
    public CrawlerClient getClient(final String url) {
        return factory.getClient(url);
    }

    @Override
    public void setClientMap(final Map<Pattern, CrawlerClient> clientMap) {
        factory.setClientMap(clientMap);
    }

    @Override
    public boolean equals(final Object obj) {
        return factory.equals(obj);
    }

    @Override
    public String toString() {
        return factory.toString();
    }
}
