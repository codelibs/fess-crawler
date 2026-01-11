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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.crawler.client.fs.FileSystemClient;
import org.codelibs.fess.crawler.client.ftp.FtpClient;
import org.codelibs.fess.crawler.client.http.Hc5HttpClient;
import org.codelibs.fess.crawler.client.smb.SmbClient;
import org.codelibs.fess.crawler.client.storage.StorageClient;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.RequestData;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

/**
 * @author shinsuke
 *
 */
public class CrawlerClientFactoryTest extends PlainTestCase {
    public CrawlerClientFactory clientFactory;

    @Override
    @BeforeEach
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("httpClient", FaultTolerantClient.class)//
                .singleton("fsClient", FileSystemClient.class)//
                .singleton("smbClient", SmbClient.class)//
                .singleton("smb1Client", org.codelibs.fess.crawler.client.smb1.SmbClient.class)//
                .singleton("ftpClient", FtpClient.class)//
                .singleton("storageClient", StorageClient.class)//
                .singleton("clientFactory", CrawlerClientFactory.class);
        clientFactory = container.getComponent("clientFactory");
        FaultTolerantClient httpClient = container.getComponent("httpClient");
        httpClient.setCrawlerClient(new Hc5HttpClient());
        clientFactory.addClient("http:.*", httpClient);
        clientFactory.addClient("https:.*", httpClient);
        clientFactory.addClient("file:.*", container.getComponent("fsClient"));
        clientFactory.addClient("smb:.*", container.getComponent("smbClient"));
        clientFactory.addClient("smb1:.*", container.getComponent("smb1Client"));
        clientFactory.addClient("ftp:.*", container.getComponent("ftpClient"));
        clientFactory.addClient("storage:.*", container.getComponent("storageClient"));
    }

    public void test_getClient() {
        String url;
        CrawlerClient client;

        url = "http://hoge.com/";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof FaultTolerantClient);
        assertTrue(((FaultTolerantClient) client).getCrawlerClient() instanceof Hc5HttpClient);

        url = "https://hoge.com/";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof FaultTolerantClient);
        assertTrue(((FaultTolerantClient) client).getCrawlerClient() instanceof Hc5HttpClient);

        url = "file:/home/hoge";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof FileSystemClient);

        url = "smb:/home/hoge";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof SmbClient);

        url = "smb1:/home/hoge";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof org.codelibs.fess.crawler.client.smb1.SmbClient);

        url = "ftp:/home/hoge";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof FtpClient);

        url = "storage:/home/hoge";
        client = clientFactory.getClient(url);
        assertNotNull(client);
        assertTrue(client instanceof StorageClient);

    }

    public void test_setInitParameterMap() {
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("hoge", "test");

        final StringBuilder buf = new StringBuilder();
        clientFactory.addClient("test:.*", new CrawlerClient() {
            @Override
            public ResponseData execute(final RequestData request) {
                return null;
            }

            @Override
            public void setInitParameterMap(final Map<String, Object> params) {
                buf.append("value=").append(params.get("hoge"));
            }

        });

        clientFactory.setInitParameterMap(paramMap);

        assertEquals("value=test", buf.toString());
    }

    public void test_getClient_null() {
        String url;
        CrawlerClient client;

        url = null;
        client = clientFactory.getClient(url);
        assertNull(client);

        url = "";
        client = clientFactory.getClient(url);
        assertNull(client);

        url = " ";
        client = clientFactory.getClient(url);
        assertNull(client);
    }
}
