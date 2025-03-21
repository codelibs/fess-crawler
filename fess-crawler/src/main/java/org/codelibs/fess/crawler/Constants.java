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
package org.codelibs.fess.crawler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Constants used in the fess-crawler.
 * This class provides a collection of constant values for HTTP methods, status codes,
 * transformer names, boolean values, character encodings, date/time formats, and XML features.
 * It is designed to avoid the instantiation.
 */
public final class Constants {
    public static final String GET_METHOD = "GET";

    public static final String HEAD_METHOD = "HEAD";

    public static final String POST_METHOD = "POST";

    public static final int OK_STATUS = 0;

    public static final int NOT_MODIFIED_STATUS = 304;

    public static final int OK_STATUS_CODE = 200;

    public static final int NOT_MODIFIED_STATUS_CODE = 304;

    public static final int BAD_REQUEST_STATUS_CODE = 400;

    public static final int FORBIDDEN_STATUS_CODE = 403;

    public static final int NOT_FOUND_STATUS_CODE = 404;

    public static final int SERVER_ERROR_STATUS_CODE = 500;

    public static final String NO_TRANSFORMER = "NONE";

    public static final String FALSE = "false";

    public static final String UTF_8 = "UTF-8";

    public static final Charset UTF_8_CHARSET = StandardCharsets.UTF_8;

    public static final Charset DEFAULT_CHARSET;

    public static final String ISO_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String FEATURE_SECURE_PROCESSING = "http://javax.xml.XMLConstants/feature/secure-processing";

    public static final String FEATURE_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";

    public static final String FEATURE_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

    static {
        DEFAULT_CHARSET = Charset.defaultCharset();
    }

    /**
     * Private constructor to prevent instantiation of this class.
     */
    private Constants() {
    }

}
