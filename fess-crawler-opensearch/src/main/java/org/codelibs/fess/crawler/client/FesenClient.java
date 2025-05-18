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

import static org.codelibs.core.stream.StreamUtil.split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fesen.client.HttpClient;
import org.codelibs.fess.crawler.exception.OpenSearchAccessException;
import org.opensearch.OpenSearchException;
import org.opensearch.action.ActionRequest;
import org.opensearch.action.ActionType;
import org.opensearch.action.admin.cluster.health.ClusterHealthResponse;
import org.opensearch.action.admin.indices.segments.IndicesSegmentResponse;
import org.opensearch.action.admin.indices.segments.PitSegmentsRequest;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkRequestBuilder;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteRequestBuilder;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.explain.ExplainRequest;
import org.opensearch.action.explain.ExplainRequestBuilder;
import org.opensearch.action.explain.ExplainResponse;
import org.opensearch.action.fieldcaps.FieldCapabilitiesRequest;
import org.opensearch.action.fieldcaps.FieldCapabilitiesRequestBuilder;
import org.opensearch.action.fieldcaps.FieldCapabilitiesResponse;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetRequestBuilder;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.get.MultiGetRequest;
import org.opensearch.action.get.MultiGetRequestBuilder;
import org.opensearch.action.get.MultiGetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexRequestBuilder;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.ClearScrollRequest;
import org.opensearch.action.search.ClearScrollRequestBuilder;
import org.opensearch.action.search.ClearScrollResponse;
import org.opensearch.action.search.CreatePitRequest;
import org.opensearch.action.search.CreatePitResponse;
import org.opensearch.action.search.DeletePitRequest;
import org.opensearch.action.search.DeletePitResponse;
import org.opensearch.action.search.GetAllPitNodesRequest;
import org.opensearch.action.search.GetAllPitNodesResponse;
import org.opensearch.action.search.MultiSearchRequest;
import org.opensearch.action.search.MultiSearchRequestBuilder;
import org.opensearch.action.search.MultiSearchResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchRequestBuilder;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.search.SearchScrollRequest;
import org.opensearch.action.search.SearchScrollRequestBuilder;
import org.opensearch.action.termvectors.MultiTermVectorsRequest;
import org.opensearch.action.termvectors.MultiTermVectorsRequestBuilder;
import org.opensearch.action.termvectors.MultiTermVectorsResponse;
import org.opensearch.action.termvectors.TermVectorsRequest;
import org.opensearch.action.termvectors.TermVectorsRequestBuilder;
import org.opensearch.action.termvectors.TermVectorsResponse;
import org.opensearch.action.update.UpdateRequest;
import org.opensearch.action.update.UpdateRequestBuilder;
import org.opensearch.action.update.UpdateResponse;
import org.opensearch.common.action.ActionFuture;
import org.opensearch.common.settings.Settings;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.action.ActionResponse;
import org.opensearch.index.IndexNotFoundException;
import org.opensearch.index.engine.VersionConflictEngineException;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.search.Scroll;
import org.opensearch.search.SearchHit;
import org.opensearch.threadpool.ThreadPool;
import org.opensearch.transport.client.AdminClient;
import org.opensearch.transport.client.Client;

import jakarta.annotation.PreDestroy;

public class FesenClient implements Client {
    public static final String HTTP_ADDRESS = "crawler.opensearch.http_address";

    public static final String TARGET_INDICES = "crawler.opensearch.target_indices";

    private static final Logger logger = LogManager.getLogger(FesenClient.class);

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

    public FesenClient() {
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
            logger.warn("Could not connect to {}", address);
        }
    }

    protected Client createClient() {
        final String[] hosts =
                split(address, ",").get(stream -> stream.map(String::trim).filter(StringUtil::isNotEmpty).toArray(n -> new String[n]));
        final Settings settings = Settings.builder().putList("http.hosts", hosts).build();
        return new HttpClient(settings, null);
    }

    public <T> T get(final Function<FesenClient, ActionFuture<T>> func) {
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
            } catch (final OpenSearchException e) {
                logger.warn("Failed to close client.", e);
            }
            logger.info("Disconnected to {}", address);
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
    public UpdateRequestBuilder prepareUpdate(final String index, final String id) {
        return client.prepareUpdate(index, id);
    }

    @Override
    public IndexRequestBuilder prepareIndex(final String index) {
        return client.prepareIndex(index);
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
    public DeleteRequestBuilder prepareDelete(final String index, final String id) {
        return client.prepareDelete(index, id);
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
    public GetRequestBuilder prepareGet(final String index, final String id) {
        return client.prepareGet(index, id);
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
    public ExplainRequestBuilder prepareExplain(final String index, final String id) {
        return client.prepareExplain(index, id);
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
    public TermVectorsRequestBuilder prepareTermVectors(final String index, final String id) {
        return client.prepareTermVectors(index, id);
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
    public <Request extends ActionRequest, Response extends ActionResponse> ActionFuture<Response> execute(
            final ActionType<Response> action, final Request request) {
        return client.execute(action, request);
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse> void execute(final ActionType<Response> action,
            final Request request, final ActionListener<Response> listener) {
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
                    throw new OpenSearchAccessException(bulkResponse.buildFailureMessage());
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
    public BulkRequestBuilder prepareBulk(final String globalIndex) {
        return client.prepareBulk(globalIndex);
    }

    @Override
    public void createPit(final CreatePitRequest createPITRequest, final ActionListener<CreatePitResponse> listener) {
        client.createPit(createPITRequest, listener);
    }

    @Override
    public void deletePits(final DeletePitRequest deletePITRequest, final ActionListener<DeletePitResponse> listener) {
        client.deletePits(deletePITRequest, listener);
    }

    @Override
    public void getAllPits(final GetAllPitNodesRequest getAllPitNodesRequest, final ActionListener<GetAllPitNodesResponse> listener) {
        client.getAllPits(getAllPitNodesRequest, listener);
    }

    @Override
    public void pitSegments(final PitSegmentsRequest pitSegmentsRequest, final ActionListener<IndicesSegmentResponse> listener) {
        client.pitSegments(pitSegmentsRequest, listener);
    }

    @Override
    public void searchView(org.opensearch.action.admin.indices.view.SearchViewAction.Request request,
            ActionListener<SearchResponse> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ActionFuture<SearchResponse> searchView(org.opensearch.action.admin.indices.view.SearchViewAction.Request request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void listViewNames(org.opensearch.action.admin.indices.view.ListViewNamesAction.Request request,
            ActionListener<org.opensearch.action.admin.indices.view.ListViewNamesAction.Response> listener) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ActionFuture<org.opensearch.action.admin.indices.view.ListViewNamesAction.Response> listViewNames(
            org.opensearch.action.admin.indices.view.ListViewNamesAction.Request request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
