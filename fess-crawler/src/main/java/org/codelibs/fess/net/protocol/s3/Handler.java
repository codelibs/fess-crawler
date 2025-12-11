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
package org.codelibs.fess.net.protocol.s3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;

import org.codelibs.core.lang.StringUtil;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Handler for the "s3" protocol, allowing access to objects stored in S3-compatible storage services.
 * This handler extends {@link URLStreamHandler} to provide a way to open connections to S3 objects
 * using URLs with the "s3" protocol.
 *
 * <p>
 * The URL format is expected to be: {@code s3://bucketName/objectName}.
 * The bucket name and object name are extracted from the URL.
 * </p>
 *
 * <p>
 * The handler relies on environment variables for configuration:
 * </p>
 * <ul>
 *   <li>{@code S3_ENDPOINT}: The endpoint URL of the S3 service.</li>
 *   <li>{@code S3_ACCESS_KEY}: The access key for authentication.</li>
 *   <li>{@code S3_SECRET_KEY}: The secret key for authentication.</li>
 *   <li>{@code S3_REGION}: The region of the S3 service (default: us-east-1).</li>
 * </ul>
 *
 * <p>
 * The {@link S3URLConnection} class handles the actual connection and data retrieval from the S3 service.
 * </p>
 */
public class Handler extends URLStreamHandler {

    /**
     * Constructs a new Handler.
     */
    public Handler() {
        // Default constructor
    }

    /**
     * Opens a connection to the S3 URL.
     *
     * @param u The URL to open a connection to
     * @return A new S3URLConnection instance
     * @throws IOException If the connection cannot be opened
     */
    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        return new S3URLConnection(u);
    }

    /**
     * S3URLConnection is a URL connection implementation for accessing S3 objects.
     * It extends URLConnection to provide connectivity to S3-compatible storage services.
     * This class handles the authentication, connection management, and data retrieval
     * from S3 buckets and objects.
     *
     * <p>
     * The connection extracts bucket and object names from the URL and uses environment
     * variables for authentication and endpoint configuration.
     * </p>
     */
    public class S3URLConnection extends URLConnection {

        /** The AWS S3 client for storage operations */
        private S3Client s3Client;
        /** The name of the storage bucket */
        private String bucketName;
        /** The name of the storage object */
        private String objectName;
        /** Cached object head response */
        private HeadObjectResponse headObject;

        /**
         * Constructs a new S3URLConnection for the specified URL.
         * This constructor parses the URL to extract bucket and object names.
         *
         * @param url The S3 URL to connect to
         */
        protected S3URLConnection(final URL url) {
            super(url);
            // Extract bucket name from host
            bucketName = url.getHost() != null ? url.getHost() : StringUtil.EMPTY;
            // Extract object name from path, removing leading slash if present and decoding URL encoding
            final String path = url.getPath();
            if (path != null && !path.isEmpty()) {
                final String pathWithoutLeadingSlash = path.startsWith("/") ? path.substring(1) : path;
                // Decode URL-encoded characters (e.g., %20 -> space)
                objectName = URLDecoder.decode(pathWithoutLeadingSlash, StandardCharsets.UTF_8);
            } else {
                objectName = StringUtil.EMPTY;
            }
        }

        /**
         * Establishes a connection to the S3 service.
         * This method creates an AWS S3 client using environment variables for configuration.
         * This method is synchronized to ensure thread-safe connection initialization.
         *
         * @throws IOException If the connection cannot be established
         */
        @Override
        public synchronized void connect() throws IOException {
            if (connected) {
                return;
            }
            final String endpoint = System.getenv().get("S3_ENDPOINT");
            final String accessKey = System.getenv().get("S3_ACCESS_KEY");
            final String secretKey = System.getenv().get("S3_SECRET_KEY");
            final String region = System.getenv().getOrDefault("S3_REGION", "us-east-1");

            // Validate endpoint before attempting connection
            if (StringUtil.isBlank(endpoint)) {
                throw new IOException("S3_ENDPOINT is blank.");
            }

            try {
                final software.amazon.awssdk.services.s3.S3ClientBuilder builder =
                        S3Client.builder().endpointOverride(URI.create(endpoint)).region(Region.of(region)).forcePathStyle(true);

                if (StringUtil.isNotBlank(accessKey) && StringUtil.isNotBlank(secretKey)) {
                    builder.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));
                }

                s3Client = builder.build();
                connected = true;
            } catch (final Exception e) {
                throw new IOException("Failed to create S3Client.", e);
            }
        }

        /**
         * Gets an input stream to read from the S3 object.
         *
         * @return An input stream for reading the object content
         * @throws IOException If the object cannot be accessed
         */
        @Override
        public InputStream getInputStream() throws IOException {
            if (!connected) {
                connect();
            }
            try {
                final GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(objectName).build();
                return s3Client.getObject(request);
            } catch (final S3Exception e) {
                throw new IOException("Failed to access " + url, e);
            }
        }

        /**
         * Gets the object head response from the S3 service.
         * This method caches the response to avoid repeated calls.
         *
         * @return The object head response
         * @throws IOException If an error occurs
         */
        private HeadObjectResponse getHeadObject() throws IOException {
            if (headObject == null) {
                final HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName).key(objectName).build();
                try {
                    headObject = s3Client.headObject(request);
                } catch (final S3Exception e) {
                    throw new IOException("Failed to get object head: " + url, e);
                }
            }
            return headObject;
        }

        /**
         * Gets the content length of the S3 object.
         *
         * @return The content length in bytes, or -1 if unavailable
         */
        @Override
        public long getContentLengthLong() {
            try {
                if (!connected) {
                    connect();
                }
                return getHeadObject().contentLength();
            } catch (final IOException e) {
                return -1;
            }
        }

        /**
         * Gets the content type of the S3 object.
         *
         * @return The content type, or null if unavailable
         */
        @Override
        public String getContentType() {
            try {
                if (!connected) {
                    connect();
                }
                return getHeadObject().contentType();
            } catch (final IOException e) {
                return null;
            }
        }

        /**
         * Gets the date of the S3 object.
         * This method returns the same value as getLastModified().
         *
         * @return The date in milliseconds since epoch
         */
        @Override
        public long getDate() {
            return getLastModified();
        }

        /**
         * Returns the last modified date of the S3 object.
         * @return The last modified date in milliseconds since epoch.
         */
        @Override
        public long getLastModified() {
            try {
                if (!connected) {
                    connect();
                }
                return getHeadObject().lastModified().toEpochMilli();
            } catch (final IOException e) {
                return 0;
            }
        }

    }
}
