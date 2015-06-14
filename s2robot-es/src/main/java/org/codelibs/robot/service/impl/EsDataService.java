package org.codelibs.robot.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.robot.entity.AccessResult;
import org.codelibs.robot.entity.EsAccessResult;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.util.AccessResultCallback;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

public class EsDataService extends AbstractRobotService implements DataService {

    public int scrollTimeout = 60000;

    public int scrollSize = 100;

    @PostConstruct
    public void init() {
        esClient.addOnConnectListener(() -> {
            createMapping("data");
        });
    }

    @Override
    public void store(final AccessResult accessResult) {
        accessResult.setId(hashCodeAsLong(super.insert(accessResult, accessResult.getId() == null ? OpType.CREATE : OpType.INDEX)));
    }

    @Override
    public void update(final AccessResult accessResult) {
        accessResult.setId(hashCodeAsLong(super.insert(accessResult, OpType.INDEX)));
    }

    @Override
    public void update(final List<AccessResult> accessResultList) {
        final List<String> idList = insertAll(accessResultList, OpType.INDEX);
        for (int i = 0; i < idList.size(); i++) {
            accessResultList.get(i).setId(hashCodeAsLong(idList.get(i)));
        }
    }

    @Override
    public int getCount(final String sessionId) {
        return (int) getClient().prepareCount(index).setTypes(type).setQuery(QueryBuilders.termQuery(SESSION_ID, sessionId)).execute()
                .actionGet().getCount();
    }

    @Override
    public void delete(final String sessionId) {
        deleteBySessionId(sessionId);
    }

    @Override
    public AccessResult getAccessResult(final String sessionId, final String url) {
        return get(EsAccessResult.class, sessionId, url);
    }

    @Override
    public List<AccessResult> getAccessResultList(final String url, final boolean hasData) {
        final SearchResponse response =
                getClient().prepareSearch(index).setTypes(type).setQuery(QueryBuilders.termQuery(URL, url)).execute().actionGet();
        final SearchHits hits = response.getHits();
        final List<AccessResult> accessResultList = new ArrayList<AccessResult>();
        if (hits.getTotalHits() != 0) {
            for (final SearchHit searchHit : hits.getHits()) {
                accessResultList.add(BeanUtil.copyMapToNewBean(searchHit.getSource(), EsAccessResult.class, option -> {
                    option.converter(new EsTimestampConverter(), timestampFields).excludeWhitespace();
                }));
            }
        }
        return accessResultList;
    }

    @Override
    public void iterate(final String sessionId, final AccessResultCallback callback) {
        SearchResponse response =
                getClient().prepareSearch(index).setTypes(type)
                        .setSearchType(SearchType.SCAN).setScroll(new TimeValue(scrollTimeout)).setQuery(QueryBuilders
                                .filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.termFilter(SESSION_ID, sessionId)))
                .setSize(scrollSize).execute().actionGet();
        while (true) {
            final SearchHits searchHits = response.getHits();
            for (final SearchHit searchHit : searchHits) {
                final AccessResult accessResult = BeanUtil.copyMapToNewBean(searchHit.getSource(), EsAccessResult.class, option -> {
                    option.converter(new EsTimestampConverter(), timestampFields).excludeWhitespace();
                });
                callback.iterate(accessResult);
            }

            if (searchHits.hits().length == 0) {
                break;
            }
            response =
                    getClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(scrollTimeout)).execute().actionGet();
        }
    }
}
