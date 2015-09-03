package org.codelibs.robot.service.impl;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.robot.entity.EsAccessResult;
import org.codelibs.robot.service.DataService;
import org.codelibs.robot.util.AccessResultCallback;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

public class EsDataService extends AbstractRobotService implements DataService<EsAccessResult> {

    @PostConstruct
    public void init() {
        esClient.addOnConnectListener(() -> {
            createMapping("data");
        });
    }

    @Override
    public void store(final EsAccessResult accessResult) {
        accessResult.setId(hashCodeAsLong(super.insert(accessResult, accessResult.getId() == null ? OpType.CREATE : OpType.INDEX)));
    }

    @Override
    public void update(final EsAccessResult accessResult) {
        accessResult.setId(hashCodeAsLong(super.insert(accessResult, OpType.INDEX)));
    }

    @Override
    public void update(final List<EsAccessResult> accessResultList) {
        final List<String> idList = insertAll(accessResultList, OpType.INDEX);
        for (int i = 0; i < idList.size(); i++) {
            accessResultList.get(i).setId(hashCodeAsLong(idList.get(i)));
        }
    }

    @Override
    public int getCount(final String sessionId) {
        return getCount(builder -> builder.setQuery(QueryBuilders.termQuery(SESSION_ID, sessionId)));
    }

    @Override
    public void delete(final String sessionId) {
        deleteBySessionId(sessionId);
    }

    @Override
    public EsAccessResult getAccessResult(final String sessionId, final String url) {
        return get(EsAccessResult.class, sessionId, url);
    }

    @Override
    public List<EsAccessResult> getAccessResultList(final String url, final boolean hasData) {
        return getList(EsAccessResult.class, builder -> builder.setQuery(QueryBuilders.termQuery(URL, url)));
    }

    public List<EsAccessResult> getAccessResultList(final Consumer<SearchRequestBuilder> callback) {
        return getList(EsAccessResult.class, callback);
    }

    @Override
    public void iterate(final String sessionId, final AccessResultCallback<EsAccessResult> callback) {
        SearchResponse response =
                getClient().prepareSearch(index).setTypes(type)
                        .setSearchType(SearchType.SCAN).setScroll(new TimeValue(scrollTimeout)).setQuery(QueryBuilders
                                .filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.termFilter(SESSION_ID, sessionId)))
                .setSize(scrollSize).execute().actionGet();
        while (true) {
            response =
                    getClient().prepareSearchScroll(response.getScrollId()).setScroll(new TimeValue(scrollTimeout)).execute().actionGet();

            final SearchHits searchHits = response.getHits();
            if (searchHits.hits().length == 0) {
                break;
            }

            for (final SearchHit searchHit : searchHits) {
                final EsAccessResult accessResult = BeanUtil.copyMapToNewBean(searchHit.getSource(), EsAccessResult.class, option -> {
                    option.converter(new EsTimestampConverter(), timestampFields).excludeWhitespace();
                });
                callback.iterate(accessResult);
            }
        }
    }
}
