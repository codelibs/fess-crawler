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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

/**
 * A factory class for managing and creating crawler clients based on URL patterns.
 * This class implements AutoCloseable to properly handle resource cleanup.
 *
 * <p>The factory maintains a map of regular expression patterns to crawler clients,
 * allowing for URL-based client selection. Clients can be added with specific patterns
 * and optionally at specific positions in the processing order.</p>
 *
 * <p>This factory is typically initialized through dependency injection and can be
 * configured with initialization parameters that are passed to all registered clients.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Pattern-based client mapping</li>
 *   <li>Ordered client registration</li>
 *   <li>Bulk client registration</li>
 *   <li>Automatic client initialization</li>
 *   <li>Resource cleanup management</li>
 * </ul>
 *
 */
public class CrawlerClientFactory implements AutoCloseable {
    private static final Logger logger = LogManager.getLogger(CrawlerClientFactory.class);

    @Resource
    protected CrawlerContainer crawlerContainer;

    protected Map<Pattern, CrawlerClient> clientMap = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        try {
            final CrawlerClientCreator creator = crawlerContainer.getComponent("crawlerClientCreator");
            if (creator != null) {
                creator.register(this);
            }
        } catch (final Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("CrawlerClientCreator is unavailable.", e);
            }
        }
    }

    public void addClient(final String regex, final CrawlerClient client) {
        if (StringUtil.isBlank(regex)) {
            throw new CrawlerSystemException("A regular expression is null.");
        }
        if (client == null) {
            throw new CrawlerSystemException("CrawlerClient is null.");
        }
        clientMap.put(Pattern.compile(regex), client);
    }

    public void addClient(final String regex, final CrawlerClient client, final int pos) {
        if (StringUtil.isBlank(regex)) {
            throw new CrawlerSystemException("A regular expression is null.");
        }
        if (client == null) {
            throw new CrawlerSystemException("CrawlerClient is null.");
        }
        int current = 0;
        boolean added = false;
        final Map<Pattern, CrawlerClient> newClientMap = new LinkedHashMap<>();
        for (final Map.Entry<Pattern, CrawlerClient> entry : clientMap.entrySet()) {
            if (pos == current) {
                newClientMap.put(Pattern.compile(regex), client);
                added = true;
            }
            newClientMap.put(entry.getKey(), entry.getValue());
            current++;
        }
        if (!added) {
            newClientMap.put(Pattern.compile(regex), client);
        }
        clientMap = newClientMap;
    }

    public void addClient(final List<String> regexList, final CrawlerClient client) {
        if (regexList == null || regexList.isEmpty()) {
            throw new CrawlerSystemException("A regular expression list is null or empty.");
        }
        if (client == null) {
            throw new CrawlerSystemException("CrawlerClient is null.");
        }
        for (final String regex : regexList) {
            if (StringUtil.isNotBlank(regex)) {
                clientMap.put(Pattern.compile(regex), client);
            }
        }
    }

    public CrawlerClient getClient(final String urlKey) {
        if (StringUtil.isBlank(urlKey)) {
            return null;
        }

        for (final Map.Entry<Pattern, CrawlerClient> entry : clientMap.entrySet()) {
            final Matcher matcher = entry.getKey().matcher(urlKey);
            if (matcher.matches()) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void setInitParameterMap(final Map<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            for (final CrawlerClient client : clientMap.values()) {
                client.setInitParameterMap(params);
            }
        }
    }

    public void setClientMap(final Map<Pattern, CrawlerClient> clientMap) {
        this.clientMap = clientMap;
    }

    @Override
    public void close() {
        clientMap.values().stream().distinct().forEach(client -> {
            try {
                client.close();
            } catch (final Exception e) {
                logger.warn("Failed to close {}.", client.getClass().getCanonicalName(), e);
            }
        });
    }
}
