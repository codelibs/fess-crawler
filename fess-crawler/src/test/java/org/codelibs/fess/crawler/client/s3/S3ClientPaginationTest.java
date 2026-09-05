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
package org.codelibs.fess.crawler.client.s3;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codelibs.core.lang.ThreadUtil;
import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.exception.ChildUrlsException;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Test class verifying that S3Client walks every page of a ListObjectsV2 result.
 *
 * <p>
 * S3 caps a single ListObjectsV2 response at 1000 entries, and objects and common
 * prefixes share that budget. This fixture deliberately exceeds the cap so that a
 * non-paginating implementation silently drops the tail of the listing.
 * </p>
 */
public class S3ClientPaginationTest extends PlainTestCase {

    private static final Logger logger = LogManager.getLogger(S3ClientPaginationTest.class);

    private static final String IMAGE_NAME = "localstack/localstack:4.14.0";

    private static final String SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

    private static final String ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";

    private static final String BUCKET_NAME = "fess";

    /** Number of objects placed directly at the bucket root, above the 1000-entry page cap. */
    private static final int FILE_COUNT = 1005;

    /** Number of sub-directories at the bucket root. They sort after the files, so they land on a later page. */
    private static final int DIR_COUNT = 5;

    public S3Client s3Client;

    private GenericContainer<?> localstackServer;

    @Override
    protected void setUp(final TestInfo testInfo) throws Exception {
        super.setUp(testInfo);

        final int port = 4566;
        logger.info("Creating {}", IMAGE_NAME);
        localstackServer = new GenericContainer<>(IMAGE_NAME)//
                .withEnv("SERVICES", "s3")//
                .withExposedPorts(port)//
                .waitingFor(new HttpWaitStrategy()//
                        .forPath("/_localstack/health")//
                        .forPort(port)//
                        .withStartupTimeout(Duration.ofSeconds(120)));
        logger.info("Starting {}", IMAGE_NAME);
        localstackServer.start();
        logger.info("Started {}", IMAGE_NAME);

        final Integer mappedPort = localstackServer.getFirstMappedPort();
        final String endpoint = String.format("http://%s:%s", localstackServer.getHost(), mappedPort);
        logger.info("endpoint: {}", endpoint);

        final StandardCrawlerContainer container = new StandardCrawlerContainer().singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("s3Client", S3Client.class);
        s3Client = container.getComponent("s3Client");
        final Map<String, Object> params = new HashMap<>();
        params.put("endpoint", endpoint);
        params.put("accessKey", ACCESS_KEY);
        params.put("secretKey", SECRET_KEY);
        params.put("region", "us-east-1");
        s3Client.setInitParameterMap(params);

        Exception lastException = null;
        for (int i = 0; i < 10; i++) {
            try {
                setupS3Fixture(endpoint);
                lastException = null;
                break;
            } catch (final Exception e) {
                lastException = e;
                logger.warn("[{}] {}", i + 1, e.getMessage());
            }
            ThreadUtil.sleep(1000L);
        }
        if (lastException != null) {
            // Never leave a partial fixture behind: it would surface as a bogus pagination failure.
            throw new IllegalStateException("Failed to create the S3 fixture.", lastException);
        }
    }

    private void setupS3Fixture(final String endpoint) throws Exception {
        try (software.amazon.awssdk.services.s3.S3Client client = software.amazon.awssdk.services.s3.S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .forcePathStyle(true)
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY)))
                .build()) {
            try {
                client.createBucket(CreateBucketRequest.builder().bucket(BUCKET_NAME).build());
            } catch (final BucketAlreadyOwnedByYouException | BucketAlreadyExistsException e) {
                // A previous attempt already created it.
            }

            final ExecutorService executorService = Executors.newFixedThreadPool(16);
            try {
                final List<Future<?>> futures = new ArrayList<>();
                for (int i = 0; i < FILE_COUNT; i++) {
                    final String key = String.format("file%04d.txt", i);
                    futures.add(executorService.submit(() -> putObject(client, key)));
                }
                for (int i = 0; i < DIR_COUNT; i++) {
                    final String key = String.format("zdir%d/file.txt", i);
                    futures.add(executorService.submit(() -> putObject(client, key)));
                }
                // get() rethrows whatever a task threw, so a short fixture cannot be mistaken
                // for a truncated listing.
                for (final Future<?> future : futures) {
                    future.get(300L, TimeUnit.SECONDS);
                }
            } finally {
                executorService.shutdownNow();
            }
        }
    }

    private void putObject(final software.amazon.awssdk.services.s3.S3Client client, final String key) {
        client.putObject(PutObjectRequest.builder().bucket(BUCKET_NAME).key(key).contentType("text/plain").build(),
                RequestBody.fromBytes("x".getBytes()));
    }

    @Override
    protected void tearDown(final TestInfo testInfo) throws Exception {
        localstackServer.stop();
        super.tearDown(testInfo);
    }

    @Test
    public void test_doGet_listingBeyondSinglePage() throws Exception {
        try (final ResponseData responseData = s3Client.doGet("s3://" + BUCKET_NAME + "/")) {
            fail();
        } catch (final ChildUrlsException e) {
            final Set<String> urls = e.getChildUrlList().stream().map(d -> d.getUrl()).collect(Collectors.toSet());

            // Every object and every sub-directory must be discovered, not just the first page.
            assertEquals(FILE_COUNT + DIR_COUNT, urls.size());

            // The first page boundary must not cut the listing short.
            assertTrue(urls.contains("s3://" + BUCKET_NAME + "/file0000.txt"));
            assertTrue(urls.contains("s3://" + BUCKET_NAME + "/file0999.txt"));
            assertTrue(urls.contains("s3://" + BUCKET_NAME + "/file1004.txt"));

            // Common prefixes share the page budget with objects, so they are lost first.
            for (int i = 0; i < DIR_COUNT; i++) {
                assertTrue(urls.contains("s3://" + BUCKET_NAME + "/zdir" + i + "/"));
            }
        }
    }
}
