package org.codelibs.fess.crawler.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.codelibs.fess.crawler.entity.EsUrlFilter;
import org.codelibs.fess.crawler.service.UrlFilterService;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.index.query.QueryBuilders;

public class EsUrlFilterService extends AbstractCrawlerService implements UrlFilterService {

    private static final String FILTER_TYPE = "filterType";

    private static final String INCLUDE = "include";

    private static final String EXCLUDE = "exclude";

    @PostConstruct
    public void init() {
        esClient.addOnConnectListener(() -> {
            createMapping("filter");
        });
    }

    @Override
    public void addIncludeUrlFilter(final String sessionId, final String url) {
        final EsUrlFilter esUrlFilter = new EsUrlFilter();
        esUrlFilter.setSessionId(sessionId);
        esUrlFilter.setFilterType(INCLUDE);
        esUrlFilter.setUrl(url);
        insert(esUrlFilter, OpType.CREATE);
    }

    @Override
    public void addIncludeUrlFilter(final String sessionId, final List<String> urlList) {
        final List<EsUrlFilter> urlFilterList = new ArrayList<EsUrlFilter>(urlList.size());
        for (final String url : urlList) {
            final EsUrlFilter esUrlFilter = new EsUrlFilter();
            esUrlFilter.setSessionId(sessionId);
            esUrlFilter.setFilterType(INCLUDE);
            esUrlFilter.setUrl(url);
            urlFilterList.add(esUrlFilter);
        }
        insertAll(urlFilterList, OpType.CREATE);
    }

    @Override
    public void addExcludeUrlFilter(final String sessionId, final String url) {
        final EsUrlFilter esUrlFilter = new EsUrlFilter();
        esUrlFilter.setSessionId(sessionId);
        esUrlFilter.setFilterType(EXCLUDE);
        esUrlFilter.setUrl(url);
        insert(esUrlFilter, OpType.CREATE);
    }

    @Override
    public void addExcludeUrlFilter(final String sessionId, final List<String> urlList) {
        final List<EsUrlFilter> urlFilterList = new ArrayList<EsUrlFilter>(urlList.size());
        for (final String url : urlList) {
            final EsUrlFilter esUrlFilter = new EsUrlFilter();
            esUrlFilter.setSessionId(sessionId);
            esUrlFilter.setFilterType(EXCLUDE);
            esUrlFilter.setUrl(url);
            urlFilterList.add(esUrlFilter);
        }
        insertAll(urlFilterList, OpType.CREATE);
    }

    @Override
    public void delete(final String sessionId) {
        deleteBySessionId(sessionId);
    }

    @Override
    public List<Pattern> getIncludeUrlPatternList(final String sessionId) {
        // TODO cache
        final List<EsUrlFilter> urlFilterList =
                getList(EsUrlFilter.class, sessionId, QueryBuilders.termQuery(FILTER_TYPE, INCLUDE), null, null, null);
        final List<Pattern> urlPatternList = new ArrayList<Pattern>();
        for (final EsUrlFilter esUrlFilter : urlFilterList) {
            urlPatternList.add(Pattern.compile(esUrlFilter.getUrl()));
        }
        return urlPatternList;
    }

    @Override
    public List<Pattern> getExcludeUrlPatternList(final String sessionId) {
        // TODO cache
        final List<EsUrlFilter> urlFilterList =
                getList(EsUrlFilter.class, sessionId, QueryBuilders.termQuery(FILTER_TYPE, EXCLUDE), null, null, null);
        final List<Pattern> urlPatternList = new ArrayList<Pattern>();
        for (final EsUrlFilter esUrlFilter : urlFilterList) {
            urlPatternList.add(Pattern.compile(esUrlFilter.getUrl()));
        }
        return urlPatternList;
    }

}
