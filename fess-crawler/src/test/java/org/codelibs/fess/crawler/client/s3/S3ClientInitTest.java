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

import java.util.HashMap;
import java.util.Map;

import org.codelibs.fess.crawler.container.StandardCrawlerContainer;
import org.codelibs.fess.crawler.exception.CrawlingAccessException;
import org.codelibs.fess.crawler.helper.impl.MimeTypeHelperImpl;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Docker-free tests for {@link S3Client#init()}.
 *
 * <p>{@code init()} builds the AWS S3 client offline: {@code builder.build()} performs no
 * network I/O and both the {@code DefaultCredentialsProvider} chain and cross-region access
 * resolve lazily on the first request. So these tests need no live S3/IMDS endpoint and no
 * MinIO container, and only assert that the client is constructed (or fails fast) as expected.
 * Behavioral tests that require object storage live in {@link S3ClientTest}.
 */
public class S3ClientInitTest extends PlainTestCase {

    private static final String ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";

    private static final String SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";

    /**
     * Builds a fresh, not-yet-initialized S3Client so that {@code init()} actually runs.
     */
    private S3Client newS3Client() {
        final StandardCrawlerContainer container = new StandardCrawlerContainer()//
                .singleton("mimeTypeHelper", MimeTypeHelperImpl.class)//
                .singleton("s3Client", S3Client.class);
        return container.getComponent("s3Client");
    }

    @Test
    public void test_init_blankCredentials_withEndpoint() {
        // Blank accessKey/secretKey with an endpoint set: no static credentials, so the AWS SDK
        // falls back to the default credential provider chain. Construction must still succeed.
        // A dummy endpoint is sufficient: init() builds the client offline and never connects.
        final S3Client client = newS3Client();
        final Map<String, Object> params = new HashMap<>();
        params.put("endpoint", "http://localhost:9000");
        params.put("region", "us-east-1");
        client.setInitParameterMap(params);

        client.init();
        assertNotNull(client.awsS3Client);
    }

    @Test
    public void test_init_partialCredentials_accessKeyOnly_throws() {
        // accessKey set but secretKey blank is almost always a misconfiguration and must fail fast.
        // init() throws at the credential guard, before the endpoint is even read.
        final S3Client client = newS3Client();
        final Map<String, Object> params = new HashMap<>();
        params.put("endpoint", "http://localhost:9000");
        params.put("accessKey", ACCESS_KEY);
        client.setInitParameterMap(params);

        try {
            client.init();
            fail();
        } catch (final CrawlingAccessException e) {
            // expected: accessKey without secretKey must fail fast
        }
    }

    @Test
    public void test_init_partialCredentials_secretKeyOnly_throws() {
        // The reverse XOR case: secretKey set but accessKey blank must also fail fast.
        final S3Client client = newS3Client();
        final Map<String, Object> params = new HashMap<>();
        params.put("endpoint", "http://localhost:9000");
        params.put("secretKey", SECRET_KEY);
        client.setInitParameterMap(params);

        try {
            client.init();
            fail();
        } catch (final CrawlingAccessException e) {
            // expected: secretKey without accessKey must fail fast
        }
    }

    @Test
    public void test_init_noEndpoint_noCredentials() {
        // Standard Amazon S3 + IAM-role usage: no endpoint and no static credentials. The endpoint
        // is derived from the region, cross-region access defaults to enabled, and construction is
        // offline, so init() must not throw.
        final S3Client client = newS3Client();
        final Map<String, Object> params = new HashMap<>();
        params.put("region", "us-east-1");
        client.setInitParameterMap(params);

        client.init();
        assertNotNull(client.awsS3Client);
        assertCrossRegion(client, true);
    }

    @Test
    public void test_init_noEndpoint_staticCredentials() {
        // Standard Amazon S3 with static credentials (no endpoint, both keys set): the SDK derives
        // the endpoint from the region and uses the static credentials. Cross-region access still
        // defaults to enabled and composes with the static credentials. Construction is offline.
        final S3Client client = newS3Client();
        final Map<String, Object> params = new HashMap<>();
        params.put("region", "us-east-1");
        params.put("accessKey", ACCESS_KEY);
        params.put("secretKey", SECRET_KEY);
        client.setInitParameterMap(params);

        client.init();
        assertNotNull(client.awsS3Client);
        assertCrossRegion(client, true);
    }

    @Test
    public void test_init_noEndpoint_crossRegionDisabled() {
        // Standard Amazon S3 with cross-region access explicitly disabled: init() must still build
        // the client, but as a plain (non-cross-region) client. The bucket's region must then match
        // the configured region at request time.
        final S3Client client = newS3Client();
        final Map<String, Object> params = new HashMap<>();
        params.put("region", "us-east-1");
        params.put("crossRegionAccessEnabled", "false");
        client.setInitParameterMap(params);

        client.init();
        assertNotNull(client.awsS3Client);
        assertCrossRegion(client, false);
    }

    @Test
    public void test_init_withEndpoint_crossRegionIgnored() {
        // Cross-region access is a standard-Amazon-S3 concept; with an endpoint override set it is
        // ignored (incompatible with a custom endpoint). Even when requested, the built client must
        // be a plain (non-cross-region) client.
        final S3Client client = newS3Client();
        final Map<String, Object> params = new HashMap<>();
        params.put("endpoint", "http://localhost:9000");
        params.put("region", "us-east-1");
        params.put("crossRegionAccessEnabled", "true");
        client.setInitParameterMap(params);

        client.init();
        assertNotNull(client.awsS3Client);
        assertCrossRegion(client, false);
    }

    /**
     * Asserts whether the built AWS S3 client actually has cross-region access applied. The AWS SDK
     * wraps the client in an {@code S3CrossRegionSyncClient} when {@code crossRegionAccessEnabled} is
     * on, and returns a plain {@code DefaultS3Client} otherwise. The exact class names are an
     * SDK-internal detail, so this matches on the {@code CrossRegion} marker in the simple name
     * rather than the fully qualified type.
     */
    private void assertCrossRegion(final S3Client client, final boolean expected) {
        final boolean actual = client.awsS3Client.getClass().getSimpleName().contains("CrossRegion");
        assertEquals(expected, actual);
    }
}
