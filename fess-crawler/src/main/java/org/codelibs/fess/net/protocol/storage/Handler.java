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
package org.codelibs.fess.net.protocol.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.codelibs.core.lang.StringUtil;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.MinioClient.Builder;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;

/**
 * Handler for the "storage" protocol, allowing access to objects stored in a MinIO-compatible storage service.
 * This handler extends {@link URLStreamHandler} to provide a way to open connections to storage objects
 * using URLs with the "storage" protocol.
 *
 * <p>
 * The URL format is expected to be: {@code storage://bucketName/objectName}.
 * The bucket name and object name are extracted from the URL.
 * </p>
 *
 * <p>
 * The handler relies on environment variables for configuration:
 * </p>
 * <ul>
 *   <li>{@code STORAGE_ENDPOINT}: The endpoint URL of the MinIO service.</li>
 *   <li>{@code STORAGE_ACCESS_KEY}: The access key for authentication.</li>
 *   <li>{@code STORAGE_SECRET_KEY}: The secret key for authentication.</li>
 *   <li>{@code STORAGE_REGION}: The region of the MinIO service.</li>
 * </ul>
 *
 * <p>
 * The {@link StorageURLConnection} class handles the actual connection and data retrieval from the storage service.
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
     * Opens a connection to the storage URL.
     *
     * @param u The URL to open a connection to
     * @return A new StorageURLConnection instance
     * @throws IOException If the connection cannot be opened
     */
    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        return new StorageURLConnection(u);
    }

    /**
     * StorageURLConnection is a URL connection implementation for accessing storage objects.
     * It extends URLConnection to provide connectivity to MinIO-compatible storage services.
     * This class handles the authentication, connection management, and data retrieval
     * from storage buckets and objects.
     *
     * <p>
     * The connection extracts bucket and object names from the URL and uses environment
     * variables for authentication and endpoint configuration.
     * </p>
     */
    public class StorageURLConnection extends URLConnection {

        /** The MinIO client for storage operations */
        private MinioClient minioClient;
        /** The name of the storage bucket */
        private String bucketName;
        /** The name of the storage object */
        private String objectName;
        /** Cached object statistics response */
        private StatObjectResponse statObject;

        /**
         * Constructs a new StorageURLConnection for the specified URL.
         * This constructor parses the URL to extract bucket and object names.
         *
         * @param url The storage URL to connect to
         */
        protected StorageURLConnection(final URL url) {
            super(url);
            final String[] values = url.toExternalForm().split("/", 2);
            if (values.length == 2) {
                bucketName = values[0];
                objectName = values[1];
            } else {
                if (values.length == 1) {
                    bucketName = values[0];
                } else {
                    bucketName = StringUtil.EMPTY;
                }
                objectName = StringUtil.EMPTY;
            }
        }

        /**
         * Establishes a connection to the storage service.
         * This method creates a MinIO client using environment variables for configuration.
         *
         * @throws IOException If the connection cannot be established
         */
        @Override
        public void connect() throws IOException {
            final String endpoint = System.getenv().get("STORAGE_ENDPOINT");
            final String accessKey = System.getenv().get("STORAGE_ACCESS_KEY");
            final String secretKey = System.getenv().get("STORAGE_SECRET_KEY");
            final String region = System.getenv().get("STORAGE_SECRET_KEY");
            try {
                if (StringUtil.isBlank(endpoint)) {
                    throw new IOException("endpoint is blank.");
                }
                final Builder builder = MinioClient.builder().endpoint(endpoint);
                if (StringUtil.isNotBlank(accessKey) && StringUtil.isNotBlank(secretKey)) {
                    builder.credentials(accessKey, secretKey);
                }
                if (StringUtil.isNotBlank(region)) {
                    builder.region(region);
                }
                minioClient = builder.build();
            } catch (final Exception e) {
                throw new IOException("Failed to create MinioClient.", e);
            }
        }

        /**
         * Gets an input stream to read from the storage object.
         *
         * @return An input stream for reading the object content
         * @throws IOException If the object cannot be accessed
         */
        @Override
        public InputStream getInputStream() throws IOException {
            if (minioClient == null) {
                throw new IOException("Access is not ready.");
            }
            try {
                final GetObjectArgs args = GetObjectArgs.builder().bucket(bucketName).object(objectName).build();
                return minioClient.getObject(args);
            } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | XmlParserException | IOException | ServerException e) {
                throw new IOException("Failed to access " + url, e);
            }
        }

        /**
         * Gets the object statistics from the storage service.
         * This method caches the response to avoid repeated calls.
         *
         * @return The object statistics response
         * @throws InvalidKeyException If the access key is invalid
         * @throws ErrorResponseException If the server returns an error
         * @throws IllegalArgumentException If the arguments are invalid
         * @throws InsufficientDataException If insufficient data is available
         * @throws InternalException If an internal error occurs
         * @throws InvalidResponseException If the response is invalid
         * @throws NoSuchAlgorithmException If the algorithm is not available
         * @throws XmlParserException If XML parsing fails
         * @throws IOException If an I/O error occurs
         * @throws ServerException If a server error occurs
         */
        private StatObjectResponse getStatObject()
                throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException, InternalException,
                InvalidResponseException, NoSuchAlgorithmException, XmlParserException, IOException, ServerException {
            if (statObject == null) {
                final StatObjectArgs args = StatObjectArgs.builder().bucket(bucketName).object(objectName).build();
                statObject = minioClient.statObject(args);
            }
            return statObject;
        }

        /**
         * Gets the content length of the storage object.
         *
         * @return The content length in bytes, or -1 if unavailable
         */
        @Override
        public long getContentLengthLong() {
            if (minioClient == null) {
                return -1;
            }
            try {
                return getStatObject().size();
            } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | XmlParserException | IOException | ServerException e) {
                return -1;
            }
        }

        /**
         * Gets the content type of the storage object.
         *
         * @return The content type, or null if unavailable
         */
        @Override
        public String getContentType() {
            if (minioClient == null) {
                return null;
            }
            try {
                return getStatObject().contentType();
            } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | XmlParserException | IOException | ServerException e) {
                return null;
            }
        }

        /**
         * Gets the date of the storage object.
         * This method returns the same value as getLastModified().
         *
         * @return The date in milliseconds since epoch
         */
        @Override
        public long getDate() {
            return getLastModified();
        }

        /**
         * Returns the last modified date of the storage object.
         * @return The last modified date.
         */
        @Override
        public long getLastModified() {
            if (minioClient == null) {
                return 0;
            }
            try {
                return getStatObject().lastModified().toEpochSecond();
            } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | XmlParserException | IOException | ServerException e) {
                return 0;
            }
        }

    }
}
