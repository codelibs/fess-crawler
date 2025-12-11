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
package org.codelibs.fess.net.protocol.gcs;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

import org.codelibs.core.lang.StringUtil;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

/**
 * Handler for the "gcs" protocol, allowing access to objects stored in Google Cloud Storage.
 * This handler extends {@link URLStreamHandler} to provide a way to open connections to GCS objects
 * using URLs with the "gcs" protocol.
 *
 * <p>
 * The URL format is expected to be: {@code gcs://bucketName/objectName}.
 * The bucket name and object name are extracted from the URL.
 * </p>
 *
 * <p>
 * The handler relies on environment variables for configuration:
 * </p>
 * <ul>
 *   <li>{@code GCS_PROJECT_ID}: The Google Cloud project ID.</li>
 *   <li>{@code GCS_ENDPOINT}: Custom endpoint URL (optional, for testing with fake-gcs-server).</li>
 *   <li>{@code GCS_CREDENTIALS_FILE}: Path to service account JSON file (optional).</li>
 * </ul>
 *
 * <p>
 * The {@link GcsURLConnection} class handles the actual connection and data retrieval from the storage service.
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
     * Opens a connection to the GCS URL.
     *
     * @param u The URL to open a connection to
     * @return A new GcsURLConnection instance
     * @throws IOException If the connection cannot be opened
     */
    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        return new GcsURLConnection(u);
    }

    /**
     * GcsURLConnection is a URL connection implementation for accessing GCS objects.
     * It extends URLConnection to provide connectivity to Google Cloud Storage.
     * This class handles the authentication, connection management, and data retrieval
     * from storage buckets and objects.
     *
     * <p>
     * The connection extracts bucket and object names from the URL and uses environment
     * variables for authentication and endpoint configuration.
     * </p>
     */
    public class GcsURLConnection extends URLConnection {

        /** The GCS Storage client for storage operations */
        private Storage storage;
        /** The name of the storage bucket */
        private String bucketName;
        /** The name of the storage object */
        private String objectName;
        /** Cached blob object */
        private Blob blob;

        /**
         * Constructs a new GcsURLConnection for the specified URL.
         * This constructor parses the URL to extract bucket and object names.
         *
         * @param url The GCS URL to connect to
         */
        protected GcsURLConnection(final URL url) {
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
         * Establishes a connection to the GCS service.
         * This method creates a GCS Storage client using environment variables for configuration.
         * This method is synchronized to ensure thread-safe connection initialization.
         *
         * @throws IOException If the connection cannot be established
         */
        @Override
        public synchronized void connect() throws IOException {
            if (connected) {
                return;
            }
            final String projectId = System.getenv("GCS_PROJECT_ID");
            final String endpoint = System.getenv("GCS_ENDPOINT");
            final String credentialsFile = System.getenv("GCS_CREDENTIALS_FILE");

            // Validate projectId before attempting connection
            if (StringUtil.isBlank(projectId)) {
                throw new IOException("GCS_PROJECT_ID is blank.");
            }

            try {
                final StorageOptions.Builder builder = StorageOptions.newBuilder().setProjectId(projectId);

                if (StringUtil.isNotBlank(endpoint)) {
                    // For fake-gcs-server or custom endpoint
                    builder.setHost(endpoint);
                    builder.setCredentials(NoCredentials.getInstance());
                } else if (StringUtil.isNotBlank(credentialsFile)) {
                    // Use credentials file if specified
                    try (InputStream is = new FileInputStream(credentialsFile)) {
                        builder.setCredentials(ServiceAccountCredentials.fromStream(is));
                    }
                }
                // If no credentials file or endpoint, GoogleCredentials.getApplicationDefault() will be used

                storage = builder.build().getService();
                connected = true;
            } catch (final Exception e) {
                throw new IOException("Failed to create GCS client.", e);
            }
        }

        /**
         * Gets an input stream to read from the GCS object.
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
                final Blob blobObj = getBlob();
                if (blobObj == null) {
                    throw new IOException("Object not found: bucket=" + bucketName + ", object=" + objectName);
                }
                return Channels.newInputStream(blobObj.reader());
            } catch (final IOException e) {
                throw e;
            } catch (final Exception e) {
                throw new IOException("Failed to access " + url, e);
            }
        }

        /**
         * Gets the Blob object from GCS.
         * This method caches the response to avoid repeated calls.
         *
         * @return The Blob object
         * @throws IOException If an I/O error occurs
         */
        private Blob getBlob() throws IOException {
            if (blob == null) {
                blob = storage.get(BlobId.of(bucketName, objectName));
            }
            return blob;
        }

        /**
         * Gets the content length of the GCS object.
         *
         * @return The content length in bytes, or -1 if unavailable
         */
        @Override
        public long getContentLengthLong() {
            try {
                if (!connected) {
                    connect();
                }
                final Blob blobObj = getBlob();
                return blobObj != null ? blobObj.getSize() : -1;
            } catch (final Exception e) {
                return -1;
            }
        }

        /**
         * Gets the content type of the GCS object.
         *
         * @return The content type, or null if unavailable
         */
        @Override
        public String getContentType() {
            try {
                if (!connected) {
                    connect();
                }
                final Blob blobObj = getBlob();
                return blobObj != null ? blobObj.getContentType() : null;
            } catch (final Exception e) {
                return null;
            }
        }

        /**
         * Gets the date of the GCS object.
         * This method returns the same value as getLastModified().
         *
         * @return The date in milliseconds since epoch
         */
        @Override
        public long getDate() {
            return getLastModified();
        }

        /**
         * Returns the last modified date of the GCS object.
         * @return The last modified date in milliseconds since epoch.
         */
        @Override
        public long getLastModified() {
            try {
                if (!connected) {
                    connect();
                }
                final Blob blobObj = getBlob();
                if (blobObj != null && blobObj.getUpdateTimeOffsetDateTime() != null) {
                    return blobObj.getUpdateTimeOffsetDateTime().toInstant().toEpochMilli();
                }
                return 0;
            } catch (final Exception e) {
                return 0;
            }
        }

    }
}
