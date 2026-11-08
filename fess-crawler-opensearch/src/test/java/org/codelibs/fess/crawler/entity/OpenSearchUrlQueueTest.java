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
package org.codelibs.fess.crawler.entity;

import static org.codelibs.fesen.opensearch.common.xcontent.XContentFactory.jsonBuilder;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.core.beans.util.BeanUtil;
import org.codelibs.fesen.opensearch.core.xcontent.XContentBuilder;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

public class OpenSearchUrlQueueTest extends PlainTestCase {

    private static final String SECRET_URL = "http://user:secret@example.com/path";

    private static final String LOG_URL = "http://example.com/path";

    @Test
    public void test_getLogUrl() {
        final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
        urlQueue.setUrl(SECRET_URL);
        assertEquals(LOG_URL, urlQueue.getLogUrl());
        assertEquals(SECRET_URL, urlQueue.getUrl());
    }

    @Test
    public void test_getLogUrl_restoredFromSource() {
        // the same copy as AbstractCrawlerService#getList
        final Map<String, Object> source = new HashMap<>();
        source.put(OpenSearchUrlQueue.SESSION_ID, "s1");
        source.put(OpenSearchUrlQueue.URL, SECRET_URL);
        source.put(OpenSearchUrlQueue.PARENT_URL, SECRET_URL);
        final OpenSearchUrlQueue urlQueue = BeanUtil.copyMapToNewBean(source, OpenSearchUrlQueue.class);
        assertEquals(SECRET_URL, urlQueue.getUrl());
        assertEquals(LOG_URL, urlQueue.getLogUrl());
        assertFalse(urlQueue.toString().contains("secret"));
    }

    @Test
    public void test_toXContent_doesNotStoreLogUrl() throws Exception {
        final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
        urlQueue.setSessionId("s1");
        urlQueue.setUrl(SECRET_URL);
        assertEquals(LOG_URL, urlQueue.getLogUrl());
        final XContentBuilder builder = jsonBuilder().value(urlQueue);
        builder.flush();
        final String json = builder.toString();
        // what is stored is the URL as it is
        assertTrue(json.contains(SECRET_URL));
        assertFalse(json.contains("logUrl"));
    }

    @Test
    public void test_toString_withoutUserInfo() {
        final OpenSearchUrlQueue urlQueue = new OpenSearchUrlQueue();
        urlQueue.setUrl(SECRET_URL);
        urlQueue.setParentUrl(SECRET_URL);
        final String text = urlQueue.toString();
        assertTrue(text.contains(LOG_URL));
        assertFalse(text.contains("secret"));
    }
}
