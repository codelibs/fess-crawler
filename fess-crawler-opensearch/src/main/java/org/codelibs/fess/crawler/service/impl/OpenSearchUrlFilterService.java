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
package org.codelibs.fess.crawler.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.codelibs.fess.crawler.entity.OpenSearchUrlFilter;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.service.UrlFilterService;
import org.codelibs.fess.crawler.util.OpenSearchCrawlerConfig;
import org.opensearch.action.DocWriteRequest.OpType;
import org.opensearch.index.query.QueryBuilders;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import jakarta.annotation.PostConstruct;

/**
 * OpenSearchUrlFilterService is an implementation of {@link UrlFilterService} for OpenSearch.
 */
public class OpenSearchUrlFilterService extends AbstractCrawlerService implements UrlFilterService {

    /**
     * Field name for filter type.
     */
    private static final String FILTER_TYPE = "filterType";

    /**
     * Filter type for include filters.
     */
    private static final String INCLUDE = "include";

    /**
     * Filter type for exclude filters.
     */
    private static final String EXCLUDE = "exclude";

    /**
     * Cache for include filters.
     */
    protected LoadingCache<String, List<Pattern>> includeFilterCache;

    /**
     * Cache for exclude filters.
     */
    protected LoadingCache<String, List<Pattern>> excludeFilterCache;

    /**
     * The expiration time for the filter cache in seconds.
     */
    protected int filterCacheExpireAfterWrite = 10; // 10sec

    /**
     * The maximum number of items to load into the cache.
     */
    protected int maxLoadSize = 10000;

    /**
     * Creates a new instance of OpenSearchUrlFilterService.
     * @param crawlerConfig The crawler configuration.
     */
    public OpenSearchUrlFilterService(final OpenSearchCrawlerConfig crawlerConfig) {
        index = crawlerConfig.getFilterIndex();
        setNumberOfShards(crawlerConfig.getFilterShards());
        setNumberOfReplicas(crawlerConfig.getFilterReplicas());
    }

    /**
     * Creates a new instance of OpenSearchUrlFilterService.
     * @param name The name.
     * @param type The type.
     */
    public OpenSearchUrlFilterService(final String name, final String type) {
        index = name + "." + type;
    }

    /**
     * Initializes the service.
     */
    @PostConstruct
    public void init() {
        fesenClient.addOnConnectListener(() -> createMapping("filter"));

        includeFilterCache = createFilterCache(INCLUDE);
        excludeFilterCache = createFilterCache(EXCLUDE);
    }

    /**
     * Creates a filter cache.
     * @param type The filter type.
     * @return The created filter cache.
     */
    protected LoadingCache<String, List<Pattern>> createFilterCache(final String type) {
        return CacheBuilder.newBuilder()//
                .expireAfterWrite(filterCacheExpireAfterWrite, TimeUnit.SECONDS)//
                .build(new CacheLoader<String, List<Pattern>>() {

                    /**
                     * Loads filter patterns for the given session ID.
                     *
                     * @param key The session ID.
                     * @return The list of compiled patterns.
                     */
                    @Override
                    public List<Pattern> load(final String key) {
                        return getList(OpenSearchUrlFilter.class, key, QueryBuilders.termQuery(FILTER_TYPE, type), null, maxLoadSize, null)
                                .stream().map(f -> Pattern.compile(f.getUrl())).collect(Collectors.toList());
                    }
                });
    }

    /**
     * Adds an include URL filter for the specified session.
     *
     * @param sessionId The session ID.
     * @param url The URL pattern to include.
     */
    @Override
    public void addIncludeUrlFilter(final String sessionId, final String url) {
        final OpenSearchUrlFilter esUrlFilter = new OpenSearchUrlFilter();
        esUrlFilter.setSessionId(sessionId);
        esUrlFilter.setFilterType(INCLUDE);
        esUrlFilter.setUrl(url);
        insert(esUrlFilter, OpType.INDEX);
        includeFilterCache.invalidate(sessionId);
    }

