/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
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
package org.codelibs.fess.crawler.client.storage;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.codelibs.core.io.InputStreamUtil;
import org.codelibs.core.lang.StringUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetObjectTagsArgs;

/**
 * @author shinsuke
 *
 */
public class StorageClientTest extends PlainTestCase {
    public StorageClient storageClient;

    private GenericContainer minioServer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        String accessKey = "AKIAIOSFODNN7EXAMPLE";
        String secretKey = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
        String bucketName = "fess";
        int i = 9000;

        minioServer = new GenericContainer<>("minio/minio")//
                .withEnv("MINIO_ACCESS_KEY", accessKey)//
                .withEnv("MINIO_SECRET_KEY", secretKey)//
                .withExposedPorts(i)//
                .withCommand("server /data")//
                .waitingFor(new HttpWaitStrategy()//
                        .forPath("/minio/health/ready")//
                        .forPort(i)//
                        .withStartupTimeout(Duration.ofSeconds(10)));
        minioServer.start();

        Integer mappedPort = minioServer.getFirstMappedPort();
        Testcontainers.exposeHostPorts(mappedPort);
        String endpoint = String.format("http://%s:%s", minioServer.getContainerIpAddress(), mappedPort);
        System.out.println("endpoint: " + endpoint);

        StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("storageClient", StorageClient.class);
        storageClient = container.getComponent("storageClient");
        Map<String, Object> params = new HashMap<>();
        params.put("endpoint", endpoint);
        params.put("accessKey", accessKey);
        params.put("secretKey", secretKey);
        storageClient.setInitParameterMap(params);

        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object("file1.txt")
                .stream(new ByteArrayInputStream("file1".getBytes()), 5, -1).contentType("application/octet-stream").build());
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object("dir1/file2.txt")
                .stream(new ByteArrayInputStream("file2".getBytes()), 5, -1).contentType("application/octet-stream").build());
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object("dir1/dir2/file3.txt")
                .stream(new ByteArrayInputStream("file3".getBytes()), 5, -1).contentType("application/octet-stream").build());
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object("dir3/file4.txt")
                .stream(new ByteArrayInputStream("file4".getBytes()), 5, -1).contentType("application/octet-stream").build());

        minioClient
                .setObjectTags(SetObjectTagsArgs.builder().bucket(bucketName).object("file1.txt").tags(Map.of("label", "label1")).build());
        minioClient.setObjectTags(
                SetObjectTagsArgs.builder().bucket(bucketName).object("dir1/file2.txt").tags(Map.of("label", "label2")).build());
        minioClient.setObjectTags(
                SetObjectTagsArgs.builder().bucket(bucketName).object("dir1/dir2/file3.txt").tags(Map.of("label", "label3")).build());
        minioClient.setObjectTags(
                SetObjectTagsArgs.builder().bucket(bucketName).object("dir3/file4.txt").tags(Map.of("label", "label4")).build());
    }

    @Override
    protected void tearDown() throws Exception {
        minioServer.stop();
        super.tearDown();
    }

    public void test_doGet() throws Exception {
        try (final ResponseData responseData = storageClient.doGet("storage://fess/file1.txt")) {
            assertEquals("storage://fess/file1.txt", responseData.getUrl());
            assertEquals("text/plain", responseData.getMimeType());
            assertEquals("file1", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
            assertEquals(5, responseData.getContentLength());
            assertEquals("label1", responseData.getMetaDataMap().get("label"));
        }
        try (final ResponseData responseData = storageClient.doGet("storage://fess/dir1/file2.txt")) {
            assertEquals("storage://fess/dir1/file2.txt", responseData.getUrl());
            assertEquals("text/plain", responseData.getMimeType());
            assertEquals("file2", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
            assertEquals(5, responseData.getContentLength());
            assertEquals("label2", responseData.getMetaDataMap().get("label"));
        }
        try (final ResponseData responseData = storageClient.doGet("storage://fess/dir1/dir2/file3.txt")) {
            assertEquals("storage://fess/dir1/dir2/file3.txt", responseData.getUrl());
            assertEquals("text/plain", responseData.getMimeType());
            assertEquals("file3", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
            assertEquals(5, responseData.getContentLength());
            assertEquals("label3", responseData.getMetaDataMap().get("label"));
        }
        try (final ResponseData responseData = storageClient.doGet("storage://fess/dir3/file4.txt")) {
            assertEquals("storage://fess/dir3/file4.txt", responseData.getUrl());
            assertEquals("text/plain", responseData.getMimeType());
            assertEquals("file4", new String(InputStreamUtil.getBytes(responseData.getResponseBody())));
            assertEquals(5, responseData.getContentLength());
            assertEquals("label4", responseData.getMetaDataMap().get("label"));
        }
        try (final ResponseData responseData = storageClient.doGet("storage://fess/")) {
            fail();
        } catch (ChildUrlsException e) {
            String[] values = e.getChildUrlList().stream().map(d -> d.getUrl()).sorted().toArray(n -> new String[n]);
            assertEquals(3, values.length);
            assertEquals("storage://fess/dir1/", values[0]);
            assertEquals("storage://fess/dir3/", values[1]);
            assertEquals("storage://fess/file1.txt", values[2]);
        }
        try (final ResponseData responseData = storageClient.doGet("storage://fess/dir1/")) {
            fail();
        } catch (ChildUrlsException e) {
            String[] values = e.getChildUrlList().stream().map(d -> d.getUrl()).sorted().toArray(n -> new String[n]);
            assertEquals(2, values.length);
            assertEquals("storage://fess/dir1/dir2/", values[0]);
            assertEquals("storage://fess/dir1/file2.txt", values[1]);
        }
        try (final ResponseData responseData = storageClient.doGet("storage://fess/dir1/dir2/")) {
            fail();
        } catch (ChildUrlsException e) {
            String[] values = e.getChildUrlList().stream().map(d -> d.getUrl()).sorted().toArray(n -> new String[n]);
            assertEquals(1, values.length);
            assertEquals("storage://fess/dir1/dir2/file3.txt", values[0]);
        }
        try (final ResponseData responseData = storageClient.doGet("storage://fess/dir3/")) {
            fail();
        } catch (ChildUrlsException e) {
            String[] values = e.getChildUrlList().stream().map(d -> d.getUrl()).sorted().toArray(n -> new String[n]);
            assertEquals(1, values.length);
            assertEquals("storage://fess/dir3/file4.txt", values[0]);
        }
        try (final ResponseData responseData = storageClient.doGet("storage://fess/none")) {
            fail();
        } catch (ChildUrlsException e) {
            String[] values = e.getChildUrlList().stream().map(d -> d.getUrl()).sorted().toArray(n -> new String[n]);
            assertEquals(0, values.length);
        }
        try (final ResponseData responseData = storageClient.doGet("")) {
            fail();
        } catch (CrawlerSystemException e) {
            // nothing
        }
    }

    public void test_doHead() throws Exception {
        try (final ResponseData responseData = storageClient.doHead("storage://fess/file1.txt")) {
            assertEquals("storage://fess/file1.txt", responseData.getUrl());
            assertEquals("application/octet-stream", responseData.getMimeType());
            assertNull(responseData.getResponseBody());
            assertNull(responseData.getMetaDataMap().get("label"));
        }
        try (final ResponseData responseData = storageClient.doHead("storage://fess/dir1/file2.txt")) {
            assertEquals("storage://fess/dir1/file2.txt", responseData.getUrl());
            assertEquals("application/octet-stream", responseData.getMimeType());
            assertNull(responseData.getResponseBody());
            assertNull(responseData.getMetaDataMap().get("label"));
        }
        try (final ResponseData responseData = storageClient.doHead("storage://fess/dir1/dir2/file3.txt")) {
            assertEquals("storage://fess/dir1/dir2/file3.txt", responseData.getUrl());
            assertEquals("application/octet-stream", responseData.getMimeType());
            assertNull(responseData.getResponseBody());
            assertNull(responseData.getMetaDataMap().get("label"));
        }
        try (final ResponseData responseData = storageClient.doHead("storage://fess/dir3/file4.txt")) {
            assertEquals("storage://fess/dir3/file4.txt", responseData.getUrl());
            assertEquals("application/octet-stream", responseData.getMimeType());
            assertNull(responseData.getResponseBody());
            assertNull(responseData.getMetaDataMap().get("label"));
        }
        try (final ResponseData responseData = storageClient.doHead("storage://fess/")) {
            assertNull(responseData);
        }
        try (final ResponseData responseData = storageClient.doHead("storage://fess/dir1/")) {
            assertNull(responseData);
        }
        try (final ResponseData responseData = storageClient.doHead("storage://fess/dir1/dir2/")) {
            assertNull(responseData);
        }
        try (final ResponseData responseData = storageClient.doHead("storage://fess/dir3/")) {
            assertNull(responseData);
        }
        try (final ResponseData responseData = storageClient.doHead("storage://fess/none")) {
            assertNull(responseData);
        }
        try (final ResponseData responseData = storageClient.doHead("")) {
            fail();
        } catch (CrawlerSystemException e) {
            // nothing
        }
    }

    public void test_parsePath() {
        String[] values;

        values = storageClient.parsePath("bucket/path");
        assertEquals("bucket", values[0]);
        assertEquals("path", values[1]);

        values = storageClient.parsePath("bucket/path1/path2");
        assertEquals("bucket", values[0]);
        assertEquals("path1/path2", values[1]);

        values = storageClient.parsePath("bucket/");
        assertEquals("bucket", values[0]);
        assertEquals(StringUtil.EMPTY, values[1]);

        try {
            storageClient.parsePath("");
            fail();
        } catch (final CrawlingAccessException e) {
            // ok
        }
        try {
            storageClient.parsePath(null);
            fail();
        } catch (final CrawlingAccessException e) {
            // ok
        }
    }
}
