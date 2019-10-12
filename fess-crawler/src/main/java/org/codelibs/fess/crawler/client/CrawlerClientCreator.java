/*
 * Copyright 2012-2019 CodeLibs Project and the Others.
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
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerClientCreator {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerClientCreator.class);

    @Resource
    protected CrawlerContainer crawlerContainer;

    protected Map<String, String> clientMap = new LinkedHashMap<>();

    public void initialize(CrawlerClientFactory crawlerClientFactory) {
        clientMap.entrySet().stream().forEach(e -> {
            final String name = e.getKey();
            if (logger.isDebugEnabled()) {
                logger.debug("loading {}", name);
            }
            final CrawlerClient client = crawlerContainer.getComponent(e.getValue());
            crawlerClientFactory.addClient(name, client);
        });
    }

    public void register(final String name, final String componentName) {
        clientMap.put(name, componentName);
    }
}
