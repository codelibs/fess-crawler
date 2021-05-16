/*
 * Copyright 2012-2021 CodeLibs Project and the Others.
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

public class Handler extends URLStreamHandler {

    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        return new StorageURLConnection(u);
    }

    public class StorageURLConnection extends URLConnection {

        private MinioClient minioClient;
        private String bucketName;
        private String objectName;
        private StatObjectResponse statObject;

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

        @Override
        public InputStream getInputStream() throws IOException {
            if (minioClient == null) {
                throw new IOException("Access is not ready.");
            }
            try {
                final GetObjectArgs args = GetObjectArgs.builder().bucket(bucketName).object(objectName).build();
                return minioClient.getObject(args);
            } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | XmlParserException
                    | IOException | ServerException e) {
                throw new IOException("Failed to access " + url, e);
            }
        }

        private StatObjectResponse getStatObject()
                throws InvalidKeyException, ErrorResponseException, IllegalArgumentException, InsufficientDataException, InternalException,
                InvalidResponseException, NoSuchAlgorithmException, XmlParserException, IOException, ServerException {
            if (statObject == null) {
                final StatObjectArgs args = StatObjectArgs.builder().bucket(bucketName).object(objectName).build();
                statObject = minioClient.statObject(args);
            }
            return statObject;
        }

        @Override
        public long getContentLengthLong() {
            if (minioClient == null) {
                return -1;
            }
            try {
                return getStatObject().size();
            } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | XmlParserException
                    | IOException | ServerException e) {
                return -1;
            }
        }

        @Override
        public String getContentType() {
            if (minioClient == null) {
                return null;
            }
            try {
                return getStatObject().contentType();
            } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | XmlParserException
                    | IOException | ServerException e) {
                return null;
            }
        }

        @Override
        public long getDate() {
            return getLastModified();
        }

        @Override
        public long getLastModified() {
            if (minioClient == null) {
                return 0;
            }
            try {
                return getStatObject().lastModified().toEpochSecond();
            } catch (InvalidKeyException | ErrorResponseException | IllegalArgumentException | InsufficientDataException | InternalException
                    | InvalidResponseException | NoSuchAlgorithmException | XmlParserException
                    | IOException | ServerException e) {
                return 0;
            }
        }

    }
}
