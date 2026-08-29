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
package org.codelibs.fess.crawler.processor.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codelibs.fess.crawler.CrawlerContext;
import org.codelibs.fess.crawler.builder.RequestDataBuilder;
import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.entity.UrlQueue;
import org.codelibs.fess.crawler.entity.UrlQueueImpl;
import org.codelibs.fess.crawler.filter.UrlFilter;
import org.codelibs.fess.crawler.service.UrlQueueService;
import org.codelibs.fess.crawler.util.CrawlingParameterUtil;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * @author shinsuke
 *
 */
public class DefaultResponseProcessorTest extends PlainTestCase {

    @Test
    public void test_isSuccessful() {
        DefaultResponseProcessor processor = new DefaultResponseProcessor();
        processor.setSuccessfulHttpCodes(new int[] { 200 });
        processor.setNotModifiedHttpCodes(new int[] { 304 });

        ResponseData responseData = new ResponseData();
        responseData.setHttpStatusCode(200);
        assertTrue(processor.isSuccessful(responseData));

        responseData.setHttpStatusCode(100);
        assertFalse(processor.isSuccessful(responseData));
        responseData.setHttpStatusCode(304);
        assertFalse(processor.isSuccessful(responseData));
        responseData.setHttpStatusCode(404);
        assertFalse(processor.isSuccessful(responseData));
    }

    @Test
    public void test_isNotModified() {
        DefaultResponseProcessor processor = new DefaultResponseProcessor();
        processor.setSuccessfulHttpCodes(new int[] { 200 });
        processor.setNotModifiedHttpCodes(new int[] { 304 });

        ResponseData responseData = new ResponseData();
        responseData.setHttpStatusCode(304);
        assertTrue(processor.isNotModified(responseData));

        responseData.setHttpStatusCode(100);
        assertFalse(processor.isNotModified(responseData));
        responseData.setHttpStatusCode(200);
        assertFalse(processor.isNotModified(responseData));
        responseData.setHttpStatusCode(404);
        assertFalse(processor.isNotModified(responseData));
    }

    /**
     * Test that storeChildUrls applies the configured weigher to the queued child URLs
     * before offering them to the URL queue service.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_storeChildUrls_appliesWeigher() {
        final DefaultResponseProcessor processor = new DefaultResponseProcessor();

        final CrawlerContainer crawlerContainer = mock(CrawlerContainer.class);
        when(crawlerContainer.getComponent("urlQueue")).thenReturn(new UrlQueueImpl<>());
        processor.crawlerContainer = crawlerContainer;

        final List<UrlQueue<?>> weighed = new ArrayList<>();
        processor.urlQueueWeigher = (sessionId, childList) -> {
            weighed.addAll(childList);
            childList.forEach(uq -> uq.setWeight(7.5f));
        };

        final UrlFilter urlFilter = mock(UrlFilter.class);
        when(urlFilter.match(anyString())).thenReturn(true);
        final CrawlerContext crawlerContext = new CrawlerContext();
        crawlerContext.setSessionId("test-session");
        crawlerContext.setUrlFilter(urlFilter);

        final UrlQueueService<UrlQueue<?>> urlQueueService = mock(UrlQueueService.class);
        CrawlingParameterUtil.setUrlQueueService(urlQueueService);
        try {
            final Set<RequestData> childUrlList = new HashSet<>();
            childUrlList.add(RequestDataBuilder.newRequestData().get().url("http://example.com/child1").build());

            processor.storeChildUrls(crawlerContext, childUrlList, "http://example.com/", 2, "UTF-8");

            assertEquals(1, weighed.size());
            assertEquals("test-session", weighed.get(0).getSessionId());
            assertEquals(Float.valueOf(7.5f), Float.valueOf(weighed.get(0).getWeight()));
            verify(urlQueueService, times(1)).offerAll(anyString(), any());
        } finally {
            CrawlingParameterUtil.setUrlQueueService(null);
        }
    }

    /**
     * Test that a throwing weigher does not lose the child URLs: they are still offered to the
     * queue with their inherited weights instead of the whole batch being dropped.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test_storeChildUrls_throwingWeigherKeepsInheritedWeights() {
        final DefaultResponseProcessor processor = new DefaultResponseProcessor();

        final CrawlerContainer crawlerContainer = mock(CrawlerContainer.class);
        when(crawlerContainer.getComponent("urlQueue")).thenReturn(new UrlQueueImpl<>());
        processor.crawlerContainer = crawlerContainer;

        processor.urlQueueWeigher = (sessionId, childList) -> {
            throw new RuntimeException("weigher failure");
        };

        final UrlFilter urlFilter = mock(UrlFilter.class);
        when(urlFilter.match(anyString())).thenReturn(true);
        final CrawlerContext crawlerContext = new CrawlerContext();
        crawlerContext.setSessionId("test-session");
        crawlerContext.setUrlFilter(urlFilter);

        final UrlQueueService<UrlQueue<?>> urlQueueService = mock(UrlQueueService.class);
        CrawlingParameterUtil.setUrlQueueService(urlQueueService);
        try {
            final Set<RequestData> childUrlList = new HashSet<>();
            childUrlList.add(RequestDataBuilder.newRequestData().get().url("http://example.com/child1").weight(3.0f).build());

            processor.storeChildUrls(crawlerContext, childUrlList, "http://example.com/", 2, "UTF-8");

            final org.mockito.ArgumentCaptor<List<UrlQueue<?>>> captor = org.mockito.ArgumentCaptor.forClass(List.class);
            verify(urlQueueService, times(1)).offerAll(anyString(), captor.capture());
            assertEquals(1, captor.getValue().size());
            assertEquals(Float.valueOf(3.0f), Float.valueOf(captor.getValue().get(0).getWeight()));
        } finally {
            CrawlingParameterUtil.setUrlQueueService(null);
        }
    }
}
