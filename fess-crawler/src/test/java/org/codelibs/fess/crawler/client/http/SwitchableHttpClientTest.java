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
package org.codelibs.fess.crawler.client.http;

import static org.mockito.Mockito.mock;

import org.codelibs.fess.crawler.client.CrawlerClient;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class SwitchableHttpClientTest extends PlainTestCase {

    private String originalPropertyValue;

    @Override
    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        // Save original property value
        originalPropertyValue = System.getProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        // Restore original property value
        if (originalPropertyValue != null) {
            System.setProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY, originalPropertyValue);
        } else {
            System.clearProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY);
        }
        super.tearDown();
    }

    public void test_constructor_defaultUsesHc5() {
        System.clearProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY);

        SwitchableHttpClient client = new SwitchableHttpClient();

        assertTrue(client.isUseHc5());
    }

    public void test_selectClient_hc4Property() {
        System.setProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY, "hc4");

        SwitchableHttpClient client = new SwitchableHttpClient();

        assertFalse(client.isUseHc5());
    }

    public void test_selectClient_hc5Property() {
        System.setProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY, "hc5");

        SwitchableHttpClient client = new SwitchableHttpClient();

        assertTrue(client.isUseHc5());
    }

    public void test_selectClient_hc4PropertyCaseInsensitive() {
        System.setProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY, "HC4");

        SwitchableHttpClient client = new SwitchableHttpClient();

        assertFalse(client.isUseHc5());
    }

    public void test_init_withHc5Client() {
        System.clearProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY);
        CrawlerClient mockHc5Client = mock(CrawlerClient.class);

        SwitchableHttpClient client = new SwitchableHttpClient();
        client.setHc5Client(mockHc5Client);
        client.init();

        assertTrue(client.isUseHc5());
    }

    public void test_init_withHc4Client() {
        System.setProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY, "hc4");
        CrawlerClient mockHc4Client = mock(CrawlerClient.class);

        SwitchableHttpClient client = new SwitchableHttpClient();
        client.setHc4Client(mockHc4Client);
        client.init();

        assertFalse(client.isUseHc5());
    }

    public void test_init_hc5Null_fallbackToHc4() {
        System.clearProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY);
        CrawlerClient mockHc4Client = mock(CrawlerClient.class);

        SwitchableHttpClient client = new SwitchableHttpClient();
        client.setHc5Client(null);
        client.setHc4Client(mockHc4Client);
        client.init();

        // Should still report useHc5 as true (original selection)
        // but internally uses hc4 as fallback
        assertTrue(client.isUseHc5());
    }

    public void test_init_hc4Null_fallbackToHc5() {
        System.setProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY, "hc4");
        CrawlerClient mockHc5Client = mock(CrawlerClient.class);

        SwitchableHttpClient client = new SwitchableHttpClient();
        client.setHc4Client(null);
        client.setHc5Client(mockHc5Client);
        client.init();

        // Should still report useHc5 as false (original selection)
        // but internally uses hc5 as fallback
        assertFalse(client.isUseHc5());
    }

    public void test_settersAndGetters() {
        CrawlerClient mockHc4Client = mock(CrawlerClient.class);
        CrawlerClient mockHc5Client = mock(CrawlerClient.class);

        SwitchableHttpClient client = new SwitchableHttpClient();
        client.setHc4Client(mockHc4Client);
        client.setHc5Client(mockHc5Client);

        assertSame(mockHc4Client, client.getHc4Client());
        assertSame(mockHc5Client, client.getHc5Client());
    }

    public void test_isUseHc5() {
        System.clearProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY);
        SwitchableHttpClient hc5Client = new SwitchableHttpClient();
        assertTrue(hc5Client.isUseHc5());

        System.setProperty(SwitchableHttpClient.HTTP_CLIENT_PROPERTY, "hc4");
        SwitchableHttpClient hc4Client = new SwitchableHttpClient();
        assertFalse(hc4Client.isUseHc5());
    }
}
