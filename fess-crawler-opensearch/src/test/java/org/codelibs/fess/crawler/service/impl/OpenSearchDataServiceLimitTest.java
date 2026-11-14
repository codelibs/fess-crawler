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

import org.codelibs.fess.crawler.entity.OpenSearchAccessResult;
import org.codelibs.fess.crawler.entity.OpenSearchAccessResultData;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

public class OpenSearchDataServiceLimitTest extends PlainTestCase {

    private final OpenSearchDataService dataService = new OpenSearchDataService("fess_crawler", "data");

    private OpenSearchAccessResult newAccessResult(final byte[] data) {
        final OpenSearchAccessResult accessResult = new OpenSearchAccessResult();
        accessResult.setSessionId("s1");
        accessResult.setUrl("http://www.example.com/");
        final OpenSearchAccessResultData accessResultData = new OpenSearchAccessResultData();
        accessResultData.setTransformerName("t");
        accessResultData.setData(data);
        accessResult.setAccessResultData(accessResultData);
        return accessResult;
    }

    @Test
    public void test_limitData_keepsDataWithinLimit() {
        dataService.setMaxDataSize(100);
        final OpenSearchAccessResult accessResult = newAccessResult(new byte[100]);

        assertFalse(dataService.limitData(accessResult));

        assertNotNull(accessResult.getAccessResultData());
        assertEquals(100, accessResult.getAccessResultData().getData().length);
    }

    @Test
    public void test_limitData_dropsDataOverLimit() {
        dataService.setMaxDataSize(100);
        final OpenSearchAccessResult accessResult = newAccessResult(new byte[101]);

        assertTrue(dataService.limitData(accessResult));

        assertNull(accessResult.getAccessResultData());
    }

    @Test
    public void test_limitData_noData() {
        dataService.setMaxDataSize(100);
        final OpenSearchAccessResult accessResult = new OpenSearchAccessResult();
        accessResult.setAccessResultData(null);

        assertFalse(dataService.limitData(accessResult));

        final OpenSearchAccessResult emptyData = newAccessResult(null);
        assertFalse(dataService.limitData(emptyData));
        assertNotNull(emptyData.getAccessResultData());
    }

    @Test
    public void test_defaultMaxDataSize_fitsInStringLimitOfOpenSearch() {
        // base64 turns n bytes into 4 * ceil(n / 3) characters, and OpenSearch reads a string value of up to 50,000,000 characters
        final long encoded = 4L * ((dataService.getMaxDataSize() + 2L) / 3L);
        assertTrue(encoded <= 50_000_000L);
        // and the limit should not be needlessly small
        assertTrue(dataService.getMaxDataSize() >= 36_000_000);
    }

    @Test
    public void test_limitData_disabled() {
        dataService.setMaxDataSize(0);
        final OpenSearchAccessResult accessResult = newAccessResult(new byte[1000]);

        assertFalse(dataService.limitData(accessResult));

        assertNotNull(accessResult.getAccessResultData());
    }

    @Test
    public void test_limitData_doesNotLoadTheData() {
        dataService.setMaxDataSize(100);
        // an access result that was read from the search, whose data is not loaded: loading it needs the data service
        final OpenSearchAccessResult accessResult = new OpenSearchAccessResult();
        accessResult.setSessionId("s1");
        accessResult.setUrl("http://www.example.com/");

        assertFalse(dataService.limitData(accessResult));
    }
}