    /**
     * Adds multiple include URL filters for the specified session.
     *
     * @param sessionId The session ID.
     * @param urlList The list of URL patterns to include.
     */
    @Override
    public void addIncludeUrlFilter(final String sessionId, final List<String> urlList) {
        final Set<String> invalidateSet = new HashSet<>();
        final List<OpenSearchUrlFilter> urlFilterList = new ArrayList<>(urlList.size());
        for (final String url : urlList) {
            final OpenSearchUrlFilter esUrlFilter = new OpenSearchUrlFilter();
            esUrlFilter.setSessionId(sessionId);
            esUrlFilter.setFilterType(INCLUDE);
            esUrlFilter.setUrl(url);
            urlFilterList.add(esUrlFilter);
            invalidateSet.add(sessionId);
        }
        insertAll(urlFilterList, OpType.INDEX);
        invalidateSet.forEach(s -> includeFilterCache.invalidate(s));
    }

    /**
     * Adds an exclude URL filter for the specified session.
     *
     * @param sessionId The session ID.
     * @param url The URL pattern to exclude.
     */
    @Override
    public void addExcludeUrlFilter(final String sessionId, final String url) {
        final OpenSearchUrlFilter esUrlFilter = new OpenSearchUrlFilter();
        esUrlFilter.setSessionId(sessionId);
        esUrlFilter.setFilterType(EXCLUDE);
        esUrlFilter.setUrl(url);
        insert(esUrlFilter, OpType.INDEX);
        excludeFilterCache.invalidate(sessionId);
    }

    /**
     * Adds multiple exclude URL filters for the specified session.
     *
     * @param sessionId The session ID.
     * @param urlList The list of URL patterns to exclude.
     */
    @Override
    public void addExcludeUrlFilter(final String sessionId, final List<String> urlList) {
        final Set<String> invalidateSet = new HashSet<>();
        final List<OpenSearchUrlFilter> urlFilterList = new ArrayList<>(urlList.size());
        for (final String url : urlList) {
            final OpenSearchUrlFilter esUrlFilter = new OpenSearchUrlFilter();
            esUrlFilter.setSessionId(sessionId);
            esUrlFilter.setFilterType(EXCLUDE);
            esUrlFilter.setUrl(url);
            urlFilterList.add(esUrlFilter);
        }
        insertAll(urlFilterList, OpType.INDEX);
        invalidateSet.forEach(s -> excludeFilterCache.invalidate(s));
    }

    /**
     * Deletes all URL filters for the specified session.
     *
     * @param sessionId The session ID.
     */
    @Override
    public void delete(final String sessionId) {
        deleteBySessionId(sessionId);
        includeFilterCache.invalidate(sessionId);
        excludeFilterCache.invalidate(sessionId);
    }

    /**
     * Gets the list of include URL patterns for the specified session.
     *
     * @param sessionId The session ID.
     * @return The list of compiled include patterns.
     * @throws CrawlerSystemException if the patterns cannot be loaded.
     */
    @Override
    public List<Pattern> getIncludeUrlPatternList(final String sessionId) {
        try {
            return includeFilterCache.get(sessionId);
        } catch (final ExecutionException e) {
            throw new CrawlerSystemException(e);
        }
    }

    /**
     * Gets the list of exclude URL patterns for the specified session.
     *
     * @param sessionId The session ID.
     * @return The list of compiled exclude patterns.
     * @throws CrawlerSystemException if the patterns cannot be loaded.
     */
    @Override
    public List<Pattern> getExcludeUrlPatternList(final String sessionId) {
        try {
            return excludeFilterCache.get(sessionId);
        } catch (final ExecutionException e) {
            throw new CrawlerSystemException(e);
        }
    }

    /**
     * Sets the filter cache expiration time.
     * @param filterCacheExpireAfterWrite The expiration time in seconds.
     */
    public void setFilterCacheExpireAfterWrite(final int filterCacheExpireAfterWrite) {
        this.filterCacheExpireAfterWrite = filterCacheExpireAfterWrite;
    }

    /**
     * Sets the maximum load size for the cache.
     * @param maxLoadSize The maximum load size.
     */
    public void setMaxLoadSize(final int maxLoadSize) {
        this.maxLoadSize = maxLoadSize;
    }

}
