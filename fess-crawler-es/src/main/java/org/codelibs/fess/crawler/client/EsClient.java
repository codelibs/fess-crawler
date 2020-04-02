/*
 * Copyright 2012-2020 CodeLibs Project and the Others.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.RandomUtils;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.elasticsearch.client.HttpClient;
import org.codelibs.fess.crawler.exception.EsAccessException;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ActionType;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainRequestBuilder;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequest;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesRequestBuilder;
import org.elasticsearch.action.fieldcaps.FieldCapabilitiesResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollRequestBuilder;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.termvectors.MultiTermVectorsRequest;
import org.elasticsearch.action.termvectors.MultiTermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.MultiTermVectorsResponse;
import org.elasticsearch.action.termvectors.TermVectorsRequest;
import org.elasticsearch.action.termvectors.TermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.threadpool.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EsClient implements Client {
    public static final String HTTP_ADDRESS = "crawler.es.http_address";

    public static final String TARGET_INDICES = "crawler.es.target_indices";

    private static final Logger logger = LoggerFactory.getLogger(EsClient.class);

    protected Client client;

    protected String address;

    protected List<OnConnectListener> onConnectListenerList = new ArrayList<>();

    private volatile boolean connected;

    protected Scroll scrollForDelete = new Scroll(TimeValue.timeValueMinutes(1));

    protected int sizeForDelete = 10;

    protected long retryInterval = 3 * 1000L;

    protected int maxRetryCount = 5;

    protected long connTimeout = 180 * 1000L;

    protected String searchPreference;

    protected String[] targetIndices;

    public EsClient() {
        address = System.getProperty(HTTP_ADDRESS, "localhost:9200").trim();
        final String targets = System.getProperty(TARGET_INDICES);
        if (StringUtil.isNotBlank(targets)) {
            targetIndices = Arrays.stream(targets.split(",")).map(String::trim).toArray(n -> new String[n]);
        }
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public void addOnConnectListener(final OnConnectListener listener) {
        onConnectListenerList.add(listener);
    }

    public boolean connected() {
        return connected;
    }

    public void connect() {
        destroy();
        client = createClient();

        final ClusterHealthResponse healthResponse =
                get(c -> c.admin().cluster().prepareHealth(targetIndices).setWaitForYellowStatus().execute());
        if (!healthResponse.isTimedOut()) {
            onConnectListenerList.forEach(l -> {
                try {
                    l.onConnect();
                } catch (final Exception e) {
                    logger.warn("Failed to invoke " + l, e);
                }
            });

            connected = true;
        } else {
            logger.warn("Could not connect to " + address);
        }
    }

    protected Client createClient() {
        final Settings settings = Settings.builder().putList("http.hosts", address).build();
        return new HttpClient(settings, null);
    }

    public <T> T get(final Function<EsClient, ActionFuture<T>> func) {
        int retryCount = 0;
        while (true) {
            try {
                return func.apply(this).actionGet(connTimeout, TimeUnit.MILLISECONDS);
            } catch (final IndexNotFoundException | VersionConflictEngineException e) {
                logger.debug("{} occurs.", e.getClass().getName(), e);
                throw e;
            } catch (final Exception e) {
                if (retryCount > maxRetryCount) {
                    throw e;
                }
                if (logger.isDebugEnabled()) {
                    logger.debug("Failed to invoke actionGet. count:{}", retryCount, e);
                }

                ThreadUtil.sleep(RandomUtils.nextLong(retryInterval + retryCount * 1000L, retryInterval + retryCount * 1000L * 2L));
                retryCount++;
            }
        }
    }

    @PreDestroy
    public void destroy() {
        if (client != null) {
            try {
                client.close();
            } catch (final ElasticsearchException e) {
                logger.warn("Failed to close client.", e);
            }
            logger.info("Disconnected to " + address);
        }
        connected = false;
    }

    @Override
    public ThreadPool threadPool() {
        return client.threadPool();
    }

    @Override
    public AdminClient admin() {
        return client.admin();
    }

    @Override
    public ActionFuture<IndexResponse> index(final IndexRequest request) {
        return client.index(request);
    }

    @Override
    public void index(final IndexRequest request, final ActionListener<IndexResponse> listener) {
        client.index(request, listener);
    }

    @Override
    public IndexRequestBuilder prepareIndex() {
        return client.prepareIndex();
    }

    @Override
    public ActionFuture<UpdateResponse> update(final UpdateRequest request) {
        return client.update(request);
    }

    @Override
    public void update(final UpdateRequest request, final ActionListener<UpdateResponse> listener) {
        client.update(request, listener);
    }

    @Override
    public UpdateRequestBuilder prepareUpdate() {
        return client.prepareUpdate();
    }

    @Override
    public UpdateRequestBuilder prepareUpdate(final String index, final String type, final String id) {
        return client.prepareUpdate(index, type, id);
    }

    @Override
    public IndexRequestBuilder prepareIndex(final String index, final String type) {
        return client.prepareIndex(index, type);
    }

    @Override
    public IndexRequestBuilder prepareIndex(final String index, final String type, final String id) {
        return client.prepareIndex(index, type, id);
    }

    @Override
    public ActionFuture<DeleteResponse> delete(final DeleteRequest request) {
        return client.delete(request);
    }

    @Override
    public void delete(final DeleteRequest request, final ActionListener<DeleteResponse> listener) {
        client.delete(request, listener);
    }

    @Override
    public DeleteRequestBuilder prepareDelete() {
        return client.prepareDelete();
    }

    @Override
    public DeleteRequestBuilder prepareDelete(final String index, final String type, final String id) {
        return client.prepareDelete(index, type, id);
    }

    @Override
    public ActionFuture<BulkResponse> bulk(final BulkRequest request) {
        return client.bulk(request);
    }

    @Override
    public void bulk(final BulkRequest request, final ActionListener<BulkResponse> listener) {
        client.bulk(request, listener);
    }

    @Override
    public BulkRequestBuilder prepareBulk() {
        return client.prepareBulk();
    }

    @Override
    public ActionFuture<GetResponse> get(final GetRequest request) {
        return client.get(request);
    }

    @Override
    public void get(final GetRequest request, final ActionListener<GetResponse> listener) {
        client.get(request, listener);
    }

    @Override
    public GetRequestBuilder prepareGet() {
        return client.prepareGet();
    }

    @Override
    public GetRequestBuilder prepareGet(final String index, final String type, final String id) {
        return client.prepareGet(index, type, id);
    }

    @Override
    public ActionFuture<MultiGetResponse> multiGet(final MultiGetRequest request) {
        return client.multiGet(request);
    }

    @Override
    public void multiGet(final MultiGetRequest request, final ActionListener<MultiGetResponse> listener) {
        client.multiGet(request, listener);
    }

    @Override
    public MultiGetRequestBuilder prepareMultiGet() {
        return client.prepareMultiGet();
    }

    @Override
    public ActionFuture<SearchResponse> search(final SearchRequest request) {
        return client.search(request);
    }

    @Override
    public void search(final SearchRequest request, final ActionListener<SearchResponse> listener) {
        client.search(request, listener);
    }

    @Override
    public SearchRequestBuilder prepareSearch(final String... indices) {
        final SearchRequestBuilder builder = client.prepareSearch(indices);
        if (searchPreference != null) {
            builder.setPreference(searchPreference);
        }
        return builder;
    }

    @Override
    public ActionFuture<SearchResponse> searchScroll(final SearchScrollRequest request) {
        return client.searchScroll(request);
    }

    @Override
    public void searchScroll(final SearchScrollRequest request, final ActionListener<SearchResponse> listener) {
        client.searchScroll(request, listener);
    }

    @Override
    public SearchScrollRequestBuilder prepareSearchScroll(final String scrollId) {
        return client.prepareSearchScroll(scrollId);
    }

    @Override
    public ActionFuture<MultiSearchResponse> multiSearch(final MultiSearchRequest request) {
        return client.multiSearch(request);
    }

    @Override
    public void multiSearch(final MultiSearchRequest request, final ActionListener<MultiSearchResponse> listener) {
        client.multiSearch(request, listener);
    }

    @Override
    public MultiSearchRequestBuilder prepareMultiSearch() {
        return client.prepareMultiSearch();
    }

    @Override
    public ExplainRequestBuilder prepareExplain(final String index, final String type, final String id) {
        return client.prepareExplain(index, type, id);
    }

    @Override
    public ActionFuture<ExplainResponse> explain(final ExplainRequest request) {
        return client.explain(request);
    }

    @Override
    public void explain(final ExplainRequest request, final ActionListener<ExplainResponse> listener) {
        client.explain(request, listener);
    }

    @Override
    public ClearScrollRequestBuilder prepareClearScroll() {
        return client.prepareClearScroll();
    }

    @Override
    public ActionFuture<ClearScrollResponse> clearScroll(final ClearScrollRequest request) {
        return client.clearScroll(request);
    }

    @Override
    public void clearScroll(final ClearScrollRequest request, final ActionListener<ClearScrollResponse> listener) {
        client.clearScroll(request, listener);
    }

    @Override
    public Settings settings() {
        return client.settings();
    }

    @Override
    public void close() {
        client.close();
    }

    @Override
    public ActionFuture<TermVectorsResponse> termVectors(final TermVectorsRequest request) {
        return client.termVectors(request);
    }

    @Override
    public void termVectors(final TermVectorsRequest request, final ActionListener<TermVectorsResponse> listener) {
        client.termVectors(request, listener);
    }

    @Override
    public TermVectorsRequestBuilder prepareTermVectors() {
        return client.prepareTermVectors();
    }

    @Override
    public TermVectorsRequestBuilder prepareTermVectors(final String index, final String type, final String id) {
        return client.prepareTermVectors(index, type, id);
    }

    @Override
    public ActionFuture<MultiTermVectorsResponse> multiTermVectors(final MultiTermVectorsRequest request) {
        return client.multiTermVectors(request);
    }

    @Override
    public void multiTermVectors(final MultiTermVectorsRequest request, final ActionListener<MultiTermVectorsResponse> listener) {
        client.multiTermVectors(request, listener);
    }

    @Override
    public MultiTermVectorsRequestBuilder prepareMultiTermVectors() {
        return client.prepareMultiTermVectors();
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> ActionFuture<Response> execute(final ActionType<Response> action,
            final Request request) {
        return client.execute(action, request);
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> void execute(final ActionType<Response> action, final Request request,
            final ActionListener<Response> listener) {
        client.execute(action, request, listener);
    }

    @Override
    public Client filterWithHeader(final Map<String, String> headers) {
        client.filterWithHeader(headers);
        return this;
    }

    public int deleteByQuery(final String index, final String type, final QueryBuilder queryBuilder) {
        SearchResponse response =
                get(c -> c.prepareSearch(index).setScroll(scrollForDelete).setSize(sizeForDelete).setQuery(queryBuilder).execute());
        String scrollId = response.getScrollId();
        int count = 0;
        try {
            while (scrollId != null) {
                final SearchHit[] hits = response.getHits().getHits();
                if (hits.length == 0) {
                    break;
                }

                count += hits.length;
                final BulkResponse bulkResponse = get(c -> {
                    final BulkRequestBuilder bulkRequest = client.prepareBulk();
                    for (final SearchHit hit : hits) {
                        bulkRequest.add(client.prepareDelete().setIndex(hit.getIndex()).setId(hit.getId()));
                    }
                    return bulkRequest.execute();
                });
                if (bulkResponse.hasFailures()) {
                    throw new EsAccessException(bulkResponse.buildFailureMessage());
                }

                final String sid = scrollId;
                response = get(c -> c.prepareSearchScroll(sid).setScroll(scrollForDelete).execute());
                if (!scrollId.equals(response.getScrollId())) {
                    clearScroll(scrollId);
                }
                scrollId = response.getScrollId();
            }
        } finally {
            clearScroll(scrollId);
        }
        return count;
    }

    public void clearScroll(final String scrollId) {
        if (scrollId != null) {
            prepareClearScroll().addScrollId(scrollId)
                    .execute(ActionListener.wrap(res -> {}, e -> logger.warn("Failed to clear " + scrollId, e)));
        }
    }

    public interface OnConnectListener {
        void onConnect();
    }

    public void setScrollForDelete(final Scroll scrollForDelete) {
        this.scrollForDelete = scrollForDelete;
    }

    public void setSizeForDelete(final int sizeForDelete) {
        this.sizeForDelete = sizeForDelete;
    }

    public void setRetryInterval(final long retryInterval) {
        this.retryInterval = retryInterval;
    }

    public void setMaxRetryCount(final int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public void setConnTimeout(final long connTimeout) {
        this.connTimeout = connTimeout;
    }

    public void setSearchPreference(final String searchPreference) {
        this.searchPreference = searchPreference;
    }

    @Override
    public FieldCapabilitiesRequestBuilder prepareFieldCaps(final String... indices) {
        return client.prepareFieldCaps(indices);
    }

    @Override
    public ActionFuture<FieldCapabilitiesResponse> fieldCaps(final FieldCapabilitiesRequest request) {
        return client.fieldCaps(request);
    }

    @Override
    public void fieldCaps(final FieldCapabilitiesRequest request, final ActionListener<FieldCapabilitiesResponse> listener) {
        client.fieldCaps(request, listener);
    }

    @Override
    public BulkRequestBuilder prepareBulk(final String globalIndex, final String globalType) {
        return client.prepareBulk(globalIndex, globalType);
    }
}
