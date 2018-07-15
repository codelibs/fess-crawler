/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

import javax.annotation.PostConstruct;

import org.codelibs.fess.crawler.entity.EsUrlFilter;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.service.UrlFilterService;
import org.codelibs.fess.crawler.util.EsCrawlerConfig;
import org.elasticsearch.action.DocWriteRequest.OpType;
import org.elasticsearch.index.query.QueryBuilders;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class EsUrlFilterService extends AbstractCrawlerService implements UrlFilterService {

    private static final String FILTER_TYPE = "filterType";

    private static final String INCLUDE = "include";

    private static final String EXCLUDE = "exclude";

    protected LoadingCache<String, List<Pattern>> includeFilterCache;

    protected LoadingCache<String, List<Pattern>> excludeFilterCache;

    protected int filterCacheExpireAfterWrite = 10; // 10sec

    protected int maxLoadSize = 10000;

    public EsUrlFilterService(final EsCrawlerConfig crawlerConfig) {
        this.index = crawlerConfig.getFilterIndex();
        this.type = "filter";
        setNumberOfShards(crawlerConfig.getFilterShards());
        setNumberOfReplicas(crawlerConfig.getFilterReplicas());
    }

    public EsUrlFilterService(final String name, final String type) {
        this.index = name + "." + type;
        this.type = type;
    }

    @PostConstruct
    public void init() {
        esClient.addOnConnectListener(() -> createMapping("filter"));

        includeFilterCache = createFilterCache(INCLUDE);
        excludeFilterCache = createFilterCache(EXCLUDE);
    }

    protected LoadingCache<String, List<Pattern>> createFilterCache(final String type) {
        return CacheBuilder.newBuilder()//
                .expireAfterWrite(filterCacheExpireAfterWrite, TimeUnit.SECONDS)//
                .build(new CacheLoader<String, List<Pattern>>() {

                    @Override
                    public List<Pattern> load(final String key) {
                        return getList(EsUrlFilter.class, key, QueryBuilders.termQuery(FILTER_TYPE, type), null, maxLoadSize, null).stream()
                                .map(f -> Pattern.compile(f.getUrl())).collect(Collectors.toList());
                    }
                });
    }

    @Override
    public void addIncludeUrlFilter(final String sessionId, final String url) {
        final EsUrlFilter esUrlFilter = new EsUrlFilter();
        esUrlFilter.setSessionId(sessionId);
        esUrlFilter.setFilterType(INCLUDE);
        esUrlFilter.setUrl(url);
        insert(esUrlFilter, OpType.INDEX);
        includeFilterCache.invalidate(sessionId);
    }

    @Override
    public void addIncludeUrlFilter(final String sessionId, final List<String> urlList) {
        final Set<String> invalidateSet = new HashSet<>();
        final List<EsUrlFilter> urlFilterList = new ArrayList<>(urlList.size());
        for (final String url : urlList) {
            final EsUrlFilter esUrlFilter = new EsUrlFilter();
            esUrlFilter.setSessionId(sessionId);
            esUrlFilter.setFilterType(INCLUDE);
            esUrlFilter.setUrl(url);
            urlFilterList.add(esUrlFilter);
            invalidateSet.add(sessionId);
        }
        insertAll(urlFilterList, OpType.INDEX);
        invalidateSet.forEach(s -> includeFilterCache.invalidate(s));
    }

    @Override
    public void addExcludeUrlFilter(final String sessionId, final String url) {
        final EsUrlFilter esUrlFilter = new EsUrlFilter();
        esUrlFilter.setSessionId(sessionId);
        esUrlFilter.setFilterType(EXCLUDE);
        esUrlFilter.setUrl(url);
        insert(esUrlFilter, OpType.INDEX);
        excludeFilterCache.invalidate(sessionId);
    }

    @Override
    public void addExcludeUrlFilter(final String sessionId, final List<String> urlList) {
        final Set<String> invalidateSet = new HashSet<>();
        final List<EsUrlFilter> urlFilterList = new ArrayList<>(urlList.size());
        for (final String url : urlList) {
            final EsUrlFilter esUrlFilter = new EsUrlFilter();
            esUrlFilter.setSessionId(sessionId);
            esUrlFilter.setFilterType(EXCLUDE);
            esUrlFilter.setUrl(url);
            urlFilterList.add(esUrlFilter);
        }
        insertAll(urlFilterList, OpType.INDEX);
        invalidateSet.forEach(s -> excludeFilterCache.invalidate(s));
    }

    @Override
    public void delete(final String sessionId) {
        deleteBySessionId(sessionId);
        includeFilterCache.invalidate(sessionId);
        excludeFilterCache.invalidate(sessionId);
    }

    @Override
    public List<Pattern> getIncludeUrlPatternList(final String sessionId) {
        try {
            return includeFilterCache.get(sessionId);
        } catch (final ExecutionException e) {
            throw new CrawlerSystemException(e);
        }
    }

    @Override
    public List<Pattern> getExcludeUrlPatternList(final String sessionId) {
        try {
            return excludeFilterCache.get(sessionId);
        } catch (final ExecutionException e) {
            throw new CrawlerSystemException(e);
        }
    }

    public void setFilterCacheExpireAfterWrite(final int filterCacheExpireAfterWrite) {
        this.filterCacheExpireAfterWrite = filterCacheExpireAfterWrite;
    }

    public void setMaxLoadSize(final int maxLoadSize) {
        this.maxLoadSize = maxLoadSize;
    }

}
